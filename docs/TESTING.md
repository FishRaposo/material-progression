# Testing toolkit

Material Progression uses two complementary headless test layers. Fast Python
contracts inspect repository resources directly. NeoForge GameTests load the
real mod in a ticking Minecraft server and exercise gameplay behavior.

Run everything with:

```bash
./gradlew headlessTest
```

The individual layers remain available when iterating:

```bash
./gradlew contractTest
./gradlew build
./gradlew runGameTestServer
```

## Fast resource contracts

`tests/content_contracts.py` is the declarative catalog of shipped content and
literal progression expectations. Add items, blocks, recipe contracts, and tool
families there. Generic assertions in `tests/test_resources.py` apply the same
checks to every catalog entry.

Shared filesystem and JSON behavior belongs in `tests/support/`. Domain
assertions belong in a focused `test_*.py` module. Keep expected values literal:
the test must not calculate its expected result from the resource it is
checking.

When adding content:

1. Add the item, block, recipe, or family to `content_contracts.py`.
2. Add a focused assertion only if the content introduces a new kind of
   contract.
3. Run `./gradlew contractTest`.

## Live NeoForge GameTests

GameTests are grouped by gameplay system under
`src/gameTest/java/dev/fishraposo/materialprogression/gametest/`.

- `GameTestSupport` contains assertions and setup useful across systems.
- A system fixture, such as `CrusherFixture`, owns repeated placement,
  inventory-slot, and block-entity setup for that system.
- A focused class, such as `CrusherGameTests`, contains observable behavior
  tests for one system.
- `ToolGameTests` demonstrates a separate domain that shares the same runner
  without sharing irrelevant setup.

New systems should follow the same shape. For example, workshop behavior should
live in `WorkshopGameTests` with a `WorkshopFixture` once the workshop exists.
Add a shared helper only after at least two tests need the same operation.

GameTests must exercise real registries, recipes, inventories, blocks, and
server ticks. Test-only setup remains in fixtures; production classes should
not gain methods solely to support tests.

## Adding a behavior

For each implemented mechanic:

1. Name the production regression the test should catch.
2. Write the smallest GameTest that demonstrates the expected result.
3. Run it and confirm it fails for the intended reason.
4. Implement the mechanic.
5. Run `./gradlew runGameTestServer`.
6. Run `./gradlew headlessTest` before integration.

Do not add placeholder tests for systems that exist only in the design
documents. Add their tests alongside their implementation.

## CI and diagnostics

GitHub Actions keeps contracts, the Gradle build, and GameTests as separate
named steps so the failing layer is immediately visible. GameTest logs and test
results are uploaded even when the live server fails.

Automation verifies deterministic correctness. Progression feel, interface
clarity, and balance remain human playtesting responsibilities.
