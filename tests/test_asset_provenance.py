import json
import subprocess
import sys
import unittest
import zipfile
from pathlib import Path

TESTS = Path(__file__).resolve().parent
if str(TESTS) not in sys.path:
    sys.path.insert(0, str(TESTS))

from content_contracts import AUTHORED_FULL_BLOCKS, AUTHORED_ITEM_GROUPS


ROOT = Path(__file__).resolve().parents[1]
MANIFEST = ROOT / "docs" / "asset-provenance.json"
DIST = ROOT / "dist"
EXPECTED_STUDY_REVISIONS = {
    "no_tree_punching": "05c46ca24a69302f0b1fe86b48a54e5704cf4928",
    "terrafirmacraft": "48a6a3bd96fa8e63e950c08f217b044a68ca9516",
    "divergent_underground": "5403463f52da21c1c4f67d2d58af20f77a435944",
}


class AssetProvenanceTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.manifest = json.loads(MANIFEST.read_text(encoding="utf-8"))
        cls.references = {
            entry["id"]: entry for entry in cls.manifest["local_only_references"]
        }

    def test_manifest_covers_current_local_asset_groups(self):
        self.assertEqual(1, self.manifest["schema_version"])
        self.assertEqual(
            set(AUTHORED_ITEM_GROUPS) | {"full_blocks"},
            {entry["id"] for entry in self.manifest["shipped_asset_groups"]},
        )
        self.assertTrue(AUTHORED_FULL_BLOCKS)
        for entry in self.manifest["shipped_asset_groups"]:
            self.assertEqual(
                "Material Progression original deterministic local art",
                entry["origin"],
            )
            self.assertEqual("tools/generate_item_art.py", entry["generator"])
            self.assertTrue((ROOT / entry["resource_root"]).is_dir())

    def test_local_studies_are_pinned_and_non_redistributable(self):
        self.assertIn("must not enter", self.manifest["non_redistribution_boundary"])
        self.assertEqual(
            {"minecraft_26_2_client", *EXPECTED_STUDY_REVISIONS},
            set(self.references),
        )
        for reference_id, revision in EXPECTED_STUDY_REVISIONS.items():
            entry = self.references[reference_id]
            self.assertEqual(revision, entry["revision"])
            self.assertTrue(entry["source_url"].startswith("https://"))
            self.assertTrue(entry["local_path"].startswith("research/reference-assets/"))
            self.assertIn("local study only", entry["license_status"])
            self.assertIn(" checkout ", entry["refresh"])
        vanilla = self.references["minecraft_26_2_client"]
        self.assertEqual("26.2", vanilla["version"])
        self.assertIn("Mojang EULA", vanilla["license_status"])
        self.assertNotIn("research/reference-assets", vanilla["local_path"])

    def test_studies_remain_ignored_and_unpackaged(self):
        for entry in self.references.values():
            local_path = entry["local_path"]
            if not local_path.startswith("research/reference-assets/"):
                continue
            ignored = subprocess.run(
                ["git", "check-ignore", "--quiet", local_path + "/sentinel"],
                cwd=ROOT,
                check=False,
            )
            self.assertEqual(0, ignored.returncode, local_path)
        tracked = subprocess.run(
            ["git", "ls-files", "--", "research/reference-assets"],
            cwd=ROOT,
            check=True,
            capture_output=True,
            text=True,
        )
        self.assertEqual("", tracked.stdout)
        [jar] = sorted(DIST.glob("material-progression-*.jar"))
        with zipfile.ZipFile(jar) as archive:
            self.assertFalse(any(entry.startswith("research/reference-assets/") for entry in archive.namelist()))

    def test_release_docs_link_the_inventory(self):
        for document in ("README.md", "docs/ITEM_ART.md", "docs/INSPIRATIONS.md"):
            self.assertIn("ASSET_PROVENANCE.md", (ROOT / document).read_text(encoding="utf-8"))


if __name__ == "__main__":
    unittest.main()
