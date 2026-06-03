package com.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
	@Autowired
	private JavaMailSender mailSender;

	@Value("${app.mail.from:}")
	private String fromAddress;

	@Value("${spring.mail.username:}")
	private String mailUsername;

	@Value("${spring.mail.password:}")
	private String mailPassword;

	public boolean canSendPasswordResetEmail() {
		return hasText(mailUsername) && hasText(mailPassword);
	}

	public void sendPasswordResetEmail(String to, String name, String resetLink) {
		if (!canSendPasswordResetEmail()) {
			throw new RuntimeException("Mail username/password are not configured. Set MAIL_USERNAME and MAIL_PASSWORD.");
		}

		SimpleMailMessage message = new SimpleMailMessage();
		if (hasText(fromAddress)) {
			message.setFrom(fromAddress);
		}
		message.setTo(to);
		message.setSubject("Reset your Mercato password");
		message.setText(buildPasswordResetBody(name, resetLink));

		try {
			mailSender.send(message);
		} catch (MailException ex) {
			throw new RuntimeException("Could not send password reset email. Please check mail configuration.", ex);
		}
	}

	private String buildPasswordResetBody(String name, String resetLink) {
		String greetingName = hasText(name) ? name.trim() : "there";
		return "Hi " + greetingName + ",\n\n"
				+ "We received a request to reset your Mercato password.\n\n"
				+ "Open this link to set a new password:\n"
				+ resetLink + "\n\n"
				+ "This link expires in 30 minutes and can be used only once.\n\n"
				+ "If you did not request this, you can ignore this email.";
	}

	private boolean hasText(String value) {
		return value != null && !value.trim().isEmpty();
	}
}
