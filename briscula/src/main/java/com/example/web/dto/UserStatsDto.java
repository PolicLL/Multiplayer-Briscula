package com.example.web.dto;

public record UserStatsDto(
    String username,
    int points,
    int level,
    long totalMatchesPlayed,
    long totalWins
) {}
