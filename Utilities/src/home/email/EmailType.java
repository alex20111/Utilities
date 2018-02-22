package home.email;

public enum EmailType {

	html("text/html"), text("text/plain");
	
	private String contentType;
	
	private EmailType(String type){
		contentType = type;
	}
	
	public String getContentType(){
		return this.contentType;
	}
}
