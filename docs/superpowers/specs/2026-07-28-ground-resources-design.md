# Ground Resources and Primitive Bootstrap Design

**Status:** Approved design for the first consumer of the reusable surface
world-placement foundation.

## Goal

Guarantee a visible, deterministic route from a fresh overworld spawn to the
first axe-capable tool without punching logs or waiting for random leaf or
gravel drops.

## Player-visible loop

The complete bootstrap is:

> Find loose rocks and ground sticks -> break them by hand -> sharpen one Rock
> into a flint shard -> craft a flint hatchet -> harvest logs

Loose rocks and ground sticks are persistent, low-profile ground blocks rather
than item entities. They behave like grass-sized environmental details:

- no collision;
- a low selection shape close to the ground;
- easy hand breaking, around vine durability;
- immediate ordinary block drops;
- persistent until collected;
- no inventory item for either world block.

A loose-rock block drops one `material_progression:rock`. A ground-stick block
drops one `minecraft:stick`.

The Rock item is named **Rock**, not Loose Rock, and is published under
`#c:rocks`. The ground-stick block does not create a second kind of stick.

## Placement

Both resources use reusable surface-resource placement rules:

1. choose a horizontal position within the chunk;
2. resolve the motion-blocking surface;
3. require an empty replaceable position;
4. require a sturdy, valid supporting block below;
5. ask the resource block whether it can survive at that position;
6. place one persistent block.

Loose rocks generate broadly across overworld surface biomes. Ground sticks
also retain a sparse broad distribution so the deterministic bootstrap is not
biome-gated; patch placement and later biome-density tuning should concentrate
them beneath trees and around shrubs. Desert and other dry-biome stick supply
continues to be reinforced by vanilla dead bushes.

The two resources share placement machinery but retain separate configured and
placed features, biome selection, attempt counts, and local acceptance rules.
Datapacks can therefore rebalance or replace either distribution independently.

Exact density is an initial playtest value, not a permanent balance contract.
The automated contract is spawn viability: representative generated overworld
terrain must expose enough valid placements that a player can obtain at least
one Rock and two sticks without random mob, gravel, or leaf drops.

## Visuals

Each block uses several model variants selected through weighted blockstate
variants. Variants may rotate and mirror the same small texture geometry. The
first implementation may use intentionally simple project-owned placeholder
art, but must not alias the block to a misleading full-cube or plant model.

The blocks are worldgen-only. They have blockstates, models, loot tables, and
translations for accessibility and commands, but no registered `BlockItem` and
no creative-tab entry.

## Bootstrap content

This slice also introduces:

- `material_progression:rock`;
- `material_progression:flint_shard`;
- an axe-capable `material_progression:flint_hatchet`;
- `1 #c:rocks -> 1 flint shard`;
- `1 minecraft:flint -> 2 flint shards`;
- `4 #c:rocks -> 1 cobblestone`;
- the approved upside-down-L 2x2 flint-hatchet recipe.

The hatchet recipe consumes the shared flint-shard tag established by the
target NeoForge common-tag vocabulary and `#c:rods/wooden`. The Rock recipes
consume `#c:rocks`.

The hatchet is deliberately the only flint tool in this foundation slice. The
knife, plant fiber, stone-drop replacement, and broader flint family remain
separate gameplay experiments.

## Architecture

`GroundResourceBlock` owns the shared plant-like geometry, collision, survival,
and easy-breaking behavior. Concrete registrations supply their distinct shape
and loot identity.

World generation remains data-driven:

- configured features select the concrete ground block;
- placed features define frequency, horizontal spread, surface resolution, and
  biome filtering;
- NeoForge biome modifiers attach them to compatible overworld biomes.

Only placement behavior that vanilla modifiers cannot express cleanly belongs
in Java. Any custom placement modifier is registered once and parameterized
rather than duplicated per resource.

## Testing

Fast contracts prove:

1. both world blocks have complete blockstate, model, loot, translation, and
   worldgen chains;
2. neither world block has an item model or registered inventory form;
3. Rock and flint shard publish the required common tags;
4. all four primitive recipes use shared tags for interchangeable inputs;
5. the flint hatchet belongs to vanilla axe and enchantment tags.

Live GameTests prove:

1. a loose-rock block survives on representative solid ground and drops one
   Rock when broken by hand;
2. a ground-stick block survives on representative solid ground and drops one
   vanilla stick when broken by hand;
3. unsupported resources break rather than float;
4. the four recipes assemble their literal outputs through the real recipe
   manager;
5. the flint hatchet exposes the standard axe digging ability;
6. representative placement attempts reject invalid surfaces and accept valid
   surface positions;
7. a deterministic generated fixture can supply at least one Rock and two
   sticks.

Human playtesting remains responsible for final density, visibility, visual
variety, and whether the opening route feels natural.

## Alternatives rejected

### Dropped item entities

Item entities despawn, merge, move in fluids, and may vanish before a player
reaches them. They cannot serve as reliable terrain resources.

### Full-cube decorative blocks

Full cubes would read as placed construction material and create needless
collision. The resources should read like small objects lying on terrain.

### Random leaf and gravel drops as the only source

Both routes can fail repeatedly before the player has any agency. They remain
renewable or concentrated sources after the deterministic ground bootstrap.
