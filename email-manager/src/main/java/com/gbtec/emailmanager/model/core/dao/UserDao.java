package com.gbtec.emailmanager.model.core.dao;

import org.springframework.stereotype.Repository;

import com.gbtec.emailmanager.api.core.constants.MailConstants;

@Repository()
public class UserDao extends AbstractCrudDao {

   @Override
   protected String getTableName() {
      return MailConstants.USER_DB_TABLENAME;
   }

   @Override
   protected String getPrimaryKey() {
      return MailConstants.USER_ID;
   }
}
