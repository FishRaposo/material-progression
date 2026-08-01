# Material Progression

> **Local 0.2.0 release candidate:** the complete opening/geology vertical
> slice and its synchronized installable JAR are ready for client inspection
> and survival playtesting. Values and content remain subject to that evidence.

Material Progression is an open-source experiment in rebuilding Minecraft's
progression around the thing its name promises: mining a world whose shape
matters, processing what you can reach, and crafting materials that gradually
increase your ability to control that world.

The name **Material Progression** is settled. Its two halves are the project's
two halves: materials are the physical vocabulary, and progression is the
agency those materials unlock. In Material Progression, materials define
progression. That thesis is broad enough to include the first Rock, geology,
metallurgy, workshops, storage, and bulk crafting.

The central question is:

> What if Minecraft genuinely committed to mining and crafting as its
> progression system?

The intended answer is not a quest book, character levels, a research tree, or
industrial bureaucracy. Caves, ravines, mineshafts, stone hardness, tool
materials, ore processing, alloys, and enchantability should form one coherent
system. Strip mining remains possible, but becomes a capability the player earns
instead of the optimal move five minutes into a world.

## The vision

- **The underground participates in progression.** Solid geology resists early
  tools, while caves and structures provide natural routes through it.
- **Primitive resources matter before metallurgy.** Ground Rocks and sticks
  bootstrap flint shards and tools deterministically; knives, Plant Fiber,
  Hammers, and Saws give wood, stone, and plants useful roles.
- **Natural openings matter until tools create new ones.** A configurable
  tree-punching rule can make forests physical obstacles, while resistant
  geology makes mountains expensive to tunnel through.
- **Tool tiers change player agency.** A better pickaxe does not merely unlock a
  colored ore; it changes what parts of the world can be reached and reshaped
  economically.
- **Tools grant material interactions.** Hatchets access wood, knives turn
  plants into fiber, Saws improve workshop wood yield, and Picks/Hammers
  progressively
  overcome geology instead of merely repeating the same tool with larger
  numbers.
- **Materials form a network, not a disposable ladder.** Mundane metals, alloy
  ingredients, specialist materials, and capstones can remain relevant for
  different reasons.
- **Infrastructure gives materials work beyond equipment.** Knives, hammers,
  and saws remain usable in the field but perform their intended processing
  roles when installed in a manual workshop. Pots, hoppers, hopper variants,
  crushers, furnaces, storage, and a bulk-crafting table turn metallurgical
  progress into a more capable base.
- **Manual and machine processing can be equally productive.** A workshop
  hammer and a fuel-burning crusher both turn one ore into two dust. The choice
  is whether to spend substantial tool durability or fuel.
- **Automation remains physical and Minecraft-shaped.** The intended vocabulary
  is blocks, adjacent inventories, hoppers, gravity, fuel, growth, and item
  movement—not an obligatory electrical grid or invisible storage network.
- **Processing must pay for itself.** A step should improve yield, create a new
  material, or unlock a new capability—not exist merely to waste time.
- **The result should still feel like Minecraft.** Progress happens through
  blocks, tools, terrain, recipes, and player decisions.

Read the complete living design:

- [Vision](docs/VISION.md)
- [Design principles](docs/DESIGN_PRINCIPLES.md)
- [Underground and geology](docs/GEOLOGY.md)
- [Primitive resources and tools](docs/PRIMITIVE_RESOURCES.md)
- [Metallurgy and material families](docs/METALLURGY.md)
- [Workshop infrastructure and automation](docs/INFRASTRUCTURE.md)
- [Shipped recipe catalogue](docs/RECIPES.md)
- [Enchantability and magical metallurgy](docs/ENCHANTING.md)
- [Development directions](docs/ROADMAP.md)
- [Testing toolkit](docs/TESTING.md)
- [Compatibility policy](docs/COMPATIBILITY.md)
- [Reference mods, source links, and credits](docs/INSPIRATIONS.md)

## Current prototype

The repository currently targets Minecraft 26.2 with NeoForge and Java 25. Its
complete opening/geology development slice contains:

- Tin ore, raw tin, ingots, dust, and tools
- Copper dust
- Bronze dust, ingots, and tools
- A fuel-burning stone crusher
- Ore and raw-metal crushing into two dust
- A provisional `3 copper dust + 1 tin dust -> 4 bronze dust` recipe
- Dust smelting
- Sixteen raw-stone families from Stone through Sulfur, Cinnabar, Nether
  families, and End Stone; sixteen Rock items; and matching family cobbles
- Loose-rock and ground-stick world generation, with family-aware Loose Rocks
  that resolve their supporting geology instead of falling back to generic
  Stone Rock and Overworld Ground Sticks concentrated around tagged trees and
  shrubs
- Four-Rock cobbling: four identical mapped Rocks produce their family cobble,
  while mixed or unmapped compatible Rocks produce vanilla Cobblestone
- Geological resistance determined by dimension depth, family, exposure, and
  whether the raw stone was player placed, with reloadable depth profiles for
  arbitrary non-built-in dimensions
- Partial raw-stone drops: two or three family Rocks with a correct tool, four
  with Fortune, the raw block with Silk Touch, and nothing with an insufficient
  tool
- Rock, flint shards, and the flint hatchet, plus Plant Fiber and Flint/Bronze
  Knives, Hammers, and Saws
- A default-enabled log-only axe requirement with a server opt-out; Saws
  satisfy it
- A persistent Manual Workshop for knife, hammer, and Saw processing, including
  six matching Planks per ordinary Log and three Sticks per Plank
- Recipe unlocks, five opening-progression advancements, localized tooltips,
  and throttled action-bar guidance

Arbitrary third-party stone-family definitions and custom-dimension geology
profiles are implemented through public datapack interfaces.

Ore samples, geological deposits, prospecting, pottery, expanded storage and
hoppers, shallow logistics, bonsai, bulk crafting, and expanded metallurgy are
later slices and are not shipped.

This is a test bed, not a promised MVP. The current content, balance, art,
architecture, and even its place in the eventual progression may be replaced.
Item visuals currently reuse vanilla textures as development placeholders.

## Install

Download [Material Progression 0.2.0](dist/material-progression-0.2.0.jar) and
place it in the `mods` directory of a Minecraft 26.2 NeoForge instance.

This prototype currently requires:

- Minecraft 26.2
- NeoForge 26.2.0.35-beta or a compatible 26.2 build

The installable JAR is committed alongside the source and documentation so the
current prototype can be installed without building it locally. CI also exposes
the same JAR as the `material-progression` artifact on every successful
workflow run.

## Development

Current toolchain:

- Minecraft 26.2
- NeoForge 26.2.0.35-beta
- Java 25

Build and run:

```bash
./gradlew build
./gradlew runClient
```

Run the full automated test suite:

```bash
./gradlew headlessTest
```

The Python contracts validate resources, recipes, translations,
world-generation wiring, stone-family data, advancement unlocks, documentation
links, and distribution boundaries. The GameTest server loads the real mod and
exercises the crusher, family-aware Loose Rocks and cobbling, geological
resistance and drops, primitive tools, the Manual Workshop, configuration,
feedback, persistence, and gameplay tags. GameTest code is development-only and
is not packaged in the production mod JAR. See the
[testing toolkit guide](docs/TESTING.md) for the reusable fixtures, content
catalog, focused commands, and extension conventions.

On Windows:

```powershell
./gradlew.bat headlessTest
./gradlew.bat runClient
```

The development build appears in `build/libs`. Maintainers can refresh the
tracked installable copy after changing the project version or production
sources with:

```bash
./gradlew syncDistributionJar
```

`./gradlew verifyDistributionJar` rejects missing, incorrectly named, or stale
tracked JARs.

Automated checks establish deterministic correctness. Progression feel,
interface clarity, and balance still require human playtesting.

## Project status

There is no frozen release scope yet. Documentation records the current design
conversation so it can be tested and revised deliberately. An idea appearing in
the repository does not make it a commitment.

## License

[MIT](LICENSE)
