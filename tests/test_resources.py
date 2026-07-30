import json
import unittest
from pathlib import Path

from content_contracts import (
    CRUSHING_RECIPES,
    PRIMITIVE_RECIPES,
    SHIPPED_BLOCKS,
    SHIPPED_ITEMS,
    SMELTING_RECIPES,
    STONE_FAMILIES,
    SURFACE_WORLDGEN_FEATURES,
    TOOL_FAMILIES,
    WORLD_ONLY_BLOCKS,
)
from support.resources import RecipeContract, ResourceTree


ROOT = Path(__file__).resolve().parents[1]
TREE = ResourceTree(ROOT, "material_progression")
RESOURCES = TREE.resources
DATA = TREE.data
ASSETS = TREE.assets
COMMON_DATA = RESOURCES / "data" / "c"

COMMON_ITEM_TAGS = {
    "dusts/bronze": ["material_progression:bronze_dust"],
    "dusts/copper": ["material_progression:copper_dust"],
    "dusts/tin": ["material_progression:tin_dust"],
    "ingots/bronze": ["material_progression:bronze_ingot"],
    "ingots/tin": ["material_progression:tin_ingot"],
    "flint_shards": ["material_progression:flint_shard"],
    "ores/tin": [
        "material_progression:tin_ore",
        "material_progression:deepslate_tin_ore",
    ],
    "raw_materials/tin": ["material_progression:raw_tin"],
    "rocks": [f"#c:rocks/{family}" for family in STONE_FAMILIES],
}

COMMON_BLOCK_TAGS = {
    "ores/tin": [
        "material_progression:tin_ore",
        "material_progression:deepslate_tin_ore",
    ],
}


class ResourceContractTests(unittest.TestCase):
    def test_geology_feedback_and_hammer_extension_are_localized_and_tagged(self):
        hammer_tag = TREE.load_json(
            DATA / "tags" / "item" / "hammers.json"
        )
        self.assertEqual({"replace": False, "values": []}, hammer_tag)

        expected_messages = {
            "config.material_progression.server.enableGeologicalHardness",
            "config.material_progression.server.enableStoneRockDrops",
            "message.material_progression.geology.insufficient",
            *{
                f"message.material_progression.geology.capability.{level}"
                for level in range(4)
            },
        }
        for language in ("en_us", "pt_br"):
            translations = TREE.load_json(
                ASSETS / "lang" / f"{language}.json"
            )
            with self.subTest(language=language):
                self.assertTrue(expected_messages.issubset(translations))
                self.assertTrue(
                    all(translations[key].strip() for key in expected_messages)
                )

    def test_every_json_resource_parses(self):
        json_files = sorted(RESOURCES.rglob("*.json"))
        self.assertGreater(len(json_files), 0)

        for path in json_files:
            with self.subTest(path=path.relative_to(ROOT)):
                TREE.load_json(path)

    def test_every_shipped_item_has_models_and_translations(self):
        english = TREE.load_json(ASSETS / "lang" / "en_us.json")
        portuguese = TREE.load_json(ASSETS / "lang" / "pt_br.json")
        item_models = TREE.names_matching(ASSETS / "items", "*.json")
        self.assertEqual(SHIPPED_ITEMS, item_models)

        for item in SHIPPED_ITEMS:
            with self.subTest(item=item):
                self.assertTrue((ASSETS / "items" / f"{item}.json").is_file())
                prefix = "block" if item in SHIPPED_BLOCKS else "item"
                translation_key = f"{prefix}.material_progression.{item}"
                self.assertIn(translation_key, english)
                self.assertIn(translation_key, portuguese)
                self.assertTrue(english[translation_key].strip())
                self.assertTrue(portuguese[translation_key].strip())

    def test_every_shipped_block_has_blockstate_and_loot(self):
        english = TREE.load_json(ASSETS / "lang" / "en_us.json")
        portuguese = TREE.load_json(ASSETS / "lang" / "pt_br.json")
        blockstates = TREE.names_matching(ASSETS / "blockstates", "*.json")
        self.assertEqual(SHIPPED_BLOCKS, blockstates)

        for block in SHIPPED_BLOCKS:
            with self.subTest(block=block):
                self.assertTrue((ASSETS / "blockstates" / f"{block}.json").is_file())
                loot = TREE.load_json(
                    DATA / "loot_table" / "blocks" / f"{block}.json"
                )
                self.assertEqual("minecraft:block", loot["type"])
                translation_key = f"block.material_progression.{block}"
                self.assertIn(translation_key, english)
                self.assertIn(translation_key, portuguese)

    def test_world_only_blocks_have_no_inventory_form(self):
        item_models = TREE.names_matching(ASSETS / "items", "*.json")
        self.assertTrue(WORLD_ONLY_BLOCKS.isdisjoint(item_models))

    def test_primitive_recipes_preserve_the_bootstrap(self):
        for name, expected in PRIMITIVE_RECIPES.items():
            with self.subTest(recipe=name):
                self.assertEqual(expected, TREE.recipe(name))

    def test_surface_resources_have_complete_worldgen_chains(self):
        for name, block_id in SURFACE_WORLDGEN_FEATURES.items():
            with self.subTest(feature=name):
                configured = TREE.load_json(
                    DATA / "worldgen" / "configured_feature" / f"{name}.json"
                )
                placed = TREE.load_json(
                    DATA / "worldgen" / "placed_feature" / f"{name}.json"
                )
                modifier = TREE.load_json(
                    DATA / "neoforge" / "biome_modifier" / f"add_{name}.json"
                )
                self.assertEqual("minecraft:simple_block", configured["type"])
                encoded = json.dumps(configured, sort_keys=True)
                self.assertIn(block_id, encoded)
                placement_types = [
                    placement["type"]
                    for placement in placed["placement"]
                ]
                self.assertIn("minecraft:random_offset", placement_types)
                self.assertIn(
                    "minecraft:block_predicate_filter",
                    placement_types,
                )
                self.assertEqual(
                    f"material_progression:{name}",
                    placed["feature"],
                )
                self.assertEqual(
                    f"material_progression:{name}",
                    modifier["features"],
                )
                self.assertEqual("vegetal_decoration", modifier["step"])

    def test_crushing_recipes_match_two_dust_contract(self):
        recipe_dir = DATA / "recipe"
        actual_names = TREE.names_matching(recipe_dir, "crushing_*.json")
        self.assertEqual(set(CRUSHING_RECIPES), actual_names)

        for name, contract in CRUSHING_RECIPES.items():
            with self.subTest(recipe=name):
                self.assert_recipe_matches(name, contract)

    def test_smelting_recipes_preserve_material_flow(self):
        actual_names = TREE.names_matching(DATA / "recipe", "smelting_*.json")
        self.assertEqual(set(SMELTING_RECIPES), actual_names)

        for name, contract in SMELTING_RECIPES.items():
            with self.subTest(recipe=name):
                self.assert_recipe_matches(name, contract)

    def test_bronze_alloy_recipe_preserves_three_to_one_ratio(self):
        recipe = TREE.recipe("bronze_dust")
        self.assertEqual("minecraft:crafting_shapeless", recipe["type"])
        self.assertEqual(
            [
                "#c:dusts/copper",
                "#c:dusts/copper",
                "#c:dusts/copper",
                "#c:dusts/tin",
            ],
            recipe["ingredients"],
        )
        self.assertEqual(
            {"count": 4, "id": "material_progression:bronze_dust"},
            recipe["result"],
        )

    def test_tool_enchantment_tags_cover_every_tool(self):
        minecraft_tags = RESOURCES / "data" / "minecraft" / "tags" / "item"
        durability = TREE.load_json(
            minecraft_tags / "enchantable" / "durability.json"
        )
        tagged_tools = {
            value.removeprefix("material_progression:")
            for value in durability["values"]
        }
        all_tools = set().union(*TOOL_FAMILIES.values())
        self.assertEqual(all_tools, tagged_tools)

        mining = TREE.load_json(minecraft_tags / "enchantable" / "mining.json")
        mining_tools = {
            value.removeprefix("material_progression:")
            for value in mining["values"]
        }
        self.assertEqual(
            all_tools - {"tin_sword", "bronze_sword"},
            mining_tools,
        )

        expected_by_type = {
            "axes": {"flint_hatchet", "tin_axe", "bronze_axe"},
            "hoes": {"tin_hoe", "bronze_hoe"},
            "pickaxes": {"tin_pickaxe", "bronze_pickaxe"},
            "shovels": {"tin_shovel", "bronze_shovel"},
            "swords": {"tin_sword", "bronze_sword"},
        }
        for tag, expected_tools in expected_by_type.items():
            with self.subTest(tag=tag):
                payload = TREE.load_json(minecraft_tags / f"{tag}.json")
                actual_tools = {
                    value.removeprefix("material_progression:")
                    for value in payload["values"]
                }
                self.assertEqual(expected_tools, actual_tools)

    def test_crusher_inputs_tag_matches_crushing_recipe_inputs(self):
        crusher_inputs = TREE.load_json(
            DATA / "tags" / "item" / "crusher_inputs.json"
        )
        self.assertEqual(
            {
                "#c:ores/copper",
                "#c:ores/tin",
                "#c:raw_materials/copper",
                "#c:raw_materials/tin",
            },
            set(crusher_inputs["values"]),
        )

    def test_materials_are_published_under_common_tags(self):
        for tag, values in COMMON_ITEM_TAGS.items():
            with self.subTest(registry="item", tag=tag):
                payload = TREE.load_json(
                    COMMON_DATA / "tags" / "item" / f"{tag}.json"
                )
                self.assertEqual(values, payload["values"])

        for tag, values in COMMON_BLOCK_TAGS.items():
            with self.subTest(registry="block", tag=tag):
                payload = TREE.load_json(
                    COMMON_DATA / "tags" / "block" / f"{tag}.json"
                )
                self.assertEqual(values, payload["values"])

    def test_common_material_roots_include_material_subtags(self):
        expected_roots = {
            "dusts": {
                "#c:dusts/bronze",
                "#c:dusts/copper",
                "#c:dusts/tin",
            },
            "ingots": {"#c:ingots/bronze", "#c:ingots/tin"},
            "ores": {"#c:ores/tin"},
            "raw_materials": {"#c:raw_materials/tin"},
        }

        for root_tag, expected_values in expected_roots.items():
            with self.subTest(registry="item", tag=root_tag):
                payload = TREE.load_json(
                    COMMON_DATA / "tags" / "item" / f"{root_tag}.json"
                )
                self.assertEqual(expected_values, set(payload["values"]))

        block_ores = TREE.load_json(
            COMMON_DATA / "tags" / "block" / "ores.json"
        )
        self.assertEqual({"#c:ores/tin"}, set(block_ores["values"]))

    def test_recipe_material_inputs_use_common_tags(self):
        direct_material_inputs = {
            "material_progression:bronze_dust",
            "material_progression:bronze_ingot",
            "material_progression:copper_dust",
            "material_progression:raw_tin",
            "material_progression:tin_dust",
            "material_progression:tin_ingot",
            "material_progression:tin_ore",
            "material_progression:deepslate_tin_ore",
            "minecraft:raw_copper",
        }

        for recipe_path in sorted((DATA / "recipe").glob("*.json")):
            recipe = TREE.load_json(recipe_path)
            ingredients = []
            if "ingredient" in recipe:
                ingredients.append(recipe["ingredient"])
            ingredients.extend(recipe.get("ingredients", []))
            ingredients.extend(recipe.get("key", {}).values())

            for ingredient in ingredients:
                with self.subTest(
                    recipe=recipe_path.stem,
                    ingredient=ingredient,
                ):
                    self.assertNotIn(ingredient, direct_material_inputs)

    def test_private_tags_are_reserved_for_private_behavior(self):
        private_item_tags = TREE.names_matching(
            DATA / "tags" / "item", "*.json"
        )
        self.assertEqual({"crusher_inputs", "hammers"}, private_item_tags)

    def test_stone_family_static_resources_preserve_material_identity(self):
        expected_resistance = {
            "soft": {
                "calcite", "dripstone", "sulfur", "sandstone",
                "red_sandstone", "netherrack",
            },
            "standard": {
                "stone", "granite", "diorite", "andesite", "tuff",
                "cinnabar", "end_stone",
            },
            "hard": {"deepslate", "basalt", "blackstone"},
        }
        self.assertEqual(
            expected_resistance,
            {
                resistance: {
                    family
                    for family, contract in STONE_FAMILIES.items()
                    if contract["resistance"] == resistance
                }
                for resistance in expected_resistance
            },
        )

        common_blocks = COMMON_DATA / "tags" / "block"
        common_items = COMMON_DATA / "tags" / "item"
        for family, contract in STONE_FAMILIES.items():
            with self.subTest(family=family):
                rock = "material_progression:rock" if family == "stone" else (
                    f"material_progression:{family}_rock"
                )
                cobbled = contract["cobbled_block"]
                raw = contract["raw_block"]
                self.assertEqual(
                    [rock],
                    TREE.load_json(
                        common_items / "rocks" / f"{family}.json"
                    )["values"],
                )
                self.assertEqual(
                    [cobbled],
                    TREE.load_json(
                        common_blocks / "cobblestones" / f"{family}.json"
                    )["values"],
                )
                self.assertEqual(
                    [cobbled],
                    TREE.load_json(
                        common_items / "cobblestones" / f"{family}.json"
                    )["values"],
                )
                self.assertEqual(
                    [raw],
                    TREE.load_json(
                        DATA / "tags" / "block" / "stone_sources"
                        / f"{family}.json"
                    )["values"],
                )
                self.assertEqual(
                    {"type": "minecraft:smelting", "ingredient": f"#c:cobblestones/{family}", "result": {"id": raw}},
                    TREE.recipe(f"smelting_cobbled_{family}"),
                )

        self.assertEqual(
            {f"#c:rocks/{family}" for family in STONE_FAMILIES},
            set(TREE.load_json(common_items / "rocks.json")["values"]),
        )
        for registry in (common_blocks, common_items):
            with self.subTest(registry=registry):
                self.assertEqual(
                    {f"#c:cobblestones/{family}" for family in STONE_FAMILIES},
                    set(TREE.load_json(registry / "cobblestones.json")["values"]),
                )

        self.assertEqual(
            {
                "minecraft:dirt", "minecraft:coarse_dirt", "minecraft:rooted_dirt",
                "minecraft:grass_block", "minecraft:podzol", "minecraft:mycelium",
                "minecraft:gravel", "minecraft:snow", "minecraft:snow_block",
                "minecraft:sand", "minecraft:red_sand", "minecraft:soul_sand",
                "minecraft:soul_soil",
            },
            set(TREE.load_json(
                DATA / "tags" / "block" / "loose_rock_cover.json"
            )["values"]),
        )
        for family, contract in STONE_FAMILIES.items():
            with self.subTest(loose_rock_surface=family):
                expected_surface = [contract["raw_block"]]
                if family == "sandstone":
                    expected_surface.append("minecraft:sand")
                if family == "red_sandstone":
                    expected_surface.append("minecraft:red_sand")
                self.assertEqual(
                    expected_surface,
                    TREE.load_json(
                        DATA / "tags" / "block" / "loose_rock_surfaces"
                        / f"{family}.json"
                    )["values"],
                )
        self.assertEqual(
            {
                f"#material_progression:loose_rock_surfaces/{family}"
                for family in STONE_FAMILIES
            },
            set(TREE.load_json(
                DATA / "tags" / "block" / "loose_rock_surfaces.json"
            )["values"]),
        )
        self.assertFalse((
            DATA / "tags" / "block" / "loose_rock_surface.json"
        ).exists())

        mod_cobbled = {
            contract["cobbled_block"]
            for contract in STONE_FAMILIES.values()
            if contract["cobbled_block"].startswith("material_progression:")
        }
        self.assertTrue(mod_cobbled.issubset(set(TREE.load_json(
            RESOURCES / "data" / "minecraft" / "tags" / "block"
            / "mineable" / "pickaxe.json"
        )["values"])))
        for tier, resistance in {
            "needs_stone_tool": "standard",
            "needs_iron_tool": "hard",
        }.items():
            self.assertEqual(
                {
                    contract["cobbled_block"]
                    for contract in STONE_FAMILIES.values()
                    if contract["resistance"] == resistance
                    and contract["cobbled_block"].startswith(
                        "material_progression:"
                    )
                },
                {
                    value
                    for value in TREE.load_json(
                    RESOURCES / "data" / "minecraft" / "tags" / "block"
                    / f"{tier}.json"
                    )["values"]
                    if value in mod_cobbled
                },
            )
        for family, contract in STONE_FAMILIES.items():
            cobbled = contract["cobbled_block"]
            if not cobbled.startswith("material_progression:"):
                continue
            with self.subTest(self_drop=cobbled):
                loot = TREE.load_json(
                    DATA / "loot_table" / "blocks"
                    / f"{cobbled.removeprefix('material_progression:')}.json"
                )
                self.assertEqual(cobbled, loot["pools"][0]["entries"][0]["name"])

    def test_runtime_stone_family_definitions_are_complete_and_literal(self):
        family_dir = DATA / "stone_family"
        self.assertEqual(
            set(STONE_FAMILIES),
            TREE.names_matching(family_dir, "*.json"),
        )
        resistance_modifiers = {
            "soft": 0.75,
            "standard": 1.0,
            "hard": 1.5,
        }
        for family, contract in STONE_FAMILIES.items():
            with self.subTest(family=family):
                self.assertEqual(
                    {
                        "source_block_tag": (
                            f"#material_progression:stone_sources/{family}"
                        ),
                        "rock_item_tag": f"#c:rocks/{family}",
                        "cobbled_block": contract["cobbled_block"],
                        "raw_block": contract["raw_block"],
                        "loose_rock_surface_block_tag": (
                            "#material_progression:"
                            f"loose_rock_surfaces/{family}"
                        ),
                        "resistance": {
                            "tier": contract["resistance"],
                            "modifier": resistance_modifiers[
                                contract["resistance"]
                            ],
                        },
                    },
                    TREE.load_json(family_dir / f"{family}.json"),
                )

    def test_loose_rocks_are_family_stateful_and_use_custom_worldgen(self):
        blockstate = TREE.load_json(
            ASSETS / "blockstates" / "loose_rocks.json"
        )
        self.assertEqual(
            {f"family={family}" for family in STONE_FAMILIES},
            set(blockstate["variants"]),
        )
        for family, variants in blockstate["variants"].items():
            with self.subTest(blockstate_family=family):
                self.assertEqual(4, len(variants))
                expected_model = (
                    "material_progression:block/loose_rocks/"
                    f"{family.removeprefix('family=')}"
                )
                self.assertEqual(
                    {expected_model},
                    {variant["model"] for variant in variants},
                )

        loot = TREE.load_json(
            DATA / "loot_table" / "blocks" / "loose_rocks.json"
        )
        self.assertEqual(len(STONE_FAMILIES), len(loot["pools"]))
        expected_drops = {
            family: (
                "material_progression:rock"
                if family == "stone"
                else f"material_progression:{family}_rock"
            )
            for family in STONE_FAMILIES
        }
        actual_drops = {}
        for pool in loot["pools"]:
            condition = pool["conditions"][0]
            self.assertEqual(
                "minecraft:block_state_property",
                condition["condition"],
            )
            self.assertEqual(
                "material_progression:loose_rocks",
                condition["block"],
            )
            family = condition["properties"]["family"]
            actual_drops[family] = pool["entries"][0]["name"]
            self.assertNotIn("functions", pool)
        self.assertEqual(expected_drops, actual_drops)

        configured = TREE.load_json(
            DATA / "worldgen" / "configured_feature" / "loose_rocks.json"
        )
        self.assertEqual(
            {"type": "material_progression:loose_rocks", "config": {}},
            configured,
        )
        self.assertNotIn("minecraft:simple_block", json.dumps(configured))

        expected_placed = {
            "loose_rocks_surface",
            "loose_rocks_caves",
            "loose_rocks_nether",
            "loose_rocks_end",
        }
        self.assertEqual(
            expected_placed,
            {
                path.stem
                for path in (
                    DATA / "worldgen" / "placed_feature"
                ).glob("loose_rocks*.json")
            },
        )
        for name in expected_placed:
            placed = TREE.load_json(
                DATA / "worldgen" / "placed_feature" / f"{name}.json"
            )
            self.assertEqual(
                "material_progression:loose_rocks",
                placed["feature"],
            )
            placement_types = {
                placement["type"] for placement in placed["placement"]
            }
            self.assertIn("minecraft:in_square", placement_types)
            self.assertIn("minecraft:biome", placement_types)
            self.assertNotIn("minecraft:would_survive", json.dumps(placed))
            vertical_placement = (
                "minecraft:heightmap"
                if name in {"loose_rocks_surface", "loose_rocks_end"}
                else "minecraft:height_range"
            )
            self.assertIn(vertical_placement, placement_types)

        expected_modifiers = {
            "add_loose_rocks_surface": (
                "#minecraft:is_overworld",
                "material_progression:loose_rocks_surface",
            ),
            "add_loose_rocks_caves": (
                "#minecraft:is_overworld",
                "material_progression:loose_rocks_caves",
            ),
            "add_loose_rocks_nether": (
                "#minecraft:is_nether",
                "material_progression:loose_rocks_nether",
            ),
            "add_loose_rocks_end": (
                "#minecraft:is_end",
                "material_progression:loose_rocks_end",
            ),
        }
        self.assertFalse((
            DATA / "neoforge" / "biome_modifier" / "add_loose_rocks.json"
        ).exists())
        for name, (biomes, feature) in expected_modifiers.items():
            modifier = TREE.load_json(
                DATA / "neoforge" / "biome_modifier" / f"{name}.json"
            )
            self.assertEqual("neoforge:add_features", modifier["type"])
            self.assertEqual(biomes, modifier["biomes"])
            self.assertEqual(feature, modifier["features"])
            self.assertEqual("vegetal_decoration", modifier["step"])

        ground_stick_modifier = TREE.load_json(
            DATA / "neoforge" / "biome_modifier" / "add_ground_stick.json"
        )
        self.assertEqual(
            "#minecraft:is_overworld",
            ground_stick_modifier["biomes"],
        )

    def test_dynamic_cobbling_has_one_non_competing_recipe(self):
        recipe_names = {
            path.stem for path in (DATA / "recipe").glob("*rock*.json")
        }
        self.assertIn("rock_cobbling", recipe_names)
        self.assertNotIn("cobblestone_from_rocks", recipe_names)
        self.assertEqual(
            {"type": "material_progression:rock_cobbling"},
            TREE.recipe("rock_cobbling"),
        )
        all_recipes = [
            TREE.load_json(path)
            for path in (DATA / "recipe").glob("*.json")
        ]
        generic_four_rock_recipes = [
            recipe for recipe in all_recipes
            if recipe.get("ingredients") == ["#c:rocks"] * 4
        ]
        self.assertEqual([], generic_four_rock_recipes)

    def test_tin_worldgen_resources_form_a_complete_chain(self):
        configured = TREE.load_json(
            DATA / "worldgen" / "configured_feature" / "tin_ore.json"
        )
        placed = TREE.load_json(
            DATA / "worldgen" / "placed_feature" / "tin_ore.json"
        )
        modifier = TREE.load_json(
            DATA / "neoforge" / "biome_modifier" / "add_tin_ore.json"
        )

        target_blocks = {
            target["state"]["Name"] for target in configured["config"]["targets"]
        }
        self.assertEqual(
            {
                "material_progression:tin_ore",
                "material_progression:deepslate_tin_ore",
            },
            target_blocks,
        )
        self.assertEqual("material_progression:tin_ore", placed["feature"])
        self.assertEqual("material_progression:tin_ore", modifier["features"])
        self.assertEqual("#minecraft:is_overworld", modifier["biomes"])
        self.assertEqual("underground_ores", modifier["step"])

    def assert_recipe_matches(
        self, name: str, contract: RecipeContract
    ) -> None:
        recipe = TREE.recipe(name)
        self.assertEqual(contract.recipe_type, recipe["type"])
        self.assertEqual(contract.ingredient, TREE.ingredient_id(recipe))

        expected_result = {"id": contract.result}
        if contract.count != 1:
            expected_result["count"] = contract.count
        self.assertEqual(expected_result, recipe["result"])

        if contract.cooking_time is not None:
            self.assertEqual(contract.cooking_time, recipe["cookingtime"])


if __name__ == "__main__":
    unittest.main()
