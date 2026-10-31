package net.ascend.mod.world;

import net.ascend.mod.AscendMod;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class AscendBiomes {
    public static final RegistryKey<Biome> ENCHANTED_FOREST = register("enchanted_forest");
    public static final RegistryKey<Biome> EMBERLANDS = register("emberlands");
    public static final RegistryKey<Biome> CRYSTAL_CAVERNS = register("crystal_caverns");
    public static final RegistryKey<Biome> SHADOW_MARSH = register("shadow_marsh");
    public static final RegistryKey<Biome> DRAGON_PEAKS = register("dragon_peaks");

    private static RegistryKey<Biome> register(String name) {
        return RegistryKey.of(RegistryKeys.BIOME, new Identifier(AscendMod.MOD_ID, name));
    }

    public static void init() {
        AscendMod.LOGGER.info("[Ascend] Custom RPG biomes registered.");
    }
}

