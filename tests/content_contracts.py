from support.resources import RecipeContract


STONE_FAMILIES = {
    "stone": {
        "raw_block": "minecraft:stone",
        "cobbled_block": "minecraft:cobblestone",
        "resistance": "standard",
    },
    "granite": {
        "raw_block": "minecraft:granite",
        "cobbled_block": "material_progression:cobbled_granite",
        "resistance": "standard",
    },
    "diorite": {
        "raw_block": "minecraft:diorite",
        "cobbled_block": "material_progression:cobbled_diorite",
        "resistance": "standard",
    },
    "andesite": {
        "raw_block": "minecraft:andesite",
        "cobbled_block": "material_progression:cobbled_andesite",
        "resistance": "standard",
    },
    "deepslate": {
        "raw_block": "minecraft:deepslate",
        "cobbled_block": "minecraft:cobbled_deepslate",
        "resistance": "hard",
    },
    "tuff": {
        "raw_block": "minecraft:tuff",
        "cobbled_block": "material_progression:cobbled_tuff",
        "resistance": "standard",
    },
    "calcite": {
        "raw_block": "minecraft:calcite",
        "cobbled_block": "material_progression:cobbled_calcite",
        "resistance": "soft",
    },
    "dripstone": {
        "raw_block": "minecraft:dripstone_block",
        "cobbled_block": "material_progression:cobbled_dripstone",
        "resistance": "soft",
    },
    "sulfur": {
        "raw_block": "minecraft:sulfur",
        "cobbled_block": "material_progression:cobbled_sulfur",
        "resistance": "soft",
    },
    "cinnabar": {
        "raw_block": "minecraft:cinnabar",
        "cobbled_block": "material_progression:cobbled_cinnabar",
        "resistance": "standard",
    },
    "sandstone": {
        "raw_block": "minecraft:sandstone",
        "cobbled_block": "material_progression:cobbled_sandstone",
        "resistance": "soft",
    },
    "red_sandstone": {
        "raw_block": "minecraft:red_sandstone",
        "cobbled_block": "material_progression:cobbled_red_sandstone",
        "resistance": "soft",
    },
    "netherrack": {
        "raw_block": "minecraft:netherrack",
        "cobbled_block": "material_progression:cobbled_netherrack",
        "resistance": "soft",
    },
    "basalt": {
        "raw_block": "minecraft:basalt",
        "cobbled_block": "material_progression:cobbled_basalt",
        "resistance": "hard",
    },
    "blackstone": {
        "raw_block": "minecraft:blackstone",
        "cobbled_block": "material_progression:cobbled_blackstone",
        "resistance": "hard",
    },
    "end_stone": {
        "raw_block": "minecraft:end_stone",
        "cobbled_block": "material_progression:cobbled_end_stone",
        "resistance": "standard",
    },
}


SHIPPED_ITEMS = {
    "bronze_axe",
    "bronze_dust",
    "bronze_hammer",
    "bronze_hoe",
    "bronze_ingot",
    "bronze_knife",
    "bronze_pickaxe",
    "bronze_saw",
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
    "plant_fiber",
    "raw_tin",
    "rock",
    "manual_workshop",
    "tin_axe",
    "tin_dust",
    "tin_hoe",
    "tin_ingot",
    "tin_ore",
    "tin_pickaxe",
    "tin_shovel",
    "tin_sword",
} | {f"{family}_rock" for family in STONE_FAMILIES if family != "stone"} | {
    f"cobbled_{family}"
    for family, contract in STONE_FAMILIES.items()
    if contract["cobbled_block"].startswith("material_progression:")
}

SHIPPED_BLOCKS = {
    "crusher",
    "deepslate_tin_ore",
    "external_loose_rocks",
    "ground_stick",
    "loose_rocks",
    "manual_workshop",
    "tin_ore",
} | {
    f"cobbled_{family}"
    for family, contract in STONE_FAMILIES.items()
    if contract["cobbled_block"].startswith("material_progression:")
}

WORLD_ONLY_BLOCKS = {
    "external_loose_rocks",
    "ground_stick",
    "loose_rocks",
}

PRIMITIVE_RECIPES = {
    "bronze_hammer": {
        "type": "minecraft:crafting_shaped",
        "category": "equipment",
        "pattern": ["III", " S ", " S "],
        "key": {"I": "#c:ingots/bronze", "S": "#c:rods/wooden"},
        "result": {"id": "material_progression:bronze_hammer"},
    },
    "bronze_knife": {
        "type": "minecraft:crafting_shaped",
        "category": "equipment",
        "pattern": ["I", "S"],
        "key": {"I": "#c:ingots/bronze", "S": "#c:rods/wooden"},
        "result": {"id": "material_progression:bronze_knife"},
    },
    "bronze_saw": {
        "type": "minecraft:crafting_shaped",
        "category": "equipment",
        "pattern": ["III", "S S"],
        "key": {"I": "#c:ingots/bronze", "S": "#c:rods/wooden"},
        "result": {"id": "material_progression:bronze_saw"},
    },
    "rock_cobbling": {
        "type": "material_progression:rock_cobbling",
    },
    "flint_hatchet": {
        "type": "minecraft:crafting_shaped",
        "pattern": ["RS", " S"],
        "key": {"R": "#c:flint_shards", "S": "#c:rods/wooden"},
        "result": {"id": "material_progression:flint_hatchet"},
    },
    "flint_hammer": {
        "type": "minecraft:crafting_shaped",
        "category": "equipment",
        "pattern": ["RRR", " S ", " S "],
        "key": {"R": "#c:flint_shards", "S": "#c:rods/wooden"},
        "result": {"id": "material_progression:flint_hammer"},
    },
    "flint_knife": {
        "type": "minecraft:crafting_shaped",
        "category": "equipment",
        "pattern": ["R", "S"],
        "key": {"R": "#c:flint_shards", "S": "#c:rods/wooden"},
        "result": {"id": "material_progression:flint_knife"},
    },
    "flint_saw": {
        "type": "minecraft:crafting_shaped",
        "category": "equipment",
        "pattern": ["RRR", "S S"],
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
    "plant_fiber_to_string": {
        "type": "minecraft:crafting_shapeless",
        "ingredients": ["#c:fibers/plant"] * 3,
        "result": {"id": "minecraft:string"},
    },
    "manual_workshop": {
        "type": "minecraft:crafting_shaped",
        "category": "building",
        "pattern": ["PPP", "S S", "CCC"],
        "key": {
            "C": "#c:cobblestones",
            "P": "#minecraft:planks",
            "S": "#c:rods/wooden",
        },
        "result": {"id": "material_progression:manual_workshop"},
    },
}

SURFACE_WORLDGEN_FEATURES = {
    "ground_stick": "material_progression:ground_stick",
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
} | {
    f"smelting_cobbled_{family}": RecipeContract(
        "minecraft:smelting",
        f"#c:cobblestones/{family}",
        contract["raw_block"],
    )
    for family, contract in STONE_FAMILIES.items()
}

WORKSHOP_PLANT_TAGS = {
    "fiber_1": ["#minecraft:leaves", "#minecraft:small_flowers"],
    "fiber_2": [
        "#minecraft:saplings",
        "minecraft:sunflower",
        "minecraft:lilac",
        "minecraft:peony",
        "minecraft:rose_bush",
        "minecraft:pitcher_plant",
        "minecraft:wheat",
    ],
    "fiber_3": ["minecraft:cactus", "minecraft:sugar_cane"],
    "fiber_5": [
        "minecraft:vine",
        "minecraft:weeping_vines",
        "minecraft:twisting_vines",
    ],
}

WORKSHOP_WOOD_RECIPES = {
    "oak": ("#minecraft:oak_logs", "minecraft:oak_planks", 6),
    "spruce": ("#minecraft:spruce_logs", "minecraft:spruce_planks", 6),
    "birch": ("#minecraft:birch_logs", "minecraft:birch_planks", 6),
    "jungle": ("#minecraft:jungle_logs", "minecraft:jungle_planks", 6),
    "acacia": ("#minecraft:acacia_logs", "minecraft:acacia_planks", 6),
    "dark_oak": (
        "#minecraft:dark_oak_logs",
        "minecraft:dark_oak_planks",
        6,
    ),
    "pale_oak": (
        "#minecraft:pale_oak_logs",
        "minecraft:pale_oak_planks",
        6,
    ),
    "mangrove": (
        "#minecraft:mangrove_logs",
        "minecraft:mangrove_planks",
        6,
    ),
    "cherry": ("#minecraft:cherry_logs", "minecraft:cherry_planks", 6),
    "crimson": (
        "#minecraft:crimson_stems",
        "minecraft:crimson_planks",
        6,
    ),
    "warped": (
        "#minecraft:warped_stems",
        "minecraft:warped_planks",
        6,
    ),
    "bamboo": (
        "#minecraft:bamboo_blocks",
        "minecraft:bamboo_planks",
        3,
    ),
}

WORKSHOP_RECIPES = {
    "manual_workshop_rock_sharpening": {
        "ingredient": "#c:rocks",
        "tool": "#c:tools/knives",
        "result": {
            "count": 2,
            "id": "material_progression:flint_shard",
        },
        "processing_time": 40,
        "tool_damage": 1,
    },
    "manual_workshop_plant_fiber_1": {
        "ingredient": "#material_progression:workshop_plants/fiber_1",
        "tool": "#c:tools/knives",
        "result": {"id": "material_progression:plant_fiber"},
        "processing_time": 40,
        "tool_damage": 1,
    },
    "manual_workshop_plant_fiber_2": {
        "ingredient": "#material_progression:workshop_plants/fiber_2",
        "tool": "#c:tools/knives",
        "result": {
            "count": 2,
            "id": "material_progression:plant_fiber",
        },
        "processing_time": 40,
        "tool_damage": 1,
    },
    "manual_workshop_plant_fiber_3": {
        "ingredient": "#material_progression:workshop_plants/fiber_3",
        "tool": "#c:tools/knives",
        "result": {
            "count": 3,
            "id": "material_progression:plant_fiber",
        },
        "processing_time": 40,
        "tool_damage": 1,
    },
    "manual_workshop_plant_fiber_5": {
        "ingredient": "#material_progression:workshop_plants/fiber_5",
        "tool": "#c:tools/knives",
        "result": {
            "count": 5,
            "id": "material_progression:plant_fiber",
        },
        "processing_time": 40,
        "tool_damage": 1,
    },
    "manual_workshop_stone_to_gravel": {
        "ingredient": "#material_progression:workshop_stone_to_gravel",
        "tool": "#c:tools/hammers",
        "result": {"id": "minecraft:gravel"},
        "processing_time": 80,
        "tool_damage": 2,
    },
    "manual_workshop_gravel_to_sand": {
        "ingredient": "minecraft:gravel",
        "tool": "#c:tools/hammers",
        "result": {"id": "minecraft:sand"},
        "processing_time": 80,
        "tool_damage": 2,
    },
    "manual_workshop_copper_ore": {
        "ingredient": "#c:ores/copper",
        "tool": "#c:tools/hammers",
        "result": {
            "count": 2,
            "id": "material_progression:copper_dust",
        },
        "processing_time": 160,
        "tool_damage": 8,
    },
    "manual_workshop_raw_copper": {
        "ingredient": "#c:raw_materials/copper",
        "tool": "#c:tools/hammers",
        "result": {
            "count": 2,
            "id": "material_progression:copper_dust",
        },
        "processing_time": 160,
        "tool_damage": 8,
    },
    "manual_workshop_tin_ore": {
        "ingredient": "#c:ores/tin",
        "tool": "#c:tools/hammers",
        "result": {
            "count": 2,
            "id": "material_progression:tin_dust",
        },
        "processing_time": 160,
        "tool_damage": 8,
    },
    "manual_workshop_raw_tin": {
        "ingredient": "#c:raw_materials/tin",
        "tool": "#c:tools/hammers",
        "result": {
            "count": 2,
            "id": "material_progression:tin_dust",
        },
        "processing_time": 160,
        "tool_damage": 8,
    },
    "manual_workshop_planks_to_sticks": {
        "ingredient": "#minecraft:planks",
        "tool": "#c:tools/saws",
        "result": {"count": 3, "id": "minecraft:stick"},
        "processing_time": 60,
        "tool_damage": 1,
    },
} | {
    f"manual_workshop_{family}_logs": {
        "ingredient": ingredient,
        "tool": "#c:tools/saws",
        "result": {"count": count, "id": result},
        "processing_time": 100,
        "tool_damage": 2,
    }
    for family, (ingredient, result, count) in WORKSHOP_WOOD_RECIPES.items()
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
        "bronze_hammer",
        "bronze_knife",
        "bronze_saw",
    },
}
