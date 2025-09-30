package com.example.web.security.service;

import com.example.web.model.User;
import com.example.web.repository.UserRepository;
import com.example.web.security.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email);

        log.info("Loading user by email {}", email);

        if (user == null) {
            log.info("User not found.");
            throw new UsernameNotFoundException("User not found.");
        }

        return new UserPrincipal(user);
    }
}
