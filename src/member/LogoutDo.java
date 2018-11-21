package member;

import java.io.IOException;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.DAO;


@WebServlet("/logoutDo")
public class LogoutDo extends HttpServlet {
	
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession();
		String userID = session.getAttribute("userID").toString();
		//���� �α׾ƿ� �ð��� DB�� �����Ѵ�.
		try {
			DAO dao = new DAO();
			//���߿� �� result������ ����� �Ҽ� ������?
			int result=dao.logoutTimeSave(userID); //����� 1�̸� ����, 0�̸� ����
			dao.closeConn();
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
		}
		
		session.invalidate(); //�������� ����
		response.sendRedirect("/photoProject/main.pro");
		
	}
}
