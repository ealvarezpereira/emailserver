package com.gbtec.emailmanager.model.core.dao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.gbtec.emailmanager.api.core.constants.MailConstants;

@Repository
public class EmailDao extends AbstractCrudDao {

   @Override
   protected String getTableName() {
      return MailConstants.EMAIL_DB_TABLENAME;
   }

   @Override
   protected String getPrimaryKey() {
      return MailConstants.EMAIL_ID;
   }

   /**
    * Inserts an email with all its specific fields and returns the inserted row.
    * 
    * @param emailFrom
    * @param emailTo
    * @param emailCc
    * @param subject
    * @param body
    * @param state
    * @return
    */

   public Map<String, Object> insertEmail(String emailFrom, String emailTo, String emailCc, String subject, String body,
         int state) {
      MapSqlParameterSource params = new MapSqlParameterSource().addValue(MailConstants.EMAIL_FROM, emailFrom)
            .addValue(MailConstants.EMAIL_TO, emailTo).addValue(MailConstants.EMAIL_CC, emailCc)
            .addValue(MailConstants.EMAIL_SUBJECT, subject).addValue(MailConstants.EMAIL_BODY, body)
            .addValue(MailConstants.EMAIL_STATE, state)
            .addValue(MailConstants.EMAIL_UPDATED_AT, Timestamp.valueOf(LocalDateTime.now()));

      KeyHolder keyHolder = new GeneratedKeyHolder();
      this.namedJdbc.update(this.buildInsertSql(), params, keyHolder, new String[] { MailConstants.EMAIL_ID });

      return this.findEmailById(keyHolder.getKey().longValue());
   }

   /**
    * Updates the editable fields of an email (only allowed in Draft status).
    * 
    * @param id
    * @param emailTo
    * @param emailCc
    * @param subject
    * @param body
    * @return
    */

   public Map<String, Object> updateEmail(Long id, String emailTo, String emailCc, String subject, String body) {
      String sql = "UPDATE " + this.getTableName() + " SET " + MailConstants.EMAIL_TO + " = :to, "
            + MailConstants.EMAIL_CC + " = :cc, " + MailConstants.EMAIL_SUBJECT + " = :subject, "
            + MailConstants.EMAIL_BODY + " = :body, " + MailConstants.EMAIL_UPDATED_AT + " = :updatedAt " + "WHERE "
            + MailConstants.EMAIL_ID + " = :id";

      this.namedJdbc.update(sql,
            new MapSqlParameterSource().addValue("to", emailTo).addValue("cc", emailCc).addValue("subject", subject)
                  .addValue("body", body).addValue("updatedAt", Timestamp.valueOf(LocalDateTime.now()))
                  .addValue("id", id));

      return this.findEmailById(id);
   }

   /**
    * Updates an email's status by its ID.
    * 
    * @param id
    * @param state
    */
   public void updateState(Long id, int state) {
      String sql = "UPDATE " + this.getTableName() + " SET " + MailConstants.EMAIL_STATE + " = :state, "
            + MailConstants.EMAIL_UPDATED_AT + " = :updatedAt " + "WHERE " + MailConstants.EMAIL_ID + " = :id";

      this.namedJdbc.update(sql, new MapSqlParameterSource().addValue("state", state)
            .addValue("updatedAt", Timestamp.valueOf(LocalDateTime.now())).addValue("id", id));
   }

   /**
    * Updates the status of all emails from a specific sender.
    * 
    * @param emailFrom
    * @param state
    */
   public void updateStateByEmailFrom(String emailFrom, int state) {
      String sql = "UPDATE " + this.getTableName() + " SET " + MailConstants.EMAIL_STATE + " = :state, "
            + MailConstants.EMAIL_UPDATED_AT + " = :updatedAt " + "WHERE " + MailConstants.EMAIL_FROM + " = :emailFrom";

      this.namedJdbc.update(sql, new MapSqlParameterSource().addValue("state", state)
            .addValue("updatedAt", Timestamp.valueOf(LocalDateTime.now())).addValue("emailFrom", emailFrom));
   }

   /**
    * Returns the current status of an email, or null if it does not exist.
    * 
    * @param id
    * @return
    */
   public Integer getState(Long id) {
      List<Map<String, Object>> rows = this.query(Map.of(MailConstants.EMAIL_ID, id),
            List.of(MailConstants.EMAIL_STATE), null);
      if (rows.isEmpty()) {
         return null;
      }
      return (Integer) rows.get(0).get(MailConstants.EMAIL_STATE);
   }

   public Map<String, Object> findEmailById(Long id) {
      List<Map<String, Object>> rows = this.query(Map.of(MailConstants.EMAIL_ID, id), null, null);
      return rows.isEmpty() ? null : rows.get(0);
   }

   /**
    * Returns a single email joined with the sender's user data from tusers.
    *
    * @param id
    * @return row with email fields plus user_name and user_surname of the sender,
    *         or null
    */
   public Map<String, Object> findEmailByIdWithSender(Long id) {
      String sql = "SELECT e.*, u." + MailConstants.USER_NAME + ", u." + MailConstants.USER_SURNAME + " FROM "
            + MailConstants.EMAIL_DB_TABLENAME + " e" + " LEFT JOIN " + MailConstants.USER_DB_TABLENAME + " u ON u."
            + MailConstants.USER_EMAIL + " = e." + MailConstants.EMAIL_FROM + " WHERE e." + MailConstants.EMAIL_ID
            + " = :id";
      List<Map<String, Object>> rows = this.namedJdbc.queryForList(sql, new MapSqlParameterSource("id", id));
      return rows.isEmpty() ? null : rows.get(0);
   }

   /**
    * Returns all emails sent by a given user (matched by user_id in tusers).
    *
    * @param userId
    * @return list of rows with email fields plus user_name and user_surname
    */
   public List<Map<String, Object>> findByUserId(Integer userId) {
      String sql = "SELECT e.*, u." + MailConstants.USER_NAME + ", u." + MailConstants.USER_SURNAME + " FROM "
            + MailConstants.EMAIL_DB_TABLENAME + " e" + " JOIN " + MailConstants.USER_DB_TABLENAME + " u ON u."
            + MailConstants.USER_EMAIL + " = e." + MailConstants.EMAIL_FROM + " WHERE u." + MailConstants.USER_ID
            + " = :userId";
      return this.namedJdbc.queryForList(sql, new MapSqlParameterSource("userId", userId));
   }

   /**
    * Returns all emails optionally filtered by state, joined with sender user
    * data.
    *
    * @param state nullable
    * @return list of rows with email + sender info
    */
   public List<Map<String, Object>> findAllWithSender(Integer state) {
      StringBuilder sql = new StringBuilder(
            "SELECT e.*, u." + MailConstants.USER_NAME + ", u." + MailConstants.USER_SURNAME + " FROM "
                  + MailConstants.EMAIL_DB_TABLENAME + " e" + " LEFT JOIN " + MailConstants.USER_DB_TABLENAME
                  + " u ON u." + MailConstants.USER_EMAIL + " = e." + MailConstants.EMAIL_FROM);
      MapSqlParameterSource params = new MapSqlParameterSource();
      if (state != null) {
         sql.append(" WHERE e." + MailConstants.EMAIL_STATE + " = :state");
         params.addValue("state", state);
      }
      return this.namedJdbc.queryForList(sql.toString(), params);
   }

   // ---- helpers ----

   protected String buildInsertSql() {
      return "INSERT INTO " + this.getTableName() + " (" + MailConstants.EMAIL_FROM + ", " + MailConstants.EMAIL_TO
            + ", " + MailConstants.EMAIL_CC + ", " + MailConstants.EMAIL_SUBJECT + ", " + MailConstants.EMAIL_BODY
            + ", " + MailConstants.EMAIL_STATE + ", " + MailConstants.EMAIL_UPDATED_AT + ") VALUES (" + ":"
            + MailConstants.EMAIL_FROM + ", " + ":" + MailConstants.EMAIL_TO + ", " + ":" + MailConstants.EMAIL_CC
            + ", " + ":" + MailConstants.EMAIL_SUBJECT + ", " + ":" + MailConstants.EMAIL_BODY + ", " + ":"
            + MailConstants.EMAIL_STATE + ", " + ":" + MailConstants.EMAIL_UPDATED_AT + ")";
   }
}
