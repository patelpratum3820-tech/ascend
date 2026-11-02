import os
import math
from PIL import Image, ImageDraw, ImageFont

OUT_DIR = "world_design/phase_1_geography/preview"
os.makedirs(OUT_DIR, exist_ok=True)

# 1 pixel = 10 blocks (800x800 image = 8,000x8,000 blocks)
# Image coordinates: (0, 0) is top-left (-4000, -4000)
# Center (400, 400) is (0, 0)

IMG_SIZE = 800

def world_to_img(wx, wz):
    ix = int((wx + 4000) / 10)
    iz = int((wz + 4000) / 10)
    return ix, iz

def make_continent_map():
    img = Image.new("RGBA", (IMG_SIZE, IMG_SIZE), (18, 42, 85, 255)) # Deep Ocean
    draw = ImageDraw.Draw(img)

    # 1. Base Continent Landmass (Irregular organic shape)
    # Organic perimeter points around center (400, 400)
    continent_pts = []
    num_pts = 64
    for i in range(num_pts):
        ang = (2 * math.pi * i) / num_pts
        # Base radius ~3200m (320px in img) with procedural harmonic wobbles
        r_base = 320.0
        r_wobble = 45.0 * math.sin(3 * ang) + 30.0 * math.cos(5 * ang) + 20.0 * math.sin(7 * ang)
        # Pull in at northwest and southeast for organic coastal bays
        if 0.5 < ang < 1.8: # Northeast / East
            r_wobble += 25.0
        elif 3.2 < ang < 4.5: # Southwest
            r_wobble -= 35.0
        r = r_base + r_wobble
        cx = 400 + r * math.cos(ang)
        cz = 400 + r * math.sin(ang)
        continent_pts.append((cx, cz))

    # Fill base landmass (Rich continental green)
    draw.polygon(continent_pts, fill=(68, 128, 56, 255), outline=(45, 95, 38, 255))

    # 2. Regional Biome Color Overlays
    # Northern Frozen Mountains (Z < -1500 -> iz < 250)
    frozen_pts = []
    for ang in [math.pi * x / 16.0 for x in range(16, 33)]:
        r = 280.0 + 35.0 * math.sin(ang * 4)
        cx = 400 + r * math.cos(ang)
        cz = 400 + r * math.sin(ang)
        if cz < 270:
            frozen_pts.append((cx, cz))
    frozen_pts.extend([(400 + 260, 270), (400 - 260, 270)])
    if len(frozen_pts) > 2:
        draw.polygon(frozen_pts, fill=(225, 238, 245, 255)) # Snow / Glacier

    # Southeastern Volcanic Region (X: 1800..3400, Z: 1000..2600 -> ix: 580..740, iz: 500..660)
    draw.ellipse([580, 500, 740, 660], fill=(85, 42, 38, 255)) # Scorched badlands
    draw.ellipse([620, 540, 700, 620], fill=(38, 24, 28, 255)) # Basalt caldera
    draw.ellipse([645, 565, 675, 595], fill=(220, 85, 25, 255)) # Magma lake

    # Eastern Dragon Ravines (ix: 560..720, iz: 240..380)
    draw.ellipse([560, 240, 720, 380], fill=(168, 105, 62, 255)) # Red sandstone canyons
    # Ravine cuts (dark lines)
    draw.line([(580, 260), (680, 340)], fill=(60, 35, 20, 255), width=4)
    draw.line([(620, 250), (690, 370)], fill=(60, 35, 20, 255), width=3)
    draw.line([(570, 310), (710, 320)], fill=(60, 35, 20, 255), width=3)

    # Western Elven Forest (ix: 100..280, iz: 240..420)
    draw.ellipse([100, 240, 280, 420], fill=(28, 72, 38, 255)) # Ancient deep forest

    # Prism Pools (ix: 100..250, iz: 480..600)
    draw.ellipse([100, 480, 250, 600], fill=(70, 155, 145, 255)) # Mineral cyan
    draw.ellipse([135, 510, 185, 555], fill=(85, 215, 225, 255)) # Hot spring pool
    draw.ellipse([175, 530, 220, 580], fill=(185, 115, 225, 255)) # Amethyst mineral pool

    # Southern Beastkin Savanna (ix: 240..420, iz: 560..720)
    draw.ellipse([240, 560, 420, 720], fill=(185, 152, 65, 255)) # Amber savanna

    # 3. Hydrography (Lake Veyra & Rivers)
    # Lake Veyra at (0, -700) -> (400, 330)
    lake_pts = [
        (400, 305), (425, 315), (440, 335), (430, 355),
        (405, 360), (375, 350), (360, 330), (375, 310)
    ]
    draw.polygon(lake_pts, fill=(35, 88, 155, 255), outline=(25, 65, 115, 255))

    # Silverfall Torrent (North down to Lake Veyra)
    draw.line([(410, 160), (405, 230), (400, 305)], fill=(35, 88, 155, 255), width=3)

    # Whispering Run (West Elven hills to Lake Veyra)
    draw.line([(220, 290), (300, 315), (365, 330)], fill=(35, 88, 155, 255), width=2)

    # Great Aurelian River (South from Lake Veyra past Aurelia to Delta)
    draw.line([(405, 360), (410, 400), (415, 490), (420, 540)], fill=(35, 88, 155, 255), width=4)
    # Eastern delta branch
    draw.line([(420, 540), (520, 560), (620, 550), (740, 530)], fill=(35, 88, 155, 255), width=3)
    # Southern delta branch
    draw.line([(420, 540), (435, 620), (450, 720)], fill=(35, 88, 155, 255), width=3)

    # 4. Regional Marker Points & Labels
    markers = [
        ("✦ TEMPLE / SPAWN (0, 0)", 0, 0, (255, 215, 0)),
        ("Aurelia Capital (0, 900)", 0, 900, (255, 255, 255)),
        ("Lake Veyra (0, -700)", 0, -700, (140, 220, 255)),
        ("Frostspire (0, -3000)", 0, -3000, (200, 240, 255)),
        ("Elarion Sanctuary (-2300, -800)", -2300, -800, (150, 255, 170)),
        ("Prism Pools (-2500, 1300)", -2500, 1300, (100, 255, 240)),
        ("Beastkin Steppe (-900, 2500)", -900, 2500, (255, 220, 120)),
        ("Draconis Ravines (2500, -900)", 2500, -900, (255, 160, 100)),
        ("Inferno Keep (2600, 1800)", 2600, 1800, (255, 100, 80)),
        ("Shattered Frontier (500, 3500)", 500, 3500, (180, 180, 220))
    ]

    for label, wx, wz, col in markers:
        ix, iz = world_to_img(wx, wz)
        # Dot
        draw.ellipse([ix - 5, iz - 5, ix + 5, iz + 5], fill=col, outline=(0, 0, 0, 255))
        # Text label with dark background box
        tx = ix + 8
        ty = iz - 6
        if tx > 650: tx = ix - 180
        if ty > 760: ty = iz - 18
        if ty < 20: ty = iz + 8
        draw.rectangle([tx - 2, ty - 1, tx + len(label) * 6 + 4, ty + 12], fill=(10, 15, 25, 210))
        draw.text((tx, ty), label, fill=col)

    # 5. World Border Box & Coordinates Grid
    draw.rectangle([0, 0, IMG_SIZE - 1, IMG_SIZE - 1], outline=(255, 60, 60, 255), width=3)
    # Coordinate grid lines every 1000m (100px)
    for g in range(100, 800, 100):
        draw.line([(g, 0), (g, 800)], fill=(255, 255, 255, 25), width=1)
        draw.line([(0, g), (800, g)], fill=(255, 255, 255, 25), width=1)
        # Axis numbers
        coord_val = (g - 400) * 10
        draw.text((g + 2, 6), f"{coord_val}X", fill=(200, 200, 200, 180))
        draw.text((6, g + 2), f"{coord_val}Z", fill=(200, 200, 200, 180))

    # Legend / Title card
    draw.rectangle([10, 730, 340, 790], fill=(10, 14, 24, 230), outline=(0, 210, 255, 255), width=1)
    draw.text((18, 736), "ASCEND: CONTINENTAL GEOGRAPHY BLUEPRINT", fill=(255, 215, 0, 255))
    draw.text((18, 752), "Scale: 1 px = 10 blocks | 8,000 x 8,000 Continent", fill=(200, 220, 240, 255))
    draw.text((18, 768), "World Border: X [-4000, +4000] | Z [-4000, +4000]", fill=(255, 100, 100, 255))

    out_file = os.path.join(OUT_DIR, "continent_geography_map.png")
    img.save(out_file)
    print(f"Generated {out_file}")

def make_elevation_heatmap():
    img = Image.new("RGBA", (IMG_SIZE, IMG_SIZE), (20, 45, 95, 255)) # Sea level base
    pix = img.load()

    # Elevation palette: Blue (Sea) -> Green (Plains Y=70-85) -> Yellow (Hills Y=90-110)
    #                   -> Orange (Plateaus Y=120-160) -> Red/White (Peaks Y=170-230)
    for iz in range(IMG_SIZE):
        for ix in range(IMG_SIZE):
            wx = (ix - 400) * 10
            wz = (iz - 400) * 10
            dist_sq = wx * wx + wz * wz
            dist = math.sqrt(dist_sq)

            # Ocean margin
            if dist > 3400 + 350 * math.sin(math.atan2(wz, wx) * 5):
                # Deep ocean Y=30..63
                pix[ix, iz] = (15, 35, 80, 255)
                continue

            # Base land elevation
            y = 74.0

            # Northern Mountains (Z < -1500)
            if wz < -1200:
                rel = (-wz - 1200) / 2200.0
                mountain_noise = 0.5 * (1 + math.sin(wx * 0.003)) * (1 + math.cos(wz * 0.004))
                y += rel * (70.0 + 80.0 * mountain_noise)
                y = min(230.0, y)

            # Volcanic Caldera (2600, 1800)
            d_volc = math.sqrt((wx - 2600)**2 + (wz - 1800)**2)
            if d_volc < 1000:
                rel_v = (1000 - d_volc) / 1000.0
                # Caldera rim
                if 250 < d_volc < 650:
                    y += rel_v * 110.0
                elif d_volc <= 250:
                    y += 65.0 # Basin
                else:
                    y += rel_v * 60.0

            # Dragon Ravines (2500, -900)
            d_drag = math.sqrt((wx - 2500)**2 + (wz + 900)**2)
            if d_drag < 1100:
                rel_d = (1100 - d_drag) / 1100.0
                # Plateau
                y += rel_d * 85.0
                # Ravine cuts
                canyon_wave = math.sin(wx * 0.02) + math.cos(wz * 0.02)
                if abs(canyon_wave) < 0.25:
                    y -= 75.0 # Plunge to Y=55..75

            # Western Elven Hills (-2300, -800)
            d_elv = math.sqrt((wx + 2300)**2 + (wz + 800)**2)
            if d_elv < 1100:
                rel_e = (1100 - d_elv) / 1100.0
                y += rel_e * 45.0

            # Lake Veyra (0, -700)
            d_lake = math.sqrt((wx - 0)**2 + (wz + 700)**2)
            if d_lake < 450:
                # Water basin
                y = 63.0 - (1.0 - (d_lake / 450.0)) * 25.0

            # Color map based on Y
            if y <= 63.0:
                # Underwater (Y: 35..63)
                depth_t = max(0.0, min(1.0, (y - 35.0) / 28.0))
                col = (int(10 + 20*depth_t), int(30 + 50*depth_t), int(70 + 80*depth_t), 255)
            elif y < 85.0:
                # Grassland (Y: 64..85)
                t = (y - 63.0) / 22.0
                col = (int(50 + 40*t), int(120 + 35*t), int(45 + 20*t), 255)
            elif y < 115.0:
                # Foothills (Y: 85..115)
                t = (y - 85.0) / 30.0
                col = (int(90 + 70*t), int(155 + 20*t), int(65 - 20*t), 255)
            elif y < 165.0:
                # Plateaus & High Ridges (Y: 115..165)
                t = (y - 115.0) / 50.0
                col = (int(160 + 55*t), int(175 - 65*t), int(45 + 10*t), 255)
            else:
                # Mountain Summits (Y: 165..230)
                t = min(1.0, (y - 165.0) / 65.0)
                col = (int(215 + 40*t), int(110 + 130*t), int(55 + 195*t), 255)

            pix[ix, iz] = col

    # Draw border
    draw = ImageDraw.Draw(img)
    draw.rectangle([0, 0, IMG_SIZE - 1, IMG_SIZE - 1], outline=(255, 60, 60, 255), width=2)
    draw.rectangle([10, 730, 310, 790], fill=(10, 14, 24, 230), outline=(0, 210, 255, 255), width=1)
    draw.text((18, 736), "ASCEND: CONTINENTAL ELEVATION HEATMAP", fill=(255, 215, 0, 255))
    draw.text((18, 752), "Y Elevation: Blue (35-63) -> Green (70-85)", fill=(200, 220, 240, 255))
    draw.text((18, 768), "Yellow (90-115) -> Orange (120-165) -> White (170-230)", fill=(255, 180, 100, 255))

    out_file = os.path.join(OUT_DIR, "elevation_heatmap.png")
    img.save(out_file)
    print(f"Generated {out_file}")

make_continent_map()
make_elevation_heatmap()
