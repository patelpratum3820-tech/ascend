package net.ascend.mod.client.gui.components;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class RpgTheme {
    // Primary Color Palette
    public static final int BG_DARK = 0xF0080D1A;
    public static final int BG_PANEL = 0xCC0D162B;
    public static final int BG_SLOT = 0xDD111B33;

    public static final int BORDER_CYAN = 0xFF00D2FF;
    public static final int BORDER_GOLD = 0xFFFFD700;
    public static final int BORDER_MUTED = 0x55005577;
    public static final int BORDER_DARK = 0xFF1B2844;

    public static final int TEXT_GOLD = 0xFFFFD700;
    public static final int TEXT_CYAN = 0xFF00E5FF;
    public static final int TEXT_WHITE = 0xFFFFFFFF;
    public static final int TEXT_MUTED = 0xFFAAAAAA;
    public static final int TEXT_GREEN = 0xFF2ECC71;
    public static final int TEXT_RED = 0xFFE74C3C;
    public static final int TEXT_PURPLE = 0xFFAF52DE;

    public static final int BAR_HEALTH = 0xFFE74C3C;
    public static final int BAR_MANA = 0xFF00D2FF;
    public static final int BAR_STAMINA = 0xFF2ECC71;
    public static final int BAR_XP = 0xFFFFD700;

    /**
     * Renders a standardized fantasy RPG panel with inner subtle fill and outer border.
     */
    public static void drawCard(DrawContext context, int x, int y, int width, int height, boolean goldAccent) {
        // Deep charcoal backdrop
        context.fill(x, y, x + width, y + height, BG_DARK);

        // Accent outer border
        int outerColor = goldAccent ? BORDER_GOLD : BORDER_CYAN;
        context.drawBorder(x, y, width, height, outerColor);
        context.drawBorder(x + 2, topY(y, 2), width - 4, height - 4, BORDER_MUTED);
    }

    private static int topY(int y, int offset) {
        return y + offset;
    }

    /**
     * Renders a styled header with glowing separator line.
     */
    public static void drawHeader(DrawContext context, TextRenderer tr, String title, int x, int y, int width, boolean goldAccent) {
        int titleColor = goldAccent ? TEXT_GOLD : TEXT_CYAN;
        int titleW = tr.getWidth(title);
        context.drawText(tr, Text.literal("§l" + title), x + (width - titleW) / 2, y, titleColor, true);

        // Header separator bar
        int sepColor = goldAccent ? 0x88FFD700 : 0x8800D2FF;
        context.fill(x + 12, y + 14, x + width - 12, y + 15, sepColor);
    }

    /**
     * Renders an RPG progress bar with dark bevel and percentage fill.
     */
    public static void drawProgressBar(DrawContext context, int x, int y, int width, int height, double percent, int fillColor, int borderColor) {
        percent = Math.max(0.0, Math.min(1.0, percent));
        int fillW = (int) (percent * (width - 2));

        // Background
        context.fill(x, y, x + width, y + height, 0xEE090D18);
        // Fill
        if (fillW > 0) {
            context.fill(x + 1, y + 1, x + 1 + fillW, y + height - 1, fillColor);
        }
        // Border
        context.drawBorder(x, y, width, height, borderColor);
    }

    /**
     * Renders a small badge tag with high-contrast text.
     */
    public static void drawBadge(DrawContext context, TextRenderer tr, String text, int x, int y, int badgeColor) {
        int w = tr.getWidth(text) + 6;
        int h = 10;
        context.fill(x, y, x + w, y + h, 0xDD000000);
        context.drawBorder(x, y, w, h, badgeColor);
        context.drawText(tr, Text.literal(text), x + 3, y + 1, badgeColor, false);
    }
}
