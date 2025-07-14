package com.example.web.service;

import static com.example.web.utils.SecurityUtils.B_CRYPT_PASSWORD_ENCODER;

import com.example.web.dto.user.UpdateUserRequest;
import com.example.web.dto.user.UserDto;
import com.example.web.dto.user.UserLoginDto;
import com.example.web.dto.user.UserStatsDto;
import com.example.web.exception.UserAlreadyExistsException;
import com.example.web.exception.UserNotFoundException;
import com.example.web.mapper.UserMapper;
import com.example.web.model.User;
import com.example.web.repository.UserRepository;
import com.example.web.security.service.JwtService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final PhotoService photoService;

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public UserDto createUser(UserDto userDto) {
    if (userRepository.existsByUsername(userDto.username())) {
      throw new UserAlreadyExistsException("Username is already taken!");
    }

    if (userRepository.existsByEmail(userDto.email())) {
      throw new UserAlreadyExistsException("Email is already taken!");
    }

    if (userDto.photoId() != null && !photoService.existsById(userDto.photoId())) {
      throw new RuntimeException("Photo id does not exists.");
    }

    User userToBeSaved = userMapper.toEntity(userDto);
    userToBeSaved.setPassword(B_CRYPT_PASSWORD_ENCODER.encode(userToBeSaved.getPassword()));

    User savedUser = userRepository.save(userToBeSaved);
    log.info("Saved user {}", savedUser);

    log.info("Test : {}", userMapper.toDto(savedUser));

    return userMapper.toDto(savedUser);
  }

  public List<UserStatsDto> getAllUsers() {
    return userRepository.fetchUserStats().stream()
        .map(userMapper::toUserStats)
        .collect(Collectors.toList());
  }

  public UserDto getUserById(String id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
    return userMapper.toDto(user);
  }

  public UserDto updateUser(String id, UpdateUserRequest userDto) {
    Optional<User> existingUserOptional = userRepository.findById(id);
    if (existingUserOptional.isEmpty())
      throw new UserNotFoundException(id);

    User existingUser = existingUserOptional.get();

    User updatedUser = userMapper.toEntity(userDto);
    updatedUser.setId(id);
    updatedUser.setPassword(existingUser.getPassword());
    updatedUser.setRole(existingUser.getRole());

    return userMapper.toDto(userRepository.save(updatedUser));
  }

  public void updateUserRecord(String userId, boolean isThereWinner, boolean areYouWinner) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    user.setPoints(user.getPoints() + 10);

    userRepository.save(user);
  }

  public void deleteUser(String id) {
    if (!userRepository.existsById(id)) {
      throw new UserNotFoundException(id);
    }
    userRepository.deleteById(id);
  }

  /**
   * Used for verifying user credentials during the login.
   */
  public String verify(UserLoginDto userLoginDto) {
    User existingUser = userRepository.findByUsername(userLoginDto.username());

    if (existingUser == null)
      throw new UserNotFoundException();

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(existingUser.getEmail(), userLoginDto.password()));

    return authentication.isAuthenticated() ? jwtService.generateToken(existingUser.getEmail()) : "Failure";
  }

  public UserDto getUserByUsername(String username) {
    User user = userRepository.findByUsername(username);
    if (user == null) throw new UserNotFoundException();
    return userMapper.toDto(user);
  }
}