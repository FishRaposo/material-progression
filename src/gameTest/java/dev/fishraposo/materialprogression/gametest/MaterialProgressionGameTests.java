package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.registry.ModItems;
import dev.fishraposo.materialprogression.registry.ModTags;
import dev.fishraposo.materialprogression.world.level.block.CrusherBlock;
import dev.fishraposo.materialprogression.world.level.block.entity.CrusherBlockEntity;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class MaterialProgressionGameTests {
    private static final BlockPos CRUSHER_POS = new BlockPos(1, 1, 1);

    private MaterialProgressionGameTests() {
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

        helper.setBlock(CRUSHER_POS, ModBlocks.CRUSHER.get());
        CrusherBlockEntity crusher = crusher(helper);
        crusher.setItem(1, Items.COAL.getDefaultInstance());

        var sequence = helper.startSequence();
        for (CrushCase crushCase : cases) {
            sequence
                    .thenExecute(() -> {
                        helper.assertTrue(
                                crusher.getItem(0).isEmpty(),
                                "Crusher input was not empty before the next case"
                        );
                        helper.assertTrue(
                                crusher.getItem(2).isEmpty(),
                                "Crusher output was not empty before the next case"
                        );
                        crusher.setItem(0, crushCase.input().getDefaultInstance());
                    })
                    .thenIdle(205)
                    .thenExecute(() -> {
                        ItemStack output = crusher.getItem(2);
                        helper.assertTrue(
                                crusher.getItem(0).isEmpty(),
                                "Crusher did not consume " + crushCase.input()
                        );
                        helper.assertTrue(
                                output.is(crushCase.output()),
                                "Crusher produced the wrong dust for " + crushCase.input()
                        );
                        helper.assertTrue(
                                output.getCount() == 2,
                                "Crusher did not produce exactly two dust"
                        );
                        crusher.setItem(2, ItemStack.EMPTY);
                    });
        }

        sequence
                .thenExecute(() -> helper.assertTrue(
                        crusher.getItem(1).isEmpty(),
                        "Crusher fuel item was not consumed"
                ))
                .thenSucceed();
    }

    @GameTest(timeoutTicks = 260)
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "The crusher cannot process without fuel")
    static void crusherRequiresFuel(ExtendedGameTestHelper helper) {
        helper.setBlock(CRUSHER_POS, ModBlocks.CRUSHER.get());
        CrusherBlockEntity crusher = crusher(helper);
        crusher.setItem(0, ModItems.RAW_TIN.get().getDefaultInstance());

        helper.startSequence()
                .thenIdle(220)
                .thenExecute(() -> {
                    helper.assertTrue(
                            crusher.getItem(0).is(ModItems.RAW_TIN.get()),
                            "Unfueled crusher consumed its input"
                    );
                    helper.assertTrue(
                            crusher.getItem(0).getCount() == 1,
                            "Unfueled crusher changed the input count"
                    );
                    helper.assertTrue(
                            crusher.getItem(2).isEmpty(),
                            "Unfueled crusher created output"
                    );
                    helper.assertTrue(
                            !helper.getBlockState(CRUSHER_POS).getValue(CrusherBlock.LIT),
                            "Unfueled crusher became lit"
                    );
                })
                .thenSucceed();
    }

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "Crusher sided inventory exposes input, fuel, and output correctly")
    static void crusherSidedInventoryRules(ExtendedGameTestHelper helper) {
        helper.setBlock(CRUSHER_POS, ModBlocks.CRUSHER.get());
        CrusherBlockEntity crusher = crusher(helper);

        helper.assertTrue(
                Arrays.equals(new int[] { 0 }, crusher.getSlotsForFace(Direction.UP)),
                "Top face must expose only the input slot"
        );
        helper.assertTrue(
                Arrays.equals(new int[] { 1 }, crusher.getSlotsForFace(Direction.NORTH)),
                "Side faces must expose only the fuel slot"
        );
        helper.assertTrue(
                Arrays.equals(new int[] { 2, 1 }, crusher.getSlotsForFace(Direction.DOWN)),
                "Bottom face must expose output then fuel remainder"
        );
        helper.assertTrue(
                crusher.canPlaceItemThroughFace(
                        0,
                        ModItems.RAW_TIN.get().getDefaultInstance(),
                        Direction.UP
                ),
                "Crusher rejected a valid input from above"
        );
        helper.assertTrue(
                !crusher.canPlaceItemThroughFace(
                        2,
                        ModItems.TIN_DUST.get().getDefaultInstance(),
                        Direction.DOWN
                ),
                "Crusher accepted insertion into its output slot"
        );
        helper.assertTrue(
                crusher.canPlaceItemThroughFace(
                        1,
                        Items.COAL.getDefaultInstance(),
                        Direction.NORTH
                ),
                "Crusher rejected fuel from the side"
        );
        helper.assertTrue(
                !crusher.canPlaceItemThroughFace(
                        1,
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
        helper.setBlock(CRUSHER_POS, ModBlocks.CRUSHER.get());
        CrusherBlockEntity crusher = crusher(helper);
        crusher.setItem(0, Items.COBBLESTONE.getDefaultInstance());
        crusher.setItem(1, Items.COAL.getDefaultInstance());

        helper.startSequence()
                .thenIdle(220)
                .thenExecute(() -> {
                    helper.assertTrue(
                            crusher.getItem(0).is(Items.COBBLESTONE),
                            "Crusher consumed a non-recipe input"
                    );
                    helper.assertTrue(
                            crusher.getItem(0).getCount() == 1,
                            "Crusher changed a non-recipe input count"
                    );
                    helper.assertTrue(
                            crusher.getItem(1).is(Items.COAL),
                            "Crusher consumed fuel for a non-recipe input"
                    );
                    helper.assertTrue(
                            crusher.getItem(2).isEmpty(),
                            "Crusher created output from a non-recipe input"
                    );
                })
                .thenSucceed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Tool durability, repair, mining, and enchantment tags are loaded")
    static void toolAndTagContracts(ExtendedGameTestHelper helper) {
        ItemStack tinPickaxe = ModItems.TIN_PICKAXE.get().getDefaultInstance();
        ItemStack bronzePickaxe = ModItems.BRONZE_PICKAXE.get().getDefaultInstance();
        ItemStack tinSword = ModItems.TIN_SWORD.get().getDefaultInstance();
        ItemStack bronzeSword = ModItems.BRONZE_SWORD.get().getDefaultInstance();

        helper.assertTrue(tinPickaxe.getMaxDamage() == 96, "Tin durability changed");
        helper.assertTrue(bronzePickaxe.getMaxDamage() == 325, "Bronze durability changed");
        helper.assertTrue(
                ModItems.TIN_INGOT.get().getDefaultInstance().is(ModTags.REPAIRS_TIN_TOOLS),
                "Tin ingots do not repair tin tools"
        );
        helper.assertTrue(
                ModItems.BRONZE_INGOT.get().getDefaultInstance().is(ModTags.REPAIRS_BRONZE_TOOLS),
                "Bronze ingots do not repair bronze tools"
        );
        helper.assertTrue(
                tinPickaxe.is(ItemTags.MINING_ENCHANTABLE),
                "Tin pickaxe is not mining-enchantable"
        );
        helper.assertTrue(
                bronzePickaxe.is(ItemTags.DURABILITY_ENCHANTABLE),
                "Bronze pickaxe is not durability-enchantable"
        );
        helper.assertTrue(
                tinSword.is(ItemTags.WEAPON_ENCHANTABLE),
                "Tin sword is not weapon-enchantable"
        );
        helper.assertTrue(
                bronzeSword.is(ItemTags.SHARP_WEAPON_ENCHANTABLE),
                "Bronze sword is not sharp-weapon-enchantable"
        );
        helper.assertTrue(
                ModBlocks.TIN_ORE.get().defaultBlockState().is(BlockTags.MINEABLE_WITH_PICKAXE),
                "Tin ore is not mineable with a pickaxe"
        );
        helper.assertTrue(
                ModBlocks.TIN_ORE.get().defaultBlockState().is(BlockTags.NEEDS_STONE_TOOL),
                "Tin ore does not require a stone-tier tool"
        );
        helper.assertTrue(
                Blocks.DIAMOND_ORE.defaultBlockState().is(ModTags.INCORRECT_FOR_TIN_TOOL),
                "Tin tools unexpectedly harvest diamond ore"
        );
        helper.assertTrue(
                Blocks.OBSIDIAN.defaultBlockState().is(ModTags.INCORRECT_FOR_BRONZE_TOOL),
                "Bronze tools unexpectedly harvest obsidian"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate(value = "3x3x3", floor = true)
    @TestHolder(description = "Breaking a crusher drops the crusher item")
    static void crusherDropsItself(ExtendedGameTestHelper helper) {
        helper.setBlock(CRUSHER_POS, ModBlocks.CRUSHER.get());
        helper.breakBlock(CRUSHER_POS, Items.IRON_PICKAXE.getDefaultInstance(), null);
        helper.assertItemEntityCountIsAtLeast(
                ModItems.CRUSHER.get(),
                CRUSHER_POS,
                1.0,
                1
        );
        helper.succeed();
    }

    private static CrusherBlockEntity crusher(ExtendedGameTestHelper helper) {
        return helper.getBlockEntity(CRUSHER_POS, CrusherBlockEntity.class);
    }

    private record CrushCase(Item input, Item output) {
    }
}
