package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.registry.ModItems;
import dev.fishraposo.materialprogression.stone.GeologyTierResolver;
import dev.fishraposo.materialprogression.stone.PlacedRawStoneTracker;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class GeologyMiningGameTests {
    private static final BlockPos BLOCK_POS = new BlockPos(2, 2, 2);

    private GeologyMiningGameTests() {
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Geology divides live mining speed by the resolved level multiplier")
    static void liveMiningSpeedUsesTierDivisor(ExtendedGameTestHelper helper) {
        ConfigFixture.setEnableStoneRockDrops(helper, true);
        helper.setBlock(BLOCK_POS, Blocks.CALCITE);
        ServerPlayer player = player(helper, new ItemStack(Items.STONE_PICKAXE));
        BlockPos absolute = helper.absolutePos(BLOCK_POS);
        var state = helper.getBlockState(BLOCK_POS);

        ConfigFixture.setEnableGeologicalHardness(helper, false);
        float vanilla = player.getDestroySpeed(state, absolute);
        ConfigFixture.setEnableGeologicalHardness(helper, true);
        int level = GeologyTierResolver.resolve(
                helper.getLevel(),
                absolute,
                state
        ).orElseThrow().level();
        float changed = player.getDestroySpeed(state, absolute);

        helper.assertValueEqual(1, level, "exposed fixture geology level");
        helper.assertTrue(
                Math.abs(changed - vanilla / 2.5F) < 0.0001F,
                "level-one speed was " + changed + " instead of " + vanilla / 2.5F
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Bronze reaches level two while stone cannot")
    static void bronzeQualifiesForIronLevelGeology(ExtendedGameTestHelper helper) {
        ConfigFixture.setEnableGeologicalHardness(helper, true);
        ConfigFixture.setEnableStoneRockDrops(helper, true);
        enclose(helper, BLOCK_POS, Blocks.CALCITE);
        helper.assertValueEqual(
                2,
                GeologyTierResolver.resolve(
                        helper.getLevel(),
                        helper.absolutePos(BLOCK_POS),
                        helper.getBlockState(BLOCK_POS)
                ).orElseThrow().level(),
                "enclosed calcite geology level"
        );

        breakBlock(helper, new ItemStack(Items.STONE_PICKAXE));
        helper.assertValueEqual(0, itemCount(helper, ModItems.ROCK.get()), "stone-pick Rock count");

        enclose(helper, BLOCK_POS, Blocks.CALCITE);
        breakBlock(helper, new ItemStack(ModItems.BRONZE_PICKAXE.get()));
        int count = itemCount(helper, ModItems.CALCITE_ROCK.get());
        helper.assertTrue(count == 2 || count == 3, "bronze Calcite Rock count was " + count);
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Ordinary raw stone drops two or three matching Rocks")
    static void normalStoneDropsTwoOrThreeRocks(ExtendedGameTestHelper helper) {
        ConfigFixture.setEnableGeologicalHardness(helper, true);
        ConfigFixture.setEnableStoneRockDrops(helper, true);
        helper.setBlock(BLOCK_POS, Blocks.STONE);
        breakBlock(helper, new ItemStack(ModItems.BRONZE_PICKAXE.get()));
        int count = itemCount(helper, ModItems.ROCK.get());
        helper.assertTrue(count == 2 || count == 3, "normal Rock count was " + count);
        helper.assertValueEqual(0, itemCount(helper, Items.COBBLESTONE), "cobblestone count");
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Fortune one or higher yields exactly four family Rocks")
    static void fortuneDropsExactlyFourRocks(ExtendedGameTestHelper helper) {
        ConfigFixture.setEnableGeologicalHardness(helper, true);
        ConfigFixture.setEnableStoneRockDrops(helper, true);
        helper.setBlock(BLOCK_POS, Blocks.GRANITE);
        ItemStack tool = new ItemStack(ModItems.BRONZE_PICKAXE.get());
        enchant(helper, tool, Enchantments.FORTUNE);
        breakBlock(helper, tool);
        helper.assertValueEqual(
                4,
                itemCount(helper, ModItems.GRANITE_ROCK.get()),
                "Fortune Granite Rock count"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Silk Touch preserves the original raw family block")
    static void silkTouchPreservesRawBlock(ExtendedGameTestHelper helper) {
        ConfigFixture.setEnableGeologicalHardness(helper, true);
        ConfigFixture.setEnableStoneRockDrops(helper, true);
        helper.setBlock(BLOCK_POS, Blocks.STONE);
        ItemStack tool = new ItemStack(ModItems.BRONZE_PICKAXE.get());
        enchant(helper, tool, Enchantments.SILK_TOUCH);
        breakBlock(helper, tool);
        helper.assertValueEqual(1, itemCount(helper, Items.STONE), "Silk Touch stone count");
        helper.assertValueEqual(0, itemCount(helper, ModItems.ROCK.get()), "Silk Touch Rock count");
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Wrong tools and insufficient capabilities drop no Rocks")
    static void wrongAndInsufficientToolsDropNothing(ExtendedGameTestHelper helper) {
        ConfigFixture.setEnableGeologicalHardness(helper, true);
        ConfigFixture.setEnableStoneRockDrops(helper, true);
        helper.setBlock(BLOCK_POS, Blocks.STONE);
        breakBlock(helper, new ItemStack(Items.IRON_SWORD));
        helper.assertValueEqual(0, allItemCount(helper), "wrong-tool drop count");

        enclose(helper, BLOCK_POS, Blocks.STONE);
        breakBlock(helper, new ItemStack(Items.STONE_PICKAXE));
        helper.assertValueEqual(0, allItemCount(helper), "insufficient-tool drop count");
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Drop opt-out preserves the vanilla raw-stone loot")
    static void dropTogglePreservesVanillaLoot(ExtendedGameTestHelper helper) {
        ConfigFixture.setEnableGeologicalHardness(helper, true);
        ConfigFixture.setEnableStoneRockDrops(helper, false);
        helper.setBlock(BLOCK_POS, Blocks.STONE);
        breakBlock(helper, new ItemStack(ModItems.BRONZE_PICKAXE.get()));
        helper.assertValueEqual(1, itemCount(helper, Items.COBBLESTONE), "vanilla cobblestone count");
        helper.assertValueEqual(0, itemCount(helper, ModItems.ROCK.get()), "disabled Rock count");
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Hardness opt-out uses vanilla correctness while Rock drops remain enabled")
    static void hardnessToggleKeepsRockDrops(ExtendedGameTestHelper helper) {
        ConfigFixture.setEnableGeologicalHardness(helper, false);
        ConfigFixture.setEnableStoneRockDrops(helper, true);
        enclose(helper, BLOCK_POS, Blocks.DEEPSLATE);
        breakBlock(helper, new ItemStack(Items.STONE_PICKAXE));
        int count = itemCount(helper, ModItems.DEEPSLATE_ROCK.get());
        helper.assertTrue(count == 2 || count == 3, "hardness-opt-out Rock count was " + count);
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Player-placed raw stone still fragments and clears its marker")
    static void placedRawStoneFragmentsAndClearsMarker(ExtendedGameTestHelper helper) {
        ConfigFixture.setEnableGeologicalHardness(helper, true);
        ConfigFixture.setEnableStoneRockDrops(helper, true);
        BlockPos support = BLOCK_POS.below();
        helper.setBlock(support, Blocks.COBBLESTONE);
        ServerPlayer player = player(helper, new ItemStack(Blocks.STONE));
        helper.placeAt(player, player.getMainHandItem(), support, Direction.UP);
        BlockPos absolute = helper.absolutePos(BLOCK_POS);
        helper.runAfterDelay(1, () -> {
            helper.assertTrue(
                    PlacedRawStoneTracker.isMarked(helper.getLevel(), absolute),
                    "fixture raw stone was not marked"
            );
            player.setItemInHand(
                    InteractionHand.MAIN_HAND,
                    new ItemStack(Items.WOODEN_PICKAXE)
            );
            player.gameMode.destroyBlock(absolute);
        });
        helper.runAfterDelay(2, () -> {
            helper.assertFalse(
                    PlacedRawStoneTracker.isMarked(helper.getLevel(), absolute),
                    "removed raw stone retained its marker"
            );
            int count = itemCount(helper, ModItems.ROCK.get());
            helper.assertTrue(
                    count == 2 || count == 3,
                    "placed Rock count was " + count
            );
            helper.succeed();
        });
    }

    private static void enchant(
            ExtendedGameTestHelper helper,
            ItemStack stack,
            net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> key
    ) {
        var enchantment = helper.getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(key);
        stack.enchant(enchantment, 1);
    }

    private static void enclose(
            ExtendedGameTestHelper helper,
            BlockPos center,
            Block block
    ) {
        helper.setBlock(center, block);
        for (Direction direction : Direction.values()) {
            helper.setBlock(center.relative(direction), Blocks.STONE);
        }
    }

    private static void breakBlock(
            ExtendedGameTestHelper helper,
            ItemStack tool
    ) {
        player(helper, tool).gameMode.destroyBlock(helper.absolutePos(BLOCK_POS));
    }

    private static ServerPlayer player(
            ExtendedGameTestHelper helper,
            ItemStack tool
    ) {
        ServerPlayer player = helper.makeTickingMockServerPlayerInLevel(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, tool);
        return player;
    }

    private static int itemCount(ExtendedGameTestHelper helper, Item item) {
        return itemEntities(helper).stream()
                .filter(entity -> entity.getItem().is(item))
                .mapToInt(entity -> entity.getItem().getCount())
                .sum();
    }

    private static int allItemCount(ExtendedGameTestHelper helper) {
        return itemEntities(helper).stream()
                .mapToInt(entity -> entity.getItem().getCount())
                .sum();
    }

    private static List<ItemEntity> itemEntities(ExtendedGameTestHelper helper) {
        BlockPos absolute = helper.absolutePos(BLOCK_POS);
        return helper.getLevel().getEntitiesOfClass(
                ItemEntity.class,
                AABB.ofSize(Vec3.atCenterOf(absolute), 5.0, 5.0, 5.0)
        );
    }
}
