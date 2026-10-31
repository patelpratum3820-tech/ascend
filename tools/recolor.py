import os
import sys
from PIL import Image

PALETTES = {
    "mythril": {
        "outline": "#1b2a4a",
        "shadow": "#3a5a9c",
        "base": "#6fa3e8",
        "light": "#a9d4ff",
        "highlight": "#eaf6ff",
        "accent": "#4dd8ff"
    },
    "dragonite": {
        "outline": "#3a0f1a",
        "shadow": "#7a1f2e",
        "base": "#b83a3a",
        "light": "#e86a4a",
        "highlight": "#ffd9a0",
        "accent": "#f2c14e"
    },
    "voidsteel": {
        "outline": "#120a1f",
        "shadow": "#2a1745",
        "base": "#4b2a7a",
        "light": "#8a4fd0",
        "highlight": "#d9a8ff",
        "accent": "#ff4fd8"
    },
    "mana_crystal": {
        "outline": "#0b3a4a",
        "shadow": "#1a7fa0",
        "base": "#3fc8e8",
        "light": "#9af0ff",
        "highlight": "#ffffff",
        "accent": "#ffffff"
    }
}

def hex_to_rgb(hex_code):
    hex_code = hex_code.lstrip("#")
    return tuple(int(hex_code[i:i+2], 16) for i in (0, 2, 4))

def get_luminance(r, g, b):
    return 0.299 * r + 0.587 * g + 0.114 * b

def is_wood_handle(r, g, b):
    return r > b + 15 and g > b + 5 and r > 40 and r < 160 and b < 80

def is_gem_or_plate(r, g, b):
    return (b > r + 10 or g > r + 10) and (r + g + b > 60)

def recolor_image(src_img, mat_name, is_ore=False, is_armor=False, is_tool=False):
    src_rgba = src_img.convert("RGBA")
    width, height = src_rgba.size
    pixels = src_rgba.load()

    pal = PALETTES[mat_name]
    outline = hex_to_rgb(pal["outline"])
    shadow = hex_to_rgb(pal["shadow"])
    base = hex_to_rgb(pal["base"])
    light = hex_to_rgb(pal["light"])
    highlight = hex_to_rgb(pal["highlight"])
    accent = hex_to_rgb(pal["accent"])

    tone_map = [outline, shadow, base, light, highlight]

    target_pixels = []
    unique_rgbs = set()

    for y in range(height):
        for x in range(width):
            r, g, b, a = pixels[x, y]
            if a < 10:
                continue
            
            if is_ore:
                if is_gem_or_plate(r, g, b):
                    target_pixels.append((x, y))
                    unique_rgbs.add((r, g, b))
            elif is_tool:
                if not is_wood_handle(r, g, b):
                    target_pixels.append((x, y))
                    unique_rgbs.add((r, g, b))
            else:
                target_pixels.append((x, y))
                unique_rgbs.add((r, g, b))

    if not unique_rgbs:
        return src_rgba

    sorted_rgbs = sorted(list(unique_rgbs), key=lambda c: get_luminance(c[0], c[1], c[2]))
    n_colors = len(sorted_rgbs)

    color_lut = {}
    for idx, c in enumerate(sorted_rgbs):
        rank = idx / max(1, n_colors - 1)
        tone_idx = int(round(rank * 4))
        color_lut[c] = tone_map[tone_idx]

    out_img = src_rgba.copy()
    out_pixels = out_img.load()

    for (x, y) in target_pixels:
        orig_rgb = (pixels[x, y][0], pixels[x, y][1], pixels[x, y][2])
        orig_a = pixels[x, y][3]
        new_rgb = color_lut.get(orig_rgb, orig_rgb)
        out_pixels[x, y] = (new_rgb[0], new_rgb[1], new_rgb[2], orig_a)

    # Hand-crafted custom details
    if is_armor and not is_ore:
        if width == 64 and height == 32: # Layer 1
            # Chestplate rune / shoulder trim
            if out_pixels[20, 21][3] > 0: out_pixels[20, 21] = (*accent, 255)
            if out_pixels[20, 22][3] > 0: out_pixels[20, 22] = (*accent, 255)
            # Visor / crown accent
            if out_pixels[4, 1][3] > 0: out_pixels[4, 1] = (*highlight, 255)
            if out_pixels[5, 1][3] > 0: out_pixels[5, 1] = (*accent, 255)

    if is_ore:
        lightest_pos = None
        max_lum = -1
        for (x, y) in target_pixels:
            p = out_pixels[x, y]
            lum = get_luminance(p[0], p[1], p[2])
            if lum > max_lum:
                max_lum = lum
                lightest_pos = (x, y)
        if lightest_pos:
            out_pixels[lightest_pos[0], lightest_pos[1]] = (*accent, 255)

    return out_img

def process_all():
    template_dir = "tools/templates"
    item_out = "src/main/resources/assets/ascend/textures/item"
    block_out = "src/main/resources/assets/ascend/textures/block"
    armor_ascend = "src/main/resources/assets/ascend/textures/models/armor"
    armor_mc = "src/main/resources/assets/minecraft/textures/models/armor"

    os.makedirs(item_out, exist_ok=True)
    os.makedirs(block_out, exist_ok=True)
    os.makedirs(armor_ascend, exist_ok=True)
    os.makedirs(armor_mc, exist_ok=True)

    items_to_recolor = [
        ("diamond_sword.png", "sword.png", True, False, False),
        ("diamond_pickaxe.png", "pickaxe.png", True, False, False),
        ("diamond_axe.png", "axe.png", True, False, False),
        ("diamond_shovel.png", "shovel.png", True, False, False),
        ("diamond_hoe.png", "hoe.png", True, False, False),
        ("diamond_helmet.png", "helmet.png", False, False, False),
        ("diamond_chestplate.png", "chestplate.png", False, False, False),
        ("diamond_leggings.png", "leggings.png", False, False, False),
        ("diamond_boots.png", "boots.png", False, False, False),
    ]

    materials = ["mythril", "dragonite", "voidsteel"]
    recolored_images = []

    for mat in materials:
        print(f"Processing material: {mat}")
        # 1. Gear Items
        for src_name, suffix, is_tool, is_armor, is_ore in items_to_recolor:
            src_path = os.path.join(template_dir, src_name)
            if not os.path.exists(src_path):
                continue
            src_img = Image.open(src_path)
            res = recolor_image(src_img, mat, is_ore=is_ore, is_armor=is_armor, is_tool=is_tool)
            out_file = f"{mat}_{suffix}"
            out_path = os.path.join(item_out, out_file)
            res.save(out_path)
            recolored_images.append((out_file, res))

        # 2. Armor layers (64x32)
        for layer_num in ["1", "2"]:
            src_name = f"diamond_layer_{layer_num}.png"
            src_path = os.path.join(template_dir, src_name)
            if not os.path.exists(src_path):
                continue
            src_img = Image.open(src_path)
            res = recolor_image(src_img, mat, is_ore=False, is_armor=True, is_tool=False)
            out_file = f"{mat}_layer_{layer_num}.png"
            res.save(os.path.join(armor_ascend, out_file))
            res.save(os.path.join(armor_mc, out_file))
            recolored_images.append((out_file, res))

        # 3. Ores & Blocks
        ore_img = Image.open(os.path.join(template_dir, "diamond_ore.png"))
        deep_ore_img = Image.open(os.path.join(template_dir, "deepslate_diamond_ore.png"))
        
        ore_res = recolor_image(ore_img, mat, is_ore=True)
        deep_res = recolor_image(deep_ore_img, mat, is_ore=True)

        ore_res.save(os.path.join(block_out, f"{mat}_ore.png"))
        deep_res.save(os.path.join(block_out, f"deepslate_{mat}_ore.png"))
        recolored_images.append((f"{mat}_ore.png", ore_res))
        recolored_images.append((f"deepslate_{mat}_ore.png", deep_res))

        # 4. Ingots
        ingot_src = Image.open(os.path.join(template_dir, "netherite_ingot.png" if mat != "mythril" else "iron_ingot.png"))
        ingot_res = recolor_image(ingot_src, mat)
        # Add subtle corner gleam
        ingot_pix = ingot_res.load()
        pal = PALETTES[mat]
        accent_rgb = hex_to_rgb(pal["accent"])
        for (gx, gy) in [(4, 5), (5, 5)]:
            if ingot_pix[gx, gy][3] > 0:
                ingot_pix[gx, gy] = (*accent_rgb, 255)
        ingot_res.save(os.path.join(item_out, f"{mat}_ingot.png"))
        recolored_images.append((f"{mat}_ingot.png", ingot_res))

    # 5. Mana crystal item and ore
    print("Processing Mana Crystal...")
    crystal_src = Image.open(os.path.join(template_dir, "diamond.png"))
    mana_crystal_res = recolor_image(crystal_src, "mana_crystal")
    mana_crystal_res.save(os.path.join(item_out, "mana_crystal.png"))
    recolored_images.append(("mana_crystal.png", mana_crystal_res))

    mana_ore = recolor_image(Image.open(os.path.join(template_dir, "diamond_ore.png")), "mana_crystal", is_ore=True)
    deep_mana_ore = recolor_image(Image.open(os.path.join(template_dir, "deepslate_diamond_ore.png")), "mana_crystal", is_ore=True)
    mana_ore.save(os.path.join(block_out, "mana_crystal_ore.png"))
    deep_mana_ore.save(os.path.join(block_out, "deepslate_mana_crystal_ore.png"))
    recolored_images.append(("mana_crystal_ore.png", mana_ore))
    recolored_images.append(("deepslate_mana_crystal_ore.png", deep_mana_ore))

    # 6. Special Weapons
    # Mythril Katana (recolored curved sword from diamond sword)
    katana = recolor_image(Image.open(os.path.join(template_dir, "diamond_sword.png")), "mythril", is_tool=True)
    katana_pix = katana.load()
    # Add cyan edge gleam along the blade
    for i in range(4, 12):
        if katana_pix[i, 15 - i][3] > 0:
            katana_pix[i, 15 - i] = (*hex_to_rgb(PALETTES["mythril"]["accent"]), 255)
    katana.save(os.path.join(item_out, "mythril_katana.png"))
    recolored_images.append(("mythril_katana.png", katana))

    # Dragonite Greatsword
    greatsword = recolor_image(Image.open(os.path.join(template_dir, "diamond_sword.png")), "dragonite", is_tool=True)
    gs_pix = greatsword.load()
    gold_rgb = hex_to_rgb(PALETTES["dragonite"]["accent"])
    # Gold guard
    for (gx, gy) in [(6, 10), (7, 9), (5, 11)]:
        gs_pix[gx, gy] = (*gold_rgb, 255)
    greatsword.save(os.path.join(item_out, "dragonite_greatsword.png"))
    recolored_images.append(("dragonite_greatsword.png", greatsword))

    # Voidsteel Dagger
    dagger = recolor_image(Image.open(os.path.join(template_dir, "diamond_sword.png")), "voidsteel", is_tool=True)
    dag_pix = dagger.load()
    void_rgb = hex_to_rgb(PALETTES["voidsteel"]["accent"])
    for (dx, dy) in [(7, 8), (8, 7)]:
        dag_pix[dx, dy] = (*void_rgb, 255)
    dagger.save(os.path.join(item_out, "voidsteel_dagger.png"))
    recolored_images.append(("voidsteel_dagger.png", dagger))

    # Longbow
    bow_src = Image.open(os.path.join(template_dir, "bow.png"))
    longbow = recolor_image(bow_src, "mythril")
    longbow.save(os.path.join(item_out, "longbow.png"))
    recolored_images.append(("longbow.png", longbow))

    # 7. Create 16x Nearest-Neighbor Preview Sheet
    create_preview_sheet(recolored_images)

def create_preview_sheet(images_list):
    tiles_16x16 = [img for name, img in images_list if img.size == (16, 16)]
    cols = 8
    rows = (len(tiles_16x16) + cols - 1) // cols

    sheet_w = cols * 16
    sheet_h = rows * 16

    base_sheet = Image.new("RGBA", (sheet_w, sheet_h), (25, 27, 33, 255))

    for idx, img in enumerate(tiles_16x16):
        c = idx % cols
        r = idx // cols
        base_sheet.paste(img, (c * 16, r * 16), img)

    # 16x scale nearest-neighbor
    preview_16x = base_sheet.resize((sheet_w * 16, sheet_h * 16), Image.Resampling.NEAREST)
    preview_path = "tools/preview_sheet.png"
    preview_16x.save(preview_path)
    print(f"Created 16x preview sheet at {preview_path} ({preview_16x.size})")

if __name__ == "__main__":
    process_all()
