package home.websocket;

import java.net.URI;
import java.net.URISyntaxException;


public class ClientExample {

	public static void main(String[] args) throws InterruptedException {
		 try {
             // open websocket
             final WebSocketClientEndPoint clientEndPoint = new WebSocketClientEndPoint(new URI("ws://localhost:8081/events/"), 15000);

             // add listener
             clientEndPoint.addMessageHandler(new WebSocketClientEndPoint.MessageHandler() {
                 public void handleMessage(String message) {
                     System.out.println(message);
                 }
             });

             // send message to websocket
             System.out.println("Sent 1");
             clientEndPoint.sendMessage("{'operation': 1,'userName':'ok_btccny_ticker'}");

//             // wait 5 seconds for messages from websocket
             Thread.sleep(1000);
             
             new Thread(new Runnable() {

				public void run() {
					  while (!Thread.currentThread().isInterrupted()) {
		            	    try {
		            	    	System.out.println("Sent 2");
		            	    	clientEndPoint.sendMessage("{'operation': 1,'userName':'ok_btccny_ticker'}");
		            	        Thread.sleep(10000);
		            	    } catch (InterruptedException ex) {
		            	        Thread.currentThread().interrupt();
		            	    }
		            	}
				}
            	 
             }).start();
             
             Thread.sleep(3000);
             System.out.println("Sent 3");
             clientEndPoint.sendMessage("{'operation': 1,'userName':'ok_btccny_ticker22'}");
           
             
             System.out.println("Out close --");

         } catch (URISyntaxException ex) {
             System.err.println("URISyntaxException exception: " + ex.getMessage());
         }

	}

}
