package home.miniHttp;

import java.io.File;

public class HttpBase {
	private String externalPage = ""; //external page name for the handler
	
	public void setExternalPage(String page) {
		this.externalPage = page;
	}
	
	public String getExternalPage() {
		return this.externalPage;
	}
	
	public File getWebPage(){
		File file = null; 

			file = ServerConfig.getExternalWebPage(externalPage);
			if (file.exists()){
				return file;
			}else{
				System.out.println("Error web page file not found");
				return null;
			}
		
	}
	

}
