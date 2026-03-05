package com.gbtec.emailmanager.model.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gbtec.emailmanager.api.core.constants.MailConstants;
import com.gbtec.emailmanager.api.core.service.IUserService;
import com.gbtec.emailmanager.model.core.dao.AbstractCrudDao;
import com.gbtec.emailmanager.model.core.dao.UserDao;
import com.gbtec.emailmanager.model.core.dto.UserDto;

@Service
public class UserService extends AbstractCrudService implements IUserService {

   @Autowired
   private UserDao userDao;

   @Override
   protected AbstractCrudDao getDao() {
      return this.userDao;
   }

   @Override
   public List<UserDto> findAll() {
      return this.query(Map.of(), List.of(), null).stream().map(this::toDto).toList();
   }

   @Override
   public UserDto findById(Integer id) {
      List<Map<String, Object>> rows = this.query(Map.of(MailConstants.USER_ID, id), List.of(), null);
      if (rows.isEmpty()) {
         throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id);
      }
      return this.toDto(rows.get(0));
   }

   @Override
   public UserDto insertUser(UserDto dto) {
      return this.toDto(this.insert(this.toMap(dto)));
   }

   @Override
   public UserDto updateUser(Integer id, UserDto dto) {
      this.findById(id);
      return this.toDto(this.update(id, this.toMap(dto)));
   }

   @Override
   public void deleteUser(Integer id) {
      this.findById(id);
      this.delete(id);
   }

   // ---- helpers ----

   private UserDto toDto(Map<String, Object> row) {
      UserDto dto = new UserDto();
      Object id = row.get(MailConstants.USER_ID);
      dto.setUserId(id != null ? ((Number) id).intValue() : null);
      dto.setUserEmail((String) row.get(MailConstants.USER_EMAIL));
      dto.setUserName((String) row.get(MailConstants.USER_NAME));
      dto.setUserSurname((String) row.get(MailConstants.USER_SURNAME));
      return dto;
   }

   private Map<String, Object> toMap(UserDto dto) {
      Map<String, Object> map = new HashMap<>();
      map.put(MailConstants.USER_EMAIL, dto.getUserEmail());
      map.put(MailConstants.USER_NAME, dto.getUserName());
      map.put(MailConstants.USER_SURNAME, dto.getUserSurname());
      return map;
   }
}
