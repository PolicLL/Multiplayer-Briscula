package com.example.briscula.game;

import com.example.briscula.model.card.Card;
import com.example.briscula.user.admin.Admin;
import com.example.briscula.user.player.Player;
import com.example.briscula.user.player.RealPlayer;
import com.example.briscula.utilities.constants.GameMode;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import com.example.web.model.ConnectedPlayer;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Game {
  private final GameOptionNumberOfPlayers gameOptions;
  private final Admin admin;

  public Game(GameOptionNumberOfPlayers gameOptions, GameMode gameMode, List<ConnectedPlayer> players) {
    this.gameOptions = gameOptions;
    this.admin = new Admin();

    admin.prepareDeckAndPlayers(gameOptions, gameMode, players);

    log.info("Main card type : " + admin.getMainCardType());
  }

  public List<Card> getCardsForPlayer(int playerId) {
    return admin.getCardsForPlayer(playerId);
  }

  public boolean isGameOver() {
    return admin.isGameOver();
  }

  public void playRound() {
    Queue<Move> queueMoves = new ArrayDeque<>();

    int numberOfIterations = gameOptions.getNumberOfPlayers();
    if (numberOfIterations == 2) numberOfIterations *= 2;

    for (int i = 0; i < numberOfIterations; i++) {
      Player player = admin.getCurrentPlayer();
      Card card = player.playRound();
      queueMoves.add(new Move(player, card));

      log.info("Move " + i + " -> " + player.getNickname() + " | " + card);
    }

    RoundWinner roundWinner = RoundJudge.calculateRound(queueMoves, admin.getMainCardType());
    roundWinner.player().incrementPoints(roundWinner.numberOfPoints());

    admin.dealNextRound();

    for (ConnectedPlayer player : admin.getPlayers()) {
      if (player.getPlayer() instanceof RealPlayer realPlayer) {
        realPlayer.sentMessageAboutNewCards(admin.getCardsForPlayer(realPlayer.getRoomPlayerId().getPlayerId()));
      }
    }

    log.info("ROUND ENDED.");
    logPlayersValues();
  }

  public ConnectedPlayer getPlayer(int playerId) {
    return this.admin.getPlayers()
        .stream().filter(player -> player.getId() == playerId)
        .findFirst().get();
  }

  private void logPlayersValues() {
    admin.getPlayers().forEach(player ->
        log.info("[" + player.getPlayer().getNickname() + "] : " + player.getPlayer().getPoints()));
    log.info("\n");
  }
}