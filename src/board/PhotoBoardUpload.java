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
 * ���� �Խ��ǿ��� �����̹���, �����̹���, �����̹����� ����, ������ �޾Ƽ� DB�� �����ϴ� ����
 * 
 * */
@WebServlet("/photoBoardUpload")
public class PhotoBoardUpload extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); //�ѱ��� ������ ������ UTF8�� ���ڵ�
		/*���� �۾��� �ۼ��ڸ� �˾ƾ��ϱ� ������ ���ǿ��� ���� �α��ε� ����ڸ��� �����´�.*/
		HttpSession session=request.getSession(); 
		String userID=session.getAttribute("userID").toString();
		/*�۾� ����� ip������ �����´�.*/
		UtilClass util = new UtilClass();
		String clientIP=util.getClientIP(request);
		//response�� �ΰ��� ��ü�̱⶧���� ��ũ��Ʈ�� ������ �̷��� ������Ѵ�.
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		
		// Linux /var/lib/tomcat8/webapps/photoProject/imgs
		// Windows C:\Users\DevOps\eclipse-workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp3\wtpwebapps\photoProject\imgs
		// ȸ�� : C:\Users\DevOps\workspaceHomeRevision\.metadata\.plugins\org.eclipse.wst.server.core\tmp1\wtpwebapps\photoProject\imgs
		String directory = request.getServletContext().getRealPath("/imgs"); 
		//String directory = "http://218.149.135.58:8080/photoProject/imgs/"; //�ȵ�..
		int fileMaxSize = 1024*1024*100; //100MB ��������� ���ε� ����
		String encoding = "UTF-8"; 
		System.out.println("directory:"+directory);
		MultipartRequest multipartRequest = 
				new MultipartRequest(request, directory, fileMaxSize, encoding,
						new DefaultFileRenamePolicy());
		
		/*enctype�� "multipart/form-data"�� �����ϰ� submit�� �����͵��� request��ü�� �ƴ� MultipartRequest��ü�� �ҷ��;� �Ѵ�.*/
		//����, ���� ��������
		Map<String,String> stringParamMap = new HashMap<String,String>(); //�Խ��� ����, ���� ����,�±׵��� ����Ǵ� ��.
		Map<String,String> photoParamMap = new HashMap<String,String>(); //���λ���, ��������� ���ϸ��� ����.
		Map<String,Map<String,String>> photoOrderMap = new HashMap<String,Map<String,String>>(); //1,2,3 ���� ������ Ű������ ����, PhotoParamMap�� �������� ����.
		
		//�Խñ� ����, ������,������ ����,�±�, ����������� ����
		Enumeration stringParamName = multipartRequest.getParameterNames(); //������ Ű���� �ƴ϶� ����, ���� ���� Ű��
		while(stringParamName.hasMoreElements()) {
			String param = (String)stringParamName.nextElement();
			stringParamMap.put(param,multipartRequest.getParameter(param));
		}
		stringParamMap.put("userID", userID); //userID�� �־��ش�.
		stringParamMap.put("userIP", clientIP); //IP�� �־��ش�.
		System.out.println(stringParamMap);
		
		//file ���ε�
		Enumeration fileNames = multipartRequest.getFileNames();
		while(fileNames.hasMoreElements()) {
			String fileParam = (String)fileNames.nextElement();
			//System.out.println(fileParam);
			String fileName = multipartRequest.getOriginalFileName(fileParam);   //����ڰ� ���ε��� ������ �̸���  �־��ش�.
			String fileRealName = multipartRequest.getFilesystemName(fileParam); //������ ���ε�� ������ �̸�
			if(fileName == null) continue;
			
			/*Ȯ���� üũ*/
			if(!fileName.endsWith(".png") && !fileName.endsWith(".jpg") && 
					!fileName.endsWith(".jpeg") && !fileName.endsWith(".raw") && 
					!fileName.endsWith(".gif")){
				File file = new File(directory + fileRealName);
				file.delete();  //�ùٸ� Ȯ���ڰ� �ƴ϶�� �� ������ �����.
				out.write("<script>");
				out.println("alert('���ε��� �� ���� Ȯ���� �Դϴ�.);");
				out.write("</script>");
			}else{
				//�̸��� �ߺ��Ǿ ������ ����� �� �����ֱ� ������ �̷��� ������ ������ �ȴ�.
				photoParamMap.put("userUploadName",fileName);  //����ڰ� ���ε��� �̸�
				photoParamMap.put("serverUploadName",fileRealName); //������ ���ε�� �̸�
				photoOrderMap.put(fileParam,photoParamMap); 
				photoParamMap = new HashMap<String,String>(); //�ʱ�ȭ ���ش�.
			}
		} //end of While
		
		
		/*Ȯ���� üũ�� ����ߴٸ� main.pro���� ���� ���λ����� ���� ����� �̹����� �����ϰ� 
		  DB�� ���ϸ�� �� ������ �ִ´�.*/
		Random random = new Random(); //����� ���ϸ� �ߺ��� �ּ�ȭ �ϱ����� �����Լ�
		String imageMain = photoOrderMap.get("imageMain").get("serverUploadName"); //���λ��� ���ϸ�
		String subject = stringParamMap.get("subject").trim(); //����� ���ϸ� ���� ���ؼ� �Խñ� ������ �����´�. 
		subject=subject.replaceAll(" ", ""); //��������
		subject=subject.replaceAll("(^\\p{Z}+|\\p{Z}+$)", ""); //��� ��������
		imageMain=directory+File.separator+imageMain; //���+���λ�����
		int randomNumber=random.nextInt(10000); //����� ���ϸ� ���� ���� ���� ��
		String thumb = subject+"_"+randomNumber+"_thumb.jpg"; //����� ���ϸ� (DB�� ��)
		String thumbImg = directory+File.separator+subject+"_"+randomNumber+"_thumb.jpg"; //���+����� ���ϸ�
		int thumbWidth = 353 ;//����� ����
        int thumbHeight = 326 ;//����� ����
        
        Image thumbnail = JimiUtils.getThumbnail(imageMain, thumbWidth, thumbHeight, Jimi.IN_MEMORY,false);// ����� ����
        try {
			Jimi.putImage(thumbnail, thumbImg); //����� ����
			photoParamMap.put("userUploadName",thumb);  //����ڰ� ���ε��� �̸�
			photoParamMap.put("serverUploadName",thumb);//������ ���ε�� �̸�
			photoOrderMap.put("mainImageThumb",photoParamMap); //����� ���ϸ� ���� �ִ´�.
		} catch (JimiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        /*UtilClass���� ����� �̹��� ����*/
        /*360*225 ������ : photoView.pro���� ����ϴ� ����� �̹���*/
        UtilClass.thumbImageCreate(360, 225, photoOrderMap, stringParamMap, directory, "main");
        UtilClass.thumbImageCreate(360, 225, photoOrderMap, stringParamMap, directory, "sub");
        System.out.println(photoOrderMap);

		//DAO�� ���� DB�� �̹��� ���ϸ�� �����̹����� ������ �ִ´�.
        try {
			DAO dao = new DAO();
			int photoBoard_insert_result=dao.photoBoard_main_insert(stringParamMap, photoOrderMap);
			if(photoBoard_insert_result==1) { 
				//����
				json.put("result", "success");
				/*out.println("<script>alert('�Խñ��� ���������� ��ϵǾ����ϴ�.');</script>");
				response.sendRedirect("/photoProject/main.pro");*/
			}else if(photoBoard_insert_result==0) {
				//SQL ����
				json.put("result", "SQL_fail");
				/*response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);*/
			}else if(photoBoard_insert_result==-1) {
				//������ ����
				/*out.println("<script>alert('�����Ͱ� �����Ǿ����� �Խñ� ��ü�� ��ϵǾ����ϴ�.');</script>");
				response.sendRedirect("/photoProject/main.pro");*/
				json.put("result", "data_omission");
			}else {
				System.out.println("PhotoBoardUpload �����߻� photoBoard_insert_result:"+photoBoard_insert_result);
				/*out.println("<script>alert('�˼����� ������ �߻��Ͽ����ϴ�. �����ڿ��� �������ּ���.');</script>");*/
			}
			out.print(json);
			out.flush();
			dao.closeConn();
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
		}
		
	
	}

}
