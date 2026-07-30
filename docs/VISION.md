# Vision

> **Status: provisional.** This document captures the current direction, not a
> locked specification. The primitive opening, Manual Workshop, and first
> geology/access slice now exist; pottery, ore prospecting, logistics, bonsai,
> bulk crafting, and expanded metallurgy remain later systems.

Minecraft's progression is nominally about mining and crafting, but much of the
generated underground stops mattering almost immediately. Once a player has an
iron pickaxe and knows the right coordinates, solid stone becomes empty travel
time between the surface and diamond. Caves, ravines, mineshafts, and geological
variation are often less efficient than digging a straight tunnel.

At the same time, adding more ores does not solve that problem. If early iron
still grants cheap access to the entire underground, copper, tin, bronze, steel,
silver, and other materials become optional clutter around the shortest route to
diamond.

Material Progression explores a different structure:

> **Progression should increase the player's ability to control the world.**

Early players should depend on the terrain. They follow exposed deposits, use
caves as breaches through resistant rock, and value ravines, mineshafts, and
other pre-cut routes. Processing accessible metals produces better tools.
Those tools expand the spaces that can be exploited, which exposes new
materials, which enable new forms of processing and equipment.

The intended rhythm is approximately:

1. Gather visible ground Rocks and sticks to establish deterministic primitive
   capability.
2. Sharpen a Rock into a flint shard, make the first cutting tools, and, when
   configured, earn access to wood.
3. Follow natural openings through forests and geology.
4. Read cave biomes as sources of clay, gravel, and exposed materials.
5. Improve yield through simple processing.
6. Combine modest metals into useful alloys.
7. Use better equipment to penetrate and reshape harder geology.
8. Develop from opportunistic cave mining into sustained excavation.
9. Earn efficient strip mining as a late capability.
10. Turn clay, wood, and metals into a workshop that supports manual
   tool-assisted processing before automating repetitive supply, item handling,
   and bulk crafting.
11. Continue material progression through specialization and magical metallurgy,
   not only larger durability and mining-speed numbers.

This is not intended to prohibit sequence breaking with invisible rules. A
player may attack a difficult stone layer early if they wish; the cost in time,
tool wear, and lost drops should make the terrain itself the constraint.
Knowledge remains useful, but coordinates alone should not erase progression.

## What the mod is

- A progression rework expressed through Minecraft's physical systems
- A broad metallurgical vocabulary of ores, metals, dusts, alloys, and tools
- An underground whose shape and composition affect how the player advances
- A deterministic primitive layer in which loose rocks, flint, plants, wood,
  clay, and gravel create useful choices before metallurgy
- A material graph with overlapping paths and long-lived ingredients
- A modest workshop progression of hand tools, pots, hoppers, processors,
  storage, a bulk-crafting table, and physical automation
- A bridge from mundane metallurgy to enchantability and artifact crafting

## What the mod is not trying to become

- A linear RPG or mandatory quest-book pack
- A research-menu progression gate
- A large energy network disguised as early metallurgy
- An electrical technology mod in which every convenience requires generators,
  cables, and machine tiers
- A machine chain where every operation exists solely to add waiting
- A collection of ores whose only identity is a higher tool statistic
- A punishment mod that makes ordinary building stone irritating after placement

## The complete loop

The foundational loop is:

> **Explore -> extract -> process -> combine -> craft -> gain agency**

Geology supplies the pressure that makes metallurgy necessary. Metallurgy
supplies the capabilities that let the player overcome geology. Infrastructure
then converts the resources the player earned into a workshop that handles more
of its own repetitive labor. Later material properties, finishing processes,
and enchantability extend the same loop beyond basic mining.

## Progression begins before metal

Wood and stone should matter for longer than the first few minutes, but the
opening should remain unmistakably Minecraft.

The current primitive-resource direction is deliberately simple:

- The item is Rock; loose rocks are its persistent world feature.
- One Rock sharpens into one flint shard through a shapeless 2x2 recipe.
- One flint produces two shards; four same-family Rocks form their matching
  cobble, while mixed or unmapped compatible Rocks form Cobblestone.
- Persistent Ground Sticks yield vanilla Sticks, while leaves remain a renewable
  fallback. Their current broad clusters still require true tree/shrub-aware
  density tuning.
- A configurable rule can prevent logs from being harvested by hand.
- A flint hatchet made from the sharpened shard then becomes the first goal and
  earns access to wood.
- Lush caves are excellent sources of clay for pottery and later bonsai.
- Other wet caves may contain smaller clay pockets.
- Cave gravel provides flint in bulk after loose rocks guarantee the bootstrap.
- Flint supplies accessible low-durability tools at stone capability.
- Flint and Bronze Knives cut plants into Fiber.
- Three Plant Fiber provide a crafting route to String.
- Bronze extends Knife, Hammer, and Saw durability.
- Workshop knives extract more fiber from plants and more shards from rocks.
- Hammers mine like alternative picks, then reduce stone to gravel and gravel to
  sand at a workshop.
- Saws harvest like alternative Axes, then turn an ordinary Log into six
  matching Planks or one Plank into three Sticks at a Workshop.

The knife is the clearest idea borrowed from No Tree Punching. The broader
survival ceremony is not. Flint already communicates "sharp stone" in
Minecraft, so it can become a knife directly instead of requiring knapping.

The same rule removes No Tree Punching's first-flint problem. Rocks do not need
a flint knife before they become useful; the player can sharpen one into a
shard through a shapeless inventory recipe. The opening still asks the player
to gather materials, but it cannot be invalidated by gravel placement or
flint-drop luck.

The implemented Saw rule preserves vanilla's four-Plank baseline and makes
Workshop sawing a 50 percent bonus. Flint and Bronze have identical processing
yield; Bronze buys durability. This makes the Saw valuable without making
ordinary crafting deliberately inefficient.

This gives the opening several connected but distinct tools:

- Stone provides dependable primitive impact and excavation, then returns Rocks
  instead of ready-made cobblestone.
- Rocks sharpen into the first flint shards or combine four at a time into
  cobblestone.
- Flint provides sharp, disposable performance.
- Hatchets earn access to wood when tree punching is disabled.
- Knives turn vegetation into fiber and string.
- Axes accelerate forestry.
- Saws improve active wood efficiency at the Manual Workshop.
- Bonsai may provide slow passive tree products.

The first hatchet uses an upside-down-L recipe in the 2x2 inventory grid: one
flint shard beside the upper stick, and a second stick directly beneath that
stick. It is the axe silhouette with its top row removed. It does not require
string; plant fiber and string are unlocked by the knife rather than used to
gate the first wood-access tool.

These are examples of a broader tool rule: tools grant interactions with
materials. Hatchets grant wood access, knives cut plants and separate useful
material, hammers mine or crush, saws harvest or subdivide wood efficiently, and
picks gradually turn resistant geology from an obstacle into excavatable
terrain. Material statistics express how well a tool performs those jobs, but
progression should not collapse into recolored stat ladders.

See [Primitive Resources and Tools](PRIMITIVE_RESOURCES.md) for the complete
living design.

## The workshop grows with the player

Progression should not only improve what the player can hold in an equipment
slot. It should change what their base can do. The first Manual Workshop now
provides that transition.

The current direction uses deliberately ordinary Minecraft objects:

- **The Manual Workshop** holds an installed hand tool and uses it to process an
  input material.
- **Knives, hammers, and saws** remain usable as alternatives to swords, picks,
  and axes in the field, but select their intended processing operations at the
  workshop while losing durability through visible use.
- **Bonsai pots** slowly produce tree drops, especially the sticks demanded by
  an expanded metallurgical workshop.
- **Clay** gives the pot a physical construction cost tied to exploration and
  excavation.
- **Hoppers** automate collection once the player can afford the additional
  metal infrastructure.
- **Alternative-metal hoppers** question vanilla's arbitrary iron exclusivity
  and give workable metals another continuing use.
- **Hopper variants and larger chests** improve local storage and movement
  without creating an abstract logistics network.
- **A bulk-crafting table** selects recipes explicitly, plans intermediate
  crafts, and consumes materials from the player, its own buffer, and directly
  adjacent inventories.
- **Crushers and furnaces** remain fuel-burning processing blocks rather than
  excuses to introduce electricity.

The workshop's core rule is **tool + material -> processed output**:

- A knife extracts more plant fiber from plants and more flint shards from
  Rocks.
- A hammer turns stone into gravel and gravel into sand.
- A hammer crushes ore into two dust at a deliberately high durability cost.
- A saw turns logs into more than the default four planks and planks into more
  sticks than ordinary crafting.

Outside the workshop, the knife is a weapon and plant harvester, the hammer is a
pickaxe alternative, and the saw is an axe alternative. Inside it, they perform
their intended processing roles:

> **Outside the workshop, tools harvest. Inside the workshop, tools process.**

The installed tool determines the operation; the block is not a universal
machine.

This gives reusable tools a physical, legible home. Durability is spent by
performing workshop operations instead of through special crafting recipes that
silently damage an ingredient. Manual ore crushing and the fuel-burning crusher
both produce two dust per ore: the hammer consumes substantial durability,
while the crusher consumes fuel and preserves tools. No hammer tier gates ore
processing; better hammers simply survive more work. Heating remains in
furnaces, alloying remains in its own metallurgical process, and passive
cultivation remains in bonsai.

None of the later automation implementations is fixed. Bonsai output may be manual before a
hopper is added; hoppers may be equivalent across materials or divided into
variants; and bulk-crafting upgrades require testing. The durable idea is that
automation should be assembled from resources the player physically earned and
should remain legible as Minecraft machinery rather than becoming a remote
storage network.

The bulk-crafting table is distinct from the manual workshop. It handles
ordinary recipes by explicit output selection, so recipe-shape conflicts cannot
make the wrong item. For bulk jobs it recursively resolves intermediate recipes:
logs can satisfy a request for sticks by becoming planks first. Its reach stops
at the player's inventory, its internal buffer, and directly adjacent
inventories. Fixed upgrade slots accept tiered modules for storage, filtering,
priority, reservation, and recipe memory, allowing specialization without
unbounded capacity.

The bonsai illustrates the intended economy particularly well. It does not give
the player free wood. The player acquires clay, makes the pot, provides a tree
or sapling, spends metal on collection, allocates space, and waits. In exchange,
the workshop gains a slow background stream of common biological materials.
Progress turns recurring manual labor into infrastructure.

Both the primitive and geological progressions express the same world-shape
principle:

> **The player initially follows natural openings, then earns the ability to
> create new ones.**

A forest cannot be punched through and a mountain cannot be strip-mined with
primitive tools. Clearings, cave mouths, ravines, and structures determine
early routes. Hatchets, axes, saws, and stronger picks gradually turn those
obstacles into resources.

The opening fixes sixteen built-in stone families, four geological resistance
levels, arbitrary third-party family extension, and a first Bronze-access
relationship for testing. Datapack-configurable depth profiles extend the same
access rules to arbitrary non-built-in dimensions. Bonsai, hopper variants,
larger chests, bulk-crafting upgrades, pottery, samples, deposits, prospecting,
and expanded metallurgy remain open later systems. The implemented Bronze slice
is still an experiment used to learn what the final loop should be.
