package com.example.web.model;

import com.example.briscula.user.player.Player;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.socket.WebSocketSession;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class ConnectedPlayer {
  private final String id;
  private WebSocketSession webSocketSession;
  private Player player;

  public ConnectedPlayer(WebSocketSession webSocketSession, Player player) {
    this.id = UUID.randomUUID().toString().substring(0, 6);
    this.webSocketSession = webSocketSession;
    this.player = player;
  }

  @Override
  public String toString() {
    return this.player.toString();
  }
}
