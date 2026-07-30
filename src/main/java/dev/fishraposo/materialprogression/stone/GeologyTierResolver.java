package dev.fishraposo.materialprogression.stone;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class GeologyTierResolver {
    private GeologyTierResolver() {
    }

    public static Optional<GeologyTier> resolve(
            ServerLevel level,
            BlockPos pos,
            BlockState state
    ) {
        Optional<StoneFamilyCatalog.Entry> raw =
                StoneFamilyCatalog.get().bySource(state);
        if (raw.isEmpty()) {
            return Optional.empty();
        }
        if (PlacedRawStoneTracker.isMarked(level, pos, state)) {
            return Optional.of(GeologyTier.LEVEL_0);
        }
        StoneFamilyCatalog.Entry entry = raw.orElseThrow();
        return Optional.of(naturalTier(
                level.dimension(),
                entry.id(),
                entry.resistance().modifier(),
                pos.getY(),
                isExposed(level, pos)
        ));
    }

    public static GeologyTier naturalTier(
            ResourceKey<Level> dimension,
            StoneFamily family,
            StoneResistance resistance,
            int y,
            boolean exposed
    ) {
        return naturalTier(
                dimension,
                family.id(),
                resistance.modifier(),
                y,
                exposed
        );
    }

    public static GeologyTier naturalTier(
            ResourceKey<Level> dimension,
            net.minecraft.resources.Identifier family,
            StoneResistance resistance,
            int y,
            boolean exposed
    ) {
        return naturalTier(
                dimension,
                family,
                resistance.modifier(),
                y,
                exposed
        );
    }

    public static GeologyTier naturalTier(
            ResourceKey<Level> dimension,
            net.minecraft.resources.Identifier family,
            int familyModifier,
            int y,
            boolean exposed
    ) {
        int base = baseLevel(dimension, family, y);
        return GeologyTier.clamped(
                base + familyModifier - (exposed ? 1 : 0)
        );
    }

    private static int baseLevel(
            ResourceKey<Level> dimension,
            net.minecraft.resources.Identifier family,
            int y
    ) {
        if (dimension == Level.OVERWORLD) {
            if (y > 48) {
                return 0;
            }
            if (y >= 17) {
                return 1;
            }
            if (y >= -15) {
                return 2;
            }
            return 3;
        }
        if (dimension == Level.NETHER) {
            if (y >= 96) {
                return 0;
            }
            if (y >= 64) {
                return 1;
            }
            if (y >= 32) {
                return 2;
            }
            return 3;
        }
        if (dimension == Level.END) {
            return 2;
        }
        return 0;
    }

    private static boolean isExposed(ServerLevel level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            if (!level.getBlockState(neighborPos).isFaceSturdy(
                    level,
                    neighborPos,
                    direction.getOpposite()
            )) {
                return true;
            }
        }
        return false;
    }
}
