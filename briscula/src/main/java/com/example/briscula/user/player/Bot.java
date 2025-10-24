package com.example.briscula.user.player;

import com.example.briscula.game.Move;
import com.example.briscula.model.card.Card;
import com.example.briscula.model.card.CardType;
import com.example.briscula.model.card.CardValue;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

import static com.example.briscula.game.RoundJudge.isCardStrongerThanAllOtherCards;
import static com.example.briscula.model.card.Card.getNumberOfPoints;
import static com.example.web.utils.Constants.RANDOM;

public class Bot extends Player {


    public Bot(String nickname, WebSocketSession webSocketSession) {
        super(nickname, webSocketSession);
    }

    public Card playRound(Queue<Move> moves, Card mainCard, GameOptionNumberOfPlayers gameOptions) {
        Card tempCard = determineCardToThrow(moves, mainCard, gameOptions);
        playerCards.remove(tempCard);


        try {
            Thread.sleep(RANDOM.nextInt(3000) + 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return tempCard;
    }

    private Card determineCardToThrow(Queue<Move> moves, Card mainCard, GameOptionNumberOfPlayers gameOptions) {
        if (moves.isEmpty()) return handleThrowingWhenYouAreFirst(mainCard, gameOptions);
        else if (moves.size() == 1) return handleThrowingWhenYouAreSecond(moves, mainCard);
        return handleThrowingWhenYouAreThirdOrFourth(moves, mainCard);
    }

    private Card handleThrowingWhenYouAreFirst(Card mainCard, GameOptionNumberOfPlayers gameOptions) {
        if (gameOptions.equals(GameOptionNumberOfPlayers.TWO_PLAYERS)) {
            Optional<Card> mainCardStrongerThanSeven = doesHaveMainCardTypeStrongerThen(mainCard.cardType(), CardValue.SEVEN);
            Optional<Card> nonMainCardStrongerThanKnight = doesHaveNonMainCardTypeStrongerThen(mainCard.cardType(), CardValue.KNIGHT);

            if (mainCardStrongerThanSeven.isPresent() && nonMainCardStrongerThanKnight.isPresent()) {
                return RANDOM.nextInt(2) == 0 ?
                        mainCardStrongerThanSeven.get() :
                        nonMainCardStrongerThanKnight.get();
            }
        }

        return getRandomCardInList(playerCards);
    }

    private Card handleThrowingWhenYouAreSecond(Queue<Move> moves, Card mainCard) {
        Move thrownCard = moves.peek();

        assert thrownCard != null;
        if (!thrownCard.card().isMainType(mainCard.cardType())) {
            Card possibleCard = doesHaveStrongestCardWhichIsNotMainType(moves, mainCard);
            if (possibleCard != null) {
                return possibleCard;
            }
        }

        return playerCards.get(0);
    }

    private Card handleThrowingWhenYouAreThirdOrFourth(Queue<Move> moves, Card mainCard) {
        Card strongestCard = doesHaveStrongestCard(moves, mainCard);
        int lineNumberOfPointsToKill = RANDOM.nextInt(5) + 10;

        int currentNumberOfPointsOnTable = getNumberOfPoints(moves);

        if (currentNumberOfPointsOnTable > 20 && strongestCard != null) {
            return strongestCard;
        }

        if (currentNumberOfPointsOnTable > lineNumberOfPointsToKill && strongestCard != null) {
            return strongestCard;
        }

        if (currentNumberOfPointsOnTable > 10 && strongestCard != null && strongestCard.cardValue().isSmallerThan(CardValue.THREE)) {
            return strongestCard;
        }

        return playerCards.get(0);
    }

    private Optional<Card> doesHaveMainCardTypeStrongerThen(CardType mainCardType, CardValue cardValue) {
        return playerCards.stream().filter(
                card -> card.isMainType(mainCardType) &&
                        card.cardValue().isBiggerThan(cardValue))
                .findFirst();
    }

    private Optional<Card> doesHaveNonMainCardTypeStrongerThen(CardType mainCardType, CardValue cardValue) {
        return playerCards.stream().filter(
                card -> !card.isMainType(mainCardType) &&
                        card.cardValue().isBiggerThan(cardValue))
                .findFirst();
    }

    private Card doesHaveStrongestCard(Queue<Move> moves, Card mainCard) {
        return playerCards.stream()
                .filter(card -> isCardStrongerThanAllOtherCards(card, moves, mainCard.cardType()))
                .findFirst()
                .orElse(null);
    }

    private Card doesHaveStrongestCardWhichIsNotMainType(Queue<Move> moves, Card mainCard) {
        return playerCards.stream()
                .filter(card -> !card.isMainType(mainCard.cardType()))
                .filter(card -> isCardStrongerThanAllOtherCards(card, moves, mainCard.cardType()))
                .findFirst()
                .orElse(null);
    }

    private Card getRandomCardInList(List<Card> cards) {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.get(RANDOM.nextInt(cards.size()));
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