package com.example.web.model.entity;

public interface UserStatsProjection {
  String getUsername();
  Integer getPoints();
  Integer getLevel();
  Long getTotalMatchesPlayed();
  Long getTotalWins();
}

