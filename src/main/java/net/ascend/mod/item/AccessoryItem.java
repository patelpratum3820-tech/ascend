package net.ascend.mod.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AccessoryItem extends Item {
    private final String statBonus1;
    private final String statBonus2;

    public AccessoryItem(Settings settings, String statBonus1, String statBonus2) {
        super(settings);
        this.statBonus1 = statBonus1;
        this.statBonus2 = statBonus2;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("§7RPG Accessory"));
        if (statBonus1 != null) {
            tooltip.add(Text.literal(statBonus1));
        }
        if (statBonus2 != null) {
            tooltip.add(Text.literal(statBonus2));
        }
        tooltip.add(Text.literal("§8Active while carried in inventory or offhand"));
    }
}
