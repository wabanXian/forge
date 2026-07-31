package forge.deck;

import forge.card.CardEdition;
import forge.card.CardRules;
import forge.card.CardRulesPredicates;
import forge.deck.generation.DeckGeneratorBase;
import forge.item.PaperCard;
import forge.item.PaperCardPredicates;
import forge.model.FModel;
import forge.util.ItemPool;
import forge.util.Localizer;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by maustin on 09/05/2017.
 */
public class CommanderDeckGenerator extends DeckProxy implements Comparable<CommanderDeckGenerator> {
    public static List<DeckProxy> getCommanderDecks(final DeckFormat format, boolean isForAi, boolean isCardGen){
        return getCommanderDecks(format, isForAi, isCardGen, false);
    }

    public static List<DeckProxy> getCommanderDecks(final DeckFormat format, boolean isForAi, boolean isCardGen, boolean includeRandom){
        if (format.equals(DeckFormat.Brawl)){
            return getBrawlDecks(format, isForAi, isCardGen, includeRandom);
        }
        ItemPool<PaperCard> uniqueCards;
        if (isCardGen){
            uniqueCards = new ItemPool<>(PaperCard.class);
            String matrixKey = (format.equals(DeckFormat.TinyLeaders) ? DeckFormat.Commander : format).toString(); //use Commander for Tiny Leaders
            HashMap<String, List<Map.Entry<PaperCard, Integer>>> matrixPool = CardRelationMatrixGenerator.cardPools.get(matrixKey);
            if (matrixPool != null) {
                Iterable<String> legendNames = matrixPool.keySet();
                for (String legendName : legendNames) {
                    uniqueCards.add(FModel.getMagicDb().getCommonCards().getUniqueByName(legendName));
                }
            }
        }
        else {
            uniqueCards = ItemPool.createFrom(FModel.getMagicDb().getCommonCards().getUniqueCards(), PaperCard.class);
        }
        Predicate<CardRules> canPlay = isForAi ? DeckGeneratorBase.AI_CAN_PLAY : CardRulesPredicates.IS_KEPT_IN_RANDOM_DECKS;
        final List<DeckProxy> decks = uniqueCards.toFlatList().stream()
                .filter(format.isLegalCommanderPredicate())
                .filter(PaperCardPredicates.fromRules(canPlay))
                .map(legend -> new CommanderDeckGenerator(legend, format, isForAi, isCardGen))
                .collect(Collectors.toList());
        if (includeRandom) {
            addRandomEntry(decks);
        }
        return decks;
    }

    public static List<DeckProxy> getBrawlDecks(final DeckFormat format, boolean isForAi, boolean isCardGen){
        return getBrawlDecks(format, isForAi, isCardGen, false);
    }

    public static List<DeckProxy> getBrawlDecks(final DeckFormat format, boolean isForAi, boolean isCardGen, boolean includeRandom){
        ItemPool<PaperCard> uniqueCards;
        if (isCardGen){
            uniqueCards = new ItemPool<>(PaperCard.class);
            Map<String, List<List<Pair<String, Double>>>> ldaPool = CardArchetypeLDAGenerator.ldaPools.get(FModel.getEffectiveBrawlFormat().getName());
            if (ldaPool != null) {
                for (String legendName : ldaPool.keySet()) {
                    uniqueCards.add(FModel.getMagicDb().getCommonCards().getUniqueByName(legendName));
                }
            }
            if (uniqueCards.isEmpty()) {
                uniqueCards = ItemPool.createFrom(FModel.getMagicDb().getCommonCards().getUniqueCards(), PaperCard.class);
            }
        }
        else {
            uniqueCards = ItemPool.createFrom(FModel.getMagicDb().getCommonCards().getUniqueCards(), PaperCard.class);
        }
        Predicate<CardRules> canPlay = isForAi ? DeckGeneratorBase.AI_CAN_PLAY : CardRulesPredicates.IS_KEPT_IN_RANDOM_DECKS;
        final List<DeckProxy> decks = uniqueCards.toFlatList().stream()
                .filter(format.isLegalCardPredicate())
                .filter(PaperCardPredicates.fromRules(CardRulesPredicates.CAN_BE_BRAWL_COMMANDER.and(canPlay)))
                .map(legend -> new CommanderDeckGenerator(legend, format, isForAi, isCardGen))
                .collect(Collectors.toList());
        if (includeRandom) {
            addRandomEntry(decks);
        }
        return decks;
    }

    private static void addRandomEntry(final List<DeckProxy> decks) {
        if (!decks.isEmpty()) {
            decks.add(0, new CommanderDeckGenerator(decks));
        }
    }

    private final PaperCard legend;
    private final int index;
    private final DeckFormat format;
    private final boolean isForAi;
    private final boolean isCardgen;
    private final List<DeckProxy> randomCandidates;

    private CommanderDeckGenerator(PaperCard legend0, DeckFormat format0, boolean isForAi0, boolean isCardgen0) {
        super();
        legend = legend0;
        index = 0;
        isForAi=isForAi0;
        format=format0;
        isCardgen=isCardgen0;
        randomCandidates = null;
    }

    private CommanderDeckGenerator(final List<DeckProxy> candidates) {
        super();
        legend = null;
        index = 0;
        isForAi = false;
        format = null;
        isCardgen = false;
        randomCandidates = new ArrayList<>(candidates);
    }

    public CardEdition getEdition() {
        return CardEdition.UNKNOWN;
    }

    @Override
    public String getName() {
        return isRandomEntry() ? Localizer.getInstance().getMessage("lblRandom") : legend.getName();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(final CommanderDeckGenerator d) {
        if (isRandomEntry()) {
            return d.isRandomEntry() ? 0 : -1;
        }
        if (d.isRandomEntry()) {
            return 1;
        }
        return this.getName().compareTo(d.getName());
    }

    @Override
    public Deck getDeck() {
        if (isRandomEntry()) {
            return RandomDeckProxyUtil.getRandomDeck(randomCandidates);
        }
        return DeckgenUtil.generateRandomCommanderDeck(legend, format, isForAi, isCardgen);
    }

    @Override
    public boolean isGeneratedDeck() {
        return true;
    }

    public String getImageKey(boolean altState) {
        return isRandomEntry() ? null : legend.getImageKey(altState);
    }

    public PaperCard getPaperCard(){
        return legend;
    }

    private boolean isRandomEntry() {
        return randomCandidates != null;
    }
}
