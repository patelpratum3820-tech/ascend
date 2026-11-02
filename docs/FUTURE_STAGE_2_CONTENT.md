# ASCEND — STAGE 2 WORLD & CONTENT ARCHITECTURE SPECIFICATION

**Mod ID:** `ascend`  
**Target:** Stage 2 Expansion  
**Prerequisite:** Stage 1 Core RPG Engine (Completed)  

---

## 1. Architectural Integration Strategy

Stage 1 established the data and UI foundation. When Stage 2 begins, new content connects directly into existing hooks without rewriting core code:

```
[ Stage 1 Core Data & Engine ]
       │
       ├── QuestManager (QuestProgress) ──────> Connected to Stage 2 NPCs & Guild Boards
       ├── DungeonProgress (E..S Ranks)  ──────> Connected to Stage 2 Dungeon Gates & Portals
       ├── ArtifactProgress (7 Relics)   ──────> Dropped by Stage 2 Story Bosses
       ├── StoryProgress (Milestones)    ──────> Advanced by Stage 2 Story Quests & Dimensions
       └── RaceEvolution (Tiers 1..3)    ──────> Triggered by Stage 2 Great Lake Boss & Rituals
```

---

## 2. Worldgen & Major Structures

Stage 2 structures will be built using NBT structure templates (`.nbt`) placed via custom structure pools and jigsaw processors to ensure 100% fidelity to the concept art:

### A. The Temple of the Chosen (Prologue Sky Realm)
- **Concept Art Reference:** Multi-tiered marble sanctuary with floating celestial rings, golden filigree arches, crystal fountain basin, and a panoramic cloud floor.
- **Implementation Plan:**
  - Replace the interim circular quartz platform with a full multi-part NBT structure (`temple_of_chosen.nbt`).
  - Add ethereal particle emitters and the celestial Goddess avatar NPC for interactive dialogue.

### B. Goblin Cave Gate (Dungeon Rank E)
- **Concept Art Reference:** Massive weathered stone archway embedded into a subterranean cliffside with glowing green runes, spiked iron portcullis, and torch-lit stone threshold.
- **Implementation Plan:**
  - Jigsaw structure generator spawning in mountain ravines and deep underground biomes.
  - Generates the entrance threshold, leading into procedural dungeon corridors and a boss chamber with the Goblin Chieftain.

### C. Human Capital & Adventurer's Guild Hall
- **Concept Art Reference:** Sprawling stone capital with timber-framed Guild Hall, bulletin quest boards, grand banquet tables, training yards, and merchant stalls.
- **Implementation Plan:**
  - Custom village type replacing standard plains villages in designated kingdom zones.
  - Guild Receptionist NPC delivering bounties via `QuestManager`.

---

## 3. Custom Mobs, Bosses & Dimensions

1. **Regional Bosses:**
   - **Great Lake Leviathan:** Water sovereign; defeats grant the first artifact (*Heart of the Great Lake*) and unlocks the Angel / Demon moral choice.
   - **Nether Sovereign:** Abyssal lord guarding the Nether Citadel; drops *Nether Sovereign Crown*.
2. **Custom Dimensions:**
   - **Dragon Ravines:** High-tier volcanic dimension accessed via Dragonite portal.
   - **Floating Islands (Skylands):** Low-gravity celestial dimension accessed via *Astral Key of the Skies*.
   - **God's Realm:** Final endgame dimension for transcendent Chosen Ascenders.

