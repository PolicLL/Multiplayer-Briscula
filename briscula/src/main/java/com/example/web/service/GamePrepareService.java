package com.example.web.service;

import static com.example.web.model.enums.ServerToClientMessageType.GAME_STARTED;

import com.example.briscula.user.player.RealPlayer;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import com.example.web.model.ConnectedPlayer;
import com.example.web.model.GameRoom;
import com.example.web.utils.WebSocketMessageReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
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


  private final Map<Integer, Set<ConnectedPlayer>> mapPreparingPlayers = new HashMap<>();

  {
    mapPreparingPlayers.put(2, new LinkedHashSet<>());
    mapPreparingPlayers.put(3, new LinkedHashSet<>());
    mapPreparingPlayers.put(4, new LinkedHashSet<>());
  }

  private final GameRoomService gameRoomService;


  // TODO: Check -> When I clicked join from the anonymous user twice, it started the game.

  public void handle(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message)
      throws JsonProcessingException {
    ConnectedPlayer connectedPlayer = new ConnectedPlayer(session, new RealPlayer(
        null, WebSocketMessageReader.getValueFromJsonMessage(message, "playerName"), session));

    int numberOfPlayersOption = Integer.parseInt(
        WebSocketMessageReader.getValueFromJsonMessage(message, "numberOfPlayers"));

    if (isThereUserFromThisWebSession(session)) {
      log.info("User {} from this session already joined.", connectedPlayer);
      return;
    }

    mapPreparingPlayers.get(numberOfPlayersOption).add(connectedPlayer);

    if (mapPreparingPlayers.get(numberOfPlayersOption).size() == numberOfPlayersOption) {
      GameRoom gameRoom = gameRoomService.createRoom(mapPreparingPlayers.get(numberOfPlayersOption),
          GameOptionNumberOfPlayers.fromInt(numberOfPlayersOption));

      mapPreparingPlayers.get(numberOfPlayersOption).forEach(tempUser -> {
        try {
          log.info("Sending message that game room started with id {}.", gameRoom.getRoomId());
          tempUser.getWebSocketSession().sendMessage(new TextMessage(
              String.format("%s %s %s", GAME_STARTED, gameRoom.getRoomId(), tempUser.getId())));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });

      mapPreparingPlayers.get(numberOfPlayersOption).clear();
    }
  }

  private boolean isThereUserFromThisWebSession(WebSocketSession session) {
    return mapPreparingPlayers.values().stream()
        .flatMap(Set::stream)
        .anyMatch(player -> player.getWebSocketSession().equals(session));
  }

}
