package net.ascend.mod.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.ascend.mod.AscendMod;
import net.ascend.mod.client.gui.components.RpgTheme;
import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.ascend.mod.skills.SkillInfo;
import net.ascend.mod.skills.SkillRank;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

@Environment(EnvType.CLIENT)
public class AscendHudRenderer implements HudRenderCallback {

    private static final String[] HOTKEYS = {"R", "F", "C", "X", "V"};
    private static final String[] RANK_REQ = {"Rank E", "Rank D", "Rank C", "Rank A", "Awakened"};

    private static final Identifier CONTINENT_MAP = new Identifier("ascend", "textures/gui/continent_map.png");
    private static final Identifier PLAYER_ARROW = new Identifier("ascend", "textures/gui/player_arrow.png");

    public static int minimapZoomLevel = 2; // 0: 1x (All), 1: 2x (Region), 2: 4x (District), 3: 8x (Local)
    public static boolean minimapVisible = true;

    public static String getMinimapZoomLabel() {
        return switch (minimapZoomLevel) {
            case 0 -> "1x (All World)";
            case 1 -> "2x (Region)";
            case 2 -> "4x (District)";
            default -> "8x (Local)";
        };
    }

    public static void cycleMinimapZoom() {
        minimapZoomLevel = (minimapZoomLevel + 1) % 4;
    }

    public static String getRegionName(int x, int z) {
        if (Math.abs(x) < 200 && Math.abs(z) < 200) {
            return "✦ Temple of the Chosen";
        }
        if (z < -1800) {
            return "✦ Frozen Mountains (Frostspire)";
        }
        if (z >= -1400 && z <= -300 && Math.abs(x) < 700) {
            return "✦ Lake Veyra (Great Lake)";
        }
        if (x >= -1200 && x <= 1200 && z >= 0 && z <= 1800) {
            return "✦ Aurelia Province (Heartland)";
        }
        if (x < -1500 && z < 400) {
            return "✦ Elven Forest (Elarion)";
        }
        if (x < -1500 && z >= 400) {
            return "✦ Prism Pools (Slime Realm)";
        }
        if (x > 1400 && z < 400) {
            return "✦ Draconis Ravines";
        }
        if (x > 1400 && z >= 400) {
            return "✦ Inferno Keep (Caldera)";
        }
        if (z > 1800 && x < 500) {
            return "✦ Beastkin Territory";
        }
        if (z > 2800) {
            return "✦ Shattered Frontier";
        }
        return "✦ Continental Frontier";
    }

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        PlayerData data = ((IPlayerDataAccessor) client.player).ascend$getPlayerData();
        if (!data.isInitialized()) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        TextRenderer tr = client.textRenderer;

        // 1. Top-Left Ascender Banner (Minimal & Clean)
        renderPlayerBanner(context, tr, data);

        // 2. Bottom-Left RPG Resource Bars (Stamina & Mana)
        renderResourceBars(context, tr, screenHeight, data);

        // 3. Bottom-Right RPG Skill & Signature Action Slots
        renderSkillSlots(context, tr, screenWidth, screenHeight, data);

        // 4. Top-Right RPG Minimap (Live Continent Radar)
        renderMinimap(context, tr, screenWidth, client.player);
    }

    private void renderPlayerBanner(DrawContext context, TextRenderer tr, PlayerData data) {
        int x = 10;
        int y = 10;
        int bannerW = 150;
        int bannerH = 46;

        // Subtle dark backdrop
        context.fill(x, y, x + bannerW, y + bannerH, 0xAA080D1A);
        context.drawBorder(x, y, bannerW, bannerH, 0x6600D2FF);

        // Name & Title
        String nameStr = "§f§l" + data.getCharacterName();
        context.drawText(tr, Text.literal(nameStr), x + 6, y + 5, 0xFFFFFFFF, true);

        // Race & Dual-Class Discipline
        String classStr = "§b" + data.getRace() + " §7| §e" + data.getCombinedClassTitle();
        if (classStr.length() > 28) classStr = classStr.substring(0, 27) + "...";
        context.drawText(tr, Text.literal(classStr), x + 6, y + 17, 0xFFE0E0E0, false);

        // Level & Rank badge
        String lvlStr = "§7Lv.§e" + data.getLevel() + " §8| §fRank: §6§l" + data.getRank();
        context.drawText(tr, Text.literal(lvlStr), x + 6, y + 28, 0xFFFFFFFF, false);

        // XP Bar
        long curXp = data.getXp();
        long reqXp = data.getXpRequiredForNextLevel();
        double xpPct = reqXp > 0 ? (double) curXp / reqXp : 0.0;
        RpgTheme.drawProgressBar(context, x + 6, y + 38, bannerW - 12, 4, xpPct, RpgTheme.BAR_XP, 0xFF554400);

        // Available Stat Points Alert
        if (data.getStatPoints() > 0) {
            context.drawText(tr, Text.literal("§a✦ " + data.getStatPoints() + " Pts [K]"), x + bannerW + 6, y + 6, 0xFF55FF55, true);
        }
    }

    private void renderResourceBars(DrawContext context, TextRenderer tr, int screenHeight, PlayerData data) {
        int barX = 12;
        int barY = screenHeight - 52;
        int barWidth = 110;
        int barHeight = 8;

        // Stamina Bar (Emerald green)
        double staminaPercent = Math.max(0.0, Math.min(1.0, data.getCurrentStamina() / 100.0));
        int staFillWidth = (int) (staminaPercent * (barWidth - 2));

        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xDD111726);
        context.fill(barX + 1, barY + 1, barX + 1 + staFillWidth, barY + barHeight - 1, 0xFF2ECC71);
        context.drawBorder(barX, barY, barWidth, barHeight, 0xFF0D5A34);
        context.drawText(tr, Text.literal("§aSTA §f" + (int) data.getCurrentStamina() + "/100"), barX + 4, barY - 1, 0xFFFFFFFF, true);

        // Mana Bar (Vibrant anime cyan)
        int manaBarY = barY + 12;
        double manaPercent = data.getIntelligence() > 0 ? Math.max(0.0, Math.min(1.0, data.getCurrentMana() / data.getIntelligence())) : 0.0;
        int manaFillWidth = (int) (manaPercent * (barWidth - 2));

        context.fill(barX, manaBarY, barX + barWidth, manaBarY + barHeight, 0xDD111726);
        context.fill(barX + 1, manaBarY + 1, barX + 1 + manaFillWidth, manaBarY + barHeight - 1, 0xFF00D2FF);
        context.drawBorder(barX, manaBarY, barWidth, barHeight, 0xFF005E82);
        context.drawText(tr, Text.literal("§bMP  §f" + (int) data.getCurrentMana() + "/" + data.getIntelligence()), barX + 4, manaBarY - 1, 0xFFFFFFFF, true);

        // Dash & Rank Indicator
        int dashY = manaBarY + 12;
        int dashCd = data.getDashCooldownTicks();
        String dashText = dashCd > 0 ? "§c[ALT] Dash (" + (dashCd / 20 + 1) + "s)" : "§a[ALT] Dash READY";
        context.drawText(tr, Text.literal(dashText), barX, dashY, 0xFFFFFFFF, true);
    }

    private void renderSkillSlots(DrawContext context, TextRenderer tr, int screenWidth, int screenHeight, PlayerData data) {
        List<String> skills = data.getSelectedSkills();
        int maxUnlocked = data.getMaxUnlockedSkillSlots();
        int slotSize = 26;
        int slotSpacing = 6;
        int totalWidth = (slotSize * 5) + (slotSpacing * 4);
        int startX = screenWidth - totalWidth - 14;
        int slotY = screenHeight - slotSize - 12;

        // Render 4 Regular Skill Slots
        for (int i = 0; i < 4; i++) {
            int x = startX + i * (slotSize + slotSpacing);
            boolean isUnlocked = (i < maxUnlocked);
            String skillId = (isUnlocked && i < skills.size()) ? skills.get(i) : null;
            int cdTicks = data.getSkillCooldown(i);

            boolean hasMana = true;
            if (skillId != null && !skillId.isEmpty()) {
                SkillInfo sk = SkillInfo.getById(skillId);
                double cost = sk.costAmount();
                if (sk.isPercent()) cost = (data.getIntelligence() * cost) / 100.0;
                if ("MANA".equalsIgnoreCase(sk.costType()) && data.getCurrentMana() < cost) {
                    hasMana = false;
                }
            }

            drawSkillSlot(context, tr, x, slotY, slotSize, HOTKEYS[i], skillId, cdTicks, isUnlocked, RANK_REQ[i], false, hasMana);
        }

        // Slot 5: Signature Move [V]
        int sigX = startX + 4 * (slotSize + slotSpacing);
        String sigIcon = getSignatureIcon(data.getPrimaryClass());
        drawSkillSlot(context, tr, sigX, slotY, slotSize, "V", sigIcon, data.getSignatureCooldownTicks(), true, "Awakened", true, true);
    }

    private void drawSkillSlot(DrawContext context, TextRenderer tr, int x, int y, int size,
                               String hotkey, String iconId, int cdTicks, boolean unlocked, String reqRank, boolean isSignature, boolean hasResource) {
        int borderColor = isSignature ? 0xFFFFD700 : (unlocked ? 0xFF00D2FF : 0xFF444455);
        int bgColor = unlocked ? (hasResource ? 0xDD0D1322 : 0xDD2A1015) : 0xDD18181F;

        // Background box & border
        context.fill(x, y, x + size, y + size, bgColor);
        context.drawBorder(x, y, size, size, borderColor);

        if (!unlocked) {
            context.fill(x + 1, y + 1, x + size - 1, y + size - 1, 0xAA111111);
            context.drawText(tr, Text.literal("🔒"), x + (size - 8) / 2, y + 4, 0xFFAAAAAA, false);
            context.drawText(tr, Text.literal("§7" + reqRank.replace("Rank ", "")), x + (size - 8) / 2, y + 14, 0xFF888888, false);
            return;
        }

        // Render Skill Logo Icon if present (16x16)
        if (iconId != null && !iconId.isEmpty()) {
            Identifier texture = new Identifier(AscendMod.MOD_ID, "textures/skill/" + iconId + ".png");
            try {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                if (!hasResource) {
                    RenderSystem.setShaderColor(0.5f, 0.5f, 0.6f, 0.7f);
                }
                context.drawTexture(texture, x + 5, y + 5, 0f, 0f, 16, 16, 16, 16);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.disableBlend();
            } catch (Exception ignored) {
            }
        }

        // Hotkey badge (top-left)
        int keyColor = isSignature ? 0xFFFFD700 : 0xFFFFFFFF;
        context.drawText(tr, Text.literal(hotkey), x + 2, y + 1, keyColor, true);

        // Rank badge (bottom-right)
        if (iconId != null && !iconId.isEmpty() && !isSignature) {
            SkillInfo sk = SkillInfo.getById(iconId);
            if (sk != null && sk.rank() != null) {
                String rStr = sk.rank().getLabel();
                int rCol = sk.rank().getHexColor();
                int rw = tr.getWidth(rStr);
                context.drawText(tr, Text.literal(sk.rank().getColorCode() + rStr), x + size - rw - 2, y + size - 9, rCol, true);
            }
        }

        // Cooldown overlay
        if (cdTicks > 0) {
            context.fill(x + 1, y + 1, x + size - 1, y + size - 1, 0xCC000000);
            int sec = (cdTicks / 20) + 1;
            String cdStr = sec >= 60 ? (sec / 60) + "m" : sec + "s";
            int cdW = tr.getWidth(cdStr);
            context.drawText(tr, Text.literal("§c§l" + cdStr), x + (size - cdW) / 2, y + 9, 0xFFFF5555, true);
        }
    }

    private String getSignatureIcon(String pClass) {
        return switch (pClass.toLowerCase()) {
            case "swordsman" -> "sig_triple_slash";
            case "summoner" -> "sig_summon_portal";
            case "mage" -> "sig_meteor";
            case "archer" -> "sig_piercing_volley";
            case "tank" -> "sig_guardian_slam";
            default -> "sig_triple_slash";
        };
    }

    private void renderMinimap(DrawContext context, TextRenderer tr, int screenWidth, net.minecraft.entity.player.PlayerEntity player) {
        if (!minimapVisible || player == null) return;

        int mapSize = 74;
        int mapX = screenWidth - mapSize - 10;
        int mapY = 14;

        // Current Region Header
        String regName = getRegionName((int) player.getX(), (int) player.getZ());
        int regW = tr.getWidth(regName);
        context.drawText(tr, Text.literal(regName), mapX + mapSize - regW, mapY - 10, 0xFFFFD700, true);

        // Minimap background card
        context.fill(mapX, mapY, mapX + mapSize, mapY + mapSize, 0xCC090E1B);

        // Scissor viewport for inner map
        context.enableScissor(mapX + 2, mapY + 2, mapX + mapSize - 2, mapY + mapSize - 2);

        float zoom = switch (minimapZoomLevel) {
            case 0 -> 1.0f;
            case 1 -> 2.0f;
            case 2 -> 4.0f;
            default -> 8.0f;
        };

        int innerSize = mapSize - 4;
        int fullMapPx = (int) (innerSize * zoom);

        double normX = (player.getX() + 4000.0) / 8000.0;
        double normZ = (player.getZ() + 4000.0) / 8000.0;

        int centerScreenX = mapX + mapSize / 2;
        int centerScreenY = mapY + mapSize / 2;

        int mapDrawX = centerScreenX - (int) (normX * fullMapPx);
        int mapDrawY = centerScreenY - (int) (normZ * fullMapPx);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        context.drawTexture(CONTINENT_MAP, mapDrawX, mapDrawY, 0f, 0f, fullMapPx, fullMapPx, 512, 512);

        // Draw Player Direction Arrow in center
        context.getMatrices().push();
        context.getMatrices().translate(centerScreenX, centerScreenY, 0);
        context.getMatrices().multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Z.rotationDegrees(player.getYaw() + 180f));
        context.drawTexture(PLAYER_ARROW, -4, -4, 0f, 0f, 8, 8, 16, 16);
        context.getMatrices().pop();

        context.disableScissor();

        // Cyan border
        context.drawBorder(mapX, mapY, mapSize, mapSize, 0xFF00D2FF);

        // Compass Indicators (N, S, E, W)
        context.drawText(tr, Text.literal("§c§lN"), mapX + (mapSize / 2) - 3, mapY + 3, 0xFFFF5555, true);
        context.drawText(tr, Text.literal("§7S"), mapX + (mapSize / 2) - 3, mapY + mapSize - 9, 0xFFAAAAAA, true);
        context.drawText(tr, Text.literal("§7W"), mapX + 3, mapY + (mapSize / 2) - 4, 0xFFAAAAAA, true);
        context.drawText(tr, Text.literal("§7E"), mapX + mapSize - 8, mapY + (mapSize / 2) - 4, 0xFFAAAAAA, true);

        // Coordinates & Zoom Badge Below
        int px = (int) player.getX();
        int pz = (int) player.getZ();
        String coordStr = "§7" + px + ", " + pz + " §e[" + (minimapZoomLevel == 0 ? "All" : (int)zoom + "x") + "]";
        int coordW = tr.getWidth(coordStr);
        context.drawText(tr, Text.literal(coordStr), mapX + (mapSize - coordW) / 2, mapY + mapSize + 3, 0xFFFFFFFF, true);
    }
}
