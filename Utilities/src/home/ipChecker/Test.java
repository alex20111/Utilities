package home.ipChecker;

import java.io.IOException;


public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws LoggerException 
	 */
	public static void main(String[] args) throws IOException {		

//		CustomLogger.createStaticFileLogger("verifyIp", "externalIp.log", Level.INFO, Level.FATAL);.
		
		VerifyExternalIp ip = new VerifyExternalIp();
		ip.setHost("https://api.ipify.org/")
		.setInterval(0)
		.setProxyName("proxy.omega.dce-eir.net")
		.setProxyPort(8080)
		.setProxyAuthUser("axb161")
		.setProxyAuthPass("###");
		
		ip.addIpListener(new IpListener() {			
			@Override
			public void IpReceived(String event, String newIp ,boolean changed) {
				System.out.println("Ip Listener event : " + event + " Ip changed : " + changed);				
			}
		});
		
		ip.verifyIp();
	}

}
