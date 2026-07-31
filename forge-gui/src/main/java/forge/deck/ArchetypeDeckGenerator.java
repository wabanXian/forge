package forge.deck;

import forge.card.CardEdition;
import forge.deck.io.Archetype;
import forge.game.GameFormat;
import forge.item.PaperCard;
import forge.model.FModel;
import forge.util.Localizer;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maustin on 09/05/2017.
 */
public class ArchetypeDeckGenerator extends DeckProxy implements Comparable<ArchetypeDeckGenerator> {
    public static List<DeckProxy> getMatrixDecks(GameFormat format, boolean isForAi){
        final List<DeckProxy> decks = new ArrayList<>();
        for(Archetype archetype: CardArchetypeLDAGenerator.ldaArchetypes.get(format.getName())) {
            decks.add(new ArchetypeDeckGenerator(archetype, format, isForAi));
        }
        if (!decks.isEmpty()) {
            decks.add(0, new ArchetypeDeckGenerator(decks));
        }
        return decks;
    }
    private final Archetype archetype;
    private final int index;
    private final GameFormat format;
    private final boolean isForAi;
    private PaperCard card;
    private final List<DeckProxy> randomCandidates;


    private ArchetypeDeckGenerator(Archetype archetype0, GameFormat format0, boolean isForAi0) {
        super();
        archetype = archetype0;
        index = 0;
        format=format0;
        isForAi=isForAi0;
        randomCandidates = null;
        for(Pair<String, Double> cardPair : archetype.getCardProbabilities()){
            PaperCard candidate = FModel.getMagicDb().getCommonCards().getUniqueByName(cardPair.getLeft());
            if(!candidate.getRules().getType().isLand()){
                card = candidate;
                break;
            }
        }
    }

    private ArchetypeDeckGenerator(final List<DeckProxy> candidates) {
        super();
        archetype = null;
        index = 0;
        format = null;
        isForAi = false;
        randomCandidates = new ArrayList<>(candidates);
    }

    public CardEdition getEdition() {
        return CardEdition.UNKNOWN;
    }


    @Override
    public String getName() {
        return isRandomEntry() ? Localizer.getInstance().getMessage("lblRandom") : archetype.getName();
    }

    @Override
    public String toString() {
        return getName();
    }

    public Archetype getArchetype() {
        return archetype;
    }

    @Override
    public int compareTo(final ArchetypeDeckGenerator d) {
        if (isRandomEntry()) {
            return d.isRandomEntry() ? 0 : -1;
        }
        if (d.isRandomEntry()) {
            return 1;
        }
        return d.getArchetype().getDeckCount().compareTo(archetype.getDeckCount());
    }

    @Override
    public Deck getDeck() {
        if (isRandomEntry()) {
            return RandomDeckProxyUtil.getRandomDeck(randomCandidates);
        }
        return DeckgenUtil.buildLDACArchetypeDeck(archetype,format,isForAi);
    }

    @Override
    public boolean isGeneratedDeck() {
        return true;
    }

    public String getImageKey(boolean altState) {
        if (isRandomEntry()) {
            return null;
        }
/*        Predicate<PaperCard> cardFilter = Predicates.and(format.getFilterPrinted(),PaperCard.Predicates.name(name));
        List<PaperCard> cards=FModel.getMagicDb().getCommonCards().getAllCards(cardFilter);
        return cards.get(cards.size()-1).getImageKey(altState);*/
        return card.getImageKey(altState);
    }

    public PaperCard getPaperCard(){
        return card;
    }

    private boolean isRandomEntry() {
        return randomCandidates != null;
    }
}
