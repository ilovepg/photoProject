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
 * 로그인 서블릿
 */
@WebServlet("/loginDo")
public class LoginDo extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			request.setCharacterEncoding("utf-8"); //한글 깨지는 것을 방지하기 위해서 utf8로 인코딩 타입 변경
			String url=""; //리다이렉션할 페이지.
			HttpSession session = request.getSession();
			
			DAO dao = new DAO();
			/*로그인 폼에서 입력한 사용자의 아이디, 비밀번호를 받는다.*/
			String userID = request.getParameter("userID");
			String userPW = request.getParameter("userPW");
			
			/*비밀번호 복호화*/
			ValidateClass validate = new ValidateClass();
			userPW=validate.decryptRsa(userPW,request);
			
			/*로그인하기 위한 아이디, 비밀번호 확인*/
			if(userID!=null && userPW!=null) {
				String result=dao.loginAuthentication(userID, userPW);
				String[] resultArray=result.split("//"); //로그인이 성공했다면 //를 기준으로 userEmail까지 같이 오니까 스플릿 형식으로 나눠준다.
				if(resultArray[0].equals("로그인완료")) {
					session.setAttribute("userID",userID);
					session.setAttribute("userEmail", resultArray[1]);
					System.out.println("사용자 로그인:"+userID + "사용자 이메일:"+resultArray[1]);
					url="/photoProject/main.pro";
				}else if(resultArray[0].equals("아이디없음")) {
					session.setAttribute("loginResult", "아이디 또는 비밀번호를 확인해주세요.");
					url="/photoProject/login.pro";
				}else if(resultArray[0].equals("암호다름")) {
					session.setAttribute("loginResult", "아이디 또는 비밀번호를 확인해주세요.");
					url="/photoProject/login.pro";
				}else if(resultArray[0].equals("오류")) {
					session.setAttribute("loginResult", "서버에 오류가 생겼습니다. 잠시 후에 다시 시도해주세요.");
					url="/photoProject/login.pro";
				}
				
			}else {
				url = "/photoProject/login.pro";
				session.setAttribute("loginResult", "아이디 또는 비밀번호를 입력해주세요.");		
			}
			response.sendRedirect(url);
			dao.closeConn();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
