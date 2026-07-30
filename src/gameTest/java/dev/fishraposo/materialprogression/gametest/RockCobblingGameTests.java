package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.registry.ModItems;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class RockCobblingGameTests {
    private RockCobblingGameTests() {
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Four matching family Rocks cobble to that family")
    static void matchingFamilyCobbling(ExtendedGameTestHelper helper) {
        assertCrafts2x2(
                helper,
                ModItems.GRANITE_ROCK.get(),
                ModBlocks.COBBLED_GRANITE.get().asItem()
        );
        assertCrafts2x2(helper, ModItems.ROCK.get(), Items.COBBLESTONE);
        assertCrafts2x2(
                helper,
                ModItems.DEEPSLATE_ROCK.get(),
                Items.COBBLED_DEEPSLATE
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Mixed family Rocks cobble to generic cobblestone")
    static void mixedFamilyCobbling(ExtendedGameTestHelper helper) {
        CraftingInput input = CraftingInput.of(
                2,
                2,
                List.of(
                        new ItemStack(ModItems.GRANITE_ROCK.get()),
                        new ItemStack(ModItems.DIORITE_ROCK.get()),
                        new ItemStack(ModItems.GRANITE_ROCK.get()),
                        new ItemStack(ModItems.DIORITE_ROCK.get())
                )
        );
        assertCrafts(helper, input, Items.COBBLESTONE);
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Four unmapped common-tag Rocks use generic fallback")
    static void unmappedCommonRockCobbling(ExtendedGameTestHelper helper) {
        assertCrafts2x2(
                helper,
                MaterialProgressionGameTestMod.UNKNOWN_ROCK.get(),
                Items.COBBLESTONE
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Cobbling accepts exactly four occupied slots in a 3x3 grid")
    static void cobblingWorksInThreeByThree(ExtendedGameTestHelper helper) {
        ItemStack empty = ItemStack.EMPTY;
        ItemStack granite = new ItemStack(ModItems.GRANITE_ROCK.get());
        CraftingInput input = CraftingInput.of(
                3,
                3,
                List.of(
                        granite, empty, granite,
                        empty, empty, empty,
                        granite, empty, granite
                )
        );
        assertCrafts(helper, input, ModBlocks.COBBLED_GRANITE.get().asItem());
        helper.succeed();
    }

    private static void assertCrafts2x2(
            ExtendedGameTestHelper helper,
            Item ingredient,
            Item expected
    ) {
        CraftingInput input = CraftingInput.of(
                2,
                2,
                List.of(
                        new ItemStack(ingredient),
                        new ItemStack(ingredient),
                        new ItemStack(ingredient),
                        new ItemStack(ingredient)
                )
        );
        assertCrafts(helper, input, expected);
    }

    private static void assertCrafts(
            ExtendedGameTestHelper helper,
            CraftingInput input,
            Item expected
    ) {
        var recipe = helper.getLevel()
                .getServer()
                .getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, input, helper.getLevel())
                .orElseThrow(() -> new AssertionError("no crafting recipe matched"));
        GameTestSupport.assertStack(
                helper,
                recipe.value().assemble(input),
                expected,
                1,
                "rock cobbling result"
        );
    }
}
