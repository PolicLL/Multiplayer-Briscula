package com.example.web.model;

import com.example.briscula.game.Game;
import com.example.briscula.model.card.Card;
import com.example.briscula.user.player.RealPlayer;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import com.example.web.model.enums.GameEndStatus;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@ToString
public class GameRoom {

  private String roomId;
  private final List<ConnectedPlayer> players = new LinkedList<>();
  private final Game game;

  private String matchId;

  private int playerIndex = 0;
  private int numberOfPlayersThatLeft = 0;
  private boolean shouldGameStop = false;

  public GameRoom(Collection<ConnectedPlayer> playerList,
      GameOptionNumberOfPlayers gameOptionNumberOfPlayers, boolean showPoints) {
    this.roomId = UUID.randomUUID().toString().substring(0, 6);

    // TODO: It's not clean, but I will set setting of index like this for now.
    playerList.forEach(player -> {
      player.setId(playerIndex);
      player.setRoomId(roomId);

      if (player.getPlayer() instanceof RealPlayer tempRealPlayer) {
        tempRealPlayer.getRoomPlayerId().setRoomId(roomId);
        tempRealPlayer.getRoomPlayerId().setPlayerId(playerIndex);
      }

      ++playerIndex;

    });

    this.players.addAll(playerList);

    this.game = new Game(gameOptionNumberOfPlayers, players, showPoints);

  }

  public List<Card> getCardsForPlayer(final int playerId) {
    return game.getCardsForPlayer(playerId);
  }

  public Card getMainCard() {
    return game.getMainCard();
  }

  public GameEndStatus startGame() {
    log.info("Game is staring.");
    while (!game.isGameOver() && (!shouldGameStop)) {
      game.playRound();
    }

    log.info("Game ended.");
    return game.notifyPlayersAndGetWinner();
  }

  public void notifyPlayerLeft(int playerId) {
    if (players.get(playerId).getPlayer() instanceof RealPlayer realPlayer) {
      realPlayer.setWaitingTimeForChoosingCardInSeconds(0);
    }

    ++numberOfPlayersThatLeft;

    if (numberOfPlayersThatLeft == players.size()) {
      this.shouldGameStop = true;
      log.info("All players left room with id {}, game stops.", roomId);
    }
  }

  public boolean isShowingPoints()  {
    return game.isShowPoints();
  }
}
