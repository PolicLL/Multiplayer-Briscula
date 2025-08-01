package com.example.web.repository;

import com.example.web.model.MatchDetails;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchDetailsRepository extends JpaRepository<MatchDetails, String> {
  List<MatchDetails> findAllByMatchId_IdAndNumberOfWinsGreaterThanEqual(String matchId, int numberOfWins);

  List<MatchDetails> findAllByMatchId(String matchId);

}
