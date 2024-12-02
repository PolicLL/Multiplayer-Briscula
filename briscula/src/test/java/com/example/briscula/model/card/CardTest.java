package com.example.briscula.model.card;


import static com.example.briscula.model.card.CardType.SPADE;
import static com.example.briscula.model.card.CardValue.ACE;
import static com.example.briscula.model.card.CardValue.FOUR;
import static com.example.briscula.model.card.CardValue.THREE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

  private static GameJudge gameJudge;
  private static final CardType mainCardType = CardType.COPPE;
  private Card firstCard, secondCard;

  @BeforeAll
  public static void setUp() {
    Admin adminMock = mock(Admin.class);
    when(adminMock.getMainCardType()).thenReturn(mainCardType);
    gameJudge = new GameJudge(adminMock);
  }

  @ParameterizedTest
  @MethodSource("testNoMainTypeCards")
  public void TestNonMainTypeCardsCombination(
      CardType cardType1, CardValue cardValue1, CardType cardType2, CardValue cardValue2
  ) {
    var firstCard = new Card(cardType1, cardValue1);
    var secondCard = new Card(cardType2, cardValue2);

    assertThat(firstCard.isCardValueBiggerThan(secondCard)).isTrue();
  }

  private static Stream<Arguments> testNoMainTypeCards() {

    return Stream.of(
        Arguments.of(SPADE, ACE, SPADE, THREE),
        Arguments.of(SPADE, THREE, SPADE, FOUR)
    );
  }

//  @Test
//  public void TestNonMainTypeCards() {
//
//    firstCard.setCardType(CardType.SPADE);
//    firstCard.setCardValue(CardValue.ACE);
//
//    secondCard.setCardType(CardType.SPADE);
//    secondCard.setCardValue(CardValue.THREE);
//
//    // ASSERTS
//
//    Assert.assertTrue(firstCard.isCardValueBiggerThan(secondCard));
//
//    secondCard.setCardValue(CardValue.FOUR);
//    Assert.assertTrue(firstCard.isCardValueBiggerThan(secondCard));
//
//    secondCard.setCardValue(CardValue.THREE);
//    Assert.assertTrue(firstCard.isCardValueBiggerThan(secondCard));
//  }
//
//  @Test
//  public void TestBothCardsAreNotMainCardType() throws DuplicateCardException {
//    firstCard.setCardType(CardType.SPADE);
//    firstCard.setCardValue(CardValue.ACE);
//
//    secondCard.setCardType(CardType.DENARI);
//    secondCard.setCardValue(CardValue.TWO);
//
//    // ASSERTS
//
//    assertFalse(gameJudge.isSecondCardStronger(firstCard, secondCard));
//
//    secondCard.setCardType(CardType.SPADE);
//    secondCard.setCardValue(CardValue.THREE);
//    assertFalse(gameJudge.isSecondCardStronger(firstCard, secondCard));
//
//    firstCard.setCardValue(CardValue.JACK);
//    Assert.assertTrue(gameJudge.isSecondCardStronger(firstCard, secondCard));
//  }
//
//  @Test
//  public void TestFirstCardMainType() throws DuplicateCardException {
//    firstCard.setCardType(CardType.SPADE);
//    firstCard.setCardValue(CardValue.ACE);
//
//    secondCard.setCardType(CardType.DENARI);
//    secondCard.setCardValue(CardValue.TWO);
//
//    // ASSERTS
//
//    assertFalse(gameJudge.isSecondCardStronger(firstCard, secondCard));
//
//    secondCard.setCardType(CardType.SPADE);
//    secondCard.setCardValue(CardValue.THREE);
//    assertFalse(gameJudge.isSecondCardStronger(firstCard, secondCard));
//  }
//
//  @Test
//  public void TestSecondCardMainType() throws DuplicateCardException {
//    firstCard.setCardType(CardType.SPADE);
//    firstCard.setCardValue(CardValue.ACE);
//
//    secondCard.setCardType(CardType.COPPE);
//    secondCard.setCardValue(CardValue.TWO);
//
//    // ASSERTS
//
//    Assert.assertTrue(gameJudge.isSecondCardStronger(firstCard, secondCard));
//
//    secondCard.setCardValue(CardValue.TWO);
//    Assert.assertTrue(gameJudge.isSecondCardStronger(firstCard, secondCard));
//  }
//
//  @Test
//  public void TestBothCardsMainType() throws DuplicateCardException {
//    firstCard.setCardType(CardType.COPPE);
//    firstCard.setCardValue(CardValue.TWO);
//
//    secondCard.setCardType(CardType.COPPE);
//    secondCard.setCardValue(CardValue.THREE);
//
//    // ASSERTS
//
//    Assert.assertTrue(gameJudge.isSecondCardStronger(firstCard, secondCard));
//
//    firstCard.setCardValue(CardValue.ACE);
//    assertFalse(gameJudge.isSecondCardStronger(firstCard, secondCard));
//  }
//
//  @Test
//  public void TestDuplicateCardException() {
//    firstCard.setCardType(CardType.COPPE);
//    firstCard.setCardValue(CardValue.TWO);
//
//    secondCard.setCardType(CardType.COPPE);
//    secondCard.setCardValue(CardValue.TWO);
//
//    assertThrows(DuplicateCardException.class, () -> {
//      gameJudge.isSecondCardStronger(firstCard, secondCard);
//    });
//  }
}