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
 * 중복체크를 하기 위한 서블릿 (Client에서는 Ajax통신)
 * 1. 아이디
 * 2. 이메일
 * */


@WebServlet("/DuplicationCheck")
public class DuplicationCheck extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//넘어온 파라미터 값 받기
		String flag=request.getParameter("duplication");
		String userID=request.getParameter("id");
		String userEmail=request.getParameter("email");
		System.out.println("Ajax통신 요청:"+userID+"//"+userEmail);
		
		//응답할 JSON 형식으로 응답
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		JSONObject json = new JSONObject();
		
		if(flag!=null && flag.equals("ok")) {
			String result=null;
			try {
				DAO dao = new DAO();
				//ID중복값 확인
				if(userID!=null) {
					boolean spaceCheckFlag=spaceCheck(userID); //공백체크
					if(!spaceCheckFlag) {
						result=dao.register_duplication_check(userID, null);
					}else {
						json.put("spaceCheck", "false");
					}
				}
				//email 중복값 확인
				if(userEmail!=null) {
					boolean spaceCheckFlag=spaceCheck(userEmail); //공백체크
					if(!spaceCheckFlag) {
						result=dao.register_duplication_check(null, userEmail);
					}else {
						json.put("spaceCheck", "false");
					}
				}
				if(result==null) {
					System.out.println("중복아님");
					json.put("result", "ok");
				}else {
					System.out.println("중복임");
					json.put("result", "no");
				}
				out.print(json);
				out.flush();
				dao.closeConn();
				System.out.println("응답완료");
			} catch (NamingException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
	}
	
	/*입력된 값에 공백이 있는지 체크*/
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
