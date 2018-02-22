package home.db;

public enum DbClass {
	H2("org.h2.Driver"), Mysql("com.mysql.jdbc.Driver");
	
	private String driver;
	
	private DbClass(String driver){
		this.driver = driver;
	}
	
	public String getDriver(){
		return this.driver;
	}
}
