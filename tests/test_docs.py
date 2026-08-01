import re
import unittest
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
MARKDOWN_LINK = re.compile(r"\[[^\]]+\]\(([^)]+)\)")
TEXT_SUFFIXES = {".gradle", ".java", ".json", ".md", ".properties", ".yml", ".yaml"}
IGNORED_DIRECTORIES = {".git", ".gradle", "build", "repo", "run", "research"}


def repository_files():
    for path in ROOT.rglob("*"):
        if not path.is_file():
            continue
        if any(part in IGNORED_DIRECTORIES for part in path.relative_to(ROOT).parts):
            continue
        yield path


class DocumentationContractTests(unittest.TestCase):
    def test_project_name_is_recorded_as_settled(self):
        readme = (ROOT / "README.md").read_text(encoding="utf-8")
        normalized_readme = " ".join(readme.split())

        self.assertIn(
            "The name **Material Progression** is settled.", normalized_readme
        )
        self.assertIn("materials define progression", normalized_readme)
        self.assertNotIn("the name, mod ID", readme)

    def test_bulk_crafter_records_settled_module_upgrade_rules(self):
        infrastructure = (ROOT / "docs" / "INFRASTRUCTURE.md").read_text(
            encoding="utf-8"
        )
        normalized = " ".join(infrastructure.split())

        self.assertIn(
            "Higher-tier modules are crafted by upgrading their lower-tier "
            "predecessors",
            normalized,
        )
        self.assertIn("Binary capabilities do not stack", normalized)
        self.assertIn("Quantity-based capabilities can stack", normalized)
        self.assertNotIn(
            "whether higher-tier modules are crafted from lower-tier modules",
            normalized,
        )

    def test_primitive_opening_records_settled_rock_and_stick_rules(self):
        primitive = (ROOT / "docs" / "PRIMITIVE_RESOURCES.md").read_text(
            encoding="utf-8"
        )
        normalized_primitive = " ".join(primitive.split())
        compatibility = (ROOT / "docs" / "COMPATIBILITY.md").read_text(
            encoding="utf-8"
        )
        living_design = "\n".join(
            path.read_text(encoding="utf-8")
            for path in (
                ROOT / "README.md",
                ROOT / "AGENTS.md",
                ROOT / "docs" / "DESIGN_PRINCIPLES.md",
                ROOT / "docs" / "GEOLOGY.md",
                ROOT / "docs" / "INFRASTRUCTURE.md",
                ROOT / "docs" / "INSPIRATIONS.md",
                ROOT / "docs" / "METALLURGY.md",
                ROOT / "docs" / "PRIMITIVE_RESOURCES.md",
                ROOT / "docs" / "ROADMAP.md",
                ROOT / "docs" / "VISION.md",
            )
        )

        for decision in (
            "1 Rock -> 1 flint shard",
            "1 flint -> 2 flint shards",
            "4 Rocks -> 1 cobblestone",
            "shapeless",
            "2x2",
            "ordinary vanilla stick",
            "persistent ground objects",
            "Leaves remain a renewable fallback",
        ):
            with self.subTest(decision=decision):
                self.assertIn(decision, primitive)

        self.assertIn("The item is **Rock**", primitive)
        self.assertIn("world feature is **loose rocks**", normalized_primitive)
        self.assertIn("`#c:rocks`", compatibility)

        for stale_rule in (
            "loose rock or flint shard",
            "rocks or shards",
            "rock or shard",
            "directly usable as one shard",
            "same recipe role as a flint shard",
            "flint-shard equivalent",
        ):
            with self.subTest(stale_rule=stale_rule):
                self.assertNotIn(stale_rule, living_design.lower())

    def test_primitive_foundations_are_marked_implemented(self):
        handbook = (ROOT / "AGENTS.md").read_text(encoding="utf-8")
        readme = (ROOT / "README.md").read_text(encoding="utf-8")
        primitive = (ROOT / "docs" / "PRIMITIVE_RESOURCES.md").read_text(
            encoding="utf-8"
        )
        roadmap = (ROOT / "docs" / "ROADMAP.md").read_text(encoding="utf-8")
        testing = (ROOT / "docs" / "TESTING.md").read_text(encoding="utf-8")

        for shipped_feature in (
            "Loose-rock and ground-stick world generation",
            "Rock, flint shards, and the flint hatchet",
            "default-enabled log-only axe requirement",
        ):
            with self.subTest(shipped_feature=shipped_feature):
                self.assertIn(shipped_feature, handbook)
                self.assertIn(shipped_feature, readme)

        self.assertIn("**Status: partially implemented.**", primitive)
        self.assertIn("Implemented primitive foundation", roadmap)
        self.assertIn("PrimitiveGameTests", testing)
        self.assertIn("LogHarvestGameTests", testing)

    def test_agent_handbook_and_project_skills_use_plural_directory(self):
        handbook = ROOT / "AGENTS.md"
        compatibility = ROOT / "docs" / "COMPATIBILITY.md"
        skills_root = ROOT / ".agents" / "skills"

        self.assertTrue(handbook.is_file())
        self.assertTrue(compatibility.is_file())
        self.assertFalse((ROOT / ".agents" / "skill").exists())

        expected_skills = {
            "developing-material-progression",
            "releasing-material-progression",
        }
        self.assertEqual(
            expected_skills,
            {
                path.name
                for path in skills_root.iterdir()
                if path.is_dir()
            },
        )
        for skill_name in expected_skills:
            skill_file = skills_root / skill_name / "SKILL.md"
            self.assertTrue(skill_file.is_file())
            contents = skill_file.read_text(encoding="utf-8")
            self.assertIn(f"name: {skill_name}", contents)

        handbook_text = handbook.read_text(encoding="utf-8")
        self.assertIn("docs/COMPATIBILITY.md", handbook_text)
        self.assertIn(".agents/skills/", handbook_text)

    def test_reference_mods_include_project_and_source_links(self):
        inspirations = (ROOT / "docs" / "INSPIRATIONS.md").read_text(
            encoding="utf-8"
        )
        expected_references = {
            "Divergent Underground": (
                "https://www.curseforge.com/minecraft/mc-mods/divergent-underground",
                "https://github.com/cleverpanda/Divergent-Underground",
            ),
            "Metallurgy 4": (
                "https://www.curseforge.com/minecraft/mc-mods/metallurgy-4-reforged",
                "https://github.com/Davoleo/Metallurgy-4-Reforged",
            ),
            "Base Metals": (
                "https://www.curseforge.com/minecraft/mc-mods/base-metals",
                "https://github.com/MinecraftModDevelopmentMods/BaseMetals",
            ),
            "SimpleOres": (
                "https://www.curseforge.com/minecraft/mc-mods/simpleores",
                "https://github.com/Sinhika/SimpleOres2",
            ),
            "Fusion": (
                "https://www.curseforge.com/minecraft/mc-mods/fusion",
                "https://github.com/Sinhika/Fusion",
            ),
            "Bonsai Trees": (
                "https://www.curseforge.com/minecraft/mc-mods/bonsai-trees",
                "https://github.com/davenonymous/BonsaiTrees",
            ),
            "No Tree Punching": (
                "https://www.curseforge.com/minecraft/mc-mods/no-tree-punching",
                "https://github.com/alcatrazEscapee/no-tree-punching",
            ),
            "Furnus": (
                "https://www.curseforge.com/minecraft/mc-mods/furnus",
                "https://github.com/KidsDontPlay/Furnus",
            ),
            "Iron Chests": (
                "https://www.curseforge.com/minecraft/mc-mods/iron-chests",
                "https://github.com/progwml6/ironchest",
            ),
            "CraftingTable IV": (
                "https://www.curseforge.com/minecraft/mc-mods/craftingtable-iv",
                "https://github.com/Elecs-Mods/CraftingTable-IV",
            ),
            "Tinkers' Construct": (
                "https://www.curseforge.com/minecraft/mc-mods/tinkers-construct",
                "https://github.com/SlimeKnights/TinkersConstruct",
            ),
            "Pyrotech": (
                "https://www.curseforge.com/minecraft/mc-mods/pyrotech",
                "https://github.com/codetaylor/pyrotech-1.12",
            ),
            "TerraFirmaCraft": (
                "https://www.curseforge.com/minecraft/mc-mods/terrafirmacraft",
                "https://github.com/TerraFirmaCraft/TerraFirmaCraft",
            ),
            "MineFantasy Reforged": (
                "https://www.curseforge.com/minecraft/mc-mods/minefantasy-reforged",
                "https://github.com/TeamMFR/MineFantasyReforged",
            ),
            "Geolosys": (
                "https://www.curseforge.com/minecraft/mc-mods/geolosys",
                "https://github.com/oitsjustjose/Geolosys",
            ),
            "Better With Mods": (
                "https://www.curseforge.com/minecraft/mc-mods/bwm-suite",
                "https://github.com/Rebirth-of-the-Night/BetterWithMods",
            ),
        }

        for project, links in expected_references.items():
            with self.subTest(project=project):
                self.assertIn(project, inspirations)
                for link in links:
                    self.assertIn(link, inspirations)

    def test_internal_markdown_links_resolve(self):
        for document in sorted(ROOT.rglob("*.md")):
            if any(
                part in IGNORED_DIRECTORIES
                for part in document.relative_to(ROOT).parts
            ):
                continue

            content = document.read_text(encoding="utf-8")
            for target in MARKDOWN_LINK.findall(content):
                with self.subTest(document=document.relative_to(ROOT), target=target):
                    if target.startswith(("http://", "https://", "mailto:", "#")):
                        continue
                    path_text = target.split("#", 1)[0]
                    self.assertTrue(
                        (document.parent / path_text).resolve().is_file(),
                        f"{document.relative_to(ROOT)} links to missing {target}",
                    )

    def test_tracked_text_has_no_trailing_whitespace(self):
        for path in repository_files():
            if path.suffix not in TEXT_SUFFIXES:
                continue

            with self.subTest(path=path.relative_to(ROOT)):
                for line_number, line in enumerate(
                    path.read_text(encoding="utf-8").splitlines(), start=1
                ):
                    self.assertEqual(
                        line.rstrip(),
                        line,
                        f"trailing whitespace at {path.relative_to(ROOT)}:{line_number}",
                    )


if __name__ == "__main__":
    unittest.main()
