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

import org.json.simple.JSONObject;

import dao.DAO;

/**
 * �ߺ�üũ�� �ϱ� ���� ���� (Client������ Ajax���)
 * 1. ���̵�
 * 2. �̸���
 * */


@WebServlet("/DuplicationCheck")
public class DuplicationCheck extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//�Ѿ�� �Ķ���� �� �ޱ�
		String flag=request.getParameter("duplication");
		String userID=request.getParameter("id");
		String userEmail=request.getParameter("email");
		System.out.println("Ajax��� ��û:"+userID+"//"+userEmail);
		
		//������ JSON �������� ����
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		
		if(flag!=null && flag.equals("ok")) {
			String result=null;
			try {
				DAO dao = new DAO();
				//ID�ߺ��� Ȯ��
				if(userID!=null) {
					boolean spaceCheckFlag=spaceCheck(userID); //����üũ
					if(!spaceCheckFlag) {
						result=dao.register_duplication_check(userID, null);
					}else {
						json.put("spaceCheck", "false");
					}
				}
				//email �ߺ��� Ȯ��
				if(userEmail!=null) {
					boolean spaceCheckFlag=spaceCheck(userEmail); //����üũ
					if(!spaceCheckFlag) {
						result=dao.register_duplication_check(null, userEmail);
					}else {
						json.put("spaceCheck", "false");
					}
				}
				if(result==null) {
					System.out.println("�ߺ��ƴ�");
					json.put("result", "ok");
				}else {
					System.out.println("�ߺ���");
					json.put("result", "no");
				}
				out.print(json);
				out.flush();
				dao.closeConn();
				System.out.println("����Ϸ�");
			} catch (NamingException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
	}
	
	/*�Էµ� ���� ������ �ִ��� üũ*/
	public boolean spaceCheck(String spaceCheck)
	{
	    for(int i = 0 ; i < spaceCheck.length() ; i++)
	    {
	        if(spaceCheck.charAt(i) == ' ')
	            return true;
	    }
	    return false;
	}

}
