package com.example.briscula.user.player;


import com.example.briscula.model.card.Card;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@ToString
public abstract class Player {

  protected List<Card> playerCards;
  @Getter
  protected String nickname;
  @Getter
  protected int points;

  public Player(List<Card> playerCards, String nickname) {
    this.playerCards = playerCards;
    this.nickname = nickname;
  }


  // TODO: I would like to when creating the players, use the same ID that is used in ConnectedPlayer task.
  public abstract Card playRound();

  public abstract void sentLoosingMessage();
  public abstract void sentWinningMessage();
  public abstract void setNoWinnerMessage();

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

  public void setPlayerCards(List<Card> playerCards) {
    this.playerCards = playerCards;
  }
}
