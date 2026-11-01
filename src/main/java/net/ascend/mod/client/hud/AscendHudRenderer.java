package net.ascend.mod.client.hud;

import net.ascend.mod.AscendMod;
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

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        PlayerData data = ((IPlayerDataAccessor) client.player).ascend$getPlayerData();
        if (!data.isInitialized()) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        TextRenderer tr = client.textRenderer;

        // 1. Draw RPG Resource Bars (Bottom Left)
        int barX = 12;
        int barY = screenHeight - 52;
        int barWidth = 110;
        int barHeight = 8;

        // Stamina Bar (Vibrant emerald green with dark cyber border)
        double staminaPercent = Math.max(0.0, Math.min(1.0, data.getCurrentStamina() / 100.0));
        int staFillWidth = (int) (staminaPercent * (barWidth - 2));

        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xDD111726);
        context.fill(barX + 1, barY + 1, barX + 1 + staFillWidth, barY + barHeight - 1, 0xFF2ECC71);
        context.drawBorder(barX, barY, barWidth, barHeight, 0xFF0D5A34);
        context.drawText(tr, Text.literal("§aSTA §f" + (int) data.getCurrentStamina() + "/100"), barX + 4, barY - 1, 0xFFFFFFFF, true);

        // Mana Bar (Vibrant anime cyan)
        int manaBarY = barY + 12;
        double manaPercent = data.getMana() > 0 ? Math.max(0.0, Math.min(1.0, data.getCurrentMana() / data.getMana())) : 0.0;
        int manaFillWidth = (int) (manaPercent * (barWidth - 2));

        context.fill(barX, manaBarY, barX + barWidth, manaBarY + barHeight, 0xDD111726);
        context.fill(barX + 1, manaBarY + 1, barX + 1 + manaFillWidth, manaBarY + barHeight - 1, 0xFF00D2FF);
        context.drawBorder(barX, manaBarY, barWidth, barHeight, 0xFF005E82);
        context.drawText(tr, Text.literal("§bMP  §f" + (int) data.getCurrentMana() + "/" + data.getMana()), barX + 4, manaBarY - 1, 0xFFFFFFFF, true);

        // Dash & Rank Indicator
        int dashY = manaBarY + 12;
        int dashCd = data.getDashCooldownTicks();
        String dashText = dashCd > 0 ? "§c[ALT] Dash (" + (dashCd / 20 + 1) + "s)" : "§a[ALT] Dash READY";
        context.drawText(tr, Text.literal(dashText + "  §8|  §eRank §l" + data.getRank()), barX, dashY, 0xFFFFFFFF, true);

        // 2. Draw RPG Skill & Signature Action Slots (Bottom Right)
        List<String> skills = data.getSelectedSkills();
        int maxUnlocked = data.getMaxUnlockedSkillSlots();
        int slotSize = 26;
        int slotSpacing = 6;
        int totalWidth = (slotSize * 5) + (slotSpacing * 4);
        int startX = screenWidth - totalWidth - 14;
        int slotY = screenHeight - slotSize - 12;

        // Render 4 Regular Skill Slots + 1 Signature Move Slot
        for (int i = 0; i < 4; i++) {
            int x = startX + i * (slotSize + slotSpacing);
            boolean isUnlocked = (i < maxUnlocked);
            String skillId = (isUnlocked && i < skills.size()) ? skills.get(i) : null;
            int cdTicks = data.getSkillCooldown(i);

            drawSkillSlot(context, tr, x, slotY, slotSize, HOTKEYS[i], skillId, cdTicks, isUnlocked, RANK_REQ[i], false);
        }

        // Slot 5: Signature Move [V]
        int sigX = startX + 4 * (slotSize + slotSpacing);
        String sigIcon = getSignatureIcon(data.getPlayerClass());
        drawSkillSlot(context, tr, sigX, slotY, slotSize, "V", sigIcon, data.getSignatureCooldownTicks(), true, "Awakened", true);
    }

    private void drawSkillSlot(DrawContext context, TextRenderer tr, int x, int y, int size,
                               String hotkey, String iconId, int cdTicks, boolean unlocked, String reqRank, boolean isSignature) {
        int borderColor = isSignature ? 0xFFFFD700 : (unlocked ? 0xFF00D2FF : 0xFF444455);
        int bgColor = unlocked ? 0xDD0D1322 : 0xDD18181F;

        // Background box & border
        context.fill(x, y, x + size, y + size, bgColor);
        context.drawBorder(x, y, size, size, borderColor);

        if (!unlocked) {
            // Locked Slot Overlay with Required Rank indicator
            context.fill(x + 1, y + 1, x + size - 1, y + size - 1, 0xAA111111);
            int rw = tr.getWidth(reqRank);
            context.drawText(tr, Text.literal("🔒"), x + (size - 8) / 2, y + 4, 0xFFAAAAAA, false);
            context.drawText(tr, Text.literal("§7" + reqRank.replace("Rank ", "")), x + (size - 8) / 2, y + 14, 0xFF888888, false);
            return;
        }

        // Render Skill Logo Icon if present (16x16)
        if (iconId != null && !iconId.isEmpty()) {
            Identifier texture = new Identifier(AscendMod.MOD_ID, "textures/skill/" + iconId + ".png");
            try {
                context.drawTexture(texture, x + 5, y + 5, 0f, 0f, 16, 16, 16, 16);
            } catch (Exception ignored) {
            }
        }

        // Hotkey badge (top-left)
        int keyColor = isSignature ? 0xFFFFD700 : 0xFFFFFFFF;
        context.drawText(tr, Text.literal(hotkey), x + 3, y + 2, keyColor, true);

        // Rank badge (top-right)
        if (iconId != null && !iconId.isEmpty() && !isSignature) {
            SkillInfo sk = SkillInfo.getById(iconId);
            if (sk != null && sk.rank() != null) {
                String rStr = sk.rank().getLabel();
                int rCol = sk.rank().getHexColor();
                int rw = tr.getWidth(rStr);
                context.drawText(tr, Text.literal(sk.rank().getColorCode() + rStr), x + size - rw - 3, y + 2, rCol, true);
            }
        }

        // Cooldown overlay
        if (cdTicks > 0) {
            context.fill(x + 1, y + 1, x + size - 1, y + size - 1, 0xCC000000);
            int sec = (cdTicks / 20) + 1;
            String cdStr = sec + "s";
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
}
