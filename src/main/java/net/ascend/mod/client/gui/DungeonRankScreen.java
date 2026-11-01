package net.ascend.mod.client.gui;

import net.ascend.mod.client.gui.components.RpgTheme;
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
public class DungeonRankScreen extends Screen {
    private static final int CARD_WIDTH = 390;
    private static final int CARD_HEIGHT = 245;

    public record DungeonTier(
            String rank,
            String name,
            String location,
            int recommendedLevel,
            int requiredClears,
            String bossName,
            String artifactReward,
            String lore
    ) {}

    public static final List<DungeonTier> TIERS = List.of(
            new DungeonTier("E", "Goblin Caves", "Great Lake Basin (Spawn < 250m)", 10, 3, "Goblin King", "Heart of the Great Lake", "Moist subterranean tunnels inhabited by feral goblin warbands guarding ancient sunken relics."),
            new DungeonTier("D", "Shadow Crypt", "Ancient Forest (250m - 500m)", 25, 5, "Shadow Knight", "Ancient Forest Seed", "Overgrown mossy stone catacombs haunted by dark wraiths and forgotten undead knights."),
            new DungeonTier("C", "Dragon's Ravine", "Crystal Caverns (500m - 1000m)", 40, 5, "Crystal Golem", "Dragon's Gaze Orb", "Crystalline fissures lined with jagged amethyst spikes and smoldering drakescale caverns."),
            new DungeonTier("B", "Forge Ruins", "Volcano & Frozen Peaks (1000m - 2000m)", 60, 7, "Infernal Drake", "Frost Ember Core", "Ancient dwarven magma smelters frozen over by primordial elemental paradoxes."),
            new DungeonTier("A", "Nether Citadel", "Nether Fortress Depths (2000m - 4000m)", 80, 10, "Nether Sovereign", "Nether Sovereign Crown", "Impenetrable volcanic bastions guarding the throne of the Nether Sovereign."),
            new DungeonTier("S", "Sky Citadel", "Floating Cloud Sanctum (> 4000m)", 100, 15, "Void Lich / Divine Herald", "Astral Key & Divine Blessing", "The celestial pinnacle floating among the clouds, serving as the ultimate trial of the Chosen One.")
    );

    private int selectedTierIdx = 0;

    public DungeonRankScreen() {
        super(Text.literal("Dungeon Rank Ladder"));
    }

    @Override
    protected void init() {
        super.init();
        clearChildren();
        if (client == null || client.player == null) return;

        int left = (width - CARD_WIDTH) / 2;
        int top = (height - CARD_HEIGHT) / 2;

        // Left Tier Buttons (Ranks E to S)
        int listY = top + 36;
        for (int i = 0; i < TIERS.size(); i++) {
            final int idx = i;
            DungeonTier t = TIERS.get(i);
            boolean isCur = idx == selectedTierIdx;

            String label = (isCur ? "§6▶ §l" : "  ") + "Rank " + t.rank() + ": " + t.name();
            if (label.length() > 18) label = label.substring(0, 17) + "...";

            ButtonWidget btn = ButtonWidget.builder(Text.literal(label), b -> {
                selectedTierIdx = idx;
                clearAndInit();
            }).dimensions(left + 16, listY + (i * 28), 136, 24).build();

            addDrawableChild(btn);
        }

        // View Artifacts button
        ButtonWidget artifactsBtn = ButtonWidget.builder(Text.literal("§e✦ Story Artifacts"), b -> {
            if (client != null) client.setScreen(new ArtifactProgressionScreen());
        }).dimensions(left + 16, top + CARD_HEIGHT - 28, 120, 20).build();
        addDrawableChild(artifactsBtn);

        // Close button
        ButtonWidget closeBtn = ButtonWidget.builder(Text.literal("Close"), b -> close())
                .dimensions(left + CARD_WIDTH - 74, top + CARD_HEIGHT - 28, 58, 20).build();
        addDrawableChild(closeBtn);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        int left = (width - CARD_WIDTH) / 2;
        int top = (height - CARD_HEIGHT) / 2;

        RpgTheme.drawCard(context, left, top, CARD_WIDTH, CARD_HEIGHT, true);
        RpgTheme.drawHeader(context, textRenderer, "✦ HUNTER RANK & DUNGEONS ✦", left, top + 8, CARD_WIDTH, true);

        if (client != null && client.player != null) {
            PlayerData data = ((IPlayerDataAccessor) client.player).ascend$getPlayerData();

            // Divider line
            context.fill(left + 158, top + 32, left + 159, top + CARD_HEIGHT - 34, 0x5500D2FF);

            // Right side detail view
            if (selectedTierIdx >= 0 && selectedTierIdx < TIERS.size()) {
                renderTierDetails(context, left + 168, top + 34, TIERS.get(selectedTierIdx), data);
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderTierDetails(DrawContext context, int x, int y, DungeonTier t, PlayerData data) {
        String pRank = data.getRank();
        int clears = data.getDungeonProgress().getClears(t.rank());
        int bestTime = data.getDungeonProgress().getBestTime(t.rank());

        // Header Title
        context.drawText(textRenderer, Text.literal("§6§l[Rank " + t.rank() + "] §f" + t.name()), x, y, 0xFFFFFFFF, true);
        context.drawText(textRenderer, Text.literal("§7Location: §b" + t.location()), x, y + 14, 0xFFE0E0E0, false);
        context.drawText(textRenderer, Text.literal("§7Req. Level: §eLv. " + t.recommendedLevel() + " §7| Boss: §c" + t.bossName()), x, y + 26, 0xFFE0E0E0, false);

        // Lore Description
        drawMultiline(context, "§f" + t.lore(), x, y + 42, 200);

        // Clearance Record Box
        int recY = y + 84;
        context.fill(x, recY, x + 200, recY + 44, 0x770D182E);
        context.drawBorder(x, recY, 200, 44, 0x4400D2FF);

        context.drawText(textRenderer, Text.literal("§6Dungeon Records:"), x + 6, recY + 5, 0xFFFFD700, true);
        context.drawText(textRenderer, Text.literal("§7Total Clears: §a" + clears + " times"), x + 6, recY + 18, 0xFFFFFFFF, false);

        String timeStr = bestTime > 0 ? (bestTime / 60) + "m " + (bestTime % 60) + "s" : "None";
        context.drawText(textRenderer, Text.literal("§7Record Clear Time: §e" + timeStr), x + 6, recY + 30, 0xFFFFFFFF, false);

        // Artifact Drop & Access
        int dropY = recY + 50;
        context.drawText(textRenderer, Text.literal("§6Compulsory Story Artifact:"), x, dropY, 0xFFFFD700, false);
        context.drawText(textRenderer, Text.literal("§b✦ " + t.artifactReward()), x + 6, dropY + 12, 0xFF00E5FF, false);

        // Promotion requirement status
        int reqClears = t.requiredClears();
        String promo = (clears >= reqClears) ? "§aPromotion Requirement Fulfilled ✓" : "§7Promotion Progress: §e" + clears + " / " + reqClears + " Clears";
        context.drawText(textRenderer, Text.literal(promo), x, dropY + 28, 0xFFFFFFFF, false);
    }

    private void drawMultiline(DrawContext context, String text, int x, int y, int wrapWidth) {
        List<net.minecraft.text.OrderedText> lines = textRenderer.wrapLines(Text.literal(text), wrapWidth);
        for (int i = 0; i < lines.size(); i++) {
            context.drawText(textRenderer, lines.get(i), x, y + (i * 11), 0xFFFFFFFF, false);
        }
    }
}
