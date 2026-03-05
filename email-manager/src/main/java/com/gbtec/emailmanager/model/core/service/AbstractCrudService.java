package com.gbtec.emailmanager.model.core.service;

import java.util.List;
import java.util.Map;

import com.gbtec.emailmanager.api.core.service.ICrudService;
import com.gbtec.emailmanager.model.core.dao.AbstractCrudDao;



/**
 * Servicio base genérico.
 * <br>
 * La subclase inyecta su DAO concreto con @Autowired estándar de Spring.
 */
public abstract class AbstractCrudService implements ICrudService<Integer> {

    protected abstract AbstractCrudDao getDao();

    @Override
    public List<Map<String, Object>> query(Map<String, Object> filter, List<String> columns) {
        return getDao().query(filter, columns);
    }

    @Override
    public Map<String, Object> insert(Map<String, Object> attributes) {
        return getDao().insert(attributes);
    }

    @Override
    public Map<String, Object> update(Integer id, Map<String, Object> attributes) {
        return getDao().update(id, attributes);
    }

    @Override
    public void delete(Integer id) {
        getDao().delete(id);
    }
}
