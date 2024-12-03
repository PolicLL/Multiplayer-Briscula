package com.example.briscula.game;


import com.example.briscula.exceptions.DuplicateCardException;
import com.example.briscula.model.card.Card;
import com.example.briscula.model.card.CardType;
import java.util.Queue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameJudge {

  private final CardType mainCardType;

  public RoundWinner calculateRound(Queue<Move> queueMoves) {
    Move roundWinnerMove = queueMoves.poll();
    int tempPointsInRound = roundWinnerMove.card().getPoints();

    while (!queueMoves.isEmpty()) {
      Move tempMove = queueMoves.poll();
      tempPointsInRound += tempMove.card().getPoints();

      try {
        if (isSecondCardStronger(roundWinnerMove.card(), tempMove.card())) {
          roundWinnerMove = tempMove;
        }
      } catch (DuplicateCardException e) {
        throw new RuntimeException(e);
      }
    }

    return new RoundWinner(roundWinnerMove.player(), tempPointsInRound);
  }

  public boolean isSecondCardStronger(Card firstCard, Card secondCard) throws DuplicateCardException {
    if (firstCard.equals(secondCard)) throw new DuplicateCardException();

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
  private boolean isSecondCardStrongerThanFirstCard(Card firstCard, Card secondCard) {
    return secondCard.cardValue().isBiggerThan(firstCard.cardValue());
  }

}
