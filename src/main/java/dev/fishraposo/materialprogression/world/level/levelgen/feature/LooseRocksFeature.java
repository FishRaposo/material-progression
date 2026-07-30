package dev.fishraposo.materialprogression.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.stone.StoneFamily;
import dev.fishraposo.materialprogression.stone.StoneFamilyCatalog;
import dev.fishraposo.materialprogression.stone.StoneFamilyResolver;
import dev.fishraposo.materialprogression.world.level.block.LooseRocksBlock;
import dev.fishraposo.materialprogression.world.level.block.entity.ExternalLooseRockBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public final class LooseRocksFeature extends Feature<NoneFeatureConfiguration> {
    public LooseRocksFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos position = context.origin();
        BlockState replaced = context.level().getBlockState(position);
        if (!replaced.canBeReplaced() || !context.level().getFluidState(position).isEmpty()) {
            return false;
        }

        BlockPos supportPos = position.below();
        BlockState support = context.level().getBlockState(supportPos);
        if (!support.isFaceSturdy(context.level(), supportPos, Direction.UP)) {
            return false;
        }

        return StoneFamilyResolver.resolveSupport(context.level(), supportPos)
                .map(entry -> placeResolved(context, position, entry))
                .orElse(false);
    }

    private static boolean placeResolved(
            FeaturePlaceContext<NoneFeatureConfiguration> context,
            BlockPos position,
            StoneFamilyCatalog.Entry entry
    ) {
        if (entry.builtInFamily().isPresent()) {
            return context.level().setBlock(
                    position,
                    ModBlocks.LOOSE_ROCKS.get()
                            .defaultBlockState()
                            .setValue(
                                    LooseRocksBlock.FAMILY,
                                    entry.builtInFamily().orElseThrow()
                            ),
                    2
            );
        }
        if (!context.level().setBlock(
                position,
                ModBlocks.EXTERNAL_LOOSE_ROCKS.get().defaultBlockState(),
                2
        )) {
            return false;
        }
        if (context.level().getBlockEntity(position)
                instanceof ExternalLooseRockBlockEntity rocks) {
            rocks.initialize(entry);
            return true;
        }
        context.level().setBlock(
                position,
                net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),
                2
        );
        return false;
    }
}
