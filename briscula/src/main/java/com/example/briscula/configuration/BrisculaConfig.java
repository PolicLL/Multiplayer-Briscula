package com.example.briscula.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "briscula")
public class BrisculaConfig {

  private static int waitingTimeInSecondsForChoosingCard;

  public static int getWaitingTimeStatic() {
    return waitingTimeInSecondsForChoosingCard;
  }
}

