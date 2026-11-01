# ASCEND: REALM OF THE CHOSEN — STAGE 1 INITIAL AUDIT
**Date:** November 1, 2026  
**Target:** Minecraft 1.20.1 (Fabric / Yarn mappings / Java 17)  
**Mod ID:** `ascend`  
**Phase:** Stage 1 — Core RPG Foundation & UI (Phase 1 Audit)  
**Document:** `docs/STAGE_1_INITIAL_AUDIT.md`  

---

## 1. Executive Summary & Audit Scope

This document provides a comprehensive technical audit of the ASCEND mod repository prior to embarking on Stage 1 development. 

In accordance with the **Master Development Prompt**:
* Stage 1 focuses strictly on core RPG progression, character profiles, multi-class systems, statistics, Grimoire skill management, unified UI screens, quest and dungeon frameworks, artifact progression models, story flags, and server-authoritative multiplayer synchronization.
* Stage 2 content (custom world generation, dimensions, cities, NPC entities, original mobs, bosses, and 3D modeling) is deferred until Stage 1 core systems and interfaces are rock-solid and verified in-game.

Every file in the codebase (`net.ascend.mod.*`, resources, configurations, and worldgen assets) has been inspected. Below is the factual, code-verified audit of the current project state.

---

## 2. Codebase Architecture & Technical Stack

* **Platform:** Fabric Loader `0.15.11` on Minecraft `1.20.1`
* **Mappings:** Yarn `1.20.1+build.10`
* **Loom Version:** `1.6.12`
* **Java Target:** Java 17 (OpenJDK 22 host environment)
* **Build Health:** Clean compilation verified via `./gradlew compileJava` (0 errors, 0 warnings).
* **Package Structure:**
  * `net.ascend.mod` — Mod initializers (`AscendMod`, `AscendClientMod`)
  * `net.ascend.mod.data` — Player persistent data (`PlayerData`, `IPlayerDataAccessor`)
  * `net.ascend.mod.skills` — Skill definitions, JSON loader, execution engine (`AscendSkills`, `SkillInfo`, `SkillRank`)
  * `net.ascend.mod.client.gui` — User interfaces (`CharacterCreationScreen`, `StatusScreen`, `SkillManagerScreen`, `SkillUnlockScreen`)
  * `net.ascend.mod.client.hud` — Real-time in-game overlay (`AscendHudRenderer`)
  * `net.ascend.mod.client.render` — Mob & boss rendering registries
  * `net.ascend.mod.network` — Server & client networking packets (`AscendPackets`, `AscendClientPackets`)
  * `net.ascend.mod.story` — Celestial prologue and world introduction (`DivineAwakening`)
  * `net.ascend.mod.config` — Configurable multipliers, costs, and thresholds (`AscendConfig`)
  * `net.ascend.mod.quest` — Legacy quest tracker (`QuestManager`)
  * `net.ascend.mod.dungeon` — Procedural dungeon generation prototype (`DungeonManager`)
  * `net.ascend.mod.item` / `net.ascend.mod.block` / `net.ascend.mod.entity` — Supporting RPG items, blocks, and test mobs
  * `net.ascend.mod.mixin` — Bytecode hooks (`PlayerEntityMixin`, `InventoryScreenMixin`)

---

## 3. Existing Implemented Features (Verified in Code)

The following systems are already functional and must be preserved:

1. **44 Active RPG Skills Executable (`AscendSkills.java`):**
   * All 44 skills from `skill_ranks.json` across Ranks E, D, C, B, A, S, and SS are implemented with sound effects, particle trails, cooldowns, and combat mechanics.
   * SS Rank ultimate costs (`judgment_meteor_shower`, `sovereigns_domain`, `heavens_edge`) are balanced at 60% mana/stamina with post-cast resource lockout.
2. **Skill Unlock & Mastery Pipeline:**
   * Skill Scrolls (Ranks E, D, C) and Ancient Tomes (Ranks B, A, S, SS) open `SkillUnlockScreen`, decrement upon engraving into the soul, and permanently register in `PlayerData.unlockedSkills`.
3. **Grimoire Hotbar Management (`SkillManagerScreen.java`):**
   * Opened via hotkey `H` or through `StatusScreen` (`K`).
   * Supports hotbar slot selection (`R`, `F`, `C`, `X`), rank tab filtering (`ALL`, `E`..`SS`), and equipping, swapping, or unequipping unlocked skills.
4. **Active Combat HUD (`AscendHudRenderer.java`):**
   * Renders stamina bar (green), mana bar (cyan), directional dash status/cooldown, player hunter rank badge, and 5 action slots (`R`, `F`, `C`, `X`, and signature `V`) with radial cooldown sweeps and 16x16 pixel-art icons.
5. **Directional Dash with i-Frames:**
   * Bound to `Left Alt`, calculated server-side based on movement vector input with invulnerability ticks and stamina deduction.
6. **Six Fantasy Starting Species:**
   * Human (+15% Essence bonus)
   * Elf (+40 MP, -2 HP, 1.5x passive mana regen, +speed)
   * Beastkin (Permanent night vision, +3 STR, +20 STA, +dash speed, -15 MP)
   * Demonkin (100% Fire/Lava immunity, +4 STR, +20 MP, 50% holy/magic vulnerability)
   * Dragonoid (100% Fire/Lava immunity, +3 STR, -5% speed, 50% cold/water vulnerability)
   * Slime (75% fall damage reduction, passive regeneration, -4 HP, -2 DEF)
7. **The Divine Awakening Prologue (`DivineAwakening.java`):**
   * Classpath NBT streaming for `temple_of_the_chosen.nbt` at `(0, 220, 0)` with automated cleanup of legacy blocks.
   * Teleports player to the sky temple with Resistance and Slow Falling, opens `CharacterCreationScreen`, and descends safely to Overworld surface upon completion with starter gear.
8. **Multiplayer Persistence & Synchronization:**
   * Server-authoritative data in `PlayerData.java`.
   * Stored in NBT under `AscendRPG` tag via `PlayerEntityMixin`.
   * Preserved across respawn (`ServerPlayerEvents.COPY_FROM`) and dimension transitions (`AFTER_PLAYER_CHANGE_WORLD`).
   * Network packets for stat upgrading, skill learning, skill casting, and full profile sync.

---

## 4. Partially Implemented Features

These systems exist in basic form but require expansion to fulfill the Stage 1 specification:

1. **Class System (Single Class vs. Dual Class Combination):**
   * *Current:* `PlayerData` only stores a single `playerClass` (`String playerClass = "Swordsman"`). `CharacterCreationScreen` only allows picking one of 5 classes (Swordsman, Tank, Mage, Archer, Summoner).
   * *Stage 1 Requirement:* The player must be able to choose **ANY TWO starting classes** (e.g. Swordsman + Mage = Magic Swordsman; Tank + Mage = Magic Guardian; Archer + Summoner = Beast Commander; etc.).
2. **Character Creation UI (`CharacterCreationScreen.java`):**
   * *Current:* 5 steps (Prologue, Race, 1 Class, Difficulty, Skills, Confirm).
   * *Stage 1 Requirement:* Needs Character Name input field, Dual Class selection with combo affinity preview, and Stat Allocation preference setting (Manual vs. Auto presets).
3. **Character Progression & Statistics:**
   * *Current:* Base attributes are Strength, Speed, Vitality, Mana, Defense. Stat points are awarded per level and spent manually via `+` buttons on `StatusScreen` or bought with Essence.
   * *Stage 1 Requirement:* Add automatic stat allocation presets (Balanced, Strength-focused, Intelligence-focused, Agility-focused), decouple progression formulas from UI code, and expand attribute representation (Strength, Vitality, Agility, Intelligence, Dexterity).
4. **Quest Framework (`QuestManager.java`):**
   * *Current:* A static array of 5 quests (`QUESTS[questIndex]`) indexed simply by `level / 5`. No persistent player quest log in NBT, no branch states, and no Quest Journal UI screen.
   * *Stage 1 Requirement:* Full quest data model in `PlayerData` (active, completed, objectives, rewards) and an in-game Quest Journal UI screen.
5. **Dungeon and Rank Progression (`DungeonManager.java`):**
   * *Current:* Procedural block-generation logic based on distance from spawn (`rankEDistance`..`rankSDistance`).
   * *Stage 1 Requirement:* Player-centric dungeon record in `PlayerData` (unlocked ranks, completion history, records, required keys/artifacts) and a dedicated Dungeon & Rank UI screen.
6. **RPG Inventory & Equipment (`InventoryScreenMixin.java`):**
   * *Current:* A simple "✦ Status" button placed next to the crafting recipe book.
   * *Stage 1 Requirement:* An integrated RPG equipment view supporting accessories (rings, amulets, talismans) and artifact slots alongside vanilla armor without breaking vanilla inventory compatibility.

---

## 5. Missing Features (To Be Implemented in Stage 1)

The following systems are currently absent from the codebase and will be developed during Stage 1:

1. **Full Character Profile Screen (`CharacterProfileScreen`):**
   * Visual overview of the Ascender: name, species, dual class combination title, character level, total essence, hunter rank, active titles, attribute breakdown, and equipped loadout.
2. **Race Evolution Data Framework:**
   * Extensible data model in `PlayerData` supporting future evolution tiers (e.g., Human -> Awakened Human -> Heroic Human; Elf -> High Elf; Dragonoid -> Elder Dragonkin; Slime -> Crystal Slime) without hardcoding world bosses yet.
3. **Artifact Progression Data Model & Interface:**
   * Structured progression model for major story artifacts (Artifact ID, name, lore, origin dungeon, rank, acquisition state, story flag) and an Artifact Progression UI interface.
4. **Story State & Angel/Demon Progression Framework:**
   * Persistent story progression flags in `PlayerData` (prologue completed, first dungeon cleared, first boss defeated, Angel/Demon moral path decision, Sovereign defeated, God's Realm unlocked, Chosen One title earned).
5. **Unified Fantasy RPG UI Component Framework:**
   * Common styling engine (dark charcoal `#0A0E1A`, gold `#FFD700`, cyan `#00D2FF`, consistent cards, header banners, tab bars, scrollable lists, tooltips) to ensure visual coherence across all screens.

---

## 6. Known Bugs & Architectural Debt

1. **Class Selection Limitation:**
   * Single string field `playerClass` in `PlayerData` prevents hybrid class combinations and hybrid skill loadouts.
2. **Hardcoded Quest Indexing:**
   * `QuestManager.displayQuestProgress()` uses `Math.min(data.getLevel() / 5, QUESTS.length - 1)`, meaning all players at the same level share the same synthetic quest without tracking actual kills or objective flags.
3. **UI Layout Inconsistencies:**
   * `StatusScreen`, `SkillManagerScreen`, `SkillUnlockScreen`, and `CharacterCreationScreen` each implement custom border/card drawing coordinates rather than sharing a unified UI widget library.
4. **Missing Secondary Class Starting Gear:**
   * `DivineAwakening.completeAwakening()` only grants weapons for a single class; dual classes require a combined starting equipment package.

---

## 7. Systems to Strictly Preserve

Throughout Stage 1 refactoring and development, the following MUST NOT be broken:

1. **All 44 skills and their particle/sound/combat execution in `AscendSkills.java`.**
2. **The 4 active hotbar keys (`R`, `F`, `C`, `X`) and signature key (`V`).**
3. **Existing 6 fantasy species and their combat passives/immunities.**
4. **Temple of the Chosen sky spawn and celestial prologue sequence in `DivineAwakening.java`.**
5. **Existing deep subterranean ore progression (Deepslate Mythril at Y < -16 requiring Diamond Pickaxe).**
6. **Server-authoritative networking architecture preventing client-side stat injection.**

---

## 8. Recommended Implementation Order (Phases 2 through 6)

Based on actual codebase dependencies, the recommended roadmap is:

### Phase 2: Core Character Data & Progression Engine
* Update `PlayerData.java`:
  * Add `characterName`.
  * Support Dual Classes: `primaryClass`, `secondaryClass`, and hybrid title generator (e.g., "Magic Swordsman").
  * Expand attributes to standard RPG schema: Strength, Vitality, Agility, Intelligence, Dexterity (with backward compatibility for existing saves).
  * Add automatic stat allocation mode and presets (`Manual`, `Balanced`, `StrengthFocused`, `IntFocused`, `AgiFocused`).
  * Add data models for Quest Progress (active/completed quest IDs, counters).
  * Add data models for Dungeon Progress (completed ranks, best times).
  * Add data models for Artifacts (list of unlocked artifact records).
  * Add data models for Story Flags (prologue, first boss, alignment path, etc.).
  * Add data models for Titles and Evolutions.
* Update NBT read/write and PacketByteBuf serialization.
* Update networking sync packets.

### Phase 3: Unified UI Component Framework & Core Screens
* Create reusable UI components in `net.ascend.mod.client.gui.components` (RPG Card panel, Tab Bar, Gold/Cyan headers, Stat Row with allocation buttons, Skill Cards).
* Overhaul `CharacterCreationScreen.java`:
  * Step 1: Prologue text & Character Name input.
  * Step 2: Race selection (with preview of passives & future evolution hints).
  * Step 3: Dual Class selection (choose any 2 classes; live preview of hybrid title and combat affinities).
  * Step 4: World Fate / Difficulty & Stat Allocation preference (Manual vs. Auto preset).
  * Step 5: Starter Arts selection (filtered to Rank E matching both selected classes).
  * Step 6: Confirmation card & awakening.
* Overhaul `StatusScreen.java` into a tabbed/expanded **Character Profile & Statistics Screen**:
  * Tab 1: Profile Overview (identity, race, dual classes, level, title, dungeon rank).
  * Tab 2: Detailed Attributes (Strength, Vitality, Agility, Intelligence, Dexterity, manual `+` points, auto-allocation mode switcher).

### Phase 4: RPG Grimoire & Interface Suite
* Enhance `SkillManagerScreen.java` (The Ascender's Grimoire):
  * Clean category navigation (All, Melee, Magic, Ranged, Defense, Summon, Ultimates).
  * Full skill detail card inspection (mana cost, cooldown, description, unlock requirement).
  * Hotbar slot assignment (`R`, `F`, `C`, `X`, `V`).
* Create `QuestJournalScreen.java`:
  * Main Story, Guild Bounties, Daily Quests, Completed Quests.
  * Interactive tracking and reward claiming.
* Create `DungeonRankScreen.java`:
  * Visual dungeon rank ladder (E -> D -> C -> B -> A -> S).
  * Dungeon history, clear records, and required story artifacts.
* Create `ArtifactProgressionScreen.java`:
  * Story artifact showcase (Great Lake Relic, Forest Heart, Dragon Eye, Sovereign Core, etc.) with schematic placeholders.
* Enhance `InventoryScreenMixin.java`:
  * Provide RPG equipment view (Accessories, Talismans, Artifacts) cleanly integrated with the vanilla survival inventory.

### Phase 5: Story Progression & Difficulty Integration
* Enhance `DivineAwakening.java`:
  * Support Dual Class starter kits (e.g. Swordsman + Mage receives sword and novice wand).
  * Set initial story state flags.
* Connect difficulty scaling to damage and essence formulas in `AscendConfig` / `PlayerData`.
* Prepare Angel/Demon moral branch data structure and future evolution hooks.

### Phase 6: Verification, Multiplayer Sync & Documentation
* In-game verification across single-player and dedicated multiplayer server.
* Test save, reload, death/respawn, and dimension change data persistence.
* Document completed implementation in `docs/STAGE_1_IMPLEMENTATION_LOG.md` and related docs.
