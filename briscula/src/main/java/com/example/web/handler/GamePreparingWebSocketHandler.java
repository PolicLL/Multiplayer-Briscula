package com.example.web.handler;

import com.example.briscula.game.Game;
import com.example.briscula.user.player.RealPlayer;
import com.example.briscula.utilities.constants.GameMode;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import com.example.web.model.ConnectedPlayer;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class GamePreparingWebSocketHandler extends TextWebSocketHandler {

  private static final Set<ConnectedPlayer> setOfPlayers = new HashSet<>();

  private final int MAX_NUMBER_OF_PLAYERS = 2;
  @Override
  public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) {

    String messageName = (String) message.getPayload();

    ConnectedPlayer connectedPlayer = new ConnectedPlayer(session, new RealPlayer(null, messageName));

    if (isThereUserFromThisWebSession(connectedPlayer)) {
      log.info("Session already used.");
      return;
    }

    setOfPlayers.add(connectedPlayer);

    if (setOfPlayers.size() == MAX_NUMBER_OF_PLAYERS) {

      Game game = new Game(GameOptionNumberOfPlayers.TWO_PLAYERS,
          GameMode.ALL_HUMANS, setOfPlayers.stream().map(ConnectedPlayer::getPlayer).toList());

      setOfPlayers.forEach(tempUser -> {
        try {
          log.info("Game can start.");
          tempUser.getWebSocketSession().sendMessage(new TextMessage("Game can start."));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }

  private boolean isThereUserFromThisWebSession(ConnectedPlayer connectedPlayer) {
    return setOfPlayers.stream().anyMatch(
        player -> player.getWebSocketSession().equals(connectedPlayer.getWebSocketSession()));
  }
}
