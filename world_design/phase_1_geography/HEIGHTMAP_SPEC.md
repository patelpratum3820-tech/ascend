# ASCEND: TERRAIN HEIGHTMAP SPECIFICATION
## Phase 1: Handcrafted World Geography

**Continent Dimension:** 8,000 × 8,000 blocks  
**Vertical Limits:** Minecraft 1.20.1 World Bounds ($Y = -64 \dots +320$)  
**Deliverable Document:** `world_design/phase_1_geography/HEIGHTMAP_SPEC.md`  

---

## 1. Elevation Master Layers

```
Y = 230 ──────── Highest Frozen Peaks (Glacial Summits)
Y = 220 ──────── [TEMPLE OF THE CHOSEN] Sky Sanctuary (Floating Island)
Y = 180 ──────── Volcanic Caldera Rims / Dragon Ravine Plateaus
Y = 145 ──────── Inferno Keep Plateau / Draconis Terraces
Y = 125 ──────── Elven Ancient Canopy Ridges
Y = 105 ──────── Forest Foothills / Savanna Bluffs
Y = 85  ──────── Rolling Heartland Grasslands / Tribal Steppe
Y = 74  ──────── Aurelia Capital City Plateau / Farmland Valleys
Y = 63  ──────── Continental Sea Level (Oceans, Major Rivers, Lake Veyra Surface)
Y = 55  ──────── Dragon Ravine Canyon Floors
Y = 35  ──────── Deep Lake Veyra Bed & Underwater Trenches
Y = 20  ──────── Coastal Ocean Bed
```

---

## 2. Regional Terrain & Elevation Rules

### A. Northern Frozen Mountain Chain ($Z = -1,800 \dots -3,800$)
- **Peak Elevation Range:** $Y = 170 \dots 230$.
- **Terrain Profile:** Glacial horn peaks formed by sharp erosion formulas. Sheer cliff drops on north faces, stepped terraces on south faces.
- **Valleys & Passes:** Glacial valleys sit at $Y = 110 \dots 140$, allowing traversable mountain passes between the heartland and the northern sea.
- **Frostspire Plateau:** Enclosed basin flattened to $Y = 175 \pm 3$, surrounded on north, east, and west by crags reaching $Y = 215$.

### B. Southeastern Volcanic Caldera ($X = 1,800 \dots 3,400$, $Z = 1,000 \dots 2,600$)
- **Caldera Rim:** Sharp basalt ridges reaching $Y = 170 \dots 210$.
- **Caldera Floor:** Lava lakes and blackstone flats at $Y = 110 \dots 125$.
- **Demon Fortress Seat:** Elevated hornfels mesa situated at $Y = 145 \pm 2$, towering 30 blocks above surrounding magma flows, connected via narrow natural ridges.

### C. Eastern Dragon Ravines ($X = 1,600 \dots 3,400$, $Z = -1,800 \dots 0$)
- **Highland Plateaus:** Mesas and flat sandstone tablelands at $Y = 140 \dots 180$.
- **Ravine Cuts:** Deep vertical fissures plunging $70 \dots 100$ blocks down to canyon beds at $Y = 55 \dots 75$. Ravine widths vary from 25 to 80 blocks to allow flight corridors.
- **Natural Bridges:** Intact rock arches left at $Y = 155$, spanning between opposing cliff faces.

### D. Western Elven Forest ($X = -3,300 \dots -1,200$, $Z = -1,800 \dots 200$)
- **General Elevation:** Undulating forest hills from $Y = 80 \dots 125$.
- **Valley Glades:** Riverbeds at $Y = 66 \dots 72$, with mossy riverbanks gently rising into ancient tree terraces.
- **Elarion Hollow:** Natural bowl depression at $Y = 95$, encircled by an outer ridge at $Y = 120$.

### E. Central Human Heartland ($X = -900 \dots +900$, $Z = 0 \dots 1,800$)
- **General Elevation:** Gentle rolling loam hills from $Y = 68 \dots 84$. Slopes never exceed $20^\circ$, ensuring natural aesthetic suitability for roads and farming.
- **Aurelia Capital Site:** Planar plateau at $Y = 74 \pm 1$, bordered by natural river escarpments on the eastern and southern edges.

### F. Lake Veyra ($X = -550 \dots +550$, $Z = -1,250 \dots -150$)
- **Water Surface:** Uniform $Y = 63$.
- **Shoreline Elevation:** Banks gently rise from $Y = 64$ to $Y = 75$ over 40 blocks.
- **Depth Profile:** Central basin depth $Y = 48 \dots 52$; submerged trench at north end plunging to $Y = 35 \dots 40$.

---

## 3. Mathematical Noise Formula for Procedural Generation

For computational generation in WorldPainter or Python scripts, the continental elevation $H(x, z)$ is modeled as:

$$H(x, z) = B(x, z) + M(x, z) \times N_{\text{macro}}(x, z) + N_{\text{detail}}(x, z)$$

Where:
- $B(x, z)$ is the continental base mask (1.0 on land, tapering to 0.0 at coastline, negative in deep ocean).
- $M(x, z)$ is the regional elevation multiplier (highest in northern mountains and volcanic rims).
- $N_{\text{macro}}$ is 4-octave Perlin noise ($\lambda = 800\text{m}$) for macroscopic mountain ridges and plateaus.
- $N_{\text{detail}}$ is 3-octave Simplex noise ($\lambda = 60\text{m}$) for organic slopes, gullies, and riverbeds.

