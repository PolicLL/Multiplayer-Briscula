package com.example.web.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Random;

public class Constants {

    public static final String ROOM_ID = "roomId";
    public static final String PLAYER_ID = "playerId";

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final Random RANDOM = new Random();

    public static int getRandomNumber(int scope) {
        return RANDOM.nextInt(scope);
    }

}
