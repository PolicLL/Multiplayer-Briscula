package com.example.briscula.user.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomPlayerId {
    private String roomId;
    private int playerId;
}

