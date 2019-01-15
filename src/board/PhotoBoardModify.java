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
 * ���� �Խ��ǿ��� �����̹���, �����̹���, �����̹����� ����, ������ �޾Ƽ� DB�� �����ϴ� ����
 * 
 * */
@WebServlet("/photoBoardModify")
public class PhotoBoardModify extends HttpServlet {
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	request.setCharacterEncoding("UTF-8"); //�ѱ��� ������ ������ UTF8�� ���ڵ�
    	
    	//response�� �ΰ��� ��ü�̱⶧���� ��ũ��Ʈ�� ������ �̷��� ������Ѵ�.
    	response.setContentType("application/json; charset=UTF-8");
    	PrintWriter out = response.getWriter(); //������� ������ PrintWriter
    	JSONObject resultJson = new JSONObject(); //��������� ������ json��
    	
    	DAO dao=null;
		try {
			dao = new DAO();
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
		}
    	// Linux /var/lib/tomcat8/webapps/photoProject/imgs
    	// Windows C:\Users\DevOps\eclipse-workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp3\wtpwebapps\photoProject\imgs
    	// ȸ�� : C:\Users\DevOps\workspaceHomeRevision\.metadata\.plugins\org.eclipse.wst.server.core\tmp1\wtpwebapps\photoProject\imgs
    	String directory = request.getServletContext().getRealPath("/imgs"); 
    	int fileMaxSize = 1024*1024*100; //100MB ��������� ���ε� ����
    	String encoding = "UTF-8"; 
    	System.out.println("directory:"+directory);
    	Map<String,Object> stringParamMap = new LinkedTreeMap<String,Object>(); //������ �ƴ� �ٸ� Ű�� ����ִ� ��
    	Map<String,Object> originalFileParamMap = new HashMap<String,Object>(); //���� Ű��
    	Map<String,Object> addFileParamMap = new HashMap<String,Object>(); //���� Ű��
    	
    	MultipartRequest multipartRequest = 
				new MultipartRequest(request, directory, fileMaxSize, encoding,
						new DefaultFileRenamePolicy());
    	
    	Enumeration param1 = multipartRequest.getParameterNames();
    	while(param1.hasMoreElements()) {
    		String param = (String)param1.nextElement();
    		System.out.println("param:"+param+" value:"+multipartRequest.getParameter(param));
    		stringParamMap.put(param, multipartRequest.getParameter(param)); //�ʿ� ���������� �ִ´�.
    	}
    	/*Enumeration fileNames = multipartRequest.getFileNames();
    	while(fileNames.hasMoreElements()) {
    		String fileParam = (String)fileNames.nextElement();
    		String fileName = multipartRequest.getOriginalFileName(fileParam);   //����ڰ� ���ε��� ������ �̸���  �־��ش�.
    		System.out.println("file_param:"+fileParam+" value:"+fileName);
    	}*/
    	
    	//1. ������ ������ ���� (delListSize�� �ִ��� �˻��Ѵ�.)
    	if(stringParamMap.containsKey("delListSize")) {
    		int delListSize = Integer.parseInt(stringParamMap.get("delListSize").toString());
    		for(int i=0; i<delListSize; i++) {
    			int del_photosubno = Integer.parseInt(stringParamMap.get("delList_"+i).toString()); //������ �������� photoSubNo
    			String result=dao.deleteSubPhoto(del_photosubno); //dao�� ���� DB���� �����ش�.
    			if(!result.equals("����")) {
    				System.out.println("�����߻�:"+result);
    				resultJson.put("result", result);
    				out.print(resultJson);
    				out.flush();
    			}
    		}
    	}
    	
    	//2. ���� �������� ���� ����
    	if(stringParamMap.containsKey("orderObject")) {
    		//GSON�� ����Ͽ� ��ü�� JSON���� ��ȯ
			Map<String,Object> orderMap = jsonfnc(stringParamMap.get("orderObject").toString()); //json ������ String���� Map���� ��ȯ���ش�.
			int sel_files_length=0; //���� �߰��� ���������� ����
			if(stringParamMap.containsKey("sel_files_length")) { 
				sel_files_length=Integer.parseInt(stringParamMap.get("sel_files_length").toString());
			}
			int orderMapSize=orderMap.size()-sel_files_length; //������ �ִ� ���ϸ� ������ �ٲ��ֱ� ���ؼ� �����߰��� ���ϰ�����ŭ ���ش�.
			for(int i=0; i<orderMapSize; i++) {
				int photo_ownNo=i;//���ϼ���
				String orderMapKey = String.valueOf(photo_ownNo);
				int photo_subNo=Integer.parseInt(orderMap.get(orderMapKey).toString()); //���� ������ ���� DB������  
				String result=dao.updatePhotosOrder(photo_ownNo, photo_subNo);
				if(!result.equals("����")) {
    				System.out.println("�����߻�:"+result);
    				resultJson.put("result", result);
    				out.print(resultJson);
    				out.flush();
    			}
			}
    	}
    	
    	//3. ������ �ִ� �������� ���� ����
    	if(stringParamMap.containsKey("updateSubPhotoContent")) {
    		Map<String,Object>updateSubPhotoContentMap = jsonfnc(stringParamMap.get("updateSubPhotoContent").toString());//json ������ String���� Map���� ��ȯ���ش�.
    		//updateSubPhotoContentMap�� Ű���� photo_subNo(DB������)
    		Set set = updateSubPhotoContentMap.keySet();
    		Iterator<Set> iterator = set.iterator();
    		while(iterator.hasNext()) {
    			String photo_subNo=String.valueOf(iterator.next()); //DB������ȣ
    			String contents = updateSubPhotoContentMap.get(photo_subNo).toString(); //������� ����
    			String result=dao.updateSubPhotoContent(Integer.parseInt(photo_subNo), contents);
    			if(!result.equals("����")) {
    				System.out.println("�����߻�:"+result);
    				resultJson.put("result", result);
    				out.print(resultJson);
    				out.flush();
    			}
    		}
    	}
    	
    	/*������ ���� �����������ϰ� �����߰��� ���� �и�*/
    	Map tempMap=null; //�ӽ� �����
    	Enumeration fileNames = multipartRequest.getFileNames();
    	while(fileNames.hasMoreElements()) {
    		String fileParam = (String)fileNames.nextElement();
    		String fileName = multipartRequest.getOriginalFileName(fileParam);   //����ڰ� ���ε��� ������ �̸���  �־��ش�.
			String fileRealName = multipartRequest.getFilesystemName(fileParam); //������ ���ε�� ������ �̸�
			tempMap = new HashMap<String,Object>(); 
    		if(fileParam.contains("newFile_")) { //���ο� ����
    			//�Խñ� ��ȣ �ʿ���.��
    			addFileParamMap = new HashMap<String,Object>(); //���� Ű��
    		}else { //���� ����
    	    	String photo_subNo = fileParam.replaceFirst("updateSubPhoto", ""); //�������� ���� ������ ���� DB������
    	    	tempMap.put("userUploadName", fileName); //����ڰ� ���ε��� �̸�
    	    	tempMap.put("serverUploadName", fileRealName);//������ ���ε�� �̸�
    	    	originalFileParamMap.put(photo_subNo, tempMap);
    		}
    		
    	}
    	
    	//4. ������ �ִ� �������� ���� ���ε�
    	if(stringParamMap.containsKey("updateListSize")) {
    		//String fileUserName=stringParamMap.get("");
    	}
    	
    	//5. ���� �߰��� ���� ���ε� ��  DB�� �߰��ϱ�
    	if(stringParamMap.containsKey("sel_files_length")) {
    		
    	}
    	
    }
    
    //������ ������ ���ε��ϴ� �޼ҵ�
    private void uploadFile() {
    	
    }
    
    //String���� �� data�� map���� ��ȯ
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
