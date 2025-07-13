package com.example.web.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MatchType {
  TWO(2),
  THREE(3),
  FOUR(4);

  private final int playerCount;

  public static MatchType fromInt(int count) {
    for (MatchType type : values()) {
      if (type.playerCount == count) {
        return type;
      }
    }
    throw new IllegalArgumentException("Invalid player count: " + count);
  }
}
