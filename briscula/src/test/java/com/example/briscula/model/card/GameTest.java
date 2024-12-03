package com.example.briscula.model.card;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

import com.example.briscula.game.GameJudge;
import com.example.briscula.game.Move;
import com.example.briscula.user.admin.Admin;
import com.example.briscula.user.player.AbstractPlayer;
import com.example.briscula.user.player.Bot;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

public class GameTest {

  private static GameJudge gameJudge;
  private final Queue<Move> queueMoves = new ArrayDeque<>();
  private static final List<Bot> playerList = new ArrayList<>(Arrays.asList(
      new Bot(new ArrayList<>(), "Name 1"), new Bot(new ArrayList<>(), "Name 2"),
      new Bot(new ArrayList<>(), "Name 3"), new Bot(new ArrayList<>(), "Name 4")
  ));

  private List<Card> cardsList;

  private List<Move> movesList;

  static CardType mainCardType;

  @BeforeEach
  public void setUp() {
    Admin adminMock = mock(Admin.class);
    Mockito.when(adminMock.getMainCardType()).thenAnswer((Answer<CardType>) invocation -> mainCardType);

    gameJudge = new GameJudge(adminMock);

    setPlayersPointsToZero();
  }

  @Test
  public void TestRoundMoreCardsOfMainType() {

    mainCardType = CardType.COPPE;

    cardsList = new ArrayList<>(Arrays.asList(
        new Card(CardType.SPADE, CardValue.THREE),
        new Card(CardType.COPPE, CardValue.TWO),
        new Card(CardType.DENARI, CardValue.JACK),
        new Card(CardType.COPPE, CardValue.KING)
    ));

    movesList = List.of(
        new Move(playerList.get(0), cardsList.get(0)), new Move(playerList.get(1), cardsList.get(1)),
        new Move(playerList.get(2), cardsList.get(2)), new Move(playerList.get(3), cardsList.get(3))
    );

    addMovesAndCalculateRound();

    assertThat(playerList.get(3).getPoints()).isEqualTo(16);
  }

  @Test
  public void TestRoundMoreCardsOfMainType2() {

    mainCardType = CardType.BASTONI;

    cardsList = new ArrayList<>(Arrays.asList(
        new Card(CardType.DENARI, CardValue.THREE),
        new Card(CardType.DENARI, CardValue.ACE),
        new Card(CardType.BASTONI, CardValue.JACK),
        new Card(CardType.BASTONI, CardValue.SEVEN)
    ));

    movesList = List.of(
        new Move(playerList.get(0), cardsList.get(0)), new Move(playerList.get(1), cardsList.get(1)),
        new Move(playerList.get(2), cardsList.get(2)), new Move(playerList.get(3), cardsList.get(3))
    );

    addMovesAndCalculateRound();

    assertThat(playerList.get(2).getPoints()).isEqualTo(23);
  }

  @Test
  public void TestRoundNoCardsOfMainType() {

    mainCardType = CardType.COPPE;

    cardsList = new ArrayList<>(Arrays.asList(
        new Card(CardType.DENARI, CardValue.THREE),
        new Card(CardType.DENARI, CardValue.SEVEN),
        new Card(CardType.BASTONI, CardValue.JACK),
        new Card(CardType.BASTONI, CardValue.SEVEN)
    ));

    movesList = List.of(
        new Move(playerList.get(0), cardsList.get(0)), new Move(playerList.get(1), cardsList.get(1)),
        new Move(playerList.get(2), cardsList.get(2)), new Move(playerList.get(3), cardsList.get(3))
    );

    addMovesAndCalculateRound();

    assertThat(playerList.get(0).getPoints()).isEqualTo(12);
  }

  @Test
  public void TestRoundSmallCardValueMainType() {

    mainCardType = CardType.COPPE;

    cardsList = new ArrayList<>(Arrays.asList(
        new Card(CardType.DENARI, CardValue.THREE),
        new Card(CardType.DENARI, CardValue.SEVEN),
        new Card(CardType.BASTONI, CardValue.JACK),
        new Card(CardType.COPPE, CardValue.TWO)
    ));

    movesList = List.of(
        new Move(playerList.get(0), cardsList.get(0)), new Move(playerList.get(1), cardsList.get(1)),
        new Move(playerList.get(2), cardsList.get(2)), new Move(playerList.get(3), cardsList.get(3))
    );

    addMovesAndCalculateRound();

    assertThat(playerList.get(3).getPoints()).isEqualTo(12);
  }

  private void printPlayers(){
    playerList.forEach(element -> System.out.println(element.getPoints()));
  }

  private static void setPlayersPointsToZero(){ playerList.forEach(AbstractPlayer::resetPoints); }

  private void addMovesAndCalculateRound(){
    queueMoves.add(movesList.get(0));
    queueMoves.add(movesList.get(1));
    queueMoves.add(movesList.get(2));
    queueMoves.add(movesList.get(3));

    gameJudge.calculateRound(queueMoves);
  }
}