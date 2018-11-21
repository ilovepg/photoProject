package api;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.NamingException;

import org.json.simple.JSONObject;

import dao.DAO;
import util.UtilClass;

public class MainProAPI {
	
	public JSONObject getMainPhoto(int offset) {
		try {
			//�������������� ���λ����� ����Ϸ� ǥ���ϱ� ���ؼ� dao���� ������ �����´�.
			DAO dao = new DAO();
			Map<Integer,Object>photoMainMap=(HashMap)dao.photoBoard_mainPhotoThumbnail_select(offset);
			//dao���� �Ѿ�� ���� ������������ ���ĵǾ������Ƿ� �������� ������ ���ؼ� comparator��ü�� ���� ������������ �ٲ��ش�.
			TreeMap<Integer,Object> photoMainTreeMap = new TreeMap<Integer,Object>(photoMainMap); 
			
			Iterator<Integer> keyiterator = photoMainTreeMap.descendingKeySet().iterator(); //Ű���� ������������ �ִ´�.
			List<Integer> photoMainMapKeyList = new ArrayList<Integer>(); //������������ ���� Ű���� �� ����Ʈ
			while(keyiterator.hasNext()) {
				int key = keyiterator.next(); //Ű���� �ϳ��� ������ ����Ʈ�� �ִ´�.
				photoMainMapKeyList.add(key); 
			}
			photoMainMap.put(-1, photoMainMapKeyList); //������������ �� Ű���� ������ �ִ� ����Ʈ�� return�� �ʿ� �ִ´�.
			if(photoMainMap!=null) {
				//request.setAttribute("photoData", photoMainMap); //request ������ ���� �����͸� �Ǿ ������.
				//request.setAttribute("photoDataKeyList", photoMainMapKeyList);
				UtilClass util = new UtilClass();
				JSONObject jsonObject=util.getJsonStringFromMap(photoMainMap);
				System.out.println(jsonObject);
				dao.closeConn();
				return jsonObject;
			}
		
		} catch (NamingException | SQLException e) { 
			//response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500���� �������� ������.
			e.printStackTrace();
		}
		
		return null;
	}
	
}
