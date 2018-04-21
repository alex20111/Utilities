package home.miniHttp;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;


public class WebServer extends NanoHTTPD{ 
	private Map<String, HttpHandler> handlers = new HashMap<String, HttpHandler>();
	private ServerConfig serverConfig = new ServerConfig();
	
	public WebServer(int port) throws URISyntaxException {
		super(port);
		File fi = new File(WebServer.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		serverConfig.setRootDir(fi.getParentFile()); 
		System.out.println("rootDir: " + serverConfig.getRootDir());
	} 
	public void addHandler(String path ,HttpHandler handler) throws IOException{
		if ( !path.startsWith("/")){
			throw new IOException("Path needs to start with a / ");
		}
		if (handlers.containsKey(path)){
			throw new IOException("Handler already exist");
		} 
		handlers.put(path, handler);
	} 
	public void addFileFolder(String folder) throws IOException{
		if (handlers.containsKey(folder)){
			throw new IOException("folder already exist");
		} 
		handlers.put("/" + folder , new FolderHandler());
	}
	public void addExternalHtmlFolder(String folderName) throws IOException{
		if (folderName.trim().length() == 0){
			throw new IOException("Please enter a name");
		}
		if (folderName.trim().startsWith("/")){ 
			serverConfig.setExternalHtmlFolder(folderName + "/");
		}else{
			serverConfig.setExternalHtmlFolder("/" + folderName + "/");
		}
	}
	public void addExternalHtmlPage(String pageName) throws IOException{
		if (pageName.trim().length() == 0){
			throw new IOException("Please enter a name");
		}
		serverConfig.getExternalWebPages().add(pageName);
	}
	public void startServer() throws IOException{
		if (handlers.isEmpty()){
			throw new IOException("No handler defined, please define an Handler"); 
		} 

		start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
	}
	@Override
	public Response serve(IHTTPSession session) {
		Response resp = null;
		try{
			Method method = session.getMethod();
			String uri = session.getUri();
			System.out.println(method + "'" + uri + "' ");
			HttpHandler handler = handlers.get(uri);
			if (handler == null){
				//try to see if it's not a folder or file
				if (uri != null && uri.length() > 0){
					handler = handlers.get(uri.substring(0, uri.lastIndexOf("/")));
					if (handler == null){ 
						System.out.println("handlder null");
						return Response.newFixedLengthResponse("Error404, invalid request");
					} 
				}else{ 
					return Response.newFixedLengthResponse(Status.NOT_FOUND,NanoHTTPD.MIME_HTML, "Error404, invalid request"); 
				}
			}
			resp = handler.handle(session, serverConfig);
			if (resp == null){
				return Response.newFixedLengthResponse(Status.NOT_FOUND,NanoHTTPD.MIME_HTML, "Error 404,invalid request");
			} 
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return resp;
	}
	public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
		WebServer server = new WebServer(8080);
		server.addHandler("/", new GeneralHandler("Here is the root or any text you want to add"));
		server.addHandler("/arf", new GeneralHandler("no tagain"));
		server.addFileFolder("css");
		server.addExternalHtmlFolder("web"); // if using a folder for external html pages
		server.addExternalHtmlPage("main.txt"); //html page to load.
		server.startServer();
		System.out.println("Started");
	}
}
