package net.ascend.mod.entity.mob.base;

import net.minecraft.util.Formatting;

public enum MobRank {
    E("E", Formatting.GRAY, 1.0f, 1.0f, 5),
    D("D", Formatting.GREEN, 1.4f, 1.25f, 15),
    C("C", Formatting.BLUE, 2.0f, 1.6f, 40),
    B("B", Formatting.DARK_PURPLE, 3.0f, 2.2f, 100),
    A("A", Formatting.GOLD, 4.5f, 3.0f, 250),
    S("S", Formatting.RED, 6.5f, 4.2f, 600);

    private final String label;
    private final Formatting formatting;
    private final float hpMultiplier;
    private final float damageMultiplier;
    private final int essenceDrop;

    MobRank(String label, Formatting formatting, float hpMultiplier, float damageMultiplier, int essenceDrop) {
        this.label = label;
        this.formatting = formatting;
        this.hpMultiplier = hpMultiplier;
        this.damageMultiplier = damageMultiplier;
        this.essenceDrop = essenceDrop;
    }

    public String getLabel() { return label; }
    public Formatting getFormatting() { return formatting; }
    public float getHpMultiplier() { return hpMultiplier; }
    public float getDamageMultiplier() { return damageMultiplier; }
    public int getEssenceDrop() { return essenceDrop; }

    public static MobRank fromDistance(double distance) {
        if (distance < 300) return E;
        if (distance < 800) return D;
        if (distance < 1500) return C;
        if (distance < 2500) return B;
        if (distance < 4000) return A;
        return S;
    }

    public static MobRank fromString(String str) {
        if (str == null) return E;
        try {
            return MobRank.valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            return E;
        }
    }
}

