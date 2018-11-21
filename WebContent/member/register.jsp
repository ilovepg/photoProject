<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입</title>
<meta name="viewport"
	content="width=device-width, initial-scale=1, user-scalable=no" />
<link rel="stylesheet" href="Resource/assets/css/main.css" />
<noscript>
	<link rel="stylesheet" href="Resource/assets/css/noscript.css" />
</noscript>


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
	
	<!-- Register Form -->
	<section style="text-align: center; margin: 100px auto;">
		<h2>Register</h2>

		<form method="post" action="registerDo" name="registerForm"
			onsubmit="return validate();">
			<div class="row gtr-uniform" style="width:45%; margin: 40px auto;">
				<div class="col-2 col-4-xsmall" style="margin:10px 0px;">
					아이디
				</div>
				<div class="col-10 col-8-xsmall">
					<input type="text" name="userID" id="userID" value=""
						placeholder="2자 이상 (한글 영문조합 가능)" />
					<div id="duplicationDIV"><input type="hidden" value="0" id="duplicationCheck" name="duplicationCheck"/></div>
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
						placeholder="유효한 이메일을 입력해주세요." />
					<div id="eMailDuplicationDIV"><input type="hidden" value="0" id="eMailDuplicationCheck" name="eMailDuplicationCheck"/></div>
				</div>
			</div>
			

			<button class="button" type="submit">가입</button>
			<button class="button" type="button" onclick="window.history.back();">취소</button>
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
	
	<!-- 회원가입 데이터 검증 -->
	<!-- 1-1. 아이디에 null값 검증 -->
	<!-- 1-2. 아이디 길이 검증(2자이상) -->
	<!-- 1-3. 아이디에 공백이 있는지 검증 -->
	<!-- 1.4. 아이디 중복확인(duplicationCheck 함수에서 진행) -->
	<!-- 2.1 비밀번호 검증이 완료되었는지 확인-->
	<!-- x.x 이메일에 공백이 있는지 확인  -->
	<script type="text/javascript">
		function validate(){
			/* 웬만하면 바닐라 자바스크립트로 하기 */
			/* 아이디 검증 */
			var du = document.registerForm; //form 객체 가져오기.
			var id = du.userID.value; //id값
			var pwValidateCheck = du.pwValidateCheck.value; //비밀번호 검증이 완료 되었는지 확인하는 값 (1이면 완료, 0이면 확인필요.)
			var email = du.userEmail.value;//email값
			var blank_pattern = /[\s]/g; //공백인 것들 정규식
			/* 1.1. 아이디 null값 검증 */
			if(!id){ 
                alert('아이디를 입력해주세요.');
                du.userID.focus();
                return false;
            }
			/*1.2 아이디 길이 검증*/
			if(id.length<2){
				alert('아이디는 2자 이상입니다.');
				du.userID.focus();
                return false;
			}
			/*1.3 아이디에 공백이 있는지 검증*/
			if(blank_pattern.test(id)==true){
				alert('아이디에 공백이 포함될 수 없습니다.');
				du.userID.focus();
                return false;
			}
			/*1.4 아이디가 중복되지 않았는지 확인*/
			if(duplicationCheck.value==0){ //값이 0이라면 아이디가 중복확인이 되지 않았거나 중복된 것이다.
				alert('아이디를 확인해주세요.');
				du.userID.focus();
				return false;
			}
			
			/*2-1. 비밀번호 검증이 완료되었는지 확인*/
			if(pwValidateCheck==0){
				alert('비밀번호를 확인해주세요.');
				du.userPWTEXT.focus();
				return false;
			}
			
			/*x.x 이메일에 공백이 있는지 확인*/
			if(blank_pattern.test(email)==true){
				alert('이메일에 공백이 포함될 수 없습니다.');
				du.email.focus();
                return false;
			}
			
			/*비밀번호 암호화*/
			var pw = $("#userPWTEXT");
			var pwck = $("#userPWCKTEXT"); 
			
			var rsa = new RSAKey();
			rsa.setPublic($('#RSAModulus').val(),$('#RSAExponent').val());
			$("#userPW").val(rsa.encrypt(pw.val()));
			rsa.setPublic($('#RSAModulusCk').val(),$('#RSAExponentCk').val());
	        $("#userPWCK").val(rsa.encrypt(pwck.val()));
	        
	        /* 기존 패스워드 text 초기화 */
	        pw.val("");
	        pwck.val("");
			return true;
		}
	
	</script>
	
	<!-- 비밀번호 검증 스크립트  -->
	<script>
		var pwValidateCheck = document.getElementById("pwValidateCheck"); //비밀번호 검증 값 1이면 검증완료, 0이면 검증필요.
		function check_pw(){
			var du = document.registerForm; //Form 객체가져오기
			var orignal_pw = du.userPWTEXT.value;  //비밀번호
			var compare_pw = du.userPWCKTEXT.value;//비밀번호 확인
			var same = "<span style='color:green;'>비밀번호가 일치합니다.</span>";
			var diff = "<span style='color:red;'>비밀번호가 일치하지 않습니다.</span>";
			var init = "<span style='color:white;'></span>";
			var error_length = "<span style='color:red;'>비밀번호는 10자 이상입니다.</span>";
			var error_specialCharacters = "<span style='color:red;'>비밀번호는 특수문자를 포함해야 합니다.</span>";
			var blank_pattern = /[\s]/g; //공백인 것들 정규식 (아직은 쓰이고 있지 않음.)
			var stringRegx = /[~!@\#$%<>^&*\()\-=+_\’]/gi; //특수문자 정규식
			
			if(orignal_pw == compare_pw){ //비밀번호==비밀번호 확인
				document.getElementById("passwordCheckDIV").innerHTML = same;
				pwValidateCheck.value=1;
			}else if(orignal_pw != compare_pw){ //비밀번호!=비밀번호 확인
				document.getElementById("passwordCheckDIV").innerHTML = diff;
				pwValidateCheck.value=0;
			}
			
			if(orignal_pw=="" && compare_pw==""){ //비밀번호==비밀번호 확인==공백
				document.getElementById("passwordCheckDIV").innerHTML = init;
				pwValidateCheck.value=0;
			}
			
			if(orignal_pw.length<10){ //비밀번호 길이가 10자 미만일 때
				document.getElementById("passwordCheckDIV").innerHTML = error_length;
				pwValidateCheck.value=0;
			}
			
			if(!stringRegx.test(orignal_pw)){ //비밀번호에 특수문자가 포함되어 있지 않을 때
				document.getElementById("passwordCheckDIV").innerHTML = error_specialCharacters;
				pwValidateCheck.value=0;
			}
			
			
		}
	</script>
	<!-- 아이디 중복확인 스크립트 -->
	<script>
			/*1.3 아이디 중복 검증 값 1이면 검증완료, 0이면 검증필요*/
			var duplicationCheck=document.getElementById("duplicationCheck");
			
			//포커스가 사라지면 발동
			$('#userID').focusout(function(){
				console.log('ID중복 확인 Ajax통신');
				//아이디에 아무것도 입력하지 않았다면 서버에 보내지않는다.
				if(!$('#userID').val()){ 
					$('#duplicationDIV').html('아이디를 입력해주세요.');
					$('#duplicationDIV').css("color","red");
					duplicationCheck.value=0;
					return ;
				}
				//아이디에 2자이상 입력되지 않았으면 서버에 보내지 않는다.
				if($('#userID').val().length<2){
					$('#duplicationDIV').html('아이디는 2글자 이상이어야 합니다.');
					$('#duplicationDIV').css("color","red");
					duplicationCheck.value=0;
					return ;
				}
				$.ajax({
					type : "post",
					url : "DuplicationCheck",
					data : {
						duplication : "ok",
						id : $('#userID').val()
					},
					success : function s(responseJSON){
						var duplicationDIV = document.getElementById("duplicationDIV");
						if(responseJSON.spaceCheck==null){
							if(responseJSON.result=='ok'){ //아이디가 중복되지 않았을 때
								duplicationDIV.innerHTML="사용 가능한 아이디 입니다.";
								duplicationDIV.style.color="green";
								duplicationCheck.value=1;
							}else if(responseJSON.result=='no'){ //아이디가 중복되었을 때
								duplicationDIV.innerHTML="사용 불가능한 아이디 입니다.";
								duplicationDIV.style.color="red";
								duplicationCheck.value=0;
							}
						}else{
							duplicationDIV.innerHTML="아이디에 공백을 제거해주세요.";
							duplicationDIV.style.color="red";
							duplicationCheck.value=0;
						}
						
					},
	                //error : function error(){ alert('error');}
	                error : function(request,status,error){
	        alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
	        duplicationCheck.value=0;
	                }

				});
			});
		
	</script>
	<!-- 이메일 중복확인 스크립트 -->
	<script>
	/*x.x 이메일 중복 검증*/
	var eMailDuplicationCheck=document.getElementById("eMailDuplicationCheck");
	$('#userEmail').focusout(function(){
		console.log('eMail중복 확인 Ajax통신');
		//이메일에 아무것도 입력하지 않았다면 서버에 보내지않는다.
		if(!$('#userEmail').val()){ 
			$('#eMailDuplicationDIV').html('이메일을 입력해주세요.');
			$('#eMailDuplicationDIV').css("color","red");
			eMailDuplicationCheck.value=0;
			return ;
		}
		$.ajax({
			type: "post",
			url : "DuplicationCheck",
			data: {
				duplication : "ok",
				email : $('#userEmail').val()
			},
			success : function s(responseJSON){
				var eMailDuplicationDIV = document.getElementById("eMailDuplicationDIV");
				if(responseJSON.spaceCheck==null){
					if(responseJSON.result=='ok'){ //이메일이 중복되지 않았을 때
						eMailDuplicationDIV.innerHTML="사용 가능한 이메일 입니다.";
						eMailDuplicationDIV.style.color="green";
						eMailDuplicationCheck.value=1;
					}else if(responseJSON.result=='no'){ //이메일이 중복되었을 때
						eMailDuplicationDIV.innerHTML="사용 불가능한 이메일 입니다.";
						eMailDuplicationDIV.style.color="red";
						eMailDuplicationCheck.value=0;
					}
				}else{
					eMailDuplicationDIV.innerHTML="이메일 공백을 제거해주세요.";
					eMailDuplicationDIV.style.color="red";
					eMailDuplicationCheck.value=0;
				}
				
			},
            //error : function error(){ alert('error');}
            error : function(request,status,error){
    alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
    duplicationCheck.value=0;
            }
		})
	});
	
	</script>
	
</body>

	


</html>