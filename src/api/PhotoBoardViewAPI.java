package api;

import java.sql.SQLException;

import javax.naming.NamingException;

import com.google.gson.Gson;

import dao.DAO;
import dto.PhotoData;

public class PhotoBoardViewAPI {
	public String getPhotoDetailData(int photoBoardNo) {
		
		//DAO�� ���ؼ� �Խñ۰�����ȣ�� �ش��ϴ� �����͸� �����´�.
		try {
			DAO dao = new DAO();
			PhotoData photoData=dao.getPhotoViewData(photoBoardNo);
			
			if(photoData==null) { //null�� �Ѿ���� ����ó��
				return null;
			}
			
			//GSON�� ����Ͽ� ��ü�� JSON���� ��ȯ
			Gson gson = new Gson();
			String resultJson = gson.toJson(photoData);
			System.out.println("getPhotoDetailData result:"+resultJson);
			dao.closeConn(); //Ŀ�ؼ��� �ݾ��ش�.
			return resultJson;
		} catch (NamingException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
