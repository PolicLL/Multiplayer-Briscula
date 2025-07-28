package com.example.web.service;

import static com.example.web.model.enums.ServerToClientMessageType.SENT_INITIAL_CARDS;
import static com.example.web.model.enums.ServerToClientMessageType.SENT_MAIN_CARD;
import static com.example.web.utils.Constants.PLAYER_ID;
import static com.example.web.utils.Constants.ROOM_ID;

import com.example.briscula.model.card.Card;
import com.example.briscula.user.player.RealPlayer;
import com.example.briscula.utilities.constants.CardFormatter;
import com.example.web.dto.Message;
import com.example.web.model.ConnectedPlayer;
import com.example.web.model.GameRoom;
import com.example.web.utils.JsonUtils;
import com.example.web.utils.WebSocketMessageReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@Slf4j
@RequiredArgsConstructor
public class GameStartService {


  private final GameRoomService gameRoomService;
  private final GameEndService gameEndService;

  public void handleGetCards(WebSocketSession session, WebSocketMessage<?> message)
      throws IOException {
    String roomId = WebSocketMessageReader.getValueFromJsonMessage(message, ROOM_ID);
    int playerId = Integer.parseInt(WebSocketMessageReader.getValueFromJsonMessage(message, PLAYER_ID));

    GameRoom gameRoom = gameRoomService.getRoom(roomId);
    List<Card> listCards = gameRoom.getCardsForPlayer(playerId);

    // TODO: Update bad practice of setting new web socket session with different uri here.

    Message sentCardsMessage = new Message(SENT_INITIAL_CARDS,
        roomId, playerId, CardFormatter.formatCards(listCards));

    Message sentMainCardMessage = new Message(SENT_MAIN_CARD,
        roomId, playerId, CardFormatter.formatCard(gameRoom.getMainCard()));

    session.sendMessage(new TextMessage(JsonUtils.toJson(sentCardsMessage)));
    session.sendMessage(new TextMessage(JsonUtils.toJson(sentMainCardMessage)));

    log.info("Handling cards {} {}.", roomId, playerId);
  }

  public synchronized void handleGetInitialCards(WebSocketMessage<?> message)
      throws JsonProcessingException {
    String roomId =  WebSocketMessageReader.getValueFromJsonMessage(message, ROOM_ID);
    gameRoomService.notifyRoomPlayerReceivedInitialCards(
        roomId, WebSocketMessageReader.getValueFromJsonMessage(message, "playerId"));

    log.info("Check handle initial cards for room {}. Value {}.", roomId,
        gameRoomService.areInitialCardsReceived(roomId));

    if (gameRoomService.areInitialCardsReceived(roomId)) {
      log.info("Initial cards for room {} are received.", roomId);
      GameRoom gameRoom = gameRoomService.getRoom(roomId);
      log.info("Starting the game between {} and {}.", gameRoom.getPlayers().get(0).getPlayer().getNickname(),
          gameRoom.getPlayers().get(1).getPlayer().getNickname());
      CompletableFuture
          .supplyAsync(gameRoom::startGame)
          .thenAccept(gameEndStatus -> gameEndService.update(gameEndStatus, gameRoom.getMatchId()))
          .exceptionally(ex -> {
            log.error("Game failed due to error: ", ex);
            return null;
          });

    }
  }

  public void handleChosenCard(WebSocketMessage<?> message) throws JsonProcessingException {
    String roomId =  WebSocketMessageReader.getValueFromJsonMessage(message, ROOM_ID);
    String playerId =  WebSocketMessageReader.getValueFromJsonMessage(message, "playerId");

    int card = Integer.parseInt(WebSocketMessageReader.getValueFromJsonMessage(message, "card"));

    GameRoom gameRoom = gameRoomService.getRoom(roomId);
    ConnectedPlayer connectedPlayer = gameRoom.getGame().getPlayer(Integer.parseInt(playerId));
    if (connectedPlayer.getPlayer() instanceof RealPlayer realPlayer) {
      realPlayer.completeSelectedCard(card);
    }
  }
}
