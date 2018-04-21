package home.miniHttp;


import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;

public class GeneralHandler implements HttpHandler{ 
	
	private String message = "";
	public GeneralHandler(String message) {
		this.message = message;
	}
	
	@Override
	public Response handle(IHTTPSession session, ServerConfig serverConfig) {
	 
		return Response.newFixedLengthResponse(message);
	} 
}