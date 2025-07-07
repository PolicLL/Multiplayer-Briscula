package com.example.web.controller;

import com.example.web.dto.tournament.JoinTournamentResponse;
import com.example.web.dto.tournament.JoinTournamentRequest;
import com.example.web.dto.tournament.TournamentCreateDto;
import com.example.web.dto.tournament.TournamentResponseDto;
import com.example.web.service.TournamentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/tournament")
public class TournamentController {

  private final TournamentService tournamentService;

  @PostMapping
  public ResponseEntity<TournamentResponseDto> create(@Valid @RequestBody TournamentCreateDto dto) {
    log.info("Received request to create tournament: {}", dto);
    return ResponseEntity.ok(tournamentService.create(dto));
  }

  @PostMapping("/join")
  public ResponseEntity<TournamentResponseDto> joinTournament(@Valid @RequestBody JoinTournamentRequest joinTournamentRequest) {
    log.info("Received request to join tournament: {}", joinTournamentRequest);
    return ResponseEntity.ok(tournamentService.joinTournament(joinTournamentRequest));
  }

  @GetMapping("/{id}")
  public ResponseEntity<TournamentResponseDto> getById(@PathVariable String id) {
    log.info("Received request to get tournament with ID: {}", id);
    return ResponseEntity.ok(tournamentService.getById(id));
  }

  @GetMapping
  public ResponseEntity<List<TournamentResponseDto>> getAll() {
    log.info("Received request to list all tournaments");
    return ResponseEntity.ok(tournamentService.getAll());
  }

  @PutMapping("/{id}")
  public ResponseEntity<TournamentResponseDto> update(
      @PathVariable String id,
      @Valid @RequestBody TournamentCreateDto dto
  ) {
    log.info("Received request to update tournament with ID: {}", id);
    return ResponseEntity.ok(tournamentService.update(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    log.info("Received request to delete tournament with ID: {}", id);
    tournamentService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
