package com.example.web.dto.match;

import java.util.List;
import lombok.Builder;

@Builder
public record CreateAllStartingMatchesInTournamentDto(
    int numberOfPlayers,
    String tournamentId,
    List<String> userIds
) {

}
