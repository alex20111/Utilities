package home.email;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.ParseException;
import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;

public class GetMail {

	private Session session = null;
	private Folder folder 	= null;
	private Store store		= null;
	private boolean debug 	= false;

	public GetMail connect(String host, String user , String password, Protocol protocol) throws MessagingException{	
		
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", protocol.getProtocol());
		props.setProperty("mail.smtp.ssl.enable", "true");
		// Get a Session object
		session = Session.getInstance(props, null);
		session.setDebug(debug);
		store = session.getStore(protocol.getProtocol());
		store.connect(host, user, password);
		return this;

	}

	public void close() throws MessagingException{
		if (folder.isOpen()){
			folder.close(false);
		}
		if (store != null && store.isConnected()){
			store.close();
		}
	}
	/**
	 * Open specified folder 
	 * @param fldNm - Folder name
	 * @return
	 * @throws MessagingException
	 */
	public GetMail openFolder(String fldNm) throws MessagingException{
		if (store == null || !store.isConnected())
		{
			throw new MessagingException("Please connect to account");
		}
		if (folder != null && folder.isOpen()){
			folder.close(false);
		}
		folder =  store.getFolder(fldNm);	
		folder.open(Folder.READ_ONLY);

		return this;
	}
	/**
	 * Get a list of folders from the account
	 * @return
	 * @throws MessagingException
	 */
	public List<MailFolder> getFolderList() throws MessagingException
	{
		List<MailFolder> mailFolders = new ArrayList<MailFolder>();
		if (store.isConnected()){

			javax.mail.Folder[] folders = store.getDefaultFolder().list("*");
			for (javax.mail.Folder folder : folders) {

				if ((folder.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0) {	
					MailFolder fld = new MailFolder(folder);
					mailFolders.add(fld);
					//System.out.println(folder.getFullName() + ": " + folder.getMessageCount());
				}
			}
		}else
		{
			throw new MessagingException("Please connect to account");
		}
		return mailFolders;
	}
	/**
	 * Get all messages except the content. (Basically subject, To, From.
	 * @throws Exception 
	 */
	public List<EmailMessage> getMessagesFromFolders() throws Exception{
		
		List<EmailMessage> mailMsg = new ArrayList<EmailMessage>();
		
		if (folder.isOpen()){
			Message[] msgs = folder.getMessages();

			// Use a suitable FetchProfile
			FetchProfile fp = new FetchProfile();
			fp.add(FetchProfile.Item.ENVELOPE);
			fp.add(FetchProfile.Item.FLAGS);
			fp.add("X-Mailer");
			folder.fetch(msgs, fp);

			for (int i = 0; i < msgs.length; i++) {
				mailMsg.add(dumpEnvelope(msgs[i]));			    
			}
		}else{
			throw new MessagingException("Folder not open, please call method openFolder first.");
		}
		
		return mailMsg;
	}
	public EmailMessage getMessage(int id) throws Exception{
		Message message = folder.getMessage(id);
		
		EmailMessage mailMessage = dumpEnvelope(message);
		dumpPart(message,mailMessage);
		
		return mailMessage;
	}
	public List<EmailMessage> checkForNewEmails() throws Exception
	{
		if (folder != null && !folder.isOpen())
		{
			throw new Exception("Please open folder 1st");
		}
		Flags seen = new Flags(Flags.Flag.SEEN);
	    FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
	 

	    Flags recent = new Flags(Flags.Flag.RECENT);
	    FlagTerm recentFlagTerm = new FlagTerm(recent, true);

		SearchTerm searchTerm = new AndTerm(unseenFlagTerm, recentFlagTerm);
		
		Message messages[] = folder.search(searchTerm);
		
		List<EmailMessage> msgs = new ArrayList<EmailMessage>();
		
		for(Message msg : messages){
			msgs.add(dumpEnvelope(msg));
		}
		return msgs;
	}

	private EmailMessage dumpEnvelope(Message m) throws Exception {

		EmailMessage mail = new EmailMessage();

		System.out.println("Message id: " + m.getHeader("Message-ID"));
		
		mail.setMessageId(m.getMessageNumber());

		List<String> from = new ArrayList<String>();
		Address[] a;
		// FROM 
		if ((a = m.getFrom()) != null) {

			for (int j = 0; j < a.length; j++){
				from.add(a[j].toString());
			}
		}

		List<String> to = new ArrayList<String>();
		// REPLY TO
		if ((a = m.getReplyTo()) != null) {				
			for (int j = 0; j < a.length; j++){
				to.add(a[j].toString());
			}
		}

		// TO
		if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
			for (int j = 0; j < a.length; j++) {
				to.add(a[j].toString());
				InternetAddress ia = (InternetAddress)a[j];
				if (ia.isGroup()) {
					InternetAddress[] aa = ia.getGroup(false);
					for (int k = 0; k < aa.length; k++){
						to.add(aa[k].toString());
					}
				}
			}
		}
		//CC
		List<String> cc = new ArrayList<String>();
		if ((a = m.getRecipients(Message.RecipientType.CC)) != null) {
			for (int j = 0; j < a.length; j++) {
				cc.add(a[j].toString());
				InternetAddress ia = (InternetAddress)a[j];
				if (ia.isGroup()) {
					InternetAddress[] aa = ia.getGroup(false);
					for (int k = 0; k < aa.length; k++){
						cc.add(aa[k].toString());
					}
				}
			}
		}

		mail.setFrom(from);
		mail.setTo(to);
		mail.setCc(cc);


		// SUBJECT
		mail.setSubject(m.getSubject());

		// DATE
		mail.setSentDate(m.getSentDate());
		//received date
		mail.setReceivedDate(m.getReceivedDate());


		// FLAGS
		mail.setFlags(m.getFlags());


		// X-MAILER
		//			String[] hdrs = m.getHeader("X-Mailer");
		//			if (hdrs != null)
		//			    pr("X-Mailer: " + hdrs[0]);
		//			else
		//			    pr("X-Mailer NOT available");
		//		    }
		return mail;
	}
	
	private void dumpPart(Part p, EmailMessage mail) throws Exception {

		String ct = p.getContentType();
		try {
			mail.setContentType((new ContentType(ct)).toString());
		} catch (ParseException pex) {
			mail.setContentType("BAD CONTENT-TYPE: " + ct);
		}
		
		System.out.println("Contain file --> ? " + p.getFileName() );
		/*
		 * Using isMimeType to determine the content type avoids
		 * fetching the actual content data until we need it.
		 */
		if (p.isMimeType("text/*")) {
			mail.setMessageBody((String)p.getContent());
		} else if (p.isMimeType("multipart/*")) {
			System.out.println("This is a Multipart");
			System.out.println("---------------------------");
			Multipart mp = (Multipart)p.getContent();

			int count = mp.getCount();
			for (int i = 0; i < count; i++){				
				Part part = mp.getBodyPart(i);
				
				if (part instanceof Message){	
					System.out.println("message of miultipart");
					EmailMessage ch = dumpEnvelope((Message)part);
					dumpPart(part, ch);
					mail.setChildMessage(ch);
				}else{
					if (part.getFileName() != null && part.getFileName().length() > 0){
						System.out.println("Mail multipart contain file.");
					}
					System.out.println("plain old multipart, do not include");
				}
			}
			mail.setMessageBody("This is a multi part message");

		} else if (p.isMimeType("message/rfc822")) {
			System.out.println("This is a Nested Message");
			System.out.println("---------------------------");

			//			    dumpPart((Part)p.getContent());
			mail.setMessageBody("This is a Nested Message");

		} else {
			/*
			 * If we actually want to see the data, and it's not a
			 * MIME type we know, fetch it and check its Java type.
			 */
			Object o = p.getContent();
			if (o instanceof String) {
				System.out.println("This is string an input stream");
				System.out.println("---------------------------");
				mail.setMessageBody((String)o);
			} else if (o instanceof InputStream) {
				System.out.println("This is just an input stream");
				System.out.println("---------------------------");
				InputStream is = (InputStream)o;				
				BufferedReader in = new BufferedReader(new InputStreamReader(is));


				StringBuilder result = new StringBuilder();
				String line = "";
				while( (line = in.readLine()) != null){
					result.append(line);
				}
	
				is.close();
			} else {

				System.out.println("This is an unknown type");
				System.out.println("---------------------------");
				mail.setMessageBody("Unknown type of message: \n" + o.toString());
			}
		}
	}

	public boolean isDebug() {
		return debug;
	}

	public GetMail setDebug(boolean debug) {
		this.debug = debug;
		return this;
	}
	public enum Protocol{
		IMAP("imap"), IMAPS("imaps"), POP3("pop3"), POP3S("pop3s"), SMTP("smtp"), SMTPS("smtps");	
		
		private String protocol = "";
		private Protocol(String prot){
			this.protocol = prot;
		}
		public String getProtocol(){
			return this.protocol;
		}
	}
}
