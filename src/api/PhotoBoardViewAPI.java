package api;

import java.sql.SQLException;

import javax.naming.NamingException;

import com.google.gson.Gson;

import dao.DAO;
import dto.PhotoData;

public class PhotoBoardViewAPI {
	public String getPhotoDetailData(int photoBoardNo) {
		
		//DAO를 통해서 게시글고유번호에 해당하는 데이터를 가져온다.
		try {
			DAO dao = new DAO();
			PhotoData photoData=dao.getPhotoViewData(photoBoardNo);
			
			if(photoData==null) { //null이 넘어오면 예외처리
				return null;
			}
			
			//GSON을 사용하여 객체를 JSON으로 변환
			Gson gson = new Gson();
			String resultJson = gson.toJson(photoData);
			System.out.println("getPhotoDetailData result:"+resultJson);
			dao.closeConn(); //커넥션을 닫아준다.
			return resultJson;
		} catch (NamingException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
