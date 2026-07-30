# Testing toolkit

Material Progression uses two complementary headless layers. Fast Python
contracts inspect repository resources directly. NeoForge GameTests load the
real mod in a ticking dedicated server and exercise gameplay behavior.

Run everything with:

```bash
./gradlew headlessTest
```

The individual gates remain available while iterating:

```bash
./gradlew contractTest
./gradlew build
./gradlew runGameTestServer
```

`headlessTest` runs the contracts, Java build, distribution check, and live
GameTests. It is the complete local verification command.

## Fast resource contracts

`tests/content_contracts.py` is the literal catalog of shipped items, blocks,
recipes, and tool families. Generic assertions in `tests/test_resources.py`
apply the same resource checks to every entry. Reusable filesystem and JSON
parsing belongs in `tests/support/`; new domain contracts belong in focused
`test_*.py` modules.

The opening/geology contracts cover:

- The exact sixteen-family catalog and soft, standard, and hard profiles
- Fifteen additional Rock items and fourteen custom cobbled blocks
- Unique source and direct-generation surfaces
- The absence of a generic Loose Rock fallback and the disjoint,
  Netherrack-only Soul Sand/Soul Soil cover interface
- Models, blockstates, translations, loot, smelting, mining tags, and recipes
- Shared-tag inputs and the single custom four-Rock cobbling recipe
- The registered custom-dimension profile schema, immutable built-in bands,
  transactional validation, and absence of stale release-blocker wording
- The Manual Workshop serializer and complete operation catalog
- Opening advancements, Recipe Book rewards, localized feedback, and tooltips
- Production-JAR exclusion of development-only GameTest code

Keep expected values literal. A test must not calculate its expected result from
the same resource it is meant to validate.

## Live NeoForge GameTests

GameTests are grouped by gameplay system under
`src/gameTest/java/dev/fishraposo/materialprogression/gametest/`.

- `GameTestSupport` contains cross-system helpers.
- `CrusherFixture` and `WorkshopFixture` own repeated block-entity setup.
- `CrusherGameTests` verifies fuel processing and sided inventory.
- `PrimitiveGameTests` verifies ground-resource support and drops.
- `GroundStickFeatureGameTests` verifies registered configured-feature density
  separation near trees, shrub and datapack anchor recognition, bare/cave
  rejection without a background chance, protected shrub and ground-resource
  targets, inclusive surface and anchor-search bounds, ordinary replaceable
  cover, placement bounds, codec bounds, and exact support-removal drops.
- `LogHarvestGameTests` verifies the configurable log-only tool rule.
- `StoneFamilyGameTests` drives the registered Loose Rocks configured feature
  across all sixteen raw supports, direct Sand surfaces, covered sources, named
  cave/Nether/End supports, Soul Sand/Soul Soil over Netherrack and rejected
  over Basalt/Blackstone, and the no-family case, then verifies support changes
  and cobbling behavior.
- `ThirdPartyStoneFamilyGameTests` verifies external registry objects and
  namespaced family IDs through reload validation, cobbling, direct and covered
  placement, persistence, support invalidation, exact player/explosion drops,
  creative suppression, Fortune and Silk Touch, capability rejection,
  structured fallback names, and changed/removed/incompatible-family
  reconciliation without replacing the Rock stored before a reload. Its strict
  reload test drives real pack resources through listener preparation,
  validation, and application, then proves malformed syntax, malformed shape,
  an out-of-range modifier, duplicate sources, and duplicate surfaces leave the
  exact catalog version and placed external Rock untouched. Delayed
  global-catalog mutations run in isolated batches.
- Geology-focused tests verify depth bands, modifiers, exposure, correct-tool
  drops, Fortune, Silk Touch, config toggles, persistent placed-stone markers,
  and piston transfer.
- `GeologyDimensionProfileGameTests` drives production reload-listener
  preparation and application through custom-dimension boundaries, immediate
  replacement, removal, duplicate ownership, malformed schema retention,
  built-in protection, family and exposure shifts, clamping, and unconfigured
  L0 fallback. Its global profile mutation runs in an isolated batch.
- Tool tests verify Plant Fiber harvesting and Knife/Hammer/Saw category
  behavior.
- Workshop tests verify recipe matching, timing, persistence, output blocking,
  recipe reset, atomic completion and breakage, wood-species preservation, and
  automation rejection.
- `DiscoverabilityGameTests` verifies localized lore, structured feedback,
  throttled log hints, the Dense-geology advancement, the dedicated Manual
  Workshop recipe category, and real inventory-triggered Recipe Book unlocks.

The current opening branch runs 131 live GameTests. Treat that count as a
snapshot, not a reason to avoid adding the next regression test.

GameTests must use real registries, recipes, inventories, blocks, and server
ticks. Test-only setup stays in fixtures; production classes must not gain
methods solely for tests. Reload-sensitive behavior uses the real
`RecipeManager` application path, and ticker wiring has sequence-driven tests
that advance level ticks.

## Known verification boundaries

Automated tests validate the registered attachment codec and live save,
removal, and piston behavior. The GameTest structure does not currently perform
a literal survival-world chunk unload/reload cycle for placed-stone markers.
That persistence boundary should be included in manual release verification or
in a future harness that can safely unload the containing chunk.

Server GameTests cannot judge the Manual Workshop's client rendering, UI
clarity, sound balance, particle restraint, translated text presentation, or
the feel of the opening. Before publishing 0.2.0, run a real client and
complete a 20-30 minute survival path through Bronze and a Dense-geology
encounter.

## Adding behavior

For every gameplay change:

1. Name the production regression the test should catch.
2. Write the smallest relevant contract or GameTest.
3. Run it and confirm it fails for the intended reason.
4. Implement the behavior.
5. Run the focused test.
6. Run `./gradlew headlessTest`.

Do not add placeholder tests for systems that exist only in design documents.
Add tests alongside implementation.

## CI and diagnostics

GitHub Actions keeps contracts, the Gradle build, and GameTests as separately
named steps. GameTest logs and results are uploaded even when the live server
fails.

Automation establishes deterministic correctness. Progression feel, client
presentation, interface clarity, and balance remain human playtesting
responsibilities.
