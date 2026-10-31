package net.ascend.mod;

import net.ascend.mod.client.gui.StatusScreen;
import net.ascend.mod.network.AscendClientPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class AscendClientMod implements ClientModInitializer {
    public static final Logger CLIENT_LOGGER = LoggerFactory.getLogger("ascend-client");

    public static KeyBinding statusScreenKey;

    @Override
    public void onInitializeClient() {
        CLIENT_LOGGER.info("[Ascend Client] Initializing Ascend Client systems (Phase 1)...");

        // 1. Client Packets
        AscendClientPackets.registerClientReceivers();

        // 2. Keybinding K
        statusScreenKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ascend.status_screen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.ascend.general"
        ));

        // 3. Tick listener for key press
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (statusScreenKey.wasPressed()) {
                if (client.player != null && client.currentScreen == null) {
                    client.setScreen(new StatusScreen());
                }
            }
        });

        CLIENT_LOGGER.info("[Ascend Client] Keybindings and GUI initialized.");
    }
}
