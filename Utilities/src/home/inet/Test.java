package home.inet;

import java.io.IOException;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws KeyStoreException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public static void main(String[] args) throws IOException, KeyManagementException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
		System.out.println("Started");
//		System.setProperty("jsse.enableSNIExtension", "false");
//		Connect con = new Connect("https://lse-esl.ec.rc.gc.ca/itb-dgi/rms/displayLogin.action");
//		con.setCharSet("iso-8859-1");
//		con.createCustomSSLSocket("c:\\temp\\testKey.jks", "123456");
//		String str = con.enableCookies(true).connectToUrlUsingGET().getResultAsStr();
//		System.out.println(str);
////		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.omega.dce-eir.net", 8080));
////		con.authenticator("axb161", "1092563We");
////		con.setCharSet("utf-8");
////		System.out.println(con.pingAddress(1000));
		
//		Connect con = new Connect("http://192.168.1.110:8081/web/logger/initAppLog/fileGgg/FILE_FIXED_SIZE");
//		con.connectToUrlUsingGET();
//		String result = con.getResultAsStr();
		
		
		Connect con = new Connect("http://192.168.1.110:8081/web/logger/log");
		String data = "{level: 'debug', className:'newAndaaaaaCool', errorMessage:'broken please fissx', fullErrorStack: 'full error stack ex', logFileName: 'fileGgg-2020-10-27-11-25-32.log'}";
		con.connectToUrlUsingPOST(data, ContentType.JSON);
		String result = con.getResultAsStr();
	System.out.println("Result: " + result);  //fileGgg-2020-10-27-11-25-32.log
	}
	

}
