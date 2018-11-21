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
 * 회원정보 수정 페이지 전 인증수단으로 패스워드를 검증하는 서블릿
 */
@WebServlet("/modCheck")
public class modifyPasswordCheck extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8"); //한글 깨지는 것을 방지하기 위해서 utf8로 인코딩 타입 변경
		
		/*세션이 있어야 처리하게끔 한다.*/
		HttpSession session = request.getSession();
		Object isNullCheck=session.getAttribute("userID");
		String url=""; //리다이렉션할 페이지.
		
		//만약에 세션이 없다면 오류메시지
		if(isNullCheck==null) {
			System.out.println("modifyPasswordCheck 세션이 없어서 종료됨.");
			url="/photoProject/main.pro";
			session.setAttribute("passwordCheckResult", "먼저 로그인을 해주세요.");
			
		}
		
		try {
			DAO dao = new DAO();
			String userID = session.getAttribute("userID").toString();
			String userPW = request.getParameter("userPW");
			
			/*비밀번호 복호화*/
			ValidateClass validate = new ValidateClass();
			userPW = validate.decryptRsa(userPW, request);
			
			/*로그인하는 메소드로 암호가 맞는지 검증 (같은 메소드를 사용해도됨 현재까지는)*/
			String result=dao.loginAuthentication(userID,userPW); 
			String[] resultArray=result.split("//"); //로그인이 성공했다면 //를 기준으로 userEmail까지 같이 오니까 스플릿 형식으로 나눠준다.
			
			if(resultArray[0].equals("로그인완료")) { 
				//인증완료
				url="/photoProject/memberModify.pro";
				/*키값을 해쉬방식으로 암호화한다.*/
				/*세션에 이 암호화된 키값이 있어야 memberModify.jsp 페이지에 접근이 가능하다.*/
				UtilClass util = new UtilClass();
				session.setAttribute("authCheck", util.HashEncryption(util.getModifyAuthKey(),"SHA-256"));
			}else if(resultArray[0].equals("암호다름")){
				//암호가 다를 때
				session.setAttribute("passwordCheckResult", "비밀번호를 확인해주세요.");
				url="/photoProject/modifyPassword.pro";
			}
			
			dao.closeConn(); //사용이 끝났으면 Connection을 닫아준다.
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		//request.getRequestDispatcher(url).forward(request, response);
		response.sendRedirect(url);
		
		
		
	}

}
