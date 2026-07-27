# Development Directions

> **Status: provisional.** This is a map of questions and experiments, not a
> release promise, ordered backlog, or fixed MVP.

The project is intentionally being developed through vertical slices. Each slice
should test a complete relationship between world access, processing, materials,
and player agency before the roster expands.

## Implemented experiment: bronze loop

The repository currently contains tin, dusts, bronze, tools, and a fuel-burning
crusher. Its purpose is to validate the basic content and processing foundation.

Questions it should answer:

- Is the crusher understandable and satisfying?
- Does ore doubling justify the detour?
- Does dust alloying feel natural?
- Can material families be added without repetitive code and data becoming
  unmanageable?
- What role could tin and bronze occupy once geology exists?

Nothing about this experiment is protected from replacement.

## Candidate experiment: meaningful geology

Build the smallest possible geology prototype capable of comparing:

- Ordinary vanilla stone behavior
- Hardness or harvest changes by layer
- Easier exposed blocks
- Enclosure-sensitive resistance
- Natural-only versus placed-block behavior

The goal is not to implement an entire underground rewrite immediately. It is to
find the smallest rule set that makes caves and structures rationally valuable
without making mining miserable.

## Candidate experiment: access progression

Once geology and bronze can interact, test a short playable arc:

> Primitive extraction -> accessible copper and tin -> processing -> bronze ->
> expanded underground access -> iron -> economical sustained excavation

This is the first point at which bronze's intended role can be evaluated rather
than assumed.

## Candidate experiment: broader base family

Only after the access loop works, evaluate additional mundane metals and alloys.
Candidates may include zinc, brass, nickel, manganese, steel, invar, silver,
electrum, lead, or others. Inclusion depends on the material graph, not on
matching another mod's roster.

## Candidate experiment: material specialization

Test equipment profiles that differ across:

- Accessibility
- Durability
- Mining speed
- Harvest capability
- Repair cost
- Enchantability
- Alloy relationships

The goal is to determine where branching choices work and where a clear upgrade
is healthier.

## Candidate experiment: magical metallurgy

Prototype enchantability tiers, gold-derived materials, silver finishing, rose
gold, and artifact-grade equipment only after the mundane material system has a
stable reason to exist.

This may remain in the core mod or become a separate compatible module.

## Release planning

No release scope is currently frozen. A future MVP should be declared only after
the relevant experiments demonstrate a coherent, enjoyable loop. The existing
bronze content is a starting test bed, not that declaration.
