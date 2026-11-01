package net.ascend.mod.client.gui;

import net.ascend.mod.client.gui.components.RpgTheme;
import net.ascend.mod.data.ArtifactProgress;
import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ArtifactProgressionScreen extends Screen {
    private static final int CARD_WIDTH = 390;
    private static final int CARD_HEIGHT = 245;

    private int selectedIdx = 0;

    public ArtifactProgressionScreen() {
        super(Text.literal("Story Artifacts"));
    }

    @Override
    protected void init() {
        super.init();
        clearChildren();
        if (client == null || client.player == null) return;

        int left = (width - CARD_WIDTH) / 2;
        int top = (height - CARD_HEIGHT) / 2;
        PlayerData data = ((IPlayerDataAccessor) client.player).ascend$getPlayerData();

        List<ArtifactProgress.StoryArtifact> artifacts = ArtifactProgress.ALL_ARTIFACTS;

        // Left side Artifact List
        int listY = top + 34;
        for (int i = 0; i < artifacts.size(); i++) {
            final int idx = i;
            ArtifactProgress.StoryArtifact a = artifacts.get(i);
            boolean isCur = idx == selectedIdx;
            boolean hasIt = data.getArtifactProgress().hasArtifact(a.id());

            String prefix = hasIt ? "§a[✓] " : "§8[🔒] ";
            String label = (isCur ? "§6▶ " : "") + prefix + a.name();
            if (label.length() > 20) label = label.substring(0, 19) + "...";

            ButtonWidget btn = ButtonWidget.builder(Text.literal(label), b -> {
                selectedIdx = idx;
                clearAndInit();
            }).dimensions(left + 16, listY + (i * 24), 146, 21).build();

            addDrawableChild(btn);
        }

        // Back to Dungeons button
        ButtonWidget backBtn = ButtonWidget.builder(Text.literal("◀ Dungeons"), b -> {
            if (client != null) client.setScreen(new DungeonRankScreen());
        }).dimensions(left + 16, top + CARD_HEIGHT - 26, 85, 18).build();
        addDrawableChild(backBtn);

        // Close button
        ButtonWidget closeBtn = ButtonWidget.builder(Text.literal("Close"), b -> close())
                .dimensions(left + CARD_WIDTH - 74, top + CARD_HEIGHT - 26, 58, 18).build();
        addDrawableChild(closeBtn);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        int left = (width - CARD_WIDTH) / 2;
        int top = (height - CARD_HEIGHT) / 2;

        RpgTheme.drawCard(context, left, top, CARD_WIDTH, CARD_HEIGHT, true);
        RpgTheme.drawHeader(context, textRenderer, "✦ COSMIC ARTIFACTS & WORLDS ✦", left, top + 8, CARD_WIDTH, true);

        if (client != null && client.player != null) {
            PlayerData data = ((IPlayerDataAccessor) client.player).ascend$getPlayerData();

            // Divider line
            context.fill(left + 168, top + 32, left + 169, top + CARD_HEIGHT - 32, 0x5500D2FF);

            // Right side Artifact Detail View
            if (selectedIdx >= 0 && selectedIdx < ArtifactProgress.ALL_ARTIFACTS.size()) {
                renderArtifactDetails(context, left + 178, top + 34, ArtifactProgress.ALL_ARTIFACTS.get(selectedIdx), data);
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderArtifactDetails(DrawContext context, int x, int y, ArtifactProgress.StoryArtifact a, PlayerData data) {
        boolean hasIt = data.getArtifactProgress().hasArtifact(a.id());

        // Header
        context.drawText(textRenderer, Text.literal("§e§l" + a.name()), x, y, 0xFFFFFFFF, true);
        String origin = "§7Rank " + a.rank() + " §8| §b" + a.originDungeon();
        context.drawText(textRenderer, Text.literal(origin), x, y + 13, 0xFFE0E0E0, false);

        // Acquisition Status Badge
        String badge = hasIt ? "§a✦ SOUL BOUND (UNLOCKED) ✦" : "§c🔒 UNDISCOVERED IN DUNGEON DEPTHS";
        context.drawText(textRenderer, Text.literal(badge), x, y + 26, hasIt ? 0xFF55FF55 : 0xFFFF5555, true);

        // Lore Description
        drawMultiline(context, "§f" + a.description(), x, y + 42, 192);

        // Dimensional Access Box
        int dimY = y + 84;
        context.fill(x, dimY, x + 195, dimY + 48, 0x770D182E);
        context.drawBorder(x, dimY, 195, 48, 0x4400D2FF);

        context.drawText(textRenderer, Text.literal("§6Dimensional Resonance:"), x + 6, dimY + 5, 0xFFFFD700, true);
        context.drawText(textRenderer, Text.literal("§7Bound Realm: §b" + a.requiredForDimension()), x + 6, dimY + 18, 0xFFFFFFFF, false);

        String accessStr = hasIt ? "§aAccess Permitted ✓" : "§cRequires Artifact to Pierce Barrier";
        context.drawText(textRenderer, Text.literal(accessStr), x + 6, dimY + 31, 0xFFFFFFFF, false);

        // Story Relevance Note
        int noteY = dimY + 54;
        context.drawText(textRenderer, Text.literal("§6Story Significance:"), x, noteY, 0xFFFFD700, false);
        String note = "Cannot be bypassed by high character levels. Every Ascender must claim the artifact from the dungeon's apex guardian.";
        drawMultiline(context, "§7" + note, x, noteY + 12, 195);
    }

    private void drawMultiline(DrawContext context, String text, int x, int y, int wrapWidth) {
        List<net.minecraft.text.OrderedText> lines = textRenderer.wrapLines(Text.literal(text), wrapWidth);
        for (int i = 0; i < lines.size(); i++) {
            context.drawText(textRenderer, lines.get(i), x, y + (i * 11), 0xFFFFFFFF, false);
        }
    }
}
