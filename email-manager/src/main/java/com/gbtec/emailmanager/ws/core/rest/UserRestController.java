package com.gbtec.emailmanager.ws.core.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gbtec.emailmanager.api.core.service.IUserService;


@RestController
@RequestMapping("/emails/users")
public class UserRestController extends AbstractCrudController<IUserService> {

   @Autowired
   private IUserService userService;

   @Override
   protected IUserService getService() {
       return userService;
   }

}
