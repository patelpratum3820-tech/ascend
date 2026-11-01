package net.ascend.mod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.ascend.mod.AscendMod;
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
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class SkillManagerScreen extends Screen {
    private static final int CARD_WIDTH = 380;
    private static final int CARD_HEIGHT = 240;

    private static final String[] HOTKEYS = {"R", "F", "C", "X"};
    private static final String[] RANK_REQS = {"Rank E", "Rank D", "Rank C", "Rank A"};

    private int selectedSlot = 0;
    private SkillRank filterRank = null; // null = all
    private int page = 0;
    private static final int PAGE_SIZE = 3;

    public SkillManagerScreen() {
        super(Text.literal("Ascender's Grimoire"));
    }

    @Override
    protected void init() {
        clearChildren();
        if (client == null || client.player == null) return;

        int left = (width - CARD_WIDTH) / 2;
        int top = (height - CARD_HEIGHT) / 2;
        PlayerData data = ((IPlayerDataAccessor) client.player).ascend$getPlayerData();
        int maxSlots = data.getMaxUnlockedSkillSlots();

        // 1. Hotbar Slot Selectors (Top)
        for (int i = 0; i < 4; i++) {
            final int slotIdx = i;
            int slotX = left + 20 + (i * 85);
            int slotY = top + 26;

            boolean unlocked = i < maxSlots;
            String hotkey = HOTKEYS[i];
            String equippedId = (i < data.getSelectedSkills().size()) ? data.getSelectedSkills().get(i) : "";
            SkillInfo info = (!equippedId.isEmpty()) ? SkillInfo.getById(equippedId) : null;

            String label = unlocked ? "[" + hotkey + "] " + (info != null ? info.displayName() : "Empty") : "🔒 " + RANK_REQS[i];
            if (label.length() > 12) label = label.substring(0, 11) + "...";

            ButtonWidget slotBtn = ButtonWidget.builder(Text.literal(label), b -> {
                if (unlocked) {
                    selectedSlot = slotIdx;
                    clearAndInit();
                }
            }).dimensions(slotX, slotY, 80, 22).build();

            slotBtn.active = unlocked;
            addDrawableChild(slotBtn);
        }

        // 2. Rank Filter Buttons
        SkillRank[] ranks = {null, SkillRank.E, SkillRank.D, SkillRank.C, SkillRank.B, SkillRank.A, SkillRank.S, SkillRank.SS};
        String[] rLabels = {"ALL", "E", "D", "C", "B", "A", "S", "SS"};
        for (int i = 0; i < ranks.length; i++) {
            final SkillRank r = ranks[i];
            boolean isCur = filterRank == r;
            ButtonWidget rBtn = ButtonWidget.builder(Text.literal((isCur ? "§e" : "§7") + rLabels[i]), b -> {
                filterRank = r;
                page = 0;
                clearAndInit();
            }).dimensions(left + 20 + (i * 42), top + 52, 38, 16).build();
            addDrawableChild(rBtn);
        }

        // 3. Unlocked Skills List
        List<SkillInfo> learnedSkills = new ArrayList<>();
        for (SkillInfo sk : SkillInfo.ALL_SKILLS) {
            if (data.hasUnlockedSkill(sk.id())) {
                if (filterRank == null || sk.rank() == filterRank) {
                    learnedSkills.add(sk);
                }
            }
        }

        int maxPages = Math.max(1, (int) Math.ceil((double) learnedSkills.size() / PAGE_SIZE));
        if (page >= maxPages) page = maxPages - 1;
        if (page < 0) page = 0;

        int startIndex = page * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, learnedSkills.size());

        for (int i = startIndex; i < endIndex; i++) {
            SkillInfo skill = learnedSkills.get(i);
            int row = i - startIndex;
            int itemY = top + 74 + (row * 44);

            int equippedInSlot = data.getSelectedSkills().indexOf(skill.id());
            boolean isEquippedHere = equippedInSlot == selectedSlot;

            if (isEquippedHere) {
                ButtonWidget unequipBtn = ButtonWidget.builder(Text.literal("§cUnequip"), b -> {
                    AscendClientPackets.sendEquipSkill(selectedSlot, "");
                    clearAndInit();
                }).dimensions(left + CARD_WIDTH - 85, itemY + 10, 68, 20).build();
                addDrawableChild(unequipBtn);
            } else {
                String btnText = equippedInSlot != -1 ? "§6Swap" : "§bEquip";
                ButtonWidget equipBtn = ButtonWidget.builder(Text.literal(btnText), b -> {
                    AscendClientPackets.sendEquipSkill(selectedSlot, skill.id());
                    clearAndInit();
                }).dimensions(left + CARD_WIDTH - 85, itemY + 10, 68, 20).build();
                addDrawableChild(equipBtn);
            }
        }

        // 4. Pagination Buttons
        if (maxPages > 1) {
            ButtonWidget prevBtn = ButtonWidget.builder(Text.literal("◀ Prev"), b -> {
                page--;
                clearAndInit();
            }).dimensions(left + 20, top + CARD_HEIGHT - 26, 55, 18).build();
            prevBtn.active = page > 0;
            addDrawableChild(prevBtn);

            ButtonWidget nextBtn = ButtonWidget.builder(Text.literal("Next ▶"), b -> {
                page++;
                clearAndInit();
            }).dimensions(left + 80, top + CARD_HEIGHT - 26, 55, 18).build();
            nextBtn.active = page < maxPages - 1;
            addDrawableChild(nextBtn);
        }

        // 5. Back & Close Buttons
        ButtonWidget backBtn = ButtonWidget.builder(Text.literal("Status (K)"), b -> {
            if (client != null) client.setScreen(new StatusScreen());
        }).dimensions(left + CARD_WIDTH - 150, top + CARD_HEIGHT - 26, 70, 18).build();
        addDrawableChild(backBtn);

        ButtonWidget closeBtn = ButtonWidget.builder(Text.literal("Close"), b -> close())
                .dimensions(left + CARD_WIDTH - 75, top + CARD_HEIGHT - 26, 60, 18).build();
        addDrawableChild(closeBtn);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        int left = (width - CARD_WIDTH) / 2;
        int top = (height - CARD_HEIGHT) / 2;

        // Dark RPG card background
        context.fill(left, top, left + CARD_WIDTH, top + CARD_HEIGHT, 0xEE090E1B);
        context.drawBorder(left, top, CARD_WIDTH, CARD_HEIGHT, 0xFF00D2FF);
        context.drawBorder(left + 2, top + 2, CARD_WIDTH - 4, CARD_HEIGHT - 4, 0x44FFFFFF);

        // Header
        String title = "✦ ASCENDER'S GRIMOIRE — ACTIVE ARTS ✦";
        int tw = textRenderer.getWidth(title);
        context.drawText(textRenderer, Text.literal("§l" + title), left + (CARD_WIDTH - tw) / 2, top + 10, 0xFFFFD700, true);

        // Highlight selected slot
        int highlightX = left + 20 + (selectedSlot * 85);
        context.drawBorder(highlightX - 1, top + 25, 82, 24, 0xFFFFD700);

        PlayerData data = null;
        if (client != null && client.player != null) {
            data = ((IPlayerDataAccessor) client.player).ascend$getPlayerData();
        }

        // Render Skill Items
        List<SkillInfo> learnedSkills = new ArrayList<>();
        if (data != null) {
            for (SkillInfo sk : SkillInfo.ALL_SKILLS) {
                if (data.hasUnlockedSkill(sk.id())) {
                    if (filterRank == null || sk.rank() == filterRank) {
                        learnedSkills.add(sk);
                    }
                }
            }
        }

        int maxPages = Math.max(1, (int) Math.ceil((double) learnedSkills.size() / PAGE_SIZE));
        int startIndex = page * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, learnedSkills.size());

        if (learnedSkills.isEmpty()) {
            context.drawText(textRenderer, Text.literal("§7No learned skills for this rank filter. Use Skill Scrolls to learn!"), left + 30, top + 110, 0xFFAAAAAA, false);
        }

        for (int i = startIndex; i < endIndex; i++) {
            SkillInfo skill = learnedSkills.get(i);
            int row = i - startIndex;
            int itemY = top + 74 + (row * 44);

            // Item card box
            context.fill(left + 15, itemY, left + CARD_WIDTH - 15, itemY + 40, 0x55112233);
            context.drawBorder(left + 15, itemY, CARD_WIDTH - 30, 40, 0x3300D2FF);

            // Icon (16x16)
            Identifier texture = new Identifier(AscendMod.MOD_ID, "textures/skill/" + skill.id() + ".png");
            try {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                context.drawTexture(texture, left + 20, itemY + 12, 0f, 0f, 16, 16, 16, 16);
                RenderSystem.disableBlend();
            } catch (Exception ignored) {
            }

            // Skill Name & Tag
            context.drawText(textRenderer, Text.literal(skill.rank().getColorCode() + "§l" + skill.displayName() + " §7[" + skill.tag() + "]"), left + 42, itemY + 4, 0xFFFFFFFF, true);

            // Cost & Cooldown
            String cost = "§eCost: " + (int) skill.costAmount() + " " + skill.costType() + (skill.isPercent() ? "%" : "") + " §7| §bCD: " + skill.cooldownSec() + "s";
            context.drawText(textRenderer, Text.literal(cost), left + 42, itemY + 15, 0xFFEEDD88, false);

            // Description
            String desc = "§f" + skill.description();
            if (textRenderer.getWidth(desc) > 210) desc = textRenderer.trimToWidth(desc, 205) + "...";
            context.drawText(textRenderer, Text.literal(desc), left + 42, itemY + 26, 0xFFCCCCCC, false);
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

