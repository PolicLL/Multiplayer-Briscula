package com.example.web.handler;

import com.example.web.exception.WebSocketException;
import com.example.web.service.GamePrepareService;
import com.example.web.service.GameStartService;
import com.example.web.utils.WebSocketMessageReader;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
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

    log.info("Handle message.");

    switch (WebSocketMessageReader.getValueFromJsonMessage(message, MESSAGE_TYPE)) {
      case "JOIN_ROOM" -> gamePrepareService.handle(session, message);
      case "GET_INITIAL_CARDS" -> gameStartService.handleGetCards(session, message);
      case "INITIAL_CARDS_RECEIVED" -> gameStartService.handleGetInitialCards(message);
      case "CARD_CHOSEN" -> gameStartService.handleChosenCard(message);
      default -> throw new WebSocketException();
    }
  }

  @OnClose
  public void onClose(WebSocketSession session, CloseReason reason) {
    log.warn("WebSocket CLOSED for session {}: {}", session.getId(), reason);
  }

  @OnError
  public void onError(WebSocketSession session, Throwable throwable) {
    log.error("WebSocket ERROR for session {}", session.getId(), throwable);
  }

}
