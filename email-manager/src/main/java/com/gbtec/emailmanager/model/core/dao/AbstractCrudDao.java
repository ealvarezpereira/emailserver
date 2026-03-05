package com.gbtec.emailmanager.model.core.dao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import jakarta.annotation.PostConstruct;

/**
 * Generic Base DAO with CRUD operations using JdbcTemplate. <br>
 * Generates dynamic SQL based on attribute maps, utilizing Spring JDBC.
 * <p>
 * <b>Usage:</b> Extend this class and implement <b>getTableName()</b> and
 * <b>getPrimaryKey()</b>.
 */
public abstract class AbstractCrudDao {

   @Autowired
   protected JdbcTemplate jdbcTemplate;

   @Autowired
   protected NamedParameterJdbcTemplate namedJdbc;

   protected SimpleJdbcInsert insertExecutor;

   /**
    * Configures the SimpleJdbcInsert after dependency injection.
    */
   @PostConstruct
   private void init() {
      this.insertExecutor = new SimpleJdbcInsert(this.jdbcTemplate).withTableName(this.getTableName())
            .usingGeneratedKeyColumns(this.getPrimaryKey());
   }

   /**
    * @return Database table name
    */
   protected abstract String getTableName();

   /**
    * @return PK column name
    */
   protected abstract String getPrimaryKey();

   /**
    * Executes a dynamic query against the table.
    *
    * @param filter  Map of column names and values for the WHERE clause.
    * @param columns List of columns to select. If null or empty, selects all (*).
    * @param sort    Optional sorting string
    * @return A list of maps representing the result rows.
    */
   public List<Map<String, Object>> query(Map<String, Object> filter, List<String> columns, String sort) {
      String selectClause = (columns == null || columns.isEmpty()) ? "*"
            : columns.stream().map(this::quoteIdentifier).collect(Collectors.joining(", "));

      StringBuilder sql = new StringBuilder("SELECT ").append(selectClause).append(" FROM ")
            .append(this.quoteIdentifier(this.getTableName()));

      MapSqlParameterSource params = new MapSqlParameterSource();
      Map<String, Object> filteredMap = this.filterNonNull(filter != null ? filter : Map.of());

      if (!filteredMap.isEmpty()) {
         String whereClause = filteredMap.keySet().stream()
               .map(column -> String.format("%s = :%s", this.quoteIdentifier(column), column))
               .collect(Collectors.joining(" AND "));

         sql.append(" WHERE ").append(whereClause);
         params.addValues(filteredMap);
      }

      if (sort != null && !sort.isBlank()) {
         sql.append(" ORDER BY ").append(sort);
      }

      return this.namedJdbc.queryForList(sql.toString(), params);
   }

   /**
    * Inserts a record and returns the inserted row including the generated PK.
    *
    * @param attributes
    * @return
    */
   public Map<String, Object> insert(Map<String, Object> attributes) {
      Map<String, Object> filtered = this.filterNonNull(attributes);

      Number generatedKey = this.insertExecutor.executeAndReturnKey(filtered);

      Map<String, Object> result = new LinkedHashMap<>(filtered);
      if (generatedKey != null) {
         result.put(this.getPrimaryKey(), generatedKey.intValue());
      }
      return result;
   }

   /**
    * Updates a record by its primary key with the provided attributes.
    * 
    * @param id         the primary key value.
    * @param attributes Map of column names and their new values.
    * @return The updated record or an empty map if not found.
    */
   public Map<String, Object> update(Integer id, Map<String, Object> attributes) {
      Map<String, Object> filtered = this.filterNonNull(attributes);
      filtered.remove(this.getPrimaryKey());

      if (filtered.isEmpty()) {
         // Nothing to update, but we return the PK to acknowledge existence
         return Map.of(this.getPrimaryKey(), id);
      }

      String sets = filtered.keySet().stream().map(k -> this.quoteIdentifier(k) + " = :" + k)
            .collect(Collectors.joining(", "));

      MapSqlParameterSource params = new MapSqlParameterSource(filtered);
      params.addValue("_pk_", id);

      String sql = "UPDATE " + this.quoteIdentifier(this.getTableName()) + " SET " + sets + " WHERE "
            + this.quoteIdentifier(this.getPrimaryKey()) + " = :_pk_";

      int affectedRows = this.namedJdbc.update(sql, params);

      if (affectedRows == 0) {
         return Map.of();
      }

      Map<String, Object> result = new LinkedHashMap<>(filtered);
      result.put(this.getPrimaryKey(), id);
      return result;
   }

   /**
    * Deletes a record by its PK and returns true if a row was actually removed.
    *
    * @param id The primary key value to delete
    * @return true if the record was deleted, false if it didn't exist
    */
   public boolean delete(Integer id) {
      // Validate input
      if (id == null) {
         return false;
      }

      String sql = "DELETE FROM " + this.quoteIdentifier(this.getTableName()) + 
                   " WHERE " + this.quoteIdentifier(this.getPrimaryKey()) + " = :id";
      
      int rowsAffected = this.namedJdbc.update(sql, Map.of("id", id));

      return rowsAffected > 0;
   }

   // ---- helpers ----

   /**
    * Filter non null fields
    * 
    * @param map
    * @return
    */
   protected Map<String, Object> filterNonNull(Map<String, Object> map) {
      Map<String, Object> result = new LinkedHashMap<>();
      map.forEach((k, v) -> {
         if (v != null) {
            result.put(k, v);
         }
      });
      return result;
   }

   /**
    * Returns identifiers in lowercase in case the query is executed in uppercase.
    * 
    * @param name the identifier
    * @return the identifier in lowercase
    */
   protected String quoteIdentifier(String name) {
      return name.toLowerCase();
   }

}
