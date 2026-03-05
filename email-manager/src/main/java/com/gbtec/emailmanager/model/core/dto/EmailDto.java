package com.gbtec.emailmanager.model.core.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailDto {

   @JsonProperty("emailId")
   private Long emailId;

   @JsonProperty("emailFrom")
   private String emailFrom;

   @JsonProperty("emailTo")
   private List<RecipientDto> emailTo;

   @JsonProperty("emailCC")
   private List<RecipientDto> emailCC;

   @JsonProperty("emailBody")
   private String emailBody;

   @JsonProperty("emailSubject")
   private String emailSubject;

   @JsonProperty("state")
   private Integer state;

   @JsonProperty("emailFromUser")
   private SenderDto emailFromUser;

   public Long getEmailId() {
      return this.emailId;
   }

   public void setEmailId(Long emailId) {
      this.emailId = emailId;
   }

   public String getEmailFrom() {
      return this.emailFrom;
   }

   public void setEmailFrom(String emailFrom) {
      this.emailFrom = emailFrom;
   }

   public List<RecipientDto> getEmailTo() {
      return this.emailTo;
   }

   public void setEmailTo(List<RecipientDto> emailTo) {
      this.emailTo = emailTo;
   }

   public List<RecipientDto> getEmailCC() {
      return this.emailCC;
   }

   public void setEmailCC(List<RecipientDto> emailCC) {
      this.emailCC = emailCC;
   }

   public String getEmailBody() {
      return this.emailBody;
   }

   public void setEmailBody(String emailBody) {
      this.emailBody = emailBody;
   }

   public String getEmailSubject() {
      return this.emailSubject;
   }

   public void setEmailSubject(String emailSubject) {
      this.emailSubject = emailSubject;
   }

   public Integer getState() {
      return this.state;
   }

   public void setState(Integer state) {
      this.state = state;
   }

   public SenderDto getEmailFromUser() {
      return this.emailFromUser;
   }

   public void setEmailFromUser(SenderDto emailFromUser) {
      this.emailFromUser = emailFromUser;
   }
}
