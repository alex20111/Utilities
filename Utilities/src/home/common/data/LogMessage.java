package home.common.data;

//{'className':'className', 'errorMessage':'broken please fix', 'fullErrorStack': 'full error stack ex'}
public class LogMessage {
	
	private String level = "";
	private String className ="";
	private String errorMessage = "";
	private String fullErrorStack = "";
	
	private String logFileName = "";
	private String logFileOption = "";
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getFullErrorStack() {
		return fullErrorStack;
	}
	public void setFullErrorStack(String fullErrorStack) {
		this.fullErrorStack = fullErrorStack;
	}
	public String getLogFileName() {
		return logFileName;
	}
	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getLogFileOption() {
		return logFileOption;
	}
	public void setLogFileOption(String logFileOption) {
		this.logFileOption = logFileOption;
	}
	@Override
	public String toString() {
		return "LogMessage [level=" + level + ", className=" + className + ", errorMessage=" + errorMessage
				+ ", fullErrorStack=" + fullErrorStack + ", logFileName=" + logFileName + ", logFileOption="
				+ logFileOption + "]";
	}


	

}
