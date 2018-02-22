package home.email;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Class containing the e-mail message to be sent out.
 * 
 * @author
 */
public class EmailMessage 
{
	
	private int messageId		= -1;
	private List<String> from 	= new ArrayList<String>();
	private List<String> to 	= new ArrayList<String>();
	private List<String> cc 	= new ArrayList<String>();
	private List<String> bcc 	= new ArrayList<String>();
	private String messageBody 	= "";
	private String subject		= "";
	private Date sentDate 		= null;
	private Date receivedDate	= null;
	private Flags flags			= null;
	
	
	private EmailMessage childMessage = null; //if the e-mail contain a child message
	private String contentType	= "";

	private Session session;
	
	public EmailMessage(){}
	public EmailMessage(Session session){
		this.session = session;
	}
	
	
	/**
	 * Get the constructed e-mail and return javax.mail.Message.
	 * 
	 * @param session 
	 * 			- the session associated to the message
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public Message getEmail(EmailType type) throws MessagingException
	{
		if (session == null){
			throw new MessagingException("Session not set, please set the session using the setSession");
		}
		
		
		Message message = new MimeMessage(this.session);

		if (getFrom().size() == 0 )
		{
			throw new MessagingException("please enter a FROM for the e-mail");
		}
		
		message.setFrom(new InternetAddress(getFrom().get(0)));

		if (getTo().size() == 0)
		{
			throw new MessagingException("please enter a TO recipient for the e-mail");
		}

		//to address
		for(String toStr : getTo())
		{
			message.addRecipients(Message.RecipientType.TO,
					InternetAddress.parse(toStr));
		}

		if (getCc() != null)
		{
			//cc address
			for(String ccStr : getCc())
			{
				message.addRecipients(Message.RecipientType.CC,	InternetAddress.parse(ccStr));
			}
		}
		if (getBcc() != null)
		{
			//bcc address
			for(String bccStr : getBcc())
			{
				message.addRecipients(Message.RecipientType.BCC,	InternetAddress.parse(bccStr));
			}
		}
		if (getSubject() != null)
		{
			message.setSubject(getSubject());
		}

		if (getMessageBody() != null)
		{
			message.setContent(getMessageBody(), type.getContentType() + "; charset=utf-8");
		}
		if (getSentDate() != null){
			message.setSentDate(getSentDate());
		}
		else
		{
			message.setSentDate(new Date());
		}

		return message;
	}
	/**
	 * @return the to
	 */
	public List<String> getTo() {
		return to;
	}
	/**
	 * @param to the to to set
	 */
	public void setTo(List<String> to) {
		this.to = to;
	}
	/**
	 * @param To mail recipient for single . 
	 */
	public void setTo(String to) {  
		if(this.to != null){
			this.to.add(to);
		}else{
			this.to = new ArrayList<String>();
			this.to.add(to);
		}
	}
	/**
	 * @return the cc
	 */
	public List<String> getCc() {
		return cc;
	}
	/**
	 * @param cc the cc to set
	 */
	public void setCc(List<String> cc) {
		this.cc = cc;
	}
	public void setCc(String cc) {  
		if(this.cc != null){
			this.cc.add(cc);
		}else{
			this.cc = new ArrayList<String>();
			this.cc.add(cc);
		}
	}
	/**
	 * @return the messageBody
	 */
	public String getMessageBody() {
		return messageBody;
	}
	/**
	 * @param messageBody the messageBody to set
	 */
	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * @return the sentDate
	 */
	public Date getSentDate() {
		return sentDate;
	}
	/**
	 * @param sentDate the sentDate to set
	 */
	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}
	public List<String> getFrom() {
		return from;
	}
	public void setFrom(List<String> from) {
		this.from = from;
	}
	public void setFrom(String from) {
		if (this.from != null){
			this.from.add(from);
		}else{
			this.from = new ArrayList<String>();
			this.from.add(from);
		}
	}
	public Date getReceivedDate() {
		return receivedDate;
	}
	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}
	public Flags getFlags() {
		return flags;
	}
	public void setFlags(Flags flags) {
		this.flags = flags;
	}
	public EmailMessage getChildMessage() {
		return childMessage;
	}
	public void setChildMessage(EmailMessage childMessage) {
		this.childMessage = childMessage;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public int getMessageId() {
		return messageId;
	}
	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}
	
	public String getFromFormatted(){
		StringBuilder from = new StringBuilder();
		for(String str : getFrom()){
			if (from.length() != 0){
				from.append(" , ");
			}
			from.append(str);
		}
		return from.toString();
	}
	public String getToFormatted() {
		StringBuilder to = new StringBuilder();
		for(String str : getTo()){
			if (to.length() != 0){
				to.append(" , ");
			}
			to.append(str);
		}
		return to.toString();
		
	}
	public String getCCFormatted() {
		StringBuilder cc = new StringBuilder();
		for(String str : getCc()){
			if (cc.length() != 0){
				cc.append(" , ");
			}
			cc.append(str);
		}
		return cc.toString();
		
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public List<String> getBcc() {
		return bcc;
	}
	public void setBcc(List<String> bcc) {
		this.bcc = bcc;
	}
	public void setBcc(String bcc) {  
		if(this.bcc != null){
			this.bcc.add(bcc);
		}else{
			this.bcc = new ArrayList<String>();
			this.bcc.add(bcc);
		}
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmailMessage [messageId=");
		builder.append(messageId);
		builder.append(", from=");
		builder.append(from);
		builder.append(",\n to=");
		builder.append(to);
		builder.append(",\n  cc=");
		builder.append(cc);
		builder.append(",\n  messageBody=");
		builder.append(messageBody);
		builder.append(",\n  subject=");
		builder.append(subject);
		builder.append(",\n  sentDate=");
		builder.append(sentDate);
		builder.append(",\n  receivedDate=");
		builder.append(receivedDate);
		builder.append(",\n  flags=");
		builder.append(flags);
		builder.append(",\n  childMessage=");
		builder.append(childMessage);
		builder.append(",\n  contentType=");
		builder.append(contentType);
		builder.append("]");
		return builder.toString();
	}
}