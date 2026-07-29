package dev.fishraposo.materialprogression.progression;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class HarvestContext {
    private final BlockState state;
    private final ItemStack tool;
    private final @Nullable ServerLevel level;
    private final @Nullable BlockPos position;
    private final @Nullable Entity breaker;
    private final @Nullable List<ItemEntity> drops;
    private boolean canHarvest;

    private HarvestContext(
            boolean canHarvest,
            BlockState state,
            ItemStack tool,
            @Nullable ServerLevel level,
            @Nullable BlockPos position,
            @Nullable Entity breaker,
            @Nullable List<ItemEntity> drops
    ) {
        this.canHarvest = canHarvest;
        this.state = state;
        this.tool = tool;
        this.level = level;
        this.position = position;
        this.breaker = breaker;
        this.drops = drops;
    }

    public static HarvestContext permissionCheck(
            boolean canHarvest,
            BlockState state,
            ItemStack tool
    ) {
        return new HarvestContext(
                canHarvest,
                state,
                tool,
                null,
                null,
                null,
                null
        );
    }

    public static HarvestContext drops(
            BlockState state,
            ItemStack tool,
            ServerLevel level,
            BlockPos position,
            @Nullable Entity breaker,
            List<ItemEntity> drops
    ) {
        return new HarvestContext(
                true,
                state,
                tool,
                level,
                position,
                breaker,
                drops
        );
    }

    public BlockState state() {
        return state;
    }

    public ItemStack tool() {
        return tool;
    }

    public @Nullable Entity breaker() {
        return breaker;
    }

    public boolean canHarvest() {
        return canHarvest;
    }

    public boolean hasDrops() {
        return drops != null;
    }

    public void denyHarvest() {
        canHarvest = false;
    }

    public void damageMainHandTool(int amount) {
        if (level == null) {
            throw new IllegalStateException(
                    "Permission checks cannot damage harvest tools"
            );
        }
        if (!(breaker instanceof LivingEntity livingBreaker)) {
            return;
        }
        livingBreaker.getMainHandItem().hurtAndBreak(
                amount,
                level,
                livingBreaker,
                brokenItem -> livingBreaker.onEquippedItemBroken(
                        brokenItem,
                        EquipmentSlot.MAINHAND
                )
        );
    }

    public void replaceDrops(ItemStack replacement) {
        if (drops == null || level == null || position == null) {
            throw new IllegalStateException(
                    "Permission checks cannot replace block drops"
            );
        }
        drops.clear();
        drops.add(new ItemEntity(
                level,
                position.getX() + 0.5,
                position.getY() + 0.5,
                position.getZ() + 0.5,
                replacement
        ));
    }
}
