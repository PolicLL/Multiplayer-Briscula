package com.example.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserDto(
    String id,
    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters.")
    String username,
    @NotBlank(message = "Password is required.")
    String password,
    @NotNull(message = "Age is required.")
    @Min(value = 3, message = "Age must be at least 3.")
    @Max(value = 100, message = "Age must be no more than 100.")
    Integer age,
    @NotBlank(message = "Country is required.")
    String country,
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,
    String photoId
) {}
