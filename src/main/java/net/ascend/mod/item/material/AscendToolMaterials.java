package net.ascend.mod.item.material;

import net.ascend.mod.item.AscendItems;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public enum AscendToolMaterials implements ToolMaterial {
    MYTHRIL(3, 950, 8.0f, 3.0f, 16, () -> Ingredient.ofItems(AscendItems.MYTHRIL_INGOT)),
    DRAGONITE(4, 1650, 9.5f, 4.5f, 18, () -> Ingredient.ofItems(AscendItems.DRAGONITE_INGOT)),
    VOIDSTEEL(5, 2400, 11.0f, 6.0f, 22, () -> Ingredient.ofItems(AscendItems.VOIDSTEEL_INGOT));

    private final int miningLevel;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final java.util.function.Supplier<Ingredient> repairIngredient;

    AscendToolMaterials(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, java.util.function.Supplier<Ingredient> repairIngredient) {
        this.miningLevel = miningLevel;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
    }

    @Override public int getDurability() { return this.itemDurability; }
    @Override public float getMiningSpeedMultiplier() { return this.miningSpeed; }
    @Override public float getAttackDamage() { return this.attackDamage; }
    @Override public int getMiningLevel() { return this.miningLevel; }
    @Override public int getEnchantability() { return this.enchantability; }
    @Override public Ingredient getRepairIngredient() { return this.repairIngredient.get(); }
}
