import json
import unittest
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
RESOURCES = ROOT / "src" / "main" / "resources"
DATA = RESOURCES / "data" / "material_progression"
ASSETS = RESOURCES / "assets" / "material_progression"

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

CRUSHING_CONTRACTS = {
    "crushing_copper_ore": ("#minecraft:copper_ores", "copper_dust"),
    "crushing_raw_copper": ("minecraft:raw_copper", "copper_dust"),
    "crushing_raw_tin": ("material_progression:raw_tin", "tin_dust"),
    "crushing_tin_ore": ("#material_progression:tin_ores", "tin_dust"),
}

SMELTING_CONTRACTS = {
    "smelting_bronze_dust": ("material_progression:bronze_dust", "material_progression:bronze_ingot"),
    "smelting_copper_dust": ("material_progression:copper_dust", "minecraft:copper_ingot"),
    "smelting_raw_tin": ("material_progression:raw_tin", "material_progression:tin_ingot"),
    "smelting_tin_dust": ("material_progression:tin_dust", "material_progression:tin_ingot"),
}

TIN_TOOLS = {"tin_sword", "tin_pickaxe", "tin_axe", "tin_shovel", "tin_hoe"}
BRONZE_TOOLS = {
    "bronze_sword",
    "bronze_pickaxe",
    "bronze_axe",
    "bronze_shovel",
    "bronze_hoe",
}


def load_json(path: Path):
    with path.open(encoding="utf-8") as source:
        return json.load(source)


def ingredient_id(recipe: dict) -> str:
    ingredient = recipe["ingredient"]
    if not isinstance(ingredient, str):
        raise AssertionError(
            "Minecraft 26.2 ingredients must use the string form, "
            f"got {ingredient!r}"
        )
    return ingredient


class ResourceContractTests(unittest.TestCase):
    def test_every_json_resource_parses(self):
        json_files = sorted(RESOURCES.rglob("*.json"))
        self.assertGreater(len(json_files), 0)

        for path in json_files:
            with self.subTest(path=path.relative_to(ROOT)):
                load_json(path)

    def test_every_shipped_item_has_models_and_translations(self):
        english = load_json(ASSETS / "lang" / "en_us.json")
        portuguese = load_json(ASSETS / "lang" / "pt_br.json")

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
        for block in SHIPPED_BLOCKS:
            with self.subTest(block=block):
                self.assertTrue((ASSETS / "blockstates" / f"{block}.json").is_file())
                loot = load_json(DATA / "loot_table" / "blocks" / f"{block}.json")
                self.assertEqual("minecraft:block", loot["type"])

    def test_crushing_recipes_match_two_dust_contract(self):
        recipe_dir = DATA / "recipe"
        actual_names = {
            path.stem for path in recipe_dir.glob("crushing_*.json")
        }
        self.assertEqual(set(CRUSHING_CONTRACTS), actual_names)

        for name, (expected_input, expected_output) in CRUSHING_CONTRACTS.items():
            with self.subTest(recipe=name):
                recipe = load_json(recipe_dir / f"{name}.json")
                self.assertEqual("material_progression:crushing", recipe["type"])
                self.assertEqual(expected_input, ingredient_id(recipe))
                self.assertEqual(
                    {
                        "count": 2,
                        "id": f"material_progression:{expected_output}",
                    },
                    recipe["result"],
                )
                self.assertEqual(200, recipe["cookingtime"])

    def test_smelting_recipes_preserve_material_flow(self):
        recipe_dir = DATA / "recipe"
        for name, (expected_input, expected_output) in SMELTING_CONTRACTS.items():
            with self.subTest(recipe=name):
                recipe = load_json(recipe_dir / f"{name}.json")
                self.assertEqual("minecraft:smelting", recipe["type"])
                self.assertEqual(expected_input, ingredient_id(recipe))
                self.assertEqual({"id": expected_output}, recipe["result"])

    def test_bronze_alloy_recipe_preserves_three_to_one_ratio(self):
        recipe = load_json(DATA / "recipe" / "bronze_dust.json")
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
        tin_repairs = load_json(DATA / "tags" / "item" / "repairs_tin_tools.json")
        bronze_repairs = load_json(
            DATA / "tags" / "item" / "repairs_bronze_tools.json"
        )
        self.assertEqual(["material_progression:tin_ingot"], tin_repairs["values"])
        self.assertEqual(
            ["material_progression:bronze_ingot"], bronze_repairs["values"]
        )

        minecraft_tags = RESOURCES / "data" / "minecraft" / "tags" / "item"
        durability = load_json(minecraft_tags / "enchantable" / "durability.json")
        tagged_tools = {
            value.removeprefix("material_progression:")
            for value in durability["values"]
        }
        self.assertEqual(TIN_TOOLS | BRONZE_TOOLS, tagged_tools)

        mining = load_json(minecraft_tags / "enchantable" / "mining.json")
        mining_tools = {
            value.removeprefix("material_progression:")
            for value in mining["values"]
        }
        self.assertEqual(
            (TIN_TOOLS | BRONZE_TOOLS) - {"tin_sword", "bronze_sword"},
            mining_tools,
        )

    def test_crusher_inputs_tag_matches_crushing_recipe_inputs(self):
        crusher_inputs = load_json(DATA / "tags" / "item" / "crusher_inputs.json")
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
        configured = load_json(DATA / "worldgen" / "configured_feature" / "tin_ore.json")
        placed = load_json(DATA / "worldgen" / "placed_feature" / "tin_ore.json")
        modifier = load_json(DATA / "neoforge" / "biome_modifier" / "add_tin_ore.json")

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


if __name__ == "__main__":
    unittest.main()
