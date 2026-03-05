package com.gbtec.emailsender.messaging;

import com.gbtec.emailsender.dto.EmailDto;
import com.gbtec.emailsender.service.MockMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ consumer: receives emails from the queue and delegates to MockMailService.
 * This channel works in parallel with the Feign/REST channel.
 */
@Component
public class EmailConsumer {

    private static final Logger log = LoggerFactory.getLogger(EmailConsumer.class);

    @Autowired
    private MockMailService mockMailService;

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void receiveEmail(EmailDto email) {
        log.info("[RabbitMQ] Received email from queue: emailId={}", email.getEmailId());
        mockMailService.send(email);
    }
}
