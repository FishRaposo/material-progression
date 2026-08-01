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

from content_contracts import (  # noqa: E402
    AUTHORED_FULL_BLOCK_FACE_TEXTURES,
    AUTHORED_FULL_BLOCKS,
    AUTHORED_ITEM_GROUPS,
)


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


def _shade(color: RGBA, numerator: int, denominator: int) -> RGBA:
    return tuple(channel * numerator // denominator for channel in color[:3]) + (255,)


def _tile(core: list[list[RGBA]]) -> tuple[tuple[RGBA, ...], ...]:
    """Repeat a 15px field into a seam-free 16px texture."""
    return tuple(
        tuple(core[y % 15][x % 15] for x in range(16))
        for y in range(16)
    )


def _cobble_block_sprite(family: str) -> tuple[tuple[RGBA, ...], ...]:
    """Render three or four joined stone fragments for a full cube face."""
    shadow, base, highlight, accent = PALETTES[family]
    joint = _shade(shadow, 3, 5)
    fragment_seeds = (
        ((3, 3), (11, 3), (4, 10)),
        ((3, 3), (10, 3), (11, 10), (3, 10)),
        ((2, 4), (8, 2), (12, 8)),
        ((3, 2), (11, 5), (6, 11), (13, 12)),
    )
    seed_group = fragment_seeds[ROCK_SILHOUETTES[family]]
    core: list[list[RGBA]] = []
    for y in range(15):
        row: list[RGBA] = []
        for x in range(15):
            distances = sorted(
                (abs(x - seed_x) + abs(y - seed_y), index)
                for index, (seed_x, seed_y) in enumerate(seed_group)
            )
            nearest, fragment = distances[0]
            if distances[1][0] - nearest <= 1:
                row.append(joint)
            elif (x + y + fragment) % 7 == 0:
                row.append(highlight)
            elif (x * 3 + y + fragment) % 9 == 0:
                row.append(shadow)
            else:
                row.append(accent if accent is not None and (x + y) % 13 == 0 else base)
        core.append(row)
    return _tile(core)


def _ore_block_sprite(host_family: str) -> tuple[tuple[RGBA, ...], ...]:
    shadow, base, highlight, _ = PALETTES[host_family]
    tin_shadow: RGBA = (64, 100, 122, 255)
    tin_base: RGBA = (104, 149, 172, 255)
    tin_highlight: RGBA = (151, 193, 207, 255)
    veins = {(2, 3), (3, 3), (3, 4), (8, 2), (9, 2), (9, 3), (11, 10), (12, 10), (12, 11), (5, 12), (6, 12)}
    core: list[list[RGBA]] = []
    for y in range(15):
        row: list[RGBA] = []
        for x in range(15):
            if (x, y) in veins:
                row.append(tin_highlight if (x + y) % 3 == 0 else tin_base)
            elif (x - 1, y) in veins or (x, y - 1) in veins:
                row.append(tin_shadow)
            elif (x * 5 + y * 3) % 11 == 0:
                row.append(highlight)
            elif (x * 2 + y) % 7 == 0:
                row.append(shadow)
            else:
                row.append(base)
        core.append(row)
    return _tile(core)


def _item_canvas() -> list[list[RGBA]]:
    return [[TRANSPARENT for _ in range(16)] for _ in range(16)]


def _paint(pixels: list[list[RGBA]], color: RGBA, coordinates: set[tuple[int, int]]) -> None:
    for x, y in coordinates:
        if 0 <= x < 16 and 0 <= y < 16:
            pixels[y][x] = color


def _rows(*ranges: tuple[int, int, int]) -> set[tuple[int, int]]:
    return {
        (x, y)
        for y, start, end in ranges
        for x in range(start, end + 1)
    }


def _flint_shard_sprite() -> tuple[tuple[RGBA, ...], ...]:
    pixels = _item_canvas()
    shadow: RGBA = (38, 43, 49, 255)
    base: RGBA = (72, 80, 89, 255)
    highlight: RGBA = (126, 137, 145, 255)
    _paint(pixels, shadow, _rows((3, 7, 8), (4, 6, 9), (5, 5, 10), (6, 5, 10), (7, 4, 10), (8, 4, 9), (9, 5, 8), (10, 5, 7), (11, 6, 6)))
    _paint(pixels, base, _rows((5, 7, 8), (6, 6, 9), (7, 5, 9), (8, 5, 8), (9, 6, 7)))
    _paint(pixels, highlight, {(7, 5), (6, 6), (7, 6), (5, 7)})
    return tuple(tuple(row) for row in pixels)


def _plant_fiber_sprite() -> tuple[tuple[RGBA, ...], ...]:
    pixels = _item_canvas()
    shadow: RGBA = (71, 106, 55, 255)
    base: RGBA = (122, 166, 88, 255)
    highlight: RGBA = (179, 207, 132, 255)
    strands = (
        ((4, 11), (5, 10), (5, 9), (6, 8), (6, 7), (7, 6), (7, 5)),
        ((7, 12), (8, 11), (8, 10), (9, 9), (9, 8), (10, 7), (10, 6)),
        ((9, 12), (10, 11), (10, 10), (11, 9), (11, 8), (12, 7)),
    )
    for strand in strands:
        _paint(pixels, shadow, set(strand))
    _paint(pixels, base, {(5, 10), (6, 8), (7, 6), (8, 11), (9, 9), (10, 7), (10, 11), (11, 9)})
    _paint(pixels, highlight, {(7, 5), (10, 6), (12, 7)})
    return tuple(tuple(row) for row in pixels)


def _dust_sprite(palette: Palette) -> tuple[tuple[RGBA, ...], ...]:
    pixels = _item_canvas()
    shadow, base, highlight, accent = palette
    piles = (
        _rows((7, 3, 5), (8, 2, 6), (9, 2, 6), (10, 3, 5)),
        _rows((8, 7, 9), (9, 6, 10), (10, 6, 10), (11, 7, 9)),
        _rows((10, 10, 12), (11, 9, 13), (12, 9, 13), (13, 10, 12)),
    )
    for pile in piles:
        _paint(pixels, shadow, pile)
    _paint(pixels, base, {(3, 8), (4, 8), (5, 8), (4, 9), (7, 9), (8, 9), (9, 9), (8, 10), (10, 11), (11, 11), (12, 11), (11, 12)})
    _paint(pixels, highlight, {(3, 7), (7, 8), (10, 10)})
    if accent is not None:
        _paint(pixels, accent, {(5, 9), (9, 10), (12, 12)})
    return tuple(tuple(row) for row in pixels)


def _ingot_sprite(palette: Palette) -> tuple[tuple[RGBA, ...], ...]:
    pixels = _item_canvas()
    shadow, base, highlight, accent = palette
    _paint(pixels, shadow, _rows((5, 3, 10), (6, 2, 11), (7, 2, 12), (8, 3, 12), (9, 4, 11), (10, 5, 10), (11, 5, 9)))
    _paint(pixels, base, _rows((6, 4, 10), (7, 3, 11), (8, 4, 11), (9, 5, 10)))
    _paint(pixels, highlight, {(4, 6), (5, 6), (6, 6), (7, 6), (5, 7), (6, 7), (7, 7)})
    if accent is not None:
        _paint(pixels, accent, {(11, 8), (10, 9)})
    return tuple(tuple(row) for row in pixels)


def _raw_tin_sprite() -> tuple[tuple[RGBA, ...], ...]:
    pixels = _item_canvas()
    shadow: RGBA = (54, 79, 91, 255)
    base: RGBA = (95, 137, 151, 255)
    highlight: RGBA = (157, 191, 198, 255)
    clusters = (
        _rows((5, 4, 6), (6, 3, 7), (7, 3, 7), (8, 4, 6)),
        _rows((8, 7, 9), (9, 6, 10), (10, 6, 10), (11, 7, 9)),
        _rows((10, 4, 6), (11, 3, 7), (12, 4, 6)),
    )
    for cluster in clusters:
        _paint(pixels, shadow, cluster)
    _paint(pixels, base, {(4, 6), (5, 6), (6, 6), (5, 7), (7, 9), (8, 9), (9, 9), (8, 10), (4, 11), (5, 11), (6, 11), (5, 12)})
    _paint(pixels, highlight, {(4, 5), (7, 8), (4, 10)})
    return tuple(tuple(row) for row in pixels)


def _ore_item_sprite(host: Palette) -> tuple[tuple[RGBA, ...], ...]:
    pixels = _item_canvas()
    shadow, base, highlight, _ = host
    vein_shadow: RGBA = (54, 88, 108, 255)
    vein_base: RGBA = (95, 142, 166, 255)
    vein_highlight: RGBA = (155, 191, 204, 255)
    _paint(pixels, shadow, _rows((3, 5, 10), (4, 4, 11), (5, 3, 12), (6, 3, 12), (7, 2, 12), (8, 2, 11), (9, 3, 11), (10, 4, 10), (11, 5, 9)))
    _paint(pixels, base, _rows((4, 6, 10), (5, 5, 11), (6, 4, 11), (7, 4, 11), (8, 3, 10), (9, 4, 9), (10, 5, 8)))
    _paint(pixels, highlight, {(5, 5), (6, 5), (4, 6), (4, 7), (9, 4)})
    _paint(pixels, vein_shadow, {(6, 6), (7, 6), (7, 7), (9, 7), (9, 8), (5, 9), (6, 9), (6, 10)})
    _paint(pixels, vein_base, {(6, 5), (7, 5), (8, 6), (8, 7), (9, 6), (5, 8), (5, 10), (6, 8)})
    _paint(pixels, vein_highlight, {(7, 5), (8, 6), (5, 8)})
    return tuple(tuple(row) for row in pixels)


def _crusher_item_sprite() -> tuple[tuple[RGBA, ...], ...]:
    pixels = _item_canvas()
    dark: RGBA = (31, 37, 39, 255)
    stone: RGBA = (67, 76, 77, 255)
    highlight: RGBA = (110, 121, 116, 255)
    aperture: RGBA = (17, 20, 21, 255)
    _paint(pixels, dark, _rows((3, 5, 10), (4, 4, 11), (5, 3, 12), (6, 3, 12), (7, 3, 12), (8, 3, 12), (9, 4, 11), (10, 4, 11), (11, 5, 10), (12, 5, 10)))
    _paint(pixels, stone, _rows((4, 6, 10), (5, 5, 11), (6, 4, 11), (7, 4, 11), (8, 4, 11), (9, 5, 10), (10, 5, 10), (11, 6, 9)))
    _paint(pixels, highlight, {(5, 5), (6, 5), (7, 5), (8, 5), (4, 6), (4, 7)})
    _paint(pixels, aperture, _rows((7, 7, 9), (8, 6, 10), (9, 6, 10), (10, 7, 9)))
    _paint(pixels, dark, {(7, 8), (8, 8), (9, 8)})
    return tuple(tuple(row) for row in pixels)


def _manual_workshop_item_sprite() -> tuple[tuple[RGBA, ...], ...]:
    pixels = _item_canvas()
    dark: RGBA = (49, 31, 21, 255)
    wood: RGBA = (112, 70, 40, 255)
    highlight: RGBA = (171, 113, 65, 255)
    recess: RGBA = (38, 25, 18, 255)
    _paint(pixels, dark, _rows((4, 4, 10), (5, 3, 11), (6, 3, 12), (7, 3, 12), (8, 4, 11), (9, 4, 11), (10, 5, 10), (11, 5, 10), (12, 6, 9)))
    _paint(pixels, wood, _rows((4, 5, 10), (5, 4, 11), (6, 4, 11), (7, 4, 11), (8, 5, 10)))
    _paint(pixels, highlight, {(5, 4), (6, 4), (7, 4), (8, 4), (4, 5), (4, 6)})
    _paint(pixels, recess, _rows((6, 7, 9), (7, 6, 10), (8, 6, 10)))
    _paint(pixels, dark, {(7, 7), (8, 7), (9, 7)})
    return tuple(tuple(row) for row in pixels)


def _crusher_front_sprite() -> tuple[tuple[RGBA, ...], ...]:
    core: list[list[RGBA]] = []
    dressed_dark: RGBA = (37, 42, 45, 255)
    dressed: RGBA = (70, 78, 80, 255)
    dressed_highlight: RGBA = (111, 119, 116, 255)
    aperture: RGBA = (20, 23, 24, 255)
    for y in range(15):
        row: list[RGBA] = []
        for x in range(15):
            if 5 <= x <= 9 and 4 <= y <= 10:
                row.append(aperture if x in (5, 9) or y in (4, 10) else (30, 34, 35, 255))
            elif x in (0, 7, 14) or y in (0, 7, 14):
                row.append(dressed_dark)
            elif (x + y) % 5 == 0:
                row.append(dressed_highlight)
            else:
                row.append(dressed)
        core.append(row)
    return _tile(core)


def _crusher_top_sprite() -> tuple[tuple[RGBA, ...], ...]:
    core: list[list[RGBA]] = []
    dark: RGBA = (33, 38, 40, 255)
    stone: RGBA = (73, 81, 82, 255)
    highlight: RGBA = (112, 120, 117, 255)
    aperture: RGBA = (18, 21, 22, 255)
    for y in range(15):
        row: list[RGBA] = []
        for x in range(15):
            if 4 <= x <= 10 and 6 <= y <= 8:
                row.append(aperture if x in (4, 10) or y in (6, 8) else (27, 31, 32, 255))
            elif x in (0, 7, 14) or y in (0, 7, 14):
                row.append(dark)
            elif (x + y) % 5 == 0:
                row.append(highlight)
            else:
                row.append(stone)
        core.append(row)
    return _tile(core)


def _crusher_side_sprite() -> tuple[tuple[RGBA, ...], ...]:
    core: list[list[RGBA]] = []
    dark: RGBA = (35, 40, 42, 255)
    stone: RGBA = (68, 75, 77, 255)
    highlight: RGBA = (103, 111, 109, 255)
    for y in range(15):
        row: list[RGBA] = []
        for x in range(15):
            if x in (0, 7, 14) or y in (0, 5, 10, 14):
                row.append(dark)
            elif (x * 2 + y) % 7 == 0:
                row.append(highlight)
            else:
                row.append(stone)
        core.append(row)
    return _tile(core)


def _crusher_bottom_sprite() -> tuple[tuple[RGBA, ...], ...]:
    core: list[list[RGBA]] = []
    dark: RGBA = (28, 33, 35, 255)
    stone: RGBA = (50, 58, 61, 255)
    for y in range(15):
        row: list[RGBA] = []
        for x in range(15):
            row.append(dark if x in (0, 5, 10, 14) or y in (0, 5, 10, 14) else stone)
        core.append(row)
    return _tile(core)


def _manual_workshop_top_sprite() -> tuple[tuple[RGBA, ...], ...]:
    core: list[list[RGBA]] = []
    wood_dark: RGBA = (68, 40, 22, 255)
    wood: RGBA = (126, 78, 42, 255)
    wood_highlight: RGBA = (173, 117, 67, 255)
    recess: RGBA = (45, 29, 19, 255)
    for y in range(15):
        row: list[RGBA] = []
        for x in range(15):
            if 5 <= x <= 10 and 5 <= y <= 7:
                row.append(recess if x not in (5, 10) else wood_dark)
            elif y >= 11:
                row.append(wood_dark if (x + y) % 3 else (51, 34, 24, 255))
            elif y in (0, 4, 10) or x in (0, 14):
                row.append(wood_dark)
            elif (x + y) % 6 == 0:
                row.append(wood_highlight)
            else:
                row.append(wood)
        core.append(row)
    return _tile(core)


def _manual_workshop_side_sprite() -> tuple[tuple[RGBA, ...], ...]:
    core: list[list[RGBA]] = []
    dark: RGBA = (56, 34, 21, 255)
    wood: RGBA = (105, 63, 36, 255)
    highlight: RGBA = (146, 93, 53, 255)
    for y in range(15):
        row: list[RGBA] = []
        for x in range(15):
            if y in (0, 3, 8, 13, 14) or x in (0, 14):
                row.append(dark)
            elif (x + y) % 7 == 0:
                row.append(highlight)
            else:
                row.append(wood)
        core.append(row)
    return _tile(core)


def _manual_workshop_bottom_sprite() -> tuple[tuple[RGBA, ...], ...]:
    core: list[list[RGBA]] = []
    dark: RGBA = (45, 29, 20, 255)
    grain: RGBA = (70, 42, 26, 255)
    for y in range(15):
        row: list[RGBA] = []
        for x in range(15):
            row.append(dark if x in (0, 4, 9, 14) or y in (0, 7, 14) else grain)
        core.append(row)
    return _tile(core)


def make_block_sprite(block_id: str) -> tuple[tuple[RGBA, ...], ...]:
    """Return a deterministic, opaque, tileable 16px full-block surface."""
    if block_id.startswith("cobbled_"):
        return _cobble_block_sprite(block_id.removeprefix("cobbled_"))
    if block_id == "tin_ore":
        return _ore_block_sprite("stone")
    if block_id == "deepslate_tin_ore":
        return _ore_block_sprite("deepslate")
    special_sprites = {
        "crusher_front": _crusher_front_sprite,
        "crusher_top": _crusher_top_sprite,
        "crusher_side": _crusher_side_sprite,
        "crusher_bottom": _crusher_bottom_sprite,
        "manual_workshop_top": _manual_workshop_top_sprite,
        "manual_workshop_side": _manual_workshop_side_sprite,
        "manual_workshop_bottom": _manual_workshop_bottom_sprite,
    }
    if block_id in special_sprites:
        return special_sprites[block_id]()
    raise ValueError(f"No block art is defined for {block_id!r}")


def make_sprite(item_id: str) -> tuple[tuple[RGBA, ...], ...]:
    """Return a deterministic 16x16 RGBA sprite for one authored item."""
    material_sprites = {
        "flint_shard": _flint_shard_sprite,
        "plant_fiber": _plant_fiber_sprite,
        "copper_dust": lambda: _dust_sprite(((111, 54, 30, 255), (185, 94, 49, 255), (234, 145, 82, 255), None)),
        "raw_tin": _raw_tin_sprite,
        "tin_dust": lambda: _dust_sprite(((59, 91, 105, 255), (105, 148, 164, 255), (166, 196, 202, 255), None)),
        "tin_ingot": lambda: _ingot_sprite(((63, 96, 111, 255), (109, 151, 167, 255), (174, 204, 211, 255), None)),
        "bronze_dust": lambda: _dust_sprite(((102, 75, 32, 255), (164, 124, 55, 255), (211, 171, 91, 255), None)),
        "bronze_ingot": lambda: _ingot_sprite(((97, 70, 30, 255), (158, 116, 49, 255), (212, 169, 82, 255), None)),
        "tin_ore": lambda: _ore_item_sprite(PALETTES["stone"]),
        "deepslate_tin_ore": lambda: _ore_item_sprite(PALETTES["deepslate"]),
        "crusher": _crusher_item_sprite,
        "manual_workshop": _manual_workshop_item_sprite,
    }
    if item_id in material_sprites:
        return material_sprites[item_id]()
    family = _family_for(item_id)
    return _cobble_sprite(family) if item_id.startswith("cobbled_") else _rock_sprite(family)


def _png_chunk(kind: bytes, payload: bytes) -> bytes:
    return struct.pack(">I", len(payload)) + kind + payload + struct.pack(">I", zlib.crc32(kind + payload) & 0xFFFFFFFF)


def _stored_zlib(payload: bytes) -> bytes:
    """Encode a zlib stream with fixed stored-DEFLATE blocks.

    PNG compression output varies across supported zlib releases.  The sprites
    are tiny, so a compact stream is less valuable than byte-identical output
    from every Python runtime used by the project.
    """
    encoded = bytearray(b"\x78\x01")
    for offset in range(0, len(payload), 0xFFFF):
        block = payload[offset:offset + 0xFFFF]
        encoded.append(1 if offset + len(block) == len(payload) else 0)
        encoded.extend(struct.pack("<H", len(block)))
        encoded.extend(struct.pack("<H", (~len(block)) & 0xFFFF))
        encoded.extend(block)
    encoded.extend(struct.pack(">I", zlib.adler32(payload) & 0xFFFFFFFF))
    return bytes(encoded)


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
    return b"\x89PNG\r\n\x1a\n" + _png_chunk(b"IHDR", header) + _png_chunk(b"IDAT", _stored_zlib(bytes(raw_rows))) + _png_chunk(b"IEND", b"")


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


def _write_block_assets(block_id: str, assets_root: Path) -> None:
    model = assets_root / "models" / "block" / f"{block_id}.json"
    blockstate = assets_root / "blockstates" / f"{block_id}.json"
    local_model = f"material_progression:block/{block_id}"
    model.parent.mkdir(parents=True, exist_ok=True)
    blockstate.parent.mkdir(parents=True, exist_ok=True)
    face_textures = AUTHORED_FULL_BLOCK_FACE_TEXTURES.get(
        block_id, {"all": block_id}
    )
    for texture_id in set(face_textures.values()):
        texture = assets_root / "textures" / "block" / f"{texture_id}.png"
        texture.parent.mkdir(parents=True, exist_ok=True)
        texture.write_bytes(encode_rgba_png(make_block_sprite(texture_id)))
    parent = (
        "minecraft:block/cube"
        if block_id in AUTHORED_FULL_BLOCK_FACE_TEXTURES
        else "minecraft:block/cube_all"
    )
    model.write_text(
        '{\n  "parent": "' + parent + '",\n  "textures": {'
        + ", ".join(
            '"' + face + '": "material_progression:block/' + texture + '"'
            for face, texture in face_textures.items()
        )
        + "}\n}\n",
        encoding="utf-8",
    )
    if block_id == "crusher":
        variants = {
            f"facing={facing},lit={str(lit).lower()}": {
                "model": local_model,
                **({"y": rotation} if rotation else {}),
            }
            for facing, rotation in (("north", 0), ("east", 90), ("south", 180), ("west", 270))
            for lit in (False, True)
        }
    else:
        variants = {"": {"model": local_model}}
    import json
    blockstate.write_text(json.dumps({"variants": variants}, indent=2) + "\n", encoding="utf-8")


def _write_block_atlas(block_ids: list[str], destination: Path) -> None:
    columns = 6
    texture_ids = [
        texture
        for block_id in block_ids
        for texture in sorted(set(AUTHORED_FULL_BLOCK_FACE_TEXTURES.get(
            block_id, {"all": block_id}
        ).values()))
    ]
    rows = (len(texture_ids) + columns - 1) // columns
    pixels = [[TRANSPARENT for _ in range(columns * 16)] for _ in range(rows * 16)]
    for index, texture_id in enumerate(texture_ids):
        sprite = make_block_sprite(texture_id)
        offset_x, offset_y = (index % columns) * 16, (index // columns) * 16
        for y, row in enumerate(sprite):
            pixels[offset_y + y][offset_x:offset_x + 16] = row
    destination.parent.mkdir(parents=True, exist_ok=True)
    destination.write_bytes(encode_rgba_png(tuple(tuple(row) for row in pixels)))


def write_group(group: str, assets_root: Path) -> None:
    """Write local sprites, generated-item models, and item definitions."""
    if group == "full_blocks":
        block_ids = sorted(AUTHORED_FULL_BLOCKS)
        for block_id in block_ids:
            _write_block_assets(block_id, assets_root)
        _write_block_atlas(block_ids, ROOT / "build" / "item-art" / "blocks.png")
        return
    try:
        item_ids = sorted(AUTHORED_ITEM_GROUPS[group])
    except KeyError as error:
        raise ValueError(f"Unknown authored item group {group!r}") from error
    for item_id in item_ids:
        _write_item_assets(item_id, assets_root)
    atlas_names = {
        "rocks_and_cobbles": "rocks-and-cobbles.png",
        "materials_and_workstations": "materials-and-workstations.png",
    }
    if group in atlas_names:
        _write_atlas(item_ids, ROOT / "build" / "item-art" / atlas_names[group])


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--group",
        required=True,
        choices=[*sorted(AUTHORED_ITEM_GROUPS), "full_blocks"],
    )
    parser.add_argument(
        "--assets-root",
        type=Path,
        default=ROOT / "src" / "main" / "resources" / "assets" / "material_progression",
    )
    arguments = parser.parse_args()
    write_group(arguments.group, arguments.assets_root)


if __name__ == "__main__":
    main()
