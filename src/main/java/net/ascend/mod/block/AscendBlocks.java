package net.ascend.mod.block;

import net.ascend.mod.AscendMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class AscendBlocks {
    // Ores
    public static final Block MYTHRIL_ORE = register("mythril_ore", new ExperienceDroppingBlock(FabricBlockSettings.create().strength(3.5f, 3.5f).sounds(BlockSoundGroup.STONE).requiresTool()));
    public static final Block DEEPSLATE_MYTHRIL_ORE = register("deepslate_mythril_ore", new ExperienceDroppingBlock(FabricBlockSettings.create().strength(5.0f, 4.0f).sounds(BlockSoundGroup.DEEPSLATE).requiresTool()));
    public static final Block MANA_CRYSTAL_ORE = register("mana_crystal_ore", new ExperienceDroppingBlock(FabricBlockSettings.create().strength(3.0f, 3.0f).sounds(BlockSoundGroup.AMETHYST_BLOCK).luminance(8).requiresTool()));
    public static final Block DEEPSLATE_MANA_CRYSTAL_ORE = register("deepslate_mana_crystal_ore", new ExperienceDroppingBlock(FabricBlockSettings.create().strength(4.5f, 3.5f).sounds(BlockSoundGroup.DEEPSLATE).luminance(8).requiresTool()));
    public static final Block DRAGONITE_ORE = register("dragonite_ore", new ExperienceDroppingBlock(FabricBlockSettings.create().strength(4.0f, 4.0f).sounds(BlockSoundGroup.NETHER_ORE).luminance(4).requiresTool()));

    // Mystic Wood Set
    public static final Block MYSTIC_LOG = register("mystic_log", new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG).sounds(BlockSoundGroup.WOOD)));
    public static final Block STRIPPED_MYSTIC_LOG = register("stripped_mystic_log", new PillarBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_OAK_LOG).sounds(BlockSoundGroup.WOOD)));
    public static final Block MYSTIC_PLANKS = register("mystic_planks", new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).sounds(BlockSoundGroup.WOOD)));
    public static final Block MYSTIC_LEAVES = register("mystic_leaves", new LeavesBlock(FabricBlockSettings.copyOf(Blocks.OAK_LEAVES).luminance(6).nonOpaque().sounds(BlockSoundGroup.GRASS)));

    // Emberwood Set
    public static final Block EMBERWOOD_LOG = register("emberwood_log", new PillarBlock(FabricBlockSettings.copyOf(Blocks.CRIMSON_STEM).sounds(BlockSoundGroup.NETHER_WOOD)));
    public static final Block STRIPPED_EMBERWOOD_LOG = register("stripped_emberwood_log", new PillarBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_CRIMSON_STEM).sounds(BlockSoundGroup.NETHER_WOOD)));
    public static final Block EMBERWOOD_PLANKS = register("emberwood_planks", new Block(FabricBlockSettings.copyOf(Blocks.CRIMSON_PLANKS).sounds(BlockSoundGroup.NETHER_WOOD)));
    public static final Block EMBERWOOD_LEAVES = register("emberwood_leaves", new LeavesBlock(FabricBlockSettings.copyOf(Blocks.NETHER_WART_BLOCK).luminance(4).nonOpaque().sounds(BlockSoundGroup.WART_BLOCK)));

    // Dungeon & Boss Blocks
    public static final Block DUNGEON_GATE_BLOCK = register("dungeon_gate_block", new Block(FabricBlockSettings.create().strength(10.0f, 1200.0f).sounds(BlockSoundGroup.ANVIL).luminance(10)));
    public static final Block BOSS_ALTAR = register("boss_altar", new Block(FabricBlockSettings.create().strength(8.0f, 1200.0f).sounds(BlockSoundGroup.LODESTONE).luminance(12)));

    private static Block register(String name, Block block) {
        Identifier id = new Identifier(AscendMod.MOD_ID, name);
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new FabricItemSettings()));
        return block;
    }

    public static void register() {
        AscendMod.LOGGER.info("[Ascend] Registering blocks...");
    }
}

