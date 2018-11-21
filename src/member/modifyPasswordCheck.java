package member;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.DAO;
import util.UtilClass;
import util.ValidateClass;

/**
 * ȸ������ ���� ������ �� ������������ �н����带 �����ϴ� ����
 */
@WebServlet("/modCheck")
public class modifyPasswordCheck extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8"); //�ѱ� ������ ���� �����ϱ� ���ؼ� utf8�� ���ڵ� Ÿ�� ����
		
		/*������ �־�� ó���ϰԲ� �Ѵ�.*/
		HttpSession session = request.getSession();
		Object isNullCheck=session.getAttribute("userID");
		String url=""; //�����̷����� ������.
		
		//���࿡ ������ ���ٸ� �����޽���
		if(isNullCheck==null) {
			System.out.println("modifyPasswordCheck ������ ��� �����.");
			url="/photoProject/main.pro";
			session.setAttribute("passwordCheckResult", "���� �α����� ���ּ���.");
			
		}
		
		try {
			DAO dao = new DAO();
			String userID = session.getAttribute("userID").toString();
			String userPW = request.getParameter("userPW");
			
			/*��й�ȣ ��ȣȭ*/
			ValidateClass validate = new ValidateClass();
			userPW = validate.decryptRsa(userPW, request);
			
			/*�α����ϴ� �޼ҵ�� ��ȣ�� �´��� ���� (���� �޼ҵ带 ����ص��� ���������)*/
			String result=dao.loginAuthentication(userID,userPW); 
			String[] resultArray=result.split("//"); //�α����� �����ߴٸ� //�� �������� userEmail���� ���� ���ϱ� ���ø� �������� �����ش�.
			
			if(resultArray[0].equals("�α��οϷ�")) { 
				//�����Ϸ�
				url="/photoProject/memberModify.pro";
				/*Ű���� �ؽ�������� ��ȣȭ�Ѵ�.*/
				/*���ǿ� �� ��ȣȭ�� Ű���� �־�� memberModify.jsp �������� ������ �����ϴ�.*/
				UtilClass util = new UtilClass();
				session.setAttribute("authCheck", util.HashEncryption(util.getModifyAuthKey(),"SHA-256"));
			}else if(resultArray[0].equals("��ȣ�ٸ�")){
				//��ȣ�� �ٸ� ��
				session.setAttribute("passwordCheckResult", "��й�ȣ�� Ȯ�����ּ���.");
				url="/photoProject/modifyPassword.pro";
			}
			
			dao.closeConn(); //����� �������� Connection�� �ݾ��ش�.
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		//request.getRequestDispatcher(url).forward(request, response);
		response.sendRedirect(url);
		
		
		
	}

}
