package net.ascend.mod.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class QuestProgress {
    private final Map<String, Integer> activeQuests = new HashMap<>();
    private final Set<String> completedQuests = new HashSet<>();

    public QuestProgress() {}

    public void startQuest(String questId) {
        if (!completedQuests.contains(questId) && !activeQuests.containsKey(questId)) {
            activeQuests.put(questId, 0);
        }
    }

    public void addProgress(String questId, int amount) {
        if (activeQuests.containsKey(questId)) {
            activeQuests.put(questId, activeQuests.get(questId) + amount);
        }
    }

    public int getProgress(String questId) {
        return activeQuests.getOrDefault(questId, 0);
    }

    public boolean isQuestActive(String questId) {
        return activeQuests.containsKey(questId);
    }

    public boolean isQuestCompleted(String questId) {
        return completedQuests.contains(questId);
    }

    public void completeQuest(String questId) {
        activeQuests.remove(questId);
        completedQuests.add(questId);
    }

    public Map<String, Integer> getActiveQuests() {
        return Collections.unmodifiableMap(activeQuests);
    }

    public Set<String> getCompletedQuests() {
        return Collections.unmodifiableSet(completedQuests);
    }

    public void writeToNbt(NbtCompound nbt) {
        NbtCompound activeTag = new NbtCompound();
        for (Map.Entry<String, Integer> entry : activeQuests.entrySet()) {
            activeTag.putInt(entry.getKey(), entry.getValue());
        }
        nbt.put("ActiveQuests", activeTag);

        NbtList completedList = new NbtList();
        for (String qId : completedQuests) {
            completedList.add(NbtString.of(qId));
        }
        nbt.put("CompletedQuests", completedList);
    }

    public void readFromNbt(NbtCompound nbt) {
        activeQuests.clear();
        if (nbt.contains("ActiveQuests")) {
            NbtCompound activeTag = nbt.getCompound("ActiveQuests");
            for (String key : activeTag.getKeys()) {
                activeQuests.put(key, activeTag.getInt(key));
            }
        }

        completedQuests.clear();
        if (nbt.contains("CompletedQuests", 9)) {
            NbtList list = nbt.getList("CompletedQuests", 8);
            for (int i = 0; i < list.size(); i++) {
                completedQuests.add(list.getString(i));
            }
        }
    }

    public void writeToBuffer(PacketByteBuf buf) {
        buf.writeInt(activeQuests.size());
        for (Map.Entry<String, Integer> entry : activeQuests.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeInt(entry.getValue());
        }

        buf.writeInt(completedQuests.size());
        for (String qId : completedQuests) {
            buf.writeString(qId);
        }
    }

    public void readFromBuffer(PacketByteBuf buf) {
        activeQuests.clear();
        int activeSize = buf.readInt();
        for (int i = 0; i < activeSize; i++) {
            String id = buf.readString();
            int progress = buf.readInt();
            activeQuests.put(id, progress);
        }

        completedQuests.clear();
        int completedSize = buf.readInt();
        for (int i = 0; i < completedSize; i++) {
            completedQuests.add(buf.readString());
        }
    }
}

