package dev.fishraposo.materialprogression.world.level.block;

import com.mojang.serialization.MapCodec;
import dev.fishraposo.materialprogression.registry.ModBlockEntities;
import dev.fishraposo.materialprogression.stone.StoneFamilyResolver;
import dev.fishraposo.materialprogression.world.level.block.entity.ExternalLooseRockBlockEntity;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

/**
 * Dynamic counterpart to the compact built-in Loose Rocks block. The family
 * ID and exact Rock output live in a synchronized block entity because
 * datapacks cannot extend a block-state enum.
 */
public final class ExternalLooseRocksBlock extends BaseEntityBlock {
    public static final MapCodec<ExternalLooseRocksBlock> CODEC =
            simpleCodec(ExternalLooseRocksBlock::new);
    private static final VoxelShape SHAPE =
            Block.box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0);

    public ExternalLooseRocksBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<ExternalLooseRocksBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ExternalLooseRockBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> actualType
    ) {
        return level.isClientSide()
                ? null
                : createTickerHelper(
                        actualType,
                        ModBlockEntities.EXTERNAL_LOOSE_ROCKS.get(),
                        ExternalLooseRockBlockEntity::serverTick
                );
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
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
        if (!(level.getBlockEntity(pos)
                instanceof ExternalLooseRockBlockEntity rocks)) {
            return false;
        }
        if (rocks.familyId().isEmpty()) {
            // setBlock creates the block entity immediately before the feature
            // assigns its external identity.
            return true;
        }
        return StoneFamilyResolver.resolveSupport(level, supportPos)
                .map(entry -> entry.id().equals(
                        rocks.familyId().orElseThrow()
                ))
                .orElse(false);
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess ticks,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        if (direction == Direction.DOWN && !state.canSurvive(level, pos)) {
            ticks.scheduleTick(pos, this, 1);
        }
        return super.updateShape(
                state,
                level,
                ticks,
                pos,
                direction,
                neighborPos,
                neighborState,
                random
        );
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
    protected void onExplosionHit(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            Explosion explosion,
            BiConsumer<ItemStack, BlockPos> onHit
    ) {
        if (explosion.getBlockInteraction()
                != Explosion.BlockInteraction.TRIGGER_BLOCK
                && state.canDropFromExplosion(level, pos, explosion)
                && level.getBlockEntity(pos)
                instanceof ExternalLooseRockBlockEntity rocks
                && !rocks.rock().isEmpty()) {
            onHit.accept(rocks.rock(), pos);
        }
        // The block's empty loot table avoids a duplicate while vanilla still
        // performs destruction callbacks and neighbor updates.
        super.onExplosionHit(state, level, pos, explosion, onHit);
    }
}
