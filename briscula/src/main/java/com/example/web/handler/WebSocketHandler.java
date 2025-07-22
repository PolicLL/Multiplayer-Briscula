package com.example.web.handler;

import com.example.web.exception.WebSocketException;
import com.example.web.service.GameEndService;
import com.example.web.service.GamePrepareService;
import com.example.web.service.GameStartService;
import com.example.web.service.TournamentService;
import com.example.web.utils.WebSocketMessageReader;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

  private final GamePrepareService gamePrepareService;

  private final GameStartService gameStartService;

  private final GameEndService gameEndService;

  private final TournamentService tournamentService;

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    log.info("New tournament WebSocket connection: {}", session.getId());
  }

  @Override
  public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message)
      throws IOException {
    log.info("Handling message type " + WebSocketMessageReader.getMessageType(message));
    switch (WebSocketMessageReader.getMessageType(message)) {
      case JOIN_ROOM -> gamePrepareService.handle(session, message);
      case GET_INITIAL_CARDS -> gameStartService.handleGetCards(session, message);
      case INITIAL_CARDS_RECEIVED -> gameStartService.handleGetInitialCards(message);
      case CARD_CHOSEN -> gameStartService.handleChosenCard(message);
      case DISCONNECT_FROM_GAME -> gameEndService.handleDisconnectionFromGame(message);
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
}
