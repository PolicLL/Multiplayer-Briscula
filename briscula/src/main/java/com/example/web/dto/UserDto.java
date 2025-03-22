package com.example.web.dto;

import lombok.Builder;

@Builder
public record UserDto(
    String username,
    int age,
    String country,
    String email
) {}
