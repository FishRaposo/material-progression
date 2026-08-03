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

# Every geological family has a deep edge, body, lit plane, and optional vein.
# The values are intentionally authored here rather than sampled from Minecraft
# or a reference mod.  Their restrained value steps keep the collection close
# to vanilla's visual weight without importing any external palette.
PALETTES: dict[str, Palette] = {
    "stone": ((49, 53, 55, 255), (92, 97, 99, 255), (137, 142, 141, 255), None),
    "granite": ((76, 48, 43, 255), (132, 83, 70, 255), (183, 124, 101, 255), None),
    "diorite": ((91, 91, 88, 255), (154, 153, 145, 255), (210, 207, 191, 255), None),
    "andesite": ((55, 60, 61, 255), (96, 102, 102, 255), (143, 148, 145, 255), None),
    "deepslate": ((29, 34, 42, 255), (53, 61, 72, 255), (87, 95, 106, 255), None),
    "tuff": ((61, 72, 59, 255), (104, 119, 96, 255), (151, 161, 130, 255), None),
    "calcite": ((123, 118, 104, 255), (185, 179, 156, 255), (228, 221, 195, 255), None),
    "dripstone": ((79, 53, 38, 255), (134, 89, 62, 255), (187, 134, 93, 255), None),
    "sulfur": ((101, 84, 18, 255), (178, 149, 34, 255), (226, 200, 76, 255), (246, 224, 111, 255)),
    "cinnabar": ((69, 39, 37, 255), (112, 67, 59, 255), (158, 98, 80, 255), (194, 49, 38, 255)),
    "sandstone": ((113, 88, 48, 255), (173, 139, 80, 255), (218, 186, 120, 255), None),
    "red_sandstone": ((105, 50, 31, 255), (165, 79, 45, 255), (211, 119, 67, 255), None),
    "netherrack": ((72, 30, 34, 255), (124, 49, 52, 255), (170, 73, 66, 255), None),
    "basalt": ((37, 40, 41, 255), (68, 72, 72, 255), (108, 111, 106, 255), None),
    "blackstone": ((31, 28, 35, 255), (57, 51, 62, 255), (96, 86, 101, 255), None),
    "end_stone": ((124, 122, 76, 255), (177, 176, 109, 255), (216, 212, 146, 255), None),
    "gravel": ((71, 69, 66, 255), (117, 113, 106, 255), (157, 151, 140, 255), None),
}

# Original material palettes are deliberately small and value-led. They are
# informed by the three local studies' readability lessons, never by copying a
# source palette or pixel arrangement.
METAL_PALETTES: dict[str, Palette] = {
    "copper": ((91, 44, 27, 255), (161, 78, 43, 255), (221, 132, 74, 255), None),
    "tin": ((42, 64, 75, 255), (84, 119, 131, 255), (146, 174, 180, 255), None),
    "bronze": ((76, 49, 19, 255), (137, 89, 33, 255), (194, 139, 62, 255), None),
    "zinc": ((62, 78, 73, 255), (102, 132, 119, 255), (165, 184, 164, 255), None),
    "lead": ((48, 50, 63, 255), (79, 83, 102, 255), (123, 127, 151, 255), None),
    "nickel": ((64, 70, 68, 255), (108, 122, 115, 255), (171, 184, 169, 255), None),
    "silver": ((83, 88, 96, 255), (143, 150, 158, 255), (207, 211, 211, 255), None),
    "steel": ((49, 58, 63, 255), (83, 98, 102, 255), (139, 153, 151, 255), None),
    "brass": ((102, 76, 20, 255), (167, 127, 39, 255), (222, 184, 78, 255), None),
    "invar": ((53, 63, 61, 255), (92, 111, 102, 255), (151, 169, 154, 255), None),
    "rose_gold": ((113, 67, 52, 255), (183, 112, 85, 255), (235, 167, 128, 255), None),
    "sulfur": ((90, 75, 16, 255), (169, 142, 30, 255), (226, 199, 75, 255), None),
    "coal": ((30, 34, 38, 255), (54, 61, 65, 255), (90, 99, 101, 255), None),
    "sulfur_coke": ((38, 36, 31, 255), (68, 63, 49, 255), (117, 104, 55, 255), None),
}

ORE_VEINS: dict[str, Palette] = {
    **{material: palette for material, palette in METAL_PALETTES.items()},
}

# Four deliberately irregular, high-mass chips.  These are original silhouettes
# built around broad planes instead of the earlier small pebble vocabulary.
CHIP_SILHOUETTES: tuple[frozenset[tuple[int, int]], ...] = (
    frozenset((x, y) for y, xs in enumerate(((3, 4, 5, 6), (2, 3, 4, 5, 6, 7), (1, 2, 3, 4, 5, 6, 7, 8), (0, 1, 2, 3, 4, 5, 6, 7, 8), (0, 1, 2, 3, 4, 5, 6, 7, 8), (1, 2, 3, 4, 5, 6, 7, 8), (2, 3, 4, 5, 6, 7), (3, 4, 5, 6))) for x in xs),
    frozenset((x, y) for y, xs in enumerate(((2, 3, 4, 5), (1, 2, 3, 4, 5, 6, 7), (0, 1, 2, 3, 4, 5, 6, 7), (0, 1, 2, 3, 4, 5, 6, 7, 8), (1, 2, 3, 4, 5, 6, 7, 8), (1, 2, 3, 4, 5, 6, 7), (2, 3, 4, 5, 6, 7), (3, 4, 5, 6))) for x in xs),
    frozenset((x, y) for y, xs in enumerate(((4, 5, 6), (2, 3, 4, 5, 6, 7), (1, 2, 3, 4, 5, 6, 7, 8), (1, 2, 3, 4, 5, 6, 7, 8), (0, 1, 2, 3, 4, 5, 6, 7), (0, 1, 2, 3, 4, 5, 6, 7), (1, 2, 3, 4, 5, 6), (3, 4, 5))) for x in xs),
    frozenset((x, y) for y, xs in enumerate(((2, 3, 4, 5, 6), (1, 2, 3, 4, 5, 6, 7), (1, 2, 3, 4, 5, 6, 7, 8), (0, 1, 2, 3, 4, 5, 6, 7, 8), (0, 1, 2, 3, 4, 5, 6, 7), (1, 2, 3, 4, 5, 6, 7), (2, 3, 4, 5, 6), (3, 4, 5))) for x in xs),
)

COBBLE_CHIPS: tuple[frozenset[tuple[int, int]], ...] = (
    frozenset((x, y) for y, xs in enumerate(((2, 3), (1, 2, 3, 4), (0, 1, 2, 3, 4), (0, 1, 2, 3, 4), (1, 2, 3))) for x in xs),
    frozenset((x, y) for y, xs in enumerate(((1, 2, 3), (0, 1, 2, 3, 4), (0, 1, 2, 3, 4), (1, 2, 3, 4), (2, 3))) for x in xs),
    frozenset((x, y) for y, xs in enumerate(((2, 3), (0, 1, 2, 3), (0, 1, 2, 3, 4), (1, 2, 3, 4), (2, 3, 4))) for x in xs),
    frozenset((x, y) for y, xs in enumerate(((1, 2), (0, 1, 2, 3), (0, 1, 2, 3, 4), (0, 1, 2, 3, 4), (1, 2, 3))) for x in xs),
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
    plane_shift: int = 0,
) -> None:
    shadow, base, highlight, accent = palette
    offset_x, offset_y = origin
    for x, y in silhouette:
        destination_x, destination_y = offset_x + x, offset_y + y
        if not (0 <= destination_x < 16 and 0 <= destination_y < 16):
            continue
        if (x, y + 1) not in silhouette or (x + 1, y) not in silhouette:
            color = shadow
        elif (x, y - 1) not in silhouette or (x - 1, y) not in silhouette:
            color = highlight
        elif (x * 2 + y + plane_shift) % 7 in (0, 1):
            color = highlight if x + y < 9 else base
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
    _paint_chip(
        pixels,
        silhouette,
        (3, 4),
        PALETTES[family],
        accent_budget=4,
        plane_shift=ROCK_SILHOUETTES[family],
    )
    return tuple(tuple(row) for row in pixels)


def _cobble_sprite(family: str) -> tuple[tuple[RGBA, ...], ...]:
    pixels = [[TRANSPARENT for _ in range(16)] for _ in range(16)]
    palette = PALETTES[family]
    index = ROCK_SILHOUETTES[family]
    _paint_chip(pixels, COBBLE_CHIPS[index], (2, 8), palette, accent_budget=2)
    _paint_chip(pixels, COBBLE_CHIPS[(index + 1) % 4], (7, 3), palette, accent_budget=1, plane_shift=2)
    _paint_chip(pixels, COBBLE_CHIPS[(index + 2) % 4], (9, 9), palette, accent_budget=1, plane_shift=4)
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
    """Render broad, mortared fragments as an original 15px repeating field."""
    shadow, base, highlight, accent = PALETTES[family]
    joint = _shade(shadow, 2, 3)
    fragment_seeds = (
        ((1, 2), (7, 1), (12, 4), (3, 9), (9, 8), (13, 12), (6, 13)),
        ((3, 1), (10, 2), (14, 6), (5, 7), (10, 10), (2, 13), (7, 14)),
        ((0, 5), (5, 2), (11, 1), (9, 6), (3, 10), (13, 11), (7, 13)),
        ((2, 2), (8, 0), (13, 4), (5, 6), (11, 9), (1, 12), (7, 13)),
    )
    seed_group = fragment_seeds[ROCK_SILHOUETTES[family]]
    owners: list[list[int]] = []
    for y in range(15):
        owner_row: list[int] = []
        for x in range(15):
            owner_row.append(min(
                range(len(seed_group)),
                key=lambda index: (
                    min(abs(x - seed_group[index][0]), 15 - abs(x - seed_group[index][0]))
                    + min(abs(y - seed_group[index][1]), 15 - abs(y - seed_group[index][1])),
                    index,
                ),
            ))
        owners.append(owner_row)

    core: list[list[RGBA]] = []
    for y in range(15):
        row: list[RGBA] = []
        for x in range(15):
            fragment = owners[y][x]
            neighbours = (
                owners[y][(x - 1) % 15],
                owners[y][(x + 1) % 15],
                owners[(y - 1) % 15][x],
                owners[(y + 1) % 15][x],
            )
            if any(neighbour != fragment for neighbour in neighbours):
                row.append(joint)
            else:
                seed_x, seed_y = seed_group[fragment]
                delta_x = x - seed_x
                delta_y = y - seed_y
                if delta_x > 7:
                    delta_x -= 15
                elif delta_x < -7:
                    delta_x += 15
                if delta_y > 7:
                    delta_y -= 15
                elif delta_y < -7:
                    delta_y += 15
                if delta_x + delta_y <= -2:
                    row.append(highlight)
                elif delta_x + delta_y >= 4:
                    row.append(shadow)
                elif accent is not None and (x * 5 + y * 3 + fragment) % 47 == 0:
                    row.append(accent)
                else:
                    row.append(base)
        core.append(row)
    return _tile(core)


def _ore_block_sprite(host_family: str, material: str = "tin") -> tuple[tuple[RGBA, ...], ...]:
    shadow, base, highlight, _ = PALETTES[host_family]
    vein_shadow, vein_base, vein_highlight, _ = ORE_VEINS[material]
    veins = {
        (2, 2), (3, 2), (2, 3), (3, 3), (4, 3), (3, 4),
        (9, 1), (10, 1), (9, 2), (10, 2), (11, 2), (10, 3),
        (11, 9), (12, 9), (13, 9), (11, 10), (12, 10), (12, 11),
        (4, 11), (5, 11), (4, 12), (5, 12), (6, 12), (5, 13),
    }
    core: list[list[RGBA]] = []
    for y in range(15):
        row: list[RGBA] = []
        for x in range(15):
            if (x, y) in veins:
                row.append(vein_highlight if (x + y) % 4 in (0, 1) else vein_base)
            elif (
                ((x - 1) % 15, y) in veins
                or (x, (y - 1) % 15) in veins
                or ((x + 1) % 15, y) in veins
            ):
                row.append(vein_shadow)
            elif (x // 4 + y // 3) % 5 == 0:
                row.append(highlight)
            elif (x // 3 + y // 4) % 4 == 0:
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


# Each tool uses a broad, role-specific working mass.  Its material variants
# share this exact alpha silhouette: only the working palette changes.
TOOL_SILHOUETTES: dict[str, dict[str, frozenset[tuple[int, int]]]] = {
    "axe": {
        "head": frozenset(_rows((1, 5, 8), (2, 2, 9), (3, 1, 9), (4, 1, 9), (5, 1, 9), (6, 2, 9), (7, 6, 9), (8, 8, 10))),
        "binding": frozenset(),
    },
    "hatchet": {
        "head": frozenset(_rows((1, 6, 8), (2, 5, 8), (3, 3, 9), (4, 2, 9), (5, 2, 9), (6, 4, 9), (7, 6, 9), (8, 8, 10))),
        "binding": frozenset(),
    },
    "hammer": {
        "head": frozenset(_rows((2, 2, 11), (3, 1, 12), (4, 1, 12), (5, 2, 11), (6, 6, 10), (7, 7, 10), (8, 7, 10))),
        "binding": frozenset(),
    },
    "knife": {
        "head": frozenset(_rows((1, 8, 8), (2, 7, 9), (3, 6, 10), (4, 4, 10), (5, 4, 10), (6, 5, 10), (7, 8, 10), (8, 8, 10))),
        "binding": frozenset(_rows((9, 6, 11))),
    },
    "pickaxe": {
        "head": frozenset(_rows((1, 3, 11), (2, 1, 13), (3, 1, 13), (4, 2, 12), (5, 4, 11), (6, 6, 10), (7, 8, 10), (8, 8, 10))),
        "binding": frozenset(),
    },
    "saw": {
        "head": frozenset(_rows((2, 2, 9), (3, 2, 10), (4, 2, 11), (5, 2, 11), (6, 4, 11), (7, 6, 11), (8, 9, 11)) | {(2, 6), (4, 7), (6, 8)}),
        "binding": frozenset(),
    },
    "hoe": {
        "head": frozenset(_rows((2, 4, 10), (3, 3, 11), (4, 4, 11), (5, 8, 11), (6, 8, 10), (7, 8, 10), (8, 8, 10))),
        "binding": frozenset(),
    },
    "shovel": {
        "head": frozenset(_rows((1, 5, 7), (2, 3, 9), (3, 2, 10), (4, 2, 10), (5, 3, 10), (6, 4, 10), (7, 6, 10), (8, 8, 10))),
        "binding": frozenset(),
    },
    "sword": {
        "head": frozenset(_rows((1, 6, 9), (2, 4, 9), (3, 4, 9), (4, 4, 9), (5, 5, 10), (6, 6, 10), (7, 8, 10), (8, 9, 10))),
        "binding": frozenset(_rows((9, 5, 12))),
    },
}

TOOL_MATERIALS: dict[str, Palette] = {
    "flint": ((27, 31, 35, 255), (58, 65, 70, 255), (105, 115, 119, 255), (143, 149, 146, 255)),
    "wood": ((49, 28, 16, 255), (116, 68, 34, 255), (177, 135, 76, 255), None),
    "stone": PALETTES["stone"],
    "copper": METAL_PALETTES["copper"],
    "tin": ((43, 67, 78, 255), (85, 121, 133, 255), (144, 174, 181, 255), (190, 207, 207, 255)),
    "bronze": ((78, 51, 20, 255), (139, 91, 34, 255), (196, 141, 65, 255), (226, 177, 91, 255)),
    "zinc": METAL_PALETTES["zinc"],
    "lead": METAL_PALETTES["lead"],
    "nickel": METAL_PALETTES["nickel"],
    "silver": METAL_PALETTES["silver"],
    "steel": METAL_PALETTES["steel"],
    "brass": METAL_PALETTES["brass"],
    "invar": METAL_PALETTES["invar"],
    "rose_gold": METAL_PALETTES["rose_gold"],
}

_TOOL_HANDLE_SHADOW = frozenset({
    *_rows((8, 8, 10), (9, 8, 11), (10, 9, 12), (11, 9, 13),
           (12, 10, 14), (13, 11, 14), (14, 12, 14)),
})
_TOOL_HANDLE_BASE = frozenset({
    *_rows((9, 9, 10), (10, 10, 11), (11, 10, 12), (12, 11, 13),
           (13, 12, 13), (14, 13, 13)),
})
_TOOL_BINDING = frozenset(_rows((8, 8, 10), (9, 8, 11)))


def _tool_sprite(role: str, material: str) -> tuple[tuple[RGBA, ...], ...]:
    pixels = _item_canvas()
    head_shadow, head_base, head_highlight, head_accent = TOOL_MATERIALS[material]
    silhouette = TOOL_SILHOUETTES[role]
    head = silhouette["head"]
    _paint(pixels, head_shadow, set(head))
    interior = {
        (x, y)
        for x, y in head
        if (x + 1, y) in head and (x, y + 1) in head
    }
    lit_plane = {
        (x, y)
        for x, y in interior
        if (x - 1, y) not in interior or (x, y - 1) not in interior
    }
    _paint(pixels, head_base, interior)
    _paint(pixels, head_highlight, lit_plane)
    if head_accent is not None:
        accent_pixels = set(sorted(interior - lit_plane, key=lambda point: (point[1], point[0]))[:3])
        _paint(pixels, head_accent, accent_pixels)
    _paint(pixels, (49, 28, 16, 255), set(_TOOL_HANDLE_SHADOW))
    _paint(pixels, (116, 68, 34, 255), set(_TOOL_HANDLE_BASE))
    _paint(pixels, (177, 135, 76, 255), set(_TOOL_BINDING | silhouette["binding"]))
    return tuple(tuple(row) for row in pixels)


def _flint_shard_sprite() -> tuple[tuple[RGBA, ...], ...]:
    pixels = _item_canvas()
    shadow: RGBA = (28, 32, 36, 255)
    base: RGBA = (62, 69, 74, 255)
    highlight: RGBA = (116, 126, 129, 255)
    edge: RGBA = (155, 160, 155, 255)
    shard = _rows((2, 9, 9), (3, 7, 10), (4, 6, 11), (5, 5, 11),
                  (6, 4, 11), (7, 3, 10), (8, 3, 10), (9, 4, 9),
                  (10, 5, 8), (11, 6, 7), (12, 7, 7))
    _paint(pixels, shadow, shard)
    _paint(pixels, base, _rows((4, 8, 9), (5, 7, 10), (6, 6, 10),
                               (7, 5, 9), (8, 5, 8), (9, 6, 7)))
    _paint(pixels, highlight, _rows((3, 8, 9), (4, 7, 8), (5, 6, 7),
                                    (6, 5, 6), (7, 4, 5)))
    _paint(pixels, edge, {(9, 2), (10, 3), (11, 4), (4, 7), (3, 8)})
    return tuple(tuple(row) for row in pixels)


def _plant_fiber_sprite() -> tuple[tuple[RGBA, ...], ...]:
    pixels = _item_canvas()
    shadow: RGBA = (55, 82, 39, 255)
    base: RGBA = (101, 139, 67, 255)
    highlight: RGBA = (159, 183, 107, 255)
    dry: RGBA = (174, 142, 75, 255)
    _paint(pixels, shadow, _rows((7, 5, 11), (8, 4, 12), (9, 4, 12),
                                 (10, 3, 12), (11, 4, 11), (12, 5, 10),
                                 (13, 6, 9)))
    _paint(pixels, base, {
        (5, 8), (6, 7), (7, 6), (8, 5), (9, 4), (10, 3),
        (8, 7), (9, 6), (10, 5), (11, 4), (12, 3),
        (9, 8), (10, 7), (11, 6), (12, 5), (13, 5),
        (5, 9), (6, 9), (7, 9), (8, 9), (9, 9), (10, 9), (11, 9),
        (5, 10), (6, 10), (7, 10), (8, 10), (9, 10), (10, 10),
    })
    _paint(pixels, highlight, {(10, 3), (12, 3), (8, 5), (11, 6), (5, 8), (5, 9)})
    _paint(pixels, dry, _rows((10, 5, 10), (11, 6, 9)))
    return tuple(tuple(row) for row in pixels)


def _dust_sprite(palette: Palette) -> tuple[tuple[RGBA, ...], ...]:
    pixels = _item_canvas()
    shadow, base, highlight, accent = palette
    piles = (
        _rows((7, 4, 5), (8, 3, 6), (9, 2, 7), (10, 2, 7), (11, 3, 6)),
        _rows((5, 8, 9), (6, 7, 10), (7, 6, 11), (8, 6, 11),
              (9, 6, 11), (10, 7, 10), (11, 7, 10), (12, 8, 9)),
        _rows((8, 11, 12), (9, 10, 13), (10, 10, 13),
              (11, 10, 13), (12, 11, 12)),
    )
    for pile in piles:
        _paint(pixels, shadow, pile)
    _paint(pixels, base, _rows((8, 4, 5), (9, 3, 6), (6, 8, 9),
                               (7, 7, 10), (8, 7, 10), (9, 8, 10),
                               (10, 3, 6), (10, 11, 12), (11, 11, 12)))
    _paint(pixels, highlight, {(4, 7), (8, 5), (7, 6), (11, 8), (3, 9), (10, 9)})
    if accent is not None:
        _paint(pixels, accent, {(5, 9), (9, 8), (12, 10)})
    return tuple(tuple(row) for row in pixels)


def _ingot_sprite(palette: Palette) -> tuple[tuple[RGBA, ...], ...]:
    pixels = _item_canvas()
    shadow, base, highlight, accent = palette
    _paint(pixels, shadow, _rows((4, 3, 12), (5, 2, 13), (6, 2, 13),
                                 (7, 2, 13), (8, 3, 13), (9, 3, 12),
                                 (10, 4, 11)))
    _paint(pixels, base, _rows((6, 3, 12), (7, 3, 12), (8, 4, 12),
                               (9, 4, 11)))
    _paint(pixels, highlight, _rows((4, 4, 11), (5, 3, 12), (6, 4, 9)))
    if accent is not None:
        _paint(pixels, accent, {(11, 5), (12, 6), (10, 7)})
    return tuple(tuple(row) for row in pixels)


def _raw_tin_sprite() -> tuple[tuple[RGBA, ...], ...]:
    pixels = _item_canvas()
    shadow: RGBA = (42, 65, 76, 255)
    base: RGBA = (85, 123, 135, 255)
    highlight: RGBA = (145, 174, 180, 255)
    glint: RGBA = (192, 205, 202, 255)
    clusters = (
        _rows((4, 5, 7), (5, 4, 8), (6, 3, 8), (7, 3, 8), (8, 4, 7)),
        _rows((6, 9, 11), (7, 8, 12), (8, 8, 12), (9, 7, 12),
              (10, 7, 11), (11, 8, 10)),
        _rows((9, 4, 6), (10, 3, 7), (11, 3, 7), (12, 4, 6)),
    )
    for cluster in clusters:
        _paint(pixels, shadow, cluster)
    _paint(pixels, base, _rows((5, 5, 7), (6, 4, 7), (7, 9, 11),
                               (8, 9, 11), (9, 8, 11), (10, 4, 6),
                               (11, 4, 6)))
    _paint(pixels, highlight, {(5, 4), (4, 5), (9, 6), (8, 7), (4, 9), (3, 10)})
    _paint(pixels, glint, {(6, 4), (10, 6), (4, 10)})
    return tuple(tuple(row) for row in pixels)


def _ore_item_sprite(
    host: Palette,
    vein: Palette = ((43, 70, 83, 255), (86, 127, 141, 255), (148, 177, 183, 255), None),
) -> tuple[tuple[RGBA, ...], ...]:
    pixels = _item_canvas()
    shadow, base, highlight, _ = host
    vein_shadow, vein_base, vein_highlight, _ = vein
    _paint(pixels, shadow, _rows((2, 5, 10), (3, 3, 12), (4, 2, 13),
                                 (5, 2, 13), (6, 1, 13), (7, 1, 14),
                                 (8, 2, 14), (9, 2, 13), (10, 3, 12),
                                 (11, 4, 11), (12, 6, 9)))
    _paint(pixels, base, _rows((3, 6, 10), (4, 4, 11), (5, 3, 12),
                               (6, 3, 12), (7, 3, 12), (8, 3, 12),
                               (9, 4, 11), (10, 5, 10)))
    _paint(pixels, highlight, _rows((3, 6, 9), (4, 4, 7), (5, 3, 5)))
    _paint(pixels, vein_shadow, {(7, 4), (8, 4), (8, 5), (9, 5), (10, 5),
                                 (5, 7), (6, 7), (6, 8), (10, 8), (11, 8),
                                 (9, 9), (10, 9), (9, 10)})
    _paint(pixels, vein_base, {(7, 3), (8, 3), (9, 4), (10, 4), (5, 6),
                               (6, 6), (5, 8), (10, 7), (11, 7), (9, 8),
                               (8, 9), (9, 9)})
    _paint(pixels, vein_highlight, {(8, 3), (5, 6), (10, 7), (8, 9)})
    return tuple(tuple(row) for row in pixels)


def _crusher_item_sprite() -> tuple[tuple[RGBA, ...], ...]:
    pixels = _item_canvas()
    dark: RGBA = (27, 31, 32, 255)
    stone: RGBA = (63, 69, 68, 255)
    highlight: RGBA = (106, 111, 105, 255)
    metal: RGBA = (120, 87, 42, 255)
    aperture: RGBA = (14, 16, 17, 255)
    cube = _rows((2, 6, 9), (3, 4, 11), (4, 2, 13), (5, 2, 13),
                 (6, 2, 13), (7, 2, 13), (8, 2, 13), (9, 2, 13),
                 (10, 3, 12), (11, 3, 12), (12, 4, 11), (13, 6, 9))
    _paint(pixels, dark, cube)
    _paint(pixels, stone, _rows((4, 4, 11), (5, 3, 12), (6, 3, 12),
                                (7, 3, 12), (8, 3, 12), (9, 3, 12),
                                (10, 4, 11), (11, 4, 11), (12, 6, 9)))
    _paint(pixels, highlight, _rows((3, 6, 9), (4, 4, 10), (5, 3, 5)))
    _paint(pixels, metal, _rows((5, 11, 12), (6, 11, 12), (7, 11, 12),
                                (8, 11, 12), (9, 11, 12), (10, 10, 11)))
    _paint(pixels, aperture, _rows((6, 5, 9), (7, 4, 10), (8, 4, 10),
                                   (9, 5, 9)))
    _paint(pixels, dark, _rows((7, 6, 8), (8, 6, 8)))
    return tuple(tuple(row) for row in pixels)


def _manual_workshop_item_sprite() -> tuple[tuple[RGBA, ...], ...]:
    pixels = _item_canvas()
    dark: RGBA = (43, 25, 15, 255)
    wood: RGBA = (102, 59, 30, 255)
    highlight: RGBA = (157, 99, 53, 255)
    metal: RGBA = (111, 119, 113, 255)
    recess: RGBA = (29, 19, 14, 255)
    _paint(pixels, dark, _rows((3, 5, 10), (4, 3, 12), (5, 2, 13),
                               (6, 2, 13), (7, 2, 13), (8, 3, 12),
                               (9, 3, 12), (10, 3, 12), (11, 4, 11),
                               (12, 4, 6), (12, 9, 11), (13, 5, 6),
                               (13, 9, 10)))
    _paint(pixels, wood, _rows((4, 5, 10), (5, 3, 12), (6, 3, 12),
                               (7, 3, 12), (8, 4, 11), (9, 4, 11),
                               (10, 4, 11), (11, 5, 10),
                               (12, 5, 6), (12, 9, 10)))
    _paint(pixels, highlight, _rows((3, 6, 9), (4, 4, 10), (5, 3, 5)))
    _paint(pixels, recess, _rows((5, 7, 10), (6, 6, 10), (7, 7, 9)))
    _paint(pixels, metal, {(7, 5), (8, 5), (9, 5), (10, 6), (9, 7)})
    _paint(pixels, dark, {(5, 9), (6, 9), (9, 9), (10, 9)})
    return tuple(tuple(row) for row in pixels)


def _crusher_front_sprite() -> tuple[tuple[RGBA, ...], ...]:
    core = [list(row[:15]) for row in _cobble_block_sprite("stone")[:15]]
    frame: RGBA = (31, 35, 36, 255)
    frame_light: RGBA = (91, 98, 95, 255)
    aperture: RGBA = (13, 15, 16, 255)
    jaw: RGBA = (61, 68, 68, 255)
    brass: RGBA = (131, 91, 38, 255)
    for y in range(3, 12):
        for x in range(3, 12):
            if x in (3, 11) or y in (3, 11):
                core[y][x] = frame_light if y == 3 or x == 3 else frame
            elif 5 <= x <= 9 and 5 <= y <= 9:
                core[y][x] = aperture
            else:
                core[y][x] = frame
    for coordinate in {(5, 6), (6, 6), (8, 6), (9, 6), (6, 8), (7, 8), (8, 8)}:
        core[coordinate[1]][coordinate[0]] = jaw
    for coordinate in {(4, 4), (10, 4), (4, 10), (10, 10)}:
        core[coordinate[1]][coordinate[0]] = brass
    return _tile(core)


def _crusher_top_sprite() -> tuple[tuple[RGBA, ...], ...]:
    core = [list(row[:15]) for row in _cobble_block_sprite("stone")[:15]]
    rim_dark: RGBA = (31, 35, 36, 255)
    rim_light: RGBA = (103, 109, 104, 255)
    throat: RGBA = (13, 15, 16, 255)
    for y, start, end in ((3, 5, 9), (4, 3, 11), (5, 2, 12),
                          (6, 2, 12), (7, 2, 12), (8, 3, 11),
                          (9, 5, 9)):
        for x in range(start, end + 1):
            core[y][x] = rim_dark
    for y, start, end in ((4, 5, 9), (5, 4, 10), (6, 4, 10),
                          (7, 4, 10), (8, 5, 9)):
        for x in range(start, end + 1):
            core[y][x] = throat
    for x, y in {(5, 3), (6, 3), (7, 3), (8, 3), (9, 3),
                 (3, 4), (4, 4), (2, 5), (2, 6)}:
        core[y][x] = rim_light
    return _tile(core)


def _crusher_side_sprite() -> tuple[tuple[RGBA, ...], ...]:
    core = [list(row[:15]) for row in _cobble_block_sprite("stone")[:15]]
    iron: RGBA = (39, 44, 45, 255)
    iron_light: RGBA = (96, 103, 101, 255)
    bronze: RGBA = (139, 92, 37, 255)
    bronze_light: RGBA = (194, 140, 66, 255)
    for y in range(15):
        for x in (2, 3, 11, 12):
            core[y][x] = iron_light if x in (2, 11) else iron
    for x in range(15):
        for y in (3, 4, 10, 11):
            core[y][x] = iron_light if y in (3, 10) else iron
    for y, start, end in ((5, 6, 8), (6, 5, 9), (7, 5, 9),
                          (8, 5, 9), (9, 6, 8)):
        for x in range(start, end + 1):
            core[y][x] = bronze
    for coordinate in {(7, 5), (6, 6), (8, 6), (5, 7), (9, 7), (7, 9)}:
        core[coordinate[1]][coordinate[0]] = bronze_light
    core[7][7] = iron
    return _tile(core)


def _crusher_bottom_sprite() -> tuple[tuple[RGBA, ...], ...]:
    core = [list(row[:15]) for row in _cobble_block_sprite("deepslate")[:15]]
    rail: RGBA = (25, 29, 31, 255)
    rail_light: RGBA = (74, 80, 79, 255)
    for index in range(15):
        core[2][index] = rail_light
        core[3][index] = rail
        core[11][index] = rail_light
        core[12][index] = rail
        core[index][2] = rail_light
        core[index][3] = rail
        core[index][11] = rail_light
        core[index][12] = rail
    return _tile(core)


def _manual_workshop_top_sprite() -> tuple[tuple[RGBA, ...], ...]:
    core: list[list[RGBA]] = []
    wood_dark: RGBA = (58, 33, 18, 255)
    wood: RGBA = (111, 64, 32, 255)
    wood_highlight: RGBA = (164, 103, 55, 255)
    recess: RGBA = (32, 21, 15, 255)
    metal: RGBA = (103, 111, 107, 255)
    for y in range(15):
        row: list[RGBA] = []
        for x in range(15):
            if y in (0, 5, 10, 14):
                row.append(wood_dark)
            elif (x + y * 2) % 11 in (0, 1):
                row.append(wood_highlight)
            else:
                row.append(wood)
        core.append(row)
    for y in range(4, 9):
        for x in range(4, 11):
            if x in (4, 10) or y in (4, 8):
                core[y][x] = wood_dark
            else:
                core[y][x] = recess
    for coordinate in {(5, 4), (6, 4), (7, 4), (8, 4), (9, 4),
                       (6, 6), (7, 6), (8, 6), (9, 7)}:
        core[coordinate[1]][coordinate[0]] = metal
    for coordinate in {(2, 2), (12, 2), (2, 12), (12, 12)}:
        core[coordinate[1]][coordinate[0]] = metal
    return _tile(core)


def _manual_workshop_side_sprite() -> tuple[tuple[RGBA, ...], ...]:
    core: list[list[RGBA]] = []
    dark: RGBA = (50, 29, 17, 255)
    wood: RGBA = (97, 55, 29, 255)
    highlight: RGBA = (147, 89, 46, 255)
    iron: RGBA = (92, 99, 96, 255)
    for y in range(15):
        row: list[RGBA] = []
        for x in range(15):
            if y in (0, 3, 4, 13, 14) or x in (0, 3, 11, 14):
                row.append(dark)
            elif (x * 2 + y) % 13 in (0, 1):
                row.append(highlight)
            else:
                row.append(wood)
        core.append(row)
    for coordinate in {(2, 2), (12, 2), (2, 12), (12, 12),
                       (5, 8), (6, 8), (7, 8), (8, 8), (9, 8)}:
        core[coordinate[1]][coordinate[0]] = iron
    for x, y in {(4, 11), (5, 10), (6, 9), (8, 7), (9, 6), (10, 5)}:
        core[y][x] = highlight
    return _tile(core)


def _manual_workshop_bottom_sprite() -> tuple[tuple[RGBA, ...], ...]:
    core: list[list[RGBA]] = []
    dark: RGBA = (42, 25, 15, 255)
    grain: RGBA = (76, 42, 23, 255)
    grain_light: RGBA = (112, 64, 34, 255)
    for y in range(15):
        row: list[RGBA] = []
        for x in range(15):
            if x in (0, 5, 10, 14) or y in (0, 7, 14):
                row.append(dark)
            elif (x + y * 3) % 17 in (0, 1):
                row.append(grain_light)
            else:
                row.append(grain)
        core.append(row)
    return _tile(core)


def _material_from_prefixed_id(item_id: str, suffix: str) -> str | None:
    if not item_id.endswith(suffix):
        return None
    material = item_id.removesuffix(suffix)
    return material if material in METAL_PALETTES else None


def _raw_sprite(palette: Palette) -> tuple[tuple[RGBA, ...], ...]:
    pixels = _item_canvas()
    shadow, base, highlight, accent = palette
    for silhouette, origin in zip(COBBLE_CHIPS[:3], ((2, 5), (7, 8), (9, 3))):
        _paint_chip(pixels, silhouette, origin, (shadow, base, highlight, accent), accent_budget=1)
    return tuple(tuple(row) for row in pixels)


def _armor_sprite(role: str, palette: Palette) -> tuple[tuple[RGBA, ...], ...]:
    pixels = _item_canvas()
    shadow, base, highlight, _ = palette
    shapes = {
        "helmet": _rows((2, 5, 10), (3, 3, 12), (4, 2, 13), (5, 2, 13), (6, 2, 13), (7, 3, 12), (8, 4, 11), (9, 5, 10), (10, 5, 10), (11, 5, 5)),
        "chestplate": _rows((2, 4, 11), (3, 3, 12), (4, 2, 13), (5, 3, 12), (6, 3, 12), (7, 3, 12), (8, 3, 12), (9, 3, 12), (10, 4, 11), (11, 4, 11), (12, 5, 10)),
        "leggings": _rows((2, 3, 12), (3, 3, 12), (4, 4, 11), (5, 4, 11), (6, 5, 10), (7, 5, 10), (8, 5, 10), (9, 5, 10), (10, 4, 11), (11, 3, 7), (11, 9, 12), (12, 3, 6), (12, 10, 13), (13, 3, 5), (13, 11, 14)),
        "boots": _rows((4, 4, 8), (5, 4, 8), (6, 4, 8), (7, 4, 8), (8, 4, 8), (9, 3, 9), (10, 2, 10), (11, 2, 11), (12, 3, 12)),
    }
    shape = shapes[role]
    _paint(pixels, shadow, shape)
    interior = {(x, y) for x, y in shape if (x + 1, y) in shape and (x, y + 1) in shape}
    _paint(pixels, base, interior)
    _paint(pixels, highlight, {(x, y) for x, y in interior if (x - 1, y) not in interior or (x, y - 1) not in interior})
    return tuple(tuple(row) for row in pixels)


def _ground_stick_sprite() -> tuple[tuple[RGBA, ...], ...]:
    pixels = [[(0, 0, 0, 0) for _ in range(16)] for _ in range(16)]
    for offset, color in ((0, (51, 30, 17, 255)), (1, (111, 64, 32, 255)), (2, (165, 105, 56, 255))):
        for index in range(2, 14):
            x, y = index, 13 - index // 2 + offset
            if 0 <= x < 16 and 0 <= y < 16:
                pixels[y][x] = color
    return tuple(tuple(row) for row in pixels)


def _loose_rock_world_sprite(family: str) -> tuple[tuple[RGBA, ...], ...]:
    pixels = [[TRANSPARENT for _ in range(16)] for _ in range(16)]
    _paint_chip(pixels, COBBLE_CHIPS[ROCK_SILHOUETTES[family]], (5, 8), PALETTES[family], accent_budget=2)
    return tuple(tuple(row) for row in pixels)


def _armor_layer(palette: Palette, leggings: bool) -> tuple[tuple[RGBA, ...], ...]:
    """Original 64x32 wearable layer, deliberately separate from inventory art."""
    shadow, base, highlight, _ = palette
    pixels = [[TRANSPARENT for _ in range(64)] for _ in range(32)]
    regions = ((4, 1, 27, 17), (28, 1, 51, 17), (4, 18, 19, 31), (20, 18, 35, 31))
    if leggings:
        regions = ((4, 2, 19, 25), (20, 2, 35, 25), (36, 2, 51, 25), (52, 2, 63, 25))
    for left, top, right, bottom in regions:
        for y in range(top, bottom + 1):
            for x in range(left, right + 1):
                edge = x in {left, right} or y in {top, bottom}
                pixels[y][x] = shadow if edge else base
                if not edge and (x + y) % 13 == 0:
                    pixels[y][x] = highlight
    return tuple(tuple(row) for row in pixels)


def _write_armor_layers(assets_root: Path) -> None:
    texture_root = assets_root / "textures" / "entity" / "equipment"
    for material in TOOL_MATERIALS:
        (texture_root / "humanoid").mkdir(parents=True, exist_ok=True)
        (texture_root / "humanoid_leggings").mkdir(parents=True, exist_ok=True)
        (texture_root / "humanoid" / f"{material}.png").write_bytes(
            encode_rgba_png(_armor_layer(TOOL_MATERIALS[material], False))
        )
        (texture_root / "humanoid_leggings" / f"{material}.png").write_bytes(
            encode_rgba_png(_armor_layer(TOOL_MATERIALS[material], True))
        )


def make_block_sprite(block_id: str) -> tuple[tuple[RGBA, ...], ...]:
    """Return a deterministic, opaque, tileable 16px full-block surface."""
    if block_id.startswith("cobbled_"):
        return _cobble_block_sprite(block_id.removeprefix("cobbled_"))
    if block_id.startswith("gravel_") and block_id.endswith("_ore"):
        material = block_id.removeprefix("gravel_").removesuffix("_ore")
        return _ore_block_sprite("gravel", material)
    if block_id.endswith("_ore"):
        material = next((candidate for candidate in sorted(ORE_VEINS, key=len, reverse=True)
                         if block_id == f"{candidate}_ore" or block_id.endswith(f"_{candidate}_ore")), None)
        if material is not None:
            prefix = "" if block_id == f"{material}_ore" else block_id.removesuffix(f"_{material}_ore")
            host = "stone" if not prefix else prefix
            return _ore_block_sprite(host, material)
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
        "copper_dust": lambda: _dust_sprite(((88, 41, 23, 255), (159, 75, 38, 255), (218, 126, 67, 255), None)),
        "raw_tin": _raw_tin_sprite,
        "tin_dust": lambda: _dust_sprite(((45, 70, 81, 255), (88, 126, 138, 255), (148, 176, 182, 255), None)),
        "tin_ingot": lambda: _ingot_sprite(((46, 72, 83, 255), (89, 126, 138, 255), (153, 181, 187, 255), None)),
        "bronze_dust": lambda: _dust_sprite(((79, 53, 21, 255), (139, 93, 36, 255), (194, 142, 67, 255), None)),
        "bronze_ingot": lambda: _ingot_sprite(((76, 50, 20, 255), (136, 89, 34, 255), (196, 140, 62, 255), None)),
        "tin_ore": lambda: _ore_item_sprite(PALETTES["stone"]),
        "deepslate_tin_ore": lambda: _ore_item_sprite(PALETTES["deepslate"]),
        "crusher": _crusher_item_sprite,
        "manual_workshop": _manual_workshop_item_sprite,
    }
    if item_id in material_sprites:
        return material_sprites[item_id]()
    raw_material = _material_from_prefixed_id(item_id, "_dust")
    if raw_material is not None:
        return _dust_sprite(METAL_PALETTES[raw_material])
    raw_material = _material_from_prefixed_id(item_id, "_ingot")
    if raw_material is not None:
        return _ingot_sprite(METAL_PALETTES[raw_material])
    raw_material = _material_from_prefixed_id(item_id, "_raw")
    if raw_material is not None:
        return _raw_sprite(METAL_PALETTES[raw_material])
    if item_id.startswith("raw_"):
        material = item_id.removeprefix("raw_")
        if material in METAL_PALETTES:
            return _raw_sprite(METAL_PALETTES[material])
    if item_id == "sulfur_coke":
        return _raw_sprite(METAL_PALETTES["sulfur_coke"])
    if item_id.endswith("_ore"):
        material = next((candidate for candidate in sorted(ORE_VEINS, key=len, reverse=True)
                         if item_id == f"{candidate}_ore" or item_id.endswith(f"_{candidate}_ore")), None)
        if material is not None:
            prefix = "" if item_id == f"{material}_ore" else item_id.removesuffix(f"_{material}_ore")
            host = "gravel" if prefix == "gravel" else ("stone" if not prefix else prefix)
            return _ore_item_sprite(PALETTES[host], ORE_VEINS[material])
    for material in sorted(TOOL_MATERIALS, key=len, reverse=True):
        prefix = material + "_"
        if item_id.startswith(prefix):
            role = item_id.removeprefix(prefix)
            if role in TOOL_SILHOUETTES:
                return _tool_sprite(role, material)
            if role in {"helmet", "chestplate", "leggings", "boots"}:
                return _armor_sprite(role, TOOL_MATERIALS[material])
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
    native = tuple(tuple(row) for row in pixels)
    destination.write_bytes(encode_rgba_png(native))
    zoomed = tuple(
        tuple(pixel for pixel in row for _ in range(8))
        for row in native
        for _ in range(8)
    )
    destination.with_name(f"{destination.stem}-8x{destination.suffix}").write_bytes(
        encode_rgba_png(zoomed)
    )


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
    native = tuple(tuple(row) for row in pixels)
    destination.write_bytes(encode_rgba_png(native))
    zoomed = tuple(
        tuple(pixel for pixel in row for _ in range(8))
        for row in native
        for _ in range(8)
    )
    destination.with_name(f"{destination.stem}-8x{destination.suffix}").write_bytes(
        encode_rgba_png(zoomed)
    )


def _write_world_resource_assets(assets_root: Path) -> None:
    """Re-author world props while retaining their existing placement geometry."""
    textures = assets_root / "textures" / "block"
    textures.mkdir(parents=True, exist_ok=True)
    (textures / "ground_stick.png").write_bytes(encode_rgba_png(_ground_stick_sprite()))
    ground_model = assets_root / "models" / "block" / "ground_stick.json"
    ground_model.write_text(
        '{\n  "textures": {"particle": "material_progression:block/ground_stick", "stick": "material_progression:block/ground_stick"},\n'
        '  "elements": [{"from": [2, 0, 7], "to": [14, 2, 9], "rotation": {"origin": [8, 1, 8], "axis": "y", "angle": 22.5}, "faces": {'
        '"up": {"texture": "#stick"}, "down": {"texture": "#stick"}, "north": {"texture": "#stick"}, "south": {"texture": "#stick"}, "west": {"texture": "#stick"}, "east": {"texture": "#stick"}}}]\n}\n',
        encoding="utf-8",
    )
    for family in ROCK_SILHOUETTES:
        texture_id = f"loose_rock_{family}"
        (textures / f"{texture_id}.png").write_bytes(
            encode_rgba_png(_loose_rock_world_sprite(family))
        )
        model = assets_root / "models" / "block" / "loose_rocks" / f"{family}.json"
        model.parent.mkdir(parents=True, exist_ok=True)
        model.write_text(
            '{"parent":"material_progression:block/loose_rocks","textures":{"particle":"material_progression:block/'
            + texture_id + '","rock":"material_progression:block/' + texture_id + '"}}\n',
            encoding="utf-8",
        )


def write_group(group: str, assets_root: Path) -> None:
    """Write local sprites, generated-item models, and item definitions."""
    if group == "full_blocks":
        block_ids = sorted(AUTHORED_FULL_BLOCKS)
        for block_id in block_ids:
            _write_block_assets(block_id, assets_root)
        _write_block_atlas(block_ids, ROOT / "build" / "item-art" / "blocks.png")
        _write_world_resource_assets(assets_root)
        return
    try:
        item_ids = sorted(AUTHORED_ITEM_GROUPS[group])
    except KeyError as error:
        raise ValueError(f"Unknown authored item group {group!r}") from error
    for item_id in item_ids:
        _write_item_assets(item_id, assets_root)
    if group == "armor":
        _write_armor_layers(assets_root)
    atlas_names = {
        "rocks_and_cobbles": "rocks-and-cobbles.png",
        "materials_and_workstations": "materials-and-workstations.png",
        "tools": "tools.png",
        "industrial_materials": "industrial-materials.png",
        "ore_blocks": "ore-blocks.png",
        "armor": "armor.png",
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
