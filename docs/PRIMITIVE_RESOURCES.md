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
legible interactions:

> **Find gravel -> obtain flint -> make a knife -> cut plants -> make string**

At the same time, cave biomes should make non-metal resources discoverable in
ways that reinforce exploration:

> **Find a lush cave -> gather abundant clay -> make pottery infrastructure**

These loops give the player useful work before metallurgy without delaying
metallurgy behind ceremony.

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

Its main early value is flint. A useful gravel deposit is therefore not merely
awkward cave fill; it is access to sharper primitive tools and plant processing.
Biome-specific abundance, vein shapes, flint yield, and interactions with
Fortune remain open design questions.

## Flint as premium primitive material

Flint is a special stone: sharper and more performance-oriented than ordinary
rock, but less suitable for sustained work.

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

## Knives

Knives are the strongest candidate borrowed from No Tree Punching. They fit
Minecraft without importing that mod's complete survival premise.

The primitive version should be direct:

> **Flint + stick -> flint knife**

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

- Knives
- Plant fiber
- Craftable string from plants
- Saws as material-efficiency tools
- Persistent uses for primitive resources

Ideas requiring stronger justification:

- Knapping
- Preventing ordinary tree punching
- Severe baseline wood-output reductions
- Extra crafting stations or multi-stage rituals for their own sake

The filter is the same one used throughout the project: a step belongs when it
creates a capability, choice, or meaningful efficiency gain. It does not belong
merely because it makes the opening longer.

## Relationship to geology and metallurgy

Primitive resources connect the two main systems before the first alloy exists:

1. Cave shape and biome determine which resources are naturally exposed.
2. Gravel provides flint.
3. Flint provides high-performance primitive tools and knives.
4. Knives turn plants into fiber and string.
5. Wet and lush caves provide clay for pottery.
6. These resources support exploration and the first workshop.
7. Copper, tin, and bronze then expand the player's authority over harder
   geology and more capable tools.
8. Saws, bonsai, hoppers, and ducts eventually turn material access into
   resource efficiency and automation.

No Tree Punching-style resource logic and Divergent Underground-style geology
therefore reinforce each other. Stone and wood matter because the player has
several ways to exploit them, while the generated world determines which
opportunities are available.

## Evaluation questions

Before this layer becomes committed content, test:

1. Does cave-biome resource placement create memorable route choices?
2. Does a lush cave reliably read as an excellent clay source?
3. Does gravel feel valuable without becoming mandatory busywork?
4. Do flint tools offer a real alternative to stone without erasing stone?
5. Is the knife-and-fiber loop obvious without a guide?
6. Does plant string broaden the opening without trivializing existing sources?
7. Do saws reward metallurgy and forestry without requiring a baseline nerf?
8. Does every added step still feel like Minecraft rather than a survival
   simulation layered on top of it?
