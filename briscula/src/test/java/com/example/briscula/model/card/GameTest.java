package com.example.briscula.model.card;

import com.example.briscula.game.Game;
import com.example.briscula.game.Move;
import com.example.briscula.game.RoundJudge;
import com.example.briscula.game.RoundWinner;
import com.example.briscula.user.player.Bot;
import com.example.briscula.user.player.Player;
import com.example.web.model.ConnectedPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.briscula.utilities.constants.GameOptionNumberOfPlayers.FOUR_PLAYERS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static utils.EntityUtils.getConnectedPlayer;
import static utils.EntityUtils.getWebSocketSession;

class GameTest {

    private List<Card> cardsList;

    private RoundWinner roundWinner;

    private final List<Bot> playerList = new ArrayList<>(Arrays.asList(
            new Bot("Bot 0", getWebSocketSession()), new Bot("Bot 1", getWebSocketSession()),
            new Bot("Bot 2", getWebSocketSession()), new Bot("Bot 3", getWebSocketSession())
    ));


    @BeforeEach
    void setUp() {
        setPlayersPointsToZero();
    }

    @Test
    void TestRoundMoreCardsOfMainType() {
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
    void TestRoundMoreCardsOfMainType2() {
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
    void TestRoundNoCardsOfMainType() {
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
    void TestRoundSmallCardValueMainType() {
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

    @Test
    void TestTwoPlayersDoubleRoundWinsSmallMainCard() {
        cardsList = new ArrayList<>(Arrays.asList(
                new Card(CardType.DENARI, CardValue.SEVEN),
                new Card(CardType.DENARI, CardValue.JACK),
                new Card(CardType.COPPE, CardValue.TWO),
                new Card(CardType.SPADE, CardValue.KING)
        ));

        setupMovesAndCalculateRoundForTwoPlayers(CardType.COPPE);

        assertThat(roundWinner.numberOfPoints()).isEqualTo(6);
        assertThat(roundWinner.player().getNickname()).isEqualTo("Bot 0");
    }

    @Test
    void TestTwoPlayersDoubleRoundWinsOrder() {
        cardsList = new ArrayList<>(Arrays.asList(
                new Card(CardType.DENARI, CardValue.SEVEN),
                new Card(CardType.DENARI, CardValue.JACK),
                new Card(CardType.DENARI, CardValue.THREE),
                new Card(CardType.DENARI, CardValue.ACE)
        ));

        setupMovesAndCalculateRoundForTwoPlayers(CardType.COPPE);

        assertThat(roundWinner.numberOfPoints()).isEqualTo(23);
        assertThat(roundWinner.player().getNickname()).isEqualTo("Bot 1");
    }

    @Test
    void testGameNotDealingLastCard() {
        List<ConnectedPlayer> connectedPlayerList = List.of(
                getConnectedPlayer(), getConnectedPlayer(), getConnectedPlayer(), getConnectedPlayer());


        for (int i = 0; i < 50; ++i) {
            Game game = new Game(FOUR_PLAYERS, connectedPlayerList, true);

            boolean isMainCardDealt = game.getCards().stream().anyMatch(card -> card.equals(game.getMainCard()));

            assertThat(isMainCardDealt).isFalse();
        }
    }


    private void printPlayers() {
        playerList.forEach(element -> System.out.println(element.getPoints()));
    }

    private void setPlayersPointsToZero() {
        playerList.forEach(Player::resetPoints);
    }

    private void setupMovesAndCalculateRound(CardType mainCardType) {
        List<Move> movesList = List.of(
                new Move(playerList.get(0), cardsList.get(0)), new Move(playerList.get(1), cardsList.get(1)),
                new Move(playerList.get(2), cardsList.get(2)), new Move(playerList.get(3), cardsList.get(3))
        );

        roundWinner = RoundJudge.calculateRound(new ArrayDeque<>(movesList.subList(0, 4)), mainCardType);
    }

    private void setupMovesAndCalculateRoundForTwoPlayers(CardType mainCardType) {
        List<Move> movesList = List.of(
                new Move(playerList.get(0), cardsList.get(0)), new Move(playerList.get(1), cardsList.get(1)),
                new Move(playerList.get(0), cardsList.get(2)), new Move(playerList.get(1), cardsList.get(3))
        );

        roundWinner = RoundJudge.calculateRound(new ArrayDeque<>(movesList.subList(0, 4)), mainCardType);
    }

}