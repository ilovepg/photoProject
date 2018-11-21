package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import api.MainProAPI;
import api.PhotoBoardViewAPI;

/**
 * 클라이언트에서 API요청을하면 API를 나눠주는 컨트롤러입니다.
 */
@WebServlet("/apiController")
public class APIController extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String apiName = request.getParameter("apiName"); //호출된 API네임
		String methodName = request.getParameter("methodName"); //호출된 메소드
		
		//응답할 JSON 형식으로 응답
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		if(apiName.equals("MainProAPI")) { //main API
			MainProAPI mainProAPI = new MainProAPI();
			if(methodName.equals("getMainPhoto")) { //메인이미지의 썸네일을 가져오는 메소드
				int offset = Integer.parseInt(request.getParameter("offset").toString()); //몇 번째 이미지부터 가져와야하는지 offset
				JSONObject jsonObject=mainProAPI.getMainPhoto(offset);
				if(jsonObject!=null) { //결과값이 null이 아니라면 응답
					out.print(jsonObject);
					out.flush();
					System.out.println("응답완료");
				}
			}else if(methodName.equals("")) {
				
			}else if(methodName.equals("")) {
				
			}
		}else if(apiName.equals("PhotoBoardViewAPI")) { // photoView API
			PhotoBoardViewAPI api = new PhotoBoardViewAPI();
			if(methodName.equals("getPhotoDetailData")) { //photoViewData를 가져오는 메소드
				int photoBoardNo = Integer.parseInt(request.getParameter("photoBoardNo").toString());
				String result=api.getPhotoDetailData(photoBoardNo);
				
				JSONObject jsonObject=new JSONObject(); //응답을 위한 JSON객체생성
				
				if(result==null) { //result가 null이 넘어왔다면
					jsonObject.put("PhotoBoardViewAPI_getPhotoDetailData_result", "fail"); //오류발생을 알린다.
				}else {
					jsonObject.put("PhotoBoardViewAPI_getPhotoDetailData_result", result); //데이터가 제대로 넘어왔을때는 데이터를 보내준다.
				}
				//응답
				out.print(jsonObject);
				out.flush();
			}
		}
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
