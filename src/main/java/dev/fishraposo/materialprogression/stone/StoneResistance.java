package dev.fishraposo.materialprogression.stone;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum StoneResistance implements StringRepresentable {
    SOFT("soft"),
    STANDARD("standard"),
    HARD("hard");

    public static final Codec<StoneResistance> CODEC =
            StringRepresentable.fromEnum(StoneResistance::values);

    private final String name;

    StoneResistance(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
