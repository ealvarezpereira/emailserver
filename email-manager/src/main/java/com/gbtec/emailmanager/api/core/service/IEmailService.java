package com.gbtec.emailmanager.api.core.service;

import com.gbtec.emailmanager.model.core.dto.EmailDto;
import com.gbtec.emailmanager.model.core.dto.EmailRequestDto;

import java.util.List;

public interface IEmailService {

   /**
    * Insert one or more emails
    * 
    * @param request the email request
    * @return list of emails
    */
   List<EmailDto> insertAll(EmailRequestDto request);

   /**
    * Search emails using state filter
    * 
    * @param state the email state
    * @return list of emails
    */
   List<EmailDto> findAll(Integer state);

   /**
    * Search emails using id filter
    * 
    * @param id the email id
    * @return the email
    */
   EmailDto findById(Long id);

   /**
    * Update draft emails
    * 
    * @param id the email id
    * @param dto of the email
    * @return the email
    */
   EmailDto update(Long id, EmailDto dto);

   /**
    * Mark one or more emails as deleted
    *
    * @param ids of the emails
    */
   void deleteAll(List<Long> ids);

   /**
    * Send a draft email: changes its state to Sent and dispatches it via Feign + RabbitMQ.
    * Only emails in DRAFT state can be sent.
    *
    * @param id the email id
    * @return the updated email
    */
   EmailDto sendEmail(Long id);

   /**
    * Find all emails sent by a given user
    *
    * @param userId the user ID
    * @return list of emails
    */
   List<EmailDto> findByUserId(Integer userId);

   /**
    * Scheduled task: mark all emails from carl@gbtec.es as spam
    */
   void markCarlEmailsAsSpam();
}
