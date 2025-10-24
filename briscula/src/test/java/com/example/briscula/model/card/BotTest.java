package com.example.briscula.model.card;

import com.example.briscula.game.Move;
import com.example.briscula.user.player.Bot;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static utils.EntityUtils.getWebSocketSession;

public class BotTest {

    private final List<Bot> playerList = List.of(
            new Bot("Bot 0", getWebSocketSession()), new Bot("Bot 1", getWebSocketSession()),
            new Bot("Bot 2", getWebSocketSession()), new Bot("Bot 3", getWebSocketSession())
    );

    @Test
    void testKillIf_OneCardOnTableNotMainType_WithNotMainType() {
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
    void testThrowingFirst_When_ThereIsStrongMainAndNonMainType() {
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
    void testThrowingThird_When_CanKill_WithSmallerCard_AndThereIsMoreThan10Points() {
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
}
