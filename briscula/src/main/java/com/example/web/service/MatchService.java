package com.example.web.service;

import static com.example.web.utils.Constants.RANDOM;

import com.example.web.dto.match.CreateAllStartingMatchesInTournamentDto;
import com.example.web.dto.match.CreateMatchDetailsDto;
import com.example.web.dto.match.CreateMatchDto;
import com.example.web.dto.match.MatchDto;
import com.example.web.exception.UserNotFoundException;
import com.example.web.mapper.MatchMapper;
import com.example.web.model.Match;
import com.example.web.model.MatchDetails;
import com.example.web.model.User;
import com.example.web.model.enums.MatchType;
import com.example.web.repository.MatchDetailsRepository;
import com.example.web.repository.MatchRepository;
import com.example.web.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchService {

  private final MatchRepository matchRepository;

  private final MatchMapper matchMapper;

  private final UserRepository userRepository;
  private final MatchDetailsRepository matchDetailsRepository;

  public MatchDto createMatches(
      CreateAllStartingMatchesInTournamentDto createAllStartingMatchesInTournamentDto) {
    List<String> userIds = new ArrayList<>(createAllStartingMatchesInTournamentDto.userIds());
    Collections.shuffle(userIds, RANDOM);

    Map<String, String> matchUsers = new HashMap<>();

    for (int i = 0; i < userIds.size(); i += 2) {
      matchUsers.put(userIds.get(i), userIds.get(i + 1));
    }

    int group = 0;

    for (Map.Entry<String, String> entry : matchUsers.entrySet()) {
      Match newMatch = createMatch(CreateMatchDto.builder()
          .userIds(List.of(entry.getKey(), entry.getValue()))
          .type(MatchType.TWO)
          .tournamentId(createAllStartingMatchesInTournamentDto.tournamentId())
          .build());

      createMatchDetails(CreateMatchDetailsDto.builder()
              .userId(entry.getKey())
              .group(group)
              .match(newMatch)
              .build());

      createMatchDetails(CreateMatchDetailsDto.builder()
          .userId(entry.getValue())
          .group(group)
          .match(newMatch)
          .build());

      ++group;

    }

    return null;
  }


  public Match createMatch(CreateMatchDto createMatchDto) {
    Match match = matchMapper.toMatch(createMatchDto);
    match.setId(UUID.randomUUID().toString());

    return matchRepository.save(match);
  }

  public MatchDetails createMatchDetails(CreateMatchDetailsDto createMatchDto) {
    User tempUser = userRepository.findById(createMatchDto.userId())
        .orElseThrow(() -> new UserNotFoundException(createMatchDto.userId()));

    return matchDetailsRepository.save(MatchDetails.builder()
        .user(tempUser)
        .match(createMatchDto.match())
        .group(createMatchDto.group())
        .id(UUID.randomUUID().toString())
        .points(0)
        .winner(false)
        .build());
  }

}
