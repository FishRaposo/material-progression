# Playable Technical Foundations Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Ship the complete technical foundation with primitive progression, a usable Workshop, atomic batching, a usable Bulk Crafting Table, ecosystem hardening, release automation, and a creative tab containing every Material Progression item and block.

**Architecture:** Each phase introduces one narrowly scoped production boundary and immediately exercises it through permanent survival gameplay. Pure planners and transactions stay independent of Minecraft UI code; menus and payloads remain server-authoritative; all interchangeable inputs use vanilla or `c:` tags. Generated regular resources live under `src/generated/resources`, while exceptional worldgen and hand-authored assets remain under `src/main/resources`.

**Tech Stack:** Java 25, Minecraft 26.2, NeoForge 26.2.0.35-beta, ModDevGradle 2.0.142, NeoForge GameTest/Test Framework, Python `unittest` contracts, Gradle 9.2.1, GitHub Actions.

## Global Constraints

- Every foundation must have a permanent survival-usable gameplay consumer.
- Every registered item and block must appear in `material_progression:main`; Loose Rocks and Ground Stick receive testing block items.
- Tags are the modern Ore Dictionary: publish and consume vanilla or `c:` tags wherever identity is interchangeable.
- Client payloads express intent only; the server resolves recipes, inventories, quantities, and mutations.
- Failed operations are atomic and consume nothing.
- Block-entity schemas are versioned and migrations are incremental and idempotent.
- Every behavior begins with a focused failing JVM test or GameTest; every resource invariant begins with a failing Python contract.
- The final `dist/material-progression-0.1.0.jar` must be byte-identical to the tested production build and contain no GameTest code.

---

### Task 1: Primitive catalog, creative tab, and deterministic datagen

**Files:**
- Modify: `src/main/java/dev/fishraposo/materialprogression/registry/ModItems.java`
- Create: `src/main/java/dev/fishraposo/materialprogression/data/MaterialFamily.java`
- Create: `src/main/java/dev/fishraposo/materialprogression/data/MaterialFamilies.java`
- Create: `src/main/java/dev/fishraposo/materialprogression/data/MaterialProgressionData.java`
- Modify: `src/main/java/dev/fishraposo/materialprogression/MaterialProgression.java`
- Modify: `build.gradle`
- Create/modify: `src/generated/resources/**`
- Test: `tests/content_contracts.py`
- Test: `tests/test_resources.py`

**Interfaces:**
- Produces: `MaterialFamilies.ALL`, an immutable catalog for flint, tin, and bronze; `ModItems.creativeTabContents()` as the ordered testing catalog.
- Consumes: existing deferred item/block registrations.

- [ ] Add a contract asserting that every `SHIPPED_ITEM` plus testing forms for every block is represented exactly once in the creative-tab catalog; run `./gradlew contractTest` and observe failure because ground resources are absent.
- [ ] Add block items for `loose_rocks` and `ground_stick`, rename `WORLD_ONLY_BLOCKS` to `WORLD_PLACED_BLOCKS`, and build `creativeTabContents()` in progression order; rerun the focused contract.
- [ ] Add literal material-family contracts for flint, tin, and bronze and observe failure because no declarative catalog exists.
- [ ] Implement immutable `MaterialFamily` definitions and migrate tool material construction and regular generated resource inputs to them without changing balance.
- [ ] Register NeoForge gather-data providers for regular recipes, tags, models, blockstates, loot, and translations; generate into `src/generated/resources`.
- [ ] Run data generation twice and verify `git diff --exit-code src/generated/resources` after the second run.
- [ ] Commit the complete creative-tab/datagen catalog.

### Task 2: Primitive progression-rule registry

**Files:**
- Create: `src/main/java/dev/fishraposo/materialprogression/progression/HarvestRule.java`
- Create: `src/main/java/dev/fishraposo/materialprogression/progression/HarvestRuleRegistry.java`
- Create: `src/main/java/dev/fishraposo/materialprogression/progression/KnifePlantHarvestRule.java`
- Create: `src/main/java/dev/fishraposo/materialprogression/progression/StoneHarvestRule.java`
- Modify: `src/main/java/dev/fishraposo/materialprogression/progression/LogHarvestRule.java`
- Modify: `src/main/java/dev/fishraposo/materialprogression/progression/HarvestRuleEvents.java`
- Modify: `src/main/java/dev/fishraposo/materialprogression/config/MaterialProgressionConfig.java`
- Modify: `src/main/java/dev/fishraposo/materialprogression/registry/ModItems.java`
- Modify: `src/main/java/dev/fishraposo/materialprogression/registry/ModTags.java`
- Create/modify: primitive recipes, tags, models, translations, and loot under `src/generated/resources`
- Test: `src/gameTest/java/dev/fishraposo/materialprogression/gametest/PrimitiveProgressionGameTests.java`

**Interfaces:**
- Produces: `HarvestRule.evaluate(HarvestContext)` and ordered `HarvestRuleRegistry`; `flint_knife`, `plant_fiber`, `#c:tools/knives`, and `#c:fibers/plant`.
- Consumes: versioned server config and the creative-tab catalog.

- [ ] Add GameTests for knife plant harvest, ordinary plant harvest, natural-stone Rock drops, config opt-outs, and knife durability; run and observe the missing items/rules.
- [ ] Add validated versioned config defaults for log gating, knife harvesting, and stone-to-Rock harvesting.
- [ ] Generalize the event dispatcher to ordered harvest rules while preserving another mod's denial and the existing narrow log behavior.
- [ ] Register the knife and fiber, their shared tags, the `Rock + stick -> flint knife` recipe, and `3 fiber -> string`.
- [ ] Implement plant and stone rules through the production event path; ensure non-natural stone variants and non-knife tools retain vanilla behavior.
- [ ] Tune ground-stick placed-feature data with a tree/shrub-biased feature plus a low-density biome-independent fallback.
- [ ] Run contracts and live GameTests; commit the complete primitive loop.

### Task 3: Manual-processing recipe and atomic transaction

**Files:**
- Create: `src/main/java/dev/fishraposo/materialprogression/world/item/crafting/ManualProcessingRecipe.java`
- Modify: `src/main/java/dev/fishraposo/materialprogression/registry/ModRecipes.java`
- Create: `src/main/java/dev/fishraposo/materialprogression/transaction/InventoryView.java`
- Create: `src/main/java/dev/fishraposo/materialprogression/transaction/ItemTransaction.java`
- Create: `src/test/java/dev/fishraposo/materialprogression/transaction/ItemTransactionTest.java`
- Create: `src/test/java/dev/fishraposo/materialprogression/world/item/crafting/ManualProcessingRecipeTest.java`
- Create: `src/generated/resources/data/material_progression/recipe/manual_processing/*.json`

**Interfaces:**
- Produces: `ManualProcessingRecipe(toolTag, input, result, durabilityCost, operationTime)`; `ItemTransaction.simulate()` and `commit()` with an immutable preview.
- Consumes: tag ingredients and ordinary `Container`/item-handler slots.

- [ ] Write fast JVM tests for matching tags, output capacity, insufficient durability, remainders, deterministic slot order, and rollback; run and observe missing types.
- [ ] Register the map/stream codecs and data-driven recipe type.
- [ ] Implement simulation as an immutable slot-delta plan and commit only after revalidation against the same inventory revision.
- [ ] Add manual recipes for knife, hammer, and saw operations using shared input/tool tags.
- [ ] Run JVM tests and resource contracts; commit the reusable recipe/transaction core.

### Task 4: Usable Workshop

**Files:**
- Create: `world/level/block/WorkshopBlock.java`
- Create: `world/level/block/entity/WorkshopBlockEntity.java`
- Create: `world/inventory/WorkshopMenu.java`
- Create: `client/WorkshopScreen.java`
- Create: `network/SelectWorkshopRecipePayload.java`
- Modify: registry classes for the block, item, block entity, menu, payload, and creative tab
- Create: generated Workshop blockstate, model, loot, recipe, translations, and tool assets
- Test: `src/gameTest/java/dev/fishraposo/materialprogression/gametest/WorkshopGameTests.java`

**Interfaces:**
- Produces: persistent three-slot Workshop, server-resolved recipe selection, single-operation execution.
- Consumes: `ManualProcessingRecipe` and `ItemTransaction`.

- [ ] Add GameTests covering every knife/hammer/saw operation and atomic failures; run and observe missing Workshop.
- [ ] Register the block, entity, menu, screen, recipe, hammer, and saw with complete assets/tags/creative-tab entries.
- [ ] Implement versioned Workshop save data with tool/input/output slots and selected recipe ID.
- [ ] Implement the stonecutter-like selection menu; reject client selections not matching the current server recipe list.
- [ ] Execute operations through `ItemTransaction`; never mutate slots in screen or payload code.
- [ ] Run Workshop GameTests and `headlessTest`; commit the usable Workshop.

### Task 5: Workshop batching and synchronized previews

**Files:**
- Create: `transaction/OperationPreview.java`
- Create: `network/RequestWorkshopPreviewPayload.java`
- Create: `network/ExecuteWorkshopBatchPayload.java`
- Modify: Workshop block entity, menu, and screen
- Test: transaction JVM tests and `WorkshopBatchGameTests.java`

**Interfaces:**
- Produces: sequence-numbered preview with requested, executable, consumed, produced, durability, remainders, and revision; all-or-nothing batch execution.
- Consumes: Workshop inventory revision and `ItemTransaction`.

- [ ] Add JVM tests for maximum batch calculation and GameTests for stale revision, close/reopen, full output, changed input, and exact multi-operation durability; observe failures.
- [ ] Calculate previews server-side from immutable inventory snapshots and monotonically increasing menu sequences.
- [ ] Add quantity controls and preview rows to the screen.
- [ ] Re-resolve and revalidate every accepted batch immediately before commit.
- [ ] Run focused tests and live GameTests; commit synchronized batching.

### Task 6: Recipe graph planner

**Files:**
- Create: `planner/RecipeGraph.java`
- Create: `planner/CraftingPlanner.java`
- Create: `planner/PlanRequest.java`
- Create: `planner/CraftingPlan.java`
- Create: `planner/PlanningFailure.java`
- Create: `src/test/java/dev/fishraposo/materialprogression/planner/CraftingPlannerTest.java`

**Interfaces:**
- Produces: bounded deterministic plans over ordinary crafting recipes with literal base costs, reused intermediates, surplus, remainders, and explicit failures.
- Consumes: recipe snapshots, tag alternatives, inventory snapshots, and validated planner limits.

- [ ] Add literal JVM fixtures for logs-to-sticks chain, existing intermediates, recipe batch surplus, mixed tag alternatives, container remainder, alternate recipes, missing ingredients, depth/node limits, and a cycle; run and observe missing planner.
- [ ] Build a recipe graph from crafting recipes only and sort alternatives by already-available material then stable registry ID.
- [ ] Implement recursive expansion with memoized remaining demand, surplus reuse, cycle path tracking, and bounded nodes/depth.
- [ ] Produce immutable plan/failure records without mutating inventories.
- [ ] Run the full planner suite; commit the pure planner.

### Task 7: Usable Bulk Crafting Table

**Files:**
- Create: `world/level/block/BulkCraftingTableBlock.java`
- Create: `world/level/block/entity/BulkCraftingTableBlockEntity.java`
- Create: `world/inventory/BulkCraftingTableMenu.java`
- Create: `client/BulkCraftingTableScreen.java`
- Create: table preview/execute network payloads
- Create: `world/item/BulkCraftingUpgradeItem.java`
- Modify: all registry and creative-tab catalogs
- Create: generated assets, recipes, loot, translations, and five module families
- Test: `BulkCraftingTableGameTests.java`

**Interfaces:**
- Produces: searchable recipe selection, requested quantities, internal storage, adjacent inventory discovery, hopper IO, upgrade budget, and atomic planned crafting.
- Consumes: `CraftingPlanner`, `ItemTransaction`, directly adjacent item handlers, and server-authoritative previews.

- [ ] Add GameTests for the logs-to-final-item chain, adjacent inventories, hopper IO, surplus, tags, modules, persistence, and atomic failures; observe missing table.
- [ ] Register the table, persistent entity, menu/screen/payloads, and storage/filter/priority/reservation/memory modules.
- [ ] Discover only player inventory, internal slots, and six directly adjacent supported handlers in deterministic order.
- [ ] Build previews server-side, display costs/missing/surplus/max quantity, and execute the exact revalidated plan transactionally.
- [ ] Enforce fixed upgrade-slot budget; stack quantity modules and treat binary modules as enabled once; replace lower tiers with higher tiers.
- [ ] Run planner/JVM tests, GameTests, and `headlessTest`; commit the usable table.

### Task 8: Interoperability companion and migration fixtures

**Files:**
- Create: `src/compatTest/java/dev/fishraposo/materialprogressioncompat/**`
- Create: `src/compatTest/resources/**`
- Modify: `build.gradle`
- Create: `persistence/SchemaMigration.java`
- Modify: Workshop and Bulk Crafting Table persistence
- Test: mixed-mod and migration GameTests

**Interfaces:**
- Produces: development-only foreign Rock, fiber, tin, bronze, tools, log, and recipe provider; schema migration results with recoverable disabled state.
- Consumes: public shared tags and production recipe APIs only.

- [ ] Add mixed-mod GameTests proving foreign inputs work in primitive crafting, harvesting, Workshop, Crusher, repair, and Bulk Crafting; observe missing companion content.
- [ ] Add an isolated development-only companion source set and mod descriptor without production dependencies on its namespace.
- [ ] Add previous-schema fixtures and GameTests asserting inventories, selections, upgrades, reservations, priorities, and jobs survive migration.
- [ ] Implement incremental idempotent migrations; invalid records preserve inventories and mark the machine disabled with a visible reason.
- [ ] Run mixed-mod and migration tests and inspect the production JAR to prove companion/test classes are absent; commit ecosystem compatibility.

### Task 9: Optional JEI integration

**Files:**
- Modify: `build.gradle`
- Create: `compat/jei/MaterialProgressionJeiPlugin.java`
- Create: `compat/jei/WorkshopRecipeCategory.java`
- Create: `compat/jei/CrushingRecipeCategory.java`
- Create: JEI category textures/translations
- Test: `tests/test_distribution.py`

**Interfaces:**
- Produces: optional Workshop/crushing recipe categories, catalysts, and supported transfer/help affordances.
- Consumes: public JEI 26.2 NeoForge API as `compileOnly`; no hard runtime dependency.

- [ ] Pin an available JEI version compatible with Minecraft 26.2 and add a packaging contract proving JEI implementation classes link optionally.
- [ ] Register complete Workshop and Crusher displays plus their catalysts.
- [ ] Verify a no-JEI GameTest server starts and a JEI-enabled client smoke run reaches title screen.
- [ ] Commit optional recipe discovery integration.

### Task 10: Release automation, documentation, and exact artifact

**Files:**
- Create: `.github/workflows/release.yml`
- Create: `CHANGELOG.md`
- Modify: `.github/workflows/build.yml`
- Modify: `README.md`, `AGENTS.md`, `docs/ROADMAP.md`, `docs/PRIMITIVE_RESOURCES.md`, `docs/INFRASTRUCTURE.md`, `docs/TESTING.md`
- Modify: `dist/material-progression-0.1.0.jar`

**Interfaces:**
- Produces: tag-triggered GitHub Release for an exact verified version/JAR and accurate shipped-status documentation.
- Consumes: `mod_version`, changelog heading, built JAR, and complete verification suite.

- [ ] Add workflow-contract tests for tag/version/changelog agreement, exact `headlessTest`, SHA-256 generation, and GitHub Release upload; observe missing workflow.
- [ ] Implement the release workflow without Modrinth/CurseForge credentials or publication.
- [ ] Update living docs and manual survival checklists to match shipped behavior and keep unresolved geology/logistics/metals clearly planned.
- [ ] Run data generation twice, then `./gradlew syncDistributionJar`.
- [ ] Run fresh `./gradlew headlessTest` on the final tree and inspect the JAR for all gameplay resources and no development-only classes.
- [ ] Review `git diff --check`, direct material inputs, creative-tab completeness, schema versions, and optional-dependency boundaries.
- [ ] Commit the exact source/docs/JAR release candidate and publish only the feature branch for CI.
