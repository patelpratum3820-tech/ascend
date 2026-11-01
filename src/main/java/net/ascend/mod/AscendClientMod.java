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
    public static KeyBinding skill3Key;
    public static KeyBinding skill4Key;
    public static KeyBinding signatureKey;
    public static KeyBinding rpgCameraKey;
    public static KeyBinding grimoireKey;
    public static KeyBinding questJournalKey;
    public static KeyBinding dungeonRankKey;

    @Override
    public void onInitializeClient() {
        CLIENT_LOGGER.info("[Ascend Client] Initializing Ascend Combat & HUD (Phases 3, 4, 5)...");

        // 1. Client Packets
        AscendClientPackets.registerClientReceivers();

        // 2. HUD Renderer
        HudRenderCallback.EVENT.register(new AscendHudRenderer());

        // Boss Entity Renderers
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                net.ascend.mod.entity.AscendEntities.GOBLIN_KING,
                net.ascend.mod.client.render.boss.AscendBossRenderers.GoblinKingRenderer::new
        );
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                net.ascend.mod.entity.AscendEntities.SHADOW_KNIGHT,
                net.ascend.mod.client.render.boss.AscendBossRenderers.ShadowKnightRenderer::new
        );
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                net.ascend.mod.entity.AscendEntities.CRYSTAL_GOLEM,
                net.ascend.mod.client.render.boss.AscendBossRenderers.CrystalGolemRenderer::new
        );
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                net.ascend.mod.entity.AscendEntities.VOID_LICH,
                net.ascend.mod.client.render.boss.AscendBossRenderers.VoidLichRenderer::new
        );

        // Step W5 Mob Renderers
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                net.ascend.mod.entity.AscendEntities.SPIRIT_FOX,
                net.ascend.mod.client.render.mob.AscendMobRenderers.SpiritFoxRenderer::new
        );
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                net.ascend.mod.entity.AscendEntities.MANA_DEER,
                net.ascend.mod.client.render.mob.AscendMobRenderers.ManaDeerRenderer::new
        );
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                net.ascend.mod.entity.AscendEntities.SLIME_PUP,
                net.ascend.mod.client.render.mob.AscendMobRenderers.SlimePupRenderer::new
        );
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                net.ascend.mod.entity.AscendEntities.MOON_MOTH,
                net.ascend.mod.client.render.mob.AscendMobRenderers.MoonMothRenderer::new
        );
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                net.ascend.mod.entity.AscendEntities.GOBLIN,
                net.ascend.mod.client.render.mob.AscendMobRenderers.GoblinRenderer::new
        );
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                net.ascend.mod.entity.AscendEntities.ORC_WARRIOR,
                net.ascend.mod.client.render.mob.AscendMobRenderers.OrcWarriorRenderer::new
        );
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                net.ascend.mod.entity.AscendEntities.SHADOW_WRAITH,
                net.ascend.mod.client.render.mob.AscendMobRenderers.ShadowWraithRenderer::new
        );
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                net.ascend.mod.entity.AscendEntities.SKELETON_KNIGHT,
                net.ascend.mod.client.render.mob.AscendMobRenderers.SkeletonKnightRenderer::new
        );
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                net.ascend.mod.entity.AscendEntities.DARK_MAGE,
                net.ascend.mod.client.render.mob.AscendMobRenderers.DarkMageRenderer::new
        );
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                net.ascend.mod.entity.AscendEntities.FIRE_DRAKE,
                net.ascend.mod.client.render.mob.AscendMobRenderers.FireDrakeRenderer::new
        );
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
                net.ascend.mod.entity.AscendEntities.DUNGEON_GOLEM,
                net.ascend.mod.client.render.mob.AscendMobRenderers.DungeonGolemRenderer::new
        );

        // Step R2 Zombie Renderers
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(net.ascend.mod.entity.AscendEntities.ZOMBIE_SOLDIER, net.ascend.mod.client.render.mob.AscendMobRenderers.AscendZombieRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(net.ascend.mod.entity.AscendEntities.ZOMBIE_BRUTE, net.ascend.mod.client.render.mob.AscendMobRenderers.AscendZombieRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(net.ascend.mod.entity.AscendEntities.ZOMBIE_BERSERKER, net.ascend.mod.client.render.mob.AscendMobRenderers.AscendZombieRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(net.ascend.mod.entity.AscendEntities.ZOMBIE_SPEARMAN, net.ascend.mod.client.render.mob.AscendMobRenderers.AscendZombieRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(net.ascend.mod.entity.AscendEntities.PLAGUE_ZOMBIE, net.ascend.mod.client.render.mob.AscendMobRenderers.AscendZombieRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(net.ascend.mod.entity.AscendEntities.ZOMBIE_NECROMANCER, net.ascend.mod.client.render.mob.AscendMobRenderers.AscendZombieRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(net.ascend.mod.entity.AscendEntities.ZOMBIE_LORD, net.ascend.mod.client.render.mob.AscendMobRenderers.AscendZombieRenderer::new);

        // Step R2 Skeleton Renderers
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(net.ascend.mod.entity.AscendEntities.SKELETON_ARCHER, net.ascend.mod.client.render.mob.AscendMobRenderers.AscendSkeletonRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(net.ascend.mod.entity.AscendEntities.SKELETON_CROSSBOWMAN, net.ascend.mod.client.render.mob.AscendMobRenderers.AscendSkeletonRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(net.ascend.mod.entity.AscendEntities.SKELETON_SWORDSMAN, net.ascend.mod.client.render.mob.AscendMobRenderers.AscendSkeletonRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(net.ascend.mod.entity.AscendEntities.BONE_KNIGHT, net.ascend.mod.client.render.mob.AscendMobRenderers.AscendSkeletonRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(net.ascend.mod.entity.AscendEntities.SKELETON_MAGE, net.ascend.mod.client.render.mob.AscendMobRenderers.AscendSkeletonRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(net.ascend.mod.entity.AscendEntities.BONE_REAPER, net.ascend.mod.client.render.mob.AscendMobRenderers.AscendSkeletonRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(net.ascend.mod.entity.AscendEntities.SKELETON_COMMANDER, net.ascend.mod.client.render.mob.AscendMobRenderers.AscendSkeletonRenderer::new);

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

        skill3Key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ascend.skill3",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                "category.ascend.general"
        ));

        skill4Key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ascend.skill4",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                "category.ascend.general"
        ));

        signatureKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ascend.signature",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "category.ascend.general"
        ));

        rpgCameraKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ascend.rpg_camera",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.ascend.general"
        ));

        grimoireKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ascend.grimoire",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.ascend.general"
        ));

        questJournalKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ascend.quest_journal",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                "category.ascend.general"
        ));

        dungeonRankKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ascend.dungeon_rank",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_U,
                "category.ascend.general"
        ));

        // 4. Tick Listener for Key inputs
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Quest Journal Window [J]
            while (questJournalKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new net.ascend.mod.client.gui.QuestJournalScreen());
                }
            }

            // Dungeon & Rank Ladder Window [U]
            while (dungeonRankKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new net.ascend.mod.client.gui.DungeonRankScreen());
                }
            }

            // Grimoire / Skills Window [H]
            while (grimoireKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new net.ascend.mod.client.gui.SkillManagerScreen());
                }
            }

            // RPG Camera Toggle (1-click first-person <-> 3rd-person RPG view)
            while (rpgCameraKey.wasPressed()) {
                if (client.currentScreen == null) {
                    if (client.options.getPerspective().isFirstPerson()) {
                        client.options.setPerspective(net.minecraft.client.option.Perspective.THIRD_PERSON_BACK);
                        client.player.sendMessage(net.minecraft.text.Text.literal("§6⚔ [Ascend] §eSwitched to RPG 3rd-Person Camera"), true);
                    } else {
                        client.options.setPerspective(net.minecraft.client.option.Perspective.FIRST_PERSON);
                        client.player.sendMessage(net.minecraft.text.Text.literal("§6⚔ [Ascend] §eSwitched to First-Person View"), true);
                    }
                }
            }

            // Status Window
            while (statusScreenKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new StatusScreen());
                }
            }

            // Directional Dash / Dodge with Dynamic Visual Animation
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

                    // Client dash particles / anime speed lines
                    if (client.world != null) {
                        for (int i = 0; i < 12; i++) {
                            double px = client.player.getX() + (client.world.random.nextDouble() - 0.5) * 1.2;
                            double py = client.player.getY() + client.world.random.nextDouble() * 1.5;
                            double pz = client.player.getZ() + (client.world.random.nextDouble() - 0.5) * 1.2;
                            client.world.addParticle(net.minecraft.particle.ParticleTypes.CLOUD, px, py, pz, -dirX * 0.3, 0.05, -dirZ * 0.3);
                        }
                    }

                    AscendClientPackets.sendDash(dirX, dirZ);
                }
            }

            // Skill 1 [R]
            while (skill1Key.wasPressed()) {
                if (client.currentScreen == null) {
                    AscendClientPackets.sendCastSkill(0);
                }
            }

            // Skill 2 [F]
            while (skill2Key.wasPressed()) {
                if (client.currentScreen == null) {
                    AscendClientPackets.sendCastSkill(1);
                }
            }

            // Skill 3 [C]
            while (skill3Key.wasPressed()) {
                if (client.currentScreen == null) {
                    AscendClientPackets.sendCastSkill(2);
                }
            }

            // Skill 4 [X]
            while (skill4Key.wasPressed()) {
                if (client.currentScreen == null) {
                    AscendClientPackets.sendCastSkill(3);
                }
            }

            // Signature Move [V]
            while (signatureKey.wasPressed()) {
                if (client.currentScreen == null) {
                    AscendClientPackets.sendCastSkill(4);
                }
            }
        });

        CLIENT_LOGGER.info("[Ascend Client] Combat controls, skills, and HUD initialized.");
    }
}
