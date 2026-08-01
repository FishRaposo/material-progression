import subprocess
import unittest
import zipfile
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
GITIGNORE = ROOT / ".gitignore"
BUILD_GRADLE = ROOT / "build.gradle"
DIST = ROOT / "dist"


class ReferenceAssetBoundaryTests(unittest.TestCase):
    def test_reference_assets_are_ignored_by_git(self):
        ignore_rules = GITIGNORE.read_text(encoding="utf-8")
        self.assertIn("research/reference-assets/", ignore_rules)

        ignored = subprocess.run(
            [
                "git",
                "check-ignore",
                "--quiet",
                "research/reference-assets/sentinel.png",
            ],
            cwd=ROOT,
            check=False,
        )
        self.assertEqual(0, ignored.returncode)

        tracked = subprocess.run(
            ["git", "ls-files", "--", "research/reference-assets"],
            cwd=ROOT,
            check=True,
            capture_output=True,
            text=True,
        )
        self.assertEqual("", tracked.stdout)

    def test_reference_assets_are_outside_production_resource_inputs(self):
        build = BUILD_GRADLE.read_text(encoding="utf-8")
        self.assertIn("sourceSets.main.resources", build)
        self.assertIn("src/generated/resources", build)
        self.assertNotIn("reference-assets", build)

        archives = sorted(DIST.glob("material-progression-*.jar"))
        self.assertEqual(1, len(archives))
        with zipfile.ZipFile(archives[0]) as archive:
            study_entries = [
                entry
                for entry in archive.namelist()
                if entry.startswith("research/reference-assets/")
            ]
        self.assertEqual([], study_entries)


if __name__ == "__main__":
    unittest.main()
