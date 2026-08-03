package dev.fishraposo.materialprogression.stone;

import dev.fishraposo.materialprogression.config.MaterialProgressionConfig;
import dev.fishraposo.materialprogression.progression.FeedbackMessages;
import dev.fishraposo.materialprogression.progression.OpeningAdvancements;
import dev.fishraposo.materialprogression.registry.ModDataAttachments;
import dev.fishraposo.materialprogression.registry.ModMaterials;
import net.minecraft.core.registries.BuiltInRegistries;
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
                || !(event.getEntity() instanceof ServerPlayer player)
                || player.getAbilities().instabuild) {
            return;
        }

        var state = player.level().getBlockState(event.getPos());
        var level = (ServerLevel) player.level();
        var ore = ModMaterials.oreMaterialForBlock(
                BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath()
        );
        if (ore.isPresent() && !state.canHarvestBlock(level, event.getPos(), player)) {
            if (tryAcquire(player)) {
                player.sendOverlayMessage(FeedbackMessages.oreToolRequired(
                        ore.orElseThrow(),
                        BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath()
                                .startsWith("gravel_")
                ));
            }
            return;
        }
        var family = StoneFamilyCatalog.get().bySource(state);
        if (family.isEmpty()) {
            return;
        }
        if (!MaterialProgressionConfig.enableGeologicalHardness()) {
            if (!GeologyMiningEvents.requiresCorrectToolForRockDrops(
                    level,
                    event.getPos(),
                    state,
                    player,
                    player.getMainHandItem()
            ) || !tryAcquire(player)) {
                return;
            }
            player.sendOverlayMessage(
                    FeedbackMessages.correctToolRequired(family.orElseThrow())
            );
            return;
        }

        var tier = GeologyTierResolver.resolve(
                level,
                event.getPos(),
                state
        );
        if (tier.isEmpty()
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
                        family.orElseThrow(),
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
