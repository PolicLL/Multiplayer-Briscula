package com.example.briscula.game;


import com.example.briscula.model.card.Card;
import com.example.briscula.model.card.CardType;

import java.util.Queue;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RoundJudge {


    public static RoundWinner calculateRound(Queue<Move> queueMoves, CardType mainCardType) {
        Move roundWinnerMove = queueMoves.poll();
        assert roundWinnerMove != null;
        int tempPointsInRound = roundWinnerMove.card().getPoints();

        while (!queueMoves.isEmpty()) {
            Move tempMove = queueMoves.poll();
            tempPointsInRound += tempMove.card().getPoints();

            if (isSecondCardStronger(roundWinnerMove.card(), tempMove.card(), mainCardType)) {
                roundWinnerMove = tempMove;
            }
        }

        return new RoundWinner(roundWinnerMove.player(), tempPointsInRound);
    }

    public static boolean isSecondCardStronger(Card firstCard, Card secondCard, CardType mainCardType) {
        if (!firstCard.isMainType(mainCardType) && secondCard.isMainType(mainCardType)) {
            return true;
        } else if (firstCard.isMainType(mainCardType) && secondCard.isMainType(mainCardType)) {
            return isSecondCardStrongerThanFirstCard(firstCard, secondCard);
        }

        return firstCard.isSameType(secondCard) && isSecondCardStrongerThanFirstCard(firstCard, secondCard);
    }

    /**
     * This function ignores the fact which card type is the main one while checking for stronger card.
     */
    private static boolean isSecondCardStrongerThanFirstCard(Card firstCard, Card secondCard) {
        return secondCard.cardValue().isBiggerThan(firstCard.cardValue());
    }

}
