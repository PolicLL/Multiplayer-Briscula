package com.example.web.repository;

import com.example.web.model.User;
import com.example.web.model.entity.UserStatsProjection;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User findByUsername(String username);

    User findByEmail(String email);

    Set<User> findAllByIdIn(List<String> ids);

    @Query(value = """
            SELECT\s
                u.username,
                u.points,
                u.level,
                COUNT(um.id) AS totalMatchesPlayed,
                SUM(um.number_of_wins) AS totalWins
            FROM users u
            LEFT JOIN match_details um ON u.id = um.user_id
            GROUP BY u.id, u.username, u.points, u.level
            ORDER BY u.points DESC
            """, nativeQuery = true)
    List<UserStatsProjection> fetchUserStats();

    @Query(value = """
            SELECT
                u.username,
                u.points,
                u.level,
                COUNT(um.id) AS totalMatchesPlayed,
                SUM(um.number_of_wins) AS totalWins
            FROM users u
            LEFT JOIN match_details um ON u.id = um.user_id
            GROUP BY u.id, u.username, u.points, u.level
            ORDER BY u.points DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<UserStatsProjection> fetchUserStats(@Param("limit") int limit);
}
