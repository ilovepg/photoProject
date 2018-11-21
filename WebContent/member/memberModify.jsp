<%@page import="sun.misc.Request"%>
<%@page import="util.UtilClass"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<%	
		//세션에 authCheck가 없다면 403에러 발생 (authCheck는 modifyPasswordCheck에서 정상적인 인증을 거치면 생성된다.)
		//세션의 authChecks는 이 페이지의 코드 마지막부분에서 삭제된다.
		Object sessionModifyPageAuthKey = session.getAttribute("authCheck");
		if(sessionModifyPageAuthKey==null){
			response.sendError(HttpServletResponse.SC_FORBIDDEN); //403 에러 발생
		}else{
			
		
	%>
	<% String contextPath=request.getContextPath();  %>
	<meta charset="UTF-8">
	<title>회원수정</title>
	<meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
	<link rel="stylesheet" href="<%=contextPath%>/Resource/assets/css/main.css" />
	<noscript><link rel="stylesheet" href="<%=contextPath%>/Resource/assets/css/noscript.css" /></noscript>
</head>
<body>
	<!-- header -->
	<%@include file="/header/header.jsp" %>
	<%	
		/*로그인이 안되어있다면 예외처리 해준다.*/
		Object userID_IsNull=session.getAttribute("userID");
		if(userID_IsNull == null){
			out.println("<script>alert('로그인을 먼저 해주세요.'); location.href='/photoProject/login.pro';</script>");
		}else {
			//로그인이 되어있다면 세션에서 로그인된 아이디를 가져온다.
			String userIDModifyPage = session.getAttribute("userID").toString();	
			String userEmailModifyPage = session.getAttribute("userEmail").toString();
		
	%>
	<!-- Modify Form -->
	<section style="text-align:center; margin: 100px auto;">
	<h2>Modify</h2>
	
	<form method="post" action="memberModifyDo" onsubmit="password_encryption();">
	 	<div class="row gtr-uniform" style="width:45%; margin: 40px auto;">
				<div class="col-2 col-4-xsmall" style="margin:10px 0px;">
					아이디
				</div>
				<div class="col-10 col-4-xsmall">
					<%=userIDModifyPage %>
				</div>
				<div class="col-2 col-4-xsmall" style="margin:10px 0px;">
					비밀번호
				</div>
				<div class="col-10 col-8-xsmall">
					<input type="password" name="userPWTEXT" id="userPWTEXT"
						placeholder="10자이상 특수문자포함" onkeyup="check_pw();"/>
					<input type="hidden" id="userPW" name="userPW">
					<input type="hidden" id="RSAModulus" value="${RSAModulus}"/>
					<input type="hidden" id="RSAExponent" value="${RSAExponent}"/>
				</div>
				
				<div class="col-2 col-4-xsmall" style="margin:5px 0px;">
					비밀번호 <br>확인
				</div>
				<div class="col-10 col-8-xsmall">
					<input type="password" name="userPWCKTEXT" id="userPWCKTEXT"
						placeholder="10자이상 특수문자포함" onkeyup="check_pw();"/>
					<input type="hidden" id="userPWCK" name="userPWCK">
					<input type="hidden" id="RSAModulusCk" value="${RSAModulus}"/>
					<input type="hidden" id="RSAExponentCk" value="${RSAExponent}"/>
					<div id="passwordCheckDIV"></div>
					<input type="hidden" id="pwValidateCheck" name="pwValidateCheck" value="0">
				</div>
				
				<div class="col-2 col-4-xsmall" style="margin:10px 0px;">
					이메일
				</div>
				<div class="col-10 col-8-xsmall">
					<input type="email" name="userEmail" id="userEmail"
						placeholder="유효한 이메일을 입력해주세요." value="<%=userEmailModifyPage %>" />
					<div id="eMailDuplicationDIV">
						<input type="hidden" value="0" id="eMailDuplicationCheck" name="eMailDuplicationCheck"/>
					</div>
				</div>
			</div>
		
		<button class="button primary" type="submit">수정</button>
		<button class="button" type="button" onclick="window.location.href='/photoProject/main.pro'">취소</button>
	</form>
	</section>
	
	<!-- HTML5 Up Scripts -->
	<!-- default Scripts -->
	<%@include file="/js/jsFile.jsp" %>
	
	<!-- RSA사용을 위한 js -->
	<script src="<%=contextPath%>/Resource/assets/js/passwordEnc/rsa.js"></script>
	<script src="<%=contextPath%>/Resource/assets/js/passwordEnc/jsbn.js"></script>
	<script src="<%=contextPath%>/Resource/assets/js/passwordEnc/prng4.js"></script>
	<script src="<%=contextPath%>/Resource/assets/js/passwordEnc/rng.js"></script>
	
	<!-- 비밀번호 암호화 JS -->
	<script type="text/javascript">
		function password_encryption(){
			/*비밀번호 암호화*/
			var pw = $("#userPWTEXT");
			
			var rsa = new RSAKey();
			rsa.setPublic($('#RSAModulus').val(),$('#RSAExponent').val());
			$("#userPW").val(rsa.encrypt(pw.val()));

	        /* 기존 패스워드 text 초기화 */
	        pw.val("");

		}
	</script>
	
	<%	
		/*로그인 결과 처리*/
		Object ob = session.getAttribute("loginResult");
		String loginResult=null;
		if(ob!=null){
			loginResult=ob.toString();
		}
	
		if(loginResult!=null){
			out.println("<script>alert('"+loginResult+"');</script>");
			session.removeAttribute("loginResult"); //값을 확인했으면 지워줘야한다.
		}
		} //로그인
		session.removeAttribute("authCheck"); //값을 확인했으면 지워줘야한다.
		} //세션
	%>
	
</body>
</html>