package home.miniHttp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.NanoHTTPD.ResponseException;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

public abstract class HttpBase implements HttpHandler {
	private boolean parmsProcessed = false;
	
	private IHTTPSession httpSession;
	private Map<String, List<String>> params;	
	private Map<String, String> files = new HashMap<String, String>(); // holder for uploaded files
	private Session session;
	
	public HttpBase(){}
	
	public abstract void handleParameters( Map<String, List<String>> params);
	public abstract Response handleRequest();
	
	public void httpSession(IHTTPSession httpSession){
		this.httpSession = httpSession;
		this.parmsProcessed = false;
	}
	@Override
	public void setSession(Session session) {
		this.session = session;
	}
	public Session getSession() {
		if (session == null) {
			throw new IllegalAccessError("Session was not activated at the start");
		}
		return session;
	}
	public Response handle(IHTTPSession session) {
		this.httpSession(session);
		try {
			getParameters();
			handleParameters(params);
		} catch (IOException | ResponseException e) {
			e.printStackTrace();
		}
		
		return handleRequest();
	}
	public IHTTPSession getHttpSession(){
		return this.httpSession;
	}

	public List<File> getWebPageOnDisk(String pageName){

		List<File> files = new ArrayList<File>();

		File file = null; 

		if ( ServerConfig.pagesMap.get(pageName) != null){

			for(String f : ServerConfig.pagesMap.get(pageName)){		

				file = ServerConfig.getExternalWebPage(f);
				if (file != null && file.exists()){
					files.add(file);

				}else{
					files.clear();
					System.out.println("Not all files found , returning empty");
					break;
				}
			}	
		}
		return files;		
	}
	/**
	 * Process the parameters.
	 * @return a list of parameters from a post or a get
	 * @throws IOException
	 * @throws ResponseException
	 */
	public Map<String, List<String>> getParameters() throws IOException, ResponseException{
		
		if (!parmsProcessed){
			params = new HashMap<String, List<String>>();
			Method method = httpSession.getMethod();
			if (Method.PUT.equals(method) || Method.POST.equals(method)) {
				files = new HashMap<String, String>();
				httpSession.parseBody(files);
				// get the POST body;
				System.out.println("Files body: " + files);
				// or you can access the POST request's parameters
				params = httpSession.getParameters();

			}else if(Method.GET.equals(method)){
				params = httpSession.getParameters();
			}
			parmsProcessed = true;
		}		
		return params;
	}
	/**
	 * verify if any files exist.
	 * @return
	 */
	public boolean gotFiles(){
		boolean filesExist = false;
		
		for(Map.Entry<String, String> file : files.entrySet()){
			if (file.getValue() != null && file.getValue().length() > 0){
				filesExist = true;
				break;
			}
		}		
		return filesExist;
	}
	/**
	 * Return a map of string , file name and temp file.
	 * @return
	 */
	public Map<String, String> getFiles(){	
		return files;
	}
	
	public void saveFiles(String pathToDir) throws IOException, ResponseException{

		Map<String, List<String>> params = getParameters();

		if (params.size() > 0){
			
			System.out.println("Params : " + params);

			for(Map.Entry<String, String> file : getFiles().entrySet()){

				
				if (file.getValue() != null && file.getValue().length() > 0){
					String nameLong = params.get(file.getKey()).get(0);
//					System.out.println("namelong: " + nameLong);
					
					String osSepChar = (nameLong.contains("\\") ? "\\" : "/" );
					String realName = nameLong;
					if (nameLong.contains(osSepChar)) {
//						System.out.println("osSepChar: " + osSepChar);
						realName = nameLong.substring(nameLong.lastIndexOf(osSepChar), nameLong.length());
					}				
					
					

					Path tempFile = Paths.get(file.getValue());

//					System.out.println("File value: " + file.getValue() + " eist: " + Files.exists(tempFile));
					if (Files.exists(tempFile)){

						if ((pathToDir.lastIndexOf(osSepChar) + 1) != pathToDir.length()){
							//dir does not contain end separator char
							pathToDir += File.separatorChar;
						}
						
//						System.out.println("Path to dir: " + pathToDir);
						Path newFile = Paths.get(pathToDir  + realName);

//						System.out.println("File exist1 : " + Files.exists(newFile));
						Files.deleteIfExists(newFile);
						Files.copy(tempFile, newFile);
						
//						System.out.println("File exist: " + Files.exists(newFile));

					}else{
						throw new IOException("CAN'T FIND FILE");
					}
				}
			}
		}else{
			System.out.println("Got file but no params");
		}
	}
	public String generateSuccessMessage(String message){
		StringBuilder sb = new StringBuilder();
		
		sb.append("<div class=\"alert alert-success\" role=\"alert\"> ");
		sb.append(message);
		sb.append("</div>");
		return sb.toString();
	}
	public String generateErrorMessage(String message){
		StringBuilder sb = new StringBuilder();
		
		sb.append("<div class=\"alert alert-danger\" role=\"alert\"> ");
		sb.append(message);
		sb.append("</div>");
		return sb.toString();
	}
	
	public Response sendToLocationSameHost(String page) {
		Response res = Response.newFixedLengthResponse(Status.REDIRECT, NanoHTTPD.MIME_HTML, "");
		res.addHeader("Location", "http://" + getHttpSession().getRemoteHostName() + "/" + page);
		return res;
	}
	
}
