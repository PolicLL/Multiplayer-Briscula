package com.example.briscula.model.card;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Card {

  private final CardType cardType;
  private final CardValue cardValue;

  public boolean isMainType(CardType mainCardType) {
    return this.cardType == mainCardType;
  }

  public boolean isSameType(Card otherCard) {
    return this.cardType == otherCard.cardType;
  }

  public boolean isCardValueBiggerThan(Card secondCard) {
    return this.cardValue.isBiggerThan(secondCard.cardValue);
  }

  public int getPoints() {
    return this.cardValue.getPoints();
  }
}
