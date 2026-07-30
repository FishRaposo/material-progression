package dev.fishraposo.materialprogression.stone;

import dev.fishraposo.materialprogression.config.MaterialProgressionConfig;
import dev.fishraposo.materialprogression.progression.FeedbackMessages;
import dev.fishraposo.materialprogression.progression.OpeningAdvancements;
import dev.fishraposo.materialprogression.registry.ModDataAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class GeologyFeedbackEvents {
    private static final long FEEDBACK_COOLDOWN_TICKS = 20L;

    private GeologyFeedbackEvents() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(GeologyFeedbackEvents::onLeftClick);
    }

    private static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getAction()
                        != PlayerInteractEvent.LeftClickBlock.Action.START
                || !MaterialProgressionConfig.enableGeologicalHardness()
                || !(event.getEntity() instanceof ServerPlayer player)
                || player.getAbilities().instabuild) {
            return;
        }

        var state = player.level().getBlockState(event.getPos());
        var tier = GeologyTierResolver.resolve(
                (ServerLevel) player.level(),
                event.getPos(),
                state
        );
        var family = StoneFamilyCatalog.get().byRaw(state);
        if (tier.isEmpty()
                || family.isEmpty()
                || GeologyToolCapability.canMine(
                        player.getMainHandItem(),
                        state,
                        tier.orElseThrow()
                )) {
            return;
        }
        if (tier.orElseThrow() == GeologyTier.LEVEL_2) {
            OpeningAdvancements.awardDenseGeology(player);
        }
        if (!tryAcquire(player)) {
            return;
        }

        player.sendOverlayMessage(
                FeedbackMessages.insufficientGeology(
                        family.orElseThrow().family(),
                        tier.orElseThrow()
                )
        );
    }

    static boolean tryAcquire(ServerPlayer player) {
        long now = player.level().getGameTime();
        long previous = player.getData(
                ModDataAttachments.GEOLOGY_FEEDBACK_TICK
        );
        if (previous <= now
                && now - previous < FEEDBACK_COOLDOWN_TICKS) {
            return false;
        }
        player.setData(ModDataAttachments.GEOLOGY_FEEDBACK_TICK, now);
        return true;
    }
}
