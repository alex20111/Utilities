package home.miniHttp;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.response.Response;

public interface HttpHandler{
	public Response handle(IHTTPSession session);
	public void setSession(Session session);
} 
