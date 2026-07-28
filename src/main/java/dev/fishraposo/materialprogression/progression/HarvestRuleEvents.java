package dev.fishraposo.materialprogression.progression;

import dev.fishraposo.materialprogression.config.MaterialProgressionConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class HarvestRuleEvents {
    private HarvestRuleEvents() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(HarvestRuleEvents::onHarvestCheck);
    }

    private static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        event.setCanHarvest(LogHarvestRule.canHarvest(
                event.canHarvest(),
                MaterialProgressionConfig.requireAxeForLogs(),
                event.getTargetBlock(),
                event.getEntity().getMainHandItem()
        ));
    }
}
