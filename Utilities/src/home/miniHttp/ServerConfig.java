package home.miniHttp;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ServerConfig {

	private File rootDir = null;
	private String externalHtmlFolder = "/web";
	private Set<String> externalWebPages = new HashSet<String>();
	
	public File getWebPage(String webPageName){
		File file = null; 
		if (externalWebPages.contains(webPageName)){
			System.out.println("WEeb found");
			file = new File(rootDir + externalHtmlFolder + File.separator + webPageName);
			if (file.exists()){
				return file;
			}else{
				System.out.println("Error web page file not found");
				return null;
			}
		}
		return file;
	}
	public File getRootDir() {
		return rootDir;
	}
	public void setRootDir(File rootDir) {
		this.rootDir = rootDir;
	}
	public String getExternalHtmlFolder() {
		return externalHtmlFolder;
	}
	public void setExternalHtmlFolder(String externalHtmlFolder) {
		this.externalHtmlFolder = externalHtmlFolder;
	}
	public Set<String> getExternalWebPages() {
		return externalWebPages;
	}
	public void setExternalWebPages(Set<String> externalWebPages) {
		this.externalWebPages = externalWebPages;
	}
}
