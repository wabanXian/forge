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
import java.util.List;

import forge.LobbyPlayer;
import forge.deck.Deck;
import forge.game.GameType;
import forge.game.player.RegisteredPlayer;
import forge.gamemodes.match.HostedMatch;
import forge.gui.GuiBase;
import forge.model.FModel;
import forge.player.GamePlayerUtil;
import forge.util.Aggregates;

/**
 * <p>
 * GauntletMini class.
 * </p>
 *
 * @author Forge
 * @version $Id: GauntletMini.java $
 * @since 1.2.xx
 */
public class GauntletMini {
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
    private boolean gauntletDraft; // Means: Draft game is in Gauntlet-mode, not a single match
    private GameType gauntletType;
    private final List<Deck> aiOpponentDecks = new ArrayList<>();
    private final List<Integer> playedOpponentIndexes = new ArrayList<>();
    private final List<MatchRecord> matchHistory = new ArrayList<>();

    public GauntletMini() {
        currentRound = 1;
        gauntletDraft = false;
        wins = 0;
        losses = 0;
        matchWins = 0;
        matchLosses = 0;
        currentMatchRecorded = false;
        gauntletType = GameType.Sealed; // Assignable in launch();
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
     *
     * Setup and launch the gauntlet.
     * Note: The AI decks are connected to the human deck.
     *
     * @param rounds0
     *          the number of rounds (opponent decks) in this tournament
     * @param humanDeck0
     *          the human deck for this tournament
     * @param gauntletType0
     *          game type (Sealed, Draft, Constructed...)
     */
    public void launch(final int rounds0, final Deck humanDeck0, final GameType gauntletType0) {
        rounds = rounds0;
        humanDeck = humanDeck0;
        gauntletType = gauntletType0;
        List<Deck> aiDecks;
        if (gauntletType == GameType.Sealed) {
            aiDecks = FModel.getDecks().getSealed().get(humanDeck.getName()).getAiDecks();
        }
        else if (gauntletType == GameType.Draft) {
            gauntletDraft = true;
            aiDecks = FModel.getDecks().getDraft().get(humanDeck.getName()).getAiDecks();
        }
        else {
            throw new IllegalStateException("Cannot launch Gauntlet, game mode not implemented.");
        }
        aiOpponentDecks.clear();
        aiOpponentDecks.addAll(aiDecks);
        if (aiOpponentDecks.isEmpty()) {
            throw new IllegalStateException("Cannot launch Gauntlet with no AI opponents.");
        }
        rounds = Math.min(MAX_MATCH_WINS, Math.min(rounds, aiOpponentDecks.size()));

        resetCurrentRound();
        startRound();
    }

    /**
     * Starts the tournament.
     */
    private void startRound() {
        final List<RegisteredPlayer> starter = new ArrayList<>();
        final RegisteredPlayer human = new RegisteredPlayer(humanDeck).setPlayer(GamePlayerUtil.getGuiPlayer());
        starter.add(human);
        currentOpponentIndex = chooseNextOpponentIndex();
        final LobbyPlayer aiPlayer = GamePlayerUtil.createAiPlayer();
        currentOpponentName = aiPlayer.getName();
        starter.add(new RegisteredPlayer(aiOpponentDecks.get(currentOpponentIndex)).setPlayer(aiPlayer));

        for (final RegisteredPlayer pl : starter) {
            pl.assignConspiracies();
        }

        hostedMatch = GuiBase.getInterface().hostMatch();
        hostedMatch.startMatch(gauntletType, null, starter, human, GuiBase.getInterface().getNewGuiGame());
    }

    private int chooseNextOpponentIndex() {
        final List<Integer> availableIndexes = new ArrayList<>();
        for (int i = 0; i < aiOpponentDecks.size(); i++) {
            if (!playedOpponentIndexes.contains(i)) {
                availableIndexes.add(i);
            }
        }
        if (availableIndexes.isEmpty()) {
            throw new IllegalStateException("Cannot advance Gauntlet; no unplayed AI opponents remain.");
        }
        final int selectedIndex = availableIndexes.get(Aggregates.randomInt(0, availableIndexes.size() - 1));
        playedOpponentIndexes.add(selectedIndex);
        return selectedIndex;
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
        if (currentMatchRecorded) {
            return;
        }
        matchWins++;
        currentMatchRecorded = true;
    }

    public void addMatchLoss() {
        if (currentMatchRecorded) {
            return;
        }
        matchLosses++;
        currentMatchRecorded = true;
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
        return gauntletType == GameType.Draft ? MAX_DRAFT_MATCH_LOSSES : MAX_SEALED_MATCH_LOSSES;
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
        if (!currentMatchScore.isEmpty() && matchHistory.stream().noneMatch(record -> record.getMatchNumber() == currentRound)) {
            matchHistory.add(getCurrentMatchRecord());
        }
    }

    public static final class MatchRecord {
        private final int matchNumber;
        private final String playerName;
        private final String opponentName;
        private final String score;
        private final boolean won;

        private MatchRecord(final int matchNumber0, final String playerName0, final String opponentName0,
                final String score0, final boolean won0) {
            matchNumber = matchNumber0;
            playerName = playerName0;
            opponentName = opponentName0;
            score = score0;
            won = won0;
        }

        public int getMatchNumber() {
            return matchNumber;
        }

        public String getPlayerName() {
            return playerName;
        }

        public String getOpponentName() {
            return opponentName;
        }

        public String getScore() {
            return score;
        }

        public boolean isWon() {
            return won;
        }
    }

    /**
     * Resets the gauntletDraft value.
     */
    public void resetGauntletDraft() {
        gauntletDraft = false;
    }

    /**
     * Draft mode status.
     * @return boolean, gauntletDraft
     */
    public final boolean isGauntletDraft() {
        return gauntletDraft;
    }

}
