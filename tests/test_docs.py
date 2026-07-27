import re
import unittest
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
MARKDOWN_LINK = re.compile(r"\[[^\]]+\]\(([^)]+)\)")
TEXT_SUFFIXES = {".gradle", ".java", ".json", ".md", ".properties", ".yml", ".yaml"}
IGNORED_DIRECTORIES = {".git", ".gradle", "build", "repo", "run"}


def repository_files():
    for path in ROOT.rglob("*"):
        if not path.is_file():
            continue
        if any(part in IGNORED_DIRECTORIES for part in path.relative_to(ROOT).parts):
            continue
        yield path


class DocumentationContractTests(unittest.TestCase):
    def test_internal_markdown_links_resolve(self):
        for document in sorted(ROOT.rglob("*.md")):
            if any(
                part in IGNORED_DIRECTORIES
                for part in document.relative_to(ROOT).parts
            ):
                continue

            content = document.read_text(encoding="utf-8")
            for target in MARKDOWN_LINK.findall(content):
                with self.subTest(document=document.relative_to(ROOT), target=target):
                    if target.startswith(("http://", "https://", "mailto:", "#")):
                        continue
                    path_text = target.split("#", 1)[0]
                    self.assertTrue(
                        (document.parent / path_text).resolve().is_file(),
                        f"{document.relative_to(ROOT)} links to missing {target}",
                    )

    def test_tracked_text_has_no_trailing_whitespace(self):
        for path in repository_files():
            if path.suffix not in TEXT_SUFFIXES:
                continue

            with self.subTest(path=path.relative_to(ROOT)):
                for line_number, line in enumerate(
                    path.read_text(encoding="utf-8").splitlines(), start=1
                ):
                    self.assertEqual(
                        line.rstrip(),
                        line,
                        f"trailing whitespace at {path.relative_to(ROOT)}:{line_number}",
                    )


if __name__ == "__main__":
    unittest.main()

