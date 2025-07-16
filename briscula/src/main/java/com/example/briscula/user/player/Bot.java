package com.example.briscula.user.player;

import static com.example.web.utils.Constants.RANDOM;

import com.example.briscula.model.card.Card;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Bot extends Player {

  public Bot(List<Card> playerCards, String nickname) {
    super(playerCards, nickname);
  }

  public Card playRound(){
    Card tempCard = playerCards.get(RANDOM.nextInt(playerCards.size()));
    playerCards.remove(tempCard);

    return tempCard;
  }

  @Override
  public void sentLoosingMessage() {

  }

  @Override
  public void sentWinningMessage() {

  }

  @Override
  public void setNoWinnerMessage() {

  }

  @Override
  public void sentMessageAboutNewCardFromAnotherPlayer(Card card) {

  }

  @Override
  public void sendMessageToWaitForNextMatch() {

  }

  @Override
  public CompletableFuture<Void> sentInformationAboutColleaguesCards(List<Card> cards) {
    return null;
  }
}