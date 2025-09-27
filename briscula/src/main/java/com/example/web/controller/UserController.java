package com.example.web.controller;

import com.example.web.dto.user.UpdateUserRequest;
import com.example.web.dto.user.UserDto;
import com.example.web.dto.user.UserLoginDto;
import com.example.web.dto.user.UserStatsDto;
import com.example.web.exception.BadRequestException;
import com.example.web.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // TODO: Make sure to check size of photo when uploading it

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> createUser(@Valid @ModelAttribute UserDto userDto) {
        log.info("Creating user with username: {}", userDto.username());
        UserDto createdUser = userService.createUser(userDto);
        log.info("User created with ID: {}", createdUser.id());
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginDto userLoginDto) {
        log.info("Login attempt for username: {}", userLoginDto.username());
        String token = userService.verify(userLoginDto);
        log.info("Login successful for username: {}", userLoginDto.username());
        return ResponseEntity.ok(token);
    }

    @GetMapping
    public ResponseEntity<List<UserStatsDto>> getAllUsers(@RequestParam(required = false) String numberOfElements) {
        log.info("Fetching all users");
        List<UserStatsDto> users = userService.getAllUsers(numberOfElements);
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
            throw new BadRequestException();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDto> updateUser(@PathVariable String id, @RequestBody @Valid UpdateUserRequest userDto) {
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

    @PostMapping("/logout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header.");
        }

        String token = authHeader.substring(7);

        try {
            userService.logout(token);
            return ResponseEntity.ok("User logged out successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(CONFLICT).body(e.getMessage());
        }
    }
}
