package util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

/*예외처리를 관리하는 클래스*/
public class ExceptionClass {
	
	/*로그인이 안되어있을 때 예외처리*/
	public void noLoginException(HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('로그인을 해주세요.');");
		out.println("location.replace('login.pro');");
		out.println("</script>");
		out.flush();
		return ;
	}
	
	/*예외내용을 클라이언트에게 자바스크립트로 어떤 내용을 보내기 위한 메소드
	 * @Param response : 응답 객체
	 * @Param comment : 보낼 내용
	 * */
	public void javaScriptToClient(HttpServletResponse response,String comment) throws IOException {
		response.setCharacterEncoding("UTF-8"); 
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('"+comment+"');");
		out.println("history.back();");
		out.println("</script>");
		out.flush();
		return ;
	}
	
}
