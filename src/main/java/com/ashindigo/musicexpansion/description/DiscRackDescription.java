package com.ashindigo.musicexpansion.description;

import com.ashindigo.musicexpansion.MusicExpansion;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlayerInvPanel;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;

public class DiscRackDescription extends SyncedGuiDescription {

    public DiscRackDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext ctx) {
        super(MusicExpansion.DISC_RACK_HANDLER_TYPE, syncId, playerInventory, getBlockInventory(ctx, 9), getBlockPropertyDelegate(ctx));
        WGridPanel root = new WGridPanel();
        root.add(new WItemSlot(blockInventory, 0, 9, 1, false), 0, 1);
        root.add(new WPlayerInvPanel(playerInventory, true), 0, 2);
        setRootPanel(root);
    }
}
