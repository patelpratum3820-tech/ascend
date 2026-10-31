package net.ascend.mod.network;

import net.ascend.mod.client.gui.CharacterCreationScreen;
import net.ascend.mod.client.gui.StatusScreen;
import net.ascend.mod.data.IPlayerDataAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
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
}
