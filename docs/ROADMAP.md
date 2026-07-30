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

## Implemented primitive foundation

The complete opening branch now implements:

> Gather family-correct Rocks and sticks -> make Flint tools -> harvest plants
> and wood -> use the Manual Workshop -> process Copper and Tin -> make Bronze
> -> enter Dense geology

The shipped slice contains:

- Sixteen Rock families, family cobbles, fragment drops, and family-aware Loose
  Rock placement without a Stone fallback
- A deterministic shapeless Rock-to-Flint-Shard recipe and mixed/family
  four-Rock cobbling
- Plant Fiber, field Knife harvesting, and three-Fiber String
- Flint and Bronze Knives, Hammers, and Saws
- The default-enabled log-only Axe/Saw rule, with localized throttled feedback
- A persistent Manual Workshop for knife, hammer, and Saw operations
- Vanilla four-Plank crafting plus six matching Planks per ordinary Log at the
  Workshop, and three Sticks per Plank
- Five opening advancements, operation unlock rewards, localized tooltips, and
  geological requirement hints
- Overworld-only Ground Sticks with bounded, datapack-extensible tree/shrub
  anchor detection and sparse background placement

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

## Implemented experiment: meaningful geology

The first geology implementation now resolves:

- Dimension-specific depth bands
- Soft, standard, and hard family modifiers
- One-level relief from any non-sturdy exposed face
- Four capability levels from Exposed through Deep
- Natural resistance versus L0 player-placed raw stone
- Correct-tool, Fortune, Silk Touch, and incorrect-tool drops
- Persistent placed-stone markers with removal and piston transfer
- Independent resistance and fragment-drop server toggles
- Reloadable depth profiles for arbitrary non-built-in dimensions, with L0 as
  the unconfigured fallback

The reloadable stone-family schema accepts arbitrary additional namespaced
family IDs with externally registered Rocks, raw blocks, and cobbles. It
validates the resolved catalog transactionally and supports external families
through cobbling, Loose Rocks, drops, rendering, and geological resistance.

## Current playtest: access progression

Geology and Bronze now interact in the intended short arc:

> Primitive extraction -> accessible copper and tin -> processing -> bronze ->
> expanded underground access -> iron -> economical sustained excavation

Bronze Hammers and Pickaxes reach Dense geology. The remaining question is
balance and discoverability: complete a 20-30 minute survival run before release
and adjust placement, durability, timings, and feedback from evidence.

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
- Convenient hopper variants for filtering, direction, or throughput
- Larger chest variants
- Crusher input and output automation

The experiment should answer whether these blocks make a workshop more capable
without beginning an electrical, industrial-tech, or abstract storage-network
progression.

## Candidate experiment: bulk-crafting table

Test a stationary alternative to the ordinary crafting table that removes
recipe-grid conflicts and intermediate-crafting repetition:

> **Select output and quantity -> plan recipe chain -> consume local materials
> -> return output and surplus**

The first implementation should:

- Treat shaped recipes as ingredient multisets after the player explicitly
  selects the desired output.
- Read the player's inventory, an internal buffer, and directly adjacent
  inventories.
- Refuse to search recursively through hopper chains or remote storage.
- Calculate intermediate recipes so logs can satisfy plank or stick costs.
- Consume existing finished ingredients and partial intermediates before making
  more.
- Respect recipe batch sizes and preserve surplus intermediates.
- Show total material cost, missing requirements, and maximum craftable amount.
- Execute the full plan transactionally.
- Choose valid tag materials and alternative recipes automatically.
- Detect cycles and preserve container remainders.
- Accept a fixed number of tiered upgrade modules.

Initial upgrade families are storage, filtering, material or inventory priority,
reserved quantities, and saved crafting jobs. Quantity-based upgrades
can stack, especially storage, memory capacity, filter capacity, and reservation
capacity, while binary capabilities do not stack. The fixed slot count prevents
indefinite expansion. Higher tiers provide more effect per occupied slot and
are crafted by upgrading lower-tier modules so previous investment is retained.

The experiment should answer:

- Can recursive recipe planning remain predictable across vanilla and modded
  recipes?
- Is the planned material preview sufficient before automatic selection?
- Which inventory should be consumed first by default?
- How large should the base buffer and fixed upgrade-slot budget be?
- How large should each stackable capacity increase be at each tier?
- Can hoppers supply ingredients and extract output without turning the table
  into an autonomous factory?
- Does the traditional crafting table remain preferable for quick portable
  work?

The bulk-crafting table must remain separate from the manual workshop, furnaces,
crushers, and bonsai. It automates ordinary crafting decisions and repetition;
it does not become a universal processing machine.

## Implemented experiment: manual workshop

The Manual Workshop now ships as one persistent workstation whose installed hand
tool selects processing:

> **Tool + material -> processed output**

The initial operations are:

- Knife + Rock -> two Flint Shards
- Knife + suitable plants -> one, two, three, or five Plant Fiber
- Hammer + Stone or Cobblestone -> Gravel
- Hammer + Gravel -> Sand
- Hammer + compatible ore or raw metal -> two Dust at high durability cost
- Saw + Log or stem -> six matching Planks
- Saw + Plank -> three Sticks

The tools remain usable in the field: the Knife as a lightweight
weapon and plant harvester, the hammer as a pickaxe alternative, and the saw as
an axe alternative. The workshop remains their intended use. Testing should
preserve the rule that field use harvests while workshop use processes.

Tool/input/output/progress persist. Output blocking pauses; recipe or tool
changes reset progress; completion, durability, and tool breakage are atomic;
breaking the block drops inventory but loses progress; and hoppers cannot
insert or extract. The public `material_progression:manual_workshop` recipe type
keeps operations datapack-driven.

Manual and machine ore processing must both turn one ore or raw metal into two
dust. The hammer spends substantial durability without tier-gating recipes; the
crusher spends fuel, preserves tools, and supports scaling or automation.
Heating, alloying, passive cultivation, and item transport remain outside the
workshop. The experiment succeeds only if those boundaries remain obvious in
play.

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

## Implemented experiment: Saws and wood efficiency

Saws are Axe alternatives in the field and satisfy the log-harvest rule:

> Harvest logs -> install a saw in the workshop -> process logs -> receive
> better yield

Vanilla's four-Plank baseline remains. Both Flint and Bronze Saws produce six
matching Planks from an ordinary Log and three Sticks from a Plank at the
Workshop. Bronze buys durability, not additional yield. Bonsai remains a later
passive-supply system.

## Candidate experiment: magical metallurgy

Prototype enchantability tiers, gold-derived materials, silver finishing, rose
gold, and artifact-grade equipment only after the mundane material system has a
stable reason to exist.

This may remain in the core mod or become a separate compatible module.

## Release planning

The local 0.2.0 release candidate is the complete opening/geology slice. Its
versioned source and installable JAR are synchronized locally. Before
publishing or integrating the candidate, it still requires:

1. A 20-30 minute survival playtest through Bronze and Dense geology
2. Client inspection of Workshop rendering, UI, sounds, particles, and feedback

Do not push, merge, or move `main` without explicit authorization.

## Automated acceptance coverage

Every implemented deterministic mechanic should gain an automated acceptance
test with the production change. The current harness has two layers:

- Repository contract tests validate JSON resources, recipe outputs, material
  flow, translations, models, tags, loot, world-generation wiring, internal
  documentation links, and whitespace.
- NeoForge GameTests load the real mod and validate crusher processing, every
  stone family, Loose Rock resolution, cobbling, geological resistance and
  drops, placed-stone persistence, primitive tools, Manual Workshop behavior,
  discoverability, configuration, and gameplay tags.

The opening branch currently runs 130 live GameTests. Future pottery, samples,
deposits, prospecting, bonsai, logistics, bulk crafting, and expanded metallurgy
must extend the suite when they are introduced. Tests must not pretend that a
documented but unimplemented feature exists.

Human playtesting remains responsible for whether the opening is satisfying,
durability costs feel fair, terrain creates interesting routes, and interfaces
communicate the intended physical fantasy.
