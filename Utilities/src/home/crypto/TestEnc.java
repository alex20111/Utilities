package home.crypto;

public class TestEnc {

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
//			String strToEnc = "this is the hunter killing the fox that jumped over the fence";
//			Enc.initCyrpto("bobbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb".toCharArray(), "saltttttt".getBytes(), true);
//			String encString = Enc.encryptString(strToEnc);
//			System.out.println("1 " + encString);
//			String decString = Enc.decryptString(encString);
//			System.out.println("2 " + decString);
//		
//			SealedObject obj = Enc.encryptStringAsSealedObject(strToEnc);
//			System.out.println("Dec sealedObj : " + (String)Enc.decryptStringFromSealedObject(obj));
//			Enc.clear();
			
			char[] pass = {'a','l','l','o','a','l','l','o','a','l','l','o','a','l','l','o','a','l','l','o'};
			char[] pass2 = {'a','l','l','o','a','l','l','o','a','l','l','o','a','l','l','o','a','l','l','o'};		
			Encryptor.setSalt("daaaaaaaaaaa");
			Encryptor.setNoLimitToKey(false);
	
			String encStr = Encryptor.encryptString("String to encrypt AAAAAAAAAAAAAAAA", pass);			
			System.out.println("encStr "  + encStr);
			
	
			String decFile = Encryptor.decryptString(encStr,pass2);
			
			System.out.println("Dec " + decFile);
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
