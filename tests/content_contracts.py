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
    "ground_stick",
    "loose_rocks",
    "tin_ore",
} | {
    f"cobbled_{family}"
    for family, contract in STONE_FAMILIES.items()
    if contract["cobbled_block"].startswith("material_progression:")
}

WORLD_ONLY_BLOCKS = {"ground_stick", "loose_rocks"}

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
