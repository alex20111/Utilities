package home.email;

public class TestSendMail {

	public static void main(String[] args) throws Exception {
		
		EmailMessage em = new EmailMessage();
		em.setSubject("Test trest 1");
		em.setMessageBody("This is a test of the voice communication system");
		em.setTo("alex.boudreault@gmail.com");
		em.setFrom("Alex@test.com");
		
		
		try{
//		SendMail.sendGoogleMail("alex.mailservice1@gmail.com", "90opklm,)", em, EmailType.text);
//		SendMail.sendYahooMail("boudale@yahoo.com", "lokia88dhryaFFer#", em,  EmailType.text);
	
			
			GoogleMail g = new GoogleMail();
			g.SendTLSSecure("alex.mailservice1@gmail.com", "90opklm,)", em, EmailType.text);
		}catch(javax.mail.AuthenticationFailedException e){
			e.printStackTrace();
			System.out.println("e");
		}
	}

}
