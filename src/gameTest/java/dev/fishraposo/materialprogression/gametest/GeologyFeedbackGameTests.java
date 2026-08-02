package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.registry.ModDataAttachments;
import dev.fishraposo.materialprogression.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "material_progression")
public final class GeologyFeedbackGameTests {
    private static final BlockPos BLOCK_POS = new BlockPos(2, 2, 2);

    private GeologyFeedbackGameTests() {
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Insufficient geology feedback is throttled per player")
    static void insufficientFeedbackIsPerPlayerAndThrottled(
            ExtendedGameTestHelper helper
    ) {
        ConfigFixture.setEnableGeologicalHardness(helper, true);
        encloseCalcite(helper);
        ServerPlayer first = player(helper);
        ServerPlayer second = player(helper);
        long now = helper.getLevel().getGameTime();

        first.setData(ModDataAttachments.GEOLOGY_FEEDBACK_TICK, now - 19);
        postStart(helper, first);
        helper.assertValueEqual(
                now - 19,
                first.getData(ModDataAttachments.GEOLOGY_FEEDBACK_TICK),
                "throttled player's feedback tick"
        );

        postStart(helper, second);
        helper.assertValueEqual(
                now,
                second.getData(ModDataAttachments.GEOLOGY_FEEDBACK_TICK),
                "independent player's feedback tick"
        );

        first.setData(ModDataAttachments.GEOLOGY_FEEDBACK_TICK, now - 20);
        postStart(helper, first);
        helper.assertValueEqual(
                now,
                first.getData(ModDataAttachments.GEOLOGY_FEEDBACK_TICK),
                "elapsed player's feedback tick"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Hardness-disabled Rock denials warn for wrong tools only")
    static void hardnessDisabledWrongToolFeedbackIsThrottled(
            ExtendedGameTestHelper helper
    ) {
        ConfigFixture.setEnableGeologicalHardness(helper, false);
        ConfigFixture.setEnableStoneRockDrops(helper, true);
        helper.setBlock(BLOCK_POS, Blocks.STONE);
        ServerPlayer player = player(helper);
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        long now = helper.getLevel().getGameTime();

        postStart(helper, player);
        helper.assertValueEqual(
                now,
                player.getData(ModDataAttachments.GEOLOGY_FEEDBACK_TICK),
                "wrong-tool generic feedback tick"
        );

        player.setData(ModDataAttachments.GEOLOGY_FEEDBACK_TICK, now - 19);
        postStart(helper, player);
        helper.assertValueEqual(
                now - 19,
                player.getData(ModDataAttachments.GEOLOGY_FEEDBACK_TICK),
                "throttled wrong-tool generic feedback tick"
        );
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Capable tools keep Rock drops without feedback")
    static void capableToolKeepsRocksWithoutFeedback(
            ExtendedGameTestHelper helper
    ) {
        ConfigFixture.setEnableGeologicalHardness(helper, false);
        ConfigFixture.setEnableStoneRockDrops(helper, true);
        helper.setBlock(BLOCK_POS, Blocks.STONE);
        ServerPlayer player = player(helper);
        long now = helper.getLevel().getGameTime();

        player.setData(ModDataAttachments.GEOLOGY_FEEDBACK_TICK, now - 20);
        player.setItemInHand(
                InteractionHand.MAIN_HAND,
                new ItemStack(Items.STONE_PICKAXE)
        );
        postStart(helper, player);
        helper.assertValueEqual(
                now - 20,
                player.getData(ModDataAttachments.GEOLOGY_FEEDBACK_TICK),
                "capable-tool feedback tick"
        );
        helper.assertTrue(
                player.gameMode.destroyBlock(helper.absolutePos(BLOCK_POS)),
                "capable tool could not break Stone"
        );
        helper.assertItemEntityCountIsAtLeast(
                ModItems.ROCK.get(),
                BLOCK_POS,
                2.0,
                2
        );
        helper.succeed();
    }

    private static void postStart(
            ExtendedGameTestHelper helper,
            ServerPlayer player
    ) {
        NeoForge.EVENT_BUS.post(new PlayerInteractEvent.LeftClickBlock(
                player,
                helper.absolutePos(BLOCK_POS),
                Direction.UP,
                PlayerInteractEvent.LeftClickBlock.Action.START
        ));
    }

    private static ServerPlayer player(ExtendedGameTestHelper helper) {
        ServerPlayer player = helper.makeTickingMockServerPlayerInLevel(
                GameType.SURVIVAL
        );
        player.setItemInHand(
                InteractionHand.MAIN_HAND,
                new ItemStack(Items.WOODEN_PICKAXE)
        );
        return player;
    }

    private static void encloseCalcite(ExtendedGameTestHelper helper) {
        helper.setBlock(BLOCK_POS, Blocks.CALCITE);
        for (Direction direction : Direction.values()) {
            helper.setBlock(BLOCK_POS.relative(direction), Blocks.STONE);
        }
    }
}
