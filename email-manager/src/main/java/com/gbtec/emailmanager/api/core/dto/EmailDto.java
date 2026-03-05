package com.gbtec.emailmanager.api.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
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

   @Data
   public static class RecipientDto {
      @JsonProperty("email")
      private String email;
   }
}
