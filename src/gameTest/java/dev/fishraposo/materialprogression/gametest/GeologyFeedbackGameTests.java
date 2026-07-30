package dev.fishraposo.materialprogression.gametest;

import dev.fishraposo.materialprogression.registry.ModDataAttachments;
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
