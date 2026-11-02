# ASCEND: CONTINENTAL COORDINATE DIRECTORY
## Phase 1: Handcrafted World Geography

**Continent Dimension:** 8,000 × 8,000 blocks  
**Origin / World Center:** (X = 0, Z = 0)  
**Deliverable Document:** `world_design/phase_1_geography/COORDINATES.md`  

---

## 1. Continental Boundaries & World Border

| Parameter | Value | Notes |
|---|---|---|
| **World Border Center** | `X = 0, Z = 0` | Enforced via `/worldborder set 8000` |
| **Western Border** | `X = -4,000` | Open deep ocean boundary |
| **Eastern Border** | `X = +4,000` | Open deep ocean boundary |
| **Northern Border** | `Z = -4,000` | Frigid outer ocean / ice sheet boundary |
| **Southern Border** | `Z = +4,000` | Stormy southern sea boundary |
| **Continent Coastline Bounds** | `X: -3,300 to +3,400` \| `Z: -3,400 to +3,500` | Irregular natural coastline surrounded by 500-700 blocks of ocean |

---

## 2. Regional Center Coordinates & Bounding Envelopes

| Region Key | Region Name | Center X | Center Z | Radius (m) | Coordinate Bounding Box (X1, Z1 to X2, Z2) |
|---|---|---:|---:|---:|---|
| `REGION_SPAWN` | **Temple of the Chosen (Sky)** | `0` | `0` | 150 | `(-150, -150)` to `(+150, +150)` @ Y=220 |
| `REGION_HEARTLAND` | **Human Kingdom (Aurelia)** | `0` | `900` | 900 | `(-900, 0)` to `(+900, 1,800)` |
| `REGION_LAKE` | **Lake Veyra / Great Lake** | `0` | `-700` | 550 | `(-550, -1,250)` to `(+550, -150)` |
| `REGION_ELVEN` | **Elven Kingdom (Elarion)** | `-2,300` | `-800` | 1,100 | `(-3,400, -1,900)` to `(-1,200, +300)` |
| `REGION_SLIME` | **Prism Pools (Slime Homeland)** | `-2,500` | `1,300` | 750 | `(-3,250, 550)` to `(-1,750, 2,050)` |
| `REGION_BEASTKIN`| **Beastkin Territory** | `-900` | `2,500` | 1,000 | `(-1,900, 1,500)` to `(+100, 3,500)` |
| `REGION_FROZEN` | **Frozen Mountains (Frostspire)** | `0` | `-3,000` | 1,300 | `(-1,300, -4,000)` to `(+1,300, -1,700)` |
| `REGION_DRAGON` | **Dragonoid Territory (Draconis)** | `2,500` | `-900` | 1,100 | `(1,400, -2,000)` to `(3,600, +200)` |
| `REGION_DEMON` | **Demon Kingdom (Inferno Keep)** | `2,600` | `1,800` | 1,000 | `(1,600, 800)` to `(3,600, 2,800)` |
| `REGION_SHATTER` | **Shattered Frontier** | `500` | `3,500` | 750 | `(-250, 2,750)` to `(1,250, 4,000)` |

---

## 3. Reserved Buildable Clearings & Future Structure Sites

To prevent geographical features (such as steep cliffs or deep water) from obstructing Stage 2 structures, the following flat/sheltered plateaus are reserved in the terrain:

| Site Identifier | Planned Future Structure | Target Center (X, Z) | Target Elevation (Y) | Reserved Dimension | Terrain Preparation Spec |
|---|---|---:|---|---:|---|
| `SITE_AURELIA_CAPITAL` | Aurelia Capital & Royal Castle | `0, 900` | `Y = 74` | 500 × 500 blocks | Broad elevated meadow above the Aurelian river confluence |
| `SITE_FROSTSPIRE_KEEP` | Frostspire Citadel | `0, -3,000` | `Y = 175` | 350 × 350 blocks | Natural mountain amphitheater valley sheltered from blizzards |
| `SITE_LAKE_DUNGEON` | Leviathan Dungeon Threshold | `0, -750` | `Y = 40 (Water: 63)` | 80 × 80 blocks | Sunken cavern maw in the bedrock floor of Lake Veyra |
| `SITE_ELARION_SANCTUARY`| Elven Capital of Elarion | `-2,300, -800` | `Y = 95` | 400 × 400 blocks | Ancient forest glade encircled by massive natural tree ridges |
| `SITE_PRISM_VILLAGE` | Slime Crystal Settlement | `-2,500, 1,300` | `Y = 82` | 300 × 300 blocks | Terraced silica mineral flat overlooking multi-colored pools |
| `SITE_BEAST_HORDE` | Beastkin Great Encampment | `-900, 2,500` | `Y = 88` | 450 × 450 blocks | Defensible savanna plateau crowned by natural sandstone ramparts |
| `SITE_DRACONIS_AERIE` | Draconis Great Spire | `2,500, -900` | `Y = 160` | 400 × 400 blocks | Massive flat-topped cliff mesa flanked by 100-block deep ravines |
| `SITE_INFERNO_KEEP` | Archdemon Citadel | `2,600, 1,800` | `Y = 145` | 400 × 400 blocks | Solid basalt plateau positioned inside the northern caldera rim |

