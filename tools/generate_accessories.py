from PIL import Image
import os

def hex_to_rgb(hex_code):
    hex_code = hex_code.lstrip("#")
    return tuple(int(hex_code[i:i+2], 16) for i in (0, 2, 4))

def create_mana_necklace():
    # 16x16 golden chain with teardrop glowing cyan mana crystal pendant
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    pix = img.load()

    # Colors
    gold_dark = hex_to_rgb("#7a4f10")
    gold_base = hex_to_rgb("#d49b20")
    gold_light = hex_to_rgb("#f5d061")
    
    cyan_dark = hex_to_rgb("#0b3a4a")
    cyan_base = hex_to_rgb("#1a7fa0")
    cyan_light = hex_to_rgb("#3fc8e8")
    cyan_bright = hex_to_rgb("#9af0ff")
    white = hex_to_rgb("#ffffff")

    # Chain loop
    chain_pts = [
        (4, 2), (5, 2), (10, 2), (11, 2),
        (3, 3), (12, 3),
        (3, 4), (12, 4),
        (4, 5), (11, 5),
        (5, 6), (10, 6),
        (6, 7), (9, 7),
        (7, 8), (8, 8)
    ]
    for x, y in chain_pts:
        pix[x, y] = (*(gold_light if x <= 7 else gold_base), 255)
    # chain shadows
    for (x, y) in [(3, 4), (12, 4), (11, 5), (10, 6), (9, 7)]:
        pix[x, y] = (*gold_dark, 255)

    # Pendant connector
    pix[7, 9] = (*gold_light, 255)
    pix[8, 9] = (*gold_base, 255)

    # Crystal teardrop gemstone
    # outline
    gem_outline = [
        (7, 10), (8, 10),
        (6, 11), (9, 11),
        (5, 12), (10, 12),
        (5, 13), (10, 13),
        (6, 14), (9, 14),
        (7, 15), (8, 15)
    ]
    for x, y in gem_outline:
        pix[x, y] = (*cyan_dark, 255)

    # gem body
    pix[7, 11] = (*cyan_light, 255)
    pix[8, 11] = (*cyan_base, 255)
    
    pix[6, 12] = (*cyan_bright, 255)
    pix[7, 12] = (*white, 255)
    pix[8, 12] = (*cyan_light, 255)
    pix[9, 12] = (*cyan_base, 255)

    pix[6, 13] = (*cyan_light, 255)
    pix[7, 13] = (*cyan_light, 255)
    pix[8, 13] = (*cyan_base, 255)
    pix[9, 13] = (*cyan_dark, 255)

    pix[7, 14] = (*cyan_base, 255)
    pix[8, 14] = (*cyan_dark, 255)

    return img

def create_dragon_pendant():
    # 16x16 fiery crimson dragon scale pendant with gold filigree
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    pix = img.load()

    gold_dark = hex_to_rgb("#5c3600")
    gold_base = hex_to_rgb("#c28b17")
    gold_light = hex_to_rgb("#ffd45e")

    red_dark = hex_to_rgb("#3a0f1a")
    red_shadow = hex_to_rgb("#7a1f2e")
    red_base = hex_to_rgb("#b83a3a")
    red_light = hex_to_rgb("#e86a4a")
    red_high = hex_to_rgb("#ffd9a0")

    # Cord
    for (x, y) in [(4, 2), (5, 2), (10, 2), (11, 2), (3, 3), (12, 3), (4, 4), (11, 4), (5, 5), (10, 5), (6, 6), (9, 6), (7, 7), (8, 7)]:
        pix[x, y] = (*(gold_light if x <= 7 else gold_dark), 255)

    # Gold mount
    for (x, y) in [(6, 8), (7, 8), (8, 8), (9, 8)]:
        pix[x, y] = (*gold_base, 255)
    pix[7, 8] = (*gold_light, 255)

    # Dragon scale diamond shape
    scale_pts = [
        (7, 9), (8, 9),
        (6, 10), (7, 10), (8, 10), (9, 10),
        (5, 11), (6, 11), (7, 11), (8, 11), (9, 11), (10, 11),
        (5, 12), (6, 12), (7, 12), (8, 12), (9, 12), (10, 12),
        (6, 13), (7, 13), (8, 13), (9, 13),
        (7, 14), (8, 14),
        (7, 15)
    ]
    for x, y in scale_pts:
        pix[x, y] = (*red_base, 255)

    # Outline
    for (x, y) in [(7, 9), (6, 10), (5, 11), (5, 12), (6, 13), (7, 14), (7, 15), (8, 14), (9, 13), (10, 12), (10, 11), (9, 10), (8, 9)]:
        pix[x, y] = (*red_dark, 255)

    # Highlights (top-left)
    pix[7, 10] = (*red_high, 255)
    pix[6, 11] = (*red_light, 255)
    pix[7, 11] = (*red_high, 255)
    pix[6, 12] = (*red_light, 255)

    # Gold horn trim on sides
    pix[4, 11] = (*gold_light, 255)
    pix[11, 11] = (*gold_dark, 255)

    return img

def create_void_amulet():
    # 16x16 dark obsidian amulet with glowing magenta void core
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    pix = img.load()

    obsidian_dark = hex_to_rgb("#120a1f")
    obsidian_base = hex_to_rgb("#2a1745")
    obsidian_light = hex_to_rgb("#4b2a7a")

    void_dark = hex_to_rgb("#8a4fd0")
    void_light = hex_to_rgb("#d9a8ff")
    void_accent = hex_to_rgb("#ff4fd8")
    white = hex_to_rgb("#ffffff")

    # Dark silver chain
    silver_light = hex_to_rgb("#99aab5")
    silver_dark = hex_to_rgb("#4f545c")
    for (x, y) in [(4, 2), (5, 2), (10, 2), (11, 2), (3, 3), (12, 3), (4, 4), (11, 4), (5, 5), (10, 5), (6, 6), (9, 6), (7, 7), (8, 7)]:
        pix[x, y] = (*(silver_light if x <= 7 else silver_dark), 255)

    # Amulet circular frame
    frame = [
        (6, 8), (7, 8), (8, 8), (9, 8),
        (5, 9), (10, 9),
        (4, 10), (11, 10),
        (4, 11), (11, 11),
        (4, 12), (11, 12),
        (5, 13), (10, 13),
        (6, 14), (7, 14), (8, 14), (9, 14)
    ]
    for x, y in frame:
        pix[x, y] = (*obsidian_dark, 255)
    # top-left highlight on frame
    pix[6, 8] = (*obsidian_light, 255)
    pix[7, 8] = (*obsidian_light, 255)
    pix[5, 9] = (*obsidian_light, 255)
    pix[4, 10] = (*obsidian_light, 255)

    # Void gem core
    for y in range(9, 14):
        for x in range(5, 11):
            if (x, y) not in frame:
                pix[x, y] = (*obsidian_base, 255)

    # Pulsing core
    pix[7, 10] = (*void_accent, 255)
    pix[8, 10] = (*void_dark, 255)
    pix[7, 11] = (*white, 255)
    pix[8, 11] = (*void_light, 255)
    pix[7, 12] = (*void_accent, 255)
    pix[8, 12] = (*void_dark, 255)

    return img

def main():
    item_out = "src/main/resources/assets/ascend/textures/item"
    os.makedirs(item_out, exist_ok=True)

    mana_neck = create_mana_necklace()
    mana_neck.save(os.path.join(item_out, "mana_necklace.png"))
    print("Saved mana_necklace.png")

    drag_pend = create_dragon_pendant()
    drag_pend.save(os.path.join(item_out, "dragon_pendant.png"))
    print("Saved dragon_pendant.png")

    void_am = create_void_amulet()
    void_am.save(os.path.join(item_out, "void_amulet.png"))
    print("Saved void_amulet.png")

if __name__ == "__main__":
    main()

