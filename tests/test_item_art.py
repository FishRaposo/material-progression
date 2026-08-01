import struct
import sys
import tempfile
import unittest
import zlib
from pathlib import Path
from unittest import mock

TESTS = Path(__file__).resolve().parent
if str(TESTS) not in sys.path:
    sys.path.insert(0, str(TESTS))

from content_contracts import (
    AUTHORED_FULL_BLOCKS,
    AUTHORED_FULL_BLOCK_FACE_TEXTURES,
    AUTHORED_ITEM_GROUPS,
    SHIPPED_ITEMS,
    WORLD_RESOURCE_ASSET_HASHES,
)
from support.png import assert_native_item_sprite, read_rgba8_png
from support.resources import ResourceTree
from tools.generate_item_art import encode_rgba_png, make_block_sprite, make_sprite


ROOT = Path(__file__).resolve().parents[1]
TREE = ResourceTree(ROOT, "material_progression")
ASSETS = TREE.assets


TOOL_ROLE_MINIMUM_MASS = {
    "axe": (76, 52),
    "hatchet": (64, 42),
    "hammer": (78, 54),
    "hoe": (58, 34),
    "knife": (58, 34),
    "pickaxe": (74, 52),
    "saw": (72, 48),
    "shovel": (70, 48),
    "sword": (68, 42),
}

# These sparse masks capture the category-defining mass and negative space,
# while leaving the generator free to shade and refine the larger silhouette.
TOOL_ROLE_REQUIRED_OPAQUE = {
    "axe": {(1, 3), (2, 5), (8, 2), (8, 7)},
    "hatchet": {(2, 4), (4, 6), (8, 3), (8, 7)},
    "hammer": {(1, 3), (11, 3), (3, 5), (9, 6)},
    "hoe": {(3, 3), (10, 3), (9, 6), (9, 8)},
    "knife": {(8, 1), (4, 4), (9, 7), (6, 9)},
    "pickaxe": {(1, 2), (13, 2), (4, 4), (9, 7)},
    "saw": {(2, 3), (10, 5), (4, 7), (9, 8)},
    "shovel": {(5, 1), (2, 4), (9, 5), (8, 8)},
    "sword": {(7, 1), (4, 4), (9, 7), (5, 9), (12, 9)},
}
TOOL_ROLE_REQUIRED_TRANSPARENT = {
    "axe": {(1, 7), (3, 8)},
    "hatchet": {(1, 7), (3, 8)},
    "hammer": {(2, 7), (4, 8)},
    "hoe": {(3, 6), (5, 7)},
    "knife": {(3, 7), (4, 8)},
    "pickaxe": {(2, 6), (4, 7)},
    "saw": {(3, 6), (5, 7), (7, 8)},
    "shovel": {(2, 7), (4, 8)},
    "sword": {(3, 7), (4, 8)},
}


def _opaque_mask(sprite):
    return {
        (x, y)
        for y, row in enumerate(sprite)
        for x, pixel in enumerate(row)
        if pixel[3]
    }


def _png_chunk(kind: bytes, payload: bytes) -> bytes:
    return (
        struct.pack(">I", len(payload))
        + kind
        + payload
        + struct.pack(">I", zlib.crc32(kind + payload) & 0xFFFFFFFF)
    )


def _rgba_png(
    width: int = 16,
    height: int = 16,
    *,
    color_type: int = 6,
    filter_byte: int = 0,
    opaque_pixel: bool = True,
    opaque_corner: bool = False,
) -> bytes:
    pixels = bytearray(width * height * 4)
    if opaque_pixel:
        center = ((height // 2) * width + (width // 2)) * 4
        pixels[center:center + 4] = b"\x7f\x7f\x7f\xff"
    if opaque_corner:
        pixels[:4] = b"\x7f\x7f\x7f\xff"
    rows = b"".join(
        bytes([filter_byte]) + pixels[row * width * 4:(row + 1) * width * 4]
        for row in range(height)
    )
    header = struct.pack(">IIBBBBB", width, height, 8, color_type, 0, 0, 0)
    compressed = zlib.compress(rows)
    midpoint = len(compressed) // 2
    return b"\x89PNG\r\n\x1a\n" + _png_chunk(b"IHDR", header) + (
        _png_chunk(b"IDAT", compressed[:midpoint])
        + _png_chunk(b"IDAT", compressed[midpoint:])
        + _png_chunk(b"IEND", b"")
    )


class ItemArtContractTests(unittest.TestCase):
    def assert_local_item_model_and_native_sprite(self, item: str) -> None:
        local_model = f"material_progression:item/{item}"
        self.assertEqual(
            {"model": {"type": "minecraft:model", "model": local_model}},
            TREE.load_json(ASSETS / "items" / f"{item}.json"),
        )
        self.assertEqual(
            {
                "parent": "minecraft:item/generated",
                "textures": {"layer0": local_model},
            },
            TREE.load_json(ASSETS / "models" / "item" / f"{item}.json"),
        )
        assert_native_item_sprite(ASSETS / "textures" / "item" / f"{item}.png")

    def test_rgba_png_parser(self):
        with tempfile.TemporaryDirectory() as temporary_directory:
            directory = Path(temporary_directory)
            valid = directory / "valid.png"
            valid.write_bytes(_rgba_png())

            image = read_rgba8_png(valid)
            self.assertEqual((16, 16), (image.width, image.height))
            self.assertEqual(16 * 16 * 4, len(image.pixels))
            self.assertEqual(255, image.pixels[((8 * 16 + 8) * 4) + 3])

            invalid_signature = directory / "invalid_signature.png"
            invalid_signature.write_bytes(b"not-a-png")
            with self.assertRaises(AssertionError):
                read_rgba8_png(invalid_signature)

            indexed = directory / "indexed.png"
            indexed.write_bytes(_rgba_png(color_type=3))
            with self.assertRaises(AssertionError):
                read_rgba8_png(indexed)

            filtered = directory / "filtered.png"
            filtered.write_bytes(_rgba_png(filter_byte=1))
            with self.assertRaises(AssertionError):
                read_rgba8_png(filtered)

            corrupted_crc = directory / "corrupted_crc.png"
            corrupted = bytearray(_rgba_png())
            corrupted[-1] = 1
            corrupted_crc.write_bytes(corrupted)
            with self.assertRaises(AssertionError):
                read_rgba8_png(corrupted_crc)

    def test_encoder_uses_a_fixed_deflate_stream(self):
        """PNG bytes must not vary with the host zlib compressor release."""
        pixels = tuple(
            tuple((x * 16, y * 16, 127, 255) for x in range(16))
            for y in range(16)
        )
        expected = encode_rgba_png(pixels)
        with mock.patch(
            "tools.generate_item_art.zlib.compress",
            side_effect=AssertionError("PNG encoder must not call zlib.compress"),
        ):
            self.assertEqual(expected, encode_rgba_png(pixels))

    def test_native_item_sprite_rejects_missing_content_or_opaque_corners(self):
        with tempfile.TemporaryDirectory() as temporary_directory:
            directory = Path(temporary_directory)
            transparent = directory / "transparent.png"
            transparent.write_bytes(_rgba_png(opaque_pixel=False))
            with self.assertRaises(AssertionError):
                assert_native_item_sprite(transparent)

            opaque_corner = directory / "opaque_corner.png"
            opaque_corner.write_bytes(_rgba_png(opaque_corner=True))
            with self.assertRaises(AssertionError):
                assert_native_item_sprite(opaque_corner)

    def test_rocks_and_cobbles_have_local_models_and_native_sprites(self):
        rocks_and_cobbles = AUTHORED_ITEM_GROUPS["rocks_and_cobbles"]
        self.assertEqual(30, len(rocks_and_cobbles))
        for item in rocks_and_cobbles:
            with self.subTest(item=item):
                self.assert_local_item_model_and_native_sprite(item)

    def test_rocks_and_cobbles_match_deterministic_generator(self):
        rocks_and_cobbles = AUTHORED_ITEM_GROUPS["rocks_and_cobbles"]
        self.assertEqual(30, len(rocks_and_cobbles))
        for item in rocks_and_cobbles:
            with self.subTest(item=item):
                expected = encode_rgba_png(make_sprite(item))
                actual = (ASSETS / "textures" / "item" / f"{item}.png").read_bytes()
                self.assertEqual(expected, actual)

    def test_materials_and_workstations_have_local_models_and_native_sprites(self):
        materials_and_workstations = AUTHORED_ITEM_GROUPS[
            "materials_and_workstations"
        ]
        self.assertEqual(12, len(materials_and_workstations))
        for item in materials_and_workstations:
            with self.subTest(item=item):
                self.assert_local_item_model_and_native_sprite(item)

    def test_materials_and_workstations_match_deterministic_generator(self):
        materials_and_workstations = AUTHORED_ITEM_GROUPS[
            "materials_and_workstations"
        ]
        self.assertEqual(12, len(materials_and_workstations))
        for item in materials_and_workstations:
            with self.subTest(item=item):
                expected = encode_rgba_png(make_sprite(item))
                actual = (ASSETS / "textures" / "item" / f"{item}.png").read_bytes()
                self.assertEqual(expected, actual)

    def test_tools_have_local_models_and_native_sprites(self):
        tools = AUTHORED_ITEM_GROUPS["tools"]
        self.assertEqual(17, len(tools))
        for item in tools:
            with self.subTest(item=item):
                self.assert_local_item_model_and_native_sprite(item)

    def test_tools_match_deterministic_generator(self):
        tools = AUTHORED_ITEM_GROUPS["tools"]
        self.assertEqual(17, len(tools))
        for item in tools:
            with self.subTest(item=item):
                expected = encode_rgba_png(make_sprite(item))
                actual = (ASSETS / "textures" / "item" / f"{item}.png").read_bytes()
                self.assertEqual(expected, actual)

    def test_tool_roles_have_chunky_readable_mass_and_intentional_negative_space(self):
        """Prevent tools collapsing back into thin, color-dependent diagonals."""
        masks_by_role = {}
        for item in sorted(AUTHORED_ITEM_GROUPS["tools"]):
            material, separator, role = item.partition("_")
            self.assertTrue(separator, item)
            self.assertIn(role, TOOL_ROLE_MINIMUM_MASS)
            sprite = make_sprite(item)
            mask = _opaque_mask(sprite)
            minimum_mass, minimum_working_mass = TOOL_ROLE_MINIMUM_MASS[role]
            working_mask = {
                coordinate
                for coordinate in mask
                if coordinate[0] <= 10 and coordinate[1] <= 9
            }
            with self.subTest(item=item, measurement="mass"):
                self.assertGreaterEqual(len(mask), minimum_mass)
                self.assertGreaterEqual(len(working_mask), minimum_working_mass)
            with self.subTest(item=item, measurement="role mask"):
                self.assertFalse(TOOL_ROLE_REQUIRED_OPAQUE[role] - mask)
                self.assertFalse(TOOL_ROLE_REQUIRED_TRANSPARENT[role] & mask)

            xs = [x for x, _ in mask]
            ys = [y for _, y in mask]
            bounding_area = (max(xs) - min(xs) + 1) * (max(ys) - min(ys) + 1)
            with self.subTest(item=item, measurement="negative space"):
                self.assertGreaterEqual(bounding_area - len(mask), 54)

            masks_by_role.setdefault(role, []).append((material, mask, sprite))

        canonical_masks = {}
        for role, variants in masks_by_role.items():
            canonical_masks[role] = variants[0][1]
            for material, mask, _ in variants[1:]:
                with self.subTest(role=role, material=material):
                    self.assertEqual(canonical_masks[role], mask)

        for left_role, left_mask in canonical_masks.items():
            for right_role, right_mask in canonical_masks.items():
                if left_role >= right_role:
                    continue
                with self.subTest(left=left_role, right=right_role):
                    minimum_difference = (
                        8 if {left_role, right_role} == {"axe", "hatchet"} else 12
                    )
                    self.assertGreaterEqual(
                        len(left_mask ^ right_mask), minimum_difference
                    )

    def test_tool_tiers_change_working_material_without_changing_role_silhouette(self):
        """The category is shape-first; tier is a palette change within it."""
        variants_by_role = {}
        for item in AUTHORED_ITEM_GROUPS["tools"]:
            material, _, role = item.partition("_")
            variants_by_role.setdefault(role, []).append((material, make_sprite(item)))

        handle_region = {(x, y) for y in range(9, 15) for x in range(8, 15)}
        working_region = {(x, y) for y in range(1, 9) for x in range(1, 14)}
        for role, variants in variants_by_role.items():
            if len(variants) < 2:
                continue
            _, baseline = variants[0]
            baseline_mask = _opaque_mask(baseline)
            baseline_handle = {
                coordinate: baseline[coordinate[1]][coordinate[0]]
                for coordinate in handle_region & baseline_mask
            }
            for material, sprite in variants[1:]:
                mask = _opaque_mask(sprite)
                with self.subTest(role=role, material=material):
                    self.assertEqual(baseline_mask, mask)
                    self.assertEqual(
                        baseline_handle,
                        {
                            coordinate: sprite[coordinate[1]][coordinate[0]]
                            for coordinate in handle_region & mask
                        },
                    )
                    self.assertNotEqual(
                        {
                            baseline[y][x]
                            for x, y in working_region & baseline_mask
                        },
                        {
                            sprite[y][x]
                            for x, y in working_region & mask
                        },
                    )

    def test_every_shipped_item_uses_authored_local_art(self):
        authored_items = set().union(*AUTHORED_ITEM_GROUPS.values())
        self.assertEqual(SHIPPED_ITEMS, authored_items)
        for item in SHIPPED_ITEMS:
            with self.subTest(item=item):
                self.assert_local_item_model_and_native_sprite(item)

    def test_full_blocks_use_local_models_and_tileable_native_surfaces(self):
        """Catch a block reverting to a vanilla model or non-tileable texture."""
        self.assertEqual(18, len(AUTHORED_FULL_BLOCKS))
        expected_textures = set()
        for block in AUTHORED_FULL_BLOCKS:
            with self.subTest(block=block):
                local_model = f"material_progression:block/{block}"
                blockstate = TREE.load_json(
                    ASSETS / "blockstates" / f"{block}.json"
                )
                variants = blockstate["variants"].values()
                self.assertTrue(variants)
                self.assertTrue(
                    all(variant["model"] == local_model for variant in variants)
                )

                model = TREE.load_json(
                    ASSETS / "models" / "block" / f"{block}.json"
                )
                face_textures = AUTHORED_FULL_BLOCK_FACE_TEXTURES.get(
                    block, {"all": block}
                )
                expected_textures.update(face_textures.values())
                self.assertEqual(
                    {
                        "parent": (
                            "minecraft:block/cube"
                            if block in AUTHORED_FULL_BLOCK_FACE_TEXTURES
                            else "minecraft:block/cube_all"
                        ),
                        "textures": {
                            face: f"material_progression:block/{texture}"
                            for face, texture in face_textures.items()
                        },
                    },
                    model,
                )

                for texture in set(face_textures.values()):
                    image = read_rgba8_png(
                        ASSETS / "textures" / "block" / f"{texture}.png"
                    )
                    self.assertEqual((16, 16), (image.width, image.height))
                    self.assertEqual(bytes([255]) * (16 * 16), image.pixels[3::4])
                    for coordinate in range(16):
                        self.assertEqual(
                            image.pixels[coordinate * 4:coordinate * 4 + 4],
                            image.pixels[(15 * 16 + coordinate) * 4:(15 * 16 + coordinate) * 4 + 4],
                        )
                        self.assertEqual(
                            image.pixels[(coordinate * 16) * 4:(coordinate * 16) * 4 + 4],
                            image.pixels[(coordinate * 16 + 15) * 4:(coordinate * 16 + 15) * 4 + 4],
                        )
        self.assertEqual(
            expected_textures,
            TREE.names_matching(ASSETS / "textures" / "block", "*.png"),
        )

    def test_full_blocks_match_deterministic_generator(self):
        for block in AUTHORED_FULL_BLOCKS:
            with self.subTest(block=block):
                face_textures = AUTHORED_FULL_BLOCK_FACE_TEXTURES.get(
                    block, {"all": block}
                )
                for texture in set(face_textures.values()):
                    with self.subTest(texture=texture):
                        expected = encode_rgba_png(make_block_sprite(texture))
                        actual = (
                            ASSETS / "textures" / "block" / f"{texture}.png"
                        ).read_bytes()
                        self.assertEqual(expected, actual)

    def test_ground_resource_assets_are_unchanged(self):
        import hashlib

        for relative_path, expected_hash in WORLD_RESOURCE_ASSET_HASHES.items():
            with self.subTest(path=relative_path):
                actual_hash = hashlib.sha256(
                    (ASSETS / relative_path).read_bytes()
                ).hexdigest()
                self.assertEqual(expected_hash, actual_hash)


if __name__ == "__main__":
    unittest.main()
