from support.resources import RecipeContract


SHIPPED_ITEMS = {
    "bronze_axe",
    "bronze_dust",
    "bronze_hoe",
    "bronze_ingot",
    "bronze_pickaxe",
    "bronze_shovel",
    "bronze_sword",
    "copper_dust",
    "crusher",
    "deepslate_tin_ore",
    "flint_hatchet",
    "flint_hammer",
    "flint_knife",
    "flint_saw",
    "flint_shard",
    "ground_stick",
    "loose_rocks",
    "plant_fiber",
    "raw_tin",
    "rock",
    "tin_axe",
    "tin_dust",
    "tin_hoe",
    "tin_ingot",
    "tin_ore",
    "tin_pickaxe",
    "tin_shovel",
    "tin_sword",
    "workshop",
}

SHIPPED_BLOCKS = {
    "crusher",
    "deepslate_tin_ore",
    "ground_stick",
    "loose_rocks",
    "tin_ore",
    "workshop",
}

WORLD_PLACED_BLOCKS = {
    "ground_stick",
    "loose_rocks",
}

CREATIVE_TAB_ITEMS = (
    "loose_rocks",
    "ground_stick",
    "rock",
    "flint_shard",
    "flint_hatchet",
    "flint_hammer",
    "flint_knife",
    "flint_saw",
    "plant_fiber",
    "tin_ore",
    "deepslate_tin_ore",
    "raw_tin",
    "tin_dust",
    "tin_ingot",
    "tin_sword",
    "tin_pickaxe",
    "tin_axe",
    "tin_shovel",
    "tin_hoe",
    "copper_dust",
    "bronze_dust",
    "bronze_ingot",
    "bronze_sword",
    "bronze_pickaxe",
    "bronze_axe",
    "bronze_shovel",
    "bronze_hoe",
    "crusher",
    "workshop",
)

MATERIAL_FAMILIES = {
    "flint": {
        "durability": 64,
        "speed": 5.0,
        "attack_bonus": 1.5,
        "enchantment_value": 5,
        "repair_tag": "c:flint_shards",
        "incorrect_blocks_tag": "material_progression:incorrect_for_flint_tool",
    },
    "tin": {
        "durability": 96,
        "speed": 3.5,
        "attack_bonus": 0.5,
        "enchantment_value": 8,
        "repair_tag": "c:ingots/tin",
        "incorrect_blocks_tag": "material_progression:incorrect_for_tin_tool",
    },
    "bronze": {
        "durability": 325,
        "speed": 6.5,
        "attack_bonus": 2.0,
        "enchantment_value": 12,
        "repair_tag": "c:ingots/bronze",
        "incorrect_blocks_tag": "material_progression:incorrect_for_bronze_tool",
    },
}

PRIMITIVE_RECIPES = {
    "cobblestone_from_rocks": {
        "type": "minecraft:crafting_shapeless",
        "ingredients": ["#c:rocks"] * 4,
        "result": {"id": "minecraft:cobblestone"},
    },
    "flint_hatchet": {
        "type": "minecraft:crafting_shaped",
        "pattern": ["RS", " S"],
        "key": {"R": "#c:flint_shards", "S": "#c:rods/wooden"},
        "result": {"id": "material_progression:flint_hatchet"},
    },
    "flint_hammer": {
        "type": "minecraft:crafting_shaped",
        "pattern": ["RRR", " S ", " S "],
        "key": {"R": "#c:rocks", "S": "#c:rods/wooden"},
        "result": {"id": "material_progression:flint_hammer"},
    },
    "flint_knife": {
        "type": "minecraft:crafting_shaped",
        "pattern": ["R", "S"],
        "key": {"R": "#c:rocks", "S": "#c:rods/wooden"},
        "result": {"id": "material_progression:flint_knife"},
    },
    "flint_saw": {
        "type": "minecraft:crafting_shaped",
        "pattern": ["RR", " S"],
        "key": {"R": "#c:flint_shards", "S": "#c:rods/wooden"},
        "result": {"id": "material_progression:flint_saw"},
    },
    "flint_shard_from_flint": {
        "type": "minecraft:crafting_shapeless",
        "ingredients": ["minecraft:flint"],
        "result": {"count": 2, "id": "material_progression:flint_shard"},
    },
    "flint_shard_from_rock": {
        "type": "minecraft:crafting_shapeless",
        "ingredients": ["#c:rocks"],
        "result": {"id": "material_progression:flint_shard"},
    },
    "string_from_plant_fiber": {
        "type": "minecraft:crafting_shapeless",
        "ingredients": ["#c:fibers/plant"] * 3,
        "result": {"id": "minecraft:string"},
    },
    "workshop": {
        "type": "minecraft:crafting_shaped",
        "pattern": ["PPP", "RCR", "PPP"],
        "key": {
            "C": "minecraft:crafting_table",
            "P": "#minecraft:planks",
            "R": "#c:rocks",
        },
        "result": {"id": "material_progression:workshop"},
    },
}

SURFACE_WORLDGEN_FEATURES = {
    "loose_rocks": "material_progression:loose_rocks",
}

CRUSHING_RECIPES = {
    "crushing_copper_ore": RecipeContract(
        "material_progression:crushing",
        "#c:ores/copper",
        "material_progression:copper_dust",
        count=2,
        cooking_time=200,
    ),
    "crushing_raw_copper": RecipeContract(
        "material_progression:crushing",
        "#c:raw_materials/copper",
        "material_progression:copper_dust",
        count=2,
        cooking_time=200,
    ),
    "crushing_raw_tin": RecipeContract(
        "material_progression:crushing",
        "#c:raw_materials/tin",
        "material_progression:tin_dust",
        count=2,
        cooking_time=200,
    ),
    "crushing_tin_ore": RecipeContract(
        "material_progression:crushing",
        "#c:ores/tin",
        "material_progression:tin_dust",
        count=2,
        cooking_time=200,
    ),
}

SMELTING_RECIPES = {
    "smelting_bronze_dust": RecipeContract(
        "minecraft:smelting",
        "#c:dusts/bronze",
        "material_progression:bronze_ingot",
    ),
    "smelting_copper_dust": RecipeContract(
        "minecraft:smelting",
        "#c:dusts/copper",
        "minecraft:copper_ingot",
    ),
    "smelting_raw_tin": RecipeContract(
        "minecraft:smelting",
        "#c:raw_materials/tin",
        "material_progression:tin_ingot",
    ),
    "smelting_tin_dust": RecipeContract(
        "minecraft:smelting",
        "#c:dusts/tin",
        "material_progression:tin_ingot",
    ),
}

MANUAL_PROCESSING_RECIPES = {
    "knife_rock": {
        "tool": "#c:tools/knives",
        "input": "#c:rocks",
        "result": {"count": 2, "id": "material_progression:flint_shard"},
        "durability_cost": 1,
        "operation_time": 20,
    },
    "knife_leaves": {
        "tool": "#c:tools/knives",
        "input": "#minecraft:leaves",
        "result": {"count": 2, "id": "material_progression:plant_fiber"},
        "durability_cost": 1,
        "operation_time": 20,
    },
    "hammer_stone": {
        "tool": "#c:tools/hammers",
        "input": "minecraft:stone",
        "result": {"id": "minecraft:gravel"},
        "durability_cost": 2,
        "operation_time": 40,
    },
    "hammer_gravel": {
        "tool": "#c:tools/hammers",
        "input": "minecraft:gravel",
        "result": {"id": "minecraft:sand"},
        "durability_cost": 2,
        "operation_time": 40,
    },
    "hammer_copper_ore": {
        "tool": "#c:tools/hammers",
        "input": "#c:ores/copper",
        "result": {"count": 2, "id": "material_progression:copper_dust"},
        "durability_cost": 12,
        "operation_time": 100,
    },
    "hammer_raw_copper": {
        "tool": "#c:tools/hammers",
        "input": "#c:raw_materials/copper",
        "result": {"count": 2, "id": "material_progression:copper_dust"},
        "durability_cost": 12,
        "operation_time": 100,
    },
    "hammer_tin_ore": {
        "tool": "#c:tools/hammers",
        "input": "#c:ores/tin",
        "result": {"count": 2, "id": "material_progression:tin_dust"},
        "durability_cost": 12,
        "operation_time": 100,
    },
    "hammer_raw_tin": {
        "tool": "#c:tools/hammers",
        "input": "#c:raw_materials/tin",
        "result": {"count": 2, "id": "material_progression:tin_dust"},
        "durability_cost": 12,
        "operation_time": 100,
    },
}

for wood, log in {
    "oak": "oak_log",
    "spruce": "spruce_log",
    "birch": "birch_log",
    "jungle": "jungle_log",
    "acacia": "acacia_log",
    "dark_oak": "dark_oak_log",
    "mangrove": "mangrove_log",
    "cherry": "cherry_log",
    "pale_oak": "pale_oak_log",
    "bamboo": "bamboo_block",
    "crimson": "crimson_stem",
    "warped": "warped_stem",
}.items():
    MANUAL_PROCESSING_RECIPES[f"saw_{wood}_log"] = {
        "tool": "#c:tools/saws",
        "input": f"minecraft:{log}",
        "result": {"count": 6, "id": f"minecraft:{wood}_planks"},
        "durability_cost": 2,
        "operation_time": 40,
    }
    MANUAL_PROCESSING_RECIPES[f"saw_{wood}_planks"] = {
        "tool": "#c:tools/saws",
        "input": f"minecraft:{wood}_planks",
        "result": {"count": 3, "id": "minecraft:stick"},
        "durability_cost": 1,
        "operation_time": 20,
    }

TOOL_FAMILIES = {
    "flint": {
        "flint_hatchet",
        "flint_hammer",
        "flint_knife",
        "flint_saw",
    },
    "tin": {
        "tin_sword",
        "tin_pickaxe",
        "tin_axe",
        "tin_shovel",
        "tin_hoe",
    },
    "bronze": {
        "bronze_sword",
        "bronze_pickaxe",
        "bronze_axe",
        "bronze_shovel",
        "bronze_hoe",
    },
}
