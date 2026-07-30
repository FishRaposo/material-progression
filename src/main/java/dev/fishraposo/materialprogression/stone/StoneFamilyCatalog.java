package dev.fishraposo.materialprogression.stone;

import dev.fishraposo.materialprogression.registry.ModTags;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.conditions.ICondition;

/**
 * Immutable server-side snapshot of all built-in and datapack-defined stone
 * families. Built-in families retain their enum identity for the compact
 * Loose Rocks block state; arbitrary external IDs use the external
 * block-entity representation.
 */
public final class StoneFamilyCatalog {
    private static final StoneFamilyCatalog EMPTY = new StoneFamilyCatalog(
            Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of()
    );
    private static volatile StoneFamilyCatalog current = EMPTY;
    private static volatile long version;

    private final Map<Identifier, Entry> byId;
    private final Map<Block, Entry> bySource;
    private final Map<Block, Entry> byDirectSurface;
    private final Map<Item, Entry> byRock;
    private final Map<Block, Entry> byRaw;
    private final Map<Block, Entry> byCobble;

    private StoneFamilyCatalog(
            Map<Identifier, Entry> byId,
            Map<Block, Entry> bySource,
            Map<Block, Entry> byDirectSurface,
            Map<Item, Entry> byRock,
            Map<Block, Entry> byRaw,
            Map<Block, Entry> byCobble
    ) {
        this.byId = Map.copyOf(byId);
        this.bySource = Map.copyOf(bySource);
        this.byDirectSurface = Map.copyOf(byDirectSurface);
        this.byRock = Map.copyOf(byRock);
        this.byRaw = Map.copyOf(byRaw);
        this.byCobble = Map.copyOf(byCobble);
    }

    public static StoneFamilyCatalog get() {
        return current;
    }

    public static long version() {
        return version;
    }

    static StoneFamilyCatalog empty() {
        return EMPTY;
    }

    static synchronized void install(StoneFamilyCatalog validatedCatalog) {
        current = validatedCatalog;
        version++;
    }

    static synchronized void clear() {
        current = EMPTY;
        version++;
    }

    static StoneFamilyCatalog build(
            Map<Identifier, StoneFamilyDefinition> definitions,
            HolderLookup.Provider registries,
            ICondition.IContext tagContext
    ) {
        for (StoneFamily family : StoneFamily.values()) {
            if (!definitions.containsKey(family.id())) {
                throw invalid(family.id(), "definition is missing");
            }
        }

        Map<Identifier, Entry> families = new HashMap<>();
        Map<Block, Entry> sources = new HashMap<>();
        Map<Block, Entry> directSurfaces = new HashMap<>();
        Map<Item, Entry> rocks = new HashMap<>();
        Map<Block, Entry> rawBlocks = new HashMap<>();
        Map<Block, Entry> cobbles = new HashMap<>();
        var blockLookup = registries.lookupOrThrow(Registries.BLOCK);
        requireLoaded(
                Identifier.parse("c:rocks"),
                "shared Rock parent tag",
                ModTags.ROCKS,
                tagContext
        );
        Collection<Holder<Item>> sharedRocks =
                tagContext.getTag(ModTags.ROCKS);

        definitions.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().toString()))
                .forEach(definitionEntry -> {
                    Identifier id = definitionEntry.getKey();
                    StoneFamilyDefinition definition = definitionEntry.getValue();
                    if (definition == null) {
                        throw invalid(id, "definition is null");
                    }

                    Holder.Reference<Block> rawHolder = blockLookup.get(
                            ResourceKey.create(
                                    Registries.BLOCK,
                                    definition.rawBlock()
                            )
                    ).orElseThrow(() -> invalid(
                            id,
                            "raw block " + definition.rawBlock()
                                    + " is not registered"
                    ));
                    Holder.Reference<Block> cobbleHolder = blockLookup.get(
                            ResourceKey.create(
                                    Registries.BLOCK,
                                    definition.cobbledBlock()
                            )
                    ).orElseThrow(() -> invalid(
                            id,
                            "cobbled block " + definition.cobbledBlock()
                                    + " is not registered"
                    ));
                    if (rawHolder.value() == cobbleHolder.value()) {
                        throw invalid(
                                id,
                                "raw block and cobbled block must be different"
                        );
                    }
                    if (rawHolder.value().asItem() == Items.AIR) {
                        throw invalid(
                                id,
                                "raw block " + definition.rawBlock()
                                        + " has no registered block item"
                        );
                    }
                    if (cobbleHolder.value().asItem() == Items.AIR) {
                        throw invalid(
                                id,
                                "cobbled block " + definition.cobbledBlock()
                                        + " has no registered block item"
                        );
                    }

                    requireLoaded(
                            id,
                            "source block tag",
                            definition.sourceBlockTag(),
                            tagContext
                    );
                    requireLoaded(
                            id,
                            "loose-rock surface block tag",
                            definition.looseRockSurfaceBlockTag(),
                            tagContext
                    );
                    requireLoaded(
                            id,
                            "Rock item tag",
                            definition.rockItemTag(),
                            tagContext
                    );
                    Collection<Holder<Block>> sourceTag =
                            tagContext.getTag(definition.sourceBlockTag());
                    Collection<Holder<Block>> directSurfaceTag =
                            tagContext.getTag(
                                    definition.looseRockSurfaceBlockTag()
                            );
                    Collection<Holder<Item>> rockTag =
                            tagContext.getTag(definition.rockItemTag());

                    requireNonEmpty(id, "source block tag", sourceTag.size());
                    requireNonEmpty(
                            id,
                            "loose-rock surface block tag",
                            directSurfaceTag.size()
                    );
                    if (rockTag.size() != 1) {
                        throw invalid(
                                id,
                                "Rock item tag must resolve to exactly one item; "
                                        + definition.rockItemTag()
                                        + " resolved to " + rockTag.size()
                        );
                    }
                    if (!sourceTag.contains(rawHolder)) {
                        throw invalid(
                                id,
                                "raw block " + definition.rawBlock()
                                        + " is absent from "
                                        + definition.sourceBlockTag()
                        );
                    }
                    if (!directSurfaceTag.contains(rawHolder)) {
                        throw invalid(
                                id,
                                "raw block " + definition.rawBlock()
                                        + " is absent from "
                                        + definition.looseRockSurfaceBlockTag()
                        );
                    }

                    Item rockItem = rockTag.iterator().next().value();
                    if (rockItem == Items.AIR) {
                        throw invalid(id, "Rock output may not be minecraft:air");
                    }
                    if (sharedRocks.stream().noneMatch(
                            holder -> holder.value() == rockItem
                    )) {
                        throw invalid(
                                id,
                                "Rock output " + rockItem
                                        + " must also belong to #c:rocks"
                        );
                    }
                    Entry entry = new Entry(
                            id,
                            StoneFamily.byId(id),
                            definition.sourceBlockTag(),
                            definition.rockItemTag(),
                            rockItem,
                            cobbleHolder.value(),
                            rawHolder.value(),
                            definition.looseRockSurfaceBlockTag(),
                            definition.resistance()
                    );
                    families.put(id, entry);
                    putUnique(rawBlocks, rawHolder.value(), entry, "raw block");
                    putUnique(
                            cobbles,
                            cobbleHolder.value(),
                            entry,
                            "cobbled block"
                    );
                    sourceTag.forEach(holder -> putUnique(
                            sources,
                            holder.value(),
                            entry,
                            "source block"
                    ));
                    directSurfaceTag.forEach(holder -> putUnique(
                            directSurfaces,
                            holder.value(),
                            entry,
                            "direct surface"
                    ));
                    putUnique(rocks, rockItem, entry, "Rock item");
                });

        return new StoneFamilyCatalog(
                families, sources, directSurfaces, rocks, rawBlocks, cobbles
        );
    }

    public Collection<Entry> entries() {
        return byId.values();
    }

    public Optional<Entry> byFamily(StoneFamily family) {
        return byId(family.id());
    }

    public Optional<Entry> byId(Identifier id) {
        return Optional.ofNullable(byId.get(id));
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
        if (previous != null && !previous.id().equals(entry.id())) {
            throw invalid(
                    entry.id(),
                    kind + " is already owned by " + previous.id()
                            + ": " + key
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

    private static String readableName(Identifier id) {
        String[] words = id.getPath().replace('/', '_').split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (!result.isEmpty()) {
                result.append(' ');
            }
            result.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1));
        }
        return result.isEmpty() ? id.toString() : result.toString();
    }

    public record Entry(
            Identifier id,
            Optional<StoneFamily> builtInFamily,
            net.minecraft.tags.TagKey<Block> sourceBlockTag,
            net.minecraft.tags.TagKey<Item> rockItemTag,
            Item rockItem,
            Block cobbledBlock,
            Block rawBlock,
            net.minecraft.tags.TagKey<Block> looseRockSurfaceBlockTag,
            StoneFamilyDefinition.Resistance resistance
    ) {
        public Component displayName() {
            String translationKey = "stone_family." + id.getNamespace()
                    + "." + id.getPath().replace('/', '.');
            return Component.translatableWithFallback(
                    translationKey,
                    readableName(id)
            );
        }
    }
}
