/*
 * Forge: Play Magic: the Gathering.
 * Copyright (C) 2011  Forge Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package forge.gamemodes.limited;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import forge.deck.Deck;
import forge.deck.DeckGroup;
import forge.game.GameType;
import forge.game.GameView;
import forge.game.player.RegisteredPlayer;
import forge.gamemodes.match.HostedMatch;
import forge.gui.GuiBase;
import forge.localinstance.properties.ForgePreferences.FPref;
import forge.model.FModel;
import forge.player.GamePlayerUtil;
import forge.util.Aggregates;

/**
 * Starts matches between a saved limited deck and the AI decks drafted or built alongside it,
 * either as a single match or as a gauntlet of rounds.
 */
public class LimitedMatch {
    private static final int MAX_MATCH_WINS = 7;
    private static final int MAX_DRAFT_MATCH_LOSSES = 3;
    private static final int MAX_SEALED_MATCH_LOSSES = 2;

    private HostedMatch hostedMatch = null;
    private int rounds;
    private Deck humanDeck;
    private int currentRound;
    private int wins;
    private int losses;
    private int matchWins;
    private int matchLosses;
    private boolean currentMatchRecorded;
    private int currentOpponentIndex = -1;
    private String currentOpponentName = "";
    private String currentPlayerName = "";
    private String currentMatchScore = "";
    private boolean currentMatchWon;
    private boolean gauntlet;
    private GameType gameType;
    private final List<Deck> aiOpponents = new ArrayList<>();
    private final List<Integer> playedOpponentIndexes = new ArrayList<>();
    private final List<MatchRecord> matchHistory = new ArrayList<>();

    public LimitedMatch() {
        currentRound = 1;
        wins = 0;
        losses = 0;
        matchWins = 0;
        matchLosses = 0;
        currentMatchRecorded = false;
    }

    /**
     * Resets the tournament.
     */
    public void resetCurrentRound() {
        wins = 0;
        losses = 0;
        matchWins = 0;
        matchLosses = 0;
        currentMatchRecorded = false;
        currentOpponentIndex = -1;
        currentOpponentName = "";
        currentPlayerName = "";
        currentMatchScore = "";
        currentMatchWon = false;
        playedOpponentIndexes.clear();
        matchHistory.clear();
        currentRound = 1;
    }

    /**
     * Advances the tournament to the next round.
     */
    public void nextRound() {
        if (hostedMatch == null) {
            throw new IllegalStateException("Cannot advance match when no match has been hosted.");
        }

        if (isRunComplete() || currentRound >= rounds) {
            return;
        }

        archiveCurrentMatchResult();
        currentRound++;
        currentMatchRecorded = false;
        currentOpponentIndex = -1;
        currentOpponentName = "";
        currentPlayerName = "";
        currentMatchScore = "";
        currentMatchWon = false;
        hostedMatch.endCurrentGame();
        startRound();
    }

    public void restartRun() {
        if (hostedMatch == null) {
            throw new IllegalStateException("Cannot restart run when no match has been hosted.");
        }
        resetCurrentRound();
        hostedMatch.endCurrentGame();
        startRound();
    }

    /**
     * @param mode Draft, Sealed or Winston
     * @return the AI decks saved with {@code humanDeck0}, or an empty list for an event deck that has none
     */
    public List<Deck> getAiDecks(final GameType mode, final Deck humanDeck0) {
        final DeckGroup group = switch (mode) {
            case Draft -> FModel.getDecks().getDraft().get(humanDeck0.getName());
            case Sealed -> FModel.getDecks().getSealed().get(humanDeck0.getName());
            case Winston -> FModel.getDecks().getWinston().get(humanDeck0.getName());
            default -> throw new IllegalArgumentException("No saved AI decks for " + mode);
        };
        return group == null ? Collections.emptyList() : group.getAiDecks();
    }

    /**
     * @return the reason {@code deck} cannot be played in {@code mode}, or null when it can or legality is not enforced
     */
    public String getDeckProblem(final GameType mode, final Deck deck) {
        if (!FModel.getPreferences().getPrefBoolean(FPref.ENFORCE_DECK_LEGALITY)) {
            return null;
        }
        return resolveGameType(mode, deck).getDeckFormat().getDeckConformanceProblem(deck);
    }

    public void startMatch(final GameType mode, final Deck humanDeck0, final List<Deck> opponents) {
        humanDeck = humanDeck0;
        gameType = resolveGameType(mode, humanDeck0);
        gauntlet = false;
        host(opponents);
    }

    /**
     * Plays the AI decks saved with {@code humanDeck0} without repeating an opponent.
     * A single round is played against a random AI deck.
     */
    public void launchGauntlet(final GameType mode, final Deck humanDeck0, final int rounds0) {
        final List<Deck> aiDecks = getAiDecks(mode, humanDeck0);
        humanDeck = humanDeck0;
        gameType = resolveGameType(mode, humanDeck0);
        gauntlet = true;
        aiOpponents.clear();

        if (rounds0 == 1) {
            rounds = 1;
            aiOpponents.add(aiDecks.get(Aggregates.randomInt(0, aiDecks.size() - 1)));
        } else {
            aiOpponents.addAll(aiDecks);
            rounds = Math.min(MAX_MATCH_WINS, Math.min(rounds0, aiOpponents.size()));
        }

        resetCurrentRound();
        startRound();
    }

    private void startRound() {
        currentOpponentIndex = chooseNextOpponentIndex();
        host(Collections.singletonList(aiOpponents.get(currentOpponentIndex)));
    }

    private int chooseNextOpponentIndex() {
        final List<Integer> availableIndexes = new ArrayList<>();
        for (int i = 0; i < aiOpponents.size(); i++) {
            if (!playedOpponentIndexes.contains(i)) {
                availableIndexes.add(i);
            }
        }
        if (availableIndexes.isEmpty()) {
            throw new IllegalStateException("Cannot advance limited run; no unplayed AI opponents remain.");
        }
        final int selectedIndex = availableIndexes.get(Aggregates.randomInt(0, availableIndexes.size() - 1));
        playedOpponentIndexes.add(selectedIndex);
        return selectedIndex;
    }

    private void host(final List<Deck> opponents) {
        final List<RegisteredPlayer> players = new ArrayList<>();
        final RegisteredPlayer human = register(humanDeck).setPlayer(GamePlayerUtil.getGuiPlayer());
        players.add(human);
        for (final Deck aiDeck : opponents) {
            final forge.LobbyPlayer aiPlayer = GamePlayerUtil.createAiPlayer();
            currentOpponentName = aiPlayer.getName();
            players.add(register(aiDeck).setPlayer(aiPlayer));
        }

        hostedMatch = GuiBase.getInterface().hostMatch();
        hostedMatch.startMatch(gameType, gameType == GameType.CommanderDraft ? EnumSet.of(GameType.Commander) : null,
                players, human, GuiBase.getInterface().getNewGuiGame());
    }

    private RegisteredPlayer register(final Deck deck) {
        final RegisteredPlayer player = gameType == GameType.CommanderDraft
                ? RegisteredPlayer.forCommander(deck) : new RegisteredPlayer(deck);
        player.assignConspiracies();
        return player;
    }

    private static GameType resolveGameType(final GameType mode, final Deck deck) {
        return mode == GameType.Draft && CommanderDraftUtil.isCommanderDraftDeck(deck) ? GameType.CommanderDraft : mode;
    }

    /**
     * Returns the total number of rounds in the tournament.
     * @return int, number of rounds in the tournament
     */
    public final int getRounds() {
        return rounds;
    }

    /**
     * Returns the number of the current round in the tournament.
     * @return int, number of rounds in the tournament
     */
    public final int getCurrentRound() {
        return currentRound;
    }

    /**
     * Adds a game win to the tournament statistics.
     */
    public void addWin() {
        wins++;
    }

    /**
     * Adds a game loss to the tournament statistics.
     */
    public void addLoss() {
        losses++;
    }

    /**
     * The total number of won games in this tournament.
     * @return int, number of wins
     */
    public final int getWins() {
        return wins;
    }

    /**
     * The total number of lost games in this tournament.
     * @return int, numer of losses
     */
    public final int getLosses() {
        return losses;
    }

    public void addMatchWin() {
        if (!currentMatchRecorded) {
            matchWins++;
            currentMatchRecorded = true;
        }
    }

    public void addMatchLoss() {
        if (!currentMatchRecorded) {
            matchLosses++;
            currentMatchRecorded = true;
        }
    }

    public int getMatchWins() {
        return matchWins;
    }

    public int getMatchLosses() {
        return matchLosses;
    }

    public int getMaxMatchWins() {
        return MAX_MATCH_WINS;
    }

    public int getMaxMatchLosses() {
        return gameType == GameType.Draft || gameType == GameType.CommanderDraft
                ? MAX_DRAFT_MATCH_LOSSES : MAX_SEALED_MATCH_LOSSES;
    }

    public boolean isRunComplete() {
        return matchWins >= getMaxMatchWins() || matchLosses >= getMaxMatchLosses() || currentRound >= rounds;
    }

    public int getMaxMatches() {
        return rounds;
    }

    public void setCurrentMatchResult(final String playerName, final String score, final boolean won) {
        currentPlayerName = playerName == null ? "" : playerName;
        currentMatchScore = score == null ? "" : score;
        currentMatchWon = won;
    }

    public MatchRecord getCurrentMatchRecord() {
        return new MatchRecord(currentRound, currentPlayerName, currentOpponentName, currentMatchScore, currentMatchWon);
    }

    public List<MatchRecord> getMatchHistory() {
        return matchHistory;
    }

    public void archiveCurrentMatchResult() {
        if (!currentMatchScore.isEmpty() && matchHistory.stream().noneMatch(record -> record.matchNumber == currentRound)) {
            matchHistory.add(getCurrentMatchRecord());
        }
    }

    public record MatchRecord(int matchNumber, String playerName, String opponentName, String score, boolean won) {}

    /**
     * @return true when {@code game} is a round of the gauntlet this class is running
     */
    public final boolean isGauntletRound(final GameView game) {
        return gauntlet && hostedMatch != null && hostedMatch.getGameView() == game;
    }

}
