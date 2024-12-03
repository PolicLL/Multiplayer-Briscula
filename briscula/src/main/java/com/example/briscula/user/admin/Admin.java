package com.example.briscula.user.admin;

import com.example.briscula.model.card.Card;
import com.example.briscula.model.card.CardType;
import com.example.briscula.model.card.Deck;
import com.example.briscula.user.player.AbstractPlayer;
import com.example.briscula.user.player.Bot;
import com.example.briscula.user.player.RealPlayer;
import com.example.briscula.utilities.constants.GameMode;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import lombok.Getter;

@Getter
public class Admin {

  private final Random random = new Random();
  private CardType mainCardType;
  private final Deck deck;

  private List<AbstractPlayer> players;
  private List<List<Card>> playersCardsList;

  private int indexOfCurrentPlayer = 0;

  public Admin() {
    deck = new Deck();
  }

  public void prepareDeckAndPlayers(GameOptionNumberOfPlayers gameOptions, GameMode gameMode) {
    prepareDeck(gameOptions);
    initializePlayers(gameOptions, gameMode);
    chooseMainCardType();
    chooseStartingPlayer();
    System.out.println("STARTING PLAYER : " + indexOfCurrentPlayer);
  }

  private void prepareDeck(GameOptionNumberOfPlayers gameOptions) {
    if (gameOptions == GameOptionNumberOfPlayers.THREE_PLAYERS) deck.removeOneWithCardValueTwo();
  }

  private void initializePlayers(GameOptionNumberOfPlayers gameOptions, GameMode gameMode) {
    dealCards(gameOptions);
    if (gameMode == GameMode.ALL_BOTS) addBotPlayers(gameOptions);
    else if (gameMode == GameMode.BOTS_AND_HUMAN) addBotPlayersAndHuman(gameOptions);
  }

  private void addBotPlayers(GameOptionNumberOfPlayers gameOptions) {
    players = new ArrayList<>();
    for (int i = 0; i < gameOptions.getNumberOfPlayers(); ++i) {
      players.add(new Bot(playersCardsList.get(i), "Name " + i));
    }
  }

  private void addBotPlayersAndHuman(GameOptionNumberOfPlayers gameOptions) {
    addBotPlayers(gameOptions);
    players.set(players.size() - 1, new RealPlayer(playersCardsList.get(playersCardsList.size() - 1), "Human Player"));
  }

  private void dealCards(GameOptionNumberOfPlayers gameOptions) {
    playersCardsList = new ArrayList<>();
    List<Card> deckCards = deck.getDeckCards();
    int cardsPerPlayer = getStartNumberOfCards(gameOptions);

    for (int i = 0; i < gameOptions.getNumberOfPlayers(); ++i) {
      List<Card> playerCards = new LinkedList<>();
      for (int j = 0; j < cardsPerPlayer; ++j) {
        Card card = deckCards.remove(random.nextInt(deckCards.size()));
        playerCards.add(card);
      }
      playersCardsList.add(playerCards);
    }
  }

  private void chooseStartingPlayer() {
    indexOfCurrentPlayer = random.nextInt(players.size());
  }

  private void chooseMainCardType() {
    mainCardType = CardType.values()[random.nextInt(CardType.values().length)];
  }

  public void dealNextRound() {
    if (deck.getNumberOfDeckCards() == 0) return;
    players.forEach(player -> player.addCard(deck.removeOneCard()));
  }

  public AbstractPlayer getCurrentPlayer() {
    AbstractPlayer currentPlayer = players.get(indexOfCurrentPlayer);
    indexOfCurrentPlayer = (indexOfCurrentPlayer + 1) % players.size();
    return currentPlayer;
  }

  private int getStartNumberOfCards(GameOptionNumberOfPlayers gameOptions) {
    return switch (gameOptions) {
      case TWO_PLAYERS, FOUR_PLAYERS -> 4;
      case THREE_PLAYERS -> 3;
    };
  }

  public boolean isGameOver() {
    return deck.getNumberOfDeckCards() == 0 &&
        players.stream().allMatch(AbstractPlayer::isPlayerDone);
  }
}
