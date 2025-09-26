package com.example.briscula.game;

import com.example.briscula.user.player.Player;

public record RoundWinner(
        Player player, int numberOfPoints
) {

}
