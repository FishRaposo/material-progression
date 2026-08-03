# Reference Mods and Inspirations

> **Status: contextual.** These projects explain the design lineage; they are
> references, not feature checklists.

## How to use these references

These links are collected so implementation work can quickly inspect an
existing mechanic, data model, interface, or design explanation. Material
Progression does not depend on these mods, bundle their assets, or treat their
code as code to transplant. Most of the closest references target older
Minecraft and Forge versions, especially 1.12.2, so their source is useful for
understanding behavior and architecture rather than as version-compatible
implementation.

When a reference materially influences a shipped feature, preserve that credit
in this document and implement the feature independently against current
Minecraft 26.2 and NeoForge APIs. Each linked repository retains its own license
and attribution requirements.

## Quick reference

| Project and credit | What to study | Links |
| --- | --- | --- |
| Divergent Underground by cleverpanda714 | Geological resistance, natural-versus-placed stone behavior, rock drops, cave-led access progression | [Project](https://www.curseforge.com/minecraft/mc-mods/divergent-underground) · [Source](https://github.com/cleverpanda/Divergent-Underground) |
| Metallurgy 4 by ShadowClaimer; 1.12.2 Reforged port by Davoleo and Matpac | Broad material families, alloy networks, themed metals, crushers, and non-linear material roles | [Project](https://www.curseforge.com/minecraft/mc-mods/metallurgy-4-reforged) · [Source](https://github.com/Davoleo/Metallurgy-4-Reforged) · [Wiki](https://github.com/Davoleo/Metallurgy-4-Reforged/wiki) |
| Base Metals by DrCyano and the MMD team | A practical base-metal roster, ordinary alloys, dust forms, equipment families, and manual ore doubling | [Project](https://www.curseforge.com/minecraft/mc-mods/base-metals) · [Source](https://github.com/MinecraftModDevelopmentMods/BaseMetals) |
| SimpleOres by AleXndrTheGr8st, maintained by Sinhika and contributors | Compact, self-contained ore families with distinct equipment identities | [Project](https://www.curseforge.com/minecraft/mc-mods/simpleores) · [Source](https://github.com/Sinhika/SimpleOres2) · [Wiki](https://github.com/Sinhika/SimpleOres2/wiki) |
| Fusion by AleXndrTheGr8st, maintained by Sinhika and contributors | Growing a small ore vocabulary through alloys and focused processing | [Project](https://www.curseforge.com/minecraft/mc-mods/fusion) · [Source](https://github.com/Sinhika/Fusion) |
| Bonsai Trees by davenonymous | Compact, slow, physical production of normal tree drops | [Project](https://www.curseforge.com/minecraft/mc-mods/bonsai-trees) · [Source](https://github.com/davenonymous/BonsaiTrees) |
| No Tree Punching by AlcatrazEscapee | Ground rocks, primitive tools, plant fiber, knives, saws, and correct-tool harvesting | [Project](https://www.curseforge.com/minecraft/mc-mods/no-tree-punching) · [Source and documentation](https://github.com/alcatrazEscapee/no-tree-punching) |
| Pyrotech by CodeTaylor | Physical primitive processing, chopping blocks, worktables, tool-mediated recipes, and an early-game guide | [Project](https://www.curseforge.com/minecraft/mc-mods/pyrotech) · [Source](https://github.com/codetaylor/pyrotech-1.12) · [Documentation](https://pyrotech.readthedocs.io/) |
| TerraFirmaCraft by AlcatrazEscapee and contributors | Persistent ground rocks and sticks, deterministic pickup resources, knapping, alloys, and in-game progression documentation | [Project](https://www.curseforge.com/minecraft/mc-mods/terrafirmacraft) · [Source](https://github.com/TerraFirmaCraft/TerraFirmaCraft) · [Field Guide](https://terrafirmacraft.github.io/Field-Guide/en_us/) |
| MineFantasy Reforged by the TeamMFR contributors | Tool-mediated workstation recipes, hammering, forging, and visible manual production steps | [Project](https://www.curseforge.com/minecraft/mc-mods/minefantasy-reforged) · [Source](https://github.com/TeamMFR/MineFantasyReforged) |
| Geolosys by oitsjustjose | Large geological deposits, surface samples, prospecting, and configurable compatibility-first ore generation | [Project](https://www.curseforge.com/minecraft/mc-mods/geolosys) · [Source archive](https://github.com/oitsjustjose/Geolosys) |
| CraftingTable IV by Elec332 | Listing currently craftable outputs, explicit click-to-craft selection, recipe discovery, and live ingredient availability | [Project](https://www.curseforge.com/minecraft/mc-mods/craftingtable-iv) · [Source](https://github.com/Elecs-Mods/CraftingTable-IV) · [Behavior overview](https://forum.feed-the-beast.com/threads/craftingtable-iv-the-successor-of-the-famous-craftingtable-iii-mod.95818/) |
| Tinkers' Construct by mDiyo, boni, KnightMiner, and the SlimeKnights contributors | A persistent crafting inventory and direct access to adjacent physical inventories | [Project](https://www.curseforge.com/minecraft/mc-mods/tinkers-construct) · [Source](https://github.com/SlimeKnights/TinkersConstruct) · [Crafting Station overview](https://tinkers-construct.fandom.com/wiki/Crafting_Station) |
| Furnus by KidsDontPlay | A single machine customized through finite upgrade slots, stackable upgrade types, automation controls, and inventory expansion | [Project](https://www.curseforge.com/minecraft/mc-mods/furnus) · [Source](https://github.com/KidsDontPlay/Furnus) |
| Iron Chests by cpw, maintained by progwml6 and contributors | In-place upgrades, preserved inventories, and progressively larger physical storage | [Project](https://www.curseforge.com/minecraft/mc-mods/iron-chests) · [Source](https://github.com/progwml6/ironchest) |
| Better With Mods by BeetoGuy and contributors; public 1.12 fork maintained by Rebirth of the Night | Physical local automation, saws, millstones, filtered item movement, and legible world-space machinery | [Project](https://www.curseforge.com/minecraft/mc-mods/bwm-suite) · [Source fork](https://github.com/Rebirth-of-the-Night/BetterWithMods) |

## Reference map by system

Use this table when implementation starts from a Material Progression system
rather than from a known reference mod.

| Material Progression system | Primary references | What each reference contributes |
| --- | --- | --- |
| Primitive world bootstrap | TerraFirmaCraft, No Tree Punching, Pyrotech, Divergent Underground | Ground rocks and sticks; primitive sharp edges and fiber; physical early processing; stone yielding rock pieces |
| Manual workshop | Pyrotech, MineFantasy Reforged, No Tree Punching, Better With Mods | Worktable and block interaction patterns; tool-mediated recipes; knife, hammer, and saw roles; physical processing machines |
| Geology and ore discovery | Vanilla Nether Gold Ore, Divergent Underground, Geolosys, TerraFirmaCraft | Dimension-native ore hosts; resistance and cave access; deposits and surface samples; readable terrain and prospecting |
| Metal and alloy graph | Metallurgy, Base Metals, SimpleOres, Fusion, TerraFirmaCraft | Broad material roles; practical base metals; compact ore families; alloy expansion; composition-aware metallurgy |
| Bulk-crafting interface | CraftingTable IV, Tinkers' Construct, Furnus | Explicit output selection; local persistent inventories; finite slotted upgrades |
| Shallow logistics | Tinkers' Construct, Iron Chests, Better With Mods, Bonsai Trees | Adjacent inventory access; larger physical storage; hopper-like routing and local machines; compact background production |

These are conceptual and implementation search paths, not packages to compose.
For example, bulk crafting combines CraftingTable IV's output-first interface,
the Crafting Station's local inventory boundary, and Furnus's upgrade vocabulary,
but its recursive recipe planner is a Material Progression system of its own.

## Item-art study boundary

The item-art baseline in [Item Art Direction and Reference Baseline](ITEM_ART.md)
uses No Tree Punching, TerraFirmaCraft, and Divergent Underground only to study
category readability: loose material, clustered cobble, primitive cutting
tools, and family distinction. The authoritative study sources are the public
[No Tree Punching source](https://github.com/alcatrazEscapee/no-tree-punching),
[TerraFirmaCraft source](https://github.com/TerraFirmaCraft/TerraFirmaCraft),
and [Divergent Underground source](https://github.com/cleverpanda/Divergent-Underground).
Their assets remain local, ignored research material; no reference or vanilla
pixels are bundled, traced, or otherwise used as Material Progression art.
Future art must retain that boundary and meet the independent authoring and
release-review requirements in [Item Art Direction and Reference Baseline](ITEM_ART.md).
The pinned local study inventory and refresh boundary are recorded in
[Asset Provenance Inventory](ASSET_PROVENANCE.md).

## Opening/geology implementation lineage

The implemented opening pass used the references selectively:

- **Divergent Underground** informed multiple geological resistance levels,
  family differences, exposure relief, natural-versus-placed behavior, and stone
  fragment drops. Material Progression implements its own four-level,
  dimension/depth/family formula, sixteen-family data catalog, placed-block
  attachment, and Rock/cobble loop.
- **No Tree Punching** informed the configurable log rule, Knives, Plant Fiber,
  and Saws. Material Progression removes the random first-Flint gate, keeps
  ordinary one-Log-to-four-Plank crafting, and gives both Flint and Bronze Saws
  a visible six-Plank Workshop operation.
- **Pyrotech** and **MineFantasy Reforged** informed the physical
  tool-plus-material Workshop fantasy and transactional durability/progress
  questions. The implemented block entity, menu, recipes, state machine, and
  rendering are independent Material Progression code.
- **Better With Mods** reinforced optional efficiency and local, visible
  material processing. The opening pass does not import its mechanical-power
  network or hardcore baseline nerfs.
- **TerraFirmaCraft** informed persistent world-space Rocks and Sticks and the
  connection between samples and local terrain. The opening keeps ordinary
  Minecraft crafting and does not require knapping or a custom field guide.

No reference project is a dependency, and no reference code was copied. Source
links exist for behavioral study and credit. Ore samples, deposits, prospecting,
pottery, logistics, bonsai, bulk crafting, and expanded metallurgy remain later
reference work rather than shipped opening features.

## Divergent Underground

Divergent Underground is the strongest reference for making the underground
itself participate in progression.

The important ideas are:

- Geological strata can have meaningfully different resistance.
- Tool tiers can affect both mining time and whether a block yields drops.
- Exposed stone can be easier to work than fully enclosed geology.
- Caves therefore become useful breaches instead of decorative voids.
- Naturally generated blocks can behave differently from player construction.
- Strip mining can be delayed through physical rules rather than prohibited by
  an arbitrary gate.

For this project, that is a "why is this not vanilla?" foundation rather than an
optional side feature. Without environmental pressure, a large material graph is
easy to bypass.

## Vanilla Nether Gold Ore

Vanilla Nether Gold Ore is the behavioral reference for the planned
Netherrack-hosted ore family: a vein can visibly belong to the dimension that
contains it instead of being represented only by an Overworld Stone form. The
future Material Progression ore matrix extends that host-aware idea to every
locally valid raw-stone family, including Basalt and Blackstone in the Nether
and End Stone in the End.

The precedent is deliberately narrow. It guides host readability and
dimension-native placement, not a copied sprite, block model, loot table, drop
rate, or source implementation. Each Material Progression ore keeps its own
data-defined harvest requirement and material-processing identity, and a gravel
ore remains the separately planned surface form excavated with a Shovel.

## Metallurgy before Metallurgy 4

Older Metallurgy versions are the main reference for material breadth and
structure:

- Large families of mundane, precious, fantasy, utility, Nether, and End metals
- Overlapping progression paths
- Dust-based alloys
- Primitive ore doubling through crushers
- Metals whose value came from their position in a network
- Precious materials that traded ordinary practicality for enchantability
- Processing that could develop from primitive to more capable forms

The lesson is not "copy every metal." It is that a sufficiently coherent family
creates a metallurgical ecosystem rather than a short equipment ladder.

## Base Metals

Base Metals represents "baby Metallurgy": a practical foundation of familiar
metals, equipment, dusts, straightforward alloys, and uncomplicated ore
processing.

Its relevance is permission to let some metals be ordinary. A general base
family has value even when each member does not carry a unique supernatural
mechanic.

## SimpleOres and Fusion

SimpleOres is another reference for a compact, self-contained mineral expansion.
Its Fusion add-on demonstrates how a simple ore vocabulary can grow through
alloys without immediately becoming an industrial technology mod.

## Bonsai Trees

The Bonsai Trees concept is a reference for compact, slow, background production
of tree resources. Its relevance is not the exact interface, timings, recipes,
or drop tables. The useful fantasy is a tiny cultivated tree that produces
ordinary tree drops over time and can feed them into collection infrastructure.

For this project, bonsai can answer a practical consequence of expanded
metallurgy: a larger workshop and equipment roster creates persistent demand for
sticks and other tree products. A bonsai does not need to replace forests or
make wood acquisition trivial. It can turn clay, metal, space, and time into a
supplemental supply.

## No Tree Punching

No Tree Punching is a reference for making wood, plants, gravel, and flint
participate in the opening rather than becoming disposable scenery.

The strongest ideas for this project are:

- Preventing logs from being harvested by hand as a configurable rule
- A flint hatchet as the first route into wood
- Loose rocks as a world-space primitive resource
- Knives as a persistent lightweight tool category
- Flint knives as an immediate use for gravel
- Cutting plants into fiber
- Crafting string from plant fiber
- Saws as a possible way to improve wood yield

The important departure is the first-flint gate. In No Tree Punching, loose
rocks can support later flint tools, but a player still needs randomly obtained
flint to make the first knife that cuts them into shards. This project removes
that dependency: one Rock sharpens into one flint shard through a shapeless 2x2
recipe, one flint produces two shards, and four Rocks form cobblestone.

That deterministic bootstrap makes configurable tree-punching prevention much
more attractive. The player must gather a sharp stone and a stick before
harvesting the first tree, but world generation and flint-drop luck cannot make
the run impossible. Knapping, aggressive baseline wood nerfs, and additional
crafting rituals still require separate justification.

The first hatchet is correspondingly direct: one flint shard beside a two-stick
handle in an upside-down L in the 2x2 crafting grid. It is an axe recipe with
the top row removed, not a bound survival tool, so string is not required before
the knife can make plant fiber.

The useful lesson is that simple tools can give familiar resources meaningful
jobs. The project keeps that progression while removing ceremony and blockers
that exist before the player has agency. Divergent Underground's loose-rock
stone drops complete the same loop from the opposite direction: wood requires a
cutting tool, while stone yields pieces that must be accumulated or used as
primitive edges.

## Pyrotech

Pyrotech is the strongest broad reference for the manual workshop. Its
primitive devices make crafting and processing visible in the world through
chopping blocks, worktables, anvils, hand tools, and illustrated progression
documentation.

The useful implementation questions are:

- How a workstation recipe declares both ingredients and a required tool
- How interaction state is communicated without hiding every action in a GUI
- How tool use, durability, output, and remainders form one safe transaction
- How multiple primitive devices divide responsibilities without creating
  arbitrary one-recipe blocks
- How an in-game guide teaches an unfamiliar opening loop

Material Progression is deliberately less ceremonial and less survival-heavy.
The workshop may have an interface, ordinary crafting remains available, and a
physical action must earn its place through material yield, tool choice, or
progression. Pyrotech supplies concrete interaction patterns, not a mandate to
reproduce its complete early game.

## TerraFirmaCraft

TerraFirmaCraft is the clearest reference for a world that visibly provides the
player's first sticks and stones. Its field guide begins with loose rocks and
twigs scattered on the ground, then develops those resources into knapping,
primitive tools, prospecting, and metallurgy.

For the primitive opening, study:

- Placement and pickup behavior for persistent ground resources
- Distribution rules that make the first tool deterministic across climates
- Visual distinction, collision, replacement, and regeneration behavior
- The relationship between loose samples and the geology beneath them
- How a guide turns a non-vanilla opening into something discoverable

For later metallurgy, its alloy and processing systems are valuable examples of
materials remaining meaningful beyond equipment tiers.

Material Progression keeps ordinary Minecraft recipes, blocks, biomes, and
survival assumptions. It does not adopt knapping as a required minigame, replace
the calendar or climate model, or become a total conversion. The immediate
lesson is much smaller: visible sticks and rocks make "gather materials, then
make a tool" readable without depending on leaf-drop luck.

## MineFantasy Reforged

MineFantasy Reforged is a focused reference for recipes that require the player
to operate a workstation with a tool. Its crafting and forging systems make
hammering and production steps part of the interaction rather than merely
putting a hammer-shaped item into a recipe grid.

The manual workshop should study:

- How a workstation records a selected recipe and partial progress
- How repeated tool actions validate the held tool and consume durability
- How the block exposes progress and failure states to the player
- How recipe data separates ingredients, tools, actions, and outputs
- How automation boundaries prevent a manual station from accidentally
  becoming a free machine

Material Progression's workshop is smaller and more utilitarian. It is intended
to make knife, hammer, and saw capabilities legible, not to reproduce
MineFantasy's combat, forging depth, or medieval total-conversion structure.

## Geolosys

Geolosys is the direct reference for replacing uniform scattered ore generation
with deposits that players can infer from the surface. Its deposits, samples,
configuration, and broad mod compatibility show how geology can remain readable
without hard-coding the system to one mineral roster.

The relevant implementation areas are:

- Deposit definitions and their placement constraints
- Surface samples as evidence rather than guaranteed exposed ore
- Prospecting feedback and the information it reveals
- Data-driven registration of ores supplied by other mods
- World-generation replacement and duplicate-ore avoidance

The linked GitHub repository is an archived source snapshot spanning the older
Forge implementations through the 1.20.1 branch. That makes it a behavioral and
architectural reference, not current NeoForge code.

Material Progression may combine Geolosys-style evidence with Divergent
Underground-style access resistance. It does not need to replace all vanilla
ore generation at once: the first geological slice can prove samples, deposits,
and compatibility with tin and copper before the full material graph exists.

## CraftingTable IV

CraftingTable IV is the closest reference for the surface behavior of the
bulk-crafting table: it discovers recipes the player can currently afford,
shows their outputs, and lets the player explicitly choose what to craft.

The relevant implementation areas are:

- Enumerating the active recipe registry
- Re-evaluating craftability when available ingredients change
- Presenting recipes by output instead of requiring grid placement
- Selecting one result when recipes overlap or use the same ingredient counts
- Consuming ingredients and handling output through one explicit action

Material Progression extends the concept substantially. Its table can request a
quantity, resolve intermediate recipes recursively, preserve batch surplus, and
source from the player, its internal buffer, and directly adjacent containers.
It must also handle recipe cycles, alternative ingredients, container
remainders, and configurable material priorities. CraftingTable IV is therefore
the interface proof and recipe-discovery reference, not the recipe-planner
implementation.

## Tinkers' Construct Crafting Station

The Tinkers' Construct Crafting Station is the clearest reference for a crafting
block that keeps ingredients in place and exposes a directly adjacent physical
inventory. Those two behaviors establish a comprehensible local boundary
without inventing a storage network.

The bulk-crafting table should study:

- Capability lookup and safe access to adjacent inventories
- Preventing duplication when a connected inventory changes mid-operation
- Keeping block inventory contents across closing, saving, and chunk reloads
- Interoperating with vanilla and modded inventory implementations
- Keeping the user interface understandable when several inventories are
  available

Material Progression does not retain a 3x3 ingredient grid and does not
recursively traverse connected storage. The reference is the physical rule:
touching storage is available; everything farther away must be moved locally by
hoppers.

## Furnus

Furnus is the main reference for configuring one machine through installed
upgrades rather than replacing it with a ladder of machine blocks.

The bulk-crafting table borrows that high-level upgrade vocabulary:

- The table has a fixed number of upgrade slots.
- Upgrade types control separate capabilities such as storage, filtering,
  prioritization, reservation, and recipe memory.
- Quantitative upgrades can stack, but the finite slot count prevents
  unbounded expansion.
- Higher-tier modules compress more capability into one slot.
- A higher-tier module is crafted from its lower-tier predecessor so earlier
  investment is preserved.

Material Progression is not copying Furnus's furnace, grinder, recipes, upgrade
values, or interface. The useful reference is the comprehensible relationship
between one persistent block, a small number of slots, and removable modules
that specialize it.

## Iron Chests

Iron Chests is the main reference for improving physical storage without
turning storage into an abstract network.

The relevant ideas are:

- Larger inventories remain ordinary, visible containers.
- Applying an upgrade in place avoids breaking and rebuilding a working setup.
- Contents survive the upgrade.
- Earlier materials and containers remain part of the upgrade path.

For Material Progression, larger chests and hopper variants support a shallow
logistics model. The bulk-crafting table may read its own inventory and directly
adjacent containers, while hoppers move items between those local blocks. It
does not recursively discover an entire base or create a remote storage system.

## Better With Mods

Better With Mods is a useful reference for automation that remains embodied in
ordinary blocks and local item movement. Saws, millstones, filtered hoppers, and
other machines expose where items go and what physical operation transforms
them.

The relevant lessons are:

- A machine can deepen one familiar material operation without becoming a
  universal processing block.
- Item routing is easier to understand when inventories and paths are local.
- Filters and hopper variants can add control without an abstract storage bus.
- Visible processing gives workshop layouts mechanical character.
- Automation can improve throughput while preserving manual routes.

The linked source is the public Rebirth of the Night fork of the original 1.12
mod and should be treated as a version-specific implementation reference.
Material Progression does not currently need Better With Mods' mechanical-power
network or hardcore rules. The useful scope is its vocabulary of legible local
machines and item movement.

## Synthesis

The intended synthesis is:

- **Base Metals and SimpleOres** supply the general metal vocabulary.
- **Older Metallurgy** expands that vocabulary into an interconnected ecosystem.
- **Divergent Underground** makes access to that ecosystem a physical
  progression.
- **No Tree Punching** supplies selective primitive resource logic for flint,
  plants, string, and wood processing.
- **Pyrotech and MineFantasy Reforged** supply references for physical,
  tool-mediated manual workstations.
- **TerraFirmaCraft** demonstrates a deterministic ground-resource opening and
  later material systems that remain readable to the player.
- **Geolosys** demonstrates deposits, surface evidence, prospecting, and
  compatibility-first ore generation.
- **Bonsai Trees** supplies a reference for small-scale, physical resource
  automation.
- **CraftingTable IV** supplies explicit output selection and live recipe
  discovery for the bulk-crafting interface.
- **Tinkers' Construct** supplies the persistent, directly adjacent inventory
  boundary for local crafting storage.
- **Furnus** supplies the finite, slotted-upgrade model for specializing one
  persistent bulk-crafting table.
- **Iron Chests** supplies the in-place-upgrade and larger-physical-container
  vocabulary for shallow logistics.
- **Better With Mods** supplies examples of visible local processing and
  controlled hopper-style item movement.
- **Material Progression** tests how those ideas can become one deliberate,
  vanilla-shaped system.

None of these inspirations determines an exact roster, recipe, machine, or
mechanic. Every borrowed idea must justify itself inside this project's own
progression loop.
