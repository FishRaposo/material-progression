# Underground and Geology

> **Status: provisional.** Divergent Underground establishes the reference
> problem and a promising solution; this mod has not yet selected an exact
> implementation.

## Why geology is foundational

A large material roster only matters when the player has reasons to engage with
it. If iron remains available immediately and makes every layer cheap to tunnel
through, the shortest path still bypasses most metals and most terrain.

The underground therefore cannot be a neutral container for the metallurgy
system. It is the physical foundation of that system.

## Desired behavior

Natural underground blocks may differ by:

- Mining time
- Minimum harvest capability
- Depth or geological layer
- Exposure to air
- Degree of enclosure or compression
- Whether the block was naturally generated or placed by a player

Exact formulas and layer counts are deliberately undecided.

Geology also includes exposed, non-metal resources. Clay and gravel should not
be distributed independently of the spaces around them. Cave biome, moisture,
water, and exposure can give them recognizable environmental homes.

Ordinary stone should participate in the primitive material loop as well.
Rather than dropping a ready-made cobblestone block, mined stone can drop loose
Rocks. One Rock can be sharpened into one flint shard through the shapeless 2x2
recipe, while four Rocks can be consolidated into cobblestone. This connects
the loose rocks gathered on the surface to the stone the player later excavates
instead of treating them as a disposable starting currency.

## Cave biomes as resource biomes

The shape of a cave matters, but its ecological identity should matter too.

Lush caves should be the premier underground clay source. Vanilla already gives
them wet floors, pools, and clay as part of their visual language; the intended
change is to make that supply abundant and reliable enough to support pottery
and bonsai infrastructure. Discovering a lush cave should plausibly mean
"pottery is solved."

Other wet caves may contain smaller clay pockets around cave floors and
underground water. Dry caves should not receive equivalent deposits merely for
uniformity. The world should communicate where a resource belongs.

Gravel should receive similar treatment. Exposed cave deposits can become
recognizable sources of flint and therefore primitive tools, knives, plant
fiber, and string. Exact biome associations, frequencies, and deposit shapes
remain undecided.

See [Primitive Resources and Tools](PRIMITIVE_RESOURCES.md) for the resource
loops these deposits support.

## Terrain as progression

The important distinction is between following existing space and creating new
space. This principle applies above ground as well as below it.

Early in progression:

- Forest edges and clearings provide routes around wood the player cannot yet
  harvest efficiently.
- Exposed deposits are more valuable than buried ones.
- Caves provide access through rock that is expensive to excavate in bulk.
- Cave biomes provide recognizable concentrations of clay, gravel, and other
  non-metal resources.
- Ravines create vertical routes across geological layers.
- Mineshafts and underground structures provide pre-cut infrastructure.
- Route finding and prospecting outperform coordinate-driven tunnelling.

Later in progression:

- Flint hatchets, axes, and possible saws turn forests from obstacles into
  increasingly efficient resources.
- Better tools make sustained excavation economical.
- The player becomes less dependent on natural openings.
- Strip mining changes from technically possible but foolish to a competitive
  strategy.
- Clay, stone, metals, and other construction resources become inputs to a
  workshop that reduces repetitive labor.
- Mastery feels like increased control over the same world that constrained the
  player earlier.

The shared rule is:

> **Natural openings matter until technology lets the player create new ones.**

A forest cannot simply be punched through before the first cutting tool. A
mountain cannot simply be strip-mined with primitive excavation tools. The
player initially works around both, then earns the ability to reshape them.

This relationship is important even for seemingly modest infrastructure. A
bonsai pot may ask for clay, while automatic collection may ask for a hopper or
another metal component. If obtaining those ingredients requires meaningful
exploration and excavation, the resulting automation is a consequence of
geological progress rather than a free convenience recipe.

## Air exposure and enclosure

Divergent Underground's most important reference point is not merely
"deeper stone is harder." It is the possibility that exposed stone is easier
than fully enclosed stone. That rule allows caves to function as natural
breaches without making their walls inaccessible.

An enclosure-sensitive model could go further by scaling resistance with the
amount of surrounding solid material. This remains a design candidate, not a
decision. It must be tested for:

- Player comprehension
- Runtime cost
- Interaction with world generation
- Interaction with explosions and artificial openings
- Compatibility with other mods
- Whether it rewards cave exploration without creating tedious edge cases

## Natural versus placed blocks

Geological resistance should apply to the generated world, not indiscriminately
to every stone block. A player who places deep stone in a house should not need
late-game mining equipment to correct it.

Tracking or inferring natural blocks is therefore a core technical question.
Possible implementations must be evaluated for save size, performance, and
compatibility before one becomes part of the design.

## Success criteria

The geology system succeeds if:

- A new player prefers caves and exposed routes for rational mechanical reasons.
- Different underground shapes create different progression stories.
- Intermediate tools expand access in perceptible steps.
- Iron and later materials do not make every prior route choice irrelevant
  immediately.
- Strip mining feels powerful because the player remembers when it was
  uneconomical.
- Building and ordinary block cleanup remain comfortable.
