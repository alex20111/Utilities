package home.misc;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Exec2 {

	private List<String> commands = new ArrayList<String>();
	private Process process;
	private String programOutput = "";
	
	public static void main(String[] args) throws InterruptedException, IOException {
		long start = new Date().getTime();
		

			Exec2 exec = new Exec2();
		exec.addCommand("CMD.exe").addCommand("/C").addCommand("dir");	

//			for(String s : args){
//				exec.addCommand(s);
//			}
		

			System.out.println("Result: " + exec.run());
		long end = new Date().getTime();
		
		System.out.println("Timer: " + (end-start));		
		
	}
	
	public Exec2 addCommand(String command){
		commands.add(command);
		return this;
	}
	
	public String run() throws IOException, InterruptedException{
		
		ProcessBuilder pb = new ProcessBuilder(commands);
		pb.redirectErrorStream( true );
		
		process = pb.start();
		
		InputStream is = process.getInputStream();
		
		try
		{
			StringBuilder sb = new StringBuilder();
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(is));
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + System.getProperty("line.separator"));
				}
			} finally {
				br.close();
			}

			programOutput = sb.toString();
		}
		catch ( IOException e )
		{
			throw new IllegalArgumentException( "IOException receiving data from child process." );
		}
		
		process.waitFor();
		
		process.getErrorStream().close();

		return programOutput;
	}
	
//	/**
//	 * thread to read output from child
//	 */
//	class Receiver implements Runnable
//	{
//		/**
//		 * stream to receive data from child
//		 */
//		private final InputStream is;
//	
//		Receiver( InputStream is )
//		{
//			this.is = is;
//		}
//
//		/**
//		 * method invoked when Receiver thread started.  Reads data from child and displays in on out.
//		 */
//		public void run()
//		{
//			try
//			{
//				StringBuilder sb = new StringBuilder();
//				BufferedReader br = null;
//				try {
//					br = new BufferedReader(new InputStreamReader(is));
//					String line = null;
//					while ((line = br.readLine()) != null) {
//						System.out.println("000: " + line);
//						sb.append(line + System.getProperty("line.separator"));
//					}
//				} finally {
//					br.close();
//				}
//
//				System.out.println("Sb to string: " + sb.toString());
//				programOutput = sb.toString();
//			}
//			catch ( IOException e )
//			{
//				throw new IllegalArgumentException( "IOException receiving data from child process." );
//			}
//		}
//	}
}

