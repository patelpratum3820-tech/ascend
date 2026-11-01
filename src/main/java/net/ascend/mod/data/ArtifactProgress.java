package net.ascend.mod.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArtifactProgress {
    public record StoryArtifact(String id, String name, String description, String originDungeon, String rank, String requiredForDimension) {}

    public static final List<StoryArtifact> ALL_ARTIFACTS = List.of(
            new StoryArtifact("lake_heart", "Heart of the Great Lake", "Glistening cerulean pearl pulsing with pure celestial water mana.", "Great Lake Human Dungeon", "E", "Overworld"),
            new StoryArtifact("forest_seed", "Ancient Forest Seed", "Living emerald seed vibrating with the primal life force of the World Tree.", "Ancient Forest Sanctuary", "D", "Overworld"),
            new StoryArtifact("dragon_orb", "Dragon's Gaze Orb", "Smoldering draconic crystal forged in the belly of an ancient wyrm.", "Dragon Ravine", "C", "Dragon Dimension"),
            new StoryArtifact("frost_ember_core", "Frost Ember Core", "Paradoxical gemstone radiating absolute zero and hellfire simultaneously.", "Volcano / Frozen Peaks", "B", "Nether"),
            new StoryArtifact("nether_crown", "Nether Sovereign Crown", "Horned crown of dark obsidian, subjugating all netherborn horrors.", "Nether Fortress Citadel", "A", "Floating Islands"),
            new StoryArtifact("sky_astral_key", "Astral Key of the Skies", "Resonant star-metal prism that parts the cloud barrier of the ancients.", "Sky Citadel", "S", "God's Realm"),
            new StoryArtifact("divine_halo", "Divine Blessing of the Chosen", "Sacred halo granted only by surviving the Goddess of Rebirth's trial.", "God's Realm", "SS", "Ascended Title")
    );

    private final Set<String> unlockedArtifacts = new HashSet<>();

    public ArtifactProgress() {}

    public boolean hasArtifact(String artifactId) {
        return unlockedArtifacts.contains(artifactId.toLowerCase());
    }

    public void unlockArtifact(String artifactId) {
        unlockedArtifacts.add(artifactId.toLowerCase());
    }

    public Set<String> getUnlockedArtifacts() {
        return Collections.unmodifiableSet(unlockedArtifacts);
    }

    public boolean canAccessDimension(String dimensionName) {
        return switch (dimensionName.toLowerCase()) {
            case "overworld" -> true;
            case "dragon" -> hasArtifact("dragon_orb");
            case "nether" -> hasArtifact("frost_ember_core");
            case "floating_islands" -> hasArtifact("nether_crown");
            case "gods_realm" -> hasArtifact("sky_astral_key");
            default -> true;
        };
    }

    public void writeToNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        for (String id : unlockedArtifacts) {
            list.add(NbtString.of(id));
        }
        nbt.put("UnlockedArtifacts", list);
    }

    public void readFromNbt(NbtCompound nbt) {
        unlockedArtifacts.clear();
        if (nbt.contains("UnlockedArtifacts", 9)) {
            NbtList list = nbt.getList("UnlockedArtifacts", 8);
            for (int i = 0; i < list.size(); i++) {
                unlockedArtifacts.add(list.getString(i));
            }
        }
    }

    public void writeToBuffer(PacketByteBuf buf) {
        buf.writeInt(unlockedArtifacts.size());
        for (String id : unlockedArtifacts) {
            buf.writeString(id);
        }
    }

    public void readFromBuffer(PacketByteBuf buf) {
        unlockedArtifacts.clear();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            unlockedArtifacts.add(buf.readString());
        }
    }
}
