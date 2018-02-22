package home.inet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

//Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.omega.dce-eir.net", 8080));

/**
 * Connect to the Internet using the URL and HttpURLConnection/HttpsURLConnection.
 * The URL will determine if the connection is a SSL connection or a regular connection.
 * 
 * If a private certificate is used, it will need to be provided to the sslSocketFactory.
 * @author 
 *
 */
public class ConnectBk
{
	private URL url;
	private boolean cookiesEnabled = false;
	
	private  HttpURLConnection conn;
	private  HttpsURLConnection conns;
	private SSLSocketFactory sslSocketFactory;
	private String charSet = "UTF-8";
	
	private boolean secure = false; //Determine if the url connection is to HTTPS	

	public ConnectBk() 	{}

	public ConnectBk(String url) throws MalformedURLException
	{
		secure = secureConnection(url);
		this.url = new URL(url);
	}	

	/**
	 * Read an url and return the result as a string. No caching is used
	 * 
	 * @return Content of the url in string.
	 * @throws IOException - If any errors
	 */
	public ConnectBk connectToUrlUsingGET() throws IOException 
	{
		return connectToUrlUsingGET(null, null);
	}
	/**
	 * Read an url and return the result as a string. No caching is used
	 * 
	 * @return Content of the url in string.
	 * @throws IOException - If any errors
	 */
	public ConnectBk connectToUrlUsingGET(Map<String, String> params) throws IOException 
	{
		return connectToUrlUsingGET(null, params);
	}
	/**
	 * Read an url and return the result as a string. No caching is used
	 * 
	 * @return Content of the url in string.
	 * @throws IOException - If any errors
	 */
	public ConnectBk connectToUrlUsingGET(Proxy proxy) throws IOException 
	{
		return connectToUrlUsingGET(proxy, null);
	}
	/**
	 * Read an url and return the result as a string. No caching is used
	 * 
	 * @param proxy - If behind a proxy.
	 * @return Content of the url in string.
	 * @throws IOException - If any errors
	 */
	public ConnectBk connectToUrlUsingGET(Proxy proxy, Map<String, String> params ) throws IOException 
	{	
		if (this.url == null || this.url.toString().length() == 0)
		{
			throw new MalformedURLException("Url is empty, please use url(string) to create a new URL");
		}

		if (params != null && params.size() > 0){

			StringBuilder queryUrl = new StringBuilder();

			for(Map.Entry<String, String> param : params.entrySet())
			{
				if (queryUrl.length() != 0){
					queryUrl.append("&");
				}
				queryUrl.append(URLEncoder.encode(param.getKey(), this.charSet) + "=" + URLEncoder.encode(param.getValue(), this.charSet));
			}	
			
			String newURL = "";
			if (this.url.toString().contains("?")){ //if other params exist, just append
				newURL = this.url.toString() +  queryUrl.toString();
			}else{
				newURL = this.url.toString() + "?" + queryUrl.toString();				
			}			
			this.url = new URL(newURL);				
		}
		
		int response = 0;		
		
		conn  = null;
		conns  = null;

		if (secure){ //secure connection using https
			if (proxy != null){
				conns = (HttpsURLConnection)url.openConnection(proxy);
			}
			else{
				conns = (HttpsURLConnection)url.openConnection();
			}	
			
			if (sslSocketFactory != null){		
				conns.setSSLSocketFactory(sslSocketFactory);
			}
			
			conns.setRequestMethod("GET");
			conns.setUseCaches(false);
			conns.setRequestProperty("Accept-Charset", this.charSet);
	
			response = conns.getResponseCode();			
		}
		else{
			if (proxy != null){
				conn = (HttpURLConnection)url.openConnection(proxy);
			}
			else{
				conn = (HttpURLConnection)url.openConnection();
			}
			
			conn.setRequestMethod("GET");
			conn.setUseCaches(false);
			conn.setRequestProperty("Accept-Charset", this.charSet);

			response = conn.getResponseCode();
		}	

		if (response < 200  || response > 399)
		{
			throw new IOException("Response code not 200. Response code: " +  response );
		}
		
		return this;
	}

	/**
	 *  send post message with parameters. The default Charset is UTF-8
	 * @param paramNamesValues
	 * 			- Parameters in a map. example: paramNamesValues.put(param, value); http://www.bla.com
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public ConnectBk connectToUrlUsingPOST(Map<String, String> paramNamesValues) throws MalformedURLException, IOException
	{
		return connectToUrlUsingPOST( paramNamesValues,  null);
	}
	/**
	 * send post message with parameters.  The default Charset is UTF-8
	 * 
	 * @param paramNamesValues
	 * 			- Parameters in a map. example: paramNamesValues.put(param, value); http://www.bla.com
	 * @param Proxy
	 * 			- The proxy for the call
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public ConnectBk connectToUrlUsingPOST(Map<String, String> paramNamesValues, Proxy proxy) throws MalformedURLException, IOException
	{		
		if (this.url == null || this.url.toString().length() == 0)
		{
			throw new MalformedURLException("Url is empty, please use url(string) or toUrl(string) to create a new URL");
		}		

		if (this.charSet == null || this.charSet.length() == 0){
			this.charSet = "UTF-8";
		}
		
		StringBuilder queryUrl = new StringBuilder();

		if (paramNamesValues != null){
			for(Map.Entry<String, String> param : paramNamesValues.entrySet())
			{
				if (queryUrl.length() != 0){
					queryUrl.append("&");
				}
				queryUrl.append(URLEncoder.encode(param.getKey(), this.charSet) + "=" + URLEncoder.encode(param.getValue(), this.charSet));
			}	
		}
		conn = null;
		conns = null;

		if (secure){
			if (proxy != null)
			{
				conns = (HttpsURLConnection)url.openConnection(proxy);
			}
			else
			{
				conns = (HttpsURLConnection)url.openConnection();
			}

			if (sslSocketFactory != null){		
				conns.setSSLSocketFactory(sslSocketFactory);
			}

			byte[] contentBytes = queryUrl.toString().getBytes(this.charSet);

			conns.setRequestMethod("POST");
			conns.setDoOutput(true); // Triggers POST.
			conns.setRequestProperty("Accept-Charset", this.charSet);
			conns.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + this.charSet);
			conns.setRequestProperty("Content-Length", String.valueOf(contentBytes.length));
			conns.setRequestProperty("User-Agent","Mozilla/5.0");

			try (OutputStream output = conns.getOutputStream()) { 
				output.write(contentBytes);
			}
		}
		else{
			if (proxy != null)
			{
				conn = (HttpURLConnection)url.openConnection(proxy);
			}
			else
			{
				conn = (HttpURLConnection)url.openConnection();
			}

			byte[] contentBytes = queryUrl.toString().getBytes(this.charSet);

			conn.setRequestMethod("POST");
			conn.setDoOutput(true); // Triggers POST.
			conn.setRequestProperty("Accept-Charset", this.charSet);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + this.charSet);
			conn.setRequestProperty("Content-Length", String.valueOf(contentBytes.length));
			conn.setRequestProperty("User-Agent","Mozilla/5.0");

			try (OutputStream output = conn.getOutputStream()) { 
				output.write(contentBytes);
			}
		}

		return this;
	}
	/**
	 * set a user and password for the proxy.
	 * @param userName
	 * @param password
	 */
	public ConnectBk authenticator(final String userName, final String password)
	{
		Authenticator authenticator = new Authenticator() {

			public PasswordAuthentication getPasswordAuthentication() {
				return (new PasswordAuthentication(userName,
						password.toCharArray()));
			}
		};
		Authenticator.setDefault(authenticator);
		return this;
	}
	/**
	 * Enable cookies on the connection and all subsequent connections. 
	 * To turn off the cookies , just set enable to false.
	 * @param enable
	 */
	public ConnectBk enableCookies(boolean enable)
	{
		if (enable)
		{
			CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_ALL ) );
		}
		else
		{
			CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_NONE ) );
		}
		
		cookiesEnabled = enable;		
		
		return this;
	}
	/**
	 * return the cookie list from the cookie handler.
	 * 
	 * @return
	 */
	public List <HttpCookie> getCookies()
	{
		CookieManager m = (CookieManager)CookieHandler.getDefault();

		CookieStore cs = m.getCookieStore();

		return cs.getCookies();
	}
	/**
	 * Get the result of the connection as a String.
	 * 
	 * @return
	 * @throws IOException
	 */
	public String getResultAsStr() throws IOException
	{
		if ( (!secure && conn == null) || (secure && conns == null))
		{
			throw new IOException("You need to start the connection by calling connectToUrlUsingPOST or connectToUrlUsingGET");
		}

		StringBuilder sb = new StringBuilder();

		InputStream is = null;
		if (secure){
			is = conns.getInputStream();
		}
		else{
			is = conn.getInputStream();			
		}

		if (is != null)
		{
			BufferedReader br =  new BufferedReader(new InputStreamReader(is, this.charSet));
			String line = null;
			while ( (line = br.readLine() ) != null)
			{
				sb.append(line);
			}

			br.close();
		}

		return sb.toString();
	}
	/**
	 * Get the result of the connection as a InputStream.
	 * 
	 * @return
	 * @throws IOException
	 */
	public InputStream getResultAsInputStream() throws IOException
	{		
		if ( (!secure && conn == null) || (secure && conns == null) )
		{
			throw new IOException("You need to start the connection by calling connectToUrlUsingPOST or connectToUrlUsingGET");
		}

		if (secure){
			return conns.getInputStream();
		}
		else{
			return conn.getInputStream();
		}
	}
	
	/**
	 * initiate a connection with a specified url.
	 * @param url	- Url to call
	 * @return
	 * @throws MalformedURLException
	 */
	public ConnectBk setUrl(String url) throws MalformedURLException
	{			
		secure = secureConnection(url);
		this.url = new URL(url);
		return this; 
	}
	/**
	 * return true if the cookies are enabled for the connection.
	 * @return
	 */
	public boolean isCookiesEnabled(){
		return cookiesEnabled;
	}
	/**
	 * Ping address to see if it exist. (Active)
	 * @param timeout - Time to wait for the connection to resolve.
	 * @return true - Connection is active
	 * 		   false - Connection is not active.
	 * @throws IOException 
	 */
	public boolean pingAddress(int timeout) throws IOException{
		//		url = url.replaceFirst("^https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.

		if (this.url == null || this.url.toString().length() == 0)
		{
			throw new MalformedURLException("Url is empty, please use setUrl(string) to create a new URL");
		}		

		HttpURLConnection connection = null;
		HttpsURLConnection secureConn = null;

		if (secure){
			secureConn = (HttpsURLConnection) url.openConnection();

			if (sslSocketFactory != null){		
				secureConn.setSSLSocketFactory(sslSocketFactory);
			}				

			secureConn.setConnectTimeout(timeout);
			secureConn.setReadTimeout(timeout);
			secureConn.setRequestMethod("HEAD");
			int responseCode = secureConn.getResponseCode();
			return (200 <= responseCode && responseCode <= 399);
		}
		else{
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			connection.setRequestMethod("HEAD");
			int responseCode = connection.getResponseCode();
			return (200 <= responseCode && responseCode <= 399);
		}		
	}
	/**
	 * disconnect from the connection if active.
	 */
	public void disconnect(){
		if ( !secure && conn != null ){
			conn.disconnect();
		}else if (secure && conns != null ){
			conns.disconnect();
		}
	}
	/**
	 * -Provide a trustore to trust the website that is not valid in the java library (like google is in java trusted library and we don't need to define a custom SSLSocketFactory).	 * 
	 * - Basically download the certificate from the website that is not supported by Java and add it to your keystore. 
	 * 
	 * @param trustStoreLoc
	 * 			- The location for the trustStore, must be a trustStore with a valid certificate from the website.			
	 * @param password
	 * 			- Password for the truststore.
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws KeyManagementException
	 */
	public  ConnectBk createCustomSSLSocket(String trustStoreLoc, String password) 
			throws NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, KeyStoreException, KeyManagementException
			{
		if (trustStoreLoc.length() > 0 && password.length() > 0)
		{
			KeyStore keyStore = KeyStore.getInstance("JKS");
			//keyStore.load(new FileInputStream("c:\\jboss\\del\\trust.jks"),"123456".toCharArray());
			keyStore.load(new FileInputStream(trustStoreLoc),password.toCharArray());

			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(keyStore);
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, tmf.getTrustManagers(), null);

			sslSocketFactory = ctx.getSocketFactory();
		}
		else{
			sslSocketFactory = null;
		}	
		
		return this;
	}
	/**
	 * Set the charset for the connection
	 * @param charSet - Charset to use. Default UTF-8

	 */
	public ConnectBk setCharSet(String charSet){
		this.charSet = charSet;
		return this;
	}
	/**
	 * Verify if the connection is a secure connection
	 * @param url - The Url to test
	 * @return
	 */
	private boolean secureConnection(String url){		
		if (url != null && url.length() > 0 && url.startsWith("https")){
			return true;
		}
		return false;
	}
	/**
	 * IF it's a secure connection , display it's certificate.
	 * @return
	 */
	public String displayHttpsCertificate(){
		if (secure && conns != null){
			StringBuilder sb = new StringBuilder();
			if(conns!=null){

				try {

					sb.append("Response Code : " + conns.getResponseCode() + "\n");
					sb.append("Cipher Suite : " + conns.getCipherSuite() + " \n");
					sb.append("\n");

					Certificate[] certs = conns.getServerCertificates();
					for(Certificate cert : certs){
						sb.append("Cert Type : " + cert.getType() + "\n");
						sb.append("Cert Hash Code : " + cert.hashCode() + "\n");
						sb.append("Cert Public Key Algorithm : " 
								+ cert.getPublicKey().getAlgorithm() + "\n");
						sb.append("Cert Public Key Format : " 
								+ cert.getPublicKey().getFormat() + "\n");
						sb.append("\n");				
					}

				} catch (SSLPeerUnverifiedException e) {
					e.printStackTrace();
				} catch (IOException e){
					e.printStackTrace();
				}

			}
			return sb.toString();
		}
		return "";
	}
	
//	public void getUrlConnection(){
//		conn.
//	}
	
}