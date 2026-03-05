package com.gbtec.emailmanager.ws.core.feign;

import com.gbtec.emailmanager.model.core.dto.EmailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client to call the email-sender microservice directly via REST.
 * Used in parallel with RabbitMQ: Feign for synchronous confirmation,
 * RabbitMQ for asynchronous processing.
 */
@FeignClient(name = "email-sender", url = "${app.feign.sender-url}")
public interface EmailSenderClient {

    @PostMapping("/emails/send")
    void sendEmail(@RequestBody EmailDto email);
}
