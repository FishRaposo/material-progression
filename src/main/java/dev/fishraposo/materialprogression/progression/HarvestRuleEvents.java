package dev.fishraposo.materialprogression.progression;

import dev.fishraposo.materialprogression.config.MaterialProgressionConfig;
import dev.fishraposo.materialprogression.registry.ModDataAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class HarvestRuleEvents {
    private static final long FEEDBACK_COOLDOWN_TICKS = 20L;

    private HarvestRuleEvents() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(HarvestRuleEvents::onHarvestCheck);
        NeoForge.EVENT_BUS.addListener(HarvestRuleEvents::onLeftClickLog);
    }

    private static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        event.setCanHarvest(LogHarvestRule.canHarvest(
                event.canHarvest(),
                MaterialProgressionConfig.requireAxeForLogs(),
                event.getTargetBlock(),
                event.getEntity().getMainHandItem()
        ));
    }

    private static void onLeftClickLog(
            PlayerInteractEvent.LeftClickBlock event
    ) {
        if (event.getAction()
                        != PlayerInteractEvent.LeftClickBlock.Action.START
                || !MaterialProgressionConfig.requireAxeForLogs()
                || !(event.getEntity() instanceof ServerPlayer player)
                || player.getAbilities().instabuild) {
            return;
        }
        var state = player.level().getBlockState(event.getPos());
        if (!state.is(BlockTags.LOGS)
                || player.getMainHandItem().is(ItemTags.AXES)
                || !tryAcquireLogFeedback(player)) {
            return;
        }
        player.sendOverlayMessage(FeedbackMessages.logRequiresTool());
    }

    static boolean tryAcquireLogFeedback(ServerPlayer player) {
        long now = player.level().getGameTime();
        long previous = player.getData(ModDataAttachments.LOG_FEEDBACK_TICK);
        if (previous <= now
                && now - previous < FEEDBACK_COOLDOWN_TICKS) {
            return false;
        }
        player.setData(ModDataAttachments.LOG_FEEDBACK_TICK, now);
        return true;
    }
}
