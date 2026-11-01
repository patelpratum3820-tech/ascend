# ASCEND — Natural World Generation & Balance Guide (Steps W1 - W6)

This guide provides everything needed to verify, test, and tune the natural world generation, dungeons, structures, and mobs in the **Ascend Mod** (`ascend-0.1.0-alpha.jar`).

---

## 1. Natural Biomes (Step W1)
All 5 biomes blend naturally into the Overworld multi-noise generation:
- **Enchanted Forest**: Mild & wet forest with Mystic Wood trees (blue-purple leaves), glowing vegetation, and celestial blue fog.
- **Emberlands**: Hot & arid badlands/desert biome with charred Emberwood trees, lava pools, and red-brown ash terrain.
- **Crystal Caverns**: Underground cave biome rich in glowing Mana Crystal clusters and ore veins.
- **Shadow Marsh**: Cold, dark swamp biome with murky waters, twisted trees, and deep atmospheric fog.
- **Dragon Peaks**: Jagged snow-capped mountain biome with cliff-exposed Dragonite ore veins.

### Test Commands:
```mcfunction
/locate biome ascend:enchanted_forest
/locate biome ascend:emberlands
/locate biome ascend:crystal_caverns
/locate biome ascend:shadow_marsh
/locate biome ascend:dragon_peaks
```

---

## 2. Configured Features: Trees & Ores (Step W2)
All ores and trees generate naturally across chunk generation:
- **Trees**:
  - `mystic_tree_placed`: Generates in Enchanted Forest (10 trees/chunk default).
  - `emberwood_tree_placed`: Generates in Emberlands (6 trees/chunk default).
- **Ores**:
  - `ore_mythril_placed`: Deep Overworld ($Y=-64$ to $16$), standard & deepslate variants.
  - `ore_mana_crystal_placed`: Caves and Crystal Caverns ($Y=-64$ to $64$).
  - `ore_dragonite_placed`: High mountain peaks ($Y=64$ to $256$) and the Nether ($Y=10$ to $118$).

---

## 3. Surface Structures (Step W3)
Procedural structure templates placed via random-spread structure sets:
- **Guild Hall** (`ascend:guild_hall`): Plains & Forests (spacing: 30)
- **Forge Ruins** (`ascend:forge_ruins`): All land biomes (spacing: 26)
- **Ruined Shrine** (`ascend:ruined_shrine`): Enchanted Forest & Marsh (spacing: 22)
- **Mage Tower** (`ascend:mage_tower`): Enchanted Forest & Plains (spacing: 28)
- **Boss Arena** (`ascend:boss_arena`): Plains & Deserts (spacing: 42)
- **Floating Temple** (`ascend:floating_temple`): Sky Isles at $Y=170$ (spacing: 36)
- **Demon Gate** (`ascend:demon_gate`): Emberlands & Shadow Marsh (spacing: 32)
- **Dungeon Gate** (`ascend:dungeon_gate`): Overworld surface entrance (spacing: 24)

### Test Commands:
```mcfunction
/locate structure ascend:guild_hall
/locate structure ascend:forge_ruins
/locate structure ascend:ruined_shrine
/locate structure ascend:mage_tower
/locate structure ascend:boss_arena
/locate structure ascend:floating_temple
/locate structure ascend:demon_gate
/locate structure ascend:dungeon_gate
```

---

## 4. Ranked Challenge Dungeons (Step W4)
Dungeons scale in difficulty based on distance from world spawn ($0, 0$):
- **Rank E** ($0 - 500$ blocks): 0 affixes, entry-level challenge.
- **Rank D** ($500 - 1000$ blocks): 1 affix (+10% mob HP/speed).
- **Rank C** ($1000 - 2000$ blocks): 2 affixes.
- **Rank B** ($2000 - 3500$ blocks): 3 affixes.
- **Rank A** ($3500 - 5000$ blocks): 4 affixes.
- **Rank S** ($5000+$ blocks): 5 affixes (+100% mob stats, nightmare hazards).

### Structure:
- Surface **Dungeon Gate Block** connected directly to a descent shaft into an underground dungeon at $Y=-20$.
- Exactly **5 challenge rooms** chosen procedurally from 10 room types (Trap Room, Treasure Vault, Monster Nest, Lava Bridge, Ambush Room, Mini-boss Arena, etc.).
- Final **Boss Chamber** with locked iron gates and interactive **Boss Altar**. Right-click the altar to summon the boss!

---

## 5. Mob Spawning & Entities (Step W5)
All 11 RPG mobs feature custom textures, AI routines, and loot:

```mcfunction
# Passive & Ambient
/summon ascend:spirit_fox       # Enchanted Forest
/summon ascend:mana_deer        # Enchanted Forest
/summon ascend:moon_moth        # Enchanted Forest (Night)

# Hostiles & Pack Mobs
/summon ascend:goblin           # Plains & Forests (Pack AI)
/summon ascend:orc_warrior      # Emberlands & Plains
/summon ascend:shadow_wraith    # Shadow Marsh (Blindness effect)
/summon ascend:dark_mage        # Shadow Marsh (Ranged spells)
/summon ascend:fire_drake       # Emberlands & Dragon Peaks (Flying/Fire immune)
/summon ascend:skeleton_knight  # Dungeons & Caves (Sword & Shield)
/summon ascend:dungeon_golem    # Dungeon Mini-boss Guardian (120 HP)
```

---

## 6. Tunable Configuration (`config/ascend.json`)
All parameters can be tuned without recompiling. The mod auto-generates and updates `config/ascend.json`:

```json
{
  "mythrilOreVeinsPerChunk": 5,
  "manaCrystalOreVeinsPerChunk": 7,
  "dragoniteOreVeinsPerChunk": 6,
  "mysticTreeCountPerChunk": 10,
  "emberwoodTreeCountPerChunk": 6,

  "spiritFoxWeight": 8,
  "manaDeerWeight": 10,
  "slimePupWeight": 6,
  "moonMothWeight": 10,
  "goblinWeight": 30,
  "orcWarriorWeight": 12,
  "shadowWraithWeight": 20,
  "skeletonKnightWeight": 10,
  "darkMageWeight": 6,
  "fireDrakeWeight": 6,

  "guildHallSpacing": 30,
  "forgeRuinsSpacing": 26,
  "ruinedShrineSpacing": 22,
  "mageTowerSpacing": 28,
  "bossArenaSpacing": 42,
  "floatingTempleSpacing": 36,
  "demonGateSpacing": 32,

  "rankEDistance": 500,
  "rankDDistance": 1000,
  "rankCDistance": 2000,
  "rankBDistance": 3500,
  "rankADistance": 5000
}
```

