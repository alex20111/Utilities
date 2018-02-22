package home.crypto;

import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

//import org.apache.commons.codec.binary.Base64;



public class CypherUtils
{

//    static Log log = LogFactory.getLog(CipherUtils.class);

//    private static byte[] key = {
//            0x74, 0x68, 0x69, 0x73, 0x49, 0x73, 0x41, 0x53, 0x65, 0x63, 0x72, 0x65, 0x74, 0x4b, 0x65, 0x79
//    };//"thisIsASecretKey";

    public static String encrypt(String strToEncrypt, byte[] key)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            final SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            final String encryptedString = "";//Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes()));
            return encryptedString;
        }
        catch (Exception e)
        {
           e.printStackTrace();
        }
        return null;

    }

    public static String decrypt(String strToDecrypt,  byte[] key)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            final SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            final String decryptedString = new String(cipher.doFinal(strToDecrypt.getBytes()));
            //new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt)));
            return decryptedString;
        }
        catch (Exception e)
        {
            e.printStackTrace();

        }
        return null;
    }


    public static void main(String args[])
    {
    	long start = new Date().getTime();
    	String keyyy = "keyy43299";

    	String text = "this is me jumping over the dotted line";
    	String encrypt = CypherUtils.encrypt(text, getKey(keyyy));
    	
    	System.out.println("encrypted: " + encrypt);
    	
    	String dec = CypherUtils.decrypt(encrypt, getKey(keyyy));
    	
    	System.out.println("decrypted: " + dec);   
    	long end = new Date().getTime();
    	
    	System.out.println("Time to execute: " + (end - start));

    }
    
    private static byte[] getKey(String key)
    {
    	String padding = "2wSaopjHyes28B5%";
    	
    	//if the key is lower that 16 char, addd more
    	if (key.length() < 16)
    	{
    		key = key + padding.substring(key.length());
    	}
    	
    	return key.getBytes();
    }
    
    
}
