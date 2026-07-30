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

## Stone-family data interface

Each built-in file under `data/material_progression/stone_family/` declares:

```json
{
  "source_block_tag": "#material_progression:stone_sources/cinnabar",
  "rock_item_tag": "#c:rocks/cinnabar",
  "cobbled_block": "material_progression:cobbled_cinnabar",
  "raw_block": "material_progression:cinnabar_block",
  "loose_rock_surface_block_tag":
    "#material_progression:loose_rock_surfaces/cinnabar",
  "resistance": {
    "tier": "standard",
    "modifier": 1.0
  }
}
```

The resistance tier is `soft`, `standard`, or `hard`. The matching modifiers
are currently `0.75`, `1.0`, and `1.5`; runtime geology converts the profile to
the family tier shift described in [Underground and Geology](GEOLOGY.md).

The public behavior tags are:

- `#material_progression:stone_sources/<family>`
- `#material_progression:loose_rock_surfaces/<family>`
- `#material_progression:loose_rock_cover`
- block and item `#c:cobblestones/<family>` plus their parents
- `#c:rocks/<family>` plus `#c:rocks`

Reload validation stages the complete catalog atomically. Duplicate source
membership and duplicate direct-surface membership are rejected with a clear
reload error.

This interface is not yet fully extensible. The runtime catalog currently
requires exactly the sixteen built-in IDs and rejects unknown family IDs because
Loose Rock block state is bound to the built-in family enum. Allowing datapacks
to define arbitrary third-party families using externally registered Rock and
cobbled objects is a required pre-0.2.0 fix. Do not claim that the schema
currently provides that promised extension.

Compatible Rocks from other mods can already join `#c:rocks`. They participate
in the custom four-Rock cobbling recipe and resolve to vanilla Cobblestone when
they have no registered family mapping.

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
recipe inputs, verifies the common-tag catalog, checks the sixteen family
definitions, and ensures GameTest code remains outside the production JAR.
