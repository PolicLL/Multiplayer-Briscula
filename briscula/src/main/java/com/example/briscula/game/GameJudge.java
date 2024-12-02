package com.example.briscula.game;


import com.example.briscula.exceptions.DuplicateCardException;
import com.example.briscula.model.card.Card;
import com.example.briscula.model.card.CardType;
import com.example.briscula.user.admin.Admin;
import java.util.Queue;

public class GameJudge {

  private final Admin admin;

  public GameJudge(Admin admin) {
    this.admin = admin;
  }

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

    CardType mainCardType = admin.getMainCardType();
    if (!firstCard.isMainType(mainCardType) && secondCard.isMainType(mainCardType)) {
      return true;
    } else if (firstCard.isMainType(mainCardType) && secondCard.isMainType(mainCardType)) {
      return secondCard.isCardValueBiggerThan(firstCard);
    }

    return firstCard.isSameType(secondCard) && secondCard.isCardValueBiggerThan(firstCard);
  }
}
