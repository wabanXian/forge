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
package forge.screens.match.controllers;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import forge.Singletons;
import forge.game.card.CardView;
import forge.game.player.PlayerView;
import forge.game.zone.ZoneType;
import forge.gui.FThreads;
import forge.gui.framework.ICDoc;
import forge.localinstance.properties.ForgePreferences.FPref;
import forge.model.FModel;
import forge.screens.match.CMatchUI;
import forge.screens.match.views.VField;
import forge.screens.match.views.VHand;
import forge.view.arcane.CardPanel;
import forge.view.arcane.HandArea;
import forge.view.arcane.util.Animation;
import forge.view.arcane.util.CardPanelMouseAdapter;
import forge.util.collect.FCollectionView;

/**
 * Controls Swing components of a player's hand instance.
 */
public class CHand implements ICDoc {
    private final CMatchUI matchUI;
    private final PlayerView player;
    private final VHand view;
    private final List<CardView> ordering = Lists.newArrayList();
    private final List<Integer> externalPlayableOrdering = Lists.newArrayList();

    /**
     * Controls Swing components of a player's hand instance.
     */
    public CHand(final CMatchUI matchUI, final PlayerView p0, final VHand v0) {
        this.matchUI = matchUI;
        this.player = p0;
        this.view = v0;
        v0.getHandArea().addCardPanelMouseListener(new CardPanelMouseAdapter() {
            @Override
            public void mouseDragEnd(final CardPanel dragPanel, final MouseEvent evt) {
                final HandArea handArea = CHand.this.view.getHandArea();
                if (handArea.isExternalPlayable(dragPanel)) {
                    updateExternalPlayableOrderingFromPanels(handArea.getCardPanels());
                    updateHand();
                    return;
                }
                int index = 0;
                for (final CardPanel panel : handArea.getCardPanels()) {
                    if (panel == dragPanel) {
                        break;
                    }
                    if (!handArea.isExternalPlayable(panel)) {
                        index++;
                    }
                }
                synchronized (ordering) {
                    ordering.remove(dragPanel.getCard());
                    if (index > ordering.size()) {
                        index = ordering.size();
                    }
                    ordering.add(index, dragPanel.getCard());
                    matchUI.getGameController(p0).reorderHand(dragPanel.getCard(), index);
                }
            }
        });
    }

    @Override
    public void register() {
    }

    @Override
    public void initialize() {
    }

    public void updateHand() {
        FThreads.assertExecutedByEdt(true);

        final HandArea p = view.getHandArea();

        //update card panels in hand area
        final List<CardView> cards;
        if (player.getHand().isEmpty()) {
            cards = ImmutableList.of();
        } else {
            synchronized (player) {
                cards = ImmutableList.copyOf(player.getHand());
            }
        }

        synchronized (ordering) {
            ordering.clear();
            ordering.addAll(cards);

            // Sort hand by CMC/color at the UI layer. This duplicates the game-layer sort in
            // PlayerZone.onChanged(), but is necessary because network clients only have CardViews
            // (no access to the game model), and because toggling the preference mid-game needs
            // to take effect immediately without waiting for a zone change event.
            if (FModel.getPreferences().getPrefBoolean(FPref.UI_ORDER_HAND)) {
                ordering.sort(Comparator.comparingInt((CardView cv) -> cv.getCurrentState().getManaCost().getCMC())
                        .thenComparing(cv -> cv.getCurrentState().getColors().getOrderWeight())
                        .thenComparing(cv -> cv.getCurrentState().getName()));
            }
        }

        final List<CardPanel> placeholders = new ArrayList<>();
        final List<CardPanel> cardPanels = new ArrayList<>();
        final List<CardView> orderedExternalPlayableCards = orderExternalPlayableCards(getExternalPlayableCards());

        for (final CardView card : ordering) {
            CardPanel cardPanel = p.getCardPanel(card.getId());
            if (cardPanel == null) {
                cardPanel = new CardPanel(matchUI, card);
                cardPanel.setDisplayEnabled(false);
                placeholders.add(cardPanel);
            } else {
                cardPanel.setCard(card); //ensure card view is updated
            }
            cardPanel.setZoneBanner(null, null);
            cardPanel.clearExternalPlayableBadge();
            cardPanels.add(cardPanel);
        }

        for (final CardView card : orderedExternalPlayableCards) {
            CardPanel cardPanel = p.getCardPanel(card.getId());
            if (cardPanel == null) {
                cardPanel = new CardPanel(matchUI, card);
                cardPanel.setDisplayEnabled(true);
            } else {
                cardPanel.setCard(card);
            }
            cardPanel.setZoneBanner(null, null);
            cardPanel.setExternalPlayableBadge(getExternalZoneLabel(card.getZone()), card.getZone());
            cardPanels.add(cardPanel);
        }

        p.setExternalPlayableCards(orderedExternalPlayableCards);
        p.setCardPanels(cardPanels);
        view.updateTabLabel(ordering.size());

        final VField vf = matchUI.getFieldViewFor(player);
        if (vf == null) {
            revealPlaceholders(placeholders);
            return;
        }
        final Rectangle rctLibraryLabel = vf.getDetailsPanel().getLblLibrary().getBounds();

        // Animation starts from the library label and runs to the hand panel.
        // If that label has not been realized yet, the hand should still update.
        if (rctLibraryLabel.isEmpty()) {
            revealPlaceholders(placeholders);
            return;
        }

        // Don't perform animations if the user's in another tab.
        if (!matchUI.isCurrentScreen()) {
            revealPlaceholders(placeholders);
            return;
        }

        //animate new cards into positions defined by placeholders
        final JLayeredPane layeredPane = Singletons.getView().getFrame().getLayeredPane();
        int fromZoneX = 0, fromZoneY = 0;

        final Point zoneLocation = SwingUtilities.convertPoint(vf.getDetailsPanel().getLblLibrary(),
                Math.round(rctLibraryLabel.width / 2.0f), Math.round(rctLibraryLabel.height / 2.0f), layeredPane);
        fromZoneX = zoneLocation.x;
        fromZoneY = zoneLocation.y;
        int startWidth, startX, startY;
        startWidth = 10;
        startX = fromZoneX - Math.round(startWidth / 2.0f);
        startY = fromZoneY - Math.round(Math.round(startWidth * forge.view.arcane.CardPanel.ASPECT_RATIO) / 2.0f);

        int endWidth, endX, endY;

        for (final CardPanel placeholder : placeholders) {
            endWidth = placeholder.getCardWidth();
            final Point toPos = SwingUtilities.convertPoint(view.getHandArea(), placeholder.getCardLocation(), layeredPane);
            endX = toPos.x;
            endY = toPos.y;

            if (Singletons.getView().getFrame().isShowing()) {
                final CardPanel animationPanel = new CardPanel(matchUI, placeholder.getCard());
                Animation.moveCard(startX, startY, startWidth, endX, endY, endWidth, animationPanel, placeholder,
                        layeredPane, 500);
            }
            else {
                Animation.moveCard(placeholder);
            }
        }
    }

    private void revealPlaceholders(final List<CardPanel> placeholders) {
        for (final CardPanel placeholder : placeholders) {
            Animation.moveCard(placeholder);
        }
    }

    public void updatePlayableStrip() {
        updateHand();
    }

    private List<CardView> getExternalPlayableCards() {
        final List<CardView> externalCards = new ArrayList<>();
        if (player != null && matchUI.isLocalPlayer(player)) {
            final FCollectionView<CardView> cards = player.getCards(ZoneType.Flashback);
            if (cards != null) {
                for (final CardView card : cards) {
                    final ZoneType zone = card.getZone();
                    if (zone != null && zone != ZoneType.Hand && matchUI.mayView(card)) {
                        externalCards.add(card);
                    }
                }
            }
        }
        return externalCards;
    }

    private List<CardView> orderExternalPlayableCards(final List<CardView> externalCards) {
        reconcileExternalPlayableOrdering(externalCards);

        final Map<Integer, CardView> externalById = externalCards.stream()
                .collect(Collectors.toMap(CardView::getId, Function.identity(), (first, second) -> first));
        final List<CardView> orderedCards = new ArrayList<>();
        for (final Integer id : externalPlayableOrdering) {
            final CardView card = externalById.get(id);
            if (card != null) {
                orderedCards.add(card);
            }
        }
        return orderedCards;
    }

    private void reconcileExternalPlayableOrdering(final List<CardView> externalCards) {
        final Set<Integer> currentIds = externalCards.stream().map(CardView::getId).collect(Collectors.toCollection(HashSet::new));
        externalPlayableOrdering.removeIf(id -> !currentIds.contains(id));

        final Set<Integer> orderedIds = new HashSet<>(externalPlayableOrdering);
        for (final CardView card : externalCards) {
            final int id = card.getId();
            if (orderedIds.add(id)) {
                externalPlayableOrdering.add(id);
            }
        }
    }

    private void updateExternalPlayableOrderingFromPanels(final List<CardPanel> panels) {
        externalPlayableOrdering.clear();
        for (final CardPanel panel : panels) {
            if (view.getHandArea().isExternalPlayable(panel)) {
                externalPlayableOrdering.add(panel.getCard().getId());
            }
        }
    }

    private static String getExternalZoneLabel(final ZoneType zone) {
        if (zone == null) {
            return null;
        }
        return switch (zone) {
            case Graveyard -> "GY";
            case Exile -> "EX";
            case Command -> "CMD";
            case Library -> "LIB";
            case Sideboard -> "SB";
            default -> zone.name().toUpperCase();
        };
    }

    @Override
    public void update() {
        updateHand();
    }
}
