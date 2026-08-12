# Forge Tools

This directory contains small repository maintenance and content-preparation scripts. Run commands from the Forge repository root unless a script says otherwise.

Most write-capable scripts support a dry-run or report-first workflow. Prefer that mode before writing resource files.

## `merge_sets.py`

Merges multiple Forge edition files into one new edition and optionally copies card images into one target image folder.

Use it when combining several existing sets into one custom set.

Example dry run:

```powershell
python tools/merge_sets.py `
  --editions-dir forge-gui/res/editions `
  --pics-dir "C:/Users/<you>/AppData/Roaming/Forge/Cache/pics/cards" `
  --sources M21 ZNR KHM `
  --target-code MIX `
  --target-name "Merged Test Set" `
  --dry-run
```

Write after reviewing the report:

```powershell
python tools/merge_sets.py `
  --editions-dir forge-gui/res/editions `
  --pics-dir "C:/Users/<you>/AppData/Roaming/Forge/Cache/pics/cards" `
  --sources M21 ZNR KHM `
  --target-code MIX `
  --target-name "Merged Test Set"
```

Useful options:

- `--sources`: Source edition codes or exact edition file stems.
- `--target-code`: Code written to the merged edition metadata.
- `--target-name`: Name and default output filename for the merged edition.
- `--target-file`: Explicit output edition file.
- `--target-folder`: Target image folder name. Defaults to `--target-code`; if it differs, the script writes `Code2=...`.
- `--dedupe none|name|name-rarity`: Card entry de-duplication. Defaults to `none`.
- `--no-copy-images`: Merge only the edition file.
- `--image-conflict error|overwrite`: Target image conflict policy. Defaults to `error`.
- `--overwrite-target`: Allow replacing an existing target edition file.
- `--dry-run`: Print planned changes without writing.

Image behavior:

- Source image folders are searched by `Code2`, `Code`, `Alias`, then `ScryfallCode`.
- Normal card image names follow Forge art-index behavior: duplicate names in the merged set become `Card Name1.full.jpg`, `Card Name2.full.jpg`, etc.
- Both `.jpg` and `.png` source images are supported.
- Missing source images are reported as warnings. The edition entry is still merged.
- Token and `other` sections are merged into the edition file, but token images are not copied because they use a different image directory/key path.

## `image_set_prepare.py`

Prepares a folder of custom card images by resizing images and generating Forge edition lines from filenames.

Use it when adding many custom cards and you want a quick `output.txt` to paste into an edition section.

Example dry run:

```powershell
python tools/image_set_prepare.py --dir "D:/custom-set/cards" --start 101 --dry-run
```

Write `output.txt` and resize wide images:

```powershell
python tools/image_set_prepare.py --dir "D:/custom-set/cards" --start 101
```

Only generate `output.txt`:

```powershell
python tools/image_set_prepare.py --dir "D:/custom-set/cards" --start 101 --no-resize
```

Only resize images:

```powershell
python tools/image_set_prepare.py --dir "D:/custom-set/cards" --start 1 --no-output
```

Useful options:

- `--dir`: Folder containing images. Defaults to the current directory.
- `--output`: Output filename. Defaults to `output.txt`.
- `--start`: Starting collector number. If omitted, the script prompts interactively.
- `--rarity`: Rarity letter for generated lines. Defaults to `U`.
- `--max-width`: Resize images wider than this width. Defaults to `512`.
- `--no-resize`: Skip ImageMagick resizing.
- `--no-output`: Skip output generation.
- `--magick`: ImageMagick executable path/name. Defaults to `magick`.
- `--dry-run`: Preview without writing.

Generated line behavior:

- `Card Name.full.png` becomes `101 U Card Name`.
- `Card Name4.full.jpg` also becomes `101 U Card Name`.
- `.full`, `.fullborder`, trailing art-index digits, and the file extension are stripped.
- Duplicate image names intentionally generate duplicate card names, matching Forge's art-index behavior within one set.

Limitations:

- The script does not validate card names against `cardsfolder`.
- It does not infer artists, extra params, split-card names, flavor names, or special faces.
- Resizing requires ImageMagick.

## `sync_mtgch_cardnames_zh_cn.py`

Synchronizes missing Simplified Chinese card names from the mtgch API into Forge's `cardnames-zh-CN.txt`.

The script uses Forge edition files as the source of truth. Only English card names that appear in selected Forge editions are eligible.

Default mode is report-only:

```powershell
python tools/sync_mtgch_cardnames_zh_cn.py --sets big,otj,mh3
```

Write changes after reviewing the report:

```powershell
python tools/sync_mtgch_cardnames_zh_cn.py --sets big,otj,mh3 --write
```

Sync explicit cards:

```powershell
python tools/sync_mtgch_cardnames_zh_cn.py --cards "Solitude,Price of Freedom"
```

Useful options:

- `--forge-root`: Forge repository root.
- `--start-set`: First set code to include.
- `--sets`: Comma-separated explicit set codes.
- `--cards`: Comma-separated English card names.
- `--cards-file`: One English card name per line.
- `--dry-run`: Report only. This is the default.
- `--write`: Append/update `cardnames-zh-CN.txt`.
- `--name-only`: Allow `EnglishName|ChineseName` when type/text are unavailable.
- `--fill-empty-existing`: Fill existing empty entries such as `EnglishName|||`.
- `--report`: Markdown report path.
- `--cache`: mtgch API cache path.
- `--sleep`: Delay between API requests.
- `--refresh-cache`: Ignore cached mtgch set responses.
- `--debug-api`, `--verbose`: Diagnostics.

Dependency: `requests`.

## `classify_mtgch_zh_cn_unmatched.py`

Generates a markdown classification report for mtgch zh-CN sync items that were not resolved by the sync script.

The tool is report-only. It reads Forge editions, the mtgch cache, and the existing sync report.

Basic usage:

```powershell
python tools/classify_mtgch_zh_cn_unmatched.py --sets big,otj,mh3
```

With Scryfall-assisted classification:

```powershell
python tools/classify_mtgch_zh_cn_unmatched.py --sets big,otj,mh3 --use-scryfall
```

Useful options:

- `--forge-root`: Forge repository root.
- `--start-set`: First Forge set code to include. Defaults to `big`.
- `--sets`: Comma-separated explicit set codes.
- `--sync-report`: Existing sync report path.
- `--cache`: mtgch API response cache path.
- `--output`: Classification markdown report path.
- `--max-candidates`: Maximum fuzzy candidates to show per mismatch.
- `--use-scryfall`, `--no-scryfall`: Enable or disable Scryfall assistance.
- `--scryfall-cache`: Scryfall cache path.
- `--refresh-scryfall-cache`: Refresh cached Scryfall responses.

## Safety Notes

- Keep `--dry-run` or report-only mode in the first pass.
- `merge_sets.py` and `image_set_prepare.py` can write files.
- `sync_mtgch_cardnames_zh_cn.py` writes only with `--write`.
- `classify_mtgch_zh_cn_unmatched.py` is report-only.
- Review generated edition lines before committing them.
