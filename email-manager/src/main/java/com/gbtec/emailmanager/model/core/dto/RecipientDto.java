package com.gbtec.emailmanager.model.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecipientDto {
   @JsonProperty("email")
   private String email;

   public String getEmail() {
      return this.email;
   }

   public void setEmail(String email) {
      this.email = email;
   }
}
