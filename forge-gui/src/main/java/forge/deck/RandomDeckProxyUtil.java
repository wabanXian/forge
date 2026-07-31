package forge.deck;

import forge.util.Aggregates;

import java.util.List;

public final class RandomDeckProxyUtil {
    private RandomDeckProxyUtil() {
    }

    public static Deck getRandomDeck(final List<? extends DeckProxy> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        return Aggregates.random(candidates).getDeck();
    }
}
