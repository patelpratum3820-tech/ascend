package net.ascend.mod.network;

import net.ascend.mod.AscendMod;
import net.ascend.mod.config.AscendConfig;
import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.ascend.mod.skills.AscendSkills;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class AscendPackets {
    public static final Identifier SYNC_PLAYER_DATA_ID = new Identifier(AscendMod.MOD_ID, "sync_player_data");
    public static final Identifier UPGRADE_STAT_ID = new Identifier(AscendMod.MOD_ID, "upgrade_stat");
    public static final Identifier OPEN_CREATION_ID = new Identifier(AscendMod.MOD_ID, "open_creation");
    public static final Identifier COMPLETE_CREATION_ID = new Identifier(AscendMod.MOD_ID, "complete_creation");
    public static final Identifier DASH_ID = new Identifier(AscendMod.MOD_ID, "dash");
    public static final Identifier CAST_SKILL_ID = new Identifier(AscendMod.MOD_ID, "cast_skill");
    public static final Identifier OPEN_SKILL_UNLOCK_ID = new Identifier(AscendMod.MOD_ID, "open_skill_unlock");
    public static final Identifier LEARN_SKILL_ID = new Identifier(AscendMod.MOD_ID, "learn_skill");

    public static void registerServerReceivers() {
        // 1. Upgrade Stat
        ServerPlayNetworking.registerGlobalReceiver(UPGRADE_STAT_ID, (server, player, handler, buf, responseSender) -> {
            String statName = buf.readString();
            server.execute(() -> {
                PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
                boolean success = data.upgradeStat(statName);
                if (success) {
                    data.applyModifiers(player);
                    syncPlayerData(player);
                }
            });
        });

        // 2. Complete Awakening
        ServerPlayNetworking.registerGlobalReceiver(COMPLETE_CREATION_ID, (server, player, handler, buf, responseSender) -> {
            String difficulty = buf.readString();
            String race = buf.readString();
            String playerClass = buf.readString();
            int skillCount = buf.readInt();
            List<String> skills = new ArrayList<>();
            for (int i = 0; i < skillCount; i++) {
                skills.add(buf.readString());
            }

            server.execute(() -> {
                PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
                data.initializeAwakening(difficulty, race, playerClass, skills);
                data.applyModifiers(player);
                syncPlayerData(player);
                net.ascend.mod.story.DivineAwakening.completeAwakening(player, data);
            });
        });

        // 3. Directional Dash
        ServerPlayNetworking.registerGlobalReceiver(DASH_ID, (server, player, handler, buf, responseSender) -> {
            double dirX = buf.readDouble();
            double dirZ = buf.readDouble();

            server.execute(() -> {
                PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
                if (data.canDash()) {
                    data.triggerDash();

                    double impulse = AscendConfig.INSTANCE.dashImpulse;
                    Vec3d look = player.getRotationVector();
                    double vx = dirX != 0 || dirZ != 0 ? dirX * impulse : look.x * impulse;
                    double vz = dirX != 0 || dirZ != 0 ? dirZ * impulse : look.z * impulse;

                    player.setVelocity(vx, 0.25, vz);
                    player.velocityModified = true;

                    player.getServerWorld().spawnParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.2, player.getZ(), 18, 0.4, 0.1, 0.4, 0.05);
                    player.getServerWorld().spawnParticles(ParticleTypes.SWEEP_ATTACK, player.getX(), player.getY() + 0.5, player.getZ(), 1, 0, 0, 0, 0);
                    player.getServerWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0f, 1.6f);

                    syncPlayerData(player);
                }
            });
        });

        // 4. Cast Skill / Signature
        ServerPlayNetworking.registerGlobalReceiver(CAST_SKILL_ID, (server, player, handler, buf, responseSender) -> {
            int slot = buf.readInt(); // 0 = Skill 1, 1 = Skill 2, 2 = Skill 3, 3 = Skill 4, 4 = Signature

            server.execute(() -> {
                PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
                if (slot == 4) {
                    if (AscendSkills.executeSignatureMove(player, data)) {
                        syncPlayerData(player);
                    }
                } else if (slot >= 0 && slot < 4) {
                    if (slot >= data.getMaxUnlockedSkillSlots()) {
                        player.sendMessage(net.minecraft.text.Text.literal("§c[Ascend] Skill Slot " + (slot + 1) + " is locked! Increase your Rank to unlock."), true);
                        return;
                    }
                    if (slot < data.getSelectedSkills().size()) {
                        String skillId = data.getSelectedSkills().get(slot);
                        if (AscendSkills.executeSkill(slot, skillId, player, data)) {
                            syncPlayerData(player);
                        }
                    }
                }
            });
        });

        // 5. Learn Skill from Scroll/Tome
        ServerPlayNetworking.registerGlobalReceiver(LEARN_SKILL_ID, (server, player, handler, buf, responseSender) -> {
            String skillId = buf.readString();
            boolean isMainHand = buf.readBoolean();

            server.execute(() -> {
                ItemStack stack = isMainHand ? player.getMainHandStack() : player.getOffHandStack();
                if (stack.getItem() instanceof net.ascend.mod.item.SkillScrollItem || stack.getItem() instanceof net.ascend.mod.item.SkillTomeItem) {
                    PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
                    if (!data.hasUnlockedSkill(skillId)) {
                        data.unlockSkill(skillId);
                        stack.decrement(1);
                        syncPlayerData(player);

                        player.getServerWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 1.5f, 1.0f);
                        player.getServerWorld().spawnParticles(ParticleTypes.ENCHANTED_HIT, player.getX(), player.getY() + 1, player.getZ(), 40, 0.5, 0.5, 0.5, 0.2);

                        net.ascend.mod.skills.SkillInfo info = net.ascend.mod.skills.SkillInfo.getById(skillId);
                        player.sendMessage(Text.literal("§6§l[SKILL MASTERED] §fYou engraved §a" + info.displayName() + " §finto your soul!"), false);
                    }
                }
            });
        });
    }

    public static void syncPlayerData(ServerPlayerEntity player) {
        PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
        PacketByteBuf buf = PacketByteBufs.create();
        data.writeToBuffer(buf);
        ServerPlayNetworking.send(player, SYNC_PLAYER_DATA_ID, buf);
    }

    public static void sendOpenCreationScreen(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, OPEN_CREATION_ID, buf);
    }

    public static void sendOpenSkillUnlock(ServerPlayerEntity player, String rankName, boolean isMainHand) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(rankName);
        buf.writeBoolean(isMainHand);
        ServerPlayNetworking.send(player, OPEN_SKILL_UNLOCK_ID, buf);
    }
}
