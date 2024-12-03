package com.example.briscula.model.card;


import static com.example.briscula.model.card.CardType.BASTONI;
import static com.example.briscula.model.card.CardType.COPPE;
import static com.example.briscula.model.card.CardType.DENARI;
import static com.example.briscula.model.card.CardType.SPADE;
import static com.example.briscula.model.card.CardValue.ACE;
import static com.example.briscula.model.card.CardValue.FIVE;
import static com.example.briscula.model.card.CardValue.FOUR;
import static com.example.briscula.model.card.CardValue.JACK;
import static com.example.briscula.model.card.CardValue.KNIGHT;
import static com.example.briscula.model.card.CardValue.SEVEN;
import static com.example.briscula.model.card.CardValue.SIX;
import static com.example.briscula.model.card.CardValue.THREE;
import static com.example.briscula.model.card.CardValue.TWO;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.briscula.exceptions.DuplicateCardException;
import com.example.briscula.game.GameJudge;
import com.example.briscula.user.admin.Admin;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * This test class is responsible for testing Card functions
 * It has testcases which are responsible for checking does comparison between
 * different Card objects works correctly.
 */

public class CardTest {

  private  static GameJudge gameJudge;
  private static final CardType mainCardType = CardType.COPPE;

  @BeforeAll
  public static void setUp() {
    Admin adminMock = mock(Admin.class);
    when(adminMock.getMainCardType()).thenReturn(mainCardType);
    gameJudge = new GameJudge(adminMock);
  }

  @ParameterizedTest
  @MethodSource("testSameNonMainCardType")
  public void testBothCardsSameNonMainTypeCombination(
      CardType cardType1, CardValue cardValue1, CardType cardType2, CardValue cardValue2
  ) throws DuplicateCardException {
    var firstCard = new Card(cardType1, cardValue1);
    var secondCard = new Card(cardType2, cardValue2);

    assertThat(gameJudge.isSecondCardStronger(firstCard, secondCard)).isFalse();
  }

  private static Stream<Arguments> testSameNonMainCardType() {

    return Stream.of(
        Arguments.of(SPADE, ACE, SPADE, THREE),
        Arguments.of(SPADE, THREE, SPADE, FOUR)
    );
  }

  @ParameterizedTest
  @MethodSource("testDifferentNonMainCardType")
  public void testCardsDifferentNonMainTypeCombination(
      CardType cardType1, CardValue cardValue1, CardType cardType2, CardValue cardValue2
  ) throws DuplicateCardException {

    var firstCard = new Card(cardType1, cardValue1);
    var secondCard = new Card(cardType2, cardValue2);

    assertThat(gameJudge.isSecondCardStronger(firstCard, secondCard)).isFalse();
  }

  private static Stream<Arguments> testDifferentNonMainCardType() {

    return Stream.of(
        Arguments.of(SPADE, ACE, DENARI, TWO),
        Arguments.of(DENARI, JACK, SPADE, SEVEN)
    );
  }


  @ParameterizedTest
  @MethodSource("testFirstCardMainCardType")
  public void TestFirstCardMainType(
      CardType cardType1, CardValue cardValue1, CardType cardType2, CardValue cardValue2
  ) throws DuplicateCardException {

    var firstCard = new Card(cardType1, cardValue1);
    var secondCard = new Card(cardType2, cardValue2);

    assertThat(gameJudge.isSecondCardStronger(firstCard, secondCard)).isFalse();
  }

  private static Stream<Arguments> testFirstCardMainCardType() {

    return Stream.of(
        Arguments.of(COPPE, ACE, DENARI, TWO),
        Arguments.of(COPPE, TWO, SPADE, ACE),
        Arguments.of(COPPE, FOUR, BASTONI, FIVE)
    );
  }


  @ParameterizedTest
  @MethodSource("testSecondCardMainCardType")
  public void TestSecondCardMainType(
      CardType cardType1, CardValue cardValue1, CardType cardType2, CardValue cardValue2
  ) throws DuplicateCardException {
    var firstCard = new Card(cardType1, cardValue1);
    var secondCard = new Card(cardType2, cardValue2);

    assertThat(gameJudge.isSecondCardStronger(firstCard, secondCard)).isTrue();
  }

  private static Stream<Arguments> testSecondCardMainCardType() {

    return Stream.of(
        Arguments.of(SPADE, ACE, COPPE, TWO),
        Arguments.of(DENARI, THREE, COPPE, FOUR),
        Arguments.of(BASTONI, KNIGHT, COPPE, SIX)
    );
  }


  @ParameterizedTest
  @MethodSource("testBothCardsMainCardType")
  public void TestBothCardsMainType(
      CardType cardType1, CardValue cardValue1, CardType cardType2, CardValue cardValue2
  ) throws DuplicateCardException {

    var firstCard = new Card(cardType1, cardValue1);
    var secondCard = new Card(cardType2, cardValue2);

    assertThat(gameJudge.isSecondCardStronger(firstCard, secondCard)).isFalse();
  }

  private static Stream<Arguments> testBothCardsMainCardType() {

    return Stream.of(
        Arguments.of(COPPE, ACE, COPPE, TWO),
        Arguments.of(COPPE, THREE, COPPE, SEVEN),
        Arguments.of(COPPE, SEVEN, COPPE, FIVE),
        Arguments.of(COPPE, JACK, COPPE, FOUR)
    );
  }

  @Test
  public void TestDuplicateCardException() {
    assertThrows(DuplicateCardException.class, () -> {
      gameJudge.isSecondCardStronger(new Card(COPPE, TWO), new Card(COPPE, TWO));
    });
  }
}