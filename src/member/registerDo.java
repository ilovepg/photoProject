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
				/*가입양식에 입력한 사용자의 정보를 받는다.*/
				request.setCharacterEncoding("utf-8"); //한글 깨지는 것을 방지하기 위해서 utf8로 인코딩 타입 변경
				String userID=request.getParameter("userID");
				String userPW=request.getParameter("userPW");
				String userPWCK=request.getParameter("userPWCK");
				String userEmail=request.getParameter("userEmail");
				
				/*추가로 필요한 ip정보를 가져온다.*/
				UtilClass util = new UtilClass();
				String clientIP=util.getClientIP(request);
				
				/*중복값이 있는지 없는지 확인하는 검증 프로세스*/
				ValidateClass validate = new ValidateClass();
				String validateResult=dao.validateData(userID,userPW,userPWCK,userEmail,validate, request);
				
				//response는 민감한 객체이기때문에 스크립트를 쓰려면 이렇게 해줘야한다.
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter out = response.getWriter();
				//out.println("<script>alert('계정이 등록 되었습니다'); location.href='이동주소';</script>");
	
				if(validateResult.equals("아이디길이")) {
					out.println("<script>alert('아이디는 2자이상입니다.'); history.go(-1);</script>");
				}else if(validateResult.equals("비밀번호길이")) {
					out.println("<script>alert('비밀번호는 10자 이상입니다.'); history.go(-1);</script>");
				}else if(validateResult.equals("비밀번호규칙")){
					out.println("<script>alert('비밀번호는 특수문자를 포함해야 합니다.'); history.go(-1);</script>");				
				}else if(validateResult.equals("비밀번호체크")) {
					out.println("<script>alert('비밀번호와 비밀번호 확인이 일치하지 않습니다.'); history.go(-1);</script>");
				}else if(validateResult.equals("아이디중복")) {
					out.println("<script>alert('아이디가 중복되었습니다.'); history.go(-1);</script>");
				}else if(validateResult.equals("이메일중복")) {
					out.println("<script>alert('이메일이 중복되었습니다.'); history.go(-1);</script>");
				}else { //성공했다면 userPW가 나온다.
					//성공했다면 DB에 그대로 insert
					userPW = validateResult;
					validateResult="성공";
					int insertResult=dao.insertAccountInfo(userID,userPW,userEmail,clientIP);
					System.out.println("DB결과:"+insertResult);
					out.println("<script>alert('회원가입을 환영합니다! 로그인 페이지로 이동합니다.'); location.href='/photoProject/login.pro';</script>");
				}
				System.out.println("회원가입결과:"+validateResult);
				dao.closeConn(); //커넥션 닫아준다.
			} catch (NamingException | SQLException e) {
				e.printStackTrace();
			}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
