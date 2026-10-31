import os
from PIL import Image, ImageDraw

OUTPUT_DIR = "src/main/resources/assets/ascend/textures/entity/mob"
os.makedirs(OUTPUT_DIR, exist_ok=True)

def create_spirit_fox():
    img = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Palette
    c_base = (235, 245, 255, 255)       # Soft white-blue
    c_shadow = (140, 180, 220, 255)     # Sky blue shadow
    c_dark = (50, 90, 160, 255)         # Deep mystic blue
    c_glow = (77, 216, 255, 255)        # Cyan glow
    c_eye = (0, 220, 255, 255)          # Bright cyan eyes
    c_nose = (20, 30, 60, 255)

    # Head (0, 0) to (24, 16)
    draw.rectangle([0, 0, 28, 15], fill=c_base)
    draw.rectangle([0, 10, 28, 15], fill=c_shadow)
    # Eyes & nose on front face (8, 4) to (14, 10)
    draw.point((9, 6), fill=c_eye)
    draw.point((14, 6), fill=c_eye)
    draw.rectangle([11, 8, 12, 9], fill=c_nose)
    # Snout
    draw.rectangle([6, 12, 10, 14], fill=c_base)
    draw.point((8, 14), fill=c_nose)
    # Ears
    draw.rectangle([8, 1, 10, 3], fill=c_dark)
    draw.rectangle([15, 1, 17, 3], fill=c_dark)
    # Body (24, 15) to (40, 31)
    draw.rectangle([24, 15, 40, 31], fill=c_base)
    draw.rectangle([24, 25, 40, 31], fill=c_shadow)
    # Rune glow stripe on back
    draw.line([(28, 17), (28, 28)], fill=c_glow, width=1)
    draw.line([(32, 17), (32, 28)], fill=c_glow, width=1)
    # Legs (13, 24) to (24, 31)
    draw.rectangle([13, 24, 24, 31], fill=c_dark)
    draw.rectangle([13, 29, 24, 31], fill=c_glow) # Glowing paws
    # Tail (30, 0) to (46, 11)
    draw.rectangle([30, 0, 46, 11], fill=c_base)
    draw.rectangle([38, 0, 46, 11], fill=c_glow) # Glowing tail tip
    
    img.save(os.path.join(OUTPUT_DIR, "spirit_fox.png"))
    print("Generated spirit_fox.png")

def create_mana_deer():
    img = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    c_fur = (35, 55, 95, 255)         # Deep mystic blue
    c_dark = (20, 30, 60, 255)        # Dark navy
    c_light = (90, 145, 220, 255)     # Mana light
    c_rune = (63, 200, 232, 255)      # Cyan rune
    c_antler = (169, 212, 255, 255)   # Crystalline silver-cyan
    c_eye = (154, 240, 255, 255)

    # Cow/Deer layout
    # Head (0, 0) to (18, 16)
    draw.rectangle([0, 0, 18, 16], fill=c_fur)
    draw.point((4, 6), fill=c_eye)
    draw.point((13, 6), fill=c_eye)
    draw.rectangle([6, 10, 11, 14], fill=c_dark) # Snout
    # Antlers / Horns
    draw.rectangle([22, 0, 23, 6], fill=c_antler)
    # Body (18, 4) to (40, 31)
    draw.rectangle([18, 4, 40, 31], fill=c_fur)
    draw.rectangle([22, 8, 36, 14], fill=c_light) # Mystic back
    # Runic spots
    draw.point((24, 16), fill=c_rune)
    draw.point((26, 18), fill=c_rune)
    draw.point((30, 16), fill=c_rune)
    draw.point((32, 20), fill=c_rune)
    # Legs (0, 16) to (18, 31)
    draw.rectangle([0, 16, 18, 31], fill=c_dark)
    draw.rectangle([0, 28, 18, 31], fill=c_rune) # Glowing hooves
    
    img.save(os.path.join(OUTPUT_DIR, "mana_deer.png"))
    print("Generated mana_deer.png")

def create_slime_pup():
    img = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Slime cube layout
    c_trans = (63, 200, 232, 190)    # Semi-transparent cyan
    c_core = (11, 58, 74, 240)       # Dark cyan core
    c_bright = (154, 240, 255, 220)  # Highlight
    c_eye = (10, 25, 45, 255)        # Cute dark eyes
    c_sparkle = (255, 255, 255, 255)

    # Outer slime body (0, 0) to (32, 16)
    draw.rectangle([0, 0, 32, 16], fill=c_trans)
    draw.rectangle([4, 2, 8, 4], fill=c_bright)
    # Cute eyes
    draw.rectangle([6, 7, 8, 10], fill=c_eye)
    draw.rectangle([14, 7, 16, 10], fill=c_eye)
    draw.point((6, 7), fill=c_sparkle)
    draw.point((14, 7), fill=c_sparkle)
    # Inner body (0, 16) to (32, 32)
    draw.rectangle([0, 16, 32, 32], fill=c_core)
    draw.rectangle([4, 18, 10, 24], fill=c_trans)
    
    img.save(os.path.join(OUTPUT_DIR, "slime_pup.png"))
    print("Generated slime_pup.png")

def create_moon_moth():
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    c_body = (35, 15, 60, 255)       # Void purple
    c_wing = (138, 79, 208, 220)     # Luminous purple wing
    c_glow = (77, 216, 255, 255)     # Glowing cyan edge
    c_eye = (255, 255, 255, 255)     # Glowing white eyes
    
    # Bat layout
    # Head & Body (0, 0) to (20, 20)
    draw.rectangle([0, 0, 20, 20], fill=c_body)
    draw.point((4, 4), fill=c_eye)
    draw.point((8, 4), fill=c_eye)
    # Wings (0, 20) to (32, 45) and (32, 20) to (64, 45)
    draw.rectangle([0, 20, 32, 45], fill=c_wing)
    draw.line([(0, 20), (31, 20)], fill=c_glow, width=2)
    draw.line([(0, 20), (0, 44)], fill=c_glow, width=2)
    
    draw.rectangle([32, 20, 64, 45], fill=c_wing)
    draw.line([(32, 20), (63, 20)], fill=c_glow, width=2)
    draw.line([(63, 20), (63, 44)], fill=c_glow, width=2)
    
    img.save(os.path.join(OUTPUT_DIR, "moon_moth.png"))
    print("Generated moon_moth.png")

def create_biped_mob(name, skin_base, skin_shadow, armor_base, armor_trim, eye_color):
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Head (0, 0, 32, 16)
    draw.rectangle([0, 0, 32, 16], fill=skin_base)
    draw.rectangle([0, 8, 32, 16], fill=skin_shadow)
    # Eyes on front face (8, 8, 16, 16)
    draw.rectangle([9, 11, 11, 12], fill=eye_color)
    draw.rectangle([13, 11, 15, 12], fill=eye_color)
    
    # Torso (16, 16, 40, 32)
    draw.rectangle([16, 16, 40, 32], fill=armor_base)
    draw.line([(16, 16), (39, 16)], fill=armor_trim, width=1)
    draw.line([(16, 31), (39, 31)], fill=armor_trim, width=1)
    # Belt / emblem
    draw.rectangle([26, 26, 29, 29], fill=armor_trim)
    
    # Right Arm (40, 16, 56, 32)
    draw.rectangle([40, 16, 56, 32], fill=skin_base)
    draw.rectangle([40, 16, 56, 22], fill=armor_base) # Pauldron
    
    # Left Arm (32, 48, 48, 64)
    draw.rectangle([32, 48, 48, 64], fill=skin_base)
    draw.rectangle([32, 48, 48, 54], fill=armor_base)
    
    # Right Leg (0, 16, 16, 32)
    draw.rectangle([0, 16, 16, 32], fill=skin_shadow)
    draw.rectangle([0, 26, 16, 32], fill=armor_base) # Boots
    
    # Left Leg (16, 48, 32, 64)
    draw.rectangle([16, 48, 32, 64], fill=skin_shadow)
    draw.rectangle([16, 58, 32, 64], fill=armor_base)
    
    img.save(os.path.join(OUTPUT_DIR, f"{name}.png"))
    print(f"Generated {name}.png")

def create_fire_drake():
    # Phantom/Dragon layout: 64x64
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    c_scale = (184, 58, 58, 255)       # Crimson scales
    c_shadow = (122, 31, 46, 255)     # Deep dark red
    c_belly = (232, 106, 74, 255)     # Fiery orange
    c_glow = (242, 193, 78, 255)      # Gold fire
    c_eye = (255, 230, 120, 255)      # Yellow fiery eye
    
    # Body
    draw.rectangle([0, 0, 24, 20], fill=c_scale)
    draw.rectangle([0, 12, 24, 20], fill=c_belly)
    draw.point((6, 6), fill=c_eye)
    draw.point((14, 6), fill=c_eye)
    # Wings
    draw.rectangle([0, 20, 32, 48], fill=c_scale)
    draw.line([(0, 20), (31, 20)], fill=c_glow, width=2)
    draw.rectangle([32, 20, 64, 48], fill=c_scale)
    draw.line([(32, 20), (63, 20)], fill=c_glow, width=2)
    # Tail
    draw.rectangle([0, 48, 20, 64], fill=c_shadow)
    draw.rectangle([8, 58, 12, 64], fill=c_glow) # Flame tip
    
    img.save(os.path.join(OUTPUT_DIR, "fire_drake.png"))
    print("Generated fire_drake.png")

def create_dungeon_golem():
    # Iron Golem layout: 128x128
    img = Image.new("RGBA", (128, 128), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    c_stone = (70, 75, 85, 255)       # Dungeon cobblestone
    c_dark = (40, 45, 55, 255)        # Deep dungeon slate
    c_rune = (77, 216, 255, 255)      # Glowing mythril rune
    c_eye = (234, 246, 255, 255)      # Piercing white-cyan eye
    c_iron = (120, 130, 145, 255)     # Iron bands
    
    # Head (0, 0) to (32, 20)
    draw.rectangle([0, 0, 40, 30], fill=c_stone)
    draw.rectangle([10, 12, 14, 16], fill=c_eye)
    draw.rectangle([20, 12, 24, 16], fill=c_eye)
    # Runic forehead engraving
    draw.line([(12, 4), (22, 4)], fill=c_rune, width=2)
    draw.line([(17, 4), (17, 10)], fill=c_rune, width=2)
    
    # Torso (0, 40) to (60, 90)
    draw.rectangle([0, 40, 70, 95], fill=c_stone)
    draw.rectangle([10, 45, 60, 50], fill=c_iron) # Chest iron band
    # Great glowing rune in center of chest
    draw.line([(25, 60), (45, 60)], fill=c_rune, width=3)
    draw.line([(35, 55), (35, 75)], fill=c_rune, width=3)
    draw.rectangle([33, 63, 37, 67], fill=c_eye)
    
    # Arms (60, 40) to (90, 110)
    draw.rectangle([70, 40, 95, 110], fill=c_stone)
    draw.rectangle([70, 70, 95, 75], fill=c_iron)
    draw.line([(80, 45), (80, 105)], fill=c_rune, width=2)
    
    # Legs (37, 0) to (75, 40)
    draw.rectangle([40, 0, 80, 40], fill=c_dark)
    draw.line([(55, 5), (55, 35)], fill=c_rune, width=2)
    
    img.save(os.path.join(OUTPUT_DIR, "dungeon_golem.png"))
    print("Generated dungeon_golem.png")

create_spirit_fox()
create_mana_deer()
create_slime_pup()
create_moon_moth()

# Bipeds
# 1. Goblin
create_biped_mob("goblin", (62, 123, 43, 255), (36, 75, 25, 255), (107, 68, 35, 255), (75, 45, 20, 255), (242, 193, 78, 255))
# 2. Orc Warrior
create_biped_mob("orc_warrior", (122, 58, 43, 255), (80, 35, 25, 255), (60, 65, 75, 255), (184, 58, 58, 255), (255, 68, 68, 255))
# 3. Shadow Wraith
create_biped_mob("shadow_wraith", (18, 10, 31, 230), (10, 5, 18, 240), (42, 23, 69, 230), (138, 79, 208, 255), (217, 168, 255, 255))
# 4. Skeleton Knight
create_biped_mob("skeleton_knight", (196, 191, 165, 255), (130, 125, 105, 255), (58, 90, 156, 255), (111, 163, 232, 255), (77, 216, 255, 255))
# 5. Dark Mage
create_biped_mob("dark_mage", (25, 20, 35, 255), (10, 5, 15, 255), (35, 20, 55, 255), (63, 200, 232, 255), (77, 216, 255, 255))

create_fire_drake()
create_dungeon_golem()
print("All 11 mob textures successfully generated!")
