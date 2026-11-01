package net.ascend.mod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.ascend.mod.dungeon.DungeonManager;
import net.ascend.mod.network.AscendPackets;
import net.ascend.mod.quest.QuestManager;
import net.ascend.mod.skills.SkillInfo;
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
            // 1. Help Guide
            .then(literal("help")
                .executes(ctx -> {
                    ServerCommandSource src = ctx.getSource();
                    src.sendFeedback(() -> Text.literal("§6§l========== ASCEND RPG COMMANDS =========="), false);
                    src.sendFeedback(() -> Text.literal("§e/ascend race <Human|Dragonoid|Slime> §7- Change race"), false);
                    src.sendFeedback(() -> Text.literal("§e/ascend class <Swordsman|Summoner|Mage|Archer|Tank> §7- Change class"), false);
                    src.sendFeedback(() -> Text.literal("§e/ascend difficulty <Easy|Normal|Hard|Nightmare> §7- Change difficulty"), false);
                    src.sendFeedback(() -> Text.literal("§e/ascend level <amount> §7- Set player level & rank"), false);
                    src.sendFeedback(() -> Text.literal("§e/ascend essence <amount> §7- Give Essence (mod XP)"), false);
                    src.sendFeedback(() -> Text.literal("§e/ascend statpoints <amount> §7- Give Stat Points"), false);
                    src.sendFeedback(() -> Text.literal("§e/ascend stat <strength|speed|vitality|mana|defense> <val> §7- Set stat"), false);
                    src.sendFeedback(() -> Text.literal("§e/ascend skill <add|list> [skill_id] §7- Manage equipped skills"), false);
                    src.sendFeedback(() -> Text.literal("§e/ascend awaken §7- Open character creation wizard"), false);
                    src.sendFeedback(() -> Text.literal("§e/ascend dungeon §7- Manifest a Ranked Dungeon"), false);
                    src.sendFeedback(() -> Text.literal("§e/ascend quest §7- View active guild quest"), false);
                    src.sendFeedback(() -> Text.literal("§e/ascend completequest §7- Complete active quest"), false);
                    src.sendFeedback(() -> Text.literal("§e/ascend reset §7- Reset character to defaults"), false);
                    src.sendFeedback(() -> Text.literal("§6§l========================================="), false);
                    return 1;
                })
            )

            // 2. Change Race
            .then(literal("race")
                .then(argument("name", StringArgumentType.word())
                    .suggests((c, b) -> {
                        b.suggest("Human");
                        b.suggest("Dragonoid");
                        b.suggest("Slime");
                        return b.buildFuture();
                    })
                    .executes(ctx -> {
                        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                        String name = StringArgumentType.getString(ctx, "name");
                        String formatted = Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();

                        if (!formatted.equals("Human") && !formatted.equals("Dragonoid") && !formatted.equals("Slime")) {
                            ctx.getSource().sendError(Text.literal("Invalid race! Valid choices: Human, Dragonoid, Slime"));
                            return 0;
                        }

                        PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
                        data.setRace(formatted);
                        data.applyModifiers(player);
                        AscendPackets.syncPlayerData(player);

                        ctx.getSource().sendFeedback(() -> Text.literal("§6[Ascend]§f Race successfully changed to §b" + formatted + "§f!"), false);
                        return 1;
                    })
                )
            )

            // 3. Change Class
            .then(literal("class")
                .then(argument("name", StringArgumentType.word())
                    .suggests((c, b) -> {
                        b.suggest("Swordsman");
                        b.suggest("Summoner");
                        b.suggest("Mage");
                        b.suggest("Archer");
                        b.suggest("Tank");
                        return b.buildFuture();
                    })
                    .executes(ctx -> {
                        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                        String name = StringArgumentType.getString(ctx, "name");
                        String formatted = Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();

                        if (!formatted.equals("Swordsman") && !formatted.equals("Summoner") && 
                            !formatted.equals("Mage") && !formatted.equals("Archer") && !formatted.equals("Tank")) {
                            ctx.getSource().sendError(Text.literal("Invalid class! Valid choices: Swordsman, Summoner, Mage, Archer, Tank"));
                            return 0;
                        }

                        PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
                        data.setPlayerClass(formatted);
                        data.applyModifiers(player);
                        AscendPackets.syncPlayerData(player);

                        ctx.getSource().sendFeedback(() -> Text.literal("§6[Ascend]§f Class successfully changed to §e" + formatted + "§f!"), false);
                        return 1;
                    })
                )
            )

            // 4. Change Difficulty
            .then(literal("difficulty")
                .then(argument("level", StringArgumentType.word())
                    .suggests((c, b) -> {
                        b.suggest("Easy");
                        b.suggest("Normal");
                        b.suggest("Hard");
                        b.suggest("Nightmare");
                        return b.buildFuture();
                    })
                    .executes(ctx -> {
                        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                        String diff = StringArgumentType.getString(ctx, "level");
                        String formatted = Character.toUpperCase(diff.charAt(0)) + diff.substring(1).toLowerCase();

                        PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
                        data.setDifficulty(formatted);
                        AscendPackets.syncPlayerData(player);

                        ctx.getSource().sendFeedback(() -> Text.literal("§6[Ascend]§f World difficulty scaling set to §c" + formatted + "§f!"), false);
                        return 1;
                    })
                )
            )

            // 5. Change Level & Rank
            .then(literal("level")
                .then(argument("value", IntegerArgumentType.integer(1, 1000))
                    .executes(ctx -> {
                        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                        int lvl = IntegerArgumentType.getInteger(ctx, "value");
                        PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
                        data.setLevel(lvl);
                        AscendPackets.syncPlayerData(player);

                        ctx.getSource().sendFeedback(() -> Text.literal("§6[Ascend]§f Level set to §e" + lvl + "§f (Rank §6§l" + data.getRank() + "§f)!"), false);
                        return 1;
                    })
                )
            )

            // 6. Manage Skills
            .then(literal("skill")
                .then(literal("list")
                    .executes(ctx -> {
                        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                        PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
                        ctx.getSource().sendFeedback(() -> Text.literal("§6[Ascend Equipped Skills]§f (" + data.getSelectedSkills().size() + "/" + data.getMaxUnlockedSkillSlots() + "): " + String.join(", ", data.getSelectedSkills())), false);
                        ctx.getSource().sendFeedback(() -> Text.literal("§dUnlocked Skills (" + data.getUnlockedSkills().size() + "): §7" + String.join(", ", data.getUnlockedSkills())), false);
                        return 1;
                    })
                )
                .then(literal("unlock")
                    .then(argument("skillId", StringArgumentType.word())
                        .suggests((c, b) -> {
                            for (SkillInfo s : SkillInfo.ALL_SKILLS) {
                                b.suggest(s.id());
                            }
                            return b.buildFuture();
                        })
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                            String skillId = StringArgumentType.getString(ctx, "skillId").toLowerCase();
                            PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
                            data.unlockSkill(skillId);
                            AscendPackets.syncPlayerData(player);
                            SkillInfo info = SkillInfo.getById(skillId);
                            ctx.getSource().sendFeedback(() -> Text.literal("§6[Ascend]§f Unlocked " + info.rank().getFormatted() + " §f" + info.displayName()), false);
                            return 1;
                        })
                    )
                )
                .then(literal("unlockall")
                    .executes(ctx -> {
                        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                        PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
                        for (SkillInfo s : SkillInfo.ALL_SKILLS) {
                            data.unlockSkill(s.id());
                        }
                        AscendPackets.syncPlayerData(player);
                        ctx.getSource().sendFeedback(() -> Text.literal("§6[Ascend]§f Unlocked all " + SkillInfo.ALL_SKILLS.size() + " RPG skills!"), false);
                        return 1;
                    })
                )
                .then(literal("add")
                    .then(argument("skillId", StringArgumentType.word())
                        .suggests((c, b) -> {
                            for (SkillInfo s : SkillInfo.ALL_SKILLS) {
                                b.suggest(s.id());
                            }
                            return b.buildFuture();
                        })
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                            String skillId = StringArgumentType.getString(ctx, "skillId").toLowerCase();
                            PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();

                            if (data.getSelectedSkills().contains(skillId)) {
                                ctx.getSource().sendError(Text.literal("You already have " + skillId + " equipped!"));
                                return 0;
                            }

                            if (!data.canEquipSkill(skillId)) {
                                ctx.getSource().sendError(Text.literal("Cannot equip " + skillId + "! Cap: 1 Rank S and 1 Rank SS equipped max."));
                                return 0;
                            }

                            int maxSlots = data.getMaxUnlockedSkillSlots();
                            if (data.getSelectedSkills().size() >= maxSlots) {
                                data.getSelectedSkills().remove(0); // replace first slot
                            }
                            data.getSelectedSkills().add(skillId);
                            AscendPackets.syncPlayerData(player);

                            SkillInfo info = SkillInfo.getById(skillId);
                            ctx.getSource().sendFeedback(() -> Text.literal("§6[Ascend]§f Equipped " + info.rank().getFormatted() + " §f" + info.displayName()), false);
                            return 1;
                        })
                    )
                )
            )

            // Scroll & Tome commands
            .then(literal("scroll")
                .then(argument("rank", StringArgumentType.word())
                    .suggests((c, b) -> {
                        b.suggest("E"); b.suggest("D"); b.suggest("C");
                        return b.buildFuture();
                    })
                    .executes(ctx -> {
                        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                        String r = StringArgumentType.getString(ctx, "rank").toUpperCase();
                        net.minecraft.item.Item item = switch (r) {
                            case "D" -> net.ascend.mod.item.AscendItems.SKILL_SCROLL_D;
                            case "C" -> net.ascend.mod.item.AscendItems.SKILL_SCROLL_C;
                            default -> net.ascend.mod.item.AscendItems.SKILL_SCROLL_E;
                        };
                        player.giveItemStack(new net.minecraft.item.ItemStack(item));
                        ctx.getSource().sendFeedback(() -> Text.literal("§6[Ascend]§f Gave Skill Scroll (" + r + ")"), false);
                        return 1;
                    })
                )
            )
            .then(literal("tome")
                .then(argument("rank", StringArgumentType.word())
                    .suggests((c, b) -> {
                        b.suggest("B"); b.suggest("A"); b.suggest("S"); b.suggest("SS");
                        return b.buildFuture();
                    })
                    .executes(ctx -> {
                        ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                        String r = StringArgumentType.getString(ctx, "rank").toUpperCase();
                        net.minecraft.item.Item item = switch (r) {
                            case "A" -> net.ascend.mod.item.AscendItems.SKILL_TOME_A;
                            case "S" -> net.ascend.mod.item.AscendItems.SKILL_TOME_S;
                            case "SS" -> net.ascend.mod.item.AscendItems.SKILL_TOME_SS;
                            default -> net.ascend.mod.item.AscendItems.SKILL_TOME_B;
                        };
                        player.giveItemStack(new net.minecraft.item.ItemStack(item));
                        ctx.getSource().sendFeedback(() -> Text.literal("§6[Ascend]§f Gave Skill Tome (" + r + ")"), false);
                        return 1;
                    })
                )
            )

            // 7. Awaken / Reset
            .then(literal("awaken")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    AscendPackets.sendOpenCreationScreen(player);
                    ctx.getSource().sendFeedback(() -> Text.literal("§6[Ascend]§f Opening Character Awakening screen..."), false);
                    return 1;
                })
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

            // 8. Quests & Dungeons
            .then(literal("quest")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    QuestManager.displayQuestProgress(player);
                    return 1;
                })
            )
            .then(literal("completequest")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    QuestManager.completeCurrentQuest(player);
                    return 1;
                })
            )
            .then(literal("dungeon")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
                    DungeonManager.DungeonRank rank = switch (data.getRank()) {
                        case "S" -> DungeonManager.DungeonRank.S;
                        case "A" -> DungeonManager.DungeonRank.A;
                        case "B" -> DungeonManager.DungeonRank.B;
                        case "C" -> DungeonManager.DungeonRank.C;
                        case "D" -> DungeonManager.DungeonRank.D;
                        default -> DungeonManager.DungeonRank.E;
                    };
                    DungeonManager.generateDungeonAt(player.getServerWorld(), player.getBlockPos().add(20, 0, 20), rank, player);
                    return 1;
                })
            )

            // 9. Stats & Essence
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
        );
    }
}
