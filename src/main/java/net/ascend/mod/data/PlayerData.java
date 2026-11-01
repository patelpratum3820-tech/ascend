package net.ascend.mod.data;

import net.ascend.mod.config.AscendConfig;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlayerData {
    private static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("b33d5483-99d9-48fe-8911-37d450838181");
    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("c44e6594-aaeb-49ef-9022-48e561949292");
    private static final UUID STRENGTH_MODIFIER_UUID = UUID.fromString("d55f76a5-bbfa-4af0-a133-59f672a5a3a3");

    // Identity & Path
    private String characterName = "Chosen Soul";
    private String race = "Human";
    private String primaryClass = "Swordsman";
    private String secondaryClass = "Swordsman";
    private String difficulty = "Normal";
    private StatAllocationMode allocationMode = StatAllocationMode.MANUAL;
    private String currentEvolutionTier = "Base Form";

    // Level & Experience Progression
    private int level = 1;
    private long xp = 0;
    private long essence = 0;
    private int statPoints = 0;

    // Attributes (Standard RPG Schema with Backward Compatible Aliases)
    private int strength = 5;
    private int agility = 5;       // Alias: speed
    private int vitality = 20;
    private int intelligence = 50; // Alias: mana
    private int dexterity = 0;     // Alias: defense

    // Real-time Resources & Combat State
    private double currentMana = 50.0;
    private double currentStamina = 100.0;
    private int dashCooldownTicks = 0;
    private int invulnerableTicks = 0;
    private int skill1CooldownTicks = 0;
    private int skill2CooldownTicks = 0;
    private int skill3CooldownTicks = 0;
    private int skill4CooldownTicks = 0;
    private int signatureCooldownTicks = 0;

    private boolean initialized = false;
    private final List<String> selectedSkills = new ArrayList<>();
    private final Set<String> unlockedSkills = new HashSet<>();
    private int resourceLockedTicks = 0; // Lockout for SS-tier skills (60s)

    // Progression Sub-frameworks
    private final QuestProgress questProgress = new QuestProgress();
    private final DungeonProgress dungeonProgress = new DungeonProgress();
    private final ArtifactProgress artifactProgress = new ArtifactProgress();
    private final StoryProgress storyProgress = new StoryProgress();

    public PlayerData() {
        resetToDefaults();
    }

    public void resetToDefaults() {
        AscendConfig config = AscendConfig.INSTANCE;
        this.characterName = "Chosen Soul";
        this.race = "Human";
        this.primaryClass = "Swordsman";
        this.secondaryClass = "Swordsman";
        this.difficulty = "Normal";
        this.allocationMode = StatAllocationMode.MANUAL;
        this.currentEvolutionTier = "Base Form";
        this.level = 1;
        this.xp = 0;
        this.essence = 0;
        this.statPoints = 0;
        this.strength = config.baseStrength;
        this.agility = config.baseSpeed;
        this.vitality = config.baseVitality;
        this.intelligence = config.baseMana;
        this.dexterity = config.baseDefense;
        this.currentMana = this.intelligence;
        this.currentStamina = config.maxStamina;
        this.dashCooldownTicks = 0;
        this.invulnerableTicks = 0;
        this.skill1CooldownTicks = 0;
        this.skill2CooldownTicks = 0;
        this.skill3CooldownTicks = 0;
        this.skill4CooldownTicks = 0;
        this.signatureCooldownTicks = 0;
        this.initialized = false;
        this.selectedSkills.clear();
        this.selectedSkills.add("basic_step");
        this.selectedSkills.add("flame_slash");
    }

    public void initializeAwakening(String difficulty, String race, String playerClass, List<String> skills) {
        initializeAwakening("Chosen Soul", difficulty, race, playerClass, playerClass, StatAllocationMode.MANUAL, skills);
    }

    public void initializeAwakening(String characterName, String difficulty, String race, String primaryClass, String secondaryClass, StatAllocationMode mode, List<String> skills) {
        AscendConfig cfg = AscendConfig.INSTANCE;
        this.characterName = (characterName != null && !characterName.isBlank()) ? characterName.trim() : "Chosen Soul";
        this.difficulty = difficulty;
        this.race = race;
        this.primaryClass = primaryClass;
        this.secondaryClass = (secondaryClass != null && !secondaryClass.isBlank()) ? secondaryClass : primaryClass;
        this.allocationMode = mode != null ? mode : StatAllocationMode.MANUAL;
        this.selectedSkills.clear();
        this.selectedSkills.addAll(skills);

        int baseStr = cfg.baseStrength;
        int baseAgi = cfg.baseSpeed;
        int baseVit = cfg.baseVitality;
        int baseInt = cfg.baseMana;
        int baseDex = cfg.baseDefense;

        // Race bonuses
        if ("Dragonoid".equalsIgnoreCase(race)) {
            baseStr += cfg.dragonoidStrengthBonus;
        } else if ("Slime".equalsIgnoreCase(race)) {
            baseVit += cfg.slimeVitalityPenalty;
            baseDex += cfg.slimeDefensePenalty;
        } else if ("Elf".equalsIgnoreCase(race)) {
            baseInt += cfg.elfManaBonus;
            baseVit += cfg.elfVitalityPenalty;
        } else if ("Beastkin".equalsIgnoreCase(race)) {
            baseStr += cfg.beastkinStrengthBonus;
            baseInt += cfg.beastkinManaPenalty;
        } else if ("Demonkin".equalsIgnoreCase(race)) {
            baseStr += cfg.demonkinStrengthBonus;
            baseInt += cfg.demonkinManaBonus;
        }

        // Apply Primary Class bonuses
        applyClassStatBonus(this.primaryClass, cfg, baseStr, baseAgi, baseVit, baseInt, baseDex);
        // Apply Secondary Class bonuses (50% effectiveness for hybrid balance if different)
        double secFactor = this.primaryClass.equalsIgnoreCase(this.secondaryClass) ? 0.5 : 0.6;
        applySecondaryClassStatBonus(this.secondaryClass, cfg, secFactor);

        this.strength = Math.max(1, baseStr);
        this.agility = Math.max(1, baseAgi);
        this.vitality = Math.max(5, baseVit);
        this.intelligence = Math.max(0, baseInt);
        this.dexterity = Math.max(0, baseDex);
        this.currentMana = this.intelligence;
        this.currentStamina = cfg.maxStamina;
        this.storyProgress.setPrologueCompleted(true);
        this.initialized = true;
    }

    private void applyClassStatBonus(String cls, AscendConfig cfg, int str, int agi, int vit, int intel, int dex) {
        switch (cls.toLowerCase()) {
            case "swordsman" -> {
                this.strength += cfg.swordsmanStrengthBonus;
                this.agility += cfg.swordsmanSpeedBonus;
            }
            case "tank" -> {
                this.dexterity += cfg.tankDefenseBonus;
                this.vitality += cfg.tankVitalityBonus;
                this.agility += cfg.tankSpeedPenalty;
            }
            case "mage" -> {
                this.intelligence += cfg.mageManaBonus;
                this.dexterity += cfg.mageDefensePenalty;
            }
            case "summoner" -> {
                this.intelligence += cfg.summonerManaBonus;
                this.vitality += cfg.summonerVitalityPenalty;
            }
            case "archer" -> {
                this.agility += cfg.archerSpeedBonus;
                this.strength += cfg.archerStrengthBonus;
                this.dexterity += cfg.archerDefensePenalty;
            }
        }
    }

    private void applySecondaryClassStatBonus(String cls, AscendConfig cfg, double factor) {
        switch (cls.toLowerCase()) {
            case "swordsman" -> {
                this.strength += (int) (cfg.swordsmanStrengthBonus * factor);
                this.agility += (int) (cfg.swordsmanSpeedBonus * factor);
            }
            case "tank" -> {
                this.dexterity += (int) (cfg.tankDefenseBonus * factor);
                this.vitality += (int) (cfg.tankVitalityBonus * factor);
            }
            case "mage" -> this.intelligence += (int) (cfg.mageManaBonus * factor);
            case "summoner" -> this.intelligence += (int) (cfg.summonerManaBonus * factor);
            case "archer" -> {
                this.agility += (int) (cfg.archerSpeedBonus * factor);
                this.strength += (int) (cfg.archerStrengthBonus * factor);
            }
        }
    }

    public boolean hasCombatClass(String className) {
        if (className == null) return false;
        return primaryClass.equalsIgnoreCase(className) || secondaryClass.equalsIgnoreCase(className);
    }

    public String getCombinedClassTitle() {
        return CharacterClass.getCombinedTitle(primaryClass, secondaryClass);
    }

    public int getMaxUnlockedSkillSlots() {
        String r = getRank();
        return switch (r) {
            case "S" -> 5;
            case "A", "B" -> 4;
            case "C" -> 3;
            default -> 2;
        };
    }

    public boolean canEquipSkill(String skillId) {
        net.ascend.mod.skills.SkillInfo info = net.ascend.mod.skills.SkillInfo.getById(skillId);
        if (info.rank() == net.ascend.mod.skills.SkillRank.S) {
            long countS = selectedSkills.stream().map(net.ascend.mod.skills.SkillInfo::getById)
                    .filter(s -> s.rank() == net.ascend.mod.skills.SkillRank.S).count();
            if (countS >= 1 && !selectedSkills.contains(skillId)) return false;
        }
        if (info.rank() == net.ascend.mod.skills.SkillRank.SS) {
            long countSS = selectedSkills.stream().map(net.ascend.mod.skills.SkillInfo::getById)
                    .filter(s -> s.rank() == net.ascend.mod.skills.SkillRank.SS).count();
            if (countSS >= 1 && !selectedSkills.contains(skillId)) return false;
        }
        return true;
    }

    public boolean equipSkillToSlot(int slotIndex, String skillId) {
        if (slotIndex < 0 || slotIndex >= getMaxUnlockedSkillSlots()) return false;
        if (!hasUnlockedSkill(skillId)) return false;
        if (!canEquipSkill(skillId)) return false;

        while (selectedSkills.size() <= slotIndex) {
            selectedSkills.add("");
        }

        int existingIdx = selectedSkills.indexOf(skillId);
        if (existingIdx != -1 && existingIdx != slotIndex) {
            String currentInTarget = selectedSkills.get(slotIndex);
            selectedSkills.set(existingIdx, currentInTarget);
        }

        selectedSkills.set(slotIndex, skillId);
        return true;
    }

    public void unequipSlot(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < selectedSkills.size()) {
            selectedSkills.set(slotIndex, "");
        }
    }

    public void tickResources() {
        AscendConfig cfg = AscendConfig.INSTANCE;

        if (resourceLockedTicks > 0) {
            resourceLockedTicks--;
        } else {
            // Mana regen
            double manaRegenRate = "Elf".equalsIgnoreCase(race) ? (cfg.manaRegenPerSecond * cfg.elfManaRegenMultiplier) : cfg.manaRegenPerSecond;
            if (currentMana < intelligence) {
                currentMana = Math.min(intelligence, currentMana + (manaRegenRate / 20.0));
            }

            // Stamina regen
            double maxSta = "Beastkin".equalsIgnoreCase(race) ? (cfg.maxStamina + cfg.beastkinStaminaBonus) : cfg.maxStamina;
            if (currentStamina < maxSta) {
                currentStamina = Math.min(maxSta, currentStamina + (cfg.staminaRegenPerSecond / 20.0));
            }
        }

        // Cooldowns
        if (dashCooldownTicks > 0) dashCooldownTicks--;
        if (invulnerableTicks > 0) invulnerableTicks--;
        if (skill1CooldownTicks > 0) skill1CooldownTicks--;
        if (skill2CooldownTicks > 0) skill2CooldownTicks--;
        if (skill3CooldownTicks > 0) skill3CooldownTicks--;
        if (skill4CooldownTicks > 0) skill4CooldownTicks--;
        if (signatureCooldownTicks > 0) signatureCooldownTicks--;
    }

    public boolean canDash() {
        return dashCooldownTicks <= 0 && currentStamina >= AscendConfig.INSTANCE.dashStaminaCost;
    }

    public void triggerDash() {
        AscendConfig cfg = AscendConfig.INSTANCE;
        currentStamina = Math.max(0, currentStamina - cfg.dashStaminaCost);
        dashCooldownTicks = cfg.dashCooldownTicks;
        invulnerableTicks = cfg.dashInvulnerabilityTicks;
    }

    public boolean hasStamina(double amount) {
        return currentStamina >= amount;
    }

    public boolean consumeStamina(double amount) {
        if (hasStamina(amount)) {
            currentStamina -= amount;
            return true;
        }
        return false;
    }

    public boolean hasMana(double amount) {
        return currentMana >= amount;
    }

    public boolean consumeMana(double amount) {
        if (hasMana(amount)) {
            currentMana -= amount;
            return true;
        }
        return false;
    }

    public String getRank() {
        return dungeonProgress.getCurrentRank();
    }

    public long getXpRequiredForNextLevel() {
        return (long) (100 * Math.pow(level, 1.45));
    }

    public void addXp(long amount) {
        if (amount <= 0) return;
        this.xp += amount;
        while (this.xp >= getXpRequiredForNextLevel() && this.level < 100) {
            this.xp -= getXpRequiredForNextLevel();
            this.level++;
            this.statPoints += 3;

            // Auto-allocation if enabled
            if (allocationMode != StatAllocationMode.MANUAL) {
                autoAllocatePoints(3);
            }
        }
    }

    public void autoAllocatePoints(int points) {
        for (int i = 0; i < points; i++) {
            if (statPoints <= 0) break;
            switch (allocationMode) {
                case BALANCED -> {
                    int mod = (strength + agility + vitality + intelligence + dexterity) % 5;
                    switch (mod) {
                        case 0 -> upgradeStat("strength");
                        case 1 -> upgradeStat("agility");
                        case 2 -> upgradeStat("vitality");
                        case 3 -> upgradeStat("intelligence");
                        case 4 -> upgradeStat("dexterity");
                    }
                }
                case STRENGTH_FOCUSED -> {
                    if (strength <= vitality * 2) upgradeStat("strength");
                    else upgradeStat("vitality");
                }
                case INTELLIGENCE_FOCUSED -> {
                    if (intelligence <= vitality * 3) upgradeStat("intelligence");
                    else upgradeStat("agility");
                }
                case AGILITY_FOCUSED -> {
                    if (agility <= strength) upgradeStat("agility");
                    else upgradeStat("dexterity");
                }
                default -> {}
            }
        }
    }

    public long getEssenceCostForStat(int currentStatValue) {
        AscendConfig cfg = AscendConfig.INSTANCE;
        return (long) (cfg.essencePerStatUpgradeBase * Math.pow(cfg.essenceCostMultiplier, Math.max(0, currentStatValue - 5)));
    }

    public boolean upgradeStat(String statName) {
        if (statPoints > 0) {
            statPoints--;
            applyStatIncrement(statName);
            return true;
        } else {
            int currentVal = getStatValue(statName);
            long cost = getEssenceCostForStat(currentVal);
            if (essence >= cost) {
                essence -= cost;
                applyStatIncrement(statName);
                return true;
            }
        }
        return false;
    }

    public int getStatValue(String statName) {
        return switch (statName.toLowerCase()) {
            case "strength" -> strength;
            case "speed", "agility" -> agility;
            case "vitality" -> vitality;
            case "mana", "intelligence" -> intelligence;
            case "defense", "dexterity" -> dexterity;
            default -> 0;
        };
    }

    private void applyStatIncrement(String statName) {
        switch (statName.toLowerCase()) {
            case "strength" -> strength++;
            case "speed", "agility" -> agility++;
            case "vitality" -> vitality++;
            case "mana", "intelligence" -> {
                intelligence += AscendConfig.INSTANCE.manaPerPoint;
                currentMana += AscendConfig.INSTANCE.manaPerPoint;
            }
            case "defense", "dexterity" -> dexterity++;
        }
    }

    public void applyModifiers(PlayerEntity player) {
        if (player == null || player.getWorld().isClient()) return;
        AscendConfig cfg = AscendConfig.INSTANCE;

        // Vitality -> Max Health
        EntityAttributeInstance maxHealthAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (maxHealthAttr != null) {
            maxHealthAttr.removeModifier(HEALTH_MODIFIER_UUID);
            double extraHealth = (vitality - cfg.baseVitality) * cfg.healthPerVitality;
            if (extraHealth != 0) {
                maxHealthAttr.addPersistentModifier(new EntityAttributeModifier(
                        HEALTH_MODIFIER_UUID, "Ascend Vitality Health", extraHealth, EntityAttributeModifier.Operation.ADDITION));
            }
            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }

        // Agility / Speed -> Movement Speed
        EntityAttributeInstance speedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.removeModifier(SPEED_MODIFIER_UUID);
            double speedMultiplier = (agility - cfg.baseSpeed) * cfg.speedPercentPerPoint;
            if ("Dragonoid".equalsIgnoreCase(race)) {
                speedMultiplier += cfg.dragonoidSpeedPenalty;
            } else if ("Elf".equalsIgnoreCase(race)) {
                speedMultiplier += cfg.elfSpeedBonus;
            } else if ("Beastkin".equalsIgnoreCase(race)) {
                speedMultiplier += cfg.beastkinSpeedBonus;
            }
            if (speedMultiplier != 0) {
                speedAttr.addPersistentModifier(new EntityAttributeModifier(
                        SPEED_MODIFIER_UUID, "Ascend Agility Speed", speedMultiplier, EntityAttributeModifier.Operation.MULTIPLY_BASE));
            }
        }

        // Strength -> Attack Damage
        EntityAttributeInstance attackAttr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (attackAttr != null) {
            attackAttr.removeModifier(STRENGTH_MODIFIER_UUID);
            double extraDamage = ((strength - cfg.baseStrength) / 2.0) * cfg.damagePerTwoStrength;
            if (extraDamage != 0) {
                attackAttr.addPersistentModifier(new EntityAttributeModifier(
                        STRENGTH_MODIFIER_UUID, "Ascend Strength Attack", extraDamage, EntityAttributeModifier.Operation.ADDITION));
            }
        }
    }

    public double getDamageReductionMultiplier() {
        AscendConfig cfg = AscendConfig.INSTANCE;
        double reduction = dexterity * cfg.defenseReductionPerPoint;
        reduction = Math.min(reduction, cfg.defenseReductionCap);
        return 1.0 - reduction;
    }

    public void addEssenceWithBonus(long amount) {
        if ("Human".equalsIgnoreCase(race)) {
            amount = Math.round(amount * (1.0 + AscendConfig.INSTANCE.humanEssenceBonus));
        }
        addEssence(amount);
        addXp(amount); // Automatically awards progression XP alongside Essence
    }

    public void writeToNbt(NbtCompound nbt) {
        nbt.putString("CharacterName", characterName);
        nbt.putString("Race", race);
        nbt.putString("PrimaryClass", primaryClass);
        nbt.putString("SecondaryClass", secondaryClass);
        nbt.putString("Class", getCombinedClassTitle()); // For legacy compatibility
        nbt.putString("Difficulty", difficulty);
        nbt.putString("AllocationMode", allocationMode.name());
        nbt.putString("CurrentEvolutionTier", currentEvolutionTier);
        nbt.putInt("Level", level);
        nbt.putLong("Xp", xp);
        nbt.putLong("Essence", essence);
        nbt.putInt("StatPoints", statPoints);
        nbt.putInt("Strength", strength);
        nbt.putInt("Agility", agility);
        nbt.putInt("Speed", agility); // Legacy compatibility
        nbt.putInt("Vitality", vitality);
        nbt.putInt("Intelligence", intelligence);
        nbt.putInt("Mana", intelligence); // Legacy compatibility
        nbt.putInt("Dexterity", dexterity);
        nbt.putInt("Defense", dexterity); // Legacy compatibility
        nbt.putDouble("CurrentMana", currentMana);
        nbt.putDouble("CurrentStamina", currentStamina);
        nbt.putBoolean("Initialized", initialized);

        NbtList skillsList = new NbtList();
        for (String skill : selectedSkills) {
            skillsList.add(NbtString.of(skill));
        }
        nbt.put("SelectedSkills", skillsList);

        NbtList unlockedList = new NbtList();
        for (String skill : unlockedSkills) {
            unlockedList.add(NbtString.of(skill));
        }
        nbt.put("UnlockedSkills", unlockedList);
        nbt.putInt("ResourceLockedTicks", resourceLockedTicks);

        // Sub-frameworks
        NbtCompound questTag = new NbtCompound();
        questProgress.writeToNbt(questTag);
        nbt.put("QuestProgress", questTag);

        NbtCompound dungeonTag = new NbtCompound();
        dungeonProgress.writeToNbt(dungeonTag);
        nbt.put("DungeonProgress", dungeonTag);

        NbtCompound artifactTag = new NbtCompound();
        artifactProgress.writeToNbt(artifactTag);
        nbt.put("ArtifactProgress", artifactTag);

        NbtCompound storyTag = new NbtCompound();
        storyProgress.writeToNbt(storyTag);
        nbt.put("StoryProgress", storyTag);
    }

    public void readFromNbt(NbtCompound nbt) {
        if (nbt.contains("CharacterName")) characterName = nbt.getString("CharacterName");
        if (nbt.contains("Race")) race = nbt.getString("Race");

        if (nbt.contains("PrimaryClass")) primaryClass = nbt.getString("PrimaryClass");
        else if (nbt.contains("Class")) primaryClass = nbt.getString("Class");

        if (nbt.contains("SecondaryClass")) secondaryClass = nbt.getString("SecondaryClass");
        else secondaryClass = primaryClass;

        if (nbt.contains("Difficulty")) difficulty = nbt.getString("Difficulty");
        if (nbt.contains("AllocationMode")) {
            allocationMode = StatAllocationMode.fromString(nbt.getString("AllocationMode"));
        }
        if (nbt.contains("CurrentEvolutionTier")) currentEvolutionTier = nbt.getString("CurrentEvolutionTier");
        if (nbt.contains("Level")) level = nbt.getInt("Level");
        if (nbt.contains("Xp")) xp = nbt.getLong("Xp");
        if (nbt.contains("Essence")) essence = nbt.getLong("Essence");
        if (nbt.contains("StatPoints")) statPoints = nbt.getInt("StatPoints");
        if (nbt.contains("Strength")) strength = nbt.getInt("Strength");

        if (nbt.contains("Agility")) agility = nbt.getInt("Agility");
        else if (nbt.contains("Speed")) agility = nbt.getInt("Speed");

        if (nbt.contains("Vitality")) vitality = nbt.getInt("Vitality");

        if (nbt.contains("Intelligence")) intelligence = nbt.getInt("Intelligence");
        else if (nbt.contains("Mana")) intelligence = nbt.getInt("Mana");

        if (nbt.contains("Dexterity")) dexterity = nbt.getInt("Dexterity");
        else if (nbt.contains("Defense")) dexterity = nbt.getInt("Defense");

        if (nbt.contains("CurrentMana")) currentMana = nbt.getDouble("CurrentMana");
        if (nbt.contains("CurrentStamina")) currentStamina = nbt.getDouble("CurrentStamina");
        if (nbt.contains("Initialized")) initialized = nbt.getBoolean("Initialized");

        selectedSkills.clear();
        if (nbt.contains("SelectedSkills", 9)) {
            NbtList skillsList = nbt.getList("SelectedSkills", 8);
            for (int i = 0; i < skillsList.size(); i++) {
                selectedSkills.add(skillsList.getString(i));
            }
        }

        unlockedSkills.clear();
        if (nbt.contains("UnlockedSkills", 9)) {
            NbtList unlockedList = nbt.getList("UnlockedSkills", 8);
            for (int i = 0; i < unlockedList.size(); i++) {
                unlockedSkills.add(unlockedList.getString(i));
            }
        }
        if (nbt.contains("ResourceLockedTicks")) resourceLockedTicks = nbt.getInt("ResourceLockedTicks");

        if (nbt.contains("QuestProgress")) questProgress.readFromNbt(nbt.getCompound("QuestProgress"));
        if (nbt.contains("DungeonProgress")) dungeonProgress.readFromNbt(nbt.getCompound("DungeonProgress"));
        if (nbt.contains("ArtifactProgress")) artifactProgress.readFromNbt(nbt.getCompound("ArtifactProgress"));
        if (nbt.contains("StoryProgress")) storyProgress.readFromNbt(nbt.getCompound("StoryProgress"));
    }

    public void writeToBuffer(PacketByteBuf buf) {
        buf.writeString(characterName);
        buf.writeString(race);
        buf.writeString(primaryClass);
        buf.writeString(secondaryClass);
        buf.writeString(difficulty);
        buf.writeString(allocationMode.name());
        buf.writeString(currentEvolutionTier);
        buf.writeInt(level);
        buf.writeLong(xp);
        buf.writeLong(essence);
        buf.writeInt(statPoints);
        buf.writeInt(strength);
        buf.writeInt(agility);
        buf.writeInt(vitality);
        buf.writeInt(intelligence);
        buf.writeInt(dexterity);
        buf.writeDouble(currentMana);
        buf.writeDouble(currentStamina);
        buf.writeInt(dashCooldownTicks);
        buf.writeInt(skill1CooldownTicks);
        buf.writeInt(skill2CooldownTicks);
        buf.writeInt(skill3CooldownTicks);
        buf.writeInt(skill4CooldownTicks);
        buf.writeInt(signatureCooldownTicks);
        buf.writeBoolean(initialized);

        buf.writeInt(selectedSkills.size());
        for (String s : selectedSkills) {
            buf.writeString(s);
        }

        buf.writeInt(unlockedSkills.size());
        for (String u : unlockedSkills) {
            buf.writeString(u);
        }
        buf.writeInt(resourceLockedTicks);

        // Sub-frameworks
        questProgress.writeToBuffer(buf);
        dungeonProgress.writeToBuffer(buf);
        artifactProgress.writeToBuffer(buf);
        storyProgress.writeToBuffer(buf);
    }

    public void readFromBuffer(PacketByteBuf buf) {
        characterName = buf.readString();
        race = buf.readString();
        primaryClass = buf.readString();
        secondaryClass = buf.readString();
        difficulty = buf.readString();
        allocationMode = StatAllocationMode.fromString(buf.readString());
        currentEvolutionTier = buf.readString();
        level = buf.readInt();
        xp = buf.readLong();
        essence = buf.readLong();
        statPoints = buf.readInt();
        strength = buf.readInt();
        agility = buf.readInt();
        vitality = buf.readInt();
        intelligence = buf.readInt();
        dexterity = buf.readInt();
        currentMana = buf.readDouble();
        currentStamina = buf.readDouble();
        dashCooldownTicks = buf.readInt();
        skill1CooldownTicks = buf.readInt();
        skill2CooldownTicks = buf.readInt();
        skill3CooldownTicks = buf.readInt();
        skill4CooldownTicks = buf.readInt();
        signatureCooldownTicks = buf.readInt();
        initialized = buf.readBoolean();

        selectedSkills.clear();
        int skillCount = buf.readInt();
        for (int i = 0; i < skillCount; i++) {
            selectedSkills.add(buf.readString());
        }

        unlockedSkills.clear();
        int unlockedCount = buf.readInt();
        for (int i = 0; i < unlockedCount; i++) {
            unlockedSkills.add(buf.readString());
        }
        resourceLockedTicks = buf.readInt();

        // Sub-frameworks
        questProgress.readFromBuffer(buf);
        dungeonProgress.readFromBuffer(buf);
        artifactProgress.readFromBuffer(buf);
        storyProgress.readFromBuffer(buf);
    }

    public boolean hasUnlockedSkill(String id) {
        return unlockedSkills.contains(id.toLowerCase());
    }

    public void unlockSkill(String id) {
        unlockedSkills.add(id.toLowerCase());
    }

    public Set<String> getUnlockedSkills() {
        return Collections.unmodifiableSet(unlockedSkills);
    }

    public int getResourceLockedTicks() { return resourceLockedTicks; }
    public void setResourceLockedTicks(int ticks) { this.resourceLockedTicks = ticks; }
    public boolean isResourceLocked() { return resourceLockedTicks > 0; }

    // Getters and setters
    public String getCharacterName() { return characterName; }
    public void setCharacterName(String name) { this.characterName = name; }

    public String getRace() { return race; }
    public void setRace(String race) { this.race = race; }

    public String getPrimaryClass() { return primaryClass; }
    public void setPrimaryClass(String primaryClass) { this.primaryClass = primaryClass; }

    public String getSecondaryClass() { return secondaryClass; }
    public void setSecondaryClass(String secondaryClass) { this.secondaryClass = secondaryClass; }

    public String getPlayerClass() { return getCombinedClassTitle(); }
    public void setPlayerClass(String playerClass) {
        this.primaryClass = playerClass;
        this.secondaryClass = playerClass;
    }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public StatAllocationMode getAllocationMode() { return allocationMode; }
    public void setAllocationMode(StatAllocationMode mode) { this.allocationMode = mode; }

    public String getCurrentEvolutionTier() { return currentEvolutionTier; }
    public void setCurrentEvolutionTier(String tier) { this.currentEvolutionTier = tier; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public long getXp() { return xp; }
    public void setXp(long xp) { this.xp = xp; }

    public long getEssence() { return essence; }
    public void setEssence(long essence) { this.essence = essence; }
    public void addEssence(long amount) { this.essence = Math.max(0, this.essence + amount); }

    public int getStatPoints() { return statPoints; }
    public void setStatPoints(int statPoints) { this.statPoints = statPoints; }
    public void addStatPoints(int amount) { this.statPoints = Math.max(0, this.statPoints + amount); }

    public int getStrength() { return strength; }
    public void setStrength(int strength) { this.strength = strength; }

    public int getAgility() { return agility; }
    public void setAgility(int agility) { this.agility = agility; }
    public int getSpeed() { return agility; }
    public void setSpeed(int speed) { this.agility = speed; }

    public int getVitality() { return vitality; }
    public void setVitality(int vitality) { this.vitality = vitality; }

    public int getIntelligence() { return intelligence; }
    public void setIntelligence(int intelligence) { this.intelligence = intelligence; }
    public int getMana() { return intelligence; }
    public void setMana(int mana) { this.intelligence = mana; }

    public int getDexterity() { return dexterity; }
    public void setDexterity(int dexterity) { this.dexterity = dexterity; }
    public int getDefense() { return dexterity; }
    public void setDefense(int defense) { this.dexterity = defense; }

    public double getCurrentMana() { return currentMana; }
    public void setCurrentMana(double currentMana) { this.currentMana = currentMana; }

    public double getCurrentStamina() { return currentStamina; }
    public void setCurrentStamina(double currentStamina) { this.currentStamina = currentStamina; }

    public int getDashCooldownTicks() { return dashCooldownTicks; }
    public void setDashCooldownTicks(int ticks) { this.dashCooldownTicks = ticks; }

    public int getInvulnerableTicks() { return invulnerableTicks; }
    public void setInvulnerableTicks(int ticks) { this.invulnerableTicks = ticks; }

    public int getSkillCooldown(int slot) {
        return switch (slot) {
            case 0 -> skill1CooldownTicks;
            case 1 -> skill2CooldownTicks;
            case 2 -> skill3CooldownTicks;
            case 3 -> skill4CooldownTicks;
            default -> 0;
        };
    }

    public void setSkillCooldown(int slot, int ticks) {
        switch (slot) {
            case 0 -> this.skill1CooldownTicks = ticks;
            case 1 -> this.skill2CooldownTicks = ticks;
            case 2 -> this.skill3CooldownTicks = ticks;
            case 3 -> this.skill4CooldownTicks = ticks;
        }
    }

    public int getSkill1CooldownTicks() { return skill1CooldownTicks; }
    public void setSkill1CooldownTicks(int ticks) { this.skill1CooldownTicks = ticks; }

    public int getSkill2CooldownTicks() { return skill2CooldownTicks; }
    public void setSkill2CooldownTicks(int ticks) { this.skill2CooldownTicks = ticks; }

    public int getSkill3CooldownTicks() { return skill3CooldownTicks; }
    public void setSkill3CooldownTicks(int ticks) { this.skill3CooldownTicks = ticks; }

    public int getSkill4CooldownTicks() { return skill4CooldownTicks; }
    public void setSkill4CooldownTicks(int ticks) { this.skill4CooldownTicks = ticks; }

    public int getSignatureCooldownTicks() { return signatureCooldownTicks; }
    public void setSignatureCooldownTicks(int ticks) { this.signatureCooldownTicks = ticks; }

    public boolean isInitialized() { return initialized; }
    public void setInitialized(boolean initialized) { this.initialized = initialized; }

    public List<String> getSelectedSkills() { return selectedSkills; }

    public QuestProgress getQuestProgress() { return questProgress; }
    public DungeonProgress getDungeonProgress() { return dungeonProgress; }
    public ArtifactProgress getArtifactProgress() { return artifactProgress; }
    public StoryProgress getStoryProgress() { return storyProgress; }
}
