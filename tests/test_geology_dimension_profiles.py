import unittest
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
MAIN_JAVA = (
    ROOT
    / "src"
    / "main"
    / "java"
    / "dev"
    / "fishraposo"
    / "materialprogression"
)


class GeologyDimensionProfileContracts(unittest.TestCase):
    def test_public_profile_schema_and_reload_listener_are_registered(self):
        definition = (
            MAIN_JAVA
            / "stone"
            / "GeologyDimensionProfileDefinition.java"
        )
        catalog = (
            MAIN_JAVA
            / "stone"
            / "GeologyDimensionProfileCatalog.java"
        )
        listener = (
            MAIN_JAVA
            / "stone"
            / "GeologyDimensionProfileReloadListener.java"
        )
        self.assertTrue(definition.is_file())
        self.assertTrue(catalog.is_file())
        self.assertTrue(listener.is_file())

        definition_text = definition.read_text(encoding="utf-8")
        self.assertIn("record GeologyDimensionProfileDefinition", definition_text)
        self.assertIn('fieldOf("dimension")', definition_text)
        self.assertIn('fieldOf("bands")', definition_text)
        self.assertIn('optionalFieldOf("minimum_y")', definition_text)
        self.assertIn("Codec.intRange(0, 3)", definition_text)
        self.assertIn("strictly descending", definition_text)
        self.assertIn("catch-all", definition_text)

        listener_text = listener.read_text(encoding="utf-8")
        self.assertIn(
            'FileToIdConverter.json("geology_dimension_profile")',
            listener_text,
        )
        self.assertIn("AddServerReloadListenersEvent", listener_text)
        self.assertIn("ServerStoppedEvent", listener_text)
        self.assertIn("GeologyDimensionProfileCatalog.install", listener_text)
        self.assertIn("GeologyDimensionProfileCatalog.clear", listener_text)

        mod_text = (MAIN_JAVA / "MaterialProgression.java").read_text(
            encoding="utf-8"
        )
        self.assertIn("GeologyDimensionProfileReloadListener.register()", mod_text)

    def test_catalog_is_transactional_and_targets_unique_custom_dimensions(self):
        catalog = (
            MAIN_JAVA
            / "stone"
            / "GeologyDimensionProfileCatalog.java"
        ).read_text(encoding="utf-8")
        listener = (
            MAIN_JAVA
            / "stone"
            / "GeologyDimensionProfileReloadListener.java"
        ).read_text(encoding="utf-8")

        self.assertIn("Map<Identifier, Profile>", catalog)
        self.assertIn("duplicate dimension", catalog.lower())
        self.assertIn("cannot override built-in dimension", catalog.lower())
        self.assertIn("Map.copyOf", catalog)
        self.assertIn("GeologyDimensionProfileCatalog.build", listener)
        self.assertIn("GeologyDimensionProfileCatalog.install", listener)

    def test_resolver_preserves_built_ins_and_uses_custom_profile_fallback(self):
        resolver = (
            MAIN_JAVA / "stone" / "GeologyTierResolver.java"
        ).read_text(encoding="utf-8")

        for boundary in (
            "y > 48",
            "y >= 17",
            "y >= -15",
            "y >= 96",
            "y >= 64",
            "y >= 32",
        ):
            with self.subTest(boundary=boundary):
                self.assertIn(boundary, resolver)

        self.assertIn("dimension == Level.END", resolver)
        self.assertIn("return 2;", resolver)
        self.assertIn("GeologyDimensionProfileCatalog.baseLevel", resolver)
        self.assertIn("base + familyModifier - (exposed ? 1 : 0)", resolver)
        self.assertLess(
            resolver.index("PlacedRawStoneTracker.isMarked"),
            resolver.index("naturalTier("),
        )

    def test_mining_and_feedback_consume_the_live_resolver(self):
        mining = (
            MAIN_JAVA / "stone" / "GeologyMiningEvents.java"
        ).read_text(encoding="utf-8")
        feedback = (
            MAIN_JAVA / "stone" / "GeologyFeedbackEvents.java"
        ).read_text(encoding="utf-8")
        live_tests = (
            ROOT
            / "src"
            / "gameTest"
            / "java"
            / "dev"
            / "fishraposo"
            / "materialprogression"
            / "gametest"
            / "GeologyMiningGameTests.java"
        ).read_text(encoding="utf-8")

        self.assertGreaterEqual(mining.count("GeologyTierResolver.resolve("), 3)
        self.assertIn("GeologyTierResolver.resolve(", feedback)
        self.assertIn("enableGeologicalHardness", mining)
        self.assertIn("enableStoneRockDrops", mining)
        self.assertIn("dropTogglePreservesVanillaLoot", live_tests)
        self.assertIn("hardnessToggleKeepsRockDrops", live_tests)
        self.assertIn("placedRawStoneFragmentsAndClearsMarker", live_tests)

    def test_public_docs_define_profile_schema_without_stale_blocker_wording(self):
        compatibility = (ROOT / "docs" / "COMPATIBILITY.md").read_text(
            encoding="utf-8"
        )
        geology = (ROOT / "docs" / "GEOLOGY.md").read_text(encoding="utf-8")
        testing = (ROOT / "docs" / "TESTING.md").read_text(encoding="utf-8")
        combined = "\n".join(
            path.read_text(encoding="utf-8")
            for path in (
                ROOT / "AGENTS.md",
                ROOT / "README.md",
                ROOT / "docs" / "ROADMAP.md",
                ROOT / "docs" / "GEOLOGY.md",
                ROOT / "docs" / "COMPATIBILITY.md",
                ROOT / "docs" / "TESTING.md",
            )
        ).lower()

        self.assertIn("geology_dimension_profile", compatibility)
        self.assertIn('"dimension"', compatibility)
        self.assertIn('"bands"', compatibility)
        self.assertIn('"minimum_y"', compatibility)
        self.assertIn("strictly descending", compatibility)
        self.assertIn("catch-all", compatibility)
        self.assertIn("cannot override", compatibility)
        self.assertIn("transactional", compatibility)
        self.assertIn("unconfigured", geology)
        self.assertIn("GeologyDimensionProfileGameTests", testing)

        for stale in (
            "required pre-0.2.0 gap",
            "must be implemented before 0.2.0",
            "required gap in the complete opening/geology pass",
            "required before 0.2.0",
        ):
            with self.subTest(stale=stale):
                self.assertNotIn(stale, combined)


if __name__ == "__main__":
    unittest.main()
