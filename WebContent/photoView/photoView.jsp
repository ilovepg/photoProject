<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>You & I</title>
	<meta charset="utf-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
	<link rel="stylesheet" href="Resource/assets/css/photoView/lens-photoView-main.css" />
	<noscript><link rel="stylesheet" href="Resource/assets/css/photoView/lens-photoView-noscript.css" /></noscript>
</head>
<body class="is-preload-0 is-preload-1 is-preload-2">
	<%
		int photoBoardNo = Integer.parseInt(request.getParameter("photo_boardNo").toString()); //GET방식으로 넘어온 페이지 고유값을 받는다.
		String serverType=application.getInitParameter("ServerType"); //이미지 경로를 나누기 위해 서버타입을 가져온다.
		Object tempID=session.getAttribute("userID");
		String sessionID=null;
		if(tempID!=null){
			sessionID = tempID.toString(); //로그인된 아이디를 가져온다.	
		}
		
	%>
	<!-- Main -->
			<div id="main">

				<!-- Header -->
					 <header id="header">
						<h1 id="mainSubject">Lens</h1>
						<p id="a_line_review">Review</p>
						<p id="writer_and_date"></p>
						<ul class="icons" id="tags">
						</ul>
					</header>

				<!-- Thumbnail -->
					<section id="thumbnails">
						<article id="removeArticle">
							<a class="thumbnail" href="images/fulls/12.jpg"><img src="images/thumbs/12.jpg" alt="" /></a>
							<h2>안녕하세요!</h2>
							<p>슬라이드를 시작해볼까요?</p>
						</article>
					</section>

				<!-- Footer -->
					<footer id="footer">
						<ul class="copyright">
							<li>&copy; Untitled.</li><li>Design: <a href="http://html5up.net">HTML5 UP</a>.</li>
						</ul>
					</footer>

			</div>
			
		<!-- Scripts -->
		<script src="Resource/assets/js/jquery.min.js"></script>
		<script src="Resource/assets/js/photoView/lens-photoView-browser.min.js"></script>
		<script src="Resource/assets/js/photoView/lens-photoView-breakpoints.min.js"></script>
		<!-- <script src="Resource/assets/js/photoView/lens-photoView-main.js"></script> -->
			
		<script>
			/*전역변수 선언*/	
			var jsonResponse=null; //API로 부터 받은 초기 데이터(수정할 때 서버에서 받아온 그대로 다시 서버로 보내줄 용도)
		
			/* 페이지의 로딩이 끝나면 상세페이지의 데이터를 가지고 오는 API 호출 */
			window.onload=function(){
				getPhotoViewData(<%=photoBoardNo%>)
			};
			
			/* 상세페이지 초기 데이터 가지고 오는 API */
			function getPhotoViewData(photoBoardNo){
				$.ajax({
					type: "get",
					url : "apiController",
					data: {
						apiName : "PhotoBoardViewAPI",
						methodName : "getPhotoDetailData",
						photoBoardNo : photoBoardNo
					},
					success : function s(responseJSON){
					/*  for(var key in responseJSON) {
						  console.log(key +":"+ responseJSON[key]);
					}  */
						console.log("응답:"+responseJSON.PhotoBoardViewAPI_getPhotoDetailData_result);
						jsonResponse=responseJSON.PhotoBoardViewAPI_getPhotoDetailData_result; //수정버튼을 눌렀을 때 보내줄 용도
						dataSet(responseJSON.PhotoBoardViewAPI_getPhotoDetailData_result); //응답된 데이터를 가지고 화면을 구성한다.
					},
		            //error : function error(){ alert('error');}
		            error : function(request,status,error){
		    			alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
		            }
				})
			}
			
			//데이터를 set시키는 함수
			function dataSet(responseJSON){
				responseJSON = JSON.parse(responseJSON); //넘어온 값을 JSON형식으로 변환해준다.
				
				var photo_boardNo = responseJSON.photo_boardNo;
				var photo_boardNoFromJava = <%=photoBoardNo%>;
				
				if(photo_boardNo==photo_boardNoFromJava){
					console.log("클릭한 게시글 고유번호와 응답된 게시글 고유번호가 같습니다.");
					/*응답 JSON 데이터를 뽑아서 변수에 저장*/
					var photo_subject = responseJSON.photo_subject; //게시글 제목
					var photo_a_line_review = responseJSON.photo_a_line_review; //게시글 한줄평
					var photo_writer = responseJSON.photo_writer; //게시글 작성자
					var photo_upload_date = responseJSON.photo_upload_date; //게시글 작성일자
					var photoBoard_thumb = responseJSON.photoBoard_thumb; //썸네일이 들어있는 리스트
					var photoBoard_tag = responseJSON.photoBoard_tag; //태그가 들어있는 리스트
					var photoBoard_sub = responseJSON.photoBoard_sub; //서브 사진과 그에 따른 서브내용이 들어있는 리스트
					var photoBoard_main = responseJSON.photo_main; //게시글 메인사진
					
					/* console.log("photo_subject:"+photo_subject);
					console.log("photo_a_line_review:"+photo_a_line_review);
					console.log("photo_writer:"+photo_writer);
					console.log("photo_upload_date:"+photo_upload_date);
					console.log("photoBoard_thumb:"+photoBoard_thumb);
					console.log("photoBoard_tag:"+photoBoard_tag);
					console.log("photoBoard_sub:"+photoBoard_sub); */
					
					//게시글 기본 정보 데이터 SET
					boardBasicInfoSet(photo_subject,photo_a_line_review,photo_writer,photo_upload_date,photoBoard_tag);
					boardPhotoSet(photoBoard_thumb,photoBoard_sub,photoBoard_main);
					
				}else{
					console.log("클릭한 게시글 고유번호와 응답된 게시글 고유번호가 같지않습니다.");
					console.log("photo_boardNo:",photo_boardNo,"photo_boardNoFromJava:",photo_boardNoFromJava);
				}
			}
			
			/*
				기본정보 SET 함수
				@Param 제목,한줄리뷰,작성자,날짜,태그
			*/ 
			function boardBasicInfoSet(subject,review,writer,date,tags){
				document.getElementById('mainSubject').innerHTML=subject;
				document.getElementById('a_line_review').innerHTML=review;
				document.getElementById('writer_and_date').innerHTML="write by "+writer+"</br>"+date;
				if(writer=='<%=sessionID%>'){
					//$('#writer_and_date').after('<button id="photoViewModify" onclick="modifyFunc(jsonResponse);">수정하기</button>');
					var html = '';
					html += '<form id="photoViewModify" method="post" action="photoModify.pro">';
					html += '	<input type="hidden" name="photoViewModifyData" id="photoViewModifyData"/>';
					html += '	<input type="hidden" name="photoBoardWriter" id="photoBoardWriter"/>';
					html += '</form>';
					html += '<button onclick="modifyFunc(jsonResponse);">수정하기</button>';
					$('#writer_and_date').after(html);
					$('#tags').before('<button id="photoViewDelete" location.href="">삭제하기</button>');	
					
				}
			}
			
			/*
				사진정보 SET 함수
				@Param 썸네일, 원본사진
			*/
			function boardPhotoSet(photoBoard_thumb,photoBoard_sub,photoBoard_main){
				var serverType="<%=serverType%>"; //web.xml에 설정된 서버타입을 가져온다.
				var server_path; //이미지 경로
				var mainPhotoIndex;
				if(serverType==="dev"){ //서버타입이 개발이라면
					server_path="http://localhost:8090/photoProject/imgs/";
				}else if(serverType==="real"){//서버타입이 실서버라면
					server_path="http://218.149.135.58:8080/photoProject/imgs/"; 
				}else if(serverType==="comp"){ //서버타입이 회사라면
					server_path="http://localhost:8012/photoProject/imgs/";
				}
				
				/* 
					DB에서 조회할때 DESC로 조회해서 서브사진들의 순서가 다 뒤바뀌는 상황이 나타나서
					여기에서 인덱스를 뒤에서부터 돌림.
				*/
				/* 사진들 썸네일 및 풀이미지 셋팅*/
				for(var i=0; i<photoBoard_thumb.length; i++){
					if(i==mainPhotoIndex){ //메인포토는 이미 위에서 적용했으므로 생략
						continue; 
					}
					var html = '';
					var photo_thumNail = server_path+photoBoard_thumb[i].photo_thumbName; //썸네일
					var photo_fullImg = server_path+photoBoard_sub[i].photo_sub; //서브 full Img
					var photo_content = photoBoard_sub[i].photo_content; //서브사진 내용
					
					/*main사진 썸네일 및 풀이미지 셋팅*/
					if(photoBoard_thumb[i].photo_thumbType=="main"){ //메인사진의 썸네일을 찾는다.
						photo_fullImg = server_path+photoBoard_main;
						photo_content = '메인사진 찰칵~';
					} 
					
					/* 사진들 썸네일 및 풀이미지 셋팅*/
					html += '<article>';
					html += '<a class="thumbnail" href='+photo_fullImg+'><img src='+photo_thumNail+' alt="" /></a>';
					html += '<h2>'+photo_content+'</h2>';
					html += '<p></p>';
					html += '</article>';
					
					if(photoBoard_thumb[i].photo_thumbType=="main"){ //메인사진의 썸네일을 찾는다.
						$('#thumbnails').prepend(html)
					}else{
						$('#thumbnails').append(html);	
					}
				}
				
				/* for(var i=photoBoard_thumb.length-1; i>=0; i--){
					if(i==mainPhotoIndex){ //메인포토는 이미 위에서 적용했으므로 생략
						continue; 
					}
					var html = '';
					var photo_thumNail = server_path+photoBoard_thumb[i].photo_thumbName; //썸네일
					var photo_fullImg = server_path+photoBoard_sub[i].photo_sub; //서브 full Img
					var photo_content = photoBoard_sub[i].photo_content; //서브사진 내용
					
					//main사진 썸네일 및 풀이미지 셋팅
					if(photoBoard_thumb[i].photo_thumbType=="main"){ //메인사진의 썸네일을 찾는다.
						photo_fullImg = server_path+photoBoard_main;
						photo_content = '메인사진 찰칵~';
					} 
					
					//사진들 썸네일 및 풀이미지 셋팅
					html += '<article>';
					html += '<a class="thumbnail" href='+photo_fullImg+'><img src='+photo_thumNail+' alt="" /></a>';
					html += '<h2>'+photo_content+'</h2>';
					html += '<p></p>';
					html += '</article>';
					
					if(photoBoard_thumb[i].photo_thumbType=="main"){ //메인사진의 썸네일을 찾는다.
						$('#thumbnails').prepend(html)
					}else{
						$('#thumbnails').append(html);	
					}
				} */
				
				$('#removeArticle').remove(); //썸네일 쪽에 나와있는 기본이미지 제거
				main.init(); //썸네일 누르면 풀이미지 뜨게 init
				
			}
			
			/*수정버튼을 눌렀을 때 서버에서 받은 데이터를 다시 서버로 보낸다. (다른API 호출)*/
			function modifyFunc(jsonResponse){
				jsonResponse=JSON.parse(jsonResponse); //string을 JSON형식으로 변환
				var loginID = '<%=sessionID%>'; //로그인 아이디
				var photo_writer = jsonResponse.photo_writer; //게시글 작성자
					if(loginID==null){
						alert('로그인을 먼저해주세요');
						return ;
					}else if(loginID != photo_writer){
						alert('잘못된 접근입니다.');
						return ;
					}
				$('#photoViewModifyData').val(JSON.stringify(jsonResponse));
				$('#photoBoardWriter').val(photo_writer); //글쓴이까지 넣어준다. (servlet에서 예외처리 다시한번 하기위해)
				$('#photoViewModify').submit();					
			}
			
		</script>	
			
			
		<!-- 메인 -->
		<script>

		var main = (function($) { var _ = {

		/**
		 * Settings.
		 * @var {object}
		 */
		settings: {

			// Preload all images.
				preload: false,

			// Slide duration (must match "duration.slide" in _vars.scss).
				slideDuration: 500,

			// Layout duration (must match "duration.layout" in _vars.scss).
				layoutDuration: 750,

			// Thumbnails per "row" (must match "misc.thumbnails-per-row" in _vars.scss).
				thumbnailsPerRow: 2,

			// Side of main wrapper (must match "misc.main-side" in _vars.scss).
				mainSide: 'right'

		},

		/**
		 * Window.
		 * @var {jQuery}
		 */
		$window: null,

		/**
		 * Body.
		 * @var {jQuery}
		 */
		$body: null,

		/**
		 * Main wrapper.
		 * @var {jQuery}
		 */
		$main: null,

		/**
		 * Thumbnails.
		 * @var {jQuery}
		 */
		$thumbnails: null,

		/**
		 * Viewer.
		 * @var {jQuery}
		 */
		$viewer: null,

		/**
		 * Toggle.
		 * @var {jQuery}
		 */
		$toggle: null,

		/**
		 * Nav (next).
		 * @var {jQuery}
		 */
		$navNext: null,

		/**
		 * Nav (previous).
		 * @var {jQuery}
		 */
		$navPrevious: null,

		/**
		 * Slides.
		 * @var {array}
		 */
		slides: [],

		/**
		 * Current slide index.
		 * @var {integer}
		 */
		current: null,

		/**
		 * Lock state.
		 * @var {bool}
		 */
		locked: false,

		/**
		 * Keyboard shortcuts.
		 * @var {object}
		 */
		keys: {

			// Escape: Toggle main wrapper.
				27: function() {
					_.toggle();
				},

			// Up: Move up.
				38: function() {
					_.up();
				},

			// Down: Move down.
				40: function() {
					_.down();
				},

			// Space: Next.
				32: function() {
					_.next();
				},

			// Right Arrow: Next.
				39: function() {
					_.next();
				},

			// Left Arrow: Previous.
				37: function() {
					_.previous();
				}

		},

		/**
		 * Initialize properties.
		 */
		initProperties: function() {

			// Window, body.
				_.$window = $(window);
				_.$body = $('body');

			// Thumbnails.
				_.$thumbnails = $('#thumbnails');

			// Viewer.
				_.$viewer = $(
					'<div id="viewer">' +
						'<div class="inner">' +
							'<div class="nav-next"></div>' +
							'<div class="nav-previous"></div>' +
							'<div class="toggle"></div>' +
						'</div>' +
					'</div>'
				).appendTo(_.$body);

			// Nav.
				_.$navNext = _.$viewer.find('.nav-next');
				_.$navPrevious = _.$viewer.find('.nav-previous');

			// Main wrapper.
				_.$main = $('#main');

			// Toggle.
				$('<div class="toggle"></div>')
					.appendTo(_.$main);

				_.$toggle = $('.toggle');

		},

		/**
		 * Initialize events.
		 */
		initEvents: function() {

			// Window.

				// Remove is-preload-* classes on load.
					_.$window.on('load', function() {

						_.$body.removeClass('is-preload-0');

						window.setTimeout(function() {
							_.$body.removeClass('is-preload-1');
						}, 100);

						window.setTimeout(function() {
							_.$body.removeClass('is-preload-2');
						}, 100 + Math.max(_.settings.layoutDuration - 150, 0));

					});

				// Disable animations/transitions on resize.
					var resizeTimeout;

					_.$window.on('resize', function() {

						_.$body.addClass('is-preload-0');
						window.clearTimeout(resizeTimeout);

						resizeTimeout = window.setTimeout(function() {
							_.$body.removeClass('is-preload-0');
						}, 100);

					});

			// Viewer.

				// Hide main wrapper on tap (<= medium only).
					_.$viewer.on('touchend', function() {

						if (breakpoints.active('<=medium'))
							_.hide();

					});

				// Touch gestures.
					_.$viewer
						.on('touchstart', function(event) {

							// Record start position.
								_.$viewer.touchPosX = event.originalEvent.touches[0].pageX;
								_.$viewer.touchPosY = event.originalEvent.touches[0].pageY;

						})
						.on('touchmove', function(event) {

							// No start position recorded? Bail.
								if (_.$viewer.touchPosX === null
								||	_.$viewer.touchPosY === null)
									return;

							// Calculate stuff.
								var	diffX = _.$viewer.touchPosX - event.originalEvent.touches[0].pageX,
									diffY = _.$viewer.touchPosY - event.originalEvent.touches[0].pageY;
									boundary = 20,
									delta = 50;

							// Swipe left (next).
								if ( (diffY < boundary && diffY > (-1 * boundary)) && (diffX > delta) )
									_.next();

							// Swipe right (previous).
								else if ( (diffY < boundary && diffY > (-1 * boundary)) && (diffX < (-1 * delta)) )
									_.previous();

							// Overscroll fix.
								var	th = _.$viewer.outerHeight(),
									ts = (_.$viewer.get(0).scrollHeight - _.$viewer.scrollTop());

								if ((_.$viewer.scrollTop() <= 0 && diffY < 0)
								|| (ts > (th - 2) && ts < (th + 2) && diffY > 0)) {

									event.preventDefault();
									event.stopPropagation();

								}

						});

			// Main.

				// Touch gestures.
					_.$main
						.on('touchstart', function(event) {

							// Bail on xsmall.
								if (breakpoints.active('<=xsmall'))
									return;

							// Record start position.
								_.$main.touchPosX = event.originalEvent.touches[0].pageX;
								_.$main.touchPosY = event.originalEvent.touches[0].pageY;

						})
						.on('touchmove', function(event) {

							// Bail on xsmall.
								if (breakpoints.active('<=xsmall'))
									return;

							// No start position recorded? Bail.
								if (_.$main.touchPosX === null
								||	_.$main.touchPosY === null)
									return;

							// Calculate stuff.
								var	diffX = _.$main.touchPosX - event.originalEvent.touches[0].pageX,
									diffY = _.$main.touchPosY - event.originalEvent.touches[0].pageY;
									boundary = 20,
									delta = 50,
									result = false;

							// Swipe to close.
								switch (_.settings.mainSide) {

									case 'left':
										result = (diffY < boundary && diffY > (-1 * boundary)) && (diffX > delta);
										break;

									case 'right':
										result = (diffY < boundary && diffY > (-1 * boundary)) && (diffX < (-1 * delta));
										break;

									default:
										break;

								}

								if (result)
									_.hide();

							// Overscroll fix.
								var	th = _.$main.outerHeight(),
									ts = (_.$main.get(0).scrollHeight - _.$main.scrollTop());

								if ((_.$main.scrollTop() <= 0 && diffY < 0)
								|| (ts > (th - 2) && ts < (th + 2) && diffY > 0)) {

									event.preventDefault();
									event.stopPropagation();

								}

						});
			// Toggle.
				_.$toggle.on('click', function() {
					_.toggle();
				});

				// Prevent event from bubbling up to "hide event on tap" event.
					_.$toggle.on('touchend', function(event) {
						event.stopPropagation();
					});

			// Nav.
				_.$navNext.on('click', function() {
					_.next();
				});

				_.$navPrevious.on('click', function() {
					_.previous();
				});

			// Keyboard shortcuts.

				// Ignore shortcuts within form elements.
					_.$body.on('keydown', 'input,select,textarea', function(event) {
						event.stopPropagation();
					});

				_.$window.on('keydown', function(event) {

					// Ignore if xsmall is active.
						if (breakpoints.active('<=xsmall'))
							return;

					// Check keycode.
						if (event.keyCode in _.keys) {

							// Stop other events.
								event.stopPropagation();
								event.preventDefault();

							// Call shortcut.
								(_.keys[event.keyCode])();

						}

				});

		},

		/**
		 * Initialize viewer.
		 */
		initViewer: function() {

			// Bind thumbnail click event.
				_.$thumbnails
					.on('click', '.thumbnail', function(event) {

						var $this = $(this);
						
						// Stop other events.
							event.preventDefault();
							event.stopPropagation();

						// Locked? Blur.
							if (_.locked)
								$this.blur();

						// Switch to this thumbnail's slide.
							_.switchTo($this.data('index'));

					});

			// Create slides from thumbnails.
				_.$thumbnails.children()
					.each(function() {
						var	$this = $(this),
							$thumbnail = $this.children('.thumbnail'),
							s;

						// Slide object.
							s = {
								$parent: $this,
								$slide: null,
								$slideImage: null,
								$slideCaption: null,
								url: $thumbnail.attr('href'),
								loaded: false
							};
							
						// Parent.
							$this.attr('tabIndex', '-1');

						// Slide.

							// Create elements.
		 						s.$slide = $('<div class="slide"><div class="caption"></div><div class="image"></div></div>');

		 					// Image.
	 							s.$slideImage = s.$slide.children('.image');

	 							// Set background stuff.
		 							s.$slideImage
			 							.css('background-image', '')
			 							.css('background-position', ($thumbnail.data('position') || 'center'));

							// Caption.
								s.$slideCaption = s.$slide.find('.caption');

								// Move everything *except* the thumbnail itself to the caption.
									$this.children().not($thumbnail)
										.appendTo(s.$slideCaption);

						// Preload?
							if (_.settings.preload) {

								// Force image to download.
									var $img = $('<img src="' + s.url + '" />');

								// Set slide's background image to it.
									s.$slideImage
										.css('background-image', 'url(' + s.url + ')');

								// Mark slide as loaded.
									s.$slide.addClass('loaded');
									s.loaded = true;

							}

						// Add to slides array.
							_.slides.push(s);

						// Set thumbnail's index.
							$thumbnail.data('index', _.slides.length - 1);

					});

		},

		/**
		 * Initialize stuff.
		 */
		init: function() {

			// Breakpoints.
				breakpoints({
					xlarge:  [ '1281px',  '1680px' ],
					large:   [ '981px',   '1280px' ],
					medium:  [ '737px',   '980px'  ],
					small:   [ '481px',   '736px'  ],
					xsmall:  [ null,      '480px'  ]
				});

			// Everything else.
				_.initProperties();
				_.initViewer();
				_.initEvents();

			// Show first slide if xsmall isn't active.
				breakpoints.on('>xsmall', function() {

					if (_.current === null)
						_.switchTo(0, true);

				});

		},

		/**
		 * Switch to a specific slide.
		 * @param {integer} index Index.
		 */
		switchTo: function(index, noHide) {

			// Already at index and xsmall isn't active? Bail.
				if (_.current == index
				&&	!breakpoints.active('<=xsmall'))
					return;

			// Locked? Bail.
				if (_.locked)
					return;

			// Lock.
				_.locked = true;

			// Hide main wrapper if medium is active.
				if (!noHide
				&&	breakpoints.active('<=medium'))
					_.hide();

			// Get slides.
				var	oldSlide = (_.current !== null ? _.slides[_.current] : null),
					newSlide = _.slides[index];
				
				
			// Update current.
				_.current = index;

			// Deactivate old slide (if there is one).
				if (oldSlide) {

					// Thumbnail.
						oldSlide.$parent
							.removeClass('active');

					// Slide.
						oldSlide.$slide.removeClass('active');

				}

			// Activate new slide.

				// Thumbnail.
					newSlide.$parent
						.addClass('active')
						.focus();
					
				// Slide.
					var f = function() {

						// Old slide exists? Detach it.
							if (oldSlide)
								oldSlide.$slide.detach();

						// Attach new slide.
							newSlide.$slide.appendTo(_.$viewer);

						// New slide not yet loaded?
							if (!newSlide.loaded) {

								window.setTimeout(function() {

									// Mark as loading.
										newSlide.$slide.addClass('loading');

									// Wait for it to load.
										$('<img src="' + newSlide.url + '" />').on('load', function() {
										//window.setTimeout(function() {

											// Set background image.
												newSlide.$slideImage
													.css('background-image', 'url(' + newSlide.url + ')');

											// Mark as loaded.
												newSlide.loaded = true;
												newSlide.$slide.removeClass('loading');

											// Mark as active.
												newSlide.$slide.addClass('active');

											// Unlock.
												window.setTimeout(function() {
													_.locked = false;
												}, 100);

										//}, 1000);
										});

								}, 100);

							}

						// Otherwise ...
							else {

								window.setTimeout(function() {

									// Mark as active.
										newSlide.$slide.addClass('active');

									// Unlock.
										window.setTimeout(function() {
											_.locked = false;
										}, 100);

								}, 100);

							}

					};

					// No old slide? Switch immediately.
						if (!oldSlide)
							(f)();

					// Otherwise, wait for old slide to disappear first.
						else
							window.setTimeout(f, _.settings.slideDuration);

		},

		/**
		 * Switches to the next slide.
		 */
		next: function() {

			// Calculate new index.
				var i, c = _.current, l = _.slides.length;

				if (c >= l - 1)
					i = 0;
				else
					i = c + 1;

			// Switch.
				_.switchTo(i);

		},

		/**
		 * Switches to the previous slide.
		 */
		previous: function() {

			// Calculate new index.
				var i, c = _.current, l = _.slides.length;

				if (c <= 0)
					i = l - 1;
				else
					i = c - 1;

			// Switch.
				_.switchTo(i);

		},

		/**
		 * Switches to slide "above" current.
		 */
		up: function() {

			// Fullscreen? Bail.
				if (_.$body.hasClass('fullscreen'))
					return;

			// Calculate new index.
				var i, c = _.current, l = _.slides.length, tpr = _.settings.thumbnailsPerRow;

				if (c <= (tpr - 1))
					i = l - (tpr - 1 - c) - 1;
				else
					i = c - tpr;

			// Switch.
				_.switchTo(i);

		},

		/**
		 * Switches to slide "below" current.
		 */
		down: function() {

			// Fullscreen? Bail.
				if (_.$body.hasClass('fullscreen'))
					return;

			// Calculate new index.
				var i, c = _.current, l = _.slides.length, tpr = _.settings.thumbnailsPerRow;

				if (c >= l - tpr)
					i = c - l + tpr;
				else
					i = c + tpr;

			// Switch.
				_.switchTo(i);

		},

		/**
		 * Shows the main wrapper.
		 */
		show: function() {

			// Already visible? Bail.
				if (!_.$body.hasClass('fullscreen'))
					return;

			// Show main wrapper.
				_.$body.removeClass('fullscreen');

			// Focus.
				_.$main.focus();

		},

		/**
		 * Hides the main wrapper.
		 */
		hide: function() {

			// Already hidden? Bail.
				if (_.$body.hasClass('fullscreen'))
					return;

			// Hide main wrapper.
				_.$body.addClass('fullscreen');

			// Blur.
				_.$main.blur();

		},

		/**
		 * Toggles main wrapper.
		 */
		toggle: function() {

			if (_.$body.hasClass('fullscreen'))
				_.show();
			else
				_.hide();

		},

	}; return _; })(jQuery);
	main.init();
		
		</script>
		
	
</body>
</html>