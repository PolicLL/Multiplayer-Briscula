package com.example.web.dto.match;

import lombok.Builder;

@Builder
public record MatchDetailsDto(
        String id,
        String userId,
        String matchId,
        int points,
        int numberOfWins,
        int group
) {

}
