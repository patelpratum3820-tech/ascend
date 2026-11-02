import os
import zipfile
import gzip
import zlib
import struct
import re

ZIP_PATH = "Copy of 20261018 Cernution 1.21.5-26.3 by McMeddon .zip"
TARGET_DATAVERSION = 3465 # Minecraft 1.20.1 Fabric

OUT_LOCAL = "world_design/phase_1_geography/prototype_world"
OUT_SAVES = os.path.expandvars(r"%APPDATA%\.minecraft\saves\ASCEND_Geography_Prototype")

# Region mapping for 1024x1024 block center area around (0, 0)
# Original Cernution region -> Output prototype region
REGION_MAP = [
    ("20261018 Cernution 1.21.5-26.3/region/r.6.6.mca", "r.0.0.mca"),     # X: [0..511], Z: [0..511]
    ("20261018 Cernution 1.21.5-26.3/region/r.5.6.mca", "r.-1.0.mca"),    # X: [-512..-1], Z: [0..511]
    ("20261018 Cernution 1.21.5-26.3/region/r.6.5.mca", "r.0.-1.mca"),    # X: [0..511], Z: [-512..-1]
    ("20261018 Cernution 1.21.5-26.3/region/r.5.5.mca", "r.-1.-1.mca"),   # X: [-512..-1], Z: [-512..-1]
]

def convert_region_stream(in_stream, dx_chunks=-192, dz_chunks=-192):
    header = in_stream.read(4096)
    timestamps = in_stream.read(4096)
    
    out_chunks = []
    new_header = bytearray(4096)
    new_timestamps = bytearray(timestamps)
    
    current_sector = 2 # Sectors 0 and 1 are header and timestamps
    
    for i in range(1024):
        offset = int.from_bytes(header[i*4 : i*4+3], 'big')
        count = header[i*4+3]
        if offset > 0:
            in_stream.seek(offset * 4096)
            chunk_len = int.from_bytes(in_stream.read(4), 'big')
            comp = in_stream.read(1)[0]
            chunk_data = in_stream.read(chunk_len - 1)
            
            raw = zlib.decompress(chunk_data)
            
            # Patch DataVersion (3837 -> 3465)
            raw = raw.replace(b'\x03\x00\x0bDataVersion\x00\x00\x0e\xfd', b'\x03\x00\x0bDataVersion\x00\x00\x0d\x89')
            
            # Patch xPos
            m_x = re.search(b'\x03\x00\x04xPos(....)', raw)
            if m_x:
                old_x = struct.unpack('>i', m_x.group(1))[0]
                new_x = old_x + dx_chunks
                raw = raw.replace(m_x.group(0), b'\x03\x00\x04xPos' + struct.pack('>i', new_x), 1)
                
            # Patch zPos
            m_z = re.search(b'\x03\x00\x04zPos(....)', raw)
            if m_z:
                old_z = struct.unpack('>i', m_z.group(1))[0]
                new_z = old_z + dz_chunks
                raw = raw.replace(m_z.group(0), b'\x03\x00\x04zPos' + struct.pack('>i', new_z), 1)
                
            # Remap short_grass -> grass
            raw = raw.replace(b'\x00\x15minecraft:short_grass', b'\x00\x0fminecraft:grass')
            
            recomp = zlib.compress(raw)
            payload_len = len(recomp) + 1
            payload = struct.pack('>i', payload_len) + bytes([2]) + recomp
            
            # Sector padding (4096 bytes)
            sectors_needed = (len(payload) + 4095) // 4096
            padded_payload = payload.ljust(sectors_needed * 4096, b'\x00')
            
            # Write to new header
            new_header[i*4 : i*4+3] = current_sector.to_bytes(3, 'big')
            new_header[i*4+3] = sectors_needed
            
            out_chunks.append(padded_payload)
            current_sector += sectors_needed

    return bytes(new_header) + bytes(new_timestamps) + b''.join(out_chunks)

def patch_level_dat(raw_level_dat):
    # Patch DataVersion
    dv_pattern = b'\x03\x00\x0bDataVersion' + struct.pack('>i', 3837)
    dv_replace = b'\x03\x00\x0bDataVersion' + struct.pack('>i', TARGET_DATAVERSION)
    if dv_pattern in raw_level_dat:
        raw_level_dat = raw_level_dat.replace(dv_pattern, dv_replace, 1)

    # Spawn coords: X=0, Y=221, Z=0 (Directly on Temple of Chosen)
    for tag_name, val in [(b'SpawnX', 0), (b'SpawnY', 221), (b'SpawnZ', 0)]:
        m = re.search(b'\x03\x00' + bytes([len(tag_name)]) + tag_name + b'....', raw_level_dat)
        if m:
            raw_level_dat = raw_level_dat.replace(m.group(0), m.group(0)[:-4] + struct.pack('>i', val), 1)

    # GameType = 1 (Creative mode for easy flight and terrain inspection)
    m_gt = re.search(b'\x03\x00\x08GameType....', raw_level_dat)
    if m_gt:
        raw_level_dat = raw_level_dat.replace(m_gt.group(0), m_gt.group(0)[:-4] + struct.pack('>i', 1), 1)

    # LevelName = "ASCEND - Handcrafted Geography Prototype"
    m_name = re.search(b'\x08\x00\tLevelName\x00(.)(.*)', raw_level_dat)
    if m_name:
        new_title = b"ASCEND - Handcrafted Geography Prototype"
        title_len = len(new_title)
        old_pattern = b'\x08\x00\tLevelName' + raw_level_dat[raw_level_dat.find(b'\x08\x00\tLevelName') + 11 : raw_level_dat.find(b'\x08\x00\tLevelName') + 13 + raw_level_dat[raw_level_dat.find(b'\x08\x00\tLevelName') + 12]]
        new_pattern = b'\x08\x00\tLevelName' + struct.pack('>H', title_len) + new_title
        raw_level_dat = raw_level_dat.replace(old_pattern, new_pattern, 1)

    return gzip.compress(raw_level_dat)

def build_prototype():
    print("=" * 65)
    print("ASCEND: BUILDING HANDCRAFTED TERRAIN PROTOTYPE SAVE (1.20.1)")
    print("=" * 65)

    if not os.path.exists(ZIP_PATH):
        print(f"Error: {ZIP_PATH} not found!")
        return

    # Prepare directories
    for target_dir in [OUT_LOCAL, OUT_SAVES]:
        reg_dir = os.path.join(target_dir, "region")
        os.makedirs(reg_dir, exist_ok=True)

    with zipfile.ZipFile(ZIP_PATH, 'r') as z:
        # 1. Process level.dat
        print("Reading and patching level.dat...")
        with z.open('20261018 Cernution 1.21.5-26.3/level.dat') as f:
            decomp_level = gzip.decompress(f.read())
            patched_level = patch_level_dat(decomp_level)

        for target_dir in [OUT_LOCAL, OUT_SAVES]:
            level_path = os.path.join(target_dir, "level.dat")
            with open(level_path, "wb") as out_f:
                out_f.write(patched_level)
            print(f"Wrote {level_path}")

        # 2. Process the 4 Center Regions (1024x1024 blocks around 0, 0)
        for src_path, out_name in REGION_MAP:
            print(f"Converting {out_name} from {src_path}...")
            with z.open(src_path) as in_f:
                converted_bytes = convert_region_stream(in_f, dx_chunks=-192, dz_chunks=-192)

            for target_dir in [OUT_LOCAL, OUT_SAVES]:
                out_reg_path = os.path.join(target_dir, "region", out_name)
                with open(out_reg_path, "wb") as out_f:
                    out_f.write(converted_bytes)
                print(f" -> Saved {out_reg_path} ({len(converted_bytes)} bytes)")

    print("\n" + "=" * 65)
    print("SUCCESS: ASCEND Geography Prototype Save has been built!")
    print(f"1. Workspace Copy: {OUT_LOCAL}")
    print(f"2. Minecraft Saves: {OUT_SAVES}")
    print("Open Minecraft Java 1.20.1 -> Singleplayer -> 'ASCEND - Handcrafted Geography Prototype'")
    print("=" * 65)

if __name__ == "__main__":
    build_prototype()
