package com.example.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserDto(
    String id,
    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters.")
    String username,
    int age,
    @NotBlank(message = "Country is required.")
    String country,
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email
) {}
