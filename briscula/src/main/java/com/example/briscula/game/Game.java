package com.example.briscula.game;

import com.example.briscula.utilities.constants.GameMode;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;

public class Game {

  private GameManager gameManager;

  public Game(GameOptionNumberOfPlayers gameOptions, GameMode gameMode) {
    gameManager = new GameManager(gameOptions, gameMode);
  }

  public void startGame(){
    while(!gameManager.isGameOver()){
      gameManager.playRound();
    }
  }
}