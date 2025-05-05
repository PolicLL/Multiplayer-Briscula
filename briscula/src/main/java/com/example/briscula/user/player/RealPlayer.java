package com.example.briscula.user.player;

import com.example.briscula.model.card.Card;
import com.example.briscula.utilities.constants.CardFormatter;
import com.example.web.dto.Message;
import com.example.web.utils.JsonUtils;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Getter
@Slf4j
public class RealPlayer extends Player {

  private final WebSocketSession webSocketSession;
  private final RoomPlayerId roomPlayerId;

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

      webSocketSession.sendMessage(new TextMessage(JsonUtils.toJson(sentCardsMessage)));

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    for (int i = 0; i < playerCards.size(); ++i) {
      log.info(i + " " + playerCards.get(i));
    }
  }

  private int enterNumber() {
    // TODO: Receive number from frontend.
    return 0;
  }

  private boolean isNumberOfCardOutOfRange(int numberInput) {
    return numberInput < 0 || numberInput >= playerCards.size();
  }
}
