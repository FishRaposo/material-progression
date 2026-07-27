package dev.fishraposo.materialprogression.world.level.block;

import com.mojang.serialization.MapCodec;
import dev.fishraposo.materialprogression.registry.ModBlockEntities;
import dev.fishraposo.materialprogression.world.level.block.entity.CrusherBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class CrusherBlock extends AbstractFurnaceBlock {
    public static final MapCodec<CrusherBlock> CODEC = simpleCodec(CrusherBlock::new);

    public CrusherBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<CrusherBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrusherBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> actualType
    ) {
        return createFurnaceTicker(level, actualType, ModBlockEntities.CRUSHER.get());
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CrusherBlockEntity crusher) {
            player.openMenu((MenuProvider) crusher);
        }
    }

    @Override
    public void animateTick(
            BlockState state,
            Level level,
            BlockPos pos,
            RandomSource random
    ) {
        if (!state.getValue(LIT)) {
            return;
        }

        double x = pos.getX() + 0.5;
        double y = pos.getY();
        double z = pos.getZ() + 0.5;
        if (random.nextDouble() < 0.1) {
            level.playLocalSound(
                    x,
                    y,
                    z,
                    SoundEvents.FURNACE_FIRE_CRACKLE,
                    SoundSource.BLOCKS,
                    0.8F,
                    0.8F,
                    false
            );
        }

        Direction direction = state.getValue(FACING);
        Direction.Axis axis = direction.getAxis();
        double sideways = random.nextDouble() * 0.6 - 0.3;
        double dx = axis == Direction.Axis.X ? direction.getStepX() * 0.52 : sideways;
        double dz = axis == Direction.Axis.Z ? direction.getStepZ() * 0.52 : sideways;
        level.addParticle(
                ParticleTypes.SMOKE,
                x + dx,
                y + random.nextDouble() * 0.35,
                z + dz,
                0.0,
                0.0,
                0.0
        );
    }
}
