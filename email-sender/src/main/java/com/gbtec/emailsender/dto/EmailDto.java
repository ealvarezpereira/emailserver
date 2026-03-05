package com.gbtec.emailsender.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

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

    public Long getEmailId() { return emailId; }
    public void setEmailId(Long emailId) { this.emailId = emailId; }

    public String getEmailFrom() { return emailFrom; }
    public void setEmailFrom(String emailFrom) { this.emailFrom = emailFrom; }

    public List<RecipientDto> getEmailTo() { return emailTo; }
    public void setEmailTo(List<RecipientDto> emailTo) { this.emailTo = emailTo; }

    public List<RecipientDto> getEmailCC() { return emailCC; }
    public void setEmailCC(List<RecipientDto> emailCC) { this.emailCC = emailCC; }

    public String getEmailBody() { return emailBody; }
    public void setEmailBody(String emailBody) { this.emailBody = emailBody; }

    public String getEmailSubject() { return emailSubject; }
    public void setEmailSubject(String emailSubject) { this.emailSubject = emailSubject; }

    public Integer getState() { return state; }
    public void setState(Integer state) { this.state = state; }

    public static class RecipientDto {
        @JsonProperty("email")
        private String email;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}
