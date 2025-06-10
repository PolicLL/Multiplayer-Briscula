package com.example.web.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static utils.EntityUtils.getConnectedPlayer;
import static utils.EntityUtils.getWebSocketSession;

import com.example.briscula.user.player.RealPlayer;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import com.example.web.model.enums.GameEndStatus;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GameRoomTest {

  @Test
  void testStartGameWithMockedEnterNumber() {
    Set<ConnectedPlayer> playerList = new HashSet<>();

    RealPlayer realPlayer1 = spy(new RealPlayer(null, null, "Player1", getWebSocketSession()));
    doReturn(0).when(realPlayer1).enterNumber();

    RealPlayer realPlayer2 = spy(new RealPlayer(null, null, "Player2", getWebSocketSession()));
    doReturn(0).when(realPlayer2).enterNumber();

    ConnectedPlayer connectedPlayer1 = getConnectedPlayer(realPlayer1);
    ConnectedPlayer connectedPlayer2 = getConnectedPlayer(realPlayer2);

    playerList.add(connectedPlayer1);
    playerList.add(connectedPlayer2);

    GameRoom gameRoom = new GameRoom(playerList, GameOptionNumberOfPlayers.TWO_PLAYERS);

    GameEndStatus gameEndStatus = gameRoom.startGame();

    if (!gameEndStatus.status().equals("NO_WINNER")) {
      ConnectedPlayer player1 = gameRoom.getPlayers().get(0);
      ConnectedPlayer player2 = gameRoom.getPlayers().get(1);

      assertThat(player1.getPlayer().getPoints()).isEqualTo(player2.getPlayer().getPoints());
    }

    else {
      ConnectedPlayer winner = gameEndStatus.winner();

      assertThat(winner).isNotNull()
          .withFailMessage("Game should return a winner after completion.");

      assertThat(winner.getPlayer().getPoints() > 60);

      System.out.println("Winner is: " + winner.getPlayer().getNickname());
    }
  }
}