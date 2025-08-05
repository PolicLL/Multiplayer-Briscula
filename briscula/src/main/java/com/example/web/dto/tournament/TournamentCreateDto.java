package com.example.web.dto.tournament;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record TournamentCreateDto(

    @NotBlank(message = "Name is mandatory")
    String name,

    @Min(value = 2, message = "Number of players must be one of 2, 4, 8, 16, 32")
    @Max(value = 32, message = "Number of players must be one of 4, 8, 16, 32")
    int numberOfPlayers,

    @Min(value = 1, message = "Rounds to win must be between 1 and 4")
    @Max(value = 4, message = "Rounds to win must be between 1 and 4")
    int roundsToWin,

    int numberOfBots
) {}