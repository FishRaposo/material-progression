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

The first primitive slice now implements this deterministic opening:

> Gather ground Rocks and sticks -> sharpen a Rock -> craft a flint hatchet ->
> harvest logs

The log-only axe requirement is enabled by default and has a server
configuration opt-out. Loose rocks and ground sticks generate as persistent,
easy hand-breakable surface blocks. Rock and flint recipes guarantee the first
hatchet without gravel luck.

The next primitive experiment should add the knife, plant fiber, craftable
string, mined-stone Rock drops, and density tuning around trees and shrubs.

Loose rocks must remove the random first-flint gate. One Rock should sharpen
into one flint shard through a shapeless 2x2 recipe, one flint should produce
two shards, and four Rocks should form one cobblestone. Mined stone should
return Rocks instead of ready-made cobblestone. No knife, knapping station, or
lucky gravel drop may be required before the first hatchet or knife.

Ground sticks should be persistent visible objects beneath trees and around
shrubs, yielding ordinary vanilla sticks. Leaves remain the renewable fallback
and dead bushes the dry-biome source. The experiment must determine placement
density and pickup behavior without making random leaf drops the bootstrap.

The first hatchet recipe should be tested in its intended 2x2 upside-down-L
shape: one flint shard beside the upper stick and one stick directly below the
first. String must not be required for this tool.

The experiment should compare stone and flint tool profiles, with flint
currently expected to trade durability for speed or damage. It must test both
premium performance at the stone harvest tier and a narrow intermediate harvest
capability for flint. It should also determine which plants produce fiber,
whether knives continue through metal families, and whether the loop is
understandable without a guide.

The experiment must validate spawn viability, ground Rock and stick
distribution, the shapeless sharpening and cobblestone recipes, the shaped
hatchet recipe, modded-log behavior, and feedback when a player tries to harvest
a log by hand. Knapping is not part of the current candidate. The goal is a
brief, useful primitive phase, not a longer ritual before ordinary play.

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

## Candidate experiment: manual workshop

Test one workstation whose installed hand tool selects the available processing
recipes:

> **Tool + material -> processed output**

The first operations to test are:

- Knife + loose rock -> improved flint-shard yield
- Knife + suitable plants -> improved fiber yield
- Hammer + stone -> gravel
- Hammer + gravel -> sand
- Hammer + ore or raw metal -> two dust at a high durability cost
- Saw + log -> more than four planks
- Saw + planks -> improved stick yield

The tools must also remain usable in the field: the knife as a lightweight
weapon and plant harvester, the hammer as a pickaxe alternative, and the saw as
an axe alternative. The workshop remains their intended use. Testing should
preserve the rule that field use harvests while workshop use processes.

The experiment should compare a compact stonecutter-like interface with any
simpler interaction that preserves the same fantasy. It must make the installed
tool, valid inputs, produced output, and durability cost legible.

The workshop should answer:

- Does visible tool installation feel more natural than damaging tools in
  crafting recipes?
- Does the installed tool clearly determine the recipe family?
- Can the block support several hand tools without becoming a universal
  machine?
- Do field drops remain predictable instead of changing merely because a hammer
  or saw broke the block?
- Are baseline plant fiber and the shapeless Rock-to-shard recipe still
  available without a workshop?
- Do tool materials create meaningful durability or speed trade-offs?
- Is the manual ore-crushing durability cost high enough to keep the
  fuel-burning crusher valuable?
- Should any tool material affect yield, or is yield principally determined by
  the operation?
- Where should automation stop so the block remains manual bench work?

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

## Candidate experiment: saws and wood efficiency

Test whether saws create a satisfying active wood-processing role while
remaining usable as an axe alternative in the field:

> Harvest logs -> install a saw in the workshop -> process logs -> receive
> better yield

Preserve vanilla's four-plank baseline and treat the saw as a bonus. Test both
log-to-plank and plank-to-stick improvement. The experiment should keep axes,
saws, and bonsai distinct: primary harvesting, alternate field use plus active
yield, and passive supply.
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

## Automated acceptance coverage

Every implemented deterministic mechanic should gain an automated acceptance
test with the production change. The current harness has two layers:

- Repository contract tests validate JSON resources, recipe outputs, material
  flow, translations, models, tags, loot, world-generation wiring, internal
  documentation links, and whitespace.
- NeoForge GameTests load the real mod and validate crusher processing, fuel
  requirements, sided inventory, block drops, tool durability, repair materials,
  mining requirements, and enchantability tags.

The Gradle build and GameTest server are separate CI gates. Future loose-rock,
tree-punching, workshop, hammer, knife, saw, bonsai, logistics, bulk-crafting,
and geology implementations should extend the live suite when they are
introduced. Tests must not pretend that a documented but unimplemented feature
exists.

Human playtesting remains responsible for whether the opening is satisfying,
durability costs feel fair, terrain creates interesting routes, and interfaces
communicate the intended physical fantasy.
