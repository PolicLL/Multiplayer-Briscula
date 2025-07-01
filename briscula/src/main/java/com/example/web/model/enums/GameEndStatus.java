package com.example.web.model.enums;

import com.example.web.model.ConnectedPlayer;
import java.util.Map;
import lombok.Getter;

public record GameEndStatus(
    Map<ConnectedPlayer, Boolean> playerResults,
    Status status
) {
  @Getter
  public enum Status {
    WINNER_FOUND("Winner Found"),
    NO_WINNER("No Winner");

    private final String value;

    Status(String value) {
      this.value = value;
    }
  }
}