package com.gbtec.emailmanager.api.core.service;

import java.util.List;
import java.util.Map;

public interface ICrudService<ID> {

    /**
     * Consulta registros con filtros opcionales y lista de columnas a devolver.
     * @param filter 
     * @param columns
     * @return
     */
    List<Map<String, Object>> query(Map<String, Object> filter, List<String> columns);

    /**
     * Inserta un nuevo registro
     * 
     * @param attributes
     * @return
     */
    Map<String, Object> insert(Map<String, Object> attributes);

    /**
     * Actualiza un registro existente por su ID.
     * 
     * @param id
     * @param attributes
     * @return
     */
    Map<String, Object> update(ID id, Map<String, Object> attributes);

    /**
     * Elimina un registro existente por su ID.
     * 
     * @param id
     */
    void delete(ID id);
}
