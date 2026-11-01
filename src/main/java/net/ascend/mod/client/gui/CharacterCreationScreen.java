package net.ascend.mod.client.gui;

import net.ascend.mod.data.CharacterClass;
import net.ascend.mod.data.RaceEvolution;
import net.ascend.mod.data.StatAllocationMode;
import net.ascend.mod.network.AscendClientPackets;
import net.ascend.mod.skills.SkillInfo;
import net.ascend.mod.skills.SkillRank;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class CharacterCreationScreen extends Screen {
    private static final int WIDGET_WIDTH = 410;
    private static final int WIDGET_HEIGHT = 250;

    // 0: Prologue & Name, 1: Race, 2: Dual Classes, 3: Fate & Allocation, 4: Starter Arts, 5: Confirm
    private int currentStep = 0;
    private int skillsPage = 0;

    private String characterName = "Chosen Soul";
    private TextFieldWidget nameField;

    private String selectedRace = "Human";
    private String primaryClass = "Swordsman";
    private String secondaryClass = "Mage";
    private String selectedDifficulty = "Normal";
    private StatAllocationMode selectedAllocation = StatAllocationMode.MANUAL;
    private final List<String> selectedSkills = new ArrayList<>();

    public CharacterCreationScreen() {
        super(Text.literal("Ascend - Character Awakening"));
        selectedSkills.add("basic_step");
        selectedSkills.add("flame_slash");
    }

    @Override
    protected void init() {
        super.init();
        if (client != null && client.player != null && "Chosen Soul".equals(characterName)) {
            characterName = client.player.getName().getString();
        }
        rebuildStepUI();
    }

    private void rebuildStepUI() {
        clearChildren();

        int left = (width - WIDGET_WIDTH) / 2;
        int top = (height - WIDGET_HEIGHT) / 2;
        int contentY = top + 42;

        switch (currentStep) {
            case 0 -> initPrologueStep(left, top);
            case 1 -> initRaceStep(left, contentY);
            case 2 -> initDualClassStep(left, contentY);
            case 3 -> initFateAndAllocationStep(left, contentY);
            case 4 -> initSkillsStep(left, contentY);
            case 5 -> initConfirmStep(left, contentY);
        }

        // Navigation Bar at Bottom (Steps 1 to 4)
        if (currentStep > 0 && currentStep < 5) {
            ButtonWidget backBtn = ButtonWidget.builder(Text.literal("◀ Back"), button -> {
                currentStep--;
                rebuildStepUI();
            }).dimensions(left + 16, top + WIDGET_HEIGHT - 28, 70, 20).build();
            addDrawableChild(backBtn);

            boolean canProceed = currentStep != 4 || selectedSkills.size() == 2;
            ButtonWidget nextBtn = ButtonWidget.builder(Text.literal("Next ▶"), button -> {
                currentStep++;
                if (currentStep == 4) {
                    validateSelectedSkills();
                }
                rebuildStepUI();
            }).dimensions(left + WIDGET_WIDTH - 86, top + WIDGET_HEIGHT - 28, 70, 20).build();
            nextBtn.active = canProceed;
            addDrawableChild(nextBtn);
        }

        // Back button on confirm screen
        if (currentStep == 5) {
            ButtonWidget backBtn = ButtonWidget.builder(Text.literal("◀ Modify"), button -> {
                currentStep = 4;
                rebuildStepUI();
            }).dimensions(left + 16, top + WIDGET_HEIGHT - 28, 70, 20).build();
            addDrawableChild(backBtn);
        }
    }

    private void initPrologueStep(int left, int top) {
        int fieldX = left + 140;
        int fieldY = top + 172;

        nameField = new TextFieldWidget(textRenderer, fieldX, fieldY, 160, 20, Text.literal("Character Name"));
        nameField.setText(characterName);
        nameField.setChangedListener(text -> characterName = text.trim().isEmpty() ? "Chosen Soul" : text.trim());
        addDrawableChild(nameField);

        ButtonWidget proceedBtn = ButtonWidget.builder(Text.literal("✦ ACCEPT YOUR DESTINY & BEGIN ✦"), button -> {
            if (nameField != null) characterName = nameField.getText().trim();
            if (characterName.isEmpty()) characterName = "Chosen Soul";
            currentStep = 1;
            rebuildStepUI();
        }).dimensions(left + (WIDGET_WIDTH - 240) / 2, top + WIDGET_HEIGHT - 32, 240, 24).build();
        addDrawableChild(proceedBtn);
    }

    private void validateSelectedSkills() {
        List<SkillInfo> allowed = getStarterSkillsForDualClass();
        List<String> validSelected = new ArrayList<>();
        for (String id : selectedSkills) {
            for (SkillInfo skill : allowed) {
                if (skill.id().equalsIgnoreCase(id)) {
                    validSelected.add(id);
                    break;
                }
            }
        }
        if (validSelected.size() < 2) {
            for (SkillInfo s : allowed) {
                if (!validSelected.contains(s.id())) {
                    validSelected.add(s.id());
                    if (validSelected.size() == 2) break;
                }
            }
        }
        selectedSkills.clear();
        selectedSkills.addAll(validSelected);
    }

    private List<SkillInfo> getStarterSkillsForDualClass() {
        Set<SkillInfo> candidates = new LinkedHashSet<>();
        candidates.addAll(SkillInfo.getSkillsForClass(primaryClass));
        candidates.addAll(SkillInfo.getSkillsForClass(secondaryClass));

        List<SkillInfo> starters = new ArrayList<>();
        for (SkillInfo s : candidates) {
            if (s.rank() == SkillRank.E) {
                starters.add(s);
            }
        }
        if (starters.isEmpty()) {
            starters.addAll(SkillInfo.getSkillsByRank(SkillRank.E));
        }
        return starters;
    }

    private void initRaceStep(int left, int contentY) {
        for (int i = 0; i < RaceEvolution.BASE_RACES.size(); i++) {
            String race = RaceEvolution.BASE_RACES.get(i);
            boolean isSel = race.equalsIgnoreCase(selectedRace);
            Text label = Text.literal((isSel ? "§b▶ §l" : "  ") + race);
            ButtonWidget btn = ButtonWidget.builder(label, button -> {
                selectedRace = race;
                rebuildStepUI();
            }).dimensions(left + 20, contentY + (i * 24), 110, 21).build();
            addDrawableChild(btn);
        }
    }

    private void initDualClassStep(int left, int contentY) {
        int col1X = left + 18;
        int col2X = left + 138;
        int startY = contentY + 16;

        // Column 1: Primary Class
        for (int i = 0; i < CharacterClass.BASE_CLASSES.size(); i++) {
            String cls = CharacterClass.BASE_CLASSES.get(i);
            boolean isSel = cls.equalsIgnoreCase(primaryClass);
            Text label = Text.literal((isSel ? "§6[1] " : "   ") + cls);
            ButtonWidget btn = ButtonWidget.builder(label, button -> {
                primaryClass = cls;
                validateSelectedSkills();
                rebuildStepUI();
            }).dimensions(col1X, startY + (i * 24), 108, 21).build();
            addDrawableChild(btn);
        }

        // Column 2: Secondary Class
        for (int i = 0; i < CharacterClass.BASE_CLASSES.size(); i++) {
            String cls = CharacterClass.BASE_CLASSES.get(i);
            boolean isSel = cls.equalsIgnoreCase(secondaryClass);
            Text label = Text.literal((isSel ? "§b[2] " : "   ") + cls);
            ButtonWidget btn = ButtonWidget.builder(label, button -> {
                secondaryClass = cls;
                validateSelectedSkills();
                rebuildStepUI();
            }).dimensions(col2X, startY + (i * 24), 108, 21).build();
            addDrawableChild(btn);
        }
    }

    private void initFateAndAllocationStep(int left, int contentY) {
        int col1X = left + 18;
        int col2X = left + 144;
        int startY = contentY + 16;

        // Difficulties
        String[] diffs = {"Easy", "Normal", "Hard", "Nightmare"};
        for (int i = 0; i < diffs.length; i++) {
            String diff = diffs[i];
            boolean isSel = diff.equalsIgnoreCase(selectedDifficulty);
            Text label = Text.literal((isSel ? "§e▶ §l" : "  ") + diff);
            ButtonWidget btn = ButtonWidget.builder(label, button -> {
                selectedDifficulty = diff;
                rebuildStepUI();
            }).dimensions(col1X, startY + (i * 26), 112, 22).build();
            addDrawableChild(btn);
        }

        // Stat Allocation Presets
        StatAllocationMode[] modes = StatAllocationMode.values();
        for (int i = 0; i < modes.length; i++) {
            StatAllocationMode m = modes[i];
            boolean isSel = m == selectedAllocation;
            Text label = Text.literal((isSel ? "§a▶ " : "  ") + m.getDisplayName());
            ButtonWidget btn = ButtonWidget.builder(label, button -> {
                selectedAllocation = m;
                rebuildStepUI();
            }).dimensions(col2X, startY + (i * 23), 130, 20).build();
            addDrawableChild(btn);
        }
    }

    private void initSkillsStep(int left, int contentY) {
        List<SkillInfo> allowed = getStarterSkillsForDualClass();
        int pageSize = 6;
        int maxPages = Math.max(1, (int) Math.ceil((double) allowed.size() / pageSize));
        if (skillsPage >= maxPages) skillsPage = maxPages - 1;

        int startIndex = skillsPage * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allowed.size());

        for (int i = startIndex; i < endIndex; i++) {
            SkillInfo skill = allowed.get(i);
            boolean isSelected = selectedSkills.contains(skill.id());
            Text label = Text.literal((isSelected ? "§a[✓] " : "[ ] ") + skill.displayName());

            int col = (i - startIndex) % 2;
            int row = (i - startIndex) / 2;
            int btnX = left + 18 + (col * 188);
            int btnY = contentY + (row * 28);

            ButtonWidget btn = ButtonWidget.builder(label, button -> {
                if (selectedSkills.contains(skill.id())) {
                    if (selectedSkills.size() > 1) selectedSkills.remove(skill.id());
                } else {
                    if (selectedSkills.size() >= 2) selectedSkills.remove(0);
                    selectedSkills.add(skill.id());
                }
                rebuildStepUI();
            }).dimensions(btnX, btnY, 180, 24).tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.literal("§e[" + skill.tag() + "] §f" + skill.description()))).build();

            addDrawableChild(btn);
        }

        // Pagination buttons
        if (maxPages > 1) {
            int pageY = contentY + 92;
            ButtonWidget prevBtn = ButtonWidget.builder(Text.literal("◀ Prev"), b -> {
                skillsPage--;
                rebuildStepUI();
            }).dimensions(left + 110, pageY, 60, 20).build();
            prevBtn.active = skillsPage > 0;
            addDrawableChild(prevBtn);

            ButtonWidget nextBtn = ButtonWidget.builder(Text.literal("Next ▶"), b -> {
                skillsPage++;
                rebuildStepUI();
            }).dimensions(left + 240, pageY, 60, 20).build();
            nextBtn.active = skillsPage < maxPages - 1;
            addDrawableChild(nextBtn);
        }
    }

    private void initConfirmStep(int left, int contentY) {
        ButtonWidget awakenBtn = ButtonWidget.builder(Text.literal("✦ AWAKEN & DESCEND TO OVERWORLD ✦"), button -> {
            AscendClientPackets.sendCompleteCreation(characterName, selectedDifficulty, selectedRace, primaryClass, secondaryClass, selectedAllocation.name(), selectedSkills);
            close();
        }).dimensions(left + (WIDGET_WIDTH - 260) / 2, contentY + 125, 260, 26).build();
        addDrawableChild(awakenBtn);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        int left = (width - WIDGET_WIDTH) / 2;
        int top = (height - WIDGET_HEIGHT) / 2;

        // Dark card background & gold/cyan frame
        context.fill(left, top, left + WIDGET_WIDTH, top + WIDGET_HEIGHT, 0xEE090E1B);
        context.drawBorder(left, top, WIDGET_WIDTH, WIDGET_HEIGHT, 0xFF00D2FF);
        context.drawBorder(left + 2, top + 2, WIDGET_WIDTH - 4, WIDGET_HEIGHT - 4, 0x66005577);

        // Header Title
        String stepTitle = switch (currentStep) {
            case 0 -> "✦ THE DIVINE AWAKENING ✦";
            case 1 -> "STEP 1/4: CHOOSE YOUR HERITAGE";
            case 2 -> "STEP 2/4: CHOOSE TWO COMBAT CLASSES";
            case 3 -> "STEP 3/4: WORLD FATE & STAT ALLOCATION";
            case 4 -> "STEP 4/4: CHOOSE 2 STARTING ARTS";
            default -> "✦ ASCENSION PACT CONFIRMATION ✦";
        };
        int titleW = textRenderer.getWidth(stepTitle);
        int titleColor = currentStep == 0 ? 0xFFFFD700 : 0xFF00E5FF;
        context.drawText(textRenderer, Text.literal("§l" + stepTitle), left + (WIDGET_WIDTH - titleW) / 2, top + 12, titleColor, true);
        context.fill(left + 16, top + 26, left + WIDGET_WIDTH - 16, top + 27, 0x8800D2FF);

        int descX = left + 140;
        int descY = top + 42;

        switch (currentStep) {
            case 0 -> renderPrologueText(context, left, top + 34);
            case 1 -> renderRaceDesc(context, descX, descY);
            case 2 -> renderDualClassPreview(context, left, top + 42);
            case 3 -> renderFateAndAllocationDesc(context, left + 284, top + 44);
            case 4 -> {
                context.drawText(textRenderer, Text.literal("§7Selected: §a" + selectedSkills.size() + "/2 §7starter arts"), left + 20, top + WIDGET_HEIGHT - 44, 0xFFFFFFFF, false);
                List<SkillInfo> allowed = getStarterSkillsForDualClass();
                int maxPages = Math.max(1, (int) Math.ceil((double) allowed.size() / 6));
                if (maxPages > 1) {
                    String pStr = (skillsPage + 1) + " / " + maxPages;
                    int pw = textRenderer.getWidth(pStr);
                    context.drawText(textRenderer, Text.literal("§e" + pStr), left + (WIDGET_WIDTH - pw) / 2, top + 152, 0xFFFFFFFF, true);
                }
            }
            case 5 -> renderSummary(context, left, top + 36);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderPrologueText(DrawContext context, int left, int y) {
        int cx = left + 20;
        context.drawText(textRenderer, Text.literal("§6§l[Goddess of Rebirth]"), cx, y, 0xFFFFD700, true);
        context.drawText(textRenderer, Text.literal("§f\"Awaken, chosen soul... You have been summoned from another world.\""), cx, y + 15, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("§7Before you take physical form and descend to the mortal lands,"), cx, y + 30, 0xFFAAAAAA, false);
        context.drawText(textRenderer, Text.literal("§7I grant you the sacred gifts of rebirth:"), cx, y + 42, 0xFFAAAAAA, false);

        context.drawText(textRenderer, Text.literal("§b✦ One Species §7— Choose from 6 unique fantasy races"), cx + 8, y + 58, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("§e✦ Two Combat Classes §7— Combine martial & magical paths"), cx + 8, y + 72, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("§a✦ Two Starting Arts §7— Initial combat & magical skills"), cx + 8, y + 86, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("§6✦ World Fate §7— Set the difficulty of your journey"), cx + 8, y + 100, 0xFFFFFFFF, false);

        context.drawText(textRenderer, Text.literal("§d\"Embrace your calling, and carve your legend into eternity!\""), cx, y + 118, 0xFFFFB6C1, true);

        // Name Prompt
        context.drawText(textRenderer, Text.literal("§eCharacter Name:"), cx, y + 142, 0xFFFFD700, true);
    }

    private void renderRaceDesc(DrawContext context, int x, int y) {
        String info = switch (selectedRace.toLowerCase()) {
            case "elf" -> "§bElf\n§a+40 Max Mana\n§a+10% Movement Speed\n§a1.5x Mana Regen\n\n§c-2 Max Health (Frail)\n\n§7Evolutions:\n§eHigh Elf -> Ancient Elf -> Elder Elf";
            case "beastkin" -> "§6Beastkin\n§a+3 Strength, +20 Max Stamina\n§aPermanent Night Vision\n§a+10% Dash Speed\n\n§c-15 Max Mana\n\n§7Evolutions:\n§eAdv. Beastkin -> Feral Monarch";
            case "demonkin" -> "§4Demonkin\n§a+4 Strength, +20 Max Mana\n§a100% Fire & Lava Immunity\n§a5% Melee Lifesteal\n\n§c+50% Holy Vulnerability\n\n§7Evolutions:\n§eHigh Demon -> Archdemon";
            case "dragonoid" -> "§6Dragonoid\n§a+3 Strength\n§a100% Fire & Lava Immunity\n§aDragon Breath Damage Bonus\n\n§c-5% Speed, +50% Cold Vulnerability\n\n§7Evolutions:\n§eElder Dragonkin -> Dragon King";
            case "slime" -> "§aSlime\n§a75% Fall Damage Reduction\n§aPassive Health Regeneration\n\n§c-4 Max Health, -2 Defense\n\n§7Evolutions:\n§eAdv. Slime -> Crystal Slime";
            default -> "§fHuman\n§a+15% Essence Gain from All Sources\n§fBalanced base statistics\n§7No inherent weaknesses\n\n§7Evolutions:\n§eAwakened Human -> Heroic Human -> Saint";
        };
        drawMultiline(context, info, x, y);
    }

    private void renderDualClassPreview(DrawContext context, int left, int y) {
        context.drawText(textRenderer, Text.literal("§6Primary Class"), left + 32, y + 2, 0xFFFFD700, true);
        context.drawText(textRenderer, Text.literal("§bSecondary Class"), left + 146, y + 2, 0xFF00D2FF, true);

        // Hybrid Title Showcase
        String hybrid = CharacterClass.getCombinedTitle(primaryClass, secondaryClass);
        context.fill(left + 254, y + 16, left + WIDGET_WIDTH - 16, y + 78, 0xAA0B1324);
        context.drawBorder(left + 254, y + 16, WIDGET_WIDTH - 270, 62, 0xFFFFD700);

        context.drawText(textRenderer, Text.literal("§6✦ HYBRID DISCIPLINE ✦"), left + 262, y + 22, 0xFFFFD700, true);
        context.drawText(textRenderer, Text.literal("§e§l" + hybrid), left + 262, y + 36, 0xFFFFFFFF, true);

        String bonusHint = primaryClass.equalsIgnoreCase(secondaryClass) ? "§7Pure Class (100% mastery)" : "§aDual Hybrid Synergy!";
        context.drawText(textRenderer, Text.literal(bonusHint), left + 262, y + 50, 0xFFAAAAAA, false);

        // Short descriptions
        context.drawText(textRenderer, Text.literal("§7[1] " + CharacterClass.getClassDescription(primaryClass)), left + 20, y + 144, 0xFFCCCCCC, false);
        context.drawText(textRenderer, Text.literal("§7[2] " + CharacterClass.getClassDescription(secondaryClass)), left + 20, y + 160, 0xFFCCCCCC, false);
    }

    private void renderFateAndAllocationDesc(DrawContext context, int x, int y) {
        String info = switch (selectedDifficulty.toLowerCase()) {
            case "easy" -> "§aEasy Mode\n§7Mob HP: 75%\n§7Damage: 75%\n§7Essence: 75%\n§fRelaxed adventure.";
            case "hard" -> "§cHard Mode\n§7Mob HP: 150%\n§7Damage: 125%\n§7Essence: 150%\n§fHigher risk & reward.";
            case "nightmare" -> "§4§lNightmare Mode\n§7Mob HP: 250%\n§7Damage: 175%\n§7Essence: 250%\n§fRuthless challenge.";
            default -> "§eNormal Mode\n§7Mob HP: 100%\n§7Damage: 100%\n§7Essence: 100%\n§fStandard RPG balance.";
        };
        drawMultiline(context, info, x, y);

        context.drawText(textRenderer, Text.literal("§6Allocation:"), x, y + 76, 0xFFFFD700, true);
        drawMultiline(context, "§7" + selectedAllocation.getDescription(), x, y + 88);
    }

    private void renderSummary(DrawContext context, int left, int y) {
        int cx = left + 24;
        String combinedTitle = CharacterClass.getCombinedTitle(primaryClass, secondaryClass);

        context.drawText(textRenderer, Text.literal("§7Character Name: §e" + characterName), cx, y, 0xFFFFFFFF, true);
        context.drawText(textRenderer, Text.literal("§7Chosen Species: §b" + selectedRace), cx, y + 15, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("§7Combat Classes: §6" + primaryClass + " §7+ §b" + secondaryClass + " §e(" + combinedTitle + ")"), cx, y + 30, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("§7World Fate: §6" + selectedDifficulty + " §7| Allocation: §a" + selectedAllocation.getDisplayName()), cx, y + 45, 0xFFFFFFFF, false);

        String skill1Name = selectedSkills.size() > 0 ? SkillInfo.getById(selectedSkills.get(0)).displayName() : "None";
        String skill2Name = selectedSkills.size() > 1 ? SkillInfo.getById(selectedSkills.get(1)).displayName() : "None";
        context.drawText(textRenderer, Text.literal("§7Starter Arts: §a" + skill1Name + "§7, §a" + skill2Name), cx, y + 60, 0xFFFFFFFF, false);

        context.fill(left + 20, y + 80, left + WIDGET_WIDTH - 20, y + 114, 0x880B1424);
        context.drawBorder(left + 20, y + 80, WIDGET_WIDTH - 40, 34, 0xFF00D2FF);
        context.drawText(textRenderer, Text.literal("§eYour soul pact is engraved into the records of the Goddess."), cx + 8, y + 86, 0xFF00E5FF, false);
        context.drawText(textRenderer, Text.literal("§fDescend now, Ascender, and carve your legend into eternity!"), cx + 8, y + 98, 0xFFFFFFFF, false);
    }

    private void drawMultiline(DrawContext context, String text, int x, int y) {
        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            context.drawText(textRenderer, Text.literal(lines[i]), x, y + (i * 12), 0xFFFFFFFF, false);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
