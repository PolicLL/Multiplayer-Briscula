package com.example.web.handler;

import com.example.web.service.GameStartService;
import com.example.web.service.PrepareGameService;
import com.example.web.utils.WebSocketMessageReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

  private final PrepareGameService prepareGameService;

  private final GameStartService gameStartService;
  @Override
  public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message)
      throws JsonProcessingException {

    switch (WebSocketMessageReader.getValueFromJsonMessage(message,"type")) {
      case "JOIN_ROOM" -> prepareGameService.handle(session, message);
      case "READY_FOR_GAME" -> gameStartService.handle(session, message);
    }
  }
}
