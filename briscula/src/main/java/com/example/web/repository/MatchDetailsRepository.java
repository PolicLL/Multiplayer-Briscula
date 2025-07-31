package com.example.web.repository;

import com.example.web.model.MatchDetails;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchDetailsRepository extends JpaRepository<MatchDetails, String> {
  List<MatchDetails> findAllByMatchIdAndWinnerTrue(String matchId);
  List<MatchDetails> findAllByMatchId(String matchId);

}
