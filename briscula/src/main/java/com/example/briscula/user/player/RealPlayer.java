package com.example.briscula.user.player;

import com.example.briscula.model.card.Card;
import java.util.List;
import java.util.Scanner;
import lombok.Getter;

@Getter
public class RealPlayer extends Player {

  private final Scanner scanner = new Scanner(System.in);

  public RealPlayer(List<Card> playerCards, String nickname) {
    super(playerCards, nickname);
  }

  @Override
  public Card playRound() {
    printInstructions();
    int numberInput = enterNumber();
    return playerCards.remove(numberInput);
  }

  private void printInstructions() {
    System.out.println("Your cards : ");
    for (int i = 0; i < playerCards.size(); ++i) {
      System.out.println(i + " " + playerCards.get(i));
    }
  }

  private int enterNumber() {
    int numberInput;
    do {
      System.out.print("Choose card: ");
      numberInput = scanner.nextInt();
    } while (isNumberOfCardOutOfRange(numberInput));
    return numberInput;
  }

  private boolean isNumberOfCardOutOfRange(int numberInput) {
    return numberInput < 0 || numberInput >= playerCards.size();
  }
}
