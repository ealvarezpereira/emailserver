package com.gbtec.emailsender.rest;

import com.gbtec.emailsender.dto.EmailDto;
import com.gbtec.emailsender.service.MockMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint called by email-manager via Feign.
 * Receives the email synchronously and delegates to MockMailService.
 */
@RestController
@RequestMapping("/emails")
public class EmailSenderRestController {

    private static final Logger log = LoggerFactory.getLogger(EmailSenderRestController.class);

    @Autowired
    private MockMailService mockMailService;

    @PostMapping("/send")
    public ResponseEntity<Void> send(@RequestBody EmailDto email) {
        log.info("[REST/Feign] Received email to send: emailId={}", email.getEmailId());
        mockMailService.send(email);
        return ResponseEntity.ok().build();
    }
}
