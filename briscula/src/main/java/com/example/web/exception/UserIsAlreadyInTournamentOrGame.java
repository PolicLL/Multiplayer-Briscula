package com.example.web.exception;

import org.springframework.web.socket.WebSocketSession;

public class UserIsAlreadyInTournamentOrGame extends RuntimeException {

  public UserIsAlreadyInTournamentOrGame(WebSocketSession session) {
    super(String.format("User with session %s is already in the tournament or game.", session));
  }
}
