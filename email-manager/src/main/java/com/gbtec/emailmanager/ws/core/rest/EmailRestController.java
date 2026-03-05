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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gbtec.emailmanager.api.core.service.IEmailService;
import com.gbtec.emailmanager.model.core.dto.EmailDto;
import com.gbtec.emailmanager.model.core.dto.EmailRequestDto;

/**
 * REST API for email management.
 *
 * POST /emails          → bulk insertion
 * GET  /emails          → list all (optional filter by state)
 * GET  /emails/{id}     → get by ID
 * POST /emails/{id}/send → send a draft email (changes state to Sent and dispatches it)
 * PUT  /emails/{id}     → update (Drafts only)
 * DELETE /emails        → bulk deletion (body: list of IDs)
 */
@RestController
@RequestMapping("/emails")
public class EmailRestController {

   @Autowired
   private IEmailService emailService;

   @PostMapping
   public ResponseEntity<List<EmailDto>> insertAll(@RequestBody EmailRequestDto request) {
      return ResponseEntity.status(HttpStatus.CREATED).body(this.emailService.insertAll(request));
   }

   @GetMapping
   public ResponseEntity<List<EmailDto>> findAll(@RequestParam(required = false) Integer state) {
      return ResponseEntity.ok(this.emailService.findAll(state));
   }

   @GetMapping("/{id}")
   public ResponseEntity<EmailDto> findById(@PathVariable Long id) {
      return ResponseEntity.ok(this.emailService.findById(id));
   }

   @PutMapping("/{id}")
   public ResponseEntity<EmailDto> update(@PathVariable Long id, @RequestBody EmailDto dto) {
      return ResponseEntity.ok(this.emailService.update(id, dto));
   }

   @PostMapping("/{id}/send")
   public ResponseEntity<EmailDto> send(@PathVariable Long id) {
      return ResponseEntity.ok(this.emailService.sendEmail(id));
   }

   @DeleteMapping
   public ResponseEntity<Void> deleteAll(@RequestBody List<Long> ids) {
      this.emailService.deleteAll(ids);
      return ResponseEntity.noContent().build();
   }
}
