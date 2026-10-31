import os
from PIL import Image

def hex_to_rgb(hex_code):
    hex_code = hex_code.lstrip("#")
    return tuple(int(hex_code[i:i+2], 16) for i in (0, 2, 4))

def get_luminance(r, g, b):
    return 0.299 * r + 0.587 * g + 0.114 * b

def recolor_mob(src_img, tone_map):
    rgba = src_img.convert("RGBA")
    w, h = rgba.size
    pix = rgba.load()

    unique_colors = set()
    valid_coords = []
    for y in range(h):
        for x in range(w):
            r, g, b, a = pix[x, y]
            if a > 10:
                valid_coords.append((x, y))
                unique_colors.add((r, g, b))

    if not unique_colors:
        return rgba

    sorted_colors = sorted(list(unique_colors), key=lambda c: get_luminance(c[0], c[1], c[2]))
    n_tones = len(tone_map)
    color_lut = {}
    for idx, c in enumerate(sorted_colors):
        rank = idx / max(1, len(sorted_colors) - 1)
        tone_idx = min(n_tones - 1, int(round(rank * (n_tones - 1))))
        color_lut[c] = tone_map[tone_idx]

    out_img = rgba.copy()
    out_pix = out_img.load()
    for (x, y) in valid_coords:
        orig = (pix[x, y][0], pix[x, y][1], pix[x, y][2])
        out_pix[x, y] = (*color_lut[orig], pix[x, y][3])

    return out_img

def generate_goblin_king():
    # Base: zombie.png (64x64 or 64x32)
    src_path = "tools/templates/entity/zombie.png"
    src = Image.open(src_path)
    if src.size == (64, 32):
        canvas = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
        canvas.paste(src, (0, 0))
        src = canvas

    tones = [
        hex_to_rgb("#172e0a"), # dark green outline
        hex_to_rgb("#274e13"), # shadow green
        hex_to_rgb("#38761d"), # goblin skin base
        hex_to_rgb("#6aa84f"), # goblin skin light
        hex_to_rgb("#93c47d")  # highlight
    ]
    img = recolor_mob(src, tones)
    pix = img.load()

    # Add Goblin King Golden Crown on Head
    # Head front is at x: 8..15, y: 8..15. Crown at y: 6..9
    gold_dark = hex_to_rgb("#7a4f10")
    gold_base = hex_to_rgb("#d49b20")
    gold_light = hex_to_rgb("#ffd45e")
    ruby = hex_to_rgb("#e02424")

    # Front face: eyes at (10, 12) and (13, 12) -> red glowing eyes
    pix[10, 12] = (*ruby, 255)
    pix[13, 12] = (*ruby, 255)

    # Crown spikes on head front (8..15, 7)
    for x in range(8, 16):
        pix[x, 8] = (*gold_base, 255)
    pix[8, 7] = (*gold_light, 255)
    pix[10, 7] = (*gold_light, 255)
    pix[12, 7] = (*gold_light, 255)
    pix[15, 7] = (*gold_light, 255)
    # Crown center ruby
    pix[11, 8] = (*ruby, 255)

    # Royal fur collar on body front (20..27, 20..22)
    fur_white = hex_to_rgb("#e6e6e6")
    for x in range(20, 28):
        pix[x, 20] = (*fur_white, 255)
        pix[x, 21] = (*gold_dark, 255)

    return img

def generate_shadow_knight():
    # Base: wither_skeleton.png
    src_path = "tools/templates/entity/wither_skeleton.png"
    src = Image.open(src_path)
    if src.size == (64, 32):
        canvas = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
        canvas.paste(src, (0, 0))
        src = canvas

    tones = [
        hex_to_rgb("#0d0614"), # void black
        hex_to_rgb("#1a0f29"), # dark obsidian
        hex_to_rgb("#2c1b44"), # shadow steel base
        hex_to_rgb("#4a2b72"), # plate highlight
        hex_to_rgb("#7242ad")  # purple glint
    ]
    img = recolor_mob(src, tones)
    pix = img.load()

    void_cyan = hex_to_rgb("#4dd8ff")
    void_pink = hex_to_rgb("#ff4fd8")
    white = hex_to_rgb("#ffffff")

    # Glowing eye slits on face (10, 12), (13, 12)
    pix[10, 12] = (*void_cyan, 255)
    pix[11, 12] = (*white, 255)
    pix[13, 12] = (*void_cyan, 255)
    pix[14, 12] = (*white, 255)

    # Void core runes on chest (22..25, 22..24)
    pix[23, 22] = (*void_pink, 255)
    pix[24, 22] = (*void_pink, 255)
    pix[23, 23] = (*white, 255)
    pix[24, 23] = (*void_cyan, 255)
    pix[23, 24] = (*void_pink, 255)
    pix[24, 24] = (*void_pink, 255)

    return img

def generate_crystal_golem():
    # Base: iron_golem.png (128x128)
    src_path = "tools/templates/entity/iron_golem.png"
    src = Image.open(src_path)

    tones = [
        hex_to_rgb("#111317"), # deepslate dark outline
        hex_to_rgb("#20232a"), # deepslate shadow
        hex_to_rgb("#343842"), # slate stone base
        hex_to_rgb("#4a505e"), # stone highlight
        hex_to_rgb("#666e80")  # edge glint
    ]
    img = recolor_mob(src, tones)
    pix = img.load()

    # Add Glowing Cyan Crystals growing on golem
    crystal_dark = hex_to_rgb("#0b3a4a")
    crystal_base = hex_to_rgb("#1a7fa0")
    crystal_light = hex_to_rgb("#3fc8e8")
    crystal_white = hex_to_rgb("#ffffff")

    # Glowing heart in chest (body front is at x: 18..58, y: 21..61)
    for cy in range(35, 42):
        for cx in range(35, 42):
            pix[cx, cy] = (*crystal_base, 255)
    pix[38, 38] = (*crystal_white, 255)
    pix[39, 38] = (*crystal_light, 255)
    pix[38, 39] = (*crystal_light, 255)

    # Crystal clusters on shoulder (arm tops)
    for sx, sy in [(10, 42), (11, 42), (12, 43), (11, 44), (70, 42), (71, 42), (72, 43)]:
        if sx < img.width and sy < img.height:
            pix[sx, sy] = (*crystal_light, 255)

    return img

def generate_void_lich():
    # Base: wither_skeleton.png
    src_path = "tools/templates/entity/wither_skeleton.png"
    src = Image.open(src_path)
    if src.size == (64, 32):
        canvas = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
        canvas.paste(src, (0, 0))
        src = canvas

    tones = [
        hex_to_rgb("#08040d"), # dark abyss
        hex_to_rgb("#160b24"), # robe shadow
        hex_to_rgb("#2b1347"), # robe dark purple
        hex_to_rgb("#4d1e82"), # robe royal violet
        hex_to_rgb("#8538db")  # trim
    ]
    img = recolor_mob(src, tones)
    pix = img.load()

    bone_white = hex_to_rgb("#f0eff5")
    bone_shade = hex_to_rgb("#b8b5c9")
    void_pink = hex_to_rgb("#ff4fd8")
    void_cyan = hex_to_rgb("#00ffff")

    # Pale bone skull mask on head front (8..15, 8..15)
    for y in range(8, 16):
        for x in range(8, 16):
            pix[x, y] = (*(bone_white if (x+y)%2==0 else bone_shade), 255)

    # Glowing demonic hollow eye sockets
    pix[10, 11] = (*void_pink, 255)
    pix[10, 12] = (*void_cyan, 255)
    pix[13, 11] = (*void_pink, 255)
    pix[13, 12] = (*void_cyan, 255)

    # Crown of Thorns on skull top
    for x in [8, 10, 12, 14]:
        pix[x, 7] = (*void_pink, 255)

    return img

def generate_abyss_dragon():
    # Base: dragon.png (256x256)
    src_path = "tools/templates/entity/dragon.png"
    src = Image.open(src_path)

    tones = [
        hex_to_rgb("#08050e"), # abyss black
        hex_to_rgb("#140a24"), # dark void scales
        hex_to_rgb("#23103d"), # void base
        hex_to_rgb("#3d186b"), # scale light
        hex_to_rgb("#6a29ba")  # spine edge
    ]
    img = recolor_mob(src, tones)
    pix = img.load()

    void_flame = hex_to_rgb("#ff4fd8")
    cyan_flame = hex_to_rgb("#4dd8ff")

    # Wings & Horn highlights (dragon eyes at x: 28, y: 34 etc)
    for y in range(30, 36):
        for x in range(25, 32):
            if pix[x, y][3] > 0:
                pix[x, y] = (*void_flame, 255)

    return img

def main():
    boss_dir = "src/main/resources/assets/ascend/textures/entity/boss"
    os.makedirs(boss_dir, exist_ok=True)

    print("Generating Goblin King...")
    gk = generate_goblin_king()
    gk.save(os.path.join(boss_dir, "goblin_king.png"))

    print("Generating Shadow Knight...")
    sk = generate_shadow_knight()
    sk.save(os.path.join(boss_dir, "shadow_knight.png"))

    print("Generating Crystal Golem...")
    cg = generate_crystal_golem()
    cg.save(os.path.join(boss_dir, "crystal_golem.png"))

    print("Generating Void Lich...")
    vl = generate_void_lich()
    vl.save(os.path.join(boss_dir, "void_lich.png"))

    print("Generating Abyss Dragon...")
    ad = generate_abyss_dragon()
    ad.save(os.path.join(boss_dir, "abyss_dragon.png"))

    print("All 5 Boss skins generated successfully in", boss_dir)

if __name__ == "__main__":
    main()
