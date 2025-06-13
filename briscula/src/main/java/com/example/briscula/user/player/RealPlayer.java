package com.example.briscula.user.player;

import static com.example.briscula.utilities.constants.CardFormatter.formatCard;
import static com.example.web.model.enums.ServerToClientMessageType.CARDS_STATE_UPDATE;
import static com.example.web.model.enums.ServerToClientMessageType.CHOOSE_CARD;
import static com.example.web.model.enums.ServerToClientMessageType.NO_WINNER;
import static com.example.web.model.enums.ServerToClientMessageType.PLAYER_LOST;
import static com.example.web.model.enums.ServerToClientMessageType.PLAYER_WON;
import static com.example.web.model.enums.ServerToClientMessageType.RECEIVED_THROWN_CARD;
import static com.example.web.model.enums.ServerToClientMessageType.REMOVE_CARD;
import static com.example.web.model.enums.ServerToClientMessageType.REMOVE_MAIN_CARD;

import com.example.briscula.model.card.Card;
import com.example.briscula.utilities.constants.CardFormatter;
import com.example.web.dto.Message;
import com.example.web.utils.JsonUtils;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Getter
@Slf4j
public class RealPlayer extends Player {

  @Setter
  private WebSocketSession webSocketSession;
  private final RoomPlayerId roomPlayerId;

  @Setter
  private CompletableFuture<Integer> selectedCardFuture;
  private final int WAITING_SECONDS = 11;

  public RealPlayer(RoomPlayerId roomPlayerId, List<Card> playerCards,
      String nickname, WebSocketSession webSocketSession) {
    super(playerCards, nickname);
    this.webSocketSession = webSocketSession;
    this.roomPlayerId = new RoomPlayerId();
  }

  @Override
  public Card playRound() {
    printInstructions();
    int numberInput = enterNumber();
    return playerCards.remove(numberInput);
  }

  public void sentMessageAboutNewCardsAndPoints() {


    CompletableFuture<Void> pauseFuture = CompletableFuture
        .runAsync(() -> {}, CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS));


    pauseFuture.thenRun(() -> {
      Message sentCardsMessage = new Message(CARDS_STATE_UPDATE,
          roomPlayerId.getRoomId(), roomPlayerId.getPlayerId(),
          CardFormatter.formatTemporaryPlayerState(this.playerCards, this.points));

      log.info("Sent cards : " + sentCardsMessage);

      try {
        webSocketSession.sendMessage(new TextMessage(JsonUtils.toJson(sentCardsMessage)));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });

  }

  private void printInstructions() {
    try {

      Message sentCardsMessage = new Message(CHOOSE_CARD, roomPlayerId.getRoomId(),
          roomPlayerId.getPlayerId(), "Choose your card.");

      log.info("Sent message to player to choose card. roomId = {}, playerId = {}", roomPlayerId.getRoomId(), roomPlayerId.getPlayerId());

      webSocketSession.sendMessage(new TextMessage(JsonUtils.toJson(sentCardsMessage)));

    } catch (IOException e) {
      log.info(String.valueOf(e));
      throw new RuntimeException(e);
    }
    for (int i = 0; i < playerCards.size(); ++i) {
      log.info(i + " " + playerCards.get(i));
    }
  }

  public int enterNumber() {
    selectedCardFuture = new CompletableFuture<>();
    try {
      return selectedCardFuture.get(WAITING_SECONDS, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
      log.warn("Player did not respond in time. Proceeding with default choice.");

      Message sentCardsMessage = new Message(CHOOSE_CARD, roomPlayerId.getRoomId(),
          roomPlayerId.getPlayerId(), "");

      Message removeCardMessage = new Message(REMOVE_CARD, roomPlayerId.getRoomId(),
          roomPlayerId.getPlayerId(), "0");

      try {
        webSocketSession.sendMessage(new TextMessage(JsonUtils.toJson(sentCardsMessage)));
        webSocketSession.sendMessage(new TextMessage(JsonUtils.toJson(removeCardMessage)));
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }

      return 0;
    } catch (Exception e) {
      throw new RuntimeException("Failed to receive input", e);
    }
  }

  public void completeSelectedCard(int selectedCardIndex) {
    selectedCardFuture.complete(selectedCardIndex);
  }

  public void sentMessageAboutRemovingMainCard() {
    try {

      Message sentCardsMessage = new Message(REMOVE_MAIN_CARD, roomPlayerId.getRoomId(),
          roomPlayerId.getPlayerId(), "Removing the main card.");

      webSocketSession.sendMessage(new TextMessage(JsonUtils.toJson(sentCardsMessage)));
    } catch (IOException e) {
      log.info(String.valueOf(e));
      throw new RuntimeException(e);
    }

  }

  public void sentLoosingMessage() {
    try {

      Message sentCardsMessage = new Message(PLAYER_LOST, roomPlayerId.getRoomId(),
          roomPlayerId.getPlayerId(), "Lost.");

      webSocketSession.sendMessage(new TextMessage(JsonUtils.toJson(sentCardsMessage)));
    } catch (IOException e) {
      log.info(String.valueOf(e));
      throw new RuntimeException(e);
    }
  }

  public void sentWinningMessage() {
    try {

      Message sentCardsMessage = new Message(PLAYER_WON, roomPlayerId.getRoomId(),
          roomPlayerId.getPlayerId(), "Win.");

      webSocketSession.sendMessage(new TextMessage(JsonUtils.toJson(sentCardsMessage)));
    } catch (IOException e) {
      log.info(String.valueOf(e));
      throw new RuntimeException(e);
    }
  }

  public void setNoWinnerMessage() {
    try {

      Message sentCardsMessage = new Message(NO_WINNER, roomPlayerId.getRoomId(),
          roomPlayerId.getPlayerId(), "No winner.");

      webSocketSession.sendMessage(new TextMessage(JsonUtils.toJson(sentCardsMessage)));
    } catch (IOException e) {
      log.info(String.valueOf(e));
      throw new RuntimeException(e);
    }
  }

  public void sentMessageAboutNewCardFromAnotherPlayer(Card card) {
    try {
      Message sentCardsMessage = new Message(RECEIVED_THROWN_CARD, roomPlayerId.getRoomId(),
          roomPlayerId.getPlayerId(), formatCard(card));

      webSocketSession.sendMessage(new TextMessage(JsonUtils.toJson(sentCardsMessage)));
    } catch (IOException e) {
      log.info(String.valueOf(e));
      throw new RuntimeException(e);
    }
  }
}
