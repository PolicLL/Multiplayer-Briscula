package com.example.web.exception;

public class TournamentIsFullException extends RuntimeException {

  public TournamentIsFullException(String tournamentId) {
    super(String.format("Tournament with id '%s' is full.", tournamentId));
  }
}