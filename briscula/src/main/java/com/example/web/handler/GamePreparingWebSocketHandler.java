package com.example.web.handler;

import com.example.briscula.user.player.RealPlayer;
import com.example.web.model.ConnectedPlayer;
import com.example.web.service.GameRoomService;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
public class GamePreparingWebSocketHandler extends TextWebSocketHandler {

  private final Set<ConnectedPlayer> setOfPlayers = new HashSet<>();

  private final GameRoomService gameRoomService;

  private final int MAX_NUMBER_OF_PLAYERS = 2;
  @Override
  public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) {
    ConnectedPlayer connectedPlayer = new ConnectedPlayer(session,
        new RealPlayer(null, (String) message.getPayload()));

    if (isThereUserFromThisWebSession(connectedPlayer)) {
      log.info("User from this session already joined.");
      return;
    }

    setOfPlayers.add(connectedPlayer);
    log.info("Added player {}.", connectedPlayer);

    if (setOfPlayers.size() == MAX_NUMBER_OF_PLAYERS) {
      setOfPlayers.forEach(tempUser -> {
        try {
          tempUser.getWebSocketSession().sendMessage(new TextMessage("START_ENABLED"));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });

      log.info("Create room {}.",  gameRoomService.createRoom(setOfPlayers));
    }
  }

  private boolean isThereUserFromThisWebSession(ConnectedPlayer connectedPlayer) {
    return setOfPlayers.stream().anyMatch(
        player -> player.getWebSocketSession().equals(connectedPlayer.getWebSocketSession()));
  }
}
