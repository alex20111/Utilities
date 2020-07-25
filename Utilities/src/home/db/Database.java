package home.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
public class Database {
	private String 
	url = "";
	private String 
	dbUser = "";
	private DbClass databaseType;
	private Connection conn;
	private char[] filePassword;
	private char[] password;
	private String passwordStr;
	public Database(String url,String dbUser, char[] password, DbClass dbType) throws SQLException, ClassNotFoundException{
		this.url = url;
		this.databaseType = dbType;
		this.dbUser = dbUser;
		this.password = password;
	}
	public Database(String url,String dbUser, String password, DbClass dbType) throws SQLException, ClassNotFoundException{
		this.url = url;
		this.databaseType = dbType;
		this.dbUser = dbUser;
		this.passwordStr = password;
		this.password = password.toCharArray();
	}
	public Connection build( ) throws ClassNotFoundException, SQLException{
		Class.forName(databaseType.getDriver());
		
		if (this.databaseType == DbClass.H2) {
			return buildH2DB();
		}else if (this.databaseType == DbClass.Mysql || this.databaseType == DbClass.MYSQL_OLD) {
			return buildMySqlDb();
		}
		
			 
		return null;
	}
	/**
	 * This is the password used to encrypt the database. 
	 * 
	 * @param filePassword - char of the password
	 * @return
	 * @throws IllegalArgumentException
	 */
	public Database filePassword(char[] filePassword) throws IllegalArgumentException{
		if (url != null && !url.contains("CIPHER")){
			throw new IllegalArgumentException("The key work CYPHER is missing in the DB URL");
		}
		this.filePassword = filePassword;
		return this;
	}
	public Connection getConn() {
		return conn;
	}
	public DbClass getDbClass() {
		return this.databaseType;
	}
	private Connection buildH2DB() throws SQLException {
		char[] space = {' '};
		char result[];
		if (filePassword != null && filePassword.length > 0){
			result = new char[filePassword.length + space.length + password.length];
			System.arraycopy(filePassword, 0, result, 0, filePassword.length);
			System.arraycopy(space, 0, result, filePassword.length, space.length);
			System.arraycopy(password, 0, result, filePassword.length + space.length, password.length);
		}else{
			result = new char[password.length];
			System.arraycopy(password, 0, result, 0, password.length);
		}
		//
		
		Properties prop = new Properties();
		prop.setProperty("user", dbUser);
		prop.put("password", result);
		try {
			conn = DriverManager.getConnection(url, prop);
		} finally {
			Arrays.fill(password, (char) 0);
			Arrays.fill(result, (char) 0);
			if (filePassword != null){
				Arrays.fill(filePassword, (char) 0);
			}
		} 
		return conn;
	}
	
	private Connection buildMySqlDb() throws SQLException {
		return  DriverManager.getConnection(url, dbUser, passwordStr);
	}
	
}