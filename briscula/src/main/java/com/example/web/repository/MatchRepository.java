package com.example.web.repository;

import com.example.web.model.Match;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, String> {
    @EntityGraph(attributePaths = {"matchDetails", "matchDetails.user"})
    Optional<Match> findWithUsersById(String id);
}
