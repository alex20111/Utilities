package home.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TEst {

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		DBConnection con = new DBConnection("jdbc:mysql://192.168.1.110:3306/webservice","tempreader","012345", DbClass.H2);

		String q = "Select count(*) AS tot from temperature ";
		
		ResultSet rs = con.createSelectQuery(q).getSelectResultSet();
		
		while(rs.next()){
			System.out.println(rs.getInt("tot"));
		}
	}

}
