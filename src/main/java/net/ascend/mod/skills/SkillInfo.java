package net.ascend.mod.skills;

import java.util.ArrayList;
import java.util.List;

public record SkillInfo(String id, String displayName, String tag, String description) {
    public static final List<SkillInfo> ALL_SKILLS = new ArrayList<>();

    public static final SkillInfo SHADOW_STEP = register("shadow_step", "Shadow Step", "mobility", "Quick dash with brief invincibility and afterimage.");
    public static final SkillInfo FLAME_SLASH = register("flame_slash", "Flame Slash", "melee", "Unleash a fiery slash wave in front of you.");
    public static final SkillInfo HEALING_AURA = register("healing_aura", "Healing Aura", "support", "Radiate a soothing aura that regenerates health.");
    public static final SkillInfo SUMMON_SHADOW_SOLDIER = register("summon_soldier", "Summon Shadow Soldier", "summon", "Rise! Summons loyal shadow minions to fight beside you.");
    public static final SkillInfo MANA_BOLT = register("mana_bolt", "Mana Bolt", "magic", "Fires a fast concentrated bolt of magical energy.");
    public static final SkillInfo ARROW_RAIN = register("arrow_rain", "Arrow Rain", "ranged", "Call down a devastating rain of arrows upon the target area.");
    public static final SkillInfo IRON_SKIN = register("iron_skin", "Iron Skin", "defense", "Hardens your body, granting massive damage reduction for a short time.");
    public static final SkillInfo LIGHTNING_STRIKE = register("lightning_strike", "Lightning Strike", "magic", "Calls lightning from above that shocks and chains nearby foes.");
    public static final SkillInfo BERSERK = register("berserk", "Berserk", "melee", "Surges with feral rage, boosting strength and speed while lowering defense.");
    public static final SkillInfo SOUL_BOND = register("soul_bond", "Soul Bond", "summon", "Forge an unyielding bond sharing damage and empowering minions.");

    private static SkillInfo register(String id, String displayName, String tag, String description) {
        SkillInfo info = new SkillInfo(id, displayName, tag, description);
        ALL_SKILLS.add(info);
        return info;
    }

    public static SkillInfo getById(String id) {
        for (SkillInfo skill : ALL_SKILLS) {
            if (skill.id().equalsIgnoreCase(id)) return skill;
        }
        return SHADOW_STEP;
    }

    public static List<SkillInfo> getSkillsForClass(String className) {
        List<SkillInfo> available = new ArrayList<>();
        List<String> allowedTags = getAllowedTagsForClass(className);

        for (SkillInfo skill : ALL_SKILLS) {
            if (allowedTags.contains(skill.tag().toLowerCase())) {
                available.add(skill);
            }
        }
        return available;
    }

    public static List<String> getAllowedTagsForClass(String className) {
        return switch (className.toLowerCase()) {
            case "swordsman" -> List.of("melee", "mobility", "defense");
            case "summoner" -> List.of("summon", "support", "magic");
            case "mage" -> List.of("magic", "support", "mobility");
            case "archer" -> List.of("ranged", "mobility", "support");
            case "tank" -> List.of("defense", "melee", "support");
            default -> List.of("melee", "mobility", "defense", "summon", "magic", "ranged", "support");
        };
    }
}

