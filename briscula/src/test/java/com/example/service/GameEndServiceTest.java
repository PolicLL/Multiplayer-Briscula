package com.example.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static utils.EntityUtils.createTournamentCreateDto;
import static utils.EntityUtils.generateValidUserDtoWithoutPhoto;
import static utils.EntityUtils.getConnectedPlayer;
import static utils.EntityUtils.getTournamentName;
import static utils.EntityUtils.getWebSocketSession;

import com.example.web.dto.match.CreateAllStartingMatchesInTournamentDto;
import com.example.web.dto.match.CreateMatchDto;
import com.example.web.dto.match.MatchesCreatedResponse;
import com.example.web.dto.tournament.JoinTournamentRequest;
import com.example.web.dto.tournament.TournamentCreateDto;
import com.example.web.dto.tournament.TournamentResponseDto;
import com.example.web.dto.user.UserDto;
import com.example.web.handler.AbstractIntegrationTest;
import com.example.web.model.ConnectedPlayer;
import com.example.web.model.Match;
import com.example.web.model.enums.GameEndStatus;
import com.example.web.model.enums.GameEndStatus.Status;
import com.example.web.model.enums.TournamentStatus;
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

  private UserDto userDto;

  private Match match;

  private TournamentResponseDto tournament;
  private final List<String> userIds = new ArrayList<>();

  @BeforeEach
  void init() {

    userDto = userService.createUser(generateValidUserDtoWithoutPhoto());

    if (userIds.isEmpty()) {
      for (int i = 0; i < 2; ++i) {
        userIds.add(userService.createUser(generateValidUserDtoWithoutPhoto()).id());
      }
    }

    tournament = tournamentService.create(TournamentCreateDto.builder()
            .roundsToWin(1)
            .status(TournamentStatus.INITIALIZING)
            .name(getTournamentName())
            .numberOfPlayers(2)
        .build());

    match = matchService.createMatch(CreateMatchDto.builder()
            .userIds(userIds)
            .tournamentId(tournament.id())
            .numberOfPlayers(3)
        .build());

    for (String userId : userIds) {
      tournamentService.joinTournament(JoinTournamentRequest.builder()
              .tournamentId(tournament.id())
              .userId(userId)
          .build(), getWebSocketSession());
    }
  }

  @Test
  void testHandlingOfEndGameNoWinner() {
    ConnectedPlayer connectedPlayer = getConnectedPlayer(userDto.id());
    GameEndStatus gameEndStatus = new GameEndStatus(Map.of(connectedPlayer, false), Status.NO_WINNER);

    int beforeUserPoints = userService.getUserById(userDto.id()).points();

    gameEndService.update(gameEndStatus, match.getId());

    int updatedUserPoints = userService.getUserById(userDto.id()).points();

    assertThat(beforeUserPoints).isEqualTo(updatedUserPoints);
  }

  @Test
  void testHandlingOfEndGame() {
    TournamentResponseDto tournamentCreateDto =  tournamentService.create(createTournamentCreateDto());

    MatchesCreatedResponse matchesCreatedResponse = matchService.createMatches(
        CreateAllStartingMatchesInTournamentDto.builder()
            .numberOfPlayers(2)
            .tournamentId(tournamentCreateDto.id())
            .userIds(userIds)
            .build());

    Match newMatch = matchesCreatedResponse.matches().get(0);

    ConnectedPlayer connectedPlayer = getConnectedPlayer(userIds.get(0));
    ConnectedPlayer connectedPlayerLoser = getConnectedPlayer(userIds.get(1));
    GameEndStatus gameEndStatus = new GameEndStatus(Map.of(connectedPlayer, true, connectedPlayerLoser, false), Status.WINNER_FOUND);

    gameEndService.update(gameEndStatus, newMatch.getId());

    validateUserPoints(userIds.get(0), 10);
  }


  // TODO I would like to test functionality related to GameEndService and make sure that is handled correctly
  // I mean logic related to case when there are multiple rounds

  @Test
  void testMultipleRounds() {
    // given
    List<String> newUsersIds = new ArrayList<>();
    for (int i = 0; i < 2; ++i) {
      newUsersIds.add(userService.createUser(generateValidUserDtoWithoutPhoto()).id());
    }

    TournamentResponseDto newTournament = tournamentService.create(TournamentCreateDto.builder()
        .roundsToWin(2)
        .status(TournamentStatus.INITIALIZING)
        .name(getTournamentName())
        .numberOfPlayers(2)
        .build());

    MatchesCreatedResponse matchesCreatedResponse = matchService.createMatches(CreateAllStartingMatchesInTournamentDto
        .builder()
            .tournamentId(newTournament.id())
            .numberOfPlayers(2)
            .userIds(newUsersIds)
        .build());

    for (String userId : newUsersIds) {
      tournamentService.joinTournament(JoinTournamentRequest.builder()
          .tournamentId(newTournament.id())
          .userId(userId)
          .build(), getWebSocketSession());
    }

    ConnectedPlayer connectedPlayerWinner = getConnectedPlayer(newUsersIds.get(0));
    ConnectedPlayer connectedPlayerLoser = getConnectedPlayer(newUsersIds.get(1));

    GameEndStatus gameEndStatusWinnerFirst = new GameEndStatus(Map.of(connectedPlayerWinner, true, connectedPlayerLoser, false), Status.WINNER_FOUND);
    GameEndStatus gameEndStatusWinnerSecond = new GameEndStatus(Map.of(connectedPlayerLoser, true, connectedPlayerWinner, false), Status.WINNER_FOUND);

    // Verify finish tournament is called when enough rounds is won
    gameEndService.update(gameEndStatusWinnerFirst, matchesCreatedResponse.matches().get(0).getId());
    verify(tournamentService, never()).finishTournament(newTournament.id(), connectedPlayerWinner, connectedPlayerLoser);

    gameEndService.update(gameEndStatusWinnerSecond, matchesCreatedResponse.matches().get(0).getId());
    verify(tournamentService, never()).finishTournament(newTournament.id(), connectedPlayerWinner, connectedPlayerLoser);

    gameEndService.update(gameEndStatusWinnerFirst, matchesCreatedResponse.matches().get(0).getId());
    verify(tournamentService).finishTournament(newTournament.id(), connectedPlayerWinner, connectedPlayerLoser);

    validateUserPoints(connectedPlayerWinner.getUserId(), 20);
    validateUserPoints(connectedPlayerLoser.getUserId(), 10);
  }

  private void validateUserPoints(String userId, int expectedPoints) {
    int updatedUserPoints = userService.getUserById(userId).points();
    assertThat(updatedUserPoints).isEqualTo(expectedPoints);
  }

}