package com.example.web.service;

import static com.example.web.model.enums.ServerToClientMessageType.GAME_STARTED;
import static com.example.web.utils.WebSocketMessageSender.sendMessage;

import com.example.briscula.user.player.RealPlayer;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import com.example.web.model.ConnectedPlayer;
import com.example.web.model.GameRoom;
import com.example.web.utils.WebSocketMessageReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
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
    boolean shouldPointsShow = Boolean.parseBoolean(
        WebSocketMessageReader.getValueFromJsonMessage(message, "shouldShowPoints"));
    int numberOfPlayersOption = Integer.parseInt(
        WebSocketMessageReader.getValueFromJsonMessage(message, "numberOfPlayers"));

    String userId =  WebSocketMessageReader.getValueFromJsonMessage(message, "userId");

    if (!mapPreparingPlayers.containsKey(numberOfPlayersOption)) {
      log.warn("Invalid numberOfPlayers value: {}", numberOfPlayersOption);
      return;
    }

    synchronized (mapPreparingPlayers) {
      ConnectedPlayer connectedPlayer = new ConnectedPlayer(session, new RealPlayer(
          null, playerName, session), shouldPointsShow);
      connectedPlayer.setUserId(userId);

      Set<ConnectedPlayer> waitingPlayers = mapPreparingPlayers.get(numberOfPlayersOption);

      if (!waitingPlayers.add(connectedPlayer)) {
        throw new RuntimeException("User already entered this game.");
      }

      if (waitingPlayers.size() == numberOfPlayersOption) {
        ConnectedPlayer firstPlayer = waitingPlayers.iterator().next();

        GameRoom gameRoom = gameRoomService.createRoom(waitingPlayers,
            GameOptionNumberOfPlayers.fromInt(numberOfPlayersOption), firstPlayer.isDoesWantPointsToShow());

        waitingPlayers.forEach(tempUser -> {
          log.info("Sending message that game room started with id {}.", gameRoom.getRoomId());
          sendMessage(tempUser.getWebSocketSession(), GAME_STARTED, gameRoom.getRoomId(), tempUser.getId());
        });

        waitingPlayers.clear();
      }
    }
  }
}
