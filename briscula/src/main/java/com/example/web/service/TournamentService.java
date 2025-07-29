package com.example.web.service;


import static com.example.web.model.enums.ServerToClientMessageType.RESTARTING_MATCH;
import static com.example.web.model.enums.ServerToClientMessageType.TOURNAMENT_UPDATE;
import static com.example.web.model.enums.ServerToClientMessageType.TOURNAMENT_WON;
import static com.example.web.utils.Constants.OBJECT_MAPPER;
import static com.example.web.utils.WebSocketMessageSender.sendMessage;

import com.example.briscula.user.player.RealPlayer;
import com.example.briscula.user.player.RoomPlayerId;
import com.example.web.dto.Message;
import com.example.web.dto.match.CreateAllStartingMatchesInTournamentDto;
import com.example.web.dto.match.MatchesCreatedResponse;
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
import com.example.web.utils.JsonUtils;
import com.example.web.utils.WebSocketMessageReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentService {

  private final TournamentRepository tournamentRepository;
  private final UserRepository userRepository;
  private final TournamentMapper tournamentMapper;

  private final GameRoomService gameRoomService;

  private final MatchService matchService;

  private final WebSocketMessageDispatcher messageDispatcher;

  private final UserService userService;


  private final Map<String, Set<ConnectedPlayer>> tournamentPlayers = new HashMap<>();
  private final Map<String, Set<User>> tournamentUsers = new HashMap<>();
  private final Map<String, Set<ConnectedPlayer>> tournamentPlayersWinners = new HashMap<>();
  private final Map<String, Integer> tournamentIdNumberOfWinners = new HashMap<>();

  @PostConstruct
  public void init() {
    for (Tournament tournament : tournamentRepository.findAll()) {
      if (tournament.getStatus().equals(TournamentStatus.INITIALIZING)) {
        tournamentPlayers.put(tournament.getId(), new HashSet<>());
        tournamentUsers.put(tournament.getId(), new HashSet<>());
        tournamentPlayersWinners.put(tournament.getId(), new HashSet<>());
        tournamentIdNumberOfWinners.put(tournament.getId(), tournament.getNumberOfPlayers() / 2);
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
    tournamentPlayersWinners.put(tournament.getId(), new HashSet<>());
    tournamentIdNumberOfWinners.put(tournament.getId(), tournament.getNumberOfPlayers() / 2);

    return tournamentMapper.toResponseDto(tournament, 0);
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
    return tournamentRepository.findWithUsersById(id)
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

    connectedPlayer.setUserId(user.getId());

    if (!tournamentPlayers.get(tournament.getId()).add(connectedPlayer)) {
      throw new UserAlreadyAssignedToTournament();
    }

    tournamentPlayers.get(request.tournamentId()).add(connectedPlayer);
    tournamentUsers.get(request.tournamentId()).add(user);


    if (isTournamentFull(tournament.getId()))
      tournament.setStatus(TournamentStatus.IN_PROGRESS);

    TournamentResponseDto tournamentResponse = tournamentMapper.toResponseDto(
        tournament, getNumberOfPlayersInTournament(tournament.getId()));

    broadcastTournamentUpdate(tournamentResponse, isTournamentFull(tournament.getId()));

    return tournamentResponse;
  }

  @Transactional
  private void broadcastTournamentUpdate(TournamentResponseDto tournamentResponseDto, boolean isFull) {
    log.info("Broadcasting tournament update to players.");


    Set<WebSocketSession> webSocketSessions = messageDispatcher.getRegisteredWebSocketSessions();


    for (WebSocketSession session : webSocketSessions) {
      try {
        if (session.isOpen()) {
          String tournamentUpdateContent = OBJECT_MAPPER.writeValueAsString(tournamentResponseDto);

          Message tournamentUpdateMessage = new Message(TOURNAMENT_UPDATE, tournamentUpdateContent);

          messageDispatcher.sendMessage(session, JsonUtils.toJson(tournamentUpdateMessage));
        }
      } catch (Exception e) {
        log.error("Error sending tournament update to session {}: {}", session.getId(), e.getMessage());
      }
    }

    if (isFull) {
      log.info("Tournament with id {} is full.", tournamentResponseDto.id());
      MatchesCreatedResponse createdMatches =
          organizeTournament(tournamentResponseDto.id(), tournamentResponseDto.numberOfPlayers());
      startTournament(createdMatches);
    }
  }

  private MatchesCreatedResponse organizeTournament(String tournamentId, int numberOfPlayers) {
    Tournament tournament = receiveTournament(tournamentId);

    Set<User> connectedUsers = tournamentUsers.get(tournamentId);

    connectedUsers.forEach(tournament::addUser);

    tournamentRepository.save(tournament);

    return matchService.createMatches(CreateAllStartingMatchesInTournamentDto.builder()
            .tournamentId(tournamentId)
            .numberOfPlayers(numberOfPlayers)
            .userIds(tournamentUsers.get(tournamentId).stream().map(User::getId).toList())
        .build());
  }

  public synchronized void collectWinnersForNextPhase(String tournamentId, ConnectedPlayer winner, ConnectedPlayer loser) {
    Set<ConnectedPlayer> winners =  tournamentPlayersWinners.get(tournamentId);
    winners.add(winner);

    boolean nextRoundCanStart = winners.size() == tournamentIdNumberOfWinners.get(tournamentId);

    if (nextRoundCanStart) {

      if (winners.size() == 1) {
        finishTournament(tournamentId, winner);
        return;
      }

      winner.getPlayer().sendMessageToWaitForNextMatch();
      loser.getPlayer().sentLoosingMessage();

      tournamentIdNumberOfWinners.put(tournamentId, tournamentIdNumberOfWinners.get(tournamentId) / 2);
      Set<ConnectedPlayer> tempWinners = new HashSet<>(winners);
      winners.clear();
      startNextRound(tempWinners, tournamentId);
    }
    else {
      winner.getPlayer().sendMessageToWaitForNextMatch();
      loser.getPlayer().sentLoosingMessage();
    }
  }

  private void startNextRound(Set<ConnectedPlayer> winners,  String tournamentId) {
    MatchesCreatedResponse matches = matchService.createMatches(CreateAllStartingMatchesInTournamentDto.builder()
        .tournamentId(tournamentId)
        .userIds(winners.stream().map(ConnectedPlayer::getUserId).toList())
        .build());

      CompletableFuture
      .runAsync(() -> {}, CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS))
      .thenRun(() -> startTournament(matches));
  }


  private void startTournament(MatchesCreatedResponse matchesCreatedResponse) {
    log.info("Starting tournament with id {}.", matchesCreatedResponse.tournamentId());
    for (Match match : matchesCreatedResponse.matches()) {
      gameRoomService.startGameForMatch(match, getPlayersForMatch(matchesCreatedResponse.tournamentId(), match));
    }
  }

  public void restartMatchWithNoWinner(String matchId, String tournamentId) {
    log.info("Starting Match with id {}.", matchId);
    Match match = matchService.retrieveMatch(matchId);

    Set<ConnectedPlayer> matchPlayers =  getPlayersForMatch(tournamentId, match);

    matchPlayers.forEach(player -> {
      if (player.getPlayer() instanceof RealPlayer realPlayer) {
        RoomPlayerId roomPlayerId = realPlayer.getRoomPlayerId();

        sendMessage(player.getWebSocketSession(), RESTARTING_MATCH, roomPlayerId.getRoomId(),
            roomPlayerId.getPlayerId(), "Restarting match.");
      }
    });


    gameRoomService.startGameForMatch(match,matchPlayers);
  }

  private Set<ConnectedPlayer> getPlayersForMatch(String tournamentId, Match match) {
    List<String> userIds = match.getUsers().stream().map(User::getId).toList();
    return  tournamentPlayers.get(tournamentId)
        .stream()
        .filter(player -> userIds.contains(player.getUserId()))
        .collect(Collectors.toSet());
  }

  public void finishTournament(String tournamentId, ConnectedPlayer winner) {
    log.info("Finishing tournament with id {}.\n Winner is {}.",
        tournamentId, winner.getPlayer().getNickname());

    sendMessage(winner.getWebSocketSession(), TOURNAMENT_WON);
  }

  private int getNumberOfPlayersInTournament(String tournamentId) {
    return tournamentPlayers.get(tournamentId).size();
  }

  public void removePlayerWithSession(WebSocketSession session) {
    String changedTournamentId = null;
    for (Map.Entry<String, Set<ConnectedPlayer>> entry : tournamentPlayers.entrySet()) {
      Set<ConnectedPlayer> players = entry.getValue();
      for (ConnectedPlayer connectedPlayer : players) {
        if (connectedPlayer.getWebSocketSession().getId().equals(session.getId())) {
          changedTournamentId = entry.getKey();
          players.remove(connectedPlayer);
          break;
        }
      }
    }

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