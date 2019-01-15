package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import dto.PhotoData;
import util.DBConnection;
import util.UtilClass;
import util.ValidateClass;
import vo.photoView.PhotoBoard_sub;
import vo.photoView.PhotoBoard_tag;
import vo.photoView.PhotoBoard_thumb;

public class DAO {
	
	private Connection conn = null;
	
	//Connection Pool에서 connection을 가져온다.
	public DAO() throws NamingException, SQLException {
		conn=DBConnection.getConnection();
	}
	
	/*받은 데이터를 검증하는 메소드*/
	/** 
	 * 
	 * 1. DB에서 중복된 ID값이 있는지 조회한다.
	 * 2. DB에서 중복된 Email이 있는지 조회한다.
	 * 3. 아이디 길이를 확인한다.
	 * 4. 패스워드 규칙에 맞는지 확인한다.
	 * 
	 **/
	public String validateData(String userID,String userPW, String userPWCK,String Email, ValidateClass validate, HttpServletRequest request) {
		String result="";
		
		try {
			 //1,2 아이디, 이메일 중복확인 동시에
			result=register_duplication_check(userID,Email);
			if(result==null) {
				//3,4 아이디 길이확인, 패스워드 규칙확인.
				result=validate.registerValidate(userID,userPW,userPWCK,request);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "내부오류";
		}
		return result;
	}
	
	/*검증이 끝난 사용자 데이터를 DB에 삽입하는 메소드*/
	public int insertAccountInfo(String userID, String userPW, String userEmail,String clientIP) {
		PreparedStatement preparedStatement=null;
		String query="INSERT INTO account_info (mem_userID,mem_userPW,mem_userEmail,mem_register_ip) values(?,SHA1(?),?,?)";
		
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, userID);
			preparedStatement.setString(2, userPW);
			preparedStatement.setString(3, userEmail);
			preparedStatement.setString(4, clientIP);
			return preparedStatement.executeUpdate(); //결과값을 리턴해준다. 1이라면 성공 그 외는 실패
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}finally {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	
	}
	
	/*중복을 확인하는 메소드*/
	/**
	 * 1. ID 중복확인
	 * 2. Email 중복확인
	 * */
	public String register_duplication_check(String userID,String userEmail) {
		String result=null;
		PreparedStatement preparedStatement=null;
		ResultSet rs = null;
		String query = null;
		try {
			if(userID!=null) {
				//아이디 중복확인
				query = "SELECT * FROM account_info WHERE mem_userID=?";
				preparedStatement = conn.prepareStatement(query);
				preparedStatement.setString(1, userID);
				rs = preparedStatement.executeQuery();
				if(rs.next()) {
					String existing_userID = rs.getString(2);
					if(existing_userID!=null) {
						System.out.println("DB에 이미 있는 아이디:"+existing_userID+"사용자가 입력한 아이디:"+userID);
						result = "아이디중복";
						return result;
					}
				}
			}
			if(userEmail!=null) {
				//Email 중복확인
				query = "SELECT * FROM account_info WHERE mem_userEmail=?";
				preparedStatement = conn.prepareStatement(query);
				preparedStatement.setString(1, userEmail);
				rs = preparedStatement.executeQuery();
				if(rs.next()) {
					String existing_email = rs.getString(4);
					if(existing_email!=null) {
						System.out.println("DB에 이미 있는 이메일:"+existing_email+"사용자가 입력한 이메일:"+existing_email);
						result="이메일중복";
						return result;
					}
				}
			}	
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				rs.close();
				preparedStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/*로그인하는 메소드*/
	/**
	 * 1. 존재하는 아이디인지 확인 
	 * 2. 아이디가 확인되었으면 암호가 맞는지 확인
	 * 
	 **/
	public String loginAuthentication(String userID, String userPW) {
		String result = null;
		PreparedStatement preparedStatement=null;
		ResultSet rs = null;
		String query = null;
		
		try {
			query = "SELECT * FROM account_info WHERE mem_userID=?";
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, userID);
			rs = preparedStatement.executeQuery();
			
			//1. 존재하는 아이디인지 확인
			if(rs.next()) { 
				String existing_userID = rs.getString(2);
				String password = rs.getString(3);
				String userEmail = rs.getString(4);
				//2. 아이디가 확인되었으면 암호가 맞는지 확인
				//SHA1으로 암호화 했기 떄문에 단방향으로 암호화가 된다. 
				//자바에서 SHA1으로 사용자가 입력한 값을 암호화한 후에 비교하는 방법을 사용했다.
				
				UtilClass util = new UtilClass();
				userPW=util.HashEncryption(userPW,"SHA-1");
				if(userPW.equals(password)) {
					/*UserDTO user = new UserDTO();
					user.setUserID(existing_userID);
					user.setUserEmail(userEmail);*/
					
					result="로그인완료//"+userEmail;
				}else {
					result="암호다름";
				}
			}else {
				result="아이디없음";
			}
			return result;
		}catch (SQLException  e) {
			e.printStackTrace();
		}finally {
			try {
				rs.close();
				preparedStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "오류";
		
		
	}
	
	/*
	 * 로그아웃 시간을 DB에 저장하는 메소드
	 * 
	 **/
	public int logoutTimeSave(String userID) {
		PreparedStatement preparedStatement=null;
		String query="UPDATE account_info SET mem_lastlogin_datetime = NOW() WHERE mem_userID=?";
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, userID);
			return preparedStatement.executeUpdate(); //결과값을 리턴해준다. 1이라면 성공 그 외는 실패
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
		
		
	}
	
	
	/*
	 * 포토 게시판의 제목, 메인사진명을 DB에 입력한다.
	 * @param stringParamMap 포토게시판의 제목, 사진내용, 테이블의 length가 들어있는 맵
	 * @param stringParamMap 포토게시판의 서브사진, 메인사진의 파일이름이 들어있는 맵
	 **/
	public int photoBoard_main_insert(Map<String,String> stringParamMap, Map<String,Map<String,String>> photoOrderMap) {
		
		String subject = stringParamMap.get("subject").toString(); //제목
		String imageMain = photoOrderMap.get("imageMain").get("serverUploadName"); //메인사진 파일명
		String imageThumb = photoOrderMap.get("mainImageThumb").get("serverUploadName"); //메인사진 썸네일명
		String a_line_review = stringParamMap.get("a_line_review").toString(); //한줄평
		String userID = stringParamMap.get("userID").toString(); //작성자 ID
		String userIP = stringParamMap.get("userIP").toString(); //작성자 IP
		
		PreparedStatement preparedStatement=null;
		String query="INSERT INTO photo_board_main (photo_subject,photo_main,photo_main_thumbnail,photo_a_line_review,photo_writer,photo_upload_ip) values(?,?,?,?,?,?)";
		
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, subject);
			preparedStatement.setString(2, imageMain);
			preparedStatement.setString(3, imageThumb);
			preparedStatement.setString(4, a_line_review);
			preparedStatement.setString(5, userID);
			preparedStatement.setString(6, userIP);
			
			int result=preparedStatement.executeUpdate(); //결과값을 리턴해준다. 1이라면 성공 그 외는 실패
			if(result==1) { //메인 테이블에 INSERT가 성공했다면
				query = "SELECT LAST_INSERT_ID()"; //현재 Connection에서 가장 최근에 성공한 AutoIncrement PK값을 가져온다.
				preparedStatement = conn.prepareStatement(query);
				ResultSet rs = preparedStatement.executeQuery();
				if(rs.next()) {
					int last_insert_id = rs.getInt(1);
					
					String subInsertResult=photoBoard_sub_insert(stringParamMap,photoOrderMap,last_insert_id); //서브사진, 서브내용들 DB INSERT 후 결과값 받기
					if(subInsertResult.equals("성공")) { //서브사진, 서브내용 DB INSERT가 성공했다면
						String tagInsertResult=photoBoard_tag_insert(stringParamMap,last_insert_id); //태그 insert 메소드
						/*if(tagInsertResult.equals("성공")) { //태그 DB INSERT 까지성공했다면
							result=1;
						}else if(tagInsertResult.equals("데이터 누락")) { //태그 DB INSERT가실패했다면
							result=-1;
						}*/
						String thumbImageName = photoOrderMap.get("imageMain").get("photoView_MainThumbImage"); //메인이미지 썸네일 파일명(photoView용)
						String thumbImageInsertResult=thumbImageInsert(stringParamMap, photoOrderMap, last_insert_id, 2, "photoView.pro", "main", thumbImageName); //썸네일 insert 메소드
						if(tagInsertResult.equals("성공") && thumbImageInsertResult.equals("성공")) { //태그 DB INSERT, 이미지 썸네일 DB INSERT 까지 성공했다면
							result=1;
						}else if(tagInsertResult.equals("데이터 누락") || thumbImageInsertResult.equals("데이터 누락")) { //태그 DB INSERT가 실패 하거나 이미지 썸네일 DB INSERT가 실패했다면
							result=-1;
						}
					}else if(subInsertResult.equals("데이터 누락")) { //서브사진, 서브내용 DB INSERT가 실패했다면
						photoBoard_tag_insert(stringParamMap,last_insert_id); //태그 insert 메소드 (이미 데이터 누락된거는 무조건 result에 -1이 들어간다.)
						//thumbImageInsert(stringParamMap, photoOrderMap, last_insert_id,"photoView.pro");//썸네일 insert 메소드(이미 데이터 누락된거는 무조건 result에 -1이 들어간다.)
						result=-1;
					}else if(subInsertResult.equals("SQL 오류")) {
						result=0;
					}
				}	
				return result;
				
			}
			return 0; //실패했다면 0반환
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}finally {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * 포토 게시판의 서브사진명, 사진내용을 입력한다.
	 * @param stringParamMap 포토게시판의 제목, 사진내용,태그, 테이블의 length가 들어있는 맵
	 * @param photoOrderMap 포토게시판의 서브사진, 메인사진의 파일이름이 들어있는 맵
	 * @param last_insert_id 마지막으로 insert에 성공한 auto increment PK의 값을 가져온다. (이 값을 sub Table의 FK값에 넣을 거임)
	 * */
	private String photoBoard_sub_insert(Map<String,String> stringParamMap, Map<String,Map<String,String>> photoOrderMap, int last_insert_id) {
		PreparedStatement preparedStatement=null;
		
		String query="";
		int photo_length = Integer.parseInt(stringParamMap.get("length").toString()); //테이블의 길이
		String result="성공"; //DB INSERT 결과값
		
		for(int subParamCount=0; subParamCount<=photo_length; subParamCount++) {
			String subImg = photoOrderMap.get("image_"+subParamCount).get("serverUploadName"); //서브사진의 파일명
			String subImgContent = stringParamMap.get("content_"+subParamCount); //서브사진의 내용
			String thumbImageName = photoOrderMap.get("image_"+subParamCount).get("photoView_SubThumbImage"); //서브사진의 썸네일 파일명
			query="INSERT INTO photo_board_sub (photo_boardNo,photo_ownNo,photo_sub,photo_content) values(?,?,?,?)";
			try {
				preparedStatement = conn.prepareStatement(query);
				preparedStatement.setInt(1,last_insert_id); //메인 테이블의 photoBoardNo
				preparedStatement.setInt(2, subParamCount); //게시글 내에 서브이미지의 개수
				preparedStatement.setString(3, subImg);  //서브이미지 파일명
				preparedStatement.setString(4, subImgContent); //서브이미지 설명
				
				int insertResult=preparedStatement.executeUpdate(); //결과값을 리턴해준다. 1이라면 성공 그 외는 실패
				if(insertResult!=1) { //테이블에 INSERT가 실패했다면
					System.out.println("*************subPhoto DB INSERT FAIL!*************");
					System.out.println("file Name:"+subImg+", length:"+subParamCount);
					System.out.println("**************************************************");
					result = "데이터 누락";
				}else { //테이블에 INSERT가 성공했다면
					query = "SELECT LAST_INSERT_ID()"; //서브이미지 썸네일 저장을 위해서 서브이미지 고유번호를 가져온다.
					preparedStatement = conn.prepareStatement(query);
					ResultSet rs = preparedStatement.executeQuery();
					if(rs.next()) {
						int subImgNo = rs.getInt(1); 
						//해당 서브이미지의 썸네일 이미지 파일명을 DB에 넣는다.
						result=thumbImageInsert(stringParamMap, photoOrderMap, last_insert_id,subImgNo, "photoView.pro", "sub", thumbImageName);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				result="SQL 오류";
			}
		}
		
		//마지막에 사용한 자원을 닫아준다.
		try {
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result; //일단은 성공적으로 완료했다는 표시..
	}
	
	/*포토게시판의 태그를 태그테이블에 입력한다.
	 * @param stringParamMap 포토게시판의 제목, 사진내용,태그, 테이블의 length가 들어있는 맵
	 * @param last_insert_id 마지막으로 insert에 성공한 auto increment PK의 값을 가져온다. (이 값을 sub Table의 FK값에 넣을 거임)
	 * */
	private String photoBoard_tag_insert(Map<String,String> stringParamMap, int last_insert_id) {
		/*DB 관련*/
		String result="성공"; //결과값
		String query = "INSERT INTO photo_board_tags (photo_boardNo,photo_tag) VALUES (?,?)"; //tag를 tag테이클에 넣는 SQL문
		PreparedStatement ps = null;
		
		String tags = stringParamMap.get("tags"); //맵에서 사용자가 입력한 태그값을 가져온다.
		StringTokenizer st = new StringTokenizer(tags, ","); //문자열을 자르는 tokenizer (자를문자열, 구분자)
		//토큰이 있을 때까지 반복한다.
		for(int i=0; st.hasMoreTokens(); i++) {
			String tag = st.nextToken();
			try {
				ps=conn.prepareStatement(query);
				ps.setInt(1, last_insert_id);
				ps.setString(2, tag);
				
				int insertResult=ps.executeUpdate(); //결과값을 리턴해준다. 1이라면 성공 그 외는 실패
				if(insertResult!=1) { //메인 테이블에 INSERT가 성공했다면
					System.out.println("*************TAG DB INSERT FAIL!*************");
					System.out.println("TAG Name:"+tag);
					System.out.println("**************************************************");
					result = "데이터 누락";
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		} //end of For
		
		//마지막에 사용한 자원을 닫아준다.
		try {
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/*
	 * 썸네일 이미지 파일명을 DB에 입력한다.
	 * @param stringParamMap 포토게시판의 제목, 사진내용,태그, 테이블의 length가 들어있는 맵
	 * @param photoOrderMap 포토게시판의 서브사진, 메인사진의 파일이름이 들어있는 맵
	 * @param last_insert_id 마지막으로 insert에 성공한 auto increment PK의 값을 가져온다. (게시글 고유번호)
	 * @param subImgNo 마지막으로 insert에 성공한 auto increment PK의 값을 가져온다. (서브이미지 고유번호)
	 * @param photo_thumbUsage 이 썸네일이 어디에서 쓰이는지 알려주는 변수
	 * @param typeFlag 이 썸네일의 타입 (main인지 sub인지)
	 * @param thumbImageName 이 썸네일이미지의 이름
	 * */
	private String thumbImageInsert(Map<String,String> stringParamMap, Map<String,Map<String,String>> photoOrderMap
			, int last_insert_id,int subImgNo, String photo_thumbUsage, String typeFlag, String thumbImageName) {
		String result="성공";
		PreparedStatement ps = null;
		String query = "INSERT INTO photo_board_thumb (photo_boardNo,photo_subNo,photo_thumbType,photo_thumbName,photo_thumbUsage) VALUES (?,?,?,?,?)";
		System.out.println("서브의 LastID:"+subImgNo);
		try {
			ps=conn.prepareStatement(query);
			ps.setInt(1, last_insert_id); //photo_boardNo 입력
			ps.setInt(2, subImgNo); //photo_subNo 입력
			if(typeFlag.equals("sub")) {
			//서브 이미지 썸네일 DB저장
				ps.setString(3, typeFlag); //thumbType입력 (main, sub)
			}else if(typeFlag.equals("main")) {
				//메인 이미지 썸네일 DB저장
				ps.setString(3, typeFlag); //thumbType입력 (main, sub)
			}
			ps.setString(4, thumbImageName); //thumbImageName입력 (썸네일 이미지 파일명)
			ps.setString(5, photo_thumbUsage); //photo_thumbUsage 입력(이미지가 어느 페이지에쓰일지)
			int insertResult = ps.executeUpdate(); //결과값 리턴 (1이라면 성공, 0이라면 실패)
			if(insertResult!=1) {
				result="데이터 누락";
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/*
	 * 메인페이지가 불려질 때 DB에서 mainPhoto 파일명을 가져오는 메소드 (메인페이지에서는 mainPhoto를 Thumbnail로 사용한다.)
	 * @param offset 어디서부터 가지고올지 정하는 파라미터
	 * 
	 * */
	public Object photoBoard_mainPhotoThumbnail_select(int offset) {
		String query = "SELECT * FROM photo_board_main ORDER BY photo_boardNo DESC LIMIT 12 OFFSET ?";
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		Map<Integer,Object>photoMainMap = new HashMap<Integer,Object>(); //쿼리결과가 들어갈 메인맵
		Map<String,Object>photoMainMap_sub = new HashMap<String,Object>(); //쿼리결과가 들어갈 서브맵
	
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, offset);
			rs=preparedStatement.executeQuery();
			while(rs.next()) {
				//결과값을 맵에 넣는다.
				photoMainMap_sub.put("photo_boardNo", rs.getInt(1));
				photoMainMap_sub.put("photo_subject", rs.getString(2));
				photoMainMap_sub.put("photo_main", rs.getString(3));
				photoMainMap_sub.put("photo_main_thumbnail", rs.getString(4));
				photoMainMap_sub.put("photo_a_line_review", rs.getString(5));
				photoMainMap_sub.put("photo_writer", rs.getString(6));
				photoMainMap_sub.put("photo_upload_date", rs.getString(7));
				photoMainMap.put(rs.getInt(1), photoMainMap_sub);
				photoMainMap_sub = new HashMap<String,Object>(); //초기화
			}
			return photoMainMap;
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				rs.close();
				preparedStatement.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/*
	 * 포토 뷰 페이지 데이터를 가져오는 메소드 (DTO를 안쓴다.)
	 * @param photoBoardNo 게시글 고유번호를 받아서 가져온다.
	 **/
	public PhotoData getPhotoViewData(int photoBoardNo) {
		String query = "SELECT "
				+ "main.photo_boardNo,main.photo_subject,main.photo_main,main.photo_a_line_review,main.photo_writer,main.photo_upload_date, "
				+ "sub.photo_subNo, sub.photo_ownNo, sub.photo_sub, sub.photo_content, "
				+ "thumb.photo_thumbNo, thumb.photo_thumbType, thumb.photo_thumbName "
				+ "FROM photo_board_main main INNER JOIN photo_board_sub sub ON main.photo_boardNo=sub.photo_boardNo "
				+ "INNER JOIN photo_board_thumb thumb ON sub.photo_subNo=thumb.photo_subNo "
				+ "WHERE thumb.photo_boardNo=? AND thumb.photo_thumbUsage='photoView.pro' "
				+ "ORDER BY sub.photo_ownNo ASC";
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, photoBoardNo);
			rs = preparedStatement.executeQuery();
			PhotoData data = new PhotoData(); //포토 데이터 DTO
			while(rs.next()) {
				data.setPhoto_boardNo(rs.getInt(1)); //게시글 고유번호
				data.setPhoto_subject(rs.getString(2));//게시글 제목
				data.setPhoto_main(rs.getString(3));//게시글 메인이미지 파일명
				data.setPhoto_a_line_review(rs.getString(4));//게시글 한줄평
				data.setPhoto_writer(rs.getString(5));//게시글 작성자
				data.setPhoto_upload_date(rs.getString(6));//게시글 작성날짜 및 시간
				
				PhotoBoard_sub sub = new PhotoBoard_sub(); //서브 데이터 (1행에 해당하는)
				sub.setPhoto_subNo(rs.getInt(7)); //서브 데이터 고유번호
				sub.setPhoto_ownNo(rs.getInt(8)); //서브 이미지 게시글 내부 번호
				sub.setPhoto_sub(rs.getString(9));//서브 이미지 파일명
				sub.setPhoto_content(rs.getString(10)); //서브 이미지 설명
				data.addPhotoBoard_sub(sub); //서브이미지 List에 add
				
				PhotoBoard_thumb thumb = new PhotoBoard_thumb(); //이미지 썸네일 데이터(1행에 해당하는)
				thumb.setPhoto_thumbNo(rs.getInt(11)); //메인,서브 이미지 썸네일 고유번호
				thumb.setPhoto_thumbType(rs.getString(12)); //썸네일 타입(main, sub)
				thumb.setPhoto_thumbName(rs.getString(13)); //썸네일 이미지 파일명
				data.addPhotoBoard_thumb(thumb); //썸네일 List에 add
				
			}
			
			//태그는 따로 가지고온다.
			query = "SELECT photo_tagNo, photo_tag "
					+ "FROM photo_board_main main "
					+ "INNER JOIN photo_board_tags tags "
					+ "ON main.photo_boardNo = tags.photo_boardNo "
					+ "WHERE main.photo_boardNo=?";
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, photoBoardNo);
			rs = preparedStatement.executeQuery();
			while (rs.next()) {
				PhotoBoard_tag tag = new PhotoBoard_tag();
				tag.setTagNo(rs.getInt(1));
				tag.setPhoto_tag(rs.getString(2));
				data.addPhotoBoard_tag(tag);
			}
			//System.out.println(data.toString());
			return data;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	/*
	 * 수정 페이지에서 지워진 서브포토 DB에서 삭제하기
	 * @param photoSubNo : 지워질 서브포토의 DB고유번호
	 * */
	public String deleteSubPhoto(int photoSubNo) {
		String result="성공";
		PreparedStatement ps = null;
		String query = "DELETE FROM photo_board_sub WHERE photo_subNo=?";
		
		try {
			ps=conn.prepareStatement(query);
			ps.setInt(1, photoSubNo); //photo_boardNo 입력
			int deleteResult = ps.executeUpdate(); //SQL 문장 실행 후, 변경된 row 수를 int type 으로 리턴합니다.
			if(deleteResult==0) {
				result="DB오류_deleteSubPhoto";
			}
		}catch (SQLException e) {
			e.printStackTrace();
			result="DB오류_deleteSubPhoto";
			return result;
		}
		
		return result;
		
	}
	
	/*
	 * DB의 기존 서브포토들의 순서를 바꾸는 메소드
	 * @param photo_ownNo : 해당 게시글에서의 서브포토의 순서
	 * @param photo_subNo : 해당 게시글에서의 서브포토 DB 고유값
	 * */
	public String updatePhotosOrder(int photo_ownNo,int photo_subNo) {
		String result="성공";
		PreparedStatement ps = null;
		String query = "UPDATE photo_board_sub SET photo_ownNo=? WHERE photo_subNo=?;";
		
		try {
			ps=conn.prepareStatement(query);
			ps.setInt(1, photo_ownNo); //photo_boardNo 입력
			ps.setInt(2, photo_subNo); //photo_boardNo 입력
			int updateResult = ps.executeUpdate(); //SQL 문장 실행 후, 변경된 row 수를 int type 으로 리턴합니다.
			if(updateResult==0) {
				result="DB오류_updatePhotosOrder";
			}
		}catch (SQLException e) {
			e.printStackTrace();
			result="DB오류_updatePhotosOrder";
			return result;
		}
		return result;
	}
	
	/*
	 * DB의 기존 서브포토 내용들을 바꾸는 메소드
	 * @param photo_subNo:DB고유값
	 * @param contents:바꿀 내용
	 * */
	public String updateSubPhotoContent(int photo_subNo, String contents) {
		String result="성공";
		PreparedStatement ps = null;
		String query = "UPDATE photo_board_sub SET photo_content=? WHERE photo_subNo=?;";
		
		try {
			ps=conn.prepareStatement(query);
			ps.setString(1, contents); //photo_boardNo 입력
			ps.setInt(2, photo_subNo); //photo_boardNo 입력
			int updateResult = ps.executeUpdate(); //SQL 문장 실행 후, 변경된 row 수를 int type 으로 리턴합니다.
			if(updateResult==0) {
				result="DB오류_updateSubPhotoContent";
			}
		}catch (SQLException e) {
			e.printStackTrace();
			result="DB오류_updateSubPhotoContent";
			return result;
		}
		
		return result;
	}
	
	/*
	 * DB의 기존 서브포토 파일명을 바꾸는 메소드
	 * @param photo_subNo:DB고유값
	 * @param fileName:바꿀 파일명
	 * */
	public String updateSubPhotoFileName(int photo_subNo, String fileName) {
		String result="성공";
		PreparedStatement ps = null;
		String query = "UPDATE photo_board_sub SET photo_sub=? WHERE photo_subNo=?;";
		
		try {
			ps=conn.prepareStatement(query);
			ps.setString(1, fileName); //photo_boardNo 입력
			ps.setInt(2, photo_subNo); //photo_boardNo 입력
			int updateResult = ps.executeUpdate(); //SQL 문장 실행 후, 변경된 row 수를 int type 으로 리턴합니다.
			if(updateResult==0) {
				result="DB오류_updateSubPhotoContent";
			}
		}catch (SQLException e) {
			e.printStackTrace();
			result="DB오류_updateSubPhotoContent";
			return result;
		}
		
		return result;
	}
	
	/*
	 * DB의 기존 서브포토 썸네일명을 바꾸는 메소드
	 * @param photo_subNo:DB고유값
	 * @param thumbName:바꿀 썸네일명
	 * */
	public String updateSubPhotoThumbName(int photo_subNo, String thumbName) {
		String result="성공";
		PreparedStatement ps = null;
		String query = "UPDATE photo_board_sub SET photo_sub=? WHERE photo_subNo=?;";
		
		try {
			ps=conn.prepareStatement(query);
			ps.setString(1, thumbName); //photo_boardNo 입력
			ps.setInt(2, photo_subNo); //photo_boardNo 입력
			int updateResult = ps.executeUpdate(); //SQL 문장 실행 후, 변경된 row 수를 int type 으로 리턴합니다.
			if(updateResult==0) {
				result="DB오류_updateSubPhotoContent";
			}
		}catch (SQLException e) {
			e.printStackTrace();
			result="DB오류_updateSubPhotoContent";
			return result;
		}
		
		return result;
	}
	
	/*DAO 사용이 끝나면 Conn을 닫아주기 위한 메소드*/
	public void closeConn() {
		try {
			conn.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
