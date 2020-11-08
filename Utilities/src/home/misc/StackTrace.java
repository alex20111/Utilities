package home.misc;

public class StackTrace {

	public static String displayStackTrace(Exception e){

		StringBuilder sb = new StringBuilder();		
		sb.append(e.getMessage() + "\n");
		for (StackTraceElement element : e.getStackTrace())
		{
			sb.append(element.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	public static String displayStackTrace(Throwable e){

		StringBuilder sb = new StringBuilder();		
		sb.append(e.getMessage() + "\n");
		for (StackTraceElement element : e.getStackTrace())
		{
			sb.append(element.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
