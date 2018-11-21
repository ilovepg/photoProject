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
			//메인페이지에서 메인사진을 썸네일로 표시하기 위해서 dao에서 사진을 가져온다.
			DAO dao = new DAO();
			Map<Integer,Object>photoMainMap=(HashMap)dao.photoBoard_mainPhotoThumbnail_select(offset);
			//dao에서 넘어온 값은 오름차순으로 정렬되어있으므로 내림차순 정렬을 위해서 comparator객체를 통해 내림차순으로 바꿔준다.
			TreeMap<Integer,Object> photoMainTreeMap = new TreeMap<Integer,Object>(photoMainMap); 
			
			Iterator<Integer> keyiterator = photoMainTreeMap.descendingKeySet().iterator(); //키값을 내림차순으로 넣는다.
			List<Integer> photoMainMapKeyList = new ArrayList<Integer>(); //내림차순으로 넣은 키값이 들어갈 리스트
			while(keyiterator.hasNext()) {
				int key = keyiterator.next(); //키값을 하나씩 꺼내서 리스트에 넣는다.
				photoMainMapKeyList.add(key); 
			}
			photoMainMap.put(-1, photoMainMapKeyList); //내림차순으로 들어간 키값을 가지고 있는 리스트를 return할 맵에 넣는다.
			if(photoMainMap!=null) {
				//request.setAttribute("photoData", photoMainMap); //request 변수에 사진 데이터를 실어서 보낸다.
				//request.setAttribute("photoDataKeyList", photoMainMapKeyList);
				UtilClass util = new UtilClass();
				JSONObject jsonObject=util.getJsonStringFromMap(photoMainMap);
				System.out.println(jsonObject);
				dao.closeConn();
				return jsonObject;
			}
		
		} catch (NamingException | SQLException e) { 
			//response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500오류 페이지로 보낸다.
			e.printStackTrace();
		}
		
		return null;
	}
	
}
