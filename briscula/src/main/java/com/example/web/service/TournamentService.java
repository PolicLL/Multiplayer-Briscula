package com.example.web.service;


import static com.example.web.utils.Constants.OBJECT_MAPPER;

import com.example.briscula.user.player.RealPlayer;
import com.example.web.dto.match.CreateMatchDto;
import com.example.web.dto.tournament.JoinTournamentRequest;
import com.example.web.dto.tournament.TournamentCreateDto;
import com.example.web.dto.tournament.TournamentResponseDto;
import com.example.web.exception.TournamentIsFullException;
import com.example.web.exception.UserAlreadyAssignedToTournament;
import com.example.web.exception.UserNotFoundException;
import com.example.web.mapper.TournamentMapper;
import com.example.web.model.ConnectedPlayer;
import com.example.web.model.Match;
import com.example.web.model.Tournament;
import com.example.web.model.User;
import com.example.web.model.enums.TournamentStatus;
import com.example.web.repository.TournamentRepository;
import com.example.web.repository.UserRepository;
import com.example.web.utils.WebSocketMessageReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentService {

  private final TournamentRepository tournamentRepository;
  private final UserRepository userRepository;
  private final TournamentMapper tournamentMapper;

  private final MatchService matchService;

  private final WebSocketMessageDispatcher messageDispatcher;


  private final Map<String, Set<ConnectedPlayer>> tournamentPlayers = new HashMap<>();
  private final Map<String, Set<User>> tournamentUsers = new HashMap<>();

  @PostConstruct
  public void init() {
    for (Tournament tournament : tournamentRepository.findAll()) {
      if (tournament.getStatus().equals(TournamentStatus.INITIALIZING)) {
        tournamentPlayers.put(tournament.getId(), new HashSet<>());
        tournamentUsers.put(tournament.getId(), new HashSet<>());
      }
    }
  }

  public TournamentResponseDto create(TournamentCreateDto dto) {
    Tournament tournament = tournamentMapper.toEntity(dto);
    tournament.setId(UUID.randomUUID().toString());
    tournamentRepository.save(tournament);
    log.debug("Tournament saved: {}", tournament);

    tournamentPlayers.put(tournament.getId(), new HashSet<>());
    tournamentUsers.put(tournament.getId(), new HashSet<>());
    return tournamentMapper.toResponseDto(tournament, 0);
  }

  public TournamentResponseDto joinTournament(JoinTournamentRequest request, WebSocketSession webSocketSession) {
    log.info("Received join tournament request: userId={}, tournamentId={}", request.userId(), request.tournamentId());

    Tournament tournament = receiveTournament(request.tournamentId());

    if (isTournamentFull(tournament.getId())) {
      log.warn("Tournament {} is full, user {} cannot join", request.tournamentId(), request.userId());
      throw new TournamentIsFullException(request.tournamentId());
    }

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> {
          log.warn("User with id {} not found", request.userId());
          return new UserNotFoundException(request.userId());
        });

    ConnectedPlayer connectedPlayer = new ConnectedPlayer(webSocketSession, new RealPlayer(
        null, user.getUsername(), webSocketSession), true);

    if (!tournamentPlayers.get(tournament.getId()).add(connectedPlayer)) {
      throw new UserAlreadyAssignedToTournament();
    }

    tournamentPlayers.get(request.tournamentId()).add(connectedPlayer);
    tournamentUsers.get(request.tournamentId()).add(user);
    messageDispatcher.registerSession(webSocketSession);


    if (isTournamentFull(tournament.getId()))
      tournament.setStatus(TournamentStatus.IN_PROGRESS);

    TournamentResponseDto tournamentResponse = tournamentMapper.toResponseDto(
        tournament, getNumberOfPlayersInTournament(tournament.getId()));

    broadcastTournamentUpdate(tournamentResponse, isTournamentFull(tournament.getId()));

    return tournamentResponse;
  }

  public TournamentResponseDto getById(String id) {
    log.info("Fetching tournament with ID: {}", id);
    Tournament tournament = receiveTournament(id);
    return tournamentMapper.toResponseDto(tournament, getNumberOfPlayersInTournament(id));
  }

  public List<TournamentResponseDto> getAll() {
    log.info("Fetching all tournaments");
    return tournamentRepository.findAll().stream()
        .map(response -> tournamentMapper.toResponseDto(response, getNumberOfPlayersInTournament(response.getId())))
        .toList();
  }

  public TournamentResponseDto update(String id, TournamentCreateDto dto) {
    log.info("Updating tournament with ID: {}", id);
    if (!tournamentRepository.existsById(id)) {
      log.warn("Cannot update, tournament not found: {}", id);
      throw new EntityNotFoundException("Tournament not found with id: " + id);
    }

    Tournament newTournament =  tournamentMapper.toEntity(dto);
    newTournament.setId(id);

    tournamentRepository.save(newTournament);
    log.debug("Updated tournament: {}", newTournament);
    return tournamentMapper.toResponseDto(newTournament, getNumberOfPlayersInTournament(newTournament.getId()));
  }

  public void delete(String id) {
    log.info("Deleting tournament with ID: {}", id);
    if (!tournamentRepository.existsById(id)) {
      log.warn("Cannot delete, tournament not found: {}", id);
      throw new EntityNotFoundException("Tournament not found with id: " + id);
    }
    tournamentRepository.deleteById(id);
    log.info("Tournament deleted: {}", id);
  }

  private Tournament receiveTournament(String id) {
    return tournamentRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Tournament not found: {}", id);
          return new EntityNotFoundException("Tournament not found with id: " + id);
        });
  }

  public void handle(WebSocketSession session, WebSocketMessage<?> message)
      throws JsonProcessingException {
    String tournamentId = WebSocketMessageReader.getValueFromJsonMessage(message, "tournamentId");
    String playerId = WebSocketMessageReader.getValueFromJsonMessage(message, "playerId");

    joinTournament(JoinTournamentRequest.builder()
        .tournamentId(tournamentId)
        .userId(playerId)
        .build(), session);
  }

  public void broadcastTournamentUpdate(TournamentResponseDto tournamentResponseDto, boolean isFull) {
    log.info("Broadcasting tournament update to players.");
    List<WebSocketSession> webSocketSessions =
        tournamentPlayers.get(tournamentResponseDto.id())
            .stream()
            .map(ConnectedPlayer::getWebSocketSession)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    for (WebSocketSession session : webSocketSessions) {
      try {
        if (session.isOpen()) {
          String json = OBJECT_MAPPER.writeValueAsString(tournamentResponseDto);
          messageDispatcher.sendMessage(session, json);

          if (isFull) {

          }

        }
      } catch (Exception e) {
        log.error("Error sending tournament update to session {}: {}", session.getId(), e.getMessage());
      }
    }
  }

  private void organizeTournament(String tournamentId, int numberOfPlayers) {
    Tournament tournament = receiveTournament(tournamentId);

    Set<User> connectedUsers = tournamentUsers.get(tournamentId);

    connectedUsers.forEach(tournament::addUser);

    tournamentRepository.save(tournament);

    // 4 -> 3
    // 8 -> 7
    // 16 -> 15

    int numberOfFirstRoundMatches = numberOfPlayers / 2;
    int numberOfMatches = numberOfPlayers - 1;

    for (int i = 0; i < numberOfFirstRoundMatches; ++i) {
      Match match = matchService.createMatch(CreateMatchDto.builder()
          .build());
    }
  }


  private int getNumberOfPlayersInTournament(String tournamentId) {
    return tournamentPlayers.get(tournamentId).size();
  }

  public void removePlayerWithSession(WebSocketSession session) {
    String changedTournamentId = tournamentPlayers.entrySet().stream()
        .filter(entry -> entry.getValue().removeIf(p -> p.getWebSocketSession().equals(session)))
        .map(Map.Entry::getKey)
        .findFirst()
        .orElse(null);

    if (changedTournamentId == null) {
      return;
    }

    Tournament tournament = receiveTournament(changedTournamentId);

    TournamentResponseDto tournamentResponseDto = tournamentMapper.toResponseDto(
        tournament, getNumberOfPlayersInTournament(changedTournamentId));

    try {
      String json = OBJECT_MAPPER.writeValueAsString(tournamentResponseDto);

      tournamentPlayers.getOrDefault(changedTournamentId, Set.of())
          .forEach(player -> {
            messageDispatcher.sendMessage(player.getWebSocketSession(), json);
            messageDispatcher.unregisterSession(session);

          });

    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize tournament DTO", e);
    }
  }

  private boolean isTournamentFull(String tournamentId) {
    return receiveTournament(tournamentId).getNumberOfPlayers() == tournamentPlayers.get(tournamentId).size();
  }
}