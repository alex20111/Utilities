package home.crypto;


import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream; 
import java.io.FileInputStream;
import java.io.FileOutputStream; 
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher; 
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;  

import org.apache.commons.codec.binary.Base64;

public class Encryptor
{  
	private static String algo = "AES/ECB/PKCS5Padding";
	private static String salt = "abyZyt6#7daa!4s&00(56A";
	private static boolean noLimitToKey = false;


	/**
	 * Encrypt a file
	 * 
	 * @param filePath 
	 * 			- File to encrypt
	 * @param fileDist
	 * 			- destination and name of file
	 * @param password
	 * 			- password
	 * @throws Exception
	 */
	public static void encryptFile(String filePath, String fileDist, byte[] password) throws Exception
	{	
		SecretKeySpec key = buildKey(null, password);
		
		//creating and initialising cipher and cipher streams 
		Cipher encrypt =  Cipher.getInstance(algo);

		encrypt.init(Cipher.ENCRYPT_MODE, key);   
		//opening streams 
		FileOutputStream fos = new FileOutputStream(fileDist); 
		File file = new File(filePath);
		try(FileInputStream fis =new FileInputStream(file)) 
		{ 			
			try(CipherOutputStream cout=new CipherOutputStream(fos, encrypt))
			{            
				copy(fis,cout, file.length(), true);     
			}      
		}    
	}  	
	
	/**
	 * Encrypt a list of objects to file.
	 * 
	 * @param objs 
	 * 			- List of objects
	 * @param fileDist
	 * 			- Destination and name of file
	 * @param password
	 * 			- password
	 * @throws Exception
	 */
	public static void encryptFromObjectList(List<Object> objs, String fileDist, char[] password) throws Exception
	{	
		SecretKeySpec key = buildKey(password, null);		
		
		//creating and initialising cipher and cipher streams 
		Cipher encrypt =  Cipher.getInstance(algo);

		encrypt.init(Cipher.ENCRYPT_MODE, key);
		
		//opening streams		
		FileOutputStream fos = new FileOutputStream(fileDist); 
		
		CipherOutputStream cout=new CipherOutputStream(fos, encrypt);
		
		ObjectOutputStream oin = new ObjectOutputStream (cout);
		
		for(Object obj : objs)
		{
			oin.writeObject(obj);
		}
		
		oin.close();
		cout.close();
		fos.close();
	}  

	/**
	 * Decrypt a encrypted file
	 * 
	 * @param encFile
	 * 			- File to decrypt
	 * @param fileDist
	 * 			- new decrypted file destination
	 * @param password
	 * @throws Exception
	 */
	public static void decryptFile(String encFile, String fileDist, byte[] password) throws Exception
	{ 
		SecretKeySpec key = buildKey(null,password);
		//creating and initialising cipher and cipher streams    
		Cipher decrypt =  Cipher.getInstance(algo);   
		decrypt.init(Cipher.DECRYPT_MODE, key); 
		//opening streams    
		File file = new File(encFile);
		
		FileInputStream fis = new FileInputStream(encFile);

		CipherInputStream cin=new CipherInputStream(fis, decrypt);

		FileOutputStream fos =new FileOutputStream(fileDist);

		copy(cin,fos, file.length(), false);
		
		//close output streams
		fos.close();
		cin.close();
		fis.close();
	} 	

	/**
	 * Decrypt a file containing a list of objects.
	 * 
	 * @param encFile
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static List<Object> decryptToObjectList(String encFile, char[] password) throws Exception
	{ 
		List<Object> objects = new ArrayList<Object>();
		
		SecretKeySpec key = buildKey(password, null);
		
		//creating and initialising cipher and cipher streams    
		Cipher decrypt =  Cipher.getInstance(algo);   
		decrypt.init(Cipher.DECRYPT_MODE, key); 
		//opening streams    
		FileInputStream fis = new FileInputStream(encFile);

		CipherInputStream cin=new CipherInputStream(fis, decrypt);

		ObjectInputStream oin = new ObjectInputStream (cin);

		boolean notEof = true;
		try
		{
			while(notEof)
			{
				Object obj = oin.readObject();
				objects.add(obj);
			}
		}
		catch(EOFException ex)
		{
			notEof = false;
		}
		finally
		{
			//close output streams
			oin.close();
			cin.close();
			fis.close();
		}
		return objects;
	} 	
	
	/**
	 * Encrypt a string 
	 * 
	 * @param str	
	 * 			- String to encrypt
	 * @param password
	 * 			- password for the string
	 * @return
	 * @throws Exception
	 */
	public static String encryptString(String str , char[] password) throws Exception
	{
		SecretKeySpec key = buildKey(password, null);		
		
		//creating and initialising cipher and cipher streams 
		Cipher encrypt =  Cipher.getInstance(algo);

		encrypt.init(Cipher.ENCRYPT_MODE, key);
		
		String encrypted = Base64.encodeBase64String(encrypt.doFinal(str.getBytes("UTF-8")));		
		
		return encrypted;
	}
	/**
	 * Decrypt a encrypted string
	 * 
	 * @param str
	 * 			- String to decrypt
	 * @param password
	 * 			- password for the string.
	 * @return
	 * @throws Exception
	 */
	public static String decryptString(String str , char[] password) throws Exception
	{
		SecretKeySpec key = buildKey(password, null);		
		
		//creating and initialising cipher and cipher streams 
		Cipher decrypt =  Cipher.getInstance(algo);

		decrypt.init(Cipher.DECRYPT_MODE, key);
		
		String decrypted = new String(decrypt.doFinal(Base64.decodeBase64(str))); //decrypt.doFinal(str.getBytes("UTF-8"));
		
		
		return decrypted;
	}
	
	
	private static void copy(InputStream is,OutputStream os, Long length, boolean enc) throws Exception
	{				
		byte buf[] = new byte[4096];  //4K buffer set   
		int read = 0;  
		while((read = is.read(buf)) != -1)//reading
		{
			os.write(buf,0,read); 	//writing		
		}
	}    
	
	
	/**
	 * It is used to build the key. Depending if the password is a char or byte , it will return a secret keu.
	 * @param charPass
	 * @param passwordByte
	 * @return
	 * @throws Exception
	 */
	private static SecretKeySpec buildKey(char[] charPass, byte[] passwordByte) throws Exception
	{
		byte key[] = null;

		if (charPass != null && charPass.length > 0)
		{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

			CharBuffer charBuffer = CharBuffer.wrap(charPass);
			ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
			byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
					byteBuffer.position(), byteBuffer.limit());
			Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
			Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data			
			outputStream.write(bytes );
			outputStream.write(salt.getBytes("UTF-8") );

			key = outputStream.toByteArray( );
		}
		else if (passwordByte != null && passwordByte.length > 0)
		{
			key = passwordByte;
		}

		if (!noLimitToKey)		
		{
			//if the key is limited to 128 bit.
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16); // use only first 128 bit
		}

		SecretKeySpec secretKeySpec = new SecretKeySpec(key, algo.split("/")[0]);

		return secretKeySpec; 
	}

	/**
	 * @param salt the salt to set
	 */
	public static void setSalt(String salt) {
		Encryptor.salt = salt;
	}

	/**
	 * @return the noLimit
	 */
	public static boolean isNoLimitToKey() {
		return noLimitToKey;
	}

	/**
	 * @param noLimit the noLimit to set
	 */
	public static void setNoLimitToKey(boolean noLimitToKey) {
		Encryptor.noLimitToKey = noLimitToKey;
	} 
} 
	