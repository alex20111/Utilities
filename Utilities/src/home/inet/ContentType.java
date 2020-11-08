package home.inet;

public enum ContentType {

	DEFAULT_POST("application/x-www-form-urlencoded"), JSON("application/json");
	
	private String type = "";
	
	private ContentType(String type) {
		this.type = type;
	}
	
	public String getTypeText() {
		return this.type;
	}
}
