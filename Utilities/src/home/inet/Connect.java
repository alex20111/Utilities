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
public class Connect
{
	private URL url;
	private boolean cookiesEnabled = false;
	
	private  HttpURLConnection conn;
	private SSLSocketFactory sslSocketFactory;
	private String charSet = "UTF-8";	

	public Connect(){}

	public Connect(String url) throws MalformedURLException
	{
		this.url = new URL(url);
	}	

	/**
	 * Read an url and return the result as a string. No caching is used
	 * 
	 * @return Content of the url in string.
	 * @throws IOException - If any errors
	 */
	public Connect connectToUrlUsingGET() throws IOException 
	{
		return connectToUrlUsingGET(null, null);
	}
	/**
	 * Read an url and return the result as a string. No caching is used
	 * 
	 * @return Content of the url in string.
	 * @throws IOException - If any errors
	 */
	public Connect connectToUrlUsingGET(Map<String, String> params) throws IOException 
	{
		return connectToUrlUsingGET(null, params);
	}
	/**
	 * Read an url and return the result as a string. No caching is used
	 * 
	 * @return Content of the url in string.
	 * @throws IOException - If any errors
	 */
	public Connect connectToUrlUsingGET(Proxy proxy) throws IOException 
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
	public Connect connectToUrlUsingGET(Proxy proxy, Map<String, String> params ) throws IOException 
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
		
		conn  = null;

		if (proxy != null){
			conn = (HttpURLConnection)url.openConnection(proxy);
		}
		else{
			conn = (HttpURLConnection)url.openConnection();
		}

		if (sslSocketFactory != null){		
			((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
		}	

		conn.setRequestMethod("GET");
		conn.setUseCaches(false);
		conn.setRequestProperty("Accept-Charset", this.charSet);

		conn.getResponseCode();

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
	public Connect connectToUrlUsingPOST(Map<String, String> paramNamesValues) throws MalformedURLException, IOException
	{
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
		
		return POST( queryUrl.toString(), ContentType.DEFAULT_POST,  null);
	}
	/*
	 * Let the user decide of the type. 
	 * 
	 */
	public Connect connectToUrlUsingPOST(String data, ContentType type) throws MalformedURLException, IOException
	{
		return POST(  data, type,  null);
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
	private Connect POST(String queryUrl ,ContentType type,  Proxy proxy) throws MalformedURLException, IOException
	{		
		if (this.url == null || this.url.toString().length() == 0)
		{
			throw new MalformedURLException("Url is empty, please use url(string) or toUrl(string) to create a new URL");
		}		

		if (this.charSet == null || this.charSet.length() == 0){
			this.charSet = "UTF-8";
		}
		
		conn = null;

		if (proxy != null)
		{
			conn = (HttpURLConnection)url.openConnection(proxy);
		}
		else
		{
			conn = (HttpURLConnection)url.openConnection();
		}

		if (sslSocketFactory != null){					
			((HttpsURLConnection)conn).setSSLSocketFactory(sslSocketFactory);
		}

		byte[] contentBytes = queryUrl.toString().getBytes(this.charSet);

		conn.setRequestMethod("POST");
		conn.setDoOutput(true); // Triggers POST.
		conn.setRequestProperty("Accept-Charset", this.charSet);
		conn.setRequestProperty("Content-Type", type.getTypeText() + ";charset=" + this.charSet);
		conn.setRequestProperty("Content-Length", String.valueOf(contentBytes.length));
		conn.setRequestProperty("User-Agent","Mozilla/5.0");

		try (OutputStream output = conn.getOutputStream()) { 
			output.write(contentBytes);
		}
		
		return this;
	}
	/**
	 * set a user and password for the proxy.
	 * @param userName
	 * @param password
	 */
	public Connect authenticator(final String userName, final String password)
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
	public Connect enableCookies(boolean enable)
	{
		if (enable){
			CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_ALL ) );
		}
		else{
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
		if ( conn == null )
		{
			throw new IOException("You need to start the connection by calling connectToUrlUsingPOST or connectToUrlUsingGET");
		}

		StringBuilder sb = new StringBuilder();

		InputStream is = conn.getInputStream();			

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
		if ( conn == null )
		{
			throw new IOException("You need to start the connection by calling connectToUrlUsingPOST or connectToUrlUsingGET");
		}

		return conn.getInputStream();
	}	
	/**
	 * initiate a connection with a specified url.
	 * @param url	- Url to call
	 * @return
	 * @throws MalformedURLException
	 */
	public Connect setUrl(String url) throws MalformedURLException
	{			
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

		connection = (HttpURLConnection) url.openConnection();

		if (sslSocketFactory != null){		
			((HttpsURLConnection)connection).setSSLSocketFactory(sslSocketFactory);
		}

		connection.setConnectTimeout(timeout);
		connection.setReadTimeout(timeout);
		connection.setRequestMethod("HEAD");
		int responseCode = connection.getResponseCode();
		return (200 <= responseCode && responseCode <= 399);
	}
	/**
	 * disconnect from the connection if active.
	 */
	public void disconnect(){
		conn.disconnect();
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
	public  Connect createCustomSSLSocket(String trustStoreLoc, String password) 
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
	public Connect setCharSet(String charSet){
		this.charSet = charSet;
		return this;
	}
	/**
	 * IF it's a secure connection , display it's certificate.
	 * @return
	 */
	public String displayHttpsCertificate(){
		if (conn != null){
			StringBuilder sb = new StringBuilder();
			if(conn!=null){

				try {
					sb.append("Response Code : " + conn.getResponseCode() + "\n");
					sb.append("Cipher Suite : " + ((HttpsURLConnection)conn).getCipherSuite() + " \n");
					sb.append("\n");

					Certificate[] certs = ((HttpsURLConnection)conn).getServerCertificates();
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
	/**
	 * Return the HttpURLConnection.
	 * @return
	 */
	public HttpURLConnection getHttpUrlConnection(){
		return conn;
	}
	/**
	 * Set the timeout (in milliseconds) for the connection before timing out.
	 * @param millis - Timeout for the connection.
	 * @return
	 */
	public Connect setTimeout(int millis){
		if (conn != null){
			conn.setConnectTimeout(millis);
		}
		
		return this;
	}
	/**
	 * Get the response code of the call. 
	 *  
	 * @return the response code of the call to the url or -1 if not connected.
	 * @throws IOException
	 */
	public int getResponseCode() throws IOException{
		if (conn != null){
			return conn.getResponseCode();
		}
		return -1;
	}
}