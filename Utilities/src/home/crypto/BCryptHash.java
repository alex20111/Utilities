package home.crypto;


public class BCryptHash {

	public static String hashString(String password){
		
		String salt = BCrypt.gensalt();
		String hashed = BCrypt.hashpw(password, salt);
		
		return hashed;
	}
	
	public static boolean verifyIfHashedStringMatch(String string, String hashedString){
		return BCrypt.checkpw(string, hashedString);
	}
	
	public static void main(String arg[]){
		System.out.println(BCryptHash.hashString("admin"));
	}
}
