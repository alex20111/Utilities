package home.email;

import java.util.List;


import home.email.GetMail.Protocol;

public class TestClass {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		GetMail getMail = new GetMail();
		List<MailFolder> folders = getMail.setDebug(true).connect("imap.google.com", "alex", "12345", Protocol.IMAPS).getFolderList();
		
		System.out.println("-----------------Folders------------");
		for(MailFolder mf : folders){
			System.out.println(mf);
		}
		System.out.println("------------------------------------\n");
		
		List<EmailMessage> messages = getMail.openFolder("inbox").getMessagesFromFolders();
		
		System.out.println("-----------------Messages------------");
		for(EmailMessage msg : messages){
			System.out.println(msg);
		}
		System.out.println("-------------------------------------");
		
		System.out.println("-----------------Message------------");
		EmailMessage msg = getMail.getMessage(2);
		System.out.println(msg);
		System.out.println("-------------------------------------");
		
		getMail.close();
		
	}

}
