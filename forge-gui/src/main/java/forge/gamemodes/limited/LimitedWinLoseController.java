package forge.gamemodes.limited;

import forge.game.GameOutcome;
import forge.game.GameView;
import forge.gamemodes.limited.GauntletMini.MatchRecord;
import forge.gui.interfaces.IButton;
import forge.gui.interfaces.IWinLoseView;
import forge.model.FModel;
import forge.player.GamePlayerUtil;
import forge.util.Localizer;

public abstract class LimitedWinLoseController {
    private final Localizer localizer = Localizer.getInstance();
    private final IWinLoseView<? extends IButton> view;
    private final GameView lastGame;
    private final boolean wonMatch;
    private GauntletMini gauntlet;
    private boolean nextRound = false;
    private boolean canRestartRun = false;

    public LimitedWinLoseController(IWinLoseView<? extends IButton> view0, final GameView game0) {
        view = view0;
        lastGame = game0;
        gauntlet = FModel.getGauntletMini();
        wonMatch = lastGame.isMatchWonBy(GamePlayerUtil.getGuiPlayer());
    }

    public void showOutcome() {
        // view.getBtnRestart().setVisible(false);
        // Deliberate; allow replaying bad tournaments

        //TODO: do per-game actions like ante here...

        resetView();
        nextRound = false;
        canRestartRun = false;

        if (lastGame.isWinner(GamePlayerUtil.getGuiPlayer())) {
            gauntlet.addWin();
        } else {
            gauntlet.addLoss();
        }

        showOutcome(() -> {
            gauntlet.setCurrentMatchResult(GamePlayerUtil.getGuiPlayer().getName(), getCurrentMatchScore(), wonMatch);
            if (!lastGame.isMatchOver()) {
                view.getBtnRestart().setVisible(false);
                showRunInfo(localizer.getMessage("btnTournamentInfo"));
                return;
            }

            if (wonMatch) {
                gauntlet.addMatchWin();
            } else {
                gauntlet.addMatchLoss();
            }

            if (!gauntlet.isRunComplete()) {
                view.getBtnContinue().setText(localizer.getMessage("btnNextLimitedMatch") + " (" + (gauntlet.getCurrentRound() + 1)
                        + "/" + gauntlet.getMaxMatches() + ")");
                view.getBtnContinue().setEnabled(true);
                view.getBtnRestart().setVisible(false);
                nextRound = true;
                showRunInfo(wonMatch ? localizer.getMessage("btnWonLimitedMatch") : localizer.getMessage("btnLostLimitedMatch"));
            } else {
                gauntlet.archiveCurrentMatchResult();
                view.getBtnContinue().setVisible(false);
                view.getBtnRestart().setText(localizer.getMessage("btnStartNewLimitedRun"));
                canRestartRun = true;
                showRunInfo(gauntlet.getMatchWins() >= gauntlet.getMaxMatchWins()
                        ? localizer.getMessage("btnWonTournament")
                        : localizer.getMessage("lblLimitedRunComplete"));
            }
        });
    }

    public final boolean actionOnContinue() {
        resetView();
        if (nextRound) {
            view.hide();
            saveOptions();
            gauntlet.nextRound();
            return true;
        }
        return false;
    }

    public final boolean actionOnRestart() {
        resetView();
        if (canRestartRun) {
            view.hide();
            saveOptions();
            gauntlet.restartRun();
            return true;
        }
        return false;
    }

    public final void actionOnQuit() {
        resetView();
        gauntlet.resetCurrentRound();
    }

    /**
     * <p>
     * Shows some tournament info in the custom panel.
     * </p>
     * @param String - the title to be displayed
     */
    private void showRunInfo(final String newTitle) {
        showMessage("<html><div style='text-align: center;'>"
                + localizer.getMessage("lblLimitedRunRecord") + ": " + gauntlet.getMatchWins() + "-" + gauntlet.getMatchLosses()
                + "<br>" + localizer.getMessage("lblLimitedRunWins") + ": " + gauntlet.getMatchWins() + "/" + gauntlet.getMaxMatchWins()
                + "<br>" + localizer.getMessage("lblLimitedRunLosses") + ": " + gauntlet.getMatchLosses() + "/" + gauntlet.getMaxMatchLosses()
                + "<br>" + localizer.getMessage("lblLimitedRunMatch") + ": " + gauntlet.getCurrentRound() + "/" + gauntlet.getMaxMatches()
                + getMatchHistoryHtml()
                + "</div></html>", newTitle);
    }

    private String getMatchHistoryHtml() {
        final StringBuilder sb = new StringBuilder();
        if (!gauntlet.getMatchHistory().isEmpty()) {
            sb.append("<br><br>").append(localizer.getMessage("lblLimitedRunMatchHistory"));
            for (final MatchRecord record : gauntlet.getMatchHistory()) {
                sb.append("<br>").append(formatMatchRecord(record, false));
            }
        }
        if (lastGame.isMatchOver() && gauntlet.isRunComplete()) {
            return sb.toString();
        }
        sb.append("<br><br>").append(localizer.getMessage("lblLimitedRunCurrentMatch"));
        sb.append("<br>").append(formatMatchRecord(gauntlet.getCurrentMatchRecord(), true));
        return sb.toString();
    }

    private String getMatchNumberPrefix(final int matchNumber) {
        final String prefix = localizer.getMessage("lblLimitedRunMatchNumber", matchNumber);
        return prefix.endsWith("：") ? prefix : prefix + " ";
    }

    private String formatMatchRecord(final MatchRecord record, final boolean current) {
        final StringBuilder sb = new StringBuilder();
        sb.append(getMatchNumberPrefix(record.getMatchNumber()))
                .append(record.getPlayerName())
                .append(" (").append(localizer.getMessage("lblPlayer")).append(") vs ")
                .append(record.getOpponentName())
                .append(" (").append(localizer.getMessage("lblAI")).append("): ")
                .append(record.getScore());
        if (current) {
            sb.append(" ").append(localizer.getMessage(lastGame.isMatchOver()
                    ? (record.isWon() ? "lblWin" : "lblLose")
                    : "lblLimitedRunInProgress"));
        }
        return sb.toString();
    }

    private String getCurrentMatchScore() {
        int playerWins = 0;
        int gamesPlayed = 0;
        for (final GameOutcome outcome : lastGame.getOutcomesOfMatch()) {
            gamesPlayed++;
            if (outcome.isWinner(GamePlayerUtil.getGuiPlayer())) {
                playerWins++;
            }
        }
        return playerWins + "-" + (gamesPlayed - playerWins);
    }

    /**
     * <p>
     * ResetView
     * </p>
     * Restore the default texts to the win/lose panel buttons.
     * 
     */
    private void resetView() {
        view.getBtnQuit().setText(localizer.getMessage("btnQuit"));
        view.getBtnContinue().setText(localizer.getMessage("btnContinue"));
        view.getBtnRestart().setText(localizer.getMessage("btnRestart"));
        view.getBtnContinue().setVisible(true);
        view.getBtnRestart().setVisible(true);
    }

    protected abstract void showOutcome(Runnable runnable);
    protected abstract void showMessage(String message, String title);
    protected abstract void saveOptions();
}
