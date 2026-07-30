package dev.fishraposo.materialprogression.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import dev.fishraposo.materialprogression.registry.ModTags;
import dev.fishraposo.materialprogression.world.level.levelgen.feature.configurations.GroundStickConfiguration;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public final class GroundStickFeature
        extends Feature<GroundStickConfiguration> {
    public GroundStickFeature(Codec<GroundStickConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<GroundStickConfiguration> context) {
        GroundStickConfiguration configuration = context.config();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        Set<Long> visited = new HashSet<>(configuration.attempts());
        boolean placedAny = false;

        for (int attempt = 0; attempt < configuration.attempts(); attempt++) {
            int x = origin.getX() + offset(
                    random,
                    configuration.horizontalSpread()
            );
            int z = origin.getZ() + offset(
                    random,
                    configuration.horizontalSpread()
            );
            BlockPos column = new BlockPos(x, origin.getY(), z);
            if (!level.hasChunkAt(column)) {
                continue;
            }

            BlockPos position = findSurfacePosition(
                    level,
                    x,
                    z,
                    origin.getY(),
                    configuration
            );
            if (position == null || !visited.add(position.asLong())) {
                continue;
            }

            boolean nearAnchor = hasAnchor(level, position, configuration);
            float chance = nearAnchor
                    ? configuration.nearChance()
                    : configuration.backgroundChance();
            if (chance <= 0.0F || random.nextFloat() >= chance) {
                continue;
            }

            BlockState state = configuration.toPlace()
                    .getOptionalState(level, random, position);
            if (state == null
                    || !canPlace(level, position, state, configuration)) {
                continue;
            }
            placedAny |= level.setBlock(position, state, 2);
        }

        return placedAny;
    }

    private static int offset(RandomSource random, int spread) {
        return spread == 0 ? 0 : random.nextInt(spread * 2 + 1) - spread;
    }

    private static BlockPos findSurfacePosition(
            WorldGenLevel level,
            int x,
            int z,
            int originY,
            GroundStickConfiguration configuration
    ) {
        int verticalRange = configuration.surfaceVerticalRange();
        for (int y = originY + verticalRange;
                y >= originY - verticalRange;
                y--) {
            BlockPos position = new BlockPos(x, y, z);
            BlockPos supportPosition = position.below();
            if (!level.hasChunkAt(position)
                    || !level.hasChunkAt(supportPosition)) {
                continue;
            }
            BlockState replaced = level.getBlockState(position);
            BlockState support = level.getBlockState(supportPosition);
            if (replaced.canBeReplaced()
                    && !replaced.is(configuration.anchorTag())
                    && !replaced.is(ModTags.GROUND_RESOURCES)
                    && level.getFluidState(position).isEmpty()
                    && !support.is(configuration.anchorTag())
                    && support.isFaceSturdy(
                            level,
                            supportPosition,
                            Direction.UP
                    )) {
                return position;
            }
        }
        return null;
    }

    private static boolean canPlace(
            WorldGenLevel level,
            BlockPos position,
            BlockState state,
            GroundStickConfiguration configuration
    ) {
        BlockPos supportPosition = position.below();
        if (!level.hasChunkAt(position)
                || !level.hasChunkAt(supportPosition)) {
            return false;
        }
        BlockState replaced = level.getBlockState(position);
        BlockState support = level.getBlockState(supportPosition);
        return replaced.canBeReplaced()
                && !replaced.is(configuration.anchorTag())
                && !replaced.is(ModTags.GROUND_RESOURCES)
                && level.getFluidState(position).isEmpty()
                && !support.is(configuration.anchorTag())
                && support.isFaceSturdy(
                        level,
                        supportPosition,
                        Direction.UP
                )
                && state.canSurvive(level, position);
    }

    private static boolean hasAnchor(
            WorldGenLevel level,
            BlockPos position,
            GroundStickConfiguration configuration
    ) {
        int horizontal = configuration.anchorHorizontalRadius();
        int vertical = configuration.anchorVerticalRadius();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int y = position.getY() - vertical;
                y <= position.getY() + vertical;
                y++) {
            for (int x = position.getX() - horizontal;
                    x <= position.getX() + horizontal;
                    x++) {
                for (int z = position.getZ() - horizontal;
                        z <= position.getZ() + horizontal;
                        z++) {
                    cursor.set(x, y, z);
                    if (level.hasChunkAt(cursor)
                            && level.getBlockState(cursor)
                                    .is(configuration.anchorTag())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
