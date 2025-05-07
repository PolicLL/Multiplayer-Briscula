package com.example.briscula.user.player;

import com.example.briscula.model.card.Card;
import com.example.briscula.utilities.constants.CardFormatter;
import com.example.web.dto.Message;
import com.example.web.utils.JsonUtils;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
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

  private CompletableFuture<Integer> selectedCardFuture;



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

  private void printInstructions() {
    try {

      Message sentCardsMessage = new Message("CHOOSE_CARD", roomPlayerId.getRoomId(),
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

  private int enterNumber() {
    selectedCardFuture = new CompletableFuture<>();
    try {
      return selectedCardFuture.get(60, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
      log.warn("Player did not respond in time. Proceeding with default choice.");
      return 0; // or random card, or throw exception if needed
    } catch (Exception e) {
      throw new RuntimeException("Failed to receive input", e);
    }
  }


  private boolean isNumberOfCardOutOfRange(int numberInput) {
    return numberInput < 0 || numberInput >= playerCards.size();
  }
}
