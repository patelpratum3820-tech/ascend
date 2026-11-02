# ASCEND: STAGE 2 STRUCTURE RESERVATIONS & TERRAIN GRADING
## Phase 1: Handcrafted World Geography

**Continent Dimension:** 8,000 × 8,000 blocks  
**Deliverable Document:** `world_design/phase_1_geography/STRUCTURE_RESERVATIONS.md`  

---

## 1. Ground Grading Protocol

While **no actual buildings, walls, or NPCs are placed in Phase 1**, the terrain in Phase 1 must be specifically graded and leveled in designated reservation zones. This guarantees that when Stage 2 begins, multi-part structure jigsaws and NBT templates can be placed seamlessly without floating in the air or becoming buried inside steep mountain terrain.

---

## 2. Master Reservation Directory

| Reservation ID | Associated Future Structure | Center Coordinates | Target Elevation | Footprint (X × Z) | Geological & Grading Rules |
|---|---|---:|---|---:|---|
| `RES_TEMPLE_SKY` | **Temple of the Chosen (Sky)** | `0, 0` | `Y = 220` | 120 × 120 | Floating void space; no ground collision |
| `RES_TEMPLE_LANDING` | **Descent Arrival Glade** | `24, 24` | `Y = 75` | 60 × 60 | Open clearing in central plains; ringed by wildflowers |
| `RES_AURELIA_ROYAL` | **Aurelia Royal Citadel & Palace** | `0, 900` | `Y = 74` | 300 × 300 | Flat natural terrace elevated 6m above eastern river |
| `RES_AURELIA_DISTRICT`| **Aurelia Commoner City & Docks** | `150, 950` | `Y = 68` | 400 × 350 | Riverfront slope gently descending to water level (Y=63) |
| `RES_GUILD_HEADQUARTERS`| **Adventurers Guild Grand Hall** | `-120, 850` | `Y = 74` | 150 × 150 | Centrally accessible meadow bordering the main avenue |
| `RES_LAKE_LEVIATHAN`| **Great Lake Leviathan Cavern Maw** | `0, -750` | `Y = 38` | 70 × 70 | Natural subterranean rock maw carved into lake bedrock |
| `RES_FROSTSPIRE_KEEP`| **Frostspire Royal Castle** | `0, -3,000` | `Y = 175` | 350 × 350 | Glacial amphitheater flanked by vertical peak ridges |
| `RES_ELARION_SANCTUARY`| **Elarion Tree Sanctuary** | `-2,300, -800` | `Y = 95` | 400 × 400 | Level circular forest glade surrounded by ancient trees |
| `RES_SLIME_COLONY` | **Prism Pools Crystal Hamlet** | `-2,500, 1,300` | `Y = 82` | 250 × 250 | Stepped silica flats overlooking multi-hued hot springs |
| `RES_BEAST_WARCAMP` | **Beastkin Tribal Warcamp** | `-900, 2,500` | `Y = 88` | 350 × 350 | Elevated red savanna bluff with two narrow ramp entrances |
| `RES_DRACONIS_AERIE`| **Draconis Dragon High Spire** | `2,500, -900` | `Y = 160` | 350 × 350 | Solitary flat-topped cliff mesa encircled by 90m deep canyons |
| `RES_INFERNO_CITADEL`| **Archdemon Obsidian Citadel** | `2,600, 1,800` | `Y = 145` | 350 × 350 | Solid blackstone plateau; 100% devoid of surface lava pools |
| `RES_GOBLIN_GATE_E` | **Goblin Cave Gate (Rank E Dungeon)**| `-350, 450` | `Y = 70` | 60 × 60 | Weathered ravine mouth leading into deep cave threshold |

---

## 3. Buffer Zone Constraints

1. **Clearance Envelopes:** Within the footprint of each reservation ID, slope variations are constrained to within $\pm 1.5$ blocks to allow natural, clean foundation seating for future buildings.
2. **Natural Vegetation Borders:** Surrounding each reserved clearing, natural trees and stone outcroppings are placed as organic frame borders so that the reserved areas appear as natural glades rather than artificial clear-cuts.
3. **No Structure Generation in Phase 1:** No stone bricks, wooden planks, walls, or chests are generated during this phase; only the bedrock, stone, dirt, grass, sand, gravel, and water foundations are prepared.

