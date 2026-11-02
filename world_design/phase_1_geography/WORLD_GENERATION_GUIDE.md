# ASCEND: WORLD GENERATION & COMPATIBILITY WORKFLOW GUIDE
## Phase 1: Handcrafted World Geography

**Mod ID:** `ascend`  
**Minecraft Version:** Java Edition 1.20.1 Fabric  
**Deliverable Document:** `world_design/phase_1_geography/WORLD_GENERATION_GUIDE.md`  

---

## 1. Analysis of User-Provided Map ("Cernution by McMeddon")

The user provided the archive:  
`Copy of 20261018 Cernution 1.21.5-26.3 by McMeddon .zip` (2,049,093,544 bytes, ~2.0 GB).

### Technical Audit Findings:
1. **Dimensions & Extents:**
   - Region files span: `r.0.0.mca` to `r.11.11.mca` (144 region files).
   - Coordinate coverage: $X = 0 \dots 6,143$, $Z = 0 \dots 6,143$ ($6,144 \times 6,144$ blocks).
   - Island Continent Shape: Centered at $(3,072, 3,072)$, enclosed by ocean on all four margins.
2. **Minecraft Version Discrepancy:**
   - `level.dat` reports `DataVersion: 3837` (Minecraft 1.20.5 / 1.21).
   - Blocks inside chunk palettes use 1.20.5+ IDs (specifically `minecraft:short_grass` instead of 1.20.1's `minecraft:grass`).
   - If opened unmodified in Minecraft 1.20.1, the client either rejects the world with a "World saved in a newer version" error, or purges chunk palettes containing unsupported block states.
3. **Feasibility Verdict ("Can it possible?"):**
   **YES, IT IS FULLY POSSIBLE**, through a 3-step conversion pipeline:
   - **Step 1 (Coordinate Re-Centering):** Shift all region files by $-6$ regions ($-3,072$ blocks) in both X and Z. Regions $r.X.Z$ map to $r.(X-6).(Z-6)$, moving the center from $(3072, 3072)$ to exact $(0, 0)$.
   - **Step 2 (Ocean Padding to 8,000 × 8,000):** The continent occupies $-3,072 \dots +3,071$. Generating a 500-block ocean buffer outward to $-4,000 \dots +4,000$ completes the exact 8,000 × 8,000 continent boundary.
   - **Step 3 (1.20.1 Backward Compatibility Patch):** Patch `level.dat` and chunk NBT headers:
     - Set `DataVersion` = 3465 (Minecraft 1.20.1).
     - Remap `minecraft:short_grass` $\rightarrow$ `minecraft:grass`.
     - Remove 1.21 trial chamber / vault block entities that crash 1.20.1.

---

## 2. World Generation Pipelines

### Approach A: The WorldPainter Workflow (Custom Handcrafted Continent)
For team members and level designers using WorldPainter:
1. **Download & Launch WorldPainter:**
   - Launch WorldPainter 2.21+ (Java 17/21).
2. **Create Blank Canvas:**
   - Width: 8,000 blocks | Height: 8,000 blocks.
   - Minecraft 1.20 format (World height: -64 to 320, Sea level: 63).
   - Coordinate offset: Centered at (0, 0).
3. **Import Heightmap:**
   - `File` $\rightarrow$ `Import` $\rightarrow$ `Heightmap` $\rightarrow$ Select `world_design/phase_1_geography/preview/continent_geography_map.png`.
   - Scale: 1 pixel = 1 block.
4. **Paint Biomes & Custom Layers:**
   - Select Biome brushes matching [`BIOME_PLAN.md`](file:///e:/games/world_design/phase_1_geography/BIOME_PLAN.md).
   - Use custom ground cover layers for Volcanic Basalt, Red Terracotta, and Podzol.
5. **Export World:**
   - `File` $\rightarrow$ `Export` $\rightarrow$ `Export as new Minecraft map`.
   - Save directly into `.minecraft/saves/ASCEND_Continent_Full`.

---

### Approach B: Python Automated Regional Conversion & Prototype Generator
Included in this mod repository under `world_design/phase_1_geography/terrain_generator/`:

1. **`convert_cernution_to_ascend.py`:**
   - Inspects the provided McMeddon zip.
   - Extracts regions, applies $-3072$ block coordinate shift, patches DataVersion to 3465 (1.20.1), remaps `short_grass` to `grass`, and generates a clean, centered save.
2. **`generate_prototype_world.py`:**
   - Generates a lightweight, playable 512 × 512 block terrain prototype centered at `(0, 0)`.
   - Features the floating Temple of the Chosen at $Y = 220$, rolling green plains down below at $Y = 74$, the northern shoreline of Lake Veyra, and smooth river grading.
   - Generates directly into `.minecraft/saves/ASCEND_Geography_Prototype` for instant in-game testing.
