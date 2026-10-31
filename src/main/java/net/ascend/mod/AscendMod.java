package net.ascend.mod;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AscendMod implements ModInitializer {
    public static final String MOD_ID = "ascend";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[Ascend] Initializing Ascend Anime RPG Mod (Phase 0)...");
        LOGGER.info("[Ascend] System initialized successfully. Ready for adventurer awakening!");
    }
}

