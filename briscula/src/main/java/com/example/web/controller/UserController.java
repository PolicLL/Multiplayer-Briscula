package com.example.web.controller;

import com.example.web.model.User;
import com.example.web.repository.UserRepository;
import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000") // Allow React frontend access
public class UserController {

  @Autowired
  private UserRepository userRepository;

  @PostMapping("/create")
  public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
    log.info("Received request to create user: {}", user.getUsername());

    // Check if the username is already taken
    if (userRepository.existsByUsername(user.getUsername())) {
      log.warn("Username already taken: {}", user.getUsername());
      return ResponseEntity.badRequest().body("Username is already taken!");
    }

    // Check if the email is already registered
    if (userRepository.existsByEmail(user.getEmail())) {
      log.warn("Email already registered: {}", user.getEmail());
      return ResponseEntity.badRequest().body("Email is already registered!");
    }

    // Save user if both checks pass
    userRepository.save(user);
    log.info("User created successfully: {}", user.getUsername());

    return ResponseEntity.ok("User created successfully!");
  }

  @GetMapping
  public List<User> getAllUsers() {
    log.info("Request received to get all users.");
    List<User> users = userRepository.findAll();
    log.info("Found {} users.", users.size());
    return users;
  }
}
