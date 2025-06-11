package com.example.briscula.user.player;

import static com.example.web.utils.Constants.RANDOM;

import com.example.briscula.model.card.Card;
import java.util.List;

public class Bot extends Player {

  public Bot(List<Card> playerCards, String nickname) {
    super(playerCards, nickname);
  }

  public Card playRound(){
    Card tempCard = playerCards.get(RANDOM.nextInt(playerCards.size()));
    playerCards.remove(tempCard);

    return tempCard;
  }
}