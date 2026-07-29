from dataclasses import dataclass
import json
from pathlib import Path
from typing import Any


@dataclass(frozen=True)
class RecipeContract:
    recipe_type: str
    ingredient: str
    result: str
    count: int = 1
    cooking_time: int | None = None


class ResourceTree:
    """Paths and JSON access shared by resource contract tests."""

    def __init__(self, root: Path, namespace: str):
        self.root = root
        self.namespace = namespace
        self.resources = root / "src" / "main" / "resources"
        self.generated_resources = root / "src" / "generated" / "resources"
        self.data = self.resources / "data" / namespace
        self.assets = self.resources / "assets" / namespace

    def load_json(self, path: Path) -> Any:
        with self.resolve(path).open(encoding="utf-8") as source:
            return json.load(source)

    def recipe(self, name: str) -> dict[str, Any]:
        return self.load_json(self.data / "recipe" / f"{name}.json")

    def names_matching(self, directory: Path, pattern: str) -> set[str]:
        return {path.stem for path in self.paths_matching(directory, pattern)}

    def paths_matching(self, directory: Path, pattern: str) -> set[Path]:
        paths = set(directory.glob(pattern))
        generated_directory = self.generated_path(directory)
        if generated_directory != directory:
            paths.update(generated_directory.glob(pattern))
        return paths

    def json_files(self) -> set[Path]:
        return set(self.resources.rglob("*.json")) | set(
            self.generated_resources.rglob("*.json")
        )

    def exists(self, path: Path) -> bool:
        return path.exists() or self.generated_path(path).exists()

    def resolve(self, path: Path) -> Path:
        if path.exists():
            return path
        generated = self.generated_path(path)
        if generated.exists():
            return generated
        return path

    def generated_path(self, path: Path) -> Path:
        try:
            relative = path.relative_to(self.resources)
        except ValueError:
            return path
        return self.generated_resources / relative

    @staticmethod
    def ingredient_id(recipe: dict[str, Any]) -> str:
        ingredient = recipe["ingredient"]
        if not isinstance(ingredient, str):
            raise AssertionError(
                "Minecraft 26.2 ingredients must use the string form, "
                f"got {ingredient!r}"
            )
        return ingredient
