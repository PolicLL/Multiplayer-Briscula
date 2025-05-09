package com.example.web.service;

import static com.example.web.utils.SecurityUtils.B_CRYPT_PASSWORD_ENCODER;

import com.example.web.dto.UserDto;
import com.example.web.dto.UserLoginDto;
import com.example.web.dto.UserResponse;
import com.example.web.exception.UserAlreadyExistsException;
import com.example.web.exception.UserNotFoundException;
import com.example.web.mapper.UserMapper;
import com.example.web.model.User;
import com.example.web.repository.UserRepository;
import com.example.web.security.service.JwtService;
import java.util.List;
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

    User userToBeSaved = userMapper.toEntity(userDto);
    userToBeSaved.setPassword(B_CRYPT_PASSWORD_ENCODER.encode(userToBeSaved.getPassword()));

    User savedUser = userRepository.save(userToBeSaved);
    log.info("Saved user {}", savedUser);

    log.info("Test : {}", userMapper.toDto(savedUser));

    return userMapper.toDto(savedUser);
  }

  public List<UserResponse> getAllUsers() {
    return userRepository.findAllByOrderByPointsDesc().stream()
        .map(userMapper::toResponse)
        .collect(Collectors.toList());
  }

  public UserDto getUserById(String id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
    return userMapper.toDto(user);
  }

  public UserDto updateUser(String id, UserDto userDto) {
    if (!userRepository.existsById(id))
      throw new UserNotFoundException(id);

    User updatedUser = userMapper.toEntity(userDto);
    updatedUser.setId(id);

    return userMapper.toDto( userRepository.save(updatedUser));
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
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(userLoginDto.username(), userLoginDto.password()));

    return authentication.isAuthenticated() ? jwtService.generateToken(userLoginDto.username()) : "Failure";
  }

  public UserDto getUserByUsername(String username) {
    User user = userRepository.findByUsername(username);
    if (user == null) throw new UserNotFoundException();
    return userMapper.toDto(user);
  }
}