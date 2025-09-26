package com.example.validation;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.web.dto.user.UserDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class UserDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Set<String> getMessages(UserDto dto) {
        return validator.validate(dto).stream()
                .map(ConstraintViolation::getMessage)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Test
    void shouldFailWhenUsernameIsBlank() {
        UserDto dto = UserDto.builder()
                .username(" ")
                .password("password123")
                .age(30)
                .country("Italy")
                .email("user@example.com")
                .build();

        Set<String> messages = getMessages(dto);
        assertThat(messages).contains("Username is required.");
    }

    @Test
    void shouldFailWhenUsernameTooShort() {
        UserDto dto = UserDto.builder()
                .username("ab")
                .password("password123")
                .age(30)
                .country("Italy")
                .email("user@example.com")
                .build();

        Set<String> messages = getMessages(dto);
        assertThat(messages).contains("Username must be between 3 and 100 characters.");
    }

    @Test
    void shouldFailWhenPasswordIsBlank() {
        UserDto dto = UserDto.builder()
                .username("validUser")
                .password(" ")
                .age(30)
                .country("Italy")
                .email("user@example.com")
                .build();

        Set<String> messages = getMessages(dto);
        assertThat(messages).contains("Password is required.");
    }

    @Test
    void shouldFailWhenAgeTooLow() {
        UserDto dto = UserDto.builder()
                .username("validUser")
                .password("password")
                .age(2)
                .country("Italy")
                .email("user@example.com")
                .build();

        Set<String> messages = getMessages(dto);
        assertThat(messages).contains("Age must be at least 3.");
    }

    @Test
    void shouldFailWhenAgeTooHigh() {
        UserDto dto = UserDto.builder()
                .username("validUser")
                .password("password")
                .age(101)
                .country("Italy")
                .email("user@example.com")
                .build();

        Set<String> messages = getMessages(dto);
        assertThat(messages).contains("Age must be no more than 100.");
    }

    @Test
    void shouldFailWhenCountryIsBlank() {
        UserDto dto = UserDto.builder()
                .username("validUser")
                .password("password")
                .age(25)
                .country(" ")
                .email("user@example.com")
                .build();

        Set<String> messages = getMessages(dto);
        assertThat(messages).contains("Country is required.");
    }

    @Test
    void shouldFailWhenEmailInvalid() {
        UserDto dto = UserDto.builder()
                .username("validUser")
                .password("password")
                .age(25)
                .country("Italy")
                .email("bad-email")
                .build();

        Set<String> messages = getMessages(dto);
        assertThat(messages).contains("Invalid email format");
    }

    @Test
    void shouldFailWithMultipleErrors() {
        UserDto dto = UserDto.builder()
                .username("ab")
                .password(" ")
                .age(2)
                .country("")
                .email("invalid")
                .build();

        Set<String> messages = getMessages(dto);
        assertThat(messages).containsExactlyInAnyOrder(
                "Username must be between 3 and 100 characters.",
                "Password is required.",
                "Age must be at least 3.",
                "Country is required.",
                "Invalid email format"
        );
    }

    @Test
    void shouldPassWithValidData() {
        UserDto dto = UserDto.builder()
                .username("ValidUser")
                .password("securePass123")
                .age(30)
                .country("France")
                .email("user@example.com")
                .photoId("abc123")
                .jwtToken("token")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }
}