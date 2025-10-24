package com.example.briscula.user.player;


import com.example.briscula.game.Move;
import com.example.briscula.model.card.Card;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

@ToString
@SuperBuilder
public abstract class Player {

    @Setter
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

    public abstract Card playRound(Queue<Move> queueMoves, Card card, GameOptionNumberOfPlayers gameOptions);

    public abstract void sentLoosingMessage(String message);

    public abstract void sentLoosingMessage();

    public abstract void sentWinningMessage();

    public abstract void sentWinningMessage(String message);

    public abstract void setNoWinnerMessage();

    public abstract void sentMessageAboutNewCardFromAnotherPlayer(Card card, boolean isPlayersCard, String name);

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
