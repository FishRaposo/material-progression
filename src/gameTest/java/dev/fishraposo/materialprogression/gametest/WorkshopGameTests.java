package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.registry.ModItems;
import dev.fishraposo.materialprogression.world.level.block.entity.WorkshopBlockEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class WorkshopGameTests {
    private WorkshopGameTests() {
    }

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "Every initial knife, hammer, and saw operation runs in the Workshop")
    static void everyManualOperationIsPlayable(
            ExtendedGameTestHelper helper
    ) {
        WorkshopBlockEntity workshop = GameTestSupport.placeBlockEntity(
                helper,
                GameTestSupport.DEFAULT_BLOCK_POS,
                ModBlocks.WORKSHOP.get(),
                WorkshopBlockEntity.class
        );

        for (Operation operation : operations()) {
            workshop.clearContent();
            ItemStack tool = operation.tool().getDefaultInstance();
            workshop.setItem(WorkshopBlockEntity.TOOL_SLOT, tool);
            workshop.setItem(
                    WorkshopBlockEntity.INPUT_SLOT,
                    operation.input().getDefaultInstance()
            );
            var recipes = workshop.matchingRecipes();
            helper.assertTrue(
                    recipes.size() == 1,
                    operation.name() + " resolved " + recipes.size()
                            + " recipes instead of one"
            );
            helper.assertTrue(
                    workshop.selectRecipe(
                            recipes.getFirst().id().identifier()
                    ),
                    operation.name() + " could not be selected"
            );
            helper.assertTrue(
                    workshop.executeSelected(),
                    operation.name() + " failed to execute"
            );
            GameTestSupport.assertEmpty(
                    helper,
                    workshop.getItem(WorkshopBlockEntity.INPUT_SLOT),
                    operation.name() + " input"
            );
            GameTestSupport.assertStack(
                    helper,
                    workshop.getItem(WorkshopBlockEntity.OUTPUT_SLOT),
                    operation.output(),
                    operation.outputCount(),
                    operation.name() + " output"
            );
            helper.assertTrue(
                    workshop.getItem(WorkshopBlockEntity.TOOL_SLOT)
                            .getDamageValue() == operation.durabilityCost(),
                    operation.name() + " spent "
                            + workshop.getItem(WorkshopBlockEntity.TOOL_SLOT)
                            .getDamageValue()
                            + " durability instead of "
                            + operation.durabilityCost()
            );
        }
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "Workshop failures never consume tools, inputs, or outputs")
    static void failuresAreAtomic(ExtendedGameTestHelper helper) {
        WorkshopBlockEntity workshop = GameTestSupport.placeBlockEntity(
                helper,
                GameTestSupport.DEFAULT_BLOCK_POS,
                ModBlocks.WORKSHOP.get(),
                WorkshopBlockEntity.class
        );
        ItemStack knife = ModItems.FLINT_KNIFE.get().getDefaultInstance();
        workshop.setItem(WorkshopBlockEntity.TOOL_SLOT, knife);
        workshop.setItem(
                WorkshopBlockEntity.INPUT_SLOT,
                ModItems.ROCK.get().getDefaultInstance()
        );
        helper.assertTrue(
                workshop.selectRecipe(
                        workshop.matchingRecipes().getFirst().id().identifier()
                ),
                "Valid knife recipe was not selectable"
        );

        workshop.setItem(
                WorkshopBlockEntity.OUTPUT_SLOT,
                new ItemStack(Items.DIRT, 64)
        );
        helper.assertFalse(
                workshop.executeSelected(),
                "Workshop executed into a full incompatible output slot"
        );
        assertUnchangedKnifeAndRock(helper, workshop, knife, "full output");

        workshop.setItem(WorkshopBlockEntity.OUTPUT_SLOT, ItemStack.EMPTY);
        knife.setDamageValue(knife.getMaxDamage() - 1);
        helper.assertFalse(
                workshop.executeSelected(),
                "Workshop executed with insufficient tool durability"
        );
        GameTestSupport.assertStack(
                helper,
                workshop.getItem(WorkshopBlockEntity.INPUT_SLOT),
                ModItems.ROCK.get(),
                1,
                "Low-durability input"
        );
        GameTestSupport.assertEmpty(
                helper,
                workshop.getItem(WorkshopBlockEntity.OUTPUT_SLOT),
                "Low-durability output"
        );

        knife.setDamageValue(0);
        workshop.setItem(
                WorkshopBlockEntity.INPUT_SLOT,
                Items.COBBLESTONE.getDefaultInstance()
        );
        helper.assertFalse(
                workshop.executeSelected(),
                "Workshop executed a stale selection after its input changed"
        );
        GameTestSupport.assertStack(
                helper,
                workshop.getItem(WorkshopBlockEntity.INPUT_SLOT),
                Items.COBBLESTONE,
                1,
                "Stale-selection input"
        );
        helper.assertTrue(
                knife.getDamageValue() == 0,
                "Stale selection damaged the installed tool"
        );
        helper.succeed();
    }

    private static void assertUnchangedKnifeAndRock(
            ExtendedGameTestHelper helper,
            WorkshopBlockEntity workshop,
            ItemStack knife,
            String context
    ) {
        helper.assertTrue(
                knife.getDamageValue() == 0,
                context + " damaged the knife"
        );
        GameTestSupport.assertStack(
                helper,
                workshop.getItem(WorkshopBlockEntity.INPUT_SLOT),
                ModItems.ROCK.get(),
                1,
                context + " input"
        );
        GameTestSupport.assertStack(
                helper,
                workshop.getItem(WorkshopBlockEntity.OUTPUT_SLOT),
                Items.DIRT,
                64,
                context + " output"
        );
    }

    private static List<Operation> operations() {
        List<Operation> operations = new ArrayList<>();
        operations.add(new Operation(
                "knife rock",
                ModItems.FLINT_KNIFE.get(),
                ModItems.ROCK.get(),
                ModItems.FLINT_SHARD.get(),
                2,
                1
        ));
        operations.add(new Operation(
                "knife leaves",
                ModItems.FLINT_KNIFE.get(),
                Items.OAK_LEAVES,
                ModItems.PLANT_FIBER.get(),
                2,
                1
        ));
        operations.add(new Operation(
                "hammer stone",
                ModItems.FLINT_HAMMER.get(),
                Items.STONE,
                Items.GRAVEL,
                1,
                2
        ));
        operations.add(new Operation(
                "hammer gravel",
                ModItems.FLINT_HAMMER.get(),
                Items.GRAVEL,
                Items.SAND,
                1,
                2
        ));
        operations.add(new Operation(
                "hammer copper ore",
                ModItems.FLINT_HAMMER.get(),
                Items.COPPER_ORE,
                ModItems.COPPER_DUST.get(),
                2,
                12
        ));
        operations.add(new Operation(
                "hammer raw copper",
                ModItems.FLINT_HAMMER.get(),
                Items.RAW_COPPER,
                ModItems.COPPER_DUST.get(),
                2,
                12
        ));
        operations.add(new Operation(
                "hammer tin ore",
                ModItems.FLINT_HAMMER.get(),
                ModItems.TIN_ORE.get(),
                ModItems.TIN_DUST.get(),
                2,
                12
        ));
        operations.add(new Operation(
                "hammer raw tin",
                ModItems.FLINT_HAMMER.get(),
                ModItems.RAW_TIN.get(),
                ModItems.TIN_DUST.get(),
                2,
                12
        ));

        Item[][] woods = {
            {Items.OAK_LOG, Items.OAK_PLANKS},
            {Items.SPRUCE_LOG, Items.SPRUCE_PLANKS},
            {Items.BIRCH_LOG, Items.BIRCH_PLANKS},
            {Items.JUNGLE_LOG, Items.JUNGLE_PLANKS},
            {Items.ACACIA_LOG, Items.ACACIA_PLANKS},
            {Items.DARK_OAK_LOG, Items.DARK_OAK_PLANKS},
            {Items.MANGROVE_LOG, Items.MANGROVE_PLANKS},
            {Items.CHERRY_LOG, Items.CHERRY_PLANKS},
            {Items.PALE_OAK_LOG, Items.PALE_OAK_PLANKS},
            {Items.BAMBOO_BLOCK, Items.BAMBOO_PLANKS},
            {Items.CRIMSON_STEM, Items.CRIMSON_PLANKS},
            {Items.WARPED_STEM, Items.WARPED_PLANKS}
        };
        for (Item[] wood : woods) {
            operations.add(new Operation(
                    "saw " + wood[0],
                    ModItems.FLINT_SAW.get(),
                    wood[0],
                    wood[1],
                    6,
                    2
            ));
            operations.add(new Operation(
                    "saw " + wood[1],
                    ModItems.FLINT_SAW.get(),
                    wood[1],
                    Items.STICK,
                    3,
                    1
            ));
        }
        return List.copyOf(operations);
    }

    private record Operation(
            String name,
            Item tool,
            Item input,
            Item output,
            int outputCount,
            int durabilityCost
    ) {
    }
}
