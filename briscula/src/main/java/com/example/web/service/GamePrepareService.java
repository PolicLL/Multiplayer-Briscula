package com.example.web.service;

import com.example.briscula.user.player.RealPlayer;
import com.example.web.model.ConnectedPlayer;
import com.example.web.model.GameRoom;
import com.example.web.utils.WebSocketMessageReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;


@Slf4j
@Component
@RequiredArgsConstructor
public class GamePrepareService {

  private final Set<ConnectedPlayer> setOfPlayers = new HashSet<>();

  private final GameRoomService gameRoomService;

  private final int MAX_NUMBER_OF_PLAYERS = 2;

  public void handle(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message)
      throws JsonProcessingException {
    ConnectedPlayer connectedPlayer = new ConnectedPlayer(session,
        new RealPlayer(null, WebSocketMessageReader.getValueFromJsonMessage(message, "playerName")));

    if (isThereUserFromThisWebSession(session)) {
      log.info("User {} from this session already joined.", connectedPlayer);
      return;
    }

    setOfPlayers.add(connectedPlayer);

    if (setOfPlayers.size() == MAX_NUMBER_OF_PLAYERS) {
      GameRoom gameRoom = gameRoomService.createRoom(setOfPlayers);

      setOfPlayers.forEach(tempUser -> {
        try {
          log.info("Sending message that game room started with id {}.", gameRoom.getRoomId());
          tempUser.getWebSocketSession().sendMessage(new TextMessage(
              String.format("GAME_STARTED %s %s", gameRoom.getRoomId(), tempUser.getId())));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });

      setOfPlayers.clear();
    }
  }

  private boolean isThereUserFromThisWebSession(WebSocketSession session) {
    return setOfPlayers.stream().anyMatch(
        player -> player.getWebSocketSession().equals(session));
  }

}
