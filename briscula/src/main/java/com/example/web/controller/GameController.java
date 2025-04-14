package com.example.web.controller;

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

  @PostMapping("/join")
  public ResponseEntity<Map<String, String>> joinGame(@RequestBody JoinGameRequest request) {
    String playerId = lobbyService.addPlayer(request.getPlayerName());
    return ResponseEntity.ok(Map.of("playerId", playerId));
  }

  @GetMapping("/status/{playerId}")
  public ResponseEntity<Map<String, String>> checkGameStatus(@PathVariable String playerId) {
    Optional<String> roomIdOpt = lobbyService.getAssignedRoomId(playerId);

    return roomIdOpt.map(string -> ResponseEntity.ok(Map.of(
        "status", "READY",
        "roomId", string
    ))).orElseGet(() -> ResponseEntity.ok(Map.of("status", "WAITING")));

  }


}
