package home.miniHttp;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Common server configuration for all handler
 * @author alex
 *
 */

public class ServerConfig {
	
	private static File rootDir = null;
	private static String externalHtmlFolder = "";
	
	private ServerConfig() {}
	
	public static Map<String, List<String>> pagesMap;
	
	public static File getExternalWebPage(String webPageName){
		File file = null; 

			file = new File(rootDir + externalHtmlFolder + File.separator + webPageName);
			if (file.exists()){
				return file;
			}else{
				System.out.println("Error web page file not found");
				return null;
			}		
	}

	public static File getRootDir() {
		return rootDir;
	}

	public static void setRootDir(File rootDir) {
		ServerConfig.rootDir = rootDir;
	}

	public static String getExternalHtmlFolder() {
		return externalHtmlFolder;
	}

	public static void setExternalHtmlFolder(String externalHtmlFolder) {
		ServerConfig.externalHtmlFolder = externalHtmlFolder;
	}
}