package com.example.briscula.user.admin;

import static com.example.briscula.utilities.constants.Constants.HUMAN_PLAYER;
import static com.example.briscula.utilities.constants.Constants.getRandomNumber;

import com.example.briscula.game.Game;
import com.example.briscula.model.card.Card;
import com.example.briscula.model.card.CardType;
import com.example.briscula.model.card.Deck;
import com.example.briscula.user.player.Player;
import com.example.briscula.user.player.Bot;
import com.example.briscula.user.player.RealPlayer;
import com.example.briscula.utilities.constants.GameMode;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class Admin {

  private CardType mainCardType;
  private final Deck deck;

  private List<Player> players;
  private List<List<Card>> listOfCardsForAllPlayers;

  private int indexOfCurrentPlayer = 0;

  public Admin() {
    deck = new Deck();
  }

  public void prepareDeckAndPlayers(GameOptionNumberOfPlayers gameOptions, GameMode gameMode, List<Player> players) {
    prepareDeck(gameOptions);
    initializePlayers(gameOptions, gameMode, players);
    chooseMainCardType();
    chooseStartingPlayer();

    log.info("STARTING PLAYER : " + indexOfCurrentPlayer);
  }

  public List<Card> getCardsForPlayer(int playerId) {
    return listOfCardsForAllPlayers.get(playerId);
  }

  private void prepareDeck(GameOptionNumberOfPlayers gameOptions) {
    if (gameOptions == GameOptionNumberOfPlayers.THREE_PLAYERS) deck.removeOneWithCardValueTwo();
  }

  private void initializePlayers(GameOptionNumberOfPlayers gameOptions, GameMode gameMode, List<Player> players) {
    dealCards(gameOptions);
    if (gameMode == GameMode.ALL_BOTS) setAllBotPlayers(gameOptions);
    else if (gameMode == GameMode.BOTS_AND_HUMAN) addBotPlayersAndHuman(gameOptions);
    else if(gameMode == GameMode.ALL_HUMANS) this.players = players;
  }

  private void dealCards(GameOptionNumberOfPlayers gameOptions) {
    listOfCardsForAllPlayers = new ArrayList<>();
    List<Card> deckCards = deck.getDeckCards();
    int cardsPerPlayer = getStartNumberOfCards(gameOptions);

    for (int i = 0; i < gameOptions.getNumberOfPlayers(); ++i) {
      List<Card> playerCards = new LinkedList<>();
      for (int j = 0; j < cardsPerPlayer; ++j) {
        Card card = deckCards.remove(getRandomNumber(deckCards.size()));
        playerCards.add(card);
      }
      listOfCardsForAllPlayers.add(playerCards);
    }
  }

  private int getStartNumberOfCards(GameOptionNumberOfPlayers gameOptions) {
    return switch (gameOptions) {
      case TWO_PLAYERS, FOUR_PLAYERS -> 4;
      case THREE_PLAYERS -> 3;
    };
  }


  private void addBotPlayersAndHuman(GameOptionNumberOfPlayers gameOptions) {
    setAllBotPlayers(gameOptions);
    players.set(players.size() - 1, new RealPlayer(
        listOfCardsForAllPlayers.get(listOfCardsForAllPlayers.size() - 1), HUMAN_PLAYER));
  }

  private void setAllBotPlayers(GameOptionNumberOfPlayers gameOptions) {
    players = new ArrayList<>();
    for (int i = 0; i < gameOptions.getNumberOfPlayers(); ++i) {
      players.add(new Bot(listOfCardsForAllPlayers.get(i), "Name " + i));
    }
  }

  private void chooseStartingPlayer() {
    indexOfCurrentPlayer = getRandomNumber(players.size());
  }

  private void chooseMainCardType() {
    mainCardType = CardType.values()[getRandomNumber(CardType.values().length)];
  }

  public void dealNextRound() {
    if (deck.getNumberOfDeckCards() == 0) return;
    players.forEach(player -> player.addCard(deck.removeOneCard()));
  }

  public Player getCurrentPlayer() {
    Player currentPlayer = players.get(indexOfCurrentPlayer);
    indexOfCurrentPlayer = (indexOfCurrentPlayer + 1) % players.size();
    return currentPlayer;
  }


  public boolean isGameOver() {
    return deck.getNumberOfDeckCards() == 0 &&
        players.stream().allMatch(Player::isPlayerDone);
  }
}
