package com.example.web.dto.tournament;

import jakarta.validation.constraints.NotNull;

public record JoinTournamentRequest(
    @NotNull(message = "Tournament id should not be null.") String tournamentId,
    @NotNull(message = "User id should not be null.") String userId
) {

}