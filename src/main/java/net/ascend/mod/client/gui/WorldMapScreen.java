package net.ascend.mod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.ascend.mod.client.gui.components.RpgTheme;
import net.ascend.mod.client.hud.AscendHudRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class WorldMapScreen extends Screen {

    private static final Identifier CONTINENT_MAP = new Identifier("ascend", "textures/gui/continent_map.png");
    private static final Identifier PLAYER_ARROW = new Identifier("ascend", "textures/gui/player_arrow.png");

    private static final int WIDGET_WIDTH = 340;
    private static final int WIDGET_HEIGHT = 230;

    private float zoomFactor = 1.0f; // 1.0x to 4.0x
    private float panOffsetX = 0f;
    private float panOffsetZ = 0f;

    private static record Landmark(String name, int worldX, int worldZ, int color) {}
    private static final List<Landmark> LANDMARKS = new ArrayList<>();

    static {
        LANDMARKS.add(new Landmark("✦ Temple of the Chosen", 0, 0, 0xFFFFD700));
        LANDMARKS.add(new Landmark("✦ Aurelia Capital", 0, 900, 0xFFFFFFFF));
        LANDMARKS.add(new Landmark("✦ Lake Veyra", 0, -700, 0xFF4DD8FF));
        LANDMARKS.add(new Landmark("✦ Frostspire Keep", 0, -3000, 0xFFE0F7FF));
        LANDMARKS.add(new Landmark("✦ Elarion Sanctuary", -2300, -800, 0xFF80FF80));
        LANDMARKS.add(new Landmark("✦ Prism Pools", -2500, 1300, 0xFF40FFE0));
        LANDMARKS.add(new Landmark("✦ Beastkin Steppe", -900, 2500, 0xFFFFD040));
        LANDMARKS.add(new Landmark("✦ Draconis Ravines", 2500, -900, 0xFFFF9040));
        LANDMARKS.add(new Landmark("✦ Inferno Keep", 2600, 1800, 0xFFFF4040));
        LANDMARKS.add(new Landmark("✦ Shattered Frontier", 500, 3500, 0xFFA0A0C0));
    }

    public WorldMapScreen() {
        super(Text.literal("ASCEND — Realm of the Chosen"));
    }

    @Override
    protected void init() {
        super.init();
        int left = (width - WIDGET_WIDTH) / 2;
        int top = (height - WIDGET_HEIGHT) / 2;

        // Zoom Controls
        addDrawableChild(ButtonWidget.builder(Text.literal("➕ In"), b -> zoomIn())
                .dimensions(left + 220, top + 36, 48, 18).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("➖ Out"), b -> zoomOut())
                .dimensions(left + 274, top + 36, 48, 18).build());

        // Minimap Zoom Cycle
        addDrawableChild(ButtonWidget.builder(Text.literal("Minimap: " + AscendHudRenderer.getMinimapZoomLabel()), b -> {
            AscendHudRenderer.cycleMinimapZoom();
            b.setMessage(Text.literal("Minimap: " + AscendHudRenderer.getMinimapZoomLabel()));
        }).dimensions(left + 220, top + 60, 102, 20).build());

        // Toggle Minimap HUD
        addDrawableChild(ButtonWidget.builder(Text.literal(AscendHudRenderer.minimapVisible ? "Minimap: ON" : "Minimap: OFF"), b -> {
            AscendHudRenderer.minimapVisible = !AscendHudRenderer.minimapVisible;
            b.setMessage(Text.literal(AscendHudRenderer.minimapVisible ? "Minimap: ON" : "Minimap: OFF"));
        }).dimensions(left + 220, top + 86, 102, 20).build());

        // Reset View
        addDrawableChild(ButtonWidget.builder(Text.literal("Center Player"), b -> centerOnPlayer())
                .dimensions(left + 220, top + 112, 102, 20).build());

        // Close Button
        addDrawableChild(ButtonWidget.builder(Text.literal("Close (ESC)"), b -> close())
                .dimensions(left + 220, top + WIDGET_HEIGHT - 28, 102, 20).build());
    }

    private void zoomIn() {
        zoomFactor = Math.min(4.0f, zoomFactor * 1.5f);
    }

    private void zoomOut() {
        zoomFactor = Math.max(1.0f, zoomFactor / 1.5f);
        if (zoomFactor <= 1.0f) {
            panOffsetX = 0f;
            panOffsetZ = 0f;
        }
    }

    private void centerOnPlayer() {
        if (client != null && client.player != null) {
            zoomFactor = 2.0f;
            panOffsetX = (float) client.player.getX();
            panOffsetZ = (float) client.player.getZ();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        int left = (width - WIDGET_WIDTH) / 2;
        int top = (height - WIDGET_HEIGHT) / 2;

        // Window Frame
        RpgTheme.drawCard(context, left, top, WIDGET_WIDTH, WIDGET_HEIGHT, false);
        RpgTheme.drawHeader(context, textRenderer, "✦ CONTINENT OF THE CHOSEN — WORLD MAP ✦", left, top + 10, WIDGET_WIDTH, true);

        // Map Viewport (Left side: 200x180)
        int mapBoxX = left + 12;
        int mapBoxY = top + 34;
        int mapBoxW = 200;
        int mapBoxH = 180;

        context.fill(mapBoxX, mapBoxY, mapBoxX + mapBoxW, mapBoxY + mapBoxH, 0xFF0D1B2A);
        context.drawBorder(mapBoxX, mapBoxY, mapBoxW, mapBoxH, 0xFF00D2FF);

        // Scissor viewport for map
        context.enableScissor(mapBoxX + 1, mapBoxY + 1, mapBoxX + mapBoxW - 1, mapBoxY + mapBoxH - 1);

        int fullMapPx = (int) (mapBoxW * zoomFactor);
        double centerWorldX = (zoomFactor > 1.0f) ? panOffsetX : 0.0;
        double centerWorldZ = (zoomFactor > 1.0f) ? panOffsetZ : 0.0;

        // Normalized (0..1)
        double normCenterX = (centerWorldX + 4000.0) / 8000.0;
        double normCenterZ = (centerWorldZ + 4000.0) / 8000.0;

        int mapOriginX = (mapBoxX + mapBoxW / 2) - (int) (normCenterX * fullMapPx);
        int mapOriginY = (mapBoxY + mapBoxH / 2) - (int) (normCenterZ * fullMapPx);

        // Render Continent Map Texture
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        context.drawTexture(CONTINENT_MAP, mapOriginX, mapOriginY, 0f, 0f, fullMapPx, fullMapPx, 512, 512);

        // Render Landmarks
        for (Landmark lm : LANDMARKS) {
            double nX = (lm.worldX() + 4000.0) / 8000.0;
            double nZ = (lm.worldZ() + 4000.0) / 8000.0;
            int lx = mapOriginX + (int) (nX * fullMapPx);
            int lz = mapOriginY + (int) (nZ * fullMapPx);

            context.fill(lx - 2, lz - 2, lx + 2, lz + 2, lm.color());
            if (zoomFactor >= 1.5f) {
                context.drawText(textRenderer, Text.literal("§7" + lm.name()), lx + 4, lz - 3, lm.color(), false);
            }
        }

        // Render Player Position
        if (client != null && client.player != null) {
            double pX = client.player.getX();
            double pZ = client.player.getZ();
            double nX = (pX + 4000.0) / 8000.0;
            double nZ = (pZ + 4000.0) / 8000.0;
            int px = mapOriginX + (int) (nX * fullMapPx);
            int pz = mapOriginY + (int) (nZ * fullMapPx);

            // Pulsating beacon ring
            long time = System.currentTimeMillis();
            int pulse = (int) (4 + 2 * Math.sin(time / 200.0));
            context.drawBorder(px - pulse, pz - pulse, pulse * 2, pulse * 2, 0xFFFFD700);

            // Player arrow heading
            context.getMatrices().push();
            context.getMatrices().translate(px, pz, 0);
            context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(client.player.getYaw() + 180f));
            context.drawTexture(PLAYER_ARROW, -4, -4, 0f, 0f, 8, 8, 16, 16);
            context.getMatrices().pop();
        }

        context.disableScissor();

        // Coordinates & Region Info panel on the right
        int infoX = left + 220;
        int infoY = top + 138;
        if (client != null && client.player != null) {
            int px = (int) client.player.getX();
            int py = (int) client.player.getY();
            int pz = (int) client.player.getZ();
            context.drawText(textRenderer, Text.literal("§6Current Position:"), infoX, infoY, 0xFFFFD700, true);
            context.drawText(textRenderer, Text.literal("§fX: " + px + "  Y: " + py + "  Z: " + pz), infoX, infoY + 11, 0xFFFFFFFF, false);
            context.drawText(textRenderer, Text.literal("§b" + AscendHudRenderer.getRegionName(px, pz)), infoX, infoY + 23, 0xFF4DD8FF, false);
        }

        // Mouse Hover World Coordinates
        if (mouseX >= mapBoxX && mouseX <= mapBoxX + mapBoxW && mouseY >= mapBoxY && mouseY <= mapBoxY + mapBoxH) {
            int relX = mouseX - mapOriginX;
            int relY = mouseY - mapOriginY;
            double hNormX = (double) relX / fullMapPx;
            double hNormZ = (double) relY / fullMapPx;
            int targetWorldX = (int) (hNormX * 8000.0 - 4000.0);
            int targetWorldZ = (int) (hNormZ * 8000.0 - 4000.0);
            String hoverStr = "§e" + targetWorldX + ", " + targetWorldZ;
            context.drawText(textRenderer, Text.literal(hoverStr), mapBoxX + 4, mapBoxY + mapBoxH - 12, 0xFFFFD700, true);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_M || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(null);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
