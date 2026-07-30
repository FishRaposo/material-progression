package dev.fishraposo.materialprogression.mixin;

import dev.fishraposo.materialprogression.stone.PlacedRawStoneEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
abstract class PlacedRawStoneLevelChunkMixin {
    @Shadow
    @Final
    private Level level;

    @Inject(method = "setBlockState", at = @At("RETURN"))
    private void observeSuccessfulStateWrite(
            BlockPos pos,
            BlockState newState,
            int flags,
            CallbackInfoReturnable<BlockState> callback
    ) {
        if (callback.getReturnValue() != null
                && level instanceof ServerLevel serverLevel) {
            PlacedRawStoneEvents.observeSuccessfulStateWrite(
                    serverLevel,
                    pos
            );
        }
    }
}
