package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import api.MainProAPI;
import api.PhotoBoardViewAPI;

/**
 * Ŭ���̾�Ʈ���� API��û���ϸ� API�� �����ִ� ��Ʈ�ѷ��Դϴ�.
 */
@WebServlet("/apiController")
public class APIController extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String apiName = request.getParameter("apiName"); //ȣ��� API����
		String methodName = request.getParameter("methodName"); //ȣ��� �޼ҵ�
		
		//������ JSON �������� ����
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		if(apiName.equals("MainProAPI")) { //main API
			MainProAPI mainProAPI = new MainProAPI();
			if(methodName.equals("getMainPhoto")) { //�����̹����� ������� �������� �޼ҵ�
				int offset = Integer.parseInt(request.getParameter("offset").toString()); //�� ��° �̹������� �����;��ϴ��� offset
				JSONObject jsonObject=mainProAPI.getMainPhoto(offset);
				if(jsonObject!=null) { //������� null�� �ƴ϶�� ����
					out.print(jsonObject);
					out.flush();
					System.out.println("����Ϸ�");
				}
			}else if(methodName.equals("")) {
				
			}else if(methodName.equals("")) {
				
			}
		}else if(apiName.equals("PhotoBoardViewAPI")) { // photoView API
			PhotoBoardViewAPI api = new PhotoBoardViewAPI();
			if(methodName.equals("getPhotoDetailData")) { //photoViewData�� �������� �޼ҵ�
				int photoBoardNo = Integer.parseInt(request.getParameter("photoBoardNo").toString());
				String result=api.getPhotoDetailData(photoBoardNo);
				
				JSONObject jsonObject=new JSONObject(); //������ ���� JSON��ü����
				
				if(result==null) { //result�� null�� �Ѿ�Դٸ�
					jsonObject.put("PhotoBoardViewAPI_getPhotoDetailData_result", "fail"); //�����߻��� �˸���.
				}else {
					jsonObject.put("PhotoBoardViewAPI_getPhotoDetailData_result", result); //�����Ͱ� ����� �Ѿ�������� �����͸� �����ش�.
				}
				//����
				out.print(jsonObject);
				out.flush();
			}
		}
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
