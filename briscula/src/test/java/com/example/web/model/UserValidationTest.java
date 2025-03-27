package com.example.web.model;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.EntityUtils.randomAge;
import static utils.EntityUtils.randomCountry;
import static utils.EntityUtils.randomEmail;
import static utils.EntityUtils.randomUsername;

import com.example.web.dto.UserDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.EntityUtils;

public class UserValidationTest {

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
    UserDto validUser = EntityUtils.generateValidUserDto();

    Set<ConstraintViolation<UserDto>> violations = validator.validate(validUser);

    assertThat(violations).isEmpty();
  }

  @Test
  void testUsernameIsBlank() {
    UserDto userDto = UserDto.builder()
        .username("")
        .age(randomAge())
        .country(randomCountry())
        .email(randomEmail())
        .build();

    Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

    assertThat(violations)
        .hasSize(2)
        .anyMatch(v -> v.getMessage().equals("Username is required."))
        .anyMatch(v -> v.getMessage().equals("Username must be between 3 and 100 characters."));
  }

  @Test
  void testUsernameTooShort() {
    UserDto userDto = UserDto.builder()
        .username("12")
        .age(randomAge())
        .country(randomCountry())
        .email(randomEmail())
        .build();

    Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

    assertThat(violations)
        .hasSize(1)
        .anyMatch(v -> v.getMessage().contains("Username must be between 3 and 100 characters."));
  }

  @Test
  void testInvalidEmail() {
    UserDto userDto = UserDto.builder()
        .username(randomUsername())
        .age(randomAge())
        .country(randomCountry())
        .email("invalid-email")
        .build();

    Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

    assertThat(violations)
        .hasSize(1)
        .anyMatch(v -> v.getMessage().contains("Invalid email format"));
  }

  @Test
  void testAgeIsToLow() {
    UserDto userDto = UserDto.builder()
        .username(randomUsername())
        .age(1)
        .country(randomCountry())
        .email(randomEmail())
        .build();

    Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

    assertThat(violations)
        .hasSize(1)
        .anyMatch(v -> v.getMessage().equals("Age must be at least 3."));
  }

  @Test
  void testAgeIsToHigh() {
    UserDto userDto = UserDto.builder()
        .username(randomUsername())
        .age(101)
        .country(randomCountry())
        .email(randomEmail())
        .build();

    Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

    assertThat(violations)
        .hasSize(1)
        .anyMatch(v -> v.getMessage().equals("Age must be no more than 100."));
  }

  @Test
  void testCountryIsBlank() {
    UserDto userDto = UserDto.builder()
        .username(randomUsername())
        .age(randomAge())
        .country("")
        .email(randomEmail())
        .build();

    Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

    assertThat(violations)
        .hasSize(1)
        .anyMatch(v -> v.getMessage().contains("Country is required."));
  }
}
