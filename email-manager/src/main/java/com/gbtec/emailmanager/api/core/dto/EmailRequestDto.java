package com.gbtec.emailmanager.api.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class EmailRequestDto {

   @JsonProperty("emails")
   private List<EmailDto> emails;
}
