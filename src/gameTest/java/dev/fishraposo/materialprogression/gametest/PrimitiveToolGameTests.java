package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.progression.LogHarvestRule;
import dev.fishraposo.materialprogression.registry.ModItems;
import dev.fishraposo.materialprogression.registry.ModTags;
import dev.fishraposo.materialprogression.stone.GeologyTier;
import dev.fishraposo.materialprogression.stone.GeologyToolCapability;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class PrimitiveToolGameTests {
    private static final BlockPos BLOCK_POS = new BlockPos(2, 2, 2);

    private PrimitiveToolGameTests() {
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "A flint knife adds one Fiber without replacing normal short-grass drops")
    static void flintKnifeAddsOneFiberAndPreservesDrops(
            ExtendedGameTestHelper helper
    ) {
        resetEventFixtures(helper);
        placeShortGrass(helper);
        MaterialProgressionGameTestMod.addSentinelDropAt(
                helper.absolutePos(BLOCK_POS)
        );

        breakBlock(
                helper,
                BLOCK_POS,
                ModItems.FLINT_KNIFE.get().getDefaultInstance(),
                GameType.SURVIVAL
        );

        helper.assertValueEqual(
                1,
                itemCount(helper, BLOCK_POS, ModItems.PLANT_FIBER.get()),
                "short-grass Plant Fiber count"
        );
        helper.assertValueEqual(
                1,
                itemCount(helper, BLOCK_POS, Items.STICK),
                "preserved sentinel drop count"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "A bronze knife adds exactly two Fiber for one two-block tall grass")
    static void bronzeKnifeAddsTwoFiberOnceForTallGrass(
            ExtendedGameTestHelper helper
    ) {
        resetEventFixtures(helper);
        helper.setBlock(BLOCK_POS.below(), Blocks.DIRT);
        DoublePlantBlock.placeAt(
                helper.getLevel(),
                Blocks.TALL_GRASS.defaultBlockState(),
                helper.absolutePos(BLOCK_POS),
                Block.UPDATE_ALL
        );

        breakBlock(
                helper,
                BLOCK_POS,
                ModItems.BRONZE_KNIFE.get().getDefaultInstance(),
                GameType.SURVIVAL
        );

        helper.assertValueEqual(
                2,
                itemCount(helper, BLOCK_POS, ModItems.PLANT_FIBER.get()),
                "tall-grass Plant Fiber count"
        );
        helper.assertBlockPresent(Blocks.AIR, BLOCK_POS);
        helper.assertBlockPresent(Blocks.AIR, BLOCK_POS.above());
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Non-knives, creative breaking, and canceled breaks add no Fiber")
    static void inapplicablePlantBreaksAddNoFiber(
            ExtendedGameTestHelper helper
    ) {
        resetEventFixtures(helper);
        placeShortGrass(helper);
        breakBlock(
                helper,
                BLOCK_POS,
                new ItemStack(Items.STICK),
                GameType.SURVIVAL
        );
        helper.assertValueEqual(
                0,
                itemCount(helper, BLOCK_POS, ModItems.PLANT_FIBER.get()),
                "non-knife Fiber count"
        );

        placeShortGrass(helper);
        breakBlock(
                helper,
                BLOCK_POS,
                ModItems.FLINT_KNIFE.get().getDefaultInstance(),
                GameType.CREATIVE
        );
        helper.assertValueEqual(
                0,
                itemCount(helper, BLOCK_POS, ModItems.PLANT_FIBER.get()),
                "creative Fiber count"
        );

        placeShortGrass(helper);
        MaterialProgressionGameTestMod.cancelNextBreakAt(
                helper.absolutePos(BLOCK_POS)
        );
        boolean broken = breakBlock(
                helper,
                BLOCK_POS,
                ModItems.FLINT_KNIFE.get().getDefaultInstance(),
                GameType.SURVIVAL
        );
        helper.assertFalse(broken, "canceled short-grass break succeeded");
        helper.assertBlockPresent(Blocks.SHORT_GRASS, BLOCK_POS);
        helper.assertValueEqual(
                0,
                itemCount(helper, BLOCK_POS, ModItems.PLANT_FIBER.get()),
                "canceled-break Fiber count"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Flint and bronze hammers expose exact geology capabilities")
    static void hammersExposeExactGeologyCapabilities(
            ExtendedGameTestHelper helper
    ) {
        ItemStack flint = ModItems.FLINT_HAMMER.get().getDefaultInstance();
        ItemStack bronze = ModItems.BRONZE_HAMMER.get().getDefaultInstance();

        helper.assertTrue(flint.is(ModTags.HAMMERS), "Flint Hammer lacks the behavior tag");
        helper.assertTrue(bronze.is(ModTags.HAMMERS), "Bronze Hammer lacks the behavior tag");
        helper.assertTrue(flint.is(ItemTags.PICKAXES), "Flint Hammer lacks the vanilla pickaxe category");
        helper.assertTrue(bronze.is(ItemTags.PICKAXES), "Bronze Hammer lacks the vanilla pickaxe category");

        helper.assertTrue(
                GeologyToolCapability.canMine(
                        flint,
                        Blocks.STONE.defaultBlockState(),
                        GeologyTier.LEVEL_0
                ),
                "Flint Hammer did not reach level zero"
        );
        helper.assertTrue(
                GeologyToolCapability.canMine(
                        flint,
                        Blocks.STONE.defaultBlockState(),
                        GeologyTier.LEVEL_1
                ),
                "Flint Hammer did not reach level one"
        );
        helper.assertFalse(
                GeologyToolCapability.canMine(
                        flint,
                        Blocks.STONE.defaultBlockState(),
                        GeologyTier.LEVEL_2
                ),
                "Flint Hammer reached level two"
        );
        helper.assertTrue(
                GeologyToolCapability.canMine(
                        bronze,
                        Blocks.STONE.defaultBlockState(),
                        GeologyTier.LEVEL_2
                ),
                "Bronze Hammer did not reach level two"
        );
        helper.assertFalse(
                GeologyToolCapability.canMine(
                        bronze,
                        Blocks.STONE.defaultBlockState(),
                        GeologyTier.LEVEL_3
                ),
                "Bronze Hammer reached level three"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Hammers harvest ordinary pickaxe-mineable ores at their material capability")
    static void hammersAreOrdinaryPickaxeAlternatives(
            ExtendedGameTestHelper helper
    ) {
        helper.setBlock(BLOCK_POS, Blocks.IRON_ORE);
        breakBlock(
                helper,
                BLOCK_POS,
                ModItems.FLINT_HAMMER.get().getDefaultInstance(),
                GameType.SURVIVAL
        );
        helper.assertValueEqual(
                1,
                itemCount(helper, BLOCK_POS, Items.RAW_IRON),
                "Flint Hammer raw-iron count"
        );

        BlockPos diamondPos = BLOCK_POS.offset(2, 0, 0);
        helper.setBlock(diamondPos, Blocks.DIAMOND_ORE);
        breakBlock(
                helper,
                diamondPos,
                ModItems.BRONZE_HAMMER.get().getDefaultInstance(),
                GameType.SURVIVAL
        );
        helper.assertValueEqual(
                1,
                itemCount(helper, diamondPos, Items.DIAMOND),
                "Bronze Hammer diamond count"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Saws are real axe alternatives and log crafting remains four planks")
    static void sawsAreAxeAlternativesWithoutWoodRecipeNerf(
            ExtendedGameTestHelper helper
    ) {
        ConfigFixture.setRequireAxeForLogs(helper, true);
        ItemStack flint = ModItems.FLINT_SAW.get().getDefaultInstance();
        ItemStack bronze = ModItems.BRONZE_SAW.get().getDefaultInstance();
        helper.assertTrue(flint.is(ModTags.SAWS), "Flint Saw lacks the behavior tag");
        helper.assertTrue(bronze.is(ModTags.SAWS), "Bronze Saw lacks the behavior tag");
        helper.assertTrue(flint.is(ItemTags.AXES), "Flint Saw lacks the vanilla axe category");
        helper.assertTrue(bronze.is(ItemTags.AXES), "Bronze Saw lacks the vanilla axe category");
        helper.assertTrue(
                flint.getItem().canPerformAction(flint, ItemAbilities.AXE_STRIP),
                "Flint Saw cannot perform axe actions"
        );
        helper.assertTrue(
                LogHarvestRule.canHarvest(
                        true,
                        true,
                        Blocks.OAK_LOG.defaultBlockState(),
                        bronze
                ),
                "Bronze Saw did not satisfy the log harvest rule"
        );

        helper.setBlock(BLOCK_POS, Blocks.OAK_LOG);
        breakBlock(helper, BLOCK_POS, flint, GameType.SURVIVAL);
        helper.assertValueEqual(
                1,
                itemCount(helper, BLOCK_POS, Items.OAK_LOG),
                "Flint Saw oak-log count"
        );

        CraftingInput input = CraftingInput.of(
                1,
                1,
                List.of(new ItemStack(Items.OAK_LOG))
        );
        ItemStack result = helper.getLevel()
                .recipeAccess()
                .getRecipeFor(RecipeType.CRAFTING, input, helper.getLevel())
                .orElseThrow()
                .value()
                .assemble(input);
        GameTestSupport.assertStack(
                helper,
                result,
                Items.OAK_PLANKS,
                4,
                "ordinary oak-log recipe"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Primitive workshop tools keep tier durability and shared repair ingredients")
    static void toolDurabilityAndRepairIngredients(
            ExtendedGameTestHelper helper
    ) {
        List<ItemStack> flintTools = List.of(
                ModItems.FLINT_KNIFE.get().getDefaultInstance(),
                ModItems.FLINT_HAMMER.get().getDefaultInstance(),
                ModItems.FLINT_SAW.get().getDefaultInstance()
        );
        List<ItemStack> bronzeTools = List.of(
                ModItems.BRONZE_KNIFE.get().getDefaultInstance(),
                ModItems.BRONZE_HAMMER.get().getDefaultInstance(),
                ModItems.BRONZE_SAW.get().getDefaultInstance()
        );
        ItemStack flintShard = ModItems.FLINT_SHARD.get().getDefaultInstance();
        ItemStack bronzeIngot = ModItems.BRONZE_INGOT.get().getDefaultInstance();

        for (ItemStack tool : flintTools) {
            helper.assertValueEqual(64, tool.getMaxDamage(), "Flint tool durability");
            helper.assertTrue(
                    tool.isValidRepairItem(flintShard),
                    tool + " rejected a shared Flint Shard repair item"
            );
            helper.assertFalse(
                    tool.isValidRepairItem(bronzeIngot),
                    tool + " accepted a Bronze Ingot repair item"
            );
        }
        for (ItemStack tool : bronzeTools) {
            helper.assertValueEqual(325, tool.getMaxDamage(), "Bronze tool durability");
            helper.assertTrue(
                    tool.isValidRepairItem(bronzeIngot),
                    tool + " rejected a shared Bronze Ingot repair item"
            );
            helper.assertFalse(
                    tool.isValidRepairItem(flintShard),
                    tool + " accepted a Flint Shard repair item"
            );
        }
        helper.succeed();
    }

    private static void placeShortGrass(ExtendedGameTestHelper helper) {
        helper.setBlock(BLOCK_POS.below(), Blocks.DIRT);
        helper.setBlock(BLOCK_POS, Blocks.SHORT_GRASS);
    }

    private static boolean breakBlock(
            ExtendedGameTestHelper helper,
            BlockPos pos,
            ItemStack tool,
            GameType gameType
    ) {
        var player = helper.makeTickingMockServerPlayerInLevel(gameType);
        player.setItemInHand(InteractionHand.MAIN_HAND, tool);
        return player.gameMode.destroyBlock(helper.absolutePos(pos));
    }

    private static int itemCount(
            ExtendedGameTestHelper helper,
            BlockPos pos,
            Item item
    ) {
        BlockPos absolute = helper.absolutePos(pos);
        return helper.getLevel()
                .getEntitiesOfClass(
                        ItemEntity.class,
                        AABB.ofSize(
                                Vec3.atCenterOf(absolute),
                                3.0,
                                3.0,
                                3.0
                        )
                )
                .stream()
                .filter(entity -> entity.getItem().is(item))
                .mapToInt(entity -> entity.getItem().getCount())
                .sum();
    }

    private static void resetEventFixtures(ExtendedGameTestHelper helper) {
        MaterialProgressionGameTestMod.clearCancellations();
        helper.addEndListener(
                ignored -> MaterialProgressionGameTestMod.clearCancellations()
        );
    }
}
