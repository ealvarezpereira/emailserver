package com.gbtec.emailmanager.api.core.constants;

public class MailConstants {

   // Users
   public static final String USER_DB_TABLENAME = "tusers";
   public static final String USER_ID = "user_id";
   public static final String USER_EMAIL = "user_email";
   public static final String USER_NAME = "user_name";
   public static final String USER_SURNAME = "user_surname";

   // Emails
   public static final String EMAIL_DB_TABLENAME = "temails";
   public static final String EMAIL_ID = "email_id";
   public static final String EMAIL_FROM = "email_from";
   public static final String EMAIL_TO = "email_to";
   public static final String EMAIL_CC = "email_cc";
   public static final String EMAIL_SUBJECT = "email_subject";
   public static final String EMAIL_BODY = "email_body";
   public static final String EMAIL_STATE = "email_state";
   public static final String EMAIL_UPDATED_AT = "email_updated_at";

   // States
   public static final int STATE_SENT = 1;
   public static final int STATE_DRAFT = 2;
   public static final int STATE_DELETED = 3;
   public static final int STATE_SPAM = 4;

   // Carl's email (scheduled task)
   public static final String CARL_EMAIL = "carl@gbtec.es";

   // RabbitMQ
   public static final String EXCHANGE = "email.exchange";
   public static final String QUEUE = "email.queue";
   public static final String ROUTING_KEY = "email.routing.key";
}
