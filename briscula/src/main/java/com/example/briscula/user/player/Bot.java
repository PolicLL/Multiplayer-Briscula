package com.example.briscula.user.player;

import com.example.briscula.model.card.Card;
import java.util.List;
import java.util.Random;

public class Bot extends Player {

  public Bot(List<Card> playerCards, String nickname) {
    super(playerCards, nickname);
  }

  public Card playRound(){
    Card tempCard = playerCards.get(new Random().nextInt(playerCards.size()));
    playerCards.remove(tempCard);

    return tempCard;
  }
}