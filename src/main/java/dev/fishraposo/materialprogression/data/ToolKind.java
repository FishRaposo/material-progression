package dev.fishraposo.materialprogression.data;

import java.util.List;

public enum ToolKind {
    SWORD("sword", "sword", "Sword", "Espada", List.of("swords")),
    PICKAXE("pickaxe", "pickaxe", "Pickaxe", "Picareta", List.of("pickaxes")),
    AXE("axe", "axe", "Axe", "Machado", List.of("axes")),
    SHOVEL("shovel", "shovel", "Shovel", "Pá", List.of("shovels")),
    HOE("hoe", "hoe", "Hoe", "Enxada", List.of("hoes")),
    HATCHET("hatchet", "axe", "Hatchet", "Machadinha", List.of("axes"));

    private final String itemSuffix;
    private final String vanillaModelSuffix;
    private final String englishName;
    private final String portugueseName;
    private final List<String> itemTags;

    ToolKind(
            String itemSuffix,
            String vanillaModelSuffix,
            String englishName,
            String portugueseName,
            List<String> itemTags
    ) {
        this.itemSuffix = itemSuffix;
        this.vanillaModelSuffix = vanillaModelSuffix;
        this.englishName = englishName;
        this.portugueseName = portugueseName;
        this.itemTags = itemTags;
    }

    public String itemSuffix() {
        return itemSuffix;
    }

    public String vanillaModelSuffix() {
        return vanillaModelSuffix;
    }

    public String englishName() {
        return englishName;
    }

    public String portugueseName() {
        return portugueseName;
    }

    public List<String> itemTags() {
        return itemTags;
    }
}
