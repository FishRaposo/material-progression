import json
import unittest
from pathlib import Path

from content_contracts import STONE_FAMILIES, WORKSHOP_RECIPES
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


PROGRESSION_ADVANCEMENTS = {
    "first_rock": {
        "parent": None,
        "icon": "material_progression:rock",
        "criterion": "has_rock",
        "trigger": "minecraft:inventory_changed",
        "items": "#c:rocks",
    },
    "flint_hatchet": {
        "parent": "material_progression:progression/first_rock",
        "icon": "material_progression:flint_hatchet",
        "criterion": "has_flint_hatchet",
        "trigger": "minecraft:inventory_changed",
        "items": "material_progression:flint_hatchet",
    },
    "manual_workshop": {
        "parent": "material_progression:progression/flint_hatchet",
        "icon": "material_progression:manual_workshop",
        "criterion": "has_manual_workshop",
        "trigger": "minecraft:inventory_changed",
        "items": "material_progression:manual_workshop",
    },
    "dense_geology": {
        "parent": "material_progression:progression/manual_workshop",
        "icon": "material_progression:cinnabar_rock",
        "criterion": "dense_geology",
        "trigger": "minecraft:impossible",
        "items": None,
    },
    "bronze_access": {
        "parent": "material_progression:progression/manual_workshop",
        "icon": "material_progression:bronze_ingot",
        "criterion": "has_bronze",
        "trigger": "minecraft:inventory_changed",
        "items": "#c:ingots/bronze",
    },
}

RECIPE_UNLOCKS = {
    "primitive/flint_shard_from_flint": {
        "items": ["minecraft:flint"],
        "recipes": ["material_progression:flint_shard_from_flint"],
    },
    "primitive/rocks": {
        "items": ["#c:rocks"],
        "recipes": [
            "material_progression:flint_shard_from_rock",
            "material_progression:rock_cobbling",
        ],
    },
    "primitive/flint_tools": {
        "items": ["#c:flint_shards", "#c:rods/wooden"],
        "recipes": [
            "material_progression:flint_hatchet",
            "material_progression:flint_knife",
            "material_progression:flint_hammer",
            "material_progression:flint_saw",
        ],
    },
    "primitive/plant_fiber": {
        "items": ["#c:fibers/plant"],
        "recipes": ["material_progression:plant_fiber_to_string"],
    },
    "primitive/manual_workshop": {
        "items": ["#minecraft:planks", "#c:cobblestones"],
        "recipes": ["material_progression:manual_workshop"],
    },
    "primitive/bronze_tools": {
        "items": ["#c:ingots/bronze", "#c:rods/wooden"],
        "recipes": [
            "material_progression:bronze_knife",
            "material_progression:bronze_hammer",
            "material_progression:bronze_saw",
        ],
    },
    "workshop/knife_operations": {
        "items": [
            "material_progression:manual_workshop",
            "#c:tools/knives",
        ],
        "recipes": [
            "material_progression:manual_workshop_rock_sharpening",
            "material_progression:manual_workshop_plant_fiber_1",
            "material_progression:manual_workshop_plant_fiber_2",
            "material_progression:manual_workshop_plant_fiber_3",
            "material_progression:manual_workshop_plant_fiber_5",
        ],
    },
    "workshop/hammer_operations": {
        "items": [
            "material_progression:manual_workshop",
            "#c:tools/hammers",
        ],
        "recipes": [
            "material_progression:manual_workshop_stone_to_gravel",
            "material_progression:manual_workshop_gravel_to_sand",
            "material_progression:manual_workshop_copper_ore",
            "material_progression:manual_workshop_raw_copper",
            "material_progression:manual_workshop_tin_ore",
            "material_progression:manual_workshop_raw_tin",
        ],
    },
    "workshop/saw_operations": {
        "items": [
            "material_progression:manual_workshop",
            "#c:tools/saws",
        ],
        "recipes": [
            "material_progression:manual_workshop_planks_to_sticks",
            *sorted(
                f"material_progression:{name}"
                for name in WORKSHOP_RECIPES
                if name.endswith("_logs")
            ),
        ],
    },
}


class DiscoverabilityContractTests(unittest.TestCase):
    def test_progression_advancements_have_exact_triggers_and_chain(self):
        advancement_dir = DATA / "advancement" / "progression"
        self.assertEqual(
            set(PROGRESSION_ADVANCEMENTS),
            TREE.names_matching(advancement_dir, "*.json"),
        )

        for name, expected in PROGRESSION_ADVANCEMENTS.items():
            with self.subTest(advancement=name):
                payload = TREE.load_json(advancement_dir / f"{name}.json")
                if expected["parent"] is None:
                    self.assertNotIn("parent", payload)
                    self.assertEqual(
                        "minecraft:gui/advancements/backgrounds/stone",
                        payload["display"]["background"],
                    )
                else:
                    self.assertEqual(expected["parent"], payload["parent"])
                self.assertEqual(expected["icon"], payload["display"]["icon"]["id"])
                self.assertEqual(
                    expected["trigger"],
                    payload["criteria"][expected["criterion"]]["trigger"],
                )
                if expected["items"] is not None:
                    self.assertEqual(
                        expected["items"],
                        payload["criteria"][expected["criterion"]][
                            "conditions"
                        ]["items"][0]["items"],
                    )
                else:
                    self.assertNotIn(
                        "conditions",
                        payload["criteria"][expected["criterion"]],
                    )
                self.assertEqual(
                    [[expected["criterion"]]],
                    payload["requirements"],
                )
                self.assertEqual(
                    {
                        "translate": (
                            f"advancement.material_progression."
                            f"{name}.title"
                        )
                    },
                    payload["display"]["title"],
                )
                self.assertEqual(
                    {
                        "translate": (
                            f"advancement.material_progression."
                            f"{name}.description"
                        )
                    },
                    payload["display"]["description"],
                )

    def test_recipe_unlock_advancements_are_input_driven_and_complete(self):
        actual_paths = {
            path.relative_to(DATA / "advancement" / "recipes")
            .with_suffix("")
            .as_posix()
            for path in (DATA / "advancement" / "recipes").rglob("*.json")
        }
        self.assertEqual(set(RECIPE_UNLOCKS), actual_paths)

        rewarded_workshop_recipes = set()
        for name, expected in RECIPE_UNLOCKS.items():
            with self.subTest(recipe_unlock=name):
                payload = TREE.load_json(
                    DATA / "advancement" / "recipes" / f"{name}.json"
                )
                self.assertEqual("minecraft:recipes/root", payload["parent"])
                self.assertNotIn("display", payload)
                self.assertEqual(
                    expected["recipes"],
                    payload["rewards"]["recipes"],
                )
                self.assertEqual(
                    len(expected["items"]),
                    len(payload["criteria"]),
                )
                self.assertEqual(
                    [[criterion] for criterion in payload["criteria"]],
                    payload["requirements"],
                )
                actual_items = []
                for criterion in payload["criteria"].values():
                    self.assertEqual(
                        "minecraft:inventory_changed",
                        criterion["trigger"],
                    )
                    actual_items.append(
                        criterion["conditions"]["items"][0]["items"]
                    )
                self.assertEqual(expected["items"], actual_items)
                rewarded_workshop_recipes.update(
                    recipe.removeprefix("material_progression:")
                    for recipe in expected["recipes"]
                    if recipe.startswith(
                        "material_progression:manual_workshop_"
                    )
                )

        self.assertEqual(set(WORKSHOP_RECIPES), rewarded_workshop_recipes)

    def test_tooltips_feedback_and_advancements_are_fully_localized(self):
        expected_keys = {
            "tooltip.material_progression.rock",
            "tooltip.material_progression.flint_shard",
            "tooltip.material_progression.plant_fiber",
            "tooltip.material_progression.flint_hatchet",
            "tooltip.material_progression.knife",
            "tooltip.material_progression.hammer",
            "tooltip.material_progression.saw",
            "tooltip.material_progression.manual_workshop",
            "message.material_progression.log.requires_tool",
            "message.material_progression.geology.insufficient",
            *{
                f"message.material_progression.geology.tier.{level}"
                for level in range(4)
            },
            *{
                f"message.material_progression.geology.capability.{level}"
                for level in range(4)
            },
            *{
                f"stone_family.material_progression.{family}"
                for family in STONE_FAMILIES
            },
            *{
                f"advancement.material_progression.{advancement}.{part}"
                for advancement in PROGRESSION_ADVANCEMENTS
                for part in ("title", "description")
            },
        }
        for language in ("en_us", "pt_br"):
            translations = TREE.load_json(
                ASSETS / "lang" / f"{language}.json"
            )
            with self.subTest(language=language):
                self.assertTrue(expected_keys.issubset(translations))
                self.assertTrue(
                    all(translations[key].strip() for key in expected_keys)
                )
                self.assertEqual(
                    1,
                    translations[
                        "message.material_progression.geology.insufficient"
                    ].count("%1$s"),
                )
                self.assertEqual(
                    1,
                    translations[
                        "message.material_progression.geology.insufficient"
                    ].count("%2$s"),
                )
                self.assertEqual(
                    1,
                    translations[
                        "message.material_progression.geology.insufficient"
                    ].count("%3$s"),
                )
                self.assertIn(
                    "Saw" if language == "en_us" else "Serra",
                    translations[
                        "message.material_progression.log.requires_tool"
                    ],
                )

    def test_items_publish_the_required_localized_lore(self):
        source = (JAVA / "registry" / "ModItems.java").read_text(
            encoding="utf-8"
        )
        for key in (
            "tooltip.material_progression.rock",
            "tooltip.material_progression.flint_shard",
            "tooltip.material_progression.plant_fiber",
            "tooltip.material_progression.flint_hatchet",
            "tooltip.material_progression.knife",
            "tooltip.material_progression.hammer",
            "tooltip.material_progression.saw",
            "tooltip.material_progression.manual_workshop",
        ):
            with self.subTest(tooltip=key):
                self.assertIn(key, source)
        self.assertIn("DataComponents.LORE", source)
        self.assertIn("ItemLore", source)

    def test_workshop_uses_a_dedicated_recipe_book_category(self):
        recipe_source = (
            JAVA
            / "world"
            / "item"
            / "crafting"
            / "ManualWorkshopRecipe.java"
        ).read_text(encoding="utf-8")
        registrations = (JAVA / "registry" / "ModRecipes.java").read_text(
            encoding="utf-8"
        )
        self.assertIn("MANUAL_WORKSHOP_CATEGORY", recipe_source)
        self.assertIn("MANUAL_WORKSHOP_CATEGORY", registrations)
        self.assertNotIn(
            "return RecipeBookCategories.CRAFTING_MISC",
            recipe_source,
        )

    def test_no_custom_guidebook_is_shipped(self):
        shipped = json.dumps(
            {
                path.as_posix(): path.read_text(encoding="utf-8")
                for path in (
                    ROOT / "src" / "main" / "resources"
                ).rglob("*.json")
            }
        ).lower()
        self.assertNotIn("guidebook", shipped)


if __name__ == "__main__":
    unittest.main()
