import unittest
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
MAIN_JAVA = ROOT / "src" / "main" / "java"
MAIN_RESOURCES = ROOT / "src" / "main" / "resources"
GAME_TEST = ROOT / "src" / "gameTest"


class ThirdPartyStoneFamilyContracts(unittest.TestCase):
    def test_catalog_is_keyed_by_resource_ids_without_enum_rejection(self):
        catalog = (
            MAIN_JAVA
            / "dev"
            / "fishraposo"
            / "materialprogression"
            / "stone"
            / "StoneFamilyCatalog.java"
        ).read_text(encoding="utf-8")
        self.assertIn("Map<Identifier, Entry>", catalog)
        self.assertIn("Optional<StoneFamily> builtInFamily", catalog)
        self.assertNotIn("not one of the 16 supported block-state families", catalog)
        self.assertIn("Rock item tag must resolve to exactly one item", catalog)
        self.assertIn("must also belong to #c:rocks", catalog)
        self.assertIn('"source block"', catalog)
        self.assertIn('"direct surface"', catalog)
        self.assertIn('" is already owned by "', catalog)

    def test_resistance_modifier_is_the_canonical_additive_tier_shift(self):
        definition = (
            MAIN_JAVA
            / "dev"
            / "fishraposo"
            / "materialprogression"
            / "stone"
            / "StoneFamilyDefinition.java"
        ).read_text(encoding="utf-8")
        resolver = (
            MAIN_JAVA
            / "dev"
            / "fishraposo"
            / "materialprogression"
            / "stone"
            / "GeologyTierResolver.java"
        ).read_text(encoding="utf-8")
        self.assertIn("record Resistance(int modifier)", definition)
        self.assertIn("Codec.INT", definition)
        self.assertIn("range -3 through 3", definition)
        self.assertNotIn('.fieldOf("tier")', definition)
        self.assertIn("entry.resistance().modifier()", resolver)
        self.assertIn("base + familyModifier", resolver)

    def test_external_loose_rocks_use_a_persistent_synced_block_entity(self):
        block = (
            MAIN_JAVA
            / "dev"
            / "fishraposo"
            / "materialprogression"
            / "world"
            / "level"
            / "block"
            / "ExternalLooseRocksBlock.java"
        )
        block_entity = (
            MAIN_JAVA
            / "dev"
            / "fishraposo"
            / "materialprogression"
            / "world"
            / "level"
            / "block"
            / "entity"
            / "ExternalLooseRockBlockEntity.java"
        )
        renderer = (
            MAIN_JAVA
            / "dev"
            / "fishraposo"
            / "materialprogression"
            / "client"
            / "ExternalLooseRockRenderer.java"
        )
        self.assertTrue(block.exists())
        self.assertTrue(block_entity.exists())
        self.assertTrue(renderer.exists())
        entity_source = block_entity.read_text(encoding="utf-8")
        self.assertIn('output.store("Family"', entity_source)
        self.assertIn('output.store("Rock"', entity_source)
        self.assertIn("getUpdatePacket()", entity_source)
        self.assertIn("getUpdateTag(", entity_source)

        blocks = (
            MAIN_JAVA
            / "dev"
            / "fishraposo"
            / "materialprogression"
            / "registry"
            / "ModBlocks.java"
        ).read_text(encoding="utf-8")
        items = (
            MAIN_JAVA
            / "dev"
            / "fishraposo"
            / "materialprogression"
            / "registry"
            / "ModItems.java"
        ).read_text(encoding="utf-8")
        self.assertIn('registerBlock(\n                    "external_loose_rocks"', blocks)
        self.assertNotIn('"external_loose_rocks"', items)

        self.assertTrue(
            (
                MAIN_RESOURCES
                / "assets"
                / "material_progression"
                / "blockstates"
                / "external_loose_rocks.json"
            ).exists()
        )
        self.assertTrue(
            (
                MAIN_RESOURCES
                / "data"
                / "material_progression"
                / "loot_table"
                / "blocks"
                / "external_loose_rocks.json"
            ).exists()
        )

    def test_test_family_is_development_only(self):
        definition = (
            GAME_TEST
            / "resources"
            / "data"
            / "material_progression_gametests"
            / "stone_family"
            / "slate.json"
        )
        self.assertTrue(definition.exists())
        main_text = "\n".join(
            path.read_text(encoding="utf-8", errors="ignore")
            for path in MAIN_JAVA.rglob("*")
            if path.is_file()
        )
        self.assertNotIn("material_progression_gametests:slate", main_text)
        self.assertNotIn("EXTERNAL_RAW_STONE", main_text)

    def test_public_docs_describe_supported_external_families(self):
        compatibility = (ROOT / "docs" / "COMPATIBILITY.md").read_text(
            encoding="utf-8"
        )
        geology = (ROOT / "docs" / "GEOLOGY.md").read_text(encoding="utf-8")
        self.assertIn("arbitrary third-party family IDs", compatibility)
        self.assertIn("exactly one registered item", compatibility)
        self.assertIn("external_loose_rocks", compatibility)
        self.assertIn("transactional", compatibility)
        self.assertNotIn("not yet fully extensible", compatibility)
        self.assertIn(
            "arbitrary third-party family registration",
            geology.lower(),
        )
        self.assertIn(
            "accepts arbitrary additional namespaced ids",
            geology.lower(),
        )


if __name__ == "__main__":
    unittest.main()
