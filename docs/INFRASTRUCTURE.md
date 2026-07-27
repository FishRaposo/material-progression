# Workshop Infrastructure and Automation

> **Status: provisional.** Every block, recipe, material choice, output rate,
> and progression position in this document is a design candidate.

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
- Hoppers and funnels
- Ducts or chutes
- Crushers and furnaces
- Fuel, gravity, growth, and elapsed time

No electrical grid is required to justify these systems.

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

## Copper ducts

Copper is a natural candidate for basic item ducts. It is workable, visually
legible, already associated with conduits, and available early enough to support
a modest logistics system.

A duct system could move items between inventories without introducing power.
Its exact behavior is open:

- Directional versus omnidirectional movement
- Push, pull, or passive routing
- Transfer speed
- Filtering
- Interaction with hoppers
- Vertical movement
- Redstone control
- Whether ducts form networks or operate one block at a time

The system should remain smaller and easier to understand than a full logistics
network mod. Copper ducts are justified if they help connect crushers, furnaces,
bonsai, and storage while giving copper a durable infrastructure role.

## Processing automation

The current stone crusher is a fuel-burning furnace analogue:

> **1 ore or raw metal -> 2 dust**

It should be possible to evaluate ordinary Minecraft automation around it:

- A hopper or duct supplies crushable material.
- Another inventory supplies fuel if the final interface supports it.
- Output is routed toward alloying, smelting, or storage.

This does not commit the crusher to full automation, a particular sided-inventory
layout, or any upgrade path. It records the desired relationship: logistics
should gradually connect the primitive processing blocks already present instead
of replacing them with powered machines from an unrelated technology tier.

## Material identity through infrastructure

Infrastructure gives materials verbs beyond combat and mining:

- Copper can **conduct or route**.
- Clay can **cultivate or contain**.
- Workable metals can **collect and transfer**.
- Stone can **process**.
- Wood can **store and structure**.

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
8. Would a simpler pot, hopper, duct, furnace, or redstone interaction solve the
   same problem?

## Current candidate loop

One possible workshop arc is:

> Cave exploration -> clay and accessible metals -> crusher and furnace ->
> bronze tools -> more capable excavation -> multi-metal hoppers and copper
> ducts -> automated processing and bonsai collection -> a workshop that
> supplies common inputs while the player pursues deeper materials

This is not a roadmap commitment. It is a compact expression of how geology,
metallurgy, logistics, and resource support could become one continuous
progression instead of four unrelated feature lists.
