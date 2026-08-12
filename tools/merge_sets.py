#!/usr/bin/env python3
"""
Merge several Forge edition files into one edition and copy card images into
the merged set folder using Forge-compatible art-index filenames.

The script does not delete source editions or source images. Use --dry-run
first; the report shows every file that would be written or copied.
"""

from __future__ import annotations

import argparse
import datetime as _dt
import re
import shutil
import sys
import unicodedata
from collections import Counter, OrderedDict, defaultdict
from dataclasses import dataclass
from pathlib import Path


CARD_SECTIONS_WITH_COLLECTOR_NUMBERS = {
    "cards",
    "special slot",
    "precon product",
    "borderless",
    "etched",
    "showcase",
    "full art",
    "extended art",
    "alternate art",
    "retro frame",
    "buy a box",
    "promo",
    "prerelease promo",
    "bundle",
    "box topper",
    "jumpstart",
    "rebalanced",
    "eternal",
    "conjured",
    "scheme",
    "printsheets",
}

CARD_LINE_RE = re.compile(
    r"^(?:(?P<collector>.?[0-9A-Z-]+\S*[A-Z]*)\s)?"
    r"(?:(?P<rarity>[SCURML])\s)?"
    r"(?P<name>[^@$]+?)"
    r"(?P<artist> @[^$]*)?"
    r"(?P<params> \$\{.+\})?$"
)

TOKEN_LINE_RE = re.compile(
    r"^(?:(?P<collector>.?[0-9A-Z-]+\S?[A-Z☇]*)\s)?"
    r"(?P<name>[^@]+?)"
    r"(?P<artist> @.*)?$"
)

IMAGE_EXTENSIONS = (".jpg", ".png")


@dataclass
class EditionFile:
    path: Path
    metadata: OrderedDict[str, str]
    sections: OrderedDict[str, list[str]]

    @property
    def code(self) -> str:
        return self.metadata.get("Code", "")

    @property
    def name(self) -> str:
        return self.metadata.get("Name", self.path.stem)


@dataclass
class CardEntry:
    source_code: str
    source_set_folders: tuple[str, ...]
    source_section: str
    old_collector: str
    new_collector: str
    rarity: str
    name: str
    suffix: str
    source_art_index: int = 1
    source_total: int = 1
    target_art_index: int = 1
    target_total: int = 1

    def to_line(self) -> str:
        bits = [self.new_collector]
        if self.rarity:
            bits.append(self.rarity)
        bits.append(self.name)
        return " ".join(bits) + self.suffix


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Merge Forge edition txt files and card images into one set."
    )
    parser.add_argument(
        "--editions-dir",
        default="forge-gui/res/editions",
        type=Path,
        help="Directory containing Forge edition txt files.",
    )
    parser.add_argument(
        "--pics-dir",
        type=Path,
        help="Card image cache directory containing per-set folders, e.g. .../pics/cards.",
    )
    parser.add_argument(
        "--sources",
        nargs="+",
        required=True,
        help="Source edition codes or exact edition file stems.",
    )
    parser.add_argument("--target-code", required=True, help="Code for the merged edition.")
    parser.add_argument("--target-name", required=True, help="Name for the merged edition.")
    parser.add_argument(
        "--target-file",
        type=Path,
        help="Output edition txt file. Defaults to <editions-dir>/<target-name>.txt.",
    )
    parser.add_argument(
        "--target-folder",
        help="Target image folder name. Defaults to --target-code.",
    )
    parser.add_argument(
        "--date",
        default=_dt.date.today().isoformat(),
        help="Date written to target metadata. Defaults to today.",
    )
    parser.add_argument(
        "--type",
        default="Custom_Set",
        help="Type written to target metadata. Defaults to Custom_Set.",
    )
    parser.add_argument(
        "--dedupe",
        choices=("none", "name", "name-rarity"),
        default="none",
        help="Card entry de-duplication strategy. Defaults to none.",
    )
    parser.add_argument(
        "--copy-images",
        dest="copy_images",
        action="store_true",
        default=True,
        help="Copy and rename card images. Enabled by default.",
    )
    parser.add_argument(
        "--no-copy-images",
        dest="copy_images",
        action="store_false",
        help="Only write the merged edition file; do not copy images.",
    )
    parser.add_argument(
        "--image-conflict",
        choices=("error", "overwrite"),
        default="error",
        help="What to do if a target image already exists. Defaults to error.",
    )
    parser.add_argument(
        "--overwrite-target",
        action="store_true",
        help="Allow replacing an existing target edition file.",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Print planned changes without writing files.",
    )
    return parser.parse_args()


def parse_edition_file(path: Path) -> EditionFile:
    sections: OrderedDict[str, list[str]] = OrderedDict()
    current: str | None = None

    for raw in path.read_text(encoding="utf-8-sig").splitlines():
        line = raw.rstrip("\n")
        section_match = re.match(r"^\[(.+)]\s*$", line)
        if section_match:
            current = section_match.group(1)
            sections.setdefault(current, [])
            continue
        if current is not None:
            sections[current].append(line)

    metadata = OrderedDict()
    for line in sections.get("metadata", []):
        if not line.strip() or line.lstrip().startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        metadata[key.strip()] = value.strip()

    return EditionFile(path=path, metadata=metadata, sections=sections)


def build_edition_index(editions_dir: Path) -> tuple[dict[str, EditionFile], dict[str, EditionFile]]:
    by_code: dict[str, EditionFile] = {}
    by_stem: dict[str, EditionFile] = {}
    for path in sorted(editions_dir.glob("*.txt")):
        edition = parse_edition_file(path)
        if edition.code:
            by_code[edition.code.upper()] = edition
        by_stem[path.stem.lower()] = edition
    return by_code, by_stem


def resolve_sources(editions_dir: Path, sources: list[str]) -> list[EditionFile]:
    by_code, by_stem = build_edition_index(editions_dir)
    resolved: list[EditionFile] = []
    missing: list[str] = []

    for source in sources:
        edition = by_code.get(source.upper()) or by_stem.get(source.lower())
        if edition is None:
            missing.append(source)
        else:
            resolved.append(edition)

    if missing:
        raise SystemExit(f"Could not resolve source edition(s): {', '.join(missing)}")
    return resolved


def parse_card_line(line: str) -> tuple[str, str, str, str] | None:
    if not line.strip() or line.lstrip().startswith("#"):
        return None
    match = CARD_LINE_RE.match(line)
    if not match:
        return None
    name = match.group("name").strip()
    if not name:
        return None
    collector = match.group("collector") or ""
    rarity = match.group("rarity") or ""
    suffix = (match.group("artist") or "") + (match.group("params") or "")
    return collector, rarity, name, suffix


def parse_token_line(line: str) -> tuple[str, str, str] | None:
    if not line.strip() or line.lstrip().startswith("#"):
        return None
    match = TOKEN_LINE_RE.match(line)
    if not match:
        return None
    name = match.group("name").strip()
    if not name:
        return None
    collector = match.group("collector") or ""
    suffix = match.group("artist") or ""
    return collector, name, suffix


def source_set_folders(edition: EditionFile) -> tuple[str, ...]:
    folders = [
        edition.metadata.get("Code2", ""),
        edition.code,
        edition.metadata.get("Alias", ""),
        edition.metadata.get("ScryfallCode", ""),
    ]
    return tuple(dict.fromkeys(folder for folder in folders if folder))


def collect_cards(editions: list[EditionFile], dedupe: str) -> tuple[OrderedDict[str, list[CardEntry]], list[str]]:
    merged: OrderedDict[str, list[CardEntry]] = OrderedDict()
    warnings: list[str] = []
    seen: set[tuple[str, ...]] = set()
    old_entries_by_source: dict[str, list[CardEntry]] = defaultdict(list)

    next_collector = 1
    for edition in editions:
        set_folders = source_set_folders(edition)
        for section, lines in edition.sections.items():
            section_key = section.lower()
            if section_key not in CARD_SECTIONS_WITH_COLLECTOR_NUMBERS:
                continue
            for line in lines:
                parsed = parse_card_line(line)
                if parsed is None:
                    continue
                old_collector, rarity, name, suffix = parsed
                dedupe_key: tuple[str, ...] | None = None
                if dedupe == "name":
                    dedupe_key = (name,)
                elif dedupe == "name-rarity":
                    dedupe_key = (name, rarity)
                if dedupe_key is not None and dedupe_key in seen:
                    continue
                if dedupe_key is not None:
                    seen.add(dedupe_key)

                entry = CardEntry(
                    source_code=edition.code,
                    source_set_folders=set_folders,
                    source_section=section,
                    old_collector=old_collector,
                    new_collector=str(next_collector),
                    rarity=rarity,
                    name=name,
                    suffix=suffix,
                )
                next_collector += 1
                merged.setdefault(section, []).append(entry)
                old_entries_by_source[edition.code].append(entry)

    for entries in old_entries_by_source.values():
        counts = Counter(entry.name for entry in entries)
        occurrence = Counter()
        for entry in entries:
            occurrence[entry.name] += 1
            entry.source_art_index = occurrence[entry.name]
            entry.source_total = counts[entry.name]

    target_counts = Counter(entry.name for entries in merged.values() for entry in entries)
    target_occurrence = Counter()
    for entries in merged.values():
        for entry in entries:
            target_occurrence[entry.name] += 1
            entry.target_art_index = target_occurrence[entry.name]
            entry.target_total = target_counts[entry.name]

    if not merged:
        warnings.append("No card entries were merged.")
    return merged, warnings


def collect_tokens_and_other(editions: list[EditionFile]) -> OrderedDict[str, list[str]]:
    result: OrderedDict[str, list[str]] = OrderedDict()
    token_counter = 1
    other_counter = 1
    for edition in editions:
        for section in ("tokens", "other"):
            for line in edition.sections.get(section, []):
                parsed = parse_token_line(line)
                if parsed is None:
                    if line.strip():
                        result.setdefault(section, []).append(line)
                    continue
                _old_collector, name, suffix = parsed
                if section == "tokens":
                    new_collector = token_counter
                    token_counter += 1
                else:
                    new_collector = other_counter
                    other_counter += 1
                result.setdefault(section, []).append(f"{new_collector} {name}{suffix}")
    return result


def to_mws_filename(name: str) -> str:
    normalized = unicodedata.normalize("NFKD", name)
    stripped = "".join(ch for ch in normalized if not unicodedata.combining(ch))
    return "".join(ch for ch in stripped if ch not in {'"', "/", ":", "?"})


def image_stem(name: str, art_index: int, total: int, is_plane: bool = False) -> str:
    stem = to_mws_filename(name)
    if total > 1:
        stem += str(art_index)
    if not is_plane:
        stem += ".full"
    return stem


def candidate_image_stems(name: str, art_index: int, total: int) -> list[str]:
    main = image_stem(name, art_index, total)
    candidates = [main]
    if main.endswith(".full"):
        candidates.append(main.replace(".full", ".fullborder"))
    if total > 1 and art_index == 1:
        base = image_stem(name, 1, 1)
        candidates.append(base)
        candidates.append(base.replace(".full", ".fullborder"))
    return list(dict.fromkeys(candidates))


def find_source_image(pics_dir: Path, entry: CardEntry) -> Path | None:
    for source_set_folder in entry.source_set_folders:
        source_dir = pics_dir / source_set_folder
        if not source_dir.is_dir():
            continue

        for stem in candidate_image_stems(entry.name, entry.source_art_index, entry.source_total):
            for ext in IMAGE_EXTENSIONS:
                candidate = source_dir / f"{stem}{ext}"
                if candidate.is_file():
                    return candidate
    return None


def plan_image_copies(
    pics_dir: Path,
    target_folder: str,
    entries_by_section: OrderedDict[str, list[CardEntry]],
) -> tuple[list[tuple[Path, Path]], list[str], Counter[str]]:
    copies: list[tuple[Path, Path]] = []
    warnings: list[str] = []
    duplicate_groups: Counter[str] = Counter()
    target_dir = pics_dir / target_folder

    for entries in entries_by_section.values():
        for entry in entries:
            if entry.target_total > 1:
                duplicate_groups[entry.name] = entry.target_total
            source = find_source_image(pics_dir, entry)
            if source is None:
                warnings.append(
                    f"Missing source image for {entry.source_code}/{entry.name} "
                    f"(source art {entry.source_art_index}/{entry.source_total})"
                )
                continue
            target_stem = image_stem(entry.name, entry.target_art_index, entry.target_total)
            target = target_dir / f"{target_stem}{source.suffix.lower()}"
            copies.append((source, target))

    return copies, warnings, duplicate_groups


def render_merged_file(
    target_code: str,
    target_name: str,
    target_type: str,
    date: str,
    entries_by_section: OrderedDict[str, list[CardEntry]],
    tokens_and_other: OrderedDict[str, list[str]],
    sources: list[EditionFile],
    target_folder: str,
) -> str:
    lines: list[str] = [
        "[metadata]",
        f"Code={target_code}",
    ]
    if target_folder != target_code:
        lines.append(f"Code2={target_folder}")
    lines.extend([
        f"Date={date}",
        f"Name={target_name}",
        f"Type={target_type}",
        f"ScryfallCode={target_code}",
        "",
        f"# Merged from: {', '.join(e.code for e in sources)}",
    ])

    for section, entries in entries_by_section.items():
        lines.extend(["", f"[{section}]"])
        last_source = None
        for entry in entries:
            if entry.source_code != last_source:
                lines.append(f"# From {entry.source_code}")
                last_source = entry.source_code
            lines.append(entry.to_line())

    for section, section_lines in tokens_and_other.items():
        lines.extend(["", f"[{section}]"])
        lines.extend(section_lines)

    lines.append("")
    return "\n".join(lines)


def ensure_can_write_target(path: Path, overwrite: bool) -> None:
    if path.exists() and not overwrite:
        raise SystemExit(f"Target edition file already exists: {path} (use --overwrite-target)")


def execute_writes(
    target_file: Path,
    content: str,
    copies: list[tuple[Path, Path]],
    overwrite_target: bool,
    image_conflict: str,
    dry_run: bool,
) -> None:
    ensure_can_write_target(target_file, overwrite_target)

    conflicts = [dst for _src, dst in copies if dst.exists()]
    if conflicts and image_conflict == "error":
        formatted = "\n  ".join(str(path) for path in conflicts[:20])
        extra = "" if len(conflicts) <= 20 else f"\n  ... and {len(conflicts) - 20} more"
        raise SystemExit(f"Target image conflict(s):\n  {formatted}{extra}\nUse --image-conflict overwrite to replace them.")

    if dry_run:
        return

    target_file.parent.mkdir(parents=True, exist_ok=True)
    target_file.write_text(content, encoding="utf-8", newline="\n")

    for source, target in copies:
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(source, target)


def print_report(
    sources: list[EditionFile],
    target_file: Path,
    pics_dir: Path | None,
    target_folder: str,
    entries_by_section: OrderedDict[str, list[CardEntry]],
    copies: list[tuple[Path, Path]],
    warnings: list[str],
    duplicate_groups: Counter[str],
    dry_run: bool,
) -> None:
    total_cards = sum(len(entries) for entries in entries_by_section.values())
    print("Mode:", "dry-run" if dry_run else "write")
    print("Source editions:")
    for edition in sources:
        print(f"  {edition.code}: {edition.name} ({edition.path})")
    print(f"Target edition file: {target_file}")
    print(f"Card entries: {total_cards}")
    print("Sections:")
    for section, entries in entries_by_section.items():
        print(f"  [{section}]: {len(entries)}")

    if pics_dir is not None:
        print(f"Target image folder: {pics_dir / target_folder}")
        print(f"Planned image copies: {len(copies)}")
        if copies:
            print("Image remap sample:")
            for source, target in copies[:20]:
                print(f"  {source} -> {target}")
            if len(copies) > 20:
                print(f"  ... and {len(copies) - 20} more")
    else:
        print("Image copy: disabled")

    if duplicate_groups:
        print("Duplicate card image groups:")
        for name, count in duplicate_groups.most_common(20):
            print(f"  {name}: {count} printings")
        if len(duplicate_groups) > 20:
            print(f"  ... and {len(duplicate_groups) - 20} more")

    if warnings:
        print("Warnings:")
        for warning in warnings[:50]:
            print(f"  {warning}")
        if len(warnings) > 50:
            print(f"  ... and {len(warnings) - 50} more")


def main() -> int:
    args = parse_args()
    editions_dir = args.editions_dir
    if not editions_dir.is_dir():
        raise SystemExit(f"Edition directory not found: {editions_dir}")

    sources = resolve_sources(editions_dir, args.sources)
    target_file = args.target_file or editions_dir / f"{args.target_name}.txt"
    target_folder = args.target_folder or args.target_code

    entries_by_section, warnings = collect_cards(sources, args.dedupe)
    tokens_and_other = collect_tokens_and_other(sources)
    content = render_merged_file(
        args.target_code,
        args.target_name,
        args.type,
        args.date,
        entries_by_section,
        tokens_and_other,
        sources,
        target_folder,
    )

    copies: list[tuple[Path, Path]] = []
    duplicate_groups: Counter[str] = Counter()
    if args.copy_images:
        if args.pics_dir is None:
            raise SystemExit("--pics-dir is required unless --no-copy-images is used")
        copies, image_warnings, duplicate_groups = plan_image_copies(
            args.pics_dir, target_folder, entries_by_section
        )
        warnings.extend(image_warnings)

    print_report(
        sources,
        target_file,
        args.pics_dir if args.copy_images else None,
        target_folder,
        entries_by_section,
        copies,
        warnings,
        duplicate_groups,
        args.dry_run,
    )

    execute_writes(
        target_file,
        content,
        copies,
        args.overwrite_target,
        args.image_conflict,
        args.dry_run,
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
