# Local Changes / Patch Notes

## Purpose

This file documents local commits and local patch differences relative to `origin/master`, not only uncommitted working-tree changes.
Read this file before merging, rebasing, resolving conflicts, or modifying related code.
These changes are for local use and may not be suitable for upstream PRs.

## 2026-07-31 Migration Result

The previous local `master` history was preserved as
`codex/backup-local-master-20260731`. Local features were then reintegrated from
the latest `origin/master` on `codex/integrate-local-features-20260731` by
porting feature groups instead of merging or rebasing the old local branch.

Final integration state before this documentation commit:

- Integration branch: `codex/integrate-local-features-20260731`
- Local `master` was moved directly to the verified integration HEAD.
- Code integration HEAD: `6aba48975263fbd273e1f647c788ae3748361cef`
- Commits ahead of `origin/master`: 30
- Backup branch: `codex/backup-local-master-20260731`

Migrated feature groups:

- Quest import preserves owned Snow-Covered lands.
- English/localized card-name search in choose-card-name prompts.
- zh-CN key alignment and `cardnames-zh-CN.txt` data.
- Random match backgrounds.
- Chosen color mana icon overlay.
- Card zoom alternate-face flip permissions.
- Yuna Grand Summon prompt count.
- Card Detail shortcut and Ctrl+T panel-tab hiding.
- Local `.gitignore` entry for `forge-gui-desktop/local/`.
- Historic Brawl and Duel Commander local overrides.
- Random color deck formats and non-color generated-list `Random` entries.
- Quick Gauntlet event-name alignment.
- Sealed ranking score.
- Black Lotus human payment / available-action handling.
- Freed macro canonical ability key, legacy fallback, and replay hardening.
- External playable cards displayed near the hand without entering real hand.
- Draft Log layout pollution fixes.
- Arena-style Draft/Sealed Gauntlet runs.
- Quest Mode world-travel set-code search.
- Puzzle cleanup timing fix.
- Mobile settings auto-save fix.
- Desktop Quest set chooser search.
- Game last-state player snapshot fix for concede-time CME.

Upstream-first / not migrated old logic:

- Ordinary `ListChooser` search follows upstream where upstream already provided
  equivalent behavior; local code only adds extra search-key support where still
  needed.
- In-game generated card text Chinese translation old logic was not migrated;
  its final net effect was judged too risky against current upstream text
  generation/localization paths.
- Older zh-CN key-fix commits were not replayed as commits; effective keys were
  aligned against the current upstream files instead.
- Local tools scripts and `tools/` documentation were not committed to the
  integration branch.
- `InputPassPriority.java` stash/worktree noise was not migrated.

Local files intentionally left uncommitted:

- `.cache/`
- `.codex/`
- `tools/`
- `forge-gui-mobile-dev/local/`
- local adventure `items-zh-CN.json` files
- local AI files such as `forge-gui/res/ai/Challenger.ai`

`stash@{0}` (`codex-pre-integration-tracked-worktree-20260731`) contained the
tracked macro fallback worktree state. Its effective `RecordActionsMacroSystem`
logic was migrated into the legacy macro fallback commit; the stash was kept
until after master alignment and documentation recording.

The current local branch is ahead of `origin/master`; this document is based on:

- `git log --oneline --decorate origin/master..HEAD`
- `git diff --stat origin/master..HEAD`
- `git diff --name-status origin/master..HEAD`
- `git diff origin/master..HEAD`

## Current Modified Files

Local patch files relative to `origin/master`:

- `M .gitignore`
- `M forge-core/src/main/java/forge/StaticData.java`
- `M forge-core/src/main/java/forge/deck/DeckRecognizer.java`
- `M forge-core/src/main/java/forge/deck/DeckFormat.java`
- `M forge-game/src/main/java/forge/game/GameFormat.java`
- `M forge-game/src/main/java/forge/game/player/Player.java`
- `M forge-game/src/main/java/forge/game/player/RegisteredPlayer.java`
- `M forge-gui-desktop/src/main/java/forge/itemmanager/DeckManager.java`
- `M forge-gui-desktop/src/main/java/forge/screens/deckeditor/controllers/CEditorCommander.java`
- `M forge-gui-desktop/src/main/java/forge/screens/deckeditor/controllers/CEditorConstructed.java`
- `M forge-gui-desktop/src/main/java/forge/screens/match/CMatchUI.java`
- `M forge-gui-desktop/src/main/java/forge/toolbox/FSkin.java`
- `M forge-gui-mobile/src/forge/assets/FSkinTexture.java`
- `M forge-gui-mobile/src/forge/screens/match/MatchController.java`
- `A forge-gui/res/formats/Casual/Duel Commander.txt`
- `A forge-gui/res/formats/Casual/Historic Brawl.txt`
- `M forge-gui/res/languages/zh-CN.properties`
- `M forge-gui/src/main/java/forge/deck/CommanderDeckGenerator.java`
- `M forge-gui/src/main/java/forge/deck/DeckImportController.java`
- `M forge-gui/src/main/java/forge/deck/DeckgenUtil.java`
- `M forge-gui/src/main/java/forge/localinstance/properties/ForgeProfileProperties.java`
- `M forge-gui/src/main/java/forge/model/FModel.java`

Additional committed/uncommitted local match UI files to keep documented:

- `M forge-gui-desktop/src/main/java/forge/control/KeyboardShortcuts.java`
- `M forge-gui-desktop/src/main/java/forge/gui/GuiChoose.java`
- `M forge-gui-desktop/src/main/java/forge/gui/ListChooser.java`
- `M forge-gui-desktop/src/main/java/forge/gui/framework/DragCell.java`
- `M forge-gui-desktop/src/main/java/forge/screens/match/controllers/CHand.java`
- `M forge-gui-desktop/src/main/java/forge/screens/match/views/VHand.java`
- `M forge-gui-desktop/src/main/java/forge/view/arcane/CardPanel.java`
- `M forge-gui-desktop/src/main/java/forge/view/arcane/CardPanelContainer.java`
- `M forge-gui-desktop/src/main/java/forge/view/arcane/HandArea.java`
- `M forge-gui-desktop/src/main/java/forge/screens/match/LimitedWinLose.java`
- `M forge-gui/res/languages/en-US.properties`
- `M forge-gui/src/main/java/forge/gamemodes/gauntlet/GauntletUtil.java`
- `M forge-gui/src/main/java/forge/gamemodes/limited/GauntletMini.java`
- `M forge-gui/src/main/java/forge/gamemodes/limited/LimitedWinLoseController.java`
- `M forge-gui/src/main/java/forge/gui/interfaces/IGuiGame.java`
- `M forge-gui/src/main/java/forge/localinstance/properties/ForgePreferences.java`
- `M forge-gui/src/main/java/forge/player/PlayerControllerHuman.java`

Additional deck editor / drafting UI layout files to keep documented:

- `M forge-gui-desktop/src/main/java/forge/gui/framework/SLayoutIO.java`
- `M forge-gui-desktop/src/main/java/forge/screens/deckeditor/CDeckEditorUI.java`
- `M forge-gui-desktop/src/main/java/forge/screens/deckeditor/controllers/CEditorDraftingProcess.java`
- `M forge-gui-desktop/src/main/java/forge/screens/deckeditor/views/VEditorLog.java`

## 1. Historic Brawl / Brawl Local Changes

### `forge-gui/res/formats/Casual/Historic Brawl.txt`

- What changed: adds a local Historic Brawl format file with Arena/Historic Brawl set coverage, an independent Historic Brawl banlist, and an `Additional:` list.
- Why: provides a real format target for the local Brawl override instead of hard-coding ordinary `Brawl.txt`.
- 2026-06-17 update:
  - Added source/check-date comments for the Wizards banned/restricted list.
  - Reconciled `Sets:` against local `forge-gui/res/formats/Sanctioned/Historic.txt`.
  - Added missing Historic set codes: `XANA`, `OANA`, `YECL`, `YSOS`.
- 2026-06-17 temporary workaround:
  - Bug recorded: Historic Brawl legality can reject card names that should be legal when Forge only has a local printing in a set code that is not present in `Historic Brawl.txt` / `Historic.txt`; example: `Dina, Essence Brewer` exists locally only in `SOC`, so direct Historic Brawl import treats it as coming from a not-allowed set.
  - Workaround implemented for deck import only: `DeckImportController` first applies normal Historic Brawl validation; if a card fails only because its set is not allowed, `DeckRecognizer` can accept it through the ordinary `Commander` legality predicate and emits an import warning.
  - This workaround is intentionally narrow: it does not bypass the Historic Brawl banlist, does not use the effective Duel Commander override, and does not affect random/basic/card-based Historic Brawl deck generation.
  - Follow-up preferred fix: model Arena Historic Brawl card-name legality independently from local printing set coverage, then remove this import-only fallback.
- Patch group: Historic Brawl / Brawl local changes.
- Merge upstream notes: keep `Banned:` independent from `Brawl.txt` and `Historic.txt`; do not blindly union `Additional:` with other formats because `Additional` is name-based and can bypass bans if used carelessly.

### `forge-game/src/main/java/forge/game/GameFormat.java`

- What changed: registers `Historic Brawl.txt` in the core format list.
- Why: lets `FModel.getFormats().get("Historic Brawl")` resolve as a loaded `GameFormat`.
- Patch group: Historic Brawl / Brawl local changes, plus Duel Commander local override.
- Merge upstream notes: if upstream changes `coreFormats`, preserve both local entries while the overrides remain: `Historic Brawl.txt` and `Duel Commander.txt`.

### `forge-gui/src/main/java/forge/localinstance/properties/ForgeProfileProperties.java`

- What changed: adds profile property `useHistoricBrawlOverride`, defaulting to false, loaded from the profile file and saved only when true.
- Why: keeps the Historic Brawl behavior opt-in for local use.
- Patch group: Historic Brawl / Brawl local changes, plus Duel Commander local override.
- Merge upstream notes: preserve default false and the exact property name if local profiles already use it.

### `forge-core/src/main/java/forge/StaticData.java`

- What changed: adds `useHistoricBrawlOverride` with getter/setter.
- Why: makes the override visible to lower-level deck format code, especially `DeckFormat.Brawl`.
- Patch group: Historic Brawl / Brawl local changes, plus Duel Commander local override.
- Merge upstream notes: conflicts here are likely if upstream changes format predicate storage; keep the override flag separate from the stored Brawl predicate.

### `forge-gui/src/main/java/forge/model/FModel.java`

- What changed:
  - Adds `isHistoricBrawlOverride()`.
  - Adds `getEffectiveBrawlFormat()`, returning `Historic Brawl` when the local override is enabled and `Brawl` otherwise.
  - Initializes the Brawl predicate from `getEffectiveBrawlFormat().getFilterRules()`.
  - Updates `brawlCommander` to use upstream-style `getAllCards(...)` plus local `getEffectiveBrawlFormat().getFilterPrinted()`.
- Why: centralizes the Brawl/Historic Brawl switch so ordinary Brawl behavior remains available when the override is off.
- Patch group: Historic Brawl / Brawl local changes, plus Duel Commander local override.
- Merge upstream notes: if `brawlCommander` conflicts again, keep both the upstream `getAllCards(...)` query style and the local `getEffectiveBrawlFormat().getFilterPrinted()` behavior. Do not regress to `getFormats().get("Brawl")` where the effective format is required.

### `forge-core/src/main/java/forge/deck/DeckFormat.java`

- What changed:
  - `DeckFormat.Brawl.getMainRange()` returns 99 main cards when `useHistoricBrawlOverride` is true; otherwise it keeps ordinary 59-card Brawl.
  - Historic Brawl override adds a stricter single-commander check for Brawl.
  - Adds a `paperCardPoolFilter` legality pass over all deck cards, reporting illegal printed cards.
- Why: Historic Brawl uses a 99-card main deck and needs the effective format filter to be enforced during deck conformance.
- Patch group: Historic Brawl / Brawl local changes.
- Merge upstream notes: this file is high-conflict risk because `DeckFormat` is central. Preserve normal Brawl when the override is false; preserve printed-card legality checking if the local override still depends on it.

### `forge-gui-desktop/src/main/java/forge/itemmanager/DeckManager.java`

- What changed: Brawl catalog set filtering uses `FModel.getEffectiveBrawlFormat().getAllowedSetCodes()` instead of hard-coded `FModel.getFormats().get("Brawl")`.
- Why: deck manager filtering should follow Historic Brawl when the override is enabled.
- Patch group: Historic Brawl / Brawl local changes.
- Merge upstream notes: avoid reverting this one-line change to hard-coded `Brawl`.

### `forge-gui-desktop/src/main/java/forge/screens/deckeditor/controllers/CEditorCommander.java`

- What changed: Brawl editor commander and normal pools use `FModel.getEffectiveBrawlFormat()`.
- Why: when editing Brawl under the override, the commander pool and card pool should follow Historic Brawl legality.
- Patch group: Historic Brawl / Brawl local changes.
- Merge upstream notes: preserve effective format use while ordinary Brawl still works with the override disabled.

### `forge-gui-desktop/src/main/java/forge/screens/deckeditor/controllers/CEditorConstructed.java`

- What changed: constructed editor's Brawl normal pool uses `FModel.getEffectiveBrawlFormat().getAllCards()`.
- Why: keeps the displayed Brawl card pool aligned with the local Historic Brawl override.
- Patch group: Historic Brawl / Brawl local changes.
- Merge upstream notes: avoid restoring `FModel.getFormats().get("Brawl").getAllCards()` unless the override is intentionally removed.

### `forge-gui/src/main/java/forge/deck/CommanderDeckGenerator.java`

- What changed: card-based Brawl commander generation reads LDA data from `FModel.getEffectiveBrawlFormat().getName()` and falls back to all unique cards when no matching LDA pool exists.
- Why: Historic Brawl override has a different format name/card pool than Standard Brawl; without this, generation could be tied to Standard data or fail when no Historic Brawl LDA data exists.
- Patch group: Historic Brawl / Brawl local changes.
- Merge upstream notes: this is intentionally limited to Brawl card generation; it does not rewrite Commander generation generally.

### `forge-gui/src/main/java/forge/deck/DeckgenUtil.java`

- What changed:
  - Brawl card-based generation uses `FModel.getEffectiveBrawlFormat()` for LDA lookup and fallback pool.
  - Brawl preselected cards handle missing LDA data and null cards more safely.
  - Partner selection is skipped for Brawl in that generation path.
  - Brawl final color-list filtering uses the effective Brawl format's `getFilterPrinted()`.
- Why: random/card-based Brawl decks should follow the local Historic Brawl override and not assume Standard Brawl data.
- Patch group: Historic Brawl / Brawl local changes.
- Merge upstream notes: preserve effective Brawl format usage in both generation and printed-card filtering.

### `forge-gui/src/main/java/forge/deck/DeckImportController.java`

- What changed: deck import now uses `FModel.getEffectiveBrawlFormat()` for Brawl and `FModel.getEffectiveCommanderFormat()` for Commander.
- Why: when `useHistoricBrawlOverride=true` or `useDuelCommanderOverride=true`, the import recognizer must validate imported decklists against the effective local format instead of hard-coded `Brawl` / `Commander`.
- Patch group: Historic Brawl / Brawl local changes, plus Duel Commander local override.
- Merge upstream notes: do not regress import format setup to `FModel.getFormats().get(gameType.name())` for Commander or Brawl while these local overrides exist.

## 2. Duel Commander Local Override

This is a low-intrusion local override:

- Does not add `GameType.DuelCommander`.
- Does not add `DeckFormat.DuelCommander`.
- Does not change UI display.
- Does not change deck storage.
- Does not change editor pools.
- Does not change `commanderPool`.
- Reuses `GameType.Commander`, `DeckFormat.Commander`, and existing Commander UI.

### `forge-gui/res/formats/Casual/Duel Commander.txt`

- What changed:
  - Adds local `Name:Duel Commander`, `Type:Casual`, `Subtype:Commander`, `Order:138`.
  - Records official source URL and checked date.
  - Writes only official "Banned in Deck" cards into `Banned:`.
  - Documents "Banned as Companion" and "Banned as Commander only" as comments/TODOs, not format bans.
- 2026-06-17 update: rechecked the official Duel Commander banlist, kept the `Banned:` line at the current 80 fully banned cards, and updated `Latest checked`.
- Why: Forge `GameFormat` can express deck-wide `Banned:` cards, but not companion-only or commander-only bans.
- Patch group: Duel Commander local override.
- Merge upstream notes: do not put companion-only or commander-only cards into `Banned:`. If upstream adds first-class support for these categories, revisit the comments and format fields.

### `forge-game/src/main/java/forge/game/GameFormat.java`

- What changed: registers `Duel Commander.txt` in the core format list.
- Why: lets `FModel.getFormats().get("Duel Commander")` resolve.
- Patch group: Duel Commander local override, plus Historic Brawl / Brawl local changes.
- Merge upstream notes: keep this registration if the local override remains.

### `forge-gui/src/main/java/forge/localinstance/properties/ForgeProfileProperties.java`

- What changed: adds `useDuelCommanderOverride`, defaulting false, loaded from the profile file and saved only when true.
- Why: makes Duel Commander behavior opt-in and local-profile driven.
- Patch group: Duel Commander local override, plus Historic Brawl / Brawl local changes.
- Merge upstream notes: preserve default false and the exact property name if local profiles already use it.

### `forge-core/src/main/java/forge/StaticData.java`

- What changed: adds `useDuelCommanderOverride` with getter/setter.
- Why: makes the Commander override state visible to game code such as life total and commander damage handling.
- Patch group: Duel Commander local override, plus Historic Brawl / Brawl local changes.
- Merge upstream notes: this intentionally does not add a separate Duel Commander predicate field.

### `forge-gui/src/main/java/forge/model/FModel.java`

- What changed:
  - Adds `isDuelCommanderOverride()`.
  - Adds `getEffectiveCommanderFormat()`, returning `Duel Commander` when enabled and `Commander` otherwise.
  - Initializes the Commander predicate from `getEffectiveCommanderFormat().getFilterRules()`.
- Why: lets ordinary `DeckFormat.Commander` validation use Duel Commander legality when the local override is enabled.
- Patch group: Duel Commander local override, plus Historic Brawl / Brawl local changes.
- Merge upstream notes: ordinary Commander must still resolve to `Commander` when the override is false.

### `forge-game/src/main/java/forge/game/player/RegisteredPlayer.java`

- What changed: Commander still assigns commanders, but skips the normal +20 life adjustment when `useDuelCommanderOverride` is true.
- Why: Duel Commander starts at 20 life; ordinary Commander remains 40 life.
- Patch group: Duel Commander local override.
- Merge upstream notes: preserve ordinary Commander 40-life behavior when the override is false.

### `forge-game/src/main/java/forge/game/player/Player.java`

- What changed: commander damage is disabled when `useDuelCommanderOverride` is true.
- Why: Duel Commander does not use commander damage.
- Patch group: Duel Commander local override.
- Merge upstream notes: preserve ordinary Commander damage behavior when the override is false; Brawl/Oathbreaker/Tiny Leaders continue to ignore commander damage as before.

## 3. Skin / UI / Theme Changes

### `forge-gui-desktop/src/main/java/forge/toolbox/FSkin.java`

- What changed:
  - Adds current-skin-first fallback helper for `bg_day.jpg`, `bg_night.jpg`, and `bg_match.jpg`.
  - Adds random per-match match background selection from the current skin's direct `bg_match/` folder.
  - Accepts direct child files ending in `.jpg`, `.jpeg`, or `.png`, case-insensitive.
  - Ignores subdirectories and unsupported extensions by file filtering.
- Why: supports local skins that keep day/night backgrounds as root single files and match backgrounds as multiple images under `bg_match/`.
- Patch group: Skin / UI / theme changes.
- Merge upstream notes: preserve fallback order: current skin `bg_match/` candidates, current skin root `bg_match.jpg`, then default skin `bg_match.jpg`. Do not move random selection into repaint/render paths.

### `forge-gui-desktop/src/main/java/forge/screens/match/CMatchUI.java`

- What changed:
  - Calls `FSkin.selectMatchBackgroundForNewMatch()` when opening a desktop match and the match has no outcomes yet.
  - Match UI usability patch: refreshes the hand display when `ZoneType.Flashback` external playable cards or their source zones change.
  - `findCardPanel(...)` can find external playable card panels that are visually appended to `HandArea`, so clicking them can anchor the ability menu without forcing a `FloatingZone` tab to open.
- Why:
  - Chooses one background for a full match, not every repaint or every game inside a match.
  - Lets non-hand playable cards be used from the hand area without moving them into the real hand.
- Patch group: Skin / UI / theme changes, plus Match UI usability changes.
- Merge upstream notes: preserve match-start timing; day/night switches and screen changes should not re-randomize the match background. For playable cards, keep `ZoneType.Flashback` as a UI data source only, refresh when Graveyard / Exile / Command / Library / Sideboard change, and do not move cards into `ZoneType.Hand`.

### `forge-gui-mobile/src/forge/assets/FSkinTexture.java`

- What changed:
  - Adds mobile random match background selection from the current skin's direct `bg_match/` folder.
  - Stores `currentMatchBackgroundFile` as the per-match cached background.
  - Resets only the `BG_MATCH` texture cache after a new match background is selected.
  - Keeps fallback to current skin root `bg_match.jpg`, then default skin `bg_match.jpg`.
- Why: keeps mobile behavior aligned with the local desktop skin feature while preserving existing single-file fallback.
- Patch group: Skin / UI / theme changes.
- Merge upstream notes: keep filtering limited to direct `.jpg`, `.jpeg`, `.png` files and avoid recursive directory reads.

### `forge-gui-mobile/src/forge/screens/match/MatchController.java`

- What changed: calls `FSkinTexture.selectMatchBackgroundForNewMatch()` when a mobile match screen is created and the match has no outcomes yet.
- Why: chooses one mobile match background per full match.
- Patch group: Skin / UI / theme changes.
- Merge upstream notes: preserve selection before `MatchScreen` creation and avoid randomizing from draw/render code.

Related files to watch, not modified by `origin/master..HEAD` in this patch set:

- `forge-gui-desktop/src/main/java/forge/control/FControl.java`
  - Watch because it uses the cached/current `BG_MATCH` when switching screens, but it is not modified in the current local diff relative to `origin/master`.

### Draft Log tab lifecycle and editor layout pollution fix

Related files:

- `forge-gui-desktop/src/main/java/forge/gui/framework/SLayoutIO.java`
- `forge-gui-desktop/src/main/java/forge/screens/deckeditor/CDeckEditorUI.java`
- `forge-gui-desktop/src/main/java/forge/screens/deckeditor/controllers/CEditorDraftingProcess.java`
- `forge-gui-desktop/src/main/java/forge/screens/deckeditor/views/VEditorLog.java`
- `forge-gui/res/languages/en-US.properties`
- `forge-gui/res/languages/zh-CN.properties`

Problem:

- The drafting process can temporarily add `VEditorLog` for Conspiracy / special draft cards that need a draft log.
- The drafting process and normal deck editor share editor layout machinery.
- If the user opened the Draft Log tab, then switched to the deck editor / home / deck editor again, the temporary `EDITOR_LOG` tab could be persisted into the user editor layout.
- The same polluted layout could also preserve the state where normal catalog-side editor tabs had been removed.
- Restarting Forge was not enough because the bad state lived in the saved local layout file; resetting layout fixed it.

What changed:

- `CEditorDraftingProcess` adds `VEditorLog` only when `boosterDraft.shouldShowDraftLog()` is true.
- Drafting-process tab removal uses a one-time removal path so repeated `update()` calls do not overwrite the remembered parent cells with null.
- Leaving `FScreen.DRAFTING_PROCESS` removes `VEditorLog` and restores temporarily removed tabs.
- `CDeckEditorUI` asks the old drafting-process controller to reset its temporary UI changes when switching away from that controller.
- `SLayoutIO.save(...)` skips saving layout while the current screen is `FScreen.DRAFTING_PROCESS`, so transient draft UI state is not written as persistent editor layout.
- `SLayoutIO.loadLayout(...)` detects a user layout containing `EDocID.EDITOR_LOG`, treats it as polluted editor layout, deletes that user layout, and falls back to the default layout.
- `VEditorLog` no longer forces opaque tab/content backgrounds, fixing the pale/white Draft Log tab title.
- The tab label is `Draft Log` in English and `轮抓日志` in zh-CN.

Scope / risks:

- This is a deck editor / drafting UI layout pollution fix, not a match UI change.
- Existing polluted user layout files are intentionally reset once if they contain `EDITOR_LOG`.
- The pollution detector is keyed to `EDocID.EDITOR_LOG`; ordinary non-editor layouts should not contain that doc.
- The fix should not affect Limited Gauntlet, Match UI, or normal deck rules.

Verification:

- Normal draft screens should not show Draft Log when no special draft-log card requires it.
- Conspiracy / special draft flows that require the draft log should show Draft Log.
- Open Draft Log, switch to Deck Editor, Home, then Deck Editor again: Draft Log should not follow.
- Normal Deck Editor catalog-side tabs should remain present after leaving the draft screen.
- A saved user layout containing `EDITOR_LOG` should reset to default instead of loading an empty/polluted editor layout.

## 4. Match UI Usability Changes

These are local desktop match UI patches. They are convenience/usability changes and may not be suitable for upstream PRs without more design review.

### Quick Gauntlet progress names for filtered allowed deck types

Related file:

- `forge-gui/src/main/java/forge/gamemodes/gauntlet/GauntletUtil.java`

Problem:

- Quick Gauntlet match progress depends on `GauntletData.eventNames`.
- Several generated opponent deck types created a deck but did not add a matching event name.
- When the user filtered allowed opponent deck types down to only those generators, `decks` was populated but `eventNames` could be empty or shorter than `decks`, so the in-match gauntlet progress list did not render.
- With all allowed deck types enabled, progress often appeared because some selected deck types, such as preconstructed/theme/quest/custom/net decks, already added event names.

What changed:

- After each generated opponent deck is accepted, `GauntletUtil.createQuickGauntlet(...)` now fills in a missing event name.
- It uses the generated deck name when available and falls back to the selected `DeckType` string if needed.
- The intent is to keep `eventNames` aligned with `decks` without changing the progress UI.

Scope:

- This only fixes Quick Gauntlet progress for filtered allowed deck types.
- It does not change Gauntlet Contests / locked contest progress handling.

Verification:

- Start a Quick Gauntlet with only generated deck types enabled, such as Standard/Pioneer/Modern cardgen or color deck generators.
- The in-match gauntlet progress panel should show entries instead of being blank.
- Quick Gauntlet with all allowed deck types still shows progress.
- Gauntlet Contests progress behavior is intentionally unchanged by this patch.

Merge upstream notes:

- Preserve the invariant that every accepted opponent deck has a corresponding `eventNames` entry.
- If upstream rewrites gauntlet generation, keep `eventNames.size()` aligned with `decks.size()`.

### Draft / Sealed Gauntlet Arena-style run display

Related files:

- `forge-gui/src/main/java/forge/gamemodes/limited/GauntletMini.java`
- `forge-gui/src/main/java/forge/gamemodes/limited/LimitedWinLoseController.java`
- `forge-gui-desktop/src/main/java/forge/screens/match/LimitedWinLose.java`
- `forge-gui/res/languages/en-US.properties`
- `forge-gui/res/languages/zh-CN.properties`

Problem:

- Draft Gauntlet and Sealed Gauntlet match-end UI used traditional round wording such as "Round: 1/n" and a restart button that could read like "start a new round" even when the next action was continuing the current limited run.
- Losing a match did not present the intended Arena-style limited event flow.
- The right-side results area did not clearly show the current run record or per-match score history.

What changed:

- Mini limited gauntlets now track match-level run results separately from old game-level win/loss counters.
- Draft runs end at 7 match wins, 3 match losses, or the maximum available matches.
- Sealed runs end at 7 match wins, 2 match losses, or the maximum available matches.
- A loss records one match loss and, while the run is still live, continues to the next match rather than retrying the same round.
- AI opponents are selected as distinct played opponent indexes when possible, and the displayed AI name is the generated lobby player name rather than an object/class string.
- The match-end panel shows the run record, wins/losses against the cap, current match number, structured match history, and current match status.
- Match history stores data in `GauntletMini.MatchRecord`; UI localization is handled by `LimitedWinLoseController`, not stored inside the record.
- Current match score uses match game outcomes and displays scores such as `1-0`, `0-1`, or `2-1`.
- Finished match history entries use `Match {n}: player (Player) vs ai (AI): score`; the still-active match is shown separately under `Current Match`.
- When the final match ends by reaching 7 wins, the loss cap, or the max match count, the final match is archived into history and no duplicate `Current Match` block is shown.
- During a best-of-three final match that is not over yet, the current match block remains visible even if the current match number equals the max match count.
- The restart button is hidden between games/matches during a live run and only becomes a real "Start New Run" action once the run is complete.

Scope:

- This is a UI/run-state patch for Draft Gauntlet and Sealed Gauntlet match-end flow.
- It does not change card rules, match rules, or deck construction rules.
- It does not change Quick Gauntlet or Gauntlet Contests progress behavior.

Verification:

- Draft Gauntlet can continue after losses until 3 match losses, and ends at 7 match wins, 3 match losses, or max matches.
- Sealed Gauntlet can continue after losses until 2 match losses, and ends at 7 match wins, 2 match losses, or max matches.
- Losing a match records a loss and advances to the next match while the run is live.
- The restart button is not shown as a misleading action between games or before a live run ends.
- The final match is present in match history when the run ends.
- The final match is not duplicated as both history and current match after the run ends.
- A best-of-three final match still shows current score/status between games before the match is over.
- Match history keeps earlier opponents and results when moving to later opponents.
- Displayed AI names are readable player names, not Java class/object strings.
- Match records show scores such as `1-0`, `0-1`, and `2-1` correctly.

Merge upstream notes:

- Keep match-level `matchWins` / `matchLosses` separate from any existing game-level counters.
- Preserve the distinction between finished `matchHistory` and the active/current match.
- Do not store localized UI strings inside `GauntletMini.MatchRecord`; keep formatting/localization in `LimitedWinLoseController`.
- Be careful around final-match archiving: the last match must be archived when the run ends, but must not also be shown as current after match over.
- If upstream changes limited win/lose screens, preserve the larger right-side text area needed for match history.

### Choose card name search supports English and localized names

Related files:

- `forge-gui-desktop/src/main/java/forge/gui/ListChooser.java`
- `forge-gui-desktop/src/main/java/forge/gui/GuiChoose.java`
- `forge-gui/src/main/java/forge/gui/interfaces/IGuiGame.java`
- `forge-gui/src/main/java/forge/player/PlayerControllerHuman.java`

Problem:

- In localized UI, especially zh-CN, "choose a card name" dialogs such as Pithing Needle, Meddling Mage, Sorcerous Spyglass, and The Stone Brain were hard to search.
- `ListChooser` filtered only against the current display text. In zh-CN the display text is usually the translated name, so English canonical names did not match.
- The old normalize logic kept only `[a-z0-9 ]`, so Chinese input was stripped to an empty search string.

What changed:

- `ListChooser` supports optional extra search keys.
- The choose-card-name path passes both English canonical card names and current-language translated names as search keys.
- Normalization keeps Unicode letters and digits, so Chinese input is not discarded.
- The UI can still display translated names.
- The selected result still returns the English canonical card name, so rules-layer named-card data is not saved as a localized string.

Verification:

- In zh-CN UI, searching `Pithing Needle` or `Lightning Bolt` should find the card.
- In zh-CN UI, searching a translated name such as `闪电击` should find the card when that translation exists.
- English UI search should not regress.
- Named-card effects should still store and compare canonical English names.

Merge upstream notes:

- Keep chooser API changes small and optional; generic chooser callers should behave as before when no search-key provider is supplied.
- Do not move localized names into rules data.
- If upstream changes `ListChooser` filtering, preserve Unicode-aware normalization and canonical-name search keys for choose-card-name flows.

### Card Detail keyboard shortcut

Related files:

- `forge-gui-desktop/src/main/java/forge/control/KeyboardShortcuts.java`
- `forge-gui/src/main/java/forge/localinstance/properties/ForgePreferences.java`
- `forge-gui/res/languages/en-US.properties`
- `forge-gui/res/languages/zh-CN.properties`

What changed:

- Adds shortcut preference `SHORTCUT_SHOWCARDDETAIL`.
- Adds keyboard action `actShowCardDetail`.
- The action calls `SDisplayUtil.showTab(EDocID.CARD_DETAIL.getDoc())`.
- Adds English and Simplified Chinese shortcut label keys.

Why:

- When panel tab headers are hidden or a multi-tab cell is inconvenient to click, Card Detail can still be brought to the foreground quickly.
- This supports the local immersive match UI workflow.

Merge upstream notes:

- Keep the preference/action/key names aligned.
- Do not remove this shortcut if also preserving the local all-tab-header hiding behavior below.

### Ctrl+T / Panel Tabs hides all DragCell headers

Related file:

- `forge-gui-desktop/src/main/java/forge/gui/framework/DragCell.java`

What changed:

- Original logic kept a header visible for multi-doc/multi-tab cells even when `showTabs=false`:
  - `showTabs || allDocs.size() > 1`
- Local logic makes header height depend only on `showTabs`:
  - `showTabs ? SLayoutConstants.HEAD_H : 0`

Effect:

- Ctrl+T / Panel Tabs off hides every `DragCell` title/header, including multi-tab cells.
- Only the header/tab strip is hidden.
- Pane bodies remain visible.
- No dockable docs are removed or added.
- Layout save/load logic is not changed.

Risks:

- When a multi-tab cell's header is hidden, the user cannot use the mouse to click other tabs in that same cell.
- Stack / Combat / Log already have shortcuts and/or automatic switching.
- Card Detail now has a shortcut.
- Dependencies / Dock / Prompt may still require turning Panel Tabs back on to switch conveniently.
- This is an immersive local UI tweak, not necessarily an upstream-ready behavior.

### External playable cards shown in HandArea

Related files:

- `forge-gui-desktop/src/main/java/forge/screens/match/CMatchUI.java`
- `forge-gui-desktop/src/main/java/forge/screens/match/controllers/CHand.java`
- `forge-gui-desktop/src/main/java/forge/screens/match/views/VHand.java`
- `forge-gui-desktop/src/main/java/forge/view/arcane/CardPanel.java`
- `forge-gui-desktop/src/main/java/forge/view/arcane/CardPanelContainer.java`
- `forge-gui-desktop/src/main/java/forge/view/arcane/HandArea.java`

What changed:

- Non-hand playable/actionable cards are visually appended to the end of `HandArea`, after real hand cards.
- Data source is `player.getCards(ZoneType.Flashback)`, filtered to `card.getZone() != ZoneType.Hand`.
- In Forge, `ZoneType.Flashback` here is an external playable cards view, not only literal flashback cards. It may include playable/actionable cards from Graveyard, Exile, Command, Library, Sideboard, and similar external zones.
- The previous right-side always-visible `PlayableArea` strip was removed; `HandArea` is again the main display area.
- `CMatchUI.updateZones(...)` refreshes the external playable display when the local player's `Graveyard`, `Exile`, `Command`, `Library`, or `Sideboard` changes, not only when `Hand` or `Flashback` changes.

Rules and data boundaries:

- External playable cards are not added to `player.getHand()`.
- They do not count toward hand count.
- They do not affect discard, random discard, or maximum hand size.
- They do not enter real `CHand.ordering`.
- They keep their real zone.
- Clicking still uses the existing `selectCard(...)` / ability flow, so commander tax, flashback, exile casting, and other rules remain handled by the normal rules layer.

UI behavior:

- External cards append after real hand cards.
- `CardPanel` has a UI-only external playable badge drawn inside the card face, such as `GY`, `EX`, `CMD`, `LIB`, or `SB`.
- The badge replaces the older `setZoneBanner(...)` approach, so it does not add height or change `CardArea` layout.
- `HandArea` tracks `externalPlayableIds` and exposes `isExternalPlayable(...)`.
- `CHand` tracks `externalPlayableOrdering`, storing only external playable card ids.
- External playable cards can be dragged to reorder within the external group.
- Dragging an external card never calls `reorderHand(...)`.
- Real hand dragging still calls `reorderHand(...)`, with indices computed while skipping external playable panels.
- The external group always remains after real hand cards. Full visual mixing with true hand cards is intentionally not implemented.

Refresh behavior:

- `PlayerView.updateZone(...)` recalculates the `Flashback` / external playable view when source zones such as Graveyard, Exile, Library, or Command change.
- The desktop UI now rereads that view after those source-zone updates.
- This fixes cases such as Phlage, Titan of Fire's Fury entering the graveyard after being cast from hand: once it has escape and becomes externally playable, it appears in the `HandArea` external group without waiting for the next real hand change.
- Escape / Flashback / Adventure / Foretell / Prepared / Command-zone playable changes should refresh through the same source-zone path.

Risks / TODO:

- External cards are currently the same size as hand cards.
- Many external cards can still make the hand area visually crowded.
- The badge position/style may need tuning.
- Full visual mixing would require a separate `visualHandOrder` design and careful handling of `UI_ORDER_HAND`, animations, and real hand reorder boundaries.

### Chosen color mana icons on CardPanel

Related file:

- `forge-gui-desktop/src/main/java/forge/view/arcane/CardPanel.java`

Problem:

- Cards such as Sunken Citadel and Painter's Servant record chosen colors in the rules layer and expose them through `CardView` / Card Detail.
- The battlefield card panel itself did not have an immediate visual marker, so the user had to hover or open Card Detail to confirm the chosen color.

What changed:

- Desktop `CardPanel` draws mana icons for `chosenColors`.
- It uses `CardFaceSymbols.drawSymbol(...)`, reusing Forge's existing skin mana symbol resources.
- It does not hand-draw colored dots.
- It does not add new icon resources.
- It does not hard-code custom-skin or default-skin paths.
- Icons are drawn inside the card face, vertically along the right-middle area, with transparency.
- Only chosen colors are shown on the card face.
- Chosen type / chosen name are not drawn on the card face; they remain available in Card Detail.

Verification:

- Sunken Citadel choosing blue should show the blue mana symbol on its `CardPanel`.
- Painter's Servant choosing red should show the red mana symbol.
- Cavern of Souls / Pithing Needle chosen type/name should not appear as card-face text, but Card Detail should still show that information.
- Counters, external playable badges, and group-count badges should not visibly conflict.
- Z-key and mouse-wheel zoom should not be affected, because this is a desktop `CardPanel` overlay only.

Merge upstream notes:

- Keep this as a UI overlay based only on `CardView.getChosenColors()`.
- Do not read rules-layer `Card` directly from the panel.
- Do not add resources or bypass skin fallback.
- Be careful around nearby counter, group-count, and external-playable badge drawing code.

### Wheel card zoom flip fix

Related file:

- `forge-gui-desktop/src/main/java/forge/view/arcane/CardPanelContainer.java`

What changed:

- Desktop match card zoom opened from mouse wheel-up now calls `CardZoomer.setCard(card.getCurrentState(), getMatchUI().mayFlip(card))`.
- The matching mouse-button zoom path also uses `getMatchUI().mayFlip(card)` instead of the old hard-coded `false`.

Why:

- The Z-key zoom path uses the Card Detail / Picture controller state, which is already initialized with `getMatchUI().mayFlip(card)` and therefore supports alternate state / flip where allowed.
- The direct `CardPanelContainer` mouse-wheel zoom path previously called `CardZoomer.setCard(card.getCurrentState(), false)`, which disabled alternate-state flipping even for visible transform, MDFC, or flip cards.

Merge upstream notes:

- Do not replace this with `true` or `card.hasAlternateState()`.
- `getMatchUI().mayFlip(card)` is required so face-down or hidden information stays protected.
- Z-key zoom behavior should remain unchanged.
- Split / aftermath rotation should be regression-tested because `CardZoomer` still has separate split-card rotation behavior when `mayFlip` is false.

## 5. Other Local Patches

### `.gitignore`

- What changed: adds `forge-gui-desktop/local/` to ignored paths.
- Why: keeps local desktop runtime files out of git.
- Patch group: Other local patches.
- Merge upstream notes: safe local maintenance change; if upstream changes nearby ignore rules, keep ignoring `forge-gui-desktop/local/` unless the local runtime layout changes.

### `forge-gui/res/languages/zh-CN.properties`

- What changed:
  - Adds missing Simplified Chinese localization keys that exist in `en-US.properties`.
  - Some keys have Chinese translations where clear.
  - A fallback block keeps uncertain values in English while preserving placeholders and escaping.
- Why: fixes missing localization/property warnings such as translation-missing messages in zh-CN.
- Patch group: Other local patches.
- Merge upstream notes: do not bulk-rewrite existing translations. When upstream adds new keys, compare against `en-US.properties` and append only missing zh-CN keys unless intentionally improving translations.

## Known Limitations / TODO

### Duel Commander

- Banned as Companion only is not represented.
  - Forge `GameFormat` currently has no `BannedAsCompanion` field.
  - Cards in this category must not be put in `Banned:`.
- Banned as Commander only is not represented.
  - Forge `GameFormat` currently has no `BannedAsCommander` field.
  - Cards in this category must not be put in `Banned:`.
- Between-game commander swap is not implemented.
  - Duel Commander allows changing commanders between games from another legal commander in the deck.
  - This local low-intrusion override does not implement that flow.
  - Workaround: exit match, manually change commander, restart match.
- `CommanderDeckGenerator.getCommanderDecks(...)` has an existing non-Brawl commander candidate filtering gap that is intentionally not fixed here.

### Historic Brawl / Brawl

- Historic Brawl is implemented as a local Brawl override, not a new public game type.
- The effective Brawl format must be preserved anywhere legality, commander pools, card pools, set catalog filtering, or Brawl generation should follow Historic Brawl.
- `Historic Brawl.txt` `Additional:` is name-based and must not be allowed to re-add banned cards.
- Keep `Banned:` independent from `Brawl.txt` and `Historic.txt`.

### Skin / UI

- Random match backgrounds are local skin behavior and may conflict with upstream skin loading changes.
- Random selection is intentionally per match, not per repaint, resize, screen switch, day/night toggle, or individual game inside a best-of match.
- The supported folder is the current skin's direct `bg_match/` directory. It is not recursive.

### Match UI

- Ctrl+T / Panel Tabs now hides all `DragCell` headers, including multi-doc cells.
- Card Detail has a dedicated shortcut so it can be restored when tab headers are hidden.
- External playable cards are visually shown in `HandArea`, but remain outside the real hand and real hand ordering.
- External playable card dragging is visual-only inside the external group and must not call `reorderHand(...)`.
- Mouse-wheel and mouse-button card zoom from match card panels use `getMatchUI().mayFlip(card)`, not `true`, `false`, or `card.hasAlternateState()`.
- Match current-state ability text is translated during generated text assembly, not only in static oracle/detail views.
- Quick Gauntlet generation keeps `eventNames` aligned with generated opponent decks so in-match progress still renders when allowed deck types are filtered.
- Draft / Sealed Gauntlet uses Arena-style limited run display: match-level record, run caps, readable AI opponent names, per-match score history, and no duplicate current match after the final match is archived.

### Localization

- The zh-CN fallback block intentionally keeps uncertain strings in English.
- Future translation cleanup should preserve placeholders such as `%s`, `%d`, `{0}`, `{1}`, `\n`, and doubled apostrophes.

## Merge / Rebase Notes for Future Codex Sessions

Before merging upstream, read this file first.

When conflicts occur:

- Do not blindly accept upstream or local.
- Identify which local patch group the conflict belongs to.
- Preserve local Historic Brawl override behavior unless intentionally removing it.
- Preserve local Duel Commander override behavior unless intentionally removing it.
- Preserve local skin/theme changes unless the user says otherwise.
- Do not put companion-only or commander-only Duel Commander bans into `Banned:`.
- Do not remove TODO comments unless the feature is actually implemented.
- If `FModel.java` conflicts around `brawlCommander`, keep upstream `getAllCards(...)` and local `getEffectiveBrawlFormat()`.
- Do not revert effective Brawl logic back to `getFormats().get("Brawl")`.
- Do not re-add the old Constructed debug tournament menu item unless explicitly debugging.

Most likely conflict points from the current local diff:

- `forge-gui/src/main/java/forge/model/FModel.java`
- `forge-core/src/main/java/forge/StaticData.java`
- `forge-gui/src/main/java/forge/localinstance/properties/ForgeProfileProperties.java`
- `forge-core/src/main/java/forge/deck/DeckFormat.java`
- `forge-game/src/main/java/forge/game/GameFormat.java`
- `forge-game/src/main/java/forge/game/player/RegisteredPlayer.java`
- `forge-game/src/main/java/forge/game/player/Player.java`
- `forge-gui/src/main/java/forge/deck/CommanderDeckGenerator.java`
- `forge-gui/src/main/java/forge/deck/DeckgenUtil.java`
- `forge-gui/src/main/java/forge/gamemodes/gauntlet/GauntletUtil.java`
- `forge-gui/src/main/java/forge/gamemodes/limited/GauntletMini.java`
- `forge-gui/src/main/java/forge/gamemodes/limited/LimitedWinLoseController.java`
- `forge-gui-desktop/src/main/java/forge/screens/match/LimitedWinLose.java`
- `forge-gui-desktop/src/main/java/forge/gui/framework/SLayoutIO.java`
- `forge-gui-desktop/src/main/java/forge/screens/deckeditor/CDeckEditorUI.java`
- `forge-gui-desktop/src/main/java/forge/screens/deckeditor/controllers/CEditorDraftingProcess.java`
- `forge-gui-desktop/src/main/java/forge/screens/deckeditor/views/VEditorLog.java`
- `forge-gui-desktop/src/main/java/forge/screens/deckeditor/controllers/CEditorCommander.java`
- `forge-gui-desktop/src/main/java/forge/screens/deckeditor/controllers/CEditorConstructed.java`
- `forge-gui-desktop/src/main/java/forge/itemmanager/DeckManager.java`
- `forge-gui-desktop/src/main/java/forge/toolbox/FSkin.java`
- `forge-gui-desktop/src/main/java/forge/screens/match/CMatchUI.java`
- `forge-gui-desktop/src/main/java/forge/screens/match/controllers/CHand.java`
- `forge-gui-desktop/src/main/java/forge/screens/match/views/VHand.java`
- `forge-gui-desktop/src/main/java/forge/gui/GuiChoose.java`
- `forge-gui-desktop/src/main/java/forge/gui/ListChooser.java`
- `forge-gui-desktop/src/main/java/forge/view/arcane/CardPanel.java`
- `forge-gui-desktop/src/main/java/forge/view/arcane/HandArea.java`
- `forge-gui-desktop/src/main/java/forge/gui/framework/DragCell.java`
- `forge-gui-desktop/src/main/java/forge/control/KeyboardShortcuts.java`
- `forge-gui-mobile/src/forge/assets/FSkinTexture.java`
- `forge-gui-mobile/src/forge/screens/match/MatchController.java`
- `forge-gui/res/formats/Casual/Historic Brawl.txt`
- `forge-gui/res/formats/Casual/Duel Commander.txt`
- `forge-gui/res/languages/en-US.properties`
- `forge-gui/res/languages/zh-CN.properties`
- `forge-gui/src/main/java/forge/gui/interfaces/IGuiGame.java`
- `forge-gui/src/main/java/forge/localinstance/properties/ForgePreferences.java`
- `forge-gui/src/main/java/forge/player/PlayerControllerHuman.java`
- `.gitignore`

## How to Enable Local Options

### Historic Brawl override

Set this profile property to true:

```properties
useHistoricBrawlOverride=true
```

Relevant code:

- `forge-gui/src/main/java/forge/localinstance/properties/ForgeProfileProperties.java`
- `forge-gui/src/main/java/forge/model/FModel.java`
- `forge-core/src/main/java/forge/StaticData.java`

When false or absent, ordinary Brawl behavior should remain active.

### Duel Commander override

Set this profile property to true:

```properties
useDuelCommanderOverride=true
```

Relevant code:

- `forge-gui/src/main/java/forge/localinstance/properties/ForgeProfileProperties.java`
- `forge-gui/src/main/java/forge/model/FModel.java`
- `forge-core/src/main/java/forge/StaticData.java`
- `forge-game/src/main/java/forge/game/player/RegisteredPlayer.java`
- `forge-game/src/main/java/forge/game/player/Player.java`

When false or absent, ordinary Commander behavior should remain active.

### Local match backgrounds

For desktop and mobile skins, put optional match background images in the current skin directory:

```text
skins/<skinName>/bg_match/
```

Accepted direct child file extensions:

- `.jpg`
- `.jpeg`
- `.png`

Fallback order:

1. current skin `bg_match/` direct image files
2. current skin root `bg_match.jpg`
3. default skin `bg_match.jpg`

Day/night backgrounds use current skin root single files first:

1. current skin root `bg_day.jpg` / `bg_night.jpg`
2. default skin `bg_day.jpg` / `bg_night.jpg`

### Random color deck formats

The generated deck type list includes local random color deck entries for Pioneer,
Historic, Legacy, Vintage, and Premodern in addition to the existing Standard,
Modern, and Pauper entries.

These entries are not separate generators. They reuse the existing random color
deck generator and pass the matching `GameFormat.getFilterPrinted()` predicate
for each format:

### Non-color random deck sentinels

Generated non-color deck lists can include a localized top-level `Random`
entry. Commander/Brawl-style commander generators, archetype matrix generators,
theme decks, and the random-deck category list use same-type sentinel entries
that choose from their current candidate snapshot when `getDeck()` is called.

The sentinel entries exclude themselves from their candidates, are skipped for
empty lists, and share `RandomDeckProxyUtil` for candidate selection. Mobile
lobby deck-name display uses the already generated deck instance so choosing
`Random` does not display one random deck name while launching another.

- `PIONEER_COLOR_DECK`
- `HISTORIC_COLOR_DECK`
- `LEGACY_COLOR_DECK`
- `VINTAGE_COLOR_DECK`
- `PREMODERN_COLOR_DECK`

Keep the shared, desktop, and mobile deck chooser paths aligned when merging
upstream: `DeckType`, `RandomDeckGenerator`, quick gauntlet/tournament utilities,
desktop `FDeckChooser` / lobby refresh, mobile `FDeckChooser` / lobby refresh,
and the language keys.

### Set chooser search

The desktop quest set chooser has a local search/filter field above the edition
checkbox tree.

Relevant code:

- `forge-gui-desktop/src/main/java/forge/screens/home/quest/DialogChooseSets.java`
- `forge-gui-desktop/src/main/java/forge/toolbox/FCheckBoxTree.java`

Behavior:

- Search filters the visible set tree by set name, code, secondary code, alias,
  Scryfall code, set type, and generic node label/key text.
- Hidden checked sets remain checked while the search filter is active.
- OK reads checked values from the full tree, not only from the filtered visible
  tree.
- Format restriction buttons and the random set selection controls operate on
  the full tree state, not only the current search results.
- Clicking random set selection clears the search field first, so the visible
  tree matches the newly randomized full-tree selection.
- The local full-tree APIs intentionally preserve the existing key fallback
  behavior where a requested key may match either the stored key or
  `requestedKey.hashCode()`.
- Quest Mode world travel now passes extra search keys to the travel chooser.
  Searching for a set code such as `THS`, `USG`, or `FIN` can find quest worlds
  whose own `QuestWorld` format includes that set.
- `Main world` is intentionally excluded from set-code fallback search. Since it
  has no world-local format, its search keys stay limited to its own name.
- The shared chooser API now supports optional extra search keys. Desktop uses
  the existing `ListChooser` search-key provider, and mobile `ListChooser`
  applies the same extra keys in its text filter.
- Quest Mode deck imports now distinguish regular basic lands from snow-covered
  basic lands. Only regular very-basic lands bypass inventory counting; owned
  snow-covered lands are imported through the normal inventory path.
- Match prompts show a `Grand Summon (Yuna)` count when Yuna, Grand Summoner has
  created pending command-zone effects, mirroring the existing storm-count style
  of lightweight prompt counters.
- Macro replay records activated abilities with a non-localized canonical
  `SpellAbility` key instead of using localized display text as the replay
  match key. This keeps localized text collisions, such as Freed from the Real
  Tap/Untap labels, from replaying the first matching display string.
- Macro replay also has hardening around repeated action playback: strict macro
  actions are not skipped after a failed replay attempt, repeat playback waits
  until the first action is executable, mana-pool payments must actually pay or
  consume mana before the action is accepted, and detailed macro diagnostics are
  available with `-Dforge.macro.debug=true`.

Merge note: keep the search/filter display logic separate from the complete
tree state logic. The search box should only hide/show rows; it should not
change which sets are selected unless the user or a bulk action explicitly
changes selection.

## Verification Checklist

- `git diff --name-status origin/master..HEAD` files are all represented in this document.
- Historic Brawl / Brawl override still switches through `getEffectiveBrawlFormat()`.
- `useHistoricBrawlOverride=false` keeps ordinary Brawl behavior.
- `useHistoricBrawlOverride=true` uses `Historic Brawl.txt` for Brawl predicate/card pool paths touched by this patch.
- Brawl commander pool keeps upstream `getAllCards(...)` lookup style plus local effective format filtering.
- `Historic Brawl.txt` `Banned:` and `Additional:` do not overlap.
- `Duel Commander.txt` loads through `GameFormat`.
- `useDuelCommanderOverride=false` keeps ordinary Commander behavior.
- `useDuelCommanderOverride=true` makes Commander predicate use `Duel Commander.txt`.
- Duel Commander override starts at 20 life.
- Duel Commander override disables commander damage.
- Companion-only and commander-only Duel Commander bans are not written into `Banned:`.
- Random match background selection happens once per match and stays fixed inside the match.
- Unsupported match background files such as `.b`, `.bb`, `.xcf`, extensionless files, and subdirectories are not selected.
- Ctrl+T / Panel Tabs hides all match `DragCell` headers, including multi-tab cells.
- Card Detail shortcut brings `EDocID.CARD_DETAIL` to the foreground.
- External playable cards appear after real hand cards in `HandArea`.
- External playable cards show internal badges such as `GY`, `EX`, `CMD`, `LIB`, or `SB`.
- External playable cards are not counted as hand cards and do not enter real hand ordering.
- Dragging external playable cards changes only external visual ordering and does not call `reorderHand(...)`.
- Dragging real hand cards still calls `reorderHand(...)` with indices that skip external playable panels.
- Clicking external playable cards still uses the normal `selectCard(...)` flow and should not force a `FloatingZone` tab just to anchor the ability menu.
- Z-key card zoom still behaves as before.
- Mouse-wheel-up card zoom can flip / show alternate state when `getMatchUI().mayFlip(card)` allows it.
- Mouse-button card zoom from match card panels follows the same `mayFlip` permission.
- Face-down or hidden cards do not expose unavailable alternate states through wheel/button zoom.
- Split / aftermath cards still rotate correctly in zoom.
- Quick Gauntlet progress appears when only generated allowed deck types are enabled.
- Quick Gauntlet `eventNames` stays aligned with generated opponent decks.
- Drafting Process does not save transient Draft Log layout state as persistent editor layout.
- Draft Log appears only when `boosterDraft.shouldShowDraftLog()` is true.
- Draft Log does not persist into normal Deck Editor after switching Drafting Process -> Deck Editor -> Home -> Deck Editor.
- Normal Deck Editor catalog-side tabs are restored after leaving Drafting Process.
- Existing user editor layout containing `EDocID.EDITOR_LOG` resets to the default layout instead of loading polluted state.
- Draft Gauntlet live runs continue after match losses until 3 losses, and end at 7 wins, 3 losses, or max matches.
- Sealed Gauntlet live runs continue after match losses until 2 losses, and end at 7 wins, 2 losses, or max matches.
- Draft / Sealed Gauntlet match-end UI shows run record, match score history, and current match status.
- Draft / Sealed Gauntlet final match is archived into match history when the run ends.
- Draft / Sealed Gauntlet final match is not duplicated as both history and current match after the run ends.
- Draft / Sealed Gauntlet best-of-three final match still shows current score/status between games before the match is over.
- Draft / Sealed Gauntlet displayed AI opponent names are readable player names.
- Choose-card-name dialogs can find cards by English canonical name in zh-CN UI.
- Choose-card-name dialogs can find cards by translated name when the translation exists.
- Choose-card-name selection still stores the canonical English name for rules effects.
- External playable cards refresh when Graveyard / Exile / Command / Library / Sideboard changes, not only when Hand changes.
- Phlage, Titan of Fire's Fury appears in the external playable group after entering the graveyard with escape, without waiting for another hand update.
- Phlage disappears from the external playable group after being cast via escape.
- Flashback / Adventure / Foretell / Prepared / Command-zone playable cards refresh when their source zones change.
- Sunken Citadel and Painter's Servant show chosen color mana icons on desktop `CardPanel`.
- Cavern of Souls / Pithing Needle chosen type/name are not drawn on the card face, but remain visible in Card Detail.
- zh-CN localization no longer reports the previously missing keys covered by this patch.
- Random Pioneer / Historic / Legacy / Vintage / Premodern color deck entries appear in the generated deck chooser lists where Standard / Modern / Pauper color deck entries appear.
- Random Pioneer / Historic / Legacy / Vintage / Premodern color decks generate through the normal color deck generator with the corresponding format printed-card filter.
- Non-color random commander, archetype, theme, and random-deck category lists show a localized `Random` entry at the top.
- Selecting the non-color `Random` entry generates from the current list candidates, excludes the Random entry itself, and keeps mobile displayed deck names aligned with the actual generated deck.
- Desktop set chooser search filters visible rows without losing hidden checked sets.
- Desktop set chooser OK includes checked sets hidden by the active search filter.
- Desktop set chooser format restriction buttons update the full set tree, not only visible search results.
- Desktop set chooser random set selection clears the search field and randomizes from the full active set tree.
- Quest Mode Travel can find worlds by world-local set code.
- Quest Mode Travel does not match Main world through the active quest/main format set list.
- Quest Mode deck import preserves owned Snow-Covered basic lands instead of
  treating them as regular free basics.
- Yuna, Grand Summoner prompt count appears after Grand Summon creates pending
  effects and disappears when none remain.
- zh-CN macro replay of Yuna + Freed from the Real repeatedly selects the
  recorded Untap ability, not the first same-text Tap ability.
- Macro replay hardening does not consume a strict action unless the current
  input accepts it, and mana payment actions are not consumed when no payment
  was made.
