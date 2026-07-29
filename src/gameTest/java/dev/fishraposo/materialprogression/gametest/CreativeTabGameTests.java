package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.registry.ModItems;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class CreativeTabGameTests {
    private CreativeTabGameTests() {
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "The testing tab contains every mod item exactly once")
    static void creativeTabContainsCompleteCatalog(
            ExtendedGameTestHelper helper
    ) {
        List<Item> contents = ModItems.creativeTabContents()
                .stream()
                .map(holder -> (Item) holder.get())
                .toList();
        Set<Item> uniqueContents = new HashSet<>(contents);
        Set<Item> registeredItems = new HashSet<>();

        BuiltInRegistries.ITEM.forEach(item -> {
            if (MaterialProgression.MOD_ID.equals(
                    BuiltInRegistries.ITEM.getKey(item).getNamespace()
            )) {
                registeredItems.add(item);
            }
        });

        helper.assertTrue(
                contents.size() == uniqueContents.size(),
                "The creative tab contains a duplicate item"
        );
        helper.assertTrue(
                registeredItems.equals(uniqueContents),
                "Creative tab contents differ from the registered mod catalog"
        );
        helper.succeed();
    }
}
