package com.example.web.exception;

public class UserAlreadyAssignedToTournament extends RuntimeException {

    public UserAlreadyAssignedToTournament(String userId, String tournamentId) {
        super(String.format("User with id %s is already assigned to the tournament with id "
                + "%s.", userId, tournamentId));
    }

    public UserAlreadyAssignedToTournament() {
        super("User is already assigned to some tournament.");
    }
}