package net.ascend.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class AscendClientMod implements ClientModInitializer {
    public static final Logger CLIENT_LOGGER = LoggerFactory.getLogger("ascend-client");

    @Override
    public void onInitializeClient() {
        CLIENT_LOGGER.info("[Ascend Client] Initializing Ascend Client systems...");
    }
}

