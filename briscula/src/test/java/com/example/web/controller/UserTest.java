package com.example.web.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.web.model.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.EntityUtils;

public class UserTest {

  private static ValidatorFactory validatorFactory;
  private static Validator validator;

  @BeforeAll
  static void setUp() {
    validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }

  @AfterAll
  static void tearDown() {
    validatorFactory.close();
  }

  @Test
  void testValidUser() {
    User validUser = EntityUtils.generateValidUser();

    Set<ConstraintViolation<User>> violations = validator.validate(validUser);

    assertThat(violations).isEmpty();
  }

  @Test
  void testUsernameIsBlank() {
    User user = EntityUtils.generateValidUser();
    user.setUsername("");

    Set<ConstraintViolation<User>> violations = validator.validate(user);

    assertThat(violations)
        .hasSize(2)
        .anyMatch(v -> v.getMessage().equals("Username is required."))
        .anyMatch(v -> v.getMessage().equals("Username must be between 3 and 100 characters."));
  }

  @Test
  void testUsernameTooShort() {
    User user = EntityUtils.generateValidUser();
    user.setUsername("12");

    Set<ConstraintViolation<User>> violations = validator.validate(user);

    assertThat(violations)
        .hasSize(1)
        .anyMatch(v -> v.getMessage().contains("Username must be between 3 and 100 characters."));
  }

  @Test
  void testInvalidEmail() {
    User user = EntityUtils.generateValidUser();
    user.setEmail("invalid-email");

    Set<ConstraintViolation<User>> violations = validator.validate(user);

    assertThat(violations)
        .hasSize(1)
        .anyMatch(v -> v.getMessage().contains("Invalid email format"));
  }

  @Test
  void testCountryIsBlank() {
    User user = EntityUtils.generateValidUser();
    user.setCountry("");

    Set<ConstraintViolation<User>> violations = validator.validate(user);

    assertThat(violations)
        .hasSize(1)
        .anyMatch(v -> v.getMessage().contains("Country is required."));
  }

  @Test
  void testPointsCannotBeNegative() {
    User user = EntityUtils.generateValidUser();
    user.setPoints(-1);

    Set<ConstraintViolation<User>> violations = validator.validate(user);

    assertThat(violations)
        .hasSize(1)
        .anyMatch(v -> v.getMessage().contains("Points cannot be negative"));
  }

  @Test
  void testLevelMustBeAtLeastOne() {
    User user = EntityUtils.generateValidUser();
    user.setLevel(-1);

    Set<ConstraintViolation<User>> violations = validator.validate(user);

    assertThat(violations)
        .hasSize(1)
        .anyMatch(v -> v.getMessage().contains("Level must be at least 1"));
  }
}
