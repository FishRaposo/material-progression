# Underground and Geology

> **Status: implemented opening slice.** The sixteen built-in stone families,
> their Loose Rocks, cobbles, fragment drops, and geological resistance ship in
> the development tree. Arbitrary third-party family registration is also
> implemented, together with datapack-configurable depth profiles for arbitrary
> non-built-in dimensions.

## Why geology is foundational

The underground is not a neutral container for ore. Caves, ravines, structures,
exposure, stone identity, and depth determine which routes are affordable.
Better materials increase the player's authority over that world:

> **Follow natural openings first; earn sustained excavation later.**

This is a physical rule, not an invisible lock. A player can attack difficult
geology early, but slow mining and lost drops make the cost legible.

## Built-in stone families

The opening pass recognizes exactly sixteen raw-stone families:

| Profile | Families |
| --- | --- |
| Soft (`-1`) | Calcite, Dripstone, Sulfur, Sandstone, Red Sandstone, Netherrack |
| Standard (`0`) | Stone, Granite, Diorite, Andesite, Tuff, Cinnabar, End Stone |
| Hard (`+1`) | Deepslate, Basalt, Blackstone |

Each family has a Rock. Stone keeps `material_progression:rock`; the other
fifteen have family-named Rock items. Stone uses vanilla Cobblestone and
Deepslate uses Cobbled Deepslate. The other fourteen families have custom
cobbled blocks that self-drop and smelt into their matching raw stone.

Slabs, stairs, walls, polished blocks, ores, Potent Sulfur, and Sulfur Spikes
are intentionally outside the raw-stone system.

## Geological resistance

Natural raw stone resolves a resistance level using:

> **dimension depth tier + family modifier - exposure modifier, clamped to
> L0-L3**

Any face adjacent to a non-sturdy block reduces the result by one level.

| Level | Name | Mining speed | Required capability |
| --- | --- | --- | --- |
| L0 | Exposed | Normal | Any correct Pickaxe or Hammer |
| L1 | Compacted | 2.5x slower | Stone-level |
| L2 | Dense | 4x slower | Iron-level, including Bronze |
| L3 | Deep | 6x slower | Diamond-level |

The built-in depth bases are:

- **Overworld:** L0 above Y48; L1 from Y48 through Y17; L2 from Y16 through
  Y-15; L3 at Y-16 and below.
- **Nether:** L0 at Y96 and above; L1 from Y95 through Y64; L2 from Y63 through
  Y32; L3 at Y31 and below.
- **End:** enclosed End Stone begins at L2.
- **Other dimensions:** use their reloadable geology dimension profile, or L0
  at every height when no profile targets that dimension. This unconfigured
  fallback is deterministic and requires no empty profile resource.

The core Overworld, Nether, and End rules are immutable built-ins so a datapack
cannot silently change the opening's baseline. The public
`material_progression:geology_dimension_profile` data interface targets any
other dimension ID without a code change. Its ordered, inclusive Y thresholds
resolve a base level before family resistance and exposure are applied. The
complete schema, validation rules, and authoring example are in
[Compatibility policy](COMPATIBILITY.md).

Raw stone placed by a player always mines at L0, while still fragmenting into
Rocks. A compact persistent chunk attachment records these positions. Saving
and loading preserves markers, block removal clears them, and piston movement
transfers them. This protects building and cleanup from natural-geology
resistance without exempting placed stone from the material loop.

The independent server options `enableGeologicalHardness` and
`enableStoneRockDrops` let a world disable resistance or fragment drops without
coupling the two systems.

### Client mining prediction

The server remains authoritative for harvest capability, break progress, and
drops. A physical client requests a server-authoritative snapshot when it
starts mining a block. The response is tied to a monotonic request generation,
dimension, position, and exact runtime block-state ID, and carries the resolved
tier, the hardness toggle, and the current stone-family and dimension-profile
catalog revisions. External families, custom dimensions, exposure, and
player-placed L0 markers therefore use the same server resolver instead of
being approximated from client constants.

There is necessarily a network round trip before a remote server can answer.
During that pending window, only blocks in the synchronized
`#material_progression:stone_sources` parent tag use the conservative L3
divisor; unrelated blocks remain at normal speed. Every valid family must place
all of its source blocks in that parent, so supported built-in and third-party
families receive the safe pending behavior. The exact snapshot then applies one
of the L0-L3 divisors once. Snapshots expire after 20 ticks while mining is held,
which bounds how long a current target can retain an old datapack, config, block
state, exposure, or placed-marker result.

Changing the target, block state, dimension, or connection invalidates the
snapshot. Request generations prevent delayed or out-of-order packets from
affecting a newer target. On a high-latency connection, the first mining-crack
frame on a tagged family can therefore be slower than its eventual tier, never
faster; the dedicated server remains authoritative throughout.

## Raw-stone drops and cobbling

When stone-fragment drops are enabled, mining a raw family block gives:

- **Correct capability:** two or three matching Rocks.
- **Fortune I or higher:** exactly four matching Rocks.
- **Silk Touch:** the original raw block.
- **Incorrect capability:** no drop.

When the mod denies a raw-stone harvest, the server sends one throttled
action-bar explanation at mining start. Geological hardness keeps the detailed
tier requirement; with hardness disabled, an incorrect tool instead explains
that the family needs a Pickaxe or Hammer for Rock drops. Successful Rock-drop
replacement never sends this warning.

Cobbled and processed variants keep normal self-drops.

The single `material_progression:rock_cobbling` recipe accepts exactly four
`#c:rocks`. Four Rocks mapped to the same registered family produce that
family's cobble. Mixed mapped Rocks, or compatible third-party Rocks without a
family mapping, produce vanilla Cobblestone. Precedence is implemented inside
the custom recipe; there are no competing generic and family recipes.

## Family-aware Loose Rocks

Built-in Loose Rocks store their resolved family in block state. Arbitrary
external families use a synchronized block entity that stores the namespaced
family ID and exact Rock stack because datapacks cannot extend a block-state
enum. Both representations render the matching Rock, drop exactly one on
normal breaks and explosions, and break and drop when their support becomes
invalid.

Support resolves in this order:

1. An explicit direct surface mapping selects its family.
2. On approved natural cover, the resolver searches downward up to eight blocks
   using that cover tag's filter. Generic soil, gravel, snow, and sand cover
   select the nearest raw family. The dedicated Netherrack-cover tag accepts
   only Netherrack and scans past Basalt, Blackstone, or other raw families.
3. Placement is skipped when no family resolves. There is no generic Stone
   fallback.

Required direct mappings include Sand to Sandstone Rock, Red Sand to Red
Sandstone Rock, and every raw stone to its matching Rock. Natural soil and
gravel cover resolve the nearest Overworld geology beneath them. Soul Sand and
Soul Soil belong only to
`#material_progression:loose_rock_netherrack_cover`: they produce Netherrack
Rock when the nearest Netherrack within eight blocks resolves below, and place
nothing over Basalt, Blackstone, or any other geology when no Netherrack is in
range.

World generation places family-correct Loose Rocks on Overworld surfaces and
cave floors, with sparse Nether and End placement. The cave set includes
Sulfur, Cinnabar, Deepslate, Tuff, and Dripstone terrain. Loose Rocks are finite
world objects and do not regenerate; mining raw stone is their renewable source.

Normal support changes update directly. Covered sources are revalidated through
targeted gameplay events for player breaking and placement, fluids, tool
transformations, explosions, living-entity destruction, and pistons. Commands,
structure loading, and mod code that writes blocks without those events are
outside this reactive boundary.

Definitions under `data/<namespace>/stone_family/` expose the reloadable schema
and tag boundaries. The catalog retains the required sixteen built-ins and
accepts arbitrary additional namespaced IDs with externally registered Rock,
raw, and cobbled objects. External family blocks persist and synchronize their
identity, and loaded blocks reconcile changed or removed family definitions
after a successful reload. Reload validation is atomic and rejects missing or
ambiguous Rock tags, unregistered objects, invalid resistance ranges, source
blocks missing from the synchronized source parent, and duplicate raw, cobbled,
Rock, source, or direct-surface ownership with a clear error. The complete
authoring contract is in
[Compatibility policy](COMPATIBILITY.md).

## Discoverability

Attempting to mine above the held tool's geological capability produces
localized, throttled action-bar feedback that names the resolved level, family,
and requirement—for example, “Dense Cinnabar — iron-level Pickaxe or Hammer
required.” Encountering Dense geology also completes the corresponding opening
advancement.

Loose Rocks, Rock tooltips, recipe unlocks, and the first-Rock advancement teach
the material loop without a custom guidebook.

## Later geological work

The following systems remain later slices:

- Ore samples and deposits
- Richer surface evidence beyond Gravel Ore and bounded Hammer prospecting

The current industrial pass implements Gravel Ore for each shipped metal ore.
It replaces naturally generated Gravel only in the ore's valid domain and is
excavated with a Shovel at the corresponding harvest capability; it never
creates artificial Gravel terrain. Each shipped ore also has forms for locally
valid raw-stone hosts, including the supported Nether and End families, while
preserving material identity and dimension boundaries. Hammer prospecting is
loaded-area only and returns a throttled material-and-direction hint, never
coordinates.
- Cave-biome clay and broader gravel deposits
- Pottery and its geological resource loop
- Expanded ore replacement and duplicate-ore governance

These ideas may build on the current resistance and family vocabulary, but they
are not implemented by the opening pass.

## Success criteria

The system succeeds when caves and exposed routes are rational early choices,
intermediate tools expand access in visible steps, Stone identity survives
through Rocks and cobbles, and building remains comfortable. Automated tests
establish deterministic behavior; a 20-30 minute survival run through Bronze
and Dense geology remains the required balance and discoverability check before
publishing 0.2.0.
