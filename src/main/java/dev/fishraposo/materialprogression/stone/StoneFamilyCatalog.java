package dev.fishraposo.materialprogression.stone;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.conditions.ICondition;

/**
 * Server-only snapshot of the stone-family data validated during datapack
 * reload. The client renders the family already encoded in block state and
 * must not use this catalog as a gameplay authority.
 */
public final class StoneFamilyCatalog {
    private static final StoneFamilyCatalog EMPTY = new StoneFamilyCatalog(
            Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of()
    );
    private static volatile StoneFamilyCatalog current = EMPTY;

    private final Map<StoneFamily, Entry> byFamily;
    private final Map<Block, Entry> bySource;
    private final Map<Block, Entry> byDirectSurface;
    private final Map<Item, Entry> byRock;
    private final Map<Block, Entry> byRaw;
    private final Map<Block, Entry> byCobble;

    private StoneFamilyCatalog(
            Map<StoneFamily, Entry> byFamily,
            Map<Block, Entry> bySource,
            Map<Block, Entry> byDirectSurface,
            Map<Item, Entry> byRock,
            Map<Block, Entry> byRaw,
            Map<Block, Entry> byCobble
    ) {
        this.byFamily = Map.copyOf(byFamily);
        this.bySource = Map.copyOf(bySource);
        this.byDirectSurface = Map.copyOf(byDirectSurface);
        this.byRock = Map.copyOf(byRock);
        this.byRaw = Map.copyOf(byRaw);
        this.byCobble = Map.copyOf(byCobble);
    }

    public static StoneFamilyCatalog get() {
        return current;
    }

    static StoneFamilyCatalog empty() {
        return EMPTY;
    }

    static void install(StoneFamilyCatalog validatedCatalog) {
        current = validatedCatalog;
    }

    static void clear() {
        current = EMPTY;
    }

    static StoneFamilyCatalog build(
            Map<Identifier, StoneFamilyDefinition> definitions,
            HolderLookup.Provider registries,
            ICondition.IContext tagContext
    ) {
        Map<StoneFamily, Entry> families = new EnumMap<>(StoneFamily.class);
        Map<Block, Entry> sources = new HashMap<>();
        Map<Block, Entry> directSurfaces = new HashMap<>();
        Map<Item, Entry> rocks = new HashMap<>();
        Map<Block, Entry> rawBlocks = new HashMap<>();
        Map<Block, Entry> cobbles = new HashMap<>();

        for (Identifier id : definitions.keySet()) {
            if (StoneFamily.byId(id).isEmpty()) {
                throw invalid(id, "is not one of the 16 supported block-state families");
            }
        }
        for (StoneFamily family : StoneFamily.values()) {
            StoneFamilyDefinition definition = definitions.get(family.id());
            if (definition == null) {
                throw invalid(family.id(), "definition is missing");
            }

            var blockLookup = registries.lookupOrThrow(Registries.BLOCK);
            Holder.Reference<Block> rawHolder = blockLookup.get(
                    ResourceKey.create(Registries.BLOCK, definition.rawBlock())
            ).orElseThrow(() -> invalid(
                    family.id(),
                    "raw block " + definition.rawBlock() + " is not registered"
            ));
            Holder.Reference<Block> cobbleHolder = blockLookup.get(
                    ResourceKey.create(Registries.BLOCK, definition.cobbledBlock())
            ).orElseThrow(() -> invalid(
                    family.id(),
                    "cobbled block " + definition.cobbledBlock() + " is not registered"
            ));
            requireLoaded(
                    family.id(),
                    "source block tag",
                    definition.sourceBlockTag(),
                    tagContext
            );
            requireLoaded(
                    family.id(),
                    "loose-rock surface block tag",
                    definition.looseRockSurfaceBlockTag(),
                    tagContext
            );
            requireLoaded(
                    family.id(),
                    "Rock item tag",
                    definition.rockItemTag(),
                    tagContext
            );
            Collection<Holder<Block>> sourceTag =
                    tagContext.getTag(definition.sourceBlockTag());
            Collection<Holder<Block>> directSurfaceTag =
                    tagContext.getTag(definition.looseRockSurfaceBlockTag());
            Collection<Holder<Item>> rockTag =
                    tagContext.getTag(definition.rockItemTag());

            requireNonEmpty(family.id(), "source block tag", sourceTag.size());
            requireNonEmpty(family.id(), "loose-rock surface block tag", directSurfaceTag.size());
            requireNonEmpty(family.id(), "Rock item tag", rockTag.size());
            if (!sourceTag.contains(rawHolder)) {
                throw invalid(
                        family.id(),
                        "raw block " + definition.rawBlock()
                                + " is absent from " + definition.sourceBlockTag()
                );
            }
            if (!directSurfaceTag.contains(rawHolder)) {
                throw invalid(
                        family.id(),
                        "raw block " + definition.rawBlock()
                                + " is absent from " + definition.looseRockSurfaceBlockTag()
                );
            }

            Entry entry = new Entry(
                    family,
                    definition.sourceBlockTag(),
                    definition.rockItemTag(),
                    cobbleHolder.value(),
                    rawHolder.value(),
                    definition.looseRockSurfaceBlockTag(),
                    definition.resistance()
            );
            families.put(family, entry);
            putUnique(rawBlocks, rawHolder.value(), entry, "raw block");
            putUnique(cobbles, cobbleHolder.value(), entry, "cobbled block");
            sourceTag.forEach(holder ->
                    putUnique(sources, holder.value(), entry, "source block"));
            directSurfaceTag.forEach(holder ->
                    putUnique(directSurfaces, holder.value(), entry, "direct surface"));
            rockTag.forEach(holder ->
                    putUnique(rocks, holder.value(), entry, "Rock item"));
        }

        return new StoneFamilyCatalog(
                families, sources, directSurfaces, rocks, rawBlocks, cobbles
        );
    }

    public Optional<Entry> byFamily(StoneFamily family) {
        return Optional.ofNullable(byFamily.get(family));
    }

    public Optional<Entry> byId(Identifier id) {
        return StoneFamily.byId(id).flatMap(this::byFamily);
    }

    public Optional<Entry> byIndex(int index) {
        StoneFamily[] values = StoneFamily.values();
        return index >= 0 && index < values.length
                ? byFamily(values[index])
                : Optional.empty();
    }

    public Optional<Entry> bySource(BlockState state) {
        return Optional.ofNullable(bySource.get(state.getBlock()));
    }

    public Optional<Entry> byDirectSupport(BlockState state) {
        return Optional.ofNullable(byDirectSurface.get(state.getBlock()));
    }

    public Optional<Entry> byRock(ItemStack stack) {
        return stack.isEmpty()
                ? Optional.empty()
                : Optional.ofNullable(byRock.get(stack.getItem()));
    }

    public Optional<Entry> byRaw(Block block) {
        return Optional.ofNullable(byRaw.get(block));
    }

    public Optional<Entry> byRaw(BlockState state) {
        return byRaw(state.getBlock());
    }

    public Optional<Entry> byCobble(Block block) {
        return Optional.ofNullable(byCobble.get(block));
    }

    public Optional<Entry> byCobble(ItemStack stack) {
        return stack.isEmpty()
                ? Optional.empty()
                : byCobble(Block.byItem(stack.getItem()));
    }

    public Optional<StoneFamilyDefinition.Resistance> resistance(
            BlockState source
    ) {
        return bySource(source).map(Entry::resistance);
    }

    private static <T> void putUnique(
            Map<T, Entry> values,
            T key,
            Entry entry,
            String kind
    ) {
        Entry previous = values.putIfAbsent(key, entry);
        if (previous != null && previous.family() != entry.family()) {
            throw invalid(
                    entry.family().id(),
                    kind + " " + key + " is already owned by "
                            + previous.family().id()
            );
        }
    }

    private static void requireNonEmpty(
            Identifier id,
            String kind,
            int size
    ) {
        if (size == 0) {
            throw invalid(id, kind + " resolves to no registry entries");
        }
    }

    private static <T> void requireLoaded(
            Identifier id,
            String kind,
            net.minecraft.tags.TagKey<T> tag,
            ICondition.IContext context
    ) {
        if (!context.isTagLoaded(tag)) {
            throw invalid(id, kind + " " + tag + " does not exist");
        }
    }

    private static IllegalStateException invalid(Identifier id, String message) {
        return new IllegalStateException(
                "Invalid stone family " + id + ": " + message
        );
    }

    public record Entry(
            StoneFamily family,
            net.minecraft.tags.TagKey<Block> sourceBlockTag,
            net.minecraft.tags.TagKey<Item> rockItemTag,
            Block cobbledBlock,
            Block rawBlock,
            net.minecraft.tags.TagKey<Block> looseRockSurfaceBlockTag,
            StoneFamilyDefinition.Resistance resistance
    ) {
    }
}
