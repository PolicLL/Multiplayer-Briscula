package com.example.web.dto.tournament;

import jakarta.validation.constraints.NotNull;

public record JoinTournamentReuqest(
    @NotNull String tournamentId,
    @NotNull String userId
) {

}