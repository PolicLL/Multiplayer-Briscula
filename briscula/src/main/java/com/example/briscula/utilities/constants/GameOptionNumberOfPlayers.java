package com.example.briscula.utilities.constants;

public enum GameOptionNumberOfPlayers {
  TWO_PLAYERS, THREE_PLAYERS, FOUR_PLAYERS;

  public int getNumberOfPlayers(){
    return switch (this){
      case TWO_PLAYERS -> 2;
      case THREE_PLAYERS -> 3;
      case FOUR_PLAYERS -> 4;
    };
  }
}