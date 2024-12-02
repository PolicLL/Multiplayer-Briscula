package com.example.briscula.game;

import com.example.briscula.model.card.Card;
import com.example.briscula.user.admin.Admin;
import com.example.briscula.user.player.AbstractPlayer;
import com.example.briscula.utilities.constants.GameMode;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import java.util.ArrayDeque;
import java.util.Queue;

public class GameManager {
  private final GameOptionNumberOfPlayers gameOptions;
  private final Admin admin;
  private final GameJudge gameJudge;

  public GameManager(GameOptionNumberOfPlayers gameOptions, GameMode gameMode) {
    this.gameOptions = gameOptions;
    this.admin = new Admin();
    this.gameJudge = new GameJudge(admin);

    admin.prepareDeckAndPlayers(gameOptions, gameMode);
    System.out.println("Main card type : " + admin.getMainCardType());
  }

  public boolean isGameOver() {
    return admin.getDeck().getNumberOfDeckCards() == 0 && admin.getPlayers().stream().allMatch(AbstractPlayer::isPlayerDone);
  }

  public void playRound() {
    Queue<Move> queueMoves = new ArrayDeque<>();

    for (int i = 0; i < gameOptions.getNumberOfPlayers(); i++) {
      AbstractPlayer player = admin.getCurrentPlayer();
      Card card = player.playRound();
      queueMoves.add(new Move(player, card));

      System.out.println("Move " + i + " -> " + player.getNickname() + " | " + card);
    }

    gameJudge.calculateRound(queueMoves);
    admin.dealNextRound();
    printPlayersValues();
  }

  private void printPlayersValues() {
    admin.getPlayers().forEach(player -> System.out.println("[" + player.getNickname() + "] : " + player.getPoints()));
    System.out.println();
  }
}