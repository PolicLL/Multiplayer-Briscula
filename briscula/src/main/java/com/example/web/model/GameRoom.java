package com.example.web.model;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Data
public class GameRoom {

  private String roomId;
  private final Set<ConnectedPlayer> players = ConcurrentHashMap.newKeySet();

  public GameRoom(Collection<ConnectedPlayer> playerList) {
    this.roomId = UUID.randomUUID().toString();
    this.players.addAll(playerList);
  }

  public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {

  }
}
