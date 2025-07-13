package com.example.web.repository;

import com.example.web.model.User;
import com.example.web.model.entity.UserStatsProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);
  User findByUsername(String username);
  User findByEmail(String email);

  List<User> findAllByOrderByPointsDesc();

  @Query(value = """
    SELECT\s
        u.username,
        u.points,
        u.level,
        COUNT(um.id) AS totalMatchesPlayed,
        SUM(CASE WHEN um.winner THEN 1 ELSE 0 END) AS totalWins
    FROM users u
    LEFT JOIN match_details um ON u.id = um.user_id
    GROUP BY u.id, u.username, u.points, u.level
    ORDER BY u.points DESC
    """, nativeQuery = true)
  List<UserStatsProjection> fetchUserStats();
}
