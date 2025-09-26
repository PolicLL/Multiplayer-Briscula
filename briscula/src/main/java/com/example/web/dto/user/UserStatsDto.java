package com.example.web.dto.user;

public record UserStatsDto(
        String username,
        int points,
        int level,
        long totalMatchesPlayed,
        long totalWins
) {
}
