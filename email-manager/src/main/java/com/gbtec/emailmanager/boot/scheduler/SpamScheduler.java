package com.gbtec.emailmanager.boot.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gbtec.emailmanager.api.core.service.IEmailService;

@Component
public class SpamScheduler {

   private static final Logger log = LoggerFactory.getLogger(SpamScheduler.class);

   @Autowired
   private IEmailService emailService;

   @Scheduled(cron = "0 0 10 * * *")
   public void markCarlEmailsAsSpam() {
      log.info("SpamScheduler: starting daily spam task");
      this.emailService.markCarlEmailsAsSpam();
      log.info("SpamScheduler: done");
   }
}
