package com.ashindigo.musicexpansion.container;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.item.ItemWalkman;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import spinnery.common.handler.BaseScreenHandler;
import spinnery.common.inventory.BaseInventory;
import spinnery.common.utility.InventoryUtilities;
import spinnery.widget.WInterface;
import spinnery.widget.WSlot;

public class WalkmanContainer extends BaseScreenHandler {

    public static final int INVENTORY = 1;
    //public final int slot;

    public WalkmanContainer(int syncId, PlayerInventory inv) {
        super(syncId, inv);
        //this.slot = slot;
        WInterface mainInterface = getInterface();
        int slot = MusicExpansion.getWalkman(inv);
        BaseInventory walkmanInv = ItemWalkman.getInventory(inv.getStack(slot), inv);
        addInventory(INVENTORY, walkmanInv);
        walkmanInv.addListener(sender -> {
            inv.getStack(slot).getTag().put("inventory", InventoryUtilities.write(sender).getCompound("inventory"));
            inv.markDirty();
        });
        for (int i = 0; i < 9; i++) {
            mainInterface.createChild(WSlot::new).setSlotNumber(i).setInventoryNumber(INVENTORY);
        }
        WSlot.addHeadlessPlayerInventory(mainInterface);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return MusicExpansion.WALKMAN_TYPE;
    }
}
