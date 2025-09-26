package com.example.briscula.model.card;

public record Card(CardType cardType, CardValue cardValue) {

    public boolean isMainType(CardType mainCardType) {
        return this.cardType == mainCardType;
    }

    public boolean isSameType(Card otherCard) {
        return this.cardType == otherCard.cardType;
    }

    public int getPoints() {
        return this.cardValue.getPoints();
    }
}
