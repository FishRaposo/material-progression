package dev.fishraposo.materialprogression.stone;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.List;
import net.minecraft.core.BlockPos;

/**
 * Chunk-local positions packed into one int each: four bits apiece for local
 * X/Z and a signed 24-bit Y. This keeps sparse player construction data small
 * without allocating a full world-height bitset for every touched chunk.
 */
public final class PlacedRawStoneMarkers {
    public static final MapCodec<PlacedRawStoneMarkers> CODEC =
            Codec.INT.listOf()
                    .fieldOf("positions")
                    .xmap(
                            PlacedRawStoneMarkers::new,
                            PlacedRawStoneMarkers::serializedPositions
                    );
    private static final int Y_MASK = 0x00FF_FFFF;
    private final IntOpenHashSet positions;

    public PlacedRawStoneMarkers() {
        this.positions = new IntOpenHashSet();
    }

    private PlacedRawStoneMarkers(List<Integer> positions) {
        this.positions = new IntOpenHashSet(positions);
    }

    public boolean contains(BlockPos pos) {
        return positions.contains(pack(pos));
    }

    public boolean add(BlockPos pos) {
        return positions.add(pack(pos));
    }

    public boolean remove(BlockPos pos) {
        return positions.remove(pack(pos));
    }

    public boolean isEmpty() {
        return positions.isEmpty();
    }

    private List<Integer> serializedPositions() {
        return positions.intStream().sorted().boxed().toList();
    }

    private static int pack(BlockPos pos) {
        if (pos.getY() < -0x80_0000 || pos.getY() > 0x7F_FFFF) {
            throw new IllegalArgumentException(
                    "Raw-stone marker Y is outside signed 24-bit range: "
                            + pos.getY()
            );
        }
        return (pos.getX() & 15) << 28
                | (pos.getZ() & 15) << 24
                | (pos.getY() & Y_MASK);
    }
}
