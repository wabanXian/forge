# Forge 工具脚本文档

这个目录里放的是一些辅助脚本，用来处理 Forge 资源、卡图、set 文件和中文牌名。除非特别说明，建议在 Forge 仓库根目录运行命令。

第一次运行时优先用 `--dry-run` 或只生成报告，确认结果没问题再真正写文件。

## `merge_sets.py`

用途：把多个 Forge 的 set 文件合并成一个新 set，并且可以把多个来源 set 的卡图复制到同一个目标卡图文件夹。

适合场景：把多个已有 set 合成一个自定义 set。

先预览：

```powershell
python tools/merge_sets.py `
  --editions-dir forge-gui/res/editions `
  --pics-dir "C:/Users/<你>/AppData/Roaming/Forge/Cache/pics/cards" `
  --sources M21 ZNR KHM `
  --target-code MIX `
  --target-name "Merged Test Set" `
  --dry-run
```

确认后正式执行：

```powershell
python tools/merge_sets.py `
  --editions-dir forge-gui/res/editions `
  --pics-dir "C:/Users/<你>/AppData/Roaming/Forge/Cache/pics/cards" `
  --sources M21 ZNR KHM `
  --target-code MIX `
  --target-name "Merged Test Set"
```

常用参数：

- `--sources`：来源 set code 或 edition 文件名。
- `--target-code`：新 set 的 Code。
- `--target-name`：新 set 的名字，也会作为默认输出文件名。
- `--target-file`：手动指定输出 set 文件路径。
- `--target-folder`：目标卡图文件夹名。默认等于 `--target-code`；如果不同，脚本会写入 `Code2=...`。
- `--dedupe none|name|name-rarity`：是否去重。默认 `none`，也就是不去重。
- `--no-copy-images`：只合并 set 文件，不复制卡图。
- `--image-conflict error|overwrite`：目标图片已存在时怎么处理。默认 `error`。
- `--overwrite-target`：允许覆盖已有目标 set 文件。
- `--dry-run`：只预览，不写文件。

卡图规则：

- 来源图片目录会按 `Code2`、`Code`、`Alias`、`ScryfallCode` 依次查找。
- 同名多图会按 Forge 的 artIndex 规则重命名。
- 例如两个来源都有 `Lightning Bolt.full.jpg`，合并后会变成：

```text
Lightning Bolt1.full.jpg
Lightning Bolt2.full.jpg
```

- `.jpg` 和 `.png` 都支持。
- 如果 set 里有牌，但来源目录没有对应图片，会给出 `Missing source image` 警告；set 内容仍会合并。
- `[tokens]` 和 `[other]` 会合并到 set 文件，但 token 图片不会复制，因为 token 图片走另一套目录和 key。

## `image_set_prepare.py`

用途：批量新增自定义牌时，根据卡图文件名生成可以追加到 set.txt 的内容，同时可以把过宽图片缩到 512 宽。

适合场景：你有一批自定义牌卡图，想快速生成：

```text
101 U My Custom Card
102 U Another Card
```

先预览：

```powershell
python tools/image_set_prepare.py --dir "D:/custom-set/cards" --start 101 --dry-run
```

正式生成 `output.txt` 并缩图：

```powershell
python tools/image_set_prepare.py --dir "D:/custom-set/cards" --start 101
```

只生成 `output.txt`，不缩图：

```powershell
python tools/image_set_prepare.py --dir "D:/custom-set/cards" --start 101 --no-resize
```

只缩图，不生成 `output.txt`：

```powershell
python tools/image_set_prepare.py --dir "D:/custom-set/cards" --start 1 --no-output
```

常用参数：

- `--dir`：图片所在目录。默认当前目录。
- `--output`：输出文件名。默认 `output.txt`。
- `--start`：开始编号。不填会交互询问。
- `--rarity`：生成行里的稀有度。默认 `U`。
- `--max-width`：超过这个宽度就缩图。默认 `512`。
- `--no-resize`：不缩图。
- `--no-output`：不生成 output。
- `--magick`：ImageMagick 命令路径。默认 `magick`。
- `--dry-run`：只预览，不写文件。

文件名转卡名规则：

```text
My Card.full.png       -> My Card
My Card4.full.jpg      -> My Card
My Card.fullborder.png -> My Card
```

生成行示例：

```text
101 U My Card
102 U My Card
103 U Another Card
```

如果同一张牌有多张图，比如：

```text
My Card.full.png
My Card2.full.png
```

脚本会生成两个 `My Card`。这正好符合 Forge 逻辑：同一个 set 里同名出现多次，Forge 会按顺序对应第 1 张图、第 2 张图。

注意：

- 脚本不会检查卡名是否真的存在于 `cardsfolder`。
- 脚本不会自动写画师。
- 脚本不会自动写特殊参数。
- 特殊双面牌、split 牌、flavor name、specialize face 等复杂情况需要你自己检查。
- 缩图需要 ImageMagick。

## `sync_mtgch_cardnames_zh_cn.py`

用途：从 mtgch API 同步缺失的简体中文牌名到 Forge 的 `cardnames-zh-CN.txt`。

这个脚本以 Forge 自己的 edition 文件为准，只处理 Forge set 里确实出现的英文牌名。

默认只生成报告，不修改文件：

```powershell
python tools/sync_mtgch_cardnames_zh_cn.py --sets big,otj,mh3
```

确认报告后写入：

```powershell
python tools/sync_mtgch_cardnames_zh_cn.py --sets big,otj,mh3 --write
```

只同步指定英文牌名：

```powershell
python tools/sync_mtgch_cardnames_zh_cn.py --cards "Solitude,Price of Freedom"
```

常用参数：

- `--forge-root`：Forge 仓库根目录。
- `--start-set`：从哪个 set code 开始处理。
- `--sets`：指定 set code，逗号分隔。
- `--cards`：指定英文牌名，逗号分隔。
- `--cards-file`：从文件读取英文牌名，一行一个。
- `--dry-run`：只生成报告，不写文件。默认就是这个行为。
- `--write`：真正写入 `cardnames-zh-CN.txt`。
- `--name-only`：缺少类型/规则文本时，也允许只写 `英文名|中文名`。
- `--fill-empty-existing`：填补已有空条目，例如 `EnglishName|||`。
- `--report`：报告输出路径。
- `--cache`：mtgch API 缓存路径。
- `--sleep`：API 请求之间等待秒数。
- `--refresh-cache`：忽略旧缓存，重新请求。
- `--debug-api`、`--verbose`：调试输出。

依赖：`requests`。

## `classify_mtgch_zh_cn_unmatched.py`

用途：分析中文同步脚本没能处理的条目，并生成分类报告。

这个脚本只生成报告，不会修改 `cardnames-zh-CN.txt`。

基本用法：

```powershell
python tools/classify_mtgch_zh_cn_unmatched.py --sets big,otj,mh3
```

启用 Scryfall 辅助判断：

```powershell
python tools/classify_mtgch_zh_cn_unmatched.py --sets big,otj,mh3 --use-scryfall
```

常用参数：

- `--forge-root`：Forge 仓库根目录。
- `--start-set`：从哪个 Forge set code 开始。默认 `big`。
- `--sets`：指定 set code，逗号分隔。
- `--sync-report`：已有同步报告路径。
- `--cache`：mtgch API 缓存路径。
- `--output`：分类报告输出路径。
- `--max-candidates`：每个名字不匹配项最多显示几个候选。
- `--use-scryfall`、`--no-scryfall`：是否使用 Scryfall 辅助。
- `--scryfall-cache`：Scryfall 缓存路径。
- `--refresh-scryfall-cache`：刷新 Scryfall 缓存。

## 安全建议

- 第一次运行尽量加 `--dry-run`。
- `merge_sets.py` 和 `image_set_prepare.py` 会写文件。
- `sync_mtgch_cardnames_zh_cn.py` 只有加 `--write` 才会写入中文牌名文件。
- `classify_mtgch_zh_cn_unmatched.py` 只生成报告。
- 把生成的 set 内容追加进正式 set 文件前，建议先快速检查编号、卡名、同名多图顺序。
