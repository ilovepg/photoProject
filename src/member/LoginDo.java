package member;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.DAO;
import util.ValidateClass;

/**
 * �α��� ����
 */
@WebServlet("/loginDo")
public class LoginDo extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			request.setCharacterEncoding("utf-8"); //�ѱ� ������ ���� �����ϱ� ���ؼ� utf8�� ���ڵ� Ÿ�� ����
			String url=""; //�����̷����� ������.
			HttpSession session = request.getSession();
			
			DAO dao = new DAO();
			/*�α��� ������ �Է��� ������� ���̵�, ��й�ȣ�� �޴´�.*/
			String userID = request.getParameter("userID");
			String userPW = request.getParameter("userPW");
			
			/*��й�ȣ ��ȣȭ*/
			ValidateClass validate = new ValidateClass();
			userPW=validate.decryptRsa(userPW,request);
			
			/*�α����ϱ� ���� ���̵�, ��й�ȣ Ȯ��*/
			if(userID!=null && userPW!=null) {
				String result=dao.loginAuthentication(userID, userPW);
				String[] resultArray=result.split("//"); //�α����� �����ߴٸ� //�� �������� userEmail���� ���� ���ϱ� ���ø� �������� �����ش�.
				if(resultArray[0].equals("�α��οϷ�")) {
					session.setAttribute("userID",userID);
					session.setAttribute("userEmail", resultArray[1]);
					System.out.println("����� �α���:"+userID + "����� �̸���:"+resultArray[1]);
					url="/photoProject/main.pro";
				}else if(resultArray[0].equals("���̵����")) {
					session.setAttribute("loginResult", "���̵� �Ǵ� ��й�ȣ�� Ȯ�����ּ���.");
					url="/photoProject/login.pro";
				}else if(resultArray[0].equals("��ȣ�ٸ�")) {
					session.setAttribute("loginResult", "���̵� �Ǵ� ��й�ȣ�� Ȯ�����ּ���.");
					url="/photoProject/login.pro";
				}else if(resultArray[0].equals("����")) {
					session.setAttribute("loginResult", "������ ������ ������ϴ�. ��� �Ŀ� �ٽ� �õ����ּ���.");
					url="/photoProject/login.pro";
				}
				
			}else {
				url = "/photoProject/login.pro";
				session.setAttribute("loginResult", "���̵� �Ǵ� ��й�ȣ�� �Է����ּ���.");		
			}
			response.sendRedirect(url);
			dao.closeConn();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
