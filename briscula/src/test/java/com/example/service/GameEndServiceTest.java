package com.example.service;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.EntityUtils.generateValidUserDtoWithoutPhoto;
import static utils.EntityUtils.getConnectedPlayer;

import com.example.web.dto.user.UserDto;
import com.example.web.handler.AbstractIntegrationTest;
import com.example.web.model.ConnectedPlayer;
import com.example.web.model.enums.GameEndStatus;
import com.example.web.model.enums.GameEndStatus.Status;
import com.example.web.service.GameEndService;
import com.example.web.service.UserService;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GameEndServiceTest extends AbstractIntegrationTest {

  @Autowired
  private GameEndService gameEndService;

  @Autowired
  private UserService userService;

  private UserDto userDto;

  @BeforeEach
  void init() {
    userDto = userService.createUser(generateValidUserDtoWithoutPhoto());
  }

  @Test
  void testHandlingOfEndGameNoWinner() {
    ConnectedPlayer connectedPlayer = getConnectedPlayer(userDto.id());
    GameEndStatus gameEndStatus = new GameEndStatus(Map.of(connectedPlayer, false), Status.NO_WINNER);

    int beforeUserPoints = userService.getUserById(userDto.id()).points();

    gameEndService.update(gameEndStatus);

    int updatedUserPoints = userService.getUserById(userDto.id()).points();

    assertThat(beforeUserPoints).isEqualTo(updatedUserPoints);
  }

  @Test
  void testHandlingOfEndGame() {
    ConnectedPlayer connectedPlayer = getConnectedPlayer(userDto.id());
    GameEndStatus gameEndStatus = new GameEndStatus(Map.of(connectedPlayer, true), Status.WINNER_FOUND);

    int beforeUserPoints = userService.getUserById(userDto.id()).points();

    gameEndService.update(gameEndStatus);

    int updatedUserPoints = userService.getUserById(userDto.id()).points();

    assertThat(beforeUserPoints + 10).isEqualTo(updatedUserPoints);
  }

}