package com.example.briscula.user.player;

import com.example.briscula.model.card.Card;
import java.util.List;
import java.util.Random;

public class Bot extends AbstractPlayer {

  private Random random;

  public Bot(List<Card> playerCards, String nickname) {
    super(playerCards, nickname);

    this.random = new Random();
  }

  public Card playRound(){
    Card tempCard = playerCards.get(random.nextInt(playerCards.size()));
    playerCards.remove(tempCard);

    return tempCard;
  }
}