package com.example.web.controller;

import com.example.web.exception.UserAlreadyExistsException;
import com.example.web.model.User;
import com.example.web.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/create")
  public ResponseEntity<String> createUser(@Valid @RequestBody User user) {
    log.info("Received request to create user: {}", user.getUsername());
    String response = userService.createUser(user);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public List<User> getAllUsers() {
    log.info("Request received to get all users.");
    return userService.getAllUsers();
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
    log.error("User creation error: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(ex.getMessage());
  }
}
