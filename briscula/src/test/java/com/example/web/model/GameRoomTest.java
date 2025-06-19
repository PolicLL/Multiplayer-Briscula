package com.example.web.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static utils.EntityUtils.getConnectedPlayer;
import static utils.EntityUtils.getConnectedPlayersBots;
import static utils.EntityUtils.getWebSocketSession;

import com.example.briscula.user.player.RealPlayer;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import com.example.web.model.enums.GameEndStatus;
import com.example.web.model.enums.GameEndStatus.Status;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GameRoomTest {

  @Test
  void testStartGameWithTwoPlayersMockedEnterNumber() {
    Set<ConnectedPlayer> playerList = new HashSet<>();

    RealPlayer realPlayer1 = spy(new RealPlayer(null, "Player1", getWebSocketSession()));
    doReturn(0).when(realPlayer1).enterNumber();

    RealPlayer realPlayer2 = spy(new RealPlayer(null, "Player2", getWebSocketSession()));
    doReturn(0).when(realPlayer2).enterNumber();

    ConnectedPlayer connectedPlayer1 = getConnectedPlayer(realPlayer1);
    ConnectedPlayer connectedPlayer2 = getConnectedPlayer(realPlayer2);

    playerList.add(connectedPlayer1);
    playerList.add(connectedPlayer2);

    GameRoom gameRoom = new GameRoom(playerList, GameOptionNumberOfPlayers.TWO_PLAYERS, true);

    GameEndStatus gameEndStatus = gameRoom.startGame();

    if (gameEndStatus.status().equals(Status.NO_WINNER)) {
      ConnectedPlayer player1 = gameRoom.getPlayers().get(0);
      ConnectedPlayer player2 = gameRoom.getPlayers().get(1);

      assertThat(player1.getPlayer().getPoints()).isEqualTo(player2.getPlayer().getPoints());
    }

    else {
      ConnectedPlayer winner = gameEndStatus.winners().get(0);

      assertThat(winner).isNotNull()
          .withFailMessage("Game should return a winner after completion.");

      assertThat(winner.getPlayer().getPoints() > 60);

      System.out.println("Winner is: " + winner.getPlayer().getNickname());
    }
  }

  @Test
  void testStartGameWithTwoBots() {
    GameRoom gameRoom = new GameRoom(new HashSet<>(Set.of(getConnectedPlayersBots(), getConnectedPlayersBots())),
        GameOptionNumberOfPlayers.TWO_PLAYERS, true);

    GameEndStatus gameEndStatus = gameRoom.startGame();

    if (gameEndStatus.status().equals(Status.NO_WINNER)) {
      ConnectedPlayer player1 = gameRoom.getPlayers().get(0);
      ConnectedPlayer player2 = gameRoom.getPlayers().get(1);

      assertThat(player1.getPlayer().getPoints()).isEqualTo(player2.getPlayer().getPoints());
    }

    else {
      ConnectedPlayer winner = gameEndStatus.winners().get(0);

      assertThat(winner).isNotNull()
          .withFailMessage("Game should return a winner after completion.");

      assertThat(winner.getPlayer().getPoints() > 60);

      System.out.println("Winner is: " + winner.getPlayer().getNickname());
    }
  }
}