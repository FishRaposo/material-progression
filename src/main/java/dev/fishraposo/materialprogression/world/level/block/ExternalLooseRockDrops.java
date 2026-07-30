package dev.fishraposo.materialprogression.world.level.block;

import dev.fishraposo.materialprogression.registry.ModBlocks;
import dev.fishraposo.materialprogression.world.level.block.entity.ExternalLooseRockBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

public final class ExternalLooseRockDrops {
    private ExternalLooseRockDrops() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ExternalLooseRockDrops::onBlockDrops);
    }

    private static void onBlockDrops(BlockDropsEvent event) {
        if (!event.getState().is(ModBlocks.EXTERNAL_LOOSE_ROCKS.get())) {
            return;
        }
        event.getDrops().clear();
        if (event.getBreaker() instanceof Player player
                && player.getAbilities().instabuild) {
            return;
        }
        if (!(event.getBlockEntity()
                instanceof ExternalLooseRockBlockEntity rocks)
                || rocks.rock().isEmpty()) {
            return;
        }
        event.getDrops().add(new ItemEntity(
                event.getLevel(),
                event.getPos().getX() + 0.5,
                event.getPos().getY() + 0.5,
                event.getPos().getZ() + 0.5,
                rocks.rock()
        ));
    }
}
