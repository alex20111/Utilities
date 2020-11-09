package home.logger.rest;

import java.io.IOException;
import java.security.InvalidParameterException;

import home.common.data.LogMessage;
import home.misc.StackTrace;

public class RemoteLogMaster {

	private String className = "";
	private RemoteLogImpl server;
	
//	private boolean loggerInitalized = false;
	
	protected RemoteLogMaster (Class<?> clazz)  {
		this.className = clazz.getName();
		this.server = RemoteLogImpl.getInstance();
	}
	
	@SuppressWarnings("unused")
	private RemoteLogMaster() {}	
	
	/**
	 * get the log file name that was initialized by the server.
	 * @return
	 */
	public String getInitializedLogFileName() {
		return this.server.getInitFileName();
	}	
	
	public void debug(String errorMessage)  {
		debug(errorMessage, null);
	}
	
	public void debug(String errorMessage, Throwable tr) {


		if (server.ready() || server.getServerStatus() == LogServerStatus.NOT_INITIALIZED || server.getServerStatus() == LogServerStatus.IN_PROCESS_OF_INIT) {
			LogMessage msg = new LogMessage();
			msg.setLevel(RemoteLogLevel.DEBUG.name());
			msg.setClassName(this.className);
			msg.setErrorMessage(errorMessage);
			if (tr != null) {
				msg.setFullErrorStack(StackTrace.displayStackTrace(tr));
			}
			callServer(msg);
		}else {
			System.err.println("not connected to log server or not initialized. cannot log.");
		}
	}
	public void info(String errorMessage) {
		info(errorMessage, null);
	}
	public void info(String errorMessage, Throwable tr) {

		if (server.ready() || server.getServerStatus() == LogServerStatus.NOT_INITIALIZED || server.getServerStatus() == LogServerStatus.IN_PROCESS_OF_INIT) {
			LogMessage msg = new LogMessage();
			msg.setLevel(RemoteLogLevel.INFO.name());
			msg.setClassName(this.className);
			msg.setErrorMessage(errorMessage);
			if (tr != null) {
				msg.setFullErrorStack(StackTrace.displayStackTrace(tr));
			}
			callServer(msg);
		}else {
			System.err.println("not connected to log server or not initialized. cannot log.");
		}
	}
	public void error(String errorMessage) {
		error(errorMessage, null);
	}
	public void error(String errorMessage, Throwable tr) {	
		
		if (server.ready() || server.getServerStatus() == LogServerStatus.NOT_INITIALIZED || server.getServerStatus() == LogServerStatus.IN_PROCESS_OF_INIT) {
			LogMessage msg = new LogMessage();
			msg.setLevel(RemoteLogLevel.ERROR.name());
			msg.setClassName(this.className);
			msg.setErrorMessage(errorMessage);
			if (tr != null) {
				msg.setFullErrorStack(StackTrace.displayStackTrace(tr));
			}
			callServer(msg);
		}else {
			System.err.println("not connected to log server or not initialized. cannot log.");
		}
	}
	
	public String getClassName() {
		return this.className;
	}
	
	private synchronized void callServer(final LogMessage message) {
		
//		System.out.println("Started: " + message);
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				int cnt = 0;
				
				if (server.getServerStatus() == LogServerStatus.NOT_INITIALIZED ) {
					server.setServerStatus(LogServerStatus.IN_PROCESS_OF_INIT);
//					System.out.println("init server");
					server.logInit();
				}else if(server.getServerStatus() == LogServerStatus.IN_PROCESS_OF_INIT) {
					
					while(true){
//						System.out.println("Wait queue: " + cnt);
						//wait for 10 sec then crash
						cnt++;
						try {
							if (cnt > 10 || server.ready()) {
//								System.out.println("More then 10");
								Thread.currentThread().interrupt();
							}
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							break;
						}
					}
				}
				
				
				try { 
					if (cnt > 10) {
						Thread.currentThread().interrupt();
					}else {
					
					server.callServerToLog(message);}
				} catch (IOException e) {
					e.printStackTrace();
				}
//				System.out.println("Finished. " + message);
				
			}
			
		}).start();
		
	}
	
	
}
