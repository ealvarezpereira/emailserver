package com.gbtec.emailmanager.model.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDto {

   @JsonProperty("userId")
   private Integer userId;

   @JsonProperty("userEmail")
   private String userEmail;

   @JsonProperty("userName")
   private String userName;

   @JsonProperty("userSurname")
   private String userSurname;

   public Integer getUserId() {
      return userId;
   }

   public void setUserId(Integer userId) {
      this.userId = userId;
   }

   public String getUserEmail() {
      return userEmail;
   }

   public void setUserEmail(String userEmail) {
      this.userEmail = userEmail;
   }

   public String getUserName() {
      return userName;
   }

   public void setUserName(String userName) {
      this.userName = userName;
   }

   public String getUserSurname() {
      return userSurname;
   }

   public void setUserSurname(String userSurname) {
      this.userSurname = userSurname;
   }
}
