package com.example.web.controller;

import com.example.web.dto.UserDto;
import com.example.web.dto.UserLoginDto;
import com.example.web.dto.UserResponse;
import com.example.web.dto.photo.UploadPhotoDto;
import com.example.web.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  @PostMapping("/create")
  public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto userDto) {
    log.info("Creating user with username: {}", userDto.username());
    UserDto createdUser = userService.createUser(userDto);
    log.info("User created with ID: {}", createdUser.id());
    return ResponseEntity.ok(createdUser);
  }

  @PostMapping("/login")
  public String login(@RequestBody @Valid UserLoginDto userLoginDto) {
    log.info("Login attempt for username: {}", userLoginDto.username());
    String token = userService.verify(userLoginDto);
    log.info("Login successful for username: {}", userLoginDto.username());
    return token;
  }

  @GetMapping
  public ResponseEntity<List<UserResponse>> getAllUsers() {
    log.info("Fetching all users");
    List<UserResponse> users = userService.getAllUsers();
    log.info("Total users fetched: {}", users.size());
    return ResponseEntity.ok(users);
  }

  @GetMapping("/by")
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  public ResponseEntity<UserDto> getUserBy(
      @RequestParam(required = false) String id,
      @RequestParam(required = false) String username) {

    if (id != null) {
      log.info("Fetching user by ID: {}", id);
      return ResponseEntity.ok(userService.getUserById(id));
    } else if (username != null) {
      log.info("Fetching user by username: {}", username);
      return ResponseEntity.ok(userService.getUserByUsername(username));
    } else {
      log.warn("Bad request: both id and username are null");
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<UserDto> updateUser(@PathVariable String id, @RequestBody @Valid UserDto userDto) {
    log.info("Updating user with ID: {}", id);
    UserDto updatedUser = userService.updateUser(id, userDto);
    return ResponseEntity.ok(updatedUser);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<String> deleteUser(@PathVariable String id) {
    log.info("Deleting user with ID: {}", id);
    userService.deleteUser(id);
    log.info("User with ID: {} deleted successfully", id);
    return ResponseEntity.ok("User deleted successfully!");
  }
}
