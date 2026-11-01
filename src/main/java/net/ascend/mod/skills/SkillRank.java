package net.ascend.mod.skills;

import net.minecraft.text.TextColor;

public enum SkillRank {
    E("E", "§7", 0xAAAAAA),
    D("D", "§a", 0x55FF55),
    C("C", "§b", 0x55FFFF),
    B("B", "§5", 0xAA00AA),
    A("A", "§6", 0xFFAA00),
    S("S", "§c", 0xFF5555),
    SS("SS", "§e§l", 0xFFFF55);

    private final String label;
    private final String colorCode;
    private final int hexColor;

    SkillRank(String label, String colorCode, int hexColor) {
        this.label = label;
        this.colorCode = colorCode;
        this.hexColor = hexColor;
    }

    public String getLabel() { return label; }
    public String getColorCode() { return colorCode; }
    public int getHexColor() { return hexColor; }

    public String getFormatted() {
        return colorCode + label;
    }

    public static SkillRank fromString(String str) {
        if (str == null) return E;
        try {
            return SkillRank.valueOf(str.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return E;
        }
    }
}

