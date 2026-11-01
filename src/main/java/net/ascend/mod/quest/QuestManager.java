package net.ascend.mod.quest;

import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.ascend.mod.item.AscendItems;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class QuestManager {
    public enum QuestCategory {
        MAIN("Main Story", "§6"),
        SIDE("Side Bounties", "§b"),
        DAILY("Daily Trials", "§a");

        private final String title;
        private final String color;

        QuestCategory(String title, String color) {
            this.title = title;
            this.color = color;
        }

        public String getTitle() { return title; }
        public String getColor() { return color; }
    }

    public record QuestDefinition(
            String id,
            String title,
            QuestCategory category,
            String description,
            int targetAmount,
            long essenceReward,
            int statPointReward,
            String rewardItemName,
            int recommendedLevel
    ) {}

    // Registry of all structured quests
    public static final List<QuestDefinition> ALL_QUESTS = List.of(
            // Main Story Quests
            new QuestDefinition("main_1_reborn", "Awakening of the Chosen", QuestCategory.MAIN, "Acclimate to this world and reach Character Level 5.", 5, 300, 1, "Bread & Supplies", 1),
            new QuestDefinition("main_2_first_dungeon", "Conquer the Goblin Caves", QuestCategory.MAIN, "Descend into a Rank E Dungeon and defeat the cave chieftain.", 1, 600, 2, "Heart of the Lake", 10),
            new QuestDefinition("main_3_forest_sanctuary", "The Whispering Sanctum", QuestCategory.MAIN, "Venture into the ancient woods and clear a Rank D Dungeon.", 1, 1200, 3, "Ancient Forest Seed", 25),
            new QuestDefinition("main_4_draconic_ravine", "Wrath of the Wyrm", QuestCategory.MAIN, "Survive the depths of a Rank C Dungeon and harvest draconic bone.", 1, 2500, 3, "Dragon's Gaze Orb", 40),
            new QuestDefinition("main_5_nether_citadel", "Domain of the Sovereign", QuestCategory.MAIN, "Breach the fiery citadel of the Nether Sovereign (Rank A).", 1, 6000, 5, "Nether Sovereign Crown", 75),
            new QuestDefinition("main_6_sky_citadel", "Gates of Eternity", QuestCategory.MAIN, "Ascend to the Floating Sky Citadel and conquer the Rank S trial.", 1, 12000, 10, "Astral Key of the Skies", 100),

            // Side Bounties
            new QuestDefinition("side_wilderness_slayer", "Wilderness Bounty", QuestCategory.SIDE, "Purge 10 hostile monsters lurking in the mortal overworld.", 10, 350, 0, "Mana Crystal", 5),
            new QuestDefinition("side_crystal_miner", "Subterranean Veins", QuestCategory.SIDE, "Excavate 5 raw Mana Crystals from deep underground caves.", 5, 500, 1, "Dungeon Map", 10),
            new QuestDefinition("side_forge_master", "Masterwork Smithing", QuestCategory.SIDE, "Acquire or smelt a rare Mythril Ingot from deepslate strata.", 1, 800, 1, "Skill Scroll (Rank C)", 20),
            new QuestDefinition("side_apex_hunter", "Apex Showdown", QuestCategory.SIDE, "Engage and defeat an Apex Boss within their sacred arena.", 1, 2000, 2, "Ancient Tome (Rank B)", 35),

            // Daily Quests
            new QuestDefinition("daily_slayer", "Daily Monster Purge", QuestCategory.DAILY, "Slay 15 monsters to maintain balance in the realm.", 15, 300, 0, "300 Essence", 1),
            new QuestDefinition("daily_agility", "Evasive Reflexes", QuestCategory.DAILY, "Execute 10 directional dashes in combat.", 10, 250, 0, "250 Essence", 1),
            new QuestDefinition("daily_sorcery", "Arcane Resonance", QuestCategory.DAILY, "Cast any martial or magical arts 15 times.", 15, 350, 0, "350 Essence", 1)
    );

    // Legacy backwards-compatible record & array
    public record Quest(String title, String description, int targetAmount, long essenceReward) {}
    public static final Quest[] QUESTS = new Quest[]{
            new Quest("First Steps of an Adventurer", "Slay 5 monsters in the wilderness.", 5, 100),
            new Quest("Crystal Collector", "Mine 3 Mana Crystals from cave depths.", 3, 200),
            new Quest("Dungeon Gate Breaker", "Conquer and clear your first Ranked Dungeon.", 1, 500),
            new Quest("Ascending Ranks", "Reach Level 10 and achieve Rank D status.", 10, 800),
            new Quest("Dragon's Nemesis", "Defeat an Apex Boss in their arena.", 1, 2000)
    };

    public static List<QuestDefinition> getQuestsByCategory(QuestCategory cat) {
        List<QuestDefinition> list = new ArrayList<>();
        for (QuestDefinition q : ALL_QUESTS) {
            if (q.category() == cat) list.add(q);
        }
        return list;
    }

    public static QuestDefinition getQuestById(String id) {
        for (QuestDefinition q : ALL_QUESTS) {
            if (q.id().equalsIgnoreCase(id)) return q;
        }
        return ALL_QUESTS.get(0);
    }

    public static boolean claimQuestReward(ServerPlayerEntity player, String questId) {
        PlayerData data = ((IPlayerDataAccessor) player).ascend$getPlayerData();
        if (data.getQuestProgress().isQuestCompleted(questId)) return false;

        QuestDefinition quest = getQuestById(questId);
        int currentProgress = data.getQuestProgress().getProgress(questId);
        if (currentProgress < quest.targetAmount() && !"main_1_reborn".equalsIgnoreCase(questId) && data.getLevel() < quest.targetAmount()) {
            return false;
        }

        // Grant rewards
        data.getQuestProgress().completeQuest(questId);
        data.addEssenceWithBonus(quest.essenceReward());
        if (quest.statPointReward() > 0) {
            data.addStatPoints(quest.statPointReward());
        }

        player.getServerWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 1.2f, 1.0f);
        player.sendMessage(Text.literal("§a§l[QUEST COMPLETED] §f" + quest.title() + " §7(+" + quest.essenceReward() + " Essence, +" + quest.statPointReward() + " Stat Pts)"), false);

        net.ascend.mod.network.AscendPackets.syncPlayerData(player);
        return true;
    }

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
