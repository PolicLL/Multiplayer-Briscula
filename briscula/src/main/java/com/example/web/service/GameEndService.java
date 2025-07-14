package com.example.web.service;

import static com.example.web.utils.Constants.PLAYER_ID;
import static com.example.web.utils.Constants.ROOM_ID;

import com.example.web.model.ConnectedPlayer;
import com.example.web.model.enums.GameEndStatus;
import com.example.web.model.enums.GameEndStatus.Status;
import com.example.web.utils.WebSocketMessageReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameEndService {

  private final GameRoomService gameRoomService;
  private final UserService userService;

  // TODO -> Has to be tested
  public void update(GameEndStatus gameEndStatus) {
    if (gameEndStatus.status().equals(Status.NO_WINNER)) {
      gameEndStatus.playerResults()
          .keySet()
          .forEach(user -> userService.updateUserRecord(null, false, false));
      return;
    }
    for (Map.Entry<ConnectedPlayer, Boolean> entry : gameEndStatus.playerResults().entrySet()) {
      ConnectedPlayer connectedPlayer = entry.getKey();
      if (connectedPlayer.getUserId() != null) {
        userService.updateUserRecord(connectedPlayer.getUserId(), true, entry.getValue());
      }
    }
  }

  public void handleDisconnectionFromGame(WebSocketMessage<?> message)
      throws JsonProcessingException {
    String roomId = WebSocketMessageReader.getValueFromJsonMessage(message, ROOM_ID);
    int playerId = Integer.parseInt(WebSocketMessageReader.getValueFromJsonMessage(message, PLAYER_ID));

    log.info("Player id={}, left room id={}.", playerId, roomId);

    gameRoomService.getRoom(roomId).notifyPlayerLeft(playerId);
  }
}
