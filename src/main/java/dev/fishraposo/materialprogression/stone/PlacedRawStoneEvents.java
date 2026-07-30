package dev.fishraposo.materialprogression.stone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDestroyBlockEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.level.PistonEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class PlacedRawStoneEvents {
    private static final Map<PistonKey, PendingPistonMove> PISTON_MOVES =
            new HashMap<>();
    private static final Map<ServerLevel, List<PendingMarkerUpdate>>
            MARKER_UPDATES = new HashMap<>();

    private PlacedRawStoneEvents() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(PlacedRawStoneEvents::onPlaced);
        NeoForge.EVENT_BUS.addListener(PlacedRawStoneEvents::onBlockBroken);
        NeoForge.EVENT_BUS.addListener(
                PlacedRawStoneEvents::onNeighborNotify
        );
        NeoForge.EVENT_BUS.addListener(
                PlacedRawStoneEvents::onFluidPlacedBlock
        );
        NeoForge.EVENT_BUS.addListener(
                PlacedRawStoneEvents::onToolModification
        );
        NeoForge.EVENT_BUS.addListener(PlacedRawStoneEvents::onExplosion);
        NeoForge.EVENT_BUS.addListener(
                PlacedRawStoneEvents::onLivingDestroyedBlock
        );
        NeoForge.EVENT_BUS.addListener(
                PlacedRawStoneEvents::onPistonPre
        );
        NeoForge.EVENT_BUS.addListener(PlacedRawStoneEvents::onPistonPost);
        NeoForge.EVENT_BUS.addListener(
                PlacedRawStoneEvents::onLevelTickPost
        );
        NeoForge.EVENT_BUS.addListener(PlacedRawStoneEvents::onLevelUnload);
        NeoForge.EVENT_BUS.addListener(PlacedRawStoneEvents::onServerStopped);
    }

    private static void onPlaced(BlockEvent.EntityPlaceEvent event) {
        if (event.isCanceled()
                || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (event instanceof BlockEvent.EntityMultiPlaceEvent multiPlace) {
            for (var snapshot : multiPlace.getReplacedBlockSnapshots()) {
                queuePlacement(
                        level,
                        snapshot.getPos(),
                        level.getBlockState(snapshot.getPos()),
                        event.getEntity() instanceof Player,
                        event
                );
            }
            return;
        }
        queuePlacement(
                level,
                event.getPos(),
                event.getPlacedBlock(),
                event.getEntity() instanceof Player,
                event
        );
    }

    private static void onBlockBroken(BreakBlockEvent event) {
        if (!event.isCanceled()
                && event.getLevel() instanceof ServerLevel level) {
            queueBreak(level, event);
        }
    }

    private static void onNeighborNotify(
            BlockEvent.NeighborNotifyEvent event
    ) {
        if (event.getLevel() instanceof ServerLevel level) {
            observeMutation(
                    level,
                    event.getPos(),
                    event.getState()
            );
        }
    }

    private static void onFluidPlacedBlock(
            BlockEvent.FluidPlaceBlockEvent event
    ) {
        if (!event.isCanceled()
                && event.getLevel() instanceof ServerLevel level) {
            queueMutation(level, event.getPos(), event.getOriginalState());
        }
    }

    private static void onToolModification(
            BlockEvent.BlockToolModificationEvent event
    ) {
        if (!event.isCanceled()
                && !event.isSimulated()
                && event.getLevel() instanceof ServerLevel level) {
            queueMutation(level, event.getPos(), event.getState());
        }
    }

    private static void onExplosion(ExplosionEvent.Detonate event) {
        if (event.getLevel() instanceof ServerLevel level) {
            for (BlockPos pos : event.getAffectedBlocks()) {
                queueMutation(
                        level,
                        pos,
                        level.getBlockState(pos)
                );
            }
        }
    }

    private static void onLivingDestroyedBlock(
            LivingDestroyBlockEvent event
    ) {
        if (!event.isCanceled()
                && event.getEntity().level() instanceof ServerLevel level) {
            queueMutation(level, event.getPos(), event.getState());
        }
    }

    private static void onPistonPre(PistonEvent.Pre event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || event.isCanceled()) {
            return;
        }

        List<BlockPos> sources = new ArrayList<>();
        List<BlockPos> destinations = new ArrayList<>();
        List<BlockPos> destroyed = new ArrayList<>();
        if (event.getPistonMoveType()
                        == PistonEvent.PistonMoveType.RETRACT
                && event.getState().is(Blocks.STICKY_PISTON)) {
            BlockPos source = event.getFaceOffsetPos()
                    .relative(event.getDirection());
            if (PlacedRawStoneTracker.isMarked(level, source)) {
                sources.add(source.immutable());
                destinations.add(event.getFaceOffsetPos().immutable());
            }
        }

        var resolver = event.getStructureHelper();
        if (resolver != null && resolver.resolve()) {
            for (BlockPos source : resolver.getToPush()) {
                if (!sources.contains(source)
                        && PlacedRawStoneTracker.isMarked(level, source)) {
                    sources.add(source.immutable());
                    destinations.add(
                            source.relative(resolver.getPushDirection())
                                    .immutable()
                    );
                }
            }
            resolver.getToDestroy().stream()
                    .filter(pos ->
                            PlacedRawStoneTracker.isMarked(level, pos)
                    )
                    .map(BlockPos::immutable)
                    .forEach(destroyed::add);
        }
        if (sources.isEmpty() && destroyed.isEmpty()) {
            return;
        }
        PISTON_MOVES.put(
                PistonKey.of(event, level),
                new PendingPistonMove(sources, destinations, destroyed)
        );
    }

    private static void onPistonPost(PistonEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        PendingPistonMove move = PISTON_MOVES.remove(
                PistonKey.of(event, level)
        );
        if (move == null) {
            return;
        }

        move.sources().forEach(pos ->
                PlacedRawStoneTracker.clear(level, pos)
        );
        move.destroyed().forEach(pos ->
                PlacedRawStoneTracker.clear(level, pos)
        );
        move.destinations().forEach(pos ->
                PlacedRawStoneTracker.markUnchecked(level, pos)
        );
    }

    private static void onLevelTickPost(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) {
            clearPistonMoves(level);
            List<PendingMarkerUpdate> updates =
                    MARKER_UPDATES.remove(level);
            if (updates != null) {
                updates.forEach(update -> update.apply(level));
            }
        }
    }

    private static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            clearPistonMoves(level);
            MARKER_UPDATES.remove(level);
        }
    }

    private static void onServerStopped(ServerStoppedEvent event) {
        PISTON_MOVES.clear();
        MARKER_UPDATES.clear();
    }

    private static void clearPistonMoves(ServerLevel level) {
        PISTON_MOVES.keySet().removeIf(key -> key.level() == level);
    }

    private static void queuePlacement(
            ServerLevel level,
            BlockPos pos,
            BlockState expectedState,
            boolean playerPlacement,
            BlockEvent.EntityPlaceEvent event
    ) {
        queueUpdate(
                level,
                PendingMarkerUpdate.placement(
                        pos,
                        expectedState,
                        playerPlacement,
                        event::isCanceled
                )
        );
    }

    private static void queueBreak(
            ServerLevel level,
            BreakBlockEvent event
    ) {
        boolean placedBeforeMutation = PlacedRawStoneTracker.isMarked(
                level,
                event.getPos(),
                event.getState()
        );
        queueUpdate(
                level,
                PendingMarkerUpdate.breakBlock(
                        event.getPos(),
                        event.getState(),
                        placedBeforeMutation,
                        event::isCanceled
                )
        );
    }

    private static void queueMutation(
            ServerLevel level,
            BlockPos pos,
            BlockState originalState
    ) {
        queueUpdate(
                level,
                PendingMarkerUpdate.mutation(pos, originalState)
        );
    }

    private static void queueUpdate(
            ServerLevel level,
            PendingMarkerUpdate update
    ) {
        MARKER_UPDATES.computeIfAbsent(
                level,
                ignored -> new ArrayList<>()
        ).add(update);
    }

    private static void observeMutation(
            ServerLevel level,
            BlockPos pos,
            BlockState observedState
    ) {
        List<PendingMarkerUpdate> updates = MARKER_UPDATES.get(level);
        if (updates == null) {
            return;
        }
        for (int index = updates.size() - 1; index >= 0; index--) {
            PendingMarkerUpdate update = updates.get(index);
            if (update.observeMutationAt(pos, observedState)) {
                return;
            }
        }
    }

    static boolean isEffectivelyMarked(
            ServerLevel level,
            BlockPos pos,
            BlockState expectedState,
            boolean storedMarker
    ) {
        List<PendingMarkerUpdate> updates = MARKER_UPDATES.get(level);
        if (updates == null) {
            return storedMarker;
        }
        boolean marked = storedMarker;
        Boolean removedStateWasMarked = null;
        for (PendingMarkerUpdate update : updates) {
            if (!update.pos().equals(pos) || update.isCanceled()) {
                continue;
            }
            if (update.placement()) {
                if (update.expectedState().equals(expectedState)) {
                    marked = update.playerPlacement();
                    removedStateWasMarked = null;
                }
            } else if (update.mutationObserved()) {
                if (update.breakBlock()
                        && update.expectedState().equals(expectedState)) {
                    removedStateWasMarked =
                            update.placedBeforeMutation();
                } else {
                    removedStateWasMarked = null;
                }
                marked = false;
            }
        }
        if (!level.getBlockState(pos).equals(expectedState)
                && removedStateWasMarked != null) {
            return removedStateWasMarked;
        }
        return marked;
    }

    private record PistonKey(
            ServerLevel level,
            BlockPos piston,
            net.minecraft.core.Direction direction,
            PistonEvent.PistonMoveType moveType
    ) {
        private static PistonKey of(
                PistonEvent event,
                ServerLevel level
        ) {
            return new PistonKey(
                    level,
                    event.getPos().immutable(),
                    event.getDirection(),
                    event.getPistonMoveType()
            );
        }
    }

    private record PendingPistonMove(
            List<BlockPos> sources,
            List<BlockPos> destinations,
            List<BlockPos> destroyed
    ) {
    }

    private record PendingMarkerUpdate(
            BlockPos pos,
            BlockState expectedState,
            boolean placement,
            boolean playerPlacement,
            boolean breakBlock,
            boolean placedBeforeMutation,
            MutationObservation mutationObservation,
            BooleanSupplier canceled
    ) {
        private static PendingMarkerUpdate placement(
                BlockPos pos,
                BlockState expectedState,
                boolean playerPlacement,
                BooleanSupplier canceled
        ) {
            return new PendingMarkerUpdate(
                    pos.immutable(),
                    expectedState,
                    true,
                    playerPlacement,
                    false,
                    false,
                    null,
                    canceled
            );
        }

        private static PendingMarkerUpdate mutation(
                BlockPos pos,
                BlockState originalState
        ) {
            return new PendingMarkerUpdate(
                    pos.immutable(),
                    originalState,
                    false,
                    false,
                    false,
                    false,
                    new MutationObservation(),
                    () -> false
            );
        }

        private static PendingMarkerUpdate breakBlock(
                BlockPos pos,
                BlockState originalState,
                boolean placedBeforeMutation,
                BooleanSupplier canceled
        ) {
            return new PendingMarkerUpdate(
                    pos.immutable(),
                    originalState,
                    false,
                    false,
                    true,
                    placedBeforeMutation,
                    new MutationObservation(),
                    canceled
            );
        }

        private boolean isCanceled() {
            return canceled.getAsBoolean();
        }

        private boolean mutationObserved() {
            return mutationObservation != null
                    && mutationObservation.observed();
        }

        private boolean observeMutationAt(
                BlockPos changedPos,
                BlockState observedState
        ) {
            if (mutationObservation == null
                    || mutationObservation.observed()
                    || !pos.equals(changedPos)
                    || expectedState.equals(observedState)) {
                return false;
            }
            mutationObservation.observe();
            return true;
        }

        private void apply(ServerLevel level) {
            if (isCanceled()) {
                return;
            }
            BlockState currentState = level.getBlockState(pos);
            if (placement) {
                if (!currentState.equals(expectedState)) {
                    return;
                }
                // World generation, commands, structures, and direct mod
                // setBlock calls do not emit a player EntityPlaceEvent and
                // therefore remain natural.
                if (playerPlacement
                        && StoneFamilyCatalog.get()
                                .byRaw(currentState)
                                .isPresent()) {
                    PlacedRawStoneTracker.mark(level, pos);
                } else {
                    PlacedRawStoneTracker.clear(level, pos);
                }
            } else if (mutationObserved()) {
                PlacedRawStoneTracker.clear(level, pos);
            }
        }
    }

    private static final class MutationObservation {
        private boolean observed;

        private boolean observed() {
            return observed;
        }

        private void observe() {
            observed = true;
        }
    }
}
