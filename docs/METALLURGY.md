# Metallurgy and Material Families

> **Status: implemented test cell.** Copper/Tin dust processing, provisional
> Bronze alloying, Bronze equipment, Bronze Workshop tools, and Bronze access to
> Dense geology ship in the opening branch. The expanded metallurgy roster
> remains later work.

## Purpose

The metallurgy system provides the tools through which players answer the
underground. Its identity comes from a broad, interconnected vocabulary rather
than a single ladder of progressively stronger pickaxes.

Base Metals and SimpleOres provide the scale of the mundane foundation: common
metals, ordinary equipment, dusts, straightforward alloys, and ingredients that
do not each demand a bespoke mechanic. Older Metallurgy versions provide the
reference for expanding that foundation into overlapping material families and
progression routes.

## The material graph

A material can fill one or more roles:

- Direct early equipment
- Alloy ingredient
- Alternative route to a harvest capability
- Durable excavation material
- Fast but fragile specialist
- Highly enchantable equipment
- Magical conductor or finishing material
- Dimension-specific progression
- Processing, storage, transport, or collection infrastructure
- Ingredient that keeps an earlier resource relevant
- Lightweight cutting or resource-efficiency tool
- Manual processing tool used at a workshop

This allows parallel and intersecting paths instead of a universal sequence.

The graph begins before metal. Mined stone can return Rocks, four Rocks can form
cobblestone, one Rock can be sharpened into one flint shard, and one flint can
supply two shards. Stone and flint then establish the first trade-off: ordinary
stone is the primitive workhorse, while flint may provide faster, sharper, less
durable tools at the same harvest capability.

This is also the first example of the wider tool philosophy. Materials do not
need complete recolored equipment sets merely to occupy the graph. A tool
belongs when it grants a useful interaction or trade-off: Knives cut plants
into Fiber, Saws improve Workshop wood efficiency, and Picks/Hammers expand
geological access. Flint and Bronze currently demonstrate those roles without
making every metal another full vanilla tool set.

## Mundane metals are allowed to be mundane

The roster can contain materials whose primary value is relational:

- **Tin** may be a modest early material and an ingredient in bronze.
- **Copper** may be accessible early, support infrastructure, and remain useful
  through several alloys.
- **Zinc** may principally justify itself through brass.
- **Nickel** or **manganese** may principally exist to open later alloy paths.

These are examples of possible roles, not a confirmed roster.

## Bronze as the current test cell

The prototype currently models:

1. Obtain copper and tin.
2. Crush ore or raw metal into two dust.
3. Combine copper and tin dust into bronze dust.
4. Smelt the alloy dust into bronze ingots.
5. Craft bronze equipment.
6. Use Bronze Picks or Hammers to harvest Dense geology.

This small loop tests:

- New ore generation
- Primitive fuel-based processing
- Ore doubling
- Dust-based alloying
- A complete equipment family
- More durable Knives, Hammers, and Saws without output inflation
- A manufactured iron-level route into Dense geology
- Tags, recipes, loot, localization, and assets

Its current `3:1` recipe, yields, tool values, ore distribution, and even its
position as the first vertical slice are provisional.

## The stone crusher

The current crusher is intentionally close to a furnace:

> **1 ore or raw metal -> 2 dust**

The dust can be smelted into two ingots or used in an alloy recipe. The machine
therefore creates two concrete forms of value: increased yield and access to
alloying.

The implemented workshop hammer is an equal-yield manual route:

> **1 ore or raw metal + substantial hammer durability -> 2 dust**

Both routes deliberately produce the same output. Manual crushing avoids fuel
but is expensive in tool durability. The crusher uses fuel like a furnace,
spares the player's tools, and can become the scalable automation route. Hammer
tier does not gate which ores can be crushed; a better hammer buys longevity,
not exclusive recipes.

The intended fantasy is primitive metallurgy, not an energy system. Faster
upgrades, alternative crushers, automation, and later processing stages are
open questions. None are required merely because an inspiration mod included
them.

## Implemented progression relationship

The opening slice implements:

- Primitive tools exploit exposed and soft geology.
- Early mundane metals provide options but limited excavation power.
- Bronze is an accessible manufactured iron-level route into Dense geology.
- Iron supports sustained excavation and helps make strip mining economical.
- Later alloys and specialist materials diversify rather than merely replacing
  iron.

This relationship still needs survival playtesting. Bronze now has a concrete
job created by geology; the remaining question is whether its access,
durability, and timing make that job satisfying.

## Materials beyond tool sets

The metallurgical graph should not terminate at pickaxes, armor, and swords.
Materials can remain useful by becoming parts of the workshop:

- Knives, hammers, and saws can be installed in a manual workshop to select
  separation, manual crushing, and efficient wood-processing operations.
- A hopper can plausibly be fabricated from more than iron.
- Different workable metals may provide equivalent hopper recipes or distinct
  hopper tiers.
- Crushers, furnaces, storage, collection blocks, hopper variants, and
  bulk-crafting upgrades can create continuing demand for base metals.
- Knives and possible saws can make a material useful for plant harvesting,
  crafting, or wood efficiency even when its mining tools are obsolete.
- Hammers can perform deliberate stone-to-gravel and gravel-to-sand processing,
  crush ores into two dust by paying a high durability cost, then gain a shaping
  role when plates or other formed components justify one.

Whether alternative hoppers are equivalent, tiered, or specialized is
undecided. The principle is narrower: an item funnel has no strong fantasy that
makes iron its only reasonable material, so iron exclusivity should not be
preserved by inertia.

The same test applies to every infrastructure block: material flexibility
should make physical and progression sense. The goal is not to manufacture
dozens of recolored machines solely to consume every ingot.

Ore samples, deposits, prospecting, additional base metals and alloys, precious
and magical families, expanded processing stages, storage, hoppers, and
bulk-crafting components are later work. Their presence in this design does not
make them part of the opening release.

## Evaluation questions for every material

Before a material becomes committed content, ask:

1. Where can the player obtain it?
2. What does obtaining it require?
3. Is it useful directly, relationally, or both?
4. What capability or choice does it create?
5. Which earlier resources does it preserve or revive?
6. What prevents it from collapsing another material's role?
7. Can it serve infrastructure after its equipment tier is obsolete?
8. Does it make the world more interesting, or only the item registry larger?
