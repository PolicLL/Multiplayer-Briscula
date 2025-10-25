package com.example.briscula.model.card;

import com.example.briscula.game.Game;
import com.example.briscula.game.Move;
import com.example.briscula.user.player.Bot;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import com.example.web.model.ConnectedPlayer;
import com.example.web.model.GameRoom;
import com.example.web.model.enums.GameEndStatus;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static utils.EntityUtils.getConnectedPlayer;
import static utils.EntityUtils.getWebSocketSession;

public class BotTest {

    private final List<Bot> playerList = List.of(
            new Bot("Bot 0", getWebSocketSession()), new Bot("Bot 1", getWebSocketSession()),
            new Bot("Bot 2", getWebSocketSession()), new Bot("Bot 3", getWebSocketSession())
    );

    @Test
    void testKill_OneCardOnTableNotMainType_WithNotMainType() {
        // given
        Queue<Move> moves = new LinkedList<>(List.of(
                new Move(playerList.get(0), new Card(CardType.COPPE, CardValue.SEVEN))
        ));

        Card mainCard = new Card(CardType.DENARI, CardValue.TWO);

        List<Card> playerCards = new ArrayList<>(List.of(
                new Card(CardType.SPADE, CardValue.TWO),
                new Card(CardType.DENARI, CardValue.TWO),
                new Card(CardType.COPPE, CardValue.ACE),
                new Card(CardType.COPPE, CardValue.THREE)));

        playerList.get(1).setPlayerCards(playerCards);

        // when
        Card cardBotChoose = playerList.get(1).playRound(moves, mainCard, GameOptionNumberOfPlayers.FOUR_PLAYERS);

        // then
        assertThat(cardBotChoose).isEqualTo(new Card(CardType.COPPE, CardValue.ACE));
    }

    @Test
    void testThrowingFirst_When_YouHaveStrongMainAndNonMainTypeCard() {
        // given
        Queue<Move> moves = new LinkedList<>(List.of());

        Card mainCard = new Card(CardType.DENARI, CardValue.TWO);

        List<Card> playerCards = new ArrayList<>(List.of(
                new Card(CardType.SPADE, CardValue.TWO),
                new Card(CardType.DENARI, CardValue.KNIGHT),
                new Card(CardType.SPADE, CardValue.ACE),
                new Card(CardType.COPPE, CardValue.FOUR)));

        playerList.get(0).setPlayerCards(playerCards);

        // when
        Card cardBotChoose = playerList.get(0).playRound(moves, mainCard, GameOptionNumberOfPlayers.TWO_PLAYERS);

        // then
        assertThat(cardBotChoose).isIn(new Card(CardType.SPADE, CardValue.ACE), new Card(CardType.DENARI, CardValue.KNIGHT));
    }

    @Test
    void testThrowingThird_WhenCanKill_WithSmallerCard_AndThereIsMoreThan10Points() {
        // given
        Queue<Move> moves = new LinkedList<>(List.of(
                new Move(playerList.get(0), new Card(CardType.COPPE, CardValue.KING)),
                new Move(playerList.get(0), new Card(CardType.SPADE, CardValue.ACE))
        ));

        Card mainCard = new Card(CardType.DENARI, CardValue.TWO);

        List<Card> playerCards = new ArrayList<>(List.of(
                new Card(CardType.DENARI, CardValue.KNIGHT),
                new Card(CardType.SPADE, CardValue.ACE),
                new Card(CardType.COPPE, CardValue.FOUR)));

        playerList.get(2).setPlayerCards(playerCards);

        // when
        Card cardBotChoose = playerList.get(2).playRound(moves, mainCard, GameOptionNumberOfPlayers.TWO_PLAYERS);

        // then
        assertThat(cardBotChoose).isIn(new Card(CardType.DENARI, CardValue.KNIGHT));
    }

    @Test
    void testKillIfHaveStrongestCard_WithEnoughPointsOnTable() {
        // given
        Queue<Move> moves = new LinkedList<>(List.of(
                new Move(playerList.get(0), new Card(CardType.COPPE, CardValue.KNIGHT)),
                new Move(playerList.get(1), new Card(CardType.COPPE, CardValue.JACK)),
                new Move(playerList.get(2), new Card(CardType.DENARI, CardValue.THREE))
        ));

        Card mainCard = new Card(CardType.COPPE, CardValue.TWO);

        List<Card> playerCards = new ArrayList<>(List.of(
                new Card(CardType.COPPE, CardValue.ACE),
                new Card(CardType.SPADE, CardValue.TWO),
                new Card(CardType.DENARI, CardValue.TWO),
                new Card(CardType.COPPE, CardValue.THREE)));

        playerList.get(3).setPlayerCards(playerCards);

        // when
        Card cardBotChoose = playerList.get(3).playRound(moves, mainCard, GameOptionNumberOfPlayers.FOUR_PLAYERS);

        // then
        assertThat(cardBotChoose).isEqualTo(new Card(CardType.COPPE, CardValue.ACE));
    }

    @Test
    void testSmartBotVsBot() {
        Bot smartBot = new Bot("SmartBot", getWebSocketSession());
        smartBot.setWaitingTimeInMiliseconds(0);

        Bot stupidBot = new Bot("StupidBot", getWebSocketSession());
        stupidBot.setHasIntelligence(false);
        stupidBot.setWaitingTimeInMiliseconds(0);

        int smartBotWins = 0;
        int stupidBotWins = 0;

        for (int i = 0; i < 100; ++i) {
            GameEndStatus gameEndStatus = new GameRoom(List.of(getConnectedPlayer(smartBot), getConnectedPlayer(stupidBot)),
                    GameOptionNumberOfPlayers.TWO_PLAYERS, true).startGame();

            List<ConnectedPlayer> winners = gameEndStatus.playerResults().entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .map(Map.Entry::getKey)
                    .toList();

            smartBot.resetPoints();
            stupidBot.resetPoints();

            if (gameEndStatus.status().equals(GameEndStatus.Status.NO_WINNER))
                continue;

            ConnectedPlayer winner = winners.get(0);

            if (winner.getPlayer().equals(smartBot)) {
                ++smartBotWins;
            }
            else ++stupidBotWins;
        }

        assertThat(smartBotWins).isGreaterThan(stupidBotWins);

        System.out.println("SmartBot wins = " +  smartBotWins + " Stupid bot wins = " + stupidBotWins);

    }
}
