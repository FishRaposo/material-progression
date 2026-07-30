package dev.fishraposo.materialprogression.stone;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum StoneResistance implements StringRepresentable {
    SOFT("soft", -1),
    STANDARD("standard", 0),
    HARD("hard", 1);

    public static final Codec<StoneResistance> CODEC =
            StringRepresentable.fromEnum(StoneResistance::values);

    private final String name;
    private final int modifier;

    StoneResistance(String name, int modifier) {
        this.name = name;
        this.modifier = modifier;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public int modifier() {
        return modifier;
    }
}
