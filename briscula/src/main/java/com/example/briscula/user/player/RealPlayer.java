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
import static com.example.web.utils.WebSocketMessageSender.sendMessage;

import com.example.briscula.configuration.BrisculaConfig;
import com.example.briscula.model.card.Card;
import com.example.briscula.utilities.constants.CardFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

@Getter
@Slf4j
public class RealPlayer extends Player {

  @Setter
  private WebSocketSession webSocketSession;
  private final RoomPlayerId roomPlayerId;

  @Setter
  private CompletableFuture<Integer> selectedCardFuture;

  @Setter
  private int waitingTimeForChoosingCardInSeconds = BrisculaConfig.getWaitingTimeStatic();

  public RealPlayer(List<Card> playerCards,
      String nickname, WebSocketSession webSocketSession) {
    super(playerCards, nickname);
    this.webSocketSession = webSocketSession;
    this.roomPlayerId = new RoomPlayerId();
  }

  public RealPlayer(RoomPlayerId roomPlayerId, List<Card> playerCards,
      String nickname, WebSocketSession webSocketSession) {
    super(playerCards, nickname);
    this.webSocketSession = webSocketSession;
    this.roomPlayerId = roomPlayerId;
  }

  @Override
  public Card playRound() {
    printInstructions();
    System.out.println("ENTER NUMBER.");
    int numberInput = enterNumber();
    return playerCards.remove(numberInput);
  }

  public CompletableFuture<Void> sentMessageAboutNewCardsAndPoints(boolean showPoints) {
    return CompletableFuture
        .runAsync(() -> {}, CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS))
        .thenRun(() -> {
          String formattedState = showPoints
              ? CardFormatter.formatTemporaryPlayerState(this.playerCards, this.points)
              : CardFormatter.formatTemporaryPlayerState(this.playerCards, -1);
          log.info("Sent cards state update");
          sendMessage(webSocketSession, CARDS_STATE_UPDATE, roomPlayerId.getRoomId(), roomPlayerId.getPlayerId(), formattedState);
        });
  }

  private void printInstructions() {
    log.info("Sent message to player to choose card. roomId = {}, playerId = {}", roomPlayerId.getRoomId(), roomPlayerId.getPlayerId());
    sendMessage(webSocketSession, CHOOSE_CARD, roomPlayerId.getRoomId(), roomPlayerId.getPlayerId(), "Choose your card.");
    for (int i = 0; i < playerCards.size(); ++i) {
      log.info(i + " " + playerCards.get(i));
    }
  }

  public int enterNumber() {
    selectedCardFuture = new CompletableFuture<>();
    try {
      return selectedCardFuture.get(waitingTimeForChoosingCardInSeconds, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
      log.warn("Player did not respond in time. Proceeding with default choice.");
      sendMessage(webSocketSession, CHOOSE_CARD, roomPlayerId.getRoomId(), roomPlayerId.getPlayerId(), "");
      sendMessage(webSocketSession, REMOVE_CARD, roomPlayerId.getRoomId(), roomPlayerId.getPlayerId(), "0");
      return 0;
    } catch (Exception e) {
      throw new RuntimeException("Failed to receive input", e);
    }
  }

  public void completeSelectedCard(int selectedCardIndex) {
    selectedCardFuture.complete(selectedCardIndex);
  }

  public void sentMessageAboutRemovingMainCard() {
    sendMessage(webSocketSession, REMOVE_MAIN_CARD, roomPlayerId.getRoomId(), roomPlayerId.getPlayerId(), "Removing the main card.");
  }

  public void sentLoosingMessage() {
    sendMessage(webSocketSession, PLAYER_LOST, roomPlayerId.getRoomId(), roomPlayerId.getPlayerId(), "Lost.");
  }

  public void sentWinningMessage() {
    sendMessage(webSocketSession, PLAYER_WON, roomPlayerId.getRoomId(), roomPlayerId.getPlayerId(), "Win.");
  }

  public void setNoWinnerMessage() {
    sendMessage(webSocketSession, NO_WINNER, roomPlayerId.getRoomId(), roomPlayerId.getPlayerId(), "No winner.");
  }

  public void sentMessageAboutNewCardFromAnotherPlayer(Card card) {
    sendMessage(webSocketSession, RECEIVED_THROWN_CARD, roomPlayerId.getRoomId(), roomPlayerId.getPlayerId(), formatCard(card));
  }
}
