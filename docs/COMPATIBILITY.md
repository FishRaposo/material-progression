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
2. Use an established shared `c:` tag for interchangeable materials and
   cross-loader conventions.
3. Create a new plural `c:` tag when the category represents a material other
   mods can reasonably provide.
4. Use `material_progression:` only when membership controls behavior unique to
   this mod.
5. Use a concrete item ID only when identity itself matters or the data format
   requires a concrete registry object.

Material tags use plural folders and material subtags:

- `#c:ingots/tin`
- `#c:dusts/copper`
- `#c:raw_materials/tin`
- `#c:ores/tin`
- `#c:rods/wooden`
- `#c:rocks` for the planned Rock item

Rock recipes must consume `#c:rocks`, not
`material_progression:rock`. Ground Rock world generation may place the
concrete Material Progression object because world placement creates a specific
block or entity, but its pickup item is published as interchangeable material.
The primitive sharpening and cobblestone recipes therefore accept compatible
Rocks supplied by other mods. Flint shards must likewise use the established
shared tag available in the target NeoForge common-tag vocabulary; verify the
exact 26.2 tag rather than inventing a private substitute during implementation.

Ground sticks yield the ordinary vanilla stick rather than a new mod item.
Recipes use the established wooden-stick category, currently
`#c:rods/wooden`, so compatible sticks remain interchangeable.

When introducing a new subtype, publish it through the parent tag as well. For
example, `#c:ingots/tin` is also included by `#c:ingots`.

## Publishing our content

Every interchangeable item must be added to its shared subtype tag. Blocks with
material identity, especially ores and storage blocks, normally need equivalent
block and item tags.

Current examples:

| Content | Shared tag |
|---|---|
| Tin ingot | `#c:ingots/tin` |
| Bronze ingot | `#c:ingots/bronze` |
| Tin dust | `#c:dusts/tin` |
| Copper dust | `#c:dusts/copper` |
| Bronze dust | `#c:dusts/bronze` |
| Raw tin | `#c:raw_materials/tin` |
| Tin ore blocks and items | `#c:ores/tin` |
| Plant fiber | `#c:fibers/plant` and parent `#c:fibers` |
| Knives | `#c:tools/knives` and parent `#c:tools` |
| Hammers | `#c:tools/hammers` and parent `#c:tools` |
| Saws | `#c:tools/saws` and parent `#c:tools` |

Tool items also belong to the established vanilla category tags such as
`#minecraft:pickaxes`, `#minecraft:axes`, `#minecraft:shovels`,
`#minecraft:hoes`, and `#minecraft:swords`. NeoForge's broader tool tags build
on those categories. Hammers join `#minecraft:pickaxes`, saws join
`#minecraft:axes`, and knives join `#minecraft:swords` because they carry the
corresponding real Tool component and field actions, not merely for recipe
classification.

The private `material_progression:knives`, `hammers`, and `saws` tags are
reloadable behavior boundaries. Each consumes its shared `c:tools/...`
category, so datapacks may refine Material Progression behavior while other
mods can publish compatible tools through the shared interface.

## Consuming compatible content

Recipes and machines should consume tags:

```json
{
  "ingredient": "#c:raw_materials/tin"
}
```

Shaped recipe keys use the same rule:

```json
{
  "key": {
    "#": "#c:rods/wooden",
    "X": "#c:ingots/bronze"
  }
}
```

Tool repair ingredients must use common ingot tags. Machine allowlists may
remain private behavior tags, but their values should be shared material tags:

```json
{
  "values": [
    "#c:ores/copper",
    "#c:ores/tin",
    "#c:raw_materials/copper",
    "#c:raw_materials/tin"
  ]
}
```

This makes `#material_progression:crusher_inputs` a behavior boundary without
hard-coding which mod supplies copper or tin.

## Legitimate concrete IDs

Tags describe equivalence, not outputs. Recipe result fields must name a
concrete item. World-generation configured features and registrations also
refer to concrete blocks. An input may use a concrete ID when the exact item is
the mechanic rather than merely one representative of a material category.

Do not create a private tag to disguise a single hard-coded material. Either
use the shared category or document why exact identity is required.

## New-content checklist

For every new material or material form:

1. Find the established vanilla and NeoForge tags before inventing a name.
2. Add the item to its `c:` subtype tag.
3. Add the subtype to its parent tag.
4. Add both block and item tags when the content exists in both registries.
5. Use the shared tag in all recipe and machine inputs.
6. Use the shared tag for repair ingredients and code membership checks.
7. Add tools to established vanilla tool tags.
8. Add literal expectations to the fast compatibility contracts.
9. Add a GameTest when runtime tag behavior affects gameplay.
10. Run `./gradlew headlessTest`.

The contract suite rejects direct Material Progression material IDs in recipe
inputs and verifies the published common-tag catalog. Expand those contracts
when a new material category is added.
