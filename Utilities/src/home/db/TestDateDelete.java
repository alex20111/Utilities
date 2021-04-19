package home.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;


public class TestDateDelete {

	public static final String DB_URL = "c:\\temp\\piModule";
	public static final String DB_USER = "piModuleUser";
	public static final String DB_PASS = "109256";


	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		System.out.println("Starting");

		LocalDateTime now = LocalDateTime.now();
		Timestamp timestamp = Timestamp.valueOf(now);

//		selectAll();

		current();

	}
	
	public static void current() throws ClassNotFoundException, SQLException {
		DBConnection con = null;

		try {
			con = getConnection();
			String query = "SELECT * FROM temperature where (recorded_date  = (SELECT MAX(recorded_date) from  temperature where recorder_name = 'BB') OR  recorded_date  = (SELECT MAX(recorded_date) from  temperature where recorder_name = 'AA') )";

			ResultSet rs = con.createSelectQuery(query)
			.getSelectResultSet();
			
			display(rs);

		}finally {
			if (con != null) {
				con.close();
			}

		}
//		SELECT ord_num, ord_amount, ord_date, agent_code
//		FROM orders
//		WHERE ord_date=(
//		SELECT MAX(ord_date) FROM orders WHERE agent_code='A002');
	}

	public static void cleanUp(Date cleanUpFrom) throws ClassNotFoundException, SQLException {
		DBConnection con = null;

		try {
			con = getConnection();
			String query = "DELETE FROM temperature where recorded_date <= :date";

			con.createSelectQuery(query)
			.setParameter("date", cleanUpFrom)
			.delete();

		}finally {
			if (con != null) {
				con.close();
			}

		}


	}

	public static void selectAll() throws ClassNotFoundException, SQLException {
		DBConnection con = null;

		try {
			con = getConnection();
			String query = "SELECT * FROM temperature ";

			ResultSet rs = con.createSelectQuery(query)
					.getSelectResultSet();

			display(rs);

		}finally {
			if (con != null) {
				con.close();
			}

		}


	}
	
	private static void display(ResultSet rs) throws SQLException {
		if (rs!=null) {
			while(rs.next()) {
				System.out.println("--------------- sTART ---------------");
				System.out.println("ID: " + rs.getInt("id"));
				System.out.println("TEmp: " + rs.getString("temp_c"));
				System.out.println("REc Date: " + rs.getTimestamp("recorded_date"));
				System.out.println("Rec name: " +  rs.getString("recorder_name"));
				System.out.println("Batt Level: " +  rs.getString("battery_level"));
				System.out.println("humidity: " +  rs.getString("humidity"));
			}
		}
	}

	private static DBConnection getConnection() throws ClassNotFoundException, SQLException{

		Database db = new Database("jdbc:h2:" +DB_URL,DB_USER, DB_PASS.toCharArray(), DbClass.H2);
		return new DBConnection(db);
	}

}
