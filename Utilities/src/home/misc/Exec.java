package home.misc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

public class Exec {

	private CommandLine command;
	private DefaultExecutor executor;
	private ExecuteWatchdog watchdog;
	private boolean quoting = true;
	
	private ByteArrayOutputStream successResult;
	private String executeExceptionStr = "";
	private boolean watchDogTerminated = false;
	
	ExecutorService executorService;
//	private boolean runInBackground = false;
	
	
	public static void main (String args[]) throws ExecuteException, IOException{
		Exec exec = new Exec();
		exec.addCommand("cmd.exe").addCommand("/c").addCommand("dir").timeout(10);
		exec.run();
		
		System.out.println("Result: " + exec.getOutput()); 
		
	}
	public Exec runInBackground(boolean runBackground){  //TODO later
		
//		runInBackground = runBackground;
		
		if (runBackground)
		{
			executorService =  Executors.newSingleThreadExecutor();			
		}
		return this;
	}
	public Exec handleQuoting(boolean quoting) {
		this.quoting = quoting;
		return this;
	}
	
	public Exec addCommand(String cmd){
		if (command == null){
			command = new CommandLine(cmd);
		}
		else
		{
			command.addArgument(cmd, quoting);
		}
		
		return this;
	}
	
	public Exec timeout(int millis){
		if (watchdog == null){
			watchdog = new ExecuteWatchdog(millis); 
		}	
		return this;
	}
	
	public int run() throws ExecuteException, IOException{

		int exitValue = -1;
		watchDogTerminated = false;

		executor = new DefaultExecutor();

		if (watchdog != null){
			executor.setWatchdog(watchdog);
		}

		successResult = new ByteArrayOutputStream();
		PumpStreamHandler p = new PumpStreamHandler(successResult);

		executor.setStreamHandler(p);
		try {
			exitValue =  executor.execute(command);
		} catch (ExecuteException e) {
			executeExceptionStr = executeExceptionStr +  " ExecuteException:: " +e.getMessage();
			exitValue =  e.getExitValue();
		}


		if(watchdog != null && watchdog.killedProcess()){
			executeExceptionStr += "killed by watchdog";
			watchDogTerminated = true;
		}
		return exitValue;

	}
	public String getOutput(){
		return  successResult.toString() + "   " + executeExceptionStr;
	}
	
	public boolean isWatchDogTerminated() {
		return watchDogTerminated;
	}
	
}

