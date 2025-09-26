package com.example.web.repository;

import com.example.web.model.Tournament;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TournamentRepository extends JpaRepository<Tournament, String> {
    @EntityGraph(value = "Tournament.users", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Tournament> findWithUsersById(String id);

    @Query("""
                SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END
                FROM Tournament t
                JOIN t.users u
                WHERE u.id = :userId
                  AND t.status IN ('INITIALIZING', 'IN_PROGRESS')
            """)
    boolean isUserInActiveTournaments(@Param("userId") String userId);

}