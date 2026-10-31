package net.ascend.mod.client.hud;

import net.ascend.mod.data.IPlayerDataAccessor;
import net.ascend.mod.data.PlayerData;
import net.ascend.mod.skills.SkillInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public class AscendHudRenderer implements HudRenderCallback {
    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        PlayerData data = ((IPlayerDataAccessor) client.player).ascend$getPlayerData();
        if (!data.isInitialized()) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        TextRenderer tr = client.textRenderer;

        // 1. Draw Resource Bars on Bottom Left
        int barX = 10;
        int barY = screenHeight - 48;
        int barWidth = 100;
        int barHeight = 8;

        // Stamina Bar
        double staminaPercent = Math.max(0.0, Math.min(1.0, data.getCurrentStamina() / 100.0));
        int staFillWidth = (int) (staminaPercent * (barWidth - 2));

        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xAA111111);
        context.fill(barX + 1, barY + 1, barX + 1 + staFillWidth, barY + barHeight - 1, 0xFF2ECC71); // vibrant green
        context.drawBorder(barX, barY, barWidth, barHeight, 0xFF145A32);
        context.drawText(tr, Text.literal("STA " + (int) data.getCurrentStamina()), barX + 4, barY - 1, 0xFFFFFFFF, true);

        // Mana Bar (shown below stamina)
        int manaBarY = barY + 12;
        double manaPercent = data.getMana() > 0 ? Math.max(0.0, Math.min(1.0, data.getCurrentMana() / data.getMana())) : 0.0;
        int manaFillWidth = (int) (manaPercent * (barWidth - 2));

        context.fill(barX, manaBarY, barX + barWidth, manaBarY + barHeight, 0xAA111111);
        context.fill(barX + 1, manaBarY + 1, barX + 1 + manaFillWidth, manaBarY + barHeight - 1, 0xFF00D2FF); // cyber cyan
        context.drawBorder(barX, manaBarY, barWidth, barHeight, 0xFF0B5394);
        context.drawText(tr, Text.literal("MP " + (int) data.getCurrentMana() + "/" + data.getMana()), barX + 4, manaBarY - 1, 0xFFFFFFFF, true);

        // Dash Indicator
        int dashY = manaBarY + 12;
        int dashCd = data.getDashCooldownTicks();
        String dashText = dashCd > 0 ? "§c[ALT] Dash (" + (dashCd / 20 + 1) + "s)" : "§a[ALT] Dash READY";
        context.drawText(tr, Text.literal(dashText), barX, dashY, 0xFFFFFFFF, true);

        // 2. Draw Skill Slots on Bottom Right
        List<String> skills = data.getSelectedSkills();
        int slotSize = 26;
        int slotSpacing = 6;
        int totalWidth = (slotSize * 3) + (slotSpacing * 2);
        int startX = screenWidth - totalWidth - 12;
        int slotY = screenHeight - slotSize - 12;

        // Slot 0: Skill 1 [R]
        String skill1Id = skills.size() > 0 ? skills.get(0) : "shadow_step";
        drawSkillSlot(context, tr, startX, slotY, slotSize, "R", SkillInfo.getById(skill1Id).displayName(), data.getSkill1CooldownTicks(), 0xFF00D2FF);

        // Slot 1: Skill 2 [F]
        String skill2Id = skills.size() > 1 ? skills.get(1) : "flame_slash";
        drawSkillSlot(context, tr, startX + slotSize + slotSpacing, slotY, slotSize, "F", SkillInfo.getById(skill2Id).displayName(), data.getSkill2CooldownTicks(), 0xFF00D2FF);

        // Slot 2: Signature Move [V]
        String sigName = getSignatureName(data.getPlayerClass());
        drawSkillSlot(context, tr, startX + (slotSize + slotSpacing) * 2, slotY, slotSize, "V", sigName, data.getSignatureCooldownTicks(), 0xFFFFD700);
    }

    private void drawSkillSlot(DrawContext context, TextRenderer tr, int x, int y, int size, String hotkey, String name, int cdTicks, int borderColor) {
        // Dark box
        context.fill(x, y, x + size, y + size, 0xDD090D1A);
        context.drawBorder(x, y, size, size, borderColor);

        // Cooldown overlay
        if (cdTicks > 0) {
            context.fill(x + 1, y + 1, x + size - 1, y + size - 1, 0xBB000000);
            int sec = (cdTicks / 20) + 1;
            String cdStr = sec + "s";
            int cdW = tr.getWidth(cdStr);
            context.drawText(tr, Text.literal("§c§l" + cdStr), x + (size - cdW) / 2, y + (size / 2) - 4, 0xFFFF5555, true);
        } else {
            // Hotkey in corner
            context.drawText(tr, Text.literal("§e" + hotkey), x + 3, y + 3, 0xFFFFFF00, true);
        }

        // Skill Name label above slot
        String shortName = name.length() > 8 ? name.substring(0, 8) + "." : name;
        int nameW = tr.getWidth(shortName);
        context.drawText(tr, Text.literal("§7" + shortName), x + (size - nameW) / 2, y - 10, 0xFFDDDDDD, false);
    }

    private String getSignatureName(String pClass) {
        return switch (pClass.toLowerCase()) {
            case "swordsman" -> "Triple Slash";
            case "summoner" -> "Summon Portal";
            case "mage" -> "Meteor Call";
            case "archer" -> "Piercing Volley";
            case "tank" -> "Guardian Slam";
            default -> "Ultimate";
        };
    }
}

