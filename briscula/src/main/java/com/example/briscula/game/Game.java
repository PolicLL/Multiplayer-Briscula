package com.example.briscula.game;

import static com.example.briscula.utilities.constants.GameOptionNumberOfPlayers.FOUR_PLAYERS;

import com.example.briscula.model.card.Card;
import com.example.briscula.user.admin.Admin;
import com.example.briscula.user.player.Player;
import com.example.briscula.user.player.RealPlayer;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import com.example.web.model.ConnectedPlayer;
import com.example.web.model.enums.GameEndStatus;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Game {
  private final GameOptionNumberOfPlayers gameOptions;
  private final Admin admin;

  @Getter
  private boolean showPoints;

  public Game(GameOptionNumberOfPlayers gameOptions, List<ConnectedPlayer> players, boolean showPoints) {
    this.gameOptions = gameOptions;
    this.admin = new Admin();
    this.showPoints = showPoints;

    admin.prepareDeckAndPlayers(gameOptions, players);

    log.info("Main card type : " + admin.getMainCard().cardType());
  }

  public List<Card> getCardsForPlayer(int playerId) {
    return admin.getCardsForPlayer(playerId);
  }

  public List<Card> getCards() {
    return admin.getListOfCardsForAllPlayers()
            .stream()
            .flatMap(List::stream)
            .toList();
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
      Move newMove = new Move(player, card);
      queueMoves.add(newMove);

      sentMessageAboutNewCardToOtherPlayers(newMove);

      log.info("Move " + i + " -> " + player.getNickname() + " | " + card);
    }

    RoundWinner roundWinner = RoundJudge.calculateRound(queueMoves, admin.getMainCard().cardType());
    roundWinner.player().incrementPoints(roundWinner.numberOfPoints());

    admin.dealNextRound(roundWinner);
    updateCardsAndPointsState().join();

    logPlayersValues();
    log.info("ROUND ENDED.");
  }

  private void sentMessageAboutNewCardToOtherPlayers(Move newMove) {
    admin.getPlayers().stream()
        .map(ConnectedPlayer::getPlayer)
        .forEach(player -> player.sentMessageAboutNewCardFromAnotherPlayer(newMove.card(), player.equals(newMove.player())));
  }

  public Card getMainCard() {
    return admin.getMainCard();
  }

  public ConnectedPlayer getPlayer(int playerId) {
    return this.admin.getPlayers()
        .stream().filter(player -> player.getId() == playerId)
        .findFirst().get();
  }

  private CompletableFuture<Void> updateCardsAndPointsState() {
    List<CompletableFuture<Void>> futures = new ArrayList<>();

    if (admin.isLastRound() && FOUR_PLAYERS == gameOptions) {
      List<ConnectedPlayer> listPlayers = admin.getPlayers();

      futures.add(listPlayers.get(0).getPlayer().sentInformationAboutColleaguesCards(admin.getCardsForPlayer(2)));
      futures.add(listPlayers.get(1).getPlayer().sentInformationAboutColleaguesCards(admin.getCardsForPlayer(3)));
      futures.add(listPlayers.get(2).getPlayer().sentInformationAboutColleaguesCards(admin.getCardsForPlayer(0)));
      futures.add(listPlayers.get(3).getPlayer().sentInformationAboutColleaguesCards(admin.getCardsForPlayer(1)));
    }

    for (ConnectedPlayer player : admin.getPlayers()) {
      if (player.getPlayer() instanceof RealPlayer realPlayer) {
        if (admin.isLastRound()) {
          if (!this.showPoints) {
            showPoints = true;
          }
          realPlayer.sentMessageAboutRemovingMainCard();
        }

        futures.add(realPlayer.sentMessageAboutNewCardsAndPoints(showPoints));
      }
    }

    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
  }

  private void logPlayersValues() {
    admin.getPlayers().forEach(player ->
        log.info("[" + player.getPlayer().getNickname() + "] : " + player.getPlayer().getPoints()));
    log.info("\n");
  }

  public GameEndStatus notifyPlayersAndGetWinner() {
    return admin.notifyPlayers();
  }
}