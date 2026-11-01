package net.ascend.mod.client.gui;

import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.ascend.mod.network.AscendClientPackets;
import net.ascend.mod.skills.SkillInfo;
import net.ascend.mod.skills.SkillRank;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public class SkillUnlockScreen extends Screen {
    private final SkillRank rank;
    private final boolean isMainHand;
    private int page = 0;
    private static final int PAGE_SIZE = 3;

    private static final int CARD_WIDTH = 360;
    private static final int CARD_HEIGHT = 220;

    public SkillUnlockScreen(SkillRank rank, boolean isMainHand) {
        super(Text.literal("Ancient Scroll"));
        this.rank = rank;
        this.isMainHand = isMainHand;
    }

    @Override
    protected void init() {
        clearChildren();
        int left = (width - CARD_WIDTH) / 2;
        int top = (height - CARD_HEIGHT) / 2;

        PlayerData data = null;
        if (client != null && client.player != null) {
            data = ((IPlayerDataAccessor) client.player).ascend$getPlayerData();
        }

        List<SkillInfo> skills = SkillInfo.getSkillsByRank(rank);
        int maxPages = Math.max(1, (int) Math.ceil((double) skills.size() / PAGE_SIZE));
        if (page >= maxPages) page = maxPages - 1;
        if (page < 0) page = 0;

        int startIndex = page * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, skills.size());

        for (int i = startIndex; i < endIndex; i++) {
            SkillInfo skill = skills.get(i);
            int row = i - startIndex;
            int btnY = top + 45 + (row * 48);

            boolean mastered = data != null && data.hasUnlockedSkill(skill.id());

            if (!mastered) {
                ButtonWidget learnBtn = ButtonWidget.builder(Text.literal("§6✦ LEARN ✦"), b -> {
                    AscendClientPackets.sendLearnSkill(skill.id(), isMainHand);
                    close();
                }).dimensions(left + CARD_WIDTH - 95, btnY + 8, 80, 22).build();
                addDrawableChild(learnBtn);
            }
        }

        // Pagination buttons
        if (maxPages > 1) {
            ButtonWidget prevBtn = ButtonWidget.builder(Text.literal("◀ Prev"), b -> {
                page--;
                clearAndInit();
            }).dimensions(left + 20, top + CARD_HEIGHT - 28, 60, 20).build();
            prevBtn.active = page > 0;
            addDrawableChild(prevBtn);

            ButtonWidget nextBtn = ButtonWidget.builder(Text.literal("Next ▶"), b -> {
                page++;
                clearAndInit();
            }).dimensions(left + 90, top + CARD_HEIGHT - 28, 60, 20).build();
            nextBtn.active = page < maxPages - 1;
            addDrawableChild(nextBtn);
        }

        // Close button
        ButtonWidget closeBtn = ButtonWidget.builder(Text.literal("Close"), b -> close())
                .dimensions(left + CARD_WIDTH - 80, top + CARD_HEIGHT - 28, 65, 20).build();
        addDrawableChild(closeBtn);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        int left = (width - CARD_WIDTH) / 2;
        int top = (height - CARD_HEIGHT) / 2;

        // Background card & border
        context.fill(left, top, left + CARD_WIDTH, top + CARD_HEIGHT, 0xEE090E1B);
        context.drawBorder(left, top, CARD_WIDTH, CARD_HEIGHT, rank.getHexColor());
        context.drawBorder(left + 2, top + 2, CARD_WIDTH - 4, CARD_HEIGHT - 4, 0x44FFFFFF);

        // Header
        String title = "✦ ANCIENT GRIMOIRE: " + rank.getFormatted() + " ART ✦";
        int tw = textRenderer.getWidth(title);
        context.drawText(textRenderer, Text.literal("§l" + title), left + (CARD_WIDTH - tw) / 2, top + 10, rank.getHexColor(), true);
        context.fill(left + 16, top + 24, left + CARD_WIDTH - 16, top + 25, 0x66FFFFFF);

        // Subtitle
        String sub = "Choose an art of this rank to engrave into your soul:";
        context.drawText(textRenderer, Text.literal("§7" + sub), left + 20, top + 30, 0xFFAAAAAA, false);

        PlayerData data = null;
        if (client != null && client.player != null) {
            data = ((IPlayerDataAccessor) client.player).ascend$getPlayerData();
        }

        List<SkillInfo> skills = SkillInfo.getSkillsByRank(rank);
        int maxPages = Math.max(1, (int) Math.ceil((double) skills.size() / PAGE_SIZE));
        int startIndex = page * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, skills.size());

        for (int i = startIndex; i < endIndex; i++) {
            SkillInfo skill = skills.get(i);
            int row = i - startIndex;
            int itemY = top + 45 + (row * 48);

            // Item card background
            context.fill(left + 15, itemY, left + CARD_WIDTH - 15, itemY + 44, 0x55112233);
            context.drawBorder(left + 15, itemY, CARD_WIDTH - 30, 44, 0x444488AA);

            // Skill Name & Tag
            context.drawText(textRenderer, Text.literal(rank.getColorCode() + "§l" + skill.displayName() + " §7[" + skill.tag() + "]"), left + 22, itemY + 5, 0xFFFFFFFF, true);

            // Cost & Cooldown
            String cost = "§eCost: " + (int) skill.costAmount() + " " + skill.costType() + (skill.isPercent() ? "%" : "") + " §7| §bCD: " + skill.cooldownSec() + "s";
            context.drawText(textRenderer, Text.literal(cost), left + 22, itemY + 17, 0xFFEEDD88, false);

            // Description
            String desc = "§f" + skill.description();
            if (textRenderer.getWidth(desc) > 230) {
                desc = textRenderer.trimToWidth(desc, 225) + "...";
            }
            context.drawText(textRenderer, Text.literal(desc), left + 22, itemY + 29, 0xFFDDDDDD, false);

            // If already mastered
            if (data != null && data.hasUnlockedSkill(skill.id())) {
                context.drawText(textRenderer, Text.literal("§a✔ Mastered"), left + CARD_WIDTH - 85, itemY + 15, 0xFF55FF55, true);
            }
        }

        // Page indicator
        if (maxPages > 1) {
            String pStr = (page + 1) + " / " + maxPages;
            int pw = textRenderer.getWidth(pStr);
            context.drawText(textRenderer, Text.literal("§7" + pStr), left + (CARD_WIDTH - pw) / 2, top + CARD_HEIGHT - 22, 0xFFAAAAAA, false);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}

