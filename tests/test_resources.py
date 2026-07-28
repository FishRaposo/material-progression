import unittest
from pathlib import Path

from content_contracts import (
    CRUSHING_RECIPES,
    SHIPPED_BLOCKS,
    SHIPPED_ITEMS,
    SMELTING_RECIPES,
    TOOL_FAMILIES,
)
from support.resources import RecipeContract, ResourceTree


ROOT = Path(__file__).resolve().parents[1]
TREE = ResourceTree(ROOT, "material_progression")
RESOURCES = TREE.resources
DATA = TREE.data
ASSETS = TREE.assets


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
        blockstates = TREE.names_matching(ASSETS / "blockstates", "*.json")
        self.assertEqual(SHIPPED_BLOCKS, blockstates)

        for block in SHIPPED_BLOCKS:
            with self.subTest(block=block):
                self.assertTrue((ASSETS / "blockstates" / f"{block}.json").is_file())
                loot = TREE.load_json(
                    DATA / "loot_table" / "blocks" / f"{block}.json"
                )
                self.assertEqual("minecraft:block", loot["type"])

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
                "material_progression:copper_dust",
                "material_progression:copper_dust",
                "material_progression:copper_dust",
                "material_progression:tin_dust",
            ],
            recipe["ingredients"],
        )
        self.assertEqual(
            {"count": 4, "id": "material_progression:bronze_dust"},
            recipe["result"],
        )

    def test_tool_repair_and_enchantment_tags_cover_every_tool(self):
        tin_repairs = TREE.load_json(
            DATA / "tags" / "item" / "repairs_tin_tools.json"
        )
        bronze_repairs = TREE.load_json(
            DATA / "tags" / "item" / "repairs_bronze_tools.json"
        )
        self.assertEqual(["material_progression:tin_ingot"], tin_repairs["values"])
        self.assertEqual(
            ["material_progression:bronze_ingot"], bronze_repairs["values"]
        )

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

    def test_crusher_inputs_tag_matches_crushing_recipe_inputs(self):
        crusher_inputs = TREE.load_json(
            DATA / "tags" / "item" / "crusher_inputs.json"
        )
        self.assertEqual(
            {
                "minecraft:raw_copper",
                "minecraft:copper_ore",
                "minecraft:deepslate_copper_ore",
                "material_progression:raw_tin",
                "material_progression:tin_ore",
                "material_progression:deepslate_tin_ore",
            },
            set(crusher_inputs["values"]),
        )

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
