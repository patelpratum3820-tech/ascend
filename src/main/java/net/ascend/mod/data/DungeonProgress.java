package net.ascend.mod.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DungeonProgress {
    private String currentRank = "E";
    private final Map<String, Integer> clearsByRank = new HashMap<>();
    private final Map<String, Integer> bestTimesByRank = new HashMap<>();

    public DungeonProgress() {
        for (String r : new String[]{"E", "D", "C", "B", "A", "S"}) {
            clearsByRank.put(r, 0);
            bestTimesByRank.put(r, 0);
        }
    }

    public String getCurrentRank() {
        return currentRank;
    }

    public void setCurrentRank(String rank) {
        if (rank != null && !rank.isBlank()) {
            this.currentRank = rank.toUpperCase();
        }
    }

    public void recordClear(String rank, int timeSeconds) {
        String r = rank.toUpperCase();
        clearsByRank.put(r, clearsByRank.getOrDefault(r, 0) + 1);

        int currentBest = bestTimesByRank.getOrDefault(r, 0);
        if (currentBest == 0 || timeSeconds < currentBest) {
            bestTimesByRank.put(r, timeSeconds);
        }

        // Rank up logic if criteria met
        if ("E".equals(currentRank) && clearsByRank.getOrDefault("E", 0) >= 3) currentRank = "D";
        else if ("D".equals(currentRank) && clearsByRank.getOrDefault("D", 0) >= 5) currentRank = "C";
        else if ("C".equals(currentRank) && clearsByRank.getOrDefault("C", 0) >= 5) currentRank = "B";
        else if ("B".equals(currentRank) && clearsByRank.getOrDefault("B", 0) >= 7) currentRank = "A";
        else if ("A".equals(currentRank) && clearsByRank.getOrDefault("A", 0) >= 10) currentRank = "S";
    }

    public int getClears(String rank) {
        return clearsByRank.getOrDefault(rank.toUpperCase(), 0);
    }

    public int getBestTime(String rank) {
        return bestTimesByRank.getOrDefault(rank.toUpperCase(), 0);
    }

    public Map<String, Integer> getClearsByRank() {
        return Collections.unmodifiableMap(clearsByRank);
    }

    public Map<String, Integer> getBestTimesByRank() {
        return Collections.unmodifiableMap(bestTimesByRank);
    }

    public void writeToNbt(NbtCompound nbt) {
        nbt.putString("CurrentRank", currentRank);

        NbtCompound clearsTag = new NbtCompound();
        for (Map.Entry<String, Integer> entry : clearsByRank.entrySet()) {
            clearsTag.putInt(entry.getKey(), entry.getValue());
        }
        nbt.put("ClearsByRank", clearsTag);

        NbtCompound timesTag = new NbtCompound();
        for (Map.Entry<String, Integer> entry : bestTimesByRank.entrySet()) {
            timesTag.putInt(entry.getKey(), entry.getValue());
        }
        nbt.put("BestTimesByRank", timesTag);
    }

    public void readFromNbt(NbtCompound nbt) {
        if (nbt.contains("CurrentRank")) currentRank = nbt.getString("CurrentRank");

        if (nbt.contains("ClearsByRank")) {
            NbtCompound clearsTag = nbt.getCompound("ClearsByRank");
            for (String key : clearsTag.getKeys()) {
                clearsByRank.put(key, clearsTag.getInt(key));
            }
        }

        if (nbt.contains("BestTimesByRank")) {
            NbtCompound timesTag = nbt.getCompound("BestTimesByRank");
            for (String key : timesTag.getKeys()) {
                bestTimesByRank.put(key, timesTag.getInt(key));
            }
        }
    }

    public void writeToBuffer(PacketByteBuf buf) {
        buf.writeString(currentRank);

        buf.writeInt(clearsByRank.size());
        for (Map.Entry<String, Integer> entry : clearsByRank.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeInt(entry.getValue());
        }

        buf.writeInt(bestTimesByRank.size());
        for (Map.Entry<String, Integer> entry : bestTimesByRank.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeInt(entry.getValue());
        }
    }

    public void readFromBuffer(PacketByteBuf buf) {
        currentRank = buf.readString();

        clearsByRank.clear();
        int clearsSize = buf.readInt();
        for (int i = 0; i < clearsSize; i++) {
            clearsByRank.put(buf.readString(), buf.readInt());
        }

        bestTimesByRank.clear();
        int timesSize = buf.readInt();
        for (int i = 0; i < timesSize; i++) {
            bestTimesByRank.put(buf.readString(), buf.readInt());
        }
    }
}
