# Development Directions

> **Status: provisional.** This is a map of questions and experiments, not a
> release promise, ordered backlog, or fixed MVP.

The project is intentionally being developed through vertical slices. Each slice
should test a complete relationship between world access, processing, materials,
and player agency before the roster expands.

## Implemented experiment: bronze loop

The repository currently contains tin, dusts, bronze, tools, and a fuel-burning
crusher. Its purpose is to validate the basic content and processing foundation.

Questions it should answer:

- Is the crusher understandable and satisfying?
- Does ore doubling justify the detour?
- Does dust alloying feel natural?
- Can material families be added without repetitive code and data becoming
  unmanageable?
- What role could tin and bronze occupy once geology exists?

Nothing about this experiment is protected from replacement.

## Candidate experiment: primitive resources

Test the smallest deterministic opening loop that gives loose rocks, flint,
wood, plants, and string distinct roles:

> Gather loose rocks and sticks -> craft a flint hatchet or knife -> harvest
> wood and plants -> make plant fiber -> craft string

The experiment should include a configuration toggle that prevents logs from
being harvested by hand. It should test that state as a candidate default while
preserving ordinary tree punching as a compatibility and player-preference
option.

Loose rocks must remove the random first-flint gate. One loose rock should fill
the recipe role of one flint shard, one flint should produce two shards, and any
four rocks or shards should form one cobblestone. Mined stone should return
loose rocks instead of ready-made cobblestone. No knife, knapping station, or
lucky gravel drop may be required before the first hatchet or knife.

The first hatchet recipe should be tested in its intended 2x2 upside-down-L
shape: one rock or shard beside the upper stick and one stick directly below the
first. String must not be required for this tool.

The experiment should compare stone and flint tool profiles, with flint
currently expected to trade durability for speed or damage. It must test both
premium performance at the stone harvest tier and a narrow intermediate harvest
capability for flint. It should also determine which plants produce fiber,
whether knives continue through metal families, and whether the loop is
understandable without a guide.

The experiment must validate spawn viability, early stick access, 2x2 recipes,
modded-log behavior, and feedback when a player tries to harvest a log by hand.
Knapping is not part of the current candidate. The goal is a brief, useful
primitive phase, not a longer ritual before ordinary play.

The same experiment should validate the broader tool rule: each included tool
must grant a material interaction or a meaningful trade-off, rather than exist
only to complete a recolored equipment set.

## Candidate experiment: cave-biome resources

Test clay and gravel distribution as parts of cave ecology:

- Abundant, reliable clay in lush caves
- Smaller clay pockets in other wet caves
- Exposed gravel deposits that make bulk flint discoverable
- Little or no equivalent clay in environments where it does not visually fit

The experiment should answer whether cave identity changes route choice and
whether lush-cave clay makes pottery infrastructure accessible without making
clay globally uniform. Gravel must remain valuable without being required to
make the first tool.

## Candidate experiment: meaningful geology

Build the smallest possible geology prototype capable of comparing:

- Ordinary vanilla stone behavior
- Hardness or harvest changes by layer
- Easier exposed blocks
- Enclosure-sensitive resistance
- Natural-only versus placed-block behavior

The goal is not to implement an entire underground rewrite immediately. It is to
find the smallest rule set that makes caves and structures rationally valuable
without making mining miserable.

## Candidate experiment: access progression

Once geology and bronze can interact, test a short playable arc:

> Primitive extraction -> accessible copper and tin -> processing -> bronze ->
> expanded underground access -> iron -> economical sustained excavation

This is the first point at which bronze's intended role can be evaluated rather
than assumed.

## Candidate experiment: broader base family

Only after the access loop works, evaluate additional mundane metals and alloys.
Candidates may include zinc, brass, nickel, manganese, steel, invar, silver,
electrum, lead, or others. Inclusion depends on the material graph, not on
matching another mod's roster.

## Candidate experiment: material specialization

Test equipment profiles that differ across:

- Accessibility
- Durability
- Mining speed
- Harvest capability
- Repair cost
- Enchantability
- Alloy relationships

The goal is to determine where branching choices work and where a clear upgrade
is healthier.

## Candidate experiment: workshop logistics

Test the smallest physical item-transport system that gives base metals an
infrastructure role. Current candidates include:

- Hoppers craftable from multiple workable metals
- Equivalent versus tiered behavior for alternative hoppers
- Copper ducts for simple item movement
- Crusher input and output automation

The experiment should answer whether these blocks make a workshop more capable
without beginning an electrical or industrial-tech progression.

## Candidate experiment: manual workshop

Test one workstation whose installed hand tool selects the available processing
recipes:

> **Tool + material -> processed output**

The first candidate operations are:

- Saw + log -> bonus planks or wooden components
- Knife + suitable plants -> improved fiber yield
- Hammer + workable material -> plates or other shaped components

The experiment should compare a compact stonecutter-like interface with any
simpler interaction that preserves the same fantasy. It must make the installed
tool, valid inputs, produced output, and durability cost legible.

The workshop should answer:

- Does visible tool installation feel more natural than damaging tools in
  crafting recipes?
- Does the installed tool clearly determine the recipe family?
- Can the block support several hand tools without becoming a universal
  machine?
- Do tool materials create meaningful durability or speed trade-offs?
- Should any tool material affect yield, or is yield principally determined by
  the operation?
- Where should automation stop so the block remains manual bench work?

Crushing, heating, alloying, passive cultivation, and item transport remain
outside the workshop. The experiment succeeds only if those boundaries remain
obvious in play.

## Candidate experiment: bonsai resource support

Test a small bonsai loop inspired by the Bonsai Trees mod:

> Obtain clay -> make a pot -> cultivate a tiny tree -> receive slow tree drops
> -> add a hopper or related component for automatic collection

The intended role is supplemental, background production—especially sticks and
other tree products consumed by a growing workshop. Drop tables, growth time,
soil rules, valid trees, manual collection, hopper requirements, and
compatibility with modded trees are all undecided.

The experiment should determine:

- Whether the construction cost makes the convenience feel earned
- Whether output supplements forestry without replacing it
- Whether automatic collection creates a satisfying infrastructure milestone
- Whether the loop remains legible without a power system or complex interface
- Whether its output meaningfully supports metallurgy rather than existing as
  unrelated convenience content

## Candidate experiment: saws and wood efficiency

Test whether saws create a satisfying active wood-processing role:

> Harvest logs -> install a saw in the workshop -> process logs -> receive
> better yield

The first comparison should preserve vanilla baseline plank output and treat the
saw as a bonus. Baseline output reductions should only be tested if the bonus
model cannot create a meaningful reward. The experiment should keep axes,
saws, and bonsai distinct: harvesting speed, active yield, and passive supply.
Saw durability should be consumed through the workshop operation rather than a
hidden reusable-tool crafting recipe.

## Candidate experiment: magical metallurgy

Prototype enchantability tiers, gold-derived materials, silver finishing, rose
gold, and artifact-grade equipment only after the mundane material system has a
stable reason to exist.

This may remain in the core mod or become a separate compatible module.

## Release planning

No release scope is currently frozen. A future MVP should be declared only after
the relevant experiments demonstrate a coherent, enjoyable loop. The existing
bronze content is a starting test bed, not that declaration.
