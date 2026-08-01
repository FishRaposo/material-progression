# Item art

Authored inventory sprites are deliberately small, physical, and legible at a
glance. They are original Material Progression art, not copies of Minecraft or
reference-mod artwork.

## Durable rules

- Use 16 by 16 RGBA pixels.
- Light comes from the upper-left; corners remain transparent.
- Outline selectively on lower and right edges rather than enclosing every
  shape in black.
- A Rock is one irregular chip. A cobble is a cluster of two to four chips.
- Each material has shadow, base, highlight, and an optional accent color.
- Sulfur and Cinnabar use no more than four accent pixels per sprite.
- Do not use gradients, anti-aliasing, text, logos, or copied art.

## Generated geological inventory art

`tools/generate_item_art.py` is the source for the Rock and cobble sprites. It
encodes named family palettes, four chip silhouettes, and deterministic PNG
output with filter byte zero. Regenerate the complete group with:

```powershell
python tools/generate_item_art.py --group rocks_and_cobbles
```

The command writes the thirty local item sprites, their generated-item models,
and their item definitions. It also writes the ignored ten-column review atlas
to `build/item-art/rocks-and-cobbles.png`. Do not use the generator for Ground
Stick or Loose Rock world assets; those assets are intentionally preserved as
their existing world-facing art.
