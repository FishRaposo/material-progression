package dev.fishraposo.materialprogression.api;

import java.util.List;

/**
 * Descriptor for a dependent mod's material integration.
 *
 * <p>Material Progression deliberately does not own third-party registries:
 * extensions publish their own forms into the shared {@code c:} tags and may
 * use this record to keep a documented, stable mapping to their public IDs.</p>
 */
public record MaterialExtension(
        String id,
        List<String> itemForms,
        List<String> blockForms,
        int harvestLevel
) {
    public MaterialExtension {
        itemForms = List.copyOf(itemForms);
        blockForms = List.copyOf(blockForms);
        if (id.isBlank() || harvestLevel < 0) {
            throw new IllegalArgumentException("Material extensions need a non-empty id and non-negative harvest level");
        }
    }
}
