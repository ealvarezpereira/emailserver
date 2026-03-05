package com.gbtec.emailmanager.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.gbtec.emailmanager")
@EnableScheduling
@EnableFeignClients(basePackages = "com.gbtec.emailmanager")
public class EmailManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailManagerApplication.class, args);
    }
}
