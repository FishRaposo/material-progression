from dataclasses import dataclass
from pathlib import Path
import struct
import zlib


PNG_SIGNATURE = b"\x89PNG\r\n\x1a\n"


@dataclass(frozen=True)
class RgbaPng:
    width: int
    height: int
    pixels: bytes


def read_rgba8_png(path: Path) -> RgbaPng:
    """Read a deliberately narrow, native-resolution item PNG encoding."""
    contents = path.read_bytes()
    if not contents.startswith(PNG_SIGNATURE):
        raise AssertionError(f"{path} is missing the PNG signature")

    offset = len(PNG_SIGNATURE)
    header = None
    idat_chunks = []
    saw_iend = False
    while offset < len(contents):
        if offset + 8 > len(contents):
            raise AssertionError(f"{path} has a truncated PNG chunk header")
        length = struct.unpack(">I", contents[offset:offset + 4])[0]
        kind = contents[offset + 4:offset + 8]
        offset += 8
        end = offset + length
        if end + 4 > len(contents):
            raise AssertionError(f"{path} has a truncated {kind!r} chunk")
        payload = contents[offset:end]
        stored_crc = struct.unpack(">I", contents[end:end + 4])[0]
        actual_crc = zlib.crc32(kind + payload) & 0xFFFFFFFF
        if stored_crc != actual_crc:
            raise AssertionError(f"{path} has an invalid {kind!r} chunk CRC")
        offset = end + 4

        if header is None and kind != b"IHDR":
            raise AssertionError(f"{path} must begin with IHDR")
        if kind == b"IHDR":
            if header is not None or idat_chunks or saw_iend:
                raise AssertionError(f"{path} must contain one leading IHDR")
            if len(payload) != 13:
                raise AssertionError(f"{path} has an invalid IHDR length")
            header = struct.unpack(">IIBBBBB", payload)
        elif kind == b"IDAT":
            if header is None or saw_iend:
                raise AssertionError(f"{path} has IDAT outside image data")
            idat_chunks.append(payload)
        elif kind == b"IEND":
            if saw_iend or payload:
                raise AssertionError(f"{path} has an invalid IEND chunk")
            saw_iend = True
            if offset != len(contents):
                raise AssertionError(f"{path} contains data after IEND")
        elif saw_iend:
            raise AssertionError(f"{path} contains data after IEND")

    if header is None:
        raise AssertionError(f"{path} is missing IHDR")
    if not saw_iend:
        raise AssertionError(f"{path} is missing IEND")
    if not idat_chunks:
        raise AssertionError(f"{path} is missing IDAT")

    width, height, bit_depth, color_type, compression, filtering, interlace = (
        header
    )
    if width == 0 or height == 0:
        raise AssertionError(f"{path} has an empty image")
    if (bit_depth, color_type) != (8, 6):
        raise AssertionError(f"{path} must use 8-bit RGBA pixels")
    if compression != 0 or filtering != 0:
        raise AssertionError(f"{path} uses an unsupported PNG encoding")
    if interlace != 0:
        raise AssertionError(f"{path} must not be interlaced")

    try:
        decoded = zlib.decompress(b"".join(idat_chunks))
    except zlib.error as error:
        raise AssertionError(f"{path} has invalid IDAT data") from error

    row_length = width * 4
    expected_length = height * (row_length + 1)
    if len(decoded) != expected_length:
        raise AssertionError(
            f"{path} decoded to {len(decoded)} bytes, expected {expected_length}"
        )

    pixels = bytearray()
    for row in range(height):
        start = row * (row_length + 1)
        if decoded[start] != 0:
            raise AssertionError(f"{path} uses a non-zero PNG row filter")
        pixels.extend(decoded[start + 1:start + 1 + row_length])

    return RgbaPng(width, height, bytes(pixels))


def assert_native_item_sprite(path: Path) -> None:
    image = read_rgba8_png(path)
    if (image.width, image.height) != (16, 16):
        raise AssertionError(
            f"{path} must be 16 by 16, got {image.width} by {image.height}"
        )

    alpha = image.pixels[3::4]
    if 255 not in alpha:
        raise AssertionError(f"{path} must contain an opaque pixel")

    for x, y in ((0, 0), (15, 0), (0, 15), (15, 15)):
        corner_alpha = alpha[y * image.width + x]
        if corner_alpha != 0:
            raise AssertionError(f"{path} must have transparent corners")
