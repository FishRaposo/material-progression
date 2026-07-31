import io
import re
import unittest
import zipfile
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
PROPERTIES = ROOT / "gradle.properties"
DIST = ROOT / "dist"
BUILD_GRADLE = ROOT / "build.gradle"
FORBIDDEN_TEST_MARKERS = (
    "gametest",
    "testframework",
    "material_progression_gametests",
    "net/neoforged/testframework/",
    "org/junit/",
    "junit/",
    "org/mockito/",
    "org/assertj/",
    "org/hamcrest/",
    "org/testng/",
    "org/spockframework/",
    "io/kotest/",
    "org/opentest4j/",
    "org/apiguardian/",
)


def read_property(name: str) -> str:
    pattern = re.compile(rf"^{re.escape(name)}=(.+)$")
    for line in PROPERTIES.read_text(encoding="utf-8").splitlines():
        match = pattern.match(line)
        if match:
            return match.group(1).strip()
    raise AssertionError(f"Missing Gradle property: {name}")


class DistributionContractTests(unittest.TestCase):
    def assert_archive_excludes_testing_content(
        self,
        archive: zipfile.ZipFile,
        context: str,
    ) -> None:
        entries = archive.namelist()
        forbidden_entries = sorted(
            entry
            for entry in entries
            if any(
                marker in entry.casefold()
                for marker in FORBIDDEN_TEST_MARKERS
            )
        )
        self.assertEqual(
            [],
            forbidden_entries,
            f"{context} contains testing classes, resources, or metadata",
        )

        metadata_names = (
            "META-INF/MANIFEST.MF",
            "META-INF/neoforge.mods.toml",
            "META-INF/jarjar/metadata.json",
        )
        for name in metadata_names:
            if name not in entries:
                continue
            metadata = archive.read(name).decode("utf-8", errors="replace")
            forbidden_metadata = sorted(
                marker
                for marker in FORBIDDEN_TEST_MARKERS
                if marker in metadata.casefold()
            )
            self.assertEqual(
                [],
                forbidden_metadata,
                f"{context}!/{name} references a testing framework or test mod",
            )

        for entry in entries:
            if not entry.casefold().endswith(".jar"):
                continue
            with zipfile.ZipFile(io.BytesIO(archive.read(entry))) as nested:
                self.assert_archive_excludes_testing_content(
                    nested,
                    f"{context}!/{entry}",
                )

    def test_production_runs_do_not_load_the_development_gametest_mod(self):
        build = BUILD_GRADLE.read_text(encoding="utf-8")

        production_only = 'loadedMods = [mods."${mod_id}"]'
        self.assertEqual(
            3,
            build.count(production_only),
            "client, server, and data runs must load only the production mod",
        )
        self.assertIn(
            'mods."${mod_id}_gametests"',
            build,
            "the dedicated GameTest server must still load the test mod",
        )
        forbidden_production_links = (
            "implementation.extendsFrom gameTest",
            "runtimeOnly.extendsFrom gameTest",
            "runtimeClasspath.extendsFrom gameTest",
            "sourceSets.main.output += sourceSets.gameTest",
            "from(sourceSets.gameTest",
            "from sourceSets.gameTest",
        )
        for marker in forbidden_production_links:
            self.assertNotIn(
                marker,
                build,
                f"production packaging includes GameTest output: {marker}",
            )

    def test_repository_contains_one_versioned_installable_mod_jar(self):
        mod_version = read_property("mod_version")
        expected = DIST / f"material-progression-{mod_version}.jar"

        self.assertEqual([expected], sorted(DIST.glob("*.jar")))

        with zipfile.ZipFile(expected) as mod_jar:
            entries = set(mod_jar.namelist())
            self.assertIn("META-INF/neoforge.mods.toml", entries)
            metadata = mod_jar.read("META-INF/neoforge.mods.toml").decode("utf-8")
            self.assert_archive_excludes_testing_content(
                mod_jar,
                expected.name,
            )

        self.assertRegex(metadata, r'modId\s*=\s*"material_progression"')
        self.assertNotIn("material_progression_gametests", metadata)
        self.assertRegex(
            metadata,
            rf'version\s*=\s*"{re.escape(mod_version)}"',
        )


if __name__ == "__main__":
    unittest.main()
