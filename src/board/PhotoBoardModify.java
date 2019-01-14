package board;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.internal.LinkedTreeMap;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

/*
 * ���� �Խ��ǿ��� �����̹���, �����̹���, �����̹����� ����, ������ �޾Ƽ� DB�� �����ϴ� ����
 * 
 * */
@WebServlet("/photoBoardModify")
public class PhotoBoardModify extends HttpServlet {
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	request.setCharacterEncoding("UTF-8"); //�ѱ��� ������ ������ UTF8�� ���ڵ�
    	
    	// Linux /var/lib/tomcat8/webapps/photoProject/imgs
    	// Windows C:\Users\DevOps\eclipse-workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp3\wtpwebapps\photoProject\imgs
    	// ȸ�� : C:\Users\DevOps\workspaceHomeRevision\.metadata\.plugins\org.eclipse.wst.server.core\tmp1\wtpwebapps\photoProject\imgs
    	String directory = request.getServletContext().getRealPath("/imgs"); 
    	int fileMaxSize = 1024*1024*100; //100MB ��������� ���ε� ����
    	String encoding = "UTF-8"; 
    	System.out.println("directory:"+directory);
    	Map<String,Object> stringParamMap = new LinkedTreeMap<String,Object>(); //������ �ƴ� �ٸ� Ű�� ����ִ� ��
    	Map<String,Object> fileParamMap = new LinkedTreeMap<String,Object>(); //���� Ű��
    	
    	MultipartRequest multipartRequest = 
				new MultipartRequest(request, directory, fileMaxSize, encoding,
						new DefaultFileRenamePolicy());
    	
    	Enumeration param1 = multipartRequest.getParameterNames();
    	while(param1.hasMoreElements()) {
    		String param = (String)param1.nextElement();
    		System.out.println("param:"+param+" value:"+multipartRequest.getParameter(param));
    	}
    	Enumeration fileNames = multipartRequest.getFileNames();
    	while(fileNames.hasMoreElements()) {
    		String fileParam = (String)fileNames.nextElement();
    		String fileName = multipartRequest.getOriginalFileName(fileParam);   //����ڰ� ���ε��� ������ �̸���  �־��ش�.
    		System.out.println("file_param:"+fileParam+" value:"+fileName);
    	}
    	
    	
    	
    }

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
