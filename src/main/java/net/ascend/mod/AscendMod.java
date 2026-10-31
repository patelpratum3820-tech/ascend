package net.ascend.mod;

import net.ascend.mod.command.AscendCommands;
import net.ascend.mod.config.AscendConfig;
import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.ascend.mod.network.AscendPackets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AscendMod implements ModInitializer {
    public static final String MOD_ID = "ascend";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[Ascend] Initializing Ascend Anime RPG Mod (Phase 2: Character Creation)...");

        // 1. Config
        AscendConfig.load();

        // 2. Networking
        AscendPackets.registerServerReceivers();

        // 3. Commands
        CommandRegistrationCallback.EVENT.register(AscendCommands::register);

        // 4. Player Lifecycle: On Join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PlayerData data = ((IPlayerDataAccessor) handler.player).ascend$getPlayerData();
            data.applyModifiers(handler.player);
            AscendPackets.syncPlayerData(handler.player);

            // If player has not awakened yet, open character creation wizard
            if (!data.isInitialized()) {
                AscendPackets.sendOpenCreationScreen(handler.player);
            }
        });

        // 5. Player Lifecycle: On Respawn (copy data from original player)
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            PlayerData oldData = ((IPlayerDataAccessor) oldPlayer).ascend$getPlayerData();
            PlayerData newData = ((IPlayerDataAccessor) newPlayer).ascend$getPlayerData();
            net.minecraft.nbt.NbtCompound tempNbt = new net.minecraft.nbt.NbtCompound();
            oldData.writeToNbt(tempNbt);
            newData.readFromNbt(tempNbt);
            newData.applyModifiers(newPlayer);
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            AscendPackets.syncPlayerData(newPlayer);
        });

        // 6. Player Lifecycle: Dimension Change
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
            data.applyModifiers(player);
            AscendPackets.syncPlayerData(player);
        });

        LOGGER.info("[Ascend] Phase 2 systems loaded. Ready for adventurer awakening!");
    }
}
