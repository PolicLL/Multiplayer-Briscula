package com.example.briscula.user.player;

import static com.example.web.utils.Constants.RANDOM;

import com.example.briscula.model.card.Card;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.socket.WebSocketSession;

public class Bot extends Player {


    public Bot(String nickname, WebSocketSession webSocketSession) {
        super(nickname, webSocketSession);

    }

    public Card playRound() {
        Card tempCard = playerCards.get(RANDOM.nextInt(playerCards.size()));
        playerCards.remove(tempCard);
        return tempCard;
    }

    @Override
    public void sentLoosingMessage(String message) {

    }

    @Override
    public void sentLoosingMessage() {

    }


    @Override
    public void sentWinningMessage() {

    }

    @Override
    public void sentWinningMessage(String message) {

    }

    @Override
    public void setNoWinnerMessage() {

    }

    @Override
    public void sentMessageAboutNewCardFromAnotherPlayer(Card card, boolean isPlayersCard, String name) {

    }

    @Override
    public void sendMessageToWaitForNextMatch() {

    }

    @Override
    public CompletableFuture<Void> sentInformationAboutColleaguesCards(List<Card> cards) {
        return null;
    }
}