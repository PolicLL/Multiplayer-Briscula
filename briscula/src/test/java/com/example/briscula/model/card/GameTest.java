package com.example.briscula.model.card;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.briscula.game.RoundJudge;
import com.example.briscula.game.Move;
import com.example.briscula.game.RoundWinner;
import com.example.briscula.user.player.Player;
import com.example.briscula.user.player.Bot;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameTest {

  private List<Card> cardsList;

  private RoundWinner roundWinner;

  private final List<Bot> playerList = new ArrayList<>(Arrays.asList(
      new Bot(new ArrayList<>(), "Bot 0"), new Bot(new ArrayList<>(), "Bot 1"),
      new Bot(new ArrayList<>(), "Bot 2"), new Bot(new ArrayList<>(), "Bot 3")
  ));


  @BeforeEach
  public void setUp() {
    setPlayersPointsToZero();
  }

  @Test
  public void TestRoundMoreCardsOfMainType() {
    cardsList = new ArrayList<>(Arrays.asList(
        new Card(CardType.SPADE, CardValue.THREE),
        new Card(CardType.COPPE, CardValue.TWO),
        new Card(CardType.DENARI, CardValue.JACK),
        new Card(CardType.COPPE, CardValue.KING)
    ));

    setupMovesAndCalculateRound(CardType.COPPE);

    assertThat(roundWinner.numberOfPoints()).isEqualTo(16);
    assertThat(roundWinner.player().getNickname()).isEqualTo("Bot 3");
  }

  @Test
  public void TestRoundMoreCardsOfMainType2() {
    cardsList = new ArrayList<>(Arrays.asList(
        new Card(CardType.DENARI, CardValue.THREE),
        new Card(CardType.DENARI, CardValue.ACE),
        new Card(CardType.BASTONI, CardValue.JACK),
        new Card(CardType.BASTONI, CardValue.SEVEN)
    ));


    setupMovesAndCalculateRound(CardType.BASTONI);

    assertThat(roundWinner.numberOfPoints()).isEqualTo(23);
    assertThat(roundWinner.player().getNickname()).isEqualTo("Bot 2");
  }

  @Test
  public void TestRoundNoCardsOfMainType() {
    cardsList = new ArrayList<>(Arrays.asList(
        new Card(CardType.DENARI, CardValue.THREE),
        new Card(CardType.DENARI, CardValue.SEVEN),
        new Card(CardType.BASTONI, CardValue.JACK),
        new Card(CardType.BASTONI, CardValue.SEVEN)
    ));


    setupMovesAndCalculateRound(CardType.COPPE);

    assertThat(roundWinner.numberOfPoints()).isEqualTo(12);
    assertThat(roundWinner.player().getNickname()).isEqualTo("Bot 0");
  }

  @Test
  public void TestRoundSmallCardValueMainType() {
    cardsList = new ArrayList<>(Arrays.asList(
        new Card(CardType.DENARI, CardValue.THREE),
        new Card(CardType.DENARI, CardValue.SEVEN),
        new Card(CardType.BASTONI, CardValue.JACK),
        new Card(CardType.COPPE, CardValue.TWO)
    ));

    setupMovesAndCalculateRound(CardType.COPPE);

    assertThat(roundWinner.numberOfPoints()).isEqualTo(12);
    assertThat(roundWinner.player().getNickname()).isEqualTo("Bot 3");
  }


  private void printPlayers(){
    playerList.forEach(element -> System.out.println(element.getPoints()));
  }

  private void setPlayersPointsToZero(){ playerList.forEach(Player::resetPoints); }

  private void setupMovesAndCalculateRound(CardType mainCardType) {
    List<Move> movesList = List.of(
        new Move(playerList.get(0), cardsList.get(0)), new Move(playerList.get(1), cardsList.get(1)),
        new Move(playerList.get(2), cardsList.get(2)), new Move(playerList.get(3), cardsList.get(3))
    );

    roundWinner = RoundJudge.calculateRound(new ArrayDeque<>(movesList.subList(0, 4)), mainCardType);
  }

}