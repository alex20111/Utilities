package home.miniHttp;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;

public class BootstrapExample {

	//external html pages _
	public static String MAIN_PAGE = "main.txt"; 
	public static void main(String[] args) throws IOException, URISyntaxException {
		WebServer server = new WebServer(8080,new File( "/home/alex/git/Utilities/Utilities"));
		server.addHandler("/", new MainPage(), MAIN_PAGE);
		server.addFileFolder("css");
		server.addFileFolder("js"); 
		server.addExternalHtmlFolder("web"); //if the html pages are loaded externally. This define the root for html pages
		server.startServer();
		System.out.println("Started");
	}
}
class MainPage extends HttpBase implements HttpHandler{
	@Override 
	public Response handle(IHTTPSession session) {
		String webPage = "No";
		//create main page with values
		File webPageFile = getWebPage();
		
		if (webPageFile == null){
			Map<String, String> values = new HashMap<String, String>();
			values.put("valuel", "The super webserver");
			values.put("value2", "never");
			try {
				webPage =	StaticPageHandler.processPage(webPageFile, values);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			webPage = "Web page not found";
		}
		return Response.newFixedLengthResponse(webPage);
	}
} 
	
	
