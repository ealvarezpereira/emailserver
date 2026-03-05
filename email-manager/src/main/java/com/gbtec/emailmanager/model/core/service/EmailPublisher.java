package com.gbtec.emailmanager.model.core.service;

import com.gbtec.emailmanager.api.core.constants.MailConstants;
import com.gbtec.emailmanager.ws.core.feign.EmailSenderClient;
import com.gbtec.emailmanager.model.core.dto.EmailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Responsible for dispatching emails to the email-sender microservice.
 *
 * Two channels are used in parallel:
 *   1. Feign (synchronous REST) – immediate confirmation that sender received the request.
 *   2. RabbitMQ (asynchronous)  – resilient delivery; sender processes from the queue.
 */
@Component
public class EmailPublisher {

    private static final Logger log = LoggerFactory.getLogger(EmailPublisher.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private EmailSenderClient emailSenderClient;

    public void publishSendEmail(EmailDto email) {
        sendViaFeign(email);
        sendViaRabbitMQ(email);
    }

    private void sendViaFeign(EmailDto email) {
        try {
            log.info("[Feign] Calling email-sender for emailId={}", email.getEmailId());
            emailSenderClient.sendEmail(email);
            log.info("[Feign] email-sender acknowledged emailId={}", email.getEmailId());
        } catch (Exception e) {
            // Feign failure is non-fatal: RabbitMQ will still process the email
            log.warn("[Feign] Could not reach email-sender for emailId={}: {}", email.getEmailId(), e.getMessage());
        }
    }

    private void sendViaRabbitMQ(EmailDto email) {
        log.info("[RabbitMQ] Publishing emailId={} to exchange '{}'", email.getEmailId(), MailConstants.EXCHANGE);
        rabbitTemplate.convertAndSend(
                MailConstants.EXCHANGE,
                MailConstants.ROUTING_KEY,
                email);
    }
}
