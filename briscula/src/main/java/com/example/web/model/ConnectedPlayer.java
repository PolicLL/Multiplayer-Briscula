package com.example.web.model;

import com.example.briscula.user.player.Player;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

@Data
@Builder
@AllArgsConstructor
public class ConnectedPlayer {
  private int id;
  private String roomId;
  private WebSocketSession webSocketSession;
  private Player player;
  private boolean initialCardsReceived = false;

  // TODO: Points should not be shown even in the first round if they are not enabled.
  @Getter
  private boolean doesWantPointsToShow = true;

  public ConnectedPlayer(WebSocketSession webSocketSession, Player player, boolean doesWantPointsToShow) {
    this.webSocketSession = webSocketSession;
    this.player = player;
    this.doesWantPointsToShow = doesWantPointsToShow;
  }

  public ConnectedPlayer(Player player) {
    this.player = player;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ConnectedPlayer that)) return false;
    if (that.webSocketSession == null) return false;
    return Objects.equals(this.webSocketSession.getId(), that.webSocketSession.getId());
  }

  // TODO: It's a bad idea to have ability to set null for web socket session.

  @Override
  public int hashCode() {
    return Objects.hash(webSocketSession == null ? 0 : webSocketSession.getId());
  }
}
