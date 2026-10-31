package net.ascend.mod.item;

import net.ascend.mod.AscendMod;
import net.ascend.mod.block.AscendBlocks;
import net.ascend.mod.item.material.AscendArmorMaterials;
import net.ascend.mod.item.material.AscendToolMaterials;
import net.ascend.mod.item.staff.MagicStaffItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AscendItems {
    // 1. Materials
    public static final Item MYTHRIL_INGOT = register("mythril_ingot", new Item(new FabricItemSettings()));
    public static final Item MANA_CRYSTAL = register("mana_crystal", new ManaCrystalItem(new FabricItemSettings()));
    public static final Item DRAGONITE_INGOT = register("dragonite_ingot", new Item(new FabricItemSettings()));
    public static final Item VOIDSTEEL_INGOT = register("voidsteel_ingot", new Item(new FabricItemSettings()));
    public static final Item SHADOW_ESSENCE = register("shadow_essence", new Item(new FabricItemSettings()));
    public static final Item DRAGON_SCALE = register("dragon_scale", new Item(new FabricItemSettings()));
    public static final Item DUNGEON_MAP = register("dungeon_map", new DungeonMapItem(new FabricItemSettings()));
    public static final Item RESET_STONE = register("reset_stone", new Item(new FabricItemSettings()));

    // Accessories
    public static final Item MANA_BRACELET = register("mana_bracelet", new AccessoryItem(new FabricItemSettings().maxCount(1), "§b✦ +50 Max Mana", "§b✦ +3 MP/s Passive Regen"));
    public static final Item STAMINA_RING = register("stamina_ring", new AccessoryItem(new FabricItemSettings().maxCount(1), "§a✦ +50 Max Stamina", "§a✦ +6 STA/s Passive Regen"));
    public static final Item SHADOW_TALISMAN = register("shadow_talisman", new AccessoryItem(new FabricItemSettings().maxCount(1), "§d✦ +20% Movement Speed", "§d✦ 2x Dash Cooldown Recovery"));

    // 2. Mythril Gear
    public static final Item MYTHRIL_SWORD = register("mythril_sword", new SwordItem(AscendToolMaterials.MYTHRIL, 3, -2.4f, new FabricItemSettings()));
    public static final Item MYTHRIL_PICKAXE = register("mythril_pickaxe", new PickaxeItem(AscendToolMaterials.MYTHRIL, 1, -2.8f, new FabricItemSettings()));
    public static final Item MYTHRIL_AXE = register("mythril_axe", new AxeItem(AscendToolMaterials.MYTHRIL, 5.0f, -3.0f, new FabricItemSettings()));
    public static final Item MYTHRIL_SHOVEL = register("mythril_shovel", new ShovelItem(AscendToolMaterials.MYTHRIL, 1.5f, -3.0f, new FabricItemSettings()));
    public static final Item MYTHRIL_HOE = register("mythril_hoe", new HoeItem(AscendToolMaterials.MYTHRIL, -3, 0.0f, new FabricItemSettings()));

    public static final Item MYTHRIL_HELMET = register("mythril_helmet", new ArmorItem(AscendArmorMaterials.MYTHRIL, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item MYTHRIL_CHESTPLATE = register("mythril_chestplate", new ArmorItem(AscendArmorMaterials.MYTHRIL, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item MYTHRIL_LEGGINGS = register("mythril_leggings", new ArmorItem(AscendArmorMaterials.MYTHRIL, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item MYTHRIL_BOOTS = register("mythril_boots", new ArmorItem(AscendArmorMaterials.MYTHRIL, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    // 3. Dragonite Gear
    public static final Item DRAGONITE_SWORD = register("dragonite_sword", new SwordItem(AscendToolMaterials.DRAGONITE, 4, -2.4f, new FabricItemSettings()));
    public static final Item DRAGONITE_PICKAXE = register("dragonite_pickaxe", new PickaxeItem(AscendToolMaterials.DRAGONITE, 1, -2.8f, new FabricItemSettings()));
    public static final Item DRAGONITE_AXE = register("dragonite_axe", new AxeItem(AscendToolMaterials.DRAGONITE, 6.0f, -3.0f, new FabricItemSettings()));
    public static final Item DRAGONITE_SHOVEL = register("dragonite_shovel", new ShovelItem(AscendToolMaterials.DRAGONITE, 2.0f, -3.0f, new FabricItemSettings()));
    public static final Item DRAGONITE_HOE = register("dragonite_hoe", new HoeItem(AscendToolMaterials.DRAGONITE, -2, 0.0f, new FabricItemSettings()));

    public static final Item DRAGONITE_HELMET = register("dragonite_helmet", new ArmorItem(AscendArmorMaterials.DRAGONITE, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item DRAGONITE_CHESTPLATE = register("dragonite_chestplate", new ArmorItem(AscendArmorMaterials.DRAGONITE, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item DRAGONITE_LEGGINGS = register("dragonite_leggings", new ArmorItem(AscendArmorMaterials.DRAGONITE, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item DRAGONITE_BOOTS = register("dragonite_boots", new ArmorItem(AscendArmorMaterials.DRAGONITE, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    // 4. Voidsteel Gear
    public static final Item VOIDSTEEL_SWORD = register("voidsteel_sword", new SwordItem(AscendToolMaterials.VOIDSTEEL, 5, -2.2f, new FabricItemSettings()));
    public static final Item VOIDSTEEL_PICKAXE = register("voidsteel_pickaxe", new PickaxeItem(AscendToolMaterials.VOIDSTEEL, 2, -2.7f, new FabricItemSettings()));
    public static final Item VOIDSTEEL_AXE = register("voidsteel_axe", new AxeItem(AscendToolMaterials.VOIDSTEEL, 7.0f, -2.9f, new FabricItemSettings()));
    public static final Item VOIDSTEEL_SHOVEL = register("voidsteel_shovel", new ShovelItem(AscendToolMaterials.VOIDSTEEL, 2.5f, -2.9f, new FabricItemSettings()));
    public static final Item VOIDSTEEL_HOE = register("voidsteel_hoe", new HoeItem(AscendToolMaterials.VOIDSTEEL, -1, 0.5f, new FabricItemSettings()));

    public static final Item VOIDSTEEL_HELMET = register("voidsteel_helmet", new ArmorItem(AscendArmorMaterials.VOIDSTEEL, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item VOIDSTEEL_CHESTPLATE = register("voidsteel_chestplate", new ArmorItem(AscendArmorMaterials.VOIDSTEEL, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item VOIDSTEEL_LEGGINGS = register("voidsteel_leggings", new ArmorItem(AscendArmorMaterials.VOIDSTEEL, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item VOIDSTEEL_BOOTS = register("voidsteel_boots", new ArmorItem(AscendArmorMaterials.VOIDSTEEL, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    // 5. Special Weapons
    public static final Item MYTHRIL_KATANA = register("mythril_katana", new SwordItem(AscendToolMaterials.MYTHRIL, 2, -1.8f, new FabricItemSettings()));
    public static final Item DRAGONITE_GREATSWORD = register("dragonite_greatsword", new SwordItem(AscendToolMaterials.DRAGONITE, 7, -3.2f, new FabricItemSettings()));
    public static final Item VOIDSTEEL_DAGGER = register("voidsteel_dagger", new SwordItem(AscendToolMaterials.VOIDSTEEL, 1, -1.2f, new FabricItemSettings()));
    public static final Item LONGBOW = register("longbow", new BowItem(new FabricItemSettings().maxDamage(800)));

    // 6. Magic Staffs
    public static final Item APPRENTICE_STAFF = register("apprentice_staff", new MagicStaffItem(MagicStaffItem.StaffType.APPRENTICE, new FabricItemSettings()));
    public static final Item FLAME_STAFF = register("flame_staff", new MagicStaffItem(MagicStaffItem.StaffType.FLAME, new FabricItemSettings()));
    public static final Item STORM_STAFF = register("storm_staff", new MagicStaffItem(MagicStaffItem.StaffType.STORM, new FabricItemSettings()));
    public static final Item SUMMONER_STAFF = register("summoner_staff", new MagicStaffItem(MagicStaffItem.StaffType.SUMMONER, new FabricItemSettings()));
    public static final Item VOID_STAFF = register("void_staff", new MagicStaffItem(MagicStaffItem.StaffType.VOID, new FabricItemSettings()));

    // Creative Tab
    public static final RegistryKey<ItemGroup> ASCEND_GROUP_KEY = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(AscendMod.MOD_ID, "ascend_tab"));
    public static final ItemGroup ASCEND_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(VOIDSTEEL_SWORD))
            .displayName(Text.literal("Ascend RPG"))
            .entries((displayContext, entries) -> {
                entries.add(MYTHRIL_INGOT);
                entries.add(MANA_CRYSTAL);
                entries.add(DRAGONITE_INGOT);
                entries.add(VOIDSTEEL_INGOT);
                entries.add(SHADOW_ESSENCE);
                entries.add(DRAGON_SCALE);
                entries.add(DUNGEON_MAP);
                entries.add(RESET_STONE);
                entries.add(MANA_BRACELET);
                entries.add(STAMINA_RING);
                entries.add(SHADOW_TALISMAN);

                entries.add(MYTHRIL_SWORD);
                entries.add(MYTHRIL_PICKAXE);
                entries.add(MYTHRIL_AXE);
                entries.add(MYTHRIL_SHOVEL);
                entries.add(MYTHRIL_HOE);
                entries.add(MYTHRIL_HELMET);
                entries.add(MYTHRIL_CHESTPLATE);
                entries.add(MYTHRIL_LEGGINGS);
                entries.add(MYTHRIL_BOOTS);

                entries.add(DRAGONITE_SWORD);
                entries.add(DRAGONITE_PICKAXE);
                entries.add(DRAGONITE_AXE);
                entries.add(DRAGONITE_SHOVEL);
                entries.add(DRAGONITE_HOE);
                entries.add(DRAGONITE_HELMET);
                entries.add(DRAGONITE_CHESTPLATE);
                entries.add(DRAGONITE_LEGGINGS);
                entries.add(DRAGONITE_BOOTS);

                entries.add(VOIDSTEEL_SWORD);
                entries.add(VOIDSTEEL_PICKAXE);
                entries.add(VOIDSTEEL_AXE);
                entries.add(VOIDSTEEL_SHOVEL);
                entries.add(VOIDSTEEL_HOE);
                entries.add(VOIDSTEEL_HELMET);
                entries.add(VOIDSTEEL_CHESTPLATE);
                entries.add(VOIDSTEEL_LEGGINGS);
                entries.add(VOIDSTEEL_BOOTS);

                entries.add(MYTHRIL_KATANA);
                entries.add(DRAGONITE_GREATSWORD);
                entries.add(VOIDSTEEL_DAGGER);
                entries.add(LONGBOW);

                entries.add(APPRENTICE_STAFF);
                entries.add(FLAME_STAFF);
                entries.add(STORM_STAFF);
                entries.add(SUMMONER_STAFF);
                entries.add(VOID_STAFF);

                entries.add(AscendBlocks.MYTHRIL_ORE);
                entries.add(AscendBlocks.DEEPSLATE_MYTHRIL_ORE);
                entries.add(AscendBlocks.MANA_CRYSTAL_ORE);
                entries.add(AscendBlocks.DEEPSLATE_MANA_CRYSTAL_ORE);
                entries.add(AscendBlocks.DRAGONITE_ORE);
                entries.add(AscendBlocks.MYSTIC_LOG);
                entries.add(AscendBlocks.STRIPPED_MYSTIC_LOG);
                entries.add(AscendBlocks.MYSTIC_PLANKS);
                entries.add(AscendBlocks.MYSTIC_LEAVES);
                entries.add(AscendBlocks.EMBERWOOD_LOG);
                entries.add(AscendBlocks.STRIPPED_EMBERWOOD_LOG);
                entries.add(AscendBlocks.EMBERWOOD_PLANKS);
                entries.add(AscendBlocks.EMBERWOOD_LEAVES);
                entries.add(AscendBlocks.DUNGEON_GATE_BLOCK);
                entries.add(AscendBlocks.BOSS_ALTAR);
            })
            .build();

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(AscendMod.MOD_ID, name), item);
    }

    public static void register() {
        Registry.register(Registries.ITEM_GROUP, ASCEND_GROUP_KEY, ASCEND_GROUP);
        AscendMod.LOGGER.info("[Ascend] Registering items & creative tabs...");
    }
}
