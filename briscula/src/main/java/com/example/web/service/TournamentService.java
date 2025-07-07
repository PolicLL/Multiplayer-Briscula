package com.example.web.service;


import com.example.web.dto.tournament.JoinTournamentRequest;
import com.example.web.dto.tournament.TournamentCreateDto;
import com.example.web.dto.tournament.TournamentResponseDto;
import com.example.web.exception.TournamentIsFullException;
import com.example.web.exception.TournamentWithIdDoesNotExists;
import com.example.web.exception.UserAlreadyAssignedToTournament;
import com.example.web.exception.UserNotFoundException;
import com.example.web.handler.TournamentWebSocketHandler;
import com.example.web.mapper.TournamentMapper;
import com.example.web.model.Tournament;
import com.example.web.model.User;
import com.example.web.model.enums.TournamentStatus;
import com.example.web.repository.TournamentRepository;
import com.example.web.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentService {

  private final TournamentRepository tournamentRepository;
  private final UserRepository userRepository;
  private final TournamentMapper tournamentMapper;

  private final TournamentWebSocketHandler tournamentWebSocketHandler;

  public TournamentResponseDto create(TournamentCreateDto dto) {
    Tournament tournament = tournamentMapper.toEntity(dto);
    tournament.setId(UUID.randomUUID().toString());
    tournamentRepository.save(tournament);
    log.debug("Tournament saved: {}", tournament);
    return tournamentMapper.toResponseDto(tournament);
  }

  public TournamentResponseDto joinTournament(JoinTournamentRequest request) {
    log.info("Received join tournament request: userId={}, tournamentId={}",
        request.userId(), request.tournamentId());

    Tournament tournament = tournamentRepository.findWithUsersById(request.tournamentId())
        .orElseThrow(() -> {
          log.warn("Tournament with id {} not found", request.tournamentId());
          return new TournamentWithIdDoesNotExists(request.tournamentId());
        });

    if (tournament.isFull()) {
      log.warn("Tournament {} is full, user {} cannot join", request.tournamentId(), request.userId());
      throw new TournamentIsFullException(request.tournamentId());
    }

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> {
          log.warn("User with id {} not found", request.userId());
          return new UserNotFoundException(request.userId());
        });

    if (tournament.getUsers().contains(user)) {
      throw new UserAlreadyAssignedToTournament();
    }

    log.info("Adding user {} to tournament {}", user.getId(), tournament.getId());
    tournament.addUser(user);

    if (tournament.isFull())
      tournament.setStatus(TournamentStatus.IN_PROGRESS);

    Tournament savedTournament = tournamentRepository.save(tournament);
    log.info("User {} successfully joined tournament {}", user.getId(), savedTournament.getId());

    TournamentResponseDto tournamentResponse =  tournamentMapper.toResponseDto(savedTournament);

    tournamentWebSocketHandler.broadcastTournamentUpdate(tournamentResponse);

    return tournamentResponse;
  }

  public TournamentResponseDto getById(String id) {
    log.info("Fetching tournament with ID: {}", id);
    Tournament tournament = tournamentRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Tournament not found: {}", id);
          return new EntityNotFoundException("Tournament not found with id: " + id);
        });
    return tournamentMapper.toResponseDto(tournament);
  }

  public List<TournamentResponseDto> getAll() {
    log.info("Fetching all tournaments");
    return tournamentRepository.findAll().stream()
        .map(tournamentMapper::toResponseDto)
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
    return tournamentMapper.toResponseDto(newTournament);
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
}