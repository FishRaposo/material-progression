package dev.fishraposo.materialprogression.progression;

import dev.fishraposo.materialprogression.registry.ModDataAttachments;
import dev.fishraposo.materialprogression.registry.ModMaterials;
import dev.fishraposo.materialprogression.registry.ModTags;
import dev.fishraposo.materialprogression.stone.StoneFamilyCatalog;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/** Hammer prospecting deliberately examines only already-loaded local terrain. */
public final class ProspectingEvents {
    private static final int HORIZONTAL_RADIUS = 8;
    private static final int VERTICAL_RADIUS = 5;
    private static final long COOLDOWN = 100L;

    private ProspectingEvents() {}

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ProspectingEvents::onRightClick);
    }

    private static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || event.getHand() != net.minecraft.world.InteractionHand.MAIN_HAND
                || !player.getMainHandItem().is(ModTags.HAMMERS)
                || player.getAbilities().instabuild) {
            return;
        }
        var level = (net.minecraft.server.level.ServerLevel) player.level();
        var target = event.getPos();
        var targetState = level.getBlockState(target);
        if (StoneFamilyCatalog.get().bySource(targetState).isEmpty()
                && ModMaterials.oreMaterialForBlock(
                        BuiltInRegistries.BLOCK.getKey(targetState.getBlock()).getPath()
                ).isEmpty()) {
            return;
        }
        if (!tryAcquire(player)) {
            return;
        }
        findNearby(level, target).ifPresentOrElse(found -> player.sendOverlayMessage(
                FeedbackMessages.prospectingHint(found.material(), direction(target, found.pos()))
        ), () -> player.sendOverlayMessage(
                net.minecraft.network.chat.Component.translatable(
                        "message.material_progression.prospecting.none"
                )
        ));
    }

    private static java.util.Optional<FoundOre> findNearby(
            net.minecraft.server.level.ServerLevel level, BlockPos origin
    ) {
        FoundOre nearest = null;
        for (BlockPos candidate : BlockPos.betweenClosed(
                origin.offset(-HORIZONTAL_RADIUS, -VERTICAL_RADIUS, -HORIZONTAL_RADIUS),
                origin.offset(HORIZONTAL_RADIUS, VERTICAL_RADIUS, HORIZONTAL_RADIUS)
        )) {
            if (!level.hasChunkAt(candidate)) {
                continue;
            }
            String id = BuiltInRegistries.BLOCK.getKey(level.getBlockState(candidate).getBlock()).getPath();
            var material = ModMaterials.oreMaterialForBlock(id);
            if (material.isEmpty()) {
                continue;
            }
            int distance = candidate.distManhattan(origin);
            if (nearest == null || distance < nearest.distance()) {
                nearest = new FoundOre(candidate.immutable(), material.orElseThrow(), distance);
            }
        }
        return java.util.Optional.ofNullable(nearest);
    }

    private static String direction(BlockPos origin, BlockPos found) {
        int x = found.getX() - origin.getX();
        int z = found.getZ() - origin.getZ();
        if (Math.abs(x) >= Math.abs(z)) {
            return x >= 0 ? "east" : "west";
        }
        return z >= 0 ? "south" : "north";
    }

    private static boolean tryAcquire(ServerPlayer player) {
        long now = player.level().getGameTime();
        long previous = player.getData(ModDataAttachments.PROSPECTING_FEEDBACK_TICK);
        if (previous <= now && now - previous < COOLDOWN) {
            return false;
        }
        player.setData(ModDataAttachments.PROSPECTING_FEEDBACK_TICK, now);
        return true;
    }

    private record FoundOre(BlockPos pos, String material, int distance) {}
}
