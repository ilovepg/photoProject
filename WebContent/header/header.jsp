<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>	
	<%	
	 	String contextPathHeader=request.getContextPath();  //경로를 절대경로로 바꾸기위한 ContextPath
		Object ob_userID = session.getAttribute("userID");
		String userID=null;
		if(ob_userID!=null){
			userID=ob_userID.toString();
		}
	%>

			<div id="wrapper">

				<!-- Header -->
					<header id="header">
						<div class="inner">

							<!-- Logo -->
								<a href="<%=contextPathHeader%>/main.pro" class="logo">
									<span class="symbol"><img src="<%=contextPathHeader%>/Resource/images/logo.svg" alt="" /></span><span class="title">You And I</span>
								</a>

							<!-- Nav -->
								<nav>
									<ul>
										<li><a href="#menu">Menu</a></li>
									</ul>
								</nav>

						</div>
					</header>

				<!-- Menu -->
					<nav id="menu">
						<h2>Menu</h2>
						<ul>
							
							<% if(userID==null){ %>
								<li><a href="<%=contextPathHeader%>/main.pro">Home</a></li>
								<li><a href="<%=contextPathHeader%>/login.pro">로그인</a></li>
								<li><a href="<%=contextPathHeader%>/register.pro">회원가입</a></li>
							<% }else{ %>
								<li><a href="<%=contextPathHeader%>/modifyPassword.pro" style="font-size: 15px; text-align:center;"><%=userID %>님  환영합니다.</a>
								<a href="<%=contextPathHeader%>/logoutDo" class="button primary small">로그아웃</a></li>
								<li><a href="<%=contextPathHeader%>/main.pro">Home</a></li>
							<% } %>
							<li><a href="<%=contextPathHeader%>/photoUploadTable.pro">포토 글쓰기</a></li>
							<li><a href="<%=contextPathHeader%>/generic.html">게시판</a></li>
							<li><a href="<%=contextPathHeader%>/elements.html">Elements</a></li>
						</ul>
					</nav>
		
</html>