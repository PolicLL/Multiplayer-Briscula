package com.example.briscula.game;

import com.example.briscula.user.player.Player;
import com.example.briscula.utilities.constants.GameMode;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import java.util.Collection;
import java.util.List;

public class Game {

  private final GameManager gameManager;

  public Game(GameOptionNumberOfPlayers gameOptions, GameMode gameMode, List<Player> players) {
    gameManager = new GameManager(gameOptions, gameMode, players);
  }

  public List<String> getCardsForPlayer(int player) {
    return List.of("First", "Second");
  }

  public void startGame(){
    while(!gameManager.isGameOver()){
      gameManager.playRound();
    }
  }
}