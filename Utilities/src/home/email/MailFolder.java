package home.email;

import javax.mail.Folder;
import javax.mail.MessagingException;

public class MailFolder {

	private String folderName 	= "";
	private int totalNbrMsg		= 0;
	private int newMessage 		= 0;
	private boolean subscribed 	= false;
	
	public MailFolder(){}
	public MailFolder(Folder folder) throws MessagingException{
		this.folderName = folder.getFullName();
		this.totalNbrMsg = folder.getMessageCount();
		this.newMessage = folder.getNewMessageCount();
		this.subscribed = folder.isSubscribed();
	}
		
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public int getTotalNbrMsg() {
		return totalNbrMsg;
	}
	public void setTotalNbrMsg(int totalNbrMsg) {
		this.totalNbrMsg = totalNbrMsg;
	}
	public int getNewMessage() {
		return newMessage;
	}
	public void setNewMessage(int newMessage) {
		this.newMessage = newMessage;
	}
	public boolean isSubscribed() {
		return subscribed;
	}
	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MailFolder [folderName=");
		builder.append(folderName);
		builder.append(",\n totalNbrMsg=");
		builder.append(totalNbrMsg);
		builder.append(",\n newMessage=");
		builder.append(newMessage);
		builder.append(",\n subscribed=");
		builder.append(subscribed);
		builder.append("]");
		return builder.toString();
	}
	
	
}
