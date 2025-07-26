package com.example.web.dto;

import com.example.web.model.enums.ServerToClientMessageType;

public record Message (ServerToClientMessageType type, String roomId, int playerId, String content) {

  public Message(ServerToClientMessageType type, String roomId, int playerId) {
    this(type, roomId, playerId, null);
  }

  public Message(ServerToClientMessageType type, String content) {
    this(type, null, 0, content);
  }

  public Message(ServerToClientMessageType type) {
    this(type, null, 0, null);
  }
}

