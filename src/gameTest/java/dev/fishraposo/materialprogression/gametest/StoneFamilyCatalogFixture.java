package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.stone.StoneFamily;
import dev.fishraposo.materialprogression.stone.StoneFamilyCatalog;
import dev.fishraposo.materialprogression.stone.StoneFamilyDefinition;
import dev.fishraposo.materialprogression.stone.StoneFamilyReloadListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;

final class StoneFamilyCatalogFixture {
    private StoneFamilyCatalogFixture() {
    }

    static Map<Identifier, StoneFamilyDefinition> replaceRawBlock(
            ExtendedGameTestHelper helper,
            StoneFamily family,
            Block rawBlock
    ) {
        Map<Identifier, StoneFamilyDefinition> original =
                definitionsFrom(StoneFamilyCatalog.get());
        Map<Identifier, StoneFamilyDefinition> changed =
                new HashMap<>(original);
        StoneFamilyDefinition definition = changed.get(family.id());
        changed.put(
                family.id(),
                new StoneFamilyDefinition(
                        definition.sourceBlockTag(),
                        definition.rockItemTag(),
                        definition.cobbledBlock(),
                        BuiltInRegistries.BLOCK.getKey(rawBlock),
                        definition.looseRockSurfaceBlockTag(),
                        definition.resistance()
                )
        );
        publish(helper, changed);
        return original;
    }

    static void publish(
            ExtendedGameTestHelper helper,
            Map<Identifier, StoneFamilyDefinition> definitions
    ) {
        StoneFamilyReloadListener listener = new StoneFamilyReloadListener();
        HolderLookup.Provider registries = helper.getLevel().registryAccess();
        listener.injectContext(boundTagContext(registries), registries);
        listener.apply(definitions, null, Profiler.get());
        listener.publishValidated();
    }

    private static ICondition.IContext boundTagContext(
            HolderLookup.Provider registries
    ) {
        return new ICondition.IContext() {
            @Override
            public <T> boolean isTagLoaded(TagKey<T> key) {
                return registries.lookupOrThrow(key.registry())
                        .get(key)
                        .isPresent();
            }

            @Override
            public <T> Collection<Holder<T>> getTag(TagKey<T> key) {
                return registries.lookupOrThrow(key.registry())
                        .get(key)
                        .<Collection<Holder<T>>>map(tag ->
                                tag.stream().toList()
                        )
                        .orElse(List.of());
            }
        };
    }

    private static Map<Identifier, StoneFamilyDefinition> definitionsFrom(
            StoneFamilyCatalog catalog
    ) {
        Map<Identifier, StoneFamilyDefinition> definitions = new HashMap<>();
        for (StoneFamily family : StoneFamily.values()) {
            StoneFamilyCatalog.Entry entry =
                    catalog.byFamily(family).orElseThrow();
            definitions.put(
                    family.id(),
                    new StoneFamilyDefinition(
                            entry.sourceBlockTag(),
                            entry.rockItemTag(),
                            BuiltInRegistries.BLOCK.getKey(
                                    entry.cobbledBlock()
                            ),
                            BuiltInRegistries.BLOCK.getKey(entry.rawBlock()),
                            entry.looseRockSurfaceBlockTag(),
                            entry.resistance()
                    )
            );
        }
        return definitions;
    }
}
