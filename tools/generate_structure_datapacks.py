import os
import json

STRUCTURES_META = {
    "guild_hall": {
        "biomes": ["#minecraft:is_forest", "minecraft:plains", "ascend:enchanted_forest"],
        "spacing": 30,
        "separation": 8,
        "salt": 100101,
        "adapt": "beard_thin",
        "height_type": "surface"
    },
    "forge_ruins": {
        "biomes": ["#minecraft:is_overworld"],
        "spacing": 26,
        "separation": 8,
        "salt": 200202,
        "adapt": "beard_thin",
        "height_type": "surface"
    },
    "ruined_shrine": {
        "biomes": ["#minecraft:is_forest", "ascend:shadow_marsh", "#minecraft:is_mountain"],
        "spacing": 22,
        "separation": 6,
        "salt": 300303,
        "adapt": "beard_thin",
        "height_type": "surface"
    },
    "mage_tower": {
        "biomes": ["ascend:enchanted_forest", "minecraft:plains"],
        "spacing": 28,
        "separation": 10,
        "salt": 400404,
        "adapt": "beard_box",
        "height_type": "surface"
    },
    "boss_arena": {
        "biomes": ["#minecraft:is_overworld"],
        "spacing": 42,
        "separation": 14,
        "salt": 500505,
        "adapt": "beard_box",
        "height_type": "surface"
    },
    "floating_temple": {
        "biomes": ["#minecraft:is_overworld"],
        "spacing": 36,
        "separation": 12,
        "salt": 600606,
        "adapt": "none",
        "height_type": "sky" # Spawn at y 150-200
    },
    "demon_gate": {
        "biomes": ["ascend:shadow_marsh", "ascend:emberlands"],
        "spacing": 32,
        "separation": 10,
        "salt": 700707,
        "adapt": "beard_thin",
        "height_type": "surface"
    },
    "dungeon": {
        "biomes": ["#minecraft:is_overworld"],
        "spacing": 24,
        "separation": 8,
        "salt": 888888,
        "adapt": "beard_thin",
        "height_type": "surface",
        "structure_file": "dungeon_gate"
    }
}

def main():
    base_data = "src/main/resources/data/ascend"

    for name, meta in STRUCTURES_META.items():
        # 1. Template Pool
        pool_dir = os.path.join(base_data, "worldgen", "template_pool", name)
        os.makedirs(pool_dir, exist_ok=True)
        struct_location = f"ascend:{meta.get('structure_file', name)}"
        pool_json = {
            "name": f"ascend:{name}/start_pool",
            "fallback": "minecraft:empty",
            "elements": [
                {
                    "weight": 1,
                    "element": {
                        "element_type": "minecraft:single_pool_element",
                        "location": struct_location,
                        "processors": "minecraft:empty",
                        "projection": "rigid"
                    }
                }
            ]
        }
        with open(os.path.join(pool_dir, "start_pool.json"), "w", encoding="utf-8") as f:
            json.dump(pool_json, f, indent=2)

        # 2. Biome Tag
        tag_dir = os.path.join(base_data, "tags", "worldgen", "biome", "has_structure")
        os.makedirs(tag_dir, exist_ok=True)
        tag_json = {
            "replace": False,
            "values": meta["biomes"]
        }
        with open(os.path.join(tag_dir, f"{name}.json"), "w", encoding="utf-8") as f:
            json.dump(tag_json, f, indent=2)

        # 3. Structure
        struct_dir = os.path.join(base_data, "worldgen", "structure")
        os.makedirs(struct_dir, exist_ok=True)
        
        start_height = (
            {"type": "minecraft:uniform", "min_inclusive": {"absolute": 150}, "max_inclusive": {"absolute": 210}}
            if meta["height_type"] == "sky"
            else {"type": "minecraft:constant", "value": {"absolute": 0}}
        )

        structure_json = {
            "type": "minecraft:jigsaw",
            "biomes": f"#ascend:has_structure/{name}",
            "step": "surface_structures",
            "spawn_overrides": {},
            "terrain_adaptation": meta["adapt"],
            "start_pool": f"ascend:{name}/start_pool",
            "size": 1,
            "start_height": start_height,
            "project_start_to_heightmap": "WORLD_SURFACE_WG" if meta["height_type"] == "surface" else None,
            "max_distance_from_center": 80,
            "use_expansion_hack": False
        }
        if meta["height_type"] != "surface":
            del structure_json["project_start_to_heightmap"]

        with open(os.path.join(struct_dir, f"{name}.json"), "w", encoding="utf-8") as f:
            json.dump(structure_json, f, indent=2)

        # 4. Structure Set
        set_dir = os.path.join(base_data, "worldgen", "structure_set")
        os.makedirs(set_dir, exist_ok=True)
        set_json = {
            "structures": [
                {
                    "structure": f"ascend:{name}",
                    "weight": 1
                }
            ],
            "placement": {
                "type": "minecraft:random_spread",
                "spacing": meta["spacing"],
                "separation": meta["separation"],
                "salt": meta["salt"]
            }
        }
        with open(os.path.join(set_dir, f"{name}.json"), "w", encoding="utf-8") as f:
            json.dump(set_json, f, indent=2)

        print(f"Generated datapack configs for structure: {name}")

if __name__ == "__main__":
    main()

