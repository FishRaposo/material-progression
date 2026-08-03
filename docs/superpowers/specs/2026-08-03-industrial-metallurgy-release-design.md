# Industrial metallurgy release design

## Status

Approved design boundary. This document defines the next complete Material
Progression release slice. It is not yet implemented.

## Outcome

Material Progression becomes the required physical-material foundation for the
author's future mods. The release completes a coherent arc:

> Read surface evidence -> excavate gravel ores -> follow a deposit through
> its local stone -> process ore with fuel -> alloy useful metals -> craft full
> equipment -> gain durable control over deeper terrain.

The slice is deliberately historical and industrial. It adds no fantasy metals,
enchanting changes, potion changes, silvering, or Smithing Table upgrades.
Those systems belong to a later expansion or to the separate RPG-progression
mod.

## Goals

- Make the visible host geology of an ore as meaningful as the ore material.
- Give Shovels an exploration use through harvest-tiered surface Gravel Ores.
- Complete an industrial metal graph in which every material has an equipment
  role, an alloy relationship, a processing purpose, or more than one of
  those roles.
- Give every equipment-capable material a complete vanilla-style tool and
  armor family, including Wood, Stone, and Flint.
- Keep processing physical, fuel-burning, and legible: no electrical grid,
  mandatory multiblocks, or steps that merely add delay.
- Expose stable Java, tag, and datapack interfaces so future dependent mods
  can build on Material Progression without registering duplicate materials.

## Non-goals

- Enchanting, potion, or magical-material mechanics.
- Silvering and every Smithing Table upgrade path.
- Fantasy metals or new dimension-specific fantasy materials.
- Better Grinder tiers, electrical machinery, bonsai, bulk crafting, and the
  broad logistics layer.
- A new armor system beyond normal vanilla-style equipment slots and material
  attributes.
- Replacing the protected Ground Stick or Loose Rock world-facing art.

## Material catalog

### Equipment-capable materials

Every listed material has a complete set of Pickaxe, Axe, Shovel, Hoe, Sword,
Knife, Hammer, Saw, Hatchet, Helmet, Chestplate, Leggings, and Boots. A shared
material profile supplies capability, mining speed, durability, combat values,
armor attributes, repair ingredients, localization keys, and compatibility
tags to all of those forms.

| Band | Material | Supply | Intended identity |
| --- | --- | --- | --- |
| Primitive | Wood | vanilla logs and planks | Cheapest emergency equipment and first protection. |
| Primitive | Stone | stone and cobblestone | Cheap, durable primitive baseline. |
| Primitive | Flint | Rocks and Flint | Sharper and faster than Stone, but fragile. |
| Early metal | Copper | Copper ore | Accessible balanced first metal. |
| Early metal | Tin | Tin ore | Modest direct equipment and Bronze ingredient. |
| Early alloy | Bronze | Copper plus Tin | Reliable manufactured iron-level route into Dense geology. |
| Industrial | Iron | Iron ore | Familiar dependable baseline. |
| Industrial | Steel | Iron plus Coal or Charcoal carbon | Durable processing-earned improvement over Iron. |
| Industrial | Zinc | Zinc ore | Light workable metal and Brass ingredient. |
| Industrial | Brass | Copper plus Zinc | Responsive equipment and useful practical alloy. |
| Industrial | Lead | Lead ore | Heavy, slow equipment with protective armor trade-offs. |
| Industrial | Nickel | Nickel ore | Durable direct equipment and Invar ingredient. |
| Industrial | Invar | Iron plus Nickel | Stable, high-durability industrial equipment. |
| Precious | Silver | Silver ore | Ordinary precious-metal equipment; no magic behavior in this release. |
| Precious | Gold | Gold ore | Fast, fragile vanilla-like equipment. |
| Precious | Rose Gold | Gold plus Copper | Gold responsiveness moderated by Copper durability. |
| Vanilla capstone | Diamond | vanilla | Existing high-end baseline. |
| Vanilla capstone | Netherite | vanilla | Existing raw-power capstone. |

Leather and Chainmail remain compatible vanilla armor paths. Wood and Stone
armor are new first-class primitive sets. Wood recipes consume generic shared
plank inputs; Stone recipes consume generic shared cobblestone or stone inputs,
not a separate set for every wood species or stone family.

The equipment table is a directional balance contract, not a promise of a
strict numerical ladder. Exact values are selected from one data table during
implementation and must preserve every stated trade-off in both field tools
and armor. No material may be added merely as a recolored replacement.

### Ore-bearing materials and alloys

Copper, Tin, Iron, Gold, Zinc, Lead, Nickel, and Silver are ore-bearing
materials. Bronze, Steel, Brass, Invar, and Rose Gold are alloys: they never
receive ore, Gravel Ore, sample, or deposit forms.

The alloy graph is intentionally compact:

- Copper + Tin -> Bronze
- Iron + Coal or Charcoal carbon -> Steel
- Copper + Zinc -> Brass
- Iron + Nickel -> Invar
- Gold + Copper -> Rose Gold

Alloys use dust ingredients and furnace smelting, matching the shipped Bronze
pattern. No alloy uses an enchanting, potion, silvering, or Smithing Table
step.

## Ore ecology

### Definitions and host forms

One ore definition owns material identity, harvest tier, underlying loot
policy, processing category, public tags, valid generation domains, deposits,
and surface evidence. Host definitions then produce the visible form of that
ore. An ore form never creates a new material identity.

Every configured ore receives forms for each raw-stone host valid to its
generation domain:

- Overworld raw families: Stone, Granite, Diorite, Andesite, Deepslate, Tuff,
  Calcite, Dripstone, Sulfur, Cinnabar, Sandstone, and Red Sandstone.
- Nether raw families: Netherrack, Basalt, and Blackstone.
- End raw family: End Stone.

Every configured ore also receives a Gravel Ore form. Gravel Ores generate in
the ore's valid exposed or surface gravel deposits and require a Shovel whose
harvest capability equals the matching stone ore's Pickaxe/Hammer capability.
They share that ore's material output, processing category, advancement
progress, and common tags.

The host matrix is domain-driven. A block model or registration never makes an
ore eligible in a dimension, biome, or depth where its definition disallows it.
The built-in deposit catalog gives the Nether and End real ore-bearing
deposits, so Netherrack, Basalt, Blackstone, and End Stone forms are gameplay
content rather than unused decorations. Vanilla Nether Gold Ore is the narrow
behavioral precedent for a dimension-native Netherrack ore: Material
Progression independently authors its code and assets and keeps each ore's own
loot and harvest rules.

Vanilla non-metal ore families are covered by the same host and Gravel Ore
framework where their normal generation domain permits it. Their existing gem,
redstone, quartz, or debris-style loot identity remains intact; only their
host representation and correctly tooled harvest feedback change.

### Discovery

Ore discovery is physical and deliberately light:

> Surface Gravel Ore or visible sample -> local clue -> host-correct deposit
> -> extraction.

Existing Hammers provide the first prospecting action. Using a Hammer on
natural surface evidence or raw host stone returns a concise, throttled,
localized hint about a nearby material or direction. It never provides exact
coordinates, scans unloaded chunks, or replaces exploration.

All denied harvests give concise, throttled server-authoritative feedback. A
failed Gravel Ore harvest names the Shovel requirement; a failed host ore
harvest names the Pickaxe or Hammer requirement. Correct tools do not receive
denial feedback.

## Processing and fuel

The existing Manual Workshop, Furnace, and Crusher/Grinder remain the only
required processing blocks. The renamed base Grinder retains its existing
fuel-burning Furnace-like state machine, sided inventory behavior, and two-dust
yield. Its new early recipe is exactly eight compatible Cobblestone blocks
surrounding one compatible Wooden Plank, making it comparable to a Furnace in
cost and available without metal.

The base processing paths are:

> Ore or Raw Metal + fuel in Grinder -> two Dust  
> Dust + Furnace -> Ingot  
> Alloy Dust + Furnace -> Alloy Ingot

The Grinder accepts all tag-compatible ore/raw-metal inputs. It also accepts
Coal and Charcoal for Coal Dust, and Sulfur Rocks for Sulfur Dust. Sulfur Rocks
remain ordinary Rocks for the geology/cobbling loop; this adds their industrial
use rather than replacing it.

Fuel preparation is optional:

> Sulfur Rock -> Sulfur Dust  
> Coal or Charcoal -> Coal Dust  
> Coal Dust + Sulfur Dust -> Sulfur Coke

Sulfur Coke is a longer-burning Furnace and Grinder fuel. Coal and Charcoal
remain valid fuels and valid Steel carbon inputs; Sulfur Coke is an efficiency
option, not a replacement or a mandatory Steel gate. Better Grinder blocks,
faster processing, and additional fuel families remain later work.

## Library and compatibility contract

Material Progression is a required dependency for the author's future mods.
Those mods consume its materials and interfaces; they do not register duplicate
Copper, Tin, Bronze, Steel, dust, tool, or ore systems.

The release publishes:

- A public immutable Java material catalogue and ore catalogue for querying
  registered definitions at runtime.
- A public Java registration path for external content that is registered by
  the dependent mod before its normal registry freeze.
- Reloadable `material_profile` and `ore_profile` data definitions that bind
  registered external content to tools, armor, forms, compatible hosts,
  deposits, processing, and presentation metadata.
- Existing reloadable `stone_family` definitions as the source of raw host
  identity, expanded only through public data rather than private hard-coded
  lists.
- Shared `c:` material tags: every ore host and Gravel Ore form joins its
  `c:ores/<material>` subtype and `c:ores` parent; ingots, dusts, raw
  materials, tools, armor components, and repair ingredients receive the
  corresponding established shared tags.
- Material Progression behavior tags only where Material Progression owns the
  rule, such as prospecting surfaces, Grinder inputs, and host-resolution
  eligibility.

The catalog validates atomically on reload. Duplicate material ownership,
duplicate host ownership, missing parent tags, unregistered declared content,
ambiguous material forms, invalid harvest requirements, or domains without a
valid host cause a clear reload error and retain the last valid snapshot.

Additive releases preserve existing IDs, tags, data fields, and public Java
API behavior. Removing or changing their meaning is a major-version decision
with a migration note.

## Content and UX requirements

- Every new item has an authored local model, English and Brazilian Portuguese
  translation, useful localized tooltip, recipes where applicable, and clear
  advancement or Recipe Book discovery.
- Every new block has a blockstate, loot table, correct mining requirements,
  tags, world-generation definition, and readable original texture.
- Every material's tool and armor forms use the shared material profile, not
  copied per-item statistics.
- All generic recipes, machine inputs, repairs, and external-facing checks use
  shared tags rather than direct Material Progression IDs.
- The full authored item/block catalogue must receive the already documented
  vanilla-baseline art recalibration before public release. Mechanical PNG
  validity is necessary but not visual acceptance.

## Verification

Fast contracts must prove:

- The literal material, alloy, tool, armor, and form catalog.
- The ore/host/Gravel Ore matrix, generation domains, tags, assets,
  translations, loot, recipes, and no-direct-ID compatibility policy.
- Base Grinder recipe and all processing/fuel/alloy definitions.
- Public Java/data API documentation, schema validation, and an external
  dependent-content fixture.
- Recipe-catalogue freshness and production-JAR exclusion of test code.

NeoForge GameTests must prove:

- Representative and exhaustive host resolution across every valid raw family,
  including Netherrack, Basalt, Blackstone, and End Stone.
- Gravel Ore generation, Shovel capability denial/success, and preserved
  material drops.
- Deposit/surface-evidence/prospecting behavior without unloaded-chunk access
  or coordinate disclosure.
- Grinder construction, fuel behavior, Coal/Sulfur Dust processing, Sulfur
  Coke duration, ordinary-fuel fallback, and all alloy paths.
- Full tool and armor material registration, mining behavior, repair behavior,
  and equipment stat/profile consistency.
- Public API/data extension behavior through a dedicated development-only
  dependent fixture.

The final release gate is `headlessTest`, source/JAR parity, GameTest exclusion
from the production JAR, visual atlas/client acceptance, and a survival
playtest from primitive resources through Bronze, industrial metals, and the
new deposit loop.

## Deferred work

- Enchanting and potion changes.
- Silvering and Smithing Table upgrades.
- Fantasy, magical, and new dimension-specific metals.
- Better Grinders and advanced fuel systems.
- Bonsai, expanded storage/hoppers, broad logistics, and bulk crafting.
- The separate RPG-progression mod, which may consume neutral material traits
  exported by this release.
