package com.gbtec.emailmanager.ws.core.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gbtec.emailmanager.api.core.service.IEmailService;
import com.gbtec.emailmanager.api.core.service.IUserService;
import com.gbtec.emailmanager.model.core.dto.EmailDto;
import com.gbtec.emailmanager.model.core.dto.UserDto;

/**
 * REST API for user management.
 *
 * GET    /users              → list all users
 * GET    /users/{id}         → get user by ID
 * GET    /users/{id}/emails  → get all emails sent by this user
 * POST   /users              → create a new user
 * PUT    /users/{id}         → update user details
 * DELETE /users/{id}         → delete user by ID
 */
@RestController
@RequestMapping("/users")
public class UserRestController {

   @Autowired
   private IUserService userService;

   @Autowired
   private IEmailService emailService;

   @GetMapping
   public ResponseEntity<List<UserDto>> findAll() {
      return ResponseEntity.ok(this.userService.findAll());
   }

   @GetMapping("/{id}")
   public ResponseEntity<UserDto> findById(@PathVariable Integer id) {
      return ResponseEntity.ok(this.userService.findById(id));
   }

   @GetMapping("/{id}/emails")
   public ResponseEntity<List<EmailDto>> findEmailsByUserId(@PathVariable Integer id) {
      return ResponseEntity.ok(this.emailService.findByUserId(id));
   }

   @PostMapping
   public ResponseEntity<UserDto> insert(@RequestBody UserDto dto) {
      return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.insertUser(dto));
   }

   @PutMapping("/{id}")
   public ResponseEntity<UserDto> update(@PathVariable Integer id, @RequestBody UserDto dto) {
      return ResponseEntity.ok(this.userService.updateUser(id, dto));
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> delete(@PathVariable Integer id) {
      this.userService.deleteUser(id);
      return ResponseEntity.noContent().build();
   }
}
