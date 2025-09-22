package com.example.briscula.utilities.constants;

import com.example.briscula.model.card.Card;
import com.example.web.utils.JsonUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CardFormatter {

  public static String formatTemporaryPlayerState(List<Card> cards, int playerPoints) {
    return formatCards(cards) + " " + playerPoints;
  }

  public static String formatSentInitialCardsState(List<Card> cards, boolean showPoints) {
    return JsonUtils.toJson(Map.of(
        "cards", formatCards(cards),
        "showPoints", showPoints
    ));
  }

  public static String formatCards(List<Card> cards) {
    return Optional.ofNullable(cards)
        .orElse(Collections.emptyList())
        .stream()
        .map(CardFormatter::formatCard)
        .collect(Collectors.joining(" "));
  }

  public static String formatCard(Card card) {
    return card.cardType().toString() + extractCardShortValue(card.cardValue().name());
  }

  public static String formatCard(Card card, boolean isPlayersCard) {
    return card.cardType().toString() + extractCardShortValue(card.cardValue().name()) + (isPlayersCard ? " players" : "");
  }

  private static String extractCardShortValue(String valueName) {
    return switch (valueName) {
      case "TWO" -> "2";
      case "THREE" -> "3";
      case "FOUR" -> "4";
      case "FIVE" -> "5";
      case "SIX" -> "6";
      case "SEVEN" -> "7";
      case "JACK" -> "J";
      case "KNIGHT" -> "K";
      case "KING" -> "KI";
      case "ACE" -> "A";
      default -> "?";
    };
  }
}
