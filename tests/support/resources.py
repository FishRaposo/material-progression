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
        self.data = self.resources / "data" / namespace
        self.assets = self.resources / "assets" / namespace

    def load_json(self, path: Path) -> Any:
        with path.open(encoding="utf-8") as source:
            return json.load(source)

    def recipe(self, name: str) -> dict[str, Any]:
        return self.load_json(self.data / "recipe" / f"{name}.json")

    def names_matching(self, directory: Path, pattern: str) -> set[str]:
        return {path.stem for path in directory.glob(pattern)}

    @staticmethod
    def ingredient_id(recipe: dict[str, Any]) -> str:
        ingredient = recipe["ingredient"]
        if not isinstance(ingredient, str):
            raise AssertionError(
                "Minecraft 26.2 ingredients must use the string form, "
                f"got {ingredient!r}"
            )
        return ingredient
