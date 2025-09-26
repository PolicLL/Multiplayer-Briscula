package com.example.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static utils.EntityUtils.getTournamentName;

import com.example.web.dto.tournament.TournamentCreateDto;
import com.example.web.dto.tournament.TournamentResponseDto;
import com.example.web.exception.TooBigNumberOfBotsException;
import com.example.web.handler.AbstractIntegrationTest;
import com.example.web.model.enums.TournamentStatus;
import com.example.web.service.TournamentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TournamentServiceTest extends AbstractIntegrationTest {

    @Autowired
    private TournamentService tournamentService;

    @Test
    void testCreateTournament() {

        TournamentCreateDto tournamentCreateDto = TournamentCreateDto.builder()
                .name("Tournament")
                .numberOfPlayers(4)
                .numberOfBots(2)
                .roundsToWin(2)
                .build();

        TournamentResponseDto tournamentResponseDto = tournamentService.create(tournamentCreateDto);

        assertThat(tournamentResponseDto.status()).isEqualTo(TournamentStatus.INITIALIZING);
        assertThat(tournamentResponseDto.id()).isNotNull();
        assertThat(tournamentResponseDto.name()).isEqualTo(tournamentCreateDto.name());
        assertThat(tournamentResponseDto.numberOfPlayers()).isEqualTo(tournamentCreateDto.numberOfPlayers());
        assertThat(tournamentResponseDto.currentNumberOfPlayers()).isEqualTo(2);
        assertThat(tournamentResponseDto.roundsToWin()).isEqualTo(tournamentCreateDto.roundsToWin());
    }

    @Test
    void testCreateTournamentThrowsTooBigNumberOfBotsException() {
        TournamentCreateDto tournamentCreateDto = TournamentCreateDto.builder()
                .name(getTournamentName())
                .numberOfPlayers(4)
                .numberOfBots(4)
                .roundsToWin(2)
                .build();

        TooBigNumberOfBotsException tooBigNumberOfBotsException = assertThrows(TooBigNumberOfBotsException.class,
                () -> tournamentService.create(tournamentCreateDto));

        assertThat(tooBigNumberOfBotsException.getMessage())
                .isEqualTo("Number of bots 4 is bigger or equal to number of players 4.");
    }

}