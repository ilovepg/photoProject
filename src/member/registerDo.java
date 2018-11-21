package member;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DAO;
import util.UtilClass;
import util.ValidateClass;


@WebServlet("/registerDo")
public class registerDo extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
			try {
				DAO dao = new DAO();
				/*���Ծ�Ŀ� �Է��� ������� ������ �޴´�.*/
				request.setCharacterEncoding("utf-8"); //�ѱ� ������ ���� �����ϱ� ���ؼ� utf8�� ���ڵ� Ÿ�� ����
				String userID=request.getParameter("userID");
				String userPW=request.getParameter("userPW");
				String userPWCK=request.getParameter("userPWCK");
				String userEmail=request.getParameter("userEmail");
				
				/*�߰��� �ʿ��� ip������ �����´�.*/
				UtilClass util = new UtilClass();
				String clientIP=util.getClientIP(request);
				
				/*�ߺ����� �ִ��� ������ Ȯ���ϴ� ���� ���μ���*/
				ValidateClass validate = new ValidateClass();
				String validateResult=dao.validateData(userID,userPW,userPWCK,userEmail,validate, request);
				
				//response�� �ΰ��� ��ü�̱⶧���� ��ũ��Ʈ�� ������ �̷��� ������Ѵ�.
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter out = response.getWriter();
				//out.println("<script>alert('������ ��� �Ǿ����ϴ�'); location.href='�̵��ּ�';</script>");
	
				if(validateResult.equals("���̵����")) {
					out.println("<script>alert('���̵�� 2���̻��Դϴ�.'); history.go(-1);</script>");
				}else if(validateResult.equals("��й�ȣ����")) {
					out.println("<script>alert('��й�ȣ�� 10�� �̻��Դϴ�.'); history.go(-1);</script>");
				}else if(validateResult.equals("��й�ȣ��Ģ")){
					out.println("<script>alert('��й�ȣ�� Ư�����ڸ� �����ؾ� �մϴ�.'); history.go(-1);</script>");				
				}else if(validateResult.equals("��й�ȣüũ")) {
					out.println("<script>alert('��й�ȣ�� ��й�ȣ Ȯ���� ��ġ���� �ʽ��ϴ�.'); history.go(-1);</script>");
				}else if(validateResult.equals("���̵��ߺ�")) {
					out.println("<script>alert('���̵� �ߺ��Ǿ����ϴ�.'); history.go(-1);</script>");
				}else if(validateResult.equals("�̸����ߺ�")) {
					out.println("<script>alert('�̸����� �ߺ��Ǿ����ϴ�.'); history.go(-1);</script>");
				}else { //�����ߴٸ� userPW�� ���´�.
					//�����ߴٸ� DB�� �״�� insert
					userPW = validateResult;
					validateResult="����";
					int insertResult=dao.insertAccountInfo(userID,userPW,userEmail,clientIP);
					System.out.println("DB���:"+insertResult);
					out.println("<script>alert('ȸ�������� ȯ���մϴ�! �α��� �������� �̵��մϴ�.'); location.href='/photoProject/login.pro';</script>");
				}
				System.out.println("ȸ�����԰��:"+validateResult);
				dao.closeConn(); //Ŀ�ؼ� �ݾ��ش�.
			} catch (NamingException | SQLException e) {
				e.printStackTrace();
			}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
