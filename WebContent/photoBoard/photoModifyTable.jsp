<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<!DOCTYPE html>
<html>
	<%	
		//넘어온 수정될 게시판 데이터를 받는다.
		String photoData = request.getParameter("photoViewModifyData");
		JSONParser parser = new JSONParser();
		Object parseTempObj = parser.parse(photoData.toString());
		JSONObject json = (JSONObject)parseTempObj;
		
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
        /* subPhotoTable에 순서 컬럼의 테두리 없애기 */
        #arrowTable{
        	border-right: hidden;
        	border-left: hidden;
        	border-top: hidden;
        	border-bottom: hidden;
        }
        
        #arrowTable td{
        	border-right: hidden;
        	border-left: hidden;
        	border-top: hidden;
        	border-bottom: hidden;
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
    
  </head>
  <body>
    <!-- 메인사진 상단 -->
    <h3 style="margin-top:50px; text-align:center;">메인사진</h3>
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
    		      <th width="64px">순서</th>
    		      <th width="200px" class="text-center">사진</th>
    		      <th class="text-center">내용</th>
    		      <th width="150px" class="text-center"></th>
    		    </tr>
    		  </thead>
    		  <tbody>
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
    <input type="file" id="subPhotos0" name="subPhotos0" class="show-for-sr" value="add.png" onchange="previewSubPhoto(this);" multiple/>   
    <script src="./Resource/assets/js/vendor/jquery.js"></script>
    <script src="./Resource/assets/js/vendor/what-input.js"></script>
    <script src="./Resource/assets/js/vendor/foundation.js"></script>
    <script src="./Resource/assets/js/app.js"></script>
    <script>
    var subPhotosCounter=-1; //JavaScript로 테이블의 행을 동적생성할 때 id값의 인덱스로 쓰일 변수
    var clickedImgIndex = 0; //내가 클릭한 이미지 인덱스
    	/*서버 경로를 설정한다.*/
	    var serverType="<%=serverType%>"; //web.xml에 설정된 서버타입을 가져온다.
		var server_path; //이미지 경로
		if(serverType==="dev"){ //서버타입이 개발이라면
			server_path="http://localhost:8090/photoProject/imgs/";
		}else if(serverType==="real"){//서버타입이 실서버라면
			server_path="http://218.149.135.58:8080/photoProject/imgs/"; 
		}else if(serverType==="comp"){ //서버타입이 회사라면
			server_path="http://localhost:8012/photoProject/imgs/";
		}	
    	
		// 업로드될 이미지 정보들, 제목을 담을 변수들
	    var sel_files = {};
	    Object.defineProperty(sel_files, 'length', {
			  value: 0, //속성의 값
			  enumerable : false, //for-in 구문에 노출이 될지 말지 설정 (console에는 연한색으로 표시)
			  writable: true //상수형태로 만들지않음
		});
	    var mainPhotoArray = []; 
		var original_subject; //사용자가 제목을 바꾸었는지 알기 위해서 전역변수로 제목을 선언한 후 비교한다. 
		var original_review; //사용자가 한줄평을 바꾸었는지 알기 위해서 전역변수로 한줄평을 선언한 후 비교한다.
		var original_tags=[]; //사용자가 태그를 변경하였는지 (태그는 순서가 변경되었더라도 변경된것으로 판단한다.)
		var original_contents={}; //사용자가 서브사진 내용을 변경하였는지 확인하기 위한 객체
		
		//서브사진 수정사항
		var updateList = {}; //사용자가 수정한 서브사진 리스트 (객체로 만든 이유는 subPhotoNo를 key, File을 name으로 하기위해서)
		Object.defineProperty(updateList, 'length', {
			  value: 0, //속성의 값
			  enumerable : false, //for-in 구문에 노출이 될지 말지 설정 (console에는 연한색으로 표시)
			  writable: true //상수형태로 만들지않음
		});
		
		var updateContentList = {}; //사용자가 수정한 서브사진 내용 (객체로 만든 이유는 subPhotoNo를 key, File을 name으로 하기위해서)
		var delList = []; //사용자가 지운 서브사진 리스트
		var originalRow = {}; //추가된 것이 아니라 기존에 있던 로우
		var rowOrderChange = {}; //순서가 변경된 행을 photoOwnNo를 키값으로 해서 순서가 변경될 때마다 삽입
		
		//페이지 로딩이 완료된 후 실행되어 수정 데이터들을 SET 시킨다.
		window.onload = function (){
			var modifyData = <%=json%>;
			var photo_boardNo = modifyData.photo_boardNo; //게시글 고유번호
			var photo_subject = modifyData.photo_subject; //게시글 제목
			var photo_a_line_review = modifyData.photo_a_line_review; //게시글 한줄평
			var photo_writer = modifyData.photo_writer; //게시글 작성자
			var photo_upload_date = modifyData.photo_upload_date; //게시글 작성일자
			var photoBoard_thumb = modifyData.photoBoard_thumb; //썸네일이 들어있는 리스트
			var photoBoard_tag = modifyData.photoBoard_tag; //태그가 들어있는 리스트
			var photoBoard_sub = modifyData.photoBoard_sub; //서브 사진과 그에 따른 서브내용이 들어있는 리스트
			var photoBoard_main = modifyData.photo_main; //게시글 메인사진
			original_subject = photo_subject;
			original_review=photo_a_line_review;
			/* console.log("photo_subject:"+photo_subject);
			console.log("photo_a_line_review:"+photo_a_line_review);
			console.log("photo_writer:"+photo_writer);
			console.log("photo_upload_date:"+photo_upload_date);
			console.log("photoBoard_thumb:"+photoBoard_thumb);
			console.log("photoBoard_tag:"+photoBoard_tag);
			console.log("photoBoard_sub:"+photoBoard_sub); 
			console.log("photoBoard_main:"+photoBoard_main);  */
			boardBasicInfoSet(photo_subject,photo_a_line_review,photo_writer,photo_upload_date,photoBoard_tag);
			boardPhotoInfoSet(photoBoard_main,photoBoard_sub,photo_boardNo);
		}
    	
	/*
		기본정보 SET 함수
		@Param 제목,한줄리뷰,작성자,날짜,태그
	*/ 
	function boardBasicInfoSet(subject,review,writer,date,tags){
		document.getElementById("photo_subject").value=subject; //제목
       	document.getElementById("photo_a_line_review").value=review; //한줄평
       	for(var tagJson in tags){
       		var tagValue = tags[tagJson].photo_tag;
       		var tagNo = tags[tagJson].tagNo;
       		original_tags.push(tagValue); //원본 태그에 넣는다.
       		$("#tag-list").append("<li class='tag-item'>"+tagValue+"<span class='del-btn' idx='"+tag_counter+"' tag-No='"+tagNo+"'>x</span></li>");
			addTag(tagValue);
       	}
	}
    
	/*
		사진정보 SET 함수
		@Param main    메인사진
		@Param subs    서브사진들
		@Param boardNo 게시글고유번호 (2보다 크면 sub포토 빼줘야함.)
	*/
	function boardPhotoInfoSet(main, subs, boardNo){
		var mainPhoto=document.getElementById('mainPhotoImg'); //메인사진
		mainPhoto.src = server_path+main; //메인사진 SET
		
		//var subPhotoImgIndex=subs.length-1; //응답 JSON에서 서브사진들을 꺼내기 위한 인덱스 (DESC로 정렬했을 때  DAO에서.)
		var subPhotoImgIndex=0; //응답 JSON에서 서브사진들을 꺼내기 위한 인덱스
		for(var index=0; index<subs.length-1; index++){
			
			var photoSubNo=subs[subPhotoImgIndex].photo_subNo;//서브사진 DB고유번호
			var photoOwnNo=subs[subPhotoImgIndex].photo_ownNo;//서브사진 테이블내 순서
			
			if(boardNo>2){
				if(subs[subPhotoImgIndex].photo_subNo==2){
					index--;
					//subPhotoImgIndex--; //(DESC로 정렬했을 때  DAO에서.)
					subPhotoImgIndex++;
					continue;
				}
				if(subs.length-1 > index){
					tableRowAdd(photoSubNo,photoOwnNo); //서브사진 테이블 추가
					//tableRowAdd(photoSubNo); //서브사진 테이블 추가 (18.12.19 photoOwnNo 자동으로 +1 되면서 추가되게 하기)
					originalRow[photoOwnNo]=photoOwnNo; //추가된 것이 아니라 기존에 있던 서브포토 순서를 넣어준다.
				}
				photoSubNo=subs[subPhotoImgIndex].photo_subNo;//서브사진 DB고유번호
				photoOwnNo=subs[subPhotoImgIndex].photo_ownNo;//서브사진 테이블내 순서
			}else{
				if(subs.length > index){
					//tableRowAdd(photoSubNo); //서브사진 테이블 추가 (18.12.19 photoOwnNo 자동으로 +1 되면서 추가되게 하기)
					tableRowAdd(photoSubNo,photoOwnNo); //서브사진 테이블 추가
				}
			}
			
			var subPhoto=document.getElementById('subPhotoImg'+index);//서브사진
			var subPhotosExplain=document.getElementById('subPhotosExplain'+index);//서브사진 설명
			var subImg=server_path+subs[subPhotoImgIndex].photo_sub; //서버경로+서브사진 데이터 
			var subPhotoExplain=subs[subPhotoImgIndex].photo_content;//서브사진 내용
			subPhoto.src = subImg; //서브사진 SET
			subPhotosExplain.value=subPhotoExplain; //서브사진 내용 SET
			subPhotoImgIndex++;
			//subPhotoImgIndex--; //(DESC로 정렬했을 때  DAO에서.)
			
			original_contents[photoSubNo]=subPhotoExplain; //원본 서브사진 내용을 넣는다.
		} 
	}
	
      //Param (분기하기위한 문자열, img객체)
      function clickEventAction(st,e){
        console.log("clickEventAction");
        var index = $(e).parent().parent().closest('tr').prevAll().length; //index값을 가져온다.
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
            console.log("index값:"+index);
            clickedImgIndex=index; //전역변수에 클릭한 인덱스를 보내준다.
            //$("#subPhotos"+index).trigger('click');
            $("#subPhotos0").trigger('click');
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
    	 //var originalRowSize = Object.size(originalRow); //기존에 있던 서브사진 로우 수 (정확한 sel_files의 인덱스를 알기위해서는 x버튼이 클릭된 행의 인덱스에서 이것을 빼줘야한다.)
    	 var index = clickedImgIndex; //클릭한 행 (0부터 시작)
         var lastRowCount = subPhotosCounter+1; //현재 마지막행
         var addRow = e.files.length; //추가할 사진들 개수
         var toBeAddRowCount = addRow+index; //더해져야할 행
         while(toBeAddRowCount>lastRowCount){ //현재 행수가 부족하면 그만큼 더해준다. 
         	lastRowCount++;
         	tableRowAdd();
         }
      	 for(var i=0; i<addRow; i++){
      		var subPhotosExplain=document.querySelector('#subPhotosExplain'+index); //클릭한 행의 내용DOM (photoSubNo를 추출하기 위함)
         	var photoSubNo = subPhotosExplain.getAttribute('photosubno'); //새로 추가된 행에는 이 값이 없음.
      		 
        	if(photoSubNo != 'undefined'){ //기존에 있던 행일 때
        		//updateList[index]=e.files[i];
        		updateList[photoSubNo]=e.files[i];
        		updateList.length++; //유사배열객체이므로 length를 수동으로 ++
			}else{ //새로 추가된 행일 때
				//Array.prototype.splice.call(sel_files,index,1,e.files[i]);
				sel_files[index]=e.files[i];
				sel_files.length++; //유사배열객체이므로 length를 수동으로 ++
			}
			let subimg = document.getElementById('subPhotoImg'+index); //해당 인덱스(행)에 해당하는 이미지 태그를 가져온다.
            //console.log(subimg);
            //console.log(e.files[i]);
            //선택한 이미지를 FileReader로 읽어서 이미지의 src속성에 넣어주는 부분.
			let file = e.files[i];
			let reader = new FileReader();
			reader.onload = function (event) {
				subimg.src = event.target.result;
			}
			reader.readAsDataURL(file);
			index++;
			}
			console.log(sel_files);
		}
	  
      
      /*
      tfoot의 '추가하기' 버튼을 누르면 row를 하나씩 추가하는 함수 (바닐라JavaScript는 tr과 td에 스타일이 안들어감.)
      @Param photoSubNo - DB 내에서의 해당 사진의 고유 번호
      @Param photoOwnNo - 해당 테이블 내의 사진 순서
      */  
      function tableRowAdd(photoSubNo, photoOwnNo){
          subPhotosCounter++; //인덱스 변수 +1;
          /* if(photoOwnNo===undefined){ //photoOwnNo가 없다면 즉, '추가하기' 버튼을 눌러서 추가된 행이라면 
    		  photoOwnNo=subPhotosCounter; //마지막 행의 인덱스를 넣어준다.
    	  } */
    	  
          var subPhotoUpload="onclick="+"clickEventAction('subPhotoUpload',this);"; //서브이미지 클릭했을 때 onclick 이벤트.
          var html='';
          html+='<tr style="height:180px;">';
          html+=' <td>';
          html+='		<table id="arrowTable">';
          html+='    		<tr class="arrowRow">';
          html+='    			<td>';
          html+='    				<button onclick="moveTop(this)";><img src="./Resource/images/top_arrow.png" /></button>';
          html+='    			</td>';
          html+='    		</tr>';
          html+='    		<tr class="arrowRow">';
          html+='    			<td>';
          html+='    				<button onclick="moveUp(this)";><img src="./Resource/images/up_arrow.png" /></button>';
          html+='    			</td>';
          html+='    		</tr>';
          html+='    		<tr class="arrowRow">';
          html+='    			<td>';
          html+='    				<button onclick="moveDown(this)";><img src="./Resource/images/down_arrow.png" /></button>';
          html+='    			</td>';
          html+='    		</tr>';
          html+='    		<tr class="arrowRow">';
          html+='    			<td>';
          html+='    				<button onclick="moveBottom(this)";><img src="./Resource/images/bottom_arrow.png" /></button>';
          html+='    			</td>';
          html+='    		</tr>';
          html+='		</table>';
          html+='  </td>';
          html+='  <td valign="top">';
          html+='    <img style="margin:0 auto;" id="subPhotoImg'+subPhotosCounter+'" '+subPhotoUpload+' src="./Resource/images/photoplus.png"/>';
          //html+='    <input type="file" id="subPhotos'+subPhotosCounter+'" photoSubNo="'+photoSubNo+'" photoOwnNo="'+photoOwnNo+'" name="subPhotos'+subPhotosCounter+'" class="show-for-sr" onchange="previewSubPhoto(this);">';
          html+='    <div style="text-align:center; margin-top:10px;">';
          html+='      <img src="./Resource/images//photozoom.png" onclick="imagesZoom(this);">';
          html+='    </div>';
          html+='  </td>';
          html+='  <td style="height:150px;">';
          html+='    <label style="height:100%; width:100%;">';
          html+='      <textarea style="height:100%; width:100%;" onchange="originalSubContentsValueChangeListener(this)" id="subPhotosExplain'+subPhotosCounter+'" photoSubNo="'+photoSubNo+'" photoOwnNo="'+photoOwnNo+'" name="subPhotosExplain'+subPhotosCounter+'" placeholder="사진에 대해 설명해주세요."></textarea>';
          html+='    </label>';
          html+='  </td>';
          html+='  <td>';
          html+='    <button type="button" class="button alert" onclick="removeTableSpecifiedRow(this);"><img src="./Resource/images//imgRemoveBtn.png"/></button>';
          html+='  </td>';
          html+='</tr>';
          $('#subPhotosTable > tbody:last').append(html); //하단에 추가.
          //console.log(originalRow);
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
    	//var originalRowSize = Object.size(originalRow); //기존에 있던 서브사진 로우 수 (정확한 sel_files의 인덱스를 알기위해서는 x버튼이 클릭된 행의 인덱스에서 이것을 빼줘야한다.)
        var remove_index=e.parentNode.parentNode.rowIndex; //'x'버튼을 누른 행의 인덱스 (1부터 시작한다.)
        var del_photosubno=$('#subPhotosExplain'+(remove_index-1)).attr('photosubno');
        //var del_photoownno=$('#subPhotosExplain'+(remove_index-1)).attr('photoownno');
       	if(del_photosubno != 'undefined'){ // (인덱스가 1부터 시작하므로 -1을 해준다.)
       		//수정페이지에서 새로 추가한 사진을 지우는 것이 아니라 원래 있던 사진을 지우는 것이라면 delList에 추가해서 서버로 보낸다.
       		delList.push(del_photosubno);
       		var result=isContainObject(updateList,del_photosubno); //updateList에 photosubno가 있는지 확인
       		if(result==true){ //updateList에 값이 있었다면 updateList에서 빼준다. 어차피 삭제한거니까.
       			delete updateList[del_photosubno]; //updateList에는 photoownno가 키값임.
       			updateList.length--; //유사배열객체이므로 length를 수동으로--
       		}
       		result=isContainObject(updateContentList,del_photosubno); //updateContentList에 photosubno가 있는지 확인
       		if(result==true){//updateContentList에 값이 있었다면 updateList에서 빼준다. 어차피 삭제한거니까.
       			delete updateContentList[del_photosubno];
       		}
       		
       		//기존 Row 수 또한 1개 줄어야한다. (originalRow로 row수를 체크하기 때문에. 이 함수도 그렇고, previewSubPhoto함수도..)
       		var del_photoOwnNo=$('#subPhotosExplain'+(remove_index-1)).attr('photoownno'); //originalRow에서 지워야하기 때문에
       		delete originalRow[del_photoOwnNo];
       	}else{ //새로 추가된 행을 삭제한거라면
       		//sel_files에서 해당 로우의 데이터를 삭제해야 submit했을 때 삭제된 데이터가 들어가지 않는다.
       		delete sel_files[remove_index-1];
       		sel_files.length--; //유사배열객체이므로 length를 수동으로 --
       	}
       	
        $(e).parent().parent().remove(); //e.parent == td, td.parent == tr이겠지?
		
        //Row가 지워졌으니 그 아래의 값들은 인덱스(id)가 바뀌어야함(id값) - img (img속성에는 name을 주지않았음.. 그래도 id만 바꾸면 됨.)
        //현재는 전체를 다 바꾸는데 후에는 제거한 row의 아래값들만 바꾸게 변경
        $("#subPhotosTable tr td > img").each(function(i,item){//첫번째부터 차례대로 td의 img src속성값을 가져온다.
          var index = $(this).parent().parent().closest('tr').prevAll().length; //index값을 가져온다.
          $(this).attr('id','subPhotoImg'+index);
        });

        //Row가 지워졌으니 그 아래의 값들은 인덱스가 바뀌어야함(id,name) - textarea
        //현재는 전체를 다 바꾸는데 후에는 제거한 row의 아래값들만 바꾸게 변경
        $("#subPhotosTable tr td  label > textarea").each(function(i,item){//첫번째부터 차례대로 td안의 laben에 textarea를 가져온다.
          var index = $(this).parent().parent().closest('tr').prevAll().length; //index값을 가져온다.
          $(this).attr('id','subPhotosExplain'+index);
          $(this).attr('name','subPhotosExplain'+index);
        });
		
        //Row가 지워졌으니 그 아래의 값들은 인덱스가 바뀌어야함(atrribute) - phowoOwnNo
        //현재는 전체를 다 바꾸는데 후에는 제거한 row의 아래값들만 바꾸게 변경
        $("#subPhotosTable tr td  label > textarea").each(function(i,item){//첫번째부터 차례대로 td안의 laben에 textarea를 가져온다.
          var index = $(this).parent().parent().closest('tr').prevAll().length; //index값을 가져온다.
          $(this).attr('photoownno',index);
        });
        
      	//동적 생성할 때 index값으로 쓰이는 변수 또한 -1 시켜줘야한다.
        subPhotosCounter--;
      }
      
      //submit 버튼을 눌렀을 때 실행되는 함수
      //업로드 준비
      function submitAction(){
        var data = new FormData(); //서버로 보낼 form data
       	var contentArray=[]; //사진들의 내용들이 들어갈 배열
       	var blank_pattern = /^\s+|\s+$/g; //공백 정규식
       	var subject = document.getElementById("photo_subject").value; //제목
       	var a_line_review = document.getElementById("photo_a_line_review").value; //한줄평
       	var orderObject = {}; //행의 순서를 보내기 위한 객체
       	
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
       	
       	//제목이 바뀌었는지 체크
        if(original_subject!==subject){
        	console.log("제목 체인지 됨");
        	data.append("subject",subject); //제목을 넣어준다.
        }
       	
       	//한줄평이 바뀌었는지 체크
       	if(original_review!==a_line_review){
       		console.log("한줄평 체인지 됨");
       		data.append("a_line_review",a_line_review); //한줄평을 넣어준다.
       	}
        
       	//태그가 바뀌었는지 체크
       	var tagIsChanged=originalTagValueChange();
       	if(tagIsChanged==true){
       		console.log("태그 값 또는 순서가 체인지됨");
       		//태그 데이터를 가져온 후 넣어준다.
    		var temp_tags = marginTag(); 
    		data.append("tags",temp_tags);
       	}
       	
       	//메인사진이 바뀌었는지 체크 (mainPhotoArray에 값이 있다면 메인사진은 변경된것)
       	if(mainPhotoArray[0] != undefined){
       		console.log("메인사진 체인지됨"+mainPhotoArray[0]);
       		//mainPhotos 이미지만 업로드 가능하게 MIME 형식 검사
            mainPhotoArray.forEach(function(f){
              if(!f.type.match("image.*")) {
                alert("이미지만 업로드 가능합니다.");
                return;
              }
            })
            //formData에 image append
            data.append("imageMain",mainPhotoArray[0]); //메인 이미지 data에 append
       	}
       	
       	//서브포토 내용에 공백이 있는지 확인
       	//추가된 서브사진들의 내용을 차례대로 배열에 삽입.
        for(var i=0; i<=subPhotosCounter; i++){
        	var $contentObj = $('#subPhotosExplain'+i); //TextArea 객체
        	var photoSubNo=$contentObj.attr('photoSubNo'); //photoSubNo
        	var content = $contentObj.val(); //내용 value
        	//내용에 공백이 있는지 확인
        	if(content == null || content.replace(blank_pattern, '')==""){ //기존 내용
        		$('#subPhotosExplain'+i).focus(); //공백이 있다면 그곳으로 포커스 이동
        		alert('내용에 공백만 입력되었습니다 ');
        		return ;
        	}
        	if(photoSubNo == 'undefined'){ //photoSubNo가 정의되어있지 않다면 추가된 내용이다.
        		contentArray.push(content);
        		data.append("newFileContents_"+i,content);
        	}
        }
       	
       	
       	if(Object.size(sel_files)>0){ //추가된 서브포토가 있다면
          	//subPhotos 이미지만 업로드 가능하게 MIME 형식 검사
       		for(key in sel_files){
       			let file=sel_files[key];
       			if(!file.type.match("image.*")) {
                    alert("이미지만 업로드 가능합니다.");
                    return;
                }
       			data.append("newFile_"+key,sel_files[key]);
       		}
            data.append("sel_files_length",Object.size(sel_files)); //몇개의 이미지가 있는지 넣어준다. (사진의 내용을 사용할 때도 같이 사용될것이다.)
       	}

		//서브포토 업데이트 리스트가 있는지 검사
		//서브포토 내용 업데이트 리스트가 있는지 검사
		//서브포토 제거 리스트가 있는지 검사
		var updateListSize = Object.size(updateList); 
		var updateContentListSize = Object.size(updateContentList);
		var delListSize = delList.length;
		if(updateListSize>0){
			for(let key in updateList){
				data.append("updateSubPhoto"+key,updateList[key]);	
			}
			data.append("updateListSize",updateListSize);
		}
		
		if(updateContentListSize>0){
			data.append("updateSubPhotoContent",JSON.stringify(updateContentList));
		}
		
		if(delListSize>0){
			for(var i=0; i<delListSize; i++){
				data.append("delList_"+i, delList[i]);
			}
			data.append("delListSize", delListSize); //서브포토 제거리스트 크기도 보내준다.
		}
		
		if(Object.size(sel_files)>0){ //새로운 행이 하나라도 추가가됬다면
			var selFilesKeyList = [];
        	for(key in sel_files){
        		selFilesKeyList.push(key);
        	}
        }
		
		//순서 정렬 orderObject
		$("#subPhotosTable tr td  label > textarea").each(function(i,item){//첫번째부터 차례대로 td안의 label에 textarea를 가져온다.
	        $photoOwnNo=$(this).attr('photoownno');
	        $photoSubNo=$(this).attr('photosubno');
	       	if($photoOwnNo!='undefined'){ //기존행
	       		orderObject[i]=$photoSubNo;
	       	}else{ //새로운 행
	       		//orderObject[i]=sel_files[];
	       		orderObject["new"+i]=sel_files[selFilesKeyList.shift()];
	       	}
      	});
		data.append("orderObject",JSON.stringify(orderObject));
		console.log(orderObject);
		
		//ajax 통신 (jQuery를 이용하지 않고 바닐라 JavaScript로 한다.)
        var xhr = new XMLHttpRequest(); 
        xhr.open("POST","./photoBoardModify",false);
        xhr.onreadystatechange = function(e){ //통신이 끝났을 때 호출된다.
          if(xhr.readyState == 4 && (xhr.status == 200 || xhr.status == 201)){ //readyState(통신상태) 4 == 완료 status(통신결과) 200 == 성공
           	console.log(e.currentTarget.responseText);
         	
          	/* var jsonResponse = JSON.parse(e.currentTarget.responseText); //넘어온 값 JSON으로 파싱
        	if(jsonResponse.result=='success'){
        		alert('게시글이 성공적으로 등록되었습니다.');
           	}else if(jsonResponse.result=='data_omission'){
           		alert('데이터 누락 발생. 관리자에게 문의해주세요.');
           	} */
          	
          	var serverType="<%=serverType%>"; //web.xml에 설정된 서버타입을 가져온다.
			/*서버타입에 맞게 경로설정*/
          	/* if(serverType==="dev"){ //서버타입이 개발이라면
				location.href="http://localhost:8090/photoProject/main.pro";
			}else if(serverType==="real"){//서버타입이 실서버라면
				location.href="http://218.149.135.58:8080/photoProject/main.pro"; 
			}else if(serverType==="comp"){ //서버타입이 회사라면
				location.href="http://localhost:8012/photoProject/main.pro";
			} */
          	
          }else{
        	alert('에러발생. 관리자에게 문의해주세요.');
            console.log("Result : "+e.currentTarget.responseText);
          }
        }
        xhr.send(data);

      } //end of submitAction
      
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
   		
	   			  //위에서 같은 태그 있는지 검사한 결과 0이면 통과, 0이 아니라면 중복값 발견
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

	//태그가 바뀌었는지 확인
	function originalTagValueChange(){
		//원본태그와 현재태그의 개수 중 큰 값을 찾아서 for문의 루프로 활용
		var tagSize=0;
		var originalSize=original_tags.length;
		var currentSize=Object.size(tag);
	
		if(originalSize>currentSize) tagSize=originalSize;
		else tagSize=currentSize;
		
		//현재 태그는 키값이 순서대로이긴 하지만 중간에 1,2,4,5 이런식으로 갈 수도있으니 (사용자가 태그를 삭제하였을때) 키값을 구해준다.
		var currentTagKey=[];
		for(currentKey in tag){
			currentTagKey.push(currentKey);
		}
		
		for(var i=0; i<tagSize; i++){
			var originalTag = original_tags[i];
			var currentTag = tag[currentTagKey[i]];
			
			if(originalTag!==currentTag){
				return true; //바뀌었다면 true
			}
		}
		return false; //안바뀌었다면 false
	}
   	
	//서브 사진 내용이 변경되었는지 확인하는 함수
	function originalSubContentsValueChangeListener(subPhotoContent){
		var photoSubNo = subPhotoContent.getAttribute('photoSubNo'); //방금 변경한 컨텐츠의 DB 고유번호
		var compareValue = subPhotoContent.value; //방금 변경한 컨텐츠의 내용
		var original=original_contents[photoSubNo]; //원본 컨텐츠의 내용
		
		if(photoSubNo!='undefined'){ //새로 추가한 로우에 대해서는 작동하지 않게 하기
			if(compareValue !== original){
				updateContentList[photoSubNo]=compareValue; //해당 고유번호 컨텐츠의 변경한 내용을 넣어준다.
			}else{ 
				delete updateContentList[photoSubNo]; //같아졌다면 굳이 서버에 보낼 필요가 없지.
			}	
		}
	}
	
	//객체의 사이즈 출력
   	Object.size = function (obj){
		var size=0;
   		for(key in obj){
   			if(obj.hasOwnProperty(key)) size++;
   		}
		return size;
   	 }
	
   	/*배열값에 중복된값이 있는지 체크하는 함수*/
    function arrayDuplicateCheck(array,compareValue){
  	var result = array.filter(function (word){
			return word === compareValue; 
		});
  	return result;
    }
	
  	/*
  		객체가 키값을 가지고 있는지 확인
  		@param obj : 키값이 있는지 확인 할 객체
  		@param key : 확인할 키값
  	*/
  	function isContainObject(obj,key){
  		if(obj.hasOwnProperty(key)){
  			return true;
  		}
  		return false;
  	}
  	
   /*배열순서 변경
  	@param arr : 변경될 배열
  	@param from : 요소의 현재 위치
  	@param to : 요소의 변경될 위치
  */
  function arrayOrderChanger(arr,from,to){
	  let elem1 = arr[from];
	  let elem2 = arr[to];
	  arr[from] = elem2;
	  arr[to] = elem1;
  }
  //맨위로 이동
  function moveTop(object){
	  let $tr = $(object).parent().parent().parent().parent().parent().parent(); // 클릭한 버튼이 속한 tr 요소
	  let trNumBefore = $tr.closest('tr').prevAll().length; //순서 바꾸기 전 index
	  let $tbody = $tr.parent(); //클릭한 요소의 tbody (subPhotosTable)
	  $tbody.find('tr:first').before($tr);//첫번째 tr 찾아서 그 앞에 클릭한 tr 요소 넣기
	  
	  //textArea photoSubNo로 새로 추가된 행인지 기존 행인지 판별
	  var photosubno_before=$('#subPhotosExplain'+trNumBefore).attr('photosubno');
	  
	  //img 태그 index 재정렬
      $("#subPhotosTable tr td > img").each(function(i,item){//첫번째부터 차례대로 td의 img src속성값을 가져온다.
        var index = $(this).parent().parent().closest('tr').prevAll().length; //index값을 가져온다.
        $(this).attr('id','subPhotoImg'+index);
      });

	  //textArea 태그 index 재정렬
      $("#subPhotosTable tr td  label > textarea").each(function(i,item){//첫번째부터 차례대로 td안의 label에 textarea를 가져온다.
        var index = $(this).parent().parent().closest('tr').prevAll().length; //index값을 가져온다.
        $(this).attr('id','subPhotosExplain'+index);
        $(this).attr('name','subPhotosExplain'+index);
        //$(this).attr('photoownno',index);
      });
	  
	  if(photosubno_before=='undefined'){ //새로운 행일 때
		arrayFirstElemChanger(sel_files,trNumBefore);
		//objectOrderPlus(updateList);
		/* }else{ //기존행 일때
		arrayFirstElemChanger(updateList,trNumBefore);
		//objectOrderPlus(sel_files);*/
	  } 
		  
	  
  }
  //위로 이동
  function moveUp(object){
	//let parentNode = object.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode; 
	//let original_index=parentNode.rowIndex; //버튼을 누른 행의 인덱스 (1부터 시작한다.)
	let $tr = $(object).parent().parent().parent().parent().parent().parent(); // 클릭한 버튼이 속한 tr 요소
	let trNumBefore = $tr.closest('tr').prevAll().length; //순서 바꾸기 전 index
	$tr.prev().before($tr); // 현재 tr 의 이전 tr 앞에 선택한 tr 넣기 (순서 바꾸기)
	let trNumAfter = $tr.closest('tr').prevAll().length; //순서 바꾼 후 index

	//순서가 바뀌었으니 id값 변경
	//img태그 id값변경
	$subTextArea_before=$('#subPhotoImg'+trNumBefore); //위 tr img
	$subTextArea_after=$('#subPhotoImg'+trNumAfter);   //아래 tr img
	$subTextArea_before.attr("id","subPhotoImg"+trNumAfter);  
	$subTextArea_after.attr("id","subPhotoImg"+trNumBefore); 
	//textArea id값,name값 변경
	$subTextArea_before=$('#subPhotosExplain'+trNumBefore); //위 tr textArea
	$subTextArea_after=$('#subPhotosExplain'+trNumAfter);   //아래 tr textArea
	$subTextArea_before.attr("id","subPhotosExplain"+trNumAfter);  //ID 변경
	$subTextArea_after.attr("id","subPhotosExplain"+trNumBefore);
	$subTextArea_before.attr("name","subPhotosExplain"+trNumAfter);  //NAME변경
	$subTextArea_after.attr("name","subPhotosExplain"+trNumBefore);
	
	//textArea photoownno 변경
	var photoownno_before = $subTextArea_before.attr('photoownno'); //방금 변경한 Row의 컨텐츠 테이블 순서 (전)
	var photoownno_after = $subTextArea_after.attr('photoownno'); //방금 변경한 Row의 컨텐츠 테이블 순서 (후)
	$subTextArea_before.attr("photoownno",photoownno_after);  //photoownno 변경
	$subTextArea_after.attr("photoownno",photoownno_before);
	
	//textArea photoSubNo로 새로 추가된 행인지 기존 행인지 판별
	var photosubno_before=$('#subPhotosExplain'+trNumBefore).attr('photosubno');
	var photosubno_after=$('#subPhotosExplain'+trNumAfter).attr('photosubno'); 
	//var photosubno_before=$subTextArea_before.attr('photosubno');
	//var photosubno_after=$subTextArea_after.attr('photosubno');
	
	//실제 파일 순서도 변경해줘야한다.
	if(photosubno_before=='undefined' && photosubno_after=='undefined'){ //순서변경 후 아래와 위 행 모두 새로 추가된 행일 때
		objectValueInterChange(sel_files,trNumBefore,trNumAfter);
	}else if(photosubno_before!='undefined' && photosubno_after!='undefined'){//순서변경 후 아래와 위 행 모두 기존 행일 때
		//objectValueInterChange(updateList,trNumBefore,trNumAfter);
	}else if(photosubno_before=='undefined' && photosubno_after!='undefined'){//순서변경 후 아래의 행이 기존 행일 때
		//objectPropertyChange(updateList,trNumBefore,trNumAfter);
		objectPropertyChange(sel_files,trNumAfter,trNumBefore);
	}else if(photosubno_before!='undefined' && photosubno_after=='undefined'){//순서변경 후 아래가 새로 추가된 행
		//objectPropertyChange(updateList,trNumAfter,trNumBefore);
		objectPropertyChange(sel_files,trNumBefore,trNumAfter);
	}
	
  }
  
  //아래로 이동
  function moveDown(object){
	//let parentNode = object.parentNode.parentNode.parentNode.parentNode.parentNode.parentNode; 
	//let original_index=parentNode.rowIndex; //버튼을 누른 행의 인덱스 (1부터 시작한다.)
	let $tr = $(object).parent().parent().parent().parent().parent().parent(); // 클릭한 버튼이 속한 tr 요소
	let trNumBefore = $tr.closest('tr').prevAll().length; //순서 바꾸기 전 index
	$tr.next().after($tr); // 현재 tr 의 다음 tr 뒤에 선택한 tr 넣기 (순서 바꾸기)
	let trNumAfter = $tr.closest('tr').prevAll().length; //순서 바꾼 후 index
	
	//순서가 바뀌었으니 id값 변경
	//img태그 id값변경
	$subTextArea_before=$('#subPhotoImg'+trNumBefore); //위 tr img
	$subTextArea_after=$('#subPhotoImg'+trNumAfter);   //아래 tr img
	$subTextArea_before.attr("id","subPhotoImg"+trNumAfter);  
	$subTextArea_after.attr("id","subPhotoImg"+trNumBefore); 
	//textArea id값,name값 변경
	$subTextArea_before=$('#subPhotosExplain'+trNumBefore); //위 tr textArea
	$subTextArea_after=$('#subPhotosExplain'+trNumAfter);   //아래 tr textArea
	$subTextArea_before.attr("id","subPhotosExplain"+trNumAfter);  //ID 변경
	$subTextArea_after.attr("id","subPhotosExplain"+trNumBefore);
	$subTextArea_before.attr("name","subPhotosExplain"+trNumAfter);  //NAME변경
	$subTextArea_after.attr("name","subPhotosExplain"+trNumBefore);
	
	//textArea photoownno 변경
	var photoownno_before = $subTextArea_before.attr('photoownno'); //방금 변경한 Row의 컨텐츠 테이블 순서 (전)
	var photoownno_after = $subTextArea_after.attr('photoownno'); //방금 변경한 Row의 컨텐츠 테이블 순서 (후)
	$subTextArea_before.attr("photoownno",photoownno_after);  //photoownno 변경
	$subTextArea_after.attr("photoownno",photoownno_before);
	
	//textArea photoSubNo로 새로 추가된 행인지 기존 행인지 판별
	var photosubno_before=$('#subPhotosExplain'+trNumBefore).attr('photosubno');
	var photosubno_after=$('#subPhotosExplain'+trNumAfter).attr('photosubno'); 
	//var photosubno_before=$subTextArea_before.attr('photosubno');
	//var photosubno_after=$subTextArea_after.attr('photosubno');
	console.log(trNumBefore,'행 photosubno_before:',photosubno_before,trNumAfter,'행 photosubno_after:',photosubno_after);
	console.log('photoownno_before:',photoownno_before,'photoownno_after:',photoownno_after);
	
	//실제 파일 순서도 변경해줘야한다.
	if(photosubno_before=='undefined' && photosubno_after=='undefined'){ //순서변경 후 아래와 위 행 모두 새로 추가된 행일 때
		objectValueInterChange(sel_files,trNumBefore,trNumAfter);
	}else if(photosubno_before!='undefined' && photosubno_after!='undefined'){//순서변경 후 아래와 위 행 모두 기존 행일 때
		//objectValueInterChange(updateList,trNumBefore,trNumAfter);
	}else if(photosubno_before=='undefined' && photosubno_after!='undefined'){//순서변경 후 아래의 행이 기존 행일 때
		//objectPropertyChange(updateList,trNumBefore,trNumAfter);
		objectPropertyChange(sel_files,trNumAfter,trNumBefore);
	}else if(photosubno_before!='undefined' && photosubno_after=='undefined'){//순서변경 후 아래가 새로 추가된 행
		//objectPropertyChange(updateList,trNumAfter,trNumBefore);
		objectPropertyChange(sel_files,trNumBefore,trNumAfter);
	}
	
  }
  
  //맨아래로 이동
  function moveBottom(object){
	  let $tr = $(object).parent().parent().parent().parent().parent().parent(); // 클릭한 버튼이 속한 tr 요소
	  let trNumBefore = $tr.closest('tr').prevAll().length; //순서 바꾸기 전 index
	  $tr.remove(); //tr삭제
	  $('#subPhotosTable > tbody:last').append($tr); //하단에 추가.
	  
	  //textArea photoSubNo로 새로 추가된 행인지 기존 행인지 판별
	  var photosubno_before=$('#subPhotosExplain'+trNumBefore).attr('photosubno');
	  
	  //img 태그 index 재정렬
      $("#subPhotosTable tr td > img").each(function(i,item){//첫번째부터 차례대로 td의 img src속성값을 가져온다.
        var index = $(this).parent().parent().closest('tr').prevAll().length; //index값을 가져온다.
        $(this).attr('id','subPhotoImg'+index);
      });

	  //textArea 태그 index 재정렬
      $("#subPhotosTable tr td  label > textarea").each(function(i,item){//첫번째부터 차례대로 td안의 laben에 textarea를 가져온다.
        var index = $(this).parent().parent().closest('tr').prevAll().length; //index값을 가져온다.
        $(this).attr('id','subPhotosExplain'+index);
        $(this).attr('name','subPhotosExplain'+index);
        //$(this).attr('photoownno',index);
      });
      let trNumAfter = $tr.closest('tr').prevAll().length; //순서 바꾼 후 index, 즉 맨 마지막 번호
	  if(photosubno_before=='undefined') //새로운 행 일때
		  arrayLastElemChanger(sel_files,trNumBefore,trNumAfter); 
	  else //기존 행 일때
		  arrayLastElemChanger(updateList,trNumBefore,trNumAfter);
  }
    
    
    
  /*
  	배열,객체 맨앞에으로 순서 변경
  	@param arr : 변경될 배열
  	@param from : 요소의 현재 위치
  */
  function arrayFirstElemChanger(arr,from){
	  let elem = arr[from]; //맨 처음에 넣어질 요소
	  if(Array.isArray(arr)){ //Array였을 때
		  Array.prototype.splice.call(arr,from,1);
		  Array.prototype.unshift.call(arr,elem); //배열 맨앞에 추가
	  }else{ //객체였을 때
		  let firstKey=-1; //첫번째 키값
		   /* 굳이 이런 방법을 쓰지 않아도 됨.
		  for(let i=0; i<arr.length;i++){
			  if(obj.hasOwnProperty(i)){ //i의 값이 있는지 판별
				  firstKey=i;
			  	  break; //맨 처음 true로 들어온 i값이 firstKey이므로 더 이상 반복할 필요가 없다.
			  }
		  }  */
		  firstKey = Object.keys(arr)[0];

		  console.log('arrayFirstElemChanger:',firstKey);
		  if(arr.hasOwnProperty(from)){
			  delete arr[from]; //맨 처음에 넣어질 요소를 지운다. (위에서 elem이라는 변수에 옮겨놨기 때문에)
			  arr.length--; //하나 지워졌으니까 lenght도 --
		  }
		  for(key in arr){
			  key=parseInt(key);
			  
			  if(key==firstKey){ //첫 번째 행일 때
				var nextElem=arr[key+1]; //원래 key에 있던 요소들(key+1로 옮겨질 요소)
				arr[0]=arr[key];
			  }else{
				arr[key+1]=nextElem;
				var nextElem=arr[key+1]; //원래 key에 있던 요소들(key+1로 옮겨질 요소)
			  }
		  }
		  if(firstKey != -1){
			  arr[firstKey]=elem;
			  arr.length++;
		  }
	  }
	  
  }
  
  /*배열,객체 맨뒤로 순서 변경
  	@param arr : 변경될 배열
  	@param from : 요소의 현재 위치
  	@param to : 배열의 맨 마지막 위치
  */
  function arrayLastElemChanger(arr,from,to){
	  let elem = arr[from]; //맨 마지막에 넣어질 요소
	  if(Array.isArray(arr)){ //Array였을 때
		arr.splice.call(arr,from,1);  
	  }else{ //객체였을 때
		delete arr[from]; //기존 from은 지워준다.
		for(let i=from; i<to; i++){ //요소를 하나씩 앞으로 당겨준다.
		 	if(arr.hasOwnProperty(i+1)) //맨 마지막 요소는 +1해도 없을 것이기 때문에.
		 		arr[i]=arr[i+1];
		}
		//맨 마지막 요소 제거
		var objSize=Object.size(arr);
		delete arr[objSize-1];
	  }
	  if(elem !== undefined){
	  	arr[to]=elem;
	  }
  }
  
  /*
  	넘어온 객체의 key값들을 +1한다. (키가 숫자일때)
  	@param obj : 키를 +1시킬 객체
  */
  function objectOrderPlus(obj){
  	for(key in obj){
  		key=parseInt(key);
  		if(typeof(key) == 'number'){ //타입이 number인지 확인
  			if(nextElem!='undefined'){
  				let temp = nextElem;
  				nextElem=obj[key+1]
  				obj[key+1]=temp;
  			}else{
  				var nextElem=obj[key+1];
  				obj[key+1]=obj[key];
  			}	
  			
			/* if(key==Object.keys(obj)[0]){ //첫 번째 행일 때
				var nextElem=obj[key+1]; //원래 key에 있던 요소들(key+1로 옮겨질 요소)
				obj[key+1]=obj[key];
			}else{
				obj[key+1]=nextElem;
				var nextElem=obj[key+1]; //원래 key에 있던 요소들(key+1로 옮겨질 요소)
			} */
  		}
  	} 
  }
  	
  /**/
  function objKeyMinus(obj, start){
 	for(let i=start+1; i<Object.size(obj); i++){
 		if(obj.hasOwnProperty(i)){ //i가 있을 경우에만
 			if(nextElem!='undefined'){
  				let temp = nextElem;
  				nextElem=obj[i+1]
  				obj[i+1]=temp;
  			}else{
  				var nextElem=obj[i+1];
  				obj[i+1]=obj[i];
  			}	
 		}
 	}
  }
  	
  	
  
  /*
	객체의 프로퍼티명을 변경 (값은 그대로)
	@param obj:프로퍼티 변경을 하기위한 객체
	@param oldProperty:기존 속성
	@param newProperty:새로운 속성
  */
  function objectPropertyChange(obj,oldProperty,newProperty){
	if(obj.hasOwnProperty(oldProperty)){ //해당 속성이 존재하는지 확인
		obj[newProperty]=obj[oldProperty]; //존재한다면 기존 속성의 값을 새로운 속성의 값으로 변경
		delete obj[oldProperty]; //기존 프로퍼티를 지워준다.
	}
  }
	
  /*
	객체의 값을 변경
	@param obj:값 변경을 하기위한 객체
	@param firstProperty:1속성
	@param secondProperty:2속성
  */
  function objectValueInterChange(obj,firstProperty,secondProperty){
	  if(obj.hasOwnProperty(firstProperty) || obj.hasOwnProperty(secondProperty)){
		  //속성 2개중에 1개만 있어도 작동되게끔한다.
		  let tempValue=obj[firstProperty];
		  obj[firstProperty]=obj[secondProperty];
		  obj[secondProperty]=tempValue;
	  }
  }
  
  
</script>


  </body>
</html>
