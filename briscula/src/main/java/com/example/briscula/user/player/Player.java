package com.example.briscula.user.player;


import com.example.briscula.model.card.Card;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.web.socket.WebSocketSession;

@ToString
@SuperBuilder
public abstract class Player {

  protected List<Card> playerCards;
  @Getter
  protected String nickname;
  @Getter
  protected int points;

  @Getter
  protected final WebSocketSession webSocketSession;

  protected Player(List<Card> playerCards, String nickname, WebSocketSession webSocketSession) {
    this.playerCards = playerCards;
    this.nickname = nickname;
    this.webSocketSession = webSocketSession;
  }

  protected Player(String nickname, WebSocketSession webSocketSession) {
    this.nickname = nickname;
    this.webSocketSession = webSocketSession;
  }

  public abstract Card playRound();
  public abstract void sentLoosingMessage();
  public abstract void sentWinningMessage();
  public abstract void setNoWinnerMessage();
  public abstract void sentMessageAboutNewCardFromAnotherPlayer(Card card, boolean isPlayersCard);

  public abstract CompletableFuture<Void> sentInformationAboutColleaguesCards(List<Card> cards);
  public abstract void sendMessageToWaitForNextMatch();

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
