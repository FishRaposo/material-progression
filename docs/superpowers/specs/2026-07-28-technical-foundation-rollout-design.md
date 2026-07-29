# Playable Technical Foundation Rollout

**Status:** approved for implementation.

## Goal

Complete the remaining technical foundation of Material Progression without
building dormant frameworks. Every foundation must ship with a permanent,
survival-usable gameplay consumer that exercises its real runtime path.

The rollout preserves the project's core loop:

> Explore -> extract -> process -> combine -> craft -> gain agency

The Bulk Crafting Table is part of this rollout as a usable block, not merely a
planner library or diagnostic interface.

## Foundation acceptance rule

A foundation is complete only when all of the following are true:

1. A player can reach and use its consumer in an ordinary survival world.
2. The consumer exercises the production API rather than a test-only adapter.
3. Failure is legible in the interface and does not partially consume items.
4. Shared material inputs use vanilla or `c:` tags.
5. Fast contracts cover resource invariants and live GameTests cover observable
   gameplay.
6. The compiled distribution JAR contains the consumer and matches the tested
   production tree.

Tests remain mandatory, but tests alone are not proof of a foundation.

All Material Progression content is also exposed in one dedicated creative-mode
tab for testing. The tab follows progression order and includes block items for
the normally world-placed Loose Rocks and Ground Stick blocks so testers can
place every registered block without commands.

## Delivery strategy

Three implementation orders were considered:

- **Playable vertical foundations:** introduce infrastructure only with the
  smallest durable feature that needs it.
- **Infrastructure-first platform:** build generalized APIs before their
  consumers.
- **Bulk-table-first:** start with the hardest system, then extract shared
  infrastructure afterward.

The rollout uses playable vertical foundations. Infrastructure-first would
make correctness depend on artificial fixtures, while bulk-table-first would
mix recipe planning, inventories, networking, menus, persistence, and content
authoring into one unreviewable change.

Each phase is independently shippable. Later phases build on the production
interfaces proven by earlier gameplay.

## Phase 1: Content authoring and primitive completion

### Foundation

- NeoForge data generation for recipes, tags, block states, item models, loot,
  and translations where the output is regular.
- Declarative material-family definitions for material forms, tool profiles,
  repair tags, and generated assets.
- A general progression-rule registry so material interactions are expressed as
  focused rules rather than unrelated event handlers.
- Versioned server configuration with validation and explicit defaults.

Hand-authored JSON remains valid for exceptional models, unusual loot, and
world-generation files. Generated resources are checked into
`src/generated/resources` and verified for deterministic output.

### Playable proof

The primitive opening becomes a complete survival loop:

> Rock and stick -> flint knife -> cut vegetation -> plant fiber -> string

The same slice makes mined natural stone drop Rocks instead of cobblestone,
retains the four-Rock cobblestone recipe, and tunes ground sticks toward trees
and shrubs without making the bootstrap biome-dependent.

The knife is a real lightweight weapon and vegetation tool. Suitable plants
broken with a knife yield fiber; the same plants retain vanilla behavior with
other tools. Plant fiber is published and consumed through a shared `c:` tag.
The rule layer therefore has two permanent consumers: log harvesting and
knife-based plant harvesting.

Existing tin and bronze families migrate to the declarative definitions so the
abstraction is proven against the current content rather than only the new
knife.

### Completion evidence

A new world can reach logs and string without random flint, a workstation, or
commands. Generated resources reproduce the complete regular material catalog,
and rerunning data generation produces no uncommitted diff.

## Phase 2: Manual Workshop

### Foundation

- A generalized data-driven manual-processing recipe type.
- Recipe fields for installed tool category, compatible tool tag, input,
  output, output count, durability cost, and operation time.
- A reusable atomic processing transaction that validates tool durability,
  input, output capacity, and remainders before mutation.
- A Workshop block entity, menu, screen, and server-authoritative recipe
  selection payload.
- Versioned Workshop persistence.

The Workshop has dedicated tool, input, and output slots. The installed tool
remains visible and persistent. When several recipes match, the player selects
the output in a stonecutter-like list. The server re-resolves the recipe and
validates every operation; the client never dictates item consumption.

### Playable proof

The first Workshop ships with three useful tool families:

- **Knife:** Rock to improved flint-shard yield; suitable plants to improved
  fiber yield.
- **Hammer:** stone to gravel; gravel to sand; ore or raw metal to two dust at
  a high durability cost.
- **Saw:** logs to improved plank yield; planks to improved stick yield.

The knife, hammer, and saw remain field tools: knife as vegetation tool and
light weapon, hammer as pickaxe alternative, and saw as axe alternative. Field
harvesting never silently applies Workshop transformations.

The Workshop does not smelt, alloy, cultivate, transport items, or execute
ordinary crafting recipes.

### Completion evidence

Every initial operation is usable through the shipped screen. A full output
slot, insufficient tool durability, invalid tool, or invalid input consumes
nothing. Manual ore crushing and the fuel-burning Crusher both yield two dust,
leaving durability versus fuel as the meaningful choice.

## Phase 3: Transactions, networking, and usable batching

### Foundation

- A shared inventory-view abstraction over internal and explicitly supplied
  item handlers.
- Simulation followed by commit, with deterministic slot ordering.
- Atomic consumption, production, durability, and crafting-remainder support.
- Server-authoritative payloads with sequence validation and stale-preview
  rejection.
- Reusable preview models for available, required, missing, produced, and
  remainder stacks.

### Playable proof

The Workshop gains a batch selector and operation preview. A player may request
several repetitions of the selected manual operation; the preview shows the
maximum executable batch, input cost, tool durability cost, output, and
remainders. Execution is all-or-nothing for the accepted batch.

This is a permanent quality-of-life feature, not a diagnostic screen. It proves
the transaction and networking layers on a smaller recipe graph before those
layers are extended across adjacent inventories by the Bulk Crafting Table.

### Completion evidence

Closing, reopening, racing, or changing the Workshop inventory cannot cause a
stale client preview to duplicate or delete items. Requested batches either
complete exactly or leave every participating stack unchanged.

## Phase 4: Usable Bulk Crafting Table

### Foundation

- A recipe graph over ordinary crafting recipes.
- Recursive expansion of craftable intermediates.
- Existing-finished-item and partial-intermediate reuse.
- Tag ingredients, alternatives, stable material selection, recipe batch
  sizes, surplus, container remainders, and cycle detection.
- Transactional execution across the player's inventory, the table's internal
  inventory, and directly adjacent supported inventories.
- Versioned planner limits and explicit failure results.

The planner is bounded by recipe depth, expanded-node count, and requested
quantity. Hitting a limit returns a visible failure and consumes nothing.
Remote inventories and recursive hopper-chain discovery are outside the local
boundary.

### Playable proof

The Bulk Crafting Table ships as a craftable, persistent block with:

- Searchable ordinary recipe selection.
- Requested-quantity controls.
- Total base-material cost.
- Available and missing requirements.
- Maximum currently craftable quantity.
- Planned material consumption and surplus.
- A server-validated craft action.
- Internal ingredient/surplus storage.
- Directly adjacent inventory access.
- Hopper insertion and output extraction.

The first complete player proof is deliberately ordinary:

> Store logs beside the table -> select a stick-consuming recipe -> request a
> quantity -> preview logs, planks, and sticks -> craft the final output and
> retain unavoidable surplus.

The table also ships with a fixed upgrade-slot budget and the five settled
module families:

- Storage
- Filter
- Priority
- Reservation
- Memory

Quantity capabilities stack; binary capabilities do not. Higher tiers upgrade
their lower-tier predecessors.

### Completion evidence

The table handles a representative vanilla chain, mixed tag materials,
pre-existing intermediates, batch surplus, a container remainder, an
alternative recipe, and a deliberately cyclic test recipe without item loss or
infinite recursion. The ordinary crafting table remains the faster portable
choice for one-off recipes.

## Phase 5: Ecosystem hardening

### Interoperability

A development-only companion mod supplies foreign tagged Rocks, fiber, tin,
bronze, tools, logs, and recipes. The shipped gameplay systems consume them
through the same production tags and recipe APIs used in a real mixed-mod
instance. No production special cases may name the companion mod.

The gameplay proof is cross-mod use in the primitive recipes, Workshop, Crusher,
tool repair, and Bulk Crafting Table.

### Recipe discovery

Just Enough Items is the first optional recipe-viewer integration for
Minecraft 26.2 NeoForge. It receives Workshop and crushing categories, complete
recipe displays, catalysts, and transfer/help affordances where the API
supports them. The mod remains functional when JEI is absent.

The Bulk Crafting Table uses its own selection and planning interface because
its job is execution planning, not merely recipe display.

### Persistence and migration

Workshop and Bulk Crafting Table data use explicit schema versions. Migration
is incremental, idempotent, preserves unknown-safe inventory contents, and
falls back to a recoverable disabled state instead of discarding items when
data is invalid.

The gameplay proof loads fixtures representing the previous schema, then opens
and uses the migrated block normally with its tool, inventory, selected recipe,
upgrades, reservations, priorities, and saved jobs intact.

### Release automation

CI remains the authority for contracts, Java compilation, live GameTests, and
distribution identity. A tagged release workflow builds the exact verified
JAR, validates version agreement, generates release notes from the changelog,
and creates a GitHub release. Modrinth or CurseForge publication remains a
separate opt-in job until project IDs and publishing credentials are explicitly
configured.

Release automation is not misrepresented as gameplay. It is proven by producing
the exact compiled artifact containing all gameplay proofs from phases 1-5.

## Shared architecture boundaries

- Ordinary crafting stays in vanilla grids and the Bulk Crafting Table.
- Direct hand-tool transformations stay in the Workshop.
- Fuel-based crushing stays in the Crusher.
- Heating and smelting stay in furnaces.
- Item movement stays physical through inventories and hoppers.
- The planner never invokes Workshop, Crusher, furnace, world interaction, or
  arbitrary modded machine recipes.
- Client messages express intent; servers resolve recipes, inventories, and
  results.
- No production API exists solely for GameTests.

## Testing and playability

Every behavior starts with a failing focused contract, JVM test, or GameTest.
Pure recipe planning and transaction algorithms receive fast JVM tests.
Registries, menus, saved data, block entities, tags, world interactions, and
real player actions receive live GameTests.

Each phase also gains a documented manual survival checklist. Automation proves
determinism; a human playtest remains responsible for pacing, interface
clarity, and whether the material choices are enjoyable.

Before a phase is integrated:

1. Data generation is clean and deterministic.
2. Fast contracts pass.
3. JVM algorithm tests pass.
4. Live GameTests pass.
5. The production JAR is rebuilt and inspected.
6. The JAR is byte-identical to the tested build.
7. Documentation describes shipped and planned behavior accurately.

## Rollout and branch policy

Each phase is designed, planned, implemented, verified, and integrated as its
own reviewable branch. A phase may expose small stable interfaces needed by the
next phase, but it may not land a dormant subsystem whose only consumer is a
test.

The first implementation target is Phase 1. The Workshop, transaction batching,
Bulk Crafting Table, and ecosystem hardening receive separate child specs and
plans when their preceding production interfaces are known.
