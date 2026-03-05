package com.gbtec.emailmanager.model.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class EmailRequestDto {

   @JsonProperty("emails")
   private List<EmailDto> emails;

   public List<EmailDto> getEmails() {
      return emails;
   }

   public void setEmails(List<EmailDto> emails) {
      this.emails = emails;
   }
}
