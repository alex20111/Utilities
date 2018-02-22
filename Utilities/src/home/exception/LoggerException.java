package home.exception;

public class LoggerException extends Exception {

	/**
	 * Version ID. Used for serialization/deserialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor. Sets a generic error message.
	 */
	public LoggerException()
	{
		super( "Error in creating CustomLogger" );
	}

	/**
	 * Constructor that accepts a error message to provide in the Exception.
	 * 
	 * @param msg
	 * 			Error message to set in the Exception
	 */
	public LoggerException(String msg)
	{
		super( msg );
	}
	/**
	 * Constructor that accepts a error message to provide in the Exception.
	 * 
	 * @param msg
	 * 			Error message to set in the Exception
	  * @param Exception
	 * 			The exception
	 */
	public LoggerException(String msg, Exception ex)
	{
		super( msg, ex );
	}

}
