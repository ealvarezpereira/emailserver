package com.gbtec.emailsender.service;

import com.gbtec.emailsender.dto.EmailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Mock implementation of email sending using JavaMailSender.
 *
 * In this test setup Spring Mail is configured with a no-op/mock SMTP server,
 * so no real email is sent. The service logs the full email as if it were real,
 * allowing end-to-end flow validation without an actual SMTP server.
 *
 * To switch to real sending, replace the spring.mail properties in application.yml
 * with valid SMTP credentials and remove the mock host.
 */
@Service
public class MockMailService {

    private static final Logger log = LoggerFactory.getLogger(MockMailService.class);

    private final JavaMailSender mailSender;

    public MockMailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void send(EmailDto email) {
        log.info("==============================================");
        log.info("[MockMailService] Simulating email delivery:");
        log.info("  EmailId : {}", email.getEmailId());
        log.info("  From    : {}", email.getEmailFrom());
        log.info("  To      : {}", email.getEmailTo());
        log.info("  CC      : {}", email.getEmailCC());
        log.info("  Subject : {}", email.getEmailSubject());
        log.info("  Body    : {}", email.getEmailBody());
        log.info("==============================================");

        try {
            SimpleMailMessage message = buildMessage(email);
            mailSender.send(message);
            log.info("[MockMailService] Email dispatched (mock) for emailId={}", email.getEmailId());
        } catch (Exception e) {
            // Expected in mock/test mode — SMTP host is not real
            log.warn("[MockMailService] SMTP not configured (mock mode) for emailId={}: {}", email.getEmailId(), e.getMessage());
        }
    }

    private SimpleMailMessage buildMessage(EmailDto email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email.getEmailFrom());

        if (email.getEmailTo() != null && !email.getEmailTo().isEmpty()) {
            String[] toAddresses = email.getEmailTo().stream()
                    .map(EmailDto.RecipientDto::getEmail)
                    .toArray(String[]::new);
            message.setTo(toAddresses);
        }

        if (email.getEmailCC() != null && !email.getEmailCC().isEmpty()) {
            String[] ccAddresses = email.getEmailCC().stream()
                    .map(EmailDto.RecipientDto::getEmail)
                    .toArray(String[]::new);
            message.setCc(ccAddresses);
        }

        message.setSubject(email.getEmailSubject() != null ? email.getEmailSubject() : "(no subject)");
        message.setText(email.getEmailBody() != null ? email.getEmailBody() : "");

        return message;
    }
}
