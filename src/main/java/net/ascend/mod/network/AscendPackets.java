package net.ascend.mod.network;

import net.ascend.mod.AscendMod;
import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class AscendPackets {
    public static final Identifier SYNC_PLAYER_DATA_ID = new Identifier(AscendMod.MOD_ID, "sync_player_data");
    public static final Identifier UPGRADE_STAT_ID = new Identifier(AscendMod.MOD_ID, "upgrade_stat");

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
    }

    public static void syncPlayerData(ServerPlayerEntity player) {
        PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
        PacketByteBuf buf = PacketByteBufs.create();
        data.writeToBuffer(buf);
        ServerPlayNetworking.send(player, SYNC_PLAYER_DATA_ID, buf);
    }
}
