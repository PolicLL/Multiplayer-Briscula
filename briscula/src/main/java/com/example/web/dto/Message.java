package com.example.web.dto;

import com.example.web.model.enums.ServerToClientMessageType;

public record Message (ServerToClientMessageType type, String roomId, int playerId, String content) {

}

