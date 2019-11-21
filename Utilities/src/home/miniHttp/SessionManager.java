package home.miniHttp;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Random;
import org.nanohttpd.protocols.http.content.CookieHandler;
public class SessionManager {
	private static final File SESSION_DATA = new File("./.sessions");
	private static final Random RANDOM = new Random();
	private static final int TOKEN_SIZE = 24;
	private static final char[] HEX = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	private static final String TOKEN_COOKIE = "__SESSION_ID__";
	private static HashMap<String, Session> sessions = new HashMap<>();
	private static String genSessionToken(){
		StringBuilder sb = new StringBuilder(TOKEN_SIZE);
		for(int i = 0; i < TOKEN_SIZE; i++){
			sb.append(HEX[RANDOM.nextInt(HEX.length)]);
		}
		return sb.toString();
	}
	private static String newSessionToken(){
		String token;
		do{
			token = genSessionToken();
		}while(sessions.containsKey(token));
		return token;
	}
	public static synchronized Session findOrCreate(CookieHandler cookies){
		String token = cookies.read(TOKEN_COOKIE);
		if(token == null){
			token = newSessionToken();
			cookies.set(TOKEN_COOKIE, token, 1);
		}
		if(!sessions.containsKey(token)){
			sessions.put(token, new Session(token));
		}
		return sessions.get(token);
	}
	public static void destroy(String token, CookieHandler cookies){
		sessions.remove(token);
		cookies.delete(TOKEN_COOKIE);
	}
	@SuppressWarnings("unchecked")
	public static void load() throws Exception{
		if(!SESSION_DATA.exists()) return;
		FileInputStream input = new FileInputStream(SESSION_DATA);
		sessions = (HashMap<String, Session>) new ObjectInputStream(input).readObject();
		input.close();
	}
	public static void save() throws Exception{
		FileOutputStream output = new FileOutputStream(SESSION_DATA);
		new ObjectOutputStream(output).writeObject(sessions);
		output.close();
	}
}
