package com.example.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/game")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class GameController {

  @PostMapping("/start-game/{player-id}")
  public void startGame(@PathVariable("player-id") String playerId) {

  }

}
