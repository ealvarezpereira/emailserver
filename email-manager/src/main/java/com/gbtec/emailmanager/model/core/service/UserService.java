package com.gbtec.emailmanager.model.core.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gbtec.emailmanager.api.core.service.IUserService;
import com.gbtec.emailmanager.model.core.dao.AbstractCrudDao;
import com.gbtec.emailmanager.model.core.dao.UserDao;



@Service
public class UserService extends AbstractCrudService implements IUserService {

	@Autowired
	private UserDao userDao;

   @Override
   protected AbstractCrudDao getDao() {
      return this.userDao;
   }

}
