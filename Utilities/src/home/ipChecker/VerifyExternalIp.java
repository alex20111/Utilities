package home.ipChecker;

import home.email.EmailMessage;
import home.email.EmailType;
import home.email.SendMail;
import home.exception.LoggerException;
import home.fileutils.FileUtils;
import home.inet.Connect;
import home.logger.CustomLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class VerifyExternalIp {

	Logger log = Logger.getLogger(VerifyExternalIp.class);

	private static String HOST 				= "hostsite";
	private static String INTERVAL 			= "interval";
	private static String PROXY 			= "proxy";
	private static String PROXY_PORT 		= "proxyport";
	private static String PROXY_AUTH_USER 	= "proxyauthuser";
	private static String PROXY_AUTH_PASS 	= "proxyauthpass";
	private static String EMAIL_ADDR 		= "emailaddr";
	private static String EMAIL_PASS 		= "emailpass";
	private static String EMAIL_SUBJ 		= "emailsubject";

	private static Connect con;
	private static String currentIp = "";

	//config info;
	public  String host 			= "https://api.ipify.org/";
	public  String proxyName 	= "";
	public  int proxyPort 		= 0;
	public  String proxyAuthUser = "";
	public  String proxyAuthPass = "";
	public  int interval  		= 0;
	public  String emailAddress  = "";
	public  String emailPass  	= "";
	public  String emailSubject  = "";
	private  Proxy proxy;

	private List<IpListener> listeners = new ArrayList<IpListener>();

	public VerifyExternalIp(){}
	public VerifyExternalIp(String host){
		this.host = host;
	}

	public void verifyIp() throws IOException, LoggerException{


		//create custom log file.
		log.info("\n ------------starting program----------- ");
		if (host == null || host.length() == 0){
			log.warn("No host defined , using host : " + host);
		}

		con = new Connect(host);			

		String result = "";

		//get ip
		if (proxyName != null && proxyName.length() > 0){
			proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyName, proxyPort));
			if (proxyAuthUser != null &&  proxyAuthUser.length() > 0){
				con.authenticator(proxyAuthUser, proxyAuthPass);
			}
			result = con.connectToUrlUsingGET(proxy).getResultAsStr();
		}else{
			result = con.connectToUrlUsingGET().getResultAsStr(); 			
		}

		if (result.length() > 0 ){
			currentIp = result;
			log.info("Current IP: " + currentIp);	

			if(interval > 0 ){
				//start loop
				final long delay = interval * 60 * 60 * 1000;
//				final long delay = interval * 1000;
				log.info("Starting interval with a delay of: " + delay + " in mill");	

				new Thread(new Runnable(){
					@Override
					public void run() {
						while(true){
							try {							
								Thread.sleep(delay);
								log.info("Woke up, Checking ip");

								String result = "";
								if (proxy != null){								
									result =  con.connectToUrlUsingGET(proxy).getResultAsStr();
								}else{
									result =  con.connectToUrlUsingGET().getResultAsStr();
								}

								if (!currentIp.equals(result)){
									log.info("Ip not the same as the original IP. Orig: " + currentIp + " new ip: " + result);
									String event = new Date() + "Ip address has changed from " + currentIp + " to " + result;									
									fireEvent(event,result,  true);
									//send e-mail if any
									if (emailAddress != null && emailAddress.length() > 0){
										sendEmail(result);
									}
									
									//store the new IP
									currentIp = result;
								}
							} catch (InterruptedException e) {}
							catch (IOException e) {
								log.error("IOException in thread. Continuing " , e);
							} catch (Exception e) {									
								log.error("Exception in thread. Continuing" , e);
							}
						}
					}
				}).start();
			}else{
				String event = "Your IP address is : " + result + "\nSince no interval is set, terminating program.\nBye Bye!!";
				fireEvent(event,result,  false);
			}

		}else{
			throw new IOException("No information returned from the host : " + host );
		}
	}

	private void displayProperties(Properties conf) throws IOException{

		StringBuilder sb = new StringBuilder();

		@SuppressWarnings("rawtypes")
		Enumeration keys = conf.keys();
		sb.append("Initial Configuration: " );
		while (keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			String value = (String)conf.get(key);

			sb.append("\n" + key + ": " + value );
		}
		System.out.println(sb.toString());
		log.info(sb.toString() );

	}
	private void sendEmail(String ip) throws Exception{

		EmailMessage email = new EmailMessage();
		if (emailSubject != null && emailSubject.length() > 0){
			email.setSubject(emailSubject);
		}else{
			email.setSubject("Notification - IP changed");
		}
		email.setMessageBody("Your external ip address has changed from : " + currentIp + " To : " + ip);
		email.setTo(emailAddress);
		email.setFrom("Your_Friendly_ip");
		SendMail.sendGoogleMail("alex.mailservice1@gmail.com", emailPass, email, EmailType.text);
	}
	/**
	 * Host to go and check the IP. It needs to return a text and not a html document.
	 * @param host
	 * @return
	 */
	public VerifyExternalIp setHost(String host) {
		this.host = host;
		return this;
	}
	public VerifyExternalIp setProxyName(String proxyName) {
		this.proxyName = proxyName;
		return this;
	}
	public VerifyExternalIp setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
		return this;
	}
	public VerifyExternalIp setProxyAuthUser(String proxyAuthUser) {
		this.proxyAuthUser = proxyAuthUser;
		return this;
	}
	public VerifyExternalIp setProxyAuthPass(String proxyAuthPass) {
		this.proxyAuthPass = proxyAuthPass;
		return this;
	}
	/**
	 * Set interval to check the IP against (in hours). Default 1 hour.
	 * If  interval of 0 or null is set, then the IP address will be returned right away.
	 * @param interval
	 * @return
	 */
	public VerifyExternalIp setInterval(int interval) {
		this.interval = interval;
		return this;
	}
	public VerifyExternalIp setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
		return this;
	}
	public VerifyExternalIp setEmailPass(String emailPass) {
		this.emailPass = emailPass;
		return this;
	}
	public VerifyExternalIp setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
		return this;
	}
	public synchronized void addIpListener( IpListener l ) {
		listeners.add( l );
	}
	public synchronized void removeIpListener( IpListener l ) {
		listeners.remove( l );
	}

	private synchronized void fireEvent(String event,String newIp, boolean changed) {

		Iterator<IpListener> _listeners = listeners.iterator();
		while( _listeners.hasNext() ) {
			( (IpListener) _listeners.next() ).IpReceived( event ,newIp, changed);
		}
	}	

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws LoggerException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, LoggerException {
		System.out.println("Starting....");
		System.out.println("All information will be through the externalIp.log");

		CustomLogger.createStaticFileLogger("externalIp", "externalIp.log", Level.INFO, Level.FATAL);

		File configFile = new File("ipConfig.cfg");

		Properties conf = new Properties();
		
		if (!configFile.exists()){
			System.out.println("Config file does not exist,creating a new one and exiting.\nPlease edit it and configure it.\nConfig file name: ipConfig.cfg" );
			StringBuilder sb = new StringBuilder();
			sb.append("##host site that you can connect to retrieve the textual ip address.\n");
			sb.append(HOST + "=https://api.ipify.org/ \n\n");
			sb.append("#check the ip every xx hours.Ex check the ip if valid every 12 hours.\n");
			sb.append(INTERVAL + "=1\n\n");
			sb.append("##Only configure this if you are behind a proxy.\n");
			sb.append(PROXY + "=\n");
			sb.append(PROXY_PORT + "=\n");
			sb.append(PROXY_AUTH_USER + "=\n");
			sb.append(PROXY_AUTH_PASS + "=\n\n");
			sb.append("##email info. If you want a warning to be e-mailed, enter info here. Google mail only\n");
			sb.append(EMAIL_ADDR + "=\n");
			sb.append(EMAIL_PASS + "=\n");
			sb.append(EMAIL_SUBJ + "=\n");
			FileUtils.writeStringToFile("ipConfig.cfg", sb.toString(), false);
			System.exit(0);
		}		
		conf.load(new FileInputStream(configFile));

		VerifyExternalIp ip = new VerifyExternalIp();		

		ip.host = conf.getProperty(HOST);		

		ip.proxyName 		= conf.getProperty(PROXY);
		ip.proxyPort	 	= (conf.getProperty(PROXY_PORT) != null ? Integer.parseInt(conf.getProperty(PROXY_PORT)):0 );
		ip.proxyAuthUser 	= conf.getProperty(PROXY_AUTH_USER);
		ip.proxyAuthPass 	= conf.getProperty(PROXY_AUTH_PASS);
		ip.emailAddress  	= conf.getProperty(EMAIL_ADDR);
		ip.emailPass  		= conf.getProperty(EMAIL_PASS);
		ip.emailSubject  	= conf.getProperty(EMAIL_SUBJ);

		try{
			ip.interval = Integer.parseInt(conf.getProperty(INTERVAL).trim());
		}catch(NumberFormatException nfx){ ip.interval = 0; nfx.printStackTrace();}

		ip.displayProperties( conf);

		ip.verifyIp();

	}
}
