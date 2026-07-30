package dev.fishraposo.materialprogression.progression;

import dev.fishraposo.materialprogression.registry.ModItems;
import dev.fishraposo.materialprogression.registry.ModTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

public final class PlantFiberHarvestEvents {
    private PlantFiberHarvestEvents() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(
                PlantFiberHarvestEvents::onBlockDrops
        );
    }

    private static void onBlockDrops(BlockDropsEvent event) {
        if (!(event.getBreaker() instanceof Player player)
                || player.isCreative()
                || !event.getTool().is(ModTags.KNIVES)) {
            return;
        }

        int count;
        if (event.getState().is(Blocks.SHORT_GRASS)) {
            count = 1;
        } else if (event.getState().is(Blocks.TALL_GRASS)) {
            count = 2;
        } else {
            return;
        }

        event.getDrops().add(new ItemEntity(
                event.getLevel(),
                event.getPos().getX() + 0.5,
                event.getPos().getY() + 0.5,
                event.getPos().getZ() + 0.5,
                new ItemStack(ModItems.PLANT_FIBER.get(), count)
        ));
    }
}
