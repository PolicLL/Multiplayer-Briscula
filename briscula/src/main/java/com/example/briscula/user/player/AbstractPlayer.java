package com.example.briscula.user.player;


import com.example.briscula.model.card.Card;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractPlayer {
  protected List<Card> playerCards;
  @Getter
  protected String nickname;
  @Getter
  protected int points;

  public AbstractPlayer(List<Card> playerCards, String nickname) {
    this.playerCards = playerCards;
    this.nickname = nickname;
  }

  public abstract Card playRound();

  public boolean isPlayerDone() {
    return playerCards.isEmpty();
  }

  public void incrementPoints(int points) {
    this.points += points;
  }

  public void addCard(Card card) {
    playerCards.add(card);
  }

  public void resetPoints() {
    points = 0;
  }

}
