package home.email;

import com.sun.mail.smtp.SMTPTransport;
import java.security.Security;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;

/**
 *
 * @author axb161
 */
public class GoogleMail 
{
	/**
	 * Send email using GMail SMTP server.
	 *
	 * @param username GMail username
	 * @param password GMail password
	 * @param recipientEmail TO recipient
	 * @throws AddressException if the email address parse failed
	 * @throws MessagingException if the connection is dead or not in the connected state or if the message is not a MimeMessage
	 */
	public void SendSecure(final String username, final String password, EmailMessage mail, EmailType type) throws AddressException, MessagingException, SendFailedException 
	{    	
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

		// Get a Properties object
		Properties props = System.getProperties();
		props.setProperty("mail.smtps.host", "smtp.gmail.com");
		props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.setProperty("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.port", "465");
		props.setProperty("mail.smtp.socketFactory.port", "465");
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
		session.setDebug(true);
		mail.setSession(session);

		// -- Create a new message --
		final Message  msg = mail.getEmail(type);

		SMTPTransport t = (SMTPTransport)session.getTransport("smtps");

		t.connect("smtp.gmail.com", username, password);
		t.sendMessage(msg, msg.getAllRecipients());      
		t.close();
	}
	public void SendTLSSecure(final String username, final String password, EmailMessage mail, EmailType type) throws AddressException, MessagingException, SendFailedException{
		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true"); //TLS

		Session session = Session.getInstance(prop,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			mail.setSession(session);

//			Message message = new MimeMessage(session);
//			message.setFrom(new InternetAddress("from@gmail.com"));
//			message.setRecipients(
//					Message.RecipientType.TO,
//					InternetAddress.parse("to_username_a@gmail.com, to_username_b@yahoo.com")
//					);
//			message.setSubject("Testing Gmail TLS");
//			message.setText("Dear Mail Crawler,"
//					+ "\n\n Please do not spam my email!");

			Transport.send(mail.getEmail(EmailType.text));

			System.out.println("Done");

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}


