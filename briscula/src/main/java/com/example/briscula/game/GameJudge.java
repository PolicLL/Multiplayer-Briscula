package com.example.briscula.game;


import com.example.briscula.exceptions.DuplicateCardException;
import com.example.briscula.model.card.Card;
import com.example.briscula.model.card.CardType;
import com.example.briscula.user.admin.Admin;
import java.util.Queue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameJudge {

  private final CardType mainCardType;

  public void calculateRound(Queue<Move> queueMoves) {
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

    roundWinnerMove.player().incrementPoints(tempPointsInRound);
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

  public boolean isSecondCardStrongerThanFirstCard(Card firstCard, Card secondCard) {
    return secondCard.cardValue().isBiggerThan(firstCard.cardValue());
  }

}
