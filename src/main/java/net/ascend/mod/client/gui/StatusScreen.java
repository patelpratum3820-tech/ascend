package net.ascend.mod.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * StatusScreen now extends the full multi-tab CharacterProfileScreen
 * ensuring 100% backwards compatibility while offering the comprehensive RPG overview.
 */
@Environment(EnvType.CLIENT)
public class StatusScreen extends CharacterProfileScreen {
    public StatusScreen() {
        super();
    }
}
