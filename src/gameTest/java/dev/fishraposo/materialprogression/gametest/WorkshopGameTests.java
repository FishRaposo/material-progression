package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.registry.ModItems;
import dev.fishraposo.materialprogression.transaction.OperationPreview;
import dev.fishraposo.materialprogression.world.inventory.WorkshopMenu;
import dev.fishraposo.materialprogression.world.level.block.entity.WorkshopBlockEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
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

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "Workshop batches execute exactly or consume nothing")
    static void batchesAreRevisionedAndAtomic(
            ExtendedGameTestHelper helper
    ) {
        WorkshopBlockEntity workshop = GameTestSupport.placeBlockEntity(
                helper,
                GameTestSupport.DEFAULT_BLOCK_POS,
                ModBlocks.WORKSHOP.get(),
                WorkshopBlockEntity.class
        );
        workshop.setItem(
                WorkshopBlockEntity.TOOL_SLOT,
                ModItems.FLINT_KNIFE.get().getDefaultInstance()
        );
        workshop.setItem(
                WorkshopBlockEntity.INPUT_SLOT,
                new ItemStack(ModItems.ROCK.get(), 4)
        );
        var recipeId = workshop.matchingRecipes()
                .getFirst()
                .id()
                .identifier();
        OperationPreview exact = workshop.preview(recipeId, 4);
        helper.assertTrue(exact != null, "Batch preview was absent");
        helper.assertTrue(
                exact.executable() == 4,
                "Expected four executable operations"
        );
        helper.assertTrue(
                workshop.execute(recipeId, 4, exact.revision()),
                "Exact four-operation batch did not commit"
        );
        GameTestSupport.assertEmpty(
                helper,
                workshop.getItem(WorkshopBlockEntity.INPUT_SLOT),
                "Batch input"
        );
        GameTestSupport.assertStack(
                helper,
                workshop.getItem(WorkshopBlockEntity.OUTPUT_SLOT),
                ModItems.FLINT_SHARD.get(),
                8,
                "Batch output"
        );
        helper.assertTrue(
                workshop.getItem(WorkshopBlockEntity.TOOL_SLOT)
                        .getDamageValue() == 4,
                "Batch did not spend exactly four durability"
        );

        workshop.setItem(
                WorkshopBlockEntity.INPUT_SLOT,
                new ItemStack(ModItems.ROCK.get(), 2)
        );
        OperationPreview stale = workshop.preview(recipeId, 2);
        helper.assertTrue(stale != null, "Stale preview was absent");
        workshop.setItem(
                WorkshopBlockEntity.INPUT_SLOT,
                ModItems.ROCK.get().getDefaultInstance()
        );
        helper.assertFalse(
                workshop.execute(recipeId, 2, stale.revision()),
                "A stale two-operation batch committed"
        );
        GameTestSupport.assertStack(
                helper,
                workshop.getItem(WorkshopBlockEntity.INPUT_SLOT),
                ModItems.ROCK.get(),
                1,
                "Stale batch input"
        );
        GameTestSupport.assertStack(
                helper,
                workshop.getItem(WorkshopBlockEntity.OUTPUT_SLOT),
                ModItems.FLINT_SHARD.get(),
                8,
                "Stale batch output"
        );
        helper.assertTrue(
                workshop.getItem(WorkshopBlockEntity.TOOL_SLOT)
                        .getDamageValue() == 4,
                "Stale batch spent durability"
        );

        OperationPreview limited = workshop.preview(recipeId, 2);
        helper.assertTrue(
                limited != null && limited.executable() == 1,
                "Limited batch did not report one executable operation"
        );
        helper.assertFalse(
                workshop.execute(recipeId, 2, limited.revision()),
                "A partially executable batch committed"
        );
        GameTestSupport.assertStack(
                helper,
                workshop.getItem(WorkshopBlockEntity.INPUT_SLOT),
                ModItems.ROCK.get(),
                1,
                "Limited batch input"
        );
        GameTestSupport.assertStack(
                helper,
                workshop.getItem(WorkshopBlockEntity.OUTPUT_SLOT),
                ModItems.FLINT_SHARD.get(),
                8,
                "Limited batch output"
        );

        workshop.setItem(
                WorkshopBlockEntity.OUTPUT_SLOT,
                new ItemStack(Items.DIRT, 64)
        );
        OperationPreview full = workshop.preview(recipeId, 1);
        helper.assertTrue(
                full != null && full.executable() == 0,
                "Full output did not reject the preview"
        );
        helper.assertFalse(
                workshop.execute(recipeId, 1, full.revision()),
                "Batch committed into a full output"
        );
        GameTestSupport.assertStack(
                helper,
                workshop.getItem(WorkshopBlockEntity.INPUT_SLOT),
                ModItems.ROCK.get(),
                1,
                "Full-output batch input"
        );
        GameTestSupport.assertStack(
                helper,
                workshop.getItem(WorkshopBlockEntity.OUTPUT_SLOT),
                Items.DIRT,
                64,
                "Full-output batch output"
        );

        workshop.setItem(
                WorkshopBlockEntity.OUTPUT_SLOT,
                ItemStack.EMPTY
        );
        var player = helper.makeTickingMockServerPlayerInLevel(
                GameType.SURVIVAL
        );
        WorkshopMenu reopened = new WorkshopMenu(
                91,
                player.getInventory(),
                workshop
        );
        helper.assertFalse(
                reopened.executeBatch(
                        player,
                        recipeId,
                        1,
                        workshop.inventoryRevision(),
                        1
                ),
                "A preview sequence from a closed menu committed after reopen"
        );
        GameTestSupport.assertStack(
                helper,
                workshop.getItem(WorkshopBlockEntity.INPUT_SLOT),
                ModItems.ROCK.get(),
                1,
                "Reopened-menu input"
        );
        GameTestSupport.assertEmpty(
                helper,
                workshop.getItem(WorkshopBlockEntity.OUTPUT_SLOT),
                "Reopened-menu output"
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
