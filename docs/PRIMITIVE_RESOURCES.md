# Primitive Resources and Tools

> **Status: provisional.** The resource roles are part of the current vision;
> exact world generation, recipes, yields, statistics, and included tools remain
> design candidates.

## Purpose

Progression should begin before the player smelts a metal ingot. Wood, stone,
gravel, flint, clay, and plants should already create useful choices and reasons
to read the generated world.

This is not intended to turn the opening into a primitive-survival checklist.
The goal is to make familiar Minecraft resources matter through a few direct,
legible interactions. The preferred configurable opening is:

> **Gather loose rocks and sticks -> make a flint hatchet or knife -> harvest
> wood and plants -> enter ordinary crafting progression**

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

This project removes that circular gate entirely. A loose rock occupies the
same recipe role as a flint shard:

> **1 loose rock = 1 flint shard**
>
> **1 flint -> 2 flint shards**
>
> **Any 4 loose rocks or flint shards -> 1 cobblestone**

A loose rock is already a suitable piece of sharp stone. Primitive recipes
should accept one loose rock wherever they accept one flint shard; converting
the rock first is unnecessary. A full flint item is a compact source of two
shards, while four usable pieces can be consolidated into cobblestone. No
special knife is required to make a rock useful.

This creates two complementary sources:

- Loose rocks are a distributed, deterministic bootstrap resource.
- Gravel is a concentrated source of flint, with every flint supplying two
  shards for replacement tools and sustained demand.
- Mined stone can drop loose rocks instead of ready-made cobblestone, completing
  the same material loop underground.

Sticks must be similarly obtainable before wood processing, through leaves,
shrubs, ground vegetation, world objects, or another legible source. Exact
generation, pickup behavior, and early stick sources remain undecided. The
success condition is not a guaranteed item at the player's feet; it is that any
reasonable spawn provides a clear, finite route to the first tool without
luck-dependent drops.

## Configurable tree punching

Preventing logs from being harvested by hand is a strong candidate for the
default experience, with a configuration toggle for compatibility and player
preference.

When enabled, the first concrete goal becomes:

> **Gather rocks and sticks -> make a flint hatchet -> cut the first tree**

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

`R` is one loose rock or flint shard. The two `S` positions are sticks. The
sharp piece sits beside the upper stick, with the other stick directly beneath
that stick: an upside-down L in the 2x2 inventory grid. String is deliberately
absent. Ordinary Minecraft tools do not require a binding material, and the
first wood-access tool should not be gated behind the later knife, plant-fiber,
and string loop.

The toggle's exact default, block-breaking feedback, interaction with modded
logs, and whether bare hands produce no drop or cannot meaningfully damage logs
remain implementation and playtesting questions.

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
cobblestone, but less suitable for sustained work. Loose rocks and flint shards
share this primitive sharp-edge role; cobblestone represents several pieces
consolidated into a durable general-purpose material.

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

The exact tool roster is undecided. Flint pickaxes, axes, shovels, hoes, swords,
and knives are candidates, not a commitment to reproduce every vanilla tool.

## Tools grant material interactions

Tools should be defined first by what they let the player do to materials, not
only by a harvest tier and a column of statistics.

- A hatchet grants the first access to logs when tree punching is disabled.
- A knife turns suitable vegetation into plant fiber.
- An axe accelerates active forestry.
- A saw may improve the yield of processed wood.
- A pick determines which geology can be exploited and whether sustained
  excavation is economical.
- Hoppers and ducts turn workable metals into collection and movement.
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

The primitive version should be direct, accepting either form of usable sharp
stone:

> **Loose rock or flint shard + stick -> flint knife**

There is no required knapping system. Knapping can be enjoyable in a mod built
around primitive survival, but here it would add ceremony between finding flint
and using an object Minecraft already treats as a prepared sharp edge.

A knife can:

- Harvest grass, tall grass, ferns, vines, leaves, and other suitable plants.
- Produce plant fiber from those materials.
- Act as a reusable cutting ingredient in appropriate recipes.
- Serve as a weak, lightweight weapon if that role survives testing.

Knives may continue through copper, tin, bronze, iron, silver, and other
materials. Better knife materials could change durability, cutting speed, fiber
yield, or crafting utility. A complete knife family is not required unless those
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

Exact plants, drop chances, fiber-to-string ratios, and recipe interactions are
undecided.

## Saws and active wood efficiency

Saws are a plausible second idea from No Tree Punching, but less settled than
knives. Their useful role would be improving the yield of actively harvested
wood.

One candidate relationship is:

- Axes make tree harvesting faster.
- Saws produce more planks or components from each log.
- Bonsai provide a slow background supply of tree drops.
- Hoppers and ducts collect and route those products.

Those are complementary tools rather than replacements for one another. A saw
rewards active forestry; a bonsai supports the workshop passively.

The current preference is to test bonus saw output before reducing vanilla's
baseline log-to-plank yield. Preserving ordinary output and making saws an
efficiency reward is less likely to make the opening feel arbitrarily stingy.
However, baseline wood output, saw recipes, valid materials, durability,
processing interface, and exact bonuses all remain unresolved.

## Selective influence from No Tree Punching

No Tree Punching is a reference for making wood, plants, gravel, and flint
participate in progression. It is not a template to copy wholesale.

Strong candidates:

- Configurably preventing logs from being harvested by hand
- A flint hatchet as the first wood-harvesting tool
- Loose rocks as direct substitutes for flint shards
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
drops as a prerequisite. Here the loose rock already counts as a shard, so
disabling tree punching creates deterministic progression rather than a
potentially impossible spawn.

The filter is the same one used throughout the project: a step belongs when it
creates a capability, choice, or meaningful efficiency gain. It does not belong
merely because it makes the opening longer.

## Relationship to geology and metallurgy

Primitive resources connect the two main systems before the first alloy exists:

1. Loose rocks and vegetation guarantee access to the first primitive tools.
2. A flint hatchet, made with either a rock or shard, earns access to wood when
   tree punching is disabled.
3. Knives turn plants into fiber and string.
4. Gravel provides flint in bulk, with each flint yielding two shards.
5. Cave shape and biome determine which larger deposits are naturally exposed.
6. Wet and lush caves provide clay for pottery.
7. Mined stone returns loose rocks that can be used directly or recombined into
   cobblestone.
8. These resources support exploration and the first workshop.
9. Copper, tin, and bronze then expand the player's authority over harder
   geology and more capable tools.
10. Saws, bonsai, hoppers, and ducts eventually turn material access into
   resource efficiency and automation.

No Tree Punching-style resource logic and Divergent Underground-style geology
therefore reinforce each other. A player cannot punch through a forest or strip
mine through a mountain with bare hands and primitive tools. Both systems make
the generated world's shape determine the available route until material
progress grants the ability to create a new one.

## Evaluation questions

Before this layer becomes committed content, test:

1. Does cave-biome resource placement create memorable route choices?
2. Does a lush cave reliably read as an excellent clay source?
3. Can every reasonable spawn reach the first hatchet without random flint?
4. Does disabling tree punching create a satisfying first goal?
5. Does gravel remain valuable after loose rocks remove its monopoly on flint?
6. Do flint tools offer a real alternative to stone without erasing stone?
7. Is the knife-and-fiber loop obvious without a guide?
8. Does plant string broaden the opening without trivializing existing sources?
9. Do saws reward metallurgy and forestry without requiring a baseline nerf?
10. Does every added step still feel like Minecraft rather than a survival
   simulation layered on top of it?
