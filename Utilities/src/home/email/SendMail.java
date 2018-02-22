package home.email;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class used to send e-mails.
 * @author 
 *
 */
public class SendMail
{
	
	/**
	 * Send Google e-mail
	 * 
	 * @param userName
	 * 			- The username for the gmail account
	 * @param pasword
	 * 			- thje password for the gmail account
	 * @param subject
	 * 			- The subject of the email.
	 * @param content
	 * 			- the text/html content of the emails
	 * @param to
	 * 			- send to 
	 * @param cc
	 * 			- send cc
	 * @throws Exception
	 */
	public static void sendGoogleMail(String from,String userName, String password, String subject, String content, String to, String cc, Date sentDate, EmailType type) throws Exception
	{
		List<String> tos = new ArrayList<String>();
		if (to != null && to.length() > 0)
		{
			tos.add(to);
		}
		List<String> ccs = new ArrayList<String>();
		if (cc != null && cc.length() > 0)
		{
			ccs.add(cc);
		}
		
		sendGoogleMail(from,userName,password,subject,content, tos, ccs, sentDate,type);
	}
	
	public static void sendGoogleMail(String from, String userName, String password, String subject, String content, List<String> to, List<String> cc, Date sentDate, EmailType type) throws Exception
	{
		EmailMessage mail = new EmailMessage();
		mail.setTo(to);
		mail.setCc(cc);
		mail.setSubject(subject);
		mail.setMessageBody(content);
		mail.setFrom(from);
		mail.setSentDate(sentDate);
		
		//send e-mail
		GoogleMail gMail = new GoogleMail();
		gMail.SendSecure(userName, password, mail,type);
	}
	
	public static void sendGoogleMail(String userName, String password, EmailMessage mail, EmailType type) throws Exception
	{	
		//send e-mail
		GoogleMail gMail = new GoogleMail();
		gMail.SendSecure(userName, password, mail, type);
	}
	public static void sendYahooMail(String from,String userName, String password, String subject, String content, String to, String cc, Date sentDate, EmailType type) throws Exception
	{
		List<String> tos = new ArrayList<String>();
		if (to != null && to.length() > 0)
		{
			tos.add(to);
		}
		List<String> ccs = new ArrayList<String>();
		if (cc != null && cc.length() > 0)
		{
			ccs.add(cc);
		}
		
		sendYahooMail(from,userName,password,subject,content, tos, ccs, sentDate, type);
	}
	
	public static void sendYahooMail(String from, String userName, String password, String subject, String content, List<String> to, List<String> cc, Date sentDate, EmailType type) throws Exception
	{
		EmailMessage mail = new EmailMessage();
		mail.setTo(to);
		mail.setCc(cc);
		mail.setSubject(subject);
		mail.setMessageBody(content);
		mail.setFrom(from);
		mail.setSentDate(sentDate);
		
		//send e-mail
		YahooMail yMail = new YahooMail();
		yMail.SendSecure(userName, password, mail, type);
	}
	
	public static void sendYahooMail(String userName, String password, EmailMessage mail, EmailType type) throws Exception
	{	
		//send e-mail
		YahooMail yMail = new YahooMail();
		yMail.SendSecure(userName, password, mail, type);
	}
	
	public static void sendOtherProviderMail(String from,String userName,String host, String port, String password, 
												String subject, String content, String to, String cc, 
												Date sentDate, boolean secure, EmailType type) throws Exception
	{
		List<String> tos = new ArrayList<String>();
		if (to != null && to.length() > 0)
		{
			tos.add(to);
		}
		List<String> ccs = new ArrayList<String>();
		if (cc != null && cc.length() > 0)
		{
			ccs.add(cc);
		}
		
		sendOtherProviderMail(from,userName,password,subject,content, tos, ccs, sentDate, host, port, secure, type);
	}
	
	public static void sendOtherProviderMail(String from, String userName, String password, String subject, String content, 
											List<String> to, List<String> cc, Date sentDate,String host, String port, boolean secure, EmailType type) throws Exception
	{
		EmailMessage mail = new EmailMessage();
		mail.setTo(to);
		mail.setCc(cc);
		mail.setSubject(subject);
		mail.setMessageBody(content);
		mail.setFrom(from);
		mail.setSentDate(sentDate);
		
		//send e-mail
		OtherMail oMail = new OtherMail();
		if (secure){		
			oMail.SendSecure(userName, password, mail, host, port, type);
		}else{
			oMail.SendNoSSl(userName, password, mail, host, port, type);
		}
		
	}
	
	public static void sendOtherProviderMail(String userName, String password, EmailMessage mail, String host, String port, boolean secure, EmailType type) throws Exception
	{	
		//send e-mail
		OtherMail oMail = new OtherMail();
		if (secure){		
			oMail.SendSecure(userName, password, mail, host, port, type);
		}else{
			oMail.SendNoSSl(userName, password, mail, host, port, type);
		}
	}
	
	
}
