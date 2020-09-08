package home.websocket;

public class WebSocketException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WebSocketException() {
		super();
	}

	public WebSocketException(String message) {
		super(message);
	}

	public WebSocketException(String message, Exception e) {
		super(message, e);
	}

}
