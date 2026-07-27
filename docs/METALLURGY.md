# Metallurgy and Material Families

> **Status: provisional.** No listed material, recipe, tier, or family is
> guaranteed for a release. Bronze is implemented as a prototype, not canon.

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

This allows parallel and intersecting paths instead of a universal sequence.

The graph begins before metal. Stone and flint establish the first trade-off:
ordinary stone is the primitive workhorse, while flint may provide faster,
sharper, less durable tools at the same harvest capability. Metal knives and
saws can later extend those cutting and efficiency roles instead of every
material existing only as another pickaxe set.

## Mundane metals are allowed to be mundane

The roster can contain materials whose primary value is relational:

- **Tin** may be a modest early material and an ingredient in bronze.
- **Copper** may be accessible early, support infrastructure, and remain useful
  through several alloys. Copper ducts are one candidate expression of that
  infrastructure role.
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

This small loop tests:

- New ore generation
- Primitive fuel-based processing
- Ore doubling
- Dust-based alloying
- A complete equipment family
- Tags, recipes, loot, localization, and assets

Its current `3:1` recipe, yields, tool values, ore distribution, and even its
position as the first vertical slice are provisional.

## The stone crusher

The current crusher is intentionally close to a furnace:

> **1 ore or raw metal -> 2 dust**

The dust can be smelted into two ingots or used in an alloy recipe. The machine
therefore creates two concrete forms of value: increased yield and access to
alloying.

The intended fantasy is primitive metallurgy, not an energy system. Faster
upgrades, alternative crushers, automation, and later processing stages are open
questions. None are required merely because an inspiration mod included them.

## Candidate progression relationship

One current hypothesis is:

- Primitive tools exploit exposed and soft geology.
- Early mundane metals provide options but limited excavation power.
- Bronze is an accessible manufactured route toward serious mining.
- Iron supports sustained excavation and helps make strip mining economical.
- Later alloys and specialist materials diversify rather than merely replacing
  iron.

This relationship needs playtesting. In particular, bronze must have a real job
created by geology; "iron stats but brown" is not enough by itself.

## Materials beyond tool sets

The metallurgical graph should not terminate at pickaxes, armor, and swords.
Materials can remain useful by becoming parts of the workshop:

- A hopper can plausibly be fabricated from more than iron.
- Different workable metals may provide equivalent hopper recipes or distinct
  hopper tiers.
- Copper ducts may provide simple item transport.
- Crushers, furnaces, storage, and collection blocks can create continuing
  demand for base metals.
- Knives and possible saws can make a material useful for plant harvesting,
  crafting, or wood efficiency even when its mining tools are obsolete.

Whether alternative hoppers are equivalent, tiered, or specialized is
undecided. The principle is narrower: an item funnel has no strong fantasy that
makes iron its only reasonable material, so iron exclusivity should not be
preserved by inertia.

The same test applies to every infrastructure block: material flexibility
should make physical and progression sense. The goal is not to manufacture
dozens of recolored machines solely to consume every ingot.

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
