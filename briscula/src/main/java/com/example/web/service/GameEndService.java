package com.example.web.service;

import static com.example.web.utils.Constants.PLAYER_ID;
import static com.example.web.utils.Constants.ROOM_ID;

import com.example.web.model.enums.GameEndStatus;
import com.example.web.utils.WebSocketMessageReader;
import com.fasterxml.jackson.core.JsonProcessingException;
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

  public void update(GameEndStatus gameEndStatus) {

  }

  public void handleDisconnectionFromGame(WebSocketMessage<?> message)
      throws JsonProcessingException {
    String roomId = WebSocketMessageReader.getValueFromJsonMessage(message, ROOM_ID);
    int playerId = Integer.parseInt(WebSocketMessageReader.getValueFromJsonMessage(message, PLAYER_ID));

    log.info("Player id={}, left room id={}.", playerId, roomId);

    gameRoomService.getRoom(roomId).notifyPlayerLeft(playerId);
  }
}
