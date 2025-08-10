package com.example.web.handler;

import static com.example.web.model.enums.ServerToClientMessageType.USER_ALREADY_IN_GAME_OR_TOURNAMENT;

import com.example.web.dto.Message;
import com.example.web.exception.UserIsAlreadyInTournamentOrGame;
import com.example.web.exception.WebSocketException;
import com.example.web.service.CardsOperationService;
import com.example.web.service.GameEndService;
import com.example.web.service.GamePrepareService;
import com.example.web.service.TournamentService;
import com.example.web.service.WebSocketMessageDispatcher;
import com.example.web.utils.JsonUtils;
import com.example.web.utils.WebSocketMessageReader;
import com.fasterxml.jackson.core.JsonProcessingException;
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

  private final CardsOperationService cardsOperationService;

  private final GameEndService gameEndService;

  private final TournamentService tournamentService;

  private final WebSocketMessageDispatcher messageDispatcher;

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    log.info("New tournament WebSocket connection: {}", session.getId());
  }

  // TODO When game is finished redirect user to the dashboard, anonymous to the home
  @Override
  public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) {
    try {
      log.debug("Handling message type " + WebSocketMessageReader.getMessageType(message));
      switch (WebSocketMessageReader.getMessageType(message)) {
        case LOGGED_IN -> messageDispatcher.registerSession(session);
        case JOIN_ROOM -> gamePrepareService.handle(session, message);
        case GET_INITIAL_CARDS -> cardsOperationService.handleGetCards(session, message);
        case INITIAL_CARDS_RECEIVED -> cardsOperationService.handleGetInitialCards(message);
        case CARD_CHOSEN -> cardsOperationService.handleChosenCard(message);
        case DISCONNECT_FROM_GAME -> gameEndService.handleDisconnectionFromGame(message, session);
        case JOIN_TOURNAMENT -> tournamentService.handle(session, message);
        default -> throw new WebSocketException();
      }
    } catch (UserIsAlreadyInTournamentOrGame e) {
      messageDispatcher.sendMessage(session, JsonUtils.toJson(Message.builder()
          .content(e.getMessage())
          .type(USER_ALREADY_IN_GAME_OR_TOURNAMENT)
          .build()));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) {
    log.error("Tournament WebSocket error:", exception);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    tournamentService.removePlayerWithSession(session);
    messageDispatcher.unregisterSession(session);
    log.info("Tournament WebSocket disconnected: {}", session.getId());
  }
}
