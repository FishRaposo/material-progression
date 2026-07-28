package dev.fishraposo.materialprogression.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class GroundResourceBlock extends BushBlock {
    public static final MapCodec<GroundResourceBlock> CODEC =
            simpleCodec(GroundResourceBlock::new);
    private static final VoxelShape SHAPE = box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0);

    public GroundResourceBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<GroundResourceBlock> codec() {
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
    protected boolean mayPlaceOn(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return state.isFaceSturdy(level, pos, Direction.UP);
    }
}
