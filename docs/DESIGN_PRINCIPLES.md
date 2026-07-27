# Design Principles

> **Status: provisional.** These are the current tests for evaluating ideas.
> They may change when playtesting exposes better rules.

## 1. Progression grants agency

A progression reward should increase what a player can reach, reshape, produce,
or specialize in. Statistical upgrades are useful when they create that change,
not merely because the numbers are larger.

## 2. The generated world must matter

Caves, ravines, mineshafts, structures, exposed blocks, and geological layers
should affect route choice. The optimal strategy should not always be to ignore
the world and tunnel directly toward known coordinates.

Forests and mountains should be physical terrain rather than differently
textured empty space. Early players adapt to clearings, cave mouths, ravines,
and other natural openings. Better tools gradually let them create their own
routes.

## 3. Progress cannot depend on lucky terrain

World shape should create choices, not impossible starts. Any resource required
before the player has meaningful agency must be reliably obtainable in every
reasonable spawn. Loose rocks can guarantee the first sharp tools; gravel can
remain a valuable bulk source without being a run-validity check.

## 4. Strip mining is earned, not banned

The game should not display an arbitrary message saying the player cannot dig a
tunnel. Primitive tools and resistant geology should make large-scale excavation
uneconomical until the player develops the materials to support it.

## 5. Materials form a graph

The target is not a single sequence of replacements. A metal can be valuable as
a tool material, an alloy ingredient, a magical conductor, a finishing
material, or one route among several to a capability.

## 6. A family can provide identity

Not every metal needs a supernatural gimmick. Tin can be valuable because it is
available, workable, and essential to bronze. Zinc can principally matter
because it enables brass. A coherent family of mundane materials creates a
metallurgical vocabulary that individual members do not need to carry alone.

## 7. Processing steps must create value

Every additional step must answer at least one of these questions:

- Does it improve yield?
- Does it make a new material possible?
- Does it produce a meaningfully different property?
- Does it unlock a new capability or choice?

If the only answer is "it takes longer," the step should not exist.

## 8. Primitive does not mean ceremonial

Early resources should create decisions before metallurgy, but the opening
should not become a ritualized survival checklist. Flint can already represent
a prepared sharp edge; a knife can be crafted and used directly without a
knapping minigame unless that interaction proves independently valuable.

Disabling tree punching can be justified because it gives the primitive tool
layer a real job. Requiring random flint before any tool can be made cannot: it
adds a blocker before the player has agency. The primitive phase should be brief,
deterministic, and physically legible.

## 9. Basic metallurgy stays basic

Bronze is copper and tin. It does not require invented mysticism to justify
itself. Mundane, legible processes establish the design language that later
systems can elaborate.

## 10. Old materials retain uses

Progression should create new demand for earlier resources through alloys,
infrastructure, finishing, or specialist equipment. Finding a better pickaxe
should not make every material below it permanently irrelevant.

## 11. Materials should build the workshop

Metals should matter outside equipment slots. Processing blocks, storage,
transport, collection, and other modest infrastructure can preserve demand for
materials after their tools have been superseded.

An infrastructure recipe should follow the physical fantasy of the block.
Hoppers are item funnels, not intrinsically iron objects; copper is a natural
candidate for ducts because it already reads as a workable conduit material.
Exact recipes and material equivalences remain design questions.

## 12. Automation is earned relief

Automation should convert resources, space, construction, and time into freedom
from repetitive work. It should neither be free at the beginning nor require a
detour into an unrelated industrial power system.

A bonsai that slowly supplies tree drops is valuable because the player first
obtains clay, fabricates a pot, supplies collection infrastructure, and waits.
The reward is a workshop that becomes increasingly self-sustaining.

## 13. Active play and passive supply should complement each other

Efficiency tools and automation should solve different problems. A saw may
increase the yield of active forestry, while a bonsai slowly supports the
workshop in the background. Neither should make the other—or the generated
world—irrelevant.

Baseline resource nerfs require stronger justification than optional efficiency
rewards. Making a saw useful by improving wood yield is preferable to making
ordinary crafting feel deliberately broken unless testing demonstrates that a
lower baseline improves the entire progression.

## 14. Trade-offs beat universal best-in-slot

Where practical, materials should occupy different profiles: accessibility,
durability, speed, harvest capability, enchantability, repair economy, or
specialized use. Capstones may be intentionally extreme, but reaching one should
not flatten every other decision by accident.

## 15. Physical rules should be legible

Players should learn by interacting with blocks and tools. A cave wall being
easier to exploit than fully enclosed stone is more Minecraft-shaped than a
hidden character level. Recipes and material properties should teach the system
without requiring a wiki open at all times.

## 16. Player construction should not be punished

Natural geology may have progression-sensitive hardness. Blocks placed for
building should remain reasonable to remove. The system exists to make
exploration and excavation meaningful, not to make correcting a misplaced block
tedious.

## 17. The design remains revisable

Implementation proves feasibility, not correctness. Current content may be
deleted, reordered, or rebuilt if it fails the principles above during
playtesting.
