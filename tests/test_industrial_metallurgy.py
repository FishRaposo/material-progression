import json
import unittest
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
RESOURCES = ROOT / "src" / "main" / "resources"


INDUSTRIAL_MATERIALS = {
    "copper", "tin", "zinc", "lead", "nickel", "silver",
    "bronze", "steel", "brass", "invar", "rose_gold",
}
ORE_MATERIALS = {"copper", "tin", "zinc", "lead", "nickel", "silver"}
OVERWORLD_HOSTS = {
    "stone", "granite", "diorite", "andesite", "deepslate", "tuff",
    "calcite", "dripstone", "sulfur", "cinnabar", "sandstone", "red_sandstone",
}
NETher_HOSTS = {"netherrack", "basalt", "blackstone"}


class IndustrialMetallurgyContractTests(unittest.TestCase):
    """The next release must keep the industrial catalog coherent as it grows."""

    def test_material_catalogue_and_ore_forms_are_published(self):
        source = (ROOT / "src" / "main" / "java" / "dev" / "fishraposo"
                  / "materialprogression" / "registry" / "ModMaterials.java")
        self.assertTrue(source.is_file(), "central public material catalogue is missing")
        contents = source.read_text(encoding="utf-8")
        for material in INDUSTRIAL_MATERIALS:
            self.assertIn('"' + material + '"', contents)
        for material in ORE_MATERIALS:
            self.assertIn('"' + material + '"', contents)
            self.assertTrue(
                (RESOURCES / "data" / "c" / "tags" / "item" / "ores" / f"{material}.json").is_file(),
                material,
            )
            self.assertTrue(
                (RESOURCES / "data" / "material_progression" / "worldgen" / "configured_feature" / f"{material}_ore.json").is_file(),
                material,
            )

    def test_every_new_ore_has_gravel_and_host_forms(self):
        blockstates = ROOT / "src" / "main" / "resources" / "assets" / "material_progression" / "blockstates"
        for material in ORE_MATERIALS:
            for host in OVERWORLD_HOSTS:
                identifier = f"{material}_ore" if host == "stone" else f"{host}_{material}_ore"
                self.assertTrue((blockstates / f"{identifier}.json").is_file(), identifier)
            self.assertTrue((blockstates / f"gravel_{material}_ore.json").is_file())

    def test_grinder_recipe_is_early_and_compatible(self):
        recipe = json.loads((RESOURCES / "data" / "material_progression" / "recipe" / "crusher.json").read_text(encoding="utf-8"))
        self.assertEqual("#c:cobblestones", recipe["key"]["C"])
        self.assertEqual("#minecraft:planks", recipe["key"]["P"])
        self.assertEqual(["CCC", "CPC", "CCC"], recipe["pattern"])

    def test_sulfur_coke_is_optional_fuel_path(self):
        recipes = RESOURCES / "data" / "material_progression" / "recipe"
        self.assertTrue((recipes / "crushing_sulfur_rock.json").is_file())
        self.assertTrue((recipes / "crushing_coal.json").is_file())
        self.assertTrue((recipes / "sulfur_coke_dust.json").is_file())
        self.assertTrue((recipes / "smelting_sulfur_coke_dust.json").is_file())

    def test_every_industrial_material_has_full_tool_and_armor_family(self):
        assets = ROOT / "src" / "main" / "resources" / "assets" / "material_progression" / "items"
        recipes = RESOURCES / "data" / "material_progression" / "recipe"
        for material in INDUSTRIAL_MATERIALS | {"flint", "wood", "stone"}:
            roles = ("knife", "hammer", "saw", "hatchet") if material in {"wood", "stone"} else ("sword", "pickaxe", "axe", "shovel", "hoe", "knife", "hammer", "saw", "hatchet")
            for role in roles + ("helmet", "chestplate", "leggings", "boots"):
                identifier = f"{material}_{role}"
                self.assertTrue((assets / f"{identifier}.json").is_file(), identifier)
                self.assertTrue((recipes / f"{identifier}.json").is_file(), identifier)


if __name__ == "__main__":
    unittest.main()
