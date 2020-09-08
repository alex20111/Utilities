package home.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class ClientExample {

	public static void main(String[] args) throws InterruptedException, WebSocketException, IOException {
		 try {
             // open websocket
             final WebSocketClientEndPoint clientEndPoint = new WebSocketClientEndPoint(new URI("ws://localhost:8081/events/"), 60000);

             // add listener
             clientEndPoint.addMessageHandler(new WebSocketClientEndPoint.MessageHandler() {
                 public void handleMessage(String message) {
                     System.out.println(message);
                 }
             });

             //connect
             clientEndPoint.connect();
             
             // send message to websocket
             System.out.println("Sent 1");
             clientEndPoint.sendMessage("{'operation': 1,'userName':'ok_btccny_ticker'}");

//             // wait 5 seconds for messages from websocket
             Thread.sleep(1000);
             
             new Thread(new Runnable() {

				public void run() {
					  while (!Thread.currentThread().isInterrupted()) {
		            	    try {
		            	    	if (clientEndPoint.isConnectionAlive()) {
		            	    	System.out.println("Sent 2");
		            	    	clientEndPoint.sendMessage("{'operation': 1,'userName':'ok_btccny_ticker'}");
		            	        Thread.sleep(10000);
		            	    	}else {
		            	    		System.out.println("re connecting");
		            	    		clientEndPoint.connect();
		            	    	}
		            	    } catch (InterruptedException | WebSocketException ex) {
		            	        Thread.currentThread().interrupt();
		            	        ex.printStackTrace();
		            	    }
		            	}
				}
            	 
             }).start();
             
             Thread.sleep(3000);
             System.out.println("Sent 3");
             clientEndPoint.sendMessage("{'operation': 1,'userName':'ok_btccny_ticker22'}");
           
             
             System.out.println("Out close -conn-");
             clientEndPoint.closeConnection();

         } catch (URISyntaxException ex) {
             System.err.println("URISyntaxException exception: " + ex.getMessage());
         }

	}

}
