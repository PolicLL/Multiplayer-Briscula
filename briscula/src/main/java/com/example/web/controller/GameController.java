package com.example.web.controller;

import com.example.web.dto.JoinGameRequest;
import com.example.web.service.LobbyService;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/game")
public class GameController {

  private final LobbyService lobbyService;

  @PostMapping("/join")
  public ResponseEntity<Map<String, String>> joinGame(@RequestBody JoinGameRequest request) {
    log.info("Received join request from player: {}", request.getPlayerName());
    String playerId = lobbyService.addPlayer(request.getPlayerName());
    log.info("Assigned player ID: {}", playerId);
    return ResponseEntity.ok(Map.of("playerId", playerId));
  }

  @GetMapping("/status/{playerId}")
  public ResponseEntity<Map<String, String>> checkGameStatus(@PathVariable String playerId) {
    log.info("Checking game status for player ID: {}", playerId);
    Optional<String> roomIdOpt = lobbyService.getAssignedRoomId(playerId);

    if (roomIdOpt.isPresent()) {
      log.info("Player {} assigned to room {}", playerId, roomIdOpt.get());
      return ResponseEntity.ok(Map.of("status", "READY", "roomId", roomIdOpt.get()));
    }

    log.info("Player {} is still waiting for a room", playerId);
    return ResponseEntity.ok(Map.of("status", "WAITING"));
  }
}
