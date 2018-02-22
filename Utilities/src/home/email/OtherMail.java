package home.email;

import java.security.Security;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.internet.AddressException;

import com.sun.mail.smtp.SMTPTransport;

public class OtherMail {

	public void SendSecure(final String username, final String password, EmailMessage mail, String host, String port, EmailType type) throws AddressException, MessagingException, SendFailedException 
    {    	
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        // Get a Properties object
        Properties props = System.getProperties();
        props.setProperty("mail.smtps.host", host);
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", port);
        props.setProperty("mail.smtp.socketFactory.port", port); //or 587
        props.setProperty("mail.smtps.auth", "true");

        /*
        If set to false, the QUIT command is sent and the connection is immediately closed. If set 
        to true (the default), causes the transport to wait for the response to the QUIT command.

        ref :   http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
                http://forum.java.sun.com/thread.jspa?threadID=5205249
                smtpsend.java - demo program from javamail
        */
        props.put("mail.smtps.quitwait", "false");

        Session session = Session.getInstance(props, null);
        
        mail.setSession(session);
        
        // -- Create a new message --
        final Message  msg = mail.getEmail(type);

        SMTPTransport t = (SMTPTransport)session.getTransport("smtps");

        t.connect(username, password);
        t.sendMessage(msg, msg.getAllRecipients());      
        t.close();
    }
	
	public void SendNoSSl(final String username, final String password, EmailMessage mail, String host, String port , EmailType type) throws AddressException, MessagingException, SendFailedException 
    {    	
		
		Properties properties = System.getProperties();
		
		 // Setup mail server
	      properties.setProperty("mail.smtp.host", host);
	      properties.setProperty("mail.smtp.port", port);
	      properties.setProperty("mail.smtp.auth", "true");


        /*
        If set to false, the QUIT command is sent and the connection is immediately closed. If set 
        to true (the default), causes the transport to wait for the response to the QUIT command.

        ref :   http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
                http://forum.java.sun.com/thread.jspa?threadID=5205249
                smtpsend.java - demo program from javamail
        */
  

        Session session = Session.getInstance(properties, null);
        
        mail.setSession(session);
        
        // -- Create a new message --
        final Message  msg = mail.getEmail(type);

        SMTPTransport t = (SMTPTransport)session.getTransport("smtp");

        t.connect(username, password);
        t.sendMessage(msg, msg.getAllRecipients());      
        t.close();
    }
	
	
}
