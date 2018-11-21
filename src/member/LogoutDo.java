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
		//최종 로그아웃 시간을 DB에 저장한다.
		try {
			DAO dao = new DAO();
			//나중에 이 result값으로 어떤것을 할수 있을까?
			int result=dao.logoutTimeSave(userID); //결과값 1이면 성공, 0이면 실패
			dao.closeConn();
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
		}
		
		session.invalidate(); //세션정보 삭제
		response.sendRedirect("/photoProject/main.pro");
		
	}
}
