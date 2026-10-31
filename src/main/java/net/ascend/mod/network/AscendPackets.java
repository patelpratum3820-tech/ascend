package net.ascend.mod.network;

import net.ascend.mod.AscendMod;
import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class AscendPackets {
    public static final Identifier SYNC_PLAYER_DATA_ID = new Identifier(AscendMod.MOD_ID, "sync_player_data");
    public static final Identifier UPGRADE_STAT_ID = new Identifier(AscendMod.MOD_ID, "upgrade_stat");
    public static final Identifier OPEN_CREATION_ID = new Identifier(AscendMod.MOD_ID, "open_creation");
    public static final Identifier COMPLETE_CREATION_ID = new Identifier(AscendMod.MOD_ID, "complete_creation");

    public static void registerServerReceivers() {
        // Handle stat upgrade request from client
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

        // Handle character creation awakening completion
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
                AscendMod.LOGGER.info("[Ascend] Player {} completed awakening as {} {} ({})",
                        player.getName().getString(), race, playerClass, difficulty);
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
}
