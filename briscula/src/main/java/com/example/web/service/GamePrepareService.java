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

    String playerName = WebSocketMessageReader.getValueFromJsonMessage(message, "playerName");
    int numberOfPlayersOption = Integer.parseInt(
        WebSocketMessageReader.getValueFromJsonMessage(message, "numberOfPlayers"));

    if (!mapPreparingPlayers.containsKey(numberOfPlayersOption)) {

      log.warn("Invalid numberOfPlayers value: {}", numberOfPlayersOption);
      return;
    }

    synchronized (mapPreparingPlayers) {
      ConnectedPlayer connectedPlayer = new ConnectedPlayer(session, new RealPlayer(
          null, playerName, session));

      Set<ConnectedPlayer> waitingPlayers = mapPreparingPlayers.get(numberOfPlayersOption);
      waitingPlayers.add(connectedPlayer);

      if (waitingPlayers.size() == numberOfPlayersOption) {
        GameRoom gameRoom = gameRoomService.createRoom(waitingPlayers,
            GameOptionNumberOfPlayers.fromInt(numberOfPlayersOption));

        waitingPlayers.forEach(tempUser -> {
          try {
            log.info("Sending message that game room started with id {}.", gameRoom.getRoomId());
            tempUser.getWebSocketSession().sendMessage(new TextMessage(
                String.format("%s %s %s", GAME_STARTED, gameRoom.getRoomId(), tempUser.getId())));
          } catch (IOException e) {
            log.error("Error sending game start message", e);
          }
        });

      waitingPlayers.clear();
    }
  }
  }
}
