package com.example.web.service;

import static com.example.web.utils.Constants.RANDOM;

import com.example.web.dto.match.CreateAllStartingMatchesInTournamentDto;
import com.example.web.dto.match.CreateMatchDetailsDto;
import com.example.web.dto.match.CreateMatchDto;
import com.example.web.dto.match.MatchDetailsDto;
import com.example.web.dto.match.MatchDto;
import com.example.web.dto.match.MatchesCreatedResponse;
import com.example.web.exception.MatchNotFoundException;
import com.example.web.exception.UserNotFoundException;
import com.example.web.mapper.MatchMapper;
import com.example.web.model.ConnectedPlayer;
import com.example.web.model.Match;
import com.example.web.model.MatchDetails;
import com.example.web.model.User;
import com.example.web.repository.MatchDetailsRepository;
import com.example.web.repository.MatchRepository;
import com.example.web.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchService {

  private final MatchRepository matchRepository;

  private final MatchMapper matchMapper;

  private final UserRepository userRepository;
  private final MatchDetailsRepository matchDetailsRepository;

  public MatchesCreatedResponse createMatches(
      CreateAllStartingMatchesInTournamentDto createAllStartingMatchesInTournamentDto) {
    List<String> userIds = new ArrayList<>(createAllStartingMatchesInTournamentDto.userIds());
    Collections.shuffle(userIds, RANDOM);

    Map<String, String> matchUsers = new HashMap<>();

    for (int i = 0; i < userIds.size(); i += 2) {
      matchUsers.put(userIds.get(i), userIds.get(i + 1));
    }

    List<MatchDetailsDto> matchDetailsDtoList = new ArrayList<>();
    List<Match> matches = new ArrayList<>();

    for (Map.Entry<String, String> entry : matchUsers.entrySet()) {
      Match newMatch = createMatch(CreateMatchDto.builder()
          .userIds(List.of(entry.getKey(), entry.getValue()))
          .numberOfPlayers(2)
          .tournamentId(createAllStartingMatchesInTournamentDto.tournamentId())
          .build());

      MatchDetailsDto firstMatchDetails = createMatchDetails(CreateMatchDetailsDto.builder()
          .userId(entry.getKey())
          .group(0)
          .match(newMatch)
          .build());

      MatchDetailsDto secondMatchDetails = createMatchDetails(CreateMatchDetailsDto.builder()
          .userId(entry.getValue())
          .group(1)
          .match(newMatch)
          .build());

      matches.add(newMatch);
      matchDetailsDtoList.add(firstMatchDetails);
      matchDetailsDtoList.add(secondMatchDetails);

    }

    return MatchesCreatedResponse.builder()
        .matchDetailsDtoList(matchDetailsDtoList)
        .matches(matches)
        .tournamentId(createAllStartingMatchesInTournamentDto.tournamentId())
        .build();
  }


  public Match createMatch(CreateMatchDto createMatchDto) {
    Match match = matchMapper.toMatch(createMatchDto);
    match.setId(UUID.randomUUID().toString());
    match.setUsers(userRepository.findAllByIdIn(createMatchDto.userIds()));

    return matchRepository.save(match);
  }

  private MatchDetailsDto createMatchDetails(CreateMatchDetailsDto createMatchDto) {
    User tempUser = userRepository.findById(createMatchDto.userId())
        .orElseThrow(() -> new UserNotFoundException(createMatchDto.userId()));

    return matchMapper.toMatchDetailsDto(matchDetailsRepository.save(MatchDetails.builder()
        .user(tempUser)
        .match(createMatchDto.match())
        .group(createMatchDto.group())
        .id(UUID.randomUUID().toString())
        .points(0)
        .numberOfWins(0)
        .build()));
  }

  public MatchDto getMatch(String matchId)  {
    log.info("Fetching match with Id: {}", matchId);
    Match match = retrieveMatch(matchId);
    return matchMapper.toMatchDto(match);

  }

  public Match retrieveMatch(String matchId) {
    return matchRepository.findWithUsersById(matchId)
        .orElseThrow(() -> new MatchNotFoundException(matchId));
  }

  public void updateResult(String matchId, ConnectedPlayer winner, ConnectedPlayer loser) {
    Map<String, MatchDetails> detailsByUserId = matchDetailsRepository.findAllByMatchId(matchId).stream()
        .collect(Collectors.toMap(value -> value.getUser().getId(), Function.identity()));

    MatchDetails matchDetailsWinner = detailsByUserId.get(winner.getUserId());
    MatchDetails matchDetailsLoser = detailsByUserId.get(loser.getUserId());

    matchDetailsWinner.setNumberOfWins(matchDetailsWinner.getNumberOfWins() + 1);

    matchDetailsRepository.saveAll(List.of(matchDetailsWinner, matchDetailsLoser));

  }
}
