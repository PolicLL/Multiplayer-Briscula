package com.example.web.dto.tournament;

import com.example.web.model.enums.TournamentStatus;
import java.util.Set;

public record JoinTournamentResponse(
    String id,
    String name,
    int numberOfPlayers,
    TournamentStatus status,
    Set<String> userIds,
    int roundsToWin
) {

}