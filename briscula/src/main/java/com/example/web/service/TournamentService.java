package com.example.web.service;


import com.example.web.dto.tournament.JoinTournamentResponse;
import com.example.web.dto.tournament.JoinTournamentReuqest;
import com.example.web.dto.tournament.TournamentCreateDto;
import com.example.web.dto.tournament.TournamentResponseDto;
import com.example.web.exception.TournamentIsFullException;
import com.example.web.exception.TournamentWithIdDoesNotExists;
import com.example.web.exception.UserNotFoundException;
import com.example.web.mapper.TournamentMapper;
import com.example.web.model.Tournament;
import com.example.web.model.User;
import com.example.web.repository.TournamentRepository;
import com.example.web.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
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

  public TournamentResponseDto create(TournamentCreateDto dto) {
    Tournament tournament = tournamentMapper.toEntity(dto);
    tournament.setId(UUID.randomUUID().toString());
    tournamentRepository.save(tournament);
    log.debug("Tournament saved: {}", tournament);
    return tournamentMapper.toResponseDto(tournament);
  }

  public JoinTournamentResponse joinTournament(JoinTournamentReuqest request) {
    Tournament tournament = tournamentRepository.findById(request.tournamentId())
        .orElseThrow(() -> new TournamentWithIdDoesNotExists(request.tournamentId()));

    if (tournament.isFull()) {
      throw new TournamentIsFullException(request.tournamentId());
    }

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException(request.userId()));

    tournament.getUsers().add(user);

    return tournamentMapper.toJoinTournamentResponseDto(tournamentRepository.save(tournament));
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

    System.out.println(newTournament);

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