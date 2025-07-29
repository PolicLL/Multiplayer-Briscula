package com.example.service;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.EntityUtils.createTournamentCreateDto;
import static utils.EntityUtils.generateValidUserDtoWithoutPhoto;
import static utils.EntityUtils.getConnectedPlayer;
import static utils.EntityUtils.getTournamentName;
import static utils.EntityUtils.getWebSocketSession;

import com.example.web.dto.match.CreateMatchDto;
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
import org.springframework.beans.factory.annotation.Autowired;

class GameEndServiceTest extends AbstractIntegrationTest {

  @Autowired
  private GameEndService gameEndService;

  @Autowired
  private UserService userService;

  @Autowired
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
    Match newMatch = matchService.createMatch(CreateMatchDto.builder()
            .numberOfPlayers(2)
            .tournamentId(tournamentCreateDto.id())
            .userIds(userIds)
        .build());

    ConnectedPlayer connectedPlayer = getConnectedPlayer(userDto.id());
    ConnectedPlayer connectedPlayerLoser = getConnectedPlayer(userDto.id());
    GameEndStatus gameEndStatus = new GameEndStatus(Map.of(connectedPlayer, true, connectedPlayerLoser, false), Status.WINNER_FOUND);

    int beforeUserPoints = userService.getUserById(userDto.id()).points();

    gameEndService.update(gameEndStatus, newMatch.getId());

    int updatedUserPoints = userService.getUserById(userDto.id()).points();

    assertThat(beforeUserPoints + 10).isEqualTo(updatedUserPoints);
  }

}