package com.example.web.service;

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

  public void handleGetCards(WebSocketSession session, WebSocketMessage<?> message)
      throws IOException {
    String roomId = WebSocketMessageReader.getValueFromJsonMessage(message, ROOM_ID);
    int playerId = Integer.parseInt(WebSocketMessageReader.getValueFromJsonMessage(message, PLAYER_ID));

    GameRoom gameRoom = gameRoomService.getRoom(roomId);
    List<Card> listCards = gameRoom.getCardsForPlayer(playerId);

    List<ConnectedPlayer> players = gameRoom.getPlayers();
    // TODO: Update bad practice of setting new web socket session with different uri here.
     ConnectedPlayer connectedPlayer = players.stream().filter(player -> player.getId() == playerId).findFirst().get();
     if (connectedPlayer.getPlayer() instanceof RealPlayer realPlayer) {
       realPlayer.setWebSocketSession(session);
     }

    Message sentCardsMessage = new Message("SENT_INITIAL_CARDS",
        roomId, playerId, CardFormatter.formatCards(listCards));

    log.info("Sent cards : " + sentCardsMessage);

    session.sendMessage(new TextMessage(JsonUtils.toJson(sentCardsMessage)));

    log.info("Handling cards {} {}.", roomId, playerId);
  }

  public void handleGetInitialCards(WebSocketMessage<?> message)
      throws JsonProcessingException {
    String roomId =  WebSocketMessageReader.getValueFromJsonMessage(message, ROOM_ID);
    gameRoomService.notifyRoomPlayerReceivedInitialCards(
        roomId, WebSocketMessageReader.getValueFromJsonMessage(message, "playerId"));

    if (gameRoomService.areInitialCardsReceived(roomId)) {
      log.info("Initial cards for rom {} are received.", roomId);
      gameRoomService.getRoom(roomId).startGame();
    }
  }

  public void handleChosenCard(WebSocketMessage<?> message) throws JsonProcessingException {
    String roomId =  WebSocketMessageReader.getValueFromJsonMessage(message, ROOM_ID);
    String playerId =  WebSocketMessageReader.getValueFromJsonMessage(message, "playerId");

    log.info("Choosen card.");
    int card = Integer.parseInt(WebSocketMessageReader.getValueFromJsonMessage(message, "card"));
    log.info("Chosen card: " + card);

    // TODO: Something does not work as expected.

    GameRoom gameRoom = gameRoomService.getRoom(roomId);
    ConnectedPlayer connectedPlayer = gameRoom.getGame().getPlayer(Integer.parseInt(playerId));
    if (connectedPlayer.getPlayer() instanceof RealPlayer realPlayer) {
      realPlayer.getSelectedCardFuture().complete(card);
    }
  }
}
