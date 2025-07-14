package com.example.service;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.EntityUtils.createTournamentCreateDto;
import static utils.EntityUtils.generateValidUserDtoWithoutPhoto;

import com.example.web.dto.match.CreateAllStartingMatchesInTournamentDto;
import com.example.web.dto.match.MatchDetailsDto;
import com.example.web.dto.match.MatchesCreatedResponse;
import com.example.web.dto.tournament.TournamentResponseDto;
import com.example.web.handler.AbstractIntegrationTest;
import com.example.web.service.MatchService;
import com.example.web.service.TournamentService;
import com.example.web.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


class MatchServiceTest extends AbstractIntegrationTest {

  @Autowired
  private MatchService matchService;

  @Autowired
  private TournamentService tournamentService;

  @Autowired
  private UserService userService;

  private final List<String> userIds = new ArrayList<>();

  @BeforeEach
  void init() {
    if (userIds.isEmpty()) {
      for (int i = 0; i < 4; ++i) {
        userIds.add(userService.createUser(generateValidUserDtoWithoutPhoto()).id());
      }
    }
  }

  @Test
  void testCreateMatches() {
    TournamentResponseDto tournamentCreateDto =  tournamentService.create(createTournamentCreateDto());

    MatchesCreatedResponse matchesCreatedResponse = matchService.createMatches(
        CreateAllStartingMatchesInTournamentDto.builder()
            .numberOfPlayers(4)
            .tournamentId(tournamentCreateDto.id())
            .userIds(userIds)
            .build());

    assertThat(matchesCreatedResponse).isNotNull();
    assertThat(matchesCreatedResponse.matchDetailsDtoList()).hasSize(4);
    assertThat(matchesCreatedResponse.tournamentId()).isEqualTo(tournamentCreateDto.id());

    List<String> receivedUserIds = matchesCreatedResponse.matchDetailsDtoList()
            .stream()
            .map(MatchDetailsDto::userId)
            .toList();

    assertThat(receivedUserIds).containsExactlyInAnyOrderElementsOf(userIds);

    matchesCreatedResponse.matchDetailsDtoList().forEach(
        matchDetails -> {
          assertThat(matchDetails.matchId()).isNotNull();
          assertThat(matchDetails.id()).isNotNull();
        }
    );
  }

}