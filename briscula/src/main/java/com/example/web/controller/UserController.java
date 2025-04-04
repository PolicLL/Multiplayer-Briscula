package com.example.web.controller;

import com.example.web.dto.UserDto;
import com.example.web.dto.UserLoginDto;
import com.example.web.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  @PostMapping("/create")
  public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto userDto) {
    return ResponseEntity.ok(userService.createUser(userDto));
  }

  @PostMapping("/login")
  public String login(@RequestBody @Valid UserLoginDto userLoginDto) {
    return userService.verify(userLoginDto);
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<UserDto>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @GetMapping("/by")
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  public ResponseEntity<UserDto> getUserBy(
      @RequestParam(required = false) String id,
      @RequestParam(required = false) String username) {

    if (id != null) return ResponseEntity.ok(userService.getUserById(id));
    else if (username != null) return ResponseEntity.ok(userService.getUserByUsername(username));
    else return ResponseEntity.badRequest().build();
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<UserDto> updateUser(@PathVariable String id, @RequestBody @Valid  UserDto userDto) {
    return ResponseEntity.ok(userService.updateUser(id, userDto));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<String> deleteUser(@PathVariable String id) {
    userService.deleteUser(id);
    return ResponseEntity.ok("User deleted successfully!");
  }
}
