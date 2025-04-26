package com.example.web.service;

import com.example.web.dto.Message;
import com.example.web.model.GameRoom;
import com.example.web.utils.JsonUtils;
import com.example.web.utils.WebSocketMessageReader;
import java.io.IOException;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@Slf4j
@RequiredArgsConstructor
public class GameStartService {

  private final GameRoomService gameRoomService;

  public void handle(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) {
    log.info("Game start.");
  }

  public void handleGetCards(WebSocketSession session, WebSocketMessage<?> message)
      throws IOException {
    String roomId = WebSocketMessageReader.getValueFromJsonMessage(message, "roomId");
    String playerId = WebSocketMessageReader.getValueFromJsonMessage(message, "playerId");

    GameRoom gameRoom = gameRoomService.getRoom(roomId);
    //gameRoom.getCardsForPlayer(playerId);

    Message sentCardsMessage = new Message("SENT_CARDS", roomId, playerId,
        "D7 C2 B3");

    log.info("Sent cards : " + sentCardsMessage);

    session.sendMessage(new TextMessage(JsonUtils.toJson(sentCardsMessage)));

    log.info("Handling cards {} {}.", roomId, playerId);
  }
}
