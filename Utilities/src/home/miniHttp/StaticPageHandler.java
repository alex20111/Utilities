package home.miniHttp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class StaticPageHandler {

	private StaticPageHandler(){}
	
	public static String processPage(List<File> filePath) throws IOException{
		return processPage(filePath, null);
	}
	///This method need the path to the file and the values to be replaced. The values in the text file should be like this %-valuel-% %-value2-%. 7 A
	public static String processPage(List<File> fileList, Map<String, String> values) throws IOException{
		String webPage = "";
		
		if (fileList != null && fileList.size() > 0){

			File filePath = null;

			if (fileList.size() > 1){
				StringBuilder sb = new StringBuilder();
				for(File f : fileList){
					sb.append(concatFiles(f));
				}

				webPage = sb.toString();
			}else{
				filePath = fileList.get(0);
				webPage = concatFiles(filePath);
			}	

			if (webPage.length() > 0 ){			

				if (values != null && !values.isEmpty()){
					for(Map.Entry<String, String> value : values.entrySet()){
						webPage = webPage.replace("%-" + value.getKey() + "-%", value.getValue());
					}
				}			
			} 
		}else{
			webPage = "Error files not found for page name";
		}
		return webPage;
	} 
	
	private static String concatFiles(File filePath) throws IOException{
		byte[] encoded = Files.readAllBytes(Paths.get(filePath.toPath().toUri()));
		return  new String(encoded, "UTF-8");
	}	
	private static boolean pageAlreadyServed(){
		return true;
	}
}

