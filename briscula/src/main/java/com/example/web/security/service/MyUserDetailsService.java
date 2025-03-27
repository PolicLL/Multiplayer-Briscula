package com.example.web.security.service;

import com.example.web.model.User;
import com.example.web.repository.UserRepository;
import com.example.web.security.model.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MyUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);

    log.info("Loading user by username {}", username);

    if (user == null) {
      log.info("User not found.");
      throw new UsernameNotFoundException("User not found.");
    }

    return new UserPrincipal(user);
  }
}
