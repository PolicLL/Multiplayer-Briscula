package com.example.briscula.controller;

import com.example.briscula.game.Game;
import com.example.briscula.utilities.constants.GameMode;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
public class ItemController {

  @GetMapping
  public List<String> getItems() {
    return List.of("Item1", "Item2", "Item3");
  }

  @PostMapping
  public String startGame() {
    //new Game(GameOptionNumberOfPlayers.FOUR_PLAYERS, GameMode.BOTS_AND_HUMAN).startGame();
    return "INSTRUCTIONS 123";
  }

  private void playTheRound() {

  }
}
