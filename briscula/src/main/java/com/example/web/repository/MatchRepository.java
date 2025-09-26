package com.example.web.repository;

import com.example.web.model.Match;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, String> {
    @EntityGraph(value = "Match.users", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Match> findWithUsersById(String id);
}
