package com.example.briscula.model.card;

import static com.example.briscula.model.card.CardType.BASTONI;
import static com.example.briscula.model.card.CardType.COPPE;
import static com.example.briscula.model.card.CardType.DENARI;
import static com.example.briscula.model.card.CardType.SPADE;
import static com.example.briscula.model.card.CardValue.ACE;
import static com.example.briscula.model.card.CardValue.FIVE;
import static com.example.briscula.model.card.CardValue.FOUR;
import static com.example.briscula.model.card.CardValue.JACK;
import static com.example.briscula.model.card.CardValue.KING;
import static com.example.briscula.model.card.CardValue.KNIGHT;
import static com.example.briscula.model.card.CardValue.SEVEN;
import static com.example.briscula.model.card.CardValue.THREE;
import static com.example.briscula.model.card.CardValue.TWO;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.briscula.utilities.constants.CardFormatter;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CardFormatterTest {

  @ParameterizedTest
  @MethodSource("cardListProvider")
  @DisplayName("Should format cards into expected string")
  public void testCardFormatter(CardType type1, CardValue value1,
      CardType type2, CardValue value2,
      CardType type3, CardValue value3,
      String expected) {

    var firstCard = new Card(type1, value1);
    var secondCard = new Card(type2, value2);
    var thirdCard = new Card(type3, value3);

    List<Card> cards = List.of(firstCard, secondCard, thirdCard);
    String result = CardFormatter.formatCards(cards);

    assertThat(result).isEqualTo(expected);
  }

  private static Stream<Arguments> cardListProvider() {
    return Stream.of(
        Arguments.of(DENARI, SEVEN, COPPE, TWO, BASTONI, THREE, "D7 C2 B3"),
        Arguments.of(SPADE, JACK, COPPE, ACE, DENARI, FIVE, "SJ CA D5"),
        Arguments.of(BASTONI, FOUR, SPADE, KNIGHT, COPPE, KING, "B4 Sk CK"),
        Arguments.of(COPPE, THREE, COPPE, FOUR, COPPE, FIVE, "C3 C4 C5"),
        Arguments.of(SPADE, ACE, SPADE, TWO, SPADE, THREE, "SA S2 S3")
    );
  }
}
