package util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

/*����ó���� �����ϴ� Ŭ����*/
public class ExceptionClass {
	
	/*�α����� �ȵǾ����� �� ����ó��*/
	public void noLoginException(HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('�α����� ���ּ���.');");
		out.println("location.replace('login.pro');");
		out.println("</script>");
		out.flush();
		return ;
	}
	
	/*���ܳ����� Ŭ���̾�Ʈ���� �ڹٽ�ũ��Ʈ�� � ������ ������ ���� �޼ҵ�
	 * @Param response : ���� ��ü
	 * @Param comment : ���� ����
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
