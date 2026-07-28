# Primitive Foundations Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use
> superpowers:subagent-driven-development (recommended) or
> superpowers:executing-plans to implement this plan task-by-task. Steps use
> checkbox (`- [ ]`) syntax for tracking.

**Goal:** Ship a deterministic Rock-and-stick bootstrap, an axe-capable flint
hatchet, and the default-enabled server-configurable log harvest rule.

**Architecture:** Resource contracts define the complete content and worldgen
surface before implementation. `GroundResourceBlock` supplies the shared
plant-like block behavior, while separate data-driven random-patch features
control Rock and stick distribution. `MaterialProgressionConfig` owns the
synced server setting, `LogHarvestRule` owns the pure decision, and one event
adapter applies it to NeoForge's harvest check without canceling block breaks.

**Tech Stack:** Minecraft 26.2, NeoForge 26.2.0.35-beta, Java 25, Gradle
ModDevPlugin, JSON data/resources, Python `unittest`, NeoForge GameTests.

## Global Constraints

- Minecraft is exactly 26.2 and NeoForge is exactly 26.2.0.35-beta.
- Java language level is 25.
- The mod ID is `material_progression`.
- The log rule defaults to enabled and is a synced per-world/server option.
- The log rule applies only to `#minecraft:logs`.
- Valid log tools belong to `#minecraft:axes`; do not check concrete classes.
- Loose rocks and ground sticks are low, non-colliding, easy hand-breakable,
  persistent world blocks with no block-item registration.
- Loose rocks drop one `material_progression:rock`; ground sticks drop one
  `minecraft:stick`.
- Rock is published and consumed through `#c:rocks`.
- Flint shard is published and consumed through `#c:flint_shards`.
- Wooden sticks are consumed through `#c:rods/wooden`.
- Every inventory item has an item definition plus `en_us` and `pt_br`
  translations; every world block has a blockstate, model, loot table, and
  translations.
- No production class receives a test-only method.
- Production resources and the versioned JAR in `dist/` must be synchronized
  before release.

---

### Task 1: Primitive resource contracts

**Files:**
- Modify: `tests/content_contracts.py`
- Modify: `tests/test_resources.py`
- Modify: `tests/test_docs.py`

**Interfaces:**
- Consumes: `ResourceTree.load_json`, `ResourceTree.recipe`,
  `SHIPPED_ITEMS`, and `SHIPPED_BLOCKS`.
- Produces: literal catalogs `WORLD_ONLY_BLOCKS`, `PRIMITIVE_RECIPES`, and
  `SURFACE_WORLDGEN_FEATURES` used by all later resource assertions.

- [ ] **Step 1: Add the failing content catalogs**

Add these literal expectations to `tests/content_contracts.py`:

```python
WORLD_ONLY_BLOCKS = {"ground_stick", "loose_rocks"}

PRIMITIVE_RECIPES = {
    "cobblestone_from_rocks": {
        "type": "minecraft:crafting_shapeless",
        "ingredients": ["#c:rocks"] * 4,
        "result": {"id": "minecraft:cobblestone"},
    },
    "flint_hatchet": {
        "type": "minecraft:crafting_shaped",
        "pattern": ["RS", " S"],
        "key": {"R": "#c:flint_shards", "S": "#c:rods/wooden"},
        "result": {"id": "material_progression:flint_hatchet"},
    },
    "flint_shard_from_flint": {
        "type": "minecraft:crafting_shapeless",
        "ingredients": ["minecraft:flint"],
        "result": {"count": 2, "id": "material_progression:flint_shard"},
    },
    "flint_shard_from_rock": {
        "type": "minecraft:crafting_shapeless",
        "ingredients": ["#c:rocks"],
        "result": {"id": "material_progression:flint_shard"},
    },
}

SURFACE_WORLDGEN_FEATURES = {
    "ground_stick": "material_progression:ground_stick",
    "loose_rocks": "material_progression:loose_rocks",
}
```

Add `rock`, `flint_shard`, and `flint_hatchet` to `SHIPPED_ITEMS`; add
`ground_stick` and `loose_rocks` to `SHIPPED_BLOCKS`.

- [ ] **Step 2: Add failing behavioral resource assertions**

In `tests/test_resources.py`, make the item-completeness test compare item
definitions only with inventory items, then add:

```python
def test_world_only_blocks_have_no_inventory_form(self):
    item_models = TREE.names_matching(ASSETS / "items", "*.json")
    self.assertTrue(WORLD_ONLY_BLOCKS.isdisjoint(item_models))

def test_primitive_recipes_preserve_the_bootstrap(self):
    for name, expected in PRIMITIVE_RECIPES.items():
        with self.subTest(recipe=name):
            self.assertEqual(expected, TREE.recipe(name))

def test_surface_resources_have_complete_worldgen_chains(self):
    for name, block_id in SURFACE_WORLDGEN_FEATURES.items():
        with self.subTest(feature=name):
            configured = TREE.load_json(
                DATA / "worldgen" / "configured_feature" / f"{name}.json"
            )
            placed = TREE.load_json(
                DATA / "worldgen" / "placed_feature" / f"{name}.json"
            )
            modifier = TREE.load_json(
                DATA / "neoforge" / "biome_modifier" / f"add_{name}.json"
            )
            self.assertEqual("minecraft:random_patch", configured["type"])
            encoded = json.dumps(configured, sort_keys=True)
            self.assertIn(block_id, encoded)
            self.assertEqual(f"material_progression:{name}", placed["feature"])
            self.assertEqual(
                f"material_progression:{name}", modifier["features"]
            )
            self.assertEqual("vegetal_decoration", modifier["step"])
```

Extend common-tag assertions with:

```python
"flint_shards": ["material_progression:flint_shard"],
"rocks": ["material_progression:rock"],
```

Extend the vanilla axe-tag expectation with `flint_hatchet`.

- [ ] **Step 3: Run the focused contracts and verify RED**

Run:

```bash
python -m unittest tests.test_resources.ResourceContractTests -v
```

Expected: failures for missing Rock, flint shard, flint hatchet, world-only
blocks, primitive recipes, tags, loot, and worldgen resources.

- [ ] **Step 4: Commit the failing contracts**

```bash
git add tests/content_contracts.py tests/test_resources.py tests/test_docs.py
git commit -m "test: define primitive bootstrap contracts"
```

### Task 2: Rock, flint shard, and flint hatchet

**Files:**
- Modify: `src/main/java/dev/fishraposo/materialprogression/registry/ModItems.java`
- Modify: `src/main/java/dev/fishraposo/materialprogression/registry/ModTags.java`
- Create: `src/main/resources/assets/material_progression/items/rock.json`
- Create: `src/main/resources/assets/material_progression/items/flint_shard.json`
- Create: `src/main/resources/assets/material_progression/items/flint_hatchet.json`
- Modify: `src/main/resources/assets/material_progression/lang/en_us.json`
- Modify: `src/main/resources/assets/material_progression/lang/pt_br.json`
- Create: `src/main/resources/data/c/tags/item/rocks.json`
- Create: `src/main/resources/data/c/tags/item/flint_shards.json`
- Create: `src/main/resources/data/material_progression/tags/block/incorrect_for_flint_tool.json`
- Modify: `src/main/resources/data/minecraft/tags/item/axes.json`
- Modify: `src/main/resources/data/minecraft/tags/item/enchantable/durability.json`
- Modify: `src/main/resources/data/minecraft/tags/item/enchantable/mining.json`
- Create: `src/main/resources/data/material_progression/recipe/cobblestone_from_rocks.json`
- Create: `src/main/resources/data/material_progression/recipe/flint_hatchet.json`
- Create: `src/main/resources/data/material_progression/recipe/flint_shard_from_flint.json`
- Create: `src/main/resources/data/material_progression/recipe/flint_shard_from_rock.json`

**Interfaces:**
- Consumes: `ModTags.commonItemTag(String)` and NeoForge's `ToolMaterial`.
- Produces: `ModItems.ROCK`, `ModItems.FLINT_SHARD`,
  `ModItems.FLINT_HATCHET`, `ModItems.FLINT`, `ModTags.ROCKS`, and
  `ModTags.FLINT_SHARDS`.

- [ ] **Step 1: Register the minimal primitive item family**

Add:

```java
public static final ToolMaterial FLINT = new ToolMaterial(
        ModTags.INCORRECT_FOR_FLINT_TOOL,
        64,
        5.0F,
        1.5F,
        5,
        ModTags.FLINT_SHARDS
);

public static final DeferredItem<Item> ROCK =
        ITEMS.registerSimpleItem("rock");
public static final DeferredItem<Item> FLINT_SHARD =
        ITEMS.registerSimpleItem("flint_shard");
public static final DeferredItem<AxeItem> FLINT_HATCHET = ITEMS.registerItem(
        "flint_hatchet",
        properties -> new AxeItem(FLINT, 5.0F, -3.2F, properties)
);
```

Include all three in the Material Progression creative tab.

- [ ] **Step 2: Publish the common and vanilla tags**

Add `ROCKS`, `FLINT_SHARDS`, and `INCORRECT_FOR_FLINT_TOOL` to `ModTags`.
Create literal tag JSONs containing the new item IDs. Add the hatchet to
`minecraft:axes`, durability-enchantable, and mining-enchantable tags.

- [ ] **Step 3: Add the four inventory recipes**

Create the literal JSON payloads from `PRIMITIVE_RECIPES`. Do not use the
concrete Rock or stick IDs as recipe inputs.

- [ ] **Step 4: Add item definitions and translations**

Use generated-item models with the existing vanilla cobblestone/flint/stone-axe
textures as temporary visual sources. Add:

```json
"item.material_progression.rock": "Rock",
"item.material_progression.flint_shard": "Flint Shard",
"item.material_progression.flint_hatchet": "Flint Hatchet"
```

and Brazilian Portuguese equivalents:

```json
"item.material_progression.rock": "Pedra",
"item.material_progression.flint_shard": "Lasca de Sílex",
"item.material_progression.flint_hatchet": "Machadinha de Sílex"
```

- [ ] **Step 5: Run the focused contracts and verify GREEN for items**

Run:

```bash
python -m unittest \
  tests.test_resources.ResourceContractTests.test_every_shipped_item_has_models_and_translations \
  tests.test_resources.ResourceContractTests.test_primitive_recipes_preserve_the_bootstrap \
  tests.test_resources.ResourceContractTests.test_materials_are_published_under_common_tags \
  tests.test_resources.ResourceContractTests.test_tool_enchantment_tags_cover_every_tool -v
```

Expected: all selected tests pass.

- [ ] **Step 6: Commit the primitive items**

```bash
git add src/main tests
git commit -m "feat: add primitive rock and flint hatchet"
```

### Task 3: Ground-resource blocks and world generation

**Files:**
- Create: `src/main/java/dev/fishraposo/materialprogression/world/level/block/GroundResourceBlock.java`
- Modify: `src/main/java/dev/fishraposo/materialprogression/registry/ModBlocks.java`
- Create: `src/main/resources/assets/material_progression/blockstates/loose_rocks.json`
- Create: `src/main/resources/assets/material_progression/blockstates/ground_stick.json`
- Create: `src/main/resources/assets/material_progression/models/block/loose_rocks.json`
- Create: `src/main/resources/assets/material_progression/models/block/ground_stick.json`
- Create: `src/main/resources/data/material_progression/loot_table/blocks/loose_rocks.json`
- Create: `src/main/resources/data/material_progression/loot_table/blocks/ground_stick.json`
- Create: `src/main/resources/data/material_progression/worldgen/configured_feature/loose_rocks.json`
- Create: `src/main/resources/data/material_progression/worldgen/configured_feature/ground_stick.json`
- Create: `src/main/resources/data/material_progression/worldgen/placed_feature/loose_rocks.json`
- Create: `src/main/resources/data/material_progression/worldgen/placed_feature/ground_stick.json`
- Create: `src/main/resources/data/material_progression/neoforge/biome_modifier/add_loose_rocks.json`
- Create: `src/main/resources/data/material_progression/neoforge/biome_modifier/add_ground_stick.json`
- Modify: `src/main/resources/assets/material_progression/lang/en_us.json`
- Modify: `src/main/resources/assets/material_progression/lang/pt_br.json`

**Interfaces:**
- Consumes: vanilla `BushBlock` survival behavior and data-driven
  `minecraft:random_patch`.
- Produces: `ModBlocks.LOOSE_ROCKS`, `ModBlocks.GROUND_STICK`, and two complete
  surface-worldgen chains.

- [ ] **Step 1: Add the shared ground-resource behavior**

Implement:

```java
public final class GroundResourceBlock extends BushBlock {
    private final VoxelShape shape;

    public GroundResourceBlock(VoxelShape shape, Properties properties) {
        super(properties);
        this.shape = shape;
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return shape;
    }

    @Override
    protected boolean mayPlaceOn(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return state.isFaceSturdy(level, pos, Direction.UP);
    }
}
```

Register both blocks with `noCollission()`, `replaceable()`, `strength(0.2F)`,
and appropriate stone/wood sounds. Do not register block items.

- [ ] **Step 2: Add low-profile block models and literal loot**

Create several weighted rotations in each blockstate. Use low cuboid elements
in the two block models. The loose-rock loot table yields one
`material_progression:rock`; the ground-stick loot table yields one
`minecraft:stick`, both with explosion decay.

- [ ] **Step 3: Add data-driven surface patches**

Use `minecraft:random_patch` configured features containing inline
`minecraft:simple_block` features with `minecraft:would_survive` predicates.
Use outer placed features with `minecraft:count`, `minecraft:in_square`,
`minecraft:heightmap` (`MOTION_BLOCKING`), and `minecraft:biome`.

Attach loose rocks broadly to `#minecraft:is_overworld`. Attach ground sticks
to the same broad tag at a lower initial count so the bootstrap is not
forest-biome gated; use additional attempts inside the configured patch so
valid terrain near vegetation receives clusters. Both biome modifiers use
`vegetal_decoration`.

- [ ] **Step 4: Run the focused contracts and verify GREEN for world resources**

Run:

```bash
python -m unittest \
  tests.test_resources.ResourceContractTests.test_every_shipped_block_has_blockstate_and_loot \
  tests.test_resources.ResourceContractTests.test_world_only_blocks_have_no_inventory_form \
  tests.test_resources.ResourceContractTests.test_surface_resources_have_complete_worldgen_chains \
  tests.test_resources.ResourceContractTests.test_every_json_resource_parses -v
```

Expected: all selected tests pass.

- [ ] **Step 5: Commit the surface resources**

```bash
git add src/main tests docs
git commit -m "feat: generate loose rocks and ground sticks"
```

### Task 4: Server config and centralized log-harvest rule

**Files:**
- Create: `src/main/java/dev/fishraposo/materialprogression/config/MaterialProgressionConfig.java`
- Create: `src/main/java/dev/fishraposo/materialprogression/progression/LogHarvestRule.java`
- Create: `src/main/java/dev/fishraposo/materialprogression/progression/HarvestRuleEvents.java`
- Modify: `src/main/java/dev/fishraposo/materialprogression/MaterialProgression.java`
- Modify: `src/main/resources/assets/material_progression/lang/en_us.json`
- Modify: `src/main/resources/assets/material_progression/lang/pt_br.json`

**Interfaces:**
- Produces:
  `MaterialProgressionConfig.requireAxeForLogs(): boolean`,
  `LogHarvestRule.canHarvest(boolean, boolean, BlockState, ItemStack): boolean`,
  and `HarvestRuleEvents.register()`.

- [ ] **Step 1: Implement the synced server configuration**

Create a `ModConfigSpec` with:

```java
private static final ModConfigSpec.Builder BUILDER =
        new ModConfigSpec.Builder();
static final ModConfigSpec.BooleanValue REQUIRE_AXE_FOR_LOGS = BUILDER
        .comment("Require an item in #minecraft:axes for logs to drop.")
        .translation(
                "config.material_progression.server.requireAxeForLogs"
        )
        .define("requireAxeForLogs", true);
public static final ModConfigSpec SPEC = BUILDER.build();

public static boolean requireAxeForLogs() {
    return REQUIRE_AXE_FOR_LOGS.get();
}
```

Inject `ModContainer` into the mod constructor and register `SPEC` with
`ModConfig.Type.SERVER`.

- [ ] **Step 2: Implement the pure policy**

```java
public static boolean canHarvest(
        boolean vanillaCanHarvest,
        boolean requireAxeForLogs,
        BlockState state,
        ItemStack heldItem
) {
    if (!requireAxeForLogs || !state.is(BlockTags.LOGS)) {
        return vanillaCanHarvest;
    }
    if (heldItem.is(ItemTags.AXES)) {
        return vanillaCanHarvest;
    }
    return false;
}
```

This deliberately never upgrades a denial from Minecraft or another mod.

- [ ] **Step 3: Adapt one NeoForge harvest event**

Register a listener on `NeoForge.EVENT_BUS`. In the
`PlayerEvent.HarvestCheck` handler, pass the event's existing result, target
state, player's main-hand stack, and resolved config value into
`LogHarvestRule`, then set the returned harvest result. Do not cancel the event
or handle spawned drops.

- [ ] **Step 4: Add config translations**

Add localized labels for `requireAxeForLogs` in both language files.

- [ ] **Step 5: Compile production code**

Run:

```bash
./gradlew compileJava
```

Expected: compilation succeeds. If sandbox dependency caching blocks Gradle,
record the exact error and use the configured remote CI only after the resource
contracts are green and the branch is pushed.

- [ ] **Step 6: Commit the configuration and policy**

```bash
git add src/main
git commit -m "feat: require axes for log drops by default"
```

### Task 5: Live GameTests for the playable boundary

**Files:**
- Create: `src/gameTest/java/dev/fishraposo/materialprogression/gametest/PrimitiveFixture.java`
- Create: `src/gameTest/java/dev/fishraposo/materialprogression/gametest/PrimitiveGameTests.java`
- Create: `src/gameTest/java/dev/fishraposo/materialprogression/config/ConfigFixture.java`
- Create: `src/gameTest/java/dev/fishraposo/materialprogression/gametest/LogHarvestGameTests.java`
- Modify: `src/gameTest/java/dev/fishraposo/materialprogression/gametest/GameTestSupport.java`

**Interfaces:**
- Consumes: real block loot, recipes, tags, config spec, and player harvest
  checks.
- Produces: observable regression coverage for bootstrap drops, recipes,
  support survival, axe tagging, enabled rule, opt-out, plank boundary, and
  crimson-stem tag behavior.

- [ ] **Step 1: Write failing ground-resource GameTests**

Add tests that place stone support plus each resource, destroy it through the
helper, and assert exactly one expected dropped item. Add a scheduled test that
removes support and asserts the resource becomes air.

- [ ] **Step 2: Write failing recipe and hatchet GameTests**

Use the real recipe manager to assemble:

- one Rock into one flint shard;
- one flint into two shards;
- four Rocks into one cobblestone;
- the upside-down-L shard-and-stick pattern into one flint hatchet.

Assert `flintHatchet.is(ItemTags.AXES)`.

- [ ] **Step 3: Write failing log-boundary GameTests**

Create a real server player fixture and assert:

- enabled + empty hand + oak log => no oak-log drop;
- enabled + flint hatchet + oak log => one oak-log drop;
- disabled + empty hand + oak log => one oak-log drop;
- enabled + empty hand + oak planks => one plank drop;
- enabled + empty hand + crimson stem => no stem drop.

Keep raw config mutation in the game-test-only `ConfigFixture`, in the config
package so it can restore the package-private BooleanValue after every test.

- [ ] **Step 4: Run live tests and verify RED**

Run:

```bash
./gradlew runGameTestServer
```

Expected before final fixture/adapter corrections: at least one new test fails
for the missing observable behavior, not because the test cannot load.

- [ ] **Step 5: Make the fixtures and adapter minimally pass**

Adjust only production behavior required by the failing assertions. Keep player
creation, inventory cleanup, item-entity collection, and config restoration in
test fixtures.

- [ ] **Step 6: Run live tests and verify GREEN**

Run:

```bash
./gradlew runGameTestServer
```

Expected: all existing crusher/tool tests and all new primitive/log tests pass.

- [ ] **Step 7: Commit the live acceptance suite**

```bash
git add src/gameTest src/main
git commit -m "test: verify primitive opening in game"
```

### Task 6: Documentation, distribution, and full verification

**Files:**
- Modify: `AGENTS.md`
- Modify: `README.md`
- Modify: `docs/PRIMITIVE_RESOURCES.md`
- Modify: `docs/ROADMAP.md`
- Modify: `docs/TESTING.md`
- Modify: `dist/material-progression-0.1.0.jar`

**Interfaces:**
- Consumes: exact committed production tree.
- Produces: accurate shipped-status documentation and matching installable JAR.

- [ ] **Step 1: Update shipped-status documentation**

Move Rock, ground sticks, flint shards, flint hatchet, primitive recipes, config,
and the log rule from "designed" to "implemented." Keep knives, fiber, stone
drop replacement, and density tuning explicitly provisional.

- [ ] **Step 2: Run all fast contracts**

Run:

```bash
python -m unittest discover -s tests -p 'test_*.py' -v
```

Expected: every contract passes with zero failures.

- [ ] **Step 3: Build and refresh the distribution JAR**

Run:

```bash
./gradlew syncDistributionJar
```

Expected: `dist/material-progression-0.1.0.jar` matches the current production
sources and resources.

- [ ] **Step 4: Run the complete verification gate**

Run:

```bash
./gradlew headlessTest
```

Expected: contracts, Java build, distribution verification, and all live
NeoForge GameTests pass.

- [ ] **Step 5: Review compatibility and packaging**

Run:

```bash
git diff --check
git status --short
jar tf dist/material-progression-0.1.0.jar
```

Confirm no game-test classes ship, both `c:` tags ship, the two world blocks
have no block items, primitive recipes contain no concrete interchangeable
inputs, and only intentional files are changed.

- [ ] **Step 6: Commit the verified slice**

```bash
git add AGENTS.md README.md docs dist
git commit -m "docs: mark primitive foundations implemented"
```
