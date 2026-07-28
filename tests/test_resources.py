import json
import unittest
from pathlib import Path

from content_contracts import (
    CRUSHING_RECIPES,
    PRIMITIVE_RECIPES,
    SHIPPED_BLOCKS,
    SHIPPED_ITEMS,
    SMELTING_RECIPES,
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
    "rocks": ["material_progression:rock"],
}

COMMON_BLOCK_TAGS = {
    "ores/tin": [
        "material_progression:tin_ore",
        "material_progression:deepslate_tin_ore",
    ],
}


class ResourceContractTests(unittest.TestCase):
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
                self.assertEqual("minecraft:random_patch", configured["type"])
                encoded = json.dumps(configured, sort_keys=True)
                self.assertIn(block_id, encoded)
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
        self.assertEqual({"crusher_inputs"}, private_item_tags)

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
