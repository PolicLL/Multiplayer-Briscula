package com.example.web.model;

import com.example.briscula.game.Game;
import com.example.briscula.utilities.constants.GameMode;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;

@Data
@Slf4j
@ToString
public class GameRoom {

  private String roomId;
  private final List<ConnectedPlayer> players = new LinkedList<>();
  private final Game game;

  public GameRoom(Collection<ConnectedPlayer> playerList) {
    this.roomId = UUID.randomUUID().toString().substring(0, 6);
    this.players.addAll(playerList);
    this.game = new Game(GameOptionNumberOfPlayers.TWO_PLAYERS, GameMode.ALL_HUMANS,
        players.stream().map(ConnectedPlayer::getPlayer).toList());

    //startGame();
  }

  private void startGame() {
    sendMessage(players.get(0), game.getCardsForPlayer(0).toString());
  }

  public void sendMessage(int playerIndex, String message) {
    try {
      players.get(playerIndex).getWebSocketSession().sendMessage(new TextMessage("SERVER " + message));
      log.info("Sending message {} to web socket {}", message, players.get(playerIndex).getWebSocketSession());
    } catch (IOException e) {
      throw new RuntimeException("Failed to send message", e);
    }
  }

  public void sendMessage(ConnectedPlayer player, String message) {
    try {
      player.getWebSocketSession().sendMessage(new TextMessage("SERVER " + message));
      log.info("Sending message {} to web socket {}", message, player.getWebSocketSession());
    } catch (IOException e) {
      throw new RuntimeException("Failed to send message", e);
    }
  }
}
