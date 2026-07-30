package dev.fishraposo.materialprogression.stone;

import dev.fishraposo.materialprogression.registry.ModTags;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public final class StoneFamilyResolver {
    public static final int COVER_SCAN_DEPTH = 8;

    private StoneFamilyResolver() {
    }

    public static Optional<StoneFamilyCatalog.Entry> resolveSupport(
            BlockGetter level,
            BlockPos supportPos
    ) {
        StoneFamilyCatalog catalog = StoneFamilyCatalog.get();
        BlockState support = level.getBlockState(supportPos);
        Optional<StoneFamilyCatalog.Entry> direct =
                catalog.byDirectSupport(support);
        if (direct.isPresent()) {
            return direct;
        }
        if (!support.is(ModTags.LOOSE_ROCK_COVER)) {
            return Optional.empty();
        }
        for (int depth = 1; depth <= COVER_SCAN_DEPTH; depth++) {
            Optional<StoneFamilyCatalog.Entry> family = catalog.bySource(
                    level.getBlockState(supportPos.below(depth))
            );
            if (family.isPresent()) {
                return family;
            }
        }
        return Optional.empty();
    }
}
