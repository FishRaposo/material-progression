# Automated Testing Harness Design

## Goal

Make every deterministic behavior in the current Material Progression prototype
automatically verifiable, and require those checks before changes reach
`main`.

## Scope

The harness covers the implemented metallurgy slice:

- Tin, copper, and bronze recipes
- Ore crushing into exactly two dust
- Fuel consumption and processing in the stone crusher
- Crusher inventory and automation-facing rules
- Tin and bronze tool materials, repair tags, mining tags, and enchantment tags
- Tin ore loot and world-generation resource wiring
- Resource JSON validity, local identifier resolution, item models, and
  translations
- Internal documentation links and whitespace

Loose rocks, flint tools, the workshop, tree-punching prevention, and other
documented systems are not implemented. They receive no placeholder tests.
Their behavioral tests belong in the same harness when the corresponding
production code exists.

## Test Layers

### Resource contract tests

Python standard-library tests parse the source resources directly. They verify
observable data-pack and resource-pack contracts without booting Minecraft:

- Every JSON file parses.
- Local identifiers used by recipes, tags, loot tables, and world generation
  resolve to an existing resource or registered vanilla identifier where
  appropriate.
- Crushing recipes return two dust.
- Dust smelting and bronze alloying preserve the intended material flow.
- Every shipped item has an English and Brazilian Portuguese translation and an
  item model.
- Every internal Markdown link resolves and tracked text files contain no
  trailing whitespace.

These tests use hand-written expected values for progression rules. They do not
derive expected values from the files being tested.

The expected content lives in a declarative catalog, while shared resource
loading and path handling live in test-support utilities. Adding another item
or recipe should usually add a catalog entry rather than duplicate assertion
logic.

### NeoForge integration tests

The NeoForge Test Framework runs inside the GameTest server. Tests use the real
registries, recipe manager, block entity, tags, and ticking server:

- All crusher inputs resolve to crushing recipes with the expected outputs.
- A fueled crusher consumes one input and produces two dust after its configured
  processing time.
- A crusher does not process without fuel.
- Crusher sided inventory rules accept valid input and fuel and expose output
  correctly.
- Tin ore blocks, tool materials, repair tags, mining tags, and enchantability
  tags behave as registered.
- Breaking the crusher yields the crusher item.

The GameTest server also provides a full mod-loading and data-pack-loading smoke
test. A malformed registry, recipe, tag, loot table, or world-generation file
fails before behavioral assertions run.

GameTests are split by gameplay system. Shared assertions and block-entity
placement live in generic support, while repeated system setup lives in a small
fixture owned by that system. New features add focused test classes and
fixtures rather than extending one monolithic suite.

### Build verification

The existing Gradle build remains mandatory. It proves Java compilation,
resource processing, packaging, and metadata expansion. It does not replace the
resource or in-game tests.

One `headlessTest` task composes contracts, the build, and the live GameTest
server. Each layer remains independently runnable for fast iteration.

## CI

GitHub Actions runs three explicit gates on Java 25:

1. Resource and documentation contracts
2. Gradle build
3. NeoForge GameTest server

Each gate has a distinct step so failures identify the broken layer. The
GameTest source set and NeoForge Test Framework are development-only and are not
included in the published mod jar.

## Human Playtesting Boundary

Automation verifies correctness, not feel. The following remain human
playtesting questions:

- Whether progression is satisfying rather than tedious
- Whether yields and durability costs feel fair
- Whether terrain constraints create interesting routes
- Whether interfaces communicate the intended physical fantasy

The roadmap should record these separately from automated acceptance criteria.
