package net.ascend.mod.client.gui.components;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RpgTabBar {
    public record Tab(String id, String label) {}

    private final List<Tab> tabs = new ArrayList<>();
    private int selectedIndex = 0;
    private final Consumer<Integer> onSelect;

    public RpgTabBar(Consumer<Integer> onSelect) {
        this.onSelect = onSelect;
    }

    public void addTab(String id, String label) {
        tabs.add(new Tab(id, label));
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int idx) {
        if (idx >= 0 && idx < tabs.size()) {
            this.selectedIndex = idx;
        }
    }

    public List<ButtonWidget> createWidgets(int x, int y, int totalWidth, int tabHeight) {
        List<ButtonWidget> widgets = new ArrayList<>();
        if (tabs.isEmpty()) return widgets;

        int tabWidth = (totalWidth - ((tabs.size() - 1) * 4)) / tabs.size();

        for (int i = 0; i < tabs.size(); i++) {
            final int idx = i;
            Tab t = tabs.get(i);
            boolean isCur = idx == selectedIndex;

            String prefix = isCur ? "§6▶ §l" : "§7";
            ButtonWidget btn = ButtonWidget.builder(Text.literal(prefix + t.label()), b -> {
                selectedIndex = idx;
                if (onSelect != null) {
                    onSelect.accept(idx);
                }
            }).dimensions(x + (i * (tabWidth + 4)), y, tabWidth, tabHeight).build();

            widgets.add(btn);
        }

        return widgets;
    }
}

