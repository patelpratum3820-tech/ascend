# ASCEND: BIOME DISTRIBUTION & TRANSITIONS SPECIFICATION
## Phase 1: Handcrafted World Geography

**Continent Dimension:** 8,000 × 8,000 blocks  
**Engine:** Minecraft 1.20.1 Biome Format (3D Voronoi Climate Noise)  
**Deliverable Document:** `world_design/phase_1_geography/BIOME_PLAN.md`  

---

## 1. Biome Assignment Blueprint

Every region uses specific vanilla and custom ASCEND biomes to reflect its narrative identity while preserving vanilla 1.20.1 block compatibility:

| Region | Primary Biomes | Secondary / Transition Biomes | Palette & Flora Characteristics |
|---|---|---|---|
| **Northern Frozen Mountains** | `minecraft:frozen_peaks`, `minecraft:jagged_peaks` | `minecraft:snowy_slopes`, `minecraft:grove`, `minecraft:snowy_taiga` | Packed ice, blue ice, snow blocks, spruce, powder snow traps |
| **Northern Foothills** | `minecraft:snowy_plains`, `minecraft:taiga` | `minecraft:old_growth_pine_taiga` | Podzol, coarse dirt, ferns, sweet berry bushes |
| **Lake Veyra** | `minecraft:river`, `minecraft:deep_cold_ocean` (lake center) | `minecraft:beach`, `minecraft:stony_shore` | Seagrass, kelp, clay, sand, gravel shoals |
| **Human Heartland** | `minecraft:plains`, `minecraft:meadow` | `minecraft:forest`, `minecraft:flower_forest`, `minecraft:sunflower_plains` | Lush green grass, wildflowers, oak, birch, farmland loam |
| **Western Elven Forest** | `ascend:enchanted_forest`, `minecraft:dark_forest` | `minecraft:old_growth_birch_forest` | Dense dark oak, mystic trees, glowing mushrooms, moss carpet |
| **Prism Pools** | `ascend:crystal_caverns` (surface), `minecraft:mangrove_swamp` | `minecraft:swamp`, `minecraft:warm_ocean` | Travertine silica, mud, tinted waters, amethyst clusters |
| **Beastkin Territory** | `minecraft:savanna`, `minecraft:windswept_savanna` | `minecraft:savanna_plateau`, `minecraft:sparse_jungle` | Acacia, golden dry grass, terracotta, tall savanna grasses |
| **Dragonoid Ravines** | `minecraft:wooded_badlands`, `minecraft:eroded_badlands` | `minecraft:badlands`, `minecraft:stony_peaks` | Red sandstone, layered terracotta bands, dead bushes, coarse stone |
| **Demon Kingdom** | `ascend:shadow_marsh`, `minecraft:badlands` | `minecraft:basalt_deltas` (adapted), `minecraft:nether_wastes` | Black basalt, smooth basalt, magma blocks, lava channels, obsidian |
| **Outer Ocean** | `minecraft:deep_ocean`, `minecraft:deep_cold_ocean` | `minecraft:ocean` | Deep sea trenches, gravel seabeds, ocean monuments |

---

## 2. Gradient Transition Rules (No Abrupt Biome Cuts)

To achieve the aesthetic of a handcrafted continental masterpiece, biome boundaries are smoothed across a minimum buffer zone of **150 to 300 blocks**:

### 1. Northward Freeze Transition
$$\text{Heartland (Plains)} \xrightarrow[Z=-1,200]{150\text{m}} \text{Cool Birch/Oak} \xrightarrow[Z=-1,600]{200\text{m}} \text{Taiga} \xrightarrow[Z=-2,100]{250\text{m}} \text{Snowy Taiga} \xrightarrow[Z=-2,600]{200\text{m}} \text{Glacial Peaks}$$
- *Temperature Rule:* Surface temperature drops linearly by 0.0004 per block north ($Z < 0$).
- *Visual Rule:* Foliage grass color shifts smoothly from emerald green (`#55C032`) to alpine olive (`#80A545`) to frosted teal (`#608575`).

### 2. Southeast Scorched Transition
$$\text{Heartland (Plains)} \xrightarrow[X=1,200]{200\text{m}} \text{Savanna Steppe} \xrightarrow[X=1,800]{200\text{m}} \text{Badlands} \xrightarrow[X=2,300]{250\text{m}} \text{Volcanic Caldera}$$
- *Downfall & Vegetation:* Downfall drops to 0.0. Grass turns amber, trees thin out, giving way to red rock rubble and smoking basalt crust.

### 3. Eastward Canyon Transition
$$\text{Heartland (Plains)} \xrightarrow[X=1,000]{150\text{m}} \text{Rolling Green Hills} \xrightarrow[X=1,600]{200\text{m}} \text{Stony Ridges} \xrightarrow[X=2,100]{200\text{m}} \text{Sheer Ravines}$$
- *Topography Rule:* Slope gradients increase from $10^\circ \rightarrow 35^\circ \rightarrow 85^\circ$ vertical canyon walls.

### 4. Westward Enchanted Transition
$$\text{Heartland (Plains)} \xrightarrow[X=-800]{150\text{m}} \text{Deciduous Forest} \xrightarrow[X=-1,400]{250\text{m}} \text{Dark Forest} \xrightarrow[X=-1,900]{300\text{m}} \text{Ancient Enchanted Forest}$$
- *Canopy Coverage:* Tree density increases from 15% $\rightarrow$ 45% $\rightarrow$ 85% closed sky canopy.
