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
and their item definitions. It also writes ignored native and nearest-neighbour
review atlases to `build/item-art/rocks-and-cobbles.png` and
`build/item-art/rocks-and-cobbles-8x.png`. Do not use the generator for Ground
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
their item definitions. It also writes ignored native and 8x review atlases to
`build/item-art/materials-and-workstations.png` and
`build/item-art/materials-and-workstations-8x.png`. Ground Stick and Loose Rock
world assets remain outside this group and must not be regenerated.

## Generated tool and full-block art

The tool group gives each role a high-mass silhouette with a broad working head,
a separate wooden handle, and deliberate role-specific negative space. Flint,
Tin, and Bronze variants of the same role share an exact alpha silhouette and
handle; only the working material changes. Regenerate the seventeen sprites and
their native and 8x review atlases with:

```powershell
python tools/generate_item_art.py --group tools
```

The full-block group authors the complete surfaces for the fourteen local
cobbles, two Tin ore hosts, the Crusher, and the Manual Workshop. It writes the
native `build/item-art/blocks.png` atlas and the nearest-neighbour
`build/item-art/blocks-8x.png` review atlas:

```powershell
python tools/generate_item_art.py --group full_blocks
```

## Reference study boundary

The local study checkout lives only at `research/reference-assets/` and is
ignored by Git. It may contain No Tree Punching, TerraFirmaCraft, and Divergent
Underground source and assets for private comparison; none of those files may
be imported into `src/`, `dist/`, generated resources, documentation images, or
release artifacts. A reference can explain a role, contrast, or readability
problem. It cannot supply a silhouette, pixel arrangement, palette, or texture
to trace.

The vanilla comparison input is the Minecraft 26.2 client archive resolved by
the local Gradle/NeoForge runtime cache. One developer machine discovered it
under its Gradle cache; locate it on the active machine instead of recording or
depending on that machine-specific path:

```powershell
Get-ChildItem "$env:USERPROFILE\.gradle\caches\neoformruntime\artifacts" `
  -Filter "minecraft_26.2_client.jar" -File -Recurse
```

If the archive is not present, run a normal Gradle task such as
`./gradlew classes` to resolve the project runtime, then repeat the search. If
the cache layout differs, use Gradle's resolved local runtime archive without
copying it into the repository. Study its entries in place (for example
`assets/minecraft/textures/item/flint.png`, `iron_ingot.png`, `raw_iron.png`,
and `stick.png`); do not extract or copy vanilla assets into this repository.
The archive is a local development input, not a project dependency or shipped
resource.

The ignored `research/reference-assets/item-art-comparison.html` sheet is a
review aid. It renders only current Material Progression sprites by relative
path and records study provenance as text. It intentionally renders no
third-party or vanilla pixels.

## Reference-baseline rubric

The study confirmed four durable readability checks in addition to the rules
above:

- Ground resources must be compact at inventory scale: one uneven chip for a
  Rock and two to four separated but clustered chips for a cobble item.
- A stone family is identified by its small material palette and internal plane
  breaks, not a borrowed outline. Its Rock and cobbled forms should read as the
  same material and different objects.
- A tool needs a recognizable handle plus a separate working edge or head;
  material variants retain that category silhouette while changing their
  working material.
- Dust, fiber, shards, ingots, and workstation blocks require different mass,
  spacing, and negative space so they do not collapse into one icon category.

Before accepting an item-art change, inspect it at native scale and at the
inventory-scale nearest-neighbour zoom used by the comparison sheet. Confirm
that the silhouette identifies its category before color; upper-left lighting
and lower/right depth read without a full outline; palettes have no soft or
interpolated pixels; and every visible decision is justified by this rubric,
not a reference sprite. The PNG contract remains the mechanical check for 16
by 16 RGBA, meaningful opaque content, and transparent corners.

An original generated image may serve as a non-shipping mood study, but it is
not an asset source and must not be copied into the resource pack.
