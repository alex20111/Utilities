package home.websocket;



import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

/**
 * ChatServer Client
 *
 * @author Jiji_Sasidharan
 */
@ClientEndpoint
public class WebSocketClientEndPoint {

    Session userSession = null;
    private MessageHandler messageHandler;
    private WebSocketContainer container ;
        
    private URI endpointURI;
    

    public WebSocketClientEndPoint(URI endpointURI, long containerTimeout) {
        try {
        	this.endpointURI = endpointURI;
        	 container = ContainerProvider.getWebSocketContainer();
            container.setDefaultMaxSessionIdleTimeout(containerTimeout);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("opening websocket");
        this.userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {

        this.userSession = null;
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    public void connect() throws WebSocketException {
        try {
        	this.userSession = container.connectToServer(this, endpointURI);
			
		} catch (DeploymentException | IOException e) {
			throw new WebSocketException("Error in connect", e);

		}

    }
    public void closeConnection() throws IOException {
    	this.userSession.close();
    }
    
    public boolean isConnectionAlive() {
    	return this.userSession != null;
    }
    /**
     * register message handler
     *
     * @param msgHandler
     */
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    public void sendPing() throws IllegalArgumentException, IOException {
    	this.userSession.getAsyncRemote().sendPing(ByteBuffer.wrap("ping message server".getBytes()));
    }
    /**
     * Message handler.
     *
     * @author Jiji_Sasidharan
     */
    public static interface MessageHandler {

        public void handleMessage(String message);
    }
}