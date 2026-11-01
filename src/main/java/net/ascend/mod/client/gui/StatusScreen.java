package net.ascend.mod.client.gui;

import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.ascend.mod.network.AscendClientPackets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class StatusScreen extends Screen {
    private static final int CARD_WIDTH = 250;
    private static final int CARD_HEIGHT = 230;

    public StatusScreen() {
        super(Text.translatable("text.ascend.status_title"));
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

        PlayerData data = ((IPlayerDataAccessor) client.player).ascend$getPlayerData();
        int left = (width - CARD_WIDTH) / 2;
        int top = (height - CARD_HEIGHT) / 2;

        int statsStartY = top + 92;
        int rowHeight = 22;

        String[] stats = {"strength", "speed", "vitality", "mana", "defense"};
        int[] values = {data.getStrength(), data.getSpeed(), data.getVitality(), data.getMana(), data.getDefense()};

        for (int i = 0; i < stats.length; i++) {
            final String stat = stats[i];
            int currentVal = values[i];
            long essenceCost = data.getEssenceCostForStat(currentVal);
            boolean canAfford = data.getStatPoints() > 0 || data.getEssence() >= essenceCost;

            int btnY = statsStartY + (i * rowHeight) - 2;
            ButtonWidget btn = ButtonWidget.builder(Text.literal("+"), button -> {
                AscendClientPackets.sendStatUpgradeRequest(stat);
            }).dimensions(left + CARD_WIDTH - 34, btnY, 20, 18).build();

            btn.active = canAfford;
            btn.setTooltip(net.minecraft.client.gui.tooltip.Tooltip.of(
                data.getStatPoints() > 0 
                    ? Text.literal("Upgrade with 1 Stat Point") 
                    : Text.literal("Upgrade Cost: " + essenceCost + " Essence")
            ));

            addDrawableChild(btn);
        }

        // Grimoire & Close buttons at bottom
        ButtonWidget grimoireBtn = ButtonWidget.builder(Text.literal("§b✦ Grimoire"), button -> {
            if (client != null) client.setScreen(new SkillManagerScreen());
        }).dimensions(left + 18, top + CARD_HEIGHT - 24, 90, 18).build();
        addDrawableChild(grimoireBtn);

        ButtonWidget closeBtn = ButtonWidget.builder(Text.literal("Close"), button -> close())
                .dimensions(left + CARD_WIDTH - 75, top + CARD_HEIGHT - 24, 58, 18)
                .build();
        addDrawableChild(closeBtn);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        int left = (width - CARD_WIDTH) / 2;
        int top = (height - CARD_HEIGHT) / 2;

        // Dark Anime RPG Card Background (Solo Leveling theme)
        context.fill(left, top, left + CARD_WIDTH, top + CARD_HEIGHT, 0xEE080D1A);
        // Outer Cyan Neon Border
        context.drawBorder(left, top, CARD_WIDTH, CARD_HEIGHT, 0xFF00D2FF);
        context.drawBorder(left + 2, top + 2, CARD_WIDTH - 4, CARD_HEIGHT - 4, 0x88005577);

        // Header Title
        Text title = Text.literal("✦ STATUS WINDOW ✦");
        int titleWidth = textRenderer.getWidth(title);
        context.drawText(textRenderer, title, left + (CARD_WIDTH - titleWidth) / 2, top + 10, 0xFF00E5FF, true);

        // Header Separator line
        context.fill(left + 12, top + 24, left + CARD_WIDTH - 12, top + 25, 0x8800D2FF);

        if (client != null && client.player != null) {
            PlayerData data = ((IPlayerDataAccessor) client.player).ascend$getPlayerData();

            // Render Player Avatar model on top left
            InventoryScreen.drawEntity(context, left + 32, top + 80, 24, (float)(left + 32) - mouseX, (float)(top + 80 - 35) - mouseY, client.player);

            // Player Info on top right
            int infoX = left + 62;
            context.drawText(textRenderer, Text.literal("Name: §f" + data.getCharacterName()), infoX, top + 30, 0xFFE0E0E0, false);
            context.drawText(textRenderer, Text.literal("Race: §b" + data.getRace() + " §7(" + data.getCurrentEvolutionTier() + ")"), infoX, top + 42, 0xFFE0E0E0, false);
            context.drawText(textRenderer, Text.literal("Class: §e" + data.getCombinedClassTitle()), infoX, top + 54, 0xFFE0E0E0, false);

            String rankColor = getRankColor(data.getRank());
            context.drawText(textRenderer, Text.literal("Level: §e" + data.getLevel() + " §8| §fRank: " + rankColor + data.getRank()), infoX, top + 66, 0xFFE0E0E0, false);

            if (data.getStatPoints() > 0) {
                context.drawText(textRenderer, Text.literal("Stat Pts: §a" + data.getStatPoints() + " §7(" + data.getAllocationMode().getDisplayName() + ")"), infoX, top + 78, 0xFF55FF55, true);
            } else {
                context.drawText(textRenderer, Text.literal("Essence: §b" + data.getEssence() + " §7(" + data.getAllocationMode().getDisplayName() + ")"), infoX, top + 78, 0xFF55FFFF, false);
            }

            // Stats Subheader line
            context.fill(left + 12, top + 86, left + CARD_WIDTH - 12, top + 87, 0x4400D2FF);

            // Stats rows
            int statsStartY = top + 92;
            int rowHeight = 22;

            drawStatRow(context, "Strength", data.getStrength(), "§c", statsStartY, left, data);
            drawStatRow(context, "Speed", data.getSpeed(), "§b", statsStartY + rowHeight, left, data);
            drawStatRow(context, "Vitality", data.getVitality(), "§a", statsStartY + (rowHeight * 2), left, data);
            drawStatRow(context, "Mana", data.getMana(), "§9", statsStartY + (rowHeight * 3), left, data);
            drawStatRow(context, "Defense", data.getDefense(), "§e", statsStartY + (rowHeight * 4), left, data);

            // Equipped Skills Section with Rank Badges
            int skillY = statsStartY + (rowHeight * 5) + 6;
            context.fill(left + 12, skillY - 4, left + CARD_WIDTH - 12, skillY - 3, 0x4400D2FF);
            context.drawText(textRenderer, Text.literal("§6✦ EQUIPPED SKILLS (" + data.getSelectedSkills().size() + "/" + data.getMaxUnlockedSkillSlots() + ")"), left + 18, skillY, 0xFFFFFFFF, false);

            int skCol = 0;
            for (String sId : data.getSelectedSkills()) {
                net.ascend.mod.skills.SkillInfo sk = net.ascend.mod.skills.SkillInfo.getById(sId);
                String tag = "[" + sk.rank().getFormatted() + "§r] §f" + sk.displayName();
                context.drawText(textRenderer, Text.literal(tag), left + 18, skillY + 12 + (skCol * 10), 0xFFFFFFFF, false);
                skCol++;
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawStatRow(DrawContext context, String label, int value, String colorCode, int y, int left, PlayerData data) {
        context.drawText(textRenderer, Text.literal(colorCode + label), left + 18, y + 3, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("§f" + value), left + 90, y + 3, 0xFFFFFFFF, false);

        long cost = data.getEssenceCostForStat(value);
        String costText = data.getStatPoints() > 0 ? "§a1 Pt" : "§7(" + cost + ")";
        context.drawText(textRenderer, Text.literal(costText), left + 140, y + 3, 0xFFAAAAAA, false);
    }

    private String getRankColor(String rank) {
        return switch (rank.toUpperCase()) {
            case "SS" -> "§e§l";
            case "S" -> "§c§l";
            case "A" -> "§6§l";
            case "B" -> "§5§l";
            case "C" -> "§9§l";
            case "D" -> "§a§l";
            default -> "§7§l";
        };
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}

