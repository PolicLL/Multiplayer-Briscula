package com.example.web.controller;

import com.example.web.model.User;
import com.example.web.repository.UserRepository;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000") // Allow React frontend access
public class UserController {

  private final UserRepository userRepository;

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @PostMapping("/create")
  public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
    if (userRepository.existsByUsername(user.getUsername())) {
      return ResponseEntity.badRequest().body("Username is already taken!");
    }
    if (userRepository.existsByEmail(user.getEmail())) {
      return ResponseEntity.badRequest().body("Email is already registered!");
    }
    userRepository.save(user);
    return ResponseEntity.ok("User created successfully!");
  }

  @GetMapping
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }
}
