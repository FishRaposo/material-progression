---
name: developing-material-progression
description: Use when implementing or changing gameplay, Java code, recipes, tags, assets, world generation, or tests in the Material Progression repository.
---

# Developing Material Progression

## Load project context

Read `AGENTS.md`, `docs/COMPATIBILITY.md`, `docs/TESTING.md`, and the design
document for the system being changed. Read `docs/INSPIRATIONS.md` only when a
reference implementation would answer a concrete design or architecture
question.

Confirm whether the requested behavior is implemented or only documented. Do
not present a design candidate as existing code.

## Protect the workspace

Inspect `git status`, the current branch, remote base, and existing worktrees.
Preserve unrelated edits. Use an isolated worktree when the primary checkout is
dirty or divergent. Do not reset, force-push, or move `main` without explicit
authorization.

## Implement test-first

1. Name the regression the change must prevent.
2. Add a focused failing Python contract for repository data or a failing
   NeoForge GameTest for runtime behavior.
3. Run the focused test and verify the intended failure.
4. Implement the smallest coherent behavior.
5. Rerun the focused test.
6. Update living design documentation when implementation resolves an open
   decision.

Use `tests/content_contracts.py` for literal content catalogs,
`tests/test_resources.py` for resource invariants, and a system fixture plus
focused class under `src/gameTest` for live behavior. Never add test-only
methods to production classes.

## Apply compatibility by construction

For each new material form:

- Publish it under the established `c:` subtype and parent tags.
- Tag both block and item forms where applicable.
- Consume shared tags in recipes, machines, repair ingredients, and code.
- Add tools to vanilla tool-category tags.
- Keep private tags only for Material Progression-specific behavior.
- Use concrete IDs only for results, registrations, world generation, or
  identity-specific mechanics.
- Extend the compatibility contract with literal expected membership.

Search recipe inputs and behavior tags for direct material IDs before review.

## Complete the content slice

Every item needs a model plus `en_us` and `pt_br` translations. Every block
needs a blockstate and loot table. Wire registrations, tags, recipes, assets,
loot, world generation, and tests as one coherent slice.

Verify in increasing scope:

```bash
./gradlew contractTest
./gradlew build
./gradlew runGameTestServer
./gradlew headlessTest
```

If production sources or resources changed, hand off to the
`releasing-material-progression` skill to refresh and validate `dist/`.
