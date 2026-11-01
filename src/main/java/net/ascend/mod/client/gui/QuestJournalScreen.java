package net.ascend.mod.client.gui;

import net.ascend.mod.client.gui.components.RpgTabBar;
import net.ascend.mod.client.gui.components.RpgTheme;
import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.ascend.mod.network.AscendClientPackets;
import net.ascend.mod.quest.QuestManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class QuestJournalScreen extends Screen {
    private static final int CARD_WIDTH = 390;
    private static final int CARD_HEIGHT = 245;

    private int currentCategory = 0; // 0: Main, 1: Side, 2: Daily, 3: Completed
    private final RpgTabBar tabBar;
    private QuestManager.QuestDefinition selectedQuest;

    public QuestJournalScreen() {
        super(Text.literal("Quest Journal"));
        this.tabBar = new RpgTabBar(idx -> {
            this.currentCategory = idx;
            this.selectedQuest = null;
            clearAndInit();
        });
        tabBar.addTab("main", "Main Story");
        tabBar.addTab("side", "Bounties");
        tabBar.addTab("daily", "Daily Trials");
        tabBar.addTab("completed", "Completed");
    }

    @Override
    protected void init() {
        super.init();
        clearChildren();
        if (client == null || client.player == null) return;

        int left = (width - CARD_WIDTH) / 2;
        int top = (height - CARD_HEIGHT) / 2;
        PlayerData data = ((IPlayerDataAccessor) client.player).ascend$getPlayerData();

        // 1. Navigation Tab Bar
        tabBar.setSelectedIndex(currentCategory);
        for (ButtonWidget tabBtn : tabBar.createWidgets(left + 16, top + 26, CARD_WIDTH - 32, 18)) {
            addDrawableChild(tabBtn);
        }

        // 2. Fetch quests for active tab
        List<QuestManager.QuestDefinition> quests = getQuestsForTab(data);
        if (selectedQuest == null && !quests.isEmpty()) {
            selectedQuest = quests.get(0);
        }

        // Left list buttons
        int listY = top + 50;
        for (int i = 0; i < Math.min(quests.size(), 5); i++) {
            QuestManager.QuestDefinition q = quests.get(i);
            boolean isCur = selectedQuest != null && selectedQuest.id().equals(q.id());
            boolean isDone = data.getQuestProgress().isQuestCompleted(q.id());

            String prefix = isDone ? "§a[✓] " : (isCur ? "§6▶ " : "§7");
            String label = prefix + q.title();
            if (label.length() > 20) label = label.substring(0, 19) + "...";

            ButtonWidget btn = ButtonWidget.builder(Text.literal(label), b -> {
                selectedQuest = q;
                clearAndInit();
            }).dimensions(left + 16, listY + (i * 26), 140, 22).build();

            addDrawableChild(btn);
        }

        // Right side Claim Button if ready
        if (selectedQuest != null) {
            boolean isCompleted = data.getQuestProgress().isQuestCompleted(selectedQuest.id());
            int progress = data.getQuestProgress().getProgress(selectedQuest.id());
            if ("main_1_reborn".equalsIgnoreCase(selectedQuest.id())) {
                progress = data.getLevel();
            }
            boolean canClaim = !isCompleted && progress >= selectedQuest.targetAmount();

            if (canClaim) {
                ButtonWidget claimBtn = ButtonWidget.builder(Text.literal("§6✦ CLAIM REWARD ✦"), b -> {
                    AscendClientPackets.sendClaimQuest(selectedQuest.id());
                    clearAndInit();
                }).dimensions(left + 170, top + CARD_HEIGHT - 30, 130, 20).build();
                addDrawableChild(claimBtn);
            }
        }

        // Close Button
        ButtonWidget closeBtn = ButtonWidget.builder(Text.literal("Close"), b -> close())
                .dimensions(left + CARD_WIDTH - 74, top + CARD_HEIGHT - 30, 58, 20)
                .build();
        addDrawableChild(closeBtn);
    }

    private List<QuestManager.QuestDefinition> getQuestsForTab(PlayerData data) {
        if (currentCategory == 3) {
            // Completed tab
            List<QuestManager.QuestDefinition> list = new ArrayList<>();
            for (QuestManager.QuestDefinition q : QuestManager.ALL_QUESTS) {
                if (data.getQuestProgress().isQuestCompleted(q.id())) {
                    list.add(q);
                }
            }
            return list;
        }

        QuestManager.QuestCategory cat = switch (currentCategory) {
            case 1 -> QuestManager.QuestCategory.SIDE;
            case 2 -> QuestManager.QuestCategory.DAILY;
            default -> QuestManager.QuestCategory.MAIN;
        };
        return QuestManager.getQuestsByCategory(cat);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        int left = (width - CARD_WIDTH) / 2;
        int top = (height - CARD_HEIGHT) / 2;

        RpgTheme.drawCard(context, left, top, CARD_WIDTH, CARD_HEIGHT, true);
        RpgTheme.drawHeader(context, textRenderer, "✦ ADVENTURER'S QUEST JOURNAL ✦", left, top + 8, CARD_WIDTH, true);

        if (client != null && client.player != null) {
            PlayerData data = ((IPlayerDataAccessor) client.player).ascend$getPlayerData();

            // Divider between quest list and quest details
            context.fill(left + 162, top + 48, left + 163, top + CARD_HEIGHT - 36, 0x5500D2FF);

            // Right side Quest Detail View
            if (selectedQuest != null) {
                renderQuestDetails(context, left + 172, top + 50, selectedQuest, data);
            } else {
                context.drawText(textRenderer, Text.literal("§7Select a quest to view objectives."), left + 180, top + 80, 0xFFAAAAAA, false);
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderQuestDetails(DrawContext context, int x, int y, QuestManager.QuestDefinition q, PlayerData data) {
        boolean isCompleted = data.getQuestProgress().isQuestCompleted(q.id());
        int progress = data.getQuestProgress().getProgress(q.id());
        if ("main_1_reborn".equalsIgnoreCase(q.id())) {
            progress = data.getLevel();
        }

        // Title & Category Badge
        context.drawText(textRenderer, Text.literal("§e§l" + q.title()), x, y, 0xFFFFFFFF, true);
        String catTag = q.category().getColor() + "[" + q.category().getTitle() + "] §7Req. Lv." + q.recommendedLevel();
        context.drawText(textRenderer, Text.literal(catTag), x, y + 14, 0xFFE0E0E0, false);

        // Lore Description
        drawMultiline(context, "§f" + q.description(), x, y + 28, 195);

        // Progress Tracker
        int barY = y + 74;
        context.drawText(textRenderer, Text.literal("§6Objective Progress:"), x, barY, 0xFFFFD700, false);
        double pct = q.targetAmount() > 0 ? (double) Math.min(progress, q.targetAmount()) / q.targetAmount() : 0.0;
        RpgTheme.drawProgressBar(context, x, barY + 12, 195, 8, pct, RpgTheme.BAR_STAMINA, 0xFF0D5A34);

        String progStr = isCompleted ? "§a§lCOMPLETED ✓" : "§f" + progress + " / " + q.targetAmount();
        context.drawText(textRenderer, Text.literal(progStr), x + 4, barY + 24, 0xFFFFFFFF, false);

        // Rewards Section
        int rewY = barY + 40;
        context.fill(x, rewY - 2, x + 195, rewY - 1, 0x4400D2FF);
        context.drawText(textRenderer, Text.literal("§6Rewards upon Completion:"), x, rewY + 4, 0xFFFFD700, false);
        context.drawText(textRenderer, Text.literal("§b✦ " + q.essenceReward() + " Essence"), x + 6, rewY + 16, 0xFF00E5FF, false);
        if (q.statPointReward() > 0) {
            context.drawText(textRenderer, Text.literal("§a✦ +" + q.statPointReward() + " Stat Point(s)"), x + 6, rewY + 28, 0xFF55FF55, false);
        }
        if (q.rewardItemName() != null && !q.rewardItemName().isBlank()) {
            context.drawText(textRenderer, Text.literal("§e✦ Item: §f" + q.rewardItemName()), x + 6, rewY + 40, 0xFFFFFFFF, false);
        }
    }

    private void drawMultiline(DrawContext context, String text, int x, int y, int wrapWidth) {
        List<net.minecraft.text.OrderedText> lines = textRenderer.wrapLines(Text.literal(text), wrapWidth);
        for (int i = 0; i < lines.size(); i++) {
            context.drawText(textRenderer, lines.get(i), x, y + (i * 11), 0xFFFFFFFF, false);
        }
    }
}
