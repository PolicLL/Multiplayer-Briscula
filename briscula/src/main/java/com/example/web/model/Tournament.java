package com.example.web.model;

import com.example.web.model.enums.TournamentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tournament")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Tournament {

  @Id
  private String id;

  private String name;

  @Column(name = "number_of_players", nullable = false)
  private int numberOfPlayers;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TournamentStatus status;

  @Column(name = "rounds_to_win", nullable = false)
  private int roundsToWin;

  public void setNumberOfPlayers(int numberOfPlayers) {
    if (numberOfPlayers != 4 && numberOfPlayers != 8 &&
        numberOfPlayers != 16 && numberOfPlayers != 32) {
      throw new IllegalArgumentException(
          "Number of players must be one of 4, 8, 16, 32.");
    }
    this.numberOfPlayers = numberOfPlayers;
  }

  public void setRoundsToWin(int roundsToWin) {
    if (roundsToWin <= 0 || roundsToWin > 4) {
      throw new IllegalArgumentException("roundsToWin must be positive");
    }
    this.roundsToWin = roundsToWin;
  }
}
