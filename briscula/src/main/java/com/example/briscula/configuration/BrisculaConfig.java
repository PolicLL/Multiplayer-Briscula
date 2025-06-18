package com.example.briscula.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "briscula")
@Getter
@Setter
public class BrisculaConfig {

  private int waitingTimeInSecondsForChoosingCard;

  @Getter
  private static int waitingTimeStatic;

  @PostConstruct
  public void init() {
    waitingTimeStatic = waitingTimeInSecondsForChoosingCard;
  }
}
