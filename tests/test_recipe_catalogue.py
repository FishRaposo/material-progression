"""Keep the committed recipe catalogue synchronized with shipped resources."""

from __future__ import annotations

import unittest
from pathlib import Path
import sys

sys.path.insert(0, str(Path(__file__).resolve().parent))

from generate_recipe_catalogue import render_catalogue


ROOT = Path(__file__).resolve().parents[1]


class RecipeCatalogueTests(unittest.TestCase):
    def test_catalogue_is_the_current_render_of_every_shipped_recipe(self):
        self.assertEqual(
            render_catalogue(ROOT),
            (ROOT / "docs" / "RECIPES.md").read_text(encoding="utf-8"),
        )


if __name__ == "__main__":
    unittest.main()
