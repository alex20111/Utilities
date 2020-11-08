package home.logger.rest;

import java.io.IOException;
import java.security.InvalidParameterException;

import com.google.gson.Gson;

import home.common.data.LogMessage;
import home.common.data.LogOption;
import home.inet.Connect;
import home.inet.ContentType;

/**
 * http://localhost:8080/web/logger/initAppLog/pimod/FILE_FIXED_SIZE
 * http://localhost:8080/web/logger/log
 * @author ADMIN
 *
 */
public class RemoteLogImpl {

	private static RemoteLogImpl logger;
	private String logFileName = "defaultLog";
	private LogOption logOption = LogOption.FILE_FIXED_SIZE;
	private String serverUrl = "http://192.168.1.110:8081";
	private String logUrl    = "/web/logger/log";
	private String logInitUrl    = "/web/logger/initAppLog";
	
	private RemoteLogLevel  level = RemoteLogLevel.DEBUG;
	
	private boolean loggerInit = false;
	
	private Gson gson = new Gson();


	protected static RemoteLogImpl getInstance() {
		if (logger == null) {
			logger = new RemoteLogImpl();
		}
		
		return logger;
	}

	/**
	 * Initalise the logger 
	 * 
	 * @param clazz
	 * @return
	 * @throws IOException 
	 * @throws InvalidParameterException 
	 */
	public  static RemoteLogMaster getLogger(Class<?> clazz)  {
		return new RemoteLogMaster(clazz);
	}
	
	protected void logInit() throws IOException, InvalidParameterException {
		Connect con = new Connect(serverUrl+logInitUrl+"/" + logFileName + "/" + this.logOption.name());

		con.connectToUrlUsingGET();
		String result = con.getResultAsStr();

//		System.out.println("Result: " + result);
		if (result.contains("FileName")) {
			//get new filename
			String fileName = result.substring(result.indexOf(":") + 2, result.indexOf(",") - 1);
//			System.out.println("resultfile: " + fileName);
			this.logFileName = fileName;
			loggerInit = true;
		}else {
			throw new InvalidParameterException("Error while requesting filename. " + result);	
		}

		con.disconnect();		

	}

	protected synchronized void callServerToLog(LogMessage message) throws IOException {

		RemoteLogLevel lvl = RemoteLogLevel.valueOf(message.getLevel());


		if (lvl.getWeight() >= level.getWeight()) {
//			System.out.println("Sending");
			Connect con = new Connect(serverUrl+logUrl);

			//add file name
			message.setLogFileName(this.logFileName);
			message.setLogFileOption(this.logOption.name());

			String json = gson.toJson(message);
//			System.out.println("Encoded message Json: " + json);
			con.connectToUrlUsingPOST(json, ContentType.JSON);
			int code = con.getResponseCode();
			//		System.out.println("Response code: " + code);
			if (code != 200) {
				throw new IOException("Error sending log. Return code: " + code);
			}
			con.disconnect();
		}
	}

	/**
	 * Return the initialized file log name.
	 * @return
	 */
	protected String getInitFileName() {
		if (this.loggerInit) {
			return this.logFileName;
		}
		
		return null;
	}
	protected boolean isLoggerInitialized() {
		return this.loggerInit;
	}
	public static void setLogFileName(String fileName) {
		RemoteLogImpl.getInstance().logFileName = fileName; 
	}
	public static void setLogLevel(RemoteLogLevel lvl) {
		RemoteLogImpl.getInstance().level = lvl; 
	}
	public static void setLogOption(LogOption option) {
		RemoteLogImpl.getInstance().logOption = option; 
	}
}
