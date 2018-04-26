package home.miniHttp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class StaticPageHandler {

	private StaticPageHandler(){}
	private static String servingPage = "";//page being served
	private static String servedPageName = ""; 
	
	public static String processPage(File filePath) throws IOException{
		return processPage(filePath, null);
	}
	///This method need the path to the file and the values to be replaced. The values in the text file should be like this %-valuel-% %-value2-%. 7 A
	public static String processPage(File filePath, Map<String, String> values) throws IOException{
		String webPage = "";
		if (filePath.exists() ){
			if (!servedPageName.equals(filePath.getName())){ 
				
				loadPageFromFile(filePath);
				servedPageName = filePath.getName();
				webPage = servingPage;
				
			}else{ 
				webPage = servingPage;
				System.out.println("no Load page");
			}
			if (values != null && !values.isEmpty()){
				for(Map.Entry<String, String> value : values.entrySet()){
					webPage = webPage.replace("%-" + value.getKey() + "-%", value.getValue());

				}
			}			
		} 
		return webPage;
	} 

	private static void loadPageFromFile(File filePath) throws IOException{
		byte[] encoded = Files.readAllBytes(Paths.get(filePath.toPath().toUri()));
		servingPage = new String(encoded, "UTF-8");
	}
}