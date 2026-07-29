package dev.fishraposo.materialprogression.world.item;

import net.minecraft.world.item.Item;

/** A removable, tiered specialization module for the Bulk Crafting Table. */
public final class BulkCraftingUpgradeItem extends Item {
    private final Family family;
    private final int tier;
    private final int units;

    public BulkCraftingUpgradeItem(
            Family family,
            int tier,
            int units,
            Properties properties
    ) {
        super(properties);
        if (tier <= 0 || units <= 0) {
            throw new IllegalArgumentException(
                    "Bulk-crafting upgrade tiers and units must be positive"
            );
        }
        this.family = family;
        this.tier = tier;
        this.units = units;
    }

    public Family family() {
        return family;
    }

    public int tier() {
        return tier;
    }

    public int units() {
        return units;
    }

    public boolean stackableCapability() {
        return family != Family.PRIORITY;
    }

    public enum Family {
        STORAGE,
        FILTER,
        PRIORITY,
        RESERVATION,
        MEMORY
    }
}
