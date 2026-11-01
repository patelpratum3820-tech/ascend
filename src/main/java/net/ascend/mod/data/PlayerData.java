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

    private String race = "Human";
    private String playerClass = "Swordsman";
    private String difficulty = "Normal";

    private int level = 1;
    private long essence = 0;
    private int statPoints = 0;

    private int strength = 5;
    private int speed = 5;
    private int vitality = 20;
    private int mana = 50;
    private int defense = 0;

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

    public PlayerData() {
        resetToDefaults();
    }

    public void resetToDefaults() {
        AscendConfig config = AscendConfig.INSTANCE;
        this.race = "Human";
        this.playerClass = "Swordsman";
        this.difficulty = "Normal";
        this.level = 1;
        this.essence = 0;
        this.statPoints = 0;
        this.strength = config.baseStrength;
        this.speed = config.baseSpeed;
        this.vitality = config.baseVitality;
        this.mana = config.baseMana;
        this.defense = config.baseDefense;
        this.currentMana = this.mana;
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
        this.selectedSkills.add("shadow_step");
        this.selectedSkills.add("flame_slash");
        this.selectedSkills.add("iron_skin");
        this.selectedSkills.add("berserk");
    }

    public void initializeAwakening(String difficulty, String race, String playerClass, List<String> skills) {
        AscendConfig cfg = AscendConfig.INSTANCE;
        this.difficulty = difficulty;
        this.race = race;
        this.playerClass = playerClass;
        this.selectedSkills.clear();
        this.selectedSkills.addAll(skills);

        int baseStr = cfg.baseStrength;
        int baseSpd = cfg.baseSpeed;
        int baseVit = cfg.baseVitality;
        int baseMan = cfg.baseMana;
        int baseDef = cfg.baseDefense;

        if ("Dragonoid".equalsIgnoreCase(race)) {
            baseStr += cfg.dragonoidStrengthBonus;
        } else if ("Slime".equalsIgnoreCase(race)) {
            baseVit += cfg.slimeVitalityPenalty;
            baseDef += cfg.slimeDefensePenalty;
        } else if ("Elf".equalsIgnoreCase(race)) {
            baseMan += cfg.elfManaBonus;
            baseVit += cfg.elfVitalityPenalty;
        } else if ("Beastkin".equalsIgnoreCase(race)) {
            baseStr += cfg.beastkinStrengthBonus;
            baseMan += cfg.beastkinManaPenalty;
        } else if ("Demonkin".equalsIgnoreCase(race)) {
            baseStr += cfg.demonkinStrengthBonus;
            baseMan += cfg.demonkinManaBonus;
        }

        switch (playerClass.toLowerCase()) {
            case "swordsman" -> {
                baseStr += cfg.swordsmanStrengthBonus;
                baseSpd += cfg.swordsmanSpeedBonus;
            }
            case "tank" -> {
                baseDef += cfg.tankDefenseBonus;
                baseVit += cfg.tankVitalityBonus;
                baseSpd += cfg.tankSpeedPenalty;
            }
            case "mage" -> {
                baseMan += cfg.mageManaBonus;
                baseDef += cfg.mageDefensePenalty;
            }
            case "summoner" -> {
                baseMan += cfg.summonerManaBonus;
                baseVit += cfg.summonerVitalityPenalty;
            }
            case "archer" -> {
                baseSpd += cfg.archerSpeedBonus;
                baseStr += cfg.archerStrengthBonus;
                baseDef += cfg.archerDefensePenalty;
            }
        }

        this.strength = Math.max(1, baseStr);
        this.speed = Math.max(1, baseSpd);
        this.vitality = Math.max(5, baseVit);
        this.mana = Math.max(0, baseMan);
        this.defense = Math.max(0, baseDef);
        this.currentMana = this.mana;
        this.currentStamina = cfg.maxStamina;
        this.initialized = true;
    }

    public int getMaxUnlockedSkillSlots() {
        String r = getRank();
        return switch (r) {
            case "S" -> 5; // +1 at S
            case "A", "B" -> 4; // +1 at A
            case "C" -> 3; // +1 at C
            default -> 2; // Skill slots: start 2
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

    public void tickResources() {
        AscendConfig cfg = AscendConfig.INSTANCE;

        // Resource lockout (e.g. SS skill aftermath: 60 seconds)
        if (resourceLockedTicks > 0) {
            resourceLockedTicks--;
        } else {
            // Mana regen
            double manaRegenRate = "Elf".equalsIgnoreCase(race) ? (cfg.manaRegenPerSecond * cfg.elfManaRegenMultiplier) : cfg.manaRegenPerSecond;
            if (currentMana < mana) {
                currentMana = Math.min(mana, currentMana + (manaRegenRate / 20.0));
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
        if (level >= 100) return "S";
        if (level >= 75) return "A";
        if (level >= 50) return "B";
        if (level >= 25) return "C";
        if (level >= 10) return "D";
        return "E";
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

    private int getStatValue(String statName) {
        return switch (statName.toLowerCase()) {
            case "strength" -> strength;
            case "speed" -> speed;
            case "vitality" -> vitality;
            case "mana" -> mana;
            case "defense" -> defense;
            default -> 0;
        };
    }

    private void applyStatIncrement(String statName) {
        switch (statName.toLowerCase()) {
            case "strength" -> strength++;
            case "speed" -> speed++;
            case "vitality" -> vitality++;
            case "mana" -> {
                mana += AscendConfig.INSTANCE.manaPerPoint;
                currentMana += AscendConfig.INSTANCE.manaPerPoint;
            }
            case "defense" -> defense++;
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

        // Speed -> Movement Speed
        EntityAttributeInstance speedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.removeModifier(SPEED_MODIFIER_UUID);
            double speedMultiplier = (speed - cfg.baseSpeed) * cfg.speedPercentPerPoint;
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
        double reduction = defense * cfg.defenseReductionPerPoint;
        reduction = Math.min(reduction, cfg.defenseReductionCap);
        return 1.0 - reduction;
    }

    public void addEssenceWithBonus(long amount) {
        if ("Human".equalsIgnoreCase(race)) {
            amount = Math.round(amount * (1.0 + AscendConfig.INSTANCE.humanEssenceBonus));
        }
        addEssence(amount);
    }

    public void writeToNbt(NbtCompound nbt) {
        nbt.putString("Race", race);
        nbt.putString("Class", playerClass);
        nbt.putString("Difficulty", difficulty);
        nbt.putInt("Level", level);
        nbt.putLong("Essence", essence);
        nbt.putInt("StatPoints", statPoints);
        nbt.putInt("Strength", strength);
        nbt.putInt("Speed", speed);
        nbt.putInt("Vitality", vitality);
        nbt.putInt("Mana", mana);
        nbt.putInt("Defense", defense);
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
    }

    public void readFromNbt(NbtCompound nbt) {
        if (nbt.contains("Race")) race = nbt.getString("Race");
        if (nbt.contains("Class")) playerClass = nbt.getString("Class");
        if (nbt.contains("Difficulty")) difficulty = nbt.getString("Difficulty");
        if (nbt.contains("Level")) level = nbt.getInt("Level");
        if (nbt.contains("Essence")) essence = nbt.getLong("Essence");
        if (nbt.contains("StatPoints")) statPoints = nbt.getInt("StatPoints");
        if (nbt.contains("Strength")) strength = nbt.getInt("Strength");
        if (nbt.contains("Speed")) speed = nbt.getInt("Speed");
        if (nbt.contains("Vitality")) vitality = nbt.getInt("Vitality");
        if (nbt.contains("Mana")) mana = nbt.getInt("Mana");
        if (nbt.contains("Defense")) defense = nbt.getInt("Defense");
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
    }

    public void writeToBuffer(PacketByteBuf buf) {
        buf.writeString(race);
        buf.writeString(playerClass);
        buf.writeString(difficulty);
        buf.writeInt(level);
        buf.writeLong(essence);
        buf.writeInt(statPoints);
        buf.writeInt(strength);
        buf.writeInt(speed);
        buf.writeInt(vitality);
        buf.writeInt(mana);
        buf.writeInt(defense);
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
    }

    public void readFromBuffer(PacketByteBuf buf) {
        race = buf.readString();
        playerClass = buf.readString();
        difficulty = buf.readString();
        level = buf.readInt();
        essence = buf.readLong();
        statPoints = buf.readInt();
        strength = buf.readInt();
        speed = buf.readInt();
        vitality = buf.readInt();
        mana = buf.readInt();
        defense = buf.readInt();
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
    public String getRace() { return race; }
    public void setRace(String race) { this.race = race; }

    public String getPlayerClass() { return playerClass; }
    public void setPlayerClass(String playerClass) { this.playerClass = playerClass; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public long getEssence() { return essence; }
    public void setEssence(long essence) { this.essence = essence; }
    public void addEssence(long amount) { this.essence = Math.max(0, this.essence + amount); }

    public int getStatPoints() { return statPoints; }
    public void setStatPoints(int statPoints) { this.statPoints = statPoints; }
    public void addStatPoints(int amount) { this.statPoints = Math.max(0, this.statPoints + amount); }

    public int getStrength() { return strength; }
    public void setStrength(int strength) { this.strength = strength; }

    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }

    public int getVitality() { return vitality; }
    public void setVitality(int vitality) { this.vitality = vitality; }

    public int getMana() { return mana; }
    public void setMana(int mana) { this.mana = mana; }

    public int getDefense() { return defense; }
    public void setDefense(int defense) { this.defense = defense; }

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
}
