package net.ascend.mod.mixin;

import net.ascend.mod.client.gui.StatusScreen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {
    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void ascend$addStatusButton(CallbackInfo ci) {
        // Place button right above or beside the crafting recipe book button
        int btnX = this.x + 128;
        int btnY = this.height / 2 - 22;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("✦ Status"), button -> {
            if (this.client != null) {
                this.client.setScreen(new StatusScreen());
            }
        }).dimensions(btnX, btnY, 44, 18).build());
    }
}
