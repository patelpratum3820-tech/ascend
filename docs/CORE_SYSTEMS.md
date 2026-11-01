# ASCEND — CORE RPG SYSTEMS SPECIFICATION (STAGE 1)

**Mod ID:** `ascend`  
**Minecraft Version:** 1.20.1 Fabric (Yarn Mappings)  
**Status:** Stage 1 Core RPG Engine Finalized  

---

## 1. System Architecture & Authoritative State

ASCEND enforces a **server-authoritative** architecture for all RPG attributes, skills, quests, and dungeon progression. The client never dictates stat changes, essence amounts, or reward claims; all modifications are verified and processed on the logical server, then mirrored to the client via `AscendPackets.SYNC_PLAYER_DATA_ID`.

### Data Persistence
- **Interface:** `IPlayerDataAccessor` attached to `PlayerEntity` via `PlayerEntityMixin`.
- **NBT Tag:** `AscendRPG` compound tag saved directly in player NBT (`writeCustomDataToNbt` / `readCustomDataFromNbt`).
- **Persistence Guarantees:** Survived across player deaths (via `ServerPlayerCopyCallback`), server restarts, disconnects, and world reloads.

---

## 2. Character Heritage & Dual-Class System

### Starting Races (6 Base Heritages)
1. **Human:**
   - Perk: *Unyielding Resolve* (+15% Essence gain from all combat and dungeon sources).
   - Stats: Perfectly balanced base attributes; no inherent vulnerabilities.
   - Evolution Candidates: *Awakened Human* $\rightarrow$ *Heroic Human* $\rightarrow$ *Saint*.
2. **Elf:**
   - Perk: *High Attunement* (+40 Max Mana, +10% Movement Speed, 1.5x Mana Regen).
   - Penalty: *Frail Body* (-2 Max Health).
   - Evolution Candidates: *High Elf* $\rightarrow$ *Ancient Elf* $\rightarrow$ *Elder Elf*.
3. **Beastkin:**
   - Perk: *Primal Instincts* (+3 Strength, +20 Max Stamina, permanent Night Vision, +10% Dash Speed).
   - Penalty: *Mana Void* (-15 Max Mana).
   - Evolution Candidates: *Advanced Beastkin* $\rightarrow$ *Feral Monarch*.
4. **Demonkin:**
   - Perk: *Abyssal Flame* (+4 Strength, +20 Max Mana, 100% Fire/Lava Immunity, 5% Melee Lifesteal).
   - Penalty: *Holy Frailty* (+50% Magic/Lightning damage taken).
   - Evolution Candidates: *High Demon* $\rightarrow$ *Archdemon*.
5. **Dragonoid:**
   - Perk: *Dragon Heritage* (+3 Strength, 100% Fire/Lava Immunity, +25% Fire Skill Damage).
   - Penalty: *Heavy Scales* (-5% Movement Speed, +50% Freeze/Drowning damage taken).
   - Evolution Candidates: *Elder Dragonkin* $\rightarrow$ *Dragon King*.
6. **Slime:**
   - Perk: *Amorphous Body* (75% Fall Damage Reduction, passive tick health regeneration).
   - Penalty: *Viscous Form* (-4 Max Health, -2 Defense).
   - Evolution Candidates: *Advanced Slime* $\rightarrow$ *Crystal Slime*.

### Dual-Class Combination Matrix
The player chooses **two** starting classes (Primary and Secondary) at creation from:
*Swordsman*, *Tank*, *Mage*, *Archer*, *Summoner*.

| Primary Class | Secondary Class | Canonical Title | Primary Focus |
|---|---|---|---|
| Swordsman | Mage | **Magic Swordsman** | Spellblade melee & element imbues |
| Swordsman | Tank | **Paladin Vanguard** | Heavy armor frontline warrior |
| Swordsman | Archer | **Blade Ranger** | Hybrid agile martial skirmisher |
| Swordsman | Summoner | **Shadow Blade** | Conjured blade phantom striker |
| Mage | Tank | **Magic Guardian** | Arcane ward protection barrier |
| Archer | Mage | **Spell Archer** | Long-range elemental infused arrows |
| Archer | Summoner | **Beast Commander** | Ranged artillery with companion horde |
| Tank | Summoner | **Golem Master** | Immovable bastion with conjured guards |
| Mage | Summoner | **Nether Conjurer** | Pure arcane destruction & summon swarms |

---

## 3. Attribute Formulas & Derived Combat Math

The character possesses 5 primary core attributes:
`Strength`, `Agility`, `Vitality`, `Intelligence`, `Dexterity`.

### Attribute Formulas
$$\begin{aligned}
\text{Max Health (HP)} &= 20.0 + (\text{Vitality} - 5) \times 2.0 \\
\text{Max Mana (MP)} &= 50.0 + (\text{Intelligence} - 5) \times 10.0 \\
\text{Movement Speed} &= 0.10 \times \left(1.0 + (\text{Agility} - 5) \times 0.02 + \text{RacialSpeed}\right) \\
\text{Attack Damage Bonus} &= \frac{\text{Strength} - 5}{2.0} \times 0.75 \\
\text{Damage Reduction (\%)} &= \frac{\text{Dexterity} \times 0.015}{1.0 + (\text{Dexterity} \times 0.015)} \times 100\%
\end{aligned}$$

### Leveling Curve
$$\text{XP Required for Level } L = 100 \times L^{1.5}$$

### Stat Upgrade Cost Formula (Essence)
$$\text{Essence Cost} = 50 \times 1.25^{(\text{CurrentStat} - 5)}$$

---

## 4. World Fate & Difficulty Scaling

Selected at character creation and persisted in `PlayerData`:

| Difficulty | Incoming Damage Multiplier | Stamina Drain Multiplier | Description |
|---|---|---|---|
| **Easy** | $0.75\times$ | $0.85\times$ | For casual story exploration |
| **Normal** | $1.00\times$ | $1.00\times$ | Standard balanced RPG experience |
| **Hard** | $1.35\times$ | $1.20\times$ | Punishing combat; tactical resource usage required |
| **Nightmare** | $1.75\times$ | $1.50\times$ | Unforgiving; lethal boss attacks and extreme endurance |

---

## 5. Story Progression & Future Moral Alignment (Stage 2 Hooks)

### Progression Milestones
- `prologueCompleted`: Divine Awakening in the sky platform completed.
- `firstDungeonCleared`: Defeated first subterranean threshold.
- `firstBossDefeated`: Boss at Great Lake defeated.
- `moralPath`:
  - `NONE`: Neutral.
  - `ANGEL`: *Path of the Seraph* (unlocked upon defeating Lake Sovereign).
  - `DEMON`: *Path of the Demon King* (unlocked upon defeating Lake Sovereign).
- `netherSovereignDefeated`: Nether citadel cleared.
- `chosenOneAchieved`: Ascension to the God's Realm complete.
