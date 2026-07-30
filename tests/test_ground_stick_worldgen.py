import json
import unittest
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
DATA = ROOT / "src" / "main" / "resources" / "data" / "material_progression"


def load_json(path: Path):
    return json.loads(path.read_text(encoding="utf-8"))


class GroundStickWorldgenContracts(unittest.TestCase):
    def test_ground_sticks_use_the_bounded_vegetation_feature(self):
        configured = load_json(
            DATA / "worldgen" / "configured_feature" / "ground_stick.json"
        )
        self.assertEqual("material_progression:ground_stick", configured["type"])
        self.assertEqual(
            {
                "to_place": {
                    "type": "minecraft:simple_state_provider",
                    "state": {"Name": "material_progression:ground_stick"},
                },
                "anchor_tag": "#material_progression:ground_stick_anchors",
                "attempts": 12,
                "horizontal_spread": 7,
                "surface_vertical_range": 4,
                "anchor_horizontal_radius": 5,
                "anchor_vertical_radius": 3,
                "near_chance": 0.55,
                "background_chance": 0.02,
            },
            configured["config"],
        )

        placed = load_json(
            DATA / "worldgen" / "placed_feature" / "ground_stick.json"
        )
        self.assertEqual(
            "material_progression:ground_stick",
            placed["feature"],
        )
        self.assertEqual(
            [
                {"type": "minecraft:in_square"},
                {
                    "type": "minecraft:heightmap",
                    "heightmap": "MOTION_BLOCKING_NO_LEAVES",
                },
                {"type": "minecraft:biome"},
            ],
            placed["placement"],
        )

    def test_anchor_tag_contains_only_tree_and_shrub_boundaries(self):
        anchors = load_json(
            DATA / "tags" / "block" / "ground_stick_anchors.json"
        )
        self.assertFalse(anchors["replace"])
        self.assertEqual(
            [
                "#minecraft:overworld_natural_logs",
                "#minecraft:leaves",
                "minecraft:azalea",
                "minecraft:flowering_azalea",
                "minecraft:sweet_berry_bush",
                "minecraft:bush",
                "minecraft:firefly_bush",
                "minecraft:dead_bush",
            ],
            anchors["values"],
        )
        serialized = json.dumps(anchors)
        self.assertNotIn("#minecraft:flowers", serialized)
        self.assertNotIn("#minecraft:crops", serialized)

    def test_ground_resources_publish_mutual_worldgen_exclusion(self):
        path = DATA / "tags" / "block" / "ground_resources.json"
        self.assertTrue(
            path.is_file(),
            "ground-resource exclusion tag is missing",
        )
        resources = load_json(path)
        self.assertEqual(
            {
                "replace": False,
                "values": [
                    "material_progression:loose_rocks",
                    "material_progression:external_loose_rocks",
                    "material_progression:ground_stick",
                ],
            },
            resources,
        )

    def test_ground_stick_worldgen_is_overworld_only(self):
        modifier_directory = DATA / "neoforge" / "biome_modifier"
        ground_stick_modifiers = sorted(
            path.name
            for path in modifier_directory.glob("*.json")
            if "ground_stick" in path.read_text(encoding="utf-8")
        )
        self.assertEqual(["add_ground_stick.json"], ground_stick_modifiers)

        modifier = load_json(modifier_directory / "add_ground_stick.json")
        self.assertEqual(
            {
                "type": "neoforge:add_features",
                "biomes": "#minecraft:is_overworld",
                "features": "material_progression:ground_stick",
                "step": "vegetal_decoration",
            },
            modifier,
        )

    def test_ground_stick_remains_world_only_with_one_stick_loot(self):
        loot = load_json(DATA / "loot_table" / "blocks" / "ground_stick.json")
        item_entries = [
            entry
            for pool in loot["pools"]
            for entry in pool["entries"]
            if entry.get("type") == "minecraft:item"
        ]
        self.assertEqual(
            [{"type": "minecraft:item", "name": "minecraft:stick"}],
            item_entries,
        )

    def test_living_docs_record_tunable_density_and_no_release_blocker(self):
        primitive = (
            ROOT / "docs" / "PRIMITIVE_RESOURCES.md"
        ).read_text(encoding="utf-8")
        roadmap = (ROOT / "docs" / "ROADMAP.md").read_text(encoding="utf-8")
        testing = (ROOT / "docs" / "TESTING.md").read_text(encoding="utf-8")
        readme = (ROOT / "README.md").read_text(encoding="utf-8")
        agents = (ROOT / "AGENTS.md").read_text(encoding="utf-8")

        for value in (
            "12 candidate attempts",
            "seven-block candidate spread",
            "five horizontal blocks",
            "three vertical blocks",
            "55 percent",
            "2 percent",
            "`#material_progression:ground_stick_anchors`",
        ):
            self.assertIn(value, primitive)
        self.assertIn("GroundStickFeatureGameTests", testing)

        stale_phrases = (
            "Tree/shrub-aware Ground Stick density is a required pre-0.2.0",
            "Ground Sticks still use broad clustered Overworld placement",
            "Tree/shrub-aware Ground Stick density",
        )
        for text in (primitive, roadmap, readme, agents):
            for phrase in stale_phrases:
                self.assertNotIn(phrase, text)


if __name__ == "__main__":
    unittest.main()
