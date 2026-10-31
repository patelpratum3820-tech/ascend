import gzip
import struct
import os
import json

# NBT Tag Type Constants
TAG_END = 0
TAG_BYTE = 1
TAG_SHORT = 2
TAG_INT = 3
TAG_LONG = 4
TAG_FLOAT = 5
TAG_DOUBLE = 6
TAG_BYTE_ARRAY = 7
TAG_STRING = 8
TAG_LIST = 9
TAG_COMPOUND = 10
TAG_INT_ARRAY = 11
TAG_LONG_ARRAY = 12

class NBT:
    @staticmethod
    def write_string(buf, s):
        raw = s.encode('utf-8')
        buf.extend(struct.pack('>H', len(raw)))
        buf.extend(raw)

    @classmethod
    def write_tag(cls, buf, tag_type, name, value):
        buf.append(tag_type)
        if name is not None:
            cls.write_string(buf, name)
        cls.write_value(buf, tag_type, value)

    @classmethod
    def write_value(cls, buf, tag_type, value):
        if tag_type == TAG_BYTE:
            buf.append(value & 0xFF)
        elif tag_type == TAG_SHORT:
            buf.extend(struct.pack('>h', value))
        elif tag_type == TAG_INT:
            buf.extend(struct.pack('>i', value))
        elif tag_type == TAG_LONG:
            buf.extend(struct.pack('>q', value))
        elif tag_type == TAG_FLOAT:
            buf.extend(struct.pack('>f', value))
        elif tag_type == TAG_DOUBLE:
            buf.extend(struct.pack('>d', value))
        elif tag_type == TAG_STRING:
            cls.write_string(buf, value)
        elif tag_type == TAG_LIST:
            elem_type, elem_list = value
            buf.append(elem_type)
            buf.extend(struct.pack('>i', len(elem_list)))
            for v in elem_list:
                cls.write_value(buf, elem_type, v)
        elif tag_type == TAG_COMPOUND:
            for k, (t, v) in value.items():
                cls.write_tag(buf, t, k, v)
            buf.append(TAG_END)

def create_structure_nbt(size_xyz, palette_names, blocks_data):
    # Palette list of compounds
    pal_compounds = []
    for p in palette_names:
        pal_compounds.append({
            "Name": (TAG_STRING, p)
        })

    # Blocks list of compounds
    block_compounds = []
    for (x, y, z, state_idx) in blocks_data:
        block_compounds.append({
            "pos": (TAG_LIST, (TAG_INT, [x, y, z])),
            "state": (TAG_INT, state_idx)
        })

    root = {
        "DataVersion": (TAG_INT, 3465),
        "size": (TAG_LIST, (TAG_INT, list(size_xyz))),
        "entities": (TAG_LIST, (TAG_COMPOUND, [])),
        "palette": (TAG_LIST, (TAG_COMPOUND, pal_compounds)),
        "blocks": (TAG_LIST, (TAG_COMPOUND, block_compounds))
    }

    buf = bytearray()
    NBT.write_tag(buf, TAG_COMPOUND, "", root)
    return gzip.compress(bytes(buf))

# 1. Guild Hall (11x7x11: Stone brick foundation, oak pillars, wood roof, table, chest)
def build_guild_hall():
    palette = [
        "minecraft:stone_bricks",
        "minecraft:oak_planks",
        "minecraft:oak_log",
        "minecraft:glass_pane",
        "minecraft:oak_door",
        "minecraft:chest",
        "minecraft:crafting_table",
        "minecraft:bookshelf",
        "minecraft:lantern"
    ]
    blocks = []
    # Floor (y=0)
    for x in range(11):
        for z in range(11):
            blocks.append((x, 0, z, 0)) # stone bricks

    # Walls (y=1..4)
    for y in range(1, 5):
        for x in range(11):
            for z in range(11):
                if x in (0, 10) or z in (0, 10):
                    # Pillars at corners
                    if (x in (0, 10) and z in (0, 10)):
                        blocks.append((x, y, z, 2)) # oak log
                    elif (x == 5 and z == 0 and y in (1, 2)):
                        pass # Doorway
                    elif y in (2, 3) and (x in (3, 7) or z in (3, 7)):
                        blocks.append((x, y, z, 3)) # glass pane
                    else:
                        blocks.append((x, y, z, 1)) # oak planks

    # Roof (y=5, 6)
    for x in range(11):
        for z in range(11):
            blocks.append((x, 5, z, 1))
    for x in range(2, 9):
        for z in range(2, 9):
            blocks.append((x, 6, z, 0))

    # Furniture inside
    blocks.append((2, 1, 8, 5)) # chest
    blocks.append((3, 1, 8, 6)) # crafting table
    blocks.append((8, 1, 8, 7)) # bookshelf
    blocks.append((8, 2, 8, 7)) # bookshelf
    blocks.append((5, 3, 5, 8)) # lantern

    return create_structure_nbt((11, 7, 11), palette, blocks)

# 2. Forge Ruins (7x5x7: cracked stone, blast furnace, anvil, chest)
def build_forge_ruins():
    palette = [
        "minecraft:cobblestone",
        "minecraft:mossy_cobblestone",
        "minecraft:blast_furnace",
        "minecraft:anvil",
        "minecraft:chest",
        "minecraft:iron_bars",
        "minecraft:lava"
    ]
    blocks = []
    for x in range(7):
        for z in range(7):
            blocks.append((x, 0, z, 0 if (x+z)%2==0 else 1))

    # Broken pillars
    for y in range(1, 4):
        blocks.append((0, y, 0, 0))
        blocks.append((6, y, 0, 1))
        blocks.append((0, y, 6, 1))
        if y < 3:
            blocks.append((6, y, 6, 0))

    # Forge setup
    blocks.append((3, 1, 5, 2)) # blast furnace
    blocks.append((2, 1, 5, 3)) # anvil
    blocks.append((4, 1, 5, 4)) # chest
    blocks.append((3, 1, 3, 6)) # lava pool
    blocks.append((2, 1, 3, 5)) # iron bars
    blocks.append((4, 1, 3, 5))

    return create_structure_nbt((7, 5, 7), palette, blocks)

# 3. Ruined Shrine (5x6x5: mossy stone pedestal with skill chest)
def build_ruined_shrine():
    palette = [
        "minecraft:mossy_stone_bricks",
        "minecraft:chiseled_stone_bricks",
        "minecraft:stone_brick_stairs",
        "minecraft:chest",
        "minecraft:sea_lantern",
        "minecraft:soul_torch"
    ]
    blocks = []
    for x in range(5):
        for z in range(5):
            blocks.append((x, 0, z, 0))

    # 4 Corner Pillars
    for y in range(1, 5):
        blocks.append((0, y, 0, 0))
        blocks.append((4, y, 0, 0))
        blocks.append((0, y, 4, 0))
        blocks.append((4, y, 4, 0))

    # Pedestal & Shrine altar
    blocks.append((2, 1, 2, 1)) # chiseled
    blocks.append((2, 2, 2, 3)) # chest
    blocks.append((2, 0, 2, 4)) # sea lantern under pedestal
    blocks.append((0, 5, 0, 5)) # torches
    blocks.append((4, 5, 0, 5))

    return create_structure_nbt((5, 6, 5), palette, blocks)

# 4. Mage Tower (9x16x9: Tall mystical stone tower with enchanting table)
def build_mage_tower():
    palette = [
        "minecraft:deepslate_bricks",
        "ascend:mystic_planks",
        "ascend:mystic_log",
        "minecraft:bookshelf",
        "minecraft:enchanting_table",
        "minecraft:chest",
        "minecraft:lantern",
        "minecraft:glass"
    ]
    blocks = []
    # Floors (y=0, 5, 10)
    for x in range(9):
        for z in range(9):
            blocks.append((x, 0, z, 0))
            blocks.append((x, 5, z, 1))
            blocks.append((x, 10, z, 1))

    # Walls (y=1..15)
    for y in range(1, 15):
        for x in range(9):
            for z in range(9):
                if x in (0, 8) or z in (0, 8):
                    if (x in (0, 8) and z in (0, 8)):
                        blocks.append((x, y, z, 2)) # mystic log corners
                    elif (x == 4 and z == 0 and y in (1, 2)):
                        pass # Doorway
                    elif y in (3, 8, 13) and (x == 4 or z == 4):
                        blocks.append((x, y, z, 7)) # glass window
                    else:
                        blocks.append((x, y, z, 0)) # deepslate

    # Roof battlements
    for x in range(9):
        for z in range(9):
            if x in (0, 8) or z in (0, 8):
                if (x + z) % 2 == 0:
                    blocks.append((x, 15, z, 0))

    # Inside furnishings: Enchanting library at top floor
    blocks.append((4, 11, 4, 4)) # enchanting table
    for bx in [2, 3, 5, 6]:
        blocks.append((bx, 11, 6, 3))
        blocks.append((bx, 12, 6, 3))
    blocks.append((2, 11, 2, 5)) # staff chest
    blocks.append((4, 14, 4, 6)) # lantern

    return create_structure_nbt((9, 16, 9), palette, blocks)

# 5. Boss Arena (19x6x19: Colosseum ring with Boss Altar at center)
def build_boss_arena():
    palette = [
        "minecraft:crying_obsidian",
        "minecraft:obsidian",
        "minecraft:polished_blackstone_bricks",
        "ascend:boss_altar",
        "minecraft:soul_fire"
    ]
    blocks = []
    # Floor (y=0)
    for x in range(19):
        for z in range(19):
            dx = x - 9
            dz = z - 9
            dist = dx*dx + dz*dz
            if dist <= 81:
                blocks.append((x, 0, z, 2 if (x+z)%2==0 else 1))

    # Ring wall & pillars
    for x in range(19):
        for z in range(19):
            dx = x - 9
            dz = z - 9
            dist = dx*dx + dz*dz
            if 70 <= dist <= 81:
                for y in range(1, 4):
                    blocks.append((x, y, z, 0 if y==3 else 1))

    # 4 Braziers with Soul Fire
    for (px, pz) in [(5, 5), (13, 5), (5, 13), (13, 13)]:
        blocks.append((px, 1, pz, 1))
        blocks.append((px, 2, pz, 4))

    # Center Boss Altar!
    blocks.append((9, 1, 9, 3))

    return create_structure_nbt((19, 6, 19), palette, blocks)

# 6. Floating Temple (Sky Isle: 13x12x13 floating stone/diorite with quartz shrine)
def build_floating_temple():
    palette = [
        "minecraft:grass_block",
        "minecraft:dirt",
        "minecraft:diorite",
        "minecraft:smooth_quartz",
        "minecraft:quartz_pillar",
        "minecraft:chest",
        "minecraft:sea_lantern"
    ]
    blocks = []
    # Island inverted cone underside (y=0..5)
    for y in range(6):
        radius = (y + 1)
        r2 = radius * radius
        for x in range(13):
            for z in range(13):
                dx = x - 6
                dz = z - 6
                if dx*dx + dz*dz <= r2:
                    blocks.append((x, y, z, 2 if y < 3 else 1))

    # Grass top surface (y=6)
    for x in range(13):
        for z in range(13):
            dx = x - 6
            dz = z - 6
            if dx*dx + dz*dz <= 36:
                blocks.append((x, 6, z, 0)) # grass block

    # Quartz Temple (y=7..11)
    for (cx, cz) in [(3, 3), (9, 3), (3, 9), (9, 9)]:
        for y in range(7, 11):
            blocks.append((cx, y, cz, 4)) # quartz pillar

    # Temple roof & altar
    for x in range(3, 10):
        for z in range(3, 10):
            blocks.append((x, 11, z, 3)) # smooth quartz

    blocks.append((6, 7, 6, 6)) # sea lantern
    blocks.append((6, 8, 6, 5)) # high-tier loot chest

    return create_structure_nbt((13, 12, 13), palette, blocks)

# 7. Demon Gate (9x10x9: Nether portal frame with dungeon gate block)
def build_demon_gate():
    palette = [
        "minecraft:nether_bricks",
        "minecraft:crying_obsidian",
        "ascend:dungeon_gate_block",
        "minecraft:soul_fire",
        "minecraft:magma_block",
        "minecraft:chain"
    ]
    blocks = []
    # Base
    for x in range(9):
        for z in range(9):
            blocks.append((x, 0, z, 0 if (x+z)%2==0 else 4))

    # Gate Pillars
    for y in range(1, 8):
        blocks.append((2, y, 4, 1))
        blocks.append((6, y, 4, 1))

    # Top Arch
    for x in range(2, 7):
        blocks.append((x, 8, 4, 1))

    # Gate block center
    blocks.append((4, 1, 4, 2)) # Dungeon Gate Block!

    # Soul Fire braziers
    blocks.append((1, 1, 4, 3))
    blocks.append((7, 1, 4, 3))

    return create_structure_nbt((9, 10, 9), palette, blocks)

# 8. Dungeon Surface Gate (7x6x7: Ruined stone portal with dungeon gate block)
def build_dungeon_gate():
    palette = [
        "minecraft:deepslate_bricks",
        "minecraft:cracked_deepslate_bricks",
        "ascend:dungeon_gate_block",
        "minecraft:soul_torch",
        "minecraft:iron_bars"
    ]
    blocks = []
    # Base
    for x in range(7):
        for z in range(7):
            blocks.append((x, 0, z, 0 if (x+z)%2==0 else 1))

    # Arch pillars
    for y in range(1, 5):
        blocks.append((1, y, 3, 0))
        blocks.append((5, y, 3, 0))

    # Top arch
    for x in range(1, 6):
        blocks.append((x, 5, 3, 0))

    # Center Dungeon Gate Block!
    blocks.append((3, 1, 3, 2))

    # Soul Torches on pillars
    blocks.append((1, 5, 2, 3))
    blocks.append((5, 5, 2, 3))

    return create_structure_nbt((7, 6, 7), palette, blocks)

def main():
    struct_dir = "src/main/resources/data/ascend/structures"
    os.makedirs(struct_dir, exist_ok=True)

    structures = {
        "guild_hall": build_guild_hall(),
        "forge_ruins": build_forge_ruins(),
        "ruined_shrine": build_ruined_shrine(),
        "mage_tower": build_mage_tower(),
        "boss_arena": build_boss_arena(),
        "floating_temple": build_floating_temple(),
        "demon_gate": build_demon_gate(),
        "dungeon_gate": build_dungeon_gate()
    }

    for name, nbt_bytes in structures.items():
        path = os.path.join(struct_dir, f"{name}.nbt")
        with open(path, "wb") as f:
            f.write(nbt_bytes)
        print(f"Generated {path} ({len(nbt_bytes)} bytes)")

if __name__ == "__main__":
    main()

