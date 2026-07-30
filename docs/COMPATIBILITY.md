# Compatibility policy

Material Progression uses registry tags as the modern Ore Dictionary. Materials
should interoperate with other mods by default instead of requiring explicit
compatibility patches after implementation.

The authoritative NeoForge convention is documented in the
[NeoForge tag guide](https://docs.neoforged.net/docs/resources/server/tags/).
Shared tags use the `c:` namespace so NeoForge and Fabric projects can converge
on the same material vocabulary.

## Namespace hierarchy

Choose an input category in this order:

1. Use an established vanilla tag when it expresses the intended generic
   category or behavior.
2. Use an established shared `c:` tag for interchangeable materials.
3. Create a plural `c:` tag when the category represents material other mods
   can reasonably provide.
4. Use `material_progression:` for behavior unique to this mod.
5. Use a concrete ID only when identity itself matters or the format requires a
   concrete registry object.

Subtype tags must also be included by their parent tags. Examples include
`#c:ingots/tin`, `#c:dusts/copper`, `#c:raw_materials/tin`,
`#c:ores/tin`, `#c:rods/wooden`, `#c:rocks/stone`, and `#c:rocks`.

## Published material and tool tags

| Content | Shared interface |
| --- | --- |
| Tin/Bronze ingots | `#c:ingots/tin`, `#c:ingots/bronze`, and `#c:ingots` |
| Tin/Copper/Bronze dusts | Material subtag and `#c:dusts` |
| Raw tin | `#c:raw_materials/tin` and `#c:raw_materials` |
| Tin ore blocks and items | `#c:ores/tin` and `#c:ores` |
| Plant Fiber | `#c:fibers/plant` and `#c:fibers` |
| Knives | `#c:tools/knives` and `#c:tools` |
| Hammers | `#c:tools/hammers` and `#c:tools` |
| Saws | `#c:tools/saws` and `#c:tools` |
| Family Rocks | `#c:rocks/<family>` and `#c:rocks` |
| Family cobbles | block and item `#c:cobblestones/<family>` and their parents |

Tools also belong to established vanilla categories when they implement that
behavior. Hammers join `#minecraft:pickaxes`, Saws join
`#minecraft:axes`, and Knives join `#minecraft:swords`. The private
`material_progression:knives`, `hammers`, and `saws` tags are reloadable
behavior boundaries that consume those shared categories.

Ground Sticks yield the ordinary vanilla Stick. Generic recipes consume
`#c:rods/wooden`.

Ground Stick density consumes the reloadable block tag
`#material_progression:ground_stick_anchors`. It ships with Overworld natural
logs, leaves, Azalea and Flowering Azalea, Sweet Berry Bush, Bush, Firefly Bush,
and Dead Bush. Datapacks may extend the tag with compatible tree or shrub
anchors without replacing the configured feature.

The `material_progression:ground_stick` configured-feature codec exposes
`to_place`, `anchor_tag`, `attempts` (1-32), `horizontal_spread` (0-16),
`surface_vertical_range` (0-8), `anchor_horizontal_radius` (1-8),
`anchor_vertical_radius` (0-4), `near_chance` (0-1), and
`background_chance` (0-1). The near chance must not be lower than the background
chance. Searches never force-load chunks.

Ground-resource world generation consumes the reloadable
`#material_progression:ground_resources` block tag. It contains built-in Loose
Rocks, externally resolved Loose Rocks, and Ground Sticks. Both configured
features reject tagged placement targets, so world-generation order cannot
replace an existing ground resource. Datapacks that introduce another
replaceable ground resource should extend this behavior tag.

## Stone-family data interface

Each file under `data/<namespace>/stone_family/` declares:

```json
{
  "source_block_tag": "#material_progression:stone_sources/cinnabar",
  "rock_item_tag": "#c:rocks/cinnabar",
  "cobbled_block": "material_progression:cobbled_cinnabar",
  "raw_block": "minecraft:cinnabar",
  "loose_rock_surface_block_tag":
    "#material_progression:loose_rock_surfaces/cinnabar",
  "resistance": {
    "modifier": 0
  }
}
```

The resource ID is the family ID. `resistance.modifier` is the additive integer
applied directly to the dimension/depth tier before exposure reduction and
clamping. It must be from `-3` through `3`. The built-in soft, standard, and
hard classifications use `-1`, `0`, and `1` respectively; third-party values
outside that built-in set remain valid and classify by sign for diagnostics.
See [Underground and Geology](GEOLOGY.md) for the complete formula.

The public behavior tags are:

- `#material_progression:stone_sources/<family>`
- `#material_progression:loose_rock_surfaces/<family>`
- `#material_progression:loose_rock_cover`
- `#material_progression:loose_rock_netherrack_cover`
- block and item `#c:cobblestones/<family>` plus their parents
- `#c:rocks/<family>` plus `#c:rocks`

The two cover tags are intentionally disjoint. `loose_rock_cover` scans for the
nearest registered raw family, while `loose_rock_netherrack_cover` scans the
same eight-block range but accepts only the Netherrack family. Soul Sand and
Soul Soil ship in the latter so they cannot surface Basalt or Blackstone Rocks.

The catalog requires the sixteen built-in definitions and also accepts
arbitrary third-party family IDs under any namespace. A third-party family may
name externally registered Rock, raw-stone, and cobbled objects. Its Rock
family tag must resolve to exactly one registered item, and that tag must also
be included by `#c:rocks`. Raw and cobbled blocks must be registered, distinct,
and have block items. Source and direct-surface tags must exist, be non-empty,
and include the declared raw block.

Raw blocks, cobbled blocks, Rock items, source membership, and direct-surface
membership may each belong to only one family. Reload validation stages the
complete resolved catalog transactionally before publishing it, so any missing
tag, ambiguous Rock tag, unregistered object, invalid modifier, or ownership
conflict rejects the reload without replacing the last valid catalog. Standard
resource-pack precedence applies when multiple packs provide the same family
resource ID: the winning resource is validated as that single family
definition.

The sixteen built-ins use the compact `family` block state on
`material_progression:loose_rocks`. External IDs use the non-obtainable
`material_progression:external_loose_rocks` runtime block with a persistent,
client-synchronized block entity containing the exact family ID and Rock
stack. It renders and drops that configured Rock, including support
invalidation and explosions. A successful catalog reload reconciles loaded
external blocks once: a changed Rock mapping updates the stored/rendered item;
a removed or now-incompatible family drops the previously stored Rock and
removes the block.

Feedback looks up `stone_family.<namespace>.<path>`, with `/` in the path
converted to `.`, and supplies a readable path-derived fallback. Third-party
packs may localize that key but do not need a translation for safe server
feedback.

Compatible Rocks that only join `#c:rocks` still participate in the custom
four-Rock cobbling recipe and resolve to vanilla Cobblestone when they have no
registered family mapping.

## Manual Workshop recipe interface

`material_progression:manual_workshop` is a public recipe type and serializer.
Its data fields are:

- `ingredient`
- `tool`
- `result`
- `processing_time`
- `tool_damage`

Generic ingredients and tools use shared or behavior tags. Results remain
concrete registry IDs. Recipe changes reload with datapacks; a running operation
preserves progress only when the recipe ID and complete operation definition
remain unchanged.

Workshop operations unlock through Recipe Book rewards and use a dedicated
recipe-book category. The Manual Workshop screen does not embed the vanilla
crafting Recipe Book as a recipe-selection interface; the block continues to
resolve operations from its installed tool and input.

## Consuming compatible content

Recipes and machines should consume tags:

```json
{
  "ingredient": "#c:raw_materials/tin"
}
```

Shaped recipe keys follow the same rule:

```json
{
  "key": {
    "#": "#c:rods/wooden",
    "X": "#c:ingots/bronze"
  }
}
```

Machine allowlists may remain private behavior tags, but their members should be
shared material tags. For example, `#material_progression:crusher_inputs`
contains compatible ore and raw-material tags rather than direct mod item IDs.

## Legitimate concrete IDs

Tags describe equivalence, not outputs. Recipe results, registrations,
configured world objects, and identity-specific stone mappings name concrete
registry objects. Do not create a private one-member tag to disguise a
hard-coded material.

## New-content checklist

For every new material or material form:

1. Find established vanilla and common tags before inventing a name.
2. Add the item to its shared subtype and the subtype to its parent.
3. Add block and item tags when both registries contain the form.
4. Use shared tags in recipe, repair, machine, and code inputs.
5. Add real tools to established vanilla tool tags.
6. Add behavior-private tags only when Material Progression owns the rule.
7. Add literal expectations to the fast compatibility contracts.
8. Add a GameTest when runtime tag behavior changes gameplay.
9. Run `./gradlew headlessTest`.

The contract suite rejects direct Material Progression material IDs in generic
recipe inputs, verifies the common-tag catalog, checks the sixteen shipped
definitions and third-party runtime boundary, and ensures GameTest code remains
outside the production JAR.
