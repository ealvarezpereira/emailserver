package com.gbtec.emailmanager.model.core.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DAO base genérico con operaciones CRUD sobre JdbcTemplate.
 * <br>
 * Genera SQL dinámico a partir de los mapas de atributos, utilizando Spring JDBC
 * <p>
 * <b>Uso:</b> Extiende esta clase e implementa <b>getTableName()</b> y <b>getPrimaryKey()</b>
 */
public abstract class AbstractCrudDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected NamedParameterJdbcTemplate namedJdbc;

    /**
     * @return Nombre de la tabla en base de datos
     */
    protected abstract String getTableName();

    /**
     * @return Nombre de la columna PK
     */
    protected abstract String getPrimaryKey();

    /**
     * Consulta registros con filtros dinámicos.
     *
     *
     * @param filter  columnas y valores para el WHERE (puede ser vacío → sin filtro)
     * @param columns columnas a devolver (null o vacío → SELECT *)
     */
    public List<Map<String, Object>> query(Map<String, Object> filter, List<String> columns) {
        String select = (columns == null || columns.isEmpty())
                ? "*"
                : columns.stream().map(this::quoteIdentifier).collect(Collectors.joining(", "));

        StringBuilder sql = new StringBuilder("SELECT ").append(select)
                .append(" FROM ").append(quoteIdentifier(getTableName()));

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (filter != null && !filter.isEmpty()) {
            String where = filter.entrySet().stream()
                    .filter(e -> e.getValue() != null)
                    .map(e -> quoteIdentifier(e.getKey()) + " = :" + e.getKey())
                    .collect(Collectors.joining(" AND "));
            if (!where.isBlank()) {
                sql.append(" WHERE ").append(where);
                filter.entrySet().stream()
                        .filter(e -> e.getValue() != null)
                        .forEach(e -> params.addValue(e.getKey(), e.getValue()));
            }
        }

        return namedJdbc.queryForList(sql.toString(), params);
    }

    /**
     * Inserta un registro y devuelve la fila insertada con la PK generada.
     *
     */
    public Map<String, Object> insert(Map<String, Object> attributes) {
        Map<String, Object> filtered = filterNonNull(attributes);
        String cols = filtered.keySet().stream()
                .map(this::quoteIdentifier)
                .collect(Collectors.joining(", "));
        String vals = filtered.keySet().stream()
                .map(k -> ":" + k)
                .collect(Collectors.joining(", "));

        String sql = "INSERT INTO " + quoteIdentifier(getTableName())
                + " (" + cols + ") VALUES (" + vals + ")";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedJdbc.update(sql, new MapSqlParameterSource(filtered), keyHolder);

        Number generatedKey = keyHolder.getKey();
        Map<String, Object> result = new LinkedHashMap<>(filtered);
        if (generatedKey != null) {
            result.put(getPrimaryKey(), generatedKey.intValue());
        }
        return result;
    }

    /**
     * Actualiza un registro por su PK.
     *
     */
    public Map<String, Object> update(Integer id, Map<String, Object> attributes) {
        Map<String, Object> filtered = filterNonNull(attributes);
        filtered.remove(getPrimaryKey()); // no actualizar la PK

        String sets = filtered.keySet().stream()
                .map(k -> quoteIdentifier(k) + " = :" + k)
                .collect(Collectors.joining(", "));

        filtered.put("_pk_", id);
        String sql = "UPDATE " + quoteIdentifier(getTableName())
                + " SET " + sets
                + " WHERE " + quoteIdentifier(getPrimaryKey()) + " = :_pk_";

        namedJdbc.update(sql, new MapSqlParameterSource(filtered));

        // Devolver la fila actualizada
        Map<String, Object> pkFilter = Map.of(getPrimaryKey(), id);
        List<Map<String, Object>> rows = query(pkFilter, null);
        return rows.isEmpty() ? Map.of() : rows.get(0);
    }

    /**
     * Elimina un registro por su PK.
     *
     */
    public void delete(Integer id) {
        String sql = "DELETE FROM " + quoteIdentifier(getTableName())
                + " WHERE " + quoteIdentifier(getPrimaryKey()) + " = ?";
        jdbcTemplate.update(sql, id);
    }

    // ---- helpers ----

    private Map<String, Object> filterNonNull(Map<String, Object> map) {
        Map<String, Object> result = new LinkedHashMap<>();
        map.forEach((k, v) -> {
            if (v != null) result.put(k, v);
        });
        return result;
    }

    /**
     * Entrecomilla identificadores SQL para evitar conflictos con palabras reservadas.
     * HSQLDB usa comillas dobles estándar SQL.
     */
    private String quoteIdentifier(String name) {
        return "\"" + name.toUpperCase() + "\"";
    }
}
