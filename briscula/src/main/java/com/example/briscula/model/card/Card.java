package com.example.briscula.model.card;

import com.example.briscula.game.Move;

import java.util.Queue;

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

    public static int getNumberOfPoints(Queue<Move> cards) {
        return cards
                .stream()
                .map(Move::card)
                .map(Card::getPoints)
                .reduce(0, Integer::sum);
    }
}
