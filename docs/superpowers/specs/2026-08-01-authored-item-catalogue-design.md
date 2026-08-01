# Authored Item Catalogue Design

**Status:** approved design; implementation pending review

## Intent

Replace every placeholder inventory representation in Material Progression 0.2.0
with original, native-resolution Minecraft item art. The result should feel
vanilla-adjacent in the way that No Tree Punching's primitive tools are readable
at a glance, while making the mod's geological material families unmistakable.

The pass covers the complete current visual surface: every inventory item and
every mod-owned full block receives original local art. The existing world-space
Loose Rock and Ground Stick models, textures, placement, collision, and loot
behavior are explicitly preserved byte-for-byte. They already communicate the
opening loop successfully.

Inventory discoverability is part of the pass. Every current mod-owned item
receives concise localized lore. It must state a real use, processing path, or
progression role; vanilla-obvious tools may use a shared material-tier line, but
unfamiliar forms must name their useful interaction directly.

Reference mods inform readability and material identity only. No source texture,
palette, or sprite is copied from No Tree Punching, Divergent Underground,
TerraFirmaCraft, or any other project.

## Scope

The first catalogue covers every one of the 59 current
`assets/material_progression/items/*.json` definitions:

- 16 family Rock items
- 14 custom cobbled-stone block items
- Flint Shard and Plant Fiber
- Tin, Copper, and Bronze ore, raw material, dust, and ingot forms
- Flint, Tin, and Bronze tools
- Crusher and Manual Workshop block items
- One localized, useful tooltip for each of those 59 items in both English and
  Brazilian Portuguese
- 18 current full-block surfaces: the fourteen custom cobbles, Tin Ore,
  Deepslate Tin Ore, Crusher, and Manual Workshop

Future content is not pre-created. Instead, this document establishes a stable
art contract that every future item must satisfy before it is considered shipped.

## Visual language

All inventory sprites are original 16 by 16 pixel PNGs with transparency, hard
pixel edges, no anti-aliasing, and a restrained vanilla-adjacent palette.

- **Rocks:** hand-sized, irregular mineral chips rather than full cubes. Each
  family uses a distinct silhouette and a two-to-four tone material palette.
  Sulfur and Cinnabar use a small high-chroma accent without appearing luminous;
  Nether and End families remain readable against Minecraft's inventory gray.
- **Cobbles:** compact clustered fragments, visually related to their source
  Rock but clearly more structural and assembled.
- **Ores and material forms:** one material grammar across the progression:
  host stone with a material vein for ore, a rough cluster for raw material,
  granular piles for dust, and a compact cast bar for ingots. Copper, Tin, and
  Bronze use distinct hue/value families rather than vanilla item substitutions.
- **Primitive tools:** Flint heads are chipped, dark, and visibly bound to wood.
  Knife, Hammer, Saw, and Hatchet retain clear silhouettes at 16 pixels.
- **Metal tools:** Tin and Bronze retain the same tool silhouettes as their
  primitive equivalents, but gain forged heads, appropriate highlights, and
  material-specific color. A Bronze Saw must read as a saw before it reads as an
  upgrade.
- **Infrastructure:** Crusher and Manual Workshop are compact isometric-like
  item sprites with a recognisable working surface or mechanism, not borrowed
  vanilla block icons.
- **Block surfaces:** cobbles are tileable clusters of family-correct fragments,
  not recoloured raw blocks. Hard families use darker, lower-value palettes;
  soft families remain visibly lighter. Tin ore keeps a readable blue-gray vein
  in its appropriate Stone or Deepslate host. Crusher and Workshop are distinct
  working blocks without borrowing a vanilla furnace or crafting-table texture.

The shared rules are: dark selective outline, one readable light source from
the upper left, stronger contrast at the outer silhouette than in interior
texture, and no text, gradients, photorealism, or glossy modern rendering.

## Technical approach

1. Add a local `textures/item/<id>.png` sprite for every current mod-owned item.
2. Replace every placeholder item declaration with a local item model reference.
3. Add `models/item/<id>.json` generated-item models that map each declaration
   to its matching local sprite. Block-item sprites may be independent from the
   corresponding world block texture.
4. Add local 16 by 16 tileable block textures and local block models for every
   current mod-owned full block, while retaining existing Loose Rock and Ground
   Stick world assets exactly.
5. Establish a documented palette/category sheet for future artists and content
   authors. Future item declarations must use the same local-texture convention.
6. Use generated visual studies only as original art-direction input; curate and
   validate the final sprites as exact native-resolution assets before they enter
   the resource pack.
6. Register all lore through the existing `DataComponents.LORE` helper and
   share translation keys only where the described use is genuinely identical.

## Acceptance criteria

- Each of the 59 shipped item declarations resolves to
  `material_progression:item/<id>` rather than a `minecraft:` placeholder.
- Each local item model resolves to its matching local `textures/item/<id>.png`.
- Every item texture is exactly 16 by 16 pixels, RGBA/alpha-capable, and has no
  opaque corner background.
- Rock and cobble sprites are distinguishable by both silhouette and value, not
  only their localized name.
- Each of the 59 items has non-empty `en_us` and `pt_br` lore attached at item
  registration. Every Rock tooltip explicitly says that one Rock sharpens into
  a Flint Shard as well as describing four-Rock cobbling.
- The world-space Loose Rock and Ground Stick assets are byte-for-byte unchanged
  by this pass.
- A resource contract rejects missing local textures, placeholder vanilla item
  references, wrong texture dimensions, and invalid alpha encoding.
- Every custom full block resolves to a local texture and no longer presents a
  placeholder vanilla raw-block, ore, furnace, or crafting-table face.
- Every path that deliberately denies a mod-owned harvest gives a throttled
  localized action-bar explanation of the required tool before it can produce
  no drops.
- The normal resource, build, distribution, and GameTest checks remain green;
  the rebuilt `dist/` JAR contains the new sprites and no test-only content.

## Out of scope

- Changes to Loose Rock or Ground Stick world visuals
- New gameplay, recipes, item registrations, tags, or balance changes beyond
  making existing denied-harvest outcomes explicitly visible
- Replacing vanilla assets globally
- Assets for unimplemented future items
- Resource-pack configuration or an external art dependency
- Tooltips that pretend to explain an unimplemented future mechanic

## Verification plan

Begin with failing contracts for the local-texture catalog, block surfaces, and
image dimensions.
After adding the assets and model mappings, run the focused contracts, then
`contractTest`, `build`, `runGameTestServer`, and `headlessTest`. Inspect the
client inventory, creative tabs, recipe book, workshop inventory, and block-item
forms at native scale. Finally refresh and validate the synchronized production
JAR through the release workflow.
