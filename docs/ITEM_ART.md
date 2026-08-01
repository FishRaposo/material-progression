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

## Generated material and workstation inventory art

The material/workstation group uses the same deterministic generator while
giving each progression form a distinct silhouette: ore is a host-stone chip
with blue-gray veins, raw tin is a rough cluster, dust is three granular piles,
and ingots are cast bars. Flint is a single angled chip, Plant Fiber is three
pale-green strands, and the Crusher and Manual Workshop are compact physical
machines with their crushing gap or tool recess visible at inventory scale.
Their inventory sprites deliberately differ from their opaque world block
faces, which makes block pickups read cleanly in a crowded inventory.

Regenerate the group with:

```powershell
python tools/generate_item_art.py --group materials_and_workstations
```

This command writes all twelve local sprites, their generated-item models, and
their item definitions. It also writes the ignored ten-column review atlas to
`build/item-art/materials-and-workstations.png`. Ground Stick and Loose Rock
world assets remain outside this group and must not be regenerated.
