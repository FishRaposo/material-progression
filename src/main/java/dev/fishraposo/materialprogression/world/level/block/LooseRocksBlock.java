package dev.fishraposo.materialprogression.world.level.block;

import com.mojang.serialization.MapCodec;
import dev.fishraposo.materialprogression.stone.StoneFamily;
import dev.fishraposo.materialprogression.stone.StoneFamilyResolver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class LooseRocksBlock extends BushBlock {
    public static final MapCodec<BushBlock> CODEC =
            simpleCodec(LooseRocksBlock::new);
    public static final EnumProperty<StoneFamily> FAMILY =
            EnumProperty.create("family", StoneFamily.class);
    private static final VoxelShape SHAPE =
            box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0);

    public LooseRocksBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FAMILY, StoneFamily.STONE));
    }

    @Override
    public MapCodec<BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    protected boolean canSurvive(
            BlockState state,
            LevelReader level,
            BlockPos pos
    ) {
        BlockPos supportPos = pos.below();
        BlockState support = level.getBlockState(supportPos);
        if (!support.isFaceSturdy(level, supportPos, Direction.UP)) {
            return false;
        }
        if (level.isClientSide()) {
            return true;
        }
        return StoneFamilyResolver.resolveSupport(level, supportPos)
                .flatMap(entry -> entry.builtInFamily())
                .map(family -> family == state.getValue(FAMILY))
                .orElse(false);
    }

    @Override
    protected void tick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder
    ) {
        builder.add(FAMILY);
    }
}
