package com.example.web.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  @NotBlank(message = "Username is required.")
  @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters.")
  private String username;

  private int age;

  @NotBlank(message = "Country is required.")
  private String country;

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  @Column(unique = true)
  private String email;

  @Min(value = 0, message = "Points cannot be negative")
  private int points = 0;

  @Min(value = 1, message = "Level must be at least 1")
  private int level = 1;
}
