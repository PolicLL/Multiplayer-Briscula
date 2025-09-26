package com.example.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static utils.EntityUtils.generateValidUserDtoWithoutPhoto;
import static utils.EntityUtils.getConnectedPlayer;
import static utils.EntityUtils.getTournamentName;
import static utils.EntityUtils.getWebSocketSession;

import com.example.web.dto.match.CreateAllStartingMatchesInTournamentDto;
import com.example.web.dto.match.MatchesCreatedResponse;
import com.example.web.dto.tournament.JoinTournamentRequest;
import com.example.web.dto.tournament.TournamentCreateDto;
import com.example.web.dto.tournament.TournamentResponseDto;
import com.example.web.handler.AbstractIntegrationTest;
import com.example.web.model.ConnectedPlayer;
import com.example.web.model.Match;
import com.example.web.model.enums.GameEndStatus;
import com.example.web.model.enums.GameEndStatus.Status;
import com.example.web.service.GameEndService;
import com.example.web.service.MatchService;
import com.example.web.service.TournamentService;
import com.example.web.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@ExtendWith(MockitoExtension.class)
class GameEndServiceTest extends AbstractIntegrationTest {

    @Autowired
    private GameEndService gameEndService;

    @Autowired
    private UserService userService;

    @MockitoSpyBean
    private TournamentService tournamentService;

    @Autowired
    private MatchService matchService;

    private MatchesCreatedResponse matchesCreatedResponse;

    private TournamentResponseDto tournament;
    private final List<String> userIds = new ArrayList<>();
    private final List<ConnectedPlayer> connectedPlayers = new ArrayList<>();

    @BeforeEach
    void init() {

        userIds.clear();
        for (int i = 0; i < 2; ++i) {
            userIds.add(userService.createUser(generateValidUserDtoWithoutPhoto()).id());
        }

        tournament = tournamentService.create(TournamentCreateDto.builder()
                .roundsToWin(2)
                .name(getTournamentName())
                .numberOfPlayers(2)
                .build());

        matchesCreatedResponse = matchService.createMatches(CreateAllStartingMatchesInTournamentDto
                .builder()
                .tournamentId(tournament.id())
                .numberOfPlayers(2)
                .userIds(userIds)
                .build());

        connectedPlayers.clear();
        for (String userId : userIds) {
            tournamentService.joinTournament(JoinTournamentRequest.builder()
                    .tournamentId(tournament.id())
                    .userId(userId)
                    .build(), getWebSocketSession());

            connectedPlayers.add(getConnectedPlayer(userId));
        }
    }

    @Test
    void testHandlingOfEndGameNoWinner() {
        String userId = userIds.get(0);
        ConnectedPlayer connectedPlayer = connectedPlayers.get(0);
        GameEndStatus gameEndStatusNoWinner = new GameEndStatus(Map.of(connectedPlayer, false), Status.NO_WINNER);
        Match match = matchesCreatedResponse.matches().get(0);

        gameEndService.update(gameEndStatusNoWinner, match.getId());

        validateUserPoints(userId, 0);
    }

    @Test
    void testHandlingOfEndGame() {
        Match newMatch = matchesCreatedResponse.matches().get(0);

        ConnectedPlayer connectedPlayer = connectedPlayers.get(0);
        ConnectedPlayer connectedPlayerLoser = connectedPlayers.get(1);
        GameEndStatus gameEndStatus = new GameEndStatus(Map.of(connectedPlayer, true, connectedPlayerLoser, false), Status.WINNER_FOUND);

        gameEndService.update(gameEndStatus, newMatch.getId());

        validateUserPoints(userIds.get(0), 10);
    }

    @Test
    void testMultipleRounds() {
        // given
        ConnectedPlayer connectedPlayerWinner = connectedPlayers.get(0);
        ConnectedPlayer connectedPlayerLoser = connectedPlayers.get(1);

        GameEndStatus gameEndStatusWinnerFirst = new GameEndStatus(Map.of(connectedPlayerWinner, true, connectedPlayerLoser, false), Status.WINNER_FOUND);
        GameEndStatus gameEndStatusWinnerSecond = new GameEndStatus(Map.of(connectedPlayerLoser, true, connectedPlayerWinner, false), Status.WINNER_FOUND);

        // Verify finish tournament is called when enough rounds is won
        gameEndService.update(gameEndStatusWinnerFirst, matchesCreatedResponse.matches().get(0).getId());
        verify(tournamentService, never()).finishTournament(tournament.id(), connectedPlayerWinner, connectedPlayerLoser);

        gameEndService.update(gameEndStatusWinnerSecond, matchesCreatedResponse.matches().get(0).getId());
        verify(tournamentService, never()).finishTournament(tournament.id(), connectedPlayerWinner, connectedPlayerLoser);

        gameEndService.update(gameEndStatusWinnerFirst, matchesCreatedResponse.matches().get(0).getId());
        verify(tournamentService).finishTournament(tournament.id(), connectedPlayerWinner, connectedPlayerLoser);

        validateUserPoints(connectedPlayerWinner.getUserId(), 20);
        validateUserPoints(connectedPlayerLoser.getUserId(), 10);
    }

    private void validateUserPoints(String userId, int expectedPoints) {
        int updatedUserPoints = userService.getUserById(userId).points();
        assertThat(updatedUserPoints).isEqualTo(expectedPoints);
    }

}