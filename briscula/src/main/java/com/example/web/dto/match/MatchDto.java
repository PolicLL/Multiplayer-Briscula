package com.example.web.dto.match;

import java.util.Set;

public record MatchDto(
    String id,
    int numberOfPlayers,
    int tournamentId,
    Set<String> userIds
) {

}
