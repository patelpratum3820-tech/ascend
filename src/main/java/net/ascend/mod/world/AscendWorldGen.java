package net.ascend.mod.world;

import net.ascend.mod.AscendMod;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;

public class AscendWorldGen {

    public static final RegistryKey<net.minecraft.world.gen.feature.PlacedFeature> MYSTIC_TREE_PLACED =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(AscendMod.MOD_ID, "mystic_tree_placed"));

    public static final RegistryKey<net.minecraft.world.gen.feature.PlacedFeature> EMBERWOOD_TREE_PLACED =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(AscendMod.MOD_ID, "emberwood_tree_placed"));

    public static final RegistryKey<net.minecraft.world.gen.feature.PlacedFeature> ORE_MYTHRIL_PLACED =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(AscendMod.MOD_ID, "ore_mythril_placed"));

    public static final RegistryKey<net.minecraft.world.gen.feature.PlacedFeature> ORE_MANA_CRYSTAL_PLACED =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(AscendMod.MOD_ID, "ore_mana_crystal_placed"));

    public static final RegistryKey<net.minecraft.world.gen.feature.PlacedFeature> ORE_DRAGONITE_PLACED =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(AscendMod.MOD_ID, "ore_dragonite_placed"));

    public static void registerWorldGen() {
        AscendMod.LOGGER.info("[Ascend] Initializing Natural Worldgen Features (W2 Trees & Ores)...");

        // 1. Initialize Biome keys
        AscendBiomes.init();

        // 2. Trees (Vegetal Decoration)
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(AscendBiomes.ENCHANTED_FOREST),
                GenerationStep.Feature.VEGETAL_DECORATION,
                MYSTIC_TREE_PLACED
        );

        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(AscendBiomes.EMBERLANDS),
                GenerationStep.Feature.VEGETAL_DECORATION,
                EMBERWOOD_TREE_PLACED
        );

        // 3. Ores (Underground Ores)
        // Mythril: Deep Overworld (y -64 to 16)
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                ORE_MYTHRIL_PLACED
        );

        // Mana Crystal: Crystal Caverns & underground caves
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                ORE_MANA_CRYSTAL_PLACED
        );

        // Dragonite: Mountain peaks & Nether
        BiomeModifications.addFeature(
                BiomeSelectors.tag(BiomeTags.IS_MOUNTAIN).or(BiomeSelectors.foundInTheNether()),
                GenerationStep.Feature.UNDERGROUND_ORES,
                ORE_DRAGONITE_PLACED
        );
    }
}
