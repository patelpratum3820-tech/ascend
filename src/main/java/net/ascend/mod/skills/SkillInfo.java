package net.ascend.mod.skills;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ascend.mod.AscendMod;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record SkillInfo(
        String id,
        String displayName,
        SkillRank rank,
        String tag,
        String description,
        String costType,
        double costAmount,
        boolean isPercent,
        int cooldownSec,
        String unlockRequirement
) {
    public static final List<SkillInfo> ALL_SKILLS = new ArrayList<>();

    // Static fallback initialization (will be enriched/replaced by JSON loader)
    static {
        loadSkillsFromJson();
    }

    public static synchronized void loadSkillsFromJson() {
        ALL_SKILLS.clear();
        try {
            InputStream is = SkillInfo.class.getResourceAsStream("/data/ascend/skills/skill_ranks.json");
            if (is != null) {
                try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    JsonArray arr = JsonParser.parseReader(reader).getAsJsonArray();
                    for (JsonElement el : arr) {
                        JsonObject obj = el.getAsJsonObject();
                        String id = obj.get("id").getAsString();
                        String name = obj.get("name").getAsString();
                        SkillRank rank = SkillRank.fromString(obj.get("rank").getAsString());
                        String tag = obj.get("tag").getAsString();
                        String effect = obj.get("effect").getAsString();
                        String costType = obj.get("cost_type").getAsString();
                        double costAmount = obj.get("cost_amount").getAsDouble();
                        boolean isPercent = obj.get("is_percent").getAsBoolean();
                        int cooldownSec = obj.get("cooldown_sec").getAsInt();
                        String unlock = obj.get("unlock").getAsString();

                        ALL_SKILLS.add(new SkillInfo(id, name, rank, tag, effect, costType, costAmount, isPercent, cooldownSec, unlock));
                    }
                }
            }
        } catch (Exception e) {
            AscendMod.LOGGER.error("[Ascend] Error loading skill_ranks.json, falling back to defaults", e);
        }

        if (ALL_SKILLS.isEmpty()) {
            // Minimal safety fallback
            ALL_SKILLS.add(new SkillInfo("shadow_step", "Shadow Step", SkillRank.E, "mobility", "Quick dash with i-frames", "STAMINA", 20.0, true, 3, "Starter"));
            ALL_SKILLS.add(new SkillInfo("flame_slash", "Flame Slash", SkillRank.E, "melee", "Fire wave", "MANA", 10.0, false, 5, "Starter"));
            ALL_SKILLS.add(new SkillInfo("mana_bolt", "Mana Bolt", SkillRank.E, "magic", "Magic bolt", "MANA", 8.0, false, 2, "Starter"));
        }

        AscendMod.LOGGER.info("[Ascend] Registered " + ALL_SKILLS.size() + " skills across ranks E to SS.");
    }

    public static SkillInfo getById(String id) {
        if (id == null) return ALL_SKILLS.get(0);
        for (SkillInfo skill : ALL_SKILLS) {
            if (skill.id().equalsIgnoreCase(id)) return skill;
        }
        return ALL_SKILLS.get(0);
    }

    public static List<SkillInfo> getSkillsByRank(SkillRank rank) {
        List<SkillInfo> list = new ArrayList<>();
        for (SkillInfo s : ALL_SKILLS) {
            if (s.rank() == rank) list.add(s);
        }
        return list;
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
            case "craftsman" -> List.of("craft", "defense", "summon");
            default -> List.of("melee", "mobility", "defense", "summon", "magic", "ranged", "support", "craft", "transform");
        };
    }
}
