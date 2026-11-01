package net.ascend.mod.client.gui;

import net.ascend.mod.client.gui.components.RpgTabBar;
import net.ascend.mod.client.gui.components.RpgTheme;
import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.ascend.mod.data.RaceEvolution;
import net.ascend.mod.data.StatAllocationMode;
import net.ascend.mod.network.AscendClientPackets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public class CharacterProfileScreen extends Screen {
    private static final int CARD_WIDTH = 380;
    private static final int CARD_HEIGHT = 240;

    private int currentTab = 0; // 0: Overview, 1: Attributes, 2: Heritage, 3: Titles
    private final RpgTabBar tabBar;

    public CharacterProfileScreen() {
        super(Text.literal("Ascend - Character Profile"));
        this.tabBar = new RpgTabBar(idx -> {
            this.currentTab = idx;
            clearAndInit();
        });
        tabBar.addTab("overview", "Overview");
        tabBar.addTab("attributes", "Attributes");
        tabBar.addTab("heritage", "Heritage");
        tabBar.addTab("titles", "Titles");
    }

    @Override
    protected void init() {
        super.init();
        refreshWidgets();
    }

    public void refreshData() {
        clearAndInit();
    }

    private void refreshWidgets() {
        clearChildren();
        if (client == null || client.player == null) return;

        int left = (width - CARD_WIDTH) / 2;
        int top = (height - CARD_HEIGHT) / 2;
        PlayerData data = ((IPlayerDataAccessor) client.player).ascend$getPlayerData();

        // 1. Navigation Tab Bar (Top)
        tabBar.setSelectedIndex(currentTab);
        for (ButtonWidget tabBtn : tabBar.createWidgets(left + 16, top + 26, CARD_WIDTH - 32, 18)) {
            addDrawableChild(tabBtn);
        }

        // 2. Tab Specific Widgets
        switch (currentTab) {
            case 0 -> initOverviewTab(left, top, data);
            case 1 -> initAttributesTab(left, top, data);
            case 2 -> initHeritageTab(left, top, data);
            case 3 -> initTitlesTab(left, top, data);
        }

        // 3. Bottom Utility Bar
        ButtonWidget grimoireBtn = ButtonWidget.builder(Text.literal("§b✦ Grimoire"), button -> {
            if (client != null) client.setScreen(new SkillManagerScreen());
        }).dimensions(left + 16, top + CARD_HEIGHT - 24, 88, 18).build();
        addDrawableChild(grimoireBtn);

        ButtonWidget closeBtn = ButtonWidget.builder(Text.literal("Close"), button -> close())
                .dimensions(left + CARD_WIDTH - 74, top + CARD_HEIGHT - 24, 58, 18)
                .build();
        addDrawableChild(closeBtn);
    }

    private void initOverviewTab(int left, int top, PlayerData data) {
        // No interactive buttons needed in basic overview; everything is visual
    }

    private void initAttributesTab(int left, int top, PlayerData data) {
        int statsStartY = top + 56;
        int rowHeight = 22;

        String[] statKeys = {"strength", "agility", "vitality", "intelligence", "dexterity"};
        int[] values = {data.getStrength(), data.getAgility(), data.getVitality(), data.getIntelligence(), data.getDexterity()};

        for (int i = 0; i < statKeys.length; i++) {
            final String stat = statKeys[i];
            int currentVal = values[i];
            long essenceCost = data.getEssenceCostForStat(currentVal);
            boolean canAfford = data.getStatPoints() > 0 || data.getEssence() >= essenceCost;

            int btnY = statsStartY + (i * rowHeight) - 2;
            ButtonWidget btn = ButtonWidget.builder(Text.literal("+"), button -> {
                AscendClientPackets.sendStatUpgradeRequest(stat);
            }).dimensions(left + 180, btnY, 18, 18).build();

            btn.active = canAfford;
            btn.setTooltip(net.minecraft.client.gui.tooltip.Tooltip.of(
                    data.getStatPoints() > 0
                            ? Text.literal("Spend 1 Stat Point")
                            : Text.literal("Upgrade Cost: " + essenceCost + " Essence")
            ));
            addDrawableChild(btn);
        }

        // Allocation Mode Selector Buttons (Right panel)
        int modeX = left + 220;
        int modeY = top + 74;
        StatAllocationMode[] modes = StatAllocationMode.values();
        for (int i = 0; i < modes.length; i++) {
            StatAllocationMode m = modes[i];
            boolean isCur = data.getAllocationMode() == m;
            ButtonWidget mBtn = ButtonWidget.builder(Text.literal((isCur ? "§a▶ " : "  ") + m.getDisplayName()), b -> {
                AscendClientPackets.sendSetAllocationMode(m.name());
            }).dimensions(modeX, modeY + (i * 22), 140, 19).build();

            mBtn.setTooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.literal(m.getDescription())));
            addDrawableChild(mBtn);
        }
    }

    private void initHeritageTab(int left, int top, PlayerData data) {
        // Heritage display is descriptive
    }

    private void initTitlesTab(int left, int top, PlayerData data) {
        int startY = top + 56;
        int idx = 0;
        for (String title : data.getStoryProgress().getUnlockedTitles()) {
            boolean isActive = title.equalsIgnoreCase(data.getStoryProgress().getActiveTitle());
            Text label = Text.literal((isActive ? "§6[Equipped] " : "§7[Equip] ") + title);

            ButtonWidget titleBtn = ButtonWidget.builder(label, b -> {
                AscendClientPackets.sendSetActiveTitle(title);
            }).dimensions(left + 24, startY + (idx * 24), 330, 20).build();

            addDrawableChild(titleBtn);
            idx++;
            if (idx >= 6) break;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        int left = (width - CARD_WIDTH) / 2;
        int top = (height - CARD_HEIGHT) / 2;

        // Base card frame
        RpgTheme.drawCard(context, left, top, CARD_WIDTH, CARD_HEIGHT, true);

        // Header Title
        RpgTheme.drawHeader(context, textRenderer, "✦ ASCENDER PROFILE ✦", left, top + 8, CARD_WIDTH, true);

        if (client != null && client.player != null) {
            PlayerData data = ((IPlayerDataAccessor) client.player).ascend$getPlayerData();

            switch (currentTab) {
                case 0 -> renderOverview(context, left, top, data, mouseX, mouseY);
                case 1 -> renderAttributes(context, left, top, data);
                case 2 -> renderHeritage(context, left, top, data);
                case 3 -> renderTitles(context, left, top, data);
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderOverview(DrawContext context, int left, int top, PlayerData data, int mouseX, int mouseY) {
        // 3D Player Avatar (Top Left)
        InventoryScreen.drawEntity(context, left + 40, top + 120, 32, (float)(left + 40) - mouseX, (float)(top + 120 - 45) - mouseY, client.player);

        int infoX = left + 88;
        int startY = top + 48;

        // Identity
        context.drawText(textRenderer, Text.literal("§e§l" + data.getCharacterName()), infoX, startY, 0xFFFFFFFF, true);
        context.drawText(textRenderer, Text.literal("§7Title: §6" + data.getStoryProgress().getActiveTitle()), infoX, startY + 12, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("§7Heritage: §b" + data.getRace() + " §7(" + data.getCurrentEvolutionTier() + ")"), infoX, startY + 24, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("§7Discipline: §e" + data.getCombinedClassTitle()), infoX, startY + 36, 0xFFFFFFFF, false);

        // Level & XP Bar
        long curXp = data.getXp();
        long reqXp = data.getXpRequiredForNextLevel();
        context.drawText(textRenderer, Text.literal("§7Level: §e" + data.getLevel() + "  §8|  §fRank: §6§l" + data.getRank()), infoX, startY + 50, 0xFFFFFFFF, false);
        double xpPct = reqXp > 0 ? (double) curXp / reqXp : 0.0;
        RpgTheme.drawProgressBar(context, infoX, startY + 62, 170, 7, xpPct, RpgTheme.BAR_XP, 0xFF7A5800);
        context.drawText(textRenderer, Text.literal("§8XP " + curXp + "/" + reqXp), infoX + 176, startY + 62, 0xFFAAAAAA, false);

        // Currency
        context.drawText(textRenderer, Text.literal("§7Essence: §b" + data.getEssence() + " §7| Points: §a" + data.getStatPoints()), infoX, startY + 74, 0xFFFFFFFF, false);

        // Equipped Skills Overview (Bottom)
        context.fill(left + 16, top + 144, left + CARD_WIDTH - 16, top + 145, 0x4400D2FF);
        context.drawText(textRenderer, Text.literal("§6Equipped Combat Arts:"), left + 20, top + 150, 0xFFFFD700, false);

        int skX = left + 20;
        int skY = top + 162;
        int slot = 0;
        for (String sId : data.getSelectedSkills()) {
            if (!sId.isBlank()) {
                net.ascend.mod.skills.SkillInfo sk = net.ascend.mod.skills.SkillInfo.getById(sId);
                String tag = "§e[" + sk.rank().getFormatted() + "§e] §f" + sk.displayName();
                context.drawText(textRenderer, Text.literal(tag), skX, skY + (slot * 11), 0xFFFFFFFF, false);
            } else {
                context.drawText(textRenderer, Text.literal("§8[Empty Slot]"), skX, skY + (slot * 11), 0xFF777777, false);
            }
            slot++;
            if (slot >= 4) break;
        }
    }

    private void renderAttributes(DrawContext context, int left, int top, PlayerData data) {
        int startY = top + 56;
        int rowHeight = 22;

        drawStatRow(context, "Strength", data.getStrength(), "§c", startY, left, data);
        drawStatRow(context, "Agility", data.getAgility(), "§b", startY + rowHeight, left, data);
        drawStatRow(context, "Vitality", data.getVitality(), "§a", startY + (rowHeight * 2), left, data);
        drawStatRow(context, "Intelligence", data.getIntelligence(), "§9", startY + (rowHeight * 3), left, data);
        drawStatRow(context, "Dexterity", data.getDexterity(), "§e", startY + (rowHeight * 4), left, data);

        // Right side header
        context.drawText(textRenderer, Text.literal("§6Allocation Presets:"), left + 220, top + 58, 0xFFFFD700, true);
        context.drawText(textRenderer, Text.literal("§7Active: §a" + data.getAllocationMode().getDisplayName()), left + 220, top + CARD_HEIGHT - 40, 0xFFFFFFFF, false);
    }

    private void drawStatRow(DrawContext context, String label, int value, String colorCode, int y, int left, PlayerData data) {
        context.drawText(textRenderer, Text.literal(colorCode + label), left + 20, y + 3, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("§f" + value), left + 104, y + 3, 0xFFFFFFFF, false);

        long cost = data.getEssenceCostForStat(value);
        String costText = data.getStatPoints() > 0 ? "§a1 Pt" : "§7(" + cost + ")";
        context.drawText(textRenderer, Text.literal(costText), left + 138, y + 3, 0xFFAAAAAA, false);
    }

    private void renderHeritage(DrawContext context, int left, int top, PlayerData data) {
        int cx = left + 24;
        int cy = top + 52;

        context.drawText(textRenderer, Text.literal("§eChosen Species: §b" + data.getRace()), cx, cy, 0xFFFFFFFF, true);
        drawMultiline(context, "§7" + RaceEvolution.getRacePassiveDescription(data.getRace()), cx, cy + 14);

        // Evolution Ladder
        context.drawText(textRenderer, Text.literal("§6Evolution Path:"), cx, cy + 46, 0xFFFFD700, true);
        List<RaceEvolution.EvolutionTier> path = RaceEvolution.getEvolutionPath(data.getRace());
        for (int i = 0; i < path.size(); i++) {
            RaceEvolution.EvolutionTier tier = path.get(i);
            boolean unlocked = data.getLevel() >= tier.requiredLevel();
            String prefix = unlocked ? "§a✦ " : "§8🔒 ";
            String line = prefix + tier.name() + " §7(Lv " + tier.requiredLevel() + ") — §f" + tier.description();
            context.drawText(textRenderer, Text.literal(line), cx + 8, cy + 60 + (i * 22), 0xFFFFFFFF, false);
        }
    }

    private void renderTitles(DrawContext context, int left, int top, PlayerData data) {
        int cx = left + 24;
        context.drawText(textRenderer, Text.literal("§6Active Title: §e" + data.getStoryProgress().getActiveTitle()), cx, top + 46, 0xFFFFD700, true);
    }

    private void drawMultiline(DrawContext context, String text, int x, int y) {
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            context.drawText(textRenderer, Text.literal(lines[i]), x, y + (i * 11), 0xFFFFFFFF, false);
        }
    }
}
