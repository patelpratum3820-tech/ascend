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

    public int baseVitality = 20;
    public double healthPerVitality = 1.0;

    public int baseStrength = 5;
    public double damagePerTwoStrength = 1.0;

    public int baseSpeed = 5;
    public double speedPercentPerPoint = 0.005; // +0.5% per point

    public int baseMana = 50;
    public int manaPerPoint = 5;

    public int baseDefense = 0;
    public double defenseReductionPerPoint = 0.01; // +1% per point
    public double defenseReductionCap = 0.60;      // max 60%

    // Essence cost to upgrade stats if points are not used
    public int essencePerStatUpgradeBase = 50;
    public double essenceCostMultiplier = 1.15;

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
