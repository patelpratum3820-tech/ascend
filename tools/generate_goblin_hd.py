import os
import random
from PIL import Image, ImageDraw

OUTPUT_DIR = "src/main/resources/assets/ascend/textures/entity/mob"
os.makedirs(OUTPUT_DIR, exist_ok=True)

def create_goblin_texture():
    random.seed(42)
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    pix = img.load()

    # Base colors
    skin_base = (78, 138, 52, 255)       # Olive goblin green
    skin_light = (98, 160, 68, 255)
    skin_dark = (52, 95, 34, 255)
    skin_shadow = (36, 68, 24, 255)

    leather_base = (112, 70, 38, 255)    # Rough brown leather
    leather_light = (138, 88, 48, 255)
    leather_dark = (76, 46, 24, 255)
    stitch_col = (200, 175, 140, 255)
    iron_buckle = (180, 190, 200, 255)

    eye_sclera = (255, 210, 60, 255)     # Glowing amber/yellow
    eye_pupil = (40, 10, 10, 255)        # Dark slit pupil
    tooth_col = (240, 235, 210, 255)

    # 1. Fill Head area: (0,0) to (32,16)
    for x in range(32):
        for y in range(16):
            n = random.randint(-8, 8)
            pix[x, y] = (
                max(0, min(255, skin_base[0] + n)),
                max(0, min(255, skin_base[1] + n)),
                max(0, min(255, skin_base[2] + n)),
                255
            )

    # Head Shading (chin and underside)
    for x in range(32):
        for y in range(13, 16):
            pix[x, y] = skin_dark

    # Front Face: x in [8, 15], y in [8, 15]
    # Eyes at (9, 11), (10, 11) and (13, 11), (14, 11)
    pix[9, 11] = eye_sclera
    pix[10, 11] = eye_pupil
    pix[13, 11] = eye_pupil
    pix[14, 11] = eye_sclera

    # Eyebrows (menacing furrow)
    pix[9, 10] = skin_shadow
    pix[10, 10] = skin_dark
    pix[13, 10] = skin_dark
    pix[14, 10] = skin_shadow

    # Flat Goblin Nose
    pix[11, 12] = skin_light
    pix[12, 12] = skin_light
    pix[11, 13] = skin_shadow
    pix[12, 13] = skin_shadow

    # Mouth & Tusks
    pix[10, 14] = skin_shadow
    pix[11, 14] = (60, 20, 20, 255)
    pix[12, 14] = (60, 20, 20, 255)
    pix[13, 14] = skin_shadow
    # Pointy little tusks sticking up
    pix[10, 13] = tooth_col
    pix[13, 13] = tooth_col

    # Ears on sides (right head profile: 0-7, left head profile: 16-23)
    pix[4, 11] = skin_light
    pix[5, 11] = skin_shadow
    pix[4, 12] = skin_dark
    pix[18, 11] = skin_shadow
    pix[19, 11] = skin_light
    pix[19, 12] = skin_dark

    # 2. Torso: (16, 16) to (40, 32)
    # Front Torso is x in [20, 27], y in [20, 31]
    for x in range(16, 40):
        for y in range(16, 32):
            pix[x, y] = leather_base

    for x in range(20, 28):
        for y in range(20, 28):
            # Vest with noise
            n = random.randint(-10, 10)
            pix[x, y] = (
                max(0, min(255, leather_base[0] + n)),
                max(0, min(255, leather_base[1] + n)),
                max(0, min(255, leather_base[2] + n)),
                255
            )

    # Torn vest center opening showing bare chest/belly
    pix[23, 20] = skin_dark
    pix[24, 20] = skin_dark
    pix[23, 21] = skin_base
    pix[24, 21] = skin_base
    pix[23, 22] = skin_light
    pix[24, 22] = skin_light
    # Stitches across vest
    pix[22, 21] = stitch_col
    pix[25, 22] = stitch_col

    # Belt at y in [28, 29]
    for x in range(16, 40):
        pix[x, 28] = leather_dark
        pix[x, 29] = (45, 25, 15, 255)
    # Iron Buckle at center
    pix[23, 28] = iron_buckle
    pix[24, 28] = iron_buckle
    pix[23, 29] = iron_buckle
    pix[24, 29] = iron_buckle

    # Lower Torso (trousers waistband)
    for x in range(16, 40):
        for y in range(30, 32):
            pix[x, y] = (50, 45, 40, 255)

    # 3. Arms (Right: 40-55, 16-31; Left: 32-47, 48-63)
    def texture_arm(x_start, y_start):
        for x in range(x_start, x_start + 16):
            for y in range(y_start, y_start + 16):
                rel_y = y - y_start
                if rel_y < 4:
                    # Shoulder leather strap
                    pix[x, y] = leather_dark
                elif rel_y < 11:
                    # Bare green arm
                    n = random.randint(-6, 6)
                    pix[x, y] = (skin_base[0]+n, skin_base[1]+n, skin_base[2]+n, 255)
                else:
                    # Wrist wrap / bracer
                    pix[x, y] = leather_base

    texture_arm(40, 16)
    texture_arm(32, 48)

    # 4. Legs (Right: 0-15, 16-31; Left: 16-31, 48-63)
    def texture_leg(x_start, y_start):
        for x in range(x_start, x_start + 16):
            for y in range(y_start, y_start + 16):
                rel_y = y - y_start
                if rel_y < 10:
                    # Ragged dark trousers
                    pix[x, y] = (55, 50, 45, 255)
                else:
                    # Worn leather boots
                    pix[x, y] = leather_dark

    texture_leg(0, 16)
    texture_leg(16, 48)

    out_file = os.path.join(OUTPUT_DIR, "goblin.png")
    img.save(out_file)
    print(f"Generated high quality shaded {out_file}")

create_goblin_texture()
