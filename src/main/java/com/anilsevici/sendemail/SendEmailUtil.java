package com.anilsevici.sendemail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmailUtil {
	
	
	public static void generateAndSendEmail(String email, String content) {
		final String username = "kariyerwebobss@gmail.com";
		final String password = "avatar1.avatar";

		Properties props = new Properties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});

		try {
			Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress("kariyerwebobss@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(email));
			message.setSubject("Bilgilendirme");
			message.setText(content);

			Transport.send(message);

			System.out.println("Done");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		
		

	}
}
