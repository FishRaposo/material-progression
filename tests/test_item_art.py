import struct
import sys
import tempfile
import unittest
import zlib
from pathlib import Path

TESTS = Path(__file__).resolve().parent
if str(TESTS) not in sys.path:
    sys.path.insert(0, str(TESTS))

from content_contracts import (
    AUTHORED_ITEM_GROUPS,
    WORLD_RESOURCE_ASSET_HASHES,
)
from support.png import assert_native_item_sprite, read_rgba8_png
from support.resources import ResourceTree
from tools.generate_item_art import encode_rgba_png, make_sprite


ROOT = Path(__file__).resolve().parents[1]
TREE = ResourceTree(ROOT, "material_progression")
ASSETS = TREE.assets


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
