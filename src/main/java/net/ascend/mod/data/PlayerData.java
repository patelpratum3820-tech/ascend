package net.ascend.mod.data;

import net.ascend.mod.config.AscendConfig;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

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

    private boolean initialized = false;

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
        this.initialized = false;
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
            case "mana" -> mana += AscendConfig.INSTANCE.manaPerPoint;
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
        }

        // Speed -> Movement Speed
        EntityAttributeInstance speedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.removeModifier(SPEED_MODIFIER_UUID);
            double extraSpeedMult = (speed - cfg.baseSpeed) * cfg.speedPercentPerPoint;
            if (extraSpeedMult != 0) {
                speedAttr.addPersistentModifier(new EntityAttributeModifier(
                        SPEED_MODIFIER_UUID, "Ascend Agility Speed", extraSpeedMult, EntityAttributeModifier.Operation.MULTIPLY_BASE));
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
        nbt.putBoolean("Initialized", initialized);
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
        if (nbt.contains("Initialized")) initialized = nbt.getBoolean("Initialized");
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
        buf.writeBoolean(initialized);
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
        initialized = buf.readBoolean();
    }

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

    public boolean isInitialized() { return initialized; }
    public void setInitialized(boolean initialized) { this.initialized = initialized; }
}
