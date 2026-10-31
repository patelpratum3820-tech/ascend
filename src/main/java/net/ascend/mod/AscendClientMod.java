package net.ascend.mod;

import net.ascend.mod.client.gui.StatusScreen;
import net.ascend.mod.client.hud.AscendHudRenderer;
import net.ascend.mod.network.AscendClientPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class AscendClientMod implements ClientModInitializer {
    public static final Logger CLIENT_LOGGER = LoggerFactory.getLogger("ascend-client");

    public static KeyBinding statusScreenKey;
    public static KeyBinding dashKey;
    public static KeyBinding skill1Key;
    public static KeyBinding skill2Key;
    public static KeyBinding signatureKey;

    @Override
    public void onInitializeClient() {
        CLIENT_LOGGER.info("[Ascend Client] Initializing Ascend Combat & HUD (Phases 3, 4, 5)...");

        // 1. Client Packets
        AscendClientPackets.registerClientReceivers();

        // 2. HUD Renderer
        HudRenderCallback.EVENT.register(new AscendHudRenderer());

        // 3. Keybindings
        statusScreenKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ascend.status_screen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.ascend.general"
        ));

        dashKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ascend.dash",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                "category.ascend.general"
        ));

        skill1Key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ascend.skill1",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.ascend.general"
        ));

        skill2Key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ascend.skill2",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F,
                "category.ascend.general"
        ));

        signatureKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ascend.signature",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "category.ascend.general"
        ));

        // 4. Tick Listener for Key inputs
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Status Window
            while (statusScreenKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new StatusScreen());
                }
            }

            // Directional Dash / Dodge
            while (dashKey.wasPressed()) {
                if (client.currentScreen == null) {
                    float forward = client.player.input.movementForward;
                    float sideways = client.player.input.movementSideways;

                    double dirX = 0;
                    double dirZ = 0;

                    if (forward != 0 || sideways != 0) {
                        float yaw = client.player.getYaw();
                        double angle = Math.toRadians(yaw);
                        double forwardX = -Math.sin(angle);
                        double forwardZ = Math.cos(angle);
                        double rightX = Math.cos(angle);
                        double rightZ = Math.sin(angle);

                        dirX = (forwardX * forward) + (rightX * sideways);
                        dirZ = (forwardZ * forward) + (rightZ * sideways);

                        double len = Math.sqrt(dirX * dirX + dirZ * dirZ);
                        if (len > 0) {
                            dirX /= len;
                            dirZ /= len;
                        }
                    }
                    AscendClientPackets.sendDash(dirX, dirZ);
                }
            }

            // Skill 1
            while (skill1Key.wasPressed()) {
                if (client.currentScreen == null) {
                    AscendClientPackets.sendCastSkill(0);
                }
            }

            // Skill 2
            while (skill2Key.wasPressed()) {
                if (client.currentScreen == null) {
                    AscendClientPackets.sendCastSkill(1);
                }
            }

            // Signature Move
            while (signatureKey.wasPressed()) {
                if (client.currentScreen == null) {
                    AscendClientPackets.sendCastSkill(2);
                }
            }
        });

        CLIENT_LOGGER.info("[Ascend Client] Combat controls, skills, and HUD initialized.");
    }
}
