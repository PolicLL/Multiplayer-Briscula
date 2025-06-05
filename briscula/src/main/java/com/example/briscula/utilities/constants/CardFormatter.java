package com.example.briscula.utilities.constants;

import com.example.briscula.model.card.Card;

import java.util.List;
import java.util.stream.Collectors;

public class CardFormatter {

  public static String formatTemporaryPlayerState(List<Card> cards, int playerPoints) {
    return formatCards(cards) + " " + playerPoints;
  }

  public static String formatCards(List<Card> cards) {
    return cards.stream()
        .map(card -> card.cardType().toString() + extractCardShortValue(card.cardValue().name()))
        .collect(Collectors.joining(" "));
  }

  public static String formatCard(Card card) {
    return card.cardType().toString() + extractCardShortValue(card.cardValue().name());
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
