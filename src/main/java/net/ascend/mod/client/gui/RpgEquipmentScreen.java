package net.ascend.mod.client.gui;

import net.ascend.mod.client.gui.components.RpgTheme;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class RpgEquipmentScreen extends Screen {
    private static final int CARD_WIDTH = 370;
    private static final int CARD_HEIGHT = 240;

    public RpgEquipmentScreen() {
        super(Text.literal("RPG Equipment & Loadout"));
    }

    @Override
    protected void init() {
        super.init();
        clearChildren();
        int left = (width - CARD_WIDTH) / 2;
        int top = (height - CARD_HEIGHT) / 2;

        // View Profile button
        ButtonWidget profileBtn = ButtonWidget.builder(Text.literal("Status (K)"), b -> {
            if (client != null) client.setScreen(new CharacterProfileScreen());
        }).dimensions(left + 16, top + CARD_HEIGHT - 26, 80, 18).build();
        addDrawableChild(profileBtn);

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
        RpgTheme.drawHeader(context, textRenderer, "✦ COMBAT LOADOUT & ACCESSORIES ✦", left, top + 8, CARD_WIDTH, true);

        if (client != null && client.player != null) {
            // 3D Avatar (Left)
            InventoryScreen.drawEntity(context, left + 45, top + 130, 36, (float)(left + 45) - mouseX, (float)(top + 130 - 50) - mouseY, client.player);

            int infoX = left + 95;
            int startY = top + 34;

            // 1. Equipped Weapons
            context.drawText(textRenderer, Text.literal("§6Active Weapons:"), infoX, startY, 0xFFFFD700, true);
            ItemStack mainHand = client.player.getMainHandStack();
            ItemStack offHand = client.player.getOffHandStack();

            String mainStr = !mainHand.isEmpty() ? "§fMain: §e" + mainHand.getName().getString() : "§7Main: Bare Hands";
            String offStr = !offHand.isEmpty() ? "§fOffhand: §b" + offHand.getName().getString() : "§7Offhand: Empty";
            context.drawText(textRenderer, Text.literal(mainStr), infoX + 6, startY + 12, 0xFFFFFFFF, false);
            context.drawText(textRenderer, Text.literal(offStr), infoX + 6, startY + 23, 0xFFFFFFFF, false);

            // 2. Equipped Armor
            int armorY = startY + 40;
            context.drawText(textRenderer, Text.literal("§6Equipped Armor:"), infoX, armorY, 0xFFFFD700, true);
            renderSlotLine(context, "Head", client.player.getEquippedStack(EquipmentSlot.HEAD), infoX + 6, armorY + 12);
            renderSlotLine(context, "Chest", client.player.getEquippedStack(EquipmentSlot.CHEST), infoX + 6, armorY + 23);
            renderSlotLine(context, "Legs", client.player.getEquippedStack(EquipmentSlot.LEGS), infoX + 6, armorY + 34);
            renderSlotLine(context, "Feet", client.player.getEquippedStack(EquipmentSlot.FEET), infoX + 6, armorY + 45);

            // 3. Detected RPG Accessories in Loadout
            int accY = armorY + 62;
            context.drawText(textRenderer, Text.literal("§6Bound Accessories:"), infoX, accY, 0xFFFFD700, true);
            List<String> accessories = findActiveAccessories();
            if (accessories.isEmpty()) {
                context.drawText(textRenderer, Text.literal("§7No RPG accessories detected in inventory."), infoX + 6, accY + 12, 0xFFAAAAAA, false);
            } else {
                for (int i = 0; i < Math.min(accessories.size(), 3); i++) {
                    context.drawText(textRenderer, Text.literal("§a✦ §f" + accessories.get(i)), infoX + 6, accY + 12 + (i * 11), 0xFFFFFFFF, false);
                }
            }

            // 4. Set Bonus Status (Bottom Box)
            int setY = top + CARD_HEIGHT - 54;
            context.fill(left + 16, setY, left + CARD_WIDTH - 16, setY + 24, 0x880B1324);
            context.drawBorder(left + 16, setY, CARD_WIDTH - 32, 24, 0x5500D2FF);

            String setBonus = detectArmorSetBonus();
            context.drawText(textRenderer, Text.literal("§6Active Set Resonance: §f" + setBonus), left + 22, setY + 7, 0xFFFFFFFF, false);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderSlotLine(DrawContext context, String slot, ItemStack stack, int x, int y) {
        String name = !stack.isEmpty() ? "§e" + stack.getName().getString() : "§8None";
        context.drawText(textRenderer, Text.literal("§7" + slot + ": " + name), x, y, 0xFFCCCCCC, false);
    }

    private List<String> findActiveAccessories() {
        List<String> list = new ArrayList<>();
        if (client == null || client.player == null) return list;

        String[] knownAccessories = {"mana_bracelet", "mana_necklace", "stamina_ring", "shadow_talisman", "void_amulet", "dragon_pendant"};
        String[] titles = {"Mana Bracelet (+3 MP/s)", "Mana Necklace (+5 MP/s)", "Stamina Ring (+6 STA/s)", "Shadow Talisman (2x Dash Recovery)", "Void Amulet (3x Dash Recovery)", "Dragon Pendant (Fire Immunity)"};

        for (int i = 0; i < knownAccessories.length; i++) {
            if (hasItemInInventory(knownAccessories[i])) {
                list.add(titles[i]);
            }
        }
        return list;
    }

    private boolean hasItemInInventory(String path) {
        if (client == null || client.player == null) return false;
        ItemStack off = client.player.getOffHandStack();
        if (!off.isEmpty()) {
            Identifier id = Registries.ITEM.getId(off.getItem());
            if ("ascend".equals(id.getNamespace()) && id.getPath().equals(path)) return true;
        }
        for (ItemStack stack : client.player.getInventory().main) {
            if (!stack.isEmpty()) {
                Identifier id = Registries.ITEM.getId(stack.getItem());
                if ("ascend".equals(id.getNamespace()) && id.getPath().equals(path)) return true;
            }
        }
        return false;
    }

    private String detectArmorSetBonus() {
        if (client == null || client.player == null) return "None";
        if (isFullSet("dragonite_")) return "§cDragonite Immolation (100% Fire Immunity)";
        if (isFullSet("mythril_")) return "§bMythril Attunement (+Bonus Mana Regeneration)";
        return "§7No set bonus active (Requires 4 matching pieces)";
    }

    private boolean isFullSet(String prefix) {
        return isPiece(client.player.getEquippedStack(EquipmentSlot.HEAD), prefix) &&
                isPiece(client.player.getEquippedStack(EquipmentSlot.CHEST), prefix) &&
                isPiece(client.player.getEquippedStack(EquipmentSlot.LEGS), prefix) &&
                isPiece(client.player.getEquippedStack(EquipmentSlot.FEET), prefix);
    }

    private boolean isPiece(ItemStack stack, String prefix) {
        if (stack == null || stack.isEmpty()) return false;
        Identifier id = Registries.ITEM.getId(stack.getItem());
        return id.getNamespace().equals("ascend") && id.getPath().startsWith(prefix);
    }
}

