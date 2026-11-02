# ASCEND: HYDROLOGICAL RIVER & DRAINAGE NETWORK
## Phase 1: Handcrafted World Geography

**Continent Dimension:** 8,000 × 8,000 blocks  
**Sea Level:** Y = 63  
**Deliverable Document:** `world_design/phase_1_geography/RIVER_PLAN.md`  

---

## 1. Hydrological Architecture

Rivers in ASCEND strictly follow downhill gravity and geographical realism. Water never flows uphill. All primary river systems originate in high precipitation highlands (Northern Glaciers, Western Elven Woods, or Eastern Highland Ravines) and discharge into Lake Veyra, low-lying marshes, or the outer ocean.

```
       [Frostspire Glaciers (Y=210)]
                   │
                   ▼ (Glacial Runoff)
            [Silverfall River]
                   │
                   ▼
           [LAKE VEYRA (Y=63)] ◄─────── [Whispering Stream (West)]
                   │
                   ▼ (Veyra Outlet)
          [Great Aurelian River]
                   │
      ┌────────────┴────────────┐
      ▼                         ▼
[Royal Reach (East)]    [Southern Estuary (South)]
      │                         │
      ▼                         ▼
[Eastern Ocean]         [Shattered Coast]
```

---

## 2. Major River Systems

### 1. The Silverfall Torrent (Northern System)
- **Source:** Glacial meltwaters beneath Frostspire Peak `(X: 100, Z: -2,400, Y: 155)`.
- **Course:** Cascades south through rocky mountain gorges, carving natural waterfalls into stepped stone terraces.
- **Terminus:** Empties into the northern shore of **Lake Veyra** `(X: 0, Z: -1,200, Y: 63)`.
- **Width:** 12 to 24 blocks wide; clear cold water with gravel riverbeds.

### 2. The Whispering Run (Western Elven System)
- **Source:** High springs in the sacred Elven hills `(X: -2,000, Z: -1,100, Y: 105)`.
- **Course:** Winds eastward through dense canopy forests, forming natural oxbow bends, mossy shallows, and quiet pools.
- **Terminus:** Discharges into the western arm of **Lake Veyra** `(X: -450, Z: -700, Y: 63)`.
- **Width:** 8 to 16 blocks wide; sandy/clay banks with lily pads.

### 3. The Great Aurelian River (Central Artery)
- **Source:** The southern sluice of **Lake Veyra** `(X: 0, Z: -200, Y: 63)`.
- **Course:** Flows south past the Temple Descent plateau, wrapping around the eastern bluffs of the **Aurelia Capital** `(X: 200, Z: 900)`.
- **Branching Delta:** At `(X: 150, Z: 1,400)`, splits into:
  - **Eastern Branch:** Navigable river corridor flowing east into the ocean between Draconis and the Demon Kingdom.
  - **Southern Branch:** Meanders south through the Beastkin borderlands, ending in the mangrove estuaries of the Shattered Frontier.
- **Width:** 28 to 45 blocks wide; deep channel (water depth 4 to 8 blocks) suited for river shipping.

### 4. The Dragon Canyon Flume (Eastern Highland System)
- **Source:** Rain basins on the Draconis plateau `(X: 2,600, Z: -1,300, Y: 135)`.
- **Course:** Plunges through vertical clefts directly into the canyon floor `(Y: 63)`, rushing between narrow red rock walls.
- **Terminus:** Emptying through a dramatic sea chasm into the Eastern Ocean `(X: 3,400, Z: -900)`.

---

## 3. Hydrological Rules for Terrain Export

1. **Continuous Downhill Grade:** River beds must maintain a monotonic non-increasing elevation:
   - Source: $Y = 120 \dots 160$ (Waterfalls / Rapids).
   - Mid-course: $Y = 68 \dots 75$ (Rocky shoals).
   - Base level: $Y = 63$ (Uniform water surface level at lake and ocean entry).
2. **Carved Valleys:** Rivers must flow within natural V-shaped or U-shaped carved channels, never raised unnaturally above surrounding fields.
3. **Shoreline Variation:** Riverbanks feature organic gravel bars, sandy beaches, reeds, and clay deposits rather than sheer vertical block cuts.
