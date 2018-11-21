<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>로그인</title>
	<meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
	<link rel="stylesheet" href="Resource/assets/css/main.css" />
	<noscript><link rel="stylesheet" href="Resource/assets/css/noscript.css" /></noscript>
</head>
<body>
	<!-- header -->
	<%@include file="/header/header.jsp" %>
	<%	
		/*이미 로그인 되어있으면 예외처리해준다.*/
		Object userID_IsNull=session.getAttribute("userID");
		if(userID_IsNull != null){
			out.println("<script>alert('이미 로그인 되었습니다.'); history.go(-1);</script>");
		}
	%>
	<!-- Login Form -->
	<section style="text-align:center; margin: 100px auto;">
	<h2>Login</h2>
	
	<form method="post" name="loginForm" action="loginDo" onsubmit="return interChanger();">
	 	<div style="width:30%; margin: 40px auto;">
			<div class="col-6 col-12-xsmall">
				<input type="text" name="userID" id="userID" placeholder="ID" />
				
			</div>
		
			<div class="col-6 col-12-xsmall">
				<input type="password" name="userPWTEXT" id="userPWTEXT" placeholder="Password" />
				<input type="hidden" id="userPW" name="userPW">
				<input type="hidden" id="RSAModulus" value="${RSAModulus}"/>
				<input type="hidden" id="RSAExponent" value="${RSAExponent}"/>
			</div>
		</div>
		
		<button class="button primary" type="submit">Login</button>
		<button class="button" type="button" onclick="window.location.href='/photoProject/register.pro'">Register</button>
		<button class="button" type="button" onclick="window.location.href='/photoProject/main.pro'">HOME</button>
	</form>
	</section>
	
	<!-- HTML5 Up Scripts -->
	<!-- default Scripts -->
	<%@include file="/js/jsFile.jsp" %>
	
	<!-- RSA사용을 위한 js -->
	<script src="./Resource/assets/js/passwordEnc/rsa.js"></script>
	<script src="./Resource/assets/js/passwordEnc/jsbn.js"></script>
	<script src="./Resource/assets/js/passwordEnc/prng4.js"></script>
	<script src="./Resource/assets/js/passwordEnc/rng.js"></script>
	
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
		
		<!-- 서버로 보내기 전에 아이디, 비밀번호 예외처리 -->
		<!-- 1. 아이디 Null값 체크 -->
		<!-- 2. 비밀번호 Null값 체크 -->
		function finalCheck(){
			/* 웬만하면 바닐라 자바스크립트로 하기 */
			/* 1. 아이디 Null값 체크 */
			var du = document.loginForm; //로그인 form 가져오기.
			var id = du.userID.value;
			var pw = du.userPWTEXT.value;
			
			if(!id){
				alert('아이디를 입력해주세요.');
				du.userID.focus();
				return false;
			}
			
			if(!pw){
				alert('패스워드를 입력해주세요.');
				du.userPWTEXT.focus();
				return false;
			}
			return true;
		}
		
		/* onsubmit이 2개 이상이므로(finalCheck, password_encryption)여기서 나눠준다. */
		function interChanger(){
			var result=finalCheck();
			if(result==false){
				return false;
			}else{
				password_encryption();
			}
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
	%>
	
</body>
</html>