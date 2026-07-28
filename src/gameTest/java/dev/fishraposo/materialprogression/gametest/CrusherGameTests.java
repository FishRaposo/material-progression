package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.registry.ModItems;
import dev.fishraposo.materialprogression.world.level.block.CrusherBlock;
import dev.fishraposo.materialprogression.world.level.block.entity.CrusherBlockEntity;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class CrusherGameTests {
    private CrusherGameTests() {
    }

    @GameTest(timeoutTicks = 1_000)
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "Every crusher input produces exactly two dust")
    static void crusherProcessesEveryInput(ExtendedGameTestHelper helper) {
        List<CrushCase> cases = List.of(
                new CrushCase(Items.RAW_COPPER, ModItems.COPPER_DUST.get()),
                new CrushCase(Items.COPPER_ORE, ModItems.COPPER_DUST.get()),
                new CrushCase(ModItems.RAW_TIN.get(), ModItems.TIN_DUST.get()),
                new CrushCase(ModItems.TIN_ORE.get(), ModItems.TIN_DUST.get())
        );

        CrusherFixture crusher = CrusherFixture.place(helper).fuel(Items.COAL);
        var sequence = helper.startSequence();

        for (CrushCase crushCase : cases) {
            sequence
                    .thenExecute(() -> {
                        GameTestSupport.assertEmpty(
                                helper,
                                crusher.input(),
                                "Crusher input before the next case"
                        );
                        GameTestSupport.assertEmpty(
                                helper,
                                crusher.output(),
                                "Crusher output before the next case"
                        );
                        crusher.input(crushCase.input());
                    })
                    .thenIdle(205)
                    .thenExecute(() -> {
                        GameTestSupport.assertEmpty(
                                helper,
                                crusher.input(),
                                "Crusher input after processing " + crushCase.input()
                        );
                        GameTestSupport.assertStack(
                                helper,
                                crusher.output(),
                                crushCase.output(),
                                2,
                                "Crusher output for " + crushCase.input()
                        );
                        crusher.clearOutput();
                    });
        }

        sequence
                .thenExecute(() -> GameTestSupport.assertEmpty(
                        helper,
                        crusher.fuel(),
                        "Crusher fuel slot after all cases"
                ))
                .thenSucceed();
    }

    @GameTest(timeoutTicks = 260)
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "The crusher cannot process without fuel")
    static void crusherRequiresFuel(ExtendedGameTestHelper helper) {
        CrusherFixture crusher = CrusherFixture.place(helper)
                .input(ModItems.RAW_TIN.get());

        helper.startSequence()
                .thenIdle(220)
                .thenExecute(() -> {
                    GameTestSupport.assertStack(
                            helper,
                            crusher.input(),
                            ModItems.RAW_TIN.get(),
                            1,
                            "Unfueled crusher input"
                    );
                    GameTestSupport.assertEmpty(
                            helper,
                            crusher.output(),
                            "Unfueled crusher output"
                    );
                    helper.assertTrue(
                            !helper.getBlockState(CrusherFixture.POSITION)
                                    .getValue(CrusherBlock.LIT),
                            "Unfueled crusher became lit"
                    );
                })
                .thenSucceed();
    }

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "Crusher sided inventory exposes input, fuel, and output correctly")
    static void crusherSidedInventoryRules(ExtendedGameTestHelper helper) {
        CrusherBlockEntity crusher = CrusherFixture.place(helper).entity();

        helper.assertTrue(
                Arrays.equals(
                        new int[] { CrusherFixture.INPUT_SLOT },
                        crusher.getSlotsForFace(Direction.UP)
                ),
                "Top face must expose only the input slot"
        );
        helper.assertTrue(
                Arrays.equals(
                        new int[] { CrusherFixture.FUEL_SLOT },
                        crusher.getSlotsForFace(Direction.NORTH)
                ),
                "Side faces must expose only the fuel slot"
        );
        helper.assertTrue(
                Arrays.equals(
                        new int[] {
                                CrusherFixture.OUTPUT_SLOT,
                                CrusherFixture.FUEL_SLOT
                        },
                        crusher.getSlotsForFace(Direction.DOWN)
                ),
                "Bottom face must expose output then fuel remainder"
        );
        helper.assertTrue(
                crusher.canPlaceItemThroughFace(
                        CrusherFixture.INPUT_SLOT,
                        ModItems.RAW_TIN.get().getDefaultInstance(),
                        Direction.UP
                ),
                "Crusher rejected a valid input from above"
        );
        helper.assertTrue(
                !crusher.canPlaceItemThroughFace(
                        CrusherFixture.OUTPUT_SLOT,
                        ModItems.TIN_DUST.get().getDefaultInstance(),
                        Direction.DOWN
                ),
                "Crusher accepted insertion into its output slot"
        );
        helper.assertTrue(
                crusher.canPlaceItemThroughFace(
                        CrusherFixture.FUEL_SLOT,
                        Items.COAL.getDefaultInstance(),
                        Direction.NORTH
                ),
                "Crusher rejected fuel from the side"
        );
        helper.assertTrue(
                !crusher.canPlaceItemThroughFace(
                        CrusherFixture.FUEL_SLOT,
                        ModItems.RAW_TIN.get().getDefaultInstance(),
                        Direction.NORTH
                ),
                "Crusher accepted a non-fuel item in its fuel slot"
        );
        helper.succeed();
    }

    @GameTest(timeoutTicks = 260)
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "The crusher leaves non-recipe inputs untouched")
    static void crusherIgnoresNonRecipeInputs(ExtendedGameTestHelper helper) {
        CrusherFixture crusher = CrusherFixture.place(helper)
                .input(Blocks.COBBLESTONE.asItem())
                .fuel(Items.COAL);

        helper.startSequence()
                .thenIdle(220)
                .thenExecute(() -> {
                    GameTestSupport.assertStack(
                            helper,
                            crusher.input(),
                            Blocks.COBBLESTONE.asItem(),
                            1,
                            "Crusher non-recipe input"
                    );
                    GameTestSupport.assertStack(
                            helper,
                            crusher.fuel(),
                            Items.COAL,
                            1,
                            "Crusher fuel for a non-recipe input"
                    );
                    GameTestSupport.assertEmpty(
                            helper,
                            crusher.output(),
                            "Crusher non-recipe output"
                    );
                })
                .thenSucceed();
    }

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "Breaking a crusher drops the crusher item")
    static void crusherDropsItself(ExtendedGameTestHelper helper) {
        CrusherFixture.place(helper);
        helper.breakBlock(
                CrusherFixture.POSITION,
                Items.IRON_PICKAXE.getDefaultInstance(),
                null
        );
        helper.assertItemEntityCountIsAtLeast(
                ModItems.CRUSHER.get(),
                CrusherFixture.POSITION,
                1.0,
                1
        );
        helper.succeed();
    }

    private record CrushCase(Item input, Item output) {
    }
}
