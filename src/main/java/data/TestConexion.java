package data;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConexion {

	public static void main(String[] args) {


		DbConnector connector = DbConnector.getInstancia();

		Connection conn = null;
		
		try {
			conn = connector.getConn();
			if (conn != null && !conn.isClosed()) {
			} else {
			}
			
		} catch (SQLException e) {
			e.printStackTrace(); 
			
		} finally {
			if (conn != null) {
				connector.releaseConn();
			}
		}
		
	}
}