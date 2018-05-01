package home.miniHttp;


import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;

public class GeneralHandler extends HttpBase implements HttpHandler{ 
	
	private String message = "";
	public GeneralHandler(String message) {
		this.message = message;
	}
	
	@Override
	public Response handle(IHTTPSession session) {
	 
		return Response.newFixedLengthResponse(message);
	} 
}