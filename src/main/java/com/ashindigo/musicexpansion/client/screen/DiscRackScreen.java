package com.ashindigo.musicexpansion.client.screen;

import com.ashindigo.musicexpansion.handler.Abstract9DiscHolderHandler;
import com.ashindigo.musicexpansion.handler.DiscRackHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import spinnery.client.screen.BaseHandledScreen;
import spinnery.widget.WInterface;
import spinnery.widget.WPanel;
import spinnery.widget.WSlot;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

public class DiscRackScreen extends BaseHandledScreen<DiscRackHandler> {

    public static final Size SLOT_SIZE = Size.of(18, 18);

    public DiscRackScreen(DiscRackHandler handler, PlayerInventory playerInv, Text title) {
        super(handler, playerInv, title);
        WInterface mainInterface = getInterface();
        WPanel panel = mainInterface.createChild(WPanel::new).setSize(Size.of(180, 160));
        panel.center();
        panel.setLabel(title);
        WSlot.addPlayerInventory(Position.of(panel).add(9, 76, 0), SLOT_SIZE, panel);
        for (int i = 0; i < 9; i++) {
            panel.createChild(WSlot::new, Position.of(panel).add(9 + (18 * i), 16, 0), SLOT_SIZE).setInventoryNumber(Abstract9DiscHolderHandler.INVENTORY).setSlotNumber(i);
        }
    }
}
