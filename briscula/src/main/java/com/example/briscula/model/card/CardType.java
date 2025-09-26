package com.example.briscula.model.card;

public enum CardType {
    DENARI, SPADE, COPPE, BASTONI;

    @Override
    public String toString() {
        return String.valueOf(name().charAt(0));
    }
}
