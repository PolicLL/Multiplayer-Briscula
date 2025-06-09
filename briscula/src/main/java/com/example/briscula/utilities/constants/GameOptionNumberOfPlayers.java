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

  public static GameOptionNumberOfPlayers fromInt(int number) {
    return switch (number) {
      case 2 -> TWO_PLAYERS;
      case 3 -> THREE_PLAYERS;
      case 4 -> FOUR_PLAYERS;
      default -> throw new IllegalArgumentException("Invalid number of players: " + number);
    };
  }
}