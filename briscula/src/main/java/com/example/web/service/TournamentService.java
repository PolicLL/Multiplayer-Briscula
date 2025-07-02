package com.example.web.service;


import com.example.web.dto.tournament.TournamentCreateDto;
import com.example.web.dto.tournament.TournamentResponseDto;
import com.example.web.mapper.TournamentMapper;
import com.example.web.model.Tournament;
import com.example.web.repository.TournamentRepository;
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

  private final TournamentRepository repository;
  private final TournamentMapper mapper;

  public TournamentResponseDto create(TournamentCreateDto dto) {
    Tournament tournament = mapper.toEntity(dto);
    tournament.setId(UUID.randomUUID().toString());
    repository.save(tournament);
    log.debug("Tournament saved: {}", tournament);
    return mapper.toResponseDto(tournament);
  }

  public TournamentResponseDto getById(String id) {
    log.info("Fetching tournament with ID: {}", id);
    Tournament tournament = repository.findById(id)
        .orElseThrow(() -> {
          log.warn("Tournament not found: {}", id);
          return new EntityNotFoundException("Tournament not found with id: " + id);
        });
    return mapper.toResponseDto(tournament);
  }

  public List<TournamentResponseDto> getAll() {
    log.info("Fetching all tournaments");
    return repository.findAll().stream()
        .map(mapper::toResponseDto)
        .toList();
  }

  public TournamentResponseDto update(String id, TournamentCreateDto dto) {
    log.info("Updating tournament with ID: {}", id);
    if (!repository.existsById(id)) {
      log.warn("Cannot update, tournament not found: {}", id);
      throw new EntityNotFoundException("Tournament not found with id: " + id);
    }

    Tournament newTournament =  mapper.toEntity(dto);
    newTournament.setId(id);

    System.out.println(newTournament);

    repository.save(newTournament);
    log.debug("Updated tournament: {}", newTournament);
    return mapper.toResponseDto(newTournament);
  }

  public void delete(String id) {
    log.info("Deleting tournament with ID: {}", id);
    if (!repository.existsById(id)) {
      log.warn("Cannot delete, tournament not found: {}", id);
      throw new EntityNotFoundException("Tournament not found with id: " + id);
    }
    repository.deleteById(id);
    log.info("Tournament deleted: {}", id);
  }
}