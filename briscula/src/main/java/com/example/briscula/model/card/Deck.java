package com.example.briscula.model.card;

import com.example.briscula.exceptions.NoCardWithNumberTwoException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.Getter;

public class Deck {

  @Getter
  private final List<Card> deckCards = new ArrayList<>();
  private final Random random = new Random();

  public Deck() {
    fillDeck();
  }

  private void fillDeck() {
    deckCards.clear();
    for (CardType type : CardType.values()) {
      for (CardValue value : CardValue.values()) {
        deckCards.add(new Card(type, value));
      }
    }
  }

  public Card removeOneCard() {
    return deckCards.remove(random.nextInt(deckCards.size()));
  }

  public Boolean removeOneWithCardValueTwo() {
    return deckCards.stream()
        .filter(card -> card.cardValue() == CardValue.TWO)
        .findFirst()
        .map(deckCards::remove)
        .orElseThrow(() -> new IllegalStateException(new NoCardWithNumberTwoException()));
  }

  public int getNumberOfDeckCards() {
    return deckCards.size();
  }

  @Override
  public String toString() {
    StringBuilder output = new StringBuilder("\n");
    int numPlayers = deckCards.size() == 39 ? 3 : 4;
    int cardsPerRow = numPlayers == 3 ? 13 : 10;

    for (int i = 0; i < numPlayers; ++i) {
      deckCards.subList(i * cardsPerRow, (i + 1) * cardsPerRow)
          .forEach(card -> output.append(card).append(" "));
      output.append("\n");
    }

    return output.append("Number of cards: ").append(deckCards.size()).append("\n").toString();
  }
}
