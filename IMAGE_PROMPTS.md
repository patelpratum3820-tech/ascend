# ASCEND — Image Prompts (for your AI image generator)

Save each result in `art_reference/final_art/` using the file name shown. The AI agent swaps them in.

## Style rules (add to EVERY prompt)
- **Pixel items/blocks/icons:** "pixel art, 16x16 Minecraft style, flat colors, limited palette, hard pixel edges, transparent background, no blur, no anti-aliasing"
- **Mob/boss skins:** "pixel art texture sheet, Minecraft style, blocky, hard edges, flat colors"
- **Big art (menus, portraits, loading screens):** "anime fantasy illustration, isekai style, vibrant colors, clean lineart, dramatic lighting" (these do NOT need to be pixel art)
- Always keep each item in the same palette family as its reference icon in `art_reference/icons_128`.
- After generating a pixel image, resize it with nearest-neighbor (no smoothing) to 16x16 (items) or the size listed.

## 1. Menu art (big anime style, 1920x1080 unless noted)
| File | Prompt |
|---|---|
| menu_difficulty.png | anime fantasy scene, four glowing stone gates labeled by color (green, yellow, orange, red), hero standing in front, isekai style |
| menu_race.png | anime lineup of five characters: human, angel with white wings and halo, demon with horns, dragonoid with green scales and horns, cute green slime; fantasy guild hall background |
| menu_class.png | anime lineup of five heroes: blacksmith with hammer and anvil, swordsman, archer, armored tank with big shield, mage with glowing staff |
| menu_skills.png | magical floating skill icons (dash, shield, slash, enchant book, golem) around a glowing rune circle |
| loading_screen_1-5.png | five different anime isekai adventure scenes: dragon over mountains, sky temple, demon castle, crystal cave, village guild at sunset |
| mod_icon_512.png (512x512) | logo "ASCEND" bold fantasy letters with a glowing sword and wings, dark background |

## 2. Race portraits (512x512, anime)
race_human.png, race_angel.png, race_demon.png, race_dragonoid.png, race_slime.png
Prompt pattern: "anime character portrait, [race description], bust shot, fantasy RPG style, transparent or simple background"

## 3. Class portraits (512x512, anime)
class_craftsman.png (blacksmith with glowing hammer), class_swordsman.png, class_archer.png, class_tank.png, class_mage.png

## 4. Skill icons (16x16 pixel, transparent) — replace the placeholders
skill_dash.png (cyan speed arrow), skill_block.png (cyan shield with spark), skill_strike.png (red slash), skill_enchant.png (purple book with sparkles), skill_reinforce.png (sword with green plus), skill_forge_legend.png (glowing golden sword), skill_construct.png (stone golem head with cyan eyes), skill_mana_bolt.png, skill_lightning.png, skill_arrow_rain.png, skill_iron_skin.png, skill_berserk.png, skill_flame_slash.png, skill_summon_soldier.png, skill_meteor.png

## 5. Items (16x16 pixel, transparent)
Ores/ingots/gems: mythril_ore, mythril_ingot (blue-silver), mana_crystal_ore, mana_crystal (cyan glowing), dragonite_ore, dragonite_ingot (deep red with gold), voidsteel_ingot (black-purple), shadow_essence (dark purple orb), dragon_scale (green-red scale)
Armor (helmet, chestplate, leggings, boots) for: mythril, dragonite, voidsteel. Also the worn-on-body texture (64x32 armor layer).
Tools/weapons per tier (mythril, dragonite, voidsteel): sword, pickaxe, axe, shovel, hoe. Specials: katana, greatsword, dagger, longbow.
Staffs: apprentice_staff (Mystic Wood + blue crystal), flame_staff, storm_staff, summoner_staff, void_staff.
Misc: dungeon_map, skill_scroll, essence_orb, forge_station (block), reset_stone.

## 6. Blocks (16x16 pixel, seamless)
mythril_ore_block, mana_crystal_ore_block, dragonite_ore_block, mystic_log (side+top), mystic_planks, mystic_leaves (blue-purple, glowing dots), emberwood_log, emberwood_planks, emberwood_leaves, forge_station, dungeon_gate_block, boss_altar.

## 7. Mobs (pixel texture, Minecraft-style model; give sizes)
Passive: spirit_fox (64x32, white fox with glowing blue tail), mana_deer (64x64, antlers with cyan crystals), slime_pup (32x32), moon_moth (32x32, glowing wings).
Aggressive: goblin (64x32, green, ragged armor), orc_warrior (64x64), shadow_wraith (64x64, dark hooded ghost, red eyes), skeleton_knight (64x64), dark_mage (64x64), fire_drake (128x64), dungeon_golem (128x64).

## 8. Bosses (concept art 1024x1024 anime + pixel texture)
| Boss | Concept prompt | Texture size |
|---|---|---|
| goblin_king | huge goblin king with crown and club, cave throne | 128x128 |
| shadow_knight | tall black knight, purple flame sword, torn cape | 128x128 |
| crystal_golem | giant stone golem covered in glowing cyan crystals | 256x256 |
| fallen_seraph | fallen angel, broken white-gold armor, dark wings, holy halo cracked | 256x256 |
| abyss_dragon | giant black-purple dragon with glowing red eyes, huge wings, abyss background | 512x512 |
| demon_lord | towering demon lord, crown of horns, dark flame cape, throne of bones | 512x512 |

## 9. Biomes and structures (concept art 1920x1080 anime; match colors of art_reference/biomes)
biome_enchanted_forest, biome_emberlands, biome_crystal_caverns, biome_sky_isles, biome_shadow_marsh, biome_dragon_peaks
structure_guild_hall, structure_forge_ruins, structure_ruined_shrine, structure_mage_tower, structure_dungeon_gate, structure_boss_arena, structure_floating_temple, structure_demon_gate

## 10. HUD / GUI (pixel, exact sizes)
hud_mana_bar.png (182x5), hud_stamina_bar.png (182x5), hud_skill_slot.png (22x22), status_window_bg.png (256x256), card_frame.png (nine-slice, 32x32)

## Tips
- Generate **one asset at a time** and check it in the game before generating the next.
- Fix prompts with: "keep same palette as [attach reference icon]".
- If your generator can't make clean 16x16, generate at 256x256 pixel-art style and downscale with nearest-neighbor.
