package com.example.briscula.game;

import com.example.briscula.model.card.Card;
import com.example.briscula.model.card.CardType;
import com.example.briscula.model.card.CardValue;
import com.example.briscula.user.admin.Admin;
import com.example.briscula.user.player.Player;
import com.example.briscula.utilities.constants.GameMode;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Game {
  private final GameOptionNumberOfPlayers gameOptions;
  private final Admin admin;
  private final RoundJudge roundJudge;

  public Game(GameOptionNumberOfPlayers gameOptions, GameMode gameMode, List<Player> players) {
    this.gameOptions = gameOptions;
    this.admin = new Admin();
    this.roundJudge = new RoundJudge(admin.getMainCardType());

    admin.prepareDeckAndPlayers(gameOptions, gameMode, players);

    log.info("Main card type : " + admin.getMainCardType());
  }

  public List<Card> getCardsForPlayer(int playerId) {
    return List.of(new Card(CardType.COPPE, CardValue.TWO), new Card(CardType.DENARI, CardValue.KNIGHT));
  }

  public boolean isGameOver() {
    return admin.isGameOver();
  }

  public void playRound() {
    Queue<Move> queueMoves = new ArrayDeque<>();

    for (int i = 0; i < gameOptions.getNumberOfPlayers(); i++) {
      Player player = admin.getCurrentPlayer();
      Card card = player.playRound();
      queueMoves.add(new Move(player, card));

      log.info("Move " + i + " -> " + player.getNickname() + " | " + card);
    }

    RoundWinner roundWinner = roundJudge.calculateRound(queueMoves);
    roundWinner.player().incrementPoints(roundWinner.numberOfPoints());

    admin.dealNextRound();
    logPlayersValues();
  }

  private void logPlayersValues() {
    admin.getPlayers().forEach(player ->
        log.info("[" + player.getNickname() + "] : " + player.getPoints()));
    log.info("\n");
  }
}