package com.example.briscula.user.admin;

import static com.example.briscula.utilities.constants.Constants.HUMAN_PLAYER;
import static com.example.web.utils.Constants.getRandomNumber;

import com.example.briscula.game.RoundWinner;
import com.example.briscula.model.card.Card;
import com.example.briscula.model.card.CardType;
import com.example.briscula.model.card.CardValue;
import com.example.briscula.model.card.Deck;
import com.example.briscula.user.player.Bot;
import com.example.briscula.user.player.Player;
import com.example.briscula.user.player.RealPlayer;
import com.example.briscula.utilities.constants.GameMode;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import com.example.web.model.ConnectedPlayer;
import com.example.web.model.enums.GameEndStatus;
import com.example.web.model.enums.GameEndStatus.Status;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class Admin {

  private Card mainCard;
  private final Deck deck;

  @Getter
  private List<ConnectedPlayer> players;
  private List<List<Card>> listOfCardsForAllPlayers;

  private int indexOfCurrentPlayer = 0;
  private GameOptionNumberOfPlayers gameOptionNumberOfPlayers;

  public Admin() {
    deck = new Deck();
  }

  public void prepareDeckAndPlayers(GameOptionNumberOfPlayers gameOptions, GameMode gameMode, List<ConnectedPlayer> players) {
    prepareDeck(gameOptions);
    chooseMainCard();
    deck.setLastCard(mainCard);

    initializePlayers(gameOptions, gameMode, players);

    chooseStartingPlayer();

    AtomicInteger index = new AtomicInteger();
    players.forEach(player -> player.getPlayer().setPlayerCards(getCardsForPlayer(
        index.getAndIncrement())));

    this.gameOptionNumberOfPlayers = gameOptions;

    log.info("STARTING PLAYER : " + indexOfCurrentPlayer);
  }

  public List<Card> getCardsForPlayer(int playerId) {
    return listOfCardsForAllPlayers.get(playerId);
  }

  // TODO: Does this check correctly for 3 players ?
  public boolean isLastRound() {
    int cardsInPlayerHand;
    if (gameOptionNumberOfPlayers == GameOptionNumberOfPlayers.THREE_PLAYERS) {
      cardsInPlayerHand = 3;
    } else {
      cardsInPlayerHand = 4;
    }

    return deck.getDeckCards().isEmpty() &&
        listOfCardsForAllPlayers.stream().allMatch(cards -> cards.size() == cardsInPlayerHand);
  }

  private void prepareDeck(GameOptionNumberOfPlayers gameOptions) {
    if (gameOptions == GameOptionNumberOfPlayers.THREE_PLAYERS) deck.removeOneWithCardValueTwo();
  }

  private void initializePlayers(GameOptionNumberOfPlayers gameOptions, GameMode gameMode, List<ConnectedPlayer> players) {
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
    // TODO: Update logic so that web socket session is set for RealPlayers.
    players.set(players.size() - 1, ConnectedPlayer.builder()
            .player(new RealPlayer(listOfCardsForAllPlayers.get(listOfCardsForAllPlayers.size() - 1),
                HUMAN_PLAYER, null))
        .build());
  }

  private void setAllBotPlayers(GameOptionNumberOfPlayers gameOptions) {
    players = new ArrayList<>();
    for (int i = 0; i < gameOptions.getNumberOfPlayers(); ++i) {
      players.add(ConnectedPlayer.builder()
              .player(new Bot(listOfCardsForAllPlayers.get(i), "Name " + i))
          .build());
    }
  }

  private void chooseStartingPlayer() {
    indexOfCurrentPlayer = getRandomNumber(players.size());
  }

  private void chooseMainCard() {
    mainCard = new Card(CardType.values()[getRandomNumber(CardType.values().length)],
        CardValue.values()[getRandomNumber(CardValue.values().length)]);
  }

  public void dealNextRound(RoundWinner roundWinner) {
    if (deck.getNumberOfDeckCards() == 0) return;

    players.forEach(player -> player.getPlayer().addCard(deck.removeOneCard()));

    if (GameOptionNumberOfPlayers.TWO_PLAYERS.equals(gameOptionNumberOfPlayers)) {
      players.forEach(player -> player.getPlayer().addCard(deck.removeOneCard()));
    }

    for (int i = 0; i < players.size(); ++i) {
      if (roundWinner.player() == players.get(i).getPlayer()) {
        indexOfCurrentPlayer = i;
        break;
      }
    }
  }

  public Player getCurrentPlayer() {
    Player currentPlayer = players.get(indexOfCurrentPlayer).getPlayer();
    indexOfCurrentPlayer = (indexOfCurrentPlayer + 1) % players.size();
    return currentPlayer;
  }


  public boolean isGameOver() {
    return deck.getNumberOfDeckCards() == 0 &&
        players.stream().allMatch(player -> player.getPlayer().isPlayerDone());
  }

  public GameEndStatus notifyPlayers() {
    GameEndStatus gameEndStatus = getGameEndStatus();

    if (Status.NO_WINNER.equals(gameEndStatus.status())) {
      players.stream()
          .filter(player -> player.getPlayer() instanceof RealPlayer)
          .map(player -> (RealPlayer) player.getPlayer())
          .forEach(RealPlayer::setNoWinnerMessage);

      return gameEndStatus;
    }


    List<Integer> winnerIds = gameEndStatus.playerResults().entrySet().stream()
        .filter(Map.Entry::getValue)
        .peek(entry -> entry.getKey().getPlayer().sentWinningMessage())
        .map(entry -> entry.getKey().getId())
        .toList();

    players.stream()
        .filter(p -> !winnerIds.contains(p.getId()))
        .map(ConnectedPlayer::getPlayer)
        .forEach(Player::sentLoosingMessage);

    return gameEndStatus;
  }

  private GameEndStatus getGameEndStatus() {
    if (players.stream().map(p -> p.getPlayer().getPoints()).distinct().count() == 1) {
      Map<ConnectedPlayer, Boolean> noWinnerPlayers = this.players.stream()
          .collect(Collectors.toMap(p -> p, p -> false));
      return new GameEndStatus(noWinnerPlayers, Status.NO_WINNER);
    }

    int maxPoints = players.stream()
        .mapToInt(p -> p.getPlayer().getPoints())
        .max()
        .orElseThrow();

    Set<ConnectedPlayer> winners;

    if (gameOptionNumberOfPlayers == GameOptionNumberOfPlayers.FOUR_PLAYERS) {
      winners = (players.get(0).getPlayer().getPoints() +
          players.get(2).getPlayer().getPoints() > 60)
          ? Set.of(players.get(0), players.get(2))
          : Set.of(players.get(1), players.get(3));
    } else {
      winners = players.stream()
          .filter(p -> p.getPlayer().getPoints() == maxPoints)
          .findFirst()
          .map(Set::of)
          .orElse(Set.of());
    }

    Map<ConnectedPlayer, Boolean> results = players.stream()
        .collect(Collectors.toMap(
            p -> p,
            winners::contains
        ));

    return new GameEndStatus(results, Status.WINNER_FOUND);
  }
}
