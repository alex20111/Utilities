package home.miniHttp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

public class FolderHandler implements HttpHandler{
	@Override
	public Response handle(IHTTPSession session, ServerConfig serverConfig) {
		
		File file = new File(serverConfig.getRootDir() + File.separator + session.getUri()); //path exists and its correct
		String mime = NanoHTTPD.getMimeTypeForFile(session.getUri());
		FileInputStream fis = null;
		System.out.println("File Handler file exist: " + file + "  " + file.exists());
		try{
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			
			System.err.println("No file found . File handler");
		}
		
		return Response.newFixedLengthResponse(Status.OK, mime, fis, (int) file.length());
	}
} 
