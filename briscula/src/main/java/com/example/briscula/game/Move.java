package com.example.briscula.game;

import com.example.briscula.model.card.Card;
import com.example.briscula.user.player.Player;

public record Move(Player player, Card card) {
  @Override
  public String toString() {
    return player + " " + card;
  }
}
