package net.ascend.mod.data;

import java.util.List;

public class RaceEvolution {
    public static final String HUMAN = "Human";
    public static final String ELF = "Elf";
    public static final String BEASTKIN = "Beastkin";
    public static final String DEMONKIN = "Demonkin";
    public static final String DRAGONOID = "Dragonoid";
    public static final String SLIME = "Slime";

    public static final List<String> BASE_RACES = List.of(HUMAN, ELF, BEASTKIN, DEMONKIN, DRAGONOID, SLIME);

    public record EvolutionTier(String name, int requiredLevel, String description, int bonusStats) {}

    public static List<EvolutionTier> getEvolutionPath(String baseRace) {
        return switch (baseRace.toLowerCase()) {
            case "human" -> List.of(
                    new EvolutionTier("Awakened Human", 25, "+5 All Attributes, +20% Essence bonus", 5),
                    new EvolutionTier("Heroic Human", 50, "+10 All Attributes, Enhanced Combat Mastery", 10),
                    new EvolutionTier("Saint", 75, "+20 All Attributes, Divine Aura", 20)
            );
            case "elf" -> List.of(
                    new EvolutionTier("High Elf", 25, "+60 Max Mana, 2.0x Mana Regen", 5),
                    new EvolutionTier("Ancient Elf", 50, "+120 Max Mana, Arcane Affinity", 10),
                    new EvolutionTier("Elder Elf", 75, "+200 Max Mana, Nature Sanctum", 20)
            );
            case "beastkin" -> List.of(
                    new EvolutionTier("Advanced Beastkin", 25, "+30 Max Stamina, +15% Dash Speed", 5),
                    new EvolutionTier("Feral Monarch", 50, "+60 Max Stamina, Primal Roar", 10),
                    new EvolutionTier("Ancient Beastkin", 75, "+100 Max Stamina, Apex Predator", 20)
            );
            case "dragonoid" -> List.of(
                    new EvolutionTier("Elder Dragonkin", 25, "+15 Attack Power, Dragon Scales", 5),
                    new EvolutionTier("Infernal Drake", 50, "+30 Attack Power, Magma Breath", 10),
                    new EvolutionTier("Dragon King", 75, "+50 Attack Power, Sovereign Dragon Form", 20)
            );
            case "slime" -> List.of(
                    new EvolutionTier("Advanced Slime", 25, "90% Fall Damage Reduction, 2x Passive Regen", 5),
                    new EvolutionTier("Crystal Slime", 50, "Total Fall Immunity, Elastic Impact", 10),
                    new EvolutionTier("Greater Crystal Slime", 75, "Regenerative Core, Amorphous Form", 20)
            );
            case "demonkin" -> List.of(
                    new EvolutionTier("High Demon", 25, "10% Lifesteal, Abyssal Fire", 5),
                    new EvolutionTier("Archdemon", 50, "15% Lifesteal, Dark Dominion", 10),
                    new EvolutionTier("Demon King", 75, "25% Lifesteal, Monarch of Chaos", 20)
            );
            default -> List.of();
        };
    }

    public static String getRacePassiveDescription(String race) {
        return switch (race.toLowerCase()) {
            case "human" -> "Versatile Heritage: +15% Essence gain from all monsters and dungeon rewards.";
            case "elf" -> "Arcane Conduit: +40 Max Mana, 1.5x passive mana regeneration, +10% speed, -2 max health.";
            case "beastkin" -> "Primal Vigor: Permanent Night Vision, +3 Strength, +20 Stamina, +10% dash speed, -15 mana.";
            case "demonkin" -> "Abyssal Flame: 100% Fire & Lava immunity, 5% melee lifesteal, +4 Strength, +20 Mana, 50% holy weakness.";
            case "dragonoid" -> "Draconic Bloodline: 100% Fire & Lava immunity, +3 Strength, -5% speed, 50% cold/water vulnerability.";
            case "slime" -> "Amorphous Resilience: 75% fall damage reduction, passive health regeneration, -4 health, -2 defense.";
            default -> "Standard fantasy mortal heritage.";
        };
    }
}
