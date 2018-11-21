<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
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
		}
	%>
	<!-- Modify Form -->
	<section style="text-align:center; margin: 100px auto;">
	<h2>Input Your Password</h2>
	
	<form method="post" action="modCheck" onsubmit="password_encryption();">
	 	<div style="width:30%; margin: 40px auto;">
			<div class="col-6 col-12-xsmall">
				<input type="password" name="userPWTEXT" id="userPWTEXT" placeholder="Password" />
				<input type="hidden" id="userPW" name="userPW">
				<input type="hidden" id="RSAModulus" value="${RSAModulus}"/>
				<input type="hidden" id="RSAExponent" value="${RSAExponent}"/>
			</div>
		</div>
		
		<button class="button primary" type="submit">SUBMIT</button>
		<button class="button" type="button" onclick="history.go(-1)">BACK</button>
		<!-- <button class="button" type="button" onclick="window.location.href='index.jsp'">HOME</button> -->
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
		/*암호 입력결과 처리*/
		Object ob = session.getAttribute("passwordCheckResult");
		String passwordCheckResult=null;
		if(ob!=null){
			passwordCheckResult=ob.toString();
		}
	
		if(passwordCheckResult!=null){
			out.println("<script>alert('"+passwordCheckResult+"');</script>");
			session.removeAttribute("passwordCheckResult"); //값을 확인했으면 지워줘야한다.
		}
	%>
	
</body>
</html>