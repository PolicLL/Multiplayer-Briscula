package com.example.web.service;

import com.example.web.dto.UserDto;
import com.example.web.exception.UserAlreadyExistsException;
import com.example.web.exception.UserNotFoundException;
import com.example.web.mapper.UserMapper;
import com.example.web.model.User;
import com.example.web.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Transactional
  public UserDto createUser(UserDto userDto) {
    if (userRepository.existsByUsername(userDto.username())) {
      throw new UserAlreadyExistsException("Username is already taken!");
    }

    if (userRepository.existsByEmail(userDto.email())) {
      throw new UserAlreadyExistsException("Email is already taken!");
    }

    User savedUser = userRepository.save(userMapper.toEntity(userDto));
    log.info("Saved user {}", savedUser);

    log.info("Test : {}", userMapper.toDto(savedUser));

    return userMapper.toDto(savedUser);
  }

  public List<UserDto> getAllUsers() {
    return userRepository.findAll().stream()
        .map(userMapper::toDto)
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
}