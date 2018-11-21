package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.NamingException;

import util.DBConnection;

public class DaoTest {
	
	private Connection conn = null;
	
	//Connection Pool에서 connection을 가져온다.
	public DaoTest() throws NamingException, SQLException {
		conn=DBConnection.getConnection();
	}
	
	public void tagInsertTest() {
		String[] tags={"베트남,한국,북한,일본,미국,중국,싱가폴,러시아,사우디,인도,인도네시아"};
		PreparedStatement preparedStatement=null;
		String query="INSERT INTO tagTest1 (tag) values (?)";
		System.out.println("오는지");
		try {
			System.out.println("오는지2");
			for(int i=0; i<10000; i++) {
				System.out.println("오는지3");
				for(int index=0; index<tags.length; index++) {
					preparedStatement = conn.prepareStatement(query);
					preparedStatement.setString(1, tags[index]);
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
