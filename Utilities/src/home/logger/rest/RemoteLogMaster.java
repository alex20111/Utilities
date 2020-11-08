package home.logger.rest;

import java.io.IOException;
import java.security.InvalidParameterException;

import home.common.data.LogMessage;
import home.misc.StackTrace;

public class RemoteLogMaster {

	private String className = "";
	private RemoteLogImpl server;
	
	private boolean loggerInitalized = false;
	
	protected RemoteLogMaster (Class<?> clazz)  {
		this.className = clazz.getName();
		this.server = RemoteLogImpl.getInstance();
		if (!server.isLoggerInitialized()) {
			try {
				server.logInit();
				this.loggerInitalized = true;
			} catch (InvalidParameterException | IOException e) {
				e.printStackTrace();
			}
		}
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

		if (loggerInitalized) {
			LogMessage msg = new LogMessage();
			msg.setLevel(RemoteLogLevel.DEBUG.name());
			msg.setClassName(this.className);
			msg.setErrorMessage(errorMessage);
			if (tr != null) {
				msg.setFullErrorStack(StackTrace.displayStackTrace(tr));
			}
			try {
				server.callServerToLog(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			System.err.println("not connected to log server or not initialized. cannot log.");
		}
	}
	public void info(String errorMessage) {
		info(errorMessage, null);
	}
	public void info(String errorMessage, Throwable tr) {

		if (loggerInitalized) {
			LogMessage msg = new LogMessage();
			msg.setLevel(RemoteLogLevel.INFO.name());
			msg.setClassName(this.className);
			msg.setErrorMessage(errorMessage);
			if (tr != null) {
				msg.setFullErrorStack(StackTrace.displayStackTrace(tr));
			}
			try {
				server.callServerToLog(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			System.err.println("not connected to log server or not initialized. cannot log.");
		}
	}
	public void error(String errorMessage) {
		error(errorMessage, null);
	}
	public void error(String errorMessage, Throwable tr) {

		if (loggerInitalized) {
			LogMessage msg = new LogMessage();
			msg.setLevel(RemoteLogLevel.ERROR.name());
			msg.setClassName(this.className);
			msg.setErrorMessage(errorMessage);
			if (tr != null) {
				msg.setFullErrorStack(StackTrace.displayStackTrace(tr));
			}
			try {
				server.callServerToLog(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			System.err.println("not connected to log server or not initialized. cannot log.");
		}
	}
	
	public String getClassName() {
		return this.className;
	}
	
	
}
