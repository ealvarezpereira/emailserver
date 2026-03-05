package com.gbtec.emailmanager.api.core.service;

import java.util.List;
import java.util.Map;

/**
 * Generic interface CRUD.
 */
public interface ICrudService {

   /**
    * Query record with custom filters
    * 
    * @param filter
    * @param columns
    * @param sort
    * @return
    */
   List<Map<String, Object>> query(Map<String, Object> filter, List<String> columns, String sort);

   /**
    * Insert record
    * 
    * @param attributes
    * @return
    */
   Map<String, Object> insert(Map<String, Object> attributes);

   /**
    * Update record
    * 
    * @param id
    * @param attributes
    * @return
    */
   Map<String, Object> update(Integer id, Map<String, Object> attributes);

   /**
    * Delete record
    * 
    * @param id
    */
   void delete(Integer id);
}
