package dev.fishraposo.materialprogression.stone;

import dev.fishraposo.materialprogression.registry.ModDataAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public final class PlacedRawStoneTracker {
    private PlacedRawStoneTracker() {
    }

    public static boolean isMarked(ServerLevel level, BlockPos pos) {
        return isMarked(level, pos, level.getBlockState(pos));
    }

    public static boolean isMarked(
            ServerLevel level,
            BlockPos pos,
            BlockState expectedState
    ) {
        LevelChunk chunk = level.getChunkAt(pos);
        PlacedRawStoneMarkers markers = chunk.getExistingDataOrNull(
                ModDataAttachments.PLACED_RAW_STONES
        );
        if (StoneFamilyCatalog.get().byRaw(expectedState).isEmpty()) {
            if (markers != null && markers.contains(pos)) {
                clear(level, pos);
            }
            return false;
        }
        if (PlacedRawStoneEvents.hasPendingPlayerPlacement(
                level,
                pos,
                expectedState
        )) {
            return true;
        }
        return markers != null && markers.contains(pos);
    }

    public static void mark(ServerLevel level, BlockPos pos) {
        if (StoneFamilyCatalog.get().byRaw(level.getBlockState(pos)).isEmpty()) {
            clear(level, pos);
            return;
        }
        markUnchecked(level, pos);
    }

    static void markUnchecked(ServerLevel level, BlockPos pos) {
        LevelChunk chunk = level.getChunkAt(pos);
        if (chunk.getData(ModDataAttachments.PLACED_RAW_STONES).add(pos)) {
            chunk.markUnsaved();
        }
    }

    public static void clear(ServerLevel level, BlockPos pos) {
        LevelChunk chunk = level.getChunkAt(pos);
        PlacedRawStoneMarkers markers = chunk.getExistingDataOrNull(
                ModDataAttachments.PLACED_RAW_STONES
        );
        if (markers == null || !markers.remove(pos)) {
            return;
        }
        if (markers.isEmpty()) {
            chunk.removeData(ModDataAttachments.PLACED_RAW_STONES);
        }
        chunk.markUnsaved();
    }
}
