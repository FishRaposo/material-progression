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
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class StoneFamilyReloadGameTests {
    private StoneFamilyReloadGameTests() {
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(
            description = "Invalid staged families cannot replace the live catalog"
    )
    static void invalidStagingPreservesPublishedCatalog(
            ExtendedGameTestHelper helper
    ) {
        Map<Identifier, StoneFamilyDefinition> definitions =
                definitionsFrom(StoneFamilyCatalog.get());
        StoneFamilyReloadListener validReload = listener(helper);
        validReload.apply(definitions, null, Profiler.get());
        validReload.publishValidated();
        StoneFamilyCatalog published = StoneFamilyCatalog.get();

        Map<Identifier, StoneFamilyDefinition> duplicates =
                new HashMap<>(definitions);
        StoneFamilyDefinition stone = duplicates.get(StoneFamily.STONE.id());
        StoneFamilyDefinition granite = duplicates.get(StoneFamily.GRANITE.id());
        duplicates.put(
                StoneFamily.GRANITE.id(),
                new StoneFamilyDefinition(
                        stone.sourceBlockTag(),
                        granite.rockItemTag(),
                        granite.cobbledBlock(),
                        stone.rawBlock(),
                        stone.looseRockSurfaceBlockTag(),
                        granite.resistance()
                )
        );

        boolean rejected = false;
        try {
            listener(helper).apply(duplicates, null, Profiler.get());
        } catch (IllegalStateException expected) {
            rejected = true;
        }

        helper.assertTrue(rejected, "duplicate family staging was accepted");
        helper.assertTrue(
                StoneFamilyCatalog.get() == published,
                "failed staging changed the published catalog"
        );
        helper.succeed();
    }

    private static StoneFamilyReloadListener listener(
            ExtendedGameTestHelper helper
    ) {
        StoneFamilyReloadListener listener = new StoneFamilyReloadListener();
        HolderLookup.Provider registries = helper.getLevel().registryAccess();
        listener.injectContext(
                boundTagContext(registries),
                registries
        );
        return listener;
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
                        .<Collection<Holder<T>>>map(tag -> tag.stream().toList())
                        .orElse(List.of());
            }
        };
    }

    private static Map<Identifier, StoneFamilyDefinition> definitionsFrom(
            StoneFamilyCatalog catalog
    ) {
        Map<Identifier, StoneFamilyDefinition> definitions = new HashMap<>();
        for (StoneFamilyCatalog.Entry entry : catalog.entries()) {
            definitions.put(
                    entry.id(),
                    new StoneFamilyDefinition(
                            entry.sourceBlockTag(),
                            entry.rockItemTag(),
                            BuiltInRegistries.BLOCK.getKey(entry.cobbledBlock()),
                            BuiltInRegistries.BLOCK.getKey(entry.rawBlock()),
                            entry.looseRockSurfaceBlockTag(),
                            entry.resistance()
                    )
            );
        }
        return definitions;
    }
}
