<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<%	
		//로그인이 안되어있다면 로그인하게끔 유도
		Object photoUploadTable_isLogin = session.getAttribute("userID");
		if(photoUploadTable_isLogin==null){
			out.println("<script>alert('로그인을 먼저 해주세요.'); location.href='/photoProject/login.pro';</script>");
		}
		
		//서버 타입을 가져온다.
		//1. 서버 타입을 가지고 사진업로드 완료 후 가는 경로 설정 (자바 스크립트에서)
		String serverType=application.getInitParameter("ServerType");
	%>
  <head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="./Resource/assets/css/foundation.css">
    <!-- This is how you would link your custom stylesheet -->
    <link rel="stylesheet" href="./Resource/assets/css/app.css">
    <style>
        #mainPhotoImg{
          border: 1px solid black;
          width: w20%;
          height: 15%;
          margin: 0 auto;
          cursor:pointer;
        }
        #subPhotoImg{
          border: 1px solid black;
          width: 100%;
          height: 50%;
          cursor:pointer;
        }
        .noColor{
          background-color: white;
        }
        
        ul li.tag-item {
        padding: 4px 8px;
        background-color: #777;
        color: #000;
    }

    .tag-item:hover {
        background-color: #262626;
        color: #fff;
    }

    .del-btn {
        font-size: 12px;
        font-weight: bold;
        cursor: pointer;
        margin-left: 8px;
    }
    
    * {
        margin: 0;
        padding: 0;
        list-style: none;
    }

    ul {
        padding: 16px 0;
    }

    ul li {
        display: inline-block;
        margin: 0 5px;
        font-size: 14px;
        letter-spacing: -.5px;
    }

    </style>
    <script>
      var subPhotosCounter=0; //JavaScript로 테이블의 행을 동적생성할 때 id값의 인덱스로 쓰일 변수
    </script>
  </head>
  <body>
    <!-- 메인사진 상단 -->
    <h3 style="margin-top:50px; text-align:center;">메인사진</h3>

    <form action="uploadDo" method="post" enctype="multipart/form-data" name="uploadForm">
      <div class="text-center" style="margin-bottom:20px;">
        <img id="mainPhotoImg" onclick="clickEventAction('mainPhotoUpload',this);" src="./Resource/images/photoplus.png"/>
        <input type="file" id="mainPhoto" name="mainPhoto" class="show-for-sr" onchange="previewMainPhoto(this);"/>
      </div>
		
		<!-- 게시글 제목 -->
		<div class="grid-x grid-padding-x" style="width: 800px; margin:0 auto;">
    		<div class="small-3 cell">
      			<label for="photo_subject" class="text-right middle">제목</label>
    		</div>
    		<div class="small-9 cell">
      			<input type="text" id="photo_subject" name="photo_subject" placeholder="제목을 입력해주세요.">
    		</div>
  		</div>
		
		<!-- 한줄평 -->
		<div class="grid-x grid-padding-x" style="width: 800px; margin:0 auto;">
    		<div class="small-3 cell">
      			<label for="photo_a_line_review" class="text-right middle">한줄평</label>
    		</div>
    		<div class="small-9 cell">
      			<input type="text" id="photo_a_line_review" name="photo_a_line_review" placeholder="한줄평을 입력해주세요.">
    		</div>
  		</div>
		
		<!-- 해시태그 -->
		<div class="grid-x grid-padding-x" style="width: 800px; margin:0 auto;">
    		<div class="small-3 cell">
      			<label for="photo_hashTag" class="text-right middle">태그</label>
    		</div>
    		<div class="small-9 cell">
      			<input type="text" id="photo_hashTag" name="photo_hashTag" placeholder="태그를 입력해주세요.">
    		</div>
  		</div>
		<ul id="tag-list">
        </ul>

        <!-- 서브사진 테이블 -->
        <table class="unstriped hover" id="subPhotosTable">
    		  <thead>
    		    <tr>
    		      <th width="200px" class="text-center">사진</th>
    		      <th class="text-center">내용</th>
    		      <th width="150px" class="text-center"></th>
    		    </tr>
    		  </thead>
    		  <tbody>
    		    <tr style="height:180px;">
    		      <td valign="top">
                <img style="margin:0 auto;" id="subPhotoImg0" onclick="clickEventAction('subPhotoUpload',this);" src="./Resource/images/photoplus.png"/>
                <input type="file" id="subPhotos0" name="subPhotos0" class="show-for-sr" value="add.png" onchange="previewSubPhoto(this);" multiple/>
                <div style="text-align:center; margin-top:10px;">
                  <img src="./Resource/images/photozoom.png" onclick="imagesZoom(this);">
                </div>
              </td>
    		      <td style="height:150px;">
                <label style="height:100%; width:100%;">
                  <textarea style="height:100%; width:100%;" id="subPhotosExplain0" name="subPhotosExplain0" placeholder="사진에 대해 설명해주세요."></textarea>
                </label>
              </td>
    		      <td>
                <button type="button" class="button alert" onclick="removeTableSpecifiedRow(this);"><img src="./Resource/images/imgRemoveBtn.png"/></button>
              </td>
    		    </tr>
    		  </tbody>
          <tfoot class="noColor">
            <tr>
              <td colspan="3" class="text-center">
                <button type="button" class="secondary button" onclick="tableRowAdd();">추가하기</button>
                <!-- 오른쪽으로 버튼 밀기 style="position: absolute; right: 0;" -->
                <button type="button" class="success button" onclick="return submitAction()">등록하기</button>
              </td>
            </tr>
          </tfoot>
  	     </table>
       <form>
    <script src="./Resource/assets/js/vendor/jquery.js"></script>
    <script src="./Resource/assets/js/vendor/what-input.js"></script>
    <script src="./Resource/assets/js/vendor/foundation.js"></script>
    <script src="./Resource/assets/js/app.js"></script>
    <script>

      // 업로드될 이미지 정보들, 제목을 담을 변수들
      var sel_files = [];
      var mainPhotoArray = []; 
	  var subject;
      
      //Param (분기하기위한 문자열, img객체)
      function clickEventAction(st,e){
        console.log("clickEventAction");
        var index = $(e).parent().parent().closest('tr').prevAll().length; //index값을 가져온다.
        console.log("index값:"+index);
        switch (st) {
          case "mainPhotoUpload":
            $("#mainPhoto").trigger('click');
            break;
          case "subPhotoUpload":
            //내가 클릭한 이미지에 파일을 넣을지 기본이미지가 있는 곳 중 첫번째에 넣을지 결정하게 되는 곳
            //console.log(e.getAttribute('src'));
            if(e.getAttribute('src')=='./Resource/images/photoplus.png'){ //만약 선택한 이미지의 src가 기본값이라면 안채워진 것부터 채운다.
              $("#subPhotosTable tr td > img").each(function(i,item){//첫번째부터 차례대로 td의 img src속성값을 가져온다.
                var imgLocation = $(this).attr('src');
                if(imgLocation=='./Resource/images/photoplus.png'){ //만약 기존이미지라면
                  //인덱스를 i값으로 변경하고 for each문을 종료한다면 index값이 내가 클릭한 곳이 아닌 기본이미지가 있는 곳 중 그 첫번째 값의 인덱스가 들어간다.
                  index=i;
                  return false ;
                }
              });
            }

            $("#subPhotos"+index).trigger('click');
            break;
        }
      }

      // 메인이미지 선택시 미리보기
      function previewMainPhoto(e){
        var mainPhotoImg = document.getElementById('mainPhotoImg');
        //선택한 이미지를 FileReader로 읽어서 이미지의 src속성에 넣어주는 부분.
        var file = e.files[0],
            reader = new FileReader();
        reader.onload = function (event) {
          mainPhotoImg.src = event.target.result;
        }
        reader.readAsDataURL(file);
        mainPhotoArray.splice(0, 1, file); //배열에 index의 파일을 먼저 지우고(있으면) 배열에 삽입
      }
      // 서브이미지 선택시 미리보기 (수정시에 활용될듯)
      function previewSubPhoto(e){
        var index = parseInt(e.id.substr(e.id.length - 1)); //id값의 끝문자를 가져오면 몇번째 행인지 알수있는 인덱스가 된다. (여기서는 넘어온 객체의 id값이므로 아래 주석처리된 코드를 안써도된다.)
        //var index = $(e).parent().parent().closest('tr').prevAll().length; //index값을 가져온다.
        var subimg = document.getElementById('subPhotoImg'+index); //해당 인덱스(행)에 해당하는 이미지 태그를 가져온다.
        //선택한 이미지를 FileReader로 읽어서 이미지의 src속성에 넣어주는 부분.
        var file = e.files[0],
            reader = new FileReader();
        reader.onload = function (event) {
              subimg.src = event.target.result;
        }
        reader.readAsDataURL(file);
        sel_files.splice(index, 1, file); //배열에 index의 파일을 먼저 지우고(있으면) 배열에 삽입
      }


      //tfoot의 '추가하기' 버튼을 누르면 row를 하나씩 추가하는 함수 (jQuery 바닐라JavaScript는 tr과 td에 스타일이 안들어감.)
      function tableRowAdd(){
        subPhotosCounter++; //인덱스 변수 +1;
        var subPhotoUpload="onclick="+"clickEventAction('subPhotoUpload',this);"; //서브이미지 클릭했을 때 onclick 이벤트.
        var html='';
        html+='<tr style="height:180px;">';
        html+='  <td valign="top">';
        html+='    <img style="margin:0 auto;" id="subPhotoImg'+subPhotosCounter+'" '+subPhotoUpload+' src="./Resource/images//photoplus.png"/>';
        html+='    <input type="file" id="subPhotos'+subPhotosCounter+'" name="subPhotos'+subPhotosCounter+'" class="show-for-sr" onchange="previewSubPhoto(this);">';
        html+='    <div style="text-align:center; margin-top:10px;">';
        html+='      <img src="./Resource/images//photozoom.png" onclick="imagesZoom(this);">';
        html+='    </div>';
        html+='  </td>';
        html+='  <td style="height:150px;">';
        html+='    <label style="height:100%; width:100%;">';
        html+='      <textarea style="height:100%; width:100%;" id="subPhotosExplain'+subPhotosCounter+'" name="subPhotosExplain'+subPhotosCounter+'" placeholder="사진에 대해 설명해주세요."></textarea>';
        html+='    </label>';
        html+='  </td>';
        html+='  <td>';
        html+='    <button type="button" class="button alert" onclick="removeTableSpecifiedRow(this);"><img src="./Resource/images//imgRemoveBtn.png"/></button>';
        html+='  </td>';
        html+='</tr>';
        $('#subPhotosTable > tbody:last').append(html); //하단에 추가.
      }

      //zoom버튼(돋보기)누르면 원본이미지  뜨게 하는 함수 (모달로 바꿔보기..)
      //e=img객체
      function imagesZoom(e){
        var index=$(e).parent().parent().parent().closest('tr').prevAll().length; //index값을 가져온다.
        var src=$('#subPhotoImg'+index).attr('src'); //해당 index의 행에 셋팅된 이미지를 가져온다.

         var win = window.open('', 'Detail', 'width=0, height=0, menubar=0, toolbar=0, directories=0, scrollbars=1, status=0, location=0, resizable=1');
         op="<html><head><title>크게 보기</title></head>";
         op+="<body leftmargin='0' topmargin='0'>";
         op+="<img src='"+ src +"' border='0' style='cursor:pointer' onclick='window.close();' onload='window.resizeTo(this.width+30, this.height+90); window.moveTo( (screen.width-this.width)/2 ,  (screen.height-this.height)/2-50 )'>";
         op+="</body></html>";

        win.document.write(op);
      }


      //'x'버튼을 누르면 해당 row를 삭제하는 함수
      //e=버튼 객체
      function removeTableSpecifiedRow(e){
        var remove_index=e.parentNode.parentNode.rowIndex; //'x'버튼을 누른 행의 인덱스 (1부터 시작한다.)
        $(e).parent().parent().remove(); //e.parent == td, td.parent == tr이겠지?

        //Row가 지워졌으니 그 아래의 값들은 인덱스(id)가 바뀌어야함(id값) - img (img속성에는 name을 주지않았음.. 그래도 id만 바꾸면 됨.)
        //현재는 전체를 다 바꾸는데 후에는 제거한 row의 아래값들만 바꾸게 변경
        $("#subPhotosTable tr td > img").each(function(i,item){//첫번째부터 차례대로 td의 img src속성값을 가져온다.
          var index = $(this).parent().parent().closest('tr').prevAll().length; //index값을 가져온다.
          $(this).attr('id','subPhotoImg'+index);
        });

        //Row가 지워졌으니 그 아래의 값들은 인덱스가 바뀌어야함(id,name) - input
        //현재는 전체를 다 바꾸는데 후에는 제거한 row의 아래값들만 바꾸게 변경
        $("#subPhotosTable tr td > input").each(function(i,item){//첫번째부터 차례대로 td의 input을 가져온다.
          var index = $(this).parent().parent().closest('tr').prevAll().length; //index값을 가져온다.
          $(this).attr('id','subPhotos'+index);
          $(this).attr('name','subPhotos'+index);
        });

        //Row가 지워졌으니 그 아래의 값들은 인덱스가 바뀌어야함(id,name) - textarea
        //현재는 전체를 다 바꾸는데 후에는 제거한 row의 아래값들만 바꾸게 변경
        $("#subPhotosTable tr td  label > textarea").each(function(i,item){//첫번째부터 차례대로 td안의 laben에 textarea를 가져온다.
          var index = $(this).parent().parent().closest('tr').prevAll().length; //index값을 가져온다.
          $(this).attr('id','subPhotosExplain'+index);
          $(this).attr('name','subPhotosExplain'+index);
        });

        //sel_files에서 해당 로우의 데이터를 삭제해야 submit했을 때 삭제된 데이터가 들어가지 않는다.
        sel_files.splice(remove_index-1,1);
        //동적 생성할 때 index값으로 쓰이는 변수 또한 -1 시켜줘야한다.
        subPhotosCounter--;
      }

      //submit 버튼을 눌렀을 때 실행되는 함수
      //업로드 준비
      function submitAction(){
        var data = new FormData(); //서버로 보낼 form data
       	var contentArray=[]; //사진들의 내용들이 들어갈 배열
       	var blank_pattern = /^\s+|\s+$/g; //공백 정규식
       	subject = document.getElementById("photo_subject").value; //제목
       	var a_line_review = document.getElementById("photo_a_line_review").value; //한줄평
       	
       	//제목이 입력되지 않았다면
       	if(!subject || subject.replace(blank_pattern, '')==""){
       		alert('제목을 입력해주세요.');
       		document.getElementById("photo_subject").focus();
       		return ;
       	}
       	
       	//한줄평이 입력되지 않았다면
       	if(!a_line_review){
       		alert('한줄평을 입력해주세요.');
       		document.getElementById("photo_a_line_review").focus();
       		return ;
       	}
       	
       	//사진들의 내용을 차례대로 배열에 삽입.
        for(var i=0; i<=subPhotosCounter; i++){
        	var content = $('#subPhotosExplain'+i).val(); //내용 value
        	//내용에 공백이 있는지 확인
        	if(content == null || content.replace(blank_pattern, '')==""){
        		$('#subPhotosExplain'+i).focus(); //공백이 있다면 그곳으로 포커스 이동
        		alert('내용에 공백만 입력되었습니다 ');
        		return ;
        	}
        	contentArray.push(content);
        }
        
        //subPhotos 이미지만 업로드 가능하게 MIME 형식 검사
        sel_files.forEach(function(f) {
          if(!f.type.match("image.*")) {
            alert("이미지만 업로드 가능합니다.");
            return;
          }
        })
        //mainPhotos 이미지만 업로드 가능하게 MIME 형식 검사
        mainPhotoArray.forEach(function(f){
          if(!f.type.match("image.*")) {
            alert("이미지만 업로드 가능합니다.");
            return;
          }
        })
		
        
        //formData에 image append
        data.append("imageMain",mainPhotoArray[0]); //메인 이미지 data에 append
        
        for(var i=0, len=sel_files.length; i<len; i++) {
            var file_name = "image_"+i; //해당 file의 parameter name
			var content_name = "content_"+i; //사진에 대한 내용
           
            data.append(file_name, sel_files[i]);
			data.append(content_name,contentArray[i]);
			
        }
        data.append("subject",subject); //제목을 넣어준다.
		data.append("length",sel_files.length-1); //몇개의 이미지가 있는지 넣어준다. (사진의 내용을 사용할 때도 같이 사용될것이다.)
        data.append("a_line_review",a_line_review);
		
		//태그 데이터를 가져온다.
		var temp_tags = marginTag();
		data.append("tags",temp_tags);
		
		//ajax 통신 (jQuery를 이용하지 않고 바닐라 JavaScript로 한다.)
        var xhr = new XMLHttpRequest(); 
        xhr.open("POST","./photoBoardUpload",false);
        //xhr.responseType = 'application/x-www-form-urlencoded; charset=UTF-8;';
        xhr.onreadystatechange = function(e){ //통신이 끝났을 때 호출된다.
          if(xhr.readyState == 4 && (xhr.status == 200 || xhr.status == 201)){ //readyState(통신상태) 4 == 완료 status(통신결과) 200 == 성공
           	console.log(e.currentTarget.responseText);
          	var jsonResponse = JSON.parse(e.currentTarget.responseText); //넘어온 값 JSON으로 파싱
        	if(jsonResponse.result=='success'){
        		alert('게시글이 성공적으로 등록되었습니다.');
           	}else if(jsonResponse.result=='data_omission'){
           		alert('데이터 누락 발생. 관리자에게 문의해주세요.');
           	}
          	
          	var serverType="<%=serverType%>"; //web.xml에 설정된 서버타입을 가져온다.
			/*서버타입에 맞게 경로설정*/
          	if(serverType==="dev"){ //서버타입이 개발이라면
				location.href="http://localhost:8090/photoProject/main.pro";
			}else if(serverType==="real"){//서버타입이 실서버라면
				location.href="http://218.149.135.58:8080/photoProject/main.pro"; 
			}else if(serverType==="comp"){ //서버타입이 회사라면
				location.href="http://localhost:8012/photoProject/main.pro";
			}
			
          }else{
        	alert('에러발생. 관리자에게 문의해주세요.');
            console.log("Result : "+e.currentTarget.responseText);
          }
        }
        xhr.send(data);

      }
      
      var tag = {}; //태그값들이 담길 Object
      var tag_counter = 0;
      
      //태그를 추가하는 함수
      function addTag(value){
    	  tag[tag_counter] = value; //태그를 Object안에 추가한다.
    	  tag_counter++; // counter 증가 삭제를 위한 del-btn 의 고유 id 가 된다.
 		  
      }
      
   	  // 최종적으로 서버에 넘길때 tag 안에 있는 값을 array type 으로 만들어서 넘긴다.
      function marginTag () {
          return Object.values(tag).filter(function (word) {
              return word !== "";
          });
      }
	
   	  //태그 input box에서 엔터 및 스페이스 바 입력 리스너
   	  $("#photo_hashTag").on("keypress", function (e){
   		  var self = $(this);
   		  
   		  //태그 input에 포커스가 있을 때 엔터 및 스페이스바  입력 시 구동
   		  if(e.key === "Enter" || e.keycode == 32){
   			  
   			  //태그 개수 5개로 제한
   			  if(Object.keys(tag).length>4){
   				alert('태그는 5개까지 사용할 수 있습니다.');
   				$('#photo_hashTag').val(''); //value값 초기화 
   				$('#photo_hashTag').focus(); //태그로 포커스 이동
   				return ;
   			  }
   			 
   			  var tagValue = self.val(); //현재 input box에 입력된 값 가져오기
   			  
   			  if(tagValue !== ""){ //값이 있어야 동작하게끔
   				  //같은 태그가 있는지 검사. 있다면 해당값이 array로 return 
   				  var result = Object.values(tag).filter(function (word){
   					 return word === tagValue; 
   				  });
   		
   			  //태그 중복검사
   			  if(result.length == 0){
   				  $("#tag-list").append("<li class='tag-item'>"+tagValue+"<span class='del-btn' idx='"+tag_counter+"'>x</span></li>");
   				  addTag(tagValue);
   				  self.val("");
   			  }else{
   				  alert("태그 값이 이미 있습니다.");
   			  }
   			  	
   			  }
   			  e.preventDefault(); //SpaceBar 할 때 빈 공간이 생기지 않도록 방지
   			  
   			 

   		  }

   	  });
   	  
   	  //삭제 버튼
   	  // 삭제 버튼은 비동기적 생성이므로 document 최초 생성시가 아닌 검색을 통해 이벤트를 구현시킨다.
        $(document).on("click", ".del-btn", function (e) {
            var index = $(this).attr("idx");
            delete tag[index]; //delete 키워드로 객체에서 해당 프로퍼티 삭제 (원래는 null로 해놓으면 delete보다 훨씬 빠르게 작동하지만 객체의 길이를 제한하기위해서 아예삭제하는 것으로 바꿈.)
            $(this).parent().remove();
        });


		
   	  
   	  


      
    </script>


  </body>
</html>
