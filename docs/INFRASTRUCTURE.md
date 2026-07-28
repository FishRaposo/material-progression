# Workshop Infrastructure and Automation

> **Status: designed, not implemented.** The system boundaries and settled
> bulk-crafter behaviors are current decisions. Exact blocks, recipes, slot
> counts, numerical effects, output rates, and progression positions remain
> subject to implementation and playtesting.

## Purpose

Material progression should eventually change the player's base as well as
their equipment. The workshop begins as a collection of manual, fuel-burning
blocks and develops into a modest physical system that can process materials,
move items, and supply some common resources in the background.

This is not intended to become an industrial technology mod. The desired
vocabulary is recognizably Minecraft:

- Pots
- Saplings and soils
- Chests and other inventories
- Hoppers and convenient hopper variants
- Crushers and furnaces
- A local bulk-crafting table
- Fuel, gravity, growth, and elapsed time

No electrical grid, recursive pipe network, or abstract storage system is
required to justify these systems. Logistics should remain shallow: inventories
store items, adjacent blocks can use them, and hoppers physically move items
where they need to go.

## The workshop block

The workshop is the proposed home for manual, tool-assisted processing. Its
core interaction is:

> **Installed hand tool + material -> processed output**

The tool selects the recipe family. A knife exposes cutting and separation
operations, a hammer exposes deliberate manual crushing and shaping operations,
and a saw exposes efficient wood subdivision. The workshop is therefore not an
arbitrary universal machine; every recipe should describe work a person could
plausibly perform at a bench with the installed tool.

The initial operation families are:

- **Knife + Rock -> more flint shards than shapeless sharpening**
- **Knife + suitable plants -> more plant fiber than field harvesting**
- **Hammer + stone -> gravel**
- **Hammer + gravel -> sand**
- **Hammer + ore or raw metal -> 2 dust at a high durability cost**
- **Saw + log -> more than the default four planks**
- **Saw + planks -> more sticks than ordinary crafting**

Plates, shaped pieces, and other hammered components remain possible later
operations when metallurgy justifies them.

Basic access does not depend on workshop efficiency. A Rock can still be
sharpened into one flint shard through the shapeless 2x2 recipe, and plants can
still be harvested with a knife in the world for fiber. The workshop replaces
No Tree Punching-like right-click processing recipes with a visible place for
careful, higher-yield work.

### Field use and intended use

Workshop tools remain real tools outside the block:

- A **knife** is a lightweight alternative to a sword and can harvest plants
  for fiber.
- A **hammer** is an alternative to a pickaxe.
- A **saw** is an alternative to an axe.

Those field roles prevent the tools from becoming inert crafting ingredients,
but they are not the primary reason the tools exist. Their intended use is the
workshop:

> **Outside the workshop, tools harvest. Inside the workshop, tools process.**

Breaking stone with a hammer should not automatically turn the drop into gravel,
and harvesting a log with a saw should not silently apply the workshop yield
bonus. The player chooses the transformation by bringing the material to the
workshop. This keeps ordinary block drops predictable and makes processing
intentional.

The exact interface remains provisional, but the conceptual slots are simply a
tool, an input material, and an output. The installed tool remains visible and
persists between operations. Its durability is consumed when processing occurs,
instead of placing a reusable tool in an ordinary crafting recipe and relying
on hidden recipe-specific damage behavior.

The interaction should resemble Minecraft's existing workstation language more
than an industrial machine. A stonecutter-like selection interface is one
candidate when a tool and material combination has several valid outputs.
Processing speed, whether output is taken immediately, tool replacement,
automation, and redstone behavior remain open implementation questions.

### Boundaries

The workshop handles hand-tool operations:

- Cutting
- Carving
- Sawing
- Manual crushing
- Hammering and shaping
- Other direct bench work justified by an installed tool

It does not absorb every processing system:

- The workshop hammer and the crusher can both process ores into two dust.
  Manual crushing consumes substantial hammer durability; machine crushing
  consumes fuel and preserves tools.
- Heating and smelting remain in furnaces.
- Alloying remains in its appropriate metallurgical process.
- Passive cultivation remains in bonsai.
- Item movement remains the job of hoppers and inventories.
- Ordinary recipe planning and bulk production remain the job of the
  bulk-crafting table.

These boundaries preserve the physical fantasy of each block and prevent the
workshop from becoming a universal recipe menu.

### Tool progression inside the workshop

Workshop tools extend the rule that tools grant material interactions. Their
material can affect durability and working speed. The workshop operation itself
can improve yield over direct field use or ordinary crafting; upgrading the
tool's material should not automatically require a ladder of further arbitrary
output multipliers.

Ore crushing has no hammer-tier gate. A primitive hammer and a later metal
hammer can perform the same ore operation and recover the same two dust; the
primitive tool simply wears out much faster. This keeps the rule simple and
makes durability the cost of choosing manual processing.

The workshop gives knives, saws, hammers, and future hand tools a continuing
role without requiring every material to receive every tool. A tool-material
combination belongs when it adds a useful operation, trade-off, or economic
path.

## Automation as a progression reward

Automation should be earned through the same world-facing loop as better tools:

> **Explore -> extract -> process -> build infrastructure -> reduce labor**

The player gains convenience by spending materials and constructing something
physical. This gives ordinary resources a long tail of usefulness and lets the
base visibly reflect progress.

Automation should:

- Remove or soften repetitive work the player has already demonstrated.
- Require ingredients whose acquisition participates in progression.
- Produce understandable inputs and outputs.
- Remain useful without becoming the only rational way to obtain a resource.
- Connect to the workshop rather than exist as an isolated convenience menu.

## Bulk-crafting table

The bulk-crafting table is an alternative interface for ordinary crafting. It
does not replace the player's 2x2 grid or the traditional crafting table.
Instead, it removes two forms of friction when the player deliberately builds a
permanent crafting station:

- Recipe conflicts caused by different shaped recipes using the same materials
- Repetitive intermediate crafting when producing items in bulk

The player selects the desired output and quantity. The table treats a shaped
recipe as a multiset of ingredients rather than asking the player to position
those ingredients in a grid. Because the output is selected explicitly, two
recipes can require the same ingredient totals without ambiguity.

### Local inventory boundary

The table can source materials from:

- The player's inventory
- Its internal working inventory
- Directly adjacent chests and other supported inventories

It does not recursively search through hopper chains, remote containers, or an
abstract storage network. A chest touching the table is available; a distant
chest must feed the local workspace through hoppers or hopper variants. Hoppers
can keep the table supplied and extract completed products.

This boundary makes larger chests and improved item movement useful without
turning the table into an Applied Energistics-like terminal. The local setup
remains visible and understandable from the blocks involved.

### Recursive recipe planning

The table resolves craftable intermediates automatically. If the requested
output requires sticks and the available storage contains logs, it can plan:

> **Logs -> planks -> sticks -> requested output**

Existing finished ingredients are consumed before making more intermediates.
Existing partial intermediates, such as planks, should also reduce the remaining
base-material cost. Recipe batch sizes are respected; unavoidable surplus
intermediates return to the internal inventory rather than disappearing.

Before execution, the interface should show:

- The selected output and requested quantity
- Total required materials after recursive expansion
- Which requirements are already available
- The maximum currently craftable quantity
- Any missing ingredient or blocked intermediate

The entire plan should execute transactionally. If the complete craft cannot be
performed, the table must not consume a partial chain and leave the player with
an unexpected half-crafted result.

Recipes can contain tags, alternative ingredients, multiple ways to make the
same intermediate, container remainders, and cycles introduced by other mods.
The planner must choose a valid available route, preserve remainder items, and
reject cycles rather than recurse forever.

### Automatic material choice

The base table automatically chooses valid materials from its accessible
inventories. This is essential to its bulk-crafting role: ordinary recipes using
the plank or log tags should not require the player to select every conversion
manually.

Automatic choice should remain predictable. A stable default ordering and a
preview of the planned consumption allow the player to understand what will be
used. More precise control is provided through upgrades rather than required
for basic operation.

### Upgrade modules

The table has a fixed number of dedicated upgrade slots. Craftable modules have
types and tiers, allowing some effects to stack while preventing unlimited
expansion. The table itself has no material tier ladder; installed modules
determine what kind of bulk-crafting station it becomes.

Initial module families are:

- **Storage:** expands the internal ingredient and surplus buffer.
- **Filter:** adds ingredient whitelists and blacklists.
- **Priority:** controls which valid materials or accessible inventories are
  consumed first.
- **Reservation:** protects configured items or minimum retained quantities.
- **Memory:** adds saved or favorited bulk-crafting jobs.

Quantity-based capabilities can stack. Storage is the clearest example, while
memory capacity, filter capacity, and reservation capacity can also gain
meaningful quantity. Players may fill most or all upgrade slots with storage
modules to build a large, simple bulk crafter. Binary capabilities do not stack;
installing a duplicate must not create a meaningless second copy of the same
ability.

Higher module tiers provide more effect per slot, allowing several capabilities
to fit within the same hard slot limit. Higher-tier modules are crafted by
upgrading their lower-tier predecessors so earlier material investment is
preserved rather than discarded.

The fixed slot count creates deliberate specialization:

- A large, mostly automatic material buffer
- A smaller table with strict filters and reservations
- A balanced general-purpose station

Exact slot count, tier count, recipes, and numerical effects remain
implementation questions.

### Crafting-table boundaries

The bulk-crafting table handles ordinary recipes and their craftable
intermediates. It does not absorb every workstation:

- Manual knife, hammer, and saw processing remains in the workshop.
- Crushing remains in the workshop or crusher.
- Heating and smelting remain in furnaces.
- Passive cultivation remains in bonsai.
- Item movement remains physical through inventories and hoppers.

The traditional crafting table remains the cheap, immediate, portable option.
The bulk-crafting table earns its place when explicit recipe selection, adjacent
storage, recursive planning, upgrades, or mass production are worth building
around.

## Bonsai

The current bonsai concept follows the broad fantasy of the Bonsai Trees mod: a
tiny cultivated tree supplies tree drops slowly over time.

Its intended jobs are:

- Provide a supplemental stream of sticks for tools, handles, fuel, recipes, and
  other workshop demands.
- Supply ordinary tree products without requiring repeated full-tree harvesting.
- Give clay, saplings, soils, hoppers, and storage a small infrastructure loop.
- Let the workshop become incrementally more self-sustaining.

The bonsai should not trivialize forestry. Its production can be slow enough
that forests and ordinary tree farms remain valuable, especially when the
player needs bulk wood. It is background support, not a wooden singularity.

Clay sourcing is part of this loop. Lush caves should be excellent and reliable
clay sources, while other wet caves may contain smaller deposits. This makes the
bonsai pot easier to pursue through exploration without reducing it to a free
opening recipe.

### Candidate construction and use

A possible progression is:

1. Obtain clay and create a bonsai pot.
2. Place an appropriate sapling and growing medium in the pot.
3. Wait while the tree produces a small selection of its ordinary drops.
4. Collect those drops manually.
5. Add a hopper or related collection component to route drops into storage.

Clay and the collection component make the block non-trivial even though the
concept is simple. In a world with meaningful geology, acquiring and processing
those ingredients represents real progress.

The following remain undecided:

- Whether manual and automated bonsai are separate blocks
- Exact clay and metal costs
- Growth rates
- Drop tables and relative drop weights
- Valid saplings and soils
- Whether the tree must be replanted
- Whether fertilizer affects production
- How modded trees participate
- Inventory size and overflow behavior
- Whether a hopper is mandatory or merely one automation option

## Active forestry and saws

A saw is the active wood-efficiency tool. Outside the workshop it can act as an
alternative axe. Installed in the workshop, it turns logs into more than the
default four planks and planks into more sticks than ordinary crafting.

The desired distinction is:

- An axe improves harvesting speed.
- A saw improves active processing yield.
- A bonsai supplies slow background drops.
- Hoppers and hopper variants automate collection and movement.

The current preference is to preserve vanilla's baseline wood output. Workshop
sawing is an optional efficiency reward, not the restoration of output removed
at the beginning. Exact saw materials, recipes, yields, and durability remain
undecided.

See [Primitive Resources and Tools](PRIMITIVE_RESOURCES.md) for the larger
relationship between wood, flint, knives, plant fiber, and metallurgy.

## Hoppers made from multiple metals

Vanilla hoppers require iron, but the function of a hopper is not intrinsically
iron-specific. It is a container and funnel that moves items. Other workable
metals should be considered valid construction materials when that supports the
material graph.

Possible models include:

- **Equivalent recipes:** several metals produce the ordinary vanilla hopper.
- **Tiered hoppers:** different materials change transfer rate, cooldown,
  filtering, capacity, or another legible property.
- **Specialized hoppers:** materials trade cost or capability without forming a
  strict ladder.

No model has been selected. Equivalent alternatives may be the cleanest answer;
tiering is only justified if it creates useful decisions rather than a parade
of recolored funnels.

Alternative hoppers can:

- Reduce the arbitrary iron bottleneck around simple automation.
- Preserve demand for copper, bronze, or other workable base metals.
- Let regional ore availability produce different but viable workshop paths.
- Connect early metallurgy directly to storage and processing.

## Shallow logistics

The intended logistics layer is vanilla-scale rather than a duct network.
Chests, larger chest variants, ordinary hoppers, and useful hopper variants
should be sufficient to connect storage, processors, bonsai, and the
bulk-crafting table.

Possible hopper improvements include filtering, directionality, throughput, or
other legible conveniences. Each variant should remain a physical item mover
with an understandable source and destination. It should not recursively expose
every connected inventory as one giant storage system.

## Processing automation

The current stone crusher is a fuel-burning furnace analogue:

> **1 ore or raw metal -> 2 dust**

The workshop hammer provides an equal-yield manual alternative:

> **1 ore or raw metal + substantial hammer durability -> 2 dust**

Neither route is intended as a superior yield tier. The hammer avoids fuel but
rapidly consumes tools. The crusher consumes fuel, preserves tool durability,
and is the natural target for automation. This separation gives both processing
routes a permanent reason to exist without restricting ores by hammer material.

It should be possible to evaluate ordinary Minecraft automation around it:

- A hopper supplies crushable material.
- Another inventory supplies fuel if the final interface supports it.
- Output is routed toward alloying, smelting, or storage.

This does not commit the crusher to full automation, a particular sided-inventory
layout, or any upgrade path. It records the desired relationship: logistics
should gradually connect the primitive processing blocks already present instead
of replacing them with powered machines from an unrelated technology tier.

## Material identity through infrastructure

Infrastructure gives materials verbs beyond combat and mining:

- Clay can **cultivate or contain**.
- Flint can **cut and separate**.
- Plants can **supply fiber**.
- Knives can **extract more fiber and shards at the workshop**.
- Hammers can **mine in the field and crush deliberately at the workshop**.
- Saws can **harvest in the field and subdivide wood efficiently at the
  workshop**.
- Workable metals can **collect and transfer**.
- Stone can **process**.
- Wood can **store, structure, and support**.

These identities do not require every material to have a unique supernatural
ability. They arise from recipes, physical associations, and positions in the
workshop graph.

## Evaluation rules

Before an infrastructure feature becomes committed content, ask:

1. Which repetitive task does it relieve?
2. What progress must the player make before building it?
3. Which materials does it preserve or create demand for?
4. Does it connect to existing mining, processing, storage, or crafting?
5. Is its behavior understandable from the blocks involved?
6. Does it supplement active play without making the world irrelevant?
7. Does it require electricity or machine bureaucracy without a compelling
   reason?
8. Would a simpler pot, hopper, chest, furnace, or redstone interaction solve the
   same problem?
9. If it belongs in the workshop, can the operation be explained by the
   installed hand tool?

## Current candidate loop

One possible workshop arc is:

> Cave exploration -> clay and accessible metals -> crusher, furnace, and
> manual workshop -> knives, saws, hammers, and bronze tools -> more capable
> processing and excavation -> larger chests, improved hoppers, and a
> bulk-crafting table -> automated processing and bonsai collection -> a base
> that supplies and combines common inputs while the player pursues deeper
> materials

This is not a roadmap commitment. It is a compact expression of how geology,
metallurgy, logistics, and resource support could become one continuous
progression instead of four unrelated feature lists.
