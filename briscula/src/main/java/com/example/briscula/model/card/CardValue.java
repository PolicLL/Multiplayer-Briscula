package com.example.briscula.model.card;

import lombok.Getter;

@Getter
public enum CardValue {
  TWO(0), FOUR(0), FIVE(0),
  SIX(0), SEVEN(0), JACK(2),
  KNIGHT(3), KING(4),
  THREE(10), ACE(11);

  public final int points;

  CardValue(int points) {
    this.points = points;
  }

  public boolean isBiggerThan(CardValue other) {
    return this.ordinal() > other.ordinal();
  }

  @Override
  public String toString() {
    return String.format("%s(%d)", name(), points);
  }
}
