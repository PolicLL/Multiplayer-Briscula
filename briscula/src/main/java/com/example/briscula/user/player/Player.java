package com.example.briscula.user.player;


import com.example.briscula.model.card.Card;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import lombok.ToString;

@ToString
public abstract class Player {

  protected List<Card> playerCards;
  @Getter
  protected String nickname;
  @Getter
  protected int points;

  protected Player(List<Card> playerCards, String nickname) {
    this.playerCards = playerCards;
    this.nickname = nickname;
  }

  // TODO: I would like to when creating the players, use the same ID that is used in ConnectedPlayer task.
  public abstract Card playRound();
  public abstract void sentLoosingMessage();
  public abstract void sentWinningMessage();
  public abstract void setNoWinnerMessage();
  public abstract void sentMessageAboutNewCardFromAnotherPlayer(Card card);

  public abstract CompletableFuture<Void> sentInformationAboutColleaguesCards(List<Card> cards);

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Player player)) return false;
    return Objects.equals(nickname, player.nickname);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nickname);
  }
}
