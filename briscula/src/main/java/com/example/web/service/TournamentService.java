package com.example.web.service;

import com.example.briscula.user.player.Bot;
import com.example.briscula.user.player.RealPlayer;
import com.example.briscula.user.player.RoomPlayerId;
import com.example.web.dto.Message;
import com.example.web.dto.match.CreateAllStartingMatchesInTournamentDto;
import com.example.web.dto.match.MatchesCreatedResponse;
import com.example.web.dto.tournament.JoinTournamentRequest;
import com.example.web.dto.tournament.TournamentCreateDto;
import com.example.web.dto.tournament.TournamentResponseDto;
import com.example.web.dto.tournament.TournamentUpdateDto;
import com.example.web.exception.*;
import com.example.web.mapper.TournamentMapper;
import com.example.web.model.*;
import com.example.web.model.enums.TournamentStatus;
import com.example.web.repository.MatchDetailsRepository;
import com.example.web.repository.TournamentRepository;
import com.example.web.repository.UserRepository;
import com.example.web.utils.JsonUtils;
import com.example.web.utils.WebSocketMessageReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.example.web.model.enums.ServerToClientMessageType.*;
import static com.example.web.utils.Constants.OBJECT_MAPPER;
import static com.example.web.utils.Constants.RANDOM;
import static com.example.web.utils.SimpleWebSocketSession.getWebSocketSession;
import static com.example.web.utils.WebSocketMessageSender.sendMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentService {

  private final MatchDetailsRepository matchDetailsRepository;
  private final TournamentRepository tournamentRepository;
  private final UserRepository userRepository;

  private final TournamentMapper tournamentMapper;

  private final GamePrepareService gamePrepareService;
  private final MatchService matchService;
  private final UserService userService;

  private final WebSocketMessageDispatcher messageDispatcher = WebSocketMessageDispatcher.getInstance();

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

        initializeTournamentWithBots(tournament, tournament.getNumberOfBots());
      }
    }
  }

  public TournamentResponseDto create(TournamentCreateDto dto) {
    if (dto.numberOfBots() >= dto.numberOfPlayers())
      throw new TooBigNumberOfBotsException(dto.numberOfBots(), dto.numberOfPlayers());

    Tournament tournament = tournamentMapper.toEntity(dto);
    tournament.setId(UUID.randomUUID().toString());
    tournamentRepository.save(tournament);
    log.debug("Tournament saved: {}", tournament);

    tournamentPlayers.put(tournament.getId(), new HashSet<>());
    tournamentUsers.put(tournament.getId(), new HashSet<>());
    tournamentPlayersWinners.put(tournament.getId(), new HashSet<>());
    tournamentIdNumberOfWinners.put(tournament.getId(), tournament.getNumberOfPlayers() / 2);

    initializeTournamentWithBots(tournament, dto.numberOfBots());

    return tournamentMapper.toResponseDto(tournament, dto.numberOfBots());
  }

  private void initializeTournamentWithBots(Tournament tournament, int numberOfBots) {
    if (numberOfBots > 0) {
      for (int i = 0; i < numberOfBots; ++i) {
        String botName = "bot" + (RANDOM.nextInt(20) + 1);

        User userBot = userService.retrieveUserByUsername(botName);


        /*
        This session is created and added for Bot player to not break equals function that uses
        id of websocket session, but this session is not used.
         */
        WebSocketSession dummyWebSocketSession = getWebSocketSession();
        Bot bot = new Bot(botName, dummyWebSocketSession);

        tournamentPlayers.get(tournament.getId()).add(ConnectedPlayer.builder()
            .userId(userBot.getId())
            .player(bot)
            .build());

        tournamentUsers.get(tournament.getId()).add(userBot);
      }
    }
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

  public TournamentResponseDto update(String id, TournamentUpdateDto dto) {
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

  public void handle(WebSocketSession session, WebSocketMessage<?> message) throws JsonProcessingException {
    try {
      String tournamentId = WebSocketMessageReader.getValueFromJsonMessage(message, "tournamentId");
      String playerId = WebSocketMessageReader.getValueFromJsonMessage(message, "playerId");

      joinTournament(JoinTournamentRequest.builder()
          .tournamentId(tournamentId)
          .userId(playerId)
          .build(), session);
    } catch (UserIsAlreadyInTournamentOrGame e) {
      messageDispatcher.sendMessage(session, JsonUtils.toJson(Message.builder()
          .content(e.getMessage())
          .type(USER_ALREADY_IN_GAME_OR_TOURNAMENT)
          .build()));
    }
  }


  public void joinTournament(JoinTournamentRequest request, WebSocketSession session) {
    log.info("Received join tournament request: userId={}, tournamentId={}", request.userId(), request.tournamentId());

    Tournament tournament = receiveTournament(request.tournamentId());

    if (isTournamentFull(tournament.getId())) {
      log.warn("Tournament {} is full, user {} cannot join", request.tournamentId(), request.userId());
      throw new TournamentIsFullException(request.tournamentId());
    }

    if (messageDispatcher.isSessionInGameOrTournament(session)) {
      throw new UserIsAlreadyInTournamentOrGame(session);
    }

    messageDispatcher.joinGameOrTournament(session);

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> {
          log.warn("User with id {} not found", request.userId());
          return new UserNotFoundException(request.userId());
        });

    ConnectedPlayer connectedPlayer = new ConnectedPlayer(new RealPlayer(user.getUsername(), session), true);

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
            .numberOfBots(tournament.getNumberOfBots())
            .userIds(tournamentUsers.get(tournamentId).stream().map(User::getId).toList())
        .build());
  }

  public synchronized void collectWinnersForNextPhase(String tournamentId, String matchId,
      ConnectedPlayer winner, ConnectedPlayer loser) {

    matchService.updateResult(matchId, winner, loser);

    if (!isMatchOver(tournamentId, matchService.retrieveMatch(matchId))) {
      startRoundForMatch(matchId, tournamentId);
      return;
    }

    Set<ConnectedPlayer> winners =  tournamentPlayersWinners.get(tournamentId);
    winners.add(winner);

    boolean nextRoundCanStart = winners.size() == tournamentIdNumberOfWinners.get(tournamentId);

    if (nextRoundCanStart) {

      if (winners.size() == 1) {
        finishTournament(tournamentId, winner, loser);
        return;
      }

      notifyPlayersAfterFinishedMatch(winner, loser, matchId);

      tournamentIdNumberOfWinners.put(tournamentId, tournamentIdNumberOfWinners.get(tournamentId) / 2);
      Set<ConnectedPlayer> tempWinners = new HashSet<>(winners);
      winners.clear();
      startNextRound(tempWinners, tournamentId);
    }
    else {
      notifyPlayersAfterFinishedMatch(winner, loser, matchId);
    }
  }

  private void notifyPlayersAfterFinishedMatch(ConnectedPlayer winner, ConnectedPlayer loser, String matchId) {
    winner.getPlayer().sendMessageToWaitForNextMatch();
    loser.getPlayer().sentLoosingMessage();
    log.info("Winner for match with id {} is {}, and loser is {}.", matchId, winner.getId(), loser.getId());
    messageDispatcher.leftGameOrTournament(loser.getWebSocketSession());
  }

  private boolean isMatchOver(String tournamentId, Match match) {
    List<MatchDetails> winners = matchDetailsRepository
        .findAllByMatchId_IdAndNumberOfWinsGreaterThanEqual(match.getId(), 1);

    if (winners.isEmpty()) return false;

    int roundsToWin = receiveTournament(tournamentId).getRoundsToWin();

    return hasAnyPlayerReachedRequiredWins(winners, roundsToWin);
  }

  private boolean hasAnyPlayerReachedRequiredWins(List<MatchDetails> winners, int roundsToWin) {
    return winners.stream()
        .anyMatch(details -> details.getNumberOfWins() >= roundsToWin);
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
      log.info("Start match with id {}.", match.getId());
      gamePrepareService.startProcessForGameStartForMatch(match, getPlayersForMatch(matchesCreatedResponse.tournamentId(), match));
    }
  }

  public void startRoundForMatch(String matchId, String tournamentId) {
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


    gamePrepareService.startProcessForGameStartForMatch(match,matchPlayers);
  }

  private Set<ConnectedPlayer> getPlayersForMatch(String tournamentId, Match match) {
    List<String> userIds = match.getUsers().stream().map(User::getId).toList();
    return  tournamentPlayers.get(tournamentId)
        .stream()
        .filter(player -> userIds.contains(player.getUserId()))
        .collect(Collectors.toSet());
  }

  public void finishTournament(String tournamentId, ConnectedPlayer winner, ConnectedPlayer loser) {
    log.info("Finishing tournament with id {}.\n Winner is {}.",
        tournamentId, winner.getPlayer().getNickname());

    messageDispatcher.leftGameOrTournament(winner.getWebSocketSession());
    messageDispatcher.leftGameOrTournament(loser.getWebSocketSession());
    sendMessage(winner.getWebSocketSession(), TOURNAMENT_WON);
    sendMessage(loser.getWebSocketSession(), TOURNAMENT_LOST);

    Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new EntityNotFoundException(tournamentId));

    tournamentPlayers.get(tournamentId).clear();
    tournamentUsers.get(tournamentId).clear();
    tournamentPlayersWinners.get(tournamentId).clear();
    tournamentIdNumberOfWinners.put(tournament.getId(), tournament.getNumberOfPlayers() / 2);
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

  public void handleLeaving(WebSocketSession session, WebSocketMessage<?> message) {
  }
}