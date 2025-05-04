package com.example.web.handler;

import com.example.web.exception.WebSocketException;
import com.example.web.service.GameStartService;
import com.example.web.service.GamePrepareService;
import com.example.web.utils.WebSocketMessageReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
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

  private final GamePrepareService gamePrepareService;

  private final GameStartService gameStartService;

  private static final String MESSAGE_TYPE = "type";

  @Override
  public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message)
      throws IOException {

    switch (WebSocketMessageReader.getValueFromJsonMessage(message, MESSAGE_TYPE)) {
      case "JOIN_ROOM" -> gamePrepareService.handle(session, message);
      case "GET_INITIAL_CARDS" -> gameStartService.handleGetCards(session, message);
      case "INITIAL_CARDS_RECEIVED" -> gameStartService.handleGetInitialCards(message);
      case "READY_FOR_GAME" -> gameStartService.handle(session, message);
      default -> throw new WebSocketException();
    }
  }
}
