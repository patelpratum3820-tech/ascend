# ASCEND RPG MOD — Comprehensive Progress, Flaw Analysis & Architecture Report

**Version:** 0.1.0-alpha  
**Platform:** Minecraft 1.20.1 Java Edition (Fabric Loader 0.15.11 / Fabric API 0.92.2)  
**Repository:** `https://github.com/patelpratum3820-tech/ascend.git` (`main` branch)  
**Compiled Artifact:** `E:\games\ascend-0.1.0-alpha.jar` (544 KB)  
**Report Date:** November 1, 2026  

---

## 1. Executive Summary & Vision

**Ascend** is a full-scale anime/isekai action RPG mod for Minecraft 1.20.1. The project combines classical fantasy world-building, fluid hotbar-based skill combat, rank progression (Rank E through SS), in-depth racial traits, mining and gear progression, and expansive hand-crafted structure generation.

The core gameplay loop centers around:
1. **The Divine Awakening**: The player is summoned across worlds to the celestial *Temple of the Chosen*, where the Goddess of Rebirth grants them their choice of Race, Combat Class, and Starter Arts.
2. **Action-Driven Combat**: Instant-cast hotbar skills (bound to `R`, `F`, `C`, `X`, and `V`), directional dashing with invulnerability frames, dynamic stamina/mana resource pools, and custom damage formulas.
3. **Ascender's Grimoire**: Complete skill mastery and hotbar customization, allowing players to learn skills from ancient scrolls/tomes and equip, shuffle, and customize their combat loadout.
4. **Tiered World Progression**: Tiered mining levels (Deepslate Mythril and Dragonite), Adventurer Rank unlocks, and world dungeons (Goblin Cave Gate, Guild Hall, Ruined Castles, Boss Arenas).

---

## 2. Full Inventory of Completed Features

### 2.1. The 6 Fantasy Races
All races have dedicated base stat modifiers, speed scaling, passives, and vulnerability hooks implemented across `PlayerData.java`, `PlayerEntityMixin.java`, `CharacterCreationScreen.java`, and `AscendCommands.java`:

| Race | Base Stat Modifiers | Unique Passive Perks | Inherent Weaknesses |
| :--- | :--- | :--- | :--- |
| **Human** | Balanced base stats | **+15% Essence Gain** from all sources. | None. Standard RPG balance. |
| **Elf** | **+40 Max Mana**, frail vitality (-2 HP) | **+10% Movement Speed**, **1.5x Mana Regen**. | -2 Max Health. |
| **Beastkin** | **+3 Strength**, **+20 Max Stamina** | **Permanent Night Vision**, **+10% Dash Speed**. | -15 Max Mana pool. |
| **Demonkin** | **+4 Strength**, **+20 Max Mana** | **100% Fire & Lava Immunity**, **5% Melee Lifesteal**. | **+50% Holy/Magic Damage** taken. |
| **Dragonoid** | **+3 Strength**, Fire Immunity | Dragon form skill affinity, fire breath. | **-5% Movement Speed**, **+50% Cold/Drown** damage. |
| **Slime** | -4 Health, -2 Defense | **75% Fall Damage reduction**, **Passive HP regen**. | Frail max HP and lower armor value. |

---

### 2.2. The 5 Combat Classes
Each class has custom starter skills, stat affinities, weapon kits, and a unique **Signature Move** (`V` key):
* **Swordsman**: +2 Strength, +1 Speed. Signature: *Triple Slash Blast* (sweeping waves launching enemies).
* **Summoner**: +20 Mana, -2 Health. Signature: *Shadow Portal* (summons 3 armored Wither Skeletons).
* **Mage**: +30 Mana, -2 Defense. Signature: *Cataclysmic Meteor* (giant plunging fireball).
* **Archer**: +2 Speed, +1 Strength, -2 Defense. Signature: *Piercing Volley* (5-arrow piercing fan).
* **Tank**: +4 Defense, +4 Health, -2 Speed. Signature: *Guardian Slam* (ground pound launching mobs into the air).

---

### 2.3. All 44 RPG Skills (Ranks E to SS)
Every skill in `skill_ranks.json` is fully coded in `AscendSkills.java` with animations, particle effects, and audio:

```
[Rank E Starter Arts]
├─ basic_step          : Quick 3-block forward step with wind trails (10 i-frames).
├─ dash                : Directional evasion slide (10 i-frames).
├─ flash_step          : 6-block instant blink passing through mobs with sonic boom & sparks.
├─ flame_slash         : Wide fiery crescent blade wave dealing fire damage + ignite.
├─ mana_bolt           : High-velocity magic projectile.
├─ block               : Raises guard; grants Resistance IV (80% damage reduction) for 2s.
├─ power_strike        : Heavy downward cleave dealing 2.5x damage and 1.5s stun.
├─ enchant             : Craftsman skill; gives +Strength & +Haste for 60s.
└─ reinforce           : Craftsman skill; fully repairs held tool and grants Resistance II.

[Rank D Arts]
├─ whirlwind           : 360-degree sweep dealing 10 + 0.7*Str damage and knockback.
├─ frost_nova          : Freezing burst dealing 8 magic damage and Slowness II for 7s.
├─ arrow_rain          : 12 arrows rain down on target area.
├─ stone_skin          : +8 Defense, grants Resistance II & Absorption for 15s.
├─ spirit_wolf         : Summons loyal tamed Spirit Wolf with speed and resistance.
└─ grapple_shot        : Fires grappling arrow that pulls player 12 blocks forward.

[Rank C Arts]
├─ chain_fire          : Bouncing fire bolt jumping between up to 5 nearby targets.
├─ lightning_strike    : Calls down real lightning bolt at targeted location.
├─ berserk             : +Strength II and +Speed I for 15s.
├─ reflect             : Thorns barrier reflecting 40% damage for 5s.
├─ explosive_arrow     : Launches explosive arrow detonating on impact.
├─ blood_rush          : Grants Regeneration II & Strength I for 10s (lifesteal boost).
├─ summon_shadow_soldier: Summons 2 Shadow Soldiers with dark particles.
├─ haste_aura          : Speed II and Haste II to player and nearby allies for 30s.
└─ rune_carve          : Engraves ancient glyphs; grants Absorption II & Resistance II.

[Rank B Arts]
├─ mana_shield         : Energy barrier absorbing incoming damage with Absorption.
├─ triple_slash        : Three rapid slashes with projectile wave finisher.
├─ piercing_volley     : 5-arrow piercing fan dealing high damage.
├─ guardian_slam       : Heavy shockwave launching all nearby hostiles into the air.
├─ summon_construct    : Summons an Iron Golem construct ally with custom buffs.
├─ phoenix_familiar    : Summons flaming firebird ally and heals 12 HP.
└─ forge_legend        : Divine forge aura; grants Strength III & Resistance II for 45s.

[Rank A Arts]
├─ meteor_call         : Giant falling meteor creating massive crater explosion.
├─ undead_army         : Summons 10 armored undead soldiers for 60s.
├─ soul_bond           : Heals player by 50% max HP, granting Regeneration III.
├─ revive_token        : Grants Totem of Undying effect and Absorption IV.
└─ blade_tempest       : Whirling vortex of 16 sword strikes around the player.

[Rank S Arts]
├─ meteor_storm        : Barrage of 6 falling meteors across a wide radius.
├─ legion_of_shadows   : Summons 15 Shadow Legionnaires with dark aura.
├─ absolute_guard      : 5s near-invulnerability and 100% damage reflection.
├─ phantom_barrage     : Instantly strikes every enemy within 15 blocks with phantom daggers.
└─ dragon_awakening    : Dragonoid ultimate; flight, flame breath, Strength IV for 30s.

[Rank SS Divine Ultimates]
├─ judgment_meteor_shower: 15s automatic meteor strikes on all hostiles (bosses take 5% max HP).
├─ sovereigns_domain   : Shadow Monarch domain; 70% slow on enemies, +50% stats to allies.
└─ heavens_edge        : Screen-wide dimensional golden strike executing non-bosses.
```

---

### 2.4. Ascender's Grimoire & Skill Loadout Management
1. **In-Game Hotbar Customization (`SkillManagerScreen.java`)**:
   * Open anytime via **`H`** or by clicking **`§b✦ Grimoire`** inside the Status Screen (`K`).
   * Displays the 4 hotbar slots (`R`, `F`, `C`, `X`).
   * Lets players filter learned skills by Rank (`ALL`, `E`, `D`, `C`, `B`, `A`, `S`, `SS`).
   * One-click **`[Equip]`**, **`[Swap]`**, or **`[Unequip]`** buttons to assign any mastered skill.
2. **Scroll / Tome Unlock Screen (`SkillUnlockScreen.java`)**:
   * Right-clicking a Skill Scroll (E, D, C) or Skill Tome (B, A, S, SS) opens a selection GUI showing all skills of that rank.
   * Displays Name, Tag, Cost, Cooldown, and Description with a **`[ LEARN ]`** button.
   * Consumes 1 scroll upon learning and plays a challenge completion fanfare.

---

### 2.5. Textures & Visual Redesigns
1. **Weapons & Katanas**:
   * Replaced experimental models with genuine 16x16 Minecraft diagonal silhouettes (`mythril_sword`, `dragonite_sword`, `dragonite_greatsword`, `voidsteel_sword`, `mythril_katana`).
   * Handle at bottom-left `(1, 14)`, blade pointing to top-right `(14, 1)`. Matches vanilla handheld transforms in both FPP and TPP.
2. **Radiant Mana Crystal**:
   * Redesigned `mana_crystal.png` from a flat shape to a faceted celestial gemstone with spectral highlights and sparkles.
3. **49 Skill Icons**:
   * Replaced placeholder dummy PNGs with vibrant, transparent-background 16x16 RPG pixel art.
4. **HUD Overlay**:
   * Added `RenderSystem.enableBlend()` to prevent black/magenta missing texture boxes.
   * Repositioned hotkey badge (top-left) and rank badge (bottom-right) so text never blocks icon art.

---

### 2.6. Mining & Ore Progression
* **Deepslate Mythril Ore**:
  * Spawns exclusively in deep underground caverns (`Y = -64` to `Y = -16`).
  * Requires a **Diamond Pickaxe** (`needs_diamond_tool.json`).
* **Deepslate Dragonite Ore**:
  * Spawns exclusively in deepslate bedrock levels (`Y = -64` to `Y = -32`).
  * Requires a **Mythril Pickaxe** (Mining Level 4, `needs_tool_level_4.json`).

---

## 3. Flaws, Errors Encountered & How They Were Resolved

| # | Flaw / Symptom | Root Cause | Exact Resolution |
| :--- | :--- | :--- | :--- |
| **1** | **Swords & Katanas looked reversed/ugly in hand** | The textures were generated along the wrong diagonal (tip at top-left, handle at bottom-right). Minecraft's `item/handheld` model assumes bottom-left handle. | Extracted vanilla `diamond_sword.png` pixel coordinates and mapped all fantasy metal palettes to the exact vanilla diagonal. |
| **2** | **SS Skills couldn't be cast at Level 100** | In `skill_ranks.json`, SS skills had `"cost_amount": 10060.0` with `"is_percent": true`, demanding 10,060% of the player's mana pool. | Corrected `"cost_amount"` to `60.0%` of mana/stamina across all SS skills. |
| **3** | **Learned skills couldn't be equipped to hotbar** | Using a tome unlocked the skill in `PlayerData.unlockedSkills`, but there was no GUI to equip it into the 4 active slots (`R`, `F`, `C`, `X`). | Created `SkillManagerScreen.java` (`H` key) and `AscendPackets.EQUIP_SKILL_ID` to equip/swap any unlocked skill to hotbar. |
| **4** | **Missing textures (pink/black boxes) in HUD** | Dummy 186-byte placeholder PNGs were missing or rendered without OpenGL alpha blending. Hotkey text also clipped directly over icons. | Rendered 49 custom transparent 16x16 PNGs; added `RenderSystem.enableBlend()`; moved Rank badge to bottom-right corner. |
| **5** | **Dialogue text clipped outside screen box** | In `CharacterCreationScreen.java`, the prologue line listing all 6 races exceeded the 390px card width and clipped through the cyan border. | Shortened line to `§b✦ One Species §7— Choose from 6 unique fantasy races`, keeping generous margins. |
| **6** | **24 Skills did nothing when cast** | `AscendSkills.java` only had `switch` cases for 20 skills; all other 24 skills fell through to `default -> return false`. | Fully implemented all 24 missing skills (`flash_step`, `power_strike`, `block`, `stone_skin`, `blade_tempest`, etc.). |
| **7** | **Mythril spawned on surface at Y=39** | `ore_mythril_placed.json` had a height range reaching positive Y levels with stone replacement. | Restricted height range in worldgen JSON to `[-64, -16]` and deepslate replaceables only. |
| **8** | **Character Creation Step 4/4 overlapping buttons** | 14 starter skills were displayed simultaneously, overflowing down over the "Back" and "Next" buttons. | Restricted Step 4/4 to Rank E starter arts only, arranged in a spacious 2-column layout with 32px clearance. |

---

## 4. Deep-Dive: The Structure Discrepancy Problem

### 4.1. Why AI Concept Art and Minecraft Worldgen Look Different
The user noted that the in-game spawn structure did not match the uploaded reference artwork. This discrepancy arises from fundamental technical differences between 2D conceptual illustrations and procedural voxel world generation:

1. **Voxel Resolution vs. Concept Art Scale**:
   * The AI reference image depicts a massive Greek acropolis: a 12-block tall Goddess statue with flowing robes, a 4-tier bell tower, a curved colonnade, and a pedimented temple.
   * In a 2D digital painting, brushstrokes create smooth curves, curved domes, and thin gold filigree.
   * In Minecraft, everything is built from discrete $1 \times 1 \times 1$ meter cubes. If a structure is too small (e.g. 15 blocks wide), curved pillars become jagged stair steps and statues look like blocky golems.
2. **NBT Structure Template Size & Chunk Limits**:
   * A single Minecraft `.nbt` structure file is best kept under $48 \times 48 \times 48$ blocks to prevent world-generation lag or chunk-loading timeouts.
   * To achieve a 100% faithful representation of a grand sky acropolis (which in concept art is roughly $120 \times 80 \times 120$ blocks), a single monolithic NBT file is insufficient.
3. **Lighting & Shaders**:
   * The reference art showcases global illumination, soft ambient occlusion, god rays through clouds, and glowing blue floor runes.
   * Vanilla Minecraft without shaders uses flat block light levels (0–15). Without Iris/Complementary shaders, glowing blue runes render as light-blue wool or sea lanterns rather than emissive neon blooms.
4. **Classpath Loading vs. Data Pack Scanning**:
   * When creating a brand-new singleplayer world, `StructureTemplateManager.getTemplate()` can fail on tick 0 if data pack indexing has not completed.
   * If `getTemplate()` returned empty, the game fell back to the old hardcoded 7-pillar gold platform.

---

### 4.2. Action Plan to Bridge the Structure Gap 100%

To make in-game structures look identical to the reference artwork, we implement three complementary strategies:

```
┌────────────────────────────────────────────────────────────────────────┐
│                        STRATEGY FOR 1:1 STRUCTURES                     │
├──────────────────────────┬─────────────────────────┬───────────────────┤
│ 1. Direct Classpath NBT  │ 2. Multi-Piece Jigsaw   │ 3. Display Models │
├──────────────────────────┼─────────────────────────┼───────────────────┤
│ Bypass data pack delays  │ Split large structures  │ Use 1.20 Display  │
│ via NbtIo.readCompressed │ into Courtyard, Temple, │ Entities for fine │
│ directly from jar.       │ Colonnade & Bell Tower. │ statues & runes.  │
└──────────────────────────┴─────────────────────────┴───────────────────┘
```

1. **Direct Classpath Loading with Area Clearing (Completed in this build)**:
   * Updated `DivineAwakening.java` to load `temple_of_the_chosen.nbt` directly from the JAR via `DivineAwakening.class.getResourceAsStream(...)` and `NbtIo.readCompressed`.
   * Added an automated clearing loop that replaces any old legacy blocks (Y=210 to Y=245) with AIR before placing the full 38x30x38 NBT structure.
2. **Multi-Part Jigsaw Assembly**:
   * Split massive builds (like the Guild Hall and Goblin Cave Gate) into 3–4 interlocking modules:
     * Module A: Floating Inverted Asteroid Island & Grand Staircase.
     * Module B: Semicircular Goddess Colonnade & Reflecting Pool.
     * Module C: Greco-Roman Altar Sanctuary (Interior Altar Hall).
     * Module D: Celestial Bell Tower.
   * This allows each module to have full architectural detail without exceeding individual NBT limits.
3. **Block Display & Emissive Rune Blocks**:
   * Use Minecraft 1.20 `BlockDisplayEntity` and `ItemDisplayEntity` for the Goddess Statue. This allows scaling and rotation of blocks at non-grid angles, producing smooth drapery, outstretched hands, and delicate gold diadems matching the concept art.

---

## 5. Architectural Codebase Reference

```
E:\games\
├── build.gradle                              # Fabric Loom 1.6.12 configuration
├── ascend-0.1.0-alpha.jar                    # Standalone compiled deliverable (544 KB)
└── src\main\
    ├── java\net\ascend\mod\
    │   ├── AscendMod.java                    # Common mod entry point & item/block registry
    │   ├── AscendClientMod.java              # Client entry point, keybinds (H, K, G, V, R, F, C, X)
    │   ├── client\
    │   │   ├── gui\
    │   │   │   ├── CharacterCreationScreen.java # Prologue dialog & 4-step Awakening flow
    │   │   │   ├── StatusScreen.java            # Stat point allocation & Grimoire link
    │   │   │   ├── SkillUnlockScreen.java       # Scroll & Tome selection GUI
    │   │   │   └── SkillManagerScreen.java      # Active hotbar loadout & technique shuffling
    │   │   └── hud\
    │   │       └── AscendHudRenderer.java       # RPG hotbar overlay, HP/MP bars, CD timers
    │   ├── data\
    │   │   ├── PlayerData.java               # Stats, Mana/Stamina resources, loadout slots
    │   │   └── IPlayerDataAccessor.java      # Duck-type interface for PlayerEntity
    │   ├── item\
    │   │   ├── SkillScrollItem.java          # Opens Scroll Selection Grimoire
    │   │   └── SkillTomeItem.java            # Opens Tome Selection Grimoire (level gated)
    │   ├── mixin\
    │   │   └── PlayerEntityMixin.java        # Combat hooks, i-frames, Demonkin lifesteal
    │   ├── network\
    │   │   ├── AscendPackets.java            # Server packet handlers (Equip, Learn, Cast)
    │   │   └── AscendClientPackets.java      # Client packet senders & GUI triggers
    │   ├── skills\
    │   │   ├── AscendSkills.java             # Execution code for all 44 skills & signatures
    │   │   ├── SkillInfo.java                # Skill record loader from JSON
    │   │   └── SkillRank.java                # Rank enum (E, D, C, B, A, S, SS)
    │   └── story\
    │       └── DivineAwakening.java          # Spawn logic, NBT temple placement, safe descent
    └── resources\
        ├── assets\ascend\
        │   ├── textures\
        │   │   ├── item\                     # 16x16 vanilla-style swords, katanas, mana crystal
        │   │   └── skill\                    # 49 custom transparent RPG skill icons
        │   └── lang\en_us.json               # Localization strings & keybind labels
        └── data\ascend\
            ├── skills\skill_ranks.json       # Database of all 44 skills, costs, cooldowns
            └── structures\                   # NBT structure files (Temple, Guild Hall, Gates)
```

---

## 6. Next Steps & Development Roadmap

1. **Jigsaw Generation for World Dungeons**:
   * Convert `goblin_cave_gate.nbt`, `guild_hall.nbt`, and `ruined_castle.nbt` into Jigsaw structures with terrain-adaptive foundations.
2. **Goddess Statue Model Refinement**:
   * Add a `BlockDisplay` entity preset for the Goddess Statue at the Temple of the Chosen to provide curved marble drapery matching the reference artwork.
3. **Boss AI & Arena Spawning**:
   * Wire the Boss Altar block to trigger multi-phase combat for the Goblin King, Shadow Knight, and Void Lich.
4. **Quest System Expansion**:
   * Connect rank promotion quests (Rank E to SS) directly to dungeon completions and boss defeats.

---

*Report compiled and verified against build `06bade8` on branch `main`.*
