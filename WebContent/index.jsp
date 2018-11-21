<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@page import="java.awt.Image"%>


<!DOCTYPE HTML>
<html>
	<head>
		<title>You & I</title>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
		<link rel="stylesheet" href="Resource/assets/css/main.css" />
		<link rel="stylesheet" href="Resource/assets/css/progressBar.css" />
		<noscript><link rel="stylesheet" href="Resource/assets/css/noscript.css" /></noscript>
	</head>
	<body class="is-preload">
		<!-- 최초로 메인사진을 가져오는 스크립틀릿 -->
		<%	
			
			//서버 업로드 폴더
			//String directory = request.getServletContext().getRealPath("/myProject/imgs"); //이렇게하면 크롬에서 보안상의 이유로 막음. (웹주소가 아닌 로컬주소로 인식을함..) 오류내용: Not allowed to load local resource
			String directory = "http://localhost:8090/photoProject/myProject/imgs/";
			//메인사진 12개를 DB에서 가져온다.
			Map<String,Object>photoData=(HashMap)request.getAttribute("photoData"); //사진 12개가 들어있다.
			List<Integer> photoMainMapKeyList = (ArrayList)request.getAttribute("photoDataKeyList"); //사진이 들어있는 맵의 키값이 들어있다.
			List<String> mainPhoto = new ArrayList<String>(); //서버의 경로 +메인사진 파일명
			int mainPhotoIndex=0; //메인사진의 인덱스값 0부터 올라간다.
			
			for(int i=0; i<photoMainMapKeyList.size(); i++){
				int key = photoMainMapKeyList.get(i);
				String fileName = ((HashMap)photoData.get(key)).get("photo_main").toString();
				fileName = directory+fileName;
				mainPhoto.add(fileName);
			}
			/*
				Set key = photoData.keySet();
				for(Iterator iterator = key.iterator(); iterator.hasNext();){
					int keyName = Integer.parseInt(iterator.next().toString());
					String photo_main=((HashMap)photoData.get(keyName)).get("photo_main").toString();
				}
			*/ 
			
			//서버 타입을 가져온다.
			//1. 서버 타입을 가지고 메인에 뿌릴 이미지 경로를 정한다. (자바 스크립트에서)
			String serverType=application.getInitParameter("ServerType");
			
		%>
		<!-- header -->
		<%@include file="/header/header.jsp" %>
		
				<!-- Main -->
					<div id="main">
						<div class="inner">
							<header>
								<!-- <h1>This place is free to say what you want to say.<br /></h1>
								<p>Make your own story by uploading photo albums and postings.</p> -->
								<h1>당신이 원하는 것을 자유롭게 표현하세요.<br /></h1>
								<p>사진 앨범과 게시글을 통해 자신만의 이야기를 만들어나가세요.</p>
							</header>
							<section id="mainSection" class="tiles">
								
							</section>
						</div>
					</div>
					
				<!-- progressBar -->
				<div class="sk-fading-circle" id="sk-fading-circleID" style="display:none;">
				  <div class="sk-circle1 sk-circle"></div>
				  <div class="sk-circle2 sk-circle"></div>
				  <div class="sk-circle3 sk-circle"></div>
				  <div class="sk-circle4 sk-circle"></div>
				  <div class="sk-circle5 sk-circle"></div>
				  <div class="sk-circle6 sk-circle"></div>
				  <div class="sk-circle7 sk-circle"></div>
				  <div class="sk-circle8 sk-circle"></div>
				  <div class="sk-circle9 sk-circle"></div>
				  <div class="sk-circle10 sk-circle"></div>
				  <div class="sk-circle11 sk-circle"></div>
				  <div class="sk-circle12 sk-circle"></div>
				</div>
				
				<!-- Footer -->
					<footer id="footer">
						<div class="inner">
							<section>
								<h2>Get in touch</h2>
								<form method="post" action="#">
									<div class="fields">
										<div class="field half">
											<input type="text" name="name" id="name" placeholder="Name" />
										</div>
										<div class="field half">
											<input type="email" name="email" id="email" placeholder="Email" />
										</div>
										<div class="field">
											<textarea name="message" id="message" placeholder="Message"></textarea>
										</div>
									</div>
									<ul class="actions">
										<li><input type="submit" value="Send" class="primary" /></li>
									</ul>
								</form>
							</section>
							<section>
								<h2>Follow</h2>
								<ul class="icons">
									<li><a href="#" class="icon style2 fa-twitter"><span class="label">Twitter</span></a></li>
									<li><a href="#" class="icon style2 fa-facebook"><span class="label">Facebook</span></a></li>
									<li><a href="#" class="icon style2 fa-instagram"><span class="label">Instagram</span></a></li>
									<li><a href="#" class="icon style2 fa-dribbble"><span class="label">Dribbble</span></a></li>
									<li><a href="#" class="icon style2 fa-github"><span class="label">GitHub</span></a></li>
									<li><a href="#" class="icon style2 fa-500px"><span class="label">500px</span></a></li>
									<li><a href="#" class="icon style2 fa-phone"><span class="label">Phone</span></a></li>
									<li><a href="#" class="icon style2 fa-envelope-o"><span class="label">Email</span></a></li>
								</ul>
							</section>
							<ul class="copyright">
								<li>&copy; Untitled. All rights reserved</li><li>Design: <a href="http://html5up.net">HTML5 UP</a></li>
							</ul>
						</div>
					</footer>

			

			<!-- default Scripts -->
			<%@include file="/js/jsFile.jsp" %>
			<script type="text/javascript">
				var offset=0; //현재 몇개의 메인사진을 불러왔는지 체크하는 변수
				
				//최초로 페이지가 로드 될때 서버에서 썸네일 이미지를 가져온다.
				window.onload=function(){
					getMainPhotoThumbnail(offset);
				}
				
				/* 서버 MainProAPI를 실행하여 최근사진 12개를 가지고 오는 코드 */
				function getMainPhotoThumbnail(offset){
					$.ajax({
						type: "get",
						url : "apiController",
						data: {
							apiName : "MainProAPI",
							methodName : "getMainPhoto",
							offset : offset
						},
						success : function s(responseJSON){
							photoSet(responseJSON);
						},
			            //error : function error(){ alert('error');}
			            error : function(request,status,error){
			    			alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
			            }
					})
				}
					
					
					/*스크롤이 맨끝에 왔는지 알려주는 함수*/
					$(window).scroll(function(){
						var scrollHeight = $(document).height();
						var scrollPosition = $(window).height()+$(window).scrollTop();
						if((scrollHeight - scrollPosition) / scrollHeight === 0){
							console.log("스크롤 끝");
							document.getElementById("sk-fading-circleID").style.display="block";
							getMainPhotoThumbnail(offset); //스크롤이 끝에 다다르면 메인이미지 썸네일을 더 로드한다.
						}
					});
					
					//서버에서 보내준 json데이터를 view단에 뿌려주는 역할
					function photoSet(jsonObject){
						var keySet=jsonObject["-1"]; //키값이 들어있는 리스트를 가져온다.
						var serverType="<%=serverType%>"; //web.xml에 설정된 서버타입을 가져온다.
						var server_path; //이미지 경로
						if(serverType==="dev"){ //서버타입이 개발이라면
							server_path="http://localhost:8090/photoProject/imgs/";
						}else if(serverType==="real"){//서버타입이 실서버라면
							server_path="http://218.149.135.58:8080/photoProject/imgs/"; 
						}else if(serverType==="comp"){ //서버타입이 회사라면
							server_path="http://localhost:8012/photoProject/imgs/";
						}
						
						var style_tag = "style"; //메인 이미지에 스타일 적용 (1~6까지 순서대로)
						var style_index = 1; //메인 이미지 스타일을 1~6까지 순서대로 적용시켜줄 인덱스값
						
						for(var i=0; i<keySet.length;i++){
							//console.log(jsonObject[keySet[i]].photo_main);
							//console.log(server_path+jsonObject[keySet[i]].photo_main);
							var photo_main = server_path+jsonObject[keySet[i]].photo_main; //이미지경로와 DB에서 가져온 메인사진 파일명을 합친다.
							var subject = jsonObject[keySet[i]].photo_subject; //게시글 제목
							var photo_boardNo = jsonObject[keySet[i]].photo_boardNo; //게시글 고유번호
							var photo_main_thumbnail = server_path+jsonObject[keySet[i]].photo_main_thumbnail; //이미지경로와 DB에서 가져온 메인사진 썸네일 파일명을 합친다.
							var a_line_review = jsonObject[keySet[i]].photo_a_line_review; //한줄평
							var photo_writer = jsonObject[keySet[i]].photo_writer; //글쓴이
							var photo_upload_date = jsonObject[keySet[i]].photo_upload_date; //작성날짜
							var aTag_href="photoView.pro?photo_boardNo="+photo_boardNo; //사진 클릭하면 게시판번호가 GET형식으로 넘어간다.
							var style=style_tag+style_index;
							photo_upload_date = photo_upload_date.substring(0,10); //년-월-일 형식으로 표현하기 위함.
							
							var html='';
							html+='<article class='+style+'>';
							html+=' <span class="image">';
							html+='  <img src='+photo_main_thumbnail+' alt="" />';
							html+=' </span>';
							html+='  <a href='+aTag_href+'>';
							html+='   <h2>'+subject+'</h2>';
							html+='    <div class="content" style="position: static;">'; 
							html+='     <p>'+a_line_review+'</p>'; 
							html+='     <p style="position: absolute; right: 0px; bottom:0px;"> write by '+photo_writer+'</p>';
							html+='     <p style="position: absolute; right: 0px; bottom:0px;">'+photo_upload_date+'</p>';
							html+='    </div>'; 
							html+='  </a>'; 
							html+='</article>'; 
							$('#mainSection').append(html);
							if(style_index==6){
								style_index=1;
							}else{
								style_index++;
							}
						} //end of For
						
						offset=offset+keySet.length; //서버에서 가져온 이미지 만큼 더해준다.
						document.getElementById("sk-fading-circleID").style.display="none"; //프로그래스바를 사라지게 한다.
						console.log("현재 offset:"+offset);
					}
			
			</script>
	</body>
</html>