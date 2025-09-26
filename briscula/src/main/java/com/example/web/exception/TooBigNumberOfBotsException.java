package com.example.web.exception;

public class TooBigNumberOfBotsException extends IllegalArgumentException {

    public TooBigNumberOfBotsException(int numberOfBots, int numberOfPlayers) {
        super(String.format("Number of bots %s is bigger or equal to number of players %s.", numberOfBots, numberOfPlayers));
    }
}