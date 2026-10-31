package net.ascend.mod.world;

import net.ascend.mod.AscendMod;

public class AscendWorldGen {
    public static void registerWorldGen() {
        AscendMod.LOGGER.info("[Ascend] Initializing Natural World Generation (Step W1 Biomes)...");
        AscendBiomes.init();
    }
}
