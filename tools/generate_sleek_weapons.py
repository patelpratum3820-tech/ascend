import os
from PIL import Image

def hex_to_rgba(h, a=255):
    h = h.lstrip('#')
    return (int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16), a)

OUT_DIR = "src/main/resources/assets/ascend/textures/item"
os.makedirs(OUT_DIR, exist_ok=True)

# -------------------------------------------------------------
# PALETTES
# -------------------------------------------------------------
PALETTES = {
    "mythril": {
        "tip": hex_to_rgba("ffffff"),
        "hi": hex_to_rgba("4dd8ff"),
        "mid": hex_to_rgba("6fa3e8"),
        "dark": hex_to_rgba("1b2a4a"),
        "shadow": hex_to_rgba("101a2e"),
        "guard_hi": hex_to_rgba("a9d4ff"),
        "guard": hex_to_rgba("3a5a9c"),
        "guard_dark": hex_to_rgba("1b2a4a"),
        "grip_hi": hex_to_rgba("8a5b3a"),
        "grip": hex_to_rgba("5a351d"),
        "pommel": hex_to_rgba("4dd8ff")
    },
    "dragonite": {
        "tip": hex_to_rgba("fff2c0"),
        "hi": hex_to_rgba("f2c14e"),
        "mid": hex_to_rgba("e86a4a"),
        "dark": hex_to_rgba("b83a3a"),
        "shadow": hex_to_rgba("520e18"),
        "guard_hi": hex_to_rgba("ffd970"),
        "guard": hex_to_rgba("d49826"),
        "guard_dark": hex_to_rgba("704707"),
        "grip_hi": hex_to_rgba("4a2222"),
        "grip": hex_to_rgba("2b1111"),
        "pommel": hex_to_rgba("f2c14e")
    },
    "voidsteel": {
        "tip": hex_to_rgba("f5e6ff"),
        "hi": hex_to_rgba("ff4fd8"),
        "mid": hex_to_rgba("8a4fd0"),
        "dark": hex_to_rgba("4b2a7a"),
        "shadow": hex_to_rgba("1a0a2e"),
        "guard_hi": hex_to_rgba("d9a8ff"),
        "guard": hex_to_rgba("3d2161"),
        "guard_dark": hex_to_rgba("160729"),
        "grip_hi": hex_to_rgba("332840"),
        "grip": hex_to_rgba("1c1524"),
        "pommel": hex_to_rgba("ff4fd8")
    }
}

# -------------------------------------------------------------
# 1. CLASSIC MINECRAFT SWORD (16x16)
# Sleek 2-pixel wide blade matching vanilla diamond sword
# -------------------------------------------------------------
def make_sword(name, p):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    pix = img.load()

    # Tip
    pix[15, 0] = p["tip"]
    pix[14, 0] = p["dark"]
    pix[15, 1] = p["dark"]

    # Blade (Diagonal running from (13,2) down to (6,9))
    blade_steps = [
        (13, 1, p["hi"]), (14, 1, p["mid"]), (14, 2, p["dark"]),
        (12, 2, p["hi"]), (13, 2, p["mid"]), (13, 3, p["dark"]),
        (11, 3, p["hi"]), (12, 3, p["mid"]), (12, 4, p["dark"]),
        (10, 4, p["hi"]), (11, 4, p["mid"]), (11, 5, p["dark"]),
        (9,  5, p["hi"]), (10, 5, p["mid"]), (10, 6, p["dark"]),
        (8,  6, p["hi"]), (9,  6, p["mid"]), (9,  7, p["dark"]),
        (7,  7, p["hi"]), (8,  7, p["mid"]), (8,  8, p["dark"]),
        (6,  8, p["hi"]), (7,  8, p["mid"]), (7,  9, p["dark"]),
        (5,  9, p["hi"]), (6,  9, p["mid"]), (6, 10, p["dark"])
    ]
    for x, y, c in blade_steps:
        pix[x, y] = c

    # Guard Left Wing
    pix[2, 9] = p["guard_hi"]
    pix[2, 10] = p["guard_dark"]
    pix[3, 9] = p["guard_hi"]
    pix[3, 10] = p["guard"]
    pix[4, 10] = p["guard_hi"]

    # Guard Right Wing
    pix[5, 10] = p["guard"]
    pix[6, 11] = p["guard_hi"]
    pix[6, 12] = p["guard"]
    pix[7, 12] = p["guard_dark"]

    # Crossguard Center Boss / Gem
    pix[4, 11] = p["guard_dark"]
    pix[5, 11] = p["tip"]

    # Hilt / Grip
    pix[3, 12] = p["grip_hi"]
    pix[2, 13] = p["grip"]

    # Pommel
    pix[1, 13] = p["guard_dark"]
    pix[1, 14] = p["pommel"]
    pix[0, 14] = p["guard_dark"]
    pix[0, 15] = p["guard_dark"]
    pix[1, 15] = p["pommel"]
    pix[2, 14] = p["guard_dark"]

    img.save(os.path.join(OUT_DIR, f"{name}_sword.png"))
    print(f"Generated {name}_sword.png")

# -------------------------------------------------------------
# 2. SLEEK KATANA (16x16)
# Single-edge curved blade, gold tsuba, wrapped grip
# -------------------------------------------------------------
def make_katana(name, p):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    pix = img.load()

    # Razor tip
    pix[15, 0] = p["tip"]
    pix[14, 0] = p["hi"]
    pix[15, 1] = p["shadow"]

    # Curved single cutting edge (highlighted edge on top, dark spine on back)
    blade_steps = [
        (13, 1, p["tip"]), (14, 1, p["hi"]), (14, 2, p["shadow"]),
        (12, 2, p["tip"]), (13, 2, p["hi"]), (13, 3, p["dark"]),
        (11, 3, p["hi"]),  (12, 3, p["mid"]), (12, 4, p["dark"]),
        (10, 4, p["hi"]),  (11, 4, p["mid"]), (11, 5, p["dark"]),
        (9,  5, p["hi"]),  (10, 5, p["mid"]), (10, 6, p["dark"]),
        (8,  6, p["hi"]),  (9,  6, p["mid"]), (9,  7, p["dark"]),
        (7,  7, p["hi"]),  (8,  7, p["mid"]), (8,  8, p["dark"]),
        (6,  8, p["hi"]),  (7,  8, p["mid"]), (7,  9, p["dark"]),
        (5,  9, p["hi"]),  (6,  9, p["mid"]), (6, 10, p["dark"])
    ]
    for x, y, c in blade_steps:
        pix[x, y] = c

    # Circular Tsuba (Guard)
    tsuba_gold_hi = hex_to_rgba("ffe680")
    tsuba_gold_mid = hex_to_rgba("cfa020")
    tsuba_dark = hex_to_rgba("5c4205")

    pix[3, 10] = tsuba_gold_hi
    pix[4, 10] = tsuba_gold_hi
    pix[5, 10] = tsuba_gold_mid
    pix[3, 11] = tsuba_gold_hi
    pix[4, 11] = tsuba_dark
    pix[5, 11] = tsuba_gold_mid
    pix[4, 12] = tsuba_dark
    pix[5, 12] = tsuba_gold_mid

    # Wrapped Tsuka (Handle with traditional diamond wraps)
    pix[3, 12] = p["tip"] # Diamond wrap accent
    pix[2, 13] = p["dark"]
    pix[3, 13] = p["tip"] # Wrap accent
    pix[1, 14] = p["dark"]
    pix[2, 14] = p["dark"]

    # Kashira (Pommel cap)
    pix[0, 15] = tsuba_gold_mid
    pix[1, 15] = tsuba_gold_hi
    pix[0, 14] = tsuba_dark

    img.save(os.path.join(OUT_DIR, f"{name}_katana.png"))
    print(f"Generated {name}_katana.png")

# -------------------------------------------------------------
# 3. DAGGER (16x16)
# Compact, agile thrusting blade
# -------------------------------------------------------------
def make_dagger(name, p):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    pix = img.load()

    # Tip at (11, 4)
    pix[11, 4] = p["tip"]
    pix[10, 4] = p["hi"]
    pix[11, 5] = p["dark"]

    # Short blade
    blade_steps = [
        (9, 5, p["hi"]),  (10, 5, p["mid"]), (10, 6, p["dark"]),
        (8, 6, p["hi"]),  (9,  6, p["mid"]), (9,  7, p["dark"]),
        (7, 7, p["hi"]),  (8,  7, p["mid"]), (8,  8, p["dark"]),
        (6, 8, p["hi"]),  (7,  8, p["mid"]), (7,  9, p["dark"])
    ]
    for x, y, c in blade_steps:
        pix[x, y] = c

    # Guard
    pix[5, 8] = p["guard_hi"]
    pix[5, 9] = p["guard"]
    pix[6, 9] = p["guard_hi"]
    pix[7, 10] = p["guard_dark"]
    pix[4, 9] = p["guard_dark"]

    # Grip
    pix[4, 10] = p["grip_hi"]
    pix[3, 11] = p["grip"]
    pix[2, 12] = p["grip"]

    # Pommel ring
    pix[1, 13] = p["pommel"]
    pix[2, 13] = p["guard_dark"]
    pix[1, 14] = p["guard_dark"]

    img.save(os.path.join(OUT_DIR, f"{name}_dagger.png"))
    print(f"Generated {name}_dagger.png")

# -------------------------------------------------------------
# 4. GREATSWORD (16x16)
# Broad, imposing two-handed blade with fuller
# -------------------------------------------------------------
def make_greatsword(name, p):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    pix = img.load()

    # Broad Tip
    pix[15, 0] = p["tip"]
    pix[14, 0] = p["hi"]
    pix[15, 1] = p["mid"]
    pix[14, 1] = p["dark"]

    # Broad blade with central fuller
    for i in range(1, 9):
        x = 14 - i
        y = i
        pix[x, y] = p["hi"]
        pix[x + 1, y] = p["tip"] # Fuller highlight
        pix[x, y + 1] = p["mid"]
        pix[x + 1, y + 1] = p["dark"]

    # Imposing Crossguard
    pix[2, 8] = p["guard_hi"]
    pix[3, 8] = p["guard_hi"]
    pix[3, 9] = p["guard"]
    pix[4, 9] = p["guard_hi"]
    pix[4, 10] = p["guard"]
    pix[5, 10] = p["guard"]
    pix[6, 10] = p["guard_hi"]
    pix[6, 11] = p["guard"]
    pix[7, 11] = p["guard_dark"]
    pix[7, 12] = p["guard_dark"]

    # Long Two-Handed Hilt
    pix[3, 11] = p["grip_hi"]
    pix[2, 12] = p["grip"]
    pix[2, 13] = p["grip_hi"]
    pix[1, 14] = p["grip"]

    # Heavy Pommel
    pix[0, 15] = p["pommel"]
    pix[1, 15] = p["guard_dark"]
    pix[0, 14] = p["guard_dark"]

    img.save(os.path.join(OUT_DIR, f"{name}_greatsword.png"))
    print(f"Generated {name}_greatsword.png")

# Generate all weapon tiers
for mat, pal in PALETTES.items():
    make_sword(mat, pal)
    make_katana(mat, pal)
    make_dagger(mat, pal)
    make_greatsword(mat, pal)
