package home.miniHttp;


import java.util.List;
import java.util.Map;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;

public class GeneralHandler extends HttpBase implements HttpHandler{ 
	
	private String message = "";
	public GeneralHandler(String message) {
		this.message = message;
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