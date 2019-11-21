package home.miniHttp;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;
import org.xml.sax.SAXException;

public class BootstrapExample {

	/**
	 * Content layout file. contentlayout.xml  if using addexternalhtmlfolder
	 * <base>
		<page name="dashboard">
			<page_part name="dashboard.html"/>
			<page_part name="footer.html"/>		
		</page>
		<page name="alarmView">
			<page_part name="dashboard.html"/>
			<page_part name="footer.html"/>		
		</page>
	  </base>
	  */
	
	//external html pages _
	public static void main(String[] args) throws IOException, URISyntaxException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ParserConfigurationException, SAXException {
		WebServer server = new WebServer(8080,new File( "/home/alex/git/Utilities/Utilities"));
		server.addHandler("/", new MainPage());
		server.addFileFolder("css");
		server.addFileFolder("js"); 
		server.addExternalHtmlFolder("web"); //if the html pages are loaded externally. This define the root for html pages
		server.startServer();
		System.out.println("Started");
	}
}
class MainPage extends HttpBase implements HttpHandler{
	
	public static String MAIN_PAGE = "main.txt"; 
	
	@Override 
	public Response handle(IHTTPSession session) {
		String webPage = "No";
		//create main page with values
		List<File> webPageFile = getWebPageOnDisk(MAIN_PAGE);
		
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

	@Override
	public void handleParameters(Map<String, List<String>> params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Response handleRequest() {
		// TODO Auto-generated method stub
		return null;
	}
} 
	
	
