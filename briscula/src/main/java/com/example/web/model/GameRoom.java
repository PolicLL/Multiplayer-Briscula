package com.example.web.model;

import com.example.briscula.game.Game;
import com.example.briscula.model.card.Card;
import com.example.briscula.user.player.RealPlayer;
import com.example.briscula.utilities.constants.GameMode;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;

@Data
@Slf4j
@ToString
public class GameRoom {

  private String roomId;
  private final List<ConnectedPlayer> players = new LinkedList<>();
  private final Game game;

  private int playerIndex = 0;

  public GameRoom(Collection<ConnectedPlayer> playerList) {
    this.roomId = UUID.randomUUID().toString().substring(0, 6);

    // TODO: It's not clean, but I will set setting of index like this for now.
    playerList.forEach(player -> {
      player.setId(playerIndex);
      player.setRoomId(roomId);

      if (player.getPlayer() instanceof RealPlayer tempRealPlayer) {
        tempRealPlayer.getRoomPlayerId().setRoomId(roomId);
        tempRealPlayer.getRoomPlayerId().setPlayerId(playerIndex++);
      }

    });

    this.players.addAll(playerList);

    this.game = new Game(GameOptionNumberOfPlayers.THREE_PLAYERS, GameMode.ALL_HUMANS, players);

  }

  public List<Card> getCardsForPlayer(final int playerId) {
    return game.getCardsForPlayer(playerId);
  }

  public void startGame() {
    log.info("Game is staring.");
    while (!game.isGameOver()) {
      game.playRound();
    }
  }

  public void notifyPlayerToChooseCard() {

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
