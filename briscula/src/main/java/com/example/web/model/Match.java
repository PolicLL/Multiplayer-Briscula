package com.example.web.model;

import com.example.web.model.enums.MatchType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "match")
public class Match {

  @Id
  private String id;

  @Enumerated(EnumType.STRING)
  private MatchType type;

  private int tournamentId;

  @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MatchDetails> matchDetails;

  @ManyToMany
  @JoinTable(
      name = "user_match",
      joinColumns = @JoinColumn(name = "match_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private Set<User> users = new HashSet<>();
}
