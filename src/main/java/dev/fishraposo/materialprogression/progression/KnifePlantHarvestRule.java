package dev.fishraposo.materialprogression.progression;

import dev.fishraposo.materialprogression.config.MaterialProgressionConfig;
import dev.fishraposo.materialprogression.registry.ModItems;
import dev.fishraposo.materialprogression.registry.ModTags;
import net.minecraft.world.item.ItemStack;

public final class KnifePlantHarvestRule implements HarvestRule {
    @Override
    public void evaluate(HarvestContext context) {
        if (!context.hasDrops()
                || !MaterialProgressionConfig.knifePlantHarvesting()
                || !context.state().is(ModTags.FIBER_PLANTS)
                || !context.tool().is(ModTags.KNIVES)) {
            return;
        }
        context.replaceDrops(new ItemStack(ModItems.PLANT_FIBER.get()));
        context.damageMainHandTool(1);
    }
}
