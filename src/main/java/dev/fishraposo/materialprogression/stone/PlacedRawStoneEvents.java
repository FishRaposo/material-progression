package dev.fishraposo.materialprogression.stone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDestroyBlockEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.PistonEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class PlacedRawStoneEvents {
    private static final Map<PistonKey, PendingPistonMove> PISTON_MOVES =
            new HashMap<>();

    private PlacedRawStoneEvents() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(PlacedRawStoneEvents::onPlaced);
        NeoForge.EVENT_BUS.addListener(
                EventPriority.LOWEST,
                PlacedRawStoneEvents::onDropsResolved
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
                EventPriority.LOWEST,
                PlacedRawStoneEvents::onPistonPre
        );
        NeoForge.EVENT_BUS.addListener(PlacedRawStoneEvents::onPistonPost);
        NeoForge.EVENT_BUS.addListener(
                PlacedRawStoneEvents::onLevelTickPost
        );
    }

    private static void onPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        if (event instanceof BlockEvent.EntityMultiPlaceEvent multiPlace) {
            for (var snapshot : multiPlace.getReplacedBlockSnapshots()) {
                updatePlacement(
                        level,
                        snapshot.getPos(),
                        event.getEntity() instanceof Player
                );
            }
            return;
        }
        updatePlacement(
                level,
                event.getPos(),
                event.getEntity() instanceof Player
        );
    }

    private static void onDropsResolved(BlockDropsEvent event) {
        PlacedRawStoneTracker.clear(event.getLevel(), event.getPos());
    }

    private static void onFluidPlacedBlock(
            BlockEvent.FluidPlaceBlockEvent event
    ) {
        if (event.getLevel() instanceof ServerLevel level) {
            PlacedRawStoneTracker.clear(level, event.getPos());
        }
    }

    private static void onToolModification(
            BlockEvent.BlockToolModificationEvent event
    ) {
        if (!event.isSimulated()
                && event.getLevel() instanceof ServerLevel level) {
            PlacedRawStoneTracker.clear(level, event.getPos());
        }
    }

    private static void onExplosion(ExplosionEvent.Detonate event) {
        if (event.getLevel() instanceof ServerLevel level) {
            for (BlockPos pos : event.getAffectedBlocks()) {
                PlacedRawStoneTracker.clear(level, pos);
            }
        }
    }

    private static void onLivingDestroyedBlock(
            LivingDestroyBlockEvent event
    ) {
        if (event.getEntity().level() instanceof ServerLevel level) {
            PlacedRawStoneTracker.clear(level, event.getPos());
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
            PISTON_MOVES.keySet().removeIf(key -> key.level() == level);
        }
    }

    private static void updatePlacement(
            ServerLevel level,
            BlockPos pos,
            boolean playerPlacement
    ) {
        // World generation, commands, structures, and direct mod setBlock calls
        // do not emit a player EntityPlaceEvent and therefore remain natural.
        // Gameplay replacements still clear an older marker at this position.
        if (playerPlacement
                && StoneFamilyCatalog.get()
                        .byRaw(level.getBlockState(pos))
                        .isPresent()) {
            PlacedRawStoneTracker.mark(level, pos);
        } else {
            PlacedRawStoneTracker.clear(level, pos);
        }
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
}
