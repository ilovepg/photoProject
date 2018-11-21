package board;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiUtils;

import dao.DAO;
import util.UtilClass;

/*
 * 포토 게시판에서 메인이미지, 서브이미지, 서브이미지의 내용, 제목을 받아서 DB에 저장하는 서블릿
 * 
 * */
@WebServlet("/photoBoardUpload")
public class PhotoBoardUpload extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); //한글이 깨지기 때문에 UTF8로 인코딩
		/*포토 글쓰기 작성자를 알아야하기 때문에 세션에서 현재 로그인된 사용자명을 가져온다.*/
		HttpSession session=request.getSession(); 
		String userID=session.getAttribute("userID").toString();
		/*글쓴 장소의 ip정보를 가져온다.*/
		UtilClass util = new UtilClass();
		String clientIP=util.getClientIP(request);
		//response는 민감한 객체이기때문에 스크립트를 쓰려면 이렇게 해줘야한다.
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		
		// Linux /var/lib/tomcat8/webapps/photoProject/imgs
		// Windows C:\Users\DevOps\eclipse-workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp3\wtpwebapps\photoProject\imgs
		// 회사 : C:\Users\DevOps\workspaceHomeRevision\.metadata\.plugins\org.eclipse.wst.server.core\tmp1\wtpwebapps\photoProject\imgs
		String directory = request.getServletContext().getRealPath("/imgs"); 
		//String directory = "http://218.149.135.58:8080/photoProject/imgs/"; //안됨..
		int fileMaxSize = 1024*1024*100; //100MB 사이즈까지 업로드 가능
		String encoding = "UTF-8"; 
		System.out.println("directory:"+directory);
		MultipartRequest multipartRequest = 
				new MultipartRequest(request, directory, fileMaxSize, encoding,
						new DefaultFileRenamePolicy());
		
		/*enctype을 "multipart/form-data"로 선언하고 submit한 데이터들은 request객체가 아닌 MultipartRequest객체로 불러와야 한다.*/
		//제목, 사진 내용정보
		Map<String,String> stringParamMap = new HashMap<String,String>(); //게시판 제목, 사진 내용,태그들이 저장되는 맵.
		Map<String,String> photoParamMap = new HashMap<String,String>(); //메인사진, 서브사진의 파일명이 들어간다.
		Map<String,Map<String,String>> photoOrderMap = new HashMap<String,Map<String,String>>(); //1,2,3 같은 순서가 키값으로 들어가고, PhotoParamMap이 벨류값을 들어간다.
		
		//게시글 제목, 한줄평,사진의 내용,태그, 서브사진들의 길이
		Enumeration stringParamName = multipartRequest.getParameterNames(); //파일의 키값이 아니라 제목, 사진 등의 키값
		while(stringParamName.hasMoreElements()) {
			String param = (String)stringParamName.nextElement();
			stringParamMap.put(param,multipartRequest.getParameter(param));
		}
		stringParamMap.put("userID", userID); //userID를 넣어준다.
		stringParamMap.put("userIP", clientIP); //IP를 넣어준다.
		System.out.println(stringParamMap);
		
		//file 업로드
		Enumeration fileNames = multipartRequest.getFileNames();
		while(fileNames.hasMoreElements()) {
			String fileParam = (String)fileNames.nextElement();
			//System.out.println(fileParam);
			String fileName = multipartRequest.getOriginalFileName(fileParam);   //사용자가 업로드한 파일의 이름을  넣어준다.
			String fileRealName = multipartRequest.getFilesystemName(fileParam); //서버에 업로드된 파일의 이름
			if(fileName == null) continue;
			
			/*확장자 체크*/
			if(!fileName.endsWith(".png") && !fileName.endsWith(".jpg") && 
					!fileName.endsWith(".jpeg") && !fileName.endsWith(".raw") && 
					!fileName.endsWith(".gif")){
				File file = new File(directory + fileRealName);
				file.delete();  //올바른 확장자가 아니라면 그 파일을 지운다.
				out.write("<script>");
				out.println("alert('업로드할 수 없는 확장자 입니다.);");
				out.write("</script>");
			}else{
				//이름이 중복되어서 파일이 덮어쓰기 될 수도있기 때문에 이렇게 나눠서 저장이 된다.
				photoParamMap.put("userUploadName",fileName);  //사용자가 업로드한 이름
				photoParamMap.put("serverUploadName",fileRealName); //서버에 업로드된 이름
				photoOrderMap.put(fileParam,photoParamMap); 
				photoParamMap = new HashMap<String,String>(); //초기화 해준다.
			}
		} //end of While
		
		
		/*확장자 체크를 통과했다면 main.pro에서 쓰일 메인사진에 대한 썸네일 이미지를 생성하고 
		  DB에 파일명과 그 내용을 넣는다.*/
		Random random = new Random(); //썸네일 파일명 중복을 최소화 하기위한 랜덤함수
		String imageMain = photoOrderMap.get("imageMain").get("serverUploadName"); //메인사진 파일명
		String subject = stringParamMap.get("subject").trim(); //썸네일 파일명에 쓰기 위해서 게시글 제목을 가져온다. 
		subject=subject.replaceAll(" ", ""); //공백제거
		subject=subject.replaceAll("(^\\p{Z}+|\\p{Z}+$)", ""); //모든 공백제거
		imageMain=directory+File.separator+imageMain; //경로+메인사진명
		int randomNumber=random.nextInt(10000); //썸네일 파일명에 쓰기 위한 랜덤 값
		String thumb = subject+"_"+randomNumber+"_thumb.jpg"; //썸네일 파일명 (DB에 들어갈)
		String thumbImg = directory+File.separator+subject+"_"+randomNumber+"_thumb.jpg"; //경로+썸네일 파일명
		int thumbWidth = 353 ;//썸네일 가로
        int thumbHeight = 326 ;//썸네일 세로
        
        Image thumbnail = JimiUtils.getThumbnail(imageMain, thumbWidth, thumbHeight, Jimi.IN_MEMORY,false);// 썸네일 설정
        try {
			Jimi.putImage(thumbnail, thumbImg); //썸네일 생성
			photoParamMap.put("userUploadName",thumb);  //사용자가 업로드한 이름
			photoParamMap.put("serverUploadName",thumb);//서버에 업로드된 이름
			photoOrderMap.put("mainImageThumb",photoParamMap); //썸네일 파일명 맵을 넣는다.
		} catch (JimiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        /*UtilClass에서 썸네일 이미지 생성*/
        /*360*225 사이즈 : photoView.pro에서 사용하는 썸네일 이미지*/
        UtilClass.thumbImageCreate(360, 225, photoOrderMap, stringParamMap, directory, "main");
        UtilClass.thumbImageCreate(360, 225, photoOrderMap, stringParamMap, directory, "sub");
        System.out.println(photoOrderMap);

		//DAO를 통해 DB에 이미지 파일명과 서브이미지의 내용을 넣는다.
        try {
			DAO dao = new DAO();
			int photoBoard_insert_result=dao.photoBoard_main_insert(stringParamMap, photoOrderMap);
			if(photoBoard_insert_result==1) { 
				//성공
				json.put("result", "success");
				/*out.println("<script>alert('게시글이 성공적으로 등록되었습니다.');</script>");
				response.sendRedirect("/photoProject/main.pro");*/
			}else if(photoBoard_insert_result==0) {
				//SQL 오류
				json.put("result", "SQL_fail");
				/*response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);*/
			}else if(photoBoard_insert_result==-1) {
				//데이터 누락
				/*out.println("<script>alert('데이터가 누락되었지만 게시글 자체는 등록되었습니다.');</script>");
				response.sendRedirect("/photoProject/main.pro");*/
				json.put("result", "data_omission");
			}else {
				System.out.println("PhotoBoardUpload 오류발생 photoBoard_insert_result:"+photoBoard_insert_result);
				/*out.println("<script>alert('알수없는 오류가 발생하였습니다. 관리자에게 문의해주세요.');</script>");*/
			}
			out.print(json);
			out.flush();
			dao.closeConn();
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
		}
		
	
	}

}
