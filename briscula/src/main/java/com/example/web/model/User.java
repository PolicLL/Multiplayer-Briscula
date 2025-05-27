package com.example.web.model;

import com.example.web.security.model.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id
  private String id;

  @Enumerated(EnumType.STRING)
  private Role role;

  private String username;

  private String password;

  private int age;

  private String country;

  private String email;

  private int points = 0;

  private int level = 1;

  private String photoId;
}
