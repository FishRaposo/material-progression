package dev.fishraposo.materialprogression.world.level.block;

import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.stone.StoneFamilyResolver;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.entity.living.LivingDestroyBlockEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.PistonEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

public final class LooseRockInvalidationEvents {
    private static final int REVALIDATION_DELAY_TICKS = 1;
    private static final int MAX_ROCK_OFFSET =
            StoneFamilyResolver.COVER_SCAN_DEPTH + 1;

    private LooseRockInvalidationEvents() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(
                LooseRockInvalidationEvents::onBlockBroken
        );
        NeoForge.EVENT_BUS.addListener(
                LooseRockInvalidationEvents::onBlockPlaced
        );
        NeoForge.EVENT_BUS.addListener(
                LooseRockInvalidationEvents::onFluidPlacedBlock
        );
        NeoForge.EVENT_BUS.addListener(
                LooseRockInvalidationEvents::onToolModification
        );
        NeoForge.EVENT_BUS.addListener(
                LooseRockInvalidationEvents::onExplosion
        );
        NeoForge.EVENT_BUS.addListener(
                LooseRockInvalidationEvents::onLivingDestroyedBlock
        );
        NeoForge.EVENT_BUS.addListener(
                LooseRockInvalidationEvents::onPistonMove
        );
    }

    private static void onBlockBroken(BreakBlockEvent event) {
        invalidateAbove(event.getLevel(), event.getPos());
    }

    private static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (event instanceof BlockEvent.EntityMultiPlaceEvent multiPlace) {
            for (BlockSnapshot replaced
                    : multiPlace.getReplacedBlockSnapshots()) {
                invalidateAbove(event.getLevel(), replaced.getPos());
            }
            return;
        }
        invalidateAbove(event.getLevel(), event.getPos());
    }

    private static void onFluidPlacedBlock(
            BlockEvent.FluidPlaceBlockEvent event
    ) {
        invalidateAbove(event.getLevel(), event.getPos());
    }

    private static void onToolModification(
            BlockEvent.BlockToolModificationEvent event
    ) {
        if (!event.isSimulated()) {
            invalidateAbove(event.getLevel(), event.getPos());
        }
    }

    private static void onExplosion(ExplosionEvent.Detonate event) {
        for (BlockPos affected : event.getAffectedBlocks()) {
            invalidateAbove(event.getLevel(), affected);
        }
    }

    private static void onLivingDestroyedBlock(
            LivingDestroyBlockEvent event
    ) {
        invalidateAbove(event.getEntity().level(), event.getPos());
    }

    private static void onPistonMove(PistonEvent.Pre event) {
        invalidateAbove(event.getLevel(), event.getPos());
        invalidateAbove(event.getLevel(), event.getFaceOffsetPos());
        if (event.getPistonMoveType() == PistonEvent.PistonMoveType.RETRACT) {
            invalidateAbove(
                    event.getLevel(),
                    event.getFaceOffsetPos().relative(event.getDirection())
            );
        }

        var resolver = event.getStructureHelper();
        if (resolver == null || !resolver.resolve()) {
            return;
        }
        for (BlockPos source : resolver.getToPush()) {
            invalidateAbove(event.getLevel(), source);
            invalidateAbove(
                    event.getLevel(),
                    source.relative(resolver.getPushDirection())
            );
        }
        for (BlockPos destroyed : resolver.getToDestroy()) {
            invalidateAbove(event.getLevel(), destroyed);
        }
    }

    private static void invalidateAbove(
            LevelAccessor levelAccessor,
            BlockPos changedPos
    ) {
        if (!(levelAccessor instanceof ServerLevel level)) {
            return;
        }

        for (int offset = 1; offset <= MAX_ROCK_OFFSET; offset++) {
            BlockPos candidate = changedPos.above(offset);
            if (level.getBlockState(candidate).is(ModBlocks.LOOSE_ROCKS.get())) {
                level.scheduleTick(
                        candidate,
                        ModBlocks.LOOSE_ROCKS.get(),
                        REVALIDATION_DELAY_TICKS
                );
            }
        }
    }
}
