package com.example.web.repository;

import com.example.web.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);
  User findByUsername(String username);
  List<User> findAllByOrderByPointsDesc();
}
