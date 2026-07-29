package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.MaterialProgression;
import dev.fishraposo.materialprogression.registry.ModItems;
import dev.fishraposo.materialprogression.registry.ModRecipes;
import dev.fishraposo.materialprogression.world.item.crafting.ManualProcessingRecipe;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
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

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Manual processing registers and decodes every datapack recipe")
    static void manualProcessingRegistryAndDatapackRecipesAreLive(
            ExtendedGameTestHelper helper
    ) {
        Identifier id = Identifier.fromNamespaceAndPath(
                MaterialProgression.MOD_ID,
                "manual_processing"
        );
        RecipeType<?> registeredType = BuiltInRegistries.RECIPE_TYPE.getValue(id);
        RecipeSerializer<?> registeredSerializer =
                BuiltInRegistries.RECIPE_SERIALIZER.getValue(id);
        Collection<RecipeHolder<ManualProcessingRecipe>> recipes = helper.getLevel()
                .recipeAccess()
                .recipeMap()
                .byType(ModRecipes.MANUAL_PROCESSING.get());

        helper.assertTrue(
                registeredType == ModRecipes.MANUAL_PROCESSING.get(),
                "Manual-processing recipe type is not the registered production instance"
        );
        helper.assertTrue(
                registeredSerializer == ModRecipes.MANUAL_PROCESSING_SERIALIZER.get(),
                "Manual-processing serializer is not the registered production instance"
        );
        helper.assertTrue(
                recipes.size() == 32,
                "Manual-processing datapack recipes did not all decode: " + recipes.size()
        );
        helper.assertTrue(
                recipes.stream().allMatch(recipe ->
                        recipe.value().getType() == registeredType
                                && recipe.value().getSerializer() == registeredSerializer
                ),
                "Decoded manual-processing recipe used the wrong production registration"
        );
        helper.succeed();
    }
}
