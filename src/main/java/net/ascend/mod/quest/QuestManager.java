package net.ascend.mod.quest;

import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.ascend.mod.item.AscendItems;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class QuestManager {
    public record Quest(String title, String description, int targetAmount, long essenceReward) {}

    public static final Quest[] QUESTS = new Quest[]{
        new Quest("First Steps of an Adventurer", "Slay 5 monsters in the wilderness.", 5, 100),
        new Quest("Crystal Collector", "Mine 3 Mana Crystals from cave depths.", 3, 200),
        new Quest("Dungeon Gate Breaker", "Conquer and clear your first Ranked Dungeon.", 1, 500),
        new Quest("Ascending Ranks", "Reach Level 10 and achieve Rank D status.", 10, 800),
        new Quest("Dragon's Nemesis", "Defeat an Apex Boss in their arena.", 1, 2000)
    };

    public static void displayQuestProgress(ServerPlayerEntity player) {
        PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
        int questIndex = Math.min(data.getLevel() / 5, QUESTS.length - 1);
        Quest q = QUESTS[questIndex];

        player.sendMessage(Text.literal("§6§l[GUILD QUEST] §e" + q.title()), false);
        player.sendMessage(Text.literal("§7Objective: §f" + q.description()), false);
        player.sendMessage(Text.literal("§7Reward: §b+" + q.essenceReward() + " Essence§7, §a1x Dungeon Map"), false);
    }

    public static void completeCurrentQuest(ServerPlayerEntity player) {
        PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
        int questIndex = Math.min(data.getLevel() / 5, QUESTS.length - 1);
        Quest q = QUESTS[questIndex];

        data.addEssenceWithBonus(q.essenceReward());
        data.addStatPoints(1);
        player.giveItemStack(new ItemStack(AscendItems.DUNGEON_MAP, 1));

        player.getServerWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 1.0f, 1.0f);
        player.sendMessage(Text.literal("§a§l[QUEST COMPLETE] §fYou received §b+" + q.essenceReward() + " Essence§f, §a+1 Stat Point§f, and a §eDungeon Map§f!"), false);
    }
}

