package dev.fishraposo.materialprogression.client;

import dev.fishraposo.materialprogression.registry.ModTags;
import dev.fishraposo.materialprogression.stone.GeologyMiningPredictionCache;
import dev.fishraposo.materialprogression.stone.GeologyMiningSnapshotPayload;
import dev.fishraposo.materialprogression.stone.GeologyMiningSnapshotRequest;
import dev.fishraposo.materialprogression.stone.GeologyMiningTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Physical-client mining prediction driven by exact server snapshots.
 */
public final class ClientGeologyMiningEvents {
    private static final GeologyMiningPredictionCache CACHE =
            new GeologyMiningPredictionCache();

    private ClientGeologyMiningEvents() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(
                ClientGeologyMiningEvents::onLeftClick
        );
        NeoForge.EVENT_BUS.addListener(
                ClientGeologyMiningEvents::onBreakSpeed
        );
        NeoForge.EVENT_BUS.addListener(
                ClientGeologyMiningEvents::onClientTick
        );
        NeoForge.EVENT_BUS.addListener(
                ClientGeologyMiningEvents::onLoggingOut
        );
        NeoForge.EVENT_BUS.addListener(
                ClientGeologyMiningEvents::onClone
        );
    }

    public static void handleSnapshot(
            GeologyMiningSnapshotPayload payload,
            IPayloadContext context
    ) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            CACHE.clear();
            return;
        }
        CACHE.clearUnlessDimension(level.dimension().identifier());
        CACHE.accept(payload, level.getGameTime());
    }

    private static void onLeftClick(
            PlayerInteractEvent.LeftClickBlock event
    ) {
        if (!(event.getEntity() instanceof LocalPlayer player)
                || !event.getLevel().isClientSide()
                || player != Minecraft.getInstance().player
                || player.getAbilities().instabuild) {
            return;
        }

        switch (event.getAction()) {
            case START -> send(CACHE.beginTarget(
                    target(player, event.getPos()),
                    isSourceCandidate(player, event.getPos()),
                    player.level().getGameTime()
            ));
            case CLIENT_HOLD -> CACHE.continueTarget(
                    target(player, event.getPos()),
                    isSourceCandidate(player, event.getPos()),
                    player.level().getGameTime()
            ).ifPresent(ClientGeologyMiningEvents::send);
            case STOP, ABORT -> CACHE.clear();
        }
    }

    private static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!(event.getEntity() instanceof LocalPlayer player)
                || !player.level().isClientSide()
                || player != Minecraft.getInstance().player
                || event.getPosition().isEmpty()) {
            return;
        }
        GeologyMiningTarget target = new GeologyMiningTarget(
                player.level().dimension().identifier(),
                event.getPosition().orElseThrow(),
                Block.getId(event.getState())
        );
        event.setNewSpeed(CACHE.adjustSpeed(
                event.getNewSpeed(),
                target,
                event.getState().is(ModTags.STONE_SOURCES),
                player.level().getGameTime()
        ));
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            CACHE.clear();
            return;
        }
        CACHE.clearUnlessDimension(level.dimension().identifier());
    }

    private static void onLoggingOut(
            ClientPlayerNetworkEvent.LoggingOut event
    ) {
        CACHE.clear();
    }

    private static void onClone(ClientPlayerNetworkEvent.Clone event) {
        CACHE.clear();
    }

    private static GeologyMiningTarget target(
            LocalPlayer player,
            net.minecraft.core.BlockPos pos
    ) {
        BlockState state = player.level().getBlockState(pos);
        return new GeologyMiningTarget(
                player.level().dimension().identifier(),
                pos,
                Block.getId(state)
        );
    }

    private static boolean isSourceCandidate(
            LocalPlayer player,
            net.minecraft.core.BlockPos pos
    ) {
        return player.level().getBlockState(pos).is(ModTags.STONE_SOURCES);
    }

    private static void send(GeologyMiningSnapshotRequest request) {
        if (Minecraft.getInstance().getConnection() != null) {
            ClientPacketDistributor.sendToServer(request);
        }
    }
}
