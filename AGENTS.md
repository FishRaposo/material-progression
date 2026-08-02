# Material Progression agent guide

Material Progression rebuilds Minecraft progression around materials, terrain,
processing, and the capabilities they grant. Preserve its central loop:

> Explore -> extract -> process -> combine -> craft -> gain agency

The mod should remain recognizably Minecraft. Prefer physical blocks, tools,
recipes, adjacent inventories, fuel, and hoppers over research menus, invisible
storage networks, electrical bureaucracy, or processing steps that exist only
to waste time.

## Start here

Before changing the project:

1. Read this file.
2. Read [the compatibility policy](docs/COMPATIBILITY.md).
3. Read the design document for the system being changed.
4. Read [the testing guide](docs/TESTING.md).
5. Use the applicable project skill under `.agents/skills/`.

The project-local skills are:

- `.agents/skills/developing-material-progression/SKILL.md`
- `.agents/skills/releasing-material-progression/SKILL.md`

All project-local skills belong under `.agents/skills/`, plural. Do not create
`.agents/skill/`.

## Toolchain and commands

- Minecraft: 26.2
- NeoForge: 26.2.0.35-beta
- Java: 25
- Build system: Gradle wrapper
- Production package: `dev.fishraposo.materialprogression`
- Mod ID: `material_progression`

Use the wrapper instead of a system Gradle installation:

```bash
./gradlew contractTest
./gradlew build
./gradlew runGameTestServer
./gradlew headlessTest
./gradlew runClient
```

`headlessTest` is the complete local verification entry point. It runs the fast
contracts, Java build, distribution check, and live NeoForge GameTests.

## Implemented boundary

Code currently implements:

- Tin ore and deepslate tin ore world generation
- Raw tin, tin ingots, tin dust, and tin tools
- Copper dust
- Bronze dust, bronze ingots, and bronze tools
- Fuel-burning stone crusher with sided inventory
- Ore and raw-metal crushing into two dust
- Dust smelting and provisional bronze alloy crafting
- Sixteen raw-stone families, family Rocks, family cobbles, and partial raw-stone
  drops
- Loose-rock and ground-stick world generation, including family-aware Rocks
  across the Overworld, Nether, and End plus tree/shrub-aware Overworld Sticks
- Geological resistance by dimension, depth, family, exposure, and natural
  versus player-placed origin
- Reloadable geological depth profiles for arbitrary non-built-in dimensions
- Rock, flint shards, and the flint hatchet, plus Plant Fiber and Flint/Bronze
  Knives, Hammers, and Saws
- A default-enabled log-only axe requirement with a server opt-out; Saws
  satisfy it, and invalid attempts receive throttled action-bar feedback
- A persistent Manual Workshop with public recipes for knife, hammer, and saw
  operations
- Recipe unlock advancements, opening-progression advancements, localized
  tooltips, and geological capability feedback
- Original deterministic 16-pixel art for every shipped inventory sprite and
  each custom full-cube block face; Ground Stick and Loose Rock retain their
  separate world-facing assets
- Reusable Python resource contracts and NeoForge GameTests
- A reproducible installable JAR under `dist/`

Expanded storage and hoppers, bonsai, pottery, ore samples, deposits and
prospecting, the bulk-crafting table, and expanded metallurgy remain documented
later work. Do not describe planned behavior as shipped behavior.

The current dependency order is:

> Primitive opening -> manual workshop -> geology/access progression ->
> shallow logistics -> bulk-crafting table

Do not expand the metal roster merely because another metal is easy to add.
Vertical slices should test a progression relationship before content breadth
increases.

## Compatibility is mandatory

Modern registry tags are the Ore Dictionary equivalent. Compatibility is a
design requirement, not cleanup:

- Publish every interchangeable material under the appropriate shared `c:` tag.
- Use shared material tags in recipe inputs, tool repair ingredients, machine
  inputs, and code checks.
- Add blocks and their item forms to the corresponding tags where both
  registries matter.
- Add tools to established vanilla tool-category tags.
- Reserve `material_progression:` tags for behavior specific to this mod.
- Use concrete item IDs for recipe results and genuinely identity-specific
  inputs.
- Add or extend a contract whenever a new compatibility category is introduced.

The exact rules, examples, and new-content checklist live in
`docs/COMPATIBILITY.md`.

## Code and resource structure

- `src/main/java`: production code and registrations
- `src/main/resources`: assets, recipes, tags, loot, and world generation
- `src/gameTest`: development-only live Minecraft tests
- `tests`: fast Python contracts over repository resources and documentation
- `docs`: design decisions, status, references, compatibility, and testing
- `dist`: exactly one versioned installable production JAR

Keep production code free of test-only hooks. Repeated GameTest setup belongs in
a system fixture. Reusable repository parsing belongs in `tests/support/`.
Literal shipped-content expectations belong in `tests/content_contracts.py`.

Every shipped item needs a model and English and Brazilian Portuguese
translations. Every shipped block needs a blockstate and loot table. New
gameplay behavior needs a live GameTest; new resource invariants need a fast
contract.

Future inventory or custom full-cube art must be original local 16-pixel art
that follows `docs/ITEM_ART.md`, has native and 8x atlas review, and extends
the literal art contract. After changing a shipped recipe resource, regenerate
`docs/RECIPES.md` with `python tests/generate_recipe_catalogue.py --write`.
When a player action denies a normal harvest or drop, provide concise,
throttled feedback and cover both the denial and the successful path.

## Development workflow

1. Inspect the current branch, worktrees, and dirty files before editing.
2. Preserve unrelated user changes. Use an isolated worktree for branch work
   when the primary checkout is dirty or divergent.
3. Write the smallest relevant failing contract or GameTest.
4. Run it and confirm it fails for the missing behavior.
5. Implement the minimal coherent slice.
6. Run the focused test, then `./gradlew headlessTest`.
7. Review the diff for stale documentation, direct material IDs, missing tags,
   missing assets, and accidental GameTest packaging.
8. Rebuild `dist/` through the release workflow when production sources change.

Do not silently change provisional design into implementation. Update the
relevant living design document when implementation resolves an open question.
Use [the reference catalog](docs/INSPIRATIONS.md) to study mechanics and credit
lineage; reference projects are neither dependencies nor permission to copy
their code.

## Git and release safety

- Never discard or overwrite unrelated dirty changes.
- Never use destructive reset or checkout commands to clean the tree.
- Never force-push.
- Never move `main` unless the user explicitly asks for integration.
- Before moving a shared branch, confirm its current remote SHA and use a
  non-forced fast-forward.
- Test the exact committed tree that will be integrated.
- Keep source, project version, README install link, and `dist/` artifact in
  sync.
- Do not claim a build or release succeeded without fresh command or CI
  evidence for the exact commit.

## Documentation map

- `README.md`: public overview, current prototype, install and basic commands
- `docs/VISION.md`: complete design thesis and progression loop
- `docs/DESIGN_PRINCIPLES.md`: durable decision rules
- `docs/PRIMITIVE_RESOURCES.md`: opening resources and primitive tools
- `docs/GEOLOGY.md`: access progression and underground structure
- `docs/METALLURGY.md`: material families and processing
- `docs/INFRASTRUCTURE.md`: workshop, storage, logistics, and bulk crafting
- `docs/ENCHANTING.md`: enchantability and magical metallurgy
- `docs/ROADMAP.md`: experiment order and unresolved questions
- `docs/COMPATIBILITY.md`: mandatory tag and interoperability policy
- `docs/TESTING.md`: contract and GameTest architecture
- `docs/INSPIRATIONS.md`: reference mods, source links, scope, and credit
