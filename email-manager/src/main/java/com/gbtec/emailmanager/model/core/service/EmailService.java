package com.gbtec.emailmanager.model.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbtec.emailmanager.api.core.constants.MailConstants;
import com.gbtec.emailmanager.api.core.service.IEmailService;
import com.gbtec.emailmanager.model.core.dao.AbstractCrudDao;
import com.gbtec.emailmanager.model.core.dao.EmailDao;
import com.gbtec.emailmanager.model.core.dao.UserDao;
import com.gbtec.emailmanager.model.core.dto.EmailDto;
import com.gbtec.emailmanager.model.core.dto.EmailRequestDto;
import com.gbtec.emailmanager.model.core.dto.RecipientDto;
import com.gbtec.emailmanager.model.core.dto.SenderDto;

@Service
public class EmailService extends AbstractCrudService implements IEmailService {

   private static final Logger log = LoggerFactory.getLogger(EmailService.class);

   @Autowired
   private EmailDao emailDao;

   @Autowired
   private UserDao userDao;

   @Autowired
   private EmailPublisher emailPublisher;

   @Autowired
   private ObjectMapper objectMapper;

   @Override
   protected AbstractCrudDao getDao() {
      return this.emailDao;
   }

   @Override
   public List<EmailDto> insertAll(EmailRequestDto request) {
      List<EmailDto> result = new ArrayList<>();
      for (EmailDto dto : request.getEmails()) {
         this.validateEmailAddress(dto.getEmailFrom());
         if (dto.getEmailTo() != null) {
            for (RecipientDto recipient : dto.getEmailTo()) {
               this.validateEmailAddress(recipient.getEmail());
            }
         }

         Map<String, Object> row = this.emailDao.insertEmail(dto.getEmailFrom(), this.toJson(dto.getEmailTo()),
               this.toJson(dto.getEmailCC()), dto.getEmailSubject(), dto.getEmailBody(),
               dto.getState() != null ? dto.getState() : MailConstants.STATE_DRAFT);
         EmailDto saved = this
               .toDto(this.emailDao.findEmailByIdWithSender(((Number) row.get(MailConstants.EMAIL_ID)).longValue()));
         result.add(saved);

         if (MailConstants.STATE_SENT == saved.getState()) {
            this.emailPublisher.publishSendEmail(saved);
         }
      }
      return result;
   }

   @Override
   public List<EmailDto> findAll(Integer state) {
      return this.emailDao.findAllWithSender(state).stream().map(this::toDto).toList();
   }

   @Override
   public EmailDto findById(Long id) {
      Map<String, Object> row = this.emailDao.findEmailByIdWithSender(id);
      if (row == null) {
         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not found: " + id);
      }
      return this.toDto(row);
   }

   @Override
   public EmailDto update(Long id, EmailDto dto) {
      Integer currentState = this.emailDao.getState(id);
      if (currentState == null) {
         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not found: " + id);
      }
      if (currentState != MailConstants.STATE_DRAFT) {
         throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
               "Only emails in DRAFT state can be updated");
      }

      this.emailDao.updateEmail(id, this.toJson(dto.getEmailTo()), this.toJson(dto.getEmailCC()), dto.getEmailSubject(),
            dto.getEmailBody());
      return this.toDto(this.emailDao.findEmailByIdWithSender(id));
   }

   @Override
   public void deleteAll(List<Long> ids) {
      for (Long id : ids) {
         if (this.emailDao.getState(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not found: " + id);
         }
         this.emailDao.updateState(id, MailConstants.STATE_DELETED);
      }
   }

   @Override
   public EmailDto sendEmail(Long id) {
      Integer currentState = this.emailDao.getState(id);
      if (currentState == null) {
         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not found: " + id);
      }
      if (currentState != MailConstants.STATE_DRAFT) {
         throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Only emails in DRAFT state can be sent");
      }

      this.emailDao.updateState(id, MailConstants.STATE_SENT);
      EmailDto sent = this.toDto(this.emailDao.findEmailByIdWithSender(id));
      this.emailPublisher.publishSendEmail(sent);
      return sent;
   }

   @Override
   public List<EmailDto> findByUserId(Integer userId) {
      List<Map<String, Object>> rows = this.userDao.query(Map.of(MailConstants.USER_ID, userId), List.of(), null);
      if (rows.isEmpty()) {
         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId);
      }
      return this.emailDao.findByUserId(userId).stream().map(this::toDto).toList();
   }

   @Override
   public void markCarlEmailsAsSpam() {
      log.info("Scheduled task: marking emails from {} as SPAM", MailConstants.CARL_EMAIL);
      this.emailDao.updateStateByEmailFrom(MailConstants.CARL_EMAIL, MailConstants.STATE_SPAM);
   }

   // ── helpers ───────────────────────────────────────────────────────────────

   /**
    * Validates that the given email address exists in tusers. Throws 400 if not
    * found.
    */
   private void validateEmailAddress(String email) {
      if (email == null || email.isBlank()) {
         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email address must not be blank");
      }
      List<Map<String, Object>> rows = this.userDao.query(Map.of(MailConstants.USER_EMAIL, email),
            List.of(MailConstants.USER_ID), null);
      if (rows.isEmpty()) {
         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email address not registered: " + email);
      }
   }

   private String toJson(Object obj) {
      if (obj == null) {
         return null;
      }
      try {
         return this.objectMapper.writeValueAsString(obj);
      } catch (JsonProcessingException e) {
         return null;
      }
   }

   private <T> T fromJson(String json, TypeReference<T> type) {
      if (json == null || json.isBlank()) {
         return null;
      }
      try {
         return this.objectMapper.readValue(json, type);
      } catch (Exception e) {
         return null;
      }
   }

   private EmailDto toDto(Map<String, Object> row) {
      EmailDto dto = new EmailDto();
      dto.setEmailId(this.toLong(row.get(MailConstants.EMAIL_ID)));
      dto.setEmailFrom((String) row.get(MailConstants.EMAIL_FROM));
      dto.setEmailTo(this.fromJson((String) row.get(MailConstants.EMAIL_TO), new TypeReference<List<RecipientDto>>() {
      }));
      dto.setEmailCC(this.fromJson((String) row.get(MailConstants.EMAIL_CC), new TypeReference<List<RecipientDto>>() {
      }));
      dto.setEmailSubject((String) row.get(MailConstants.EMAIL_SUBJECT));
      dto.setEmailBody((String) row.get(MailConstants.EMAIL_BODY));
      Object stateObj = row.get(MailConstants.EMAIL_STATE);
      dto.setState(stateObj != null ? ((Number) stateObj).intValue() : null);

      String userName = (String) row.get(MailConstants.USER_NAME);
      String userSurname = (String) row.get(MailConstants.USER_SURNAME);
      if (userName != null || userSurname != null) {
         dto.setEmailFromUser(new SenderDto(userName, userSurname));
      }

      return dto;
   }

   private Long toLong(Object val) {
      if (val == null) {
         return null;
      }
      return ((Number) val).longValue();
   }
}