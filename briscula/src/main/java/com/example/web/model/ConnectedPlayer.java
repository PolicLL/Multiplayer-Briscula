package com.example.web.model;

import com.example.briscula.user.player.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.socket.WebSocketSession;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class ConnectedPlayer {
  private int id;
  private WebSocketSession webSocketSession;
  private Player player;

  public ConnectedPlayer(WebSocketSession webSocketSession, Player player) {
    this.webSocketSession = webSocketSession;
    this.player = player;
  }

  @Override
  public String toString() {
    return this.player.toString();
  }
}
