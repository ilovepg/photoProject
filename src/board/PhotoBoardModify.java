package board;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import dao.DAO;

/*
 * 포토 게시판에서 메인이미지, 서브이미지, 서브이미지의 내용, 제목을 받아서 DB에 수정하는 서블릿
 * 
 * */
@WebServlet("/photoBoardModify")
public class PhotoBoardModify extends HttpServlet {
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	request.setCharacterEncoding("UTF-8"); //한글이 깨지기 때문에 UTF8로 인코딩
    	
    	//response는 민감한 객체이기때문에 스크립트를 쓰려면 이렇게 해줘야한다.
    	response.setContentType("application/json; charset=UTF-8");
    	PrintWriter out = response.getWriter(); //결과값을 보내줄 PrintWriter
    	JSONObject resultJson = new JSONObject(); //결과값으로 보내질 json값
    	
    	DAO dao=null;
		try {
			dao = new DAO();
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
		}
    	// Linux /var/lib/tomcat8/webapps/photoProject/imgs
    	// Windows C:\Users\DevOps\eclipse-workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp3\wtpwebapps\photoProject\imgs
    	// 회사 : C:\Users\DevOps\workspaceHomeRevision\.metadata\.plugins\org.eclipse.wst.server.core\tmp1\wtpwebapps\photoProject\imgs
    	String directory = request.getServletContext().getRealPath("/imgs"); 
    	int fileMaxSize = 1024*1024*100; //100MB 사이즈까지 업로드 가능
    	String encoding = "UTF-8"; 
    	System.out.println("directory:"+directory);
    	Map<String,Object> stringParamMap = new LinkedTreeMap<String,Object>(); //파일이 아닌 다른 키가 들어있는 맵
    	Map<String,Object> originalFileParamMap = new HashMap<String,Object>(); //파일 키값
    	Map<String,Object> addFileParamMap = new HashMap<String,Object>(); //파일 키값
    	
    	MultipartRequest multipartRequest = 
				new MultipartRequest(request, directory, fileMaxSize, encoding,
						new DefaultFileRenamePolicy());
    	
    	Enumeration param1 = multipartRequest.getParameterNames();
    	while(param1.hasMoreElements()) {
    		String param = (String)param1.nextElement();
    		System.out.println("param:"+param+" value:"+multipartRequest.getParameter(param));
    		stringParamMap.put(param, multipartRequest.getParameter(param)); //맵에 통합적으로 넣는다.
    	}
    	/*Enumeration fileNames = multipartRequest.getFileNames();
    	while(fileNames.hasMoreElements()) {
    		String fileParam = (String)fileNames.nextElement();
    		String fileName = multipartRequest.getOriginalFileName(fileParam);   //사용자가 업로드한 파일의 이름을  넣어준다.
    		System.out.println("file_param:"+fileParam+" value:"+fileName);
    	}*/
    	
    	//1. 삭제할 데이터 삭제 (delListSize가 있는지 검사한다.)
    	if(stringParamMap.containsKey("delListSize")) {
    		int delListSize = Integer.parseInt(stringParamMap.get("delListSize").toString());
    		for(int i=0; i<delListSize; i++) {
    			int del_photosubno = Integer.parseInt(stringParamMap.get("delList_"+i).toString()); //지워질 데이터의 photoSubNo
    			String result=dao.deleteSubPhoto(del_photosubno); //dao를 통해 DB에서 지워준다.
    			if(!result.equals("성공")) {
    				System.out.println("오류발생:"+result);
    				resultJson.put("result", result);
    				out.print(resultJson);
    				out.flush();
    			}
    		}
    	}
    	
    	//2. 기존 서브포토 순서 변경
    	if(stringParamMap.containsKey("orderObject")) {
    		//GSON을 사용하여 객체를 JSON으로 변환
			Map<String,Object> orderMap = jsonfnc(stringParamMap.get("orderObject").toString()); //json 형태의 String값을 Map으로 변환해준다.
			int sel_files_length=0; //새로 추가된 서브포토의 개수
			if(stringParamMap.containsKey("sel_files_length")) { 
				sel_files_length=Integer.parseInt(stringParamMap.get("sel_files_length").toString());
			}
			int orderMapSize=orderMap.size()-sel_files_length; //기존에 있는 파일만 순서를 바꿔주기 위해서 새로추가된 파일개수만큼 빼준다.
			for(int i=0; i<orderMapSize; i++) {
				int photo_ownNo=i;//파일순서
				String orderMapKey = String.valueOf(photo_ownNo);
				int photo_subNo=Integer.parseInt(orderMap.get(orderMapKey).toString()); //파일 순서에 따른 DB고유값  
				String result=dao.updatePhotosOrder(photo_ownNo, photo_subNo);
				if(!result.equals("성공")) {
    				System.out.println("오류발생:"+result);
    				resultJson.put("result", result);
    				out.print(resultJson);
    				out.flush();
    			}
			}
    	}
    	
    	//3. 기존에 있던 서브포토 내용 수정
    	if(stringParamMap.containsKey("updateSubPhotoContent")) {
    		Map<String,Object>updateSubPhotoContentMap = jsonfnc(stringParamMap.get("updateSubPhotoContent").toString());//json 형태의 String값을 Map으로 변환해준다.
    		//updateSubPhotoContentMap의 키값은 photo_subNo(DB고유값)
    		Set set = updateSubPhotoContentMap.keySet();
    		Iterator<Set> iterator = set.iterator();
    		while(iterator.hasNext()) {
    			String photo_subNo=String.valueOf(iterator.next()); //DB고유번호
    			String contents = updateSubPhotoContentMap.get(photo_subNo).toString(); //서브사진 내용
    			String result=dao.updateSubPhotoContent(Integer.parseInt(photo_subNo), contents);
    			if(!result.equals("성공")) {
    				System.out.println("오류발생:"+result);
    				resultJson.put("result", result);
    				out.print(resultJson);
    				out.flush();
    			}
    		}
    	}
    	
    	/*수정된 기존 서브포토파일과 새로추가된 파일 분리*/
    	Map tempMap=null; //임시 저장맵
    	Enumeration fileNames = multipartRequest.getFileNames();
    	while(fileNames.hasMoreElements()) {
    		String fileParam = (String)fileNames.nextElement();
    		String fileName = multipartRequest.getOriginalFileName(fileParam);   //사용자가 업로드한 파일의 이름을  넣어준다.
			String fileRealName = multipartRequest.getFilesystemName(fileParam); //서버에 업로드된 파일의 이름
			tempMap = new HashMap<String,Object>(); 
    		if(fileParam.contains("newFile_")) { //새로운 파일
    			//게시글 번호 필요함.ㅎ
    			addFileParamMap = new HashMap<String,Object>(); //파일 키값
    		}else { //기존 파일
    	    	String photo_subNo = fileParam.replaceFirst("updateSubPhoto", ""); //오리지널 파일 변경을 위한 DB고유값
    	    	tempMap.put("userUploadName", fileName); //사용자가 업로드한 이름
    	    	tempMap.put("serverUploadName", fileRealName);//서버에 업로드된 이름
    	    	originalFileParamMap.put(photo_subNo, tempMap);
    		}
    		
    	}
    	
    	//4. 기존에 있던 서브포토 파일 업로드
    	if(stringParamMap.containsKey("updateListSize")) {
    		//String fileUserName=stringParamMap.get("");
    	}
    	
    	//5. 새로 추가된 파일 업로드 및  DB에 추가하기
    	if(stringParamMap.containsKey("sel_files_length")) {
    		
    	}
    	
    }
    
    //실제로 파일을 업로드하는 메소드
    private void uploadFile() {
    	
    }
    
    //String으로 온 data를 map으로 변환
  	private Map<String, Object> jsonfnc(String data) {
  		String json = URLDecoder.decode(data);
  		Gson g = new Gson();
  		Map<String,Object> map = new HashMap<String,Object>();
  		return g.fromJson(json, map.getClass());
  	}
  	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
