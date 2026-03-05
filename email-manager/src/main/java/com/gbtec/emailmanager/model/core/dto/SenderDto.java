package com.gbtec.emailmanager.model.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SenderDto {
   @JsonProperty("userName")
   private String userName;

   @JsonProperty("userSurname")
   private String userSurname;

   public SenderDto(String userName, String userSurname) {
      this.userName = userName;
      this.userSurname = userSurname;
   }

   public String getUserName() {
      return this.userName;
   }

   public String getUserSurname() {
      return this.userSurname;
   }
}