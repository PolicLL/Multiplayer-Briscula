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
  private String userId;
  private String roomId;
  private WebSocketSession webSocketSession;
  private Player player;
  private boolean initialCardsReceived = false;

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

  public void resetValues() {
    this.getPlayer().resetPoints();
    this.setInitialCardsReceived(false);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConnectedPlayer that = (ConnectedPlayer) o;
    return Objects.equals(webSocketSession.getId(), that.webSocketSession.getId())
        || Objects.equals(player.getNickname(), that.player.getNickname());
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public String toString() {
    return "ConnectedPlayer{" +
        "id=" + id +
        ", player=" + player.getNickname() +
        '}';
  }
}
