package com.example.briscula.model.card;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

class CardValueTest {

    @Test
    void TestForThree() {

        CardValue mainCardValue = CardValue.THREE;

        assertThat(mainCardValue.isBiggerThan(CardValue.TWO)).isTrue();
        assertThat(mainCardValue.isBiggerThan(CardValue.FOUR)).isTrue();
        assertThat(mainCardValue.isBiggerThan(CardValue.JACK)).isTrue();
        assertThat(mainCardValue.isBiggerThan(CardValue.FIVE)).isTrue();
        assertThat(mainCardValue.isBiggerThan(CardValue.SIX)).isTrue();
        assertThat(mainCardValue.isBiggerThan(CardValue.KING)).isTrue();
        assertThat(mainCardValue.isBiggerThan(CardValue.KNIGHT)).isTrue();

        assertThat(mainCardValue.isBiggerThan(CardValue.ACE)).isFalse();
        assertThat(mainCardValue.isBiggerThan(CardValue.THREE)).isFalse();
    }

    @Test
    void TestForTwo() {

        CardValue mainCardValue = CardValue.TWO;

        assertThat(mainCardValue.isBiggerThan(CardValue.THREE)).isFalse();
        assertThat(mainCardValue.isBiggerThan(CardValue.FOUR)).isFalse();
        assertThat(mainCardValue.isBiggerThan(CardValue.FIVE)).isFalse();
        assertThat(mainCardValue.isBiggerThan(CardValue.SIX)).isFalse();
        assertThat(mainCardValue.isBiggerThan(CardValue.SEVEN)).isFalse();
        assertThat(mainCardValue.isBiggerThan(CardValue.JACK)).isFalse();
        assertThat(mainCardValue.isBiggerThan(CardValue.KNIGHT)).isFalse();
        assertThat(mainCardValue.isBiggerThan(CardValue.KING)).isFalse();
        assertThat(mainCardValue.isBiggerThan(CardValue.THREE)).isFalse();
        assertThat(mainCardValue.isBiggerThan(CardValue.ACE)).isFalse();
    }

}
