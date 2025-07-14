package com.example.web.model;

import com.example.web.model.enums.TournamentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
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
@NamedEntityGraph(
    name = "Tournament.users",
    attributeNodes = @NamedAttributeNode("users")
)
public class Tournament {

  @Id
  private String id;

  private String name;

  @Column(name = "number_of_players", nullable = false)
  private int numberOfPlayers;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TournamentStatus status;

  @ManyToMany
  @JoinTable(
      name = "user_tournament",
      joinColumns = @JoinColumn(name = "tournament_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private Set<User> users = new HashSet<>();

  @Column(name = "rounds_to_win", nullable = false)
  private int roundsToWin;

  public void addUser(User user) {
    users.add(user);
  }

  public void removeUser(User user) {
    users.remove(user);
  }


  public boolean isFull() {
    return users.size() >= numberOfPlayers;
  }

  public void setNumberOfPlayers(int numberOfPlayers) {
    if (numberOfPlayers != 2 && numberOfPlayers != 4 && numberOfPlayers != 8 &&
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Tournament that = (Tournament) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

}
