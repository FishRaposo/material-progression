import unittest
from pathlib import Path

from content_contracts import (
    STONE_FAMILIES,
    WORKSHOP_PLANT_TAGS,
    WORKSHOP_RECIPES,
    WORKSHOP_WOOD_RECIPES,
)
from support.resources import ResourceTree


ROOT = Path(__file__).resolve().parents[1]
TREE = ResourceTree(ROOT, "material_progression")
DATA = TREE.data
ASSETS = TREE.assets
JAVA = (
    ROOT
    / "src"
    / "main"
    / "java"
    / "dev"
    / "fishraposo"
    / "materialprogression"
)


class WorkshopResourceContractTests(unittest.TestCase):
    def test_manual_workshop_recipe_catalog_is_exact(self):
        actual = TREE.names_matching(
            DATA / "recipe",
            "manual_workshop_*.json",
        )
        self.assertEqual(set(WORKSHOP_RECIPES), actual)

        for name, fields in WORKSHOP_RECIPES.items():
            with self.subTest(recipe=name):
                self.assertEqual(
                    {"type": "material_progression:manual_workshop"} | fields,
                    TREE.recipe(name),
                )

    def test_manual_workshop_plant_interfaces_are_narrow_and_published(self):
        for tier, expected in WORKSHOP_PLANT_TAGS.items():
            with self.subTest(tier=tier):
                payload = TREE.load_json(
                    DATA / "tags" / "item" / "workshop_plants"
                    / f"{tier}.json"
                )
                self.assertEqual(
                    {"replace": False, "values": expected},
                    payload,
                )

        all_members = {
            member
            for members in WORKSHOP_PLANT_TAGS.values()
            for member in members
        }
        self.assertNotIn("#minecraft:flowers", all_members)
        self.assertNotIn("#minecraft:logs", all_members)

    def test_only_vanilla_stone_and_cobblestone_crush_to_gravel(self):
        tag = TREE.load_json(
            DATA / "tags" / "item" / "workshop_stone_to_gravel.json"
        )
        self.assertEqual(
            {
                "replace": False,
                "values": ["minecraft:stone", "minecraft:cobblestone"],
            },
            tag,
        )

        encoded = str(WORKSHOP_RECIPES)
        for family, contract in STONE_FAMILIES.items():
            if family == "stone":
                continue
            with self.subTest(family=family):
                self.assertNotIn(contract["raw_block"], encoded)
                self.assertNotIn(contract["cobbled_block"], encoded)

    def test_saw_recipes_preserve_every_vanilla_wood_family(self):
        self.assertEqual(
            {
                "oak",
                "spruce",
                "birch",
                "jungle",
                "acacia",
                "dark_oak",
                "pale_oak",
                "mangrove",
                "cherry",
                "crimson",
                "warped",
                "bamboo",
            },
            set(WORKSHOP_WOOD_RECIPES),
        )

        for family, (ingredient, result, count) in (
            WORKSHOP_WOOD_RECIPES.items()
        ):
            with self.subTest(family=family):
                recipe = TREE.recipe(f"manual_workshop_{family}_logs")
                self.assertEqual(ingredient, recipe["ingredient"])
                self.assertEqual(result, recipe["result"]["id"])
                self.assertEqual(count, recipe["result"]["count"])
                self.assertEqual(100, recipe["processing_time"])
                self.assertEqual(2, recipe["tool_damage"])

        self.assertEqual(
            (
                "#minecraft:bamboo_blocks",
                "minecraft:bamboo_planks",
                3,
            ),
            WORKSHOP_WOOD_RECIPES["bamboo"],
            "Bamboo keeps the same 50% efficiency bonus as four-to-six logs",
        )

    def test_workshop_ore_routes_match_current_crusher_compatibility(self):
        expected = {
            "#c:ores/copper": "material_progression:copper_dust",
            "#c:raw_materials/copper": "material_progression:copper_dust",
            "#c:ores/tin": "material_progression:tin_dust",
            "#c:raw_materials/tin": "material_progression:tin_dust",
        }
        actual = {
            fields["ingredient"]: fields["result"]["id"]
            for fields in WORKSHOP_RECIPES.values()
            if fields["processing_time"] == 160
        }
        self.assertEqual(expected, actual)
        for fields in WORKSHOP_RECIPES.values():
            if fields["processing_time"] == 160:
                self.assertEqual(2, fields["result"]["count"])
                self.assertEqual("#c:tools/hammers", fields["tool"])
                self.assertEqual(8, fields["tool_damage"])

    def test_workshop_has_complete_block_resources(self):
        self.assertTrue(
            (ASSETS / "blockstates" / "manual_workshop.json").is_file()
        )
        self.assertTrue(
            (ASSETS / "models" / "block" / "manual_workshop.json").is_file()
        )
        self.assertTrue(
            (ASSETS / "items" / "manual_workshop.json").is_file()
        )
        self.assertTrue(
            (
                DATA
                / "loot_table"
                / "blocks"
                / "manual_workshop.json"
            ).is_file()
        )
        for language in ("en_us", "pt_br"):
            translations = TREE.load_json(
                ASSETS / "lang" / f"{language}.json"
            )
            self.assertIn(
                "block.material_progression.manual_workshop",
                translations,
            )
            self.assertIn(
                "container.material_progression.manual_workshop",
                translations,
            )


class WorkshopJavaContractTests(unittest.TestCase):
    def test_workshop_runtime_layers_are_registered(self):
        expected_files = {
            "world/item/crafting/ManualWorkshopRecipe.java",
            "world/item/crafting/ManualWorkshopRecipeInput.java",
            "world/level/block/ManualWorkshopBlock.java",
            "world/level/block/entity/ManualWorkshopBlockEntity.java",
            "world/inventory/ManualWorkshopMenu.java",
            "client/ManualWorkshopScreen.java",
            "client/ManualWorkshopRenderer.java",
            "client/ManualWorkshopRenderState.java",
        }
        for relative in expected_files:
            with self.subTest(source=relative):
                self.assertTrue((JAVA / relative).is_file())

        registrations = {
            "registry/ModBlocks.java": '"manual_workshop"',
            "registry/ModItems.java": '"manual_workshop"',
            "registry/ModBlockEntities.java": '"manual_workshop"',
            "registry/ModMenus.java": '"manual_workshop"',
            "registry/ModRecipes.java": '"manual_workshop"',
            "MaterialProgressionClient.java": (
                "EntityRenderersEvent.RegisterRenderers"
            ),
        }
        for relative, needle in registrations.items():
            with self.subTest(source=relative):
                text = (JAVA / relative).read_text(encoding="utf-8")
                self.assertIn(needle, text)

    def test_workshop_recipe_uses_public_codecs_and_ingredients(self):
        source = (
            JAVA
            / "world"
            / "item"
            / "crafting"
            / "ManualWorkshopRecipe.java"
        ).read_text(encoding="utf-8")
        for needle in (
            "Ingredient ingredient",
            "Ingredient tool",
            "ItemStackTemplate result",
            "MAP_CODEC",
            "STREAM_CODEC",
            "processingTime",
            "toolDamage",
        ):
            with self.subTest(needle=needle):
                self.assertIn(needle, source)

    def test_workshop_explicitly_rejects_sided_automation(self):
        source = (
            JAVA
            / "world"
            / "level"
            / "block"
            / "entity"
            / "ManualWorkshopBlockEntity.java"
        ).read_text(encoding="utf-8")
        self.assertIn("implements WorldlyContainer", source)
        self.assertIn("NO_SLOTS", source)
        self.assertIn("getSlotsForFace", source)
        self.assertIn("canPlaceItemThroughFace", source)
        self.assertIn("canTakeItemThroughFace", source)


if __name__ == "__main__":
    unittest.main()
