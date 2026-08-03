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
| `crusher` | 8 × `#c:cobblestones`; 1 × `#minecraft:planks` (shape `CCC / CPC / CCC`; C = `#c:cobblestones`; P = `#minecraft:planks`) | 1 x `material_progression:crusher` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `flint_hammer` | 3 × `#c:flint_shards`; 2 × `#c:rods/wooden` (shape `RRR /  S  /  S `; R = `#c:flint_shards`; S = `#c:rods/wooden`) | 1 x `material_progression:flint_hammer` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `flint_hatchet` | 1 × `#c:flint_shards`; 2 × `#c:rods/wooden` (shape `RS /  S`; R = `#c:flint_shards`; S = `#c:rods/wooden`) | 1 x `material_progression:flint_hatchet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `flint_knife` | 1 × `#c:flint_shards`; 1 × `#c:rods/wooden` (shape `R / S`; R = `#c:flint_shards`; S = `#c:rods/wooden`) | 1 x `material_progression:flint_knife` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `flint_saw` | 3 × `#c:flint_shards`; 2 × `#c:rods/wooden` (shape `RRR / S S`; R = `#c:flint_shards`; S = `#c:rods/wooden`) | 1 x `material_progression:flint_saw` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `flint_shard_from_flint` | 1 x `minecraft:flint` | 2 x `material_progression:flint_shard` | Data-driven `minecraft:crafting_shapeless` | Crafting grid; no fuel. |
| `flint_shard_from_rock` | 1 x `#c:rocks` | 1 x `material_progression:flint_shard` | Data-driven `minecraft:crafting_shapeless` | Crafting grid; no fuel. |
| `manual_workshop` | 3 × `#c:cobblestones`; 2 × `#c:rods/wooden`; 3 × `#minecraft:planks` (shape `PPP / S S / CCC`; C = `#c:cobblestones`; P = `#minecraft:planks`; S = `#c:rods/wooden`) | 1 x `material_progression:manual_workshop` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `plant_fiber_to_string` | 3 x `#c:fibers/plant` | 1 x `minecraft:string` | Data-driven `minecraft:crafting_shapeless` | Crafting grid; no fuel. |

## Industrial crafting, gear, armor, and alloys

| Recipe ID | Inputs per operation | Output | Type | Requirements |
| --- | --- | --- | --- | --- |
| `brass_axe` | 3 × `#c:ingots/brass`; 2 × `#c:rods/wooden` (shape `II / IS /  S`; I = `#c:ingots/brass`; S = `#c:rods/wooden`) | 1 x `material_progression:brass_axe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `brass_boots` | 4 × `#c:ingots/brass` (shape `I I / I I`; I = `#c:ingots/brass`) | 1 x `material_progression:brass_boots` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `brass_chestplate` | 8 × `#c:ingots/brass` (shape `I I / III / III`; I = `#c:ingots/brass`) | 1 x `material_progression:brass_chestplate` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `brass_dust` | 1 x `#c:dusts/copper`; 1 x `#c:dusts/zinc` | 2 x `material_progression:brass_dust` | Data-driven `minecraft:crafting_shapeless` | Crafting grid; no fuel. |
| `brass_hammer` | 3 × `#c:ingots/brass`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/brass`; S = `#c:rods/wooden`) | 1 x `material_progression:brass_hammer` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `brass_hatchet` | 1 × `#c:ingots/brass`; 2 × `#c:rods/wooden` (shape `IS /  S`; I = `#c:ingots/brass`; S = `#c:rods/wooden`) | 1 x `material_progression:brass_hatchet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `brass_helmet` | 5 × `#c:ingots/brass` (shape `III / I I`; I = `#c:ingots/brass`) | 1 x `material_progression:brass_helmet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `brass_hoe` | 2 × `#c:ingots/brass`; 2 × `#c:rods/wooden` (shape `II /  S /  S`; I = `#c:ingots/brass`; S = `#c:rods/wooden`) | 1 x `material_progression:brass_hoe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `brass_knife` | 1 × `#c:ingots/brass`; 1 × `#c:rods/wooden` (shape `I / S`; I = `#c:ingots/brass`; S = `#c:rods/wooden`) | 1 x `material_progression:brass_knife` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `brass_leggings` | 7 × `#c:ingots/brass` (shape `III / I I / I I`; I = `#c:ingots/brass`) | 1 x `material_progression:brass_leggings` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `brass_pickaxe` | 3 × `#c:ingots/brass`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/brass`; S = `#c:rods/wooden`) | 1 x `material_progression:brass_pickaxe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `brass_saw` | 3 × `#c:ingots/brass`; 2 × `#c:rods/wooden` (shape `III / S S`; I = `#c:ingots/brass`; S = `#c:rods/wooden`) | 1 x `material_progression:brass_saw` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `brass_shovel` | 1 × `#c:ingots/brass`; 2 × `#c:rods/wooden` (shape `I / S / S`; I = `#c:ingots/brass`; S = `#c:rods/wooden`) | 1 x `material_progression:brass_shovel` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `brass_sword` | 2 × `#c:ingots/brass`; 1 × `#c:rods/wooden` (shape `I / I / S`; I = `#c:ingots/brass`; S = `#c:rods/wooden`) | 1 x `material_progression:brass_sword` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `bronze_axe` | 3 × `#c:ingots/bronze`; 2 × `#c:rods/wooden` (shape `II / IS /  S`; I = `#c:ingots/bronze`; S = `#c:rods/wooden`) | 1 x `material_progression:bronze_axe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `bronze_boots` | 4 × `#c:ingots/bronze` (shape `I I / I I`; I = `#c:ingots/bronze`) | 1 x `material_progression:bronze_boots` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `bronze_chestplate` | 8 × `#c:ingots/bronze` (shape `I I / III / III`; I = `#c:ingots/bronze`) | 1 x `material_progression:bronze_chestplate` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `bronze_dust` | 3 x `#c:dusts/copper`; 1 x `#c:dusts/tin` | 4 x `material_progression:bronze_dust` | Data-driven `minecraft:crafting_shapeless` | Crafting grid; no fuel. |
| `bronze_hammer` | 3 × `#c:ingots/bronze`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/bronze`; S = `#c:rods/wooden`) | 1 x `material_progression:bronze_hammer` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `bronze_hatchet` | 1 × `#c:ingots/bronze`; 2 × `#c:rods/wooden` (shape `IS /  S`; I = `#c:ingots/bronze`; S = `#c:rods/wooden`) | 1 x `material_progression:bronze_hatchet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `bronze_helmet` | 5 × `#c:ingots/bronze` (shape `III / I I`; I = `#c:ingots/bronze`) | 1 x `material_progression:bronze_helmet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `bronze_hoe` | 2 × `#c:ingots/bronze`; 2 × `#c:rods/wooden` (shape `II /  S /  S`; I = `#c:ingots/bronze`; S = `#c:rods/wooden`) | 1 x `material_progression:bronze_hoe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `bronze_knife` | 1 × `#c:ingots/bronze`; 1 × `#c:rods/wooden` (shape `I / S`; I = `#c:ingots/bronze`; S = `#c:rods/wooden`) | 1 x `material_progression:bronze_knife` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `bronze_leggings` | 7 × `#c:ingots/bronze` (shape `III / I I / I I`; I = `#c:ingots/bronze`) | 1 x `material_progression:bronze_leggings` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `bronze_pickaxe` | 3 × `#c:ingots/bronze`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/bronze`; S = `#c:rods/wooden`) | 1 x `material_progression:bronze_pickaxe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `bronze_saw` | 3 × `#c:ingots/bronze`; 2 × `#c:rods/wooden` (shape `III / S S`; I = `#c:ingots/bronze`; S = `#c:rods/wooden`) | 1 x `material_progression:bronze_saw` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `bronze_shovel` | 1 × `#c:ingots/bronze`; 2 × `#c:rods/wooden` (shape `I / S / S`; I = `#c:ingots/bronze`; S = `#c:rods/wooden`) | 1 x `material_progression:bronze_shovel` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `bronze_sword` | 2 × `#c:ingots/bronze`; 1 × `#c:rods/wooden` (shape `I / I / S`; I = `#c:ingots/bronze`; S = `#c:rods/wooden`) | 1 x `material_progression:bronze_sword` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `copper_axe` | 3 × `#c:ingots/copper`; 2 × `#c:rods/wooden` (shape `II / IS /  S`; I = `#c:ingots/copper`; S = `#c:rods/wooden`) | 1 x `material_progression:copper_axe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `copper_boots` | 4 × `#c:ingots/copper` (shape `I I / I I`; I = `#c:ingots/copper`) | 1 x `material_progression:copper_boots` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `copper_chestplate` | 8 × `#c:ingots/copper` (shape `I I / III / III`; I = `#c:ingots/copper`) | 1 x `material_progression:copper_chestplate` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `copper_hammer` | 3 × `#c:ingots/copper`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/copper`; S = `#c:rods/wooden`) | 1 x `material_progression:copper_hammer` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `copper_hatchet` | 1 × `#c:ingots/copper`; 2 × `#c:rods/wooden` (shape `IS /  S`; I = `#c:ingots/copper`; S = `#c:rods/wooden`) | 1 x `material_progression:copper_hatchet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `copper_helmet` | 5 × `#c:ingots/copper` (shape `III / I I`; I = `#c:ingots/copper`) | 1 x `material_progression:copper_helmet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `copper_hoe` | 2 × `#c:ingots/copper`; 2 × `#c:rods/wooden` (shape `II /  S /  S`; I = `#c:ingots/copper`; S = `#c:rods/wooden`) | 1 x `material_progression:copper_hoe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `copper_knife` | 1 × `#c:ingots/copper`; 1 × `#c:rods/wooden` (shape `I / S`; I = `#c:ingots/copper`; S = `#c:rods/wooden`) | 1 x `material_progression:copper_knife` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `copper_leggings` | 7 × `#c:ingots/copper` (shape `III / I I / I I`; I = `#c:ingots/copper`) | 1 x `material_progression:copper_leggings` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `copper_pickaxe` | 3 × `#c:ingots/copper`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/copper`; S = `#c:rods/wooden`) | 1 x `material_progression:copper_pickaxe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `copper_saw` | 3 × `#c:ingots/copper`; 2 × `#c:rods/wooden` (shape `III / S S`; I = `#c:ingots/copper`; S = `#c:rods/wooden`) | 1 x `material_progression:copper_saw` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `copper_shovel` | 1 × `#c:ingots/copper`; 2 × `#c:rods/wooden` (shape `I / S / S`; I = `#c:ingots/copper`; S = `#c:rods/wooden`) | 1 x `material_progression:copper_shovel` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `copper_sword` | 2 × `#c:ingots/copper`; 1 × `#c:rods/wooden` (shape `I / I / S`; I = `#c:ingots/copper`; S = `#c:rods/wooden`) | 1 x `material_progression:copper_sword` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `flint_axe` | 3 × `#c:flint_shards`; 2 × `#c:rods/wooden` (shape `II / IS /  S`; I = `#c:flint_shards`; S = `#c:rods/wooden`) | 1 x `material_progression:flint_axe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `flint_boots` | 4 × `#c:flint_shards` (shape `I I / I I`; I = `#c:flint_shards`) | 1 x `material_progression:flint_boots` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `flint_chestplate` | 8 × `#c:flint_shards` (shape `I I / III / III`; I = `#c:flint_shards`) | 1 x `material_progression:flint_chestplate` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `flint_helmet` | 5 × `#c:flint_shards` (shape `III / I I`; I = `#c:flint_shards`) | 1 x `material_progression:flint_helmet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `flint_hoe` | 2 × `#c:flint_shards`; 2 × `#c:rods/wooden` (shape `II /  S /  S`; I = `#c:flint_shards`; S = `#c:rods/wooden`) | 1 x `material_progression:flint_hoe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `flint_leggings` | 7 × `#c:flint_shards` (shape `III / I I / I I`; I = `#c:flint_shards`) | 1 x `material_progression:flint_leggings` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `flint_pickaxe` | 3 × `#c:flint_shards`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:flint_shards`; S = `#c:rods/wooden`) | 1 x `material_progression:flint_pickaxe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `flint_shovel` | 1 × `#c:flint_shards`; 2 × `#c:rods/wooden` (shape `I / S / S`; I = `#c:flint_shards`; S = `#c:rods/wooden`) | 1 x `material_progression:flint_shovel` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `flint_sword` | 2 × `#c:flint_shards`; 1 × `#c:rods/wooden` (shape `I / I / S`; I = `#c:flint_shards`; S = `#c:rods/wooden`) | 1 x `material_progression:flint_sword` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `invar_axe` | 3 × `#c:ingots/invar`; 2 × `#c:rods/wooden` (shape `II / IS /  S`; I = `#c:ingots/invar`; S = `#c:rods/wooden`) | 1 x `material_progression:invar_axe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `invar_boots` | 4 × `#c:ingots/invar` (shape `I I / I I`; I = `#c:ingots/invar`) | 1 x `material_progression:invar_boots` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `invar_chestplate` | 8 × `#c:ingots/invar` (shape `I I / III / III`; I = `#c:ingots/invar`) | 1 x `material_progression:invar_chestplate` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `invar_dust` | 1 x `#c:dusts/iron`; 1 x `#c:dusts/nickel` | 2 x `material_progression:invar_dust` | Data-driven `minecraft:crafting_shapeless` | Crafting grid; no fuel. |
| `invar_hammer` | 3 × `#c:ingots/invar`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/invar`; S = `#c:rods/wooden`) | 1 x `material_progression:invar_hammer` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `invar_hatchet` | 1 × `#c:ingots/invar`; 2 × `#c:rods/wooden` (shape `IS /  S`; I = `#c:ingots/invar`; S = `#c:rods/wooden`) | 1 x `material_progression:invar_hatchet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `invar_helmet` | 5 × `#c:ingots/invar` (shape `III / I I`; I = `#c:ingots/invar`) | 1 x `material_progression:invar_helmet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `invar_hoe` | 2 × `#c:ingots/invar`; 2 × `#c:rods/wooden` (shape `II /  S /  S`; I = `#c:ingots/invar`; S = `#c:rods/wooden`) | 1 x `material_progression:invar_hoe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `invar_knife` | 1 × `#c:ingots/invar`; 1 × `#c:rods/wooden` (shape `I / S`; I = `#c:ingots/invar`; S = `#c:rods/wooden`) | 1 x `material_progression:invar_knife` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `invar_leggings` | 7 × `#c:ingots/invar` (shape `III / I I / I I`; I = `#c:ingots/invar`) | 1 x `material_progression:invar_leggings` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `invar_pickaxe` | 3 × `#c:ingots/invar`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/invar`; S = `#c:rods/wooden`) | 1 x `material_progression:invar_pickaxe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `invar_saw` | 3 × `#c:ingots/invar`; 2 × `#c:rods/wooden` (shape `III / S S`; I = `#c:ingots/invar`; S = `#c:rods/wooden`) | 1 x `material_progression:invar_saw` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `invar_shovel` | 1 × `#c:ingots/invar`; 2 × `#c:rods/wooden` (shape `I / S / S`; I = `#c:ingots/invar`; S = `#c:rods/wooden`) | 1 x `material_progression:invar_shovel` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `invar_sword` | 2 × `#c:ingots/invar`; 1 × `#c:rods/wooden` (shape `I / I / S`; I = `#c:ingots/invar`; S = `#c:rods/wooden`) | 1 x `material_progression:invar_sword` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `lead_axe` | 3 × `#c:ingots/lead`; 2 × `#c:rods/wooden` (shape `II / IS /  S`; I = `#c:ingots/lead`; S = `#c:rods/wooden`) | 1 x `material_progression:lead_axe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `lead_boots` | 4 × `#c:ingots/lead` (shape `I I / I I`; I = `#c:ingots/lead`) | 1 x `material_progression:lead_boots` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `lead_chestplate` | 8 × `#c:ingots/lead` (shape `I I / III / III`; I = `#c:ingots/lead`) | 1 x `material_progression:lead_chestplate` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `lead_hammer` | 3 × `#c:ingots/lead`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/lead`; S = `#c:rods/wooden`) | 1 x `material_progression:lead_hammer` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `lead_hatchet` | 1 × `#c:ingots/lead`; 2 × `#c:rods/wooden` (shape `IS /  S`; I = `#c:ingots/lead`; S = `#c:rods/wooden`) | 1 x `material_progression:lead_hatchet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `lead_helmet` | 5 × `#c:ingots/lead` (shape `III / I I`; I = `#c:ingots/lead`) | 1 x `material_progression:lead_helmet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `lead_hoe` | 2 × `#c:ingots/lead`; 2 × `#c:rods/wooden` (shape `II /  S /  S`; I = `#c:ingots/lead`; S = `#c:rods/wooden`) | 1 x `material_progression:lead_hoe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `lead_knife` | 1 × `#c:ingots/lead`; 1 × `#c:rods/wooden` (shape `I / S`; I = `#c:ingots/lead`; S = `#c:rods/wooden`) | 1 x `material_progression:lead_knife` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `lead_leggings` | 7 × `#c:ingots/lead` (shape `III / I I / I I`; I = `#c:ingots/lead`) | 1 x `material_progression:lead_leggings` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `lead_pickaxe` | 3 × `#c:ingots/lead`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/lead`; S = `#c:rods/wooden`) | 1 x `material_progression:lead_pickaxe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `lead_saw` | 3 × `#c:ingots/lead`; 2 × `#c:rods/wooden` (shape `III / S S`; I = `#c:ingots/lead`; S = `#c:rods/wooden`) | 1 x `material_progression:lead_saw` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `lead_shovel` | 1 × `#c:ingots/lead`; 2 × `#c:rods/wooden` (shape `I / S / S`; I = `#c:ingots/lead`; S = `#c:rods/wooden`) | 1 x `material_progression:lead_shovel` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `lead_sword` | 2 × `#c:ingots/lead`; 1 × `#c:rods/wooden` (shape `I / I / S`; I = `#c:ingots/lead`; S = `#c:rods/wooden`) | 1 x `material_progression:lead_sword` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `nickel_axe` | 3 × `#c:ingots/nickel`; 2 × `#c:rods/wooden` (shape `II / IS /  S`; I = `#c:ingots/nickel`; S = `#c:rods/wooden`) | 1 x `material_progression:nickel_axe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `nickel_boots` | 4 × `#c:ingots/nickel` (shape `I I / I I`; I = `#c:ingots/nickel`) | 1 x `material_progression:nickel_boots` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `nickel_chestplate` | 8 × `#c:ingots/nickel` (shape `I I / III / III`; I = `#c:ingots/nickel`) | 1 x `material_progression:nickel_chestplate` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `nickel_hammer` | 3 × `#c:ingots/nickel`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/nickel`; S = `#c:rods/wooden`) | 1 x `material_progression:nickel_hammer` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `nickel_hatchet` | 1 × `#c:ingots/nickel`; 2 × `#c:rods/wooden` (shape `IS /  S`; I = `#c:ingots/nickel`; S = `#c:rods/wooden`) | 1 x `material_progression:nickel_hatchet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `nickel_helmet` | 5 × `#c:ingots/nickel` (shape `III / I I`; I = `#c:ingots/nickel`) | 1 x `material_progression:nickel_helmet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `nickel_hoe` | 2 × `#c:ingots/nickel`; 2 × `#c:rods/wooden` (shape `II /  S /  S`; I = `#c:ingots/nickel`; S = `#c:rods/wooden`) | 1 x `material_progression:nickel_hoe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `nickel_knife` | 1 × `#c:ingots/nickel`; 1 × `#c:rods/wooden` (shape `I / S`; I = `#c:ingots/nickel`; S = `#c:rods/wooden`) | 1 x `material_progression:nickel_knife` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `nickel_leggings` | 7 × `#c:ingots/nickel` (shape `III / I I / I I`; I = `#c:ingots/nickel`) | 1 x `material_progression:nickel_leggings` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `nickel_pickaxe` | 3 × `#c:ingots/nickel`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/nickel`; S = `#c:rods/wooden`) | 1 x `material_progression:nickel_pickaxe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `nickel_saw` | 3 × `#c:ingots/nickel`; 2 × `#c:rods/wooden` (shape `III / S S`; I = `#c:ingots/nickel`; S = `#c:rods/wooden`) | 1 x `material_progression:nickel_saw` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `nickel_shovel` | 1 × `#c:ingots/nickel`; 2 × `#c:rods/wooden` (shape `I / S / S`; I = `#c:ingots/nickel`; S = `#c:rods/wooden`) | 1 x `material_progression:nickel_shovel` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `nickel_sword` | 2 × `#c:ingots/nickel`; 1 × `#c:rods/wooden` (shape `I / I / S`; I = `#c:ingots/nickel`; S = `#c:rods/wooden`) | 1 x `material_progression:nickel_sword` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `rose_gold_axe` | 3 × `#c:ingots/rose_gold`; 2 × `#c:rods/wooden` (shape `II / IS /  S`; I = `#c:ingots/rose_gold`; S = `#c:rods/wooden`) | 1 x `material_progression:rose_gold_axe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `rose_gold_boots` | 4 × `#c:ingots/rose_gold` (shape `I I / I I`; I = `#c:ingots/rose_gold`) | 1 x `material_progression:rose_gold_boots` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `rose_gold_chestplate` | 8 × `#c:ingots/rose_gold` (shape `I I / III / III`; I = `#c:ingots/rose_gold`) | 1 x `material_progression:rose_gold_chestplate` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `rose_gold_dust` | 1 x `#c:dusts/copper`; 1 x `#c:dusts/gold` | 2 x `material_progression:rose_gold_dust` | Data-driven `minecraft:crafting_shapeless` | Crafting grid; no fuel. |
| `rose_gold_hammer` | 3 × `#c:ingots/rose_gold`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/rose_gold`; S = `#c:rods/wooden`) | 1 x `material_progression:rose_gold_hammer` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `rose_gold_hatchet` | 1 × `#c:ingots/rose_gold`; 2 × `#c:rods/wooden` (shape `IS /  S`; I = `#c:ingots/rose_gold`; S = `#c:rods/wooden`) | 1 x `material_progression:rose_gold_hatchet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `rose_gold_helmet` | 5 × `#c:ingots/rose_gold` (shape `III / I I`; I = `#c:ingots/rose_gold`) | 1 x `material_progression:rose_gold_helmet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `rose_gold_hoe` | 2 × `#c:ingots/rose_gold`; 2 × `#c:rods/wooden` (shape `II /  S /  S`; I = `#c:ingots/rose_gold`; S = `#c:rods/wooden`) | 1 x `material_progression:rose_gold_hoe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `rose_gold_knife` | 1 × `#c:ingots/rose_gold`; 1 × `#c:rods/wooden` (shape `I / S`; I = `#c:ingots/rose_gold`; S = `#c:rods/wooden`) | 1 x `material_progression:rose_gold_knife` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `rose_gold_leggings` | 7 × `#c:ingots/rose_gold` (shape `III / I I / I I`; I = `#c:ingots/rose_gold`) | 1 x `material_progression:rose_gold_leggings` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `rose_gold_pickaxe` | 3 × `#c:ingots/rose_gold`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/rose_gold`; S = `#c:rods/wooden`) | 1 x `material_progression:rose_gold_pickaxe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `rose_gold_saw` | 3 × `#c:ingots/rose_gold`; 2 × `#c:rods/wooden` (shape `III / S S`; I = `#c:ingots/rose_gold`; S = `#c:rods/wooden`) | 1 x `material_progression:rose_gold_saw` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `rose_gold_shovel` | 1 × `#c:ingots/rose_gold`; 2 × `#c:rods/wooden` (shape `I / S / S`; I = `#c:ingots/rose_gold`; S = `#c:rods/wooden`) | 1 x `material_progression:rose_gold_shovel` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `rose_gold_sword` | 2 × `#c:ingots/rose_gold`; 1 × `#c:rods/wooden` (shape `I / I / S`; I = `#c:ingots/rose_gold`; S = `#c:rods/wooden`) | 1 x `material_progression:rose_gold_sword` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `silver_axe` | 3 × `#c:ingots/silver`; 2 × `#c:rods/wooden` (shape `II / IS /  S`; I = `#c:ingots/silver`; S = `#c:rods/wooden`) | 1 x `material_progression:silver_axe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `silver_boots` | 4 × `#c:ingots/silver` (shape `I I / I I`; I = `#c:ingots/silver`) | 1 x `material_progression:silver_boots` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `silver_chestplate` | 8 × `#c:ingots/silver` (shape `I I / III / III`; I = `#c:ingots/silver`) | 1 x `material_progression:silver_chestplate` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `silver_hammer` | 3 × `#c:ingots/silver`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/silver`; S = `#c:rods/wooden`) | 1 x `material_progression:silver_hammer` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `silver_hatchet` | 1 × `#c:ingots/silver`; 2 × `#c:rods/wooden` (shape `IS /  S`; I = `#c:ingots/silver`; S = `#c:rods/wooden`) | 1 x `material_progression:silver_hatchet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `silver_helmet` | 5 × `#c:ingots/silver` (shape `III / I I`; I = `#c:ingots/silver`) | 1 x `material_progression:silver_helmet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `silver_hoe` | 2 × `#c:ingots/silver`; 2 × `#c:rods/wooden` (shape `II /  S /  S`; I = `#c:ingots/silver`; S = `#c:rods/wooden`) | 1 x `material_progression:silver_hoe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `silver_knife` | 1 × `#c:ingots/silver`; 1 × `#c:rods/wooden` (shape `I / S`; I = `#c:ingots/silver`; S = `#c:rods/wooden`) | 1 x `material_progression:silver_knife` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `silver_leggings` | 7 × `#c:ingots/silver` (shape `III / I I / I I`; I = `#c:ingots/silver`) | 1 x `material_progression:silver_leggings` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `silver_pickaxe` | 3 × `#c:ingots/silver`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/silver`; S = `#c:rods/wooden`) | 1 x `material_progression:silver_pickaxe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `silver_saw` | 3 × `#c:ingots/silver`; 2 × `#c:rods/wooden` (shape `III / S S`; I = `#c:ingots/silver`; S = `#c:rods/wooden`) | 1 x `material_progression:silver_saw` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `silver_shovel` | 1 × `#c:ingots/silver`; 2 × `#c:rods/wooden` (shape `I / S / S`; I = `#c:ingots/silver`; S = `#c:rods/wooden`) | 1 x `material_progression:silver_shovel` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `silver_sword` | 2 × `#c:ingots/silver`; 1 × `#c:rods/wooden` (shape `I / I / S`; I = `#c:ingots/silver`; S = `#c:rods/wooden`) | 1 x `material_progression:silver_sword` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `steel_axe` | 3 × `#c:ingots/steel`; 2 × `#c:rods/wooden` (shape `II / IS /  S`; I = `#c:ingots/steel`; S = `#c:rods/wooden`) | 1 x `material_progression:steel_axe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `steel_boots` | 4 × `#c:ingots/steel` (shape `I I / I I`; I = `#c:ingots/steel`) | 1 x `material_progression:steel_boots` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `steel_chestplate` | 8 × `#c:ingots/steel` (shape `I I / III / III`; I = `#c:ingots/steel`) | 1 x `material_progression:steel_chestplate` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `steel_dust` | 1 x `#c:dusts/iron`; 1 x `#material_progression:carbon_dusts` | 1 x `material_progression:steel_dust` | Data-driven `minecraft:crafting_shapeless` | Crafting grid; no fuel. |
| `steel_hammer` | 3 × `#c:ingots/steel`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/steel`; S = `#c:rods/wooden`) | 1 x `material_progression:steel_hammer` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `steel_hatchet` | 1 × `#c:ingots/steel`; 2 × `#c:rods/wooden` (shape `IS /  S`; I = `#c:ingots/steel`; S = `#c:rods/wooden`) | 1 x `material_progression:steel_hatchet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `steel_helmet` | 5 × `#c:ingots/steel` (shape `III / I I`; I = `#c:ingots/steel`) | 1 x `material_progression:steel_helmet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `steel_hoe` | 2 × `#c:ingots/steel`; 2 × `#c:rods/wooden` (shape `II /  S /  S`; I = `#c:ingots/steel`; S = `#c:rods/wooden`) | 1 x `material_progression:steel_hoe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `steel_knife` | 1 × `#c:ingots/steel`; 1 × `#c:rods/wooden` (shape `I / S`; I = `#c:ingots/steel`; S = `#c:rods/wooden`) | 1 x `material_progression:steel_knife` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `steel_leggings` | 7 × `#c:ingots/steel` (shape `III / I I / I I`; I = `#c:ingots/steel`) | 1 x `material_progression:steel_leggings` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `steel_pickaxe` | 3 × `#c:ingots/steel`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/steel`; S = `#c:rods/wooden`) | 1 x `material_progression:steel_pickaxe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `steel_saw` | 3 × `#c:ingots/steel`; 2 × `#c:rods/wooden` (shape `III / S S`; I = `#c:ingots/steel`; S = `#c:rods/wooden`) | 1 x `material_progression:steel_saw` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `steel_shovel` | 1 × `#c:ingots/steel`; 2 × `#c:rods/wooden` (shape `I / S / S`; I = `#c:ingots/steel`; S = `#c:rods/wooden`) | 1 x `material_progression:steel_shovel` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `steel_sword` | 2 × `#c:ingots/steel`; 1 × `#c:rods/wooden` (shape `I / I / S`; I = `#c:ingots/steel`; S = `#c:rods/wooden`) | 1 x `material_progression:steel_sword` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `stone_boots` | 4 × `#c:cobblestones` (shape `I I / I I`; I = `#c:cobblestones`) | 1 x `material_progression:stone_boots` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `stone_chestplate` | 8 × `#c:cobblestones` (shape `I I / III / III`; I = `#c:cobblestones`) | 1 x `material_progression:stone_chestplate` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `stone_hammer` | 3 × `#c:cobblestones`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:cobblestones`; S = `#c:rods/wooden`) | 1 x `material_progression:stone_hammer` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `stone_hatchet` | 1 × `#c:cobblestones`; 2 × `#c:rods/wooden` (shape `IS /  S`; I = `#c:cobblestones`; S = `#c:rods/wooden`) | 1 x `material_progression:stone_hatchet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `stone_helmet` | 5 × `#c:cobblestones` (shape `III / I I`; I = `#c:cobblestones`) | 1 x `material_progression:stone_helmet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `stone_knife` | 1 × `#c:cobblestones`; 1 × `#c:rods/wooden` (shape `I / S`; I = `#c:cobblestones`; S = `#c:rods/wooden`) | 1 x `material_progression:stone_knife` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `stone_leggings` | 7 × `#c:cobblestones` (shape `III / I I / I I`; I = `#c:cobblestones`) | 1 x `material_progression:stone_leggings` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `stone_saw` | 3 × `#c:cobblestones`; 2 × `#c:rods/wooden` (shape `III / S S`; I = `#c:cobblestones`; S = `#c:rods/wooden`) | 1 x `material_progression:stone_saw` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `sulfur_coke_dust` | 1 x `#c:dusts/coal`; 1 x `#c:dusts/sulfur` | 1 x `material_progression:sulfur_coke_dust` | Data-driven `minecraft:crafting_shapeless` | Crafting grid; no fuel. |
| `tin_axe` | 3 × `#c:ingots/tin`; 2 × `#c:rods/wooden` (shape `II / IS /  S`; I = `#c:ingots/tin`; S = `#c:rods/wooden`) | 1 x `material_progression:tin_axe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `tin_boots` | 4 × `#c:ingots/tin` (shape `I I / I I`; I = `#c:ingots/tin`) | 1 x `material_progression:tin_boots` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `tin_chestplate` | 8 × `#c:ingots/tin` (shape `I I / III / III`; I = `#c:ingots/tin`) | 1 x `material_progression:tin_chestplate` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `tin_hammer` | 3 × `#c:ingots/tin`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/tin`; S = `#c:rods/wooden`) | 1 x `material_progression:tin_hammer` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `tin_hatchet` | 1 × `#c:ingots/tin`; 2 × `#c:rods/wooden` (shape `IS /  S`; I = `#c:ingots/tin`; S = `#c:rods/wooden`) | 1 x `material_progression:tin_hatchet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `tin_helmet` | 5 × `#c:ingots/tin` (shape `III / I I`; I = `#c:ingots/tin`) | 1 x `material_progression:tin_helmet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `tin_hoe` | 2 × `#c:ingots/tin`; 2 × `#c:rods/wooden` (shape `II /  S /  S`; I = `#c:ingots/tin`; S = `#c:rods/wooden`) | 1 x `material_progression:tin_hoe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `tin_knife` | 1 × `#c:ingots/tin`; 1 × `#c:rods/wooden` (shape `I / S`; I = `#c:ingots/tin`; S = `#c:rods/wooden`) | 1 x `material_progression:tin_knife` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `tin_leggings` | 7 × `#c:ingots/tin` (shape `III / I I / I I`; I = `#c:ingots/tin`) | 1 x `material_progression:tin_leggings` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `tin_pickaxe` | 3 × `#c:ingots/tin`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/tin`; S = `#c:rods/wooden`) | 1 x `material_progression:tin_pickaxe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `tin_saw` | 3 × `#c:ingots/tin`; 2 × `#c:rods/wooden` (shape `III / S S`; I = `#c:ingots/tin`; S = `#c:rods/wooden`) | 1 x `material_progression:tin_saw` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `tin_shovel` | 1 × `#c:ingots/tin`; 2 × `#c:rods/wooden` (shape `I / S / S`; I = `#c:ingots/tin`; S = `#c:rods/wooden`) | 1 x `material_progression:tin_shovel` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `tin_sword` | 2 × `#c:ingots/tin`; 1 × `#c:rods/wooden` (shape `I / I / S`; I = `#c:ingots/tin`; S = `#c:rods/wooden`) | 1 x `material_progression:tin_sword` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `wood_boots` | 4 × `#minecraft:planks` (shape `I I / I I`; I = `#minecraft:planks`) | 1 x `material_progression:wood_boots` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `wood_chestplate` | 8 × `#minecraft:planks` (shape `I I / III / III`; I = `#minecraft:planks`) | 1 x `material_progression:wood_chestplate` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `wood_hammer` | 2 × `#c:rods/wooden`; 3 × `#minecraft:planks` (shape `III /  S  /  S `; I = `#minecraft:planks`; S = `#c:rods/wooden`) | 1 x `material_progression:wood_hammer` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `wood_hatchet` | 2 × `#c:rods/wooden`; 1 × `#minecraft:planks` (shape `IS /  S`; I = `#minecraft:planks`; S = `#c:rods/wooden`) | 1 x `material_progression:wood_hatchet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `wood_helmet` | 5 × `#minecraft:planks` (shape `III / I I`; I = `#minecraft:planks`) | 1 x `material_progression:wood_helmet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `wood_knife` | 1 × `#c:rods/wooden`; 1 × `#minecraft:planks` (shape `I / S`; I = `#minecraft:planks`; S = `#c:rods/wooden`) | 1 x `material_progression:wood_knife` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `wood_leggings` | 7 × `#minecraft:planks` (shape `III / I I / I I`; I = `#minecraft:planks`) | 1 x `material_progression:wood_leggings` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `wood_saw` | 2 × `#c:rods/wooden`; 3 × `#minecraft:planks` (shape `III / S S`; I = `#minecraft:planks`; S = `#c:rods/wooden`) | 1 x `material_progression:wood_saw` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `zinc_axe` | 3 × `#c:ingots/zinc`; 2 × `#c:rods/wooden` (shape `II / IS /  S`; I = `#c:ingots/zinc`; S = `#c:rods/wooden`) | 1 x `material_progression:zinc_axe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `zinc_boots` | 4 × `#c:ingots/zinc` (shape `I I / I I`; I = `#c:ingots/zinc`) | 1 x `material_progression:zinc_boots` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `zinc_chestplate` | 8 × `#c:ingots/zinc` (shape `I I / III / III`; I = `#c:ingots/zinc`) | 1 x `material_progression:zinc_chestplate` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `zinc_hammer` | 3 × `#c:ingots/zinc`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/zinc`; S = `#c:rods/wooden`) | 1 x `material_progression:zinc_hammer` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `zinc_hatchet` | 1 × `#c:ingots/zinc`; 2 × `#c:rods/wooden` (shape `IS /  S`; I = `#c:ingots/zinc`; S = `#c:rods/wooden`) | 1 x `material_progression:zinc_hatchet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `zinc_helmet` | 5 × `#c:ingots/zinc` (shape `III / I I`; I = `#c:ingots/zinc`) | 1 x `material_progression:zinc_helmet` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `zinc_hoe` | 2 × `#c:ingots/zinc`; 2 × `#c:rods/wooden` (shape `II /  S /  S`; I = `#c:ingots/zinc`; S = `#c:rods/wooden`) | 1 x `material_progression:zinc_hoe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `zinc_knife` | 1 × `#c:ingots/zinc`; 1 × `#c:rods/wooden` (shape `I / S`; I = `#c:ingots/zinc`; S = `#c:rods/wooden`) | 1 x `material_progression:zinc_knife` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `zinc_leggings` | 7 × `#c:ingots/zinc` (shape `III / I I / I I`; I = `#c:ingots/zinc`) | 1 x `material_progression:zinc_leggings` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `zinc_pickaxe` | 3 × `#c:ingots/zinc`; 2 × `#c:rods/wooden` (shape `III /  S  /  S `; I = `#c:ingots/zinc`; S = `#c:rods/wooden`) | 1 x `material_progression:zinc_pickaxe` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `zinc_saw` | 3 × `#c:ingots/zinc`; 2 × `#c:rods/wooden` (shape `III / S S`; I = `#c:ingots/zinc`; S = `#c:rods/wooden`) | 1 x `material_progression:zinc_saw` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `zinc_shovel` | 1 × `#c:ingots/zinc`; 2 × `#c:rods/wooden` (shape `I / S / S`; I = `#c:ingots/zinc`; S = `#c:rods/wooden`) | 1 x `material_progression:zinc_shovel` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |
| `zinc_sword` | 2 × `#c:ingots/zinc`; 1 × `#c:rods/wooden` (shape `I / I / S`; I = `#c:ingots/zinc`; S = `#c:rods/wooden`) | 1 x `material_progression:zinc_sword` | Data-driven `minecraft:crafting_shaped` | Crafting grid; no fuel. |

## Fuel-burning Crusher processing

| Recipe ID | Inputs per operation | Output | Type | Requirements |
| --- | --- | --- | --- | --- |
| `crushing_coal` | 1 x `#material_progression:carbon_sources` | 2 x `material_progression:coal_dust` | Data-driven `material_progression:crushing` | Fuel-burning Grinder; 200 ticks (10 s); any valid furnace fuel. |
| `crushing_copper_ore` | 1 x `#c:ores/copper` | 2 x `material_progression:copper_dust` | Data-driven `material_progression:crushing` | Fuel-burning Grinder; 200 ticks (10 s); any valid furnace fuel. |
| `crushing_lead_ore` | 1 x `#c:ores/lead` | 2 x `material_progression:lead_dust` | Data-driven `material_progression:crushing` | Fuel-burning Grinder; 200 ticks (10 s); any valid furnace fuel. |
| `crushing_nickel_ore` | 1 x `#c:ores/nickel` | 2 x `material_progression:nickel_dust` | Data-driven `material_progression:crushing` | Fuel-burning Grinder; 200 ticks (10 s); any valid furnace fuel. |
| `crushing_raw_copper` | 1 x `#c:raw_materials/copper` | 2 x `material_progression:copper_dust` | Data-driven `material_progression:crushing` | Fuel-burning Grinder; 200 ticks (10 s); any valid furnace fuel. |
| `crushing_raw_lead` | 1 x `#c:raw_materials/lead` | 2 x `material_progression:lead_dust` | Data-driven `material_progression:crushing` | Fuel-burning Grinder; 200 ticks (10 s); any valid furnace fuel. |
| `crushing_raw_nickel` | 1 x `#c:raw_materials/nickel` | 2 x `material_progression:nickel_dust` | Data-driven `material_progression:crushing` | Fuel-burning Grinder; 200 ticks (10 s); any valid furnace fuel. |
| `crushing_raw_silver` | 1 x `#c:raw_materials/silver` | 2 x `material_progression:silver_dust` | Data-driven `material_progression:crushing` | Fuel-burning Grinder; 200 ticks (10 s); any valid furnace fuel. |
| `crushing_raw_tin` | 1 x `#c:raw_materials/tin` | 2 x `material_progression:tin_dust` | Data-driven `material_progression:crushing` | Fuel-burning Grinder; 200 ticks (10 s); any valid furnace fuel. |
| `crushing_raw_zinc` | 1 x `#c:raw_materials/zinc` | 2 x `material_progression:zinc_dust` | Data-driven `material_progression:crushing` | Fuel-burning Grinder; 200 ticks (10 s); any valid furnace fuel. |
| `crushing_silver_ore` | 1 x `#c:ores/silver` | 2 x `material_progression:silver_dust` | Data-driven `material_progression:crushing` | Fuel-burning Grinder; 200 ticks (10 s); any valid furnace fuel. |
| `crushing_sulfur_rock` | 1 x `#c:rocks/sulfur` | 2 x `material_progression:sulfur_dust` | Data-driven `material_progression:crushing` | Fuel-burning Grinder; 200 ticks (10 s); any valid furnace fuel. |
| `crushing_tin_ore` | 1 x `#c:ores/tin` | 2 x `material_progression:tin_dust` | Data-driven `material_progression:crushing` | Fuel-burning Grinder; 200 ticks (10 s); any valid furnace fuel. |
| `crushing_zinc_ore` | 1 x `#c:ores/zinc` | 2 x `material_progression:zinc_dust` | Data-driven `material_progression:crushing` | Fuel-burning Grinder; 200 ticks (10 s); any valid furnace fuel. |

## Smelting material products

| Recipe ID | Inputs per operation | Output | Type | Requirements |
| --- | --- | --- | --- | --- |
| `smelting_brass_dust` | 1 x `#c:dusts/brass` | 1 x `material_progression:brass_ingot` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_bronze_dust` | 1 x `#c:dusts/bronze` | 1 x `material_progression:bronze_ingot` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_copper_dust` | 1 x `#c:dusts/copper` | 1 x `minecraft:copper_ingot` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_invar_dust` | 1 x `#c:dusts/invar` | 1 x `material_progression:invar_ingot` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_lead_dust` | 1 x `#c:dusts/lead` | 1 x `material_progression:lead_ingot` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_nickel_dust` | 1 x `#c:dusts/nickel` | 1 x `material_progression:nickel_ingot` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_raw_lead` | 1 x `#c:raw_materials/lead` | 1 x `material_progression:lead_ingot` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_raw_nickel` | 1 x `#c:raw_materials/nickel` | 1 x `material_progression:nickel_ingot` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_raw_silver` | 1 x `#c:raw_materials/silver` | 1 x `material_progression:silver_ingot` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_raw_tin` | 1 x `#c:raw_materials/tin` | 1 x `material_progression:tin_ingot` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_raw_zinc` | 1 x `#c:raw_materials/zinc` | 1 x `material_progression:zinc_ingot` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_rose_gold_dust` | 1 x `#c:dusts/rose_gold` | 1 x `material_progression:rose_gold_ingot` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_silver_dust` | 1 x `#c:dusts/silver` | 1 x `material_progression:silver_ingot` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_steel_dust` | 1 x `#c:dusts/steel` | 1 x `material_progression:steel_ingot` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_sulfur_coke_dust` | 1 x `#c:dusts/sulfur_coke` | 1 x `material_progression:sulfur_coke` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_tin_dust` | 1 x `#c:dusts/tin` | 1 x `material_progression:tin_ingot` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |
| `smelting_zinc_dust` | 1 x `#c:dusts/zinc` | 1 x `material_progression:zinc_ingot` | Data-driven `minecraft:smelting` | Furnace; 200 ticks (10 s); any valid furnace fuel (the recipe does not prescribe one). |

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

All rows after the custom cobbling rule are **data-driven** recipe resources: they are reloadable JSON definitions. `rock_cobbling` is intentionally separate because its resource only selects a serializer; Java behaviour validates its inputs and selects its result dynamically. Manual Workshop rows consume one input, require the listed installed tool, and damage that tool only when an operation completes. The fuel-burning Grinder (registry ID remains `crusher`) uses the furnace fuel model; Manual Workshop processing and crafting do not consume fuel.
