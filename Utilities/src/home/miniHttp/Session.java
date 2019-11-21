package home.miniHttp;




//https://github.com/NanoHttpd/nanohttpd/issues/400
import java.io.Serializable;
import java.util.HashMap;
import org.nanohttpd.protocols.http.content.CookieHandler;

@SuppressWarnings("serial")
public class Session implements Serializable{
	private HashMap<String, Object> data = new HashMap<>();
	private final String sessionID;
	public Session(String sessionID){
		this.sessionID = sessionID;
	}
	public Object get(String key){
		return data.get(key);
	}
	public void set(String key, Object value){
		data.put(key, value);
	}
	public void destroy(CookieHandler cookies) {
		SessionManager.destroy(sessionID, cookies);
	}
}
