import re
import unittest
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
JAVA_ROOT = ROOT / "src" / "main" / "java"
GAME_TEST_JAVA_ROOT = ROOT / "src" / "gameTest" / "java"
TYPE_DECLARATIONS = ("class ", "interface ", "enum ", "record ")
PREDICTION_CACHE = (
    JAVA_ROOT
    / "dev"
    / "fishraposo"
    / "materialprogression"
    / "stone"
    / "GeologyMiningPredictionCache.java"
)
CLIENT_GEOLOGY_EVENTS = (
    JAVA_ROOT
    / "dev"
    / "fishraposo"
    / "materialprogression"
    / "client"
    / "ClientGeologyMiningEvents.java"
)


class JavaSourceContractTests(unittest.TestCase):
    def test_prediction_cache_exposes_only_client_runtime_operations(self):
        source = PREDICTION_CACHE.read_text(encoding="utf-8")
        client_source = CLIENT_GEOLOGY_EVENTS.read_text(encoding="utf-8")
        public_methods = set(
            re.findall(
                r"^    public (?:[\w<>,.?\[\]]+\s+)+(\w+)\s*\(",
                source,
                flags=re.MULTILINE,
            )
        )
        runtime_operations = set(
            re.findall(r"\bCACHE\.(\w+)\s*\(", client_source)
        )

        self.assertEqual(
            set(),
            public_methods - runtime_operations,
            "Every public prediction-cache method must be called by the "
            "physical client runtime; observable test state belongs in "
            "GameTest assertions instead of production hooks",
        )

    def test_game_tests_do_not_split_production_packages(self):
        production_packages = {
            path.parent.relative_to(JAVA_ROOT)
            for path in JAVA_ROOT.rglob("*.java")
        }
        game_test_packages = {
            path.parent.relative_to(GAME_TEST_JAVA_ROOT)
            for path in GAME_TEST_JAVA_ROOT.rglob("*.java")
        }

        split_packages = production_packages & game_test_packages

        self.assertEqual(
            set(),
            split_packages,
            "GameTest sources must not share packages with the production mod",
        )

    def test_imports_precede_type_declarations(self):
        for path in JAVA_ROOT.rglob("*.java"):
            with self.subTest(path=path.relative_to(ROOT)):
                type_declared = False
                for line_number, line in enumerate(
                    path.read_text(encoding="utf-8").splitlines(),
                    start=1,
                ):
                    stripped = line.strip()
                    if any(
                        declaration in stripped
                        for declaration in TYPE_DECLARATIONS
                    ):
                        type_declared = True
                    if type_declared and stripped.startswith("import "):
                        self.fail(
                            f"{path.relative_to(ROOT)}:{line_number} "
                            "places an import after a type declaration"
                        )
