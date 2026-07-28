import re
import unittest
import zipfile
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
PROPERTIES = ROOT / "gradle.properties"
DIST = ROOT / "dist"


def read_property(name: str) -> str:
    pattern = re.compile(rf"^{re.escape(name)}=(.+)$")
    for line in PROPERTIES.read_text(encoding="utf-8").splitlines():
        match = pattern.match(line)
        if match:
            return match.group(1).strip()
    raise AssertionError(f"Missing Gradle property: {name}")


class DistributionContractTests(unittest.TestCase):
    def test_repository_contains_one_versioned_installable_mod_jar(self):
        mod_version = read_property("mod_version")
        expected = DIST / f"material-progression-{mod_version}.jar"

        self.assertEqual([expected], sorted(DIST.glob("*.jar")))

        with zipfile.ZipFile(expected) as mod_jar:
            entries = set(mod_jar.namelist())
            self.assertIn("META-INF/neoforge.mods.toml", entries)
            metadata = mod_jar.read("META-INF/neoforge.mods.toml").decode("utf-8")

        self.assertRegex(metadata, r'modId\s*=\s*"material_progression"')
        self.assertRegex(
            metadata,
            rf'version\s*=\s*"{re.escape(mod_version)}"',
        )


if __name__ == "__main__":
    unittest.main()
