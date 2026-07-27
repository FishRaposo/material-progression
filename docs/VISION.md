# Vision

> **Status: provisional.** This document captures the current direction, not a
> locked specification.

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

1. Gather loose rocks and sticks to establish deterministic primitive
   capability.
2. Make the first cutting tools and, when configured, earn access to wood.
3. Follow natural openings through forests and geology.
4. Read cave biomes as sources of clay, gravel, and exposed materials.
5. Improve yield through simple processing.
6. Combine modest metals into useful alloys.
7. Use better equipment to penetrate and reshape harder geology.
8. Develop from opportunistic cave mining into sustained excavation.
9. Earn efficient strip mining as a late capability.
10. Turn clay, wood, and metals into workshop infrastructure that automates
   repetitive supply and item handling.
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
- A modest workshop progression of pots, hoppers, ducts, processors, storage,
  and physical automation
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

- Loose rocks provide guaranteed sharp stone without random flint drops.
- One loose rock is equivalent to one flint; four form one cobblestone.
- A configurable rule can prevent logs from being harvested by hand.
- A flint hatchet then becomes the first goal and earns access to wood.
- Lush caves are excellent sources of clay for pottery and later bonsai.
- Other wet caves may contain smaller clay pockets.
- Cave gravel provides flint in bulk after loose rocks guarantee the bootstrap.
- Flint may support sharper, faster, less durable tools at stone harvest level.
- A flint knife cuts plants into fiber.
- Plant fiber provides a crafting route to string.
- Metal knives may extend the cutting-tool family.
- Saws may later improve active wood yield.

The knife is the clearest idea borrowed from No Tree Punching. The broader
survival ceremony is not. Flint already communicates "sharp stone" in
Minecraft, so it can become a knife directly instead of requiring knapping.

The same rule removes No Tree Punching's first-flint problem. Loose rocks do not
need a flint knife before they become useful; they are immediately equivalent to
flint. The opening still asks the player to gather materials, but it cannot be
invalidated by gravel placement or flint-drop luck.

Saws remain more provisional. They could let copper, bronze, or later metals
improve the output of active forestry. The preferred first experiment is to
preserve vanilla wood output and make sawing a bonus, rather than reduce
baseline output and force the player to recover it.

This gives the opening several connected but distinct tools:

- Stone provides dependable primitive impact and excavation.
- Loose rocks guarantee the first sharp tools and can form cobblestone.
- Flint provides sharp, disposable performance.
- Hatchets earn access to wood when tree punching is disabled.
- Knives turn vegetation into fiber and string.
- Axes accelerate forestry.
- Saws may improve active wood efficiency.
- Bonsai may provide slow passive tree products.

See [Primitive Resources and Tools](PRIMITIVE_RESOURCES.md) for the complete
living design.

## The workshop grows with the player

Progression should not only improve what the player can hold in an equipment
slot. It should change what their base can do.

The current direction uses deliberately ordinary Minecraft objects:

- **Bonsai pots** slowly produce tree drops, especially the sticks demanded by
  an expanded metallurgical workshop.
- **Clay** gives the pot a physical construction cost tied to exploration and
  excavation.
- **Hoppers** automate collection once the player can afford the additional
  metal infrastructure.
- **Alternative-metal hoppers** question vanilla's arbitrary iron exclusivity
  and give workable metals another continuing use.
- **Copper ducts** extend item movement through a material whose physical
  fantasy already fits a conduit.
- **Crushers and furnaces** remain fuel-burning processing blocks rather than
  excuses to introduce electricity.

None of those implementations is fixed. Bonsai output may be manual before a
hopper is added; hoppers may be equivalent across materials or divided into
tiers; ducts may be limited or omitted after testing. The durable idea is that
automation should be assembled from resources the player physically earned and
should remain legible as Minecraft machinery.

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

The exact stones, strata, metals, recipes, tiers, dimensions, and pacing remain
open design questions. The same is true of bonsai, hopper variants, ducts, and
all automation details. Even the currently implemented bronze slice is an
experiment used to learn what the final loop should be.
