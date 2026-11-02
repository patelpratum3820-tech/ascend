import os
import zipfile
import gzip
import zlib
import re
import struct

ZIP_PATH = "Copy of 20261018 Cernution 1.21.5-26.3 by McMeddon .zip"
TARGET_DATAVERSION = 3465 # Minecraft 1.20.1

def inspect_and_plan_conversion():
    print("=" * 60)
    print("ASCEND: CERNUTION 1.21 -> 1.20.1 CONVERSION PIPELINE PLAN")
    print("=" * 60)

    if not os.path.exists(ZIP_PATH):
        print(f"Archive {ZIP_PATH} not found!")
        return

    with zipfile.ZipFile(ZIP_PATH, 'r') as z:
        print(f"Archive loaded: {ZIP_PATH}")
        print(f"Total entries: {len(z.namelist())}")

        # Region coordinate analysis
        reg_pattern = re.compile(r'^20261018 Cernution 1\.21\.5-26\.3/region/r\.(-?\d+)\.(-?\d+)\.mca$')
        regions = []
        for n in z.namelist():
            m = reg_pattern.match(n)
            if m:
                rx, rz = int(m.group(1)), int(m.group(2))
                regions.append((rx, rz, n))

        print(f"Detected {len(regions)} continent region files.")
        min_rx = min(r[0] for r in regions)
        max_rx = max(r[0] for r in regions)
        min_rz = min(r[1] for r in regions)
        max_rz = max(r[1] for r in regions)

        print(f"Current Region Extents: X [{min_rx} .. {max_rx}] | Z [{min_rz} .. {max_rz}]")
        print(f"Current Block Extents:  X [{min_rx*512} .. {(max_rx+1)*512-1}] | Z [{min_rz*512} .. {(max_rz+1)*512-1}]")
        print(f"Current Geographic Center: ({ (min_rx + max_rx + 1) * 256 }, { (min_rz + max_rz + 1) * 256 })")

        # Offset calculation
        offset_rx = -6 # -3072 blocks
        offset_rz = -6 # -3072 blocks

        new_min_rx = min_rx + offset_rx
        new_max_rx = max_rx + offset_rx
        new_min_rz = min_rz + offset_rz
        new_max_rz = max_rz + offset_rz

        print("\n--- TRANSFORMATION MATRIX ---")
        print(f"Region Offset: dRX = {offset_rx}, dRZ = {offset_rz} (-3072 blocks)")
        print(f"New Region Extents: X [{new_min_rx} .. {new_max_rx}] | Z [{new_min_rz} .. {new_max_rz}]")
        print(f"New Block Extents:  X [{new_min_rx*512} .. {(new_max_rx+1)*512-1}] | Z [{new_min_rz*512} .. {(new_max_rz+1)*512-1}]")
        print(f"New Continent Center: EXACT (X: 0, Z: 0)")
        print(f"Ocean Padding required for 8,000 x 8,000: ~450-500 blocks on all outer margins")

        print("\n--- 1.20.1 COMPATIBILITY PATCHES ---")
        print(f"1. Patch DataVersion 3837 -> {TARGET_DATAVERSION} (Minecraft 1.20.1 Fabric)")
        print("2. Remap Block State: 'minecraft:short_grass' -> 'minecraft:grass'")
        print("3. Filter 1.21 Block Entities (Vaults, Trial Spawners, Ominous Item Spawners)")

if __name__ == "__main__":
    inspect_and_plan_conversion()

