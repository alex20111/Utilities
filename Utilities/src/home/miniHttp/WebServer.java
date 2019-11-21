package home.miniHttp;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;
import org.xml.sax.SAXException;


public class WebServer extends NanoHTTPD{ 
	private Map<String, HttpHandler> handlers = new HashMap<String, HttpHandler>();
	private boolean sessionActive = false;
	
	public WebServer(int port) {
		super(port);
	}
	public WebServer(int port, File rootDir) throws URISyntaxException {
		super(port);
		ServerConfig.setRootDir(rootDir);
		System.out.println("rootDir: " + ServerConfig.getRootDir());

	} 	 
	public void addHandler(String path, HttpHandler handler) throws IOException {
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
			ServerConfig.setExternalHtmlFolder(folderName + "/");
		}else{
			ServerConfig.setExternalHtmlFolder("/" + folderName + "/");
		}
	}

	public void addRootDir(File rootDir) {
		
		ServerConfig.setRootDir(rootDir); 
		System.out.println("rootDir: " + ServerConfig.getRootDir());
	}
	public void startServer() throws IOException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ParserConfigurationException, SAXException{
		if (handlers.isEmpty()){
			throw new IOException("No handler defined, please define an Handler"); 
		} 

		if (ServerConfig.getExternalHtmlFolder() != null &&
				ServerConfig.getExternalHtmlFolder().length() > 0){
			//load external xml pages if needed load xml configuration
			ServerConfig.pagesMap = LoadHtmlPages.loadPagePartsFromXml(
					ServerConfig.getRootDir().getPath() + File.separatorChar + ServerConfig.getExternalHtmlFolder() + File.separatorChar);
		}

		start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
	}
	public void enableSessionManagement() {
		sessionActive = true;
	}
	@Override
	public synchronized Response serve(IHTTPSession httpSession) {
		Response resp = null;
		try{	
			String uri = httpSession.getUri();
			System.out.println( "URI: " + uri );
			HttpHandler handler = handlers.get(uri);		
			
			boolean serve = canServe(uri, handler);//determine if it serve a webpage or just return null
			if (serve) {
				
				Session session = null;
				
				if (sessionActive) {
					session = SessionManager.findOrCreate(httpSession.getCookies());
				}

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
				
				if (session != null && sessionActive) {
					handler.setSession(session);
				}

				resp = handler.handle(httpSession);
				if (resp == null){
					return Response.newFixedLengthResponse(Status.NOT_FOUND,NanoHTTPD.MIME_HTML, "Error 404,invalid request");
				} 
			}
		}catch(Exception ex){
			System.out.println("Errorrrorororor");
			ex.printStackTrace();
		}
		return resp;
	}
	private boolean canServe(String uri, HttpHandler handler) {
		if ("/favicon.ico".equals(uri) && handler == null ) {
			return false;
		}
		return true;
	}
}