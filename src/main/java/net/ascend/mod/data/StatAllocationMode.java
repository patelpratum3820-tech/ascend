package net.ascend.mod.data;

public enum StatAllocationMode {
    MANUAL("Manual", "Allocate points manually via the status menu."),
    BALANCED("Balanced", "Evenly spreads points across all attributes."),
    STRENGTH_FOCUSED("Strength-Focused", "Prioritizes Strength and Vitality for heavy combat."),
    INTELLIGENCE_FOCUSED("Intelligence-Focused", "Prioritizes Mana and Intelligence for sorcery."),
    AGILITY_FOCUSED("Agility-Focused", "Prioritizes Agility and Speed for evasion and rapid strikes.");

    private final String displayName;
    private final String description;

    StatAllocationMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static StatAllocationMode fromString(String name) {
        if (name == null) return MANUAL;
        for (StatAllocationMode mode : values()) {
            if (mode.name().equalsIgnoreCase(name) || mode.displayName.equalsIgnoreCase(name)) {
                return mode;
            }
        }
        return MANUAL;
    }
}
