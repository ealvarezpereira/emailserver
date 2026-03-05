package com.gbtec.emailmanager.api.core.service;

import com.gbtec.emailmanager.model.core.dto.UserDto;

import java.util.List;

public interface IUserService {

   /**
    * Find all users
    * 
    * @return List of users
    */
   List<UserDto> findAll();

   /**
    * Find user by id
    * 
    * @param id the user ID
    * @return the user
    */
   UserDto findById(Integer id);

   /**
    * Insert user
    * 
    * @param dto of the user
    * @return the user
    */
   UserDto insertUser(UserDto dto);

   /**
    * Update user
    * 
    * @param id the user id
    * @param dto of the user
    * @return
    */
   UserDto updateUser(Integer id, UserDto dto);

   /**
    * Delete user
    * 
    * @param id the user id
    */
   void deleteUser(Integer id);
}
