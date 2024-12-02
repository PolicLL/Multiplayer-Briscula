package com.example.briscula.user.player;


import com.example.briscula.model.card.Card;
import java.util.List;

public abstract class AbstractPlayer {
  protected List<Card> playerCards;
  protected String nickname;
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

  public int getPoints() {
    return points;
  }

  public String getNickname() {
    return nickname;
  }

  public void resetPoints() {
    points = 0;
  }
}
