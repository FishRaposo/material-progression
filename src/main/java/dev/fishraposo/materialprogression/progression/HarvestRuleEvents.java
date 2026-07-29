package dev.fishraposo.materialprogression.progression;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

public final class HarvestRuleEvents {
    private static final HarvestRuleRegistry RULES =
            HarvestRuleRegistry.defaults();

    private HarvestRuleEvents() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(HarvestRuleEvents::onHarvestCheck);
        NeoForge.EVENT_BUS.addListener(HarvestRuleEvents::onBlockDrops);
    }

    private static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        HarvestContext context = HarvestContext.permissionCheck(
                event.canHarvest(),
                event.getTargetBlock(),
                event.getEntity().getMainHandItem()
        );
        RULES.evaluate(context);
        event.setCanHarvest(context.canHarvest());
    }

    private static void onBlockDrops(BlockDropsEvent event) {
        if (event.isCanceled()) {
            return;
        }
        HarvestContext context = HarvestContext.drops(
                event.getState(),
                event.getTool(),
                event.getLevel(),
                event.getPos(),
                event.getBreaker(),
                event.getDrops()
        );
        RULES.evaluate(context);
    }
}
