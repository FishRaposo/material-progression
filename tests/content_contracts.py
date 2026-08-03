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

TOOLTIP_KEY_BY_ITEM = {
    **{
        "rock" if family == "stone" else f"{family}_rock":
        "tooltip.material_progression.rock"
        for family in STONE_FAMILIES
    },
    **{
        f"cobbled_{family}": "tooltip.material_progression.cobble"
        for family, contract in STONE_FAMILIES.items()
        if contract["cobbled_block"].startswith("material_progression:")
    },
    "raw_tin": "tooltip.material_progression.raw_tin",
    "tin_ingot": "tooltip.material_progression.tin_ingot",
    "tin_dust": "tooltip.material_progression.tin_dust",
    "copper_dust": "tooltip.material_progression.copper_dust",
    "bronze_dust": "tooltip.material_progression.bronze_dust",
    "bronze_ingot": "tooltip.material_progression.bronze_ingot",
    "plant_fiber": "tooltip.material_progression.plant_fiber",
    "flint_shard": "tooltip.material_progression.flint_shard",
    "flint_hatchet": "tooltip.material_progression.flint_hatchet",
    "flint_knife": "tooltip.material_progression.knife",
    "flint_hammer": "tooltip.material_progression.hammer",
    "flint_saw": "tooltip.material_progression.saw",
    "tin_ore": "tooltip.material_progression.tin_ore",
    "deepslate_tin_ore": "tooltip.material_progression.tin_ore",
    "crusher": "tooltip.material_progression.crusher",
    "manual_workshop": "tooltip.material_progression.manual_workshop",
    **{
        item: "tooltip.material_progression.tin_tool"
        for item in {"tin_sword", "tin_pickaxe", "tin_axe", "tin_shovel", "tin_hoe"}
    },
    **{
        item: "tooltip.material_progression.bronze_tool"
        for item in {
            "bronze_sword", "bronze_pickaxe", "bronze_axe", "bronze_shovel", "bronze_hoe"
        }
    },
    "bronze_knife": "tooltip.material_progression.knife",
    "bronze_hammer": "tooltip.material_progression.hammer",
    "bronze_saw": "tooltip.material_progression.saw",
}
assert set(TOOLTIP_KEY_BY_ITEM) == SHIPPED_ITEMS

AUTHORED_ITEM_GROUPS = {
    "rocks_and_cobbles": {
        "rock", "granite_rock", "diorite_rock", "andesite_rock",
        "deepslate_rock", "tuff_rock", "calcite_rock", "dripstone_rock",
        "sulfur_rock", "cinnabar_rock", "sandstone_rock",
        "red_sandstone_rock", "netherrack_rock", "basalt_rock",
        "blackstone_rock", "end_stone_rock",
        "cobbled_andesite", "cobbled_basalt", "cobbled_blackstone",
        "cobbled_calcite", "cobbled_cinnabar", "cobbled_diorite",
        "cobbled_dripstone", "cobbled_end_stone", "cobbled_granite",
        "cobbled_netherrack", "cobbled_red_sandstone",
        "cobbled_sandstone", "cobbled_sulfur", "cobbled_tuff",
    },
    "materials_and_workstations": {
        "flint_shard", "plant_fiber", "copper_dust", "raw_tin", "tin_dust",
        "tin_ingot", "bronze_dust", "bronze_ingot", "tin_ore",
        "deepslate_tin_ore", "crusher", "manual_workshop",
    },
    "tools": {
        "flint_hatchet", "flint_hammer", "flint_knife", "flint_saw",
        "tin_axe", "tin_hoe", "tin_pickaxe", "tin_shovel", "tin_sword",
        "bronze_axe", "bronze_hammer", "bronze_hoe", "bronze_knife",
        "bronze_pickaxe", "bronze_saw", "bronze_shovel", "bronze_sword",
    },
}
assert set().union(*AUTHORED_ITEM_GROUPS.values()) == SHIPPED_ITEMS

# Full-cube block surfaces that must remain locally authored instead of falling
# back to vanilla raw-stone, ore, furnace, or crafting-table art.
AUTHORED_FULL_BLOCKS = {
    "cobbled_granite",
    "cobbled_diorite",
    "cobbled_andesite",
    "cobbled_tuff",
    "cobbled_calcite",
    "cobbled_dripstone",
    "cobbled_sulfur",
    "cobbled_cinnabar",
    "cobbled_sandstone",
    "cobbled_red_sandstone",
    "cobbled_netherrack",
    "cobbled_basalt",
    "cobbled_blackstone",
    "cobbled_end_stone",
    "tin_ore",
    "deepslate_tin_ore",
    "crusher",
    "manual_workshop",
}

AUTHORED_FULL_BLOCK_FACE_TEXTURES = {
    "crusher": {
        "down": "crusher_bottom",
        "up": "crusher_top",
        "north": "crusher_front",
        "south": "crusher_side",
        "east": "crusher_side",
        "west": "crusher_side",
    },
    "manual_workshop": {
        "down": "manual_workshop_bottom",
        "up": "manual_workshop_top",
        "north": "manual_workshop_side",
        "south": "manual_workshop_side",
        "east": "manual_workshop_side",
        "west": "manual_workshop_side",
    },
}

WORLD_RESOURCE_ASSET_HASHES = {
    "blockstates/ground_stick.json": "225de9ab49ed1e8927ad153822ed8f6ba6e41270ab93654918e2988d2de8f226",
    "blockstates/loose_rocks.json": "9e71acbab30bb2767e4cb82039f7835877ad923dc1f2c7916218e0b89d9c35e2",
    "models/block/ground_stick.json": "7dea14663e4123cacfd2a56669e70d5f77f44f133e040d4a52028dadfafcd9d8",
    "models/block/loose_rocks/andesite.json": "16a3d7d88447f08fc24d71e05a2906da318d0b7b57c23c63106b61da66e1fd7c",
    "models/block/loose_rocks/basalt.json": "cdfb8687e3d938ddd34ca21596a17b5e3febdc608155e8f41ec702c47d3db648",
    "models/block/loose_rocks/blackstone.json": "6009c1f2680bdffa001173e429d163fb8f004798b259ab47996fc18803827207",
    "models/block/loose_rocks/calcite.json": "705e74033c8fef8cd5a13998bd6427decaeda84b669d1ee401744f136dcb1b42",
    "models/block/loose_rocks/cinnabar.json": "06e9c39088fc0b8a1e3dfb9821132ca9fa5a57bb3ca7e293de7d94c52f6e31d7",
    "models/block/loose_rocks/deepslate.json": "503312d0e2bb9f76bf62ba1349cf1367ba266effbca12d45674e534e579ee6aa",
    "models/block/loose_rocks/diorite.json": "0713d63cf730407edfbfeadfd70d5dbfb340440a14dda8f105f0a84bbf905bc1",
    "models/block/loose_rocks/dripstone.json": "02158d83bef828bb02829a25f4edcd64443d0bc7b8e2255d2a8da20dd4236fb1",
    "models/block/loose_rocks/end_stone.json": "b3a3b83491015eb63aaf9af82b07b49792acdca060aac533bcbfa44ca1f1ef4d",
    "models/block/loose_rocks/granite.json": "ceb7e0d5e8c660f8da37a24b9b3ffad5d9a751617280bbb1d1ee15cbb5dbb663",
    "models/block/loose_rocks/netherrack.json": "4922071c1d4586818437e84a06752d9b7beafdab6cf7fcc1016429ababada01b",
    "models/block/loose_rocks/red_sandstone.json": "f02f0f6f795f6a65823a7fccfa9407f268c7103a1b9863e5093c7568b9e08f44",
    "models/block/loose_rocks/sandstone.json": "7b625c9481f9ed6c782d6382f2629604d6c6dc378fa26d2c7c0471a664658281",
    "models/block/loose_rocks/stone.json": "7e3811ccdf82a7312cae49f8959c57999225811aff5d258a2fa5f0c57937286e",
    "models/block/loose_rocks/sulfur.json": "e4e264102ac33048a81983fe12d8ad16fd734a74a6def73478d26f0eff504054",
    "models/block/loose_rocks/tuff.json": "49e3eb03d5a07a661d323168bfe88f0a3d107d5de31135d6d71d6e7ead9ab613",
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

# Industrial metallurgy is deliberately literal here: the resource tests must
# notice a missing host, form, recipe, translation, or inventory asset instead
# of deriving expectations from production Java registries.
INDUSTRIAL_ORES = {"copper", "tin", "zinc", "lead", "nickel", "silver"}
INDUSTRIAL_HOSTS = {
    "copper": {"stone", "granite", "diorite", "andesite", "deepslate", "tuff", "calcite", "dripstone", "sulfur", "cinnabar", "sandstone", "red_sandstone"},
    "tin": {"stone", "granite", "diorite", "andesite", "deepslate", "tuff", "calcite", "dripstone", "sulfur", "cinnabar", "sandstone", "red_sandstone"},
    "zinc": {"stone", "granite", "diorite", "andesite", "deepslate", "tuff", "calcite", "dripstone", "sulfur", "cinnabar", "sandstone", "red_sandstone"},
    "lead": {"stone", "granite", "diorite", "andesite", "deepslate", "tuff", "calcite", "dripstone", "sulfur", "cinnabar", "sandstone", "red_sandstone"},
    "nickel": {"stone", "granite", "diorite", "andesite", "deepslate", "tuff", "calcite", "dripstone", "sulfur", "cinnabar", "sandstone", "red_sandstone", "netherrack", "basalt", "blackstone"},
    "silver": {"stone", "granite", "diorite", "andesite", "deepslate", "tuff", "calcite", "dripstone", "sulfur", "cinnabar", "sandstone", "red_sandstone", "end_stone"},
}
INDUSTRIAL_EQUIPMENT = {"wood", "stone", "flint", "copper", "tin", "bronze", "zinc", "lead", "steel", "brass", "nickel", "invar", "silver", "rose_gold"}
INDUSTRIAL_STANDARD_TOOLS = {"sword", "pickaxe", "axe", "shovel", "hoe"}
INDUSTRIAL_FIELD_TOOLS = {"knife", "hammer", "saw", "hatchet"}
INDUSTRIAL_ARMOR = {"helmet", "chestplate", "leggings", "boots"}
INDUSTRIAL_ORE_BLOCKS = {
    (f"{material}_ore" if host == "stone" else f"{host}_{material}_ore")
    for material, hosts in INDUSTRIAL_HOSTS.items()
    for host in hosts
} | {f"gravel_{material}_ore" for material in INDUSTRIAL_ORES}
INDUSTRIAL_MATERIAL_ITEMS = {
    *(f"raw_{material}" for material in {"zinc", "lead", "nickel", "silver"}),
    *(f"{material}_dust" for material in {"zinc", "lead", "nickel", "silver", "steel", "brass", "invar", "rose_gold", "sulfur", "coal", "sulfur_coke"}),
    *(f"{material}_ingot" for material in {"zinc", "lead", "nickel", "silver", "steel", "brass", "invar", "rose_gold"}),
    "sulfur_coke",
}
INDUSTRIAL_TOOLS = {
    f"{material}_{role}"
    for material in INDUSTRIAL_EQUIPMENT
    for role in ((INDUSTRIAL_FIELD_TOOLS if material in {"wood", "stone"} else INDUSTRIAL_STANDARD_TOOLS | INDUSTRIAL_FIELD_TOOLS))
} - {"flint_knife", "flint_hammer", "flint_saw", "flint_hatchet", "tin_sword", "tin_pickaxe", "tin_axe", "tin_shovel", "tin_hoe", "bronze_sword", "bronze_pickaxe", "bronze_axe", "bronze_shovel", "bronze_hoe", "bronze_knife", "bronze_hammer", "bronze_saw"}
INDUSTRIAL_ARMOR_ITEMS = {
    f"{material}_{role}" for material in INDUSTRIAL_EQUIPMENT for role in INDUSTRIAL_ARMOR
}

SHIPPED_ITEMS.update(INDUSTRIAL_ORE_BLOCKS | INDUSTRIAL_MATERIAL_ITEMS | INDUSTRIAL_TOOLS | INDUSTRIAL_ARMOR_ITEMS)
SHIPPED_BLOCKS.update(INDUSTRIAL_ORE_BLOCKS)
AUTHORED_ITEM_GROUPS.setdefault("industrial_materials", set()).update(INDUSTRIAL_MATERIAL_ITEMS)
AUTHORED_ITEM_GROUPS.setdefault("ore_blocks", set()).update(INDUSTRIAL_ORE_BLOCKS)
AUTHORED_ITEM_GROUPS["tools"].update(INDUSTRIAL_TOOLS)
AUTHORED_ITEM_GROUPS.setdefault("armor", set()).update(INDUSTRIAL_ARMOR_ITEMS)
AUTHORED_FULL_BLOCKS.update(INDUSTRIAL_ORE_BLOCKS)
TOOL_FAMILIES.update({
    material: {
        f"{material}_{role}"
        for role in (INDUSTRIAL_FIELD_TOOLS if material in {"wood", "stone"} else INDUSTRIAL_STANDARD_TOOLS | INDUSTRIAL_FIELD_TOOLS)
    }
    for material in INDUSTRIAL_EQUIPMENT
    if material not in {"flint", "tin", "bronze"}
})
TOOL_FAMILIES["flint"].update({f"flint_{role}" for role in INDUSTRIAL_STANDARD_TOOLS})
TOOL_FAMILIES["tin"].update({f"tin_{role}" for role in INDUSTRIAL_FIELD_TOOLS})
TOOL_FAMILIES["bronze"].add("bronze_hatchet")

for item in INDUSTRIAL_MATERIAL_ITEMS:
    TOOLTIP_KEY_BY_ITEM[item] = "tooltip.material_progression.sulfur_coke" if item == "sulfur_coke" else "tooltip.material_progression.tool"
for item in INDUSTRIAL_ORE_BLOCKS:
    TOOLTIP_KEY_BY_ITEM[item] = "tooltip.material_progression.ore"
for item in INDUSTRIAL_TOOLS:
    TOOLTIP_KEY_BY_ITEM[item] = "tooltip.material_progression." + item.rsplit("_", 1)[-1]
for item in INDUSTRIAL_ARMOR_ITEMS:
    TOOLTIP_KEY_BY_ITEM[item] = "tooltip.material_progression.armor"

# The full reference-grounded art pass deliberately re-authors all world
# props.  Determinism, model ownership, atlas review, and JAR parity now guard
# them; historical byte hashes are no longer a product requirement.
WORLD_RESOURCE_ASSET_HASHES = {}
