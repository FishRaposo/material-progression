# Recipe catalogue

> **Generated file.** Update it with `python tests/generate_recipe_catalogue.py --write` after changing a shipped recipe resource; `tests/test_recipe_catalogue.py` rejects a stale committed copy.

This catalogue covers every recipe under `data/material_progression/recipe` that ships with the mod. Inputs use the literal item or tag accepted by the recipe; a tag accepts any compatible member. Times use Minecraft ticks (20 ticks = 1 second). Smelting and Crusher entries state fuel separately because the recipe JSON does not bind a particular fuel item.

## Behaviour-driven custom recipe

| Recipe ID | Inputs per operation | Output | Type | Requirements |
| --- | --- | --- | --- | --- |
| `rock_cobbling` | Exactly 4 x `#c:rocks`; all four must fit the crafting grid. | 1 x mapped family cobble when all Rocks map to the same registered family; otherwise 1 x `minecraft:cobblestone`. | Behaviour-driven `material_progression:rock_cobbling` custom recipe | Crafting grid; no fuel. The serializer validates the four Rocks and resolves the output from the live stone-family catalog, so neither output is hard-coded in JSON. |

## Primitive crafting and workstations

| Recipe ID | Inputs per operation | Output | Type | Requirements |
| --- | --- | --- | --- | --- |
| `crusher` | 8 × `minecraft:cobblestone`; 1 × `minecraft:flint` (shape `CCC / CFC / CCC`; C = `minecraft:cobblestone`; F = `minecraft:flint`) | 1 x `material_progression:crusher` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `flint_hammer` | 3 × `#c:flint_shards`; 2 × `#c:rods/wooden` (shape `RRR /  S  /  S `; R = `#c:flint_shards`; S = `#c:rods/wooden`) | 1 x `material_progression:flint_hammer` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `flint_hatchet` | 1 × `#c:flint_shards`; 2 × `#c:rods/wooden` (shape `RS /  S`; R = `#c:flint_shards`; S = `#c:rods/wooden`) | 1 x `material_progression:flint_hatchet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `flint_knife` | 1 × `#c:flint_shards`; 1 × `#c:rods/wooden` (shape `R / S`; R = `#c:flint_shards`; S = `#c:rods/wooden`) | 1 x `material_progression:flint_knife` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `flint_saw` | 3 × `#c:flint_shards`; 2 × `#c:rods/wooden` (shape `RRR / S S`; R = `#c:flint_shards`; S = `#c:rods/wooden`) | 1 x `material_progression:flint_saw` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `flint_shard_from_flint` | 1 x `minecraft:flint` | 2 x `material_progression:flint_shard` | Data-driven `minecraft:crafting_shapeless` | Crafting grid; no fuel. |
| `flint_shard_from_rock` | 1 x `#c:rocks` | 1 x `material_progression:flint_shard` | Data-driven `minecraft:crafting_shapeless` | Crafting grid; no fuel. |
| `manual_workshop` | 3 × `#c:cobblestones`; 2 × `#c:rods/wooden`; 3 × `#minecraft:planks` (shape `PPP / S S / CCC`; C = `#c:cobblestones`; P = `#minecraft:planks`; S = `#c:rods/wooden`) | 1 x `material_progression:manual_workshop` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `plant_fiber_to_string` | 3 x `#c:fibers/plant` | 1 x `minecraft:string` | Data-driven `minecraft:crafting_shapeless` | Crafting grid; no fuel. |

## Metal crafting and tools

| Recipe ID | Inputs per operation | Output | Type | Requirements |
| --- | --- | --- | --- | --- |
| `bronze_axe` | 3 × `#c:ingots/bronze`; 2 × `#c:rods/wooden` (shape `XX / X# /  #`; # = `#c:rods/wooden`; X = `#c:ingots/bronze`) | 1 x `material_progression:bronze_axe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `bronze_dust` | 3 x `#c:dusts/copper`; 1 x `#c:dusts/tin` | 4 x `material_progression:bronze_dust` | Data-driven `minecraft:crafting_shapeless` | Crafting grid; no fuel. |
| `bronze_hammer` | 3 × `#c:ingots/bronze`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/bronze`; S = `#c:rods/wooden`) | 1 x `material_progression:bronze_hammer` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `bronze_hoe` | 2 × `#c:ingots/bronze`; 2 × `#c:rods/wooden` (shape `XX /  # /  #`; # = `#c:rods/wooden`; X = `#c:ingots/bronze`) | 1 x `material_progression:bronze_hoe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `bronze_knife` | 1 × `#c:ingots/bronze`; 1 × `#c:rods/wooden` (shape `I / S`; I = `#c:ingots/bronze`; S = `#c:rods/wooden`) | 1 x `material_progression:bronze_knife` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `bronze_pickaxe` | 3 × `#c:ingots/bronze`; 2 × `#c:rods/wooden` (shape `XXX /  #  /  # `; # = `#c:rods/wooden`; X = `#c:ingots/bronze`) | 1 x `material_progression:bronze_pickaxe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `bronze_saw` | 3 × `#c:ingots/bronze`; 2 × `#c:rods/wooden` (shape `III / S S`; I = `#c:ingots/bronze`; S = `#c:rods/wooden`) | 1 x `material_progression:bronze_saw` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `bronze_shovel` | 1 × `#c:ingots/bronze`; 2 × `#c:rods/wooden` (shape `X / # / #`; # = `#c:rods/wooden`; X = `#c:ingots/bronze`) | 1 x `material_progression:bronze_shovel` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `bronze_sword` | 2 × `#c:ingots/bronze`; 1 × `#c:rods/wooden` (shape `X / X / #`; # = `#c:rods/wooden`; X = `#c:ingots/bronze`) | 1 x `material_progression:bronze_sword` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `tin_axe` | 3 × `#c:ingots/tin`; 2 × `#c:rods/wooden` (shape `XX / X# /  #`; # = `#c:rods/wooden`; X = `#c:ingots/tin`) | 1 x `material_progression:tin_axe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `tin_hoe` | 2 × `#c:ingots/tin`; 2 × `#c:rods/wooden` (shape `XX /  # /  #`; # = `#c:rods/wooden`; X = `#c:ingots/tin`) | 1 x `material_progression:tin_hoe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `tin_pickaxe` | 3 × `#c:ingots/tin`; 2 × `#c:rods/wooden` (shape `XXX /  #  /  # `; # = `#c:rods/wooden`; X = `#c:ingots/tin`) | 1 x `material_progression:tin_pickaxe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `tin_shovel` | 1 × `#c:ingots/tin`; 2 × `#c:rods/wooden` (shape `X / # / #`; # = `#c:rods/wooden`; X = `#c:ingots/tin`) | 1 x `material_progression:tin_shovel` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `tin_sword` | 2 × `#c:ingots/tin`; 1 × `#c:rods/wooden` (shape `X / X / #`; # = `#c:rods/wooden`; X = `#c:ingots/tin`) | 1 x `material_progression:tin_sword` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |

## Fuel-burning Crusher processing

| Recipe ID | Inputs per operation | Output | Type | Requirements |
| --- | --- | --- | --- | --- |
| `crushing_copper_ore` | 1 x `#c:ores/copper` | 2 x `material_progression:copper_dust` | Data-driven `material_progression:crushing` | Fuel-burning Crusher; 200 ticks (10 s); any valid furnace fuel. |
| `crushing_raw_copper` | 1 x `#c:raw_materials/copper` | 2 x `material_progression:copper_dust` | Data-driven `material_progression:crushing` | Fuel-burning Crusher; 200 ticks (10 s); any valid furnace fuel. |
| `crushing_raw_tin` | 1 x `#c:raw_materials/tin` | 2 x `material_progression:tin_dust` | Data-driven `material_progression:crushing` | Fuel-burning Crusher; 200 ticks (10 s); any valid furnace fuel. |
| `crushing_tin_ore` | 1 x `#c:ores/tin` | 2 x `material_progression:tin_dust` | Data-driven `material_progression:crushing` | Fuel-burning Crusher; 200 ticks (10 s); any valid furnace fuel. |

## Smelting material products

| Recipe ID | Inputs per operation | Output | Type | Requirements |
| --- | --- | --- | --- | --- |
| `smelting_bronze_dust` | 1 x `#c:dusts/bronze` | 1 x `material_progression:bronze_ingot` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_copper_dust` | 1 x `#c:dusts/copper` | 1 x `minecraft:copper_ingot` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_raw_tin` | 1 x `#c:raw_materials/tin` | 1 x `material_progression:tin_ingot` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_tin_dust` | 1 x `#c:dusts/tin` | 1 x `material_progression:tin_ingot` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |

## Smelting family cobbles back to raw stone

| Recipe ID | Inputs per operation | Output | Type | Requirements |
| --- | --- | --- | --- | --- |
| `smelting_cobbled_andesite` | 1 x `#c:cobblestones/andesite` | 1 x `minecraft:andesite` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s) (recipe default); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_cobbled_basalt` | 1 x `#c:cobblestones/basalt` | 1 x `minecraft:basalt` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s) (recipe default); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_cobbled_blackstone` | 1 x `#c:cobblestones/blackstone` | 1 x `minecraft:blackstone` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s) (recipe default); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_cobbled_calcite` | 1 x `#c:cobblestones/calcite` | 1 x `minecraft:calcite` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s) (recipe default); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_cobbled_cinnabar` | 1 x `#c:cobblestones/cinnabar` | 1 x `minecraft:cinnabar` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s) (recipe default); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_cobbled_deepslate` | 1 x `#c:cobblestones/deepslate` | 1 x `minecraft:deepslate` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s) (recipe default); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_cobbled_diorite` | 1 x `#c:cobblestones/diorite` | 1 x `minecraft:diorite` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s) (recipe default); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_cobbled_dripstone` | 1 x `#c:cobblestones/dripstone` | 1 x `minecraft:dripstone_block` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s) (recipe default); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_cobbled_end_stone` | 1 x `#c:cobblestones/end_stone` | 1 x `minecraft:end_stone` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s) (recipe default); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_cobbled_granite` | 1 x `#c:cobblestones/granite` | 1 x `minecraft:granite` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s) (recipe default); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_cobbled_netherrack` | 1 x `#c:cobblestones/netherrack` | 1 x `minecraft:netherrack` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s) (recipe default); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_cobbled_red_sandstone` | 1 x `#c:cobblestones/red_sandstone` | 1 x `minecraft:red_sandstone` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s) (recipe default); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_cobbled_sandstone` | 1 x `#c:cobblestones/sandstone` | 1 x `minecraft:sandstone` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s) (recipe default); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_cobbled_stone` | 1 x `#c:cobblestones/stone` | 1 x `minecraft:stone` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s) (recipe default); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_cobbled_sulfur` | 1 x `#c:cobblestones/sulfur` | 1 x `minecraft:sulfur` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s) (recipe default); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_cobbled_tuff` | 1 x `#c:cobblestones/tuff` | 1 x `minecraft:tuff` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s) (recipe default); any valid furnace fuel (the recipe does not prescribe one). |

## Manual Workshop: knife operations

| Recipe ID | Inputs per operation | Output | Type | Requirements |
| --- | --- | --- | --- | --- |
| `manual_workshop_plant_fiber_1` | 1 x `#material_progression:workshop_plants/fiber_1` | 1 x `material_progression:plant_fiber` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/knives` loses 1 durability; 40 ticks (2 s); no fuel. |
| `manual_workshop_plant_fiber_2` | 1 x `#material_progression:workshop_plants/fiber_2` | 2 x `material_progression:plant_fiber` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/knives` loses 1 durability; 40 ticks (2 s); no fuel. |
| `manual_workshop_plant_fiber_3` | 1 x `#material_progression:workshop_plants/fiber_3` | 3 x `material_progression:plant_fiber` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/knives` loses 1 durability; 40 ticks (2 s); no fuel. |
| `manual_workshop_plant_fiber_5` | 1 x `#material_progression:workshop_plants/fiber_5` | 5 x `material_progression:plant_fiber` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/knives` loses 1 durability; 40 ticks (2 s); no fuel. |
| `manual_workshop_rock_sharpening` | 1 x `#c:rocks` | 2 x `material_progression:flint_shard` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/knives` loses 1 durability; 40 ticks (2 s); no fuel. |

## Manual Workshop: hammer operations

| Recipe ID | Inputs per operation | Output | Type | Requirements |
| --- | --- | --- | --- | --- |
| `manual_workshop_copper_ore` | 1 x `#c:ores/copper` | 2 x `material_progression:copper_dust` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/hammers` loses 8 durability; 160 ticks (8 s); no fuel. |
| `manual_workshop_gravel_to_sand` | 1 x `minecraft:gravel` | 1 x `minecraft:sand` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/hammers` loses 2 durability; 80 ticks (4 s); no fuel. |
| `manual_workshop_raw_copper` | 1 x `#c:raw_materials/copper` | 2 x `material_progression:copper_dust` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/hammers` loses 8 durability; 160 ticks (8 s); no fuel. |
| `manual_workshop_raw_tin` | 1 x `#c:raw_materials/tin` | 2 x `material_progression:tin_dust` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/hammers` loses 8 durability; 160 ticks (8 s); no fuel. |
| `manual_workshop_stone_to_gravel` | 1 x `#material_progression:workshop_stone_to_gravel` | 1 x `minecraft:gravel` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/hammers` loses 2 durability; 80 ticks (4 s); no fuel. |
| `manual_workshop_tin_ore` | 1 x `#c:ores/tin` | 2 x `material_progression:tin_dust` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/hammers` loses 8 durability; 160 ticks (8 s); no fuel. |

## Manual Workshop: saw operations

| Recipe ID | Inputs per operation | Output | Type | Requirements |
| --- | --- | --- | --- | --- |
| `manual_workshop_acacia_logs` | 1 x `#minecraft:acacia_logs` | 6 x `minecraft:acacia_planks` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/saws` loses 2 durability; 100 ticks (5 s); no fuel. |
| `manual_workshop_bamboo_logs` | 1 x `#minecraft:bamboo_blocks` | 3 x `minecraft:bamboo_planks` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/saws` loses 2 durability; 100 ticks (5 s); no fuel. |
| `manual_workshop_birch_logs` | 1 x `#minecraft:birch_logs` | 6 x `minecraft:birch_planks` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/saws` loses 2 durability; 100 ticks (5 s); no fuel. |
| `manual_workshop_cherry_logs` | 1 x `#minecraft:cherry_logs` | 6 x `minecraft:cherry_planks` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/saws` loses 2 durability; 100 ticks (5 s); no fuel. |
| `manual_workshop_crimson_logs` | 1 x `#minecraft:crimson_stems` | 6 x `minecraft:crimson_planks` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/saws` loses 2 durability; 100 ticks (5 s); no fuel. |
| `manual_workshop_dark_oak_logs` | 1 x `#minecraft:dark_oak_logs` | 6 x `minecraft:dark_oak_planks` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/saws` loses 2 durability; 100 ticks (5 s); no fuel. |
| `manual_workshop_jungle_logs` | 1 x `#minecraft:jungle_logs` | 6 x `minecraft:jungle_planks` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/saws` loses 2 durability; 100 ticks (5 s); no fuel. |
| `manual_workshop_mangrove_logs` | 1 x `#minecraft:mangrove_logs` | 6 x `minecraft:mangrove_planks` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/saws` loses 2 durability; 100 ticks (5 s); no fuel. |
| `manual_workshop_oak_logs` | 1 x `#minecraft:oak_logs` | 6 x `minecraft:oak_planks` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/saws` loses 2 durability; 100 ticks (5 s); no fuel. |
| `manual_workshop_pale_oak_logs` | 1 x `#minecraft:pale_oak_logs` | 6 x `minecraft:pale_oak_planks` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/saws` loses 2 durability; 100 ticks (5 s); no fuel. |
| `manual_workshop_planks_to_sticks` | 1 x `#minecraft:planks` | 3 x `minecraft:stick` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/saws` loses 1 durability; 60 ticks (3 s); no fuel. |
| `manual_workshop_spruce_logs` | 1 x `#minecraft:spruce_logs` | 6 x `minecraft:spruce_planks` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/saws` loses 2 durability; 100 ticks (5 s); no fuel. |
| `manual_workshop_warped_logs` | 1 x `#minecraft:warped_stems` | 6 x `minecraft:warped_planks` | Data-driven `material_progression:manual_workshop` | Manual Workshop; tool `#c:tools/saws` loses 2 durability; 100 ticks (5 s); no fuel. |

## Scope and terminology

All rows after the custom cobbling rule are **data-driven** recipe resources: they are reloadable JSON definitions. `rock_cobbling` is intentionally separate because its resource only selects a serializer; Java behaviour validates its inputs and selects its result dynamically. Manual Workshop rows consume one input, require the listed installed tool, and damage that tool only when an operation completes. The fuel-burning Crusher uses the furnace fuel model; Manual Workshop processing and crafting do not consume fuel.
