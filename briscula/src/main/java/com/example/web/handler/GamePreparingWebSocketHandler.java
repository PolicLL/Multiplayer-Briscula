package com.example.web.handler;

import static com.example.web.model.enums.ClientToServerMessageType.JOIN_GAME;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
public class GamePreparingWebSocketHandler extends TextWebSocketHandler {

  private final PrepareGameService prepareGameService;
  @Override
  public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message)
      throws JsonProcessingException {

    String payload = (String) message.getPayload();
    JsonNode json = new ObjectMapper().readTree(payload);

    String type = json.get("type").asText();

    switch (type) {
      case "JOIN_ROOM" -> prepareGameService.handle(session, message);
      case "TEMP" -> prepareGameService.handle(session, message);
    }
  }
}
