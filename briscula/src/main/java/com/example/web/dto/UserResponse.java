package com.example.web.dto;

import lombok.Builder;

@Builder
public record UserResponse(
    String id,
    String username,
    String password,
    Integer age,
    String country,
    String email,
    int points,
    int level
) {}
