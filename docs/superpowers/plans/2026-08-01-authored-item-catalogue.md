# Authored Item Catalogue Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace every Material Progression inventory placeholder with an original 16 by 16 sprite, while preserving the existing Loose Rock and Ground Stick world visuals exactly.

**Architecture:** A checked-in standard-library Python source catalog creates original RGBA sprites and local generated-item models. Fast contracts validate each resource path, exact PNG encoding, opaque content and transparent corners. World-space ground-resource JSON assets receive SHA-256 regression guards and are not edited.

**Tech Stack:** Minecraft 26.2, NeoForge 26.2.0.35-beta, Java 25, Gradle, Python `unittest`, PNG via `struct` and `zlib`, JSON resources, and the built-in image generator for original visual studies.

## Global Constraints

- Preserve the existing Ground Stick and Loose Rock blockstates and block models byte-for-byte.
- Cover all 59 current `assets/material_progression/items/*.json` definitions; do not pre-create unimplemented items.
- Create original 16 by 16 RGBA PNG sprites with hard pixel edges, transparent corners, no anti-aliasing, no gradients, and no copied reference art.
- Use a local `material_progression:item/<id>` model for every shipped inventory item; no final item definition may reference a `minecraft:` model.
- Keep world block textures and models independent from block-item sprites.
- Future shipped item definitions must automatically be required to use the same local texture convention.
- Do not add runtime dependencies, gameplay changes, registrations, recipes, tags, or balance changes.
- Rebuild and validate the versioned JAR under `dist/` after changing production resources.

---

### Task 1: Native PNG, item-model, and ground-asset contracts

**Files:**

- Create: `tests/support/png.py`
- Create: `tests/test_item_art.py`
- Modify: `tests/content_contracts.py`
- Modify: `tests/test_resources.py`

**Interfaces:**

- Consumes: `SHIPPED_ITEMS`, `ResourceTree.load_json`, and asset roots already exposed by `ResourceTree`.
- Produces: `read_rgba8_png(path: Path) -> RgbaPng`, `assert_native_item_sprite(path: Path) -> None`, `AUTHORED_ITEM_GROUPS`, and reusable local-item assertions.

- [ ] **Step 1: Define literal art groups and preservation hashes**

Add this exact catalog to `tests/content_contracts.py`:

~~~python
AUTHORED_ITEM_GROUPS = {
    "rocks_and_cobbles": {
        "rock", "granite_rock", "diorite_rock", "andesite_rock",
        "deepslate_rock", "tuff_rock", "calcite_rock", "dripstone_rock",
        "sulfur_rock", "cinnabar_rock", "sandstone_rock",
        "red_sandstone_rock", "netherrack_rock", "basalt_rock",
        "blackstone_rock", "end_stone_rock",
        "cobbled_andesite", "cobbled_basalt", "cobbled_blackstone",
        "cobbled_calcite", "cobbled_cinnabar", "cobbled_diorite",
        "cobbled_dripstone", "cobbled_end_stone", "cobbled_granite",
        "cobbled_netherrack", "cobbled_red_sandstone",
        "cobbled_sandstone", "cobbled_sulfur", "cobbled_tuff",
    },
    "materials_and_workstations": {
        "flint_shard", "plant_fiber", "copper_dust", "raw_tin", "tin_dust",
        "tin_ingot", "bronze_dust", "bronze_ingot", "tin_ore",
        "deepslate_tin_ore", "crusher", "manual_workshop",
    },
    "tools": {
        "flint_hatchet", "flint_hammer", "flint_knife", "flint_saw",
        "tin_axe", "tin_hoe", "tin_pickaxe", "tin_shovel", "tin_sword",
        "bronze_axe", "bronze_hammer", "bronze_hoe", "bronze_knife",
        "bronze_pickaxe", "bronze_saw", "bronze_shovel", "bronze_sword",
    },
}
assert set().union(*AUTHORED_ITEM_GROUPS.values()) == SHIPPED_ITEMS
~~~

Add this full ground-resource baseline:

~~~python
WORLD_RESOURCE_ASSET_HASHES = {
    "blockstates/ground_stick.json": "c73fe9ee735fd4662ff2bb08e5fc9584726dd463dd7333627cc4402d5e9b0056",
    "blockstates/loose_rocks.json": "9e71acbab30bb2767e4cb82039f7835877ad923dc1f2c7916218e0b89d9c35e2",
    "models/block/ground_stick.json": "50a9612f18e41089d05dd677bed5b82e4ef5af96b287a525aefa3a0a544cf116",
    "models/block/loose_rocks/andesite.json": "16a3d7d88447f08fc24d71e05a2906da318d0b7b57c23c63106b61da66e1fd7c",
    "models/block/loose_rocks/basalt.json": "cdfb8687e3d938ddd34ca21596a17b5e3febdc608155e8f41ec702c47d3db648",
    "models/block/loose_rocks/blackstone.json": "6009c1f2680bdffa001173e429d163fb8f004798b259ab47996fc18803827207",
    "models/block/loose_rocks/calcite.json": "705e74033c8fef8cd5a13998bd6427decaeda84b669d1ee401744f136dcb1b42",
    "models/block/loose_rocks/cinnabar.json": "06e9c39088fc0b8a1e3dfb9821132ca9fa5a57bb3ca7e293de7d94c52f6e31d7",
    "models/block/loose_rocks/deepslate.json": "503312d0e2bb9f76bf62ba1349cf1367ba266effbca12d45674e534e579ee6aa",
    "models/block/loose_rocks/diorite.json": "0713d63cf730407edfbfeadfd70d5dbfb340440a14dda8f105f0a84bbf905bc1",
    "models/block/loose_rocks/dripstone.json": "02158d83bef828bb02829a25f4edcd64443d0bc7b8e2255d2a8da20dd4236fb1",
    "models/block/loose_rocks/end_stone.json": "b3a3b83491015eb63aaf9af82b07b49792acdca060aac533bcbfa44ca1f1ef4d",
    "models/block/loose_rocks/granite.json": "ceb7e0d5e8c660f8da37a24b9b3ffad5d9a751617280bbb1d1ee15cbb5dbb663",
    "models/block/loose_rocks/netherrack.json": "4922071c1d4586818437e84a06752d9b7beafdab6cf7fcc1016429ababada01b",
    "models/block/loose_rocks/red_sandstone.json": "f02f0f6f795f6a65823a7fccfa9407f268c7103a1b9863e5093c7568b9e08f44",
    "models/block/loose_rocks/sandstone.json": "7b625c9481f9ed6c782d6382f2629604d6c6dc378fa26d2c7c0471a664658281",
    "models/block/loose_rocks/stone.json": "7e3811ccdf82a7312cae49f8959c57999225811aff5d258a2fa5f0c57937286e",
    "models/block/loose_rocks/sulfur.json": "e4e264102ac33048a81983fe12d8ad16fd734a74a6def73478d26f0eff504054",
    "models/block/loose_rocks/tuff.json": "49e3eb03d5a07a661d323168bfe88f0a3d107d5de31135d6d71d6e7ead9ab613",
}
~~~

- [ ] **Step 2: Write failing sprite contracts**

Create `tests/support/png.py`:

~~~python
@dataclass(frozen=True)
class RgbaPng:
    width: int
    height: int
    pixels: bytes

def read_rgba8_png(path: Path) -> RgbaPng: ...
def assert_native_item_sprite(path: Path) -> None: ...
~~~

The parser must validate PNG signature, one IHDR, 8-bit RGBA color type, no interlace, concatenated IDAT bytes, filter byte zero per row, and decoded row length. The assertion must require 16 by 16, one opaque pixel, and alpha zero in all four corners.

Create `tests/test_item_art.py` with temporary valid/invalid PNG fixtures and a Rock-group test that requires each definition and model to equal:

~~~python
{"model": {"type": "minecraft:model", "model": "material_progression:item/<id>"}}
{"parent": "minecraft:item/generated", "textures": {"layer0": "material_progression:item/<id>"}}
~~~

Then call `assert_native_item_sprite` on `textures/item/<id>.png`.

- [ ] **Step 3: Run the focused contract and verify RED**

Run:

~~~powershell
python -m unittest tests.test_item_art -v
~~~

Expected: parser imports fail first; after its implementation, the Rock-group test fails because current item definitions point at vanilla models and local assets are absent.

- [ ] **Step 4: Implement the PNG helper and unchanged-ground check**

Implement the parser using only `dataclasses`, `hashlib`, `pathlib`, `struct`, and `zlib`. Hash each `WORLD_RESOURCE_ASSET_HASHES` path with `sha256(path.read_bytes()).hexdigest()` in `test_ground_resource_assets_are_unchanged`.

- [ ] **Step 5: Verify the helper is green and commit**

Run:

~~~powershell
python -m unittest tests.test_item_art.ItemArtContractTests.test_rgba_png_parser -v
python -m unittest tests.test_item_art.ItemArtContractTests.test_ground_resource_assets_are_unchanged -v
git add tests/content_contracts.py tests/support/png.py tests/test_item_art.py tests/test_resources.py
git commit -m "test: define authored item art contracts"
~~~

Expected: both selected tests pass; the Rock-group test remains intentionally red.

### Task 2: Original Rock and cobble inventory art

**Files:**

- Create: `tools/generate_item_art.py`
- Create: `docs/ITEM_ART.md`
- Create: `src/main/resources/assets/material_progression/textures/item/*.png` for the 30 Rock/cobble IDs
- Create: `src/main/resources/assets/material_progression/models/item/*.json` for the same IDs
- Modify: matching files under `src/main/resources/assets/material_progression/items/`
- Modify: `tests/test_item_art.py`

**Interfaces:**

- Consumes: `AUTHORED_ITEM_GROUPS["rocks_and_cobbles"]`.
- Produces: `make_sprite(item_id: str) -> tuple[tuple[RGBA, ...], ...]`, `encode_rgba_png(rows) -> bytes`, and `write_group(group: str, assets: Path) -> None`.

- [ ] **Step 1: Make original visual studies**

Use the built-in image generator twice: one original Rock/cobble sprite-sheet study and one primitive-material/tool study. Prompt for vanilla-adjacent pixel art, hard pixels, restrained palettes, no text, no logo, and no reference-mod artwork. Inspect the studies, but keep them outside the resource pack; they are art direction only.

- [ ] **Step 2: Encode the durable art language**

Create `docs/ITEM_ART.md` with exact future rules: 16 by 16 RGBA; upper-left light; transparent corners; selective lower/right outlines; rocks are one irregular chip; cobbles are two-to-four clustered chips; each material has shadow/base/highlight/optional accent; Sulfur and Cinnabar use no more than four accent pixels; no gradients, anti-aliasing, text, or copied art.

Create `tools/generate_item_art.py` with:

~~~python
RGBA = tuple[int, int, int, int]
def make_sprite(item_id: str) -> tuple[tuple[RGBA, ...], ...]: ...
def encode_rgba_png(rows: tuple[tuple[RGBA, ...], ...]) -> bytes: ...
def write_group(group: str, assets_root: Path) -> None: ...
~~~

Include named palettes for all sixteen stone families, four irregular chip silhouettes, a distinct `ROCK_SILHOUETTES` assignment for every family, and a clustered-fragment cobble renderer. PNG output uses filter byte zero.

- [ ] **Step 3: Expand the failing contract to the whole geological group**

Make `test_rocks_and_cobbles_have_local_models_and_native_sprites` cover all thirty IDs. Run:

~~~powershell
python -m unittest tests.test_item_art.ItemArtContractTests.test_rocks_and_cobbles_have_local_models_and_native_sprites -v
~~~

Expected: FAIL because definitions still reference `minecraft:block/*`.

- [ ] **Step 4: Generate and wire the geological items**

Run:

~~~powershell
python tools/generate_item_art.py --group rocks_and_cobbles
~~~

The command must create one PNG, one generated-item model, and one local-model item definition per group member. It must never write under `blockstates/` or `models/block/`.

- [ ] **Step 5: Verify and commit**

Run:

~~~powershell
python -m unittest tests.test_item_art.ItemArtContractTests.test_rocks_and_cobbles_have_local_models_and_native_sprites -v
python -m unittest tests.test_item_art.ItemArtContractTests.test_ground_resource_assets_are_unchanged -v
git add docs/ITEM_ART.md tools/generate_item_art.py tests/test_item_art.py src/main/resources/assets/material_progression
git commit -m "feat: author rock and cobble inventory art"
~~~

Generate an untracked nearest-neighbor 10-column atlas at `build/item-art/rocks-and-cobbles.png` and inspect family distinction at native and 8x scale before committing.

### Task 3: Material, ore, and workstation inventory art

**Files:**

- Modify: `tools/generate_item_art.py`
- Create: matching PNG/model files for `AUTHORED_ITEM_GROUPS["materials_and_workstations"]`
- Modify: matching `assets/material_progression/items/*.json`
- Modify: `tests/test_item_art.py`

**Interfaces:**

- Consumes: the groups and local-model assertions from Task 1.
- Produces: distinct original sprites for materials, ore blocks, Crusher, and Manual Workshop.

- [ ] **Step 1: Write and run the failing group contract**

Add `test_materials_and_workstations_have_local_models_and_native_sprites`, using the same exact definition/model/PNG checks for all twelve group members. Run:

~~~powershell
python -m unittest tests.test_item_art.ItemArtContractTests.test_materials_and_workstations_have_local_models_and_native_sprites -v
~~~

Expected: FAIL because the current definitions delegate to vanilla ore, ingot, dust, string, Furnace, and block models.

- [ ] **Step 2: Add these fixed sprite forms**

Extend `make_sprite` with: Tin ore/deepslate ore as gray/dark-gray host chips with blue-gray veins; Raw Tin as a rough cluster; Tin Dust as three granular piles; Tin Ingot as a cool cast bar; Copper Dust as warm orange grains; Bronze Dust and Ingot as muted gold-brown forms; Flint Shard as one angled dark chip; Plant Fiber as three pale-green strands; Crusher as dark stone with a central crushing gap; Workshop as a wooden top with a tool recess and dark base. Keep each category to its documented palette tones.

- [ ] **Step 3: Generate, verify, and commit the group**

Run:

~~~powershell
python tools/generate_item_art.py --group materials_and_workstations
python -m unittest tests.test_item_art.ItemArtContractTests.test_materials_and_workstations_have_local_models_and_native_sprites -v
python -m unittest tests.test_item_art.ItemArtContractTests.test_ground_resource_assets_are_unchanged -v
git add docs/ITEM_ART.md tools/generate_item_art.py tests/test_item_art.py src/main/resources/assets/material_progression
git commit -m "feat: author material and workstation inventory art"
~~~

Create an untracked `build/item-art/materials-and-workstations.png` atlas and inspect that ore, raw material, dust, and ingot are distinguishable at 16 pixels.

### Task 4: Flint, Tin, and Bronze tool inventory art

**Files:**

- Modify: `tools/generate_item_art.py`
- Create: matching PNG/model files for `AUTHORED_ITEM_GROUPS["tools"]`
- Modify: matching `assets/material_progression/items/*.json`
- Modify: `tests/test_item_art.py`
- Modify: `tests/test_resources.py`
- Modify: `tests/content_contracts.py`

**Interfaces:**

- Consumes: canonical axe, hammer, hoe, knife, pickaxe, saw, shovel, and sword silhouettes.
- Produces: local original Flint, Tin, and Bronze tool icons plus the permanent full-catalog assertion.

- [ ] **Step 1: Write and run the failing tool contract**

Add `test_tools_have_local_models_and_native_sprites` for all seventeen tools. Run:

~~~powershell
python -m unittest tests.test_item_art.ItemArtContractTests.test_tools_have_local_models_and_native_sprites -v
~~~

Expected: FAIL because all tools still point at vanilla item models.

- [ ] **Step 2: Add tool silhouettes and tier overlays**

Add `TOOL_SILHOUETTES` and `TOOL_MATERIALS` to the generator. The Saw has four lower-edge teeth and a narrow spine; Hammer a two-pixel striking face and square head; Knife a tapered single-edge blade; Hatchet an asymmetric bound head; Pickaxe a two-ended head; Axe one broad cutting head; Hoe an offset blade; Shovel a rounded blade; Sword a straight blade and crossguard. Use dark chipped Flint plus binding, cool Tin, and warm Bronze. Material changes only the head/blade pixels so tier variants retain their role silhouette.

- [ ] **Step 3: Generate and verify the tool group**

Run:

~~~powershell
python tools/generate_item_art.py --group tools
python -m unittest tests.test_item_art.ItemArtContractTests.test_tools_have_local_models_and_native_sprites -v
python -m unittest tests.test_item_art.ItemArtContractTests.test_ground_resource_assets_are_unchanged -v
~~~

Create and inspect an untracked `build/item-art/tools.png` sorted Flint, Tin, Bronze. Confirm a Saw cannot read as an Axe.

- [ ] **Step 4: Make the all-item guard permanent and commit**

Add `test_every_shipped_item_uses_authored_local_art`, assert the union of the three groups equals `SHIPPED_ITEMS`, then validate every current item. Delete `FAMILY_ROCK_MODELS` and `test_family_rock_item_models_resolve_to_vanilla_block_models`.

Run and commit:

~~~powershell
python -m unittest tests.test_item_art -v
python -m unittest tests.test_resources.ResourceContractTests.test_every_shipped_item_has_models_and_translations -v
git add tools/generate_item_art.py tests/test_item_art.py tests/test_resources.py tests/content_contracts.py src/main/resources/assets/material_progression
git commit -m "feat: author flint tin and bronze tool art"
~~~

Expected: all 59 item definitions resolve locally; no `minecraft:block/*` or `minecraft:item/*` placeholder remains.

### Task 5: Future-art policy, client inspection, and release synchronization

**Files:**

- Modify: `AGENTS.md`
- Modify: `README.md`
- Modify: `docs/INSPIRATIONS.md`
- Modify: `docs/TESTING.md`
- Modify: `docs/ITEM_ART.md`
- Modify: `dist/material-progression-0.2.0.jar`

**Interfaces:**

- Consumes: the full 59-item local-art contract.
- Produces: documented future-content rules and a synchronized installable production JAR.

- [ ] **Step 1: Document the permanent future-item requirement**

Add to `AGENTS.md`: every shipped inventory item needs an original local 16 by 16 texture and a local generated-item model; vanilla models are not final art. Link `docs/ITEM_ART.md` from README and replace its statement that visuals reuse vanilla placeholders. Record in `docs/INSPIRATIONS.md` that NTP, Divergent Underground, and TerraFirmaCraft inform visual readability/material identity only and no sprites are copied. Add the focused art-contract command to `docs/TESTING.md`.

- [ ] **Step 2: Run contract and build gates**

Run:

~~~powershell
./gradlew.bat contractTest --no-daemon --no-problems-report
./gradlew.bat build --no-daemon --no-problems-report
~~~

Expected: the full resource catalog passes and the local PNG assets package successfully.

- [ ] **Step 3: Inspect the client**

Run:

~~~powershell
./gradlew.bat runClient --no-daemon --no-problems-report
~~~

Inspect creative-tab items, recipe outputs, Workshop/Crusher slots, and advancement icons in a throwaway world. Confirm ground Rocks and Sticks are unchanged, no missing-texture sprite appears, and inventory corners remain transparent. Record only concrete art corrections in `docs/ITEM_ART.md`; do not alter gameplay.

- [ ] **Step 4: Synchronize the release artifact and run the full gate**

Use the release workflow:

~~~powershell
./gradlew.bat syncDistributionJar --no-daemon --no-configuration-cache --no-problems-report
./gradlew.bat verifyDistributionJar --no-daemon --no-configuration-cache --no-problems-report
./gradlew.bat headlessTest --no-daemon --no-configuration-cache --no-problems-report
~~~

Expected: exactly one `dist/material-progression-0.2.0.jar`, matching production resources, every contract passing, and all live GameTests passing.

- [ ] **Step 5: Audit the production JAR and commit**

Run:

~~~powershell
git diff --check
git status --short
& 'C:\tmp\material-progression-jdk25\jdk-25.0.3+9\bin\jar.exe' tf dist\material-progression-0.2.0.jar | Select-String 'assets/material_progression/textures/item/'
git add AGENTS.md README.md docs dist
git commit -m "docs: establish authored item art standard"
~~~

Confirm 59 local item PNG entries, one versioned JAR, and no GameTest, TestFramework, JUnit, or Mockito entry before committing.
