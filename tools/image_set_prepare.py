#!/usr/bin/env python3
"""
Prepare a small Forge-style image set folder.

This combines two older PowerShell helpers:
1. resize image files wider than a configured maximum width;
2. generate output.txt entries from image filenames.

By default it works on the current directory. Use --dir to point at another
folder.
"""

from __future__ import annotations

import argparse
import re
import shutil
import subprocess
import sys
from pathlib import Path


IMAGE_EXTENSIONS = {".png", ".jpg", ".jpeg", ".webp"}


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Resize card images and generate Forge edition lines from filenames."
    )
    parser.add_argument(
        "--dir",
        type=Path,
        default=Path.cwd(),
        help="Folder containing images. Defaults to the current directory.",
    )
    parser.add_argument(
        "--output",
        default="output.txt",
        help="Output txt filename. Defaults to output.txt inside --dir.",
    )
    parser.add_argument(
        "--start",
        type=int,
        help="Starting collector number. If omitted, asks interactively.",
    )
    parser.add_argument(
        "--rarity",
        default="U",
        help="Rarity letter written to output lines. Defaults to U.",
    )
    parser.add_argument(
        "--max-width",
        type=int,
        default=512,
        help="Resize images wider than this width. Defaults to 512.",
    )
    parser.add_argument(
        "--no-resize",
        action="store_true",
        help="Skip image resizing and only generate output.txt.",
    )
    parser.add_argument(
        "--no-output",
        action="store_true",
        help="Skip output.txt generation and only resize images.",
    )
    parser.add_argument(
        "--magick",
        default="magick",
        help="ImageMagick executable name/path. Defaults to magick.",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Print what would happen without writing files.",
    )
    return parser.parse_args()


def ask_start_index() -> int:
    raw = input("Start index (press Enter for 1): ").strip()
    if not raw:
        return 1
    if raw.isdigit():
        return int(raw)
    raise SystemExit("Start index must be a number.")


def list_image_files(folder: Path) -> list[Path]:
    return sorted(
        (
            path
            for path in folder.iterdir()
            if path.is_file() and path.suffix.lower() in IMAGE_EXTENSIONS
        ),
        key=lambda path: path.name.lower(),
    )


def output_source_files(folder: Path, output_file: Path) -> list[Path]:
    return sorted(
        (
            path
            for path in folder.iterdir()
            if path.is_file()
            and path.name != output_file.name
            and path.suffix.lower() != ".ps1"
            and path.name != Path(__file__).name
        ),
        key=lambda path: path.name.lower(),
    )


def card_name_from_file(path: Path) -> str:
    name = path.stem
    name = re.sub(r"\.(full|fullborder)$", "", name, flags=re.IGNORECASE)
    name = re.sub(r"\d+$", "", name)
    return name


def generate_output(folder: Path, output_file: Path, start: int, rarity: str, dry_run: bool) -> int:
    lines: list[str] = []
    index = start
    for path in output_source_files(folder, output_file):
        lines.append(f"{index} {rarity} {card_name_from_file(path)}")
        index += 1

    if dry_run:
        print(f"[dry-run] Would write {len(lines)} line(s) to {output_file}")
        for line in lines[:20]:
            print(f"  {line}")
        if len(lines) > 20:
            print(f"  ... and {len(lines) - 20} more")
    else:
        output_file.write_text("\n".join(lines) + ("\n" if lines else ""), encoding="utf-8")
        print(f"Done. Wrote output from index {start} to {output_file}")
    return len(lines)


def run_magick(args: list[str]) -> subprocess.CompletedProcess[str]:
    return subprocess.run(args, text=True, capture_output=True, check=True)


def image_size(magick: str, path: Path) -> tuple[int, int]:
    result = run_magick([magick, "identify", "-format", "%w %h", str(path)])
    width_text, height_text = result.stdout.strip().split()
    return int(width_text), int(height_text)


def resize_images(folder: Path, max_width: int, magick: str, dry_run: bool) -> int:
    resized = 0
    for path in list_image_files(folder):
        width, _height = image_size(magick, path)
        if width <= max_width:
            continue

        resized += 1
        temp_file = path.with_name(f"__tmp__{path.name}")
        if dry_run:
            print(f"[dry-run] Would resize {path.name}: width {width} -> {max_width}")
            continue

        try:
            run_magick([magick, str(path), "-resize", f"{max_width}x", str(temp_file)])
            shutil.move(str(temp_file), str(path))
            print(f"Resized {path.name}: width {width} -> {max_width}")
        finally:
            if temp_file.exists():
                temp_file.unlink()
    return resized


def main() -> int:
    args = parse_args()
    folder = args.dir.resolve()
    if not folder.is_dir():
        raise SystemExit(f"Directory does not exist: {folder}")

    output_file = (folder / args.output).resolve()
    start = args.start if args.start is not None else ask_start_index()
    if start < 0:
        raise SystemExit("Start index must be a non-negative number.")

    try:
        if not args.no_resize:
            resized = resize_images(folder, args.max_width, args.magick, args.dry_run)
            print(f"Resize check complete. Files processed: {resized}.")

        if not args.no_output:
            count = generate_output(folder, output_file, start, args.rarity, args.dry_run)
            print(f"Output generation complete. Lines: {count}.")
    except FileNotFoundError:
        raise SystemExit(f"ImageMagick command not found: {args.magick}")
    except subprocess.CalledProcessError as exc:
        if exc.stderr:
            print(exc.stderr, file=sys.stderr)
        raise SystemExit(f"ImageMagick failed: {' '.join(exc.cmd)}")

    return 0


if __name__ == "__main__":
    sys.exit(main())
