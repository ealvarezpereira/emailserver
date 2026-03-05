package com.gbtec.emailmanager.model.core.service;

import java.util.List;
import java.util.Map;

import com.gbtec.emailmanager.api.core.service.ICrudService;
import com.gbtec.emailmanager.model.core.dao.AbstractCrudDao;

/**
 * Generic base service that delegates to the specific DAO.
 * <p>
 * The subclass implements {@link #getDao()} returning its specific DAO. Any
 * method can be overridden to add business logic.
 */
public abstract class AbstractCrudService implements ICrudService {

   protected abstract AbstractCrudDao getDao();

   @Override
   public List<Map<String, Object>> query(Map<String, Object> filter, List<String> columns, String sort) {
      return this.getDao().query(filter, columns, sort);
   }

   @Override
   public Map<String, Object> insert(Map<String, Object> attributes) {
      return this.getDao().insert(attributes);
   }

   @Override
   public Map<String, Object> update(Integer id, Map<String, Object> attributes) {
      return this.getDao().update(id, attributes);
   }

   @Override
   public void delete(Integer id) {
      this.getDao().delete(id);
   }
}
