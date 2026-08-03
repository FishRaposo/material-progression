"""Generate the data-driven Industrial Metallurgy content matrix.

This is intentionally local project source, not a data generator run at game
startup. The literal profiles below are the single authoring point for each
registered host ore, Gravel Ore, processing form, recipe, and compatibility
tag. It keeps the broad material matrix reviewable and deterministic.
"""

from __future__ import annotations

import json
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
DATA = ROOT / "src" / "main" / "resources" / "data"
ASSETS = ROOT / "src" / "main" / "resources" / "assets" / "material_progression"

OVERWORLD_HOSTS = (
    "stone", "granite", "diorite", "andesite", "deepslate", "tuff",
    "calcite", "dripstone", "sulfur", "cinnabar", "sandstone", "red_sandstone",
)
NETHER_HOSTS = ("netherrack", "basalt", "blackstone")
END_HOSTS = ("end_stone",)
RAW_HOST_BLOCKS = {
    "stone": "minecraft:stone", "granite": "minecraft:granite",
    "diorite": "minecraft:diorite", "andesite": "minecraft:andesite",
    "deepslate": "minecraft:deepslate", "tuff": "minecraft:tuff",
    "calcite": "minecraft:calcite", "dripstone": "minecraft:dripstone_block",
    "sulfur": "minecraft:sulfur", "cinnabar": "minecraft:cinnabar",
    "sandstone": "minecraft:sandstone", "red_sandstone": "minecraft:red_sandstone",
    "netherrack": "minecraft:netherrack", "basalt": "minecraft:basalt",
    "blackstone": "minecraft:blackstone", "end_stone": "minecraft:end_stone",
}
ORES = {
    "copper": {"level": 1, "hosts": OVERWORLD_HOSTS, "raw": "minecraft:raw_copper", "dust": "material_progression:copper_dust", "domains": ("overworld",)},
    "tin": {"level": 1, "hosts": OVERWORLD_HOSTS, "raw": "material_progression:raw_tin", "dust": "material_progression:tin_dust", "domains": ("overworld",)},
    "zinc": {"level": 1, "hosts": OVERWORLD_HOSTS, "raw": "material_progression:raw_zinc", "dust": "material_progression:zinc_dust", "domains": ("overworld",)},
    "lead": {"level": 1, "hosts": OVERWORLD_HOSTS, "raw": "material_progression:raw_lead", "dust": "material_progression:lead_dust", "domains": ("overworld",)},
    "nickel": {"level": 2, "hosts": OVERWORLD_HOSTS + NETHER_HOSTS, "raw": "material_progression:raw_nickel", "dust": "material_progression:nickel_dust", "domains": ("overworld", "nether")},
    "silver": {"level": 2, "hosts": OVERWORLD_HOSTS + END_HOSTS, "raw": "material_progression:raw_silver", "dust": "material_progression:silver_dust", "domains": ("overworld", "end")},
}
CUSTOM_RAW = ("tin", "zinc", "lead", "nickel", "silver")
CUSTOM_INGOTS = ("tin", "bronze", "zinc", "lead", "nickel", "silver", "steel", "brass", "invar", "rose_gold")
CUSTOM_DUSTS = ("copper", "tin", "bronze", "zinc", "lead", "nickel", "silver", "steel", "brass", "invar", "rose_gold", "sulfur", "coal", "sulfur_coke")
EQUIPMENT = ("wood", "stone", "flint", "copper", "tin", "bronze", "zinc", "lead", "steel", "brass", "nickel", "invar", "silver", "rose_gold")
STANDARD_ROLES = ("sword", "pickaxe", "axe", "shovel", "hoe")
FIELD_ROLES = ("knife", "hammer", "saw", "hatchet")
ARMOR = ("helmet", "chestplate", "leggings", "boots")
ALLOYS = {
    "bronze": (["#c:dusts/copper", "#c:dusts/copper", "#c:dusts/copper", "#c:dusts/tin"], 4),
    "steel": (["#c:dusts/iron", "#material_progression:carbon_dusts"], 1),
    "brass": (["#c:dusts/copper", "#c:dusts/zinc"], 2),
    "invar": (["#c:dusts/iron", "#c:dusts/nickel"], 2),
    "rose_gold": (["#c:dusts/gold", "#c:dusts/copper"], 2),
}


def write_json(path: Path, value: object) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, indent=2, sort_keys=True) + "\n", encoding="utf-8")


def ore_block(host: str, material: str) -> str:
    return f"{material}_ore" if host == "stone" else f"{host}_{material}_ore"


def block_id(name: str) -> str:
    return f"material_progression:{name}"


def item_id_for_material(material: str, form: str) -> str:
    if material == "copper" and form == "raw":
        return "minecraft:raw_copper"
    return block_id(f"{material}_{form}")


def write_tags() -> None:
    roots = {
        "ores": [f"#c:ores/{material}" for material in ORES],
        "dusts": [f"#c:dusts/{material}" for material in CUSTOM_DUSTS] + ["#c:dusts/iron", "#c:dusts/gold"],
        "ingots": [f"#c:ingots/{material}" for material in CUSTOM_INGOTS],
        "raw_materials": [f"#c:raw_materials/{material}" for material in CUSTOM_RAW],
    }
    for root, values in roots.items():
        write_json(DATA / "c" / "tags" / "item" / f"{root}.json", {"values": values})
    write_json(DATA / "c" / "tags" / "block" / "ores.json", {
        "values": [f"#c:ores/{material}" for material in ORES]
    })
    write_json(DATA / "material_progression" / "tags" / "item" / "carbon_dusts.json", {"values": ["material_progression:coal_dust"]})
    for material, profile in ORES.items():
        ids = [block_id(ore_block(host, material)) for host in profile["hosts"]]
        ids.append(block_id(f"gravel_{material}_ore"))
        write_json(DATA / "c" / "tags" / "block" / "ores" / f"{material}.json", {"values": ids})
        write_json(DATA / "c" / "tags" / "item" / "ores" / f"{material}.json", {"values": ids})
    for material in CUSTOM_RAW:
        write_json(DATA / "c" / "tags" / "item" / "raw_materials" / f"{material}.json", {"values": [block_id(f"raw_{material}")]})
    for material in CUSTOM_INGOTS:
        write_json(DATA / "c" / "tags" / "item" / "ingots" / f"{material}.json", {"values": [block_id(f"{material}_ingot")]})
    for material in CUSTOM_DUSTS:
        write_json(DATA / "c" / "tags" / "item" / "dusts" / f"{material}.json", {"values": [block_id(f"{material}_dust")]})
    # Vanilla does not publish dust forms.  These tags intentionally retain
    # vanilla ingots as the compatible industrial input until a future pass
    # introduces their physical dust items.
    write_json(DATA / "c" / "tags" / "item" / "dusts" / "iron.json", {"values": ["minecraft:iron_ingot"]})
    write_json(DATA / "c" / "tags" / "item" / "dusts" / "gold.json", {"values": ["minecraft:gold_ingot"]})

    # Preserve the opening pass's established mining membership while adding
    # the matrix. Tags are additive compatibility interfaces, never a reset.
    base_pickaxe = [
        "material_progression:crusher", "material_progression:tin_ore",
        "material_progression:deepslate_tin_ore",
        *[f"material_progression:cobbled_{family}" for family in (
            "granite", "diorite", "andesite", "tuff", "calcite", "dripstone",
            "sulfur", "cinnabar", "sandstone", "red_sandstone", "netherrack",
            "basalt", "blackstone", "end_stone",
        )],
    ]
    base_stone = [
        "material_progression:tin_ore", "material_progression:deepslate_tin_ore",
        *[f"material_progression:cobbled_{family}" for family in (
            "granite", "diorite", "andesite", "tuff", "cinnabar", "end_stone",
        )],
    ]
    base_iron = [
        "material_progression:cobbled_basalt",
        "material_progression:cobbled_blackstone",
    ]
    pickaxe, shovel, stone, iron = base_pickaxe, [], base_stone, base_iron
    for material, profile in ORES.items():
        for host in profile["hosts"]:
            identifier = block_id(ore_block(host, material))
            pickaxe.append(identifier)
            (iron if profile["level"] >= 2 else stone).append(identifier)
        identifier = block_id(f"gravel_{material}_ore")
        shovel.append(identifier)
        (iron if profile["level"] >= 2 else stone).append(identifier)
    for path, values in {
        "mineable/pickaxe": pickaxe,
        "mineable/shovel": shovel,
        "needs_stone_tool": stone,
        "needs_iron_tool": iron,
    }.items():
        write_json(DATA / "minecraft" / "tags" / "block" / f"{path}.json", {"replace": False, "values": values})

    tool_tags = {"hammers": [], "knives": [], "saws": []}
    for material in EQUIPMENT:
        for role, tag in (("hammer", "hammers"), ("knife", "knives"), ("saw", "saws")):
            tool_tags[tag].append(block_id(f"{material}_{role}"))
    for tag, values in tool_tags.items():
        write_json(DATA / "c" / "tags" / "item" / "tools" / f"{tag}.json", {"values": values})

    all_tools = sorted({item for values in tool_tags.values() for item in values} | {
        f"material_progression:{material}_{role}"
        for material in EQUIPMENT
        for role in (() if material in {"wood", "stone"} else STANDARD_ROLES)
    } | {f"material_progression:{material}_hatchet" for material in EQUIPMENT})
    write_json(DATA / "minecraft" / "tags" / "item" / "enchantable" / "durability.json", {"replace": False, "values": all_tools})
    mining = [tool for tool in all_tools if not tool.endswith("_sword") and not tool.endswith("_knife")]
    write_json(DATA / "minecraft" / "tags" / "item" / "enchantable" / "mining.json", {"replace": False, "values": mining})
    categories = {
        "axes": [tool for tool in all_tools if tool.endswith("_axe") or tool.endswith("_hatchet") or tool.endswith("_saw")],
        "hoes": [tool for tool in all_tools if tool.endswith("_hoe")],
        "pickaxes": [tool for tool in all_tools if tool.endswith("_pickaxe") or tool.endswith("_hammer")],
        "shovels": [tool for tool in all_tools if tool.endswith("_shovel")],
        "swords": [tool for tool in all_tools if tool.endswith("_sword") or tool.endswith("_knife")],
    }
    for category, values in categories.items():
        write_json(DATA / "minecraft" / "tags" / "item" / f"{category}.json", {"replace": False, "values": values})
    write_json(DATA / "material_progression" / "tags" / "item" / "crusher_inputs.json", {"values": [
        *[f"#c:ores/{material}" for material in ORES],
        *[f"#c:raw_materials/{material}" for material in ORES],
        "#material_progression:carbon_sources", "#c:rocks/sulfur",
    ]})


def write_blocks() -> None:
    for material, profile in ORES.items():
        for host in profile["hosts"]:
            identifier = ore_block(host, material)
            write_json(ASSETS / "blockstates" / f"{identifier}.json", {"variants": {"": {"model": f"material_progression:block/{identifier}"}}})
            write_json(ASSETS / "models" / "block" / f"{identifier}.json", {"parent": "minecraft:block/cube_all", "textures": {"all": f"material_progression:block/{identifier}"}})
            write_json(DATA / "material_progression" / "loot_table" / "blocks" / f"{identifier}.json", ore_loot(profile["raw"]))
        identifier = f"gravel_{material}_ore"
        write_json(ASSETS / "blockstates" / f"{identifier}.json", {"variants": {"": {"model": f"material_progression:block/{identifier}"}}})
        write_json(ASSETS / "models" / "block" / f"{identifier}.json", {"parent": "minecraft:block/cube_all", "textures": {"all": f"material_progression:block/{identifier}"}})
        write_json(DATA / "material_progression" / "loot_table" / "blocks" / f"{identifier}.json", ore_loot(profile["raw"]))


def ore_loot(result: str) -> dict:
    return {"type": "minecraft:block", "pools": [{"rolls": 1, "entries": [{"type": "minecraft:item", "name": result, "functions": [{"function": "minecraft:apply_bonus", "enchantment": "minecraft:fortune", "formula": "minecraft:ore_drops"}, {"function": "minecraft:explosion_decay"}]}]}]}


def write_recipes() -> None:
    recipes = DATA / "material_progression" / "recipe"
    write_json(recipes / "crusher.json", {"type": "minecraft:crafting_shaped", "category": "building", "key": {"C": "#c:cobblestones", "P": "#minecraft:planks"}, "pattern": ["CCC", "CPC", "CCC"], "result": {"id": "material_progression:crusher"}})
    for material, profile in ORES.items():
        dust = profile["dust"]
        write_json(recipes / f"crushing_{material}_ore.json", cooking("#c:ores/" + material, dust))
        raw_tag = "#c:raw_materials/" + material
        write_json(recipes / f"crushing_raw_{material}.json", cooking(raw_tag, dust))
        if material != "copper":
            write_json(recipes / f"smelting_raw_{material}.json", smelting(raw_tag, block_id(f"{material}_ingot")))
            write_json(recipes / f"smelting_{material}_dust.json", smelting("#c:dusts/" + material, block_id(f"{material}_ingot")))
    for material, (ingredients, count) in ALLOYS.items():
        write_json(recipes / f"{material}_dust.json", {"type": "minecraft:crafting_shapeless", "category": "misc", "ingredients": ingredients, "result": {"id": block_id(f"{material}_dust"), "count": count}})
        write_json(recipes / f"smelting_{material}_dust.json", smelting("#c:dusts/" + material, block_id(f"{material}_ingot")))
    write_json(recipes / "crushing_sulfur_rock.json", cooking("#c:rocks/sulfur", "material_progression:sulfur_dust"))
    write_json(recipes / "crushing_coal.json", cooking("#material_progression:carbon_sources", "material_progression:coal_dust"))
    write_json(DATA / "material_progression" / "tags" / "item" / "carbon_sources.json", {"values": ["minecraft:coal", "minecraft:charcoal"]})
    write_json(recipes / "sulfur_coke_dust.json", {"type": "minecraft:crafting_shapeless", "category": "misc", "ingredients": ["#c:dusts/coal", "#c:dusts/sulfur"], "result": {"id": "material_progression:sulfur_coke_dust"}})
    write_json(recipes / "smelting_sulfur_coke_dust.json", smelting("#c:dusts/sulfur_coke", "material_progression:sulfur_coke"))
    for material in EQUIPMENT:
        ingredient = material_ingredient(material)
        roles = FIELD_ROLES if material in {"wood", "stone"} else STANDARD_ROLES + FIELD_ROLES
        for role in roles:
            if material == "flint" and role in FIELD_ROLES:
                continue
            write_json(recipes / f"{material}_{role}.json", tool_recipe(material, role, ingredient))
        for armor in ARMOR:
            write_json(recipes / f"{material}_{armor}.json", armor_recipe(material, armor, ingredient))

    # Flint is intentionally its own primitive tier.  Do not let the general
    # metal-tool template erase its shard-based recipes.
    for role, pattern in {
        "hatchet": ["RS", " S"],
        "hammer": ["RRR", " S ", " S "],
        "knife": ["R", "S"],
        "saw": ["RRR", "S S"],
    }.items():
        recipe = {
            "type": "minecraft:crafting_shaped",
            "pattern": pattern,
            "key": {"R": "#c:flint_shards", "S": "#c:rods/wooden"},
            "result": {"id": block_id(f"flint_{role}")},
        }
        if role != "hatchet":
            recipe["category"] = "equipment"
        write_json(recipes / f"flint_{role}.json", recipe)

    write_json(DATA / "neoforge" / "data_maps" / "item" / "furnace_fuels.json", {"values": {"material_progression:sulfur_coke": {"burn_time": 3200}}})


def cooking(ingredient: str, result: str) -> dict:
    return {"type": "material_progression:crushing", "category": "misc", "ingredient": ingredient, "result": {"id": result, "count": 2}, "experience": 0.1, "cookingtime": 200}


def smelting(ingredient: str, result: str) -> dict:
    return {"type": "minecraft:smelting", "category": "misc", "ingredient": ingredient, "result": {"id": result}, "experience": 0.1, "cookingtime": 200}


def material_ingredient(material: str) -> str:
    return {"wood": "#minecraft:planks", "stone": "#c:cobblestones", "flint": "#c:flint_shards"}.get(material, f"#c:ingots/{material}")


def tool_recipe(material: str, role: str, ingredient: str) -> dict:
    patterns = {
        "sword": ["I", "I", "S"], "pickaxe": ["III", " S ", " S "], "axe": ["II", "IS", " S"],
        "shovel": ["I", "S", "S"], "hoe": ["II", " S", " S"], "knife": ["I", "S"],
        "hammer": ["III", " S ", " S "], "saw": ["III", "S S"], "hatchet": ["IS", " S"],
    }
    return {"type": "minecraft:crafting_shaped", "category": "equipment", "key": {"I": ingredient, "S": "#c:rods/wooden"}, "pattern": patterns[role], "result": {"id": block_id(f"{material}_{role}")}}


def armor_recipe(material: str, role: str, ingredient: str) -> dict:
    patterns = {"helmet": ["III", "I I"], "chestplate": ["I I", "III", "III"], "leggings": ["III", "I I", "I I"], "boots": ["I I", "I I"]}
    return {"type": "minecraft:crafting_shaped", "category": "equipment", "key": {"I": ingredient}, "pattern": patterns[role], "result": {"id": block_id(f"{material}_{role}")}}


def write_worldgen() -> None:
    for material, profile in ORES.items():
        targets = []
        for host in profile["hosts"]:
            targets.append({"state": {"Name": block_id(ore_block(host, material))}, "target": {"predicate_type": "minecraft:tag_match", "tag": f"material_progression:ore_replaceables/{host}"}})
            write_json(DATA / "material_progression" / "tags" / "block" / "ore_replaceables" / f"{host}.json", {"values": [RAW_HOST_BLOCKS[host]]})
        write_json(DATA / "material_progression" / "worldgen" / "configured_feature" / f"{material}_ore.json", {"type": "minecraft:ore", "config": {"discard_chance_on_air_exposure": 0.0, "size": 8, "targets": targets}})
        write_json(DATA / "material_progression" / "worldgen" / "placed_feature" / f"{material}_ore.json", placed(f"material_progression:{material}_ore", 9, -48, 96))
        gravel = f"gravel_{material}_ore"
        write_json(DATA / "material_progression" / "worldgen" / "configured_feature" / f"{material}_gravel_ore.json", {"type": "minecraft:ore", "config": {"discard_chance_on_air_exposure": 0.0, "size": 4, "targets": [{"state": {"Name": block_id(gravel)}, "target": {"predicate_type": "minecraft:block_match", "block": "minecraft:gravel"}}]}})
        write_json(DATA / "material_progression" / "worldgen" / "placed_feature" / f"{material}_gravel_ore.json", placed(f"material_progression:{material}_gravel_ore", 3, 32, 128))
        for domain in profile["domains"]:
            biome = {"overworld": "#minecraft:is_overworld", "nether": "#minecraft:is_nether", "end": "#minecraft:is_end"}[domain]
            write_json(DATA / "material_progression" / "neoforge" / "biome_modifier" / f"add_{material}_ore_{domain}.json", {"type": "neoforge:add_features", "biomes": biome, "features": f"material_progression:{material}_ore", "step": "underground_ores"})
            write_json(DATA / "material_progression" / "neoforge" / "biome_modifier" / f"add_{material}_gravel_ore_{domain}.json", {"type": "neoforge:add_features", "biomes": biome, "features": f"material_progression:{material}_gravel_ore", "step": "underground_ores"})


def placed(feature: str, count: int, minimum: int, maximum: int) -> dict:
    return {"feature": feature, "placement": [{"type": "minecraft:count", "count": count}, {"type": "minecraft:in_square"}, {"type": "minecraft:height_range", "height": {"type": "minecraft:trapezoid", "min_inclusive": {"absolute": minimum}, "max_inclusive": {"absolute": maximum}, "plateau": 0}}, {"type": "minecraft:biome"}]}


def write_equipment_assets() -> None:
    for material in EQUIPMENT:
        layers = {"humanoid": [{"texture": f"material_progression:{material}"}], "humanoid_baby": [{"texture": f"material_progression:{material}"}], "humanoid_leggings": [{"texture": f"material_progression:{material}"}]}
        write_json(ASSETS / "equipment" / f"{material}.json", {"layers": layers})


def write_localization() -> None:
    en_path, pt_path = ASSETS / "lang" / "en_us.json", ASSETS / "lang" / "pt_br.json"
    en = json.loads(en_path.read_text(encoding="utf-8"))
    pt = json.loads(pt_path.read_text(encoding="utf-8"))
    names = {"tin": "Tin", "zinc": "Zinc", "lead": "Lead", "nickel": "Nickel", "silver": "Silver", "steel": "Steel", "brass": "Brass", "invar": "Invar", "rose_gold": "Rose Gold", "sulfur_coke": "Sulfur Coke", "sulfur": "Sulfur", "coal": "Coal", "copper": "Copper", "bronze": "Bronze", "wood": "Wood", "stone": "Stone", "flint": "Flint"}
    pt_names = {"tin": "Estanho", "zinc": "Zinco", "lead": "Chumbo", "nickel": "Níquel", "silver": "Prata", "steel": "Aço", "brass": "Latão", "invar": "Invar", "rose_gold": "Ouro Rosa", "sulfur_coke": "Coque de Enxofre", "sulfur": "Enxofre", "coal": "Carvão", "copper": "Cobre", "bronze": "Bronze", "wood": "Madeira", "stone": "Pedra", "flint": "Sílex"}
    for material in CUSTOM_RAW:
        en[f"item.material_progression.raw_{material}"] = f"Raw {names[material]}"; pt[f"item.material_progression.raw_{material}"] = f"{pt_names[material]} Bruto"
        en[f"tooltip.material_progression.raw_{material}"] = f"Smelt into a {names[material]} Ingot or grind into Dust."; pt[f"tooltip.material_progression.raw_{material}"] = f"Funda em uma barra de {pt_names[material]} ou moa em pó."
    for material in CUSTOM_INGOTS:
        en[f"item.material_progression.{material}_ingot"] = f"{names[material]} Ingot"; pt[f"item.material_progression.{material}_ingot"] = f"Barra de {pt_names[material]}"
        en[f"tooltip.material_progression.{material}_ingot"] = "Craft tools, armor, and material machinery."; pt[f"tooltip.material_progression.{material}_ingot"] = "Crie ferramentas, armaduras e máquinas de materiais."
    for material in CUSTOM_DUSTS:
        en[f"item.material_progression.{material}_dust"] = f"{names[material]} Dust"; pt[f"item.material_progression.{material}_dust"] = f"Pó de {pt_names[material]}"
        en[f"tooltip.material_progression.{material}_dust"] = "Smelt into an ingot or combine into an alloy."; pt[f"tooltip.material_progression.{material}_dust"] = "Funda em uma barra ou combine em uma liga."
    for material, profile in ORES.items():
        for host in profile["hosts"]:
            identifier = ore_block(host, material)
            en[f"block.material_progression.{identifier}"] = f"{names[material]} Ore"; pt[f"block.material_progression.{identifier}"] = f"Minério de {pt_names[material]}"
        en[f"block.material_progression.gravel_{material}_ore"] = f"{names[material]} Gravel Ore"; pt[f"block.material_progression.gravel_{material}_ore"] = f"Cascalho com Minério de {pt_names[material]}"
    for material in EQUIPMENT:
        en[f"material.material_progression.{material}"] = names[material]
        pt[f"material.material_progression.{material}"] = pt_names[material]
        for role in STANDARD_ROLES + FIELD_ROLES + ARMOR:
            if material in {"wood", "stone"} and role in STANDARD_ROLES:
                continue
            name = role.replace("_", " ").title()
            en[f"item.material_progression.{material}_{role}"] = f"{names[material]} {name}"; pt[f"item.material_progression.{material}_{role}"] = f"{name} de {pt_names[material]}"
    en.update({"tooltip.material_progression.ore": "Mine with the required Pickaxe or Hammer, then grind for double Dust.", "tooltip.material_progression.tool": "A material tool for harvesting and progression.", "tooltip.material_progression.armor": "Wear a full matching set to reveal its material trait.", "tooltip.material_progression.sulfur_coke": "An optional long-burning fuel for Furnaces and Grinders.", "container.material_progression.crusher": "Grinder", "block.material_progression.crusher": "Grinder", "tooltip.material_progression.axe": "Fells Logs and strips wood.", "tooltip.material_progression.hoe": "Prepares soil for crops.", "tooltip.material_progression.sword": "A reliable close-range weapon.", "tooltip.material_progression.pickaxe": "Mines stone and ore at its material capability.", "tooltip.material_progression.shovel": "Excavates Gravel Ores at the required tier.", "tooltip.material_progression.hatchet": "A compact Axe for Logs and field work.", "message.material_progression.ore.pickaxe_required": "%s Ore needs a capable Pickaxe or Hammer.", "message.material_progression.ore.shovel_required": "%s Gravel Ore needs a capable Shovel.", "message.material_progression.prospecting.hint": "%s detected to the %s.", "message.material_progression.prospecting.none": "No ore signal in the loaded area.", "message.material_progression.direction.north": "north", "message.material_progression.direction.south": "south", "message.material_progression.direction.east": "east", "message.material_progression.direction.west": "west"})
    pt.update({"tooltip.material_progression.ore": "Extraia com a Picareta ou Martelo necessário e moa para obter o dobro de pó.", "tooltip.material_progression.tool": "Uma ferramenta de material para coleta e progressão.", "tooltip.material_progression.armor": "Use um conjunto completo igual para revelar seu traço de material.", "tooltip.material_progression.sulfur_coke": "Combustível opcional de longa duração para Fornalhas e Moinhos.", "container.material_progression.crusher": "Moinho", "block.material_progression.crusher": "Moinho", "tooltip.material_progression.axe": "Derruba troncos e descasca madeira.", "tooltip.material_progression.hoe": "Prepara a terra para plantio.", "tooltip.material_progression.sword": "Uma arma confiável de curto alcance.", "tooltip.material_progression.pickaxe": "Minera pedra e minério na capacidade do material.", "tooltip.material_progression.shovel": "Escava Cascalho com Minério no nível necessário.", "tooltip.material_progression.hatchet": "Um Machado compacto para troncos e trabalho de campo.", "message.material_progression.ore.pickaxe_required": "Minério de %s precisa de uma Picareta ou Martelo capaz.", "message.material_progression.ore.shovel_required": "Cascalho com Minério de %s precisa de uma Pá capaz.", "message.material_progression.prospecting.hint": "%s detectado a %s.", "message.material_progression.prospecting.none": "Nenhum sinal de minério na área carregada.", "message.material_progression.direction.north": "norte", "message.material_progression.direction.south": "sul", "message.material_progression.direction.east": "leste", "message.material_progression.direction.west": "oeste"})
    en["item.material_progression.sulfur_coke"] = "Sulfur Coke"; pt["item.material_progression.sulfur_coke"] = "Coque de Enxofre"
    write_json(en_path, en); write_json(pt_path, pt)


def main() -> None:
    write_tags(); write_blocks(); write_recipes(); write_worldgen(); write_equipment_assets(); write_localization()


if __name__ == "__main__":
    main()
