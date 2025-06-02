package com.example.web.dto;

import lombok.Builder;

@Builder
public record UserResponse(
    String id,
    String username,
    int points,
    int level,
    int numberOfPlayedMatches,
    int numberOfWonPlayedMatches) {}
