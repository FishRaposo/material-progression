# Primitive Resources and Tools

> **Status: partially implemented.** Family-aware loose rocks, ground sticks,
> Rock and flint-shard conversions, partial raw-stone drops, the flint hatchet,
> the default-enabled log-only axe requirement, Plant Fiber, and Flint/Bronze
> Knives, Hammers, and Saws now ship. Tree-adjacent stick-density tuning,
> workshop processing, and expanded plant yields remain subject to
> implementation and playtesting.

## Purpose

Progression should begin before the player smelts a metal ingot. Wood, stone,
gravel, flint, clay, and plants should already create useful choices and reasons
to read the generated world.

This is not intended to turn the opening into a primitive-survival checklist.
The goal is to make familiar Minecraft resources matter through a few direct,
legible interactions. The preferred configurable opening is:

> **Gather ground Rocks and sticks -> sharpen a Rock into a flint shard -> make
> a flint hatchet or knife -> harvest wood and plants -> enter ordinary
> crafting progression**

At the same time, cave biomes should make non-metal resources discoverable in
ways that reinforce exploration:

> **Find a lush cave -> gather abundant clay -> make pottery infrastructure**

These loops give the player useful work before metallurgy without delaying
metallurgy behind ceremony.

## Deterministic bootstrap

The primitive opening must never be gated by random flint.

No Tree Punching already presents loose rocks in the world, but its first knife
still requires actual flint. Only after obtaining that knife can the player cut
loose rocks into flint shards. This leaves the first tool dependent on finding
gravel and receiving a random drop before the player has meaningful agency.

This project removes that circular gate with three explicit inventory recipes:

> **1 Rock -> 1 flint shard**
>
> **1 flint -> 2 flint shards**
>
> **4 Rocks -> 1 cobblestone**

The first conversion is shapeless and fits in the player's 2x2 crafting grid.
It represents sharpening an ordinary Rock into a usable edge, with no knife,
workstation, knapping interface, or lucky flint drop required. A full flint item
remains the superior source because it produces two shards. Four unsharpened
Rocks can instead be consolidated into cobblestone; flint shards are not valid
ingredients for that recipe.

The item is **Rock** (`material_progression:rock`), while its world feature is
**loose rocks**. “Loose” describes how Rocks appear in the world, not the item
name. Rock is published under and consumed through `#c:rocks` so compatible
items from other mods participate in the same recipes.

This creates two complementary sources:

- Loose rocks are a distributed, deterministic bootstrap resource.
- Gravel is a concentrated source of flint, with every flint supplying two
  shards for replacement tools and sustained demand.
- Mined stone can drop loose rocks instead of ready-made cobblestone, completing
  the same material loop underground.

Sticks receive the same visible bootstrap treatment. Loose sticks are
persistent ground objects placed especially beneath trees and around shrubs.
Picking one up yields the ordinary vanilla stick; there is no separate
Material Progression stick item. Leaves remain a renewable fallback through
their existing stick drops, and dead bushes remain the dry-biome source.

Ground sticks provide the deterministic opening while leaf drops provide
continued availability. Loose rocks and ground sticks are low-profile,
non-colliding ground blocks, similar to tall grass as world objects and vines in
hand-breaking effort. They persist until collected, have several natural visual
variants, and exist only as world blocks rather than separate inventory items.
Loose rocks drop one Rock; ground sticks drop one vanilla stick. A reasonable
spawn must offer a clear finite route to one Rock and two sticks without asking
the player to break leaves repeatedly and wait for random drops.

Family-aware loose rocks revalidate when normal gameplay changes the raw stone
beneath up to eight tagged cover blocks. Direct support changes still use
vanilla shape updates; covered sources use targeted NeoForge events for player
breaking and placement, fluid-created blocks, tool transformations, explosions,
living-entity destruction, and piston movement. Commands, structure loading,
and other mod code that writes blocks directly without one of those gameplay
events are outside this reactive boundary. This explicit boundary avoids
perpetual block polling while covering ordinary survival-world changes.

Loose rocks generate broadly on valid solid overworld ground. Ground sticks
retain a sparse broad distribution so the bootstrap is not biome-gated, with
patch placement and later biome-density tuning concentrating them beneath trees
and around shrubs. Both use the reusable surface-resource placement design
recorded in
[the ground-resource design](superpowers/specs/2026-07-28-ground-resources-design.md).

## Configurable tree punching

Logs require an axe or hatchet by default, with a world/server configuration
toggle that restores vanilla harvesting for compatibility and player
preference.

When enabled, the first concrete goal becomes:

> **Gather a Rock and sticks -> sharpen the Rock -> make a flint hatchet -> cut
> the first tree**

This rule gives the primitive tool layer a real purpose. Without it, a player
can punch one log, make a wooden pickaxe, reach cobblestone, and bypass most of
the opening in seconds. With it, flint has a brief but necessary role before
ordinary wood crafting begins.

The rule is not intended to make the opening punitive:

- The first hatchet must fit in the 2x2 inventory crafting grid.
- Its ingredients must be obtainable without first harvesting logs.
- No knapping station or crafting minigame is required.
- Failure to find random flint must never halt progression.
- Once the player has the hatchet, ordinary wood output need not be reduced.

The current hatchet recipe is Minecraft's axe silhouette with the top row
removed:

```text
R S
  S
```

`R` is one flint shard. The two `S` positions are sticks. The sharp piece sits
beside the upper stick, with the other stick directly beneath that stick: an
upside-down L in the 2x2 inventory grid. String is deliberately absent.
Ordinary Minecraft tools do not require a binding material, and the first
wood-access tool should not be gated behind the later knife, plant-fiber, and
string loop.

The rule applies exclusively to blocks in `#minecraft:logs`. Blocks are
identified through that vanilla tag so correctly tagged modded logs participate
without compatibility patches. Valid tools are identified through the standard
vanilla `#minecraft:axes` item tag; hatchets belong to that tag and are
therefore a kind of axe for harvesting purposes.

With the rule enabled, breaking a log without a valid tool behaves like
breaking stone without a pickaxe: the block can be broken but produces no
drop. A valid axe or hatchet leaves the ordinary log loot table unchanged.
Leaves, planks, crafting tables, and other wooden blocks remain vanilla unless
they are deliberately members of `#minecraft:logs`.

The exact implementation boundary and required GameTests are recorded in
[the configurable log harvest rule design](superpowers/specs/2026-07-28-log-harvest-rule-design.md).

## Cave-biome resources

Underground resources should respond to the character of the cave rather than
being distributed as uniform noise.

### Clay

Clay should occur naturally in exposed underground deposits:

- Lush caves should be the premier underground clay source.
- Ordinary wet caves may contain smaller or less reliable pockets.
- Deposits should favor cave floors, underground water, and visibly damp areas.
- Clay should be locally abundant where it makes environmental sense rather
  than appearing as a clay ore embedded uniformly in dry stone.

Vanilla already associates lush caves with water and clay. Strengthening that
identity makes the biome economically important without inventing a new
resource. Discovering a lush cave should plausibly mean that pottery is solved
for the near future.

Clay connects world generation to the workshop. It can support bonsai pots and
other ceramic infrastructure, so easier underground access to clay makes those
systems available through cave exploration rather than river hunting or biome
coordinate knowledge alone.

### Gravel

Gravel should receive the same biome-aware attention. Exact placement is
undecided, but exposed cave deposits should make it a resource the player can
deliberately recognize and pursue.

Its main early value is bulk flint. A useful gravel deposit is therefore not
merely awkward cave fill; it supports replacement primitive tools and continued
plant processing after loose rocks have guaranteed the first tool.
Biome-specific abundance, vein shapes, flint yield, and interactions with
Fortune remain open design questions.

## Flint as premium primitive material

Flint is a special stone: sharper and more performance-oriented than ordinary
cobblestone, but less suitable for sustained work. Rocks are sharpened into
flint shards for primitive cutting tools; cobblestone represents several
unsharpened Rocks consolidated into a durable general-purpose material.

A candidate tool profile is:

- At least the same harvest capability as stone
- Faster mining or cutting
- More attack damage
- Lower durability
- Possibly different enchantability

That makes stone the cheap, durable primitive workhorse and flint the sharp,
disposable performance option. A flint pickaxe could be excellent for exploiting
soft or exposed cave geology while being a poor choice for long tunnels. It
would improve what the player can do within the primitive tier without unlocking
the next geological tier by itself.

An alternative is to treat flint as a small capability step above ordinary
stone because it is a deliberately selected, unusually useful stone rather than
generic cobble. Whether flint is premium performance within the stone tier or a
narrow intermediate tier of its own must be decided through the geology model,
not from tool statistics in isolation.

The exact conventional flint-tool roster is undecided. Flint pickaxes, axes,
shovels, hoes, and swords are candidates, not a commitment to reproduce every
vanilla tool. The knife, hammer, and saw belong to the current workshop
direction even though their exact material families remain open.

## Tools grant material interactions

Tools should be defined first by what they let the player do to materials, not
only by a harvest tier and a column of statistics.

- A hatchet grants the first access to logs when tree punching is disabled.
- A knife turns suitable vegetation into plant fiber.
- An axe accelerates active forestry.
- A knife installed in a workshop extracts more fiber from plants and more
  shards from loose rocks.
- A saw harvests like an alternative axe in the field and improves the yield of
  wood processed at a workshop.
- A hammer mines like an alternative pick in the field and deliberately reduces
  stone to gravel and gravel to sand at a workshop.
- A pick determines which geology can be exploited and whether sustained
  excavation is economical.
- Hoppers and hopper variants turn workable metals into collection and
  movement.
- Bonsai converts pottery, planting material, time, and collection
  infrastructure into supplemental tree products.

Statistics still express material identity. Flint may be faster and hit harder
than stone while breaking sooner. Bronze may offer an accessible manufactured
route into more serious excavation. Iron may make sustained digging practical.
Those profiles support the verbs; they are not a substitute for them.

This also explains why tool families need not be exhaustive. A material should
receive a knife, saw, pick, or other tool when that combination creates a useful
interaction or trade-off—not merely because every material is expected to
receive a recolored copy of every vanilla tool.

## Knives

Knives are the strongest candidate borrowed from No Tree Punching. They fit
Minecraft without importing that mod's complete survival premise.

The primitive version should be direct:

> **Flint shard + stick -> flint knife**

There is no required knapping system. Knapping can be enjoyable in a mod built
around primitive survival, but here it would add ceremony between finding flint
and using an object Minecraft already treats as a prepared sharp edge.

A knife can:

- Harvest grass, tall grass, ferns, vines, leaves, and other suitable plants.
- Produce plant fiber from those materials.
- Act as a reusable cutting ingredient in appropriate recipes.
- Extract more plant fiber from plants when installed in a workshop.
- Extract more flint shards from Rocks when installed in a workshop.
- Serve as a lightweight alternative to the sword.

The field interaction remains important: plants can still be broken with a
knife to obtain fiber. Workshop processing is the higher-yield version and
replaces No Tree Punching-like right-click knife recipes; it does not gate the
basic resource. A Rock can always be sharpened through the shapeless 2x2 recipe,
so the first hatchet never requires a workshop or a knife.

The implemented opening includes Flint and Bronze Knives. Both preserve normal
plant loot and add one Plant Fiber from short grass or two from a single
two-block tall grass. Recognition uses the reloadable knife behavior tag rather
than an item identity list. Flint has 64 durability; Bronze has 325 durability.
Bronze improves longevity without changing the field Fiber yield.

Knives may later continue through copper, tin, iron, silver, and other
materials, but a complete recolored family is not required unless those
differences justify it.

## Plant fiber and string

Plant fiber gives ordinary vegetation an immediate material role.

The intended loop is:

> **Cut suitable plants with a knife -> receive plant fiber -> combine fiber
> into string**

This creates an exploration and crafting route to string without making spiders
the sole early source. It can support bows, fishing rods, leads, wool-related
recipes, and future workshop components.

The system should remain simple:

- Players should not need a botany chart to know what can produce fiber.
- Different plants may have different yields only when the differences are easy
  to learn.
- Fiber costs should preserve spiders and cobwebs as valuable string sources.
- Ordinary hand-breaking may keep vanilla drops while the knife supplies fiber.

The first implemented field contract is deliberately narrow and deterministic:
short grass yields one Fiber, tall grass yields two, and three
`#c:fibers/plant` craft shapelessly into one String. Workshop plant processing
will broaden the accepted plants and yields without replacing these field
drops.

## Saws and active wood efficiency

Saws carry No Tree Punching's wood-efficiency idea into the workshop model.
Their defining role is improving the yield of actively harvested wood.

One candidate relationship is:

- Axes make tree harvesting faster.
- Saws produce more planks or components from each log.
- Bonsai provide a slow background supply of tree drops.
- Hoppers and hopper variants collect and route those products.

Those are complementary tools rather than replacements for one another. A saw
can harvest like an axe in the field and rewards active forestry through
higher-yield workshop processing; a bonsai supports the workshop passively.

Vanilla's four-plank inventory-crafting baseline is preserved. Flint and Bronze
Saws are real axe alternatives in the field and both satisfy the configured log
harvest rule. Workshop processing will provide the separately visible
efficiency bonus; no ordinary log recipe is replaced. Flint has 64 durability
and Bronze has 325 durability, while both will use the same workshop yield.

The preferred interface is the workshop block rather than a crafting recipe
that silently damages a reusable saw. The player installs the saw, supplies the
log, selects a valid output if necessary, and spends durability through the
visible processing operation. The same interaction can support knives and
hammers without turning the ordinary crafting grid into a hidden tool machine.

## The three workshop tools

The initial tool set follows one shared rule:

> **Outside the workshop, tools harvest. Inside the workshop, tools process.**

| Tool | Field role | Workshop role |
| --- | --- | --- |
| Knife | Weapon; plant harvesting | More fiber and flint shards |
| Hammer | Pickaxe alternative | Stone to gravel; gravel to sand |
| Saw | Axe alternative | Better plank and stick yield |

The field roles make each item independently usable. Flint Hammers carry
stone-level geology capability and Bronze Hammers carry iron-level capability,
reaching Dense geology but not Deep geology. Both also use the real pickaxe
Tool component for ordinary blocks. The workshop roles remain their intended
processing identities: controlled transformations and better recovery from
materials already gathered. Tool material changes durability and capability,
not processing yield.

## Selective influence from No Tree Punching

No Tree Punching is a reference for making wood, plants, gravel, and flint
participate in progression. It is not a template to copy wholesale.

Strong candidates:

- Configurably preventing logs from being harvested by hand
- A flint hatchet as the first wood-harvesting tool
- Loose Rocks as deterministic material for crafting flint shards
- Knives
- Plant fiber
- Craftable string from plants
- Saws as material-efficiency tools
- Persistent uses for primitive resources

Ideas requiring stronger justification:

- Knapping
- Severe baseline wood-output reductions
- Extra crafting stations or multi-stage rituals for their own sake

The key departure from No Tree Punching is the first-flint gate. Requiring a
flint knife to turn a loose rock into a usable shard preserves random gravel
drops as a prerequisite. Here a Rock can be sharpened into one shard directly
in the 2x2 player grid, so disabling tree punching creates deterministic
progression rather than a potentially impossible spawn.

The filter is the same one used throughout the project: a step belongs when it
creates a capability, choice, or meaningful efficiency gain. It does not belong
merely because it makes the opening longer.

## Relationship to geology and metallurgy

Primitive resources connect the two main systems before the first alloy exists:

1. Ground Rocks and sticks guarantee the materials for the first primitive
   tools.
2. A Rock sharpens into a flint shard, which crafts the first hatchet and earns
   access to wood when tree punching is disabled.
3. Knives turn plants into fiber and string.
4. Gravel provides flint in bulk, with each flint yielding two shards.
5. Cave shape and biome determine which larger deposits are naturally exposed.
6. Wet and lush caves provide clay for pottery.
7. Mined stone returns Rocks that can be sharpened into shards or combined four
   at a time into cobblestone.
8. These resources support exploration and the first workshop.
9. Copper, tin, and bronze then expand the player's authority over harder
   geology and more capable tools.
10. Saws, bonsai, hoppers, hopper variants, and local bulk crafting eventually
   turn material access into resource efficiency and automation.

No Tree Punching-style resource logic and Divergent Underground-style geology
therefore reinforce each other. A player cannot punch through a forest or strip
mine through a mountain with bare hands and primitive tools. Both systems make
the generated world's shape determine the available route until material
progress grants the ability to create a new one.

## Evaluation questions

Before this layer becomes committed content, test:

1. Does cave-biome resource placement create memorable route choices?
2. Does a lush cave reliably read as an excellent clay source?
3. Can every reasonable spawn find visible Rocks and sticks and reach the first
   hatchet without random drops?
4. Does disabling tree punching create a satisfying first goal?
5. Does gravel remain valuable after loose rocks remove its monopoly on flint?
6. Do flint tools offer a real alternative to stone without erasing stone?
7. Is the knife-and-fiber loop obvious without a guide?
8. Does plant string broaden the opening without trivializing existing sources?
9. Do saws reward metallurgy and forestry without requiring a baseline nerf?
10. Does every added step still feel like Minecraft rather than a survival
   simulation layered on top of it?
