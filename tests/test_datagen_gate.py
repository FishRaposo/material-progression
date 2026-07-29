import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import textwrap
import unittest


ROOT = Path(__file__).resolve().parents[1]
GATE = ROOT / "scripts" / "verify-datagen.sh"
WORKFLOW = ROOT / ".github" / "workflows" / "build.yml"


class DatagenGateTests(unittest.TestCase):
    def test_stable_generation_runs_production_datagen_twice(self):
        result, calls = self.run_gate("stable", '{"value":"tracked"}\n')

        self.assertEqual(0, result.returncode, result.stdout + result.stderr)
        self.assertEqual(["runData --stacktrace"] * 2, calls)

    def test_datagen_hash_cache_is_ignored_at_all_drift_boundaries(self):
        result, calls = self.run_gate("stable_with_cache", '{"value":"tracked"}\n')

        self.assertEqual(0, result.returncode, result.stdout + result.stderr)
        self.assertEqual(["runData --stacktrace"] * 2, calls)

    def test_checked_in_generated_resource_drift_fails(self):
        result, calls = self.run_gate("drift", '{"value":"tracked"}\n')

        self.assertNotEqual(0, result.returncode)
        self.assertIn("checked-in generated resources are stale", result.stderr)
        self.assertEqual(["runData --stacktrace"] * 2, calls)

    def test_second_run_nondeterminism_fails(self):
        result, calls = self.run_gate(
            "nondeterministic",
            '{"value":"first"}\n',
        )

        self.assertNotEqual(0, result.returncode)
        self.assertIn("second datagen run changed output", result.stderr)
        self.assertEqual(["runData --stacktrace"] * 2, calls)

    def test_build_workflow_runs_datagen_gate_before_build(self):
        workflow = WORKFLOW.read_text(encoding="utf-8")

        self.assertIn("./scripts/verify-datagen.sh", workflow)
        gate = workflow.index("./scripts/verify-datagen.sh")
        build = workflow.index("./gradlew build --stacktrace")
        game_tests = workflow.index("./gradlew runGameTestServer --stacktrace")
        self.assertLess(gate, build)
        self.assertLess(gate, game_tests)

    def run_gate(
        self,
        mode: str,
        tracked_content: str,
    ) -> tuple[subprocess.CompletedProcess[str], list[str]]:
        self.assertTrue(GATE.is_file(), f"missing executable gate: {GATE}")
        with tempfile.TemporaryDirectory() as temporary:
            repo = Path(temporary)
            generated = (
                repo
                / "src"
                / "generated"
                / "resources"
                / "data"
                / "example"
            )
            generated.mkdir(parents=True)
            (generated / "catalog.json").write_text(
                tracked_content,
                encoding="utf-8",
            )
            shutil.copy2(GATE, repo / "verify-datagen.sh")
            gradlew = repo / "gradlew"
            gradlew.write_text(
                textwrap.dedent(
                    """\
                    #!/usr/bin/env bash
                    set -euo pipefail
                    echo "$*" >> datagen-calls.txt
                    count="$(wc -l < datagen-calls.txt)"
                    case "${DATAGEN_TEST_MODE}" in
                      stable)
                        printf '{"value":"tracked"}\\n' > \
                          src/generated/resources/data/example/catalog.json
                        ;;
                      stable_with_cache)
                        printf '{"value":"tracked"}\\n' > \
                          src/generated/resources/data/example/catalog.json
                        mkdir -p src/generated/resources/.cache
                        printf 'provider hash %s\\n' "$count" > \
                          src/generated/resources/.cache/cache
                        ;;
                      drift)
                        printf '{"value":"changed"}\\n' > \
                          src/generated/resources/data/example/catalog.json
                        ;;
                      nondeterministic)
                        printf '{"value":"%s"}\\n' "$count" > \
                          src/generated/resources/data/example/catalog.json
                        ;;
                    esac
                    """
                ),
                encoding="utf-8",
            )
            gradlew.chmod(0o755)

            subprocess.run(
                ["git", "init", "-q"],
                cwd=repo,
                check=True,
            )
            subprocess.run(
                ["git", "config", "user.email", "contracts@example.invalid"],
                cwd=repo,
                check=True,
            )
            subprocess.run(
                ["git", "config", "user.name", "Contract Tests"],
                cwd=repo,
                check=True,
            )
            subprocess.run(["git", "add", "."], cwd=repo, check=True)
            subprocess.run(
                ["git", "commit", "-qm", "fixture"],
                cwd=repo,
                check=True,
            )

            environment = os.environ.copy()
            environment["DATAGEN_TEST_MODE"] = mode
            result = subprocess.run(
                ["./verify-datagen.sh"],
                cwd=repo,
                env=environment,
                text=True,
                capture_output=True,
                check=False,
            )
            calls_path = repo / "datagen-calls.txt"
            calls = (
                calls_path.read_text(encoding="utf-8").splitlines()
                if calls_path.exists()
                else []
            )
            return result, calls


if __name__ == "__main__":
    unittest.main()
