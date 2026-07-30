package dev.fishraposo.materialprogression.stone;

import dev.fishraposo.materialprogression.MaterialProgression;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;

public enum StoneFamily implements StringRepresentable {
    STONE("stone"),
    GRANITE("granite"),
    DIORITE("diorite"),
    ANDESITE("andesite"),
    DEEPSLATE("deepslate"),
    TUFF("tuff"),
    CALCITE("calcite"),
    DRIPSTONE("dripstone"),
    SULFUR("sulfur"),
    CINNABAR("cinnabar"),
    SANDSTONE("sandstone"),
    RED_SANDSTONE("red_sandstone"),
    NETHERRACK("netherrack"),
    BASALT("basalt"),
    BLACKSTONE("blackstone"),
    END_STONE("end_stone");

    private final String name;
    private final Identifier id;

    StoneFamily(String name) {
        this.name = name;
        this.id = Identifier.fromNamespaceAndPath(MaterialProgression.MOD_ID, name);
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public Identifier id() {
        return id;
    }

    public static Optional<StoneFamily> byId(Identifier id) {
        return Arrays.stream(values()).filter(family -> family.id.equals(id)).findFirst();
    }
}
