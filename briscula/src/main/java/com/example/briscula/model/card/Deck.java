package com.example.briscula.model.card;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Deck {

    @Getter
    private final List<Card> deckCards = new ArrayList<>();
    private final Random random = new Random();

    public Deck() {
        fillDeck();
    }

    private void fillDeck() {
        deckCards.clear();
        for (CardType type : CardType.values()) {
            for (CardValue value : CardValue.values()) {
                deckCards.add(new Card(type, value));
            }
        }
    }

    public void setLastCard(Card mainCard) {
        int tempIndexOfMainCard = 0;
        for (int i = 0; i < deckCards.size(); ++i) {
            if (deckCards.get(i).equals(mainCard)) {
                tempIndexOfMainCard = i;
                break;
            }
        }

        log.info("Set main card {}.", mainCard);

        Card tempLastCard = deckCards.get(deckCards.size() - 1);
        deckCards.set(deckCards.size() - 1, mainCard);
        deckCards.set(tempIndexOfMainCard, tempLastCard);
    }

    public Card removeOneCard() {
        if (deckCards.size() == 1) return deckCards.remove(0);
        return deckCards.remove(random.nextInt(deckCards.size() - 1));
    }

    public void removeOneWithCardValueTwo() {
        deckCards.stream()
                .filter(card -> card.cardValue() == CardValue.TWO)
                .findFirst()
                .map(deckCards::remove);
    }

    public int getNumberOfDeckCards() {
        return deckCards.size();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("\n");
        int numPlayers = deckCards.size() == 39 ? 3 : 4;
        int cardsPerRow = numPlayers == 3 ? 13 : 10;

        for (int i = 0; i < numPlayers; ++i) {
            deckCards.subList(i * cardsPerRow, (i + 1) * cardsPerRow)
                    .forEach(card -> output.append(card).append(" "));
            output.append("\n");
        }

        return output.append("Number of cards: ").append(deckCards.size()).append("\n").toString();
    }
}
