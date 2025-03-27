package com.example.web.security.service;


import com.example.web.dto.UserDto;
import com.example.web.model.User;
import com.example.web.repository.UserRepository;
import com.example.web.security.model.Role;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  private final JwtService jwtService;

  private final AuthenticationManager authenticationManager;

  private final BCryptPasswordEncoder bCryptPasswordEncode = new BCryptPasswordEncoder();

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public Optional<User> getUserById(String id) {
    return userRepository.findById(id);
  }

  public User updateUser(String id, User userDetails) {
    return userRepository.findById(id).map(user -> {
      user.setUsername(userDetails.getUsername());
      return userRepository.save(user);
    }).orElseThrow(() -> new RuntimeException("User not found"));
  }

  public void deleteUser(String id) {
    userRepository.deleteById(id);
  }

  public User register(UserDto userDto) {
    User user = User.builder()
        .password(bCryptPasswordEncode.encode(userDto.password()))
        .username(userDto.username())
        .role(Role.ROLE_USER)
        .build();

    userRepository.save(user);
    return user;
  }

  public String verify(User user) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

    return authentication.isAuthenticated() ? jwtService.generateToken(user.getUsername()) : "Failure";
  }
}
