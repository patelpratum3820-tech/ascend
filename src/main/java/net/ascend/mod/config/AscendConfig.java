package net.ascend.mod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ascend.mod.AscendMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AscendConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "ascend.json");

    // Base Stats
    public int baseVitality = 20;
    public double healthPerVitality = 1.0;

    public int baseStrength = 5;
    public double damagePerTwoStrength = 1.0;

    public int baseSpeed = 5;
    public double speedPercentPerPoint = 0.005;

    public int baseMana = 50;
    public int manaPerPoint = 5;

    public int baseDefense = 0;
    public double defenseReductionPerPoint = 0.01;
    public double defenseReductionCap = 0.60;

    // Resources
    public double maxStamina = 100.0;
    public double staminaRegenPerSecond = 10.0;
    public double manaRegenPerSecond = 3.0;

    // Dash / Dodge
    public double dashImpulse = 1.25;
    public double dashStaminaCost = 20.0;
    public int dashCooldownTicks = 50; // 2.5 seconds
    public int dashInvulnerabilityTicks = 7; // ~0.35s i-frames

    // Stat Upgrades
    public int essencePerStatUpgradeBase = 50;
    public double essenceCostMultiplier = 1.15;

    // Difficulty Multipliers (Health / Damage / Essence)
    public double easyHealthMult = 0.75;
    public double easyDamageMult = 0.75;
    public double easyEssenceMult = 0.75;

    public double normalHealthMult = 1.0;
    public double normalDamageMult = 1.0;
    public double normalEssenceMult = 1.0;

    public double hardHealthMult = 1.5;
    public double hardDamageMult = 1.25;
    public double hardEssenceMult = 1.5;

    public double nightmareHealthMult = 2.5;
    public double nightmareDamageMult = 1.75;
    public double nightmareEssenceMult = 2.5;

    // World Generation & Feature Frequencies (Tunable via config)
    public int mythrilOreVeinsPerChunk = 5;
    public int manaCrystalOreVeinsPerChunk = 7;
    public int dragoniteOreVeinsPerChunk = 6;
    public int mysticTreeCountPerChunk = 10;
    public int emberwoodTreeCountPerChunk = 6;

    // Mob Spawning Weights & Group Sizes
    public int spiritFoxWeight = 8;
    public int spiritFoxMinGroup = 1;
    public int spiritFoxMaxGroup = 2;

    public int manaDeerWeight = 10;
    public int manaDeerMinGroup = 2;
    public int manaDeerMaxGroup = 4;

    public int slimePupWeight = 6;
    public int slimePupMinGroup = 1;
    public int slimePupMaxGroup = 3;

    public int moonMothWeight = 10;
    public int moonMothMinGroup = 2;
    public int moonMothMaxGroup = 5;

    public int goblinWeight = 30;
    public int goblinMinGroup = 3;
    public int goblinMaxGroup = 5;

    public int orcWarriorWeight = 12;
    public int orcWarriorMinGroup = 1;
    public int orcWarriorMaxGroup = 2;

    public int shadowWraithWeight = 20;
    public int shadowWraithMinGroup = 1;
    public int shadowWraithMaxGroup = 2;

    public int skeletonKnightWeight = 10;
    public int skeletonKnightMinGroup = 1;
    public int skeletonKnightMaxGroup = 2;

    public int darkMageWeight = 6;
    public int darkMageMinGroup = 1;
    public int darkMageMaxGroup = 1;

    public int fireDrakeWeight = 6;
    public int fireDrakeMinGroup = 1;
    public int fireDrakeMaxGroup = 1;

    // Surface Structure Spacings (Tunable)
    public int guildHallSpacing = 30;
    public int forgeRuinsSpacing = 26;
    public int ruinedShrineSpacing = 22;
    public int mageTowerSpacing = 28;
    public int bossArenaSpacing = 42;
    public int floatingTempleSpacing = 36;
    public int demonGateSpacing = 32;

    // Dungeon Rank Distance from Spawn (Tunable)
    public int rankEDistance = 500;
    public int rankDDistance = 1000;
    public int rankCDistance = 2000;
    public int rankBDistance = 3500;
    public int rankADistance = 5000;

    // Race Balance Numbers
    public double humanEssenceBonus = 0.15;

    public int dragonoidStrengthBonus = 3;
    public double dragonoidSpeedPenalty = -0.05;
    public double dragonoidColdWaterVulnerability = 1.5;

    public int slimeVitalityPenalty = -4;
    public int slimeDefensePenalty = -2;
    public boolean slimeNoFallDamage = true;
    public double slimeRegenChancePerTick = 0.02;

    // Class Base Adjustments
    public int swordsmanStrengthBonus = 2;
    public int swordsmanSpeedBonus = 1;

    public int tankDefenseBonus = 4;
    public int tankVitalityBonus = 4;
    public int tankSpeedPenalty = -2;

    public int mageManaBonus = 30;
    public int mageDefensePenalty = -2;

    public int summonerManaBonus = 20;
    public int summonerVitalityPenalty = -2;

    public int archerSpeedBonus = 2;
    public int archerStrengthBonus = 1;
    public int archerDefensePenalty = -2;

    public static AscendConfig INSTANCE = new AscendConfig();

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                INSTANCE = GSON.fromJson(reader, AscendConfig.class);
                if (INSTANCE == null) {
                    INSTANCE = new AscendConfig();
                }
            } catch (Exception e) {
                AscendMod.LOGGER.error("Failed to load Ascend config, using defaults", e);
                INSTANCE = new AscendConfig();
            }
            save();
        } else {
            save();
        }
    }

    public static void save() {
        try {
            if (!CONFIG_FILE.getParentFile().exists()) {
                CONFIG_FILE.getParentFile().mkdirs();
            }
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (IOException e) {
            AscendMod.LOGGER.error("Failed to save Ascend config", e);
        }
    }
}
