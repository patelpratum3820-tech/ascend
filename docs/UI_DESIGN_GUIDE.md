# ASCEND: UI/UX DESIGN SYSTEM GUIDE

**Document:** `docs/UI_DESIGN_GUIDE.md`  
**Target Platform:** Minecraft 1.20.1 (Fabric / Java 17)  
**Package:** `net.ascend.mod.client.gui.components`  

---

## 1. Design Philosophy: The Anime / Fantasy RPG Aesthetic

The ASCEND visual language combines the sleek, dark cyberpunk energy of *Solo Leveling* hunters with high-fantasy celestial gold accents. 

### Core Principles
1. **Dark, Atmospheric Backdrops:** Interfaces use deep translucent charcoal (`0xF0080D1A` / `0xEE090E1B`), keeping the game world partially visible without sacrificing readability.
2. **Subtle Neon & Gold Trims:** Primary borders feature cyan energy lines (`0xFF00D2FF`) paired with celestial gold headers (`0xFFFFD700`).
3. **No Visual Clutter:** Clean typography, generous internal padding (at least 8–16px from borders), and compact stat rows prevent elements from colliding or running offscreen.
4. **Standardized Reusability:** Every screen uses the shared `RpgTheme` component engine rather than hardcoded coordinate offsets.

---

## 2. Color Palette & Typography Tokens

| Token | Hex Value | Usage |
| :--- | :--- | :--- |
| `BG_DARK` | `0xF0080D1A` | Main card background (charcoal slate) |
| `BG_PANEL` | `0xCC0D162B` | Inner grouping panels / item boxes |
| `BG_SLOT` | `0xDD111B33` | Hotbar & skill slot backgrounds |
| `BORDER_CYAN` | `0xFF00D2FF` | Active borders, interactive highlights, MP bars |
| `BORDER_GOLD` | `0xFFFFD700` | Header trims, ultimate/signature moves, rank S badges |
| `BORDER_MUTED`| `0x55005577` | Inner double-border glow |
| `TEXT_WHITE` | `0xFFFFFFFF` | Primary body text |
| `TEXT_GOLD` | `0xFFFFD700` | Section titles, hunter rank labels |
| `TEXT_CYAN` | `0xFF00E5FF` | Dialogue, magic arts, mana numbers |
| `TEXT_GREEN` | `0xFF2ECC71` | Stamina bar, verified status `[✓]`, stat points |
| `TEXT_RED` | `0xFFE74C3C` | Cooldown timers, HP indicators, warning alerts |
| `TEXT_MUTED` | `0xFFAAAAAA` | Secondary descriptions, lore, requirements |

---

## 3. UI Component Library

### 3.1 `RpgTheme.java`
* `drawCard(context, x, y, width, height, goldAccent)`: Standardized dark card with outer neon border and inner inset line.
* `drawHeader(context, tr, title, x, y, width, goldAccent)`: Centered bold header with an aesthetic gradient line underneath.
* `drawProgressBar(context, x, y, width, height, percent, fillColor, borderColor)`: Rounded/beveled progression bar with smooth percentage fills.
* `drawBadge(context, tr, text, x, y, color)`: Compact tag badge (e.g. `[Rank E]`, `[E]`, `[Equipped]`).

### 3.2 `RpgTabBar.java`
* Multi-tab navigation system creating equal-width buttons across the top of cards.
* Selected tabs feature a gold arrow indicator (`§6▶ §lOverview`), while inactive tabs remain muted (`§7Attributes`).

---

## 4. Implemented Screens

### 4.1 Character Creation Screen (`CharacterCreationScreen.java`)
* **Step 0:** Divine Awakening Prologue with editable **Character Name input field**.
* **Step 1:** Heritage selection (Human, Elf, Beastkin, Demonkin, Dragonoid, Slime) with preview of passives and evolution tiers.
* **Step 2:** Dual-Class Combat Discipline (Swordsman, Tank, Mage, Archer, Summoner). Live **Hybrid Title preview banner** (e.g. *Magic Swordsman*, *Paladin Vanguard*).
* **Step 3:** World Fate (Easy, Normal, Hard, Nightmare) + Stat Allocation Preference (Manual, Balanced, Strength, Intelligence, Agility).
* **Step 4:** Starter Combat Arts (filtered to Rank E matching either selected class).
* **Step 5:** Final Ascension Pact summary and descent trigger.

### 4.2 Character Profile Screen (`CharacterProfileScreen.java`)
* **Tab 1: Overview:** 3D entity avatar, identity, active title, race & evolution, dual-class discipline, level, XP progress bar, rank, essence, and equipped arts.
* **Tab 2: Attributes:** Strength, Agility, Vitality, Intelligence, Dexterity breakdown with manual point allocation (`+1`) and allocation mode switcher.
* **Tab 3: Heritage:** In-depth species lore, passives, vulnerabilities, and 3-tier evolution ladder with unlock level requirements.
* **Tab 4: Titles:** Unlocked title management and 1-click active title equipping.

### 4.3 Ascender's Grimoire Screen (`SkillManagerScreen.java`)
* Hotbar slot selectors (`R`, `F`, `C`, `X`) with rank unlock locks.
* Rank filter tabs (`ALL`, `E`, `D`, `C`, `B`, `A`, `S`, `SS`).
* Skill cards displaying 16x16 pixel-art icons, rank badges, cost, cooldown, description, and Equip / Swap / Unequip buttons.

### 4.4 Main RPG HUD (`AscendHudRenderer.java`)
* **Top-Left Player Banner:** Name, race, dual-class title, level, rank, and XP bar.
* **Bottom-Left Combat Meters:** Emerald Stamina Bar (`STA`), Cyan Mana Bar (`MP`), and Dash status indicator.
* **Bottom-Right Skill Slots:** 4 action slots (`R`, `F`, `C`, `X`) + 1 signature ultimate slot (`V`) with radial cooldown sweeps, rank badges, and mana insufficiency dimming.
