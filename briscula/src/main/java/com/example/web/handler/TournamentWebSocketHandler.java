package com.example.web.handler;

import com.example.web.exception.WebSocketException;
import com.example.web.service.TournamentService;
import com.example.web.utils.WebSocketMessageReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class TournamentWebSocketHandler extends TextWebSocketHandler {


  private final TournamentService tournamentService;

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    log.info("New tournament WebSocket connection: {}", session.getId());
  }

  @Override
  public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message)
      throws JsonProcessingException {
    switch (WebSocketMessageReader.getMessageType(message)) {
      case JOIN_TOURNAMENT -> tournamentService.handle(session, message);
      default -> throw new WebSocketException();
    }
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) {
    log.error("Tournament WebSocket error:", exception);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    tournamentService.removePlayerWithSession(session);
    log.info("Tournament WebSocket disconnected: {}", session.getId());
  }

  @Override
  public boolean supportsPartialMessages() {
    return false;
  }

}