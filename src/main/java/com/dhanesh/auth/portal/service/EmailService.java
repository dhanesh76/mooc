package com.dhanesh.auth.portal.service;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Sends a plain text email containing the OTP or any other information.
     *
     * @param toEmail  Recipient's email address
     * @param subject  Subject line of the email (context-specific)
     * @param body     Email body text (e.g., OTP instructions or reset link)
     *
     * Note:
     * - Use meaningful subject and body depending on context:
     *     - Email verification
     *     - Password reset
     * - Enhance for templating (HTML) in future if needed
     */
    
    public void sendOtp(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
        } catch (MailException ex) {
            throw new RuntimeException("Failed to send email to " + toEmail, ex);
        }
    }
}
