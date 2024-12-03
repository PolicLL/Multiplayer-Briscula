package com.example.briscula.utilities.constants;

import java.util.Random;

public class Constants {

  public static final Random RANDOM = new Random();

  public static final String HUMAN_PLAYER = "Human Player";

  public static int getRandomNumber(int scope) {
    return RANDOM.nextInt(scope);
  }
}
