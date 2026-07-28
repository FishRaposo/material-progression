import unittest
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
JAVA_ROOT = ROOT / "src" / "main" / "java"
GAME_TEST_JAVA_ROOT = ROOT / "src" / "gameTest" / "java"
TYPE_DECLARATIONS = ("class ", "interface ", "enum ", "record ")


class JavaSourceContractTests(unittest.TestCase):
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
