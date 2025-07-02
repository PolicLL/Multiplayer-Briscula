package com.example.web.dto.tournament;

import com.example.web.model.enums.TournamentStatus;

public record TournamentResponseDto(
    String id,
    String name,
    int numberOfPlayers,
    TournamentStatus status,
    int roundsToWin
) {}