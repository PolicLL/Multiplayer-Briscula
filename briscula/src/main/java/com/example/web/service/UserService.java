package com.example.web.service;

import com.example.web.exception.UserAlreadyExistsException;
import com.example.web.model.User;
import com.example.web.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public String createUser(User user) {
    log.info("Checking if user exists: {}", user.getUsername());

    if (userRepository.existsByUsername(user.getUsername())) {
      log.warn("Username already taken: {}", user.getUsername());
      throw new UserAlreadyExistsException("Username is already taken!");
    }

    if (userRepository.existsByEmail(user.getEmail())) {
      log.warn("Email already registered: {}", user.getEmail());
      throw new UserAlreadyExistsException("Email is already registered!");
    }

    userRepository.save(user);
    log.info("User created successfully: {}", user.getUsername());

    return "User created successfully!";
  }

  public List<User> getAllUsers() {
    log.info("Fetching all users.");
    return userRepository.findAll();
  }
}
