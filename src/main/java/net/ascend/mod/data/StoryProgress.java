package net.ascend.mod.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class StoryProgress {
    public enum MoralPath {
        NONE("Unchosen"),
        ANGEL("Path of the Seraph"),
        DEMON("Path of the Demon King");

        private final String title;
        MoralPath(String title) { this.title = title; }
        public String getTitle() { return title; }
    }

    private boolean prologueCompleted = false;
    private boolean firstDungeonCleared = false;
    private boolean firstBossDefeated = false;
    private MoralPath moralPath = MoralPath.NONE;
    private boolean regionalBossesDefeated = false;
    private boolean netherSovereignDefeated = false;
    private boolean floatingIslandsUnlocked = false;
    private boolean godsRealmUnlocked = false;
    private boolean chosenOneAchieved = false;

    private String activeTitle = "The Reborn Soul";
    private final Set<String> unlockedTitles = new HashSet<>();

    public StoryProgress() {
        unlockedTitles.add("The Reborn Soul");
    }

    public boolean isPrologueCompleted() { return prologueCompleted; }
    public void setPrologueCompleted(boolean val) { this.prologueCompleted = val; }

    public boolean isFirstDungeonCleared() { return firstDungeonCleared; }
    public void setFirstDungeonCleared(boolean val) { this.firstDungeonCleared = val; }

    public boolean isFirstBossDefeated() { return firstBossDefeated; }
    public void setFirstBossDefeated(boolean val) { this.firstBossDefeated = val; }

    public MoralPath getMoralPath() { return moralPath; }
    public void setMoralPath(MoralPath path) { this.moralPath = path != null ? path : MoralPath.NONE; }

    public boolean isRegionalBossesDefeated() { return regionalBossesDefeated; }
    public void setRegionalBossesDefeated(boolean val) { this.regionalBossesDefeated = val; }

    public boolean isNetherSovereignDefeated() { return netherSovereignDefeated; }
    public void setNetherSovereignDefeated(boolean val) { this.netherSovereignDefeated = val; }

    public boolean isFloatingIslandsUnlocked() { return floatingIslandsUnlocked; }
    public void setFloatingIslandsUnlocked(boolean val) { this.floatingIslandsUnlocked = val; }

    public boolean isGodsRealmUnlocked() { return godsRealmUnlocked; }
    public void setGodsRealmUnlocked(boolean val) { this.godsRealmUnlocked = val; }

    public boolean isChosenOneAchieved() { return chosenOneAchieved; }
    public void setChosenOneAchieved(boolean val) {
        this.chosenOneAchieved = val;
        if (val) {
            unlockTitle("✦ THE CHOSEN ONE ✦");
            setActiveTitle("✦ THE CHOSEN ONE ✦");
        }
    }

    public String getActiveTitle() { return activeTitle; }
    public void setActiveTitle(String title) {
        if (unlockedTitles.contains(title)) {
            this.activeTitle = title;
        }
    }

    public void unlockTitle(String title) {
        unlockedTitles.add(title);
    }

    public Set<String> getUnlockedTitles() {
        return Collections.unmodifiableSet(unlockedTitles);
    }

    public void writeToNbt(NbtCompound nbt) {
        nbt.putBoolean("PrologueCompleted", prologueCompleted);
        nbt.putBoolean("FirstDungeonCleared", firstDungeonCleared);
        nbt.putBoolean("FirstBossDefeated", firstBossDefeated);
        nbt.putString("MoralPath", moralPath.name());
        nbt.putBoolean("RegionalBossesDefeated", regionalBossesDefeated);
        nbt.putBoolean("NetherSovereignDefeated", netherSovereignDefeated);
        nbt.putBoolean("FloatingIslandsUnlocked", floatingIslandsUnlocked);
        nbt.putBoolean("GodsRealmUnlocked", godsRealmUnlocked);
        nbt.putBoolean("ChosenOneAchieved", chosenOneAchieved);
        nbt.putString("ActiveTitle", activeTitle);

        NbtList titlesList = new NbtList();
        for (String title : unlockedTitles) {
            titlesList.add(NbtString.of(title));
        }
        nbt.put("UnlockedTitles", titlesList);
    }

    public void readFromNbt(NbtCompound nbt) {
        if (nbt.contains("PrologueCompleted")) prologueCompleted = nbt.getBoolean("PrologueCompleted");
        if (nbt.contains("FirstDungeonCleared")) firstDungeonCleared = nbt.getBoolean("FirstDungeonCleared");
        if (nbt.contains("FirstBossDefeated")) firstBossDefeated = nbt.getBoolean("FirstBossDefeated");
        if (nbt.contains("MoralPath")) {
            try {
                moralPath = MoralPath.valueOf(nbt.getString("MoralPath"));
            } catch (Exception ignored) {
                moralPath = MoralPath.NONE;
            }
        }
        if (nbt.contains("RegionalBossesDefeated")) regionalBossesDefeated = nbt.getBoolean("RegionalBossesDefeated");
        if (nbt.contains("NetherSovereignDefeated")) netherSovereignDefeated = nbt.getBoolean("NetherSovereignDefeated");
        if (nbt.contains("FloatingIslandsUnlocked")) floatingIslandsUnlocked = nbt.getBoolean("FloatingIslandsUnlocked");
        if (nbt.contains("GodsRealmUnlocked")) godsRealmUnlocked = nbt.getBoolean("GodsRealmUnlocked");
        if (nbt.contains("ChosenOneAchieved")) chosenOneAchieved = nbt.getBoolean("ChosenOneAchieved");
        if (nbt.contains("ActiveTitle")) activeTitle = nbt.getString("ActiveTitle");

        unlockedTitles.clear();
        if (nbt.contains("UnlockedTitles", 9)) {
            NbtList list = nbt.getList("UnlockedTitles", 8);
            for (int i = 0; i < list.size(); i++) {
                unlockedTitles.add(list.getString(i));
            }
        }
        if (unlockedTitles.isEmpty()) unlockedTitles.add("The Reborn Soul");
    }

    public void writeToBuffer(PacketByteBuf buf) {
        buf.writeBoolean(prologueCompleted);
        buf.writeBoolean(firstDungeonCleared);
        buf.writeBoolean(firstBossDefeated);
        buf.writeString(moralPath.name());
        buf.writeBoolean(regionalBossesDefeated);
        buf.writeBoolean(netherSovereignDefeated);
        buf.writeBoolean(floatingIslandsUnlocked);
        buf.writeBoolean(godsRealmUnlocked);
        buf.writeBoolean(chosenOneAchieved);
        buf.writeString(activeTitle);

        buf.writeInt(unlockedTitles.size());
        for (String title : unlockedTitles) {
            buf.writeString(title);
        }
    }

    public void readFromBuffer(PacketByteBuf buf) {
        prologueCompleted = buf.readBoolean();
        firstDungeonCleared = buf.readBoolean();
        firstBossDefeated = buf.readBoolean();
        try {
            moralPath = MoralPath.valueOf(buf.readString());
        } catch (Exception ignored) {
            moralPath = MoralPath.NONE;
        }
        regionalBossesDefeated = buf.readBoolean();
        netherSovereignDefeated = buf.readBoolean();
        floatingIslandsUnlocked = buf.readBoolean();
        godsRealmUnlocked = buf.readBoolean();
        chosenOneAchieved = buf.readBoolean();
        activeTitle = buf.readString();

        unlockedTitles.clear();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            unlockedTitles.add(buf.readString());
        }
    }
}

