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
		System.setProperty("jsse.enableSNIExtension", "false");
		Connect con = new Connect("https://lse-esl.ec.rc.gc.ca/itb-dgi/rms/displayLogin.action");
		con.setCharSet("iso-8859-1");
		con.createCustomSSLSocket("c:\\temp\\testKey.jks", "123456");
		String str = con.enableCookies(true).connectToUrlUsingGET().getResultAsStr();
		System.out.println(str);
//		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.omega.dce-eir.net", 8080));
//		con.authenticator("axb161", "1092563We");
//		con.setCharSet("utf-8");
//		System.out.println(con.pingAddress(1000));
	}
	

}
