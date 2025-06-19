package com.example.web.model;

import com.example.briscula.user.player.Player;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@Data
@AllArgsConstructor
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

  public ConnectedPlayer(Player player) {
    this.player = player;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ConnectedPlayer that)) return false;
    return Objects.equals(this.player, that.player);
  }

  @Override
  public int hashCode() {
    return Objects.hash(player);
  }
}
