package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.NamingException;

import util.DBConnection;

public class DaoTest {
	
	private Connection conn = null;
	
	//Connection Pool���� connection�� �����´�.
	public DaoTest() throws NamingException, SQLException {
		conn=DBConnection.getConnection();
	}
	
	public void tagInsertTest() {
		String[] tags={"��Ʈ��,�ѱ�,����,�Ϻ�,�̱�,�߱�,�̰���,���þ�,����,�ε�,�ε��׽þ�"};
		PreparedStatement preparedStatement=null;
		String query="INSERT INTO tagTest1 (tag) values (?)";
		System.out.println("������");
		try {
			System.out.println("������2");
			for(int i=0; i<10000; i++) {
				System.out.println("������3");
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
