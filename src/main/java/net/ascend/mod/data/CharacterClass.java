package net.ascend.mod.data;

import java.util.List;

public class CharacterClass {
    public static final String SWORDSMAN = "Swordsman";
    public static final String TANK = "Tank";
    public static final String MAGE = "Mage";
    public static final String ARCHER = "Archer";
    public static final String SUMMONER = "Summoner";

    public static final List<String> BASE_CLASSES = List.of(SWORDSMAN, TANK, MAGE, ARCHER, SUMMONER);

    /**
     * Resolves a unique hybrid title for any two selected classes.
     */
    public static String getCombinedTitle(String primary, String secondary) {
        if (primary == null || primary.isBlank()) primary = SWORDSMAN;
        if (secondary == null || secondary.isBlank()) secondary = primary;

        String p = primary.trim();
        String s = secondary.trim();

        if (p.equalsIgnoreCase(s)) {
            return switch (p.toLowerCase()) {
                case "swordsman" -> "Grand Swordsman";
                case "tank" -> "Vanguard Fortress";
                case "mage" -> "Grand Archmage";
                case "archer" -> "Apex Marksman";
                case "summoner" -> "High Summoner";
                default -> "Master " + p;
            };
        }

        // Canonical hybrid combinations
        if (matches(p, s, SWORDSMAN, MAGE)) return "Magic Swordsman";
        if (matches(p, s, ARCHER, SUMMONER)) return "Beast Commander";
        if (matches(p, s, TANK, MAGE)) return "Magic Guardian";
        if (matches(p, s, ARCHER, MAGE)) return "Spell Archer";
        if (matches(p, s, SWORDSMAN, TANK)) return "Paladin Vanguard";
        if (matches(p, s, SWORDSMAN, ARCHER)) return "Blade Ranger";
        if (matches(p, s, SWORDSMAN, SUMMONER)) return "Shadow Blade";
        if (matches(p, s, TANK, ARCHER)) return "Iron Sentinel";
        if (matches(p, s, TANK, SUMMONER)) return "Golem Master";
        if (matches(p, s, MAGE, SUMMONER)) return "Nether Conjurer";

        return p + " / " + s;
    }

    private static boolean matches(String p, String s, String c1, String c2) {
        return (p.equalsIgnoreCase(c1) && s.equalsIgnoreCase(c2)) || (p.equalsIgnoreCase(c2) && s.equalsIgnoreCase(c1));
    }

    public static String getClassDescription(String className) {
        return switch (className.toLowerCase()) {
            case "swordsman" -> "Stamina-driven rapid melee attacker focusing on burst slashes, mobility, and physical strength.";
            case "tank" -> "Indomitable frontline guardian boasting high defense, shield arts, and damage absorption.";
            case "mage" -> "Devastating ranged spellcaster harnessing primordial elements, mana shields, and cataclysms.";
            case "archer" -> "Agile sharpshooter delivering rapid-fire precision, arrow rain volleys, and mobility arts.";
            case "summoner" -> "Commander of spirit familiars, summoning shadow soldiers, spirit wolves, and golems.";
            default -> "Adventurer combat discipline.";
        };
    }
}
