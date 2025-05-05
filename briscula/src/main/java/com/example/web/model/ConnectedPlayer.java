package com.example.web.model;

import com.example.briscula.user.player.Player;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.socket.WebSocketSession;

@Data
@EqualsAndHashCode
public class ConnectedPlayer {
  private int id;
  private String roomId;
  private WebSocketSession webSocketSession;
  private Player player;
  private boolean initialCardsReceived = false;

  public ConnectedPlayer(WebSocketSession webSocketSession, Player player) {
    this.webSocketSession = webSocketSession;
    this.player = player;
  }

  @Override
  public String toString() {
    return this.player.toString();
  }
}
