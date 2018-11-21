package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import dao.DAO;
import test.DaoTest;
import util.ExceptionClass;
import util.UtilClass;
import util.ValidateClass;

/**
 * View�� �����ִ� ��Ʈ�ѷ��Դϴ�.
 */
@WebServlet("*.pro")
public class BasicController extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI(); // ����ڰ� ��û�� �ּ�
		//����� ���ڵ� Ÿ�� ����
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		String view = ""; //����� ������
		System.out.println("��û�� �ּ�:"+uri);
		if(uri.equals("/photoProject/modifyPassword.pro")) { //ȸ������ ��й�ȣ ����������
			UtilClass util = new UtilClass();
			util.initRsa(request);
			view="/member/modifyPassword.jsp";
		}else if(uri.equals("/photoProject/register.pro")) { //ȸ������ ������
			UtilClass util = new UtilClass();
			util.initRsa(request);
			view="/member/register.jsp";
		}else if(uri.equals("/photoProject/main.pro")) { //����������
			try {
				//�������������� ���λ����� ����Ϸ� ǥ���ϱ� ���ؼ� dao���� ������ �����´�.
				DAO dao = new DAO();
				Map<Integer,Object>photoMainMap=(HashMap)dao.photoBoard_mainPhotoThumbnail_select(0);
				//dao���� �Ѿ�� ���� ������������ ���ĵǾ������Ƿ� �������� ������ ���ؼ� comparator��ü�� ���� ������������ �ٲ��ش�.
				TreeMap<Integer,Object> photoMainTreeMap = new TreeMap<Integer,Object>(photoMainMap);
				Set<Integer> keySet = photoMainMap.keySet();
				Iterator<Integer> keyiterator = photoMainTreeMap.descendingKeySet().iterator();
				List<Integer> photoMainMapKeyList = new ArrayList<Integer>();
				while(keyiterator.hasNext()) {
					int key = keyiterator.next();
					photoMainMapKeyList.add(key);
				}

				if(photoMainMap!=null) {
					request.setAttribute("photoData", photoMainMap); //request ������ ���� �����͸� �Ǿ ������.
					request.setAttribute("photoDataKeyList", photoMainMapKeyList);
					view="/index.jsp";
				}
				dao.closeConn();
			} catch (NamingException | SQLException e) { 
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500���� �������� ������.
				e.printStackTrace();
			}
			
		}else if(uri.equals("/photoProject/login.pro")) { //�α��� ������
			UtilClass util = new UtilClass();
			util.initRsa(request);
			view="/member/login.jsp";
		}else if(uri.equals("/photoProject/memberModify.pro")) { //ȸ������ ������
			UtilClass util = new UtilClass();
			util.initRsa(request);
			view="/member/memberModify.jsp";
		}else if(uri.equals("/photoProject/photoUploadTable.pro")) { //���� �۾���
			view = "photoUploadTable.jsp";
		}else if(uri.equals("/photoProject/test.pro")) { //� ���� �׽�Ʈ�� �� ���
			try {
				DaoTest test = new DaoTest();
				test.tagInsertTest();
			} catch (NamingException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(uri.equals("/photoProject/photoView.pro")) { //����Խ��� ��������(�� ������)
			view = "/photoView/photoView.jsp";
		}else if(uri.equals("/photoProject/photoModify.pro")) { //����Խ��� ���� ������
			ValidateClass validate = new ValidateClass();
			ExceptionClass exception = new ExceptionClass();
			Object userID=validate.isNullSessionValue(request, "userID"); //���ǿ� �����ִ��� ����(���⼭�� ���̵�)
			if(userID==null) { //���� ���ٸ� �α����� �ȵȰ�
				exception.noLoginException(response);
			}else {
				//���� ���̵��� �۾��̿� �´��� ����.
				Boolean result=validate.loginUserCompare(request, request.getParameter("photoBoardWriter"));
				if(result==false) {
					exception.javaScriptToClient(response, "�߸��� �����Դϴ�.");
					return ;
				}
				view = "/photoBoard/photoModifyTable.jsp";
			}
			
		}else if(uri.equals("")) {
			
		}else if(uri.equals("")) {
			
		}
		System.out.println("�����ּ�:"+view);
		if(!view.equals("")) {
			request.getRequestDispatcher(view).forward(request, response);
		}
		
	}
	
	/*doPost�� ������ POST�� ��û�� �������� HTTP Status 405 - HTTP method POST is not supported by this URL ������ �߻�.*/
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
	

}
