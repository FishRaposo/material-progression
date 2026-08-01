"""Generate deterministic local inventory art for authored item groups.

The generator deliberately has no image-library dependency.  It writes the
small RGBA PNGs directly so committed sprites are reproducible byte-for-byte.
"""

from __future__ import annotations

import argparse
import struct
import sys
import zlib
from pathlib import Path


RGBA = tuple[int, int, int, int]
TRANSPARENT: RGBA = (0, 0, 0, 0)
ROOT = Path(__file__).resolve().parents[1]

if str(ROOT / "tests") not in sys.path:
    sys.path.insert(0, str(ROOT / "tests"))

from content_contracts import AUTHORED_ITEM_GROUPS  # noqa: E402


Palette = tuple[RGBA, RGBA, RGBA, RGBA | None]

# Every geological family has shadow, base, highlight, and an optional accent.
PALETTES: dict[str, Palette] = {
    "stone": ((54, 56, 58, 255), (99, 103, 106, 255), (151, 155, 157, 255), None),
    "granite": ((91, 61, 54, 255), (151, 99, 83, 255), (202, 144, 119, 255), None),
    "diorite": ((102, 103, 101, 255), (166, 166, 158, 255), (220, 218, 202, 255), None),
    "andesite": ((66, 69, 70, 255), (109, 114, 114, 255), (158, 163, 160, 255), None),
    "deepslate": ((33, 39, 49, 255), (61, 70, 83, 255), (98, 107, 122, 255), None),
    "tuff": ((76, 87, 72, 255), (119, 134, 109, 255), (169, 179, 146, 255), None),
    "calcite": ((143, 137, 119, 255), (202, 194, 169, 255), (239, 232, 204, 255), None),
    "dripstone": ((98, 68, 49, 255), (151, 105, 76, 255), (204, 155, 113, 255), None),
    "sulfur": ((133, 111, 24, 255), (203, 174, 44, 255), (242, 220, 104, 255), (255, 240, 143, 255)),
    "cinnabar": ((82, 49, 46, 255), (126, 80, 70, 255), (171, 112, 94, 255), (207, 55, 42, 255)),
    "sandstone": ((139, 111, 61, 255), (194, 161, 96, 255), (234, 207, 139, 255), None),
    "red_sandstone": ((126, 62, 38, 255), (184, 95, 56, 255), (227, 139, 81, 255), None),
    "netherrack": ((88, 38, 42, 255), (142, 59, 62, 255), (190, 84, 77, 255), None),
    "basalt": ((46, 48, 50, 255), (79, 83, 84, 255), (127, 130, 124, 255), None),
    "blackstone": ((39, 35, 44, 255), (67, 61, 73, 255), (112, 101, 118, 255), None),
    "end_stone": ((147, 145, 93, 255), (193, 191, 125, 255), (227, 224, 158, 255), None),
}

# Four deliberately irregular 8x8 chips.  All rocks assign one of these,
# while the position and palette make each family read independently.
CHIP_SILHOUETTES: tuple[frozenset[tuple[int, int]], ...] = (
    frozenset((x, y) for y, xs in enumerate(((3, 4), (2, 3, 4, 5), (1, 2, 3, 4, 5, 6), (1, 2, 3, 4, 5, 6), (2, 3, 4, 5, 6), (2, 3, 4, 5), (3, 4))) for x in xs),
    frozenset((x, y) for y, xs in enumerate(((2, 3, 4), (1, 2, 3, 4, 5), (1, 2, 3, 4, 5), (0, 1, 2, 3, 4, 5), (1, 2, 3, 4, 5, 6), (2, 3, 4, 5, 6), (3, 4, 5))) for x in xs),
    frozenset((x, y) for y, xs in enumerate(((3, 4), (2, 3, 4, 5), (1, 2, 3, 4, 5), (1, 2, 3, 4, 5, 6), (1, 2, 3, 4, 5, 6), (2, 3, 4, 5), (3, 4))) for x in xs),
    frozenset((x, y) for y, xs in enumerate(((2, 3, 4), (1, 2, 3, 4), (1, 2, 3, 4, 5), (0, 1, 2, 3, 4, 5), (1, 2, 3, 4, 5), (2, 3, 4, 5, 6), (3, 4, 5))) for x in xs),
)

ROCK_SILHOUETTES: dict[str, int] = {
    "stone": 0, "granite": 1, "diorite": 2, "andesite": 3,
    "deepslate": 1, "tuff": 2, "calcite": 3, "dripstone": 0,
    "sulfur": 3, "cinnabar": 1, "sandstone": 2, "red_sandstone": 0,
    "netherrack": 1, "basalt": 3, "blackstone": 2, "end_stone": 0,
}

ROCK_ITEMS: dict[str, str] = {
    "rock": "stone",
    **{f"{family}_rock": family for family in PALETTES if family != "stone"},
}


def _family_for(item_id: str) -> str:
    if item_id in ROCK_ITEMS:
        return ROCK_ITEMS[item_id]
    if item_id.startswith("cobbled_"):
        return item_id.removeprefix("cobbled_")
    raise ValueError(f"No art family is defined for {item_id!r}")


def _paint_chip(
    pixels: list[list[RGBA]],
    silhouette: frozenset[tuple[int, int]],
    origin: tuple[int, int],
    palette: Palette,
    *,
    accent_budget: int = 0,
) -> None:
    shadow, base, highlight, accent = palette
    offset_x, offset_y = origin
    for x, y in silhouette:
        destination_x, destination_y = offset_x + x, offset_y + y
        if not (0 <= destination_x < 16 and 0 <= destination_y < 16):
            continue
        if (x, y + 1) not in silhouette or (x + 1, y) not in silhouette:
            color = shadow
        elif x + y <= 5:
            color = highlight
        else:
            color = base
        pixels[destination_y][destination_x] = color
    if accent is not None:
        candidates = sorted(silhouette, key=lambda point: (point[1], point[0]))
        for x, y in candidates[2:2 + accent_budget]:
            pixels[offset_y + y][offset_x + x] = accent


def _rock_sprite(family: str) -> tuple[tuple[RGBA, ...], ...]:
    pixels = [[TRANSPARENT for _ in range(16)] for _ in range(16)]
    silhouette = CHIP_SILHOUETTES[ROCK_SILHOUETTES[family]]
    _paint_chip(pixels, silhouette, (4, 4), PALETTES[family], accent_budget=4)
    return tuple(tuple(row) for row in pixels)


def _cobble_sprite(family: str) -> tuple[tuple[RGBA, ...], ...]:
    pixels = [[TRANSPARENT for _ in range(16)] for _ in range(16)]
    palette = PALETTES[family]
    primary = CHIP_SILHOUETTES[ROCK_SILHOUETTES[family]]
    secondary = CHIP_SILHOUETTES[(ROCK_SILHOUETTES[family] + 1) % 4]
    tertiary = CHIP_SILHOUETTES[(ROCK_SILHOUETTES[family] + 2) % 4]
    _paint_chip(pixels, primary, (2, 5), palette, accent_budget=2)
    _paint_chip(pixels, secondary, (7, 3), palette, accent_budget=1)
    _paint_chip(pixels, tertiary, (8, 8), palette, accent_budget=1)
    return tuple(tuple(row) for row in pixels)


def make_sprite(item_id: str) -> tuple[tuple[RGBA, ...], ...]:
    """Return a deterministic 16x16 RGBA sprite for one geological item."""
    family = _family_for(item_id)
    return _cobble_sprite(family) if item_id.startswith("cobbled_") else _rock_sprite(family)


def _png_chunk(kind: bytes, payload: bytes) -> bytes:
    return struct.pack(">I", len(payload)) + kind + payload + struct.pack(">I", zlib.crc32(kind + payload) & 0xFFFFFFFF)


def encode_rgba_png(rows: tuple[tuple[RGBA, ...], ...]) -> bytes:
    """Encode equally wide RGBA rows as an 8-bit PNG using filter byte zero."""
    if not rows or not rows[0] or any(len(row) != len(rows[0]) for row in rows):
        raise ValueError("PNG rows must form a non-empty rectangle")
    height, width = len(rows), len(rows[0])
    raw_rows = bytearray()
    for row in rows:
        raw_rows.append(0)
        for pixel in row:
            if len(pixel) != 4 or any(channel not in range(256) for channel in pixel):
                raise ValueError("PNG pixels must be RGBA bytes")
            raw_rows.extend(pixel)
    header = struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0)
    return b"\x89PNG\r\n\x1a\n" + _png_chunk(b"IHDR", header) + _png_chunk(b"IDAT", zlib.compress(bytes(raw_rows))) + _png_chunk(b"IEND", b"")


def _write_item_assets(item_id: str, assets_root: Path) -> None:
    texture = assets_root / "textures" / "item" / f"{item_id}.png"
    model = assets_root / "models" / "item" / f"{item_id}.json"
    definition = assets_root / "items" / f"{item_id}.json"
    local_model = f"material_progression:item/{item_id}"
    texture.parent.mkdir(parents=True, exist_ok=True)
    model.parent.mkdir(parents=True, exist_ok=True)
    definition.parent.mkdir(parents=True, exist_ok=True)
    texture.write_bytes(encode_rgba_png(make_sprite(item_id)))
    model.write_text(
        '{\n  "parent": "minecraft:item/generated",\n  "textures": {"layer0": "' + local_model + '"}\n}\n',
        encoding="utf-8",
    )
    definition.write_text(
        '{\n  "model": {"type": "minecraft:model", "model": "' + local_model + '"}\n}\n',
        encoding="utf-8",
    )


def _write_atlas(item_ids: list[str], destination: Path) -> None:
    columns = 10
    rows = (len(item_ids) + columns - 1) // columns
    pixels = [[TRANSPARENT for _ in range(columns * 16)] for _ in range(rows * 16)]
    for index, item_id in enumerate(item_ids):
        sprite = make_sprite(item_id)
        offset_x, offset_y = (index % columns) * 16, (index // columns) * 16
        for y, row in enumerate(sprite):
            pixels[offset_y + y][offset_x:offset_x + 16] = row
    destination.parent.mkdir(parents=True, exist_ok=True)
    destination.write_bytes(encode_rgba_png(tuple(tuple(row) for row in pixels)))


def write_group(group: str, assets_root: Path) -> None:
    """Write local sprites, generated-item models, and item definitions."""
    try:
        item_ids = sorted(AUTHORED_ITEM_GROUPS[group])
    except KeyError as error:
        raise ValueError(f"Unknown authored item group {group!r}") from error
    for item_id in item_ids:
        _write_item_assets(item_id, assets_root)
    if group == "rocks_and_cobbles":
        _write_atlas(item_ids, ROOT / "build" / "item-art" / "rocks-and-cobbles.png")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--group", required=True, choices=sorted(AUTHORED_ITEM_GROUPS))
    parser.add_argument(
        "--assets-root",
        type=Path,
        default=ROOT / "src" / "main" / "resources" / "assets" / "material_progression",
    )
    arguments = parser.parse_args()
    write_group(arguments.group, arguments.assets_root)


if __name__ == "__main__":
    main()
