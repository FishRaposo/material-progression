import io
import re
import unittest
import zipfile
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
PROPERTIES = ROOT / "gradle.properties"
DIST = ROOT / "dist"
BUILD_GRADLE = ROOT / "build.gradle"
GIT_ATTRIBUTES = ROOT / ".gitattributes"
BUILD_WORKFLOW = ROOT / ".github" / "workflows" / "build.yml"
MAIN_RESOURCES = ROOT / "src" / "main" / "resources"
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
    def test_distribution_verifier_identifies_the_exact_archive_difference(self):
        build = BUILD_GRADLE.read_text(encoding="utf-8")

        self.assertIn("missing from freshly built JAR", build)
        self.assertIn("missing from tracked distribution JAR", build)
        self.assertIn("fresh SHA-256", build)
        self.assertIn("tracked SHA-256", build)

    def test_ci_preserves_the_fresh_jar_before_distribution_verification(self):
        workflow = BUILD_WORKFLOW.read_text(encoding="utf-8")

        upload_step = "name: Upload freshly built mod JAR"
        verify_step = "name: Verify installable mod JAR"
        self.assertIn(upload_step, workflow)
        self.assertIn("path: build/libs/*.jar", workflow)
        self.assertLess(
            workflow.index(upload_step),
            workflow.index(verify_step),
            "CI must preserve the fresh JAR even when distribution verification fails",
        )

    def test_ci_pins_the_canonical_release_jdk(self):
        workflow = BUILD_WORKFLOW.read_text(encoding="utf-8")

        self.assertIn("java-version: '25.0.3+9'", workflow)
        self.assertIn("distribution: 'temurin'", workflow)

    def test_tracked_jar_preserves_source_resource_bytes(self):
        mod_version = read_property("mod_version")
        archive_path = DIST / f"material-progression-{mod_version}.jar"

        with zipfile.ZipFile(archive_path) as archive:
            for resource in sorted(MAIN_RESOURCES.rglob("*")):
                if not resource.is_file() or resource.suffix == ".bbmodel":
                    continue
                entry = resource.relative_to(MAIN_RESOURCES).as_posix()
                self.assertIn(entry, archive.namelist())
                self.assertEqual(
                    resource.read_bytes(),
                    archive.read(entry),
                    f"tracked JAR transformed resource bytes for {entry}",
                )

    def test_production_artifact_inputs_are_checked_out_with_lf_line_endings(self):
        attributes = GIT_ATTRIBUTES.read_text(encoding="utf-8")

        self.assertIn(
            "* text=auto eol=lf",
            attributes,
            "source and resource inputs must be normalized before JAR packaging",
        )
        self.assertIn(
            "*.jar binary",
            attributes,
            "the tracked production archive must remain byte-for-byte opaque to Git",
        )

    def test_production_jar_uses_platform_independent_archive_entries(self):
        build = BUILD_GRADLE.read_text(encoding="utf-8")

        self.assertIn(
            "entryCompression = org.gradle.api.tasks.bundling.ZipEntryCompression.STORED",
            build,
            "the tracked JAR must not depend on host-specific DEFLATE output",
        )
        self.assertIn("filePermissions { unix(\"644\") }", build)
        self.assertIn("dirPermissions { unix(\"755\") }", build)

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
