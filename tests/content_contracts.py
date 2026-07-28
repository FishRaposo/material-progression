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
    "raw_tin",
    "tin_axe",
    "tin_dust",
    "tin_hoe",
    "tin_ingot",
    "tin_ore",
    "tin_pickaxe",
    "tin_shovel",
    "tin_sword",
}

SHIPPED_BLOCKS = {"crusher", "deepslate_tin_ore", "tin_ore"}

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

TOOL_FAMILIES = {
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
