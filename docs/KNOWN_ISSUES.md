# ASCEND — KNOWN ISSUES & RESOLUTION LOG (STAGE 1)

**Mod ID:** `ascend`  
**Minecraft Version:** 1.20.1 Fabric  
**Audit Date:** November 1, 2026  

---

## 1. Resolved In-Game Issues

| Issue / Symptom | Root Cause | Resolution | Status |
|---|---|---|---|
| **Chunky, Jagged Sword Held in Hand** | Sword textures were generated with an over-wide 5-pixel diagonal staircase and no crossguard, extruding into a massive paddle in first-person handheld view. | Re-engineered all 12 weapon textures (`mythril_sword`, `dragonite_sword`, `voidsteel_sword`, katanas, greatswords, daggers) using standard 2-pixel wide blades, wings, pommels, and curved single-edge katana silhouettes. | **RESOLVED** |
| **Awakening Dialog Overflow & Toast Overlap** | Line listing all 6 races exceeded dialogue box width; Minecraft recipe unlock toast hovered over top-right corner; character name field overlapped proceed button. | Auto-cleared recipe toasts on opening; refined margins to compact lines ("Six Fantasy Species", "Dual Combat Classes"); spaced name input and proceed button with 10px clear margin. | **RESOLVED** |
| **Mithril Ore at Y=39 in Surface Caves** | Worldgen height range placed stone-variant ore across $Y=-64$ to $+64$. | Restrained placed feature strictly to $Y=-64$ to $-16$ deepslate strata only; tagged with `minecraft:needs_diamond_tool`. | **RESOLVED** |
| **Flat, Blocky Solid-Color Goblin Mob** | Procedural script filled solid flat color boxes for the head, torso, and eyes without shading. | Created high-detail goblin texture with mottled olive skin shading, amber eyes with slit pupils, tusks, leather vest stitching, belt buckle, and arm wraps. | **RESOLVED** |
| **Stat Progression & Difficulty Untethered** | Player difficulty was recorded in NBT but did not scale combat damage or stamina expenditure. | Implemented `getDifficultyDamageMultiplier()` and `getStaminaConsumptionMultiplier()` in `PlayerData`, hooked directly into `PlayerEntityMixin.damage()` and stamina consumption. | **RESOLVED** |

---

## 2. Architectural Design Constraints & Notes

1. **Server Authority:**
   All stat allocations, essence deductions, and quest rewards must remain server-side. The client sends intents (`COMPLETE_CREATION_ID`, `CLAIM_QUEST_ID`, etc.), which the logical server validates.
2. **Keybinding Collisions in Modpacks:**
   - `K` = Character Profile Screen
   - `J` = Quest Journal Screen
   - `U` = Dungeon & Hunter Rank Screen
   - `G` = Grimoire & Skill Manager Screen
   If key conflicts occur with other mods, players can customize bindings in Minecraft's standard Options $\rightarrow$ Controls menu or use the in-inventory shortcut buttons (`✦ Status`, `✦ Gear`).
3. **Stage 2 Content Boundaries:**
   World generation structures (Temple of the Chosen sky palace, Goblin Cave Gate, Guild Hall, Capital City) and custom bosses are reserved for Stage 2. Stage 1 provides the foundational progression, rank ladders, artifact registries, and quest data.
