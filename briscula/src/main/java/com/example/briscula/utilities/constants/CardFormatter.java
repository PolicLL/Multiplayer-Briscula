package com.example.briscula.utilities.constants;

import com.example.briscula.model.card.Card;

import java.util.List;
import java.util.stream.Collectors;

public class CardFormatter {

  public static String formatCards(List<Card> cards) {
    return cards.stream()
        .map(card -> card.cardType().toString() + extractCardShortValue(card.cardValue().name()))
        .collect(Collectors.joining(" "));
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
