# 本地改动说明

## 这份文件是干什么的

这个仓库不是纯净 upstream。当前本地分支里有一些我自己长期使用的修改，包括玩法格式 override、皮肤背景加载、本地化补全和本地忽略规则。

以后拉取 upstream、合并冲突、或者新开 Codex 会话时，先读这个文件，避免把这些本地修改覆盖掉。

英文版 `LOCAL_CHANGES.md` 是给 Codex / 合并冲突用的技术账本，写得更精确、更适合机器和代码审查。这个 `LOCAL_CHANGES.zh-CN.md` 是给我自己看的中文人话版，重点是快速看懂：我到底改了什么、为什么改、下次合并 upstream 时要注意什么。

## 2026-07-31 迁移结果

这次没有把旧本地 `master` 直接 merge / rebase 到新 upstream 上，而是先保留旧
`master` 为备份分支，再从最新 `origin/master` 重新按功能组移植本地改动。

最终状态：

- 集成分支：`codex/integrate-local-features-20260731`
- 旧本地 master 备份分支：`codex/backup-local-master-20260731`
- 本地 `master` 已直接移动到已验证的集成分支结果。
- 代码集成 HEAD：`6aba48975263fbd273e1f647c788ae3748361cef`
- 相对 `origin/master` ahead：30 个提交

已迁移的功能：

- Quest 导入保留库存里的 Snow-Covered land。
- 选牌名窗口支持英文名 / 本地化名搜索。
- zh-CN key 对齐和 `cardnames-zh-CN.txt` 数据。
- match 背景每场随机一次。
- chosen color mana icon 卡面提示。
- 鼠标滚轮 / 鼠标按钮大图翻面权限修复。
- Yuna Grand Summon 提示框计数。
- Card Detail 快捷键，以及 Ctrl+T 隐藏所有面板标题头。
- `.gitignore` 保留 `forge-gui-desktop/local/` 本地忽略。
- Historic Brawl / Duel Commander 本地 override。
- 随机颜色套牌赛制补齐，以及非颜色随机套牌列表顶部 `Random`。
- Quick Gauntlet `eventNames` 对齐。
- Sealed ranking score。
- Black Lotus 人类付费和可行动作检测修复。
- Freed 宏 canonical ability key、legacy fallback、replay hardening。
- 外部可施放牌显示在手牌区附近，但不进入真实 hand。
- Draft Log layout 污染修复。
- Arena-style Draft / Sealed Gauntlet run。
- Quest Mode 时空旅行 set code 搜索。
- Puzzle cleanup timing 修复。
- Mobile settings auto-save 修复。
- 桌面 Quest 选择系列搜索。
- 认输时复制 last game state 的玩家列表 snapshot，避免 CME。

以上游为准、没有迁移的旧逻辑：

- 普通 `ListChooser` 搜索以上游现有行为为准；本地只保留仍需要的 extra search key 支持。
- `in-game generated card text 中文翻译` 旧逻辑没有迁移，因为净效果和当前 upstream 文本生成 / 本地化路径冲突风险高。
- 旧的 zh-CN key 修复提交没有按 commit 原样重放，而是按当前 upstream 文件逐 key 对齐。
- 本地 `tools/` 脚本和 tools 文档没有进入集成分支。
- `InputPassPriority.java` 的 stash / 工作区噪音没有迁移。

确认未提交的本地文件：

- `.cache/`
- `.codex/`
- `tools/`
- `forge-gui-mobile-dev/local/`
- 本地 adventure `items-zh-CN.json`
- 本地 AI 文件，例如 `forge-gui/res/ai/Challenger.ai`

`stash@{0}`（`codex-pre-integration-tracked-worktree-20260731`）里有效的
`RecordActionsMacroSystem` legacy fallback 逻辑已经迁移到 macro legacy fallback
提交中。stash 在 master 对齐和文档记录完成前先保留。

## 当前本地改动范围

这里记录的是当前本地分支相对 `origin/master` 的本地提交差异，不只是未提交的 working tree diff。

当前本地改动大概分为五组：

1. Historic Brawl / Brawl 本地 override
2. Duel Commander 本地 override
3. 皮肤 / 背景 / UI 修改
4. zh-CN 本地化补全
5. `.gitignore` 本地忽略规则
6. Match UI 易用性本地修改

当前相对 `origin/master` 有差异的文件：

- `.gitignore`
- `forge-core/src/main/java/forge/StaticData.java`
- `forge-core/src/main/java/forge/deck/DeckRecognizer.java`
- `forge-core/src/main/java/forge/deck/DeckFormat.java`
- `forge-game/src/main/java/forge/game/GameFormat.java`
- `forge-game/src/main/java/forge/game/player/Player.java`
- `forge-game/src/main/java/forge/game/player/RegisteredPlayer.java`
- `forge-gui-desktop/src/main/java/forge/itemmanager/DeckManager.java`
- `forge-gui-desktop/src/main/java/forge/screens/deckeditor/controllers/CEditorCommander.java`
- `forge-gui-desktop/src/main/java/forge/screens/deckeditor/controllers/CEditorConstructed.java`
- `forge-gui-desktop/src/main/java/forge/screens/match/CMatchUI.java`
- `forge-gui-desktop/src/main/java/forge/toolbox/FSkin.java`
- `forge-gui-mobile/src/forge/assets/FSkinTexture.java`
- `forge-gui-mobile/src/forge/screens/match/MatchController.java`
- `forge-gui/res/formats/Casual/Duel Commander.txt`
- `forge-gui/res/formats/Casual/Historic Brawl.txt`
- `forge-gui/res/languages/zh-CN.properties`
- `forge-gui/src/main/java/forge/deck/CommanderDeckGenerator.java`
- `forge-gui/src/main/java/forge/deck/DeckImportController.java`
- `forge-gui/src/main/java/forge/deck/DeckgenUtil.java`
- `forge-gui/src/main/java/forge/localinstance/properties/ForgeProfileProperties.java`
- `forge-gui/src/main/java/forge/model/FModel.java`

另外，还有一组已提交或未提交的 match UI 易用性本地 patch，也需要在合并 upstream 时保留或手动处理：

- `forge-gui-desktop/src/main/java/forge/control/KeyboardShortcuts.java`
- `forge-gui-desktop/src/main/java/forge/gui/GuiChoose.java`
- `forge-gui-desktop/src/main/java/forge/gui/ListChooser.java`
- `forge-gui-desktop/src/main/java/forge/gui/framework/DragCell.java`
- `forge-gui-desktop/src/main/java/forge/screens/match/controllers/CHand.java`
- `forge-gui-desktop/src/main/java/forge/screens/match/views/VHand.java`
- `forge-gui-desktop/src/main/java/forge/view/arcane/CardPanel.java`
- `forge-gui-desktop/src/main/java/forge/view/arcane/CardPanelContainer.java`
- `forge-gui-desktop/src/main/java/forge/view/arcane/HandArea.java`
- `forge-gui-desktop/src/main/java/forge/screens/match/LimitedWinLose.java`
- `forge-gui/res/languages/en-US.properties`
- `forge-gui/src/main/java/forge/gamemodes/gauntlet/GauntletUtil.java`
- `forge-gui/src/main/java/forge/gamemodes/limited/GauntletMini.java`
- `forge-gui/src/main/java/forge/gamemodes/limited/LimitedWinLoseController.java`
- `forge-gui/src/main/java/forge/gui/interfaces/IGuiGame.java`
- `forge-gui/src/main/java/forge/localinstance/properties/ForgePreferences.java`
- `forge-gui/src/main/java/forge/player/PlayerControllerHuman.java`

另外，还有一组 deck editor / 轮抓界面 layout 污染修复相关文件：

- `forge-gui-desktop/src/main/java/forge/gui/framework/SLayoutIO.java`
- `forge-gui-desktop/src/main/java/forge/screens/deckeditor/CDeckEditorUI.java`
- `forge-gui-desktop/src/main/java/forge/screens/deckeditor/controllers/CEditorDraftingProcess.java`
- `forge-gui-desktop/src/main/java/forge/screens/deckeditor/views/VEditorLog.java`

## 1. Historic Brawl / Brawl 本地修改

这里主要是让 Forge 的 Brawl 可以通过本地开关切到 Historic Brawl。

不开开关时还是普通 Brawl。开了 `useHistoricBrawlOverride=true` 后，Brawl 会使用 `Historic Brawl.txt` 的牌池、banlist、牌表大小等逻辑。

最重要的点：

- `useHistoricBrawlOverride` 是开关，默认关。
- `getEffectiveBrawlFormat()` 是核心。它负责判断现在应该用普通 `Brawl`，还是本地 `Historic Brawl`。
- 以后合并冲突时，不要把 `getEffectiveBrawlFormat()` 退回 `getFormats().get("Brawl")`。
- `brawlCommander` 那里要同时保留 upstream 的 `getAllCards(...)` 和本地的 `getEffectiveBrawlFormat().getFilterPrinted()`。
- `Historic Brawl.txt` 是本地新增的格式文件，里面的 `Banned:` 和 `Additional:` 要独立维护。

相关文件：

### `forge-gui/res/formats/Casual/Historic Brawl.txt`

本地新增的 Historic Brawl 格式文件。里面有 Historic Brawl 的 `Sets:`、`Banned:` 和 `Additional:`。

2026-06-17 更新：

- 补充 Wizards banned/restricted list 来源和检查日期注释。
- 以本地 `forge-gui/res/formats/Sanctioned/Historic.txt` 的 `Sets:` 为基准同步 Historic Brawl。
- 补入缺少的 set code：`XANA`、`OANA`、`YECL`、`YSOS`。

合并 upstream 时注意：不要把它和 `Brawl.txt` / `Historic.txt` 乱合并。`Banned:` 要独立维护，`Additional:` 是按牌名放行，不能把 banned 牌又加回来。

### `forge-game/src/main/java/forge/game/GameFormat.java`

把 `Historic Brawl.txt` 加进 core format 列表，让 Forge 能加载到这个格式。

合并 upstream 时注意：如果 upstream 改了 core format 加载列表，要保留 `Historic Brawl.txt` 这一项。

### `forge-gui/src/main/java/forge/localinstance/properties/ForgeProfileProperties.java`

增加 `useHistoricBrawlOverride` 配置项。默认 false，只在 true 时写入 profile。

合并 upstream 时注意：开关名不要改，默认值要保持 false。

### `forge-core/src/main/java/forge/StaticData.java`

保存 `useHistoricBrawlOverride` 的运行时状态，让 deck format 等底层代码也能知道当前是不是 Historic Brawl override。

合并 upstream 时注意：这个 flag 要保留，不要和普通 Brawl predicate 混在一起。

### `forge-gui/src/main/java/forge/model/FModel.java`

这里是 Brawl override 的核心：

- 增加 `isHistoricBrawlOverride()`。
- 增加 `getEffectiveBrawlFormat()`。
- 初始化 Brawl predicate 时使用 effective format。
- `brawlCommander` 使用 effective Brawl format 的 printed filter。

合并 upstream 时最容易出错的是 `brawlCommander`。正确方向是：保留 upstream 的 `getAllCards(...)` 查询方式，同时保留本地的 `getEffectiveBrawlFormat().getFilterPrinted()`。

### `forge-core/src/main/java/forge/deck/DeckFormat.java`

让 `DeckFormat.Brawl` 在 Historic Brawl override 开启时使用 99 张主牌，普通 Brawl 仍然是 59 张主牌。这里也加入了 printed-card legality 检查。

合并 upstream 时注意：普通 Brawl 行为不能被破坏；override 关闭时必须还是普通 Brawl。

### `forge-gui-desktop/src/main/java/forge/itemmanager/DeckManager.java`

Brawl 的 set catalog 过滤改成使用 `FModel.getEffectiveBrawlFormat()`。

合并 upstream 时注意：不要退回硬编码 `FModel.getFormats().get("Brawl")`。

### `forge-gui-desktop/src/main/java/forge/screens/deckeditor/controllers/CEditorCommander.java`

Brawl deck editor 里的 commander pool 和 normal pool 使用 effective Brawl format。

合并 upstream 时注意：这样开了 Historic Brawl override 后，编辑器看到的牌池才会跟着切。

### `forge-gui-desktop/src/main/java/forge/screens/deckeditor/controllers/CEditorConstructed.java`

构组编辑器里的 Brawl normal pool 使用 effective Brawl format。

合并 upstream 时注意：不要退回普通 `Brawl` 硬编码。

### `forge-gui/src/main/java/forge/deck/CommanderDeckGenerator.java`

这里的本地修改是 Brawl / Historic Brawl 生成相关，不是修普通 Commander。

card-based Brawl 生成时，LDA 数据改成按 `FModel.getEffectiveBrawlFormat().getName()` 找。如果没有对应 LDA pool，就 fallback 到全部 unique cards。

合并 upstream 时注意：这个改动是为了 Brawl override，不代表已经修了 `getCommanderDecks(...)` 的非 Brawl commander 候选过滤问题。

### `forge-gui/src/main/java/forge/deck/DeckgenUtil.java`

Brawl card-based 生成时使用 effective Brawl format。没有 LDA 数据时会 fallback 到 effective Brawl 的牌池；Brawl 路径也避免走 partner 选择逻辑。

合并 upstream 时注意：Brawl 生成和最终 printed filter 都要继续跟随 effective Brawl format。

### `forge-gui/src/main/java/forge/deck/DeckImportController.java`

导入牌组时，Brawl 现在使用 `FModel.getEffectiveBrawlFormat()`，Commander 现在使用 `FModel.getEffectiveCommanderFormat()`。

原因是 `useHistoricBrawlOverride=true` 或 `useDuelCommanderOverride=true` 时，导入识别器也必须按当前 effective format 判断 set / banlist / restricted，而不是硬拿普通 `Brawl` 或普通 `Commander`。

2026-06-17 暂时方案：

- 记录的 bug：Historic Brawl 的合法性目前依赖本地印刷版本的 set code。如果某张牌按牌名应当属于 Historic Brawl，但 Forge 本地只有一个不在 `Historic Brawl.txt` / `Historic.txt` 的印刷，例如 `Dina, Essence Brewer` 只有 `SOC`，导入时会被误判为“来自不允许系列”。
- 临时修法：Historic Brawl 导入时仍先按 Historic Brawl 正常校验；如果失败原因是 set 不允许，再用普通 `Commander` 的合法性谓词兜底接受，并在导入结果里给 warning。
- 这个兜底只用于导入牌组，不影响随机 / 基本 / card-based Historic Brawl 套牌生成。
- 这个兜底不会绕过 Historic Brawl 自己的禁牌表，也不会使用 Duel Commander override。
- 长期更合理的修法：Historic Brawl 应该有独立的“牌名合法性 / Arena availability”数据，而不是完全依赖本地印刷 set 覆盖。

合并 upstream 时注意：只要这两个本地 override 还存在，导入逻辑就不要退回 `FModel.getFormats().get(gameType.name())`。

### `forge-core/src/main/java/forge/deck/DeckRecognizer.java`

为上面的 Historic Brawl 导入暂时方案增加了一个很窄的 fallback 入口：只有原本会变成 `NotAllowedCard` 的牌，才允许调用 fallback predicate。fallback 成功后仍按原来的 section 识别逻辑放到 Main / Commander 等区域。

合并 upstream 时注意：这个 fallback 是临时修补导入体验，不是通用格式合法性模型。不要把它扩大到所有格式，也不要让它绕过 banlist。

## 2. Duel Commander 本地 override

这里不是正式新增一个 Duel Commander 模式。

实际做法是：普通 Commander 仍然显示为 Commander，DeckFormat 仍然是 Commander，牌组存储也还是 Commander；但是打开 `useDuelCommanderOverride=true` 后，底层规则切到 Duel Commander。

这样做的目的很明确：本地长期维护时，尽量少改 UI、少改 deck storage、少和 upstream 冲突。

开启 `useDuelCommanderOverride=true` 后：

- Commander 的合法性 / banlist 使用 `Duel Commander.txt`。
- 起始生命变成 20。
- commander damage 关闭。
- command zone、commander tax、singleton、color identity、partner 继续复用普通 Commander。
- `useDuelCommanderOverride=false` 时，普通 Commander 应该仍然是 40 血、有 commander damage。

故意不做的事情：

- 不新增 `GameType.DuelCommander`。
- 不新增 `DeckFormat.DuelCommander`。
- 不改 UI 显示。
- 不改 deck storage。
- 不改 editor pools。
- 不改 `commanderPool`。
- 不实现小局之间换 commander。
- 不实现 companion-only ban。
- 不实现 commander-only ban。
- 不修 `CommanderDeckGenerator.getCommanderDecks(...)` 的历史候选过滤问题。

相关文件：

### `forge-gui/res/formats/Casual/Duel Commander.txt`

本地新增 Duel Commander 格式文件。

非常重要：`Banned:` 只应该放官方 “Banned in Deck” 分类里的牌。

`Banned as Companion` 和 `Banned as Commander only` 不能放进 `Banned:`，因为 Forge 当前不能表达这种“只禁某个用途”的规则。如果放进去，会错误地把这些牌从整个牌组里禁掉。

这个文件顶部的注释记录了官方来源、检查日期，以及当前没实现的规则限制。

2026-06-17 更新：重新核对官方 Duel Commander banlist，`Banned:` 仍保持当前 80 张 fully banned cards，只更新 `Latest checked` 日期。

### `forge-game/src/main/java/forge/game/GameFormat.java`

把 `Duel Commander.txt` 加进 core format 列表，让 `FModel.getFormats().get("Duel Commander")` 能找到它。

合并 upstream 时注意：如果还要保留 Duel Commander override，就不能删这个注册。

### `forge-gui/src/main/java/forge/localinstance/properties/ForgeProfileProperties.java`

增加 `useDuelCommanderOverride` 配置项。默认 false，只在 true 时写入 profile。

合并 upstream 时注意：开关名不要改，默认值要保持 false。

### `forge-core/src/main/java/forge/StaticData.java`

保存 `useDuelCommanderOverride` 的运行时状态，给 `RegisteredPlayer` / `Player` 这类游戏逻辑使用。

合并 upstream 时注意：这里没有新增单独的 duel commander predicate，这是低侵入设计的一部分。

### `forge-gui/src/main/java/forge/model/FModel.java`

这里是 Duel Commander override 的核心：

- 增加 `isDuelCommanderOverride()`。
- 增加 `getEffectiveCommanderFormat()`。
- Commander predicate 初始化时使用 effective Commander format。

合并 upstream 时注意：override 关掉时必须回到普通 `Commander`，不能让普通 Commander 永久变成 Duel Commander。

### `forge-game/src/main/java/forge/game/player/RegisteredPlayer.java`

普通 Commander 原本会在默认生命上加 20，变成 40。Duel Commander override 开启时跳过这个 +20，所以保持 20 血。

合并 upstream 时注意：普通 Commander 的 40 血不能被破坏。

### `forge-game/src/main/java/forge/game/player/Player.java`

Duel Commander override 开启时关闭 commander damage。

合并 upstream 时注意：普通 Commander 的 commander damage 仍然要保留。

## 3. 皮肤 / 背景 / UI 修改

这里是本地皮肤功能。

目的是让 match 背景可以从当前皮肤的 `bg_match/` 文件夹里随机选。桌面和移动端都尽量做到“一场 match 开始时随机一次”，而不是每次重绘、切屏、日夜切换都换。

规则：

- `bg_day.jpg` / `bg_night.jpg`：优先当前 skin 根目录，找不到再用默认。
- `bg_match/`：当前 skin 下的随机 match 背景目录。
- 只接受直接子文件里的 `.jpg` / `.jpeg` / `.png`。
- 不递归读子文件夹。
- 不读 `.xcf`、`.b`、`.bb`、无扩展名等文件。
- `bg_match.jpg` 仍然作为 fallback。
- 不要把随机逻辑放到 repaint / render / screen switch 里。

相关文件：

### `forge-gui-desktop/src/main/java/forge/toolbox/FSkin.java`

桌面端 skin 背景加载的核心。这里修了 `bg_day.jpg` / `bg_night.jpg` 当前 skin 优先的 fallback，也增加了从 `bg_match/` 随机挑选 match 背景的逻辑。

合并 upstream 时注意：随机只应该发生在 match 开始，不要挪到绘制逻辑里。

### `forge-gui-desktop/src/main/java/forge/screens/match/CMatchUI.java`

桌面端 match 开始时调用 `FSkin.selectMatchBackgroundForNewMatch()`。

它也参与了后面的 match UI 易用性修改：当 `ZoneType.Flashback` 这类 external playable cards 变化时，会刷新手牌区显示；并且 `findCardPanel(...)` 能找到显示在 `HandArea` 里的外部可施放牌，避免点击这些牌时只是为了定位菜单就强制打开黑色 floating zone tab。

现在它还会在 external playable 来源区域变化时刷新手牌区，包括 `Graveyard` / `Exile` / `Command` / `Library` / `Sideboard`。这样像 Phlage 进坟场后获得 Escape 可施放状态，不需要等下一次真实手牌变化才出现在手牌区末尾。

合并 upstream 时注意：这里用 `getOutcomes().isEmpty()` 来避免 best-of match 中每个小局都重新随机。

### `forge-gui-mobile/src/forge/assets/FSkinTexture.java`

移动端 match 背景随机逻辑。这里缓存 `currentMatchBackgroundFile`，并且只重置 `BG_MATCH` 纹理缓存。

合并 upstream 时注意：移动端也要保持“一场 match 一次”，不要每次 render 都随机。

### `forge-gui-mobile/src/forge/screens/match/MatchController.java`

移动端创建 match screen 时选择本场 match 背景。

合并 upstream 时注意：随机选择要发生在 screen 创建前后这个级别，不要进入 draw/render 路径。

### 相关关注文件，但当前 diff 没改

`forge-gui-desktop/src/main/java/forge/control/FControl.java` 是相关关注文件，因为它会使用当前缓存的 `BG_MATCH` 切换界面。但当前 `origin/master..HEAD` diff 里没有修改它，不要把它误当作本地 modified file。

### Draft Log / 轮抓日志布局污染修复

相关文件：

- `forge-gui-desktop/src/main/java/forge/gui/framework/SLayoutIO.java`
- `forge-gui-desktop/src/main/java/forge/screens/deckeditor/CDeckEditorUI.java`
- `forge-gui-desktop/src/main/java/forge/screens/deckeditor/controllers/CEditorDraftingProcess.java`
- `forge-gui-desktop/src/main/java/forge/screens/deckeditor/views/VEditorLog.java`
- `forge-gui/res/languages/en-US.properties`
- `forge-gui/res/languages/zh-CN.properties`

问题：

Conspiracy / 特殊轮抓牌可能会用到 Draft Log / 轮抓日志。这个日志 tab 本来只是轮抓抓牌过程里的临时 UI，但轮抓界面和普通套牌编辑器共用 editor layout 机制。

如果在轮抓界面点开“轮抓日志”，然后切到套牌编辑器、主页、再回套牌编辑器，临时的 `EDITOR_LOG` 有机会被写进用户本地 editor layout。同时，轮抓界面临时移除普通 editor tabs 的状态也可能一起被保存。表现就是日志 tab 被带到普通套牌编辑器里，或者套牌编辑器原本“卡组目录”后面的正常 tabs 消失。

这个问题重启 Forge 不一定会恢复，因为污染已经写进本地 layout 文件；重置布局能恢复也说明问题在持久化 layout。

本地修复：

- `CEditorDraftingProcess` 只有在 `boosterDraft.shouldShowDraftLog()` 为 true 时才添加 `VEditorLog`。
- 轮抓界面移除 tabs 时使用一次性移除逻辑，避免重复 `update()` 把记住的 parent cell 覆盖成 null。
- 离开 `FScreen.DRAFTING_PROCESS` 时移除 `VEditorLog`，并恢复临时移除的 tabs。
- `CDeckEditorUI` 在从 `CEditorDraftingProcess` 切到其他 editor controller 时，会要求旧 controller 清理临时 UI 状态。
- `SLayoutIO.save(...)` 在当前 screen 是 `FScreen.DRAFTING_PROCESS` 时不保存 layout，避免把轮抓临时 UI 写进持久 editor layout。
- `SLayoutIO.loadLayout(...)` 如果发现用户 layout 里含有 `EDocID.EDITOR_LOG`，就认为这是被污染的 editor layout，删除该用户 layout 并回落默认 layout。
- `VEditorLog` 不再强制 opaque tab/content 背景，修掉日志 tab 标题泛白问题。
- 文案是英文 `Draft Log`，中文 `轮抓日志`。

合并 upstream 时注意：

- 这是 deck editor / 轮抓 UI 的 layout 污染修复，不是 Match UI 修改。
- 核心原则是：轮抓过程里的临时 tab 不应该写进普通 editor 的持久 layout。
- 如果 upstream 改了 `SLayoutIO` 或 deck editor tab 生命周期，要保留“Drafting Process 不保存 layout”和“污染 layout 自动回落默认布局”的行为。
- `EDocID.EDITOR_LOG` 是污染检测标记；正常非 editor layout 理论上不应该含有这个 doc。

验证：

- 普通轮抓没有特殊日志需求时，不应显示 Draft Log。
- Conspiracy / 特殊轮抓需要日志时，能显示 Draft Log。
- 轮抓界面点开 Draft Log 后，切到套牌编辑器、主页、再回套牌编辑器，Draft Log 不应被带过去。
- 普通套牌编辑器“卡组目录”后的正常 tabs 不应消失。
- 已经污染的本地 editor layout 如果含 `EDITOR_LOG`，应自动回落默认布局。

## 4. Match UI 易用性修改

这一组是桌面端对局界面的本地体验修改，不是规则修改，也不一定适合直接做 upstream PR。

### Quick Gauntlet 筛选允许套牌类型后仍显示决斗进度

相关文件：

- `forge-gui/src/main/java/forge/gamemodes/gauntlet/GauntletUtil.java`

问题：

Quick Gauntlet 的对局内进度显示依赖 `GauntletData.eventNames`。

部分生成型对手套牌类型会生成 deck，但没有同步写入 event name。比如只勾选某些 Standard / Pioneer / Modern cardgen 或 color deck generator 时，`decks` 有内容，但 `eventNames` 可能为空或数量不足。这样对局内“决斗进度”面板就不会正常显示。

全选时看起来正常，是因为 precon / theme / quest / custom / net deck 等类型本来就会写 `eventNames`，随机到这些类型后进度就能显示。

本地修复：

- `GauntletUtil.createQuickGauntlet(...)` 每接受一个生成出来的对手 deck 后，检查是否缺 event name。
- 如果缺，就补一个名字。
- 优先使用 `deck.getName()`。
- 极端情况下 deck 为空时，fallback 到当前 `DeckType` 文本。
- 目标是保证 `eventNames` 和 `decks` 数量对齐，不改进度 UI。

范围：

- 只修 Quick Gauntlet 里 allowed deck types 被筛选后进度不显示的问题。
- 不处理 Gauntlet Contests / 天命战场列表 Progress 0% 的问题。

验证：

- 只勾选生成型 allowed deck types 开 Quick Gauntlet，对局内决斗进度应显示条目。
- allowed deck types 全选时进度仍正常。
- 天命战场 / Gauntlet Contests 的进度行为不属于这次修改。

合并 upstream 时注意：

- 如果 upstream 改了 `GauntletUtil` 的生成逻辑，要保留“每个 accepted opponent deck 都有对应 event name”的不变量。
- 不要让 `eventNames.size()` 再次小于 `decks.size()`。

### Draft / Sealed Gauntlet 类 Arena 赛程显示

相关文件：

- `forge-gui/src/main/java/forge/gamemodes/limited/GauntletMini.java`
- `forge-gui/src/main/java/forge/gamemodes/limited/LimitedWinLoseController.java`
- `forge-gui-desktop/src/main/java/forge/screens/match/LimitedWinLose.java`
- `forge-gui/res/languages/en-US.properties`
- `forge-gui/res/languages/zh-CN.properties`

问题：

Draft Gauntlet / Sealed Gauntlet 原来的结束界面更像传统 round 显示，只显示 `Round: 1/n` 或“你赢得了此轮 n/m”这种信息。

这和我想要的 Arena Limited event 体验不一致：输掉一场不应该是“重试当前轮”，而是记录 1 负，然后继续下一场，直到达到胜场 / 负场上限。原来的按钮文案也容易把“继续当前 run”误解成“开始新的一轮”。

本地修复：

- Mini Limited Gauntlet 现在单独记录 match 级别赛程，不再只依赖旧的 game 级别胜负。
- Draft run：7 胜、3 负、或最大场数用完时结束。
- Sealed run：7 胜、2 负、或最大场数用完时结束。
- 输掉一场 match 时，如果 run 还没结束，会记录 1 负并进入下一场，而不是重打当前轮。
- AI 对手尽量从未打过的对手里随机选择。
- 对手名字显示为实际生成的 AI 玩家名，不再显示 `forge.gamemodes.limited...` 这种 Java 对象 / 类名。
- 右侧结果区域显示当前 run 战绩、胜负上限、当前场次、对局记录和当前对局状态。
- 历史记录使用结构化 `GauntletMini.MatchRecord` 保存数据；本地化显示仍放在 `LimitedWinLoseController`，不把中文/英文 UI 字符串存进记录。
- 当前 match 比分从 match 的 game outcomes 计算，显示 `1-0`、`0-1`、`2-1` 这类比分。
- 历史记录格式类似：`第 1 场：玩家 (玩家) vs AI (AI)：2-1`。
- 还在进行的 match 单独显示在“当前对局”下面。
- 最后一场结束导致 run 结束时，会先把最后一场归档进对局记录，然后不再额外显示重复的“当前对局”。
- 如果最后一场是 BO3 且 match 还没结束，即使已经是最大场次，也仍会显示当前比分 / 进行中状态。
- live run 中间隐藏 Restart；只有 run 结束后，Restart 才作为真正的“开始新的赛程”出现。

范围：

- 只影响 Draft Gauntlet / Sealed Gauntlet 的赛程记录和胜负界面。
- 不改卡牌规则、不改比赛规则、不改牌组构筑规则。
- 不影响 Quick Gauntlet / Gauntlet Contests 的进度显示逻辑。

验证：

- Draft Gauntlet 输 match 后仍能继续，直到 3 负；7 胜 / 3 负 / 最大场数用完后结束。
- Sealed Gauntlet 输 match 后仍能继续，直到 2 负；7 胜 / 2 负 / 最大场数用完后结束。
- live run 中间不显示误导性的 Restart / 开始新一轮按钮。
- 每场 match 结束后，对局记录保留之前所有对手和比分。
- 最后一场导致 run 结束时，最后一场会进入对局记录。
- run 结束后，最后一场不会同时显示在“对局记录”和“当前对局”里。
- BO3 最后一场还没打完时，仍会显示当前对局比分和“进行中”。
- AI 对手名字是可读名字，不是 Java 类名。
- 比分显示正确，不应出现 `1--1`，也不应把胜负反着记。

合并 upstream 时注意：

- `matchWins` / `matchLosses` 是 match 级别记录，不要和旧 game 级别 wins/losses 混掉。
- `matchHistory` 只记录已经完成的 match；当前 match 单独显示。
- `GauntletMini.MatchRecord` 只存数据，不存已经本地化的 UI 文案。
- 最后一场归档逻辑要保留：run 结束时最后一场必须进入 history，但 match 结束后不能再重复显示为 current match。
- 如果 upstream 改了 limited win/lose 界面，要保留右侧结果区域可以容纳 match history 的布局。

### 选择牌名搜索支持英文名和本地化名称

相关文件：

- `forge-gui-desktop/src/main/java/forge/gui/ListChooser.java`
- `forge-gui-desktop/src/main/java/forge/gui/GuiChoose.java`
- `forge-gui/src/main/java/forge/gui/interfaces/IGuiGame.java`
- `forge-gui/src/main/java/forge/player/PlayerControllerHuman.java`

问题：

中文 UI 下，断针、扰咒法师、巫视望远镜、碎颅留念这类 “choose a card name / 选择一个牌名” 窗口搜索不好用。

原来的 `ListChooser` 只按当前显示文本过滤。中文环境里显示文本通常是翻译名，所以输入英文 canonical name 搜不到。同时旧的 normalize 只保留 `[a-z0-9 ]`，中文输入会被删空，所以输入中文也搜不到。

本地修复：

- `ListChooser` 支持额外 search keys。
- choose card name 路径传入英文 canonical name 和当前语言 translated name。
- normalize 保留 Unicode 字母和数字，中文输入不会被清空。
- UI 仍然可以显示翻译名。
- 选择结果仍然返回英文 canonical card name，避免规则层把中文名保存成 named card。

验证：

- 中文 UI 下搜索 `Pithing Needle` / `Lightning Bolt` 可以命中。
- 中文 UI 下搜索“闪电击”等中文翻译名，在翻译存在时可以命中。
- 英文 UI 搜索不回归。
- 断针等 named-card 效果仍使用 canonical English name。

合并 upstream 时注意：

- `ListChooser` 的额外 search key 是可选能力；没传 search key 的普通 chooser 行为应该不变。
- 不要把中文名写进规则层。
- 如果 upstream 改了 chooser 过滤逻辑，要保留 Unicode normalize 和 canonical name 搜索。

### Card Detail / 卡牌详情快捷键

相关文件：

- `forge-gui-desktop/src/main/java/forge/control/KeyboardShortcuts.java`
- `forge-gui/src/main/java/forge/localinstance/properties/ForgePreferences.java`
- `forge-gui/res/languages/en-US.properties`
- `forge-gui/res/languages/zh-CN.properties`

改了什么：

- 新增快捷键偏好，例如 `SHORTCUT_SHOWCARDDETAIL`。
- 新增键盘动作，例如 `actShowCardDetail`。
- 动作会调用 `SDisplayUtil.showTab(EDocID.CARD_DETAIL.getDoc())`，把卡牌详情窗格切到前台。
- 增加英文和中文的快捷键说明 key。

为什么改：

- 后面 Ctrl+T 会把多 tab cell 的标题头也隐藏掉。
- 标题头隐藏后，Card Detail 不方便用鼠标点 tab 切回来，所以给它一个直接快捷键。

合并 upstream 时注意：

- 如果保留“隐藏所有标题头”的本地 UI 逻辑，就要保留这个 Card Detail 快捷键。
- 不要把快捷键 pref、action、语言 key 拆散。

### Ctrl+T / Panel Tabs 隐藏所有 DragCell 标题头

相关文件：

- `forge-gui-desktop/src/main/java/forge/gui/framework/DragCell.java`

原逻辑：

即使 `showTabs=false`，只要某个 cell 里有多个 doc / 多个 tab，也会强制显示标题头：

```java
showTabs || allDocs.size() > 1
```

本地改动：

`DragCell.doCellLayout(boolean showTabs)` 里的 header 高度只看 `showTabs`：

```java
showTabs ? SLayoutConstants.HEAD_H : 0
```

效果：

- Ctrl+T / Panel Tabs 关闭时，所有 `DragCell` 标题头都会隐藏。
- 包括 Stack / Combat / Log / Card Detail / Dependencies 等多 tab cell。
- 只隐藏标题头，不隐藏窗格内容。
- 不 remove doc，不 add doc。
- 不改 layout 保存逻辑。

风险：

- 多 tab cell 的标题头隐藏后，不能直接用鼠标点击同一个 cell 里的其他 tab。
- Stack / Combat / Log 已经有快捷键或自动切换。
- Card Detail 已经新增快捷键。
- Dependencies / Dock / Prompt 这类如果没有快捷键，可能要重新按 Ctrl+T 打开标题头后再切。
- 这是本地沉浸式 UI 修改，不一定适合 upstream。

### 外部可施放牌显示在手牌区

相关文件：

- `forge-gui-desktop/src/main/java/forge/screens/match/CMatchUI.java`
- `forge-gui-desktop/src/main/java/forge/screens/match/controllers/CHand.java`
- `forge-gui-desktop/src/main/java/forge/screens/match/views/VHand.java`
- `forge-gui-desktop/src/main/java/forge/view/arcane/CardPanel.java`
- `forge-gui-desktop/src/main/java/forge/view/arcane/HandArea.java`

改了什么：

把“不是手牌、但当前可以施放 / 操作”的牌，视觉上显示到 `HandArea` 里，排在真实手牌后面。

数据源：

- `player.getCards(ZoneType.Flashback)`
- 再过滤掉真实手牌：`card.getZone() != ZoneType.Hand`

注意：Forge 里的 `ZoneType.Flashback` 在这里不是单纯的“返照区”。它更像一个 external playable cards 视图集合，可能包含：

- Graveyard
- Exile
- Command
- Library
- Sideboard
- 其他可从非手牌区域操作的牌

规则边界：

- 不把这些牌加入 `player.getHand()`。
- 不计入 hand count。
- 不影响 discard / random discard / maximum hand size。
- 不进入真实 `CHand.ordering`。
- 不改变真实 zone。
- 点击仍然走现有 `selectCard(...)` 流程，按真实 zone 施放 / 操作。
- commander tax、flashback、exile casting 等规则仍然走原本规则层。

UI 行为：

- 外部牌 append 到真实手牌末尾。
- `CardPanel` 新增了 UI-only 的 external playable badge。
- badge 画在牌面内部，例如 `GY` / `EX` / `CMD` / `LIB` / `SB`。
- 不再用 `setZoneBanner(...)` 那种卡牌上方横条，所以不会额外占高度，也不会改变 `CardArea` 布局。
- `HandArea` 维护 `externalPlayableIds` / `isExternalPlayable(...)`。
- `CHand` 维护 `externalPlayableOrdering`，只保存 external playable card id。
- 外部牌可以在 external group 内部拖动排序。
- 拖 external card 不会调用 `reorderHand(...)`。
- 拖真实手牌仍然正常调用 `reorderHand(...)`。
- 真实手牌排序 index 计算会跳过 external playable panels。
- external group 整体仍然排在真实手牌后面，没有做完全混排。

刷新行为：

- `PlayerView.updateZone(...)` 在 `Graveyard` / `Exile` / `Library` / `Command` 等来源区域变化时，会重算 `Flashback` / external playable 集合。
- 桌面端 `CMatchUI.updateZones(...)` 现在会在本地玩家的 `Graveyard` / `Exile` / `Command` / `Library` / `Sideboard` 更新时刷新 `HandArea` external playable 显示。
- 这修复了 Phlage, Titan of Fire's Fury 这类牌：从手牌施放后进入坟场，因为有 Escape，应该立刻出现在 external playable 区，而不是等下一次真实手牌变化。
- Phlage 从坟场 Escape 施放后，也应从 external playable 区消失。
- Flashback / Adventure / Foretell / Prepared / Command 区可施放牌变化后也走同一类刷新路径。

合并 upstream 时注意：

- 这个功能是显示层快捷入口，不是规则层改动。
- 不要把 external cards 加进真实 hand。
- 不要让 external cards 进入真实 `ordering` 或调用 `reorderHand(...)`。
- `CMatchUI.findCardPanel(...)` 要能找到 `HandArea` 里的 external card panel，否则点击后可能又退回强制打开 floating zone tab。
- `VHand` 之前的右侧常驻 `PlayableArea` 已经撤掉，不要恢复成第二个手牌栏。

已知限制 / TODO：

- 目前不是完全视觉混排，external group 仍整体在真实手牌后面。
- 外部牌和手牌同尺寸，还没有“小一号”显示。
- 外部牌很多时，手牌区仍然会变拥挤。
- `CardPanel` badge 的位置和样式以后可以继续微调。
- 如果未来要做完全混排，需要引入 `visualHandOrder`，并小心处理 `UI_ORDER_HAND`、动画和真实手牌 reorder 边界。

### CardPanel 上显示 chosen color 法术力图标

相关文件：

- `forge-gui-desktop/src/main/java/forge/view/arcane/CardPanel.java`

问题：

Sunken Citadel、Painter's Servant 这类牌选择颜色后，规则层和 `CardView` 已经记录了 `chosenColors`，Card Detail 也能看到“选择的颜色”。但战场上的卡牌本体没有直观标记，需要 hover 或切到 Card Detail 才知道选了什么颜色。

本地修复：

- 桌面端 `CardPanel` 会读取 `CardView.getChosenColors()`。
- 用 `CardFaceSymbols.drawSymbol(...)` 画对应的 mana icon。
- 复用 Forge 现有 skin mana symbol 资源。
- 不手绘圆点。
- 不新增图标资源。
- 不硬编码 custom skin 或默认 skin 路径。
- 图标显示在牌面右侧中部，竖向排列，半透明。
- 只显示 chosen color。
- chosen type / chosen name 不显示在卡面上，仍然只在 Card Detail 里查看。

验证：

- Sunken Citadel 选蓝后，战场 `CardPanel` 显示蓝色 mana icon。
- Painter's Servant 选红后，显示红色 mana icon。
- Cavern of Souls / Pithing Needle 的 chosen type/name 不显示在卡面上，但 Card Detail 仍显示。
- counter、external playable badge、group count 不应明显冲突。
- Z / 鼠标滚轮大图不受影响。

合并 upstream 时注意：

- 这个 overlay 只应基于 `CardView.getChosenColors()`。
- 不要从规则层 `Card` 直接读数据。
- 不要新增资源或绕过 skin fallback。
- 这段绘制和 counter、group count、external playable badge 的绘制位置接近，合并冲突时要小心保留。

### 鼠标滚轮大图翻面修复

相关文件：

- `forge-gui-desktop/src/main/java/forge/view/arcane/CardPanelContainer.java`

问题：

桌面端对局中，按 Z 查看卡牌大图时，可以翻面 / 查看 alternate state。可是用鼠标滚轮向上直接在牌上打开大图时，不能翻面。

原因：

- Z 键路径使用 Card Detail / Picture 已经设置好的 `CardZoomer` 状态。
- 这条路径会通过 `getMatchUI().mayFlip(card)` 判断玩家是否允许查看 alternate state。
- 但是 `CardPanelContainer` 的滚轮 zoom 入口以前直接调用 `CardZoomer.setCard(card.getCurrentState(), false)`。
- 这个硬编码 `false` 会把翻面能力关掉，即使这张牌本来是可见的 transform / MDFC / flip card。

本地修复：

- 鼠标滚轮向上打开大图时，改成使用 `getMatchUI().mayFlip(card)`。
- 同类的 `mouseButtonZoom(...)` 入口也改成使用 `getMatchUI().mayFlip(card)`，让鼠标按钮大图和滚轮 / Z 键行为一致。

合并 upstream 时注意：

- 不要改成直接传 `true`。
- 不要改成 `card.hasAlternateState()`。
- 必须用 `getMatchUI().mayFlip(card)`，这样 face-down / hidden info 的不可见背面不会被暴露。
- Z 键大图行为不应该被改变。
- split / aftermath 这类靠 zoom 旋转显示的牌，合并后要回归测试。

## 5. 其他本地修改

### `.gitignore`

增加：

```text
forge-gui-desktop/local/
```

目的是不把本地桌面端运行文件提交进 git。

合并 upstream 时注意：如果 upstream 改了附近 ignore 规则，尽量保留这一行。

### `forge-gui/res/languages/zh-CN.properties`

补了一批 zh-CN 缺失 key。

有些能确定的翻成中文；不确定的先保留英文 fallback。这样至少不会继续出现缺 key / translation missing 的问题。

以后改翻译时要特别小心保留这些东西：

- `%s`
- `%d`
- `{0}`
- `{1}`
- `\n`
- 双 apostrophe，例如 `don''t`

不要为了“整理”而批量重写已有中文翻译。

## 已知 TODO / 没做的东西

### Duel Commander

- companion-only ban 没实现，只注释记录。
- commander-only ban 没实现，只注释记录。
- 小局之间换 commander 没实现。
- `CommanderDeckGenerator.getCommanderDecks(...)` 的历史问题没修。

### Historic Brawl / Brawl

- 合并冲突时要保留 effective format。
- `Historic Brawl.txt` 的 `Banned:` 和 `Additional:` 不要乱合并。
- `Additional:` 是按名字加牌，要小心不要把 banned 牌又加回来。

### 皮肤 / UI

- 随机背景是一场 match 一次。
- 不要变成每次渲染都随机。
- `bg_match/` 只读直接图片文件。
- Ctrl+T / Panel Tabs 会隐藏所有 `DragCell` 标题头，包括多 tab cell。
- Card Detail 有单独快捷键，方便标题头隐藏时切回来。
- 外部可施放牌显示在 `HandArea` 中，但仍不是手牌。
- 外部牌拖动只改变 external group 的视觉顺序，不调用 `reorderHand(...)`。
- external playable 来源区，例如坟场、放逐、指挥官区、牌库、备牌区变化时，会刷新手牌区末尾的 external playable 显示。
- Quick Gauntlet 生成对手时要保持 `eventNames` 和 `decks` 对齐，否则筛选 allowed deck types 后对局内决斗进度会空白。
- Draft / Sealed Gauntlet 使用类 Arena Limited 赛程显示：match 级别战绩、胜负上限、可读 AI 对手名、每场比分记录，以及 run 结束后不重复显示当前对局。
- Drafting Process 里的 Draft Log / 轮抓日志是临时 UI，不能写进普通套牌编辑器的持久 layout。
- 如果本地 editor layout 里已经含有 `EDocID.EDITOR_LOG`，会被视为污染 layout 并回落默认 layout。
- chosen color 会在桌面端 `CardPanel` 上显示 mana icon；chosen type/name 不画在卡面上，仍看 Card Detail。
- 鼠标滚轮 / 鼠标按钮打开卡牌大图时，要使用 `getMatchUI().mayFlip(card)` 判断是否允许翻面；不要写成 `true`、`false` 或 `card.hasAlternateState()`。
- 随机颜色套牌补齐 Pioneer / Historic / Legacy / Vintage / Premodern；这些不是单独生成器，而是复用现有随机颜色套牌生成器，并传入对应赛制的 `getFilterPrinted()` 过滤器。
- 非颜色随机套牌列表可以在顶部显示本地化 `Random` 条目。Commander / Brawl 同类指挥官生成器、原型矩阵生成器、主题套牌、随机套牌总类都使用同类型哨兵条目，并在 `getDeck()` 时从当前候选快照里随机选择。
- 这些 Random 哨兵会排除自身，空候选列表不会加入 Random，并共用 `RandomDeckProxyUtil` 做候选选择。移动端 lobby 显示套牌名时使用已经生成出的 deck，避免显示随机到 A、实际进入游戏用 B。
- 合并 upstream 时要保持 shared、desktop、mobile 的入口同步：`DeckType`、`RandomDeckGenerator`、Quick Gauntlet / Tournament 工具、desktop/mobile deck chooser、lobby AI deck 刷新、语言 key。
- 选择系列窗口在桌面端加了搜索框。相关文件：
  - `forge-gui-desktop/src/main/java/forge/screens/home/quest/DialogChooseSets.java`
  - `forge-gui-desktop/src/main/java/forge/toolbox/FCheckBoxTree.java`
- 选择系列搜索只过滤左侧可见树，不应该改变完整树里的选择状态。
- 搜索可以匹配系列名、code、code2、alias、Scryfall code、系列类型，以及普通节点 label/key 文本。
- 搜索时隐藏的已勾选系列必须保留；点 OK 时要读取完整树的已选项，而不是只读当前搜索结果。
- 格式限制按钮、No Format、随机选择系列、spinner 最大值都应该基于完整树状态，不应该只基于搜索结果。
- 点击随机选择系列时会先清空搜索框，避免左侧仍显示搜索结果但实际已在完整树中随机勾选了其他系列。
- `FCheckBoxTree` 的完整树 key API 要保留旧 `getNodeByKey(...)` 的 fallback：请求 key 可以匹配原 key，也可以匹配 `requestedKey.hashCode()`。系列类型父节点依赖这个兼容。
- Quest Mode 的时空旅行选择框现在会传入额外搜索关键字。搜索 `THS`、`USG`、`FIN` 这类 set code 时，可以找到自身 `QuestWorld` 格式包含该系列的世界。
- `Main world` 特意不使用 set code 回退搜索；因为它没有 world 自己的 format，所以搜索关键字只保留自身名称。
- 通用选择框 API 增加了可选的额外搜索关键字。桌面端复用已有 `ListChooser` 搜索 key 机制，移动端 `ListChooser` 也在文本过滤时检查这些额外 key。
- Quest Mode 套牌导入现在区分普通基本地和 Snow-Covered 基本地。只有普通 very-basic lands 继续绕过库存计数；玩家拥有的 Snow-Covered lands 会走正常库存导入路径。
- 对局提示框会显示 `Grand Summon (Yuna)` 计数，用来显示 Yuna, Grand Summoner 已创建、仍待使用的 command-zone 效果，形式类似现有风暴计数。
- 宏回放录制 activated ability 时使用非本地化的 canonical `SpellAbility` key，而不是用本地化显示文本作为唯一匹配 key。这样中文界面下 Freed from the Real 的 Tap / Untap 文本碰撞时，回放也能选择录制时的真实能力。
- 宏回放额外做了健壮性改进：strict macro action 失败时不会跳过继续消费后续 strict action；重复播放下一轮会等待第一步可执行；mana pool 支付必须真的支付或消耗 mana 才会消费动作；需要诊断时可用 `-Dforge.macro.debug=true` 打开详细日志。

### Match UI

## 下次拉 upstream / 开新 Codex 会话时怎么说

可以直接复制这段：

```text
先阅读 LOCAL_CHANGES.md 和 LOCAL_CHANGES.zh-CN.md。这个仓库有本地修改：Historic Brawl override、Duel Commander override、皮肤/背景修改、Match UI 易用性修改、随机颜色套牌赛制补齐、桌面端选择系列搜索、zh-CN 本地化补全。合并 upstream 时不要直接覆盖这些本地改动。遇到冲突先判断属于哪一组改动，再决定保留 upstream、保留本地，还是手动合并。
```

## 检查清单

- [ ] `LOCAL_CHANGES.md` 和 `LOCAL_CHANGES.zh-CN.md` 是否都更新了。
- [ ] `git diff --name-status origin/master..HEAD` 的文件是否都在说明里。
- [ ] 普通 Brawl 是否还正常。
- [ ] Historic Brawl override 是否还正常。
- [ ] 普通 Commander 是否还正常。
- [ ] Duel Commander override 是否能切换。
- [ ] Duel Commander 是否 20 血。
- [ ] Duel Commander 是否没有 commander damage。
- [ ] companion-only / commander-only 没被误写进 `Banned:`。
- [ ] 桌面 match 背景是否一场 match 只随机一次。
- [ ] 移动端 match 背景是否一场 match 只随机一次。
- [ ] `bg_match/` 是否只读取 jpg / jpeg / png。
- [ ] zh-CN 是否没有新增缺失 key 报错。
- [ ] Card Detail 快捷键是否能把 `CARD_DETAIL` 窗格切到前台。
- [ ] Ctrl+T / Panel Tabs 关闭时，所有 `DragCell` 标题头是否隐藏，包括多 tab cell。
- [ ] Ctrl+T / Panel Tabs 开启时，标题头是否恢复。
- [ ] 外部可施放牌是否显示在真实手牌后面。
- [ ] 外部可施放牌是否显示 `GY` / `EX` / `CMD` / `LIB` / `SB` 内部 badge。
- [ ] 外部可施放牌是否不计入 hand count。
- [ ] 拖 external card 是否不会调用 `reorderHand(...)`。
- [ ] 拖真实手牌是否仍正常调用 `reorderHand(...)`。
- [ ] 中文 UI 下，choose card name 窗口搜索 `Pithing Needle` / `Lightning Bolt` 是否能命中。
- [ ] 中文 UI 下，choose card name 窗口搜索“闪电击”等中文翻译名，在翻译存在时是否能命中。
- [ ] choose card name 选择结果是否仍保存 canonical English name。
- [ ] Phlage 从手牌施放后进入坟场，是否立刻出现在 external playable 区。
- [ ] Phlage 从坟场 Escape 施放后，是否从 external playable 区消失。
- [ ] Flashback / Adventure / Foretell / Prepared / Command 区可施放牌变化后是否刷新正常。
- [ ] Sunken Citadel 选蓝后，CardPanel 是否显示蓝色 mana icon。
- [ ] Painter's Servant 选红后，CardPanel 是否显示红色 mana icon。
- [ ] Cavern of Souls / Pithing Needle 的 chosen type/name 是否不显示在卡面上，但 Card Detail 仍显示。
- [ ] Z 键卡牌大图行为是否保持不变。
- [ ] 鼠标滚轮向上打开卡牌大图时，允许翻面的牌是否也能翻面 / 查看 alternate state。
- [ ] 鼠标按钮打开的卡牌大图是否也使用同样的 `mayFlip` 权限。
- [ ] face-down / hidden card 是否不会通过滚轮 / 鼠标按钮大图暴露不可见背面。
- [ ] split / aftermath 牌的大图旋转行为是否没有回归。
- [ ] Quick Gauntlet 只勾选生成型 allowed deck types 时，对局内决斗进度是否仍显示。
- [ ] Quick Gauntlet 生成出的 `eventNames` 是否和 `decks` 数量对齐。
- [ ] Drafting Process 是否不会把临时 Draft Log layout 状态保存成普通 editor layout。
- [ ] 普通轮抓没有特殊日志需求时，是否不显示 Draft Log。
- [ ] Conspiracy / 特殊轮抓需要日志时，是否显示 Draft Log。
- [ ] 轮抓界面点开 Draft Log 后，切到套牌编辑器、主页、再回套牌编辑器，Draft Log 是否不会被带过去。
- [ ] 普通套牌编辑器“卡组目录”后的正常 tabs 是否仍然存在。
- [ ] 已污染的用户 editor layout 如果含 `EDocID.EDITOR_LOG`，是否会回落默认布局。
- [ ] Draft Gauntlet 输 match 后是否继续下一场，直到 3 负；7 胜 / 3 负 / 最大场数用完后是否结束。
- [ ] Sealed Gauntlet 输 match 后是否继续下一场，直到 2 负；7 胜 / 2 负 / 最大场数用完后是否结束。
- [ ] Draft / Sealed Gauntlet 右侧是否显示 run 战绩、对局记录和当前对局状态。
- [ ] Draft / Sealed Gauntlet 最后一场是否会进入对局记录。
- [ ] Draft / Sealed Gauntlet run 结束后，最后一场是否不会重复显示成“当前对局”。
- [ ] Draft / Sealed Gauntlet BO3 最后一场未结束时，是否仍显示当前比分 / 进行中。
- [ ] Draft / Sealed Gauntlet AI 对手名是否是可读名字，不是 Java 类名。
- [ ] 随机 Pioneer / Historic / Legacy / Vintage / Premodern 颜色套牌是否出现在原本 Standard / Modern / Pauper 颜色套牌所在的生成型套牌入口。
- [ ] 随机 Pioneer / Historic / Legacy / Vintage / Premodern 颜色套牌是否都通过对应赛制过滤器生成。
- [ ] 非颜色随机指挥官、原型、主题套牌、随机套牌总类列表顶部是否显示本地化 `Random` 条目。
- [ ] 选择非颜色 `Random` 条目时，是否从当前实际候选中生成、不会选择自身，并且移动端显示套牌名和实际进入游戏的套牌一致。
- [ ] 桌面端选择系列搜索是否只过滤显示，不丢失隐藏的已勾选系列。
- [ ] 桌面端选择系列在搜索状态下点 OK，是否仍包含隐藏的已勾选系列。
- [ ] 桌面端选择系列的格式限制按钮是否作用于完整系列树，而不是当前搜索结果。
- [ ] 桌面端选择系列的随机选择是否会先清空搜索框，并从完整 active 系列范围随机勾选。
- [ ] Quest Mode 时空旅行搜索 `THS` / `USG` / `FIN` 等 set code 是否能找到对应世界。
- [ ] Quest Mode 时空旅行是否不会因为当前主格式包含某 set code 而匹配到 `Main world`。
- [ ] Quest Mode 套牌导入是否保留库存中拥有的 Snow-Covered basic lands，而不是当作免费普通基本地跳过。
- [ ] Yuna, Grand Summoner 的 Grand Summon 产生待使用效果后，提示框是否显示 `Grand Summon (Yuna)` 计数；效果消失后计数是否消失。
- [ ] 中文界面下 Yuna + Freed from the Real 宏重复播放时，是否持续选择录制的 Untap 能力，而不是第一个同文本 Tap 能力。
- [ ] 宏回放 strict action 失败时是否不会错误消费后续动作；mana 支付没有实际支付时是否不会消费支付动作。
