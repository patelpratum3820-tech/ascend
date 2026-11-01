package net.ascend.mod.network;

import net.ascend.mod.client.gui.CharacterCreationScreen;
import net.ascend.mod.client.gui.StatusScreen;
import net.ascend.mod.data.IPlayerDataAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

import java.util.List;

@Environment(EnvType.CLIENT)
public class AscendClientPackets {
    public static void registerClientReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(AscendPackets.SYNC_PLAYER_DATA_ID, (client, handler, buf, responseSender) -> {
            PacketByteBuf copiedBuf = PacketByteBufs.copy(buf);
            client.execute(() -> {
                if (client.player != null) {
                    ((IPlayerDataAccessor) client.player).ascend$getPlayerData().readFromBuffer(copiedBuf);
                    if (client.currentScreen instanceof StatusScreen statusScreen) {
                        statusScreen.refreshData();
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(AscendPackets.OPEN_CREATION_ID, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                client.setScreen(new CharacterCreationScreen());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(AscendPackets.OPEN_SKILL_UNLOCK_ID, (client, handler, buf, responseSender) -> {
            String rankName = buf.readString();
            boolean isMainHand = buf.readBoolean();
            client.execute(() -> {
                net.ascend.mod.skills.SkillRank rank = net.ascend.mod.skills.SkillRank.valueOf(rankName);
                client.setScreen(new net.ascend.mod.client.gui.SkillUnlockScreen(rank, isMainHand));
            });
        });
    }

    public static void sendLearnSkill(String skillId, boolean isMainHand) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(skillId);
        buf.writeBoolean(isMainHand);
        ClientPlayNetworking.send(AscendPackets.LEARN_SKILL_ID, buf);
    }

    public static void sendStatUpgradeRequest(String statName) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(statName);
        ClientPlayNetworking.send(AscendPackets.UPGRADE_STAT_ID, buf);
    }

    public static void sendCompleteCreation(String difficulty, String race, String playerClass, List<String> skills) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(difficulty);
        buf.writeString(race);
        buf.writeString(playerClass);
        buf.writeInt(skills.size());
        for (String s : skills) {
            buf.writeString(s);
        }
        ClientPlayNetworking.send(AscendPackets.COMPLETE_CREATION_ID, buf);
    }

    public static void sendDash(double dirX, double dirZ) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeDouble(dirX);
        buf.writeDouble(dirZ);
        ClientPlayNetworking.send(AscendPackets.DASH_ID, buf);
    }

    public static void sendCastSkill(int slot) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(slot);
        ClientPlayNetworking.send(AscendPackets.CAST_SKILL_ID, buf);
    }
}
