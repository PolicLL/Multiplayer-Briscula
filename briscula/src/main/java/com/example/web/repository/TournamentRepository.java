package com.example.web.repository;

import com.example.web.model.Tournament;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<Tournament, String> {
  @EntityGraph(value = "Tournament.users", type = EntityGraph.EntityGraphType.LOAD)
  Optional<Tournament> findWithUsersById(String id);
}