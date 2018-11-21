package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import dao.DAO;
import test.DaoTest;
import util.ExceptionClass;
import util.UtilClass;
import util.ValidateClass;

/**
 * View를 나눠주는 컨트롤러입니다.
 */
@WebServlet("*.pro")
public class BasicController extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI(); // 사용자가 요청한 주소
		//응답시 인코딩 타입 변경
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		String view = ""; //응답될 페이지
		System.out.println("요청한 주소:"+uri);
		if(uri.equals("/photoProject/modifyPassword.pro")) { //회원수정 비밀번호 인증페이지
			UtilClass util = new UtilClass();
			util.initRsa(request);
			view="/member/modifyPassword.jsp";
		}else if(uri.equals("/photoProject/register.pro")) { //회원가입 페이지
			UtilClass util = new UtilClass();
			util.initRsa(request);
			view="/member/register.jsp";
		}else if(uri.equals("/photoProject/main.pro")) { //메인페이지
			try {
				//메인페이지에서 메인사진을 썸네일로 표시하기 위해서 dao에서 사진을 가져온다.
				DAO dao = new DAO();
				Map<Integer,Object>photoMainMap=(HashMap)dao.photoBoard_mainPhotoThumbnail_select(0);
				//dao에서 넘어온 값은 오름차순으로 정렬되어있으므로 내림차순 정렬을 위해서 comparator객체를 통해 내림차순으로 바꿔준다.
				TreeMap<Integer,Object> photoMainTreeMap = new TreeMap<Integer,Object>(photoMainMap);
				Set<Integer> keySet = photoMainMap.keySet();
				Iterator<Integer> keyiterator = photoMainTreeMap.descendingKeySet().iterator();
				List<Integer> photoMainMapKeyList = new ArrayList<Integer>();
				while(keyiterator.hasNext()) {
					int key = keyiterator.next();
					photoMainMapKeyList.add(key);
				}

				if(photoMainMap!=null) {
					request.setAttribute("photoData", photoMainMap); //request 변수에 사진 데이터를 실어서 보낸다.
					request.setAttribute("photoDataKeyList", photoMainMapKeyList);
					view="/index.jsp";
				}
				dao.closeConn();
			} catch (NamingException | SQLException e) { 
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500오류 페이지로 보낸다.
				e.printStackTrace();
			}
			
		}else if(uri.equals("/photoProject/login.pro")) { //로그인 페이지
			UtilClass util = new UtilClass();
			util.initRsa(request);
			view="/member/login.jsp";
		}else if(uri.equals("/photoProject/memberModify.pro")) { //회원수정 페이지
			UtilClass util = new UtilClass();
			util.initRsa(request);
			view="/member/memberModify.jsp";
		}else if(uri.equals("/photoProject/photoUploadTable.pro")) { //포토 글쓰기
			view = "photoUploadTable.jsp";
		}else if(uri.equals("/photoProject/test.pro")) { //어떤 것을 테스트할 때 사용
			try {
				DaoTest test = new DaoTest();
				test.tagInsertTest();
			} catch (NamingException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(uri.equals("/photoProject/photoView.pro")) { //포토게시판 뷰페이지(상세 페이지)
			view = "/photoView/photoView.jsp";
		}else if(uri.equals("/photoProject/photoModify.pro")) { //포토게시판 수정 페이지
			ValidateClass validate = new ValidateClass();
			ExceptionClass exception = new ExceptionClass();
			Object userID=validate.isNullSessionValue(request, "userID"); //세션에 값이있는지 검증(여기서는 아이디값)
			if(userID==null) { //값이 없다면 로그인이 안된것
				exception.noLoginException(response);
			}else {
				//유저 아이디값이 글쓴이와 맞는지 검증.
				Boolean result=validate.loginUserCompare(request, request.getParameter("photoBoardWriter"));
				if(result==false) {
					exception.javaScriptToClient(response, "잘못된 접근입니다.");
					return ;
				}
				view = "/photoBoard/photoModifyTable.jsp";
			}
			
		}else if(uri.equals("")) {
			
		}else if(uri.equals("")) {
			
		}
		System.out.println("응답주소:"+view);
		if(!view.equals("")) {
			request.getRequestDispatcher(view).forward(request, response);
		}
		
	}
	
	/*doPost가 없으면 POST로 요청된 페이지는 HTTP Status 405 - HTTP method POST is not supported by this URL 오류가 발생.*/
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
	

}
