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
	
	//Connection Pool���� connection�� �����´�.
	public DAO() throws NamingException, SQLException {
		conn=DBConnection.getConnection();
	}
	
	/*���� �����͸� �����ϴ� �޼ҵ�*/
	/** 
	 * 
	 * 1. DB���� �ߺ��� ID���� �ִ��� ��ȸ�Ѵ�.
	 * 2. DB���� �ߺ��� Email�� �ִ��� ��ȸ�Ѵ�.
	 * 3. ���̵� ���̸� Ȯ���Ѵ�.
	 * 4. �н����� ��Ģ�� �´��� Ȯ���Ѵ�.
	 * 
	 **/
	public String validateData(String userID,String userPW, String userPWCK,String Email, ValidateClass validate, HttpServletRequest request) {
		String result="";
		
		try {
			 //1,2 ���̵�, �̸��� �ߺ�Ȯ�� ���ÿ�
			result=register_duplication_check(userID,Email);
			if(result==null) {
				//3,4 ���̵� ����Ȯ��, �н����� ��ĢȮ��.
				result=validate.registerValidate(userID,userPW,userPWCK,request);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "���ο���";
		}
		return result;
	}
	
	/*������ ���� ����� �����͸� DB�� �����ϴ� �޼ҵ�*/
	public int insertAccountInfo(String userID, String userPW, String userEmail,String clientIP) {
		PreparedStatement preparedStatement=null;
		String query="INSERT INTO account_info (mem_userID,mem_userPW,mem_userEmail,mem_register_ip) values(?,SHA1(?),?,?)";
		
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, userID);
			preparedStatement.setString(2, userPW);
			preparedStatement.setString(3, userEmail);
			preparedStatement.setString(4, clientIP);
			return preparedStatement.executeUpdate(); //������� �������ش�. 1�̶�� ���� �� �ܴ� ����
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
	
	/*�ߺ��� Ȯ���ϴ� �޼ҵ�*/
	/**
	 * 1. ID �ߺ�Ȯ��
	 * 2. Email �ߺ�Ȯ��
	 * */
	public String register_duplication_check(String userID,String userEmail) {
		String result=null;
		PreparedStatement preparedStatement=null;
		ResultSet rs = null;
		String query = null;
		try {
			if(userID!=null) {
				//���̵� �ߺ�Ȯ��
				query = "SELECT * FROM account_info WHERE mem_userID=?";
				preparedStatement = conn.prepareStatement(query);
				preparedStatement.setString(1, userID);
				rs = preparedStatement.executeQuery();
				if(rs.next()) {
					String existing_userID = rs.getString(2);
					if(existing_userID!=null) {
						System.out.println("DB�� �̹� �ִ� ���̵�:"+existing_userID+"����ڰ� �Է��� ���̵�:"+userID);
						result = "���̵��ߺ�";
						return result;
					}
				}
			}
			if(userEmail!=null) {
				//Email �ߺ�Ȯ��
				query = "SELECT * FROM account_info WHERE mem_userEmail=?";
				preparedStatement = conn.prepareStatement(query);
				preparedStatement.setString(1, userEmail);
				rs = preparedStatement.executeQuery();
				if(rs.next()) {
					String existing_email = rs.getString(4);
					if(existing_email!=null) {
						System.out.println("DB�� �̹� �ִ� �̸���:"+existing_email+"����ڰ� �Է��� �̸���:"+existing_email);
						result="�̸����ߺ�";
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
	
	/*�α����ϴ� �޼ҵ�*/
	/**
	 * 1. �����ϴ� ���̵����� Ȯ�� 
	 * 2. ���̵� Ȯ�εǾ����� ��ȣ�� �´��� Ȯ��
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
			
			//1. �����ϴ� ���̵����� Ȯ��
			if(rs.next()) { 
				String existing_userID = rs.getString(2);
				String password = rs.getString(3);
				String userEmail = rs.getString(4);
				//2. ���̵� Ȯ�εǾ����� ��ȣ�� �´��� Ȯ��
				//SHA1���� ��ȣȭ �߱� ������ �ܹ������� ��ȣȭ�� �ȴ�. 
				//�ڹٿ��� SHA1���� ����ڰ� �Է��� ���� ��ȣȭ�� �Ŀ� ���ϴ� ����� ����ߴ�.
				
				UtilClass util = new UtilClass();
				userPW=util.HashEncryption(userPW,"SHA-1");
				if(userPW.equals(password)) {
					/*UserDTO user = new UserDTO();
					user.setUserID(existing_userID);
					user.setUserEmail(userEmail);*/
					
					result="�α��οϷ�//"+userEmail;
				}else {
					result="��ȣ�ٸ�";
				}
			}else {
				result="���̵����";
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
		return "����";
		
		
	}
	
	/*
	 * �α׾ƿ� �ð��� DB�� �����ϴ� �޼ҵ�
	 * 
	 **/
	public int logoutTimeSave(String userID) {
		PreparedStatement preparedStatement=null;
		String query="UPDATE account_info SET mem_lastlogin_datetime = NOW() WHERE mem_userID=?";
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, userID);
			return preparedStatement.executeUpdate(); //������� �������ش�. 1�̶�� ���� �� �ܴ� ����
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
		
		
	}
	
	
	/*
	 * ���� �Խ����� ����, ���λ������� DB�� �Է��Ѵ�.
	 * @param stringParamMap ����Խ����� ����, ��������, ���̺��� length�� ����ִ� ��
	 * @param stringParamMap ����Խ����� �������, ���λ����� �����̸��� ����ִ� ��
	 **/
	public int photoBoard_main_insert(Map<String,String> stringParamMap, Map<String,Map<String,String>> photoOrderMap) {
		
		String subject = stringParamMap.get("subject").toString(); //����
		String imageMain = photoOrderMap.get("imageMain").get("serverUploadName"); //���λ��� ���ϸ�
		String imageThumb = photoOrderMap.get("mainImageThumb").get("serverUploadName"); //���λ��� ����ϸ�
		String a_line_review = stringParamMap.get("a_line_review").toString(); //������
		String userID = stringParamMap.get("userID").toString(); //�ۼ��� ID
		String userIP = stringParamMap.get("userIP").toString(); //�ۼ��� IP
		
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
			
			int result=preparedStatement.executeUpdate(); //������� �������ش�. 1�̶�� ���� �� �ܴ� ����
			if(result==1) { //���� ���̺� INSERT�� �����ߴٸ�
				query = "SELECT LAST_INSERT_ID()"; //���� Connection���� ���� �ֱٿ� ������ AutoIncrement PK���� �����´�.
				preparedStatement = conn.prepareStatement(query);
				ResultSet rs = preparedStatement.executeQuery();
				if(rs.next()) {
					int last_insert_id = rs.getInt(1);
					
					String subInsertResult=photoBoard_sub_insert(stringParamMap,photoOrderMap,last_insert_id); //�������, ���곻��� DB INSERT �� ����� �ޱ�
					if(subInsertResult.equals("����")) { //�������, ���곻�� DB INSERT�� �����ߴٸ�
						String tagInsertResult=photoBoard_tag_insert(stringParamMap,last_insert_id); //�±� insert �޼ҵ�
						/*if(tagInsertResult.equals("����")) { //�±� DB INSERT ���������ߴٸ�
							result=1;
						}else if(tagInsertResult.equals("������ ����")) { //�±� DB INSERT�������ߴٸ�
							result=-1;
						}*/
						String thumbImageName = photoOrderMap.get("imageMain").get("photoView_MainThumbImage"); //�����̹��� ����� ���ϸ�(photoView��)
						String thumbImageInsertResult=thumbImageInsert(stringParamMap, photoOrderMap, last_insert_id, 2, "photoView.pro", "main", thumbImageName); //����� insert �޼ҵ�
						if(tagInsertResult.equals("����") && thumbImageInsertResult.equals("����")) { //�±� DB INSERT, �̹��� ����� DB INSERT ���� �����ߴٸ�
							result=1;
						}else if(tagInsertResult.equals("������ ����") || thumbImageInsertResult.equals("������ ����")) { //�±� DB INSERT�� ���� �ϰų� �̹��� ����� DB INSERT�� �����ߴٸ�
							result=-1;
						}
					}else if(subInsertResult.equals("������ ����")) { //�������, ���곻�� DB INSERT�� �����ߴٸ�
						photoBoard_tag_insert(stringParamMap,last_insert_id); //�±� insert �޼ҵ� (�̹� ������ �����ȰŴ� ������ result�� -1�� ����.)
						//thumbImageInsert(stringParamMap, photoOrderMap, last_insert_id,"photoView.pro");//����� insert �޼ҵ�(�̹� ������ �����ȰŴ� ������ result�� -1�� ����.)
						result=-1;
					}else if(subInsertResult.equals("SQL ����")) {
						result=0;
					}
				}	
				return result;
				
			}
			return 0; //�����ߴٸ� 0��ȯ
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
	 * ���� �Խ����� ���������, ���������� �Է��Ѵ�.
	 * @param stringParamMap ����Խ����� ����, ��������,�±�, ���̺��� length�� ����ִ� ��
	 * @param photoOrderMap ����Խ����� �������, ���λ����� �����̸��� ����ִ� ��
	 * @param last_insert_id ���������� insert�� ������ auto increment PK�� ���� �����´�. (�� ���� sub Table�� FK���� ���� ����)
	 * */
	private String photoBoard_sub_insert(Map<String,String> stringParamMap, Map<String,Map<String,String>> photoOrderMap, int last_insert_id) {
		PreparedStatement preparedStatement=null;
		
		String query="";
		int photo_length = Integer.parseInt(stringParamMap.get("length").toString()); //���̺��� ����
		String result="����"; //DB INSERT �����
		
		for(int subParamCount=0; subParamCount<=photo_length; subParamCount++) {
			String subImg = photoOrderMap.get("image_"+subParamCount).get("serverUploadName"); //��������� ���ϸ�
			String subImgContent = stringParamMap.get("content_"+subParamCount); //��������� ����
			String thumbImageName = photoOrderMap.get("image_"+subParamCount).get("photoView_SubThumbImage"); //��������� ����� ���ϸ�
			query="INSERT INTO photo_board_sub (photo_boardNo,photo_ownNo,photo_sub,photo_content) values(?,?,?,?)";
			try {
				preparedStatement = conn.prepareStatement(query);
				preparedStatement.setInt(1,last_insert_id); //���� ���̺��� photoBoardNo
				preparedStatement.setInt(2, subParamCount); //�Խñ� ���� �����̹����� ����
				preparedStatement.setString(3, subImg);  //�����̹��� ���ϸ�
				preparedStatement.setString(4, subImgContent); //�����̹��� ����
				
				int insertResult=preparedStatement.executeUpdate(); //������� �������ش�. 1�̶�� ���� �� �ܴ� ����
				if(insertResult!=1) { //���̺� INSERT�� �����ߴٸ�
					System.out.println("*************subPhoto DB INSERT FAIL!*************");
					System.out.println("file Name:"+subImg+", length:"+subParamCount);
					System.out.println("**************************************************");
					result = "������ ����";
				}else { //���̺� INSERT�� �����ߴٸ�
					query = "SELECT LAST_INSERT_ID()"; //�����̹��� ����� ������ ���ؼ� �����̹��� ������ȣ�� �����´�.
					preparedStatement = conn.prepareStatement(query);
					ResultSet rs = preparedStatement.executeQuery();
					if(rs.next()) {
						int subImgNo = rs.getInt(1); 
						//�ش� �����̹����� ����� �̹��� ���ϸ��� DB�� �ִ´�.
						result=thumbImageInsert(stringParamMap, photoOrderMap, last_insert_id,subImgNo, "photoView.pro", "sub", thumbImageName);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				result="SQL ����";
			}
		}
		
		//�������� ����� �ڿ��� �ݾ��ش�.
		try {
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result; //�ϴ��� ���������� �Ϸ��ߴٴ� ǥ��..
	}
	
	/*����Խ����� �±׸� �±����̺� �Է��Ѵ�.
	 * @param stringParamMap ����Խ����� ����, ��������,�±�, ���̺��� length�� ����ִ� ��
	 * @param last_insert_id ���������� insert�� ������ auto increment PK�� ���� �����´�. (�� ���� sub Table�� FK���� ���� ����)
	 * */
	private String photoBoard_tag_insert(Map<String,String> stringParamMap, int last_insert_id) {
		/*DB ����*/
		String result="����"; //�����
		String query = "INSERT INTO photo_board_tags (photo_boardNo,photo_tag) VALUES (?,?)"; //tag�� tag����Ŭ�� �ִ� SQL��
		PreparedStatement ps = null;
		
		String tags = stringParamMap.get("tags"); //�ʿ��� ����ڰ� �Է��� �±װ��� �����´�.
		StringTokenizer st = new StringTokenizer(tags, ","); //���ڿ��� �ڸ��� tokenizer (�ڸ����ڿ�, ������)
		//��ū�� ���� ������ �ݺ��Ѵ�.
		for(int i=0; st.hasMoreTokens(); i++) {
			String tag = st.nextToken();
			try {
				ps=conn.prepareStatement(query);
				ps.setInt(1, last_insert_id);
				ps.setString(2, tag);
				
				int insertResult=ps.executeUpdate(); //������� �������ش�. 1�̶�� ���� �� �ܴ� ����
				if(insertResult!=1) { //���� ���̺� INSERT�� �����ߴٸ�
					System.out.println("*************TAG DB INSERT FAIL!*************");
					System.out.println("TAG Name:"+tag);
					System.out.println("**************************************************");
					result = "������ ����";
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		} //end of For
		
		//�������� ����� �ڿ��� �ݾ��ش�.
		try {
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/*
	 * ����� �̹��� ���ϸ��� DB�� �Է��Ѵ�.
	 * @param stringParamMap ����Խ����� ����, ��������,�±�, ���̺��� length�� ����ִ� ��
	 * @param photoOrderMap ����Խ����� �������, ���λ����� �����̸��� ����ִ� ��
	 * @param last_insert_id ���������� insert�� ������ auto increment PK�� ���� �����´�. (�Խñ� ������ȣ)
	 * @param subImgNo ���������� insert�� ������ auto increment PK�� ���� �����´�. (�����̹��� ������ȣ)
	 * @param photo_thumbUsage �� ������� ��𿡼� ���̴��� �˷��ִ� ����
	 * @param typeFlag �� ������� Ÿ�� (main���� sub����)
	 * @param thumbImageName �� ������̹����� �̸�
	 * */
	private String thumbImageInsert(Map<String,String> stringParamMap, Map<String,Map<String,String>> photoOrderMap
			, int last_insert_id,int subImgNo, String photo_thumbUsage, String typeFlag, String thumbImageName) {
		String result="����";
		PreparedStatement ps = null;
		String query = "INSERT INTO photo_board_thumb (photo_boardNo,photo_subNo,photo_thumbType,photo_thumbName,photo_thumbUsage) VALUES (?,?,?,?,?)";
		System.out.println("������ LastID:"+subImgNo);
		try {
			ps=conn.prepareStatement(query);
			ps.setInt(1, last_insert_id); //photo_boardNo �Է�
			ps.setInt(2, subImgNo); //photo_subNo �Է�
			if(typeFlag.equals("sub")) {
			//���� �̹��� ����� DB����
				ps.setString(3, typeFlag); //thumbType�Է� (main, sub)
			}else if(typeFlag.equals("main")) {
				//���� �̹��� ����� DB����
				ps.setString(3, typeFlag); //thumbType�Է� (main, sub)
			}
			ps.setString(4, thumbImageName); //thumbImageName�Է� (����� �̹��� ���ϸ�)
			ps.setString(5, photo_thumbUsage); //photo_thumbUsage �Է�(�̹����� ��� ��������������)
			int insertResult = ps.executeUpdate(); //����� ���� (1�̶�� ����, 0�̶�� ����)
			if(insertResult!=1) {
				result="������ ����";
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/*
	 * ������������ �ҷ��� �� DB���� mainPhoto ���ϸ��� �������� �޼ҵ� (���������������� mainPhoto�� Thumbnail�� ����Ѵ�.)
	 * @param offset ��𼭺��� ��������� ���ϴ� �Ķ����
	 * 
	 * */
	public Object photoBoard_mainPhotoThumbnail_select(int offset) {
		String query = "SELECT * FROM photo_board_main ORDER BY photo_boardNo DESC LIMIT 12 OFFSET ?";
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		Map<Integer,Object>photoMainMap = new HashMap<Integer,Object>(); //��������� �� ���θ�
		Map<String,Object>photoMainMap_sub = new HashMap<String,Object>(); //��������� �� �����
	
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setInt(1, offset);
			rs=preparedStatement.executeQuery();
			while(rs.next()) {
				//������� �ʿ� �ִ´�.
				photoMainMap_sub.put("photo_boardNo", rs.getInt(1));
				photoMainMap_sub.put("photo_subject", rs.getString(2));
				photoMainMap_sub.put("photo_main", rs.getString(3));
				photoMainMap_sub.put("photo_main_thumbnail", rs.getString(4));
				photoMainMap_sub.put("photo_a_line_review", rs.getString(5));
				photoMainMap_sub.put("photo_writer", rs.getString(6));
				photoMainMap_sub.put("photo_upload_date", rs.getString(7));
				photoMainMap.put(rs.getInt(1), photoMainMap_sub);
				photoMainMap_sub = new HashMap<String,Object>(); //�ʱ�ȭ
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
	 * ���� �� ������ �����͸� �������� �޼ҵ� (DTO�� �Ⱦ���.)
	 * @param photoBoardNo �Խñ� ������ȣ�� �޾Ƽ� �����´�.
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
			PhotoData data = new PhotoData(); //���� ������ DTO
			while(rs.next()) {
				data.setPhoto_boardNo(rs.getInt(1)); //�Խñ� ������ȣ
				data.setPhoto_subject(rs.getString(2));//�Խñ� ����
				data.setPhoto_main(rs.getString(3));//�Խñ� �����̹��� ���ϸ�
				data.setPhoto_a_line_review(rs.getString(4));//�Խñ� ������
				data.setPhoto_writer(rs.getString(5));//�Խñ� �ۼ���
				data.setPhoto_upload_date(rs.getString(6));//�Խñ� �ۼ���¥ �� �ð�
				
				PhotoBoard_sub sub = new PhotoBoard_sub(); //���� ������ (1�࿡ �ش��ϴ�)
				sub.setPhoto_subNo(rs.getInt(7)); //���� ������ ������ȣ
				sub.setPhoto_ownNo(rs.getInt(8)); //���� �̹��� �Խñ� ���� ��ȣ
				sub.setPhoto_sub(rs.getString(9));//���� �̹��� ���ϸ�
				sub.setPhoto_content(rs.getString(10)); //���� �̹��� ����
				data.addPhotoBoard_sub(sub); //�����̹��� List�� add
				
				PhotoBoard_thumb thumb = new PhotoBoard_thumb(); //�̹��� ����� ������(1�࿡ �ش��ϴ�)
				thumb.setPhoto_thumbNo(rs.getInt(11)); //����,���� �̹��� ����� ������ȣ
				thumb.setPhoto_thumbType(rs.getString(12)); //����� Ÿ��(main, sub)
				thumb.setPhoto_thumbName(rs.getString(13)); //����� �̹��� ���ϸ�
				data.addPhotoBoard_thumb(thumb); //����� List�� add
				
			}
			
			//�±״� ���� ������´�.
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
	 * ���� ���������� ������ �������� DB���� �����ϱ�
	 * @param photoSubNo : ������ ���������� DB������ȣ
	 * */
	public String deleteSubPhoto(int photoSubNo) {
		String result="����";
		PreparedStatement ps = null;
		String query = "DELETE FROM photo_board_sub WHERE photo_subNo=?";
		
		try {
			ps=conn.prepareStatement(query);
			ps.setInt(1, photoSubNo); //photo_boardNo �Է�
			int deleteResult = ps.executeUpdate(); //SQL ���� ���� ��, ����� row ���� int type ���� �����մϴ�.
			if(deleteResult==0) {
				result="DB����_deleteSubPhoto";
			}
		}catch (SQLException e) {
			e.printStackTrace();
			result="DB����_deleteSubPhoto";
			return result;
		}
		
		return result;
		
	}
	
	/*
	 * DB�� ���� ����������� ������ �ٲٴ� �޼ҵ�
	 * @param photo_ownNo : �ش� �Խñۿ����� ���������� ����
	 * @param photo_subNo : �ش� �Խñۿ����� �������� DB ������
	 * */
	public String updatePhotosOrder(int photo_ownNo,int photo_subNo) {
		String result="����";
		PreparedStatement ps = null;
		String query = "UPDATE photo_board_sub SET photo_ownNo=? WHERE photo_subNo=?;";
		
		try {
			ps=conn.prepareStatement(query);
			ps.setInt(1, photo_ownNo); //photo_boardNo �Է�
			ps.setInt(2, photo_subNo); //photo_boardNo �Է�
			int updateResult = ps.executeUpdate(); //SQL ���� ���� ��, ����� row ���� int type ���� �����մϴ�.
			if(updateResult==0) {
				result="DB����_updatePhotosOrder";
			}
		}catch (SQLException e) {
			e.printStackTrace();
			result="DB����_updatePhotosOrder";
			return result;
		}
		return result;
	}
	
	/*
	 * DB�� ���� �������� ������� �ٲٴ� �޼ҵ�
	 * @param photo_subNo:DB������
	 * @param contents:�ٲ� ����
	 * */
	public String updateSubPhotoContent(int photo_subNo, String contents) {
		String result="����";
		PreparedStatement ps = null;
		String query = "UPDATE photo_board_sub SET photo_content=? WHERE photo_subNo=?;";
		
		try {
			ps=conn.prepareStatement(query);
			ps.setString(1, contents); //photo_boardNo �Է�
			ps.setInt(2, photo_subNo); //photo_boardNo �Է�
			int updateResult = ps.executeUpdate(); //SQL ���� ���� ��, ����� row ���� int type ���� �����մϴ�.
			if(updateResult==0) {
				result="DB����_updateSubPhotoContent";
			}
		}catch (SQLException e) {
			e.printStackTrace();
			result="DB����_updateSubPhotoContent";
			return result;
		}
		
		return result;
	}
	
	/*
	 * DB�� ���� �������� ���ϸ��� �ٲٴ� �޼ҵ�
	 * @param photo_subNo:DB������
	 * @param fileName:�ٲ� ���ϸ�
	 * */
	public String updateSubPhotoFileName(int photo_subNo, String fileName) {
		String result="����";
		PreparedStatement ps = null;
		String query = "UPDATE photo_board_sub SET photo_sub=? WHERE photo_subNo=?;";
		
		try {
			ps=conn.prepareStatement(query);
			ps.setString(1, fileName); //photo_boardNo �Է�
			ps.setInt(2, photo_subNo); //photo_boardNo �Է�
			int updateResult = ps.executeUpdate(); //SQL ���� ���� ��, ����� row ���� int type ���� �����մϴ�.
			if(updateResult==0) {
				result="DB����_updateSubPhotoContent";
			}
		}catch (SQLException e) {
			e.printStackTrace();
			result="DB����_updateSubPhotoContent";
			return result;
		}
		
		return result;
	}
	
	/*
	 * DB�� ���� �������� ����ϸ��� �ٲٴ� �޼ҵ�
	 * @param photo_subNo:DB������
	 * @param thumbName:�ٲ� ����ϸ�
	 * */
	public String updateSubPhotoThumbName(int photo_subNo, String thumbName) {
		String result="����";
		PreparedStatement ps = null;
		String query = "UPDATE photo_board_sub SET photo_sub=? WHERE photo_subNo=?;";
		
		try {
			ps=conn.prepareStatement(query);
			ps.setString(1, thumbName); //photo_boardNo �Է�
			ps.setInt(2, photo_subNo); //photo_boardNo �Է�
			int updateResult = ps.executeUpdate(); //SQL ���� ���� ��, ����� row ���� int type ���� �����մϴ�.
			if(updateResult==0) {
				result="DB����_updateSubPhotoContent";
			}
		}catch (SQLException e) {
			e.printStackTrace();
			result="DB����_updateSubPhotoContent";
			return result;
		}
		
		return result;
	}
	
	/*DAO ����� ������ Conn�� �ݾ��ֱ� ���� �޼ҵ�*/
	public void closeConn() {
		try {
			conn.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
