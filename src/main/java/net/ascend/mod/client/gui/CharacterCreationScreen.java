package net.ascend.mod.client.gui;

import net.ascend.mod.network.AscendClientPackets;
import net.ascend.mod.skills.SkillInfo;
import net.ascend.mod.skills.SkillRank;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CharacterCreationScreen extends Screen {
    private static final int WIDGET_WIDTH = 390;
    private static final int WIDGET_HEIGHT = 245;

    // 0: Prologue, 1: Race, 2: Class, 3: Difficulty, 4: Skills, 5: Confirm
    private int currentStep = 0;
    private int skillsPage = 0;

    private String selectedRace = "Human";
    private String selectedClass = "Swordsman";
    private String selectedDifficulty = "Normal";
    private final List<String> selectedSkills = new ArrayList<>();

    public CharacterCreationScreen() {
        super(Text.literal("Ascend - Character Awakening"));
        selectedSkills.add("basic_step");
        selectedSkills.add("flame_slash");
    }

    @Override
    protected void init() {
        super.init();
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
            case 2 -> initClassStep(left, contentY);
            case 3 -> initDifficultyStep(left, contentY);
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
        }

        if (currentStep > 0 && currentStep < 5) {
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
        ButtonWidget proceedBtn = ButtonWidget.builder(Text.literal("✦ ACCEPT YOUR DESTINY & BEGIN ✦"), button -> {
            currentStep = 1;
            rebuildStepUI();
        }).dimensions(left + (WIDGET_WIDTH - 240) / 2, top + WIDGET_HEIGHT - 34, 240, 24).build();
        addDrawableChild(proceedBtn);
    }

    private void validateSelectedSkills() {
        List<SkillInfo> allowed = getStarterSkillsForClass(selectedClass);
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

    private List<SkillInfo> getStarterSkillsForClass(String pClass) {
        List<SkillInfo> classSkills = SkillInfo.getSkillsForClass(pClass);
        List<SkillInfo> starters = new ArrayList<>();
        for (SkillInfo s : classSkills) {
            if (s.rank() == SkillRank.E) {
                starters.add(s);
            }
        }
        if (starters.isEmpty()) {
            for (SkillInfo s : SkillInfo.getSkillsByRank(SkillRank.E)) {
                starters.add(s);
            }
        }
        return starters;
    }

    private void initRaceStep(int left, int contentY) {
        String[] races = {"Human", "Elf", "Beastkin", "Demonkin", "Dragonoid", "Slime"};
        for (int i = 0; i < races.length; i++) {
            String race = races[i];
            boolean isSel = race.equalsIgnoreCase(selectedRace);
            Text label = Text.literal((isSel ? "§b▶ §l" : "  ") + race);
            ButtonWidget btn = ButtonWidget.builder(label, button -> {
                selectedRace = race;
                rebuildStepUI();
            }).dimensions(left + 24, contentY + (i * 22), 110, 20).build();
            addDrawableChild(btn);
        }
    }

    private void initClassStep(int left, int contentY) {
        String[] classes = {"Swordsman", "Summoner", "Mage", "Archer", "Tank"};
        for (int i = 0; i < classes.length; i++) {
            String cls = classes[i];
            boolean isSel = cls.equalsIgnoreCase(selectedClass);
            Text label = Text.literal((isSel ? "§e▶ §l" : "  ") + cls);
            ButtonWidget btn = ButtonWidget.builder(label, button -> {
                selectedClass = cls;
                validateSelectedSkills();
                rebuildStepUI();
            }).dimensions(left + 20, contentY + (i * 25), 110, 22).build();
            addDrawableChild(btn);
        }
    }

    private void initDifficultyStep(int left, int contentY) {
        String[] diffs = {"Easy", "Normal", "Hard", "Nightmare"};
        for (int i = 0; i < diffs.length; i++) {
            String diff = diffs[i];
            boolean isSel = diff.equalsIgnoreCase(selectedDifficulty);
            Text label = Text.literal((isSel ? "§6▶ §l" : "  ") + diff);
            ButtonWidget btn = ButtonWidget.builder(label, button -> {
                selectedDifficulty = diff;
                rebuildStepUI();
            }).dimensions(left + 24, contentY + (i * 26), 110, 22).build();
            addDrawableChild(btn);
        }
    }

    private void initSkillsStep(int left, int contentY) {
        List<SkillInfo> allowed = getStarterSkillsForClass(selectedClass);
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
            int btnX = left + 18 + (col * 180);
            int btnY = contentY + (row * 28);

            ButtonWidget btn = ButtonWidget.builder(label, button -> {
                if (selectedSkills.contains(skill.id())) {
                    if (selectedSkills.size() > 1) selectedSkills.remove(skill.id());
                } else {
                    if (selectedSkills.size() >= 2) selectedSkills.remove(0);
                    selectedSkills.add(skill.id());
                }
                rebuildStepUI();
            }).dimensions(btnX, btnY, 172, 24).tooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.literal("§e[" + skill.tag() + "] §f" + skill.description()))).build();

            addDrawableChild(btn);
        }

        // Pagination buttons
        if (maxPages > 1) {
            int pageY = contentY + 90;
            ButtonWidget prevBtn = ButtonWidget.builder(Text.literal("◀ Prev"), b -> {
                skillsPage--;
                rebuildStepUI();
            }).dimensions(left + 100, pageY, 60, 20).build();
            prevBtn.active = skillsPage > 0;
            addDrawableChild(prevBtn);

            ButtonWidget nextBtn = ButtonWidget.builder(Text.literal("Next ▶"), b -> {
                skillsPage++;
                rebuildStepUI();
            }).dimensions(left + 230, pageY, 60, 20).build();
            nextBtn.active = skillsPage < maxPages - 1;
            addDrawableChild(nextBtn);
        }
    }

    private void initConfirmStep(int left, int contentY) {
        ButtonWidget awakenBtn = ButtonWidget.builder(Text.literal("✦ AWAKEN & DESCEND TO OVERWORLD ✦"), button -> {
            AscendClientPackets.sendCompleteCreation(selectedDifficulty, selectedRace, selectedClass, selectedSkills);
            close();
        }).dimensions(left + (WIDGET_WIDTH - 250) / 2, contentY + 120, 250, 26).build();
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
            case 1 -> "STEP 1/4: CHOOSE YOUR RACE";
            case 2 -> "STEP 2/4: CHOOSE YOUR CLASS";
            case 3 -> "STEP 3/4: CHOOSE DIFFICULTY";
            case 4 -> "STEP 4/4: CHOOSE 2 STARTING ARTS";
            default -> "✦ CONFIRMATION & DESCENT ✦";
        };
        int titleW = textRenderer.getWidth(stepTitle);
        int titleColor = currentStep == 0 ? 0xFFFFD700 : 0xFF00E5FF;
        context.drawText(textRenderer, Text.literal("§l" + stepTitle), left + (WIDGET_WIDTH - titleW) / 2, top + 12, titleColor, true);
        context.fill(left + 16, top + 26, left + WIDGET_WIDTH - 16, top + 27, 0x8800D2FF);

        int descX = left + 144;
        int descY = top + 42;

        switch (currentStep) {
            case 0 -> renderPrologueText(context, left, top + 36);
            case 1 -> renderRaceDesc(context, descX, descY);
            case 2 -> renderClassDesc(context, descX, descY);
            case 3 -> renderDifficultyDesc(context, descX, descY);
            case 4 -> {
                context.drawText(textRenderer, Text.literal("§7Selected: §a" + selectedSkills.size() + "/2 §7starter arts"), left + 20, top + WIDGET_HEIGHT - 44, 0xFFFFFFFF, false);
                List<SkillInfo> allowed = getStarterSkillsForClass(selectedClass);
                int maxPages = Math.max(1, (int) Math.ceil((double) allowed.size() / 6));
                if (maxPages > 1) {
                    String pStr = (skillsPage + 1) + " / " + maxPages;
                    int pw = textRenderer.getWidth(pStr);
                    context.drawText(textRenderer, Text.literal("§e" + pStr), left + (WIDGET_WIDTH - pw) / 2, top + 152, 0xFFFFFFFF, true);
                }
            }
            case 5 -> renderSummary(context, left, top + 40);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderPrologueText(DrawContext context, int left, int y) {
        int cx = left + 20;
        context.drawText(textRenderer, Text.literal("§6§l[Goddess of Rebirth]"), cx, y, 0xFFFFD700, true);
        context.drawText(textRenderer, Text.literal("§f\"Awaken, chosen soul... You have been summoned from another world.\""), cx, y + 16, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("§7Before you take physical form and descend to the mortal lands,"), cx, y + 32, 0xFFAAAAAA, false);
        context.drawText(textRenderer, Text.literal("§7I grant you the sacred gifts of rebirth:"), cx, y + 44, 0xFFAAAAAA, false);

        context.drawText(textRenderer, Text.literal("§b✦ One Species §7— Choose heritage (Human, Elf, Beastkin, Demonkin, Dragonoid, Slime)"), cx + 8, y + 62, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("§e✦ One Combat Class §7— Dictate your path of power"), cx + 8, y + 76, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("§a✦ Two Starting Arts §7— Your initial combat & magical skills"), cx + 8, y + 90, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("§6✦ World Fate §7— Set the difficulty of your journey"), cx + 8, y + 104, 0xFFFFFFFF, false);

        context.drawText(textRenderer, Text.literal("§d\"Embrace your calling, and carve your legend into eternity!\""), cx, y + 124, 0xFFFFB6C1, true);
    }

    private void renderRaceDesc(DrawContext context, int x, int y) {
        String info = switch (selectedRace.toLowerCase()) {
            case "elf" -> "§bElf\n§a+40 Max Mana\n§a+10% Movement Speed\n§a1.5x Mana Regen\n\n§c-2 Max Health (Frail)";
            case "beastkin" -> "§6Beastkin\n§a+3 Strength\n§a+20 Max Stamina\n§aNight Vision\n§a+10% Dash Speed\n\n§c-15 Max Mana";
            case "demonkin" -> "§4Demonkin\n§a+4 Strength\n§a+20 Max Mana\n§aFire & Lava Immunity\n§a5% Melee Lifesteal\n\n§c+50% Holy Vulnerability";
            case "dragonoid" -> "§6Dragonoid\n§a+3 Strength\n§aFire Immunity\n§aBreath bonus\n\n§c-5% Speed\n§c+50% Cold vulnerability";
            case "slime" -> "§aSlime\n§a0 Fall Damage\n§aPassive Health Regen\n§aBouncy Jump\n\n§c-4 Max Health\n§c-2 Defense";
            default -> "§fHuman\n§a+15% Essence Gain\n§fBalanced base stats\n\n§7No inherent weaknesses.";
        };
        drawMultiline(context, info, x, y);
    }

    private void renderClassDesc(DrawContext context, int x, int y) {
        String info = switch (selectedClass.toLowerCase()) {
            case "summoner" -> "§dSummoner\n§7Minions fight for you.\n§a+20 Mana\n§c-2 Health\n\n§eTags: §fsummon, support, magic";
            case "mage" -> "§9Mage\n§7Devastating ranged spells.\n§a+30 Mana\n§c-2 Defense\n\n§eTags: §fmagic, support, mobility";
            case "archer" -> "§eArcher\n§7Precision ranged attacks.\n§a+2 Speed, +1 Strength\n§c-2 Defense\n\n§eTags: §franged, mobility, support";
            case "tank" -> "§6Tank\n§7Indomitable defender.\n§a+4 Defense, +4 Health\n§c-2 Speed\n\n§eTags: §fdefense, melee, support";
            default -> "§cSwordsman\n§7Rapid combos & slashes.\n§a+2 Strength, +1 Speed\n§cUses stamina\n\n§eTags: §fmelee, mobility, defense";
        };
        drawMultiline(context, info, x, y);
    }

    private void renderDifficultyDesc(DrawContext context, int x, int y) {
        String info = switch (selectedDifficulty.toLowerCase()) {
            case "easy" -> "§aEasy Mode\n§7Mob HP: 75%\n§7Damage: 75%\n§7Essence: 75%\n\n§fRelaxed adventure.";
            case "hard" -> "§cHard Mode\n§7Mob HP: 150%\n§7Damage: 125%\n§7Essence: 150%\n\n§fHigher risk & reward.";
            case "nightmare" -> "§4§lNightmare Mode\n§7Mob HP: 250%\n§7Damage: 175%\n§7Essence: 250%\n\n§fRuthless challenge.";
            default -> "§eNormal Mode\n§7Mob HP: 100%\n§7Damage: 100%\n§7Essence: 100%\n\n§fStandard RPG balance.";
        };
        drawMultiline(context, info, x, y);
    }

    private void renderSummary(DrawContext context, int left, int y) {
        int centerX = left + 20;
        context.drawText(textRenderer, Text.literal("§7Race: §b" + selectedRace), centerX, y, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("§7Class: §6" + selectedClass), centerX, y + 16, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("§7Difficulty: §e" + selectedDifficulty), centerX, y + 32, 0xFFFFFFFF, false);

        String skill1Name = selectedSkills.size() > 0 ? SkillInfo.getById(selectedSkills.get(0)).displayName() : "None";
        String skill2Name = selectedSkills.size() > 1 ? SkillInfo.getById(selectedSkills.get(1)).displayName() : "None";
        context.drawText(textRenderer, Text.literal("§7Starting Arts: §a" + skill1Name + "§7, §a" + skill2Name), centerX, y + 48, 0xFFFFFFFF, false);

        context.drawText(textRenderer, Text.literal("§fYour pact is prepared. Are you ready to awaken?"), centerX, y + 74, 0xFF00E5FF, false);
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
