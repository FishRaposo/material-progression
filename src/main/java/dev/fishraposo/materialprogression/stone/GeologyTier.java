package dev.fishraposo.materialprogression.stone;

public enum GeologyTier {
    LEVEL_0(0, 1.0F),
    LEVEL_1(1, 2.5F),
    LEVEL_2(2, 4.0F),
    LEVEL_3(3, 6.0F);

    private final int level;
    private final float speedDivisor;

    GeologyTier(int level, float speedDivisor) {
        this.level = level;
        this.speedDivisor = speedDivisor;
    }

    public int level() {
        return level;
    }

    public float speedDivisor() {
        return speedDivisor;
    }

    public static GeologyTier clamped(int level) {
        return values()[Math.max(0, Math.min(3, level))];
    }
}
