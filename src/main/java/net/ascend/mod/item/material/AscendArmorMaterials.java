package net.ascend.mod.item.material;

import net.ascend.mod.item.AscendItems;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.function.Supplier;

public enum AscendArmorMaterials implements ArmorMaterial {
    MYTHRIL("ascend:mythril", 28, new int[]{3, 6, 8, 3}, 18, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0f, 0.05f, () -> Ingredient.ofItems(AscendItems.MYTHRIL_INGOT)),
    DRAGONITE("ascend:dragonite", 38, new int[]{4, 7, 9, 4}, 20, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 3.0f, 0.1f, () -> Ingredient.ofItems(AscendItems.DRAGONITE_INGOT)),
    VOIDSTEEL("ascend:voidsteel", 45, new int[]{5, 8, 10, 5}, 24, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 4.0f, 0.2f, () -> Ingredient.ofItems(AscendItems.VOIDSTEEL_INGOT));

    private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
    private final String name;
    private final int durabilityMultiplier;
    private final int[] protectionAmounts;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;

    AscendArmorMaterials(String name, int durabilityMultiplier, int[] protectionAmounts, int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmounts = protectionAmounts;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
    }

    @Override public int getDurability(ArmorItem.Type type) { return BASE_DURABILITY[type.getEquipmentSlot().getEntitySlotId()] * this.durabilityMultiplier; }
    @Override public int getProtection(ArmorItem.Type type) { return this.protectionAmounts[type.getEquipmentSlot().getEntitySlotId()]; }
    @Override public int getEnchantability() { return this.enchantability; }
    @Override public SoundEvent getEquipSound() { return this.equipSound; }
    @Override public Ingredient getRepairIngredient() { return this.repairIngredient.get(); }
    @Override public String getName() { return this.name; }
    @Override public float getToughness() { return this.toughness; }
    @Override public float getKnockbackResistance() { return this.knockbackResistance; }
}

