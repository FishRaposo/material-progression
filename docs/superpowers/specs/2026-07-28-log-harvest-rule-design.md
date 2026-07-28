# Configurable Log Harvest Rule Design

**Status:** Approved design for the first consumer of the configuration and
progression-rule foundations.

## Goal

Make the intended opening require an axe or hatchet before logs yield wood,
while preserving ordinary Minecraft behavior everywhere else and allowing a
world or server to opt out.

## Scope

Together with the ground-resource bootstrap slice, this introduces:

- a world/server configuration foundation;
- one centralized harvest rule;
- runtime tests proving the rule's exact boundary.

It does not introduce a general No Tree Punching ruleset. It does not change
leaves, planks, wooden blocks, crafting recipes, tool durability, or baseline
wood yields. The primitive Rock, flint-shard, ground-stick, and hatchet content
ships in the same playable slice so the default rule cannot create an
impossible opening. Its exact behavior is recorded in
[the ground-resource design](2026-07-28-ground-resources-design.md).

## Player-visible behavior

The configuration option `requireAxeForLogs` is enabled by default.

When enabled:

- Every block in the vanilla `#minecraft:logs` block tag requires an
  axe-capable held item to produce its normal drops.
- Breaking a log without a valid tool behaves like breaking stone without a
  pickaxe: the block can still be broken, but it yields no drops.
- An axe or hatchet permits the log's ordinary loot table to run unchanged.
- Leaves, planks, crafting tables, chests, fences, and other wooden blocks are
  unaffected unless they are deliberately published in `#minecraft:logs`.

When disabled, Material Progression does not alter log harvesting. Bare-handed
log breaking and drops return to vanilla behavior.

The setting is authoritative for the whole world/server rather than a
per-player preference. A singleplayer world can opt out through the same
server-configuration mechanism.

## Compatibility boundary

Blocks are recognized through `#minecraft:logs`; there is no hard-coded list of
oak, nether, or Material Progression block IDs. This includes ordinary logs,
stripped logs, wood, stems, and hyphae when their owners correctly publish them
to the vanilla tag.

Tools are recognized through the vanilla `#minecraft:axes` item tag rather than
by checking for the vanilla `AxeItem` class or a private Material Progression
item list. The flint hatchet is published as an axe. Properly tagged modded axes
and hatchets therefore work without compatibility patches.

This separation is deliberate:

- block identity comes from the established vanilla block tag;
- tool category comes from the established vanilla item tag;
- configuration controls whether the rule participates at all.

## Architecture

`MaterialProgressionConfig` owns registration, defaults, validation, and access
to the server setting. The configuration API exposes the resolved boolean; no
event handler reads raw config-file structures directly.

`LogHarvestRule` owns the pure policy:

1. If the setting is disabled, defer to the existing harvest result.
2. If the block is not in `#minecraft:logs`, defer to the existing result.
3. If the held item belongs to `#minecraft:axes`, defer to the existing harvest
   result.
4. Otherwise, deny harvesting.

One NeoForge event adapter translates the live harvest event into that policy
and changes only the harvest/drop decision. It does not cancel block breaking,
replace loot tables, delete item entities after spawning, or alter unrelated
block speed and hardness.

Future progression rules may share the event adapter and configuration
registration, but they must remain separate policy units. This first rule is
not a generic list of arbitrary block/tool pairs and does not justify a
data-driven rules engine by itself.

## Alternatives rejected

### Cancel bare-handed log breaking

This communicates the gate strongly, but it does not match Minecraft's existing
wrong-tool behavior. It also requires client-visible break-speed or cancellation
feedback and is more invasive than the requested rule.

### Remove or rewrite log recipes

Recipe changes occur after the log has already been harvested and therefore
cannot create the intended first-tool gate. They also risk affecting unrelated
wood crafting and mod compatibility.

### Apply a broad No Tree Punching ruleset

Blanket restrictions create collateral damage across leaves, plants, wooden
blocks, and other early interactions. Material Progression needs one narrow
capability gate, not a survival overhaul.

## Testing

Live NeoForge GameTests must prove observable drops rather than source
structure:

1. With the option enabled, an empty-handed player breaks an oak log and
   receives no log.
2. With the option enabled, an axe-capable Material Progression tool breaks an
   oak log and receives its normal log drop.
3. With the option disabled, an empty-handed player breaks an oak log and
   receives its normal log drop.
4. With the option enabled, an empty-handed player breaks oak planks and still
   receives the plank drop.
5. With the option enabled, representative non-overworld log-tag members such
   as a crimson stem follow the same rule.

Test fixtures must restore configuration state after every test. Production
code receives no test-only reset methods; configuration cleanup belongs in the
GameTest fixture.

The regression this suite catches is expansion or weakening of the boundary:
checking concrete vanilla IDs instead of the log tag, checking concrete axe
classes instead of the axe tag, applying the rule to all wooden blocks,
ignoring the opt-out, or canceling ordinary loot even with a valid tool.

## Release constraint

The default-enabled rule and deterministic, log-independent route to an
axe-capable hatchet are one release unit. Rock, ground sticks, flint shards, and
the 2x2 flint-hatchet recipe must be verified together before the installable
production JAR is refreshed.
