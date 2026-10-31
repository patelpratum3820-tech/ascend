# ASCEND — Anime Isekai Minecraft Mod (Master Plan + Master Prompt)

Working title: **Ascend** (mod id: `ascend`). Rename anytime.

## HOW TO USE THIS FILE
1. Create a new project folder, save this file inside it as `MASTER_PLAN.md`.
2. Open the folder in Antigravity.
3. Paste **PART 2 (MASTER PROMPT)** into the agent as your first message.
4. The agent builds **one phase at a time**. After each phase: run the game, test using the checklist, then tell the agent "Phase X works, continue" (or paste the error).
5. Whenever the agent forgets something, tell it: "Re-read MASTER_PLAN.md".

---

# PART 1 — THE DESIGN (what we are building)

## 1. Vision
Minecraft Java Edition mod. Same sandbox adventure, but with an anime/isekai RPG system: you are reborn into this world, choose a race, class and skills, level up through a status screen (Solo Leveling style), clear ranked dungeons, and fight huge bosses with animated moves and dashes.

Version 1 is **solo only**. Multiplayer comes later.

## 2. Game Start Flow (on first join of a world)
A custom screen appears with 4 steps:
1. **Difficulty**: Easy / Normal / Hard / Nightmare (scales mob health, mob damage, drops, and Essence rewards)
2. **Race**: pick 1
3. **Class**: pick 1
4. **Skills**: pick 2 (only skills allowed for the chosen class)

Then a confirm screen shows the final build. Choice is stored per player and saved with the world. (Later: a rare "Reset Stone" item to re-pick.)

## 3. Races (launch with 3, add more in updates)
Each race = 1 passive perk + 1 weakness.

| Race | Perk | Weakness |
|---|---|---|
| Human | +15% Essence gain, balanced stats | no special resistances |
| Dragonoid | +Strength, fire resistance, small dragon-breath bonus | slower speed, takes extra damage from cold/water effects |
| Slime | no fall damage, bouncy jump boost, regenerates slowly | lower max health and defense |

Future races: Elf (mana bonus), Demon, Beastkin, Undead.

## 4. Classes (5)
Each class has: base stats, a signature move, a weakness, and a list of **allowed skill tags**.

| Class | Style | Weakness | Allowed skill tags |
|---|---|---|---|
| Swordsman | fast melee, combo attacks | no mana pool (uses stamina) | melee, mobility, defense |
| Summoner | summons fight for you | low health | summon, support, magic |
| Mage | ranged spells, staffs | low defense, spells need mana | magic, support, mobility |
| Archer | ranged precision, mobility | weak in close melee | ranged, mobility, support |
| Tank | heavy armor, protects | slow, low damage | defense, melee, support |

Signature moves (animated, big effects):
- Swordsman: Triple Slash
- Summoner: Summoning Portal (opens a portal, minions rush out)
- Mage: Meteor Call
- Archer: Piercing Volley
- Tank: Guardian Slam

## 5. Skills (10 at launch)
Pick 2 at start. Each skill has a tag (see class table), a cooldown, and a mana or stamina cost.

| # | Skill | Tag | Effect |
|---|---|---|---|
| 1 | Shadow Step | mobility | quick dash with brief invincibility, afterimage effect |
| 2 | Flame Slash | melee | fire wave in front of you |
| 3 | Healing Aura | support | heals you and nearby allies/minions over time |
| 4 | Summon Shadow Soldier | summon | summons 1–3 minions (count scales with mana level) |
| 5 | Mana Bolt | magic | fast ranged magic projectile |
| 6 | Arrow Rain | ranged | rain of arrows on a target area |
| 7 | Iron Skin | defense | big damage reduction for a few seconds |
| 8 | Lightning Strike | magic | strikes a targeted enemy, chains to nearby enemies |
| 9 | Berserk | melee | strength and attack speed up, defense down |
| 10 | Soul Bond | summon | minions share damage with you and get stronger |

Build skills as small reusable pieces (dash, projectile, area effect, buff, summon) so new skills are mostly combinations.

## 6. Status System (Solo Leveling style)
Opened with a key (default `K`) and a button in the inventory screen.

- Shows: Name, Race, Class, Level, Rank (E, D, C, B, A, S), skills, and stats.
- **Stats**: Strength, Speed, Vitality (health), Mana, Defense.
- **Essence** = the mod's own XP. Dropped by mobs and dungeon rooms. Spend it (or level-up stat points) to upgrade stats.
- **Vanilla XP is NOT replaced**, so enchanting still works.
- **Mana** bar shown on HUD (for Summoner/Mage), **Stamina** bar (for Swordsman/Tank/Archer).
- Player Rank rises with level; dungeons have ranks too and higher ranks give better loot.

## 7. Combat Feel
- **Dash/Dodge**: key + direction (A/D = side slide, S = backstep, W = forward dash; or double-tap). Short invincibility window (about 0.3s), stamina cost, cooldown, particles, speed lines, sound.
- **Animations**: custom player animations (slashes, skill poses, dash) using the Player Animator library; animations authored in Blockbench.
- **Combos**: Swordsman hit chain (1-2-3), finisher on the third hit.
- **Juice**: camera shake on heavy hits, short slow-motion on boss kills, glowing skill effects, hit particles.
- Be realistic: Minecraft is not a fighting game. Target "cool anime feel", not Devil May Cry.

## 8. Quests, Dungeons, Bosses
**Story**: main quest line (follow it to the final bosses) plus side quests.

**Village side quests**: small tasks (kill pests, deliver items, find a lost pet, protect a farm). Rewards: Essence, items, and **Dungeon Maps** (an item that points to a dungeon location).

**Dungeon structure** (always the same pattern):
- Entrance → **5 rooms** → **Boss room**
- Rooms are built from a pool of ~10 premade room templates (combat room, trap room, puzzle room, treasure room, mini-boss room). The game picks 5 at random, so each run differs.
- Dungeon has a **Rank (E to S)**; higher rank = harder mobs and better rewards.
- Boss drops: unique material + Essence + a chance to **extract** the boss as a Summoner minion (Summoner only).

**Bosses**
| Boss | Tier | Notes |
|---|---|---|
| Goblin King | mini | first dungeon, summons goblin packs |
| Shadow Knight | mini | fast, teleports, dodges |
| Crystal Golem | mini | armored, weak point glows |
| Abyss Dragon | FINAL 1 (Ender Dragon level) | flying, breath attacks, phases |
| Void Lich / Demon Lord | FINAL 2 (Wither level) | summons, spells, 3 phases |

Boss health and drops scale with difficulty.

## 9. Villagers and NPCs
New villager types with dialogue and quests: Guild Master (gives rank quests), Blacksmith (upgrades gear with new materials), Mage Merchant (staffs, mana potions), Quest Giver (side quests), Wandering Hero (rare, gives skill scrolls).
- Reputation: helping villages unlocks better trades.
- Villagers react to race (example: villagers fear Dragonoids at first, Humans get small discounts).

## 10. New Content

### Ores and Materials
| Name | Where | Used for |
|---|---|---|
| Mythril Ore → Mythril Ingot | Overworld, deep underground | Tier 1 armor, tools, weapons |
| Mana Crystal Ore → Mana Crystal | Overworld caves | Staffs, mana potions, skill upgrades |
| Dragonite Ore → Dragonite Ingot | Nether | Tier 2 armor, tools, weapons |
| Voidsteel (crafted from End materials + boss drop) | End / final boss | Tier 3 endgame gear |
| Shadow Essence | mob drop | crafting, minion upgrades |
| Dragon Scale | boss/mob drop | Dragonoid-style gear |

### New Woods
- **Mystic Wood**: glowing blue-purple leaves, grows in a new Enchanted Forest biome. Used for staffs and magic blocks.
- **Emberwood**: dark red wood from a volcanic/ashen biome. Used for fire gear.
Each wood: log, stripped log, planks, stairs, slab, fence, door, sapling.

### Armor Sets (with 4-piece set bonuses)
- Mythril Armor: +defense, +mana regen
- Dragonite Armor: +strength, fire immunity
- Voidsteel Armor: +all stats, small dash bonus
- Class sets (later): Shadow Assassin set (dash cooldown), Summoner Robes (+minion count)

### Tools and Weapons (per tier: Mythril, Dragonite, Voidsteel)
Sword, pickaxe, axe, shovel, hoe, plus special weapons:
- Katana (fast), Greatsword (heavy), Daggers (dash + crit), Longbow
- **Magic Staffs** (use mana, each has an active effect):
  - Apprentice Staff (Mystic Wood + Mana Crystal): Mana Bolt
  - Flame Staff: fireball
  - Storm Staff: lightning
  - Summoner's Staff: boosts minion count and speed
  - Void Staff (endgame): gravity pull + dark blast

### New Mobs
**Passive (friendly, give small rewards):**
- Spirit Fox: follows you, glows, drops Mana Dust
- Mana Deer: grazes in Enchanted Forest, drops Mana Crystal shards
- Slime Pup: can be tamed as a pet
- Moon Moth: ambient glowing creature, light source at night

**Aggressive:**
- Goblin (comes in packs), Orc Warrior (strong melee), Shadow Wraith (fast, ghostly), Skeleton Knight, Dark Mage (casts bolts), Fire Drake (small flying dragon), Dungeon Golem (slow, tanky)

Every mob has: health, damage, speed, spawn rules, drops, and a difficulty multiplier.

---

# PART 2 — MASTER PROMPT (paste this into Antigravity)

```
You are my senior Minecraft mod developer. We are building a big anime/isekai
RPG mod called "Ascend" (mod id: ascend) for Minecraft JAVA EDITION on PC.

I am a self-taught "vibe coder". I do NOT know 3D modeling and I am not an
expert in Java. Explain things simply, tell me exactly how to run and test,
and do the work for me. Everything must be FREE tools only.

FIRST: read MASTER_PLAN.md in this folder. It contains the full design
(race, class, skills, status system, dash, dungeons, bosses, ores, woods,
armor, tools, staffs, mobs). Treat it as the source of truth.

TECH RULES
- Use Fabric mod loader with the Fabric Loom Gradle setup and Java.
- Use the latest stable Minecraft Java version that Fabric and the libraries
  below support. Check the official Fabric docs for current versions and
  mappings before writing code. DO NOT guess APIs from memory; if unsure,
  look it up or tell me.
- Libraries: GeckoLib (3D mob/boss animation), Player Animator (player
  animations). Add only when the phase needs them.
- Use data-driven design where possible: JSON for recipes, loot tables,
  tags, and a datapack-style approach for structures (NBT templates).
- Player data (race, class, skills, stats, level, Essence, rank) must be
  saved per player and synced to the client properly (use Fabric's
  attachment/networking APIs). Never trust the client for stats.
- Put ALL balance numbers (damage, costs, cooldowns, multipliers) in
  config/JSON files so I can tweak them without touching code.
- Use placeholder textures (simple colored squares) for everything first so
  the game always runs. I will replace art later. Keep a file
  ASSETS_TODO.md listing every texture/model/sound still needed and its
  exact file name and size.
- Code must be modular: one system per package (stats, race, classes,
  skills, combat, items, mobs, dungeons, quests, ui). New skills/classes/
  races must be addable by adding a small class + JSON, not by editing many
  files.
- Initialize git and commit after every working step.

HOW TO WORK
- Work in the PHASES below, ONE phase at a time. Do not start the next phase
  until I say "continue".
- At the end of each phase: (1) make sure the project builds with no errors,
  (2) tell me the exact command to run the game (runClient), (3) give me a
  short test checklist, (4) list what is still placeholder.
- If something is too big or risky, say so honestly and propose a smaller
  version. Do not silently skip features and do not fake things.
- If I report an error, fix the root cause, don't just hide it.
- Ask me a question only when a decision truly changes the design.

PHASES
Phase 0 - Setup: Fabric project, mod id, Gradle builds, runClient works,
  empty mod loads, git init, create ASSETS_TODO.md.
Phase 1 - Player data + Status screen: saved/synced player data (race,
  class, level, Essence, rank, stats Strength/Speed/Vitality/Mana/Defense).
  Status screen opened with K and a button in inventory. Essence is
  separate from vanilla XP. Stat upgrade buttons spend Essence/points.
Phase 2 - Character creation flow: on first join show screens for
  Difficulty -> Race -> Class -> 2 Skills, with the class-skill tag
  restrictions from the plan. Confirm screen, then save. Implement the 3
  races and 5 classes with perks and weaknesses (values from config).
Phase 3 - Mana/Stamina + Skills framework + first 3 skills: resource bars on
  HUD, skill bar/hotkeys, cooldowns, skill registry. Implement Shadow Step,
  Mana Bolt, Summon Shadow Soldier (minions follow and fight for me).
Phase 4 - Dash/Dodge + combat feel: directional dash with i-frames, stamina
  cost, cooldown, particles and sound. Then Player Animator animations for
  dash and Swordsman combo chain. Camera shake on heavy hits.
Phase 5 - Remaining 7 skills + class signature moves.
Phase 6 - Content: ores (Mythril, Mana Crystal, Dragonite), world
  generation, Mystic Wood and Emberwood with full block sets, materials,
  armor sets with set bonuses, tools, weapons, and the magic staffs.
Phase 7 - Mobs: passive and aggressive mobs from the plan using GeckoLib
  (placeholder models first), spawn rules, drops, difficulty scaling.
Phase 8 - Villagers, quests, dungeons: new villager types with dialogue,
  side quest system, Dungeon Maps, dungeon generator (entrance + 5 random
  rooms from templates + boss room), dungeon ranks E-S.
Phase 9 - Bosses: Goblin King, Shadow Knight, Crystal Golem (mini), then the
  two final bosses with phases and boss bars. Boss extraction for Summoner.
Phase 10 - Polish and balance: sounds, effects, tooltips, config cleanup,
  bug fixing, README, and prepare a release build.

OUT OF SCOPE FOR NOW: multiplayer scaling and co-op quests (design the data
so it can be added later), extra races/classes beyond the plan.

START NOW: read MASTER_PLAN.md, confirm in 5 lines what you understood, then
do Phase 0 only.
```

---

# PART 3 — ART PIPELINE (using AI-generated images, no 3D skills)

- **Where AI images work great**: class selection screens, boss portraits, loading screens, mod icon, quest/guild UI art, wallpapers.
- **Textures** (blocks, items, mobs): ask your image generator for **"pixel art, 16x16 Minecraft style, flat colors, no shading blur"** for items and blocks. For bosses use 64x64 or 128x128 pixel art skins.
- **Models**: free tool **Blockbench**. Start by reskinning vanilla-shaped mobs (zombie/skeleton/golem shapes) with your textures. Ask the AI agent to generate the box-model files, then open them in Blockbench to check.
- Use `ASSETS_TODO.md` as your art shopping list: generate one asset, drop it in the exact file path, run the game, repeat.
- Realistic: the mod fully works with placeholder art. Art is the last thing to polish.

---

# PART 4 — BALANCE STARTER NUMBERS (tweak in config)

| Stat | Starting value | Per level |
|---|---|---|
| Health (Vitality) | 20 | +1 per Vitality point |
| Strength | 5 | +1 damage per 2 points |
| Speed | 5 | +0.5% move speed per point |
| Mana | 50 | +5 per Mana point |
| Defense | 0 | +1% reduction per point (cap 60%) |

Difficulty multipliers (mob health / damage / Essence): Easy 0.75/0.75/0.75, Normal 1/1/1, Hard 1.5/1.25/1.5, Nightmare 2.5/1.75/2.5

Dash: 0.3s invincibility, 3s cooldown, 20% stamina cost.

---

# PART 5 — REALISTIC EXPECTATIONS
- This is a modpack-sized project. Phases 0–5 give you a **playable, fun core** (race, class, skills, dash). Get that working and fun before the content phases.
- Expect lots of debugging. That is normal for every mod.
- If it gets too big, cut in this order: extra woods → class armor sets → some mobs → second final boss. Never cut the status system, creation flow, skills, or dash.
- Testing tip: make a creative-mode test world with commands to give yourself Essence and items.
- Public release (later): CurseForge and Modrinth are free. Check each Minecraft/Fabric version you target.
