package com.example.briscula.game;

import com.example.briscula.user.player.AbstractPlayer;

public record RoundWinner(
    AbstractPlayer player, int numberOfPoints
) {

}
