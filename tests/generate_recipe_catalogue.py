"""Render the human-readable catalogue from the shipped recipe resources."""

from __future__ import annotations

import argparse
import json
from collections import Counter
from pathlib import Path
from typing import Callable


DATA = Path("src/main/resources/data/material_progression/recipe")


def read_recipes(root: Path) -> dict[str, dict]:
    return {
        path.stem: json.loads(path.read_text(encoding="utf-8"))
        for path in sorted((root / DATA).glob("*.json"))
    }


def item_count(stack: dict) -> str:
    return f"{stack.get('count', 1)} x `{stack['id']}`"


def counted_values(values: list[str]) -> str:
    counts = Counter(values)
    return "; ".join(
        f"{count} x `{value}`" for value, count in sorted(counts.items())
    )


def crafting_inputs(recipe: dict) -> str:
    if "ingredients" in recipe:
        return counted_values(recipe["ingredients"])

    symbols = {
        symbol: ingredient for symbol, ingredient in recipe["key"].items()
    }
    counts = Counter(
        symbols[symbol]
        for row in recipe["pattern"]
        for symbol in row
        if symbol != " "
    )
    shape = " / ".join(recipe["pattern"])
    legend = "; ".join(
        f"{symbol} = `{ingredient}`"
        for symbol, ingredient in sorted(symbols.items())
    )
    values = "; ".join(
        f"{count} × `{ingredient}`"
        for ingredient, count in sorted(counts.items())
    )
    return f"{values} (shape `{shape}`; {legend})"


def ticks(value: int) -> str:
    return f"{value} ticks ({value / 20:g} s)"


def requirements(recipe: dict) -> str:
    recipe_type = recipe["type"]
    if recipe_type.startswith("minecraft:crafting_"):
        return "Crafting grid; no fuel."
    if recipe_type == "minecraft:smelting":
        cooking_time = recipe.get("cookingtime", 200)
        default = " (recipe default)" if "cookingtime" not in recipe else ""
        return (
            f"Furnace; {ticks(cooking_time)}{default}; any valid furnace "
            "fuel (the recipe does not prescribe one)."
        )
    if recipe_type == "material_progression:crushing":
        return (
            f"Fuel-burning Grinder; {ticks(recipe['cookingtime'])}; any valid "
            "furnace fuel."
        )
    if recipe_type == "material_progression:manual_workshop":
        return (
            "Manual Workshop; "
            f"tool `{recipe['tool']}` loses {recipe['tool_damage']} durability; "
            f"{ticks(recipe['processing_time'])}; no fuel."
        )
    raise ValueError(f"Unhandled recipe type: {recipe_type}")


def resource_row(name: str, recipe: dict) -> str:
    recipe_type = recipe["type"]
    if recipe_type == "material_progression:manual_workshop":
        inputs = f"1 x `{recipe['ingredient']}`"
    elif recipe_type in {
        "minecraft:smelting",
        "material_progression:crushing",
    }:
        inputs = f"1 x `{recipe['ingredient']}`"
    else:
        inputs = crafting_inputs(recipe)
    return (
        f"| `{name}` | {inputs} | {item_count(recipe['result'])} | "
        f"Data-driven `{recipe_type}` | {requirements(recipe)} |"
    )


def category_rows(
    recipes: dict[str, dict], predicate: Callable[[str, dict], bool]
) -> list[tuple[str, dict]]:
    return [
        (name, recipe)
        for name, recipe in recipes.items()
        if predicate(name, recipe)
    ]


def render_section(title: str, rows: list[tuple[str, dict]]) -> list[str]:
    if not rows:
        return []
    rendered = [f"## {title}", "", "| Recipe ID | Inputs per operation | Output | Type | Requirements |", "| --- | --- | --- | --- | --- |"]
    rendered.extend(resource_row(name, recipe) for name, recipe in rows)
    rendered.append("")
    return rendered


def render_catalogue(root: Path) -> str:
    recipes = read_recipes(root)
    selected: set[str] = set()

    def take(predicate: Callable[[str, dict], bool]) -> list[tuple[str, dict]]:
        rows = category_rows(recipes, predicate)
        selected.update(name for name, _ in rows)
        return rows

    primitive_names = {
        "crusher",
        "flint_hammer",
        "flint_hatchet",
        "flint_knife",
        "flint_saw",
        "flint_shard_from_flint",
        "flint_shard_from_rock",
        "manual_workshop",
        "plant_fiber_to_string",
    }
    sections = [
        ("Primitive crafting and workstations", take(lambda name, _: name in primitive_names)),
        (
            "Industrial crafting, gear, armor, and alloys",
            take(lambda name, recipe: (
                recipe["type"].startswith("minecraft:crafting_")
                and name not in primitive_names
            )),
        ),
        ("Fuel-burning Crusher processing", take(lambda _, recipe: recipe["type"] == "material_progression:crushing")),
        ("Smelting material products", take(lambda name, recipe: recipe["type"] == "minecraft:smelting" and not name.startswith("smelting_cobbled_"))),
        ("Smelting family cobbles back to raw stone", take(lambda name, _: name.startswith("smelting_cobbled_"))),
        ("Manual Workshop: knife operations", take(lambda _, recipe: recipe.get("tool") == "#c:tools/knives")),
        ("Manual Workshop: hammer operations", take(lambda _, recipe: recipe.get("tool") == "#c:tools/hammers")),
        ("Manual Workshop: saw operations", take(lambda _, recipe: recipe.get("tool") == "#c:tools/saws")),
    ]
    sections = [(title, rows) for title, rows in sections if rows]
    behavior_driven = {"rock_cobbling"}
    unlisted = set(recipes) - selected - behavior_driven
    duplicate_selection = [
        name for name, count in Counter(
            name for _, rows in sections for name, _ in rows
        ).items() if count != 1
    ]
    if unlisted or duplicate_selection or behavior_driven - set(recipes):
        raise ValueError(
            "Recipe categories must cover every resource exactly once: "
            f"unlisted={sorted(unlisted)}, duplicates={sorted(duplicate_selection)}, "
            f"missing_behavior={sorted(behavior_driven - set(recipes))}"
        )

    lines = [
        "# Recipe catalogue",
        "",
        "> **Generated file.** Update it with `python tests/generate_recipe_catalogue.py --write` after changing a shipped recipe resource; `tests/test_recipe_catalogue.py` rejects a stale committed copy.",
        "",
        "This catalogue covers every recipe under `data/material_progression/recipe` that ships with the mod. Inputs use the literal item or tag accepted by the recipe; a tag accepts any compatible member. Times use Minecraft ticks (20 ticks = 1 second). Smelting and Crusher entries state fuel separately because the recipe JSON does not bind a particular fuel item.",
        "",
        "## Behaviour-driven custom recipe",
        "",
        "| Recipe ID | Inputs per operation | Output | Type | Requirements |",
        "| --- | --- | --- | --- | --- |",
        "| `rock_cobbling` | Exactly 4 x `#c:rocks`; all four must fit the crafting grid. | 1 x mapped family cobble when all Rocks map to the same registered family; otherwise 1 x `minecraft:cobblestone`. | Behaviour-driven `material_progression:rock_cobbling` custom recipe | Crafting grid; no fuel. The serializer validates the four Rocks and resolves the output from the live stone-family catalog, so neither output is hard-coded in JSON. |",
        "",
    ]
    for title, rows in sections:
        lines.extend(render_section(title, rows))
    lines.extend([
        "## Scope and terminology",
        "",
        "All rows after the custom cobbling rule are **data-driven** recipe resources: they are reloadable JSON definitions. `rock_cobbling` is intentionally separate because its resource only selects a serializer; Java behaviour validates its inputs and selects its result dynamically. Manual Workshop rows consume one input, require the listed installed tool, and damage that tool only when an operation completes. The fuel-burning Grinder (registry ID remains `crusher`) uses the furnace fuel model; Manual Workshop processing and crafting do not consume fuel.",
        "",
    ])
    return "\n".join(lines)


def main() -> None:
    parser = argparse.ArgumentParser()
    action = parser.add_mutually_exclusive_group()
    action.add_argument("--check", action="store_true", help="exit non-zero when docs/RECIPES.md is stale")
    action.add_argument("--write", action="store_true", help="write the current render to docs/RECIPES.md")
    args = parser.parse_args()
    root = Path(__file__).resolve().parents[1]
    rendered = render_catalogue(root)
    destination = root / "docs" / "RECIPES.md"
    if args.check:
        if destination.read_text(encoding="utf-8") != rendered:
            raise SystemExit("docs/RECIPES.md is stale; run python tests/generate_recipe_catalogue.py")
        return
    if args.write:
        destination.write_text(rendered, encoding="utf-8")
        return
    print(rendered, end="")


if __name__ == "__main__":
    main()
