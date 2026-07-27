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

## Terrain as progression

The important distinction is between following existing space and creating new
space.

Early in progression:

- Exposed deposits are more valuable than buried ones.
- Caves provide access through rock that is expensive to excavate in bulk.
- Ravines create vertical routes across geological layers.
- Mineshafts and underground structures provide pre-cut infrastructure.
- Route finding and prospecting outperform coordinate-driven tunnelling.

Later in progression:

- Better tools make sustained excavation economical.
- The player becomes less dependent on natural openings.
- Strip mining changes from technically possible but foolish to a competitive
  strategy.
- Mastery feels like increased control over the same world that constrained the
  player earlier.

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
