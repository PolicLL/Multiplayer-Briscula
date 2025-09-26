package com.example.web.exception;

public class TournamentWithIdDoesNotExists extends RuntimeException {

    public TournamentWithIdDoesNotExists(String tournamentId) {
        super(String.format("Tournament with id '%s' does not exists.", tournamentId));
    }
}