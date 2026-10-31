package net.ascend.mod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.ascend.mod.network.AscendPackets;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class AscendCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("ascend")
            .then(literal("awaken")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    AscendPackets.sendOpenCreationScreen(player);
                    ctx.getSource().sendFeedback(() -> Text.literal("§6[Ascend]§f Opening Character Awakening screen..."), false);
                    return 1;
                })
            )
            .then(literal("essence")
                .then(argument("amount", IntegerArgumentType.integer(1))
                    .executes(ctx -> {
                        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                        int amount = IntegerArgumentType.getInteger(ctx, "amount");
                        PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
                        data.addEssenceWithBonus(amount);
                        AscendPackets.syncPlayerData(player);
                        ctx.getSource().sendFeedback(() -> Text.literal("§6[Ascend]§f Added §b" + amount + "§f Essence. Total: §b" + data.getEssence()), false);
                        return 1;
                    })
                )
            )
            .then(literal("statpoints")
                .then(argument("amount", IntegerArgumentType.integer(1))
                    .executes(ctx -> {
                        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                        int amount = IntegerArgumentType.getInteger(ctx, "amount");
                        PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
                        data.addStatPoints(amount);
                        AscendPackets.syncPlayerData(player);
                        ctx.getSource().sendFeedback(() -> Text.literal("§6[Ascend]§f Added §a" + amount + "§f Stat Points. Total: §a" + data.getStatPoints()), false);
                        return 1;
                    })
                )
            )
            .then(literal("stat")
                .then(argument("name", StringArgumentType.word())
                    .then(argument("value", IntegerArgumentType.integer(0))
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                            String name = StringArgumentType.getString(ctx, "name").toLowerCase();
                            int value = IntegerArgumentType.getInteger(ctx, "value");
                            PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();

                            switch (name) {
                                case "strength" -> data.setStrength(value);
                                case "speed" -> data.setSpeed(value);
                                case "vitality" -> data.setVitality(value);
                                case "mana" -> data.setMana(value);
                                case "defense" -> data.setDefense(value);
                                default -> {
                                    ctx.getSource().sendError(Text.literal("Unknown stat: " + name + ". Valid: strength, speed, vitality, mana, defense"));
                                    return 0;
                                }
                            }
                            data.applyModifiers(player);
                            AscendPackets.syncPlayerData(player);
                            ctx.getSource().sendFeedback(() -> Text.literal("§6[Ascend]§f Stat §e" + name + "§f set to §e" + value), false);
                            return 1;
                        })
                    )
                )
            )
            .then(literal("reset")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
                    data.resetToDefaults();
                    data.applyModifiers(player);
                    AscendPackets.syncPlayerData(player);
                    AscendPackets.sendOpenCreationScreen(player);
                    ctx.getSource().sendFeedback(() -> Text.literal("§6[Ascend]§f Character stats reset. Opening Awakening flow..."), false);
                    return 1;
                })
            )
        );
    }
}
